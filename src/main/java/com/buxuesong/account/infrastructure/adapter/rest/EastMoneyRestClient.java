package com.buxuesong.account.infrastructure.adapter.rest;

import com.buxuesong.account.infrastructure.adapter.rest.response.FundNetDiagramResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EastMoneyRestClient {
    private static Gson gson = new Gson();
    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_FUND_CODE_BY_NAME_URL = "http://fund.eastmoney.com/js/fundcode_search.js";

    private static final String GET_FUND_NET_DIAGRAM_URL = "https://fundmobapi.eastmoney.com/FundMApi/FundNetDiagram.ashx";

    public String searchAllFundsFromEastMoney() {
        log.info("通过东方财富接口获取基金，URL：{}", GET_FUND_CODE_BY_NAME_URL);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                GET_FUND_CODE_BY_NAME_URL, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            log.info("通过东方财富接口获取基金接口异常: {}", e);
            return null;
        }
        return response.getBody();
    }

    public FundNetDiagramResponse getFundNetDiagram(String code) {
        log.info("通过东方财富接口获取基金单位净值，URL：{}", GET_FUND_NET_DIAGRAM_URL + "?FCODE=" + code + "&RANGE=y&deviceid=Wap&plat=Wap&product=EFund&version=2.0.0&_=");
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    GET_FUND_NET_DIAGRAM_URL + "?FCODE=" + code + "&RANGE=y&deviceid=Wap&plat=Wap&product=EFund&version=2.0.0&_=", HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            log.info("通过东方财富接口获取基金单位净值接口异常: {}", e);
            return null;
        }
        return gson.fromJson(response.getBody(), FundNetDiagramResponse.class);
    }


}
