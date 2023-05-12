function autoRefresh () {
    var date = new Date();
    var isTradingTime = (date.toLocaleTimeString() >= "09:15:00" && date.toLocaleTimeString() <= "11:30:00")
        || (date.toLocaleTimeString() >= "13:00:00" && date.toLocaleTimeString() <= "15:00:00");
    if ($("#enableAutoRefresh").is(":checked") && isTradingTime) {
        initData();
        getLargeMarketData();
    }
}