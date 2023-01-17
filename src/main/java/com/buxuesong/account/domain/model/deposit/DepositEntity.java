package com.buxuesong.account.domain.model.deposit;

import com.buxuesong.account.apis.model.request.BuyOrSellStockRequest;
import com.buxuesong.account.domain.service.CacheService;
import com.buxuesong.account.infrastructure.adapter.rest.SzseRestClient;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.domain.model.stock.StockEntity;
import com.buxuesong.account.infrastructure.adapter.rest.response.TradingDateResponse;
import com.buxuesong.account.infrastructure.persistent.po.BuyOrSellStockPO;
import com.buxuesong.account.infrastructure.persistent.po.DepositPO;
import com.buxuesong.account.infrastructure.persistent.repository.DepositMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class DepositEntity {
    @Autowired
    private SzseRestClient szseRestClient;
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private FundEntity fundEntity;
    @Autowired
    private StockEntity stockEntity;
    @Autowired
    private CacheService cacheService;

    public DepositPO getDepositByDate(String date) {
        return depositMapper.findDepositByDate(date);
    }

    public void deposit() {
        cacheService.removeAllCache();
        LocalDate date = LocalDate.now();
        if (!isTradingDate(date.toString())) {
            log.info("非交易日期，不统计");
            return;
        }

        DepositPO deposit = depositMapper.findDepositByDate(date.toString());
        if (deposit != null) {
            log.info("已经存在当日盈利汇总： {}", deposit);
            return;
        }

        BigDecimal fundTotalDayIncome = depositFundDayIncome();
        BigDecimal stockTotalDayIncome = depositStockDayIncome();
        BigDecimal totalDayIncome = stockTotalDayIncome.add(fundTotalDayIncome);

        BigDecimal fundTotalMarketValue = depositFundMarketValue();
        BigDecimal stockTotalMarketValue = depositStockMarketValue();
        BigDecimal totalMarketValue = stockTotalMarketValue.add(fundTotalMarketValue);
        log.info("当日盈利: {}, 总市值: {}", totalDayIncome, totalMarketValue);
        depositMapper.save(DepositPO
            .builder()
            .date(LocalDate.now().toString())
            .fundDayIncome(fundTotalDayIncome)
            .stockDayIncome(stockTotalDayIncome)
            .totalDayIncome(totalDayIncome)
            .fundMarketValue(fundTotalMarketValue)
            .stockMarketValue(stockTotalMarketValue)
            .totalMarketValue(totalMarketValue)
            .build());
    }

    public void deleteDeposit() {
        LocalDate date = LocalDate.now();
        log.info("Delete deposit date : {}", date);
        depositMapper.deleteDeposit(date.toString());
    }

    public List<DepositPO> getDepositList(String beginDate, String endDate) {
        List<DepositPO> list = depositMapper.getDepositList(beginDate, endDate);
        log.info("Get deposit list between {} and {} : {}", beginDate, endDate, list);
        return list;
    }

    private BigDecimal depositFundDayIncome() {
        List<String> fundListFrom = fundEntity.getFundList(null);
        List<FundEntity> funds = fundEntity.getFundDetails(fundListFrom);
        BigDecimal fundTotalDayIncome = new BigDecimal("0");
        for (FundEntity fund : funds) {
            BigDecimal dayIncome = (new BigDecimal(fund.getGszzl())
                .multiply(new BigDecimal(fund.getDwjz())).multiply(new BigDecimal(fund.getBonds()))
                .divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
            fundTotalDayIncome = fundTotalDayIncome.add(dayIncome);
        }
        log.info("基金当日盈利: {}", fundTotalDayIncome);
        return fundTotalDayIncome;
    }

    private BigDecimal depositStockDayIncome() {
        List<String> stockListFrom = stockEntity.getStockList(null);
        List<StockEntity> stocks = stockEntity.getStockDetails(stockListFrom);
        BigDecimal stockTotalDayIncome = new BigDecimal("0");
        for (StockEntity stock : stocks) {
            int maxBuyOrSellBonds = 0;
            BigDecimal todayBuyIncome = new BigDecimal("0");
            BigDecimal todaySellIncom = new BigDecimal("0");
            BigDecimal dayIncome = new BigDecimal("0");
            for (BuyOrSellStockPO buyOrSellStockPO : stock.getBuyOrSellStockRequestList()) {
                // 当天购买过
                if (buyOrSellStockPO.getType().equals("1")) {
                    maxBuyOrSellBonds = maxBuyOrSellBonds + buyOrSellStockPO.getBonds();
                    log.info("买入价格: {}", buyOrSellStockPO.getPrice());
                    log.info("当前价格: {}", stock.getNow());
                    BigDecimal buyIncome = (new BigDecimal(stock.getNow()))
                        .subtract(new BigDecimal(buyOrSellStockPO.getPrice() + ""))
                        .multiply(new BigDecimal(buyOrSellStockPO.getBonds() + ""));
                    todayBuyIncome = todayBuyIncome.add(buyIncome);
                    log.info("买入收益： {}", todayBuyIncome);
                }
                // 当天卖出过
                if (buyOrSellStockPO.getType().equals("2")) {
                    todaySellIncom = todaySellIncom.add(new BigDecimal(buyOrSellStockPO.getIncome() + ""));
                    log.info("卖出收益： {}", todaySellIncom);
                }
            }
            log.info("买卖最大数: {}", maxBuyOrSellBonds);
            if (maxBuyOrSellBonds < Integer.parseInt(stock.getBonds())) {
                BigDecimal restBonds = (new BigDecimal(stock.getBonds())).subtract(new BigDecimal(maxBuyOrSellBonds + ""));
                log.info("剩余股数： {}", restBonds);
                dayIncome = (new BigDecimal(stock.getChange())).multiply(restBonds).setScale(2,
                    BigDecimal.ROUND_HALF_UP);
            } else {
                dayIncome = new BigDecimal("0").setScale(2,
                    BigDecimal.ROUND_HALF_UP);
                ;
            }
            dayIncome = dayIncome.add(todayBuyIncome).add(todaySellIncom);
            stockTotalDayIncome = stockTotalDayIncome.add(dayIncome);
        }
        log.info("股票当日盈利: {}", stockTotalDayIncome);
        return stockTotalDayIncome;

    }

    private BigDecimal depositFundMarketValue() {
        List<String> fundListFrom = fundEntity.getFundList(null);
        List<FundEntity> funds = fundEntity.getFundDetails(fundListFrom);
        BigDecimal fundTotalMarketValue = new BigDecimal("0");
        for (FundEntity fund : funds) {
            BigDecimal marketValue = (new BigDecimal(fund.getGsz())
                .multiply(new BigDecimal(fund.getBonds())).setScale(2, BigDecimal.ROUND_HALF_UP));
            fundTotalMarketValue = fundTotalMarketValue.add(marketValue);
        }
        log.info("基金总市值: {}", fundTotalMarketValue);
        return fundTotalMarketValue;
    }

    private BigDecimal depositStockMarketValue() {
        List<String> stockListFrom = stockEntity.getStockList(null);
        List<StockEntity> stocks = stockEntity.getStockDetails(stockListFrom);
        BigDecimal stockTotalMarketValue = new BigDecimal("0");
        for (StockEntity stock : stocks) {
            BigDecimal marketValue = (new BigDecimal(stock.getNow()).multiply(new BigDecimal(stock.getBonds()))).setScale(2,
                BigDecimal.ROUND_HALF_UP);
            stockTotalMarketValue = stockTotalMarketValue.add(marketValue);
        }
        log.info("股票总市值: {}", stockTotalMarketValue);
        return stockTotalMarketValue;

    }

    private boolean isTradingDate(String date) {
        List<TradingDateResponse.TradingDate> tradingDates = szseRestClient.getTradingDate().getData();
        TradingDateResponse.TradingDate tradingDate = tradingDates.stream()
            .filter(s -> date.equals(s.getJyrq()))
            .findFirst().get();
        log.info("当天交易状态: {}", tradingDate);
        if ("1".equals(tradingDate.getJybz())) {
            return true;
        }
        return false;
    }

}
