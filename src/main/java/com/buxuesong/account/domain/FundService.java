package com.buxuesong.account.domain;

import com.buxuesong.account.apis.model.request.FundRequest;
import com.buxuesong.account.domain.model.fund.FundEntity;

import java.util.List;

public interface FundService {
    List<FundEntity> getFundDetails(List<String> codes);

    boolean saveFund(FundRequest fundRequest);

    List<String> getFundList(String app);

    void deleteFund(FundRequest fundRequest);

    FundRequest findFundByCode(String code);
}
