package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.ParamPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ParamMapper {

    @Select("select TYPE, CODE, NAME from PARAM where TYPE = #{type} AND USERNAME = #{username} ")
    List<ParamPO> findParamByType(@Param("type") String type, @Param("username") String username);

    @Select("select TYPE, CODE, NAME from PARAM where TYPE = #{paramEntity.type} AND CODE = #{paramEntity.code} AND USERNAME = #{username} ")
    ParamPO findParamByTypeAndCode(@Param("paramEntity") ParamPO paramPO, @Param("username") String username);

    @Update("INSERT INTO PARAM (TYPE, CODE, NAME, USERNAME) values (#{paramEntity.type},#{paramEntity.code},#{paramEntity.name}) ")
    int save(@Param("paramEntity") ParamPO paramPO, @Param("username") String username);

    @Update("UPDATE PARAM set NAME = #{paramEntity.name} where TYPE = #{paramEntity.type} and CODE = #{paramEntity.code} AND USERNAME = #{username} ")
    int update(@Param("paramEntity") ParamPO paramPO, @Param("username") String username);

    @Select("select TYPE, CODE, NAME from PARAM WHERE USERNAME = #{username}")
    List<ParamPO> findAllParam(@Param("username") String username);

    @Delete("delete from PARAM where TYPE = #{paramEntity.type} and CODE = #{paramEntity.code} AND USERNAME = #{username} ")
    int delete(@Param("paramEntity") ParamPO paramPO, @Param("username") String username);

}