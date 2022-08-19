package com.buxuesong.account.service;

import com.buxuesong.account.model.FundBean;
import com.buxuesong.account.model.StockBean;
import com.buxuesong.account.persist.dao.DepositMapper;
import com.buxuesong.account.persist.entity.Deposit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class DepositServiceImpl implements DepositService {
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private FundService fundService;
    @Autowired
    private StockService stockService;

    @Override
    public Deposit getDepositByDate(String date) {
        return depositMapper.findDepositByDate(date);
    }

    @Override
    public void deposit() {
        LocalDate date = LocalDate.now();
        Deposit deposit = depositMapper.findDepositByDate(date.toString());
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
        depositMapper.save(Deposit
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

    @Override
    public void deleteDeposit() {
        LocalDate date = LocalDate.now();
        log.info("Delete deposit date : {}", date);
        depositMapper.deleteDeposit(date.toString());
    }

    @Override
    public List<Deposit> getDepositList(String beginDate, String endDate) {
        List<Deposit> list = depositMapper.getDepositList(beginDate, endDate);
        log.info("Get deposit list between {} and {} : {}", beginDate, endDate, list);
        return list;
    }

    private BigDecimal depositFundDayIncome() {
        List<String> fundListFrom = fundService.getFundList();
        List<FundBean> funds = fundService.getFundDetails(fundListFrom);
        BigDecimal fundTotalDayIncome = new BigDecimal("0");
        for (FundBean fund : funds) {
            BigDecimal dayIncome = (new BigDecimal(fund.getGszzl())
                .multiply(new BigDecimal(fund.getDwjz())).multiply(new BigDecimal(fund.getBonds()))
                .divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
            fundTotalDayIncome = fundTotalDayIncome.add(dayIncome);
        }
        log.info("基金当日盈利: {}", fundTotalDayIncome);
        return fundTotalDayIncome;
    }

    private BigDecimal depositStockDayIncome() {
        List<String> stockListFrom = stockService.getStockList();
        List<StockBean> stocks = stockService.getStockDetails(stockListFrom);
        BigDecimal stockTotalDayIncome = new BigDecimal("0");
        for (StockBean stock : stocks) {
            BigDecimal dayIncome = (new BigDecimal(stock.getChange()).multiply(new BigDecimal(stock.getBonds()))).setScale(2,
                BigDecimal.ROUND_HALF_UP);
            stockTotalDayIncome = stockTotalDayIncome.add(dayIncome);
        }
        log.info("股票当日盈利: {}", stockTotalDayIncome);
        return stockTotalDayIncome;

    }

    private BigDecimal depositFundMarketValue() {
        List<String> fundListFrom = fundService.getFundList();
        List<FundBean> funds = fundService.getFundDetails(fundListFrom);
        BigDecimal fundTotalMarketValue = new BigDecimal("0");
        for (FundBean fund : funds) {
            BigDecimal marketValue = (new BigDecimal(fund.getGsz())
                .multiply(new BigDecimal(fund.getBonds())).setScale(2, BigDecimal.ROUND_HALF_UP));
            fundTotalMarketValue = fundTotalMarketValue.add(marketValue);
        }
        log.info("基金总市值: {}", fundTotalMarketValue);
        return fundTotalMarketValue;
    }

    private BigDecimal depositStockMarketValue() {
        List<String> stockListFrom = stockService.getStockList();
        List<StockBean> stocks = stockService.getStockDetails(stockListFrom);
        BigDecimal stockTotalMarketValue = new BigDecimal("0");
        for (StockBean stock : stocks) {
            BigDecimal marketValue = (new BigDecimal(stock.getNow()).multiply(new BigDecimal(stock.getBonds()))).setScale(2,
                BigDecimal.ROUND_HALF_UP);
            stockTotalMarketValue = stockTotalMarketValue.add(marketValue);
        }
        log.info("股票总市值: {}", stockTotalMarketValue);
        return stockTotalMarketValue;

    }
}
