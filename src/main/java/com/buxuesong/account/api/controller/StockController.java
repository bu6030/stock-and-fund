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
    @PostMapping(value = "/saveStock")
    public Response saveStock(@RequestBody SaveStockRequest saveStockRequest) throws Exception {
        log.info("Save stock request: {}", saveStockRequest);
        if(stockService.saveStock(saveStockRequest)){
            return Response.builder().value(true).code("00000000").build();
        }
        return Response.builder().value(true).code("00000001").build();
    }

    /**
     * 获取账本列表接口
     *
     * @return
     */
    @PostMapping(value = "/deleteStock")
    public Response deleteStock(@RequestBody SaveStockRequest saveStockRequest) throws Exception {
        log.info("Delete stock request: {}", saveStockRequest);
        stockService.deleteStock(saveStockRequest);
        return Response.builder().value(true).code("00000000").build();
    }
}
