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
public class SinaFundEstimateResponse {
    private Result result;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private Status status;
        private D data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Status {
        private int code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class D {
        private String worth;
        private String worth_date;
        private double worth_rate;
        private List<Networth> networth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Networth {
        private String symbol;
        private String min_time;
        private String pre_nav;
        private String nav_pct;
        private String pre_nav2;
        private String nav2_pct;
        private String pre_date;
        private double growthrate;
        private String growthrate2;
    }
}
