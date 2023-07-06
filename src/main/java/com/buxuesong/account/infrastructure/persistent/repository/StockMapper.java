package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.StockPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StockMapper {

    @Update("INSERT INTO STOCK (CODE, NAME, COST_PRICE, BONDS, APP, HIDE, USERNAME) values (#{stock.code},#{stock.name},#{stock.costPrise},#{stock.bonds},#{stock.app},#{stock.hide}, #{username}) ")
    int save(@Param("stock") StockPO StockPO, @Param("username") String username);

    @Select({
        "<script> select CODE, COST_PRICE costPrise, BONDS, APP, HIDE from STOCK WHERE 1=1 AND USERNAME = #{username} <if test=\"app!=null and app!=''\"> and APP = #{app} </if> order by APP ASC, substr(code,3,6) ASC </script>" })
    List<StockPO> findAllStock(@Param("app") String app, @Param("username") String username);

    @Select("select CODE, COST_PRICE costPrise, BONDS, APP, HIDE from STOCK where code = #{code} AND USERNAME = #{username} ")
    StockPO findStockByCode(@Param("code") String code, @Param("username") String username);

    @Update("update STOCK set COST_PRICE = #{stock.costPrise}, BONDS = #{stock.bonds}, APP = #{stock.app}, HIDE = #{stock.hide} where CODE = #{stock.code} AND USERNAME = #{username} ")
    int updateStock(@Param("stock") StockPO stockPO, @Param("username") String username);

    @Delete("delete from STOCK where CODE = #{stock.code} AND USERNAME = #{username} ")
    int deleteStock(@Param("stock") StockPO stockPO, @Param("username") String username);
}