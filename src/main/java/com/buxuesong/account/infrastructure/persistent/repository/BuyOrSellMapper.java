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
    List<BuyOrSellStockPO> findAllBuyOrSellStocks(String date);

}