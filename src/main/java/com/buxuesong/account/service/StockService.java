package com.buxuesong.account.service;

import com.buxuesong.account.model.SaveFundRequest;
import com.buxuesong.account.model.SaveStockRequest;
import com.buxuesong.account.model.StockBean;

import java.util.List;

public interface StockService {
    List<StockBean> getStockDetails(List<String> codes);

    boolean saveStock(SaveStockRequest stock);

    void deleteStock(SaveStockRequest saveStockRequest);

    List<String> getStockList();

    SaveStockRequest findStockByCode(String code);
}
