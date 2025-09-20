package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.request.AdviceRequest;
import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.domain.model.advice.AdviceEntity;
import com.buxuesong.account.infrastructure.persistent.po.AdvicePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class AdviceController {

    @Autowired
    private AdviceEntity adviceEntity;

    /**
     * 获取建议列表接口
     *
     * @return
     */
    @GetMapping(value = "/advice")
    public Response getAdviceList(@RequestParam(value = "app", required = false) String app,
        @RequestParam(value = "code", required = false) String code) throws Exception {
        List<AdvicePO> fundList = adviceEntity.getAdvice();
        return Response.builder().code("00000000").value(fundList).build();
    }


    /**
     * 修改建议接口
     *
     * @return
     */
    @PostMapping(value = "/updateAdvice")
    public Response saveFund(@RequestBody AdviceRequest request) throws Exception {
        log.info("Update advice request: {}", request);
        if (adviceEntity.updateAdvice(request)) {
            return Response.builder().value(true).code("00000000").build();
        }
        return Response.builder().value(true).code("00000001").build();
    }

    /**
     * 删除建议接口
     *
     * @return
     */
    @PostMapping(value = "/deleteAdvice")
    public Response deleteAdvice(@RequestBody AdviceRequest request) throws Exception {
        log.info("Delete advice request: {}", request);
        adviceEntity.deleteAdvice(request);
        return Response.builder().value(true).code("00000000").build();
    }
}
