package com.buxuesong.account.apis.model.request;

import java.math.BigDecimal;

public class FundRequest {
    private String name;
    private String code;
    private BigDecimal costPrise;
    private String bonds;
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

    public String getBonds() {
        return bonds;
    }

    public void setBonds(String bonds) {
        this.bonds = bonds;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public boolean isHide() {
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

    @Override
    public String toString() {
        return "FundRequest{" +
            "name='" + name + '\'' +
            ", code='" + code + '\'' +
            ", costPrise=" + costPrise +
            ", bonds='" + bonds + '\'' +
            ", app='" + app + '\'' +
            ", hide=" + hide +
            '}';
    }
}
