package com.buxuesong.account.scheduler;

import com.buxuesong.account.service.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositScheduler {
    @Autowired
    private DepositService depositService;

    // 每周一至周五15点30分统计当日盈亏
    @Scheduled(cron = "0 30 15 ? * MON-FRI")
    public void autoStar() {
        log.info("======= DepositScheduler started =======");
        execute();
        log.info("======= DepositScheduler finished =======");
    }

    private void execute() {
        depositService.deposit();
    }

}
