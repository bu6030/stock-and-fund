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
}
