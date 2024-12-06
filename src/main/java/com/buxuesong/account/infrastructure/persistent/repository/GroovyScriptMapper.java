package com.buxuesong.account.infrastructure.persistent.repository;

import com.buxuesong.account.infrastructure.persistent.po.GroovyScriptPO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GroovyScriptMapper {

    @Update("INSERT INTO GROOVY_SCRIPT (CODE_TEXT) values (#{groovyScriptPO.codeText}) ")
    int save(@Param("groovyScriptPO") GroovyScriptPO groovyScriptPO);

    @Select("select CODE_TEXT from GROOVY_SCRIPT order by CREATE_DATE desc limit 1")
    GroovyScriptPO findGroovyScript();

    @Update("update GROOVY_SCRIPT set CODE_TEXT = #{groovyScriptPO.codeText} ")
    int updateGroovyScript(@Param("groovyScriptPO") GroovyScriptPO groovyScriptPO);
}