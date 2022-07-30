package com.buxuesong.account.util;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class HttpClientPoolUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientPoolUtil.class);

    private static volatile HttpClientPoolUtil clientInstance;
    private HttpClient httpClient;

    public static HttpClientPoolUtil getHttpClient() {
        HttpClientPoolUtil tmp = clientInstance;
        if (tmp == null) {
            synchronized (HttpClientPoolUtil.class) {
                tmp = clientInstance;
                if (tmp == null) {
                    tmp = new HttpClientPoolUtil();
                    clientInstance = tmp;
                }
            }
        }
        return tmp;
    }

    private HttpClientPoolUtil() {
        buildHttpClient(null);
    }

    public void buildHttpClient(String proxyStr) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(100, TimeUnit.SECONDS);
        connectionManager.setMaxTotal(200);// 连接池
        connectionManager.setDefaultMaxPerRoute(100);// 每条通道的并发连接数
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2000).setSocketTimeout(2000).build();
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        if (proxyStr != null && !proxyStr.isEmpty()) {
            String[] s = proxyStr.split(":");
            if (s.length == 2) {
                String host = s[0];
                int port = Integer.parseInt(s[1]);
                httpClientBuilder.setProxy(new HttpHost(host, port));
            }
            logger.info("Leeks setup proxy success->" + proxyStr);
        }
        httpClient = httpClientBuilder.setDefaultRequestConfig(requestConfig).build();
    }

    public String get(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        return getResponseContent(url, httpGet);
    }

    public String post(String url) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        return getResponseContent(url, httpPost);
    }

    private String getResponseContent(String url, HttpRequestBase request) throws Exception {
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new Exception("got an error from HTTP for url : " + URLDecoder.decode(url, "UTF-8"), e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            request.releaseConnection();
        }
    }
}