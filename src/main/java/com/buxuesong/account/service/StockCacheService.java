package com.buxuesong.account.service;

public interface StockCacheService {
    String getStockInfoFromApi(String param);

    String getStockInfoFromApiCache(String param);
}
