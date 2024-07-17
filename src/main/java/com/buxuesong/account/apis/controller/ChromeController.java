package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.request.AdviceRequest;
import com.buxuesong.account.apis.model.response.ChromeStockAndFund;
import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.domain.model.advice.AdviceEntity;
import com.buxuesong.account.domain.model.deposit.DepositEntity;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.domain.model.stock.StockEntity;
import com.buxuesong.account.infrastructure.persistent.po.AdvicePO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private DepositEntity depositEntity;

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
            .stocks(stockEntity.getStockDetails(stockListFromRedis, "buxuesong"))
            .dayIncomeHistorys(depositEntity.getDepositList(null, null, "buxuesong"))
            .build()).build();
    }

    @PostMapping(value = "/chrome/advice")
    public Response saveAdvice(@RequestBody AdviceRequest adviceRequest) throws Exception {
        log.info("saveAdvice: {}", adviceRequest);
        return Response.builder().code("00000000").value(adviceEntity.saveAdvice(adviceRequest)).build();
    }

    @GetMapping(value = "/chrome/advice")
    public Response getAdvice() throws Exception {
        List<AdvicePO> advicePOList = adviceEntity.getAdvice();
        log.info("GetAdvice ");
        return Response.builder().code("00000000").value(advicePOList).build();
    }

    @PutMapping(value = "/chrome/advice")
    public Response updateAdvice(@RequestBody AdviceRequest adviceRequest) throws Exception {
        log.info("updateAdvice: {}", adviceRequest);
        return Response.builder().code("00000000").value(adviceEntity.updateAdvice(adviceRequest)).build();
    }

    @DeleteMapping(value = "/chrome/advice")
    public Response deleteAdvice(@RequestBody AdviceRequest adviceRequest) throws Exception {
        log.info("deleteAdvice: {}", adviceRequest);
        return Response.builder().code("00000000").value(adviceEntity.deleteAdvice(adviceRequest)).build();
    }

    @GetMapping(value = "/chrome/stocks")
    public Response getStocks(@RequestParam(value = "stockArr", required = false) String stockArr) throws Exception {
        log.info("getStocks stockArr: {}", stockArr);
        List<String> stocks = Arrays.stream(stockArr.split(";")).toList();
        return Response.builder().code("00000000").value(stockEntity.getStockDetails(stocks, null)).build();
    }

    /**
     * 通过名称搜索股票接口
     *
     * @return
     */
    @GetMapping(value = "/chrome/stock/search")
    public Response searchStockByName(@RequestParam(value = "name", required = false) String name) throws Exception {
        return Response.builder().code("00000000").value(stockEntity.searchStockByName(name)).build();
    }

    /**
     * 通过名称搜索基金接口
     *
     * @return
     */
    @GetMapping(value = "/chrome/fund/search")
    public Response searchFundByName(@RequestParam(value = "name", required = false) String name) throws Exception {
        return Response.builder().code("00000000").value(fundEntity.searchFundByName(name)).build();
    }
}
