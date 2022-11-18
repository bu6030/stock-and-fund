package com.buxuesong.account.infrastructure.adapter.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SinaRestClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_FUND_INFO_SINA_URL = "https://hq.sinajs.cn/?_={timestamp}/&list=sz{code},f_{code}";

    public String getFundInfo(String code) {
        log.info("通过新浪基金接口获取基金，编码：{}， URL：{}", code, GET_FUND_INFO_SINA_URL);
        ResponseEntity<String> response = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Referer", "http://finance.sina.com.cn/");
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        try {
            response = restTemplate.exchange(
                GET_FUND_INFO_SINA_URL, HttpMethod.GET, httpEntity, String.class, System.currentTimeMillis(), code, code);
        } catch (Exception e) {
            log.info("获取新浪基金接口异常: {]", e);
            return null;
        }
        return response.getBody();
    }
}
