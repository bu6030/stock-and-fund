package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.AdvicePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdviceMapper {
    @Update(" INSERT INTO ADVICE (DATE, ADVICE_CONTENT) values (#{advicePO.date},#{advicePO.adviceContent}) ")
    int save(@Param("advicePO") AdvicePO advicePO);

    @Select("select DATE, ADVICE_CONTENT adviceContent, ADVICE_DEVELOP_VERSION adviceDevelopVersion from ADVICE t limit 10")
    List<AdvicePO> getAdvice();
}
