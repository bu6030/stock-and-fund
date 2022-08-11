package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.service.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class DepositController {

    @Autowired
    private DepositService depositService;

    /**
     * 获取统计信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/deposit")
    public Response getFundList(HttpServletRequest request) throws Exception {
        return Response.builder().code("00000000").value(depositService.getDepositList()).build();
    }
}
