package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.StockPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StockMapper {

    @Update("INSERT INTO STOCK (CODE, COST_PRICE, BONDS, APP, HIDE) values (#{stock.code},#{stock.costPrise},#{stock.bonds},#{stock.app},#{stock.hide}) ")
    int save(@Param("stock") StockPO StockPO);

    @Select({
        "<script> select CODE, COST_PRICE costPrise, BONDS, APP, HIDE from STOCK WHERE 1=1 <if test=\"app!=null and app!=''\"> and APP = #{app} </if> order by APP ASC, substr(code,3,6) ASC </script>" })
    List<StockPO> findAllStock(@Param("app") String app);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP, HIDE from STOCK where code = #{code}")
    StockPO findStockByCode(String code);

    @Update("update STOCK set COST_PRICE = #{stock.costPrise}, BONDS = #{stock.bonds}, APP = #{stock.app}, HIDE = #{stock.hide} where CODE = #{stock.code} ")
    int updateStock(@Param("stock") StockPO stockPO);

    @Delete("delete from STOCK where CODE = #{stock.code} ")
    int deleteStock(@Param("stock") StockPO stockPO);
}
/**
 * CREATE TABLE STOCK ( CODE TEXT(10) PRIMARY KEY NOT NULL, COST_PRICE CHAR(20)
 * NOT NULL, BOUNDS INT NOT NULL, APP CHAR(10) NOT NULL );
 */
