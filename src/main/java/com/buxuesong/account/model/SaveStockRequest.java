package com.buxuesong.account.model;

public class SaveStockRequest {
    private String stock;

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "SaveStockRequest{" +
            "stock='" + stock + '\'' +
            '}';
    }
}
