package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.domain.model.deposit.DepositEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class DepositController {

    @Autowired
    private DepositEntity depositEntity;

    /**
     * 获取统计信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/deposit")
    public Response getDepositList(@RequestParam(value = "beginDate", required = false) String beginDate,
        @RequestParam(value = "endDate", required = false) String endDate) throws Exception {
        return Response.builder().code("00000000").value(depositEntity.getDepositList(beginDate, endDate)).build();
    }

    /**
     * 重新统计当日盈利接口
     *
     * @return
     */
    @PostMapping(value = "/deposit")
    public Response depositToday() throws Exception {
        depositEntity.deposit();
        return Response.builder().code("00000000").build();
    }

    /**
     * 删除当日盈利接口
     *
     * @return
     */
    @DeleteMapping(value = "/deposit")
    public Response deleteDeposit() throws Exception {
        depositEntity.deleteDeposit();
        return Response.builder().code("00000000").build();
    }
}
