package com.buxuesong.account.model;

import java.math.BigDecimal;

public class SaveFundRequest {
    private String code;
    private BigDecimal costPrise;
    private String bonds;
    private String app;

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

    @Override
    public String toString() {
        return "SaveFundRequest{" +
            "code='" + code + '\'' +
            ", costPrise='" + costPrise + '\'' +
            ", bonds='" + bonds + '\'' +
            ", app='" + app + '\'' +
            '}';
    }
}
