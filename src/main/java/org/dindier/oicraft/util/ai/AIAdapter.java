package org.dindier.oicraft.util.ai;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class AIAdapter {
    @Value("${ai.host-url}")
    public String hostUrl;
    @Value("${ai.app-id}")
    public String appId;
    @Value("${ai.api-secret}")
    public String apiSecret;
    @Value("${ai.api-key}")
    public String apiKey;

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
        private final String prompt;
        boolean closeFlag = false;

        private GetAIResponseThread(WebSocket webSocket, String prompt, String question) {
            this.webSocket = webSocket;
            this.prompt = prompt;
            this.question = question;
        }

        private JSONObject generateRequest(String prompt, String question) {
            JSONObject request = new JSONObject();

            JSONObject header = new JSONObject();
            header.put("app_id", appId);
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

            JSONObject chat = new JSONObject();
            chat.put("domain", "generalv3.5");
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 2048);

            JSONObject parameter = new JSONObject();
            parameter.put("chat", chat);

            JSONObject payload = getPayLoad(prompt, question);

            request.put("header", header);
            request.put("parameter", parameter);
            request.put("payload", payload);
            return request;
        }

        private static @NotNull JSONObject getPayLoad(String prompt, String question) {
            JSONArray text = new JSONArray();
            JSONObject jsonSystemText = new JSONObject();
            jsonSystemText.put("content", prompt);
            jsonSystemText.put("role", "system");
            text.put(jsonSystemText);
            JSONObject jsonUserText = new JSONObject();
            jsonUserText.put("content", question);
            jsonUserText.put("role", "user");
            text.put(jsonUserText);

            JSONObject message = new JSONObject();
            message.put("text", text);
            JSONObject payload = new JSONObject();
            payload.put("message", message);
            return payload;
        }

        @Override
        @SuppressWarnings("BusyWait")
        public void run() {
            JSONObject request = generateRequest(prompt, question);
            webSocket.send(request.toString());
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("Thread interrupted");
                }
            } while (!closeFlag);
            webSocket.close(1000, "OK");
        }
    }

    private class AIWebSocketListener extends WebSocketListener {
        final StringBuilder answer = new StringBuilder();
        private final String question;
        private final String prompt;
        private GetAIResponseThread getAIResponseThread;

        private AIWebSocketListener(String prompt, String question) {
            this.question = question;
            this.prompt = prompt;
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            getAIResponseThread = new GetAIResponseThread(webSocket, prompt, question);
            getAIResponseThread.start();
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            JSONObject response = new JSONObject(text);
            JSONObject header = response.getJSONObject("header");
            int code = header.getInt("code");
            if (code != 0) {
                log.error("AI service error: {}", code);
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
     * @param prompt   the prompt for AI. It should describe the role and ability of the AI
     * @param question the question to ask
     * @return the answer from the AI service
     */
    @SuppressWarnings("BusyWait")
    public String requestAI(String prompt, String question) throws Exception {
        String authUrl = getAuthUrl();
        OkHttpClient client = new OkHttpClient();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        AIWebSocketListener listener = new AIWebSocketListener(prompt, question);
        client.newWebSocket(request, listener);
        while (listener.getAIResponseThread == null || !listener.getAIResponseThread.closeFlag) {
            Thread.sleep(100);  // Wait for the last message to be received
        }
        Thread.sleep(30);  // Wait for the last message to be received
        return listener.answer.toString();
    }
}
