package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.entity.ParamPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ParamMapper {

    @Select("select TYPE, CODE, NAME from PARAM where TYPE = #{type} ")
    List<ParamPO> findParamByType(@Param("type") String type);

    @Select("select TYPE, CODE, NAME from PARAM where TYPE = #{paramEntity.type} AND CODE = #{paramEntity.code} ")
    ParamPO findParamByTypeAndCode(@Param("paramEntity") ParamPO paramPO);

    @Update("INSERT INTO PARAM (TYPE, CODE, NAME) values (#{paramEntity.type},#{paramEntity.code},#{paramEntity.name}) ")
    int save(@Param("paramEntity") ParamPO paramPO);

    @Update("UPDATE PARAM set NAME = #{paramEntity.name} where TYPE = #{paramEntity.type} and CODE = #{paramEntity.code} ")
    int update(@Param("paramEntity") ParamPO paramPO);

    @Select("select TYPE, CODE, NAME from PARAM")
    List<ParamPO> findAllParam();

    @Delete("delete from PARAM where TYPE = #{paramEntity.type} and CODE = #{paramEntity.code} ")
    int delete(@Param("paramEntity") ParamPO paramPO);

}