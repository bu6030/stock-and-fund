package com.buxuesong.account.service;

import com.buxuesong.account.persist.dao.DepositMapper;
import com.buxuesong.account.persist.entity.Deposit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DepositServiceImpl implements DepositService {
    @Autowired
    private DepositMapper depositMapper;

    @Override
    public List<Deposit> getDepositList() {
        return depositMapper.findAllDeposit();
    }

}
