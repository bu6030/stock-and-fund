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

}
