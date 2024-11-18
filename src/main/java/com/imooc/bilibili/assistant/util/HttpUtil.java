package com.imooc.bilibili.assistant.util;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    /**
     * HTTP GET REQUEST，请求指定资源，然后把响应写到response输出流
     */
    public static void get(
            String url, Map<String, Object> headers, HttpServletResponse response) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection connect = (HttpURLConnection) urlObj.openConnection();
        connect.setDoInput(true);
        connect.setRequestMethod("GET");
        connect.setConnectTimeout(120000);
        for (Entry<String, Object> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            connect.setRequestProperty(key, value);
        }
        // 具体工作
        connect.connect();
        try (BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
             OutputStream os = response.getOutputStream()) {
            int responseCode = connect.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) { // 2xx
                int len;
                byte[] buffer = new byte[1024];
                while ((len = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
            }
        }
        connect.disconnect();
    }

}
