package com.buxuesong.account.apis.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@RestController
public class GroovyScriptController {

    @PostMapping("/execute-query")
    String executeQuery(@RequestParam String query) throws Exception{
        query = "import groovy.sql.Sql\n" +
                "def sql = Sql.newInstance('jdbc:sqlite:/Users/buxuesong/Documents/git_code/stock-and-fund/stock-and-fund.db', '', '', 'org.sqlite.JDBC')\n"
                + query;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
        // 假设query是一个简单的Groovy表达式
        Object result = engine.eval(query);
        return "Result: " + result.toString();
    }
}