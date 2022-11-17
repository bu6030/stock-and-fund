package com.buxuesong.account.domain.service;

import com.buxuesong.account.infrastructure.adapter.rest.SinaRestClient;
import com.buxuesong.account.infrastructure.adapter.rest.TiantianFundRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = "fund")
public class FundCacheService {

    @Autowired
    private SinaRestClient sinaRestClient;

    @Autowired
    private TiantianFundRestClient tiantianFundRestClient;

    @Cacheable(key = "'sina_'+#code")
    public String getFundInfoFromSina(String code) {
        log.info("通过新浪基金缓存接口获取基金，编码：{}， URL：{}", code);
        return sinaRestClient.getFundInfo(code);
    }

    @Cacheable(key = "#code")
    public String getFundInfoFromTiantianFund(String code) {
        log.info("通过天天基金缓存接口获取基金，编码：{}， URL：{}", code);
        return tiantianFundRestClient.getFundInfo(code);
    }
}
