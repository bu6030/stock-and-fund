package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.FundHisPO;
import com.buxuesong.account.infrastructure.persistent.po.StockHisPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FundHisMapper {

    @Update("INSERT INTO FUND_HIS (CODE, NAME, COST_PRICE, BONDS, APP, HIDE, CREATE_DATE, USERNAME) select t.CODE, t.NAME, t.COST_PRICE, t.BONDS, t.APP, t.HIDE, datetime('now','localtime'), t.USERNAME from FUND t where t.code=#{code} AND t.USERNAME = #{username} ")
    int saveFromFund(@Param("code") String code, @Param("username") String username);

    @Select({
        "<script> select CODE, NAME, COST_PRICE costPrise, BONDS, APP, HIDE, CREATE_DATE createDate from FUND_HIS WHERE 1=1 AND USERNAME = #{username} "
            +
            " <if test=\"code!=null and code!=''\"> and code = #{code} </if> " +
            " <if test=\"beginDate!=null and beginDate!=''\"> and CREATE_DATE &gt;= #{beginDate} </if> " +
            " <if test=\"endDate!=null and endDate!=''\"> and CREATE_DATE &lt;= #{endDate} </if> " +
            " <if test=\"app!=null and app!=''\"> and APP = #{app} </if> order by APP ASC, substr(code,3,6) ASC, CREATE_DATE DESC </script>" })
    List<FundHisPO> findAllFundHis(@Param("app") String app, @Param("code") String code, @Param("beginDate") String beginDate,
        @Param("endDate") String endDate, @Param("username") String username);
}
