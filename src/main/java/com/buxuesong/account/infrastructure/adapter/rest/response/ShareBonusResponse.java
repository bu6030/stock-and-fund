package com.buxuesong.account.infrastructure.adapter.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareBonusResponse {

    private String day;
    // 转股
    private BigDecimal shareStock;
    // 送股
    private BigDecimal sendStock;
    // 派息
    private BigDecimal shareMoney;
}
