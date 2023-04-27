package com.buxuesong.account.domain.model.stock;

import com.buxuesong.account.apis.model.request.BuyOrSellStockRequest;
import com.buxuesong.account.apis.model.request.StockRequest;
import com.buxuesong.account.domain.service.CacheService;
import com.buxuesong.account.infrastructure.adapter.rest.GTimgRestClient;
import com.buxuesong.account.infrastructure.adapter.rest.SinaRestClient;
import com.buxuesong.account.infrastructure.adapter.rest.response.ShareBonusResponse;
import com.buxuesong.account.infrastructure.adapter.rest.response.StockDayHistoryResponse;
import com.buxuesong.account.infrastructure.general.utils.DateTimeUtils;
import com.buxuesong.account.infrastructure.persistent.po.BuyOrSellStockPO;
import com.buxuesong.account.infrastructure.persistent.po.StockHisPO;
import com.buxuesong.account.infrastructure.persistent.po.StockPO;
import com.buxuesong.account.infrastructure.persistent.repository.BuyOrSellMapper;
import com.buxuesong.account.infrastructure.persistent.repository.StockHisMapper;
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
    private List<BuyOrSellStockPO> buyOrSellStockRequestList;

    private boolean hide;// 是否隐藏

    // 唐安奇通道法，最近20个交易日最高价格以及最低价格
    private String day20Max;
    private String day20Min;

    private String day10Min;

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

    public String getDay20Max() {
        return day20Max;
    }

    public void setDay20Max(String day20Max) {
        this.day20Max = day20Max;
    }

    public String getDay20Min() {
        return day20Min;
    }

    public void setDay20Min(String day20Min) {
        this.day20Min = day20Min;
    }

    public String getDay10Min() {
        return day10Min;
    }

    public void setDay10Min(String day10Min) {
        this.day10Min = day10Min;
    }

    public List<BuyOrSellStockPO> getBuyOrSellStockRequestList() {
        return buyOrSellStockRequestList;
    }

    public void setBuyOrSellStockRequestList(List<BuyOrSellStockPO> buyOrSellStockRequestList) {
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
    private StockHisMapper stockHisMapper;

    @Autowired
    private BuyOrSellMapper buyOrSellMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private GTimgRestClient gTimgRestClient;

    @Autowired
    private SinaRestClient sinaRestClient;

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
                result = cacheService.getStockInfo(urlPara);
            }

            log.info("获取股票信息 {}", result);
            String[] lines = result.split("\n");
            List<BuyOrSellStockPO> buyOrSellStockPOs = buyOrSellMapper.findAllBuyOrSellStocksByDate(LocalDate.now().toString());
            log.info("当日买卖的股票信息 {}", buyOrSellStockPOs);
            for (String line : lines) {
                String code = line.substring(line.indexOf("_") + 1, line.indexOf("="));
                String dataStr = line.substring(line.indexOf("=") + 2, line.length() - 2);
                String[] values = dataStr.split("~");
                StockEntity bean = new StockEntity(code, codeMap);
                BigDecimal now = new BigDecimal(values[3]).setScale(2, RoundingMode.HALF_UP);
                bean.setName(values[1]);
                bean.setNow(now + "");
                bean.setChange(new BigDecimal(values[31]).setScale(2, RoundingMode.HALF_UP) + "");
                bean.setChangePercent(values[32]);
                bean.setTime(values[30]);
                bean.setMax(new BigDecimal(values[33]).setScale(2, RoundingMode.HALF_UP) + "");// 33
                bean.setMin(new BigDecimal(values[34]).setScale(2, RoundingMode.HALF_UP) + "");// 34
                bean.setBuyOrSellStockRequestList(buyOrSellStockPOs.stream().filter(s -> s.getCode().equals(code))
                    .collect(Collectors.toList()));

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
                // 增加20日最高最低价格
                List<StockDayHistoryResponse> stockDayHistory20 = cacheService.getStockDayHistory(code, "20");
                log.info("Stock day history is {}", stockDayHistory20);
                if (stockDayHistory20 != null && stockDayHistory20.size() >= 20) {
                    List<StockDayHistoryResponse> stockDayHistory10 = stockDayHistory20.subList(10, 20);
                    StockDayHistoryResponse maxStockDay = stockDayHistory20.stream()
                        .max((s1, s2) -> Double.compare(s1.getHigh(), s2.getHigh()))
                        .get();
                    StockDayHistoryResponse minStockDay20 = stockDayHistory20.stream()
                        .min((s1, s2) -> Double.compare(s1.getLow(), s2.getLow()))
                        .get();
                    StockDayHistoryResponse minStockDay10 = stockDayHistory10.stream()
                        .min((s1, s2) -> Double.compare(s1.getLow(), s2.getLow()))
                        .get();
                    log.info("Stock day history max is {}, 20 day low is {}, 10 day low is {}", maxStockDay, minStockDay20, minStockDay10);
                    bean.setDay20Max(maxStockDay.getHigh() + "");
                    bean.setDay20Min(minStockDay20.getLow() + "");
                    bean.setDay10Min(minStockDay10.getLow() + "");
                } else {
                    bean.setDay20Max(values[3]);
                    bean.setDay20Min(values[3]);
                    bean.setDay10Min(values[3]);
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
            stockRequest.setName(values[1]);
        } catch (Exception e) {
            log.info("获取股票信息异常 {}", e.getMessage());
            return false;
        }
        StockPO stockPOFromTable = stockMapper.findStockByCode(stockRequest.getCode());
        if (stockPOFromTable != null) {
            stockHisMapper.saveFromStock(stockRequest.getCode());
            stockMapper.updateStock(StockPO.builder().name(stockRequest.getName()).app(stockRequest.getApp()).bonds(stockRequest.getBonds())
                .code(stockRequest.getCode())
                .costPrise(stockRequest.getCostPrise()).hide(stockRequest.getHide()).build());
        } else {
            stockMapper.save(StockPO.builder().name(stockRequest.getName()).app(stockRequest.getApp()).bonds(stockRequest.getBonds())
                .code(stockRequest.getCode())
                .costPrise(stockRequest.getCostPrise()).hide(stockRequest.getHide()).build());
        }
        return true;
    }

    public void deleteStock(StockRequest stockRequest) {
        stockHisMapper.saveFromStock(stockRequest.getCode());
        stockMapper.deleteStock(StockPO.builder().app(stockRequest.getApp()).bonds(stockRequest.getBonds()).code(stockRequest.getCode())
            .costPrise(stockRequest.getCostPrise()).hide(stockRequest.getHide()).build());
    }

    public List<String> getStockList(String app) {
        List<StockPO> stock = stockMapper.findAllStock(app);
        log.info("APP: {} ,数据库中的股票为：{}", app, stock);
        if (stock == null || stock.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>();
        for (StockPO stockPO : stock) {
            String stockArr = stockPO.getCode() + "," + stockPO.getCostPrise() + "," + stockPO.getBonds() + ","
                + stockPO.getApp() + "," + stockPO.isHide();
            list.add(stockArr);
        }
        return list;
    }

    public List<StockHisPO> getStockHisList(String app, String code, String beginDate, String endDate) {
        List<StockHisPO> stockHis = stockHisMapper.findAllStockHis(app, code, beginDate, endDate);
        log.info("APP: {} ,数据库中的股票历史为：{}", app, stockHis);
        return stockHis;
    }

    public List<BuyOrSellStockPO> getBuyOrSellStocks(String code, String beginDate, String endDate) {
        List<BuyOrSellStockPO> buyOrSellStockPOS = buyOrSellMapper.findAllBuyOrSellStocks(code, beginDate, endDate);
        log.info("数据库中的买卖历史为：{}", buyOrSellStockPOS);
        return buyOrSellStockPOS;
    }

    public void buyOrSellStock(BuyOrSellStockRequest buyOrSellStockRequest) {
        StockPO stockPO = stockMapper.findStockByCode(buyOrSellStockRequest.getCode());
        List<String> list = new ArrayList<>();
        list.add(stockPO.getCode() + "," + stockPO.getCostPrise() + "," + stockPO.getBonds() + ","
            + stockPO.getApp() + "," + stockPO.isHide());
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
        buyOrSellMapper.save(BuyOrSellStockPO.builder().code(buyOrSellStockRequest.getCode()).type(buyOrSellStockRequest.getType())
            .cost(buyOrSellStockRequest.getCost())
            .date(buyOrSellStockRequest.getDate()).price(buyOrSellStockRequest.getPrice())
            .bonds(buyOrSellStockRequest.getBonds()).app(buyOrSellStockRequest.getApp())
            .income(buyOrSellStockRequest.getIncome()).openPrice(buyOrSellStockRequest.getOpenPrice())
            .build());
        // 购买
        if ("1".equals(buyOrSellStockRequest.getType())) {
            int restBound = 0;
            BigDecimal newCostPrice;
            // 说明未持有该股票新买入
            if (stockPO == null) {
                restBound = buyOrSellStockRequest.getBonds();
                newCostPrice = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                    .add(buyOrSellStockRequest.getCost())
                    .divide(new BigDecimal(restBound), 3, BigDecimal.ROUND_HALF_UP);
                // 说明持有该股票再次买入
            } else {
                restBound = stockPO.getBonds() + buyOrSellStockRequest.getBonds();
                BigDecimal newBuyTotalFee = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                    .add(buyOrSellStockRequest.getCost());
                newCostPrice = stockPO.getCostPrise().multiply(new BigDecimal(stockPO.getBonds())).add(newBuyTotalFee)
                    .divide(new BigDecimal(restBound), 3, BigDecimal.ROUND_HALF_UP);
            }
            stockPO.setBonds(restBound);
            stockPO.setCostPrise(newCostPrice);
            // 卖出
        } else {
            int restBound = stockPO.getBonds() - buyOrSellStockRequest.getBonds();
            BigDecimal newSellTotalFee = buyOrSellStockRequest.getPrice().multiply(new BigDecimal(buyOrSellStockRequest.getBonds()))
                .subtract(buyOrSellStockRequest.getCost());
            BigDecimal newCostPrice = new BigDecimal("0");
            if (restBound != 0) {
                newCostPrice = stockPO.getCostPrise().multiply(new BigDecimal(stockPO.getBonds()))
                    .subtract(newSellTotalFee).divide(new BigDecimal(restBound), 3, BigDecimal.ROUND_HALF_UP);
            }
            stockPO.setBonds(restBound);
            stockPO.setCostPrise(newCostPrice);
        }
        stockHisMapper.saveFromStock(stockPO.getCode());
        log.info("买卖后的stockPO： {}", stockPO);
        stockMapper.updateStock(stockPO);
    }

    public String computeStock(String code, String dataLen) {
        List<StockDayHistoryResponse> stockDayHistory = cacheService.getStockDayHistory(code, dataLen);
        Map<String, ShareBonusResponse> shareBonusResponseMap = sinaRestClient
            .getShareBonusHistory(code.replaceAll("sh", "").replace("sz", ""));
        boolean haveOne = false;
        BigDecimal buyPrice = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        // 持有多少股
        BigDecimal bonds = BigDecimal.ZERO;
        StockDayHistoryResponse currentStockDay = null;
        int totalCnt = 0;
        int successCnt = 0;
        // 从第21天开始计算
        for (int i = 20; i < stockDayHistory.size(); i++) {
            List<StockDayHistoryResponse> stockDayHistory20 = stockDayHistory.subList(i - 20, i);
            List<StockDayHistoryResponse> stockDayHistory10 = stockDayHistory.subList(i - 10, i);
            currentStockDay = stockDayHistory.get(i);
            StockDayHistoryResponse maxStockDay = stockDayHistory20.stream()
                .max(Comparator.comparingDouble(StockDayHistoryResponse::getHigh))
                .get();
            StockDayHistoryResponse minStockDay = stockDayHistory10.stream()
                .min(Comparator.comparingDouble(StockDayHistoryResponse::getLow))
                .get();
            // 未持有，并且突破20日新高，买入
            if (!haveOne && currentStockDay.getHigh() > maxStockDay.getHigh()) {
                buyPrice = (new BigDecimal(currentStockDay.getHigh())).add(new BigDecimal(currentStockDay.getLow()))
                    .divide(new BigDecimal(2));
                haveOne = true;
                bonds = new BigDecimal(100);
                log.info("Buy one at : {} , price is {}", currentStockDay.getDay(), buyPrice.setScale(2, BigDecimal.ROUND_UP));
            }
            // 已经持有，并且当前日期分红除权，重新计算
            if (haveOne && shareBonusResponseMap.get(currentStockDay.getDay()) != null) {
                ShareBonusResponse shareBonusResponse = shareBonusResponseMap.get(currentStockDay.getDay());
                // 有分红
                if (shareBonusResponse.getShareMoney().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal income = shareBonusResponse.getShareMoney().multiply(new BigDecimal(10));
                    buyPrice = buyPrice.subtract(shareBonusResponse.getShareMoney().divide(new BigDecimal(10)));
                    totalIncome = totalIncome.add(income);
                    log.info("Share bonus at : {} , income is {}, total income is {}, new buy price is {}", currentStockDay.getDay(),
                        income.setScale(2, BigDecimal.ROUND_UP), totalIncome.setScale(2, BigDecimal.ROUND_UP),
                        buyPrice.setScale(2, BigDecimal.ROUND_UP));
                }
                // 有转股
                if (shareBonusResponse.getShareStock().compareTo(BigDecimal.ZERO) > 0) {
                    bonds = bonds.add(shareBonusResponse.getShareStock().multiply(new BigDecimal(10)));
                    log.info("Share stock at : {} , new bonds is {}", currentStockDay.getDay(), bonds.setScale(2, BigDecimal.ROUND_UP));
                }
                // 有赠股
                if (shareBonusResponse.getSendStock().compareTo(BigDecimal.ZERO) > 0) {
                    bonds = bonds.add(shareBonusResponse.getSendStock().multiply(new BigDecimal(10)));
                    log.info("Share stock at : {} , new bonds is {}", currentStockDay.getDay(), bonds.setScale(2, BigDecimal.ROUND_UP));
                }
            }
            // 已经持有，并且持有达到突破10/20日最低价格卖出
            if (haveOne && currentStockDay.getLow() < minStockDay.getLow()) {
                BigDecimal sellPrice = (new BigDecimal(currentStockDay.getHigh())).add(new BigDecimal(currentStockDay.getLow()))
                    .divide(new BigDecimal(2));
                BigDecimal income = sellPrice.multiply(bonds).subtract(buyPrice.multiply(new BigDecimal(100)));
                totalIncome = totalIncome.add(income);
                haveOne = false;
                bonds = BigDecimal.ZERO;
                totalCnt++;
                if (income.compareTo(BigDecimal.ZERO) > 0) {
                    successCnt++;
                }
                log.info("Sell one at : {} , sell price is {} , income is {}, total income is {}", currentStockDay.getDay(),
                    sellPrice.setScale(2, BigDecimal.ROUND_UP), income.setScale(2, BigDecimal.ROUND_UP),
                    totalIncome.setScale(2, BigDecimal.ROUND_UP));
            }
        }
        if (haveOne) {
            totalIncome = totalIncome
                .add((new BigDecimal(currentStockDay.getHigh())).add(new BigDecimal(currentStockDay.getLow())).divide(new BigDecimal(2)));
        }
        log.info("successCnt is {}, totalCnt is {}", successCnt, totalCnt);
        BigDecimal successRate = (new BigDecimal(successCnt)).divide(new BigDecimal(totalCnt), 4, BigDecimal.ROUND_UP)
            .multiply(new BigDecimal(100));
        log.info("Total income is {}, success rate is {}%", totalIncome.setScale(2, BigDecimal.ROUND_UP),
            successRate.setScale(2, BigDecimal.ROUND_UP));
        return String.format("Total income is %s, success rate is %s%%", totalIncome.setScale(2, BigDecimal.ROUND_UP),
            successRate.setScale(2, BigDecimal.ROUND_UP));
    }

}
