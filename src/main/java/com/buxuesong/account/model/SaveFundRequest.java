package com.buxuesong.account.model;

public class SaveFundRequest {
    private String fund;

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    @Override
    public String toString() {
        return "SaveFundRequest{" +
            "fund='" + fund + '\'' +
            '}';
    }
}
