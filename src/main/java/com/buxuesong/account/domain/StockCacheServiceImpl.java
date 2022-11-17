package com.buxuesong.account.domain;

import com.buxuesong.account.infrastructure.adapter.rest.GTimgRestClient;
import com.buxuesong.account.infrastructure.adapter.rest.TiantianFundRestClient;
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
    private GTimgRestClient gTimgRestClient;

    @Override
    @Cacheable(key = "#param")
    public String getStockInfoFromApiCache(String param) {
        log.info("通过腾讯股票接口缓存获取股票，编码：{}， URL：{}", param);
        return gTimgRestClient.getStockInfoFromApi(param);
    }

    @Override
    public String getStockInfoFromApi(String param) {
        log.info("通过腾讯股票接口缓存获取股票，编码：{}， URL：{}", param);
        return gTimgRestClient.getStockInfoFromApi(param);
    }
}
