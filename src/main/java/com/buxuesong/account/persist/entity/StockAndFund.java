package com.buxuesong.account.persist.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAndFund {
    private int id;
    private String stockAndFundInfo;
    // 1为股票，2为基金
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockAndFundInfo() {
        return stockAndFundInfo;
    }

    public void setStockAndFundInfo(String stockAndFundInfo) {
        this.stockAndFundInfo = stockAndFundInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "StockAndFund{" +
            "id=" + id +
            ", stockAndFundInfo='" + stockAndFundInfo + '\'' +
            ", type=" + type +
            '}';
    }
}
