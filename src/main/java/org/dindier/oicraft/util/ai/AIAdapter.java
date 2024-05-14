package org.dindier.oicraft.util.ai;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class AIAdapter {
    @Value("${ai.host-url}")
    public String hostUrl;
    @Value("${ai.app-id}")
    public String appId;
    @Value("${ai.api-secret}")
    public String apiSecret;
    @Value("${ai.api-key}")
    public String apiKey;

    private final Logger logger = LoggerFactory.getLogger(AIAdapter.class);

    /*
     * Generate the URL for the AI service
     * Copied from the official document
     */
    private String getAuthUrl() throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        String preStr = String.format("host: %s\ndate: %s\nGET %s HTTP/1.1", url.getHost(), date, url.getPath());
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath()))
                .newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        return httpUrl.toString();
    }

    private class GetAIResponseThread extends Thread {
        private final WebSocket webSocket;
        private final String question;
        boolean closeFlag = false;

        private GetAIResponseThread(WebSocket webSocket, String question) {
            this.webSocket = webSocket;
            this.question = question;
        }

        private JSONObject generateRequest(String question) {
            JSONObject request = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("app_id", appId);
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

            JSONObject chat = new JSONObject();
            chat.put("domain", "generalv2");
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 4096);

            JSONObject parameter = new JSONObject();
            parameter.put("chat", chat);

            JSONArray text = new JSONArray();
            JSONObject jsonText = new JSONObject();
            jsonText.put("content", question);
            jsonText.put("role", "user");
            text.put(jsonText);

            JSONObject message = new JSONObject();
            message.put("text", text);
            JSONObject payload = new JSONObject();
            payload.put("message", message);

            request.put("header", header);
            request.put("parameter", parameter);
            request.put("payload", payload);
            return request;
        }

        @Override
        @SuppressWarnings("BusyWait")
        public void run() {
            JSONObject request = generateRequest(question);
            webSocket.send(request.toString());
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted");
                }
            } while (!closeFlag);
            webSocket.close(1000, "OK");
        }
    }

    private class AIWebSocketListener extends WebSocketListener {
        final StringBuilder answer = new StringBuilder();
        private final String question;
        private GetAIResponseThread getAIResponseThread;

        private AIWebSocketListener(String question) {
            this.question = question;
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            getAIResponseThread = new GetAIResponseThread(webSocket, question);
            getAIResponseThread.start();
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            JSONObject response = new JSONObject(text);
            JSONObject header = response.getJSONObject("header");
            int code = header.getInt("code");
            if (code != 0) {
                logger.error("AI service error: {}", code);
                webSocket.close(1000, "AI service error");
            }
            List<Text> texts = getText(response);
            for (Text t : texts) {
                answer.append(t.content);
            }
            if (header.getInt("status") == 2) {
                getAIResponseThread.closeFlag = true;
            }
        }

        private List<Text> getText(JSONObject response) {
            JSONObject payload = response.getJSONObject("payload");
            JSONObject choices = payload.getJSONObject("choices");
            JSONArray textArray = choices.getJSONArray("text");
            List<Text> texts = new ArrayList<>();
            for (int i = 0; i < textArray.length(); i++) {
                JSONObject text = textArray.getJSONObject(i);
                Text t = new Text();
                t.content = text.getString("content");
                t.role = text.getString("role");
                texts.add(t);
            }
            return texts;
        }
    }

    private static class Text {
        String content;
        String role;
    }

    /**
     * Request the AI service
     *
     * @param question the question to ask
     * @return the answer from the AI service
     */
    @SuppressWarnings("BusyWait")
    public String requestAI(String question) throws Exception {
        String authUrl = getAuthUrl();
        OkHttpClient client = new OkHttpClient();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        AIWebSocketListener listener = new AIWebSocketListener(question);
        client.newWebSocket(request, listener);
        while (listener.getAIResponseThread == null || !listener.getAIResponseThread.closeFlag) {
            Thread.sleep(100);  // Wait for the last message to be received
        }
        Thread.sleep(30);  // Wait for the last message to be received
        return listener.answer.toString();
    }
}
