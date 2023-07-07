package com.buxuesong.account.infrastructure.adapter.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EastMoneyRestClient {
    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_FUND_CODE_BY_NAME_URL = "http://fund.eastmoney.com/js/fundcode_search.js";

    public String searchAllFundsFromEastMoney() {
        log.info("通过东方财富接口获取基金，URL：{}", GET_FUND_CODE_BY_NAME_URL);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    GET_FUND_CODE_BY_NAME_URL, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            log.info("通过东方财富接口获取基金接口异常: {]", e);
            return null;
        }
        return response.getBody();
    }
}
