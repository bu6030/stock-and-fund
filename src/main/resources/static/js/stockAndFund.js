var pageSize = 15;
var filteredApp = "ALL";
var stockTotalIncome;
var fundTotalIncome;
var stockDayIncome;
var fundDayIncome;
var stockTotalmarketValue;
var fundTotalmarketValue;

function getData() {
    var userId = $("#userId").val();
    var personName = $("#personName").val();
    var accountId = $("#accountId").val();
    initStock();
    lay('#version').html('-v'+ laydate.v);
    // 30s刷新
    setInterval(function () {
        var date = new Date();
        if ($("#enableAutoRefresh").is(":checked") && date.toLocaleTimeString() >= "09:15:00"
            && date.toLocaleTimeString() <= "15:00:00") {
            window.location.reload()
        }
    }, 30000)
}

function initStock(){
    $.ajax({
        url:"/stock",
        type:"get",
        data :{
        },
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            var str = getStockTableHtml(result);
            $("#stock-nr").html(str);
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
            var result = data.value;
            var str = getFundTableHtml(result);
            $("#fund-nr").html(str);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function getStockTableHtml(result){
    var str = "";
    stockTotalIncome = new BigDecimal("0");
    stockDayIncome = new BigDecimal("0");
    stockTotalmarketValue = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        dayIncome = (new BigDecimal(result[k].change)).multiply(new BigDecimal(result[k].bonds));
        marketValue = (new BigDecimal(result[k].now)).multiply(new BigDecimal(result[k].bonds));
        str += "<tr><td>"
            + "<a onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" + result[k].code
            + "</td><td>" +result[k].name
            + "</td><td>" + result[k].change
            + "</td><td>" + dayIncome
            + "</td><td>" + result[k].changePercent +"%"
            + "</td><td>" +result[k].max
            + "</td><td>" + result[k].min
            + "</td><td>" + result[k].now
            + "</td><td>" + result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + marketValue
            + "</td><td>" + result[k].incomePercent +"%"
            + "</td><td>" + result[k].income
            +"</td></tr>";
        stockTotalIncome = stockTotalIncome.add(new BigDecimal(result[k].income));
        stockDayIncome = stockDayIncome.add(dayIncome);
        stockTotalmarketValue = stockTotalmarketValue.add(marketValue);
    }
    str += "<tr><td>合计</td><td colspan='3'></td><td>" + stockDayIncome + "</td><td colspan='6'></td><td>" + stockTotalmarketValue + "</td><td></td><td>" + stockTotalIncome
        +"</td></tr>";
    return str;
}

function getFundTableHtml(result){
    var str = "";
    fundTotalIncome = new BigDecimal("0");
    fundDayIncome = new BigDecimal("0");
    fundTotalmarketValue = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        dayIncome = new BigDecimal(parseFloat((new BigDecimal(result[k].gszzl)).multiply((new BigDecimal(result[k].dwjz))).multiply(new BigDecimal(result[k].bonds)).divide(new BigDecimal("100"))).toFixed(2));
        marketValue = new BigDecimal(parseFloat((new BigDecimal(result[k].gsz)).multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        str += "<tr><td>"
            + "<a onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" + result[k].fundCode
            + "</td><td>" +result[k].fundName
            + "</td><td>"
            + "</td><td>" + dayIncome
            + "</td><td>" +result[k].gszzl + "%"
            + "</td><td>"
            + "</td><td>" + result[k].dwjz + "(" + result[k].jzrq + ")"
            + "</td><td>" + result[k].gsz
            + "</td><td>" +result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + marketValue
            + "</td><td>" + result[k].incomePercent + "%"
            + "</td><td>" + result[k].income
            +"</td></tr>";
        fundTotalIncome = fundTotalIncome.add(new BigDecimal(result[k].income));
        fundDayIncome = fundDayIncome.add(dayIncome);
        fundTotalmarketValue = fundTotalmarketValue.add(marketValue);
    }
    str += "<tr><td>合计</td><td colspan='3'></td><td>" + fundDayIncome + "</td><td colspan='6'></td><td>" + fundTotalmarketValue + "</td><td></td><td>" + fundTotalIncome
        +"</td></tr>";
    str += "<tr><td>股票基金汇总合计</td><td colspan='3'></td><td>" + fundDayIncome.add(stockDayIncome) + "</td><td colspan='6'></td><td>" + fundTotalmarketValue.add(stockTotalmarketValue) + "</td><td></td><td>" + fundTotalIncome.add(stockTotalIncome)
        +"</td></tr>";

    return str;
}

function getAppName(app){
    if(app == "ZFB"){
        return "支付宝";
    } else if(app == "DFCF"){
        return "东方财富";
    } else if(app == "DFZQ"){
        return "东方证券";
    } else if(app == "ZGYH"){
        return "中国银行";
    }
}

function filterApp(app) {
    filteredApp = app;
    getData();
}