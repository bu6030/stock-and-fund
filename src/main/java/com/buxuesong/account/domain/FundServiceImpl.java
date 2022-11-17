package com.buxuesong.account.domain;

import com.buxuesong.account.apis.model.request.FundRequest;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.infrastructure.persistent.repository.FundMapper;
import com.buxuesong.account.infrastructure.general.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FundServiceImpl implements FundService {

    private static Gson gson = new Gson();
    @Autowired
    private FundMapper fundMapper;
    @Autowired
    private FundCacheService fundCacheService;

    @Override
    public List<FundEntity> getFundDetails(List<String> codes) {
        List<FundEntity> funds = new ArrayList<>();
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
                String result = null;
                if (DateTimeUtils.isTradingTime()) {
                    result = fundCacheService.getFundInfoFromApi(code);
                } else {
                    result = fundCacheService.getFundInfoFromApiCache(code);
                }

                // 天天基金存在基金信息
                if (result != null && !result.equals("jsonpgz();")) {
                    String json = result.substring(8, result.length() - 2);
                    log.info("天天基金结果： {}", json);
                    if (!json.isEmpty()) {
                        FundEntity bean = gson.fromJson(json, FundEntity.class);
                        FundEntity.loadFund(bean, codeMap);

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
                    if (DateTimeUtils.isTradingTime()) {
                        result = fundCacheService.getFundInfoFromSinaApi(code);
                    } else {
                        result = fundCacheService.getFundInfoFromSinaApiCache(code);
                    }
                    log.info("sina基金结果： {}", result);
                    FundEntity bean = FundEntity.loadFundFromSina(code, result);
                    FundEntity.loadFund(bean, codeMap);
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
    public boolean saveFund(FundRequest fundRequest) {
        try {
            String result = fundCacheService.getFundInfoFromApi(fundRequest.getCode());
            if (result != null && !result.equals("jsonpgz")) {
                String json = result.substring(8, result.length() - 2);
                log.info("天天基金结果： {}", json);
                FundEntity bean = gson.fromJson(json, FundEntity.class);
            } else {
                result = fundCacheService.getFundInfoFromSinaApi(fundRequest.getCode());
                log.info("sina基金结果： {}", result);
                FundEntity bean = FundEntity.loadFundFromSina(fundRequest.getCode(), result);
            }
        } catch (Exception e) {
            log.info("获取基金信息异常 {}", e.getMessage());
            return false;
        }
        FundRequest fundRequestFromTable = fundMapper.findFundByCode(fundRequest.getCode());
        if (fundRequestFromTable != null) {
            fundMapper.updateFund(fundRequest);
        } else {
            fundMapper.save(fundRequest);
        }
        return true;
    }

    @Override
    public void deleteFund(FundRequest fundRequest) {
        fundMapper.deleteFund(fundRequest);
    }

    @Override
    public List<String> getFundList(String app) {
        List<FundRequest> fund = fundMapper.findAllFund(app);
        log.info("APP: {} ,数据库中的基金为：{}", app, fund);
        if (fund == null || fund.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (FundRequest fundRequest : fund) {
            String fundArr = fundRequest.getCode() + "," + fundRequest.getCostPrise() + "," + fundRequest.getBonds() + ","
                + fundRequest.getApp();
            list.add(fundArr);
        }
        return list;
    }

    @Override
    public FundRequest findFundByCode(String code) {
        return fundMapper.findFundByCode(code);
    }

}
