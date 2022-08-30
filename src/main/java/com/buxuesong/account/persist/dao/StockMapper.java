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

    @Select({"<script> select CODE, COST_PRICE costPrise, BONDS, APP from STOCK WHERE 1=1 <if test=\"app!=null and app!=''\"> and APP = #{app} </if> order by APP ASC, substr(code,3,6) ASC </script>"})
    List<SaveStockRequest> findAllStock(@Param("app") String app);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP from STOCK where code = #{code}")
    SaveStockRequest findStockByCode(String code);

    @Update("update STOCK set COST_PRICE = #{saveStockRequest.costPrise}, BONDS = #{saveStockRequest.bonds}, APP = #{saveStockRequest.app} where CODE = #{saveStockRequest.code} ")
    int updateStock(@Param("saveStockRequest") SaveStockRequest saveStockRequest);

    @Delete("delete from STOCK where CODE = #{saveStockRequest.code} ")
    int deleteStock(@Param("saveStockRequest") SaveStockRequest saveStockRequest);
}
/**
 * CREATE TABLE STOCK ( CODE TEXT(10) PRIMARY KEY NOT NULL, COST_PRICE CHAR(20)
 * NOT NULL, BOUNDS INT NOT NULL, APP CHAR(10) NOT NULL );
 */
