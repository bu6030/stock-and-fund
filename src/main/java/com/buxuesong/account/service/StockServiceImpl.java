package com.buxuesong.account.service;

import com.buxuesong.account.model.BuyOrSellStockRequest;
import com.buxuesong.account.model.SaveStockRequest;
import com.buxuesong.account.model.StockBean;
import com.buxuesong.account.persist.dao.BuyOrSellMapper;
import com.buxuesong.account.persist.dao.StockMapper;
import com.buxuesong.account.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private BuyOrSellMapper buyOrSellMapper;

    @Autowired
    private StockCacheService stockCacheService;

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
            String result = null;
            if (DateTimeUtils.isTradingTime()) {
                result = stockCacheService.getStockInfoFromApi(urlPara);
            } else {
                result = stockCacheService.getStockInfoFromApiCache(urlPara);
            }

            log.info("获取股票信息 {}", result);
            String[] lines = result.split("\n");
            List<BuyOrSellStockRequest> buyOrSellStockRequests = buyOrSellMapper.findAllBuyOrSellStocks(LocalDate.now().toString());
            log.info("当日买卖的股票信息 {}", buyOrSellStockRequests);
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
                bean.setBuyOrSellStockRequestList(buyOrSellStockRequests.stream().filter(s -> s.getCode().equals(code))
                    .collect(Collectors.toList()));

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
    public boolean saveStock(SaveStockRequest saveStockRequest) {
        try {
            String result = stockCacheService.getStockInfoFromApi(saveStockRequest.getCode());
            String code = result.substring(result.indexOf("_") + 1, result.indexOf("="));
            String dataStr = result.substring(result.indexOf("=") + 2, result.length() - 2);
            String[] values = dataStr.split("~");
            log.info("添加股票名称 {}", values[1]);
        } catch (Exception e) {
            log.info("获取股票信息异常 {}", e.getMessage());
            return false;
        }
        SaveStockRequest saveStockRequestFromTable = stockMapper.findStockByCode(saveStockRequest.getCode());
        if (saveStockRequestFromTable != null) {
            stockMapper.updateStock(saveStockRequest);
        } else {
            stockMapper.save(saveStockRequest);
        }
        return true;
    }

    @Override
    public void deleteStock(SaveStockRequest saveStockRequest) {
        stockMapper.deleteStock(saveStockRequest);
    }

    @Override
    public List<String> getStockList(String app) {
        List<SaveStockRequest> stock = stockMapper.findAllStock(app);
        log.info("APP: {} ,数据库中的股票为：{}", app, stock);
        if (stock == null || stock.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (SaveStockRequest stockRequest : stock) {
            String stockArr = stockRequest.getCode() + "," + stockRequest.getCostPrise() + "," + stockRequest.getBonds() + ","
                + stockRequest.getApp();
            list.add(stockArr);
        }
        return list;
    }

    public SaveStockRequest findStockByCode(String code) {
        return stockMapper.findStockByCode(code);
    }

    @Override
    public void buyOrSellStock(BuyOrSellStockRequest buyOrSellStockRequest) {
        SaveStockRequest stockRequest = stockMapper.findStockByCode(buyOrSellStockRequest.getCode());
        List<String> list = new ArrayList<>();
        list.add(stockRequest.getCode() + "," + stockRequest.getCostPrise() + "," + stockRequest.getBonds() + ","
            + stockRequest.getApp());
        StockBean stock = getStockDetails(list).get(0);
        // 开盘价格
        BigDecimal openPrice = (new BigDecimal(stock.getNow())).subtract(new BigDecimal(stock.getChange()));
        log.info("开盘价格： {}", openPrice);
        buyOrSellStockRequest.setOpenPrice(openPrice);
        // 计算卖出盈利，买入不用计算
        if ("2".equals(buyOrSellStockRequest.getType())) {
            BigDecimal income = buyOrSellStockRequest.getPrice().subtract(openPrice)
                .multiply(new BigDecimal(buyOrSellStockRequest.getBonds())).subtract(buyOrSellStockRequest.getCost());
            log.info("卖出当日收益： {}", income);
            buyOrSellStockRequest.setIncome(income);
        } else {
            buyOrSellStockRequest.setIncome(new BigDecimal("0"));
        }
        buyOrSellMapper.save(buyOrSellStockRequest);
        SaveStockRequest saveStockRequest = stockMapper.findStockByCode(buyOrSellStockRequest.getCode());
        // 购买
        if ("1".equals(buyOrSellStockRequest.getType())) {
            int restBound = 0;
            BigDecimal newCostPrice;
            // 说明未持有该股票新买入
            if (saveStockRequest == null) {
                restBound = buyOrSellStockRequest.getBonds();
                newCostPrice = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                    .add(buyOrSellStockRequest.getCost())
                    .divide(new BigDecimal(restBound), 2, BigDecimal.ROUND_HALF_UP);
                // 说明持有该股票再次买入
            } else {
                restBound = saveStockRequest.getBonds() + buyOrSellStockRequest.getBonds();
                BigDecimal newBuyTotalFee = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                    .add(buyOrSellStockRequest.getCost());
                newCostPrice = saveStockRequest.getCostPrise().multiply(new BigDecimal(saveStockRequest.getBonds())).add(newBuyTotalFee)
                    .divide(new BigDecimal(restBound), 2, BigDecimal.ROUND_HALF_UP);
            }
            saveStockRequest.setBonds(restBound);
            saveStockRequest.setCostPrise(newCostPrice);
            // 卖出
        } else {
            int restBound = saveStockRequest.getBonds() - buyOrSellStockRequest.getBonds();
            BigDecimal newSellTotalFee = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                .subtract(buyOrSellStockRequest.getCost());
            BigDecimal newCostPrice = new BigDecimal("0");
            if (restBound != 0) {
                newCostPrice = saveStockRequest.getCostPrise().multiply(new BigDecimal(saveStockRequest.getBonds()))
                    .subtract(newSellTotalFee).divide(new BigDecimal(restBound), 2, BigDecimal.ROUND_HALF_UP);
            }
            saveStockRequest.setBonds(restBound);
            saveStockRequest.setCostPrise(newCostPrice);
        }
        log.info("买卖后的saveStockRequest： {}", saveStockRequest);
        stockMapper.updateStock(saveStockRequest);
    }
}
