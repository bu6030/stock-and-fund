package com.buxuesong.account.persist.dao;

import com.buxuesong.account.model.BuyOrSellStockRequest;
import com.buxuesong.account.persist.entity.Parameter;
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

    @Update("UPDATE PARAM set NAME = #{parameter.name} where TYPE = #{parameter.type} and CODE = #{parameter.code} ")
    int update(@Param("parameter") Parameter parameter);

    @Select("select DATE, CODE, TYPE, PRICE, COST, BONDS, INCOME, OPENPRICE from BUY_OR_SELL t where t.date = #{date}")
    List<BuyOrSellStockRequest> findAllBuyOrSellStocks(String date);

    @Delete("delete from PARAM where TYPE = #{parameter.type} and CODE = #{parameter.code} ")
    int delete(@Param("parameter") Parameter parameter);

}