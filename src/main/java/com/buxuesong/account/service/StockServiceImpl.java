package com.buxuesong.account.service;

import com.buxuesong.account.model.StockBean;
import com.buxuesong.account.persist.dao.StockAndFundMapper;
import com.buxuesong.account.persist.entity.StockAndFund;
import com.buxuesong.account.util.HttpClientPoolUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
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

@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    public final static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static Gson gson = new Gson();

    @Autowired
    private StockAndFundMapper stockAndFundMapper;

    @Override
    public List<StockBean> getStockDetails(List<String> codes) {
        List<StockBean> stocks = new ArrayList<>();
        List<String> codeList = new ArrayList<>();
        HashMap<String, String[]> codeMap = new HashMap<>();
        for (String str : codes) {
            // 兼容原有设置
            String[] strArray;
            if (str.contains(",")) {
                strArray = str.split(",");
            } else {
                strArray = new String[] { str };
            }
            codeList.add(strArray[0]);
            codeMap.put(strArray[0], strArray);
        }

        String urlPara = String.join(",", codeList);

        try {
            String result = HttpClientPoolUtil.getHttpClient().get("http://qt.gtimg.cn/q=" + urlPara);
            String[] lines = result.split("\n");
            for (String line : lines) {
                String code = line.substring(line.indexOf("_") + 1, line.indexOf("="));
                String dataStr = line.substring(line.indexOf("=") + 2, line.length() - 2);
                String[] values = dataStr.split("~");
                StockBean bean = new StockBean(code, codeMap);
                bean.setName(values[1]);
                bean.setNow(values[3]);
                bean.setChange(values[31]);
                bean.setChangePercent(values[32]);
                bean.setTime(values[30]);
                bean.setMax(values[33]);// 33
                bean.setMin(values[34]);// 34

                BigDecimal now = new BigDecimal(values[3]);
                String costPriceStr = bean.getCostPrise();
                if (StringUtils.isNotEmpty(costPriceStr)) {
                    BigDecimal costPriceDec = new BigDecimal(costPriceStr);
                    BigDecimal incomeDiff = now.add(costPriceDec.negate());
                    if (costPriceDec.compareTo(BigDecimal.ZERO) <= 0) {
                        bean.setIncomePercent("0");
                    } else {
                        BigDecimal incomePercentDec = incomeDiff.divide(costPriceDec, 5, RoundingMode.HALF_UP)
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
                stocks.add(bean);
            }
        } catch (Exception e) {

        }
        return stocks;
    }

    @Override
    public void saveStock(String stock) {
        stockAndFundMapper.update(StockAndFund.builder().stockAndFundInfo(stock).type(1).build());
    }

    @Override
    public List<String> getStockList() {
        String stock = stockAndFundMapper.findByType(1).getStockAndFundInfo();
        logger.info("缓存的股票为：{}", stock);
        if (StringUtils.isEmpty(stock)) {
            return new ArrayList<>();
        }
        String[] stockArr = stock.split(";");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < stockArr.length; i++) {
            list.add(stockArr[i]);
        }
        return list;
    }

    @Override
    public String getStock() {
        return (String) stockAndFundMapper.findByType(1).getStockAndFundInfo();
    }
}
