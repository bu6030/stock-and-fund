package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.StockHisPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StockHisMapper {

    @Update("INSERT INTO STOCK_HIS (CODE, NAME, COST_PRICE, BONDS, APP, HIDE, CREATE_DATE) select t.CODE, t.NAME, t.COST_PRICE, t.BONDS, t.APP, t.HIDE, datetime('now','localtime') from stock t where t.code=#{code} ")
    int saveFromStock(@Param("code") String code);

    @Select({
            "<script> select CODE, NAME, COST_PRICE costPrise, BONDS, APP, HIDE, CREATE_DATE createDate from STOCK_HIS WHERE 1=1" +
                    " <if test=\"app!=null and app!=''\"> and APP = #{app} </if> order by APP ASC, substr(code,3,6) ASC </script>" })
    List<StockHisPO> findAllStockHis(@Param("app") String app);
}
