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
public class StockHisPO {
    private String code;
    private String name;
    private BigDecimal costPrise;
    private int bonds;
    private String app;
    private boolean hide;
    private String createDate;
}
