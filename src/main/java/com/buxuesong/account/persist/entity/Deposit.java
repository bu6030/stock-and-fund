package com.buxuesong.account.persist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {
    private String date;
    private BigDecimal fundDayIncome;
    private BigDecimal stockDayIncome;
    private BigDecimal totalDayIncome;

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

    @Override
    public String toString() {
        return "Deposit{" +
            "date='" + date + '\'' +
            ", fundDayIncome=" + fundDayIncome +
            ", stockDayIncome=" + stockDayIncome +
            ", totalDayIncome=" + totalDayIncome +
            '}';
    }
}
