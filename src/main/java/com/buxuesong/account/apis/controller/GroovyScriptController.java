package com.buxuesong.account.apis.controller;

import com.buxuesong.account.domain.service.GroovyScriptService;
import com.buxuesong.account.infrastructure.persistent.po.GroovyScriptPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Objects;

@RestController
public class GroovyScriptController {

    @Value("${sqllite.db.file}")
    private String sqlliteDbFile;

    @Autowired
    private GroovyScriptService groovyScriptService;

    @PostMapping("/execute-query")
    public String executeQuery(@RequestParam String query) throws Exception {
        GroovyScriptPO groovyScriptPO = groovyScriptService.getGroovyScript();
        if (Objects.isNull(groovyScriptPO)) {
            groovyScriptPO = new GroovyScriptPO();
            groovyScriptPO.setCodeText(query);
            groovyScriptService.saveGroovyScript(groovyScriptPO);
        } else {
            groovyScriptPO.setCodeText(query);
            groovyScriptService.updateGroovyScript(groovyScriptPO);
        }
        query = "import groovy.sql.Sql \n" +
            "def sql = null \n" +
            "try { \n" +
            "sql = Sql.newInstance('jdbc:sqlite:" + sqlliteDbFile + "', '', '', 'org.sqlite.JDBC')\n" +
            query + "\n" +
            "} finally {\n" +
            "sql.close()\n" +
            "}\n";
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
        // 假设query是一个简单的Groovy表达式
        Object result = engine.eval(query);
        return "Result: " + result.toString();
    }

    @GetMapping("/groovy-script")
    public String getGroovyScript() throws Exception {
        GroovyScriptPO groovyScriptPO = groovyScriptService.getGroovyScript();
        if (Objects.isNull(groovyScriptPO)) {
            groovyScriptPO = new GroovyScriptPO();
            groovyScriptPO.setCodeText("""
                    def results = []
                    sql.eachRow('SELECT * FROM stock') { row ->
                        def id = row['id']
                        def name = row['name']
                        // 将结果添加到列表中
                        results << [id: id, name: name]
                    }
                    return results
                    """);
        }
        return groovyScriptPO.getCodeText();
    }
}