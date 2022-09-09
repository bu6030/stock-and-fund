var pageSize = 15;
var filteredApp = "ALL";
var appList;

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

    lay('#version').html('-v'+ laydate.v);
    // 30s刷新
    setInterval('autoRefresh()', 30000)
}

function getTableHtml(result){
    var str = "";
    var totalIncome = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var totalDayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    var totalmarketValue = new BigDecimal("0");
    var marketValuePercent = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        marketValue = (new BigDecimal(result[k].now)).multiply(new BigDecimal(result[k].bonds));
        totalmarketValue = totalmarketValue.add(marketValue);
    }

    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        dayIncome = (new BigDecimal(result[k].change)).multiply(new BigDecimal(result[k].bonds));
        marketValue = (new BigDecimal(result[k].now)).multiply(new BigDecimal(result[k].bonds));
        totalDayIncome = totalDayIncome.add(dayIncome);
        // totalmarketValue = totalmarketValue.add(marketValue);
        marketValuePercent = marketValue.multiply(new BigDecimal("100")).divide(totalmarketValue);
        var dayIncomeStyle = dayIncome == 0 ? "" : (dayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalIncomeStyle = result[k].income == 0 ? "" : (result[k].income > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

        str += "<tr><td>"
            + "<a onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" +result[k].name
            + "</td><td " + dayIncomeStyle + ">" + result[k].change
            + "</td><td " + dayIncomeStyle + ">" + result[k].changePercent +"%"
            + "</td><td " + dayIncomeStyle + ">" + dayIncome
            + "</td><td>" + result[k].max
            + "</td><td>" + result[k].min
            + "</td><td>" + result[k].now
            + "</td><td>" + result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + marketValue
            + "</td><td>" + marketValuePercent + "%"
            + "</td><td " + totalIncomeStyle + ">" + result[k].incomePercent +"%"
            + "</td><td " + totalIncomeStyle + ">" + result[k].income
            + "</td><td>" + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"修改\" onclick=\"updateStock('" + result[k].code + "')\">"
            + "<span class=\"am-icon-pencil-square-o\"></span></button>"
            + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"删除\" onclick=\"deleteStock('" + result[k].code + "')\">"
            + "<span class=\"am-icon-remove\"></span></button>"
            +"</td></tr>";
        totalIncome = totalIncome.add(new BigDecimal(result[k].income));
    }
    str += "<tr><td>合计</td><td colspan='3'></td><td>" + totalDayIncome + "</td><td colspan='6'></td><td>" + totalmarketValue + "</td></td><td></td><td>" + totalIncome
        +"</td><td></td></tr>";
    return str;
}

function getAppName(app){
    for(var k in appList) {
        if(app == appList[k].code){
            return appList[k].name;
        }
    }
    return app;
}

function filterApp(app) {
    filteredApp = app;
    getData();
}

function showDialog(type){
    var iHeight = 600;
    var iWidth = 800;
    //获得窗口的垂直位置
    var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    //获得窗口的水平位置
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    var url = '/addStockAndFund.html?type='+type;

    window.open (url, 'newwindow', 'height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
}

function deleteStock(code){
    if(!confirm("确定要删除吗？")){
        return;
    }
    var url = "/deleteStock";
    var req = {
        "code" : code
    }
    $.ajax({
        url: url,
        type:"post",
        data : JSON.stringify(req),
        dataType:'json',
        contentType: 'application/json',
        success: function (data){
            if(data.code=="00000000") {
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

function updateStock(code){
    var iHeight = 600;
    var iWidth = 800;
    //获得窗口的垂直位置
    var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    //获得窗口的水平位置
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    window.open ('/updateStockAndFund.html?code='+code+'&type=stock', 'newwindow', 'height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
}