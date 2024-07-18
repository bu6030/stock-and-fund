package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.apis.model.request.BuyOrSellStockRequest;
import com.buxuesong.account.apis.model.request.StockRequest;
import com.buxuesong.account.domain.model.stock.StockEntity;
import com.buxuesong.account.infrastructure.persistent.po.BuyOrSellStockPO;
import com.buxuesong.account.infrastructure.persistent.po.StockHisPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class StockController {
    @Autowired
    private StockEntity stockEntity;

    /**
     * 获取股票信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/stock")
    public Response getStockList(@RequestParam(value = "app", required = false) String app,
        @RequestParam(value = "code", required = false) String code) throws Exception {
        List<String> stockList = stockEntity.getStockList(app);
        if (code != null && !"".equals(code)) {
            stockList = stockList.stream().filter(s -> s.contains(code)).collect(Collectors.toList());
        }
        return Response.builder().code("00000000").value(stockEntity.getStockDetails(stockList)).build();
    }

    /**
     * 通过名称搜索股票接口
     *
     * @return
     */
    @GetMapping(value = "/stock/search")
    public Response searchStockByName(@RequestParam(value = "name", required = false) String name) throws Exception {
        return Response.builder().code("00000000").value(stockEntity.searchStockByName(name)).build();
    }

    /**
     * 获取大盘指数，上证，深成指，创业板指
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/stockLargeMarket")
    public Response getStockLargeMarketList() throws Exception {
        List<String> stockList = new ArrayList<>();
        stockList.add("sh000001,0,0,,0");
        stockList.add("sz399001,0,0,,0");
        stockList.add("sz399006,0,0,,0");
        return Response.builder().code("00000000").value(stockEntity.getStockDetails(stockList)).build();
    }

    /**
     * 获取 Stock His 历史数据
     *
     * @param app
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/stockHis")
    public Response getStockHisList(@RequestParam(value = "app", required = false) String app,
        @RequestParam(value = "code", required = false) String code,
        @RequestParam(value = "beginDate", required = false) String beginDate,
        @RequestParam(value = "endDate", required = false) String endDate) throws Exception {
        List<StockHisPO> stockHisList = stockEntity.getStockHisList(app, code, beginDate, endDate);
        return Response.builder().code("00000000").value(stockHisList).build();
    }

    /**
     * 买卖股票历史接口
     *
     * @return
     */
    @GetMapping(value = "/buyOrSellStock")
    public Response buyOrSellStock(@RequestParam(value = "code", required = false) String code,
        @RequestParam(value = "beginDate", required = false) String beginDate,
        @RequestParam(value = "endDate", required = false) String endDate) throws Exception {
        Thread.sleep(200);
        List<BuyOrSellStockPO> buyOrSellStockPOS = stockEntity.getBuyOrSellStocks(code, beginDate, endDate);
        return Response.builder().value(buyOrSellStockPOS).code("00000000").build();
    }

    /**
     * 保存/修改股票接口
     *
     * @return
     */
    @PostMapping(value = "/saveStock")
    public Response saveStock(@RequestBody StockRequest stockRequest) throws Exception {
        log.info("Save stock request: {}", stockRequest);
        if (stockEntity.saveStock(stockRequest)) {
            return Response.builder().value(true).code("00000000").build();
        }
        return Response.builder().value(true).code("00000001").build();
    }

    /**
     * 删除股票接口
     *
     * @return
     */
    @PostMapping(value = "/deleteStock")
    public Response deleteStock(@RequestBody StockRequest stockRequest) throws Exception {
        log.info("Delete stock request: {}", stockRequest);
        stockEntity.deleteStock(stockRequest);
        return Response.builder().value(true).code("00000000").build();
    }

    /**
     * 买卖股票接口
     *
     * @return
     */
    @PostMapping(value = "/buyOrSellStock")
    public Response buyOrSellStock(@RequestBody BuyOrSellStockRequest buyOrSellStockRequest) throws Exception {
        log.info("Buy or sell stock request: {}", buyOrSellStockRequest);
        stockEntity.buyOrSellStock(buyOrSellStockRequest);
        return Response.builder().value(true).code("00000000").build();
    }

    /**
     * 通过唐安奇通道法计算股票
     *
     * @return
     */
    @GetMapping(value = "/stock/compute")
    public Response computeStock(@RequestParam(value = "code", required = false) String code,
        @RequestParam(value = "dataLen", required = false) String dataLen) throws Exception {
        return Response.builder().code("00000000").value(stockEntity.computeStock(code, dataLen)).build();
    }

}
