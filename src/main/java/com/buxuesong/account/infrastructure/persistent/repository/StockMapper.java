package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.apis.model.request.StockRequest;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StockMapper {

    @Update("INSERT INTO STOCK (CODE, COST_PRICE, BONDS, APP, HIDE) values (#{saveStockRequest.code},#{saveStockRequest.costPrise},#{saveStockRequest.bonds},#{saveStockRequest.app},#{saveStockRequest.hide}) ")
    int save(@Param("saveStockRequest") StockRequest stockRequest);

    @Select({
        "<script> select CODE, COST_PRICE costPrise, BONDS, APP, HIDE from STOCK WHERE 1=1 <if test=\"app!=null and app!=''\"> and APP = #{app} </if> order by APP ASC, substr(code,3,6) ASC </script>" })
    List<StockRequest> findAllStock(@Param("app") String app);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP, HIDE from STOCK where code = #{code}")
    StockRequest findStockByCode(String code);

    @Update("update STOCK set COST_PRICE = #{saveStockRequest.costPrise}, BONDS = #{saveStockRequest.bonds}, APP = #{saveStockRequest.app}, HIDE = #{saveStockRequest.hide} where CODE = #{saveStockRequest.code} ")
    int updateStock(@Param("saveStockRequest") StockRequest stockRequest);

    @Delete("delete from STOCK where CODE = #{saveStockRequest.code} ")
    int deleteStock(@Param("saveStockRequest") StockRequest stockRequest);
}
/**
 * CREATE TABLE STOCK ( CODE TEXT(10) PRIMARY KEY NOT NULL, COST_PRICE CHAR(20)
 * NOT NULL, BOUNDS INT NOT NULL, APP CHAR(10) NOT NULL );
 */
