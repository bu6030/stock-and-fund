package com.buxuesong.account.domain.model.deposit;

import com.buxuesong.account.domain.service.CacheService;
import com.buxuesong.account.infrastructure.adapter.rest.EastMoneyRestClient;
import com.buxuesong.account.infrastructure.adapter.rest.SzseRestClient;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.domain.model.stock.StockEntity;
import com.buxuesong.account.infrastructure.adapter.rest.response.FundNetDiagramResponse;
import com.buxuesong.account.infrastructure.adapter.rest.response.TradingDateResponse;
import com.buxuesong.account.infrastructure.general.utils.MailUtils;
import com.buxuesong.account.infrastructure.general.utils.UserUtils;
import com.buxuesong.account.infrastructure.persistent.po.*;
import com.buxuesong.account.infrastructure.persistent.repository.DepositMapper;
import com.buxuesong.account.infrastructure.persistent.repository.FundJZMapper;
import com.buxuesong.account.infrastructure.persistent.repository.OpenPersistentMonthMapper;
import com.buxuesong.account.infrastructure.persistent.repository.UserMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DepositEntity {
    @Autowired
    private SzseRestClient szseRestClient;
    @Autowired
    private EastMoneyRestClient eastMoneyRestClient;
    @Autowired
    private DepositMapper depositMapper;
    @Autowired
    private FundJZMapper fundJZMapper;
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
    @Autowired
    private MailUtils mailUtils;

    private static Gson gson = new Gson();

    public DepositPO getDepositByDate(String date) {
        String username = UserUtils.getUsername();
        return depositMapper.findDepositByDate(date, username);
    }

    /**
     * 定时程序统计所有用户盈亏
     */
    public void depositAllUsers() {
        List<UserPO> users = userMapper.findAllUsers();
        // JAVA 旧版本线程池写法
//        ExecutorService executor = Executors.newFixedThreadPool(10);
        // JAVA 21 新的虚拟线程写法，如果这个报错，修改为上面的
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        users.forEach(user -> {
            executor.submit(() -> {
                log.info("任务" + Thread.currentThread().getName() + "开始执行");
                deposit(user.getUsername());
                log.info("任务" + Thread.currentThread().getName() + "执行完成");
            });
        });
        executor.shutdown();
    }

    /**
     * 用户手动点击计算盈亏
     */
    public void deposit() {
        String username = UserUtils.getUsername();
        deposit(username);
    }

    /**
     * 支持用户手动点击计算盈亏以及定时程序统计所有用户盈亏具体执行方法
     */
    private void deposit(String username) {
        cacheService.removeAllCache();
        LocalDate date = LocalDate.now();
        if (!isTradingDate(date.toString())) {
            log.info("非交易日期，不统计 {}", username);
            return;
        }

        DepositPO deposit = depositMapper.findDepositByDate(date.toString(), username);
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
        // 获取大盘涨跌数据
        List<String> bigMarketList = new ArrayList<>();
        bigMarketList.add("sh000001,0,0,,0");
        List<StockEntity> list = stockEntity.getStockDetails(bigMarketList, username);
        StockEntity bigMarket = list.get(0);
        String bigMarketChangePercent = bigMarket.getChangePercent();
        String bigMarketValue = bigMarket.getNow();
        if (deposit == null) {
            depositMapper.save(DepositPO
                .builder()
                .date(LocalDate.now().toString())
                .fundDayIncome(fundTotalDayIncome)
                .stockDayIncome(stockTotalDayIncome)
                .totalDayIncome(totalDayIncome)
                .fundMarketValue(fundTotalMarketValue)
                .stockMarketValue(stockTotalMarketValue)
                .totalMarketValue(totalMarketValue)
                .bigMarketChangePercent(bigMarketChangePercent)
                .bigMarketValue(bigMarketValue)
                .build(), username);
        } else {
            depositMapper.update(DepositPO
                .builder()
                .id(deposit.getId())
                .date(LocalDate.now().toString())
                .fundDayIncome(fundTotalDayIncome)
                .stockDayIncome(stockTotalDayIncome)
                .totalDayIncome(totalDayIncome)
                .fundMarketValue(fundTotalMarketValue)
                .stockMarketValue(stockTotalMarketValue)
                .totalMarketValue(totalMarketValue)
                .bigMarketChangePercent(bigMarketChangePercent)
                .bigMarketValue(bigMarketValue)
                .build(), username);
        }
        try {
            if (Float.parseFloat(bigMarketChangePercent) > 0) {
                bigMarketChangePercent = "+" + bigMarketChangePercent;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            bigMarketValue = decimalFormat.format(Double.parseDouble(bigMarketValue));
            String mailContent = "日期：" + LocalDate.now() + " 用户：" + username + "当日盈利统计完毕，"
                    + "基金收益：" + fundTotalDayIncome + "，股票收益：" + stockTotalDayIncome + "，总收益：" + totalDayIncome
                    +  "，当日大盘：" + bigMarketValue + "(" + bigMarketChangePercent + "%)。";
            mailUtils.sendMailNoArchieve("当日盈利", mailContent);
        } catch(Exception e) {
            log.error("Send deposit mail error, ", e);
        }
    }

    public void deleteDeposit() {
        LocalDate date = LocalDate.now();
        String username = UserUtils.getUsername();
        log.info("User : {} delete deposit date : {}", username, date);
        depositMapper.deleteDeposit(date.toString(), username);
    }

    public List<DepositPO> getDepositList(String beginDate, String endDate) {
        String username = UserUtils.getUsername();
        return getDepositList(beginDate, endDate, username);
    }

    public List<DepositPO> getDepositList(String beginDate, String endDate, String username) {
        List<DepositPO> list = depositMapper.getDepositList(beginDate, endDate, username);
        log.info("User : {} get deposit list between {} and {} : {}", username, beginDate, endDate, list);
        return list;
    }

    public List<DepositPO> getDepositYearSummitList(String beginDate, String endDate) {
        String username = UserUtils.getUsername();
        List<DepositPO> list = depositMapper.getDepositYearSummitList(beginDate, endDate, username);
        log.info("User : {} get deposit year summit list between {} and {} : {}", username, beginDate, endDate, list);
        return list;
    }

    public List<DepositPO> getDepositMonthSummitList(String beginDate, String endDate) {
        String username = UserUtils.getUsername();
        List<DepositPO> list = depositMapper.getDepositMonthSummitList(beginDate, endDate, username);
        log.info("User : {} get deposit month summit list between {} and {} : {}", username, beginDate, endDate, list);
        return list;
    }

    private BigDecimal depositFundDayIncome(String username) {
        List<String> fundListFrom = fundEntity.getFundList(null, username);
        List<FundEntity> funds = fundEntity.getFundDetails(fundListFrom);
        BigDecimal fundTotalDayIncome = new BigDecimal("0");
        for (FundEntity fund : funds) {
            String currentDayDate = LocalDate.now().toString();
            FundNetDiagramResponse fundNetDiagram = getFundNetDiagramResponse(fund.getFundCode(), currentDayDate);
            log.info("fundNetDiagram : {}", fundNetDiagram);
            // 当日净值已出
            if (fundNetDiagram != null) {
                Optional<FundNetDiagramResponse.DataItem> optionalCurrentDay = fundNetDiagram.getDatas().stream()
                    .filter(dataItem -> dataItem.getFSRQ().equals(currentDayDate)).findFirst();
                FundNetDiagramResponse.DataItem currentDayItem = optionalCurrentDay.get();
                int currentIndex = fundNetDiagram.getDatas().indexOf(currentDayItem);
                FundNetDiagramResponse.DataItem yesTodayItem = fundNetDiagram.getDatas().get(currentIndex - 1);
                BigDecimal dayIncome = (new BigDecimal(currentDayItem.getDWJZ())
                    .subtract(new BigDecimal(yesTodayItem.getDWJZ())).multiply(new BigDecimal(fund.getBonds())))
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                fundTotalDayIncome = fundTotalDayIncome.add(dayIncome);
                log.info("按照当日净值计算，基金： {} ,当日盈利： {}", fund.getFundName(), dayIncome.toString());
            } else {
                BigDecimal dayIncome = (new BigDecimal(fund.getGszzl())
                    .multiply(new BigDecimal(fund.getDwjz())).multiply(new BigDecimal(fund.getBonds()))
                    .divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                fundTotalDayIncome = fundTotalDayIncome.add(dayIncome);
                log.info("按照估值计算，基金： {} ,当日盈利： {}", fund.getFundName(), dayIncome.toString());
            }
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
            String currentDayDate = LocalDate.now().toString();
            FundNetDiagramResponse fundNetDiagram = getFundNetDiagramResponse(fund.getFundCode(), currentDayDate);
            log.info("fundNetDiagram : {}", fundNetDiagram);
            // 当日净值已出
            if (fundNetDiagram != null) {
                Optional<FundNetDiagramResponse.DataItem> optionalCurrentDay = fundNetDiagram.getDatas().stream()
                    .filter(dataItem -> dataItem.getFSRQ().equals(currentDayDate)).findFirst();
                FundNetDiagramResponse.DataItem currentDayItem = optionalCurrentDay.get();
                BigDecimal marketValue = (new BigDecimal(currentDayItem.getDWJZ())
                    .multiply(new BigDecimal(fund.getBonds())).setScale(2, BigDecimal.ROUND_HALF_UP));
                fundTotalMarketValue = fundTotalMarketValue.add(marketValue);
            } else {
                BigDecimal marketValue = (new BigDecimal(fund.getGsz())
                    .multiply(new BigDecimal(fund.getBonds())).setScale(2, BigDecimal.ROUND_HALF_UP));
                fundTotalMarketValue = fundTotalMarketValue.add(marketValue);
            }
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

    private FundNetDiagramResponse getFundNetDiagramResponse(String fundCode, String currentDayDate) {
        List<FundJZPO> fundJZPOs = fundJZMapper.findResent5FundJZByCode(fundCode);
        Optional<FundJZPO> optional = fundJZPOs.stream().filter(dataItem -> dataItem.getFSRQ().equals(currentDayDate)).findFirst();
        FundNetDiagramResponse fundNetDiagram;
        if (optional.isPresent()) {
            fundNetDiagram = new FundNetDiagramResponse();
            List<FundNetDiagramResponse.DataItem> datas = fundJZPOs.stream()
                .map(item -> FundNetDiagramResponse.DataItem.builder().DWJZ(item.getDWJZ()).FSRQ(item.getFSRQ()).build())
                .collect(Collectors.toList());
            fundNetDiagram.setDatas(datas);
        } else {
            fundNetDiagram = cacheService.getFundNetDiagram(fundCode, currentDayDate);
            if (fundNetDiagram != null) {
                fundNetDiagram.getDatas().forEach(item -> {
                    FundJZPO fundJZPO = fundJZMapper.findFundJZByCodeAndDate(fundCode, item.getFSRQ());
                    if (fundJZPO == null) {
                        fundJZPO = FundJZPO.builder().FSRQ(item.getFSRQ()).DWJZ(item.getDWJZ()).code(fundCode).build();
                        fundJZMapper.save(fundJZPO);
                    }
                });
            }
        }
        return fundNetDiagram;
    }
}
