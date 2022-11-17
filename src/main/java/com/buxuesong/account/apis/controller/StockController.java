package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.apis.model.request.BuyOrSellStockRequest;
import com.buxuesong.account.apis.model.request.StockRequest;
import com.buxuesong.account.domain.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class StockController {
    @Autowired
    private StockService stockService;

    /**
     * 获取股票信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/stock")
    public Response getStockList(HttpServletRequest request, @RequestParam(value = "app", required = false) String app) throws Exception {
        List<String> stockListFromRedis = stockService.getStockList(app);
        return Response.builder().code("00000000").value(stockService.getStockDetails(stockListFromRedis)).build();
    }

    /**
     * 保存/修改股票接口
     *
     * @return
     */
    @PostMapping(value = "/saveStock")
    public Response saveStock(@RequestBody StockRequest stockRequest) throws Exception {
        log.info("Save stock request: {}", stockRequest);
        if (stockService.saveStock(stockRequest)) {
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
        stockService.deleteStock(stockRequest);
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
        stockService.buyOrSellStock(buyOrSellStockRequest);
        return Response.builder().value(true).code("00000000").build();
    }

}
