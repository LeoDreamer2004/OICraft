package org.dindier.oicraft.util.crawl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class WebUtil {
    public static boolean SHOW_PROCESS = true;

    /* Set the header of the http connection here */
    private static HttpURLConnection getHttpURLConnection(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0");
        urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9, image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        urlConnection.setRequestProperty("Cache-Control", "max-age=0");
        return urlConnection;
    }

    /**
     * Get the content from the url
     *
     * @param strUrl   The url
     * @param charCode The charset of the content
     * @return The content from the url
     */
    public static String getContentFromUrl(String strUrl, String charCode) {
        if (SHOW_PROCESS)
            log.info("==============> Crawling: {}", strUrl);
        try {
            HttpURLConnection urlConnection = getHttpURLConnection(strUrl);
            InputStream stream = urlConnection.getInputStream();
            return readAll(stream, charCode);
        } catch (MalformedURLException e) {
            log.warn("URL format is wrong");
        } catch (IOException ioe) {
            log.warn("IO exception, maybe the website is down or the request is denied");
        }
        return "";
    }

    /**
     * Save the content to the file
     *
     * @param content The content
     * @param path    The path of the file
     */
    public static void saveToFile(String content, String path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            log.info("Cannot save the content to the file {}", path);
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

    /**
     * Find the pattern in the string
     *
     * @param string     The string
     * @param pattern    The pattern
     * @param groupIndex The index of the group
     * @return The matched string
     */
    public static String findPattern(String string, String pattern, int groupIndex) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find())
            return m.group(groupIndex);
        return null;
    }

    /**
     * Find all the patterns in the string
     *
     * @param string     The string
     * @param pattern    The pattern
     * @param groupIndex The index of the group
     * @return The matched strings
     */
    public static List<String> findAllPatterns(String string, String pattern, int groupIndex) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        return m.results().map(r -> r.group(groupIndex)).toList();
    }

    /**
     * Download files from the urls to the paths, using multiple threads
     *
     * @param urls  The urls
     * @param paths The paths
     */
    public static void downloadFiles(String[] urls, String[] paths) {
        assert urls.length == paths.length;
        CountDownLatch latch = new CountDownLatch(urls.length);

        for (int i = 0; i < urls.length; i++) {
            final int idx = i;
            new Thread(() -> {
                try {
                    download(urls[idx], paths[idx]);
                } catch (Exception ex) {
                    log.warn("Error occurs when downloading from{}", urls[idx]);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            log.warn("Error occurs when waiting for the download threads");
        }
    }

    private static void download(String url, String path) throws Exception {
        if (SHOW_PROCESS)
            log.info("==============> Downloading: {}", url);
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
