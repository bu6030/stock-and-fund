package com.buxuesong.account.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@CacheConfig(cacheNames = "stock")
public class StockCacheServiceImpl implements StockCacheService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GET_STOCK_INFO_URL = "http://qt.gtimg.cn/q={param}";

    @Override
    @Cacheable(key = "#param")
    public String getStockInfoFromApiCache(String param) {
        log.info("通过腾讯股票接口缓存获取股票，编码：{}， URL：{}", param, GET_STOCK_INFO_URL);
        return getStockInfoFromApi(param);
    }

    @Override
    public String getStockInfoFromApi(String param) {
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
}
