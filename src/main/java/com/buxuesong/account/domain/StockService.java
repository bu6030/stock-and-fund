package com.buxuesong.account.domain;

import com.buxuesong.account.apis.model.request.BuyOrSellStockRequest;
import com.buxuesong.account.apis.model.request.StockRequest;
import com.buxuesong.account.domain.model.stock.StockEntity;

import java.util.List;

public interface StockService {
    List<StockEntity> getStockDetails(List<String> codes);

    boolean saveStock(StockRequest stock);

    void deleteStock(StockRequest stockRequest);

    List<String> getStockList(String app);

    StockRequest findStockByCode(String code);

    void buyOrSellStock(BuyOrSellStockRequest buyOrSellStockRequest);

}
