package com.buxuesong.account.infrastructure.adapter.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradingDateResponse {
    private List<TradingDate> data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TradingDate {
        private String zrxh;
        private String jybz;
        private String jyrq;
    }
}
