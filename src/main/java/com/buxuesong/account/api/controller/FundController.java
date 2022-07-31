package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.SaveFundRequest;
import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.service.FundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
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
    @PostMapping(value = "/saveFund")
    public Response saveFund(@RequestBody SaveFundRequest saveFundRequest) throws Exception {
        log.info("Save fund request: {}", saveFundRequest);
        if (fundService.saveFund(saveFundRequest)) {
            return Response.builder().value(true).code("00000000").build();
        }
        return Response.builder().value(true).code("00000001").build();
    }

    /**
     * 获取账本列表接口
     *
     * @return
     */
    @PostMapping(value = "/deleteFund")
    public Response deleteFund(@RequestBody SaveFundRequest saveFundRequest) throws Exception {
        log.info("Delete fund request: {}", saveFundRequest);
        fundService.deleteFund(saveFundRequest);
        return Response.builder().value(true).code("00000000").build();
    }
}
