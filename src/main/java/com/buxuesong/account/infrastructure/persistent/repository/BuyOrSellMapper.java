package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.BuyOrSellStockPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BuyOrSellMapper {

    @Update(" INSERT INTO BUY_OR_SELL (DATE, CODE, TYPE, PRICE," +
        " COST, BONDS, INCOME, OPENPRICE) " +
        " values " +
        " (#{buyOrSellStock.date},#{buyOrSellStock.code},#{buyOrSellStock.type},#{buyOrSellStock.price}," +
        " #{buyOrSellStock.cost},#{buyOrSellStock.bonds},#{buyOrSellStock.income},#{buyOrSellStock.openPrice}) ")
    int save(@Param("buyOrSellStock") BuyOrSellStockPO buyOrSellStockPO);

    @Select("select DATE, CODE, TYPE, PRICE, COST, BONDS, INCOME, OPENPRICE from BUY_OR_SELL t where t.date = #{date}")
    List<BuyOrSellStockPO> findAllBuyOrSellStocksByDate(String date);

    @Select("<script> select DATE, CODE, TYPE, PRICE, COST, BONDS, INCOME, OPENPRICE from BUY_OR_SELL t where 1=1" +
        " <if test=\"code!=null and code!=''\"> and code = #{code} </if> " +
        " <if test=\"beginDate!=null and beginDate!=''\"> and DATE &gt;= #{beginDate} </if> " +
        " <if test=\"endDate!=null and endDate!=''\"> and DATE &lt;= #{endDate} </if> " +
        " order by DATE ASC </script>")
    List<BuyOrSellStockPO> findAllBuyOrSellStocks(@Param("code") String code, @Param("beginDate") String beginDate,
        @Param("endDate") String endDate);
}