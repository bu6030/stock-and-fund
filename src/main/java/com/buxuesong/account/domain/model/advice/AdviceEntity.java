package com.buxuesong.account.domain.model.advice;

import com.buxuesong.account.infrastructure.persistent.po.AdvicePO;
import com.buxuesong.account.infrastructure.persistent.repository.AdviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class AdviceEntity {
    private String adviceContent;

    public String getAdviceContent() {
        return adviceContent;
    }

    public void setAdviceContent(String adviceContent) {
        this.adviceContent = adviceContent;
    }

    public AdviceMapper getAdviceMapper() {
        return adviceMapper;
    }

    public void setAdviceMapper(AdviceMapper adviceMapper) {
        this.adviceMapper = adviceMapper;
    }

    @Autowired
    private AdviceMapper adviceMapper;

    /**
     * 保存股票基金神器扩展程序的反馈建议
     * 
     * @param adviceContent
     * @return
     */
    public boolean saveAdvice(String adviceContent) {
        adviceMapper.save(AdvicePO.builder().adviceContent(adviceContent)
            .date(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString())
            .build());
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
}
