package com.buxuesong.account.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositPO {
    private String date;
    private BigDecimal fundDayIncome;
    private BigDecimal stockDayIncome;
    private BigDecimal totalDayIncome;

    private BigDecimal fundMarketValue;
    private BigDecimal stockMarketValue;
    private BigDecimal totalMarketValue;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getFundDayIncome() {
        return fundDayIncome;
    }

    public void setFundDayIncome(BigDecimal fundDayIncome) {
        this.fundDayIncome = fundDayIncome;
    }

    public BigDecimal getStockDayIncome() {
        return stockDayIncome;
    }

    public void setStockDayIncome(BigDecimal stockDayIncome) {
        this.stockDayIncome = stockDayIncome;
    }

    public BigDecimal getTotalDayIncome() {
        return totalDayIncome;
    }

    public void setTotalDayIncome(BigDecimal totalDayIncome) {
        this.totalDayIncome = totalDayIncome;
    }

    public BigDecimal getFundMarketValue() {
        return fundMarketValue;
    }

    public void setFundMarketValue(BigDecimal fundMarketValue) {
        this.fundMarketValue = fundMarketValue;
    }

    public BigDecimal getStockMarketValue() {
        return stockMarketValue;
    }

    public void setStockMarketValue(BigDecimal stockMarketValue) {
        this.stockMarketValue = stockMarketValue;
    }

    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }

    @Override
    public String toString() {
        return "Deposit{" +
            "date='" + date + '\'' +
            ", fundDayIncome=" + fundDayIncome +
            ", stockDayIncome=" + stockDayIncome +
            ", totalDayIncome=" + totalDayIncome +
            ", fundMarketValue=" + fundMarketValue +
            ", stockMarketValue=" + stockMarketValue +
            ", totalMarketValue=" + totalMarketValue +
            '}';
    }
}
