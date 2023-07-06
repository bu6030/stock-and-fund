package com.buxuesong.account.domain.model.deposit;

import com.buxuesong.account.domain.service.CacheService;
import com.buxuesong.account.infrastructure.adapter.rest.SzseRestClient;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.domain.model.stock.StockEntity;
import com.buxuesong.account.infrastructure.adapter.rest.response.TradingDateResponse;
import com.buxuesong.account.infrastructure.persistent.po.BuyOrSellStockPO;
import com.buxuesong.account.infrastructure.persistent.po.DepositPO;
import com.buxuesong.account.infrastructure.persistent.po.OpenPersistentMonthPO;
import com.buxuesong.account.infrastructure.persistent.po.UserPO;
import com.buxuesong.account.infrastructure.persistent.repository.DepositMapper;
import com.buxuesong.account.infrastructure.persistent.repository.OpenPersistentMonthMapper;
import com.buxuesong.account.infrastructure.persistent.repository.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private OpenPersistentMonthMapper openPersistentMonthMapper;
    @Autowired
    private UserMapper userMapper;

    private static Gson gson = new Gson();

    public DepositPO getDepositByDate(String date) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return depositMapper.findDepositByDate(date, username);
    }

    /**
     * 定时程序统计所有用户盈亏
     */
    public void depositAllUsers() {
        List<UserPO> users = userMapper.findAllUsers();
        users.forEach(user -> deposit(user.getUsername()));
    }

    /**
     * 用户手动点击计算盈亏
     */
    public void deposit() {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        deposit(username);
    }

    /**
     * 支持用户手动点击计算盈亏以及定时程序统计所有用户盈亏具体执行方法
     */
    private void deposit(String username) {
        cacheService.removeAllCache();
        LocalDate date = LocalDate.now();
        if (!isTradingDate(date.toString())) {
            log.info("非交易日期，不统计");
            return;
        }

        DepositPO deposit = depositMapper.findDepositByDate(date.toString(), username);
        if (deposit != null) {
            log.info("用户：{}, 已经存在当日盈利汇总： {}", username, deposit);
            return;
        }

        BigDecimal fundTotalDayIncome = depositFundDayIncome(username);
        BigDecimal stockTotalDayIncome = depositStockDayIncome(username);
        BigDecimal totalDayIncome = stockTotalDayIncome.add(fundTotalDayIncome);

        BigDecimal fundTotalMarketValue = depositFundMarketValue(username);
        BigDecimal stockTotalMarketValue = depositStockMarketValue(username);
        BigDecimal totalMarketValue = stockTotalMarketValue.add(fundTotalMarketValue);
        log.info("用户：{}, 当日盈利: {}, 总市值: {}", username, totalDayIncome, totalMarketValue);
        if (fundTotalMarketValue.compareTo(BigDecimal.ZERO) == 0 && stockTotalMarketValue.compareTo(BigDecimal.ZERO) == 0
                && totalMarketValue.compareTo(BigDecimal.ZERO) == 0) {
            log.info("用户：{} 没有持股，不统计", username);
            return;
        }
        depositMapper.save(DepositPO
            .builder()
            .date(LocalDate.now().toString())
            .fundDayIncome(fundTotalDayIncome)
            .stockDayIncome(stockTotalDayIncome)
            .totalDayIncome(totalDayIncome)
            .fundMarketValue(fundTotalMarketValue)
            .stockMarketValue(stockTotalMarketValue)
            .totalMarketValue(totalMarketValue)
            .build(), username);
    }

    public void deleteDeposit() {
        LocalDate date = LocalDate.now();
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        log.info("User : {} delete deposit date : {}", username, date);
        depositMapper.deleteDeposit(date.toString(), username);
    }

    public List<DepositPO> getDepositList(String beginDate, String endDate) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        List<DepositPO> list = depositMapper.getDepositList(beginDate, endDate, username);
        log.info("User : {} get deposit list between {} and {} : {}", username, beginDate, endDate, list);
        return list;
    }

    public List<DepositPO> getDepositYearSummitList(String beginDate, String endDate) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        List<DepositPO> list = depositMapper.getDepositYearSummitList(beginDate, endDate, username);
        log.info("User : {} get deposit year summit list between {} and {} : {}", username, beginDate, endDate, list);
        return list;
    }

    public List<DepositPO> getDepositMonthSummitList(String beginDate, String endDate) {
        String username = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        List<DepositPO> list = depositMapper.getDepositMonthSummitList(beginDate, endDate, username);
        log.info("User : {} get deposit month summit list between {} and {} : {}", username, beginDate, endDate, list);
        return list;
    }

    private BigDecimal depositFundDayIncome(String username) {
        List<String> fundListFrom = fundEntity.getFundList(null, username);
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

    private BigDecimal depositStockDayIncome(String username) {
        List<String> stockListFrom = stockEntity.getStockList(null, username);
        List<StockEntity> stocks = stockEntity.getStockDetails(stockListFrom, username);
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

    private BigDecimal depositFundMarketValue(String username) {
        List<String> fundListFrom = fundEntity.getFundList(null, username);
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

    private BigDecimal depositStockMarketValue(String username) {
        List<String> stockListFrom = stockEntity.getStockList(null, username);
        List<StockEntity> stocks = stockEntity.getStockDetails(stockListFrom, username);
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
        OpenPersistentMonthPO opt = openPersistentMonthMapper.findByMonth(date.substring(0, 7));
        List<TradingDateResponse.TradingDate> tradingDates;
        if (opt == null) {
            tradingDates = szseRestClient.getTradingDate().getData();
            OpenPersistentMonthPO openPersistentMonthPO = new OpenPersistentMonthPO();
            openPersistentMonthPO.setData(gson.toJson(tradingDates));
            openPersistentMonthPO.setMonth(date.substring(0, 7));
            openPersistentMonthMapper.save(openPersistentMonthPO);
        } else {
            Type listType = new TypeToken<ArrayList<TradingDateResponse.TradingDate>>() {}.getType();
            tradingDates = gson.fromJson(opt.getData(), listType);
        }

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
