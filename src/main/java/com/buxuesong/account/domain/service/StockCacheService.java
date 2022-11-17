package com.buxuesong.account.domain.service;

import com.buxuesong.account.infrastructure.adapter.rest.GTimgRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = "stock")
public class StockCacheService {

    @Autowired
    private GTimgRestClient gTimgRestClient;

    @Cacheable(key = "#param")
    public String getStockInfo(String param) {
        log.info("通过腾讯股票接口缓存获取股票，编码：{}， URL：{}", param);
        return gTimgRestClient.getStockInfo(param);
    }
}
