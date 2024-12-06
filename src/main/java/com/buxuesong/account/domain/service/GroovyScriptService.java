package com.buxuesong.account.domain.service;

import com.buxuesong.account.infrastructure.persistent.po.GroovyScriptPO;
import com.buxuesong.account.infrastructure.persistent.repository.GroovyScriptMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class GroovyScriptService {

    @Autowired
    private GroovyScriptMapper groovyScriptMapper;

    public GroovyScriptPO getGroovyScript() {
        return groovyScriptMapper.findGroovyScript();
    }

    public void saveGroovyScript(GroovyScriptPO groovyScriptPO) {
        groovyScriptMapper.save(groovyScriptPO);
    }
    public void updateGroovyScript(GroovyScriptPO groovyScriptPO) {
        groovyScriptMapper.updateGroovyScript(groovyScriptPO);
    }

}
