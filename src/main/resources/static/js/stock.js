var pageSize = 15;
var filteredApp = "ALL";

function getData() {
    var userId = $("#userId").val();
    var personName = $("#personName").val();
    var accountId = $("#accountId").val();
    $.ajax({
        url:"/stock",
        type:"get",
        data :{
        },
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            var str = getTableHtml(result);
            $("#nr").html(str);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });

    $.ajax({
        url:"/stock/table",
        type:"get",
        data :{
        },
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            $("#stock").val(result);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
    lay('#version').html('-v'+ laydate.v);
    // 10s刷新
    // setInterval(function () {
    //     window.location.reload()
    // }, 10000)
}

function getTableHtml(result){
    var str = "";
    var totalIncome = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var totalDayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    var totalmarketValue = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        dayIncome = (new BigDecimal(result[k].change)).multiply(new BigDecimal(result[k].bonds));
        marketValue = (new BigDecimal(result[k].now)).multiply(new BigDecimal(result[k].bonds));
        totalDayIncome = totalDayIncome.add(dayIncome);
        totalmarketValue = totalmarketValue.add(marketValue);
        str += "<tr><td>"
            + "<a onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" + result[k].code
            + "</td><td>" +result[k].name
            + "</td><td>" + result[k].change
            + "</td><td>" + result[k].changePercent +"%"
            + "</td><td>" + dayIncome
            + "</td><td>" + result[k].max
            + "</td><td>" + result[k].min
            + "</td><td>" + result[k].now + "</td><td>" + result[k].costPrise
            + "</td><td>" + result[k].bonds + "</td><td>" + result[k].incomePercent +"%"
            + "</td><td>" + marketValue
            + "</td><td>" + result[k].income
            +"</td></tr>";
        totalIncome = totalIncome.add(new BigDecimal(result[k].income));
    }
    str += "<tr><td>合计</td><td colspan='4'></td><td>" + totalDayIncome + "</td><td colspan='6'></td><td>" + totalmarketValue + "</td><td>" + totalIncome
        +"</td></tr>";
    return str;
}

function saveStock(){
    var userId = $("#userId").val();
    var stock = $("#stock").val();
    var req = {
        "userId" : userId,
        "stock":stock
    }
    $.ajax({
        url:"/stock",
        type:"post",
        data : JSON.stringify(req),
        dataType:'json',
        contentType: 'application/json',
        success: function (data){
            if(data.code=="00000000"){
                getData();
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
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
