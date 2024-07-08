package com.buxuesong.account.infrastructure.adapter.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDayHistoryResponse {
    private String day;
    private Double high;
    private Double low;
    private Double open;
    private Double close;

    public StockDayHistoryResponse clone() {
        StockDayHistoryResponse clone = new StockDayHistoryResponse(this.getDay(), this.getHigh(), this.getLow(), this.getOpen(), this.getClose());
        return clone;
    }
}
