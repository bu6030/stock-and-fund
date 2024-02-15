package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.response.ChromeStockAndFund;
import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.domain.model.advice.AdviceEntity;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.domain.model.stock.StockEntity;
import com.buxuesong.account.infrastructure.persistent.po.AdvicePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    private AdviceEntity adviceEntity;

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
     * 获取数据导入格式json 自己 chrome 插件使用，因此 username 参数默认写了 buxuesong
     * 
     * @return
     */
    @GetMapping(value = "/chrome/stockAndFund")
    public Response getStockAndFundList()
        throws Exception {
        List<String> fundListFromRedis = fundEntity.getFundList(null, "buxuesong");
        List<String> stockListFromRedis = stockEntity.getStockList(null, "buxuesong");
        return Response.builder().code("00000000").value(ChromeStockAndFund.builder().funds(fundEntity.getFundDetails(fundListFromRedis))
            .stocks(stockEntity.getStockDetails(stockListFromRedis, "buxuesong")).build()).build();
    }

    @PostMapping(value = "/chrome/advice")
    public Response saveAdvice(@RequestParam(value = "adviceContent", required = false) String adviceContent) throws Exception {
        log.info("adviceContent: {}", adviceContent);
        return Response.builder().code("00000000").value(adviceEntity.saveAdvice(adviceContent)).build();
    }

    @GetMapping(value = "/chrome/advice")
    public Response getAdvice() throws Exception {
        List<AdvicePO> advicePOList = adviceEntity.getAdvice();
        log.info("getAdvice ");
        return Response.builder().code("00000000").value(advicePOList).build();
    }

}
