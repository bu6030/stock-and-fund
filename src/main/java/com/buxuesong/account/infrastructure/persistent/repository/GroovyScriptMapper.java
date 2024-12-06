package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.GroovyScriptPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GroovyScriptMapper {

    @Update("INSERT INTO GROOVY_SCRIPT (CODE_TEXT, CREATE_DATE) values (#{groovyScriptPO.codeText}, #{groovyScriptPO.createDate}) ")
    int save(@Param("groovyScriptPO") GroovyScriptPO groovyScriptPO);

    @Select("select ID, CODE_TEXT as codeText, CREATE_DATE as createDate from GROOVY_SCRIPT order by CREATE_DATE desc limit 1")
    GroovyScriptPO findGroovyScript();

    @Update("update GROOVY_SCRIPT set CODE_TEXT = #{groovyScriptPO.codeText}, CREATE_DATE = #{groovyScriptPO.createDate} where id = #{groovyScriptPO.id} ")
    int updateGroovyScript(@Param("groovyScriptPO") GroovyScriptPO groovyScriptPO);
}