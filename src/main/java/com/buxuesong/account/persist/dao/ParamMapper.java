package com.buxuesong.account.persist.dao;

import com.buxuesong.account.persist.entity.Parameter;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ParamMapper {

    @Select("select TYPE, CODE, NAME from PARAM where TYPE = #{type} ")
    List<Parameter> findParamByType(@Param("type") String type);

    @Select("select TYPE, CODE, NAME from PARAM where TYPE = #{parameter.type} AND CODE = #{parameter.code} ")
    Parameter findParamByTypeAndCode(@Param("parameter") Parameter parameter);

    @Update("INSERT INTO PARAM (TYPE, CODE, NAME) values (#{parameter.type},#{parameter.code},#{parameter.name}) ")
    int save(@Param("parameter") Parameter parameter);

    @Update("UPDATE PARAM set NAME = #{parameter.name} where TYPE = #{parameter.type} and CODE = #{parameter.code} ")
    int update(@Param("parameter") Parameter parameter);

    @Select("select TYPE, CODE, NAME from PARAM")
    List<Parameter> findAllParam();

    @Delete("delete from PARAM where TYPE = #{parameter.type} and CODE = #{parameter.code} ")
    int delete(@Param("parameter") Parameter parameter);

}