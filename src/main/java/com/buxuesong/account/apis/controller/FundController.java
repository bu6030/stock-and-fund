package com.buxuesong.account.apis.controller;

import com.buxuesong.account.apis.model.request.FundRequest;
import com.buxuesong.account.apis.model.response.Response;
import com.buxuesong.account.domain.model.fund.FundEntity;
import com.buxuesong.account.apis.model.response.StockAndFundBean;
import com.buxuesong.account.domain.model.stock.StockEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class FundController {

    @Autowired
    private FundEntity fundEntity;

    @Autowired
    private StockEntity stockEntity;

    /**
     * 获取基金信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/fund")
    public Response getFundList(HttpServletRequest request, @RequestParam(value = "app", required = false) String app) throws Exception {
        List<String> fundListFromRedis = fundEntity.getFundList(app);
        return Response.builder().code("00000000").value(fundEntity.getFundDetails(fundListFromRedis)).build();
    }

    /**
     * 保存/修改基金接口
     *
     * @return
     */
    @PostMapping(value = "/saveFund")
    public Response saveFund(@RequestBody FundRequest fundRequest) throws Exception {
        log.info("Save fund request: {}", fundRequest);
        if (fundEntity.saveFund(fundRequest)) {
            return Response.builder().value(true).code("00000000").build();
        }
        return Response.builder().value(true).code("00000001").build();
    }

    /**
     * 删除基金接口
     *
     * @return
     */
    @PostMapping(value = "/deleteFund")
    public Response deleteFund(@RequestBody FundRequest fundRequest) throws Exception {
        log.info("Delete fund request: {}", fundRequest);
        fundEntity.deleteFund(fundRequest);
        return Response.builder().value(true).code("00000000").build();
    }

    /**
     * 获取基金信息列表接口
     *
     * @return
     */
    @GetMapping(value = "/stockAndFund")
    public Response getStockAndFundList(HttpServletRequest request, @RequestParam(value = "app", required = false) String app)
        throws Exception {
        List<String> fundListFrom = fundEntity.getFundList(app);
        List<String> stcokListFrom = stockEntity.getStockList(app);
        List<FundEntity> funds = fundEntity.getFundDetails(fundListFrom);
        List<StockEntity> stocks = stockEntity.getStockDetails(stcokListFrom);
        List<StockAndFundBean> stockAndFundsFromFunds = funds.stream()
            .map(s -> StockAndFundBean.builder().type("FUND").code(s.getFundCode())
                .name(s.getFundName()).costPrise(s.getCostPrise()).bonds(s.getBonds())
                .app(s.getApp()).incomePercent(s.getIncomePercent()).income(s.getIncome())
                // 基金部分内容
                .jzrq(s.getJzrq()).dwjz(s.getDwjz()).gsz(s.getGsz())
                .gszzl(s.getGszzl()).gztime(s.getGztime())
                .build())
            .collect(Collectors.toList());
        List<StockAndFundBean> stockAndFundsFromStocks = stocks.stream()
            .map(s -> StockAndFundBean.builder().type("STOCK").code(s.getCode()).name(s.getName())
                .costPrise(s.getCostPrise()).bonds(s.getBonds()).app(s.getApp())
                .incomePercent(s.getIncomePercent()).income(s.getIncome())
                // 股票部分内容
                .now(s.getNow()).change(s.getChange()).changePercent(s.getChangePercent())
                .time(s.getTime()).max(s.getMax()).min(s.getMin())
                .buyOrSellStockRequestList(s.getBuyOrSellStockRequestList())
                .build())
            .collect(Collectors.toList());
        stockAndFundsFromStocks.addAll(stockAndFundsFromFunds);
        return Response.builder().code("00000000").value(stockAndFundsFromStocks).build();
    }
}
