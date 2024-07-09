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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DepositEntity {
    record DepositItem(String name, String type, BigDecimal dayIncome, BigDecimal marketValue, BigDecimal totalIncome, boolean isCurrentDayNewValue) {
    }

    record DepositResult(BigDecimal totalDayIncome, BigDecimal totalMarketValue, BigDecimal totalIncome, List<DepositItem> depositItems) {
    }

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
                log.info("任务 {} 开始执行", Thread.currentThread().getName());
                deposit(user.getUsername());
                log.info("任务 {} 执行完成", Thread.currentThread().getName());
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
        DepositResult stockDepositResult = depositStock(username);
        DepositResult fundDepositResult = depositFund(username);
        // 当日盈利
        BigDecimal fundTotalDayIncome = fundDepositResult.totalDayIncome();
        BigDecimal stockTotalDayIncome = stockDepositResult.totalDayIncome();
        BigDecimal totalDayIncome = stockTotalDayIncome.add(fundTotalDayIncome);

        // 总收益
        BigDecimal stockTotalIncome = stockDepositResult.totalIncome();
        BigDecimal fundTotalIncome = fundDepositResult.totalIncome();
        BigDecimal totalIncome = stockTotalIncome.add(fundTotalIncome);

        // 总市值
        BigDecimal fundTotalMarketValue = fundDepositResult.totalMarketValue();
        BigDecimal stockTotalMarketValue = stockDepositResult.totalMarketValue();
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
            if (deposit != null && deposit.getTotalDayIncome().compareTo(totalDayIncome) == 0) {
                log.info("当前统计的和上一次统计结果一致，不发送邮件！");
                return;
            }
            String style = "";
            String totalIncomeStyle = "";
            String redColorStyle = "color: red;";
            String greenColorStyle = "color: green;";
            String decimalFormatPattern = "#.##";
            DecimalFormat decimalFormat = new DecimalFormat(decimalFormatPattern);

            String bigMarketValueFormatted = decimalFormat.format(Double.parseDouble(bigMarketValue));
            String bigMarketContent = Float.parseFloat(bigMarketChangePercent) >= 0
                ? String.format("<span style=\"%s\">%s（%s%%）</span>", redColorStyle, bigMarketValueFormatted, bigMarketChangePercent)
                : String.format("<span style=\"%s\">%s（%s%%）</span>", greenColorStyle, bigMarketValueFormatted, bigMarketChangePercent);

            BigDecimal fundTotalDayIncomePercent = fundTotalDayIncome.multiply(new BigDecimal("100"))
                .divide(fundTotalMarketValue.add(fundTotalDayIncome), BigDecimal.ROUND_HALF_UP);
            String fundTotalDayIncomeContent = fundTotalDayIncome.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("<span style=\"%s\">%s（%s%%）</span>", redColorStyle, fundTotalDayIncome, fundTotalDayIncomePercent)
                : String.format("<span style=\"%s\">%s（%s%%）</span>", greenColorStyle, fundTotalDayIncome, fundTotalDayIncomePercent);

            BigDecimal stockTotalDayIncomePercent = stockTotalDayIncome.multiply(new BigDecimal("100"))
                .divide(stockTotalMarketValue.add(stockTotalDayIncome), BigDecimal.ROUND_HALF_UP);
            String stockTotalDayIncomeContent = stockTotalDayIncome.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("<span style=\"%s\">%s（%s%%）</span>", redColorStyle, stockTotalDayIncome, stockTotalDayIncomePercent)
                : String.format("<span style=\"%s\">%s（%s%%）</span>", greenColorStyle, stockTotalDayIncome, stockTotalDayIncomePercent);

            BigDecimal totalDayIncomePercent = totalDayIncome.multiply(new BigDecimal("100")).divide(totalMarketValue.add(totalDayIncome),
                BigDecimal.ROUND_HALF_UP);
            String totalDayIncomeContent = totalDayIncome.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("<span style=\"%s\">%s（%s%%）</span>", redColorStyle, totalDayIncome, totalDayIncomePercent)
                : String.format("<span style=\"%s\">%s（%s%%）</span>", greenColorStyle, totalDayIncome, totalDayIncomePercent);

            StringBuilder stockItemContent = new StringBuilder();
            List<DepositItem> stockDepositItems = stockDepositResult.depositItems();
            for (DepositItem item : stockDepositItems) {
                style = item.dayIncome().compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
                BigDecimal dayIncomePercent = item.dayIncome().multiply(new BigDecimal("100"))
                    .divide(item.marketValue().subtract(item.dayIncome()), BigDecimal.ROUND_HALF_UP);
                BigDecimal stockTotalIncomePercent = item.totalIncome().multiply(new BigDecimal("100"))
                    .divide(item.marketValue().subtract(item.totalIncome()), 3, BigDecimal.ROUND_HALF_UP);
                totalIncomeStyle = item.totalIncome().compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
                stockItemContent.append(String.format(
                    "<tr><td>%s</td><td><span style=\"%s\">%s（%s%%）</span></td><td>%s</td><td><span style=\"%s\">%s（%s%%）</span></td></tr>",
                    item.name(), style, item.dayIncome(), dayIncomePercent, item.marketValue(), totalIncomeStyle,
                    item.totalIncome().setScale(2, BigDecimal.ROUND_HALF_UP), stockTotalIncomePercent));
            }
            style = stockTotalDayIncome.compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
            BigDecimal stockTotalIncomePercent = stockTotalIncome.multiply(new BigDecimal("100"))
                .divide(stockTotalMarketValue.subtract(stockTotalIncome), 3, BigDecimal.ROUND_HALF_UP);
            totalIncomeStyle = stockTotalIncome.compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
            stockItemContent.append(String.format(
                "<tr><td>%s</td><td><span style=\"%s\">%s（%s%%）</span></td><td>%s</td><td><span style=\"%s\">%s（%s%%）</span></td></tr>",
                "合计", style, stockTotalDayIncome, stockTotalDayIncomePercent, stockTotalMarketValue, totalIncomeStyle,
                stockTotalIncome.setScale(2, BigDecimal.ROUND_HALF_UP), stockTotalIncomePercent));
            String stockTotalIncomeContent = stockTotalIncome.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("<span style=\"%s\">%s（%s%%）</span>", redColorStyle, stockTotalIncome.setScale(2, BigDecimal.ROUND_HALF_UP),
                    stockTotalIncomePercent)
                : String.format("<span style=\"%s\">%s（%s%%）</span>", greenColorStyle,
                    stockTotalIncome.setScale(2, BigDecimal.ROUND_HALF_UP),
                    stockTotalIncomePercent);

            StringBuilder fundItemContent = new StringBuilder();
            List<DepositItem> fundDepositItems = fundDepositResult.depositItems();
            for (DepositItem item : fundDepositItems) {
                style = item.dayIncome().compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
                BigDecimal dayIncomePercent = item.dayIncome().multiply(new BigDecimal("100"))
                    .divide(item.marketValue().subtract(item.dayIncome()), BigDecimal.ROUND_HALF_UP);
                BigDecimal fundTotalIncomePercent = item.totalIncome().multiply(new BigDecimal("100"))
                    .divide(item.marketValue().subtract(item.totalIncome()), 3, BigDecimal.ROUND_HALF_UP);
                totalIncomeStyle = item.totalIncome().compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
                fundItemContent.append(String.format(
                    "<tr><td>%s</td><td><span style=\"%s\">%s（%s%%）(%s)</span></td><td>%s</td><td><span style=\"%s\">%s（%s%%）</span></td></tr>",
                    item.name(), style, item.dayIncome(), dayIncomePercent, item.isCurrentDayNewValue() ? "实" : "估", item.marketValue(),
                    totalIncomeStyle, item.totalIncome().setScale(2, BigDecimal.ROUND_HALF_UP), fundTotalIncomePercent));
            }
            style = fundTotalDayIncome.compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
            BigDecimal fundTotalIncomePercent = fundTotalIncome.multiply(new BigDecimal("100"))
                .divide(fundTotalMarketValue.subtract(fundTotalIncome), 3, BigDecimal.ROUND_HALF_UP);
            totalIncomeStyle = fundTotalIncome.compareTo(BigDecimal.ZERO) >= 0 ? redColorStyle : greenColorStyle;
            fundItemContent.append(String.format(
                "<tr><td>%s</td><td><span style=\"%s\">%s（%s%%）</span></td><td>%s</td><td><span style=\"%s\">%s（%s%%）</span></td></tr>",
                "合计", style, fundTotalDayIncome, fundTotalDayIncomePercent, fundTotalMarketValue, totalIncomeStyle,
                fundTotalIncome.setScale(2, BigDecimal.ROUND_HALF_UP), fundTotalIncomePercent));
            String fundTotalIncomeContent = fundTotalIncome.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("<span style=\"%s\">%s（%s%%）</span>", redColorStyle, fundTotalIncome.setScale(2, BigDecimal.ROUND_HALF_UP),
                    fundTotalIncomePercent)
                : String.format("<span style=\"%s\">%s（%s%%）</span>", greenColorStyle,
                    fundTotalIncome.setScale(2, BigDecimal.ROUND_HALF_UP),
                    fundTotalIncomePercent);
            BigDecimal totalIncomePercent = totalIncome.multiply(new BigDecimal("100"))
                .divide(totalMarketValue.subtract(totalIncome), 3, BigDecimal.ROUND_HALF_UP);
            String totalIncomeContent = totalIncome.compareTo(BigDecimal.ZERO) >= 0
                ? String.format("<span style=\"%s\">%s（%s%%）</span>", redColorStyle, totalIncome.setScale(2, BigDecimal.ROUND_HALF_UP),
                    totalIncomePercent)
                : String.format("<span style=\"%s\">%s（%s%%）</span>", greenColorStyle, totalIncome.setScale(2, BigDecimal.ROUND_HALF_UP),
                    totalIncomePercent);

            String mailContent = String.format(
                "<html>" +
                    "<head>" +
                    "<style>" +
                    "body {font-family: Arial, sans-serif; line-height: 1.6;}" +
                    "table {width: 600px; border-collapse: collapse; margin: 20px 0;}" +
                    "th, td {padding: 8px 12px; border: 1px solid #ddd; text-align: left;}" +
                    "th {background-color: #f4f4f4;}" +
                    "td {font-size: small;}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h2>每日收益报告</h2>" +
                    "<p>日期：%s</p>" +
                    "<p>用户：%s</p>" +
                    "<p>上政指数：%s</p>" +
                    "<p>当日基金收益：%s</p>" +
                    "<p>当日股票收益：%s</p>" +
                    "<p>当日总收益：%s</p>" +
                    "<p>基金总收益：%s</p>" +
                    "<p>股票总收益：%s</p>" +
                    "<p>总收益：%s</p>" +
                    "<h3>股票明细：</h3>" +
                    "<table>" +
                    "<tr><th>名称</th><th>当日盈利</th><th>市值</th><th>总收益</th></tr>" +
                    "%s" +
                    "</table>" +
                    "<h3>基金明细：</h3>" +
                    "<table>" +
                    "<tr><th>名称</th><th>当日盈利</th><th>市值</th><th>总收益</th></tr>" +
                    "%s" +
                    "</table>" +
                    "</body>" +
                    "</html>",
                LocalDate.now(), username, bigMarketContent, fundTotalDayIncomeContent,
                stockTotalDayIncomeContent, totalDayIncomeContent, fundTotalIncomeContent, stockTotalIncomeContent,
                totalIncomeContent, stockItemContent, fundItemContent);
            mailUtils.sendMailNoArchieve("每日收益报告" + LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), mailContent);
        } catch (Exception e) {
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

    private DepositResult depositFund(String username) {
        List<DepositItem> depositItems = new ArrayList<>();
        List<String> fundListFrom = fundEntity.getFundList(null, username);
        List<FundEntity> funds = fundEntity.getFundDetails(fundListFrom);
        BigDecimal fundTotalDayIncome = new BigDecimal("0");
        BigDecimal fundTotalMarketValue = new BigDecimal("0");
        BigDecimal fundTotalIncome = new BigDecimal("0");
        for (FundEntity fund : funds) {
            BigDecimal dayIncome;
            BigDecimal marketValue;
            BigDecimal totalIncome;
            boolean isCurrentDayNewValue = false;
            String currentDayDate = LocalDate.now().toString();
            FundNetDiagramResponse fundNetDiagram = getFundNetDiagramResponse(fund.getFundCode(), currentDayDate);
            // 当日净值已出
            if (fundNetDiagram != null) {
                Optional<FundNetDiagramResponse.DataItem> optionalCurrentDay = fundNetDiagram.getDatas().stream()
                    .filter(dataItem -> dataItem.getFSRQ().equals(currentDayDate)).findFirst();
                FundNetDiagramResponse.DataItem currentDayItem = optionalCurrentDay.get();
                int currentIndex = fundNetDiagram.getDatas().indexOf(currentDayItem);
                FundNetDiagramResponse.DataItem yesTodayItem = fundNetDiagram.getDatas().get(currentIndex - 1);
                // 计算当日盈利
                dayIncome = (new BigDecimal(currentDayItem.getDWJZ())
                    .subtract(new BigDecimal(yesTodayItem.getDWJZ())).multiply(new BigDecimal(fund.getBonds())))
                        .setScale(2, BigDecimal.ROUND_HALF_UP);
                fundTotalDayIncome = fundTotalDayIncome.add(dayIncome);
                // 计算市值
                marketValue = (new BigDecimal(currentDayItem.getDWJZ())
                    .multiply(new BigDecimal(fund.getBonds())).setScale(2, BigDecimal.ROUND_HALF_UP));
                fundTotalMarketValue = fundTotalMarketValue.add(marketValue);
                // 计算总收益
                totalIncome = marketValue.subtract((new BigDecimal(fund.getCostPrise())).multiply(new BigDecimal(fund.getBonds())))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
                fundTotalIncome = fundTotalIncome.add(totalIncome);
                isCurrentDayNewValue = true;
                log.info("按照当日净值计算，基金: {} ,当日盈利: {} ，市值: {} ，总收益: {}", fund.getFundName(), dayIncome.toString(), marketValue, totalIncome);
            } else {
                // 计算当日盈利
                dayIncome = (new BigDecimal(fund.getGszzl())
                    .multiply(new BigDecimal(fund.getDwjz())).multiply(new BigDecimal(fund.getBonds()))
                    .divide(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
                fundTotalDayIncome = fundTotalDayIncome.add(dayIncome);
                // 计算市值
                marketValue = (new BigDecimal(fund.getGsz())
                    .multiply(new BigDecimal(fund.getBonds())).setScale(2, BigDecimal.ROUND_HALF_UP));
                fundTotalMarketValue = fundTotalMarketValue.add(marketValue);
                // 计算总收益
                totalIncome = marketValue.subtract((new BigDecimal(fund.getCostPrise())).multiply(new BigDecimal(fund.getBonds())))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
                fundTotalIncome = fundTotalIncome.add(totalIncome);
                log.info("按照估值计算，基金: {} ,当日盈利: {} ，市值: {} ，总收益: {}", fund.getFundName(), dayIncome.toString(), marketValue, totalIncome);
            }
            DepositItem depositItem = new DepositItem(fund.getFundName(), "FUND", dayIncome, marketValue, totalIncome, isCurrentDayNewValue);
            depositItems.add(depositItem);
        }
        log.info("基金当日盈利: {}，基金当日市值: {}，基金总收益: {}", fundTotalDayIncome, fundTotalMarketValue, fundTotalIncome);
        return new DepositResult(fundTotalDayIncome, fundTotalMarketValue, fundTotalIncome, depositItems);
    }

    private DepositResult depositStock(String username) {
        List<DepositItem> depositItems = new ArrayList<>();
        List<String> stockListFrom = stockEntity.getStockList(null, username);
        List<StockEntity> stocks = stockEntity.getStockDetails(stockListFrom, username);
        BigDecimal stockTotalDayIncome = new BigDecimal("0");
        BigDecimal stockTotalMarketValue = new BigDecimal("0");
        BigDecimal stockTotalIncome = new BigDecimal("0");
        for (StockEntity stock : stocks) {
            // 计算当日盈利
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
            }
            dayIncome = dayIncome.add(todayBuyIncome).add(todaySellIncom);
            stockTotalDayIncome = stockTotalDayIncome.add(dayIncome);
            // 计算市值
            BigDecimal marketValue = (new BigDecimal(stock.getNow()).multiply(new BigDecimal(stock.getBonds()))).setScale(2,
                BigDecimal.ROUND_HALF_UP);
            stockTotalMarketValue = stockTotalMarketValue.add(marketValue);
            // 计算总收益
            BigDecimal totalIncome = marketValue
                .subtract((new BigDecimal(stock.getCostPrise())).multiply(new BigDecimal(stock.getBonds())));
            stockTotalIncome = stockTotalIncome.add(totalIncome);
            DepositItem depositItem = new DepositItem(stock.getName(), "STOCK", dayIncome, marketValue, totalIncome, false);
            depositItems.add(depositItem);
        }
        log.info("股票当日盈利: {}，股票总市值: {}，股票总收益: {}", stockTotalDayIncome, stockTotalMarketValue, stockTotalIncome);
        return new DepositResult(stockTotalDayIncome, stockTotalMarketValue, stockTotalIncome, depositItems);
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
            fundNetDiagram = cacheService.getFundNetDiagram3N(fundCode, currentDayDate);
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