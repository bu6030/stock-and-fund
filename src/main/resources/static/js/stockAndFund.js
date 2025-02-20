var pageSize = 15;
var filteredApp = "ALL";
var stockTotalIncome;
var fundTotalIncome;
var stockDayIncome;
var fundDayIncome;
var stockTotalmarketValue;
var fundTotalmarketValue;
var stockTotalCostValue;
var fundTotalCostValue;
var appList;
var stockList;
var fundList;
var totalMarketValue;

function getData() {
    var userId = $("#userId").val();
    var personName = $("#personName").val();
    var accountId = $("#accountId").val();
    $.ajax({
        url:"/param?type=APP",
        type:"get",
        data :{},
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            appList = data.value;
            initData();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
    lay('#version').html('-v'+ laydate.v);
    // 30s刷新
    setInterval('autoRefresh()', 30000);
}

function initData(){
    $.ajax({
        url:"/stock",
        type:"get",
        data :{
        },
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            stockList = data.value;
            initFund();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function initFund() {
    $.ajax({
        url:"/fund",
        type:"get",
        data :{
        },
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            fundList = data.value;
            initStockAndFundHtml();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function initStockAndFundHtml(){
    var marketValue = new BigDecimal("0");
    totalMarketValue = new BigDecimal("0");
    for(var k in stockList){
        if (filteredApp != "ALL" && stockList[k].app != filteredApp) {
            continue;
        }
        marketValue = (new BigDecimal(stockList[k].now)).multiply(new BigDecimal(stockList[k].bonds));
        totalMarketValue = totalMarketValue.add(marketValue);
    }

    for(var k in fundList){
        if (filteredApp != "ALL" && fundList[k].app != filteredApp) {
            continue;
        }
        marketValue = new BigDecimal(parseFloat((new BigDecimal(fundList[k].gsz)).multiply(new BigDecimal(fundList[k].bonds))).toFixed(2));
        totalMarketValue = totalMarketValue.add(marketValue);
    }

    var str = getStockTableHtml(stockList, totalMarketValue);
    $("#stock-nr").html(str);

    str = getFundTableHtml(fundList, totalMarketValue);
    $("#fund-nr").html(str);

    str = getTotalTableHtml(totalMarketValue);
    $("#total-nr").html(str);
}

function getStockTableHtml(result, totalMarketValueResult){
    var str = "";
    stockTotalIncome = new BigDecimal("0");
    stockDayIncome = new BigDecimal("0");
    stockTotalmarketValue = new BigDecimal("0");
    stockTotalCostValue = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    var marketValuePercent = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        if ($("#enableFilterHide").is(":checked") && result[k].hide == true) {
            continue;
        }
        var buyOrSells = result[k].buyOrSellStockRequestList;
        var todayBuyIncom = new BigDecimal("0");
        var todaySellIncom = new BigDecimal("0");
        var maxBuyOrSellBonds = 0;
        for(var l in buyOrSells) {
            // 当天购买过
            if(buyOrSells[l].type == "1") {
                maxBuyOrSellBonds = maxBuyOrSellBonds + buyOrSells[l].bonds;
                console.log("买入价格"+buyOrSells[l].price);
                console.log("当前价格"+result[k].now);
                var buyIncome = (new BigDecimal(result[k].now))
                    .subtract(new BigDecimal(buyOrSells[l].price+""))
                    .multiply(new BigDecimal(buyOrSells[l].bonds+""));
                todayBuyIncom = todayBuyIncom.add(buyIncome);
                console.log("买入收益："+todayBuyIncom);
            }
            // 当天卖出过
            if(buyOrSells[l].type == "2") {
                todaySellIncom = todaySellIncom.add(new BigDecimal(buyOrSells[l].income+""));
                console.log("卖出收益："+todaySellIncom);
            }
        }
        console.log("买卖最大数"+maxBuyOrSellBonds);
        if (maxBuyOrSellBonds < result[k].bonds) {
            var restBonds = (new BigDecimal(result[k].bonds)).subtract(new BigDecimal(maxBuyOrSellBonds+""));
            console.log("剩余股数："+restBonds);
            dayIncome = (new BigDecimal(result[k].change)).multiply(restBonds);
        } else {
            dayIncome = new BigDecimal("0");
        }
        console.log(result[k].name+"计算当日买卖前："+ dayIncome);
        console.log(result[k].name+"计算："+ dayIncome.add(todayBuyIncom).add(todaySellIncom));
        dayIncome = dayIncome.add(todayBuyIncom).add(todaySellIncom).setScale(2);
        console.log(result[k].name+"计算当日买卖后："+ dayIncome);
        marketValue = (new BigDecimal(result[k].now)).multiply(new BigDecimal(result[k].bonds)).setScale(2);
        if (totalMarketValueResult.compareTo(new BigDecimal("0")) != 0) {
            marketValuePercent = marketValue.multiply(new BigDecimal("100")).divide(totalMarketValueResult);
        }
        var dayIncomeStyle = dayIncome == 0 ? "" : (dayIncome >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalIncomeStyle = result[k].income == 0 ? "" : (result[k].income >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneYearAgoUpperStyle = result[k].oneYearAgoUpper == 0 ? "" : (result[k].oneYearAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneSeasonAgoUpperStyle = result[k].oneSeasonAgoUpper == 0 ? "" : (result[k].oneSeasonAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneMonthAgoUpperStyle = result[k].oneMonthAgoUpper == 0 ? "" : (result[k].oneMonthAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneWeekAgoUpperStyle = result[k].oneWeekAgoUpper == 0 ? "" : (result[k].oneWeekAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var day50Max = new BigDecimal(result[k].day50Max + "");
        var day50Min = new BigDecimal(result[k].day50Min + "");
        var day20Max = new BigDecimal(result[k].day20Max + "");
        var day20Min = new BigDecimal(result[k].day20Min + "");
        var day10Max = new BigDecimal(result[k].day10Max + "");
        var day10Min = new BigDecimal(result[k].day10Min + "");
        var now = new BigDecimal(result[k].now + "");
        var donchianChennel = "";
        var donchianChennelStyle = "";
        if (now.compareTo(day50Max) > 0) {
            donchianChennel += "破50高(" + day50Max + ")；";
            donchianChennelStyle = "style=\"color:#c12e2a\"";
        } else if(now.compareTo(day50Min) < 0) {
            donchianChennel += "破50低(" + day50Min + ")；";
            donchianChennelStyle = "style=\"color:#3e8f3e\"";
        }
        if (now.compareTo(day20Max) > 0) {
            donchianChennel += "破20高(" + day20Max + ")；";
            donchianChennelStyle = "style=\"color:#c12e2a\"";
        } else if(now.compareTo(day20Min) < 0) {
            donchianChennel += "破20低(" + day20Min + ")；";
            donchianChennelStyle = "style=\"color:#3e8f3e\"";
        }
        if (now.compareTo(day10Max) > 0) {
            donchianChennel += "破10高(" + day10Max + ")；";
            donchianChennelStyle = "style=\"color:#c12e2a\"";
        } else if(now.compareTo(day10Min) < 0) {
            donchianChennel += "破10低(" + day10Min + ")；";
            donchianChennelStyle = "style=\"color:#3e8f3e\"";
        }
        if (donchianChennel == "") {
            donchianChennel += "监控中；";
            donchianChennelStyle = "";
        }
        // 计算股票总成本
        var costPrice = new BigDecimal(result[k].costPrise+"");
        var costPriceValue = new BigDecimal(parseFloat(costPrice.multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        stockTotalCostValue = stockTotalCostValue.add(costPriceValue);
        str += "<tr><td class='no-wrap'>"
            + "<a href='#' onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"分时图\" onclick=\"showTimeImageModal('" + result[k].code + "','STOCK')\"><span class=\"am-icon-clock-o\"></span></button>"
            + "</td><td class='no-wrap' onclick=\"getStockHistory('" + result[k].code + "')\">" +result[k].name
            + "</td><td " + dayIncomeStyle + ">" + result[k].change
            + "</td><td " + dayIncomeStyle + ">" + dayIncome
            + "</td><td " + dayIncomeStyle + ">" + result[k].changePercent +"%"
            + "</td><td>" +result[k].max
            + "</td><td>" + result[k].min
            + "</td><td " + donchianChennelStyle + " onclick=\"showDonchianChennel('" + result[k].name + "','" + day50Max + "','" + day50Min + "','" + day20Max + "','" + day20Min + "','" + day10Max + "','" + day10Min + "')\">" + donchianChennel
            + "</td><td " + oneYearAgoUpperStyle + ">" + result[k].oneYearAgoUpper + "%"
            + "</td><td " + oneSeasonAgoUpperStyle + ">" + result[k].oneSeasonAgoUpper + "%"
            + "</td><td " + oneMonthAgoUpperStyle + ">" + result[k].oneMonthAgoUpper + "%"
            + "</td><td " + oneWeekAgoUpperStyle + ">" + result[k].oneWeekAgoUpper + "%"
            + "</td><td>" + result[k].now
            + "</td><td>" + result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + marketValue
            + "</td><td>" + marketValuePercent + "%"
            + "</td><td>" + costPriceValue
            + "</td><td " + totalIncomeStyle + ">" + result[k].incomePercent +"%"
            + "</td><td " + totalIncomeStyle + ">" + result[k].income
            +"</td></tr>";
        stockTotalIncome = stockTotalIncome.add(new BigDecimal(result[k].income));
        stockDayIncome = stockDayIncome.add(dayIncome);
        stockTotalmarketValue = stockTotalmarketValue.add(marketValue);
    }
    var stockDayIncomePercent = new BigDecimal("0");
    var stockTotalIncomePercent = new BigDecimal("0");
    if (stockTotalmarketValue != 0) {
        stockDayIncomePercent = stockDayIncome.multiply(new BigDecimal("100")).divide(stockTotalmarketValue.subtract(stockDayIncome));
        stockTotalIncomePercent = stockTotalIncome.multiply(new BigDecimal("100")).divide(stockTotalCostValue);
    }
    var stockDayIncomePercentStyle = stockDayIncome == 0 ? "" : (stockDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    var stockTotalIncomePercentStyle = stockTotalIncome == 0 ? "" : (stockTotalIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    str += "<tr><td>合计</td><td colspan='3'></td><td " + stockDayIncomePercentStyle + ">" + stockDayIncome + "</td><td " + stockDayIncomePercentStyle + ">" + stockDayIncomePercent + "%</td><td colspan='10'></td><td colspan='2'>" + stockTotalmarketValue + "</td><td>"+stockTotalCostValue.setScale(2)+"</td><td " + stockTotalIncomePercentStyle + ">" + stockTotalIncomePercent + "%</td><td " + stockTotalIncomePercentStyle + ">" + stockTotalIncome
        +"</td></tr>";
    return str;
}

function getFundTableHtml(result, totalMarketValueResult){
    var str = "";
    fundTotalIncome = new BigDecimal("0");
    fundDayIncome = new BigDecimal("0");
    fundTotalmarketValue = new BigDecimal("0");
    fundTotalCostValue = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    var marketValuePercent = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        // 计算基金总成本
        var costPrice = new BigDecimal(result[k].costPrise+"");
        var costPriceValue = new BigDecimal(parseFloat(costPrice.multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        fundTotalCostValue = fundTotalCostValue.add(costPriceValue);
        if (result[k].currentDayJingzhi != null && result[k].currentDayJingzhi != '') {
            result[k].gsz = result[k].currentDayJingzhi + '(实)';
            dayIncome = new BigDecimal(parseFloat(((new BigDecimal(result[k].currentDayJingzhi + "")).subtract(new BigDecimal(result[k].previousDayJingzhi + ""))).multiply(new BigDecimal(result[k].bonds + ""))).toFixed(2));
            marketValue = new BigDecimal(parseFloat((new BigDecimal(result[k].currentDayJingzhi + "")).multiply(new BigDecimal(result[k].bonds + ""))).toFixed(2));
            result[k].income = marketValue.subtract(costPriceValue) + "";
            result[k].incomePercent = marketValue.subtract(costPriceValue).multiply(new BigDecimal("100")).divide(costPriceValue) + "";
        } else {
            dayIncome = new BigDecimal(parseFloat((new BigDecimal(result[k].gszzl)).multiply((new BigDecimal(result[k].dwjz))).multiply(new BigDecimal(result[k].bonds)).divide(new BigDecimal("100"))).toFixed(2));
            marketValue = new BigDecimal(parseFloat((new BigDecimal(result[k].gsz)).multiply(new BigDecimal(result[k].bonds))).toFixed(2));
            result[k].gsz = result[k].gsz + '(估)';
        }
        if (totalMarketValueResult.compareTo(new BigDecimal("0")) != 0) {
            marketValuePercent = marketValue.multiply(new BigDecimal("100")).divide(totalMarketValueResult);
        }
        var dayIncomeStyle = dayIncome == 0 ? "" : (dayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalIncomeStyle = result[k].income == 0 ? "" : (result[k].income > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneYearAgoUpperStyle = result[k].oneYearAgoUpper == 0 ? "" : (result[k].oneYearAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneSeasonAgoUpperStyle = result[k].oneSeasonAgoUpper == 0 ? "" : (result[k].oneSeasonAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneMonthAgoUpperStyle = result[k].oneMonthAgoUpper == 0 ? "" : (result[k].oneMonthAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var oneWeekAgoUpperStyle = result[k].oneWeekAgoUpper == 0 ? "" : (result[k].oneWeekAgoUpper >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

        str += "<tr><td class='no-wrap'>"
            + "<a href='#' onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"分时图\" onclick=\"showTimeImageModal('" + result[k].fundCode + "','FUND')\"><span class=\"am-icon-clock-o\"></span></button>"
            + "</td><td class='no-wrap' colspan=\"2\" onclick=\"getFundHistory('" + result[k].fundCode + "')\">" +result[k].fundName
            + "</td><td " + dayIncomeStyle + ">" + dayIncome
            + "</td><td " + dayIncomeStyle + " colspan='2'>" +result[k].gszzl + "%"
            + "</td><td colspan='2'>" + result[k].dwjz + "(" + result[k].jzrq + ")"
            + "</td><td " + oneYearAgoUpperStyle + ">" + result[k].oneYearAgoUpper + "%"
            + "</td><td " + oneSeasonAgoUpperStyle + ">" + result[k].oneSeasonAgoUpper + "%"
            + "</td><td " + oneMonthAgoUpperStyle + ">" + result[k].oneMonthAgoUpper + "%"
            + "</td><td " + oneWeekAgoUpperStyle + ">" + result[k].oneWeekAgoUpper + "%"
            + "</td><td>" + result[k].gsz
            + "</td><td>" +result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + marketValue
            + "</td><td>" + marketValuePercent + "%"
            + "</td><td>" + costPriceValue
            + "</td><td " + totalIncomeStyle + ">" + result[k].incomePercent + "%"
            + "</td><td " + totalIncomeStyle + ">" + result[k].income
            +"</td></tr>";
        fundTotalIncome = fundTotalIncome.add(new BigDecimal(result[k].income));
        fundDayIncome = fundDayIncome.add(dayIncome);
        fundTotalmarketValue = fundTotalmarketValue.add(marketValue);
    }
    var fundDayIncomePercent = new BigDecimal("0");
    var fundTotalIncomePercent = new BigDecimal("0");
    if (fundTotalmarketValue != 0) {
        fundDayIncomePercent = fundDayIncome.multiply(new BigDecimal("100")).divide(fundTotalmarketValue.subtract(fundDayIncome));
        fundTotalIncomePercent = fundTotalIncome.multiply(new BigDecimal("100")).divide(fundTotalCostValue);
    }
    var fundDayIncomePercentStyle = fundDayIncome == 0 ? "" : (fundDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    var fundTotalIncomePercentStyle = fundTotalIncome == 0 ? "" : (fundTotalIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    str += "<tr><td>合计</td><td colspan='3'></td><td " + fundDayIncomePercentStyle + ">" + fundDayIncome + "</td><td colspan='2' " + fundDayIncomePercentStyle + ">" + fundDayIncomePercent + "%</td><td colspan='9'></td><td colspan='2'>" + fundTotalmarketValue + "</td><td>"+fundTotalCostValue+"</td><td " + fundTotalIncomePercentStyle + ">" + fundTotalIncomePercent + "%</td><td " + fundTotalIncomePercentStyle + ">" + fundTotalIncome
        +"</td></tr>";


    return str;
}

function getTotalTableHtml(totalMarketValueResult) {
    var str = "";
    var allDayIncome = fundDayIncome.add(stockDayIncome);
    var allTotalIncome = fundTotalIncome.add(stockTotalIncome);
    var allDayIncomePercent = new BigDecimal("0");
    var allTotalIncomePercent = new BigDecimal("0");
    var totalCostValue = fundTotalCostValue.add(stockTotalCostValue);
    if (totalMarketValueResult != 0) {
        allDayIncomePercent = allDayIncome.multiply(new BigDecimal("100")).divide(totalMarketValueResult.subtract(allDayIncome));
        allTotalIncomePercent = allTotalIncome.multiply(new BigDecimal("100")).divide(totalCostValue);
    }
    var allDayIncomePercentStyle = allDayIncome == 0 ? "" : (allDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    var allTotalIncomePercentStyle = allTotalIncome == 0 ? "" : (allTotalIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

    str += "<tr><td class='no-wrap'>股票基金汇总合计</td><td colspan='3'></td><td " + allDayIncomePercentStyle + ">" + allDayIncome + "</td><td colspan='2' " + allDayIncomePercentStyle + ">" + allDayIncomePercent + "%</td><td colspan='9'></td><td colspan='2'>" + totalMarketValueResult.setScale(2) + "</td><td>"+totalCostValue+"</td><td " + allTotalIncomePercentStyle + ">" + allTotalIncomePercent + "%</td><td " + allTotalIncomePercentStyle + ">" + allTotalIncome
        +"</td></tr>";
    return str;
}

function filterApp(app) {
    filteredApp = app;
    getData();
}

function enableFilterHideChanged() {
    initData();
}

//展示分时图
function showTimeImageModal(code, type) {
    console.log("分时图", code, "==", type);
    $("#time-image-code").val(code);
    $("#time-image-type").val(type);
    let path = "";
    if (type == "FUND") {
        path = "http://j4.dfcfw.com/charts/pic7/" + code + ".png";
    } else {
        path = "http://image.sinajs.cn/newchart/min/n/" + code + ".gif";
    }
    $("#time-image-modal").modal();
    $("#time-image").html('<img src="' + path + '" width="100%" length="100%" />');
}

//展示分时图
function showTimeImage() {
    let code = $("#time-image-code").val();
    let type = $("#time-image-type").val();
    console.log("分时图", code, "==", type);
    showTimeImageModal(code, type);
}

function showDayImage() {
    let code = $("#time-image-code").val();
    let type = $("#time-image-type").val();
    console.log("日线图", code, "==", type);
    let path = "";
    if (type == "FUND") {
        let timestamp = (Date.now() + "").substring(0, 10);
        path = "http://webquoteklinepic.eastmoney.com/GetPic.aspx?nid=0." + code + "&type=&unitWidth=-6&ef=&formula=RSI&AT=1&imageType=KXL&timespan=" + timestamp;
    } else {
        path = "http://image.sinajs.cn/newchart/daily/n/" + code + ".gif";
    }
    $("#time-image-modal").modal();
    $("#time-image").html('<img src="' + path + '" width="100%" length="100%" />');
}

function showWeekImage() {
    let code = $("#time-image-code").val();
    let type = $("#time-image-type").val();
    console.log("周线图", code, "==", type);
    let path = "";
    if (type == "FUND") {
        let timestamp = (Date.now() + "").substring(0, 10);
        path = "http://webquoteklinepic.eastmoney.com/GetPic.aspx?nid=0." + code + "&type=W&unitWidth=-6&ef=&formula=RSI&AT=1&imageType=KXL&timespan=" + timestamp;
    } else {
        path = "http://image.sinajs.cn/newchart/weekly/n/" + code + ".gif";
    }
    // $("#time-image-modal").modal();
    $("#time-image").html('<img src="' + path + '" width="100%" length="100%" />');
}

function showMonthImage() {
    let code = $("#time-image-code").val();
    let type = $("#time-image-type").val();
    console.log("月线图", code, "==", type);
    let path = "";
    if (type == "FUND") {
        let timestamp = (Date.now() + "").substring(0, 10);
        path = "http://webquoteklinepic.eastmoney.com/GetPic.aspx?nid=0." + code + "&type=M&unitWidth=-6&ef=&formula=RSI&AT=1&imageType=KXL&timespan=" + timestamp;
    } else {
        path = "http://image.sinajs.cn/newchart/monthly/n/" + code + ".gif";
    }
    // $("#time-image-modal").modal();
    $("#time-image").html('<img src="' + path + '" width="100%" length="100%" />');
}

function largeMarketClick(timeImageCode) {
    $("#time-image-code").val(timeImageCode);
    $("#time-image-type").val("STOCK");
    let path = "http://image.sinajs.cn/newchart/min/n/" + timeImageCode + ".gif";
    $("#time-image-day-button")[0].style.display  = 'block';
    $("#time-image-week-button")[0].style.display  = 'block';
    $("#time-image-month-button")[0].style.display  = 'block';
    $("#time-image-modal").modal();
    $("#time-image").html('<img src="'+path+'" width="100%" length="100%" />');
}

function getFundHistory(code) {
    $("#show-buy-or-sell-button")[0].style.display  = 'none';
    $.ajax({
        url:"/fundHis?code=" + code,
        type:"get",
        data :{},
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            var str = "";
            for(var k in result) {
                var costPrise = new BigDecimal(result[k].costPrise + "");
                var costPriseChange = new BigDecimal(result[k].costPriseChange + "");
                var newCostPrise = costPrise.add(costPriseChange);
                let costPriseChangeStyle = "";
                if (costPriseChange > (new BigDecimal("0"))) {
                    costPriseChange = "(+" + costPriseChange + ")";
                    costPriseChangeStyle = "style=\"color:#c12e2a\"";
                } else if (costPriseChange < (new BigDecimal("0"))) {
                    costPriseChange = "(" + costPriseChange + ")";
                    costPriseChangeStyle = "style=\"color:#3e8f3e\"";
                } else {
                    costPriseChange = "(不变)";
                }
                var bonds = new BigDecimal(result[k].bonds + "");
                var bondsChange = new BigDecimal(result[k].bondsChange + "");
                var newBonds = bonds.add(bondsChange);
                let bondsChangeStyle = "";
                if (bondsChange > (new BigDecimal("0"))) {
                    bondsChange = "(+" + bondsChange + ")";
                    bondsChangeStyle = "style=\"color:#c12e2a\"";
                } else if (bondsChange < (new BigDecimal("0"))) {
                    bondsChange = "(" + bondsChange + ")";
                    bondsChangeStyle = "style=\"color:#3e8f3e\"";
                } else {
                    bondsChange = "(不变)";
                }
                var marketValue = parseFloat(costPrise.multiply(bonds)).toFixed(2);
                str += "<tr class='my-history-tr'><td>" + (parseInt(k) + 1)
                    + "</td><td>" + result[k].name
                    + "</td><td>" + costPrise
                    + "</td><td "+ costPriseChangeStyle +">" + newCostPrise + costPriseChange
                    + "</td><td>" + bonds
                    + "</td><td "+ bondsChangeStyle +">" + newBonds + bondsChange
                    + "</td><td>" + marketValue
                    + "</td><td>" + result[k].createDate
                    +"</td></tr>";
            }
            $("#history-nr").html(str);
            $("#history-modal").modal();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function getStockHistory(code) {
    $("#show-buy-or-sell-button")[0].style.display  = 'block';
    $("#buy-or-sell-stock-code").val(code);
    $.ajax({
        url:"/stockHis?code=" + code,
        type:"get",
        data :{},
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            var str = "";
            for(var k in result) {
                $("#buy-or-sell-stock-name").val(result[k].name);
                var costPrise = new BigDecimal(result[k].costPrise + "");
                var costPriseChange = new BigDecimal(result[k].costPriseChange + "");
                var newCostPrise = costPrise.add(costPriseChange);
                let costPriseChangeStyle = "";
                if (costPriseChange > (new BigDecimal("0"))) {
                    costPriseChange = "(+" + costPriseChange + ")";
                    costPriseChangeStyle = "style=\"color:#c12e2a\"";
                } else if (costPriseChange < (new BigDecimal("0"))) {
                    costPriseChange = "(" + costPriseChange + ")";
                    costPriseChangeStyle = "style=\"color:#3e8f3e\"";
                } else {
                    costPriseChange = "(不变)";
                }
                var bonds = new BigDecimal(result[k].bonds + "");
                var bondsChange = new BigDecimal(result[k].bondsChange + "");
                var newBonds = bonds.add(bondsChange);
                let bondsChangeStyle = "";
                if (bondsChange > (new BigDecimal("0"))) {
                    bondsChange = "(+" + bondsChange + ")";
                    bondsChangeStyle = "style=\"color:#c12e2a\"";
                } else if (bondsChange < (new BigDecimal("0"))) {
                    bondsChange = "(" + bondsChange + ")";
                    bondsChangeStyle = "style=\"color:#3e8f3e\"";
                } else {
                    bondsChange = "(不变)";
                }
                var marketValue = parseFloat(costPrise.multiply(bonds)).toFixed(2);
                str += "<tr class='my-history-tr'><td>" + (parseInt(k) + 1)
                    + "</td><td>" + result[k].name
                    + "</td><td>" + costPrise
                    + "</td><td " + costPriseChangeStyle+ ">" + newCostPrise + costPriseChange
                    + "</td><td>" + bonds
                    + "</td><td "+ bondsChangeStyle +">" + newBonds + bondsChange
                    + "</td><td>" + marketValue
                    + "</td><td>" + result[k].createDate
                    +"</td></tr>";
            }
            $("#history-nr").html(str);
            $("#history-modal").modal();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function showBuyOrSell() {
    $("#history-modal").modal('hide');
    let code = $("#buy-or-sell-stock-code").val();
    let name = $("#buy-or-sell-stock-name").val();
    $("#buy-or-sell-stock-code").val(code);
    $.ajax({
        url:"/buyOrSellStock?code=" + code,
        type:"get",
        data :{},
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data) {
            var result = data.value;
            var str = "";
            for(var k in result) {
                let type = '';
                let style = '';
                if (result[k].type == "1") {
                    type = "买";
                    style = "class=\"my-buy-tr\"";
                } else {
                    type = "卖";
                    style = "class=\"my-sell-tr\"";
                }
                var cost = new BigDecimal(result[k].cost + "");
                var bonds = new BigDecimal(result[k].bonds + "");
                var price = new BigDecimal(result[k].price + "");
                var totalPrice = parseFloat(price.multiply(bonds).subtract(cost)).toFixed(2);
                str += "<tr " + style + "><td>" + name
                    + "</td><td>" + type
                    + "</td><td>" + price
                    + "</td><td>" + cost
                    + "</td><td>" + bonds
                    + "</td><td>" + totalPrice
                    + "</td><td>" + result[k].date
                    +"</td></tr>";
            }
            $("#buy-or-sell-nr").html(str);

            $("#buy-or-sell-modal").modal();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function showDonchianChennel(name, day50Max, day50Min, day20Max, day20Min, day10Max, day10Min) {
    alert("股票：" + name + "；50最高：" + day50Max + "；50最低：" + day50Min + "；20最高：" + day20Max + "；20最低：" + day20Min + "；10最高：" + day10Max + "；10最低：" + day10Min + "");
}