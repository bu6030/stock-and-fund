package com.buxuesong.account.service;

import com.buxuesong.account.model.SaveFundRequest;
import com.buxuesong.account.persist.dao.FundMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.buxuesong.account.model.FundBean;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FundServiceImpl implements FundService {
    @Autowired
    private FundMapper fundMapper;
    @Autowired
    private RestTemplate restTemplate;

    private static Gson gson = new Gson();
    private static final String GET_FUND_INFO_URL = "http://fundgz.1234567.com.cn/js/{code}.js";
    private static final String GET_FUND_INFO_SINA_URL = "https://hq.sinajs.cn/?_={timestamp}/&list=sz{code},f_{code}";

    @Override
    public List<FundBean> getFundDetails(List<String> codes) {
        List<FundBean> funds = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        Map<String, String[]> codeMap = new HashMap<>();
        for (String str : codes) {
            String[] strArray;
            if (str.contains(",")) {
                strArray = str.split(",");
            } else {
                strArray = new String[] { str };
            }
            codeList.add(strArray[0]);
            codeMap.put(strArray[0], strArray);
        }

        for (String code : codeList) {
            try {
                String result = getFundInfoFromApi(code);
                // 天天基金存在基金信息
                if (result != null && !result.equals("jsonpgz();")) {
                    String json = result.substring(8, result.length() - 2);
                    log.info("天天基金结果： {}", json);
                    if (!json.isEmpty()) {
                        FundBean bean = gson.fromJson(json, FundBean.class);
                        FundBean.loadFund(bean, codeMap);

                        BigDecimal now = new BigDecimal(bean.getGsz());
                        String costPriceStr = bean.getCostPrise();
                        if (StringUtils.isNotEmpty(costPriceStr)) {
                            BigDecimal costPriceDec = new BigDecimal(costPriceStr);
                            BigDecimal incomeDiff = now.add(costPriceDec.negate());
                            if (costPriceDec.compareTo(BigDecimal.ZERO) <= 0) {
                                bean.setIncomePercent("0");
                            } else {
                                BigDecimal incomePercentDec = incomeDiff.divide(costPriceDec, 8, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.TEN)
                                    .multiply(BigDecimal.TEN)
                                    .setScale(3, RoundingMode.HALF_UP);
                                bean.setIncomePercent(incomePercentDec.toString());
                            }

                            String bondStr = bean.getBonds();
                            if (StringUtils.isNotEmpty(bondStr)) {
                                BigDecimal bondDec = new BigDecimal(bondStr);
                                BigDecimal incomeDec = incomeDiff.multiply(bondDec)
                                    .setScale(2, RoundingMode.HALF_UP);
                                bean.setIncome(incomeDec.toString());
                            }
                        }
                        funds.add(bean);
                        log.info("Fund编码:[" + code + "]信息：{}", bean);
                    } else {
                        log.info("Fund编码:[" + code + "]无法获取数据");
                    }
                    // 天天基金不存在基金信息，去新浪查找
                } else {
                    result = getFundInfoFromSinaApi(code);
                    log.info("sina基金结果： {}", result);
                    FundBean bean = FundBean.loadFundFromSina(code, result);
                    FundBean.loadFund(bean, codeMap);
                    BigDecimal now = new BigDecimal(bean.getGsz());
                    String costPriceStr = bean.getCostPrise();
                    if (StringUtils.isNotEmpty(costPriceStr)) {
                        BigDecimal costPriceDec = new BigDecimal(costPriceStr);
                        BigDecimal incomeDiff = now.add(costPriceDec.negate());
                        if (costPriceDec.compareTo(BigDecimal.ZERO) <= 0) {
                            bean.setIncomePercent("0");
                        } else {
                            BigDecimal incomePercentDec = incomeDiff.divide(costPriceDec, 8, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.TEN)
                                .multiply(BigDecimal.TEN)
                                .setScale(3, RoundingMode.HALF_UP);
                            bean.setIncomePercent(incomePercentDec.toString());
                        }

                        String bondStr = bean.getBonds();
                        if (StringUtils.isNotEmpty(bondStr)) {
                            BigDecimal bondDec = new BigDecimal(bondStr);
                            BigDecimal incomeDec = incomeDiff.multiply(bondDec)
                                .setScale(2, RoundingMode.HALF_UP);
                            bean.setIncome(incomeDec.toString());
                        }
                    }
                    funds.add(bean);
                    log.info("Fund编码:[" + code + "]信息：{}", bean);
                }
            } catch (Exception e) {
                log.info("Fund编码:[" + code + "]异常");
                e.printStackTrace();
            }
        }
        return funds;
    }

    @Override
    public boolean saveFund(SaveFundRequest saveFundRequest) {
        try {
            String result = getFundInfoFromApi(saveFundRequest.getCode());
            if (result != null && !result.equals("jsonpgz")) {
                String json = result.substring(8, result.length() - 2);
                log.info("天天基金结果： {}", json);
                FundBean bean = gson.fromJson(json, FundBean.class);
            } else {
                result = getFundInfoFromSinaApi(saveFundRequest.getCode());
                log.info("sina基金结果： {}", result);
                FundBean bean = FundBean.loadFundFromSina(saveFundRequest.getCode(), result);
            }
        } catch (Exception e) {
            log.info("获取基金信息异常 {}", e.getMessage());
            return false;
        }
        SaveFundRequest saveFundRequestFromTable = fundMapper.findFundByCode(saveFundRequest.getCode());
        if (saveFundRequestFromTable != null) {
            fundMapper.updateFund(saveFundRequest);
        } else {
            fundMapper.save(saveFundRequest);
        }
        return true;
    }

    @Override
    public void deleteFund(SaveFundRequest saveFundRequest) {
        fundMapper.deleteFund(saveFundRequest);
    }

    @Override
    public List<String> getFundList() {
        List<SaveFundRequest> fund = fundMapper.findAllFund();
        log.info("数据库中的基金为：{}", fund);
        if (fund == null || fund.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (SaveFundRequest fundRequest : fund) {
            String fundArr = fundRequest.getCode() + "," + fundRequest.getCostPrise() + "," + fundRequest.getBonds() + ","
                + fundRequest.getApp();
            list.add(fundArr);
        }
        return list;
    }

    @Override
    public SaveFundRequest findFundByCode(String code) {
        return fundMapper.findFundByCode(code);
    }

    private String getFundInfoFromApi(String code) {
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

    private String getFundInfoFromSinaApi(String code) {
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

}
