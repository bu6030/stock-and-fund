package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.FundPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundMapper {

    @Update("INSERT INTO FUND (CODE, NAME, COST_PRICE, BONDS, APP, USERNAME) values (#{fund.code},#{fund.name},#{fund.costPrise},#{fund.bonds},#{fund.app},#{username}) ")
    int save(@Param("fund") FundPO fundPO, @Param("username") String username);

    @Select({
        "<script> select CODE, COST_PRICE costPrise, BONDS, APP from FUND WHERE 1=1 AND USERNAME = #{username} <if test=\"app!=null and app!=''\"> and APP = #{app}  </if> order by APP ASC, CODE ASC </script>" })
    List<FundPO> findAllFund(@Param("app") String app, @Param("username") String username);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from FUND where code = #{code} AND USERNAME = #{username}")
    FundPO findFundByCode(String code, @Param("username") String username);

    @Update("update FUND set COST_PRICE = #{fund.costPrise}, BONDS = #{fund.bonds}, APP = #{fund.app} where CODE = #{fund.code} AND USERNAME = #{username} ")
    int updateFund(@Param("fund") FundPO fundPO, @Param("username") String username);

    @Delete("delete from FUND where CODE = #{fund.code} AND USERNAME = #{username} ")
    int deleteFund(@Param("fund") FundPO fundPO, @Param("username") String username);
}