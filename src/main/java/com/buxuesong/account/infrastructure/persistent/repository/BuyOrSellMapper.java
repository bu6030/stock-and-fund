package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.BuyOrSellStockPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BuyOrSellMapper {

    @Update(" INSERT INTO BUY_OR_SELL (DATE, CODE, TYPE, PRICE," +
        " COST, BONDS, INCOME, OPENPRICE, USERNAME) " +
        " values " +
        " (#{buyOrSellStock.date},#{buyOrSellStock.code},#{buyOrSellStock.type},#{buyOrSellStock.price}," +
        " #{buyOrSellStock.cost},#{buyOrSellStock.bonds},#{buyOrSellStock.income},#{buyOrSellStock.openPrice}, #{username}) ")
    int save(@Param("buyOrSellStock") BuyOrSellStockPO buyOrSellStockPO, @Param("username") String username);

    @Select("select DATE, CODE, TYPE, PRICE, COST, BONDS, INCOME, OPENPRICE from BUY_OR_SELL t where t.date = #{date} AND t.USERNAME = #{username}")
    List<BuyOrSellStockPO> findAllBuyOrSellStocksByDate(@Param("date") String date, @Param("username") String username);

    @Select("<script> select DATE, CODE, TYPE, PRICE, COST, BONDS, INCOME, OPENPRICE from BUY_OR_SELL t where USERNAME = #{username}" +
        " <if test=\"code!=null and code!=''\"> and code = #{code} </if> " +
        " <if test=\"beginDate!=null and beginDate!=''\"> and DATE &gt;= #{beginDate} </if> " +
        " <if test=\"endDate!=null and endDate!=''\"> and DATE &lt;= #{endDate} </if> " +
        " order by DATE DESC </script>")
    List<BuyOrSellStockPO> findAllBuyOrSellStocks(@Param("code") String code, @Param("beginDate") String beginDate,
        @Param("endDate") String endDate, @Param("username") String username);
}