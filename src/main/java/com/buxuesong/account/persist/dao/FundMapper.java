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

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from FUND")
    List<SaveFundRequest> findAllFund();

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from FUND where code = #{code}")
    SaveFundRequest findFundByCode(String code);

    @Update("update FUND set COST_PRICE = #{saveFundRequest.costPrise}, BONDS = #{saveFundRequest.bonds}, APP = #{saveFundRequest.app} where CODE = #{saveFundRequest.code} ")
    int updateFund(@Param("saveFundRequest") SaveFundRequest saveFundRequest);

    @Delete("delete from FUND where CODE = #{saveFundRequest.code} ")
    int deleteFund(@Param("saveFundRequest") SaveFundRequest saveFundRequest);
}