package com.buxuesong.account.domain.model.advice;

import com.buxuesong.account.apis.model.request.AdviceRequest;
import com.buxuesong.account.infrastructure.general.utils.MailUtils;
import com.buxuesong.account.infrastructure.persistent.po.AdvicePO;
import com.buxuesong.account.infrastructure.persistent.repository.AdviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class AdviceEntity {

    @Autowired
    private AdviceMapper adviceMapper;
    @Autowired
    private MailUtils mailUtils;

    /**
     * 保存股票基金神器扩展程序的反馈建议
     * 
     * @param request
     * @return
     */
    public boolean saveAdvice(AdviceRequest request) {
        adviceMapper.save(AdvicePO.builder().adviceContent(request.getAdviceContent())
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString())
            .build());
        try {
            mailUtils.sendMailNoArchieve("用户建议 " + LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), request.getAdviceContent());
        } catch(Exception e) {
            e.printStackTrace();
            log.error("邮件发送失败", e);
        }
        return true;
    }

    /**
     * 获取股票基金神器扩展程序的反馈建议
     * 
     * @return
     */
    public List<AdvicePO> getAdvice() {
        return adviceMapper.getAdvice();
    }

    /**
     * 更新股票基金神器扩展程序的反馈建议的发布版本
     *
     * @param request
     * @return
     */
    public boolean updateAdvice(AdviceRequest request) {
        adviceMapper.update(AdvicePO.builder().id(request.getId())
            .adviceContent(request.getAdviceContent())
            .adviceDevelopVersion(request.getAdviceDevelopVersion())
            .build());
        return true;
    }

    /**
     * 删除股票基金神器扩展程序的反馈建议的发布版本
     *
     * @param request
     * @return
     */
    public boolean deleteAdvice(AdviceRequest request) {
        adviceMapper.delete(AdvicePO.builder().id(request.getId())
            .build());
        return true;
    }
}
