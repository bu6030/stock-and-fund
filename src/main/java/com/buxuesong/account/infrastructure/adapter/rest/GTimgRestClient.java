package com.buxuesong.account.infrastructure.adapter.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class GTimgRestClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_STOCK_INFO_URL = "http://qt.gtimg.cn/q={param}";

    private static final String GET_STOCK_CODE_BY_NAME_FROM_GTIMG = "https://smartbox.gtimg.cn/s3/?v=2&t=all&c=1&q=";

    public String getStockInfo(String param) {
        log.info("通过腾讯股票接口获取股票，编码：{}， URL：{}", param, GET_STOCK_INFO_URL);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                GET_STOCK_INFO_URL, HttpMethod.GET, null, String.class, param);
        } catch (Exception e) {
            log.info("获取腾讯股票接口异常: {]", e);
            return null;
        }
        return response.getBody();
    }

    public String getGetStockCodeByName(String name) {
        log.info("通过腾讯股票接口搜索股票名称：{}， URL：{}", name, GET_STOCK_CODE_BY_NAME_FROM_GTIMG);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    GET_STOCK_CODE_BY_NAME_FROM_GTIMG + name, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            log.info("通过腾讯股票接口搜索股票名称异常: {]", e);
            return null;
        }
        return response.getBody();
    }
}
