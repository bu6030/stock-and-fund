package com.buxuesong.account.scheduler;

import com.buxuesong.account.model.FundBean;
import com.buxuesong.account.model.StockBean;
import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.persist.dao.DepositMapper;
import com.buxuesong.account.persist.entity.Deposit;
import com.buxuesong.account.service.FundService;
import com.buxuesong.account.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositScheduler {
    @Autowired
    private StockService stockService;
    @Autowired
    private FundService fundService;
    @Autowired
    private DepositMapper depositMapper;

    // 每天15点30分统计当日盈亏
    @Scheduled(cron = "0 30 15 * * ?")
    public void autoStar() {
        log.info("======= DepositScheduler started =======");
        execute();
        log.info("======= DepositScheduler finished =======");
    }

    private void execute() {
        BigDecimal fundTotalDayIncome = depositFund();
        BigDecimal stockTotalDayIncome = depositStock();
        BigDecimal totalDayIncome = stockTotalDayIncome.add(fundTotalDayIncome);
        log.info("当日盈利: {}", totalDayIncome);
        depositMapper.save(Deposit.builder().date(LocalDate.now().toString()).fundDayIncome(fundTotalDayIncome)
            .stockDayIncome(stockTotalDayIncome).totalDayIncome(totalDayIncome).build());
    }

    private BigDecimal depositFund() {
        List<String> fundListFrom = fundService.getFundList();
        List<FundBean> funds = fundService.getFundDetails(fundListFrom);
        BigDecimal fundTotalDayIncome = new BigDecimal("0");
        for (FundBean fund : funds) {
            BigDecimal dayIncome = new BigDecimal(fund.getGszzl())
                .multiply(new BigDecimal(fund.getDwjz())).multiply(new BigDecimal(fund.getBonds()))
                .divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            fundTotalDayIncome = fundTotalDayIncome.add(dayIncome);
        }
        System.out.println(fundTotalDayIncome);
        log.info("基金当日盈利: {}", fundTotalDayIncome);
        return fundTotalDayIncome;
    }

    private BigDecimal depositStock() {
        List<String> stockListFrom = stockService.getStockList();
        List<StockBean> stocks = stockService.getStockDetails(stockListFrom);
        BigDecimal stockTotalDayIncome = new BigDecimal("0");
        for (StockBean stock : stocks) {
            BigDecimal dayIncome = (new BigDecimal(stock.getChange())).multiply(new BigDecimal(stock.getBonds())).setScale(2,
                BigDecimal.ROUND_HALF_UP);
            stockTotalDayIncome = stockTotalDayIncome.add(dayIncome);
        }
        log.info("股票当日盈利: {}", stockTotalDayIncome);
        return stockTotalDayIncome;

    }
}
