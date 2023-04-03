package com.buxuesong.account.application.task;

import com.buxuesong.account.domain.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClearCacheTask {

    @Autowired
    private CacheService cacheService;

    // 每周一至周五9点清理缓存
    @Scheduled(cron = "0 9 ? * MON-FRI")
    public void clearCache() {
        log.info("======= ClearCacheTask started =======");
        execute();
        log.info("======= ClearCacheTask finished =======");
    }

    private void execute() {
        cacheService.removeAllCache();
    }
}
