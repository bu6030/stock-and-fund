package com.buxuesong.account.service;

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
    private RestTemplate restTemplate;

    private static final String GET_FUND_INFO_URL = "http://fundgz.1234567.com.cn/js/{code}.js";
    private static final String GET_FUND_INFO_SINA_URL = "https://hq.sinajs.cn/?_={timestamp}/&list=sz{code},f_{code}";

    @Override
    public String getFundInfoFromApi(String code) {
        log.info("通过天天基金接口获取基金，编码：{}， URL：{}", code, GET_FUND_INFO_URL);
        String url = UriComponentsBuilder.fromUriString(GET_FUND_INFO_URL)
            .queryParam("rt", System.currentTimeMillis())
            .build()
            .toUriString();
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                url, HttpMethod.GET, null, String.class, code);
        } catch (Exception e) {
            log.info("获取天天基金接口异常: {]", e);
            return null;
        }
        return response.getBody();
    }

    @Override
    public String getFundInfoFromSinaApi(String code) {
        log.info("通过新浪基金接口获取基金，编码：{}， URL：{}", code, GET_FUND_INFO_SINA_URL);
        ResponseEntity<String> response = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Referer", "http://finance.sina.com.cn/");
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        try {
            response = restTemplate.exchange(
                GET_FUND_INFO_SINA_URL, HttpMethod.GET, httpEntity, String.class, System.currentTimeMillis(), code, code);
        } catch (Exception e) {
            log.info("获取天天基金接口异常: {]", e);
            return null;
        }
        return response.getBody();
    }

    @Override
    @Cacheable(key = "'sina_'+#code")
    public String getFundInfoFromSinaApiCache(String code) {
        log.info("通过新浪基金缓存接口获取基金，编码：{}， URL：{}", code, GET_FUND_INFO_SINA_URL);
        return getFundInfoFromSinaApi(code);
    }

    @Override
    @Cacheable(key = "#code")
    public String getFundInfoFromApiCache(String code) {
        log.info("通过天天基金缓存接口获取基金，编码：{}， URL：{}", code, GET_FUND_INFO_URL);
        return getFundInfoFromApi(code);
    }
}
