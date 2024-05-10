package org.dindier.oicraft.util.crawl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtil {
    public static boolean SHOW_PROCESS = true;

    private static HttpURLConnection getHttpURLConnection(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0");
        urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9, image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        urlConnection.setRequestProperty("Cache-Control", "max-age=0");
        return urlConnection;
    }

    public static String getContentFromUrl(String strUrl, String charCode) {
        if (SHOW_PROCESS)
            System.out.println("==============> 正在爬取：" + strUrl);
        try {
            HttpURLConnection urlConnection = getHttpURLConnection(strUrl);
            InputStream stream = urlConnection.getInputStream();
            return readAll(stream, charCode);
        } catch (MalformedURLException e) {
            System.out.println("URL格式有错");
        } catch (IOException ioe) {
            System.out.println("IO异常");

        }
        return "";
    }

    public static void saveToFile(String content, String path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.out.println("存入文件" + path + "异常");
        }
    }

    private static String readAll(InputStream stream, String charCode) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, charCode));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static String findPattern(String string, String pattern, int groupIndex) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find())
            return m.group(groupIndex);
        return null;
    }

    public static List<String> findAllPatterns(String string, String pattern, int groupIndex) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        return m.results().map(r -> r.group(groupIndex)).toList();
    }

    public static void downloadFiles(String[] urls, String[] paths) throws Exception {
        assert urls.length == paths.length;
        CountDownLatch latch = new CountDownLatch(urls.length);

        for (int i = 0; i < urls.length; i++) {
            final int idx = i;
            new Thread(() -> {
                try {
                    download(urls[idx], paths[idx]);
                } catch (Exception ex) {
                    System.out.println("下载" + urls[idx] + "线程出现异常");
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
    }

    private static void download(String url, String path) throws Exception {
        if (SHOW_PROCESS)
            System.out.println("==============> 正在下载：" + url);
        InputStream input = getHttpURLConnection(url).getInputStream();

        try (OutputStream output = new FileOutputStream(path)) {
            byte[] data = new byte[1024];
            int length;
            while ((length = input.read(data)) != -1) {
                output.write(data, 0, length);
            }
        }
    }
}
