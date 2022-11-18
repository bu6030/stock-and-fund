package com.buxuesong.account.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyOrSellStockPO implements Serializable {
    private String date;
    private String code;
    private BigDecimal price;
    private String type;
    private int bonds;
    private BigDecimal cost;
    private String app;
    private BigDecimal openPrice;
    private BigDecimal income;
}
