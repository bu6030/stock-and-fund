package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.response.ChromeStockAndFund;
import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.domain.model.stock.StockEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class ChromeController {

    @Autowired
    private FundEntity fundEntity;

    @Autowired
    private StockEntity stockEntity;

    /**
     * 获取单个基金信息接口
     *
     * @return
     */
    @GetMapping(value = "/chrome/fund")
    public Response getFundList(@RequestParam(value = "fundCode", required = false) String fundCode,
        @RequestParam(value = "costPrise", required = false) String costPrise,
        @RequestParam(value = "bonds", required = false) String bonds,
        @RequestParam(value = "app", required = false) String app) throws Exception {

        String fundArr = fundCode + "," + costPrise + "," + bonds + "," + app;
        List<String> fundListFromRedis = new ArrayList<>();
        fundListFromRedis.add(fundArr);
        return Response.builder().code("00000000").value(fundEntity.getFundDetails(fundListFromRedis).get(0)).build();
    }

    /**
     * 获取数据导入格式json
     *
     * @return
     */
    @GetMapping(value = "/chrome/stockAndFund")
    public Response getStockAndFundList()
        throws Exception {
        List<String> fundListFromRedis = fundEntity.getFundList(null);
        List<String> stockListFromRedis = stockEntity.getStockList(null);
        return Response.builder().code("00000000").value(ChromeStockAndFund.builder().funds(fundEntity.getFundDetails(fundListFromRedis))
            .stocks(stockEntity.getStockDetails(stockListFromRedis)).build()).build();
    }
}