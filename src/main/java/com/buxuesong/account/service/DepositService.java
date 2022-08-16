package com.buxuesong.account.service;

import com.buxuesong.account.persist.entity.Deposit;

import java.util.List;

public interface DepositService {
    void deposit();

    List<Deposit> getDepositList();

    Deposit getDepositByDate(String date);

    void deleteDeposit();
}
