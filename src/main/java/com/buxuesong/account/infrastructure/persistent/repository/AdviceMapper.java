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
    @Update(" INSERT INTO ADVICE (DATE, ADVICE_CONTENT, ENABLED) values (#{advicePO.date},#{advicePO.adviceContent}, true) ")
    int save(@Param("advicePO") AdvicePO advicePO);

    @Update(" UPDATE ADVICE SET ADVICE_DEVELOP_VERSION = #{advicePO.adviceDevelopVersion} where ID = #{advicePO.id} ")
    int update(@Param("advicePO") AdvicePO advicePO);

    @Update(" UPDATE ADVICE SET ENABLED = false where ID = #{advicePO.id} ")
    int delete(@Param("advicePO") AdvicePO advicePO);

    @Select("select ID, DATE, ADVICE_CONTENT adviceContent, ADVICE_DEVELOP_VERSION adviceDevelopVersion from ADVICE t where t.ENABLED = true limit 10")
    List<AdvicePO> getAdvice();
}
