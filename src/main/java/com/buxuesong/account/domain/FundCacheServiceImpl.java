package com.buxuesong.account.domain;

import com.buxuesong.account.infrastructure.adapter.rest.SinaRestClient;
import com.buxuesong.account.infrastructure.adapter.rest.TiantianFundRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@CacheConfig(cacheNames = "fund")
public class FundCacheServiceImpl implements FundCacheService {

    @Autowired
    private SinaRestClient sinaRestClient;

    @Autowired
    private TiantianFundRestClient tiantianFundRestClient;

    public String getFundInfoFromSinaApi(String code) {
        log.info("通过新浪基金缓存接口获取基金，编码：{}", code);
        return sinaRestClient.getFundInfo(code);
    }

    public String getFundInfoFromApi(String code) {
        log.info("通过天天基金缓存接口获取基金，编码：{}", code);
        return tiantianFundRestClient.getFundInfo(code);
    }

    @Override
    @Cacheable(key = "'sina_'+#code")
    public String getFundInfoFromSinaApiCache(String code) {
        log.info("通过新浪基金缓存接口获取基金，编码：{}， URL：{}", code);
        return sinaRestClient.getFundInfo(code);
    }

    @Override
    @Cacheable(key = "#code")
    public String getFundInfoFromApiCache(String code) {
        log.info("通过天天基金缓存接口获取基金，编码：{}， URL：{}", code);
        return tiantianFundRestClient.getFundInfo(code);
    }
}
