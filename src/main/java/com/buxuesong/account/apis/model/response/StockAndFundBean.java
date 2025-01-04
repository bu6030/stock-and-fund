package com.buxuesong.account.apis.model.response;

import com.buxuesong.account.infrastructure.persistent.po.BuyOrSellStockPO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StockAndFundBean {

    // 公共部分
    private String type;// STOCK股票，FUND基金
    private String code;
    private String name;
    private String costPrise;// 成本价
    private String bonds;// 持仓
    private String app;// 支付宝/东方财富/东方证券
    private String incomePercent;// 收益率
    private String income;// 收益
    private boolean hide;

    // 基金部分
    private String jzrq;// 净值日期
    private String dwjz;// 当日净值
    private String gsz; // 估算净值
    private String gszzl;// 估算涨跌百分比 即-0.42%
    private String gztime;// gztime估值时间

    // 股票部分
    private String now;
    private String change;// 涨跌
    private String changePercent;
    private String time;
    private String max;// 最高价
    private String min;// 最低价
    private List<BuyOrSellStockPO> buyOrSellStockRequestList;

    // 唐安奇通道法，最近XX个交易日最高价格以及最低价格
    private String day50Max;
    private String day50Min;
    private String day20Max;
    private String day20Min;
    private String day10Max;
    private String day10Min;
    private String ma20;

    private String oneYearAgoUpper;
    private String oneSeasonAgoUpper;
    private String oneMonthAgoUpper;
    private String oneWeekAgoUpper;

    private String currentDayJingzhi;// 当日净值（每个交易日晚9点之后有新日期数据）
    private String previousDayJingzhi;// 前一日净值

}
