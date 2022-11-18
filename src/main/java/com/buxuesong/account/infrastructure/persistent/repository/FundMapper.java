package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.apis.model.request.FundRequest;
import com.buxuesong.account.infrastructure.persistent.po.FundPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundMapper {

    @Update("INSERT INTO FUND (CODE, COST_PRICE, BONDS, APP) values (#{fund.code},#{fund.costPrise},#{fund.bonds},#{fund.app}) ")
    int save(@Param("fund") FundPO fundPO);

    @Select({
        "<script> select CODE, COST_PRICE costPrise, BONDS, APP from FUND WHERE 1=1 <if test=\"app!=null and app!=''\"> and APP = #{app}  </if> order by APP ASC, CODE ASC </script>" })
    List<FundPO> findAllFund(@Param("app") String app);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from FUND where code = #{code}")
    FundPO findFundByCode(String code);

    @Update("update FUND set COST_PRICE = #{fund.costPrise}, BONDS = #{fund.bonds}, APP = #{fund.app} where CODE = #{fund.code} ")
    int updateFund(@Param("fund") FundPO fundPO);

    @Delete("delete from FUND where CODE = #{fund.code} ")
    int deleteFund(@Param("fund") FundPO fundPO);
}
/**
 * CREATE TABLE FUND ( CODE TEXT(10) PRIMARY KEY NOT NULL, COST_PRICE CHAR(20)
 * NOT NULL, BOUNDS CHAR(30) NOT NULL, APP CHAR(10) NOT NULL );
 */