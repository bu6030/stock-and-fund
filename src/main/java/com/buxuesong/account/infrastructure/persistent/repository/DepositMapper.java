package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.DepositPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepositMapper {

    @Select("select ID, DATE, FUND_DAY_INCOME fundDayIncome, STOCK_DAY_INCOME stockDayIncome, DAY_INCOME totalDayIncome, FUND_MARKET_VALUE fundMarketValue, STOCK_MARKET_VALUE stockMarketValue, TOTAL_MARKET_VALUE totalMarketValue, BIG_MARKET_CHANGE_PERCENT bigMarketChangePercent, BIG_MARKET_VALUE bigMarketValue from DEPOSIT where DATE = #{date} AND USERNAME = #{username} ")
    DepositPO findDepositByDate(@Param("date") String date, @Param("username") String username);

    @Update("INSERT INTO DEPOSIT (DATE, FUND_DAY_INCOME, STOCK_DAY_INCOME, DAY_INCOME, FUND_MARKET_VALUE, STOCK_MARKET_VALUE, TOTAL_MARKET_VALUE, BIG_MARKET_CHANGE_PERCENT, BIG_MARKET_VALUE, USERNAME) values (#{deposit.date},#{deposit.fundDayIncome},#{deposit.stockDayIncome},#{deposit.totalDayIncome},#{deposit.fundMarketValue},#{deposit.stockMarketValue},#{deposit.totalMarketValue},#{deposit.bigMarketChangePercent},#{deposit.bigMarketValue},#{username}) ")
    int save(@Param("deposit") DepositPO deposit, @Param("username") String username);

    @Update("UPDATE DEPOSIT SET FUND_DAY_INCOME = #{deposit.fundDayIncome}, STOCK_DAY_INCOME = #{deposit.stockDayIncome}, DAY_INCOME = #{deposit.totalDayIncome}, FUND_MARKET_VALUE = #{deposit.fundMarketValue}, STOCK_MARKET_VALUE = #{deposit.stockMarketValue}, TOTAL_MARKET_VALUE = #{deposit.totalMarketValue}, BIG_MARKET_CHANGE_PERCENT = #{deposit.bigMarketChangePercent}, BIG_MARKET_VALUE = #{deposit.bigMarketValue} WHERE ID = #{deposit.id} ")
    int update(@Param("deposit") DepositPO deposit, @Param("username") String username);

    @Select("select DATE, FUND_DAY_INCOME fundDayIncome, STOCK_DAY_INCOME stockDayIncome, DAY_INCOME totalDayIncome, FUND_MARKET_VALUE fundMarketValue, STOCK_MARKET_VALUE stockMarketValue, TOTAL_MARKET_VALUE totalMarketValue, BIG_MARKET_CHANGE_PERCENT bigMarketChangePercent, BIG_MARKET_VALUE bigMarketValue from DEPOSIT WHERE USERNAME = #{username} order by DATE ASC")
    List<DepositPO> findAllDeposit(@Param("username") String username);

    @Select({ "<script> select DATE, FUND_DAY_INCOME fundDayIncome, STOCK_DAY_INCOME stockDayIncome, DAY_INCOME totalDayIncome, " +
        " FUND_MARKET_VALUE fundMarketValue, STOCK_MARKET_VALUE stockMarketValue, TOTAL_MARKET_VALUE totalMarketValue, BIG_MARKET_CHANGE_PERCENT bigMarketChangePercent, "
        +
        " BIG_MARKET_VALUE bigMarketValue " +
        " from DEPOSIT " +
        " where 1=1 AND USERNAME = #{username}" +
        " <if test=\"beginDate!=null and beginDate!=''\"> and DATE &gt;= #{beginDate} </if> " +
        " <if test=\"endDate!=null and endDate!=''\"> and DATE &lt;= #{endDate} </if> " +
        " order by DATE DESC</script>" })
    List<DepositPO> getDepositList(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
        @Param("username") String username);

    @Select({
        "<script> select substr(DATE, 0, 5) date, sum(FUND_DAY_INCOME) fundDayIncome, sum(STOCK_DAY_INCOME) stockDayIncome, " +
            " sum(DAY_INCOME) totalDayIncome " +
            " from DEPOSIT " +
            " where 1=1 AND USERNAME = #{username}" +
            " <if test=\"beginDate!=null and beginDate!=''\"> and DATE &gt;= #{beginDate} </if> " +
            " <if test=\"endDate!=null and endDate!=''\"> and DATE &lt;= #{endDate} </if> " +
            " group by substr(DATE, 0, 5) " +
            " order by substr(DATE, 0, 5) DESC</script>" })
    List<DepositPO> getDepositYearSummitList(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
        @Param("username") String username);

    @Select({
        "<script> select substr(DATE, 0, 8) date, sum(FUND_DAY_INCOME) fundDayIncome, sum(STOCK_DAY_INCOME) stockDayIncome, " +
            " sum(DAY_INCOME) totalDayIncome " +
            " from DEPOSIT " +
            " where 1=1 AND USERNAME = #{username} " +
            " <if test=\"beginDate!=null and beginDate!=''\"> and DATE &gt;= #{beginDate} </if> " +
            " <if test=\"endDate!=null and endDate!=''\"> and DATE &lt;= #{endDate} </if> " +
            " group by substr(DATE, 0, 8) " +
            " order by substr(DATE, 0, 8) DESC</script>" })
    List<DepositPO> getDepositMonthSummitList(@Param("beginDate") String beginDate, @Param("endDate") String endDate,
        @Param("username") String username);

    @Delete("delete from DEPOSIT where DATE = #{date} AND USERNAME = #{username} ")
    int deleteDeposit(@Param("date") String date, @Param("username") String username);

}