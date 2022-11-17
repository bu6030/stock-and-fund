package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.request.ParamRequest;
import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.domain.model.param.ParamEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class ParamController {

    @Autowired
    private ParamEntity paramEntity;

    /**
     * 获取字典表参数接口
     *
     * @return
     */
    @GetMapping(value = "/param")
    public Response getDepositList(HttpServletRequest request, @RequestParam(value = "type", required = false) String type)
        throws Exception {
        if (type == null) {
            return Response.builder().code("00000000").value(paramEntity.getParamList()).build();
        } else {
            return Response.builder().code("00000000").value(paramEntity.getParamList(type)).build();
        }
    }

    /**
     * 新增字典表参数接口
     *
     * @return
     */
    @PostMapping(value = "/param")
    public Response saveParam(HttpServletRequest request, @RequestBody ParamRequest paramRequest) throws Exception {
        paramEntity.saveParam(paramRequest);
        return Response.builder().code("00000000").build();
    }

    /**
     * 删除字典表参数接口
     *
     * @return
     */
    @DeleteMapping(value = "/param")
    public Response deleteDeposit(HttpServletRequest request, @RequestBody ParamRequest paramRequest) throws Exception {
        paramEntity.deleteParam(paramRequest);
        return Response.builder().code("00000000").build();
    }
}
