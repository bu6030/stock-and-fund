package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.SaveFundRequest;
import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.service.FundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class FundController {

    @Autowired
    private FundService fundService;

    /**
     * 获取账本列表接口
     *
     * @return
     */
    @GetMapping(value = "/fund")
    public Response getFundList(HttpServletRequest request) throws Exception {
        List<String> fundListFromRedis = fundService.getFundList();
        return Response.builder().code("00000000").value(fundService.getFundDetails(fundListFromRedis)).build();
    }

    /**
     * 获取账本列表接口
     *
     * @return
     */
    @GetMapping(value = "/fund/table")
    public Response getRedisFund(HttpServletRequest request) throws Exception {
        return Response.builder().code("00000000").value(fundService.getFund()).build();
    }

    /**
     * 获取账本列表接口
     *
     * @return
     */
    @PostMapping(value = "/fund")
    public Response saveFund(HttpServletRequest request, @RequestBody SaveFundRequest saveFundRequest) throws Exception {
        fundService.saveFund(saveFundRequest.getFund());
        return Response.builder().value(true).code("00000000").build();
    }
}
