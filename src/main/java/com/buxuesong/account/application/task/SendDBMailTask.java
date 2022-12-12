package com.buxuesong.account.application.task;

import com.buxuesong.account.infrastructure.general.utils.MailUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendDBMailTask {
    @Autowired
    private MailUtils mailUtils;
    @Value("${send.db.mail.task.enable}")
    private boolean sendDbMailTaskEnable;

    // 每周一至周五15点30分统计当日盈亏
    @Scheduled(cron = "45 0/1 * * * MON-FRI")
    public void sendDBMail() {
        log.info("======= SendDBMailTask started =======");
        if (!sendDbMailTaskEnable) {
            log.info("======= SendDBMailTask disabled =======");
            return;
        }
        execute();
        log.info("======= SendDBMailTask finished =======");
    }

    private void execute() {
        try {
            mailUtils.sendMail("数据库文件备份", "数据库文件备份");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
