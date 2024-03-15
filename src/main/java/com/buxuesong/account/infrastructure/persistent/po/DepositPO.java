package com.buxuesong.account.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositPO {
    private Long id;
    private String date;
    private BigDecimal fundDayIncome;
    private BigDecimal stockDayIncome;
    private BigDecimal totalDayIncome;
    private BigDecimal fundMarketValue;
    private BigDecimal stockMarketValue;
    private BigDecimal totalMarketValue;
    private String bigMarketChangePercent;
    private String bigMarketValue;
}
