package com.buxuesong.account.service;

import com.buxuesong.account.persist.dao.StockAndFundMapper;
import com.buxuesong.account.persist.entity.StockAndFund;
import org.apache.commons.lang3.StringUtils;
import com.buxuesong.account.model.FundBean;
import com.buxuesong.account.util.HttpClientPoolUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FundServiceImpl implements FundService {
    @Autowired
    private StockAndFundMapper stockAndFundMapper;

    private static final Logger logger = LoggerFactory.getLogger(FundServiceImpl.class);

    private static Gson gson = new Gson();

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
                String result = HttpClientPoolUtil.getHttpClient()
                    .get("http://fundgz.1234567.com.cn/js/" + code + ".js?rt=" + System.currentTimeMillis());
                String json = result.substring(8, result.length() - 2);
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
                    logger.info("Fund编码:[" + code + "]信息：{}", bean);
                } else {
                    logger.info("Fund编码:[" + code + "]无法获取数据");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return funds;
    }

    @Override
    public void saveFund(String fund) {
        stockAndFundMapper.update(StockAndFund.builder().stockAndFundInfo(fund).type(2).build());
    }

    @Override
    public List<String> getFundList() {
        String fund = stockAndFundMapper.findByType(2).getStockAndFundInfo();
        logger.info("表中基金为：{}", fund);
        if (StringUtils.isEmpty(fund)) {
            return new ArrayList<>();
        }
        String[] fundArr = fund.split(";");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < fundArr.length; i++) {
            list.add(fundArr[i]);
        }
        return list;
    }

    @Override
    public String getFund() {
        return (String) stockAndFundMapper.findByType(2).getStockAndFundInfo();
    }
}
