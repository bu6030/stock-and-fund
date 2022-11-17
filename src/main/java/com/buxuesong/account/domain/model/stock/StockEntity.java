package com.buxuesong.account.domain.model.stock;

import com.buxuesong.account.apis.model.request.BuyOrSellStockRequest;
import com.buxuesong.account.apis.model.request.StockRequest;
import com.buxuesong.account.domain.service.StockCacheService;
import com.buxuesong.account.infrastructure.adapter.rest.GTimgRestClient;
import com.buxuesong.account.infrastructure.general.utils.DateTimeUtils;
import com.buxuesong.account.infrastructure.persistent.repository.BuyOrSellMapper;
import com.buxuesong.account.infrastructure.persistent.repository.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockEntity {
    private String code;
    private String name;
    private String now;
    private String change;// 涨跌
    private String changePercent;
    private String time;
    /**
     * 最高价
     */
    private String max;
    /**
     * 最低价
     */
    private String min;

    private String costPrise;// 成本价

    private String bonds;// 持仓
    private String app;// 支付宝/东方财富/东方证券
    private String incomePercent;// 收益率
    private String income;// 收益
    private List<BuyOrSellStockRequest> buyOrSellStockRequestList;

    private boolean hide;// 是否隐藏

    public StockEntity() {
    }

    // 配置code同时配置成本价和成本值
    public StockEntity(String code) {
        if (StringUtils.isNotBlank(code)) {
            String[] codeStr = code.split(",");
            if (codeStr.length > 2) {
                this.code = codeStr[0];
                this.costPrise = codeStr[1];
                this.bonds = codeStr[2];
            } else {
                this.code = codeStr[0];
                this.costPrise = "--";
                this.bonds = "--";
            }
        } else {
            this.code = code;
        }
        this.name = "--";
    }

    public StockEntity(String code, Map<String, String[]> codeMap) {
        this.code = code;
        if (codeMap.containsKey(code)) {
            String[] codeStr = codeMap.get(code);
            if (codeStr.length > 3) {
                this.code = codeStr[0];
                this.costPrise = codeStr[1];
                this.bonds = codeStr[2];
                this.app = codeStr[3];
                this.hide = Boolean.parseBoolean(codeStr[4]);
            } else {
                this.code = codeStr[0];
                this.costPrise = codeStr[1];
                this.bonds = codeStr[2];
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getCostPrise() {
        return costPrise;
    }

    public void setCostPrise(String costPrise) {
        this.costPrise = costPrise;
    }

    public String getBonds() {
        return bonds;
    }

    public void setBonds(String bonds) {
        this.bonds = bonds;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getIncomePercent() {
        return incomePercent;
    }

    public void setIncomePercent(String incomePercent) {
        this.incomePercent = incomePercent;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public List<BuyOrSellStockRequest> getBuyOrSellStockRequestList() {
        return buyOrSellStockRequestList;
    }

    public void setBuyOrSellStockRequestList(List<BuyOrSellStockRequest> buyOrSellStockRequestList) {
        this.buyOrSellStockRequestList = buyOrSellStockRequestList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        StockEntity bean = (StockEntity) o;
        return Objects.equals(code, bean.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "StockBean{" +
            "code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", now='" + now + '\'' +
            ", change='" + change + '\'' +
            ", changePercent='" + changePercent + '\'' +
            ", time='" + time + '\'' +
            ", max='" + max + '\'' +
            ", min='" + min + '\'' +
            ", costPrise='" + costPrise + '\'' +
            ", bonds='" + bonds + '\'' +
            ", app='" + app + '\'' +
            ", incomePercent='" + incomePercent + '\'' +
            ", income='" + income + '\'' +
            ", hide='" + hide + '\'' +
            ", buyOrSellStockRequestList=" + buyOrSellStockRequestList +
            '}';
    }




    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private BuyOrSellMapper buyOrSellMapper;

    @Autowired
    private StockCacheService stockCacheService;

    @Autowired
    private GTimgRestClient gTimgRestClient;

    public List<StockEntity> getStockDetails(List<String> codes) {
        List<StockEntity> stocks = new ArrayList<>();
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
                result = gTimgRestClient.getStockInfo(urlPara);
            } else {
                result = stockCacheService.getStockInfo(urlPara);
            }

            log.info("获取股票信息 {}", result);
            String[] lines = result.split("\n");
            List<BuyOrSellStockRequest> buyOrSellStockRequests = buyOrSellMapper.findAllBuyOrSellStocks(LocalDate.now().toString());
            log.info("当日买卖的股票信息 {}", buyOrSellStockRequests);
            for (String line : lines) {
                String code = line.substring(line.indexOf("_") + 1, line.indexOf("="));
                String dataStr = line.substring(line.indexOf("=") + 2, line.length() - 2);
                String[] values = dataStr.split("~");
                StockEntity bean = new StockEntity(code, codeMap);
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

    public boolean saveStock(StockRequest stockRequest) {
        try {
            String result = gTimgRestClient.getStockInfo(stockRequest.getCode());
            String code = result.substring(result.indexOf("_") + 1, result.indexOf("="));
            String dataStr = result.substring(result.indexOf("=") + 2, result.length() - 2);
            String[] values = dataStr.split("~");
            log.info("添加股票名称 {}", values[1]);
        } catch (Exception e) {
            log.info("获取股票信息异常 {}", e.getMessage());
            return false;
        }
        StockRequest stockRequestFromTable = stockMapper.findStockByCode(stockRequest.getCode());
        if (stockRequestFromTable != null) {
            stockMapper.updateStock(stockRequest);
        } else {
            stockMapper.save(stockRequest);
        }
        return true;
    }

    public void deleteStock(StockRequest stockRequest) {
        stockMapper.deleteStock(stockRequest);
    }

    public List<String> getStockList(String app) {
        List<StockRequest> stock = stockMapper.findAllStock(app);
        log.info("APP: {} ,数据库中的股票为：{}", app, stock);
        if (stock == null || stock.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (StockRequest stockRequest : stock) {
            String stockArr = stockRequest.getCode() + "," + stockRequest.getCostPrise() + "," + stockRequest.getBonds() + ","
                    + stockRequest.getApp() + "," + stockRequest.getHide();
            list.add(stockArr);
        }
        return list;
    }

    public StockRequest findStockByCode(String code) {
        return stockMapper.findStockByCode(code);
    }

    public void buyOrSellStock(BuyOrSellStockRequest buyOrSellStockRequest) {
        StockRequest stockRequest = stockMapper.findStockByCode(buyOrSellStockRequest.getCode());
        List<String> list = new ArrayList<>();
        list.add(stockRequest.getCode() + "," + stockRequest.getCostPrise() + "," + stockRequest.getBonds() + ","
                + stockRequest.getApp() + "," + stockRequest.getHide());
        StockEntity stock = getStockDetails(list).get(0);
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
        StockRequest saveStockRequest = stockMapper.findStockByCode(buyOrSellStockRequest.getCode());
        // 购买
        if ("1".equals(buyOrSellStockRequest.getType())) {
            int restBound = 0;
            BigDecimal newCostPrice;
            // 说明未持有该股票新买入
            if (saveStockRequest == null) {
                restBound = buyOrSellStockRequest.getBonds();
                newCostPrice = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                        .add(buyOrSellStockRequest.getCost())
                        .divide(new BigDecimal(restBound), 3, BigDecimal.ROUND_HALF_UP);
                // 说明持有该股票再次买入
            } else {
                restBound = saveStockRequest.getBonds() + buyOrSellStockRequest.getBonds();
                BigDecimal newBuyTotalFee = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                        .add(buyOrSellStockRequest.getCost());
                newCostPrice = saveStockRequest.getCostPrise().multiply(new BigDecimal(saveStockRequest.getBonds())).add(newBuyTotalFee)
                        .divide(new BigDecimal(restBound), 3, BigDecimal.ROUND_HALF_UP);
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
                        .subtract(newSellTotalFee).divide(new BigDecimal(restBound), 3, BigDecimal.ROUND_HALF_UP);
            }
            saveStockRequest.setBonds(restBound);
            saveStockRequest.setCostPrise(newCostPrice);
        }
        log.info("买卖后的saveStockRequest： {}", saveStockRequest);
        stockMapper.updateStock(saveStockRequest);
    }
}
