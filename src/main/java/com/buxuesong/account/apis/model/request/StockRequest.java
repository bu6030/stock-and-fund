package com.buxuesong.account.apis.model.request;

import java.math.BigDecimal;

public class StockRequest {
    private String code;
    private String name;
    private BigDecimal costPrise;
    private int bonds;
    private String app;

    private boolean hide;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getCostPrise() {
        return costPrise;
    }

    public void setCostPrise(BigDecimal costPrise) {
        this.costPrise = costPrise;
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

    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHide() {
        return hide;
    }

    @Override
    public String toString() {
        return "StockRequest{" +
            "code='" + code + '\'' +
            ", name='" + name + '\'' +
            ", costPrise=" + costPrise +
            ", bonds=" + bonds +
            ", app='" + app + '\'' +
            ", hide=" + hide +
            '}';
    }
}
