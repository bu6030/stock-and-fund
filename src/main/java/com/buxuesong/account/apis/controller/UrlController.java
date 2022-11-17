package com.buxuesong.account.apis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class UrlController {

    // 老版本页面，BootStrap版本

    /**
     * 基金页面初始化
     *
     * @return
     */
    @GetMapping(value = "/fund.html")
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
    @GetMapping(value = "/stock.html")
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
    @GetMapping(value = { "/main.html", "/" })
    public ModelAndView stockAndFundInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("stockAndFund");
        return m;
    }

    /**
     * 收益汇总页面初始化
     *
     * @return
     */
    @GetMapping(value = "/deposit.html")
    public ModelAndView depositInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("deposit");
        return m;
    }

    /**
     * 字典数据页面初始化
     *
     * @return
     */
    @GetMapping(value = "/param.html")
    public ModelAndView paramInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("param");
        return m;
    }

    // 新版本页面，layui版本
    /**
     * 新基金页面初始化，引入layui
     *
     * @return
     */
    @GetMapping(value = "/fundNew.html")
    public ModelAndView fundNewInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("fundNew");
        return m;
    }

    /**
     * 新股票页面初始化，引入layui
     *
     * @return
     */
    @GetMapping(value = "/stockNew.html")
    public ModelAndView stockNewInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("stockNew");
        return m;
    }

    /**
     * 新股票基金汇总页面初始化，引入layui
     *
     * @return
     */
    @GetMapping(value = "/stockAndFundNew.html")
    public ModelAndView stockAndFundNewInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("stockAndFundNew");
        return m;
    }

    /**
     * 新收益汇总页面初始化，引入layui
     *
     * @return
     */
    @GetMapping(value = "/depositNew.html")
    public ModelAndView depositNewInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("depositNew");
        return m;
    }

    /**
     * 字典数据页面初始化
     *
     * @return
     */
    @GetMapping(value = "/paramNew.html")
    public ModelAndView paramNewInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("paramNew");
        return m;
    }
}
