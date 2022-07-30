package com.buxuesong.account.service;

import com.buxuesong.account.model.StockBean;

import java.util.List;

public interface StockService {
    List<StockBean> getStockDetails(List<String> codes);

    void saveStock(String stock);

    List<String> getStockList();

    String getStock();
}
