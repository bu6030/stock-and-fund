package com.buxuesong.account.api.controller;

import com.buxuesong.account.model.SaveFundRequest;
import com.buxuesong.account.model.SaveStockRequest;
import com.buxuesong.account.service.FundService;
import com.buxuesong.account.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class UrlController {
    @Autowired
    private StockService stockService;
    @Autowired
    private FundService fundService;

    /**
     * 基金页面初始化
     *
     * @return
     */
    @GetMapping(value = "/fundInit")
    public ModelAndView fundInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("fund");
        return m;
    }

    /**
     * 股票页面初始化
     *
     * @return
     */
    @GetMapping(value = "/stockInit")
    public ModelAndView stockInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("stock");
        return m;
    }

    /**
     * 股票基金汇总页面初始化
     *
     * @return
     */
    @GetMapping(value = { "/main", "/" })
    public ModelAndView stockAndFundInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("stockAndFund");
        return m;
    }

    /**
     * 添加股票基金页面初始化
     *
     * @return
     */
    @GetMapping(value = "/addStockAndFundInit")
    public ModelAndView addStockAndFundInit(@RequestParam String type, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("addStockAndFund");
        m.getModel().put("type", type);
        return m;
    }

    /**
     * 修改股票基金页面初始化
     *
     * @return
     */
    @GetMapping(value = "/updateStockAndFundInit")
    public ModelAndView updateStockAndFundInit(@RequestParam String type, @RequestParam String code, HttpServletRequest request,
        HttpServletResponse response) {
        log.info("修改股票基金初始化 type : {}, code : {}", type, code);
        ModelAndView m = new ModelAndView();
        m.setViewName("updateStockAndFund");
        m.getModel().put("type", type);
        if (type.equals("fund")) {
            SaveFundRequest saveFundRequest = fundService.findFundByCode(code);
            log.info("修改股票基金初始化 saveFundRequest : {}", saveFundRequest);
            m.getModel().put("code", saveFundRequest.getCode());
            m.getModel().put("costPrise", saveFundRequest.getCostPrise());
            m.getModel().put("bonds", saveFundRequest.getBonds());
            m.getModel().put("app", saveFundRequest.getApp());
        } else {
            SaveStockRequest saveStockRequest = stockService.findStockByCode(code);
            log.info("修改股票基金初始化 saveStockRequest : {}", saveStockRequest);
            m.getModel().put("code", saveStockRequest.getCode());
            m.getModel().put("costPrise", saveStockRequest.getCostPrise());
            m.getModel().put("bonds", saveStockRequest.getBonds());
            m.getModel().put("app", saveStockRequest.getApp());
        }
        return m;
    }
}
