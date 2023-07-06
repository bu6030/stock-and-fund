package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.OpenPersistentMonthPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
@Repository
public interface OpenPersistentMonthMapper {
    @Update("INSERT INTO OPEN_PERSISTENT_MONTH (MONTH, DATA) values (#{openPersistentMonthPO.month},#{openPersistentMonthPO.data}) ")
    int save(@Param("openPersistentMonthPO") OpenPersistentMonthPO openPersistentMonthPO);

    @Select("select MONTH, DATA from OPEN_PERSISTENT_MONTH where MONTH = #{month} ")
    OpenPersistentMonthPO findByMonth(@Param("month") String month);

}
