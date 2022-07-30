package com.buxuesong.account.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class UrlController {

    /**
     * PC网页端类别页初始化
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
     * PC网页端类别页初始化
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
     * PC网页端类别页初始化
     *
     * @return
     */
    @GetMapping(value = "/main")
    public ModelAndView stockAndFundInit(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView m = new ModelAndView();
        m.setViewName("stockAndFund");
        return m;
    }

}
