package com.buxuesong.account.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundJZPO {
    private Long id;
    private String code;
    // 净值日期
    private String FSRQ;
    // 当日净值
    private String DWJZ;
}
