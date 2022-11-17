package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.apis.model.request.FundRequest;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundMapper {

    @Update("INSERT INTO FUND (CODE, COST_PRICE, BONDS, APP) values (#{saveFundRequest.code},#{saveFundRequest.costPrise},#{saveFundRequest.bonds},#{saveFundRequest.app}) ")
    int save(@Param("saveFundRequest") FundRequest fundRequest);

    @Select({
        "<script> select CODE, COST_PRICE costPrise, BONDS, APP from FUND WHERE 1=1 <if test=\"app!=null and app!=''\"> and APP = #{app}  </if> order by APP ASC, CODE ASC </script>" })
    List<FundRequest> findAllFund(@Param("app") String app);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from FUND where code = #{code}")
    FundRequest findFundByCode(String code);

    @Update("update FUND set COST_PRICE = #{saveFundRequest.costPrise}, BONDS = #{saveFundRequest.bonds}, APP = #{saveFundRequest.app} where CODE = #{saveFundRequest.code} ")
    int updateFund(@Param("saveFundRequest") FundRequest fundRequest);

    @Delete("delete from FUND where CODE = #{saveFundRequest.code} ")
    int deleteFund(@Param("saveFundRequest") FundRequest fundRequest);
}
/**
 * CREATE TABLE FUND ( CODE TEXT(10) PRIMARY KEY NOT NULL, COST_PRICE CHAR(20)
 * NOT NULL, BOUNDS CHAR(30) NOT NULL, APP CHAR(10) NOT NULL );
 */