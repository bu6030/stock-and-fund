package com.buxuesong.account.persist.dao;

import com.buxuesong.account.model.SaveFundRequest;
import com.buxuesong.account.model.SaveStockRequest;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundMapper {

    @Update("INSERT INTO FUND (CODE, COST_PRICE, BONDS, APP) values (#{saveFundRequest.code},#{saveFundRequest.costPrise},#{saveFundRequest.bonds},#{saveFundRequest.app}) ")
    int save(@Param("saveFundRequest") SaveFundRequest saveFundRequest);

    @Select({
        "<script> select CODE, COST_PRICE costPrise, BONDS, APP from FUND WHERE 1=1 <if test=\"app!=null and app!=''\"> and APP = #{app}  </if> order by APP ASC, CODE ASC </script>" })
    List<SaveFundRequest> findAllFund(@Param("app") String app);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from FUND where code = #{code}")
    SaveFundRequest findFundByCode(String code);

    @Update("update FUND set COST_PRICE = #{saveFundRequest.costPrise}, BONDS = #{saveFundRequest.bonds}, APP = #{saveFundRequest.app} where CODE = #{saveFundRequest.code} ")
    int updateFund(@Param("saveFundRequest") SaveFundRequest saveFundRequest);

    @Delete("delete from FUND where CODE = #{saveFundRequest.code} ")
    int deleteFund(@Param("saveFundRequest") SaveFundRequest saveFundRequest);
}
/**
 * CREATE TABLE FUND ( CODE TEXT(10) PRIMARY KEY NOT NULL, COST_PRICE CHAR(20)
 * NOT NULL, BOUNDS CHAR(30) NOT NULL, APP CHAR(10) NOT NULL );
 */