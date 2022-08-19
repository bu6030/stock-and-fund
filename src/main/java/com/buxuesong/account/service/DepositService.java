package com.buxuesong.account.service;

import com.buxuesong.account.persist.entity.Deposit;

import java.util.List;

public interface DepositService {
    void deposit();

    List<Deposit> getDepositList(String beginDate, String endDate);

    Deposit getDepositByDate(String date);

    void deleteDeposit();
}
