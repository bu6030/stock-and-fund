package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.service.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Response getDepositList(HttpServletRequest request,@RequestParam(value = "beginDate",required = false) String beginDate,@RequestParam(value = "endDate",required = false) String endDate) throws Exception {
         return Response.builder().code("00000000").value(depositService.getDepositList(beginDate, endDate)).build();
    }

    /**
     * 重新统计当日盈利接口
     *
     * @return
     */
    @PostMapping(value = "/deposit")
    public Response depositToday(HttpServletRequest request) throws Exception {
        depositService.deposit();
        return Response.builder().code("00000000").build();
    }

    /**
     * 删除当日盈利接口
     *
     * @return
     */
    @DeleteMapping(value = "/deposit")
    public Response deleteDeposit(HttpServletRequest request) throws Exception {
        depositService.deleteDeposit();
        return Response.builder().code("00000000").build();
    }
}
