package com.buxuesong.account.service;

import com.buxuesong.account.model.FundBean;
import com.buxuesong.account.model.SaveFundRequest;

import java.util.List;

public interface FundService {
    List<FundBean> getFundDetails(List<String> codes);

    boolean saveFund(SaveFundRequest saveFundRequest);

    List<String> getFundList(String app);

    void deleteFund(SaveFundRequest saveFundRequest);

    SaveFundRequest findFundByCode(String code);
}
