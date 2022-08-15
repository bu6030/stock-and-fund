package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.persist.entity.Parameter;
import com.buxuesong.account.service.ParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class ParamController {

    @Autowired
    private ParamService paramService;

    /**
     * 获取字典表参数接口
     *
     * @return
     */
    @GetMapping(value = "/param")
    public Response getDepositList(HttpServletRequest request, @RequestParam(value = "type", required = false) String type) throws Exception {
        if(type == null){
            return Response.builder().code("00000000").value(paramService.getParamList()).build();
        } else {
            return Response.builder().code("00000000").value(paramService.getParamList(type)).build();
        }
    }

    /**
     * 新增字典表参数接口
     *
     * @return
     */
    @PostMapping(value = "/param")
    public Response depositToday(HttpServletRequest request, @RequestBody Parameter parameter) throws Exception {
        paramService.saveParam(parameter);
        return Response.builder().code("00000000").build();
    }

    /**
     * 删除字典表参数接口
     *
     * @return
     */
    @DeleteMapping(value = "/param")
    public Response deleteDeposit(HttpServletRequest request, @RequestBody Parameter parameter) throws Exception {
        paramService.deleteParam(parameter);
        return Response.builder().code("00000000").build();
    }
}
