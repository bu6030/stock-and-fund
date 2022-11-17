package com.buxuesong.account.domain;

public interface StockCacheService {
    String getStockInfoFromApi(String param);

    String getStockInfoFromApiCache(String param);
}
