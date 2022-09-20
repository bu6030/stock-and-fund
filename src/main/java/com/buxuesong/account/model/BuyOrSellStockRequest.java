package com.buxuesong.account.model;

import java.math.BigDecimal;

public class BuyOrSellStockRequest {
    private String date;
    private String code;
    private BigDecimal price;
    private String type;
    private int bonds;
    private BigDecimal cost;
    private String app;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getBonds() {
        return bonds;
    }

    public void setBonds(int bonds) {
        this.bonds = bonds;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Override
    public String toString() {
        return "BuyOrSellStockRequest{" +
            "date='" + date + '\'' +
            ", code='" + code + '\'' +
            ", price=" + price +
            ", type='" + type + '\'' +
            ", bonds=" + bonds +
            ", cost=" + cost +
            ", app='" + app + '\'' +
            '}';
    }
}
