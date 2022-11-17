package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.apis.model.request.BuyOrSellStockRequest;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BuyOrSellMapper {

    @Update(" INSERT INTO BUY_OR_SELL (DATE, CODE, TYPE, PRICE," +
        " COST, BONDS, INCOME, OPENPRICE) " +
        " values " +
        " (#{buyOrSellStockRequest.date},#{buyOrSellStockRequest.code},#{buyOrSellStockRequest.type},#{buyOrSellStockRequest.price}," +
        " #{buyOrSellStockRequest.cost},#{buyOrSellStockRequest.bonds},#{buyOrSellStockRequest.income},#{buyOrSellStockRequest.openPrice}) ")
    int save(@Param("buyOrSellStockRequest") BuyOrSellStockRequest buyOrSellStockRequest);

    @Select("select DATE, CODE, TYPE, PRICE, COST, BONDS, INCOME, OPENPRICE from BUY_OR_SELL t where t.date = #{date}")
    List<BuyOrSellStockRequest> findAllBuyOrSellStocks(String date);

}