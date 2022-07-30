package com.buxuesong.account.persist.dao;

import com.buxuesong.account.persist.entity.StockAndFund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StockAndFundMapper {

    @Select("select ID id,STOCK_AND_FUND_INFO stockAndFundInfo,TYPE from STOCK_AND_FUND where TYPE = #{type}")
    StockAndFund findByType(int type);

    @Update("update STOCK_AND_FUND set STOCK_AND_FUND_INFO = #{stockAndFund.stockAndFundInfo} where TYPE = #{stockAndFund.type ")
    int update(@Param("stockAndFund") StockAndFund stockAndFund);
}
