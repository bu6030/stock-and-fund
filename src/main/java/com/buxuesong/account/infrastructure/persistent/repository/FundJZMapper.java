package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.FundJZPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundJZMapper {
    @Update("INSERT INTO FUND_JZ (CODE, FSRQ, DWJZ) values (#{fundJZ.code}, #{fundJZ.FSRQ}, #{fundJZ.DWJZ}) ")
    int save(@Param("fundJZ") FundJZPO fundJZPO);

    @Select({"<script>select ID, CODE, FSRQ, DWJZ from FUND_JZ WHERE 1=1 <if test=\"code!=null and code!=''\"> and CODE = #{code}  </if> order by FSRQ ASC limit 5 </script>" })
    List<FundJZPO> findResent5FundJZByCode(@Param("code") String code);

    @Select("select ID, CODE, FSRQ, DWJZ from FUND_JZ where CODE = #{code} AND FSRQ = #{date}")
    FundJZPO findFundJZByCodeAndDate(@Param("code") String code, @Param("date") String date);
}