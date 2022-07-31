package com.buxuesong.account.persist.dao;

import com.buxuesong.account.model.SaveStockRequest;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StockMapper {

    @Update("INSERT INTO STOCK (CODE, COST_PRICE, BONDS, APP) values (#{saveStockRequest.code},#{saveStockRequest.costPrise},#{saveStockRequest.bonds},#{saveStockRequest.app}) ")
    int save(@Param("saveStockRequest") SaveStockRequest saveStockRequest);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from STOCK")
    List<SaveStockRequest> findAllStock();

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from STOCK where code = #{code}")
    SaveStockRequest findStockByCode(String code);

    @Update("update STOCK set COST_PRICE = #{saveStockRequest.costPrise}, BONDS = #{saveStockRequest.bonds}, APP = #{saveStockRequest.app} where CODE = #{saveStockRequest.code} ")
    int updateStock(@Param("saveStockRequest") SaveStockRequest saveStockRequest);

    @Delete("delete from STOCK where CODE = #{saveStockRequest.code} ")
    int deleteStock(@Param("saveStockRequest") SaveStockRequest saveStockRequest);
}