package com.buxuesong.account.service;

import com.buxuesong.account.model.FundBean;

import java.util.List;

public interface FundService {
    List<FundBean> getFundDetails(List<String> codes);

    void saveFund(String fund);

    List<String> getFundList();

    String getFund();
}
