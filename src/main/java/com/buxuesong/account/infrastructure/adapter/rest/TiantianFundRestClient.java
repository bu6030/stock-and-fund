package com.buxuesong.account.infrastructure.adapter.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

@Slf4j
@Service
public class TiantianFundRestClient {
    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_FUND_INFO_URL = "http://fundgz.1234567.com.cn/js/{code}.js";

//    public String getFundInfo(String code) {
//        log.info("通过天天基金接口获取基金，编码：{}， URL：{}", code, GET_FUND_INFO_URL);
//        String url = UriComponentsBuilder.fromUriString(GET_FUND_INFO_URL)
//            .queryParam("rt", System.currentTimeMillis())
//            .build()
//            .toUriString();
//        ResponseEntity<String> response = null;
//        try {
//            response = restTemplate.exchange(
//                url, HttpMethod.GET, null, String.class, code);
//        } catch (Exception e) {
//            log.info("获取天天基金接口异常: {]", e);
//            return null;
//        }
//        return response.getBody();
//    }

    public String getFundInfo(String code) {
        log.info("通过天天基金接口获取基金，编码：{}， URL：{}", code, GET_FUND_INFO_URL);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(GET_FUND_INFO_URL.replace("{code}", code) + "?rt=" + System.currentTimeMillis()))
            .header("Accept", "application/javascript")
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(Charset.forName("UTF-8")));
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                log.warn("获取天天基金接口异常 HTTP status code: {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            log.info("获取天天基金接口异常: {}", e.getMessage());
            return null;
        }
    }
}
