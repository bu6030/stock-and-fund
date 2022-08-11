package com.buxuesong.account.persist.dao;

import com.buxuesong.account.persist.entity.Deposit;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepositMapper {

    @Update("INSERT INTO DEPOSIT (DATE, FUND_DAY_INCOME, STOCK_DAY_INCOME, DAY_INCOME) values (#{deposit.date},#{deposit.fundDayIncome},#{deposit.stockDayIncome},#{deposit.totalDayIncome}) ")
    int save(@Param("deposit") Deposit deposit);

    @Select("select DATE, FUND_DAY_INCOME fundDayIncome, STOCK_DAY_INCOME stockDayIncome, DAY_INCOME totalDayIncome from DEPOSIT order by DATE ASC")
    List<Deposit> findAllDeposit();

}
/**
 * CREATE TABLE DEPOSIT ( DATE TEXT(10) PRIMARY KEY NOT NULL, FUND_DAY_INCOME
 * CHAR(20) NOT NULL, STOCK_DAY_INCOME CHAR(20) NOT NULL, DAY_INCOME CHAR(20)
 * NOT NULL );
 */