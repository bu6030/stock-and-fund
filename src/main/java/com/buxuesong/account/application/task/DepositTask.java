package com.buxuesong.account.application.task;

import com.buxuesong.account.domain.model.deposit.DepositEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositTask {
    @Autowired
    private DepositEntity depositEntity;

    // 每周一至周五16/17/18/19/20/21点30分统计当日盈亏，防止偶尔抽风没跑
    @Scheduled(cron = "0 30 16,17,18,19,20,21 ? * MON-FRI")
    public void deposit() {
        log.info("======= DepositTask started =======");
        execute();
        log.info("======= DepositTask finished =======");
    }

    private void execute() {
        depositEntity.depositAllUsers();
    }

}
