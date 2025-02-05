package com.buxuesong.account.infrastructure.adapter.rest;

import com.buxuesong.account.infrastructure.adapter.rest.response.TradingDateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SzseRestClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_TRADE_DATE_URL = "http://www.szse.cn/api/report/exchange/onepersistenthour/monthList";

    public TradingDateResponse getTradingDate() {
        log.info("获取交易日期，URL：{}", GET_TRADE_DATE_URL);
        ResponseEntity<TradingDateResponse> response = null;
        try {
            response = restTemplate.exchange(
                GET_TRADE_DATE_URL, HttpMethod.POST, null, TradingDateResponse.class);
        } catch (Exception e) {
            log.info("获取交易日期接口异常", e);
            return null;
        }
        return response.getBody();
    }
}
