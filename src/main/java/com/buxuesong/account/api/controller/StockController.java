package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.SaveStockRequest;
import com.buxuesong.account.model.res.Response;
import com.buxuesong.account.service.StockService;
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
     * 获取账本列表接口
     *
     * @return
     */
    @GetMapping(value = "/stock")
    public Response getStockList(HttpServletRequest request) throws Exception {
        List<String> stockListFromRedis = stockService.getStockList();
        return Response.builder().code("00000000").value(stockService.getStockDetails(stockListFromRedis)).build();
    }

    /**
     * 获取账本列表接口
     *
     * @return
     */
    @GetMapping(value = "/stock/table")
    public Response getRedisStock(HttpServletRequest request) throws Exception {
        return Response.builder().code("00000000").value(stockService.getStock()).build();
    }

    /**
     * 获取账本列表接口
     *
     * @return
     */
    @PostMapping(value = "/stock")
    public Response saveStock(HttpServletRequest request, @RequestBody SaveStockRequest saveStockRequest) throws Exception {
        log.info("Save stock request: {}", request);
        stockService.saveStock(saveStockRequest.getStock());
        return Response.builder().value(true).code("00000000").build();
    }
}
