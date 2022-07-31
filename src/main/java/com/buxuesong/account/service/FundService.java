package com.buxuesong.account.service;

import com.buxuesong.account.model.FundBean;
import com.buxuesong.account.model.SaveFundRequest;

import java.util.List;

public interface FundService {
    List<FundBean> getFundDetails(List<String> codes);
    void saveFund(SaveFundRequest saveFundRequest);
    List<String> getFundList();
    void deleteFund(SaveFundRequest saveFundRequest);
}
