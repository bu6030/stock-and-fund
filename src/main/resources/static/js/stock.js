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
            var app = $("#app");
            app.find('option').remove();
            app.append("<option value=''>请选择</option>");
            for(var k in appList) {
                var opt = $("<option></option>").text(appList[k].name).val(appList[k].code);
                app.append(opt);
            }
            initData();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
    // 30s刷新
    setInterval('autoRefresh()', 30000);
}

function initData() {
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
}

function getTableHtml(result){
    var str = "";
    var totalIncome = new BigDecimal("0");
    var dayIncome = new BigDecimal("0");
    var totalDayIncome = new BigDecimal("0");
    var marketValue = new BigDecimal("0");
    var totalmarketValue = new BigDecimal("0");
    var marketValuePercent = new BigDecimal("0");
    var stockTotalCostValue = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        if ($("#enableFilterHide").is(":checked") && result[k].hide == true) {
            continue;
        }
        marketValue = (new BigDecimal(result[k].now)).multiply(new BigDecimal(result[k].bonds));
        totalmarketValue = totalmarketValue.add(marketValue);
    }

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
        totalDayIncome = totalDayIncome.add(dayIncome);
        // totalmarketValue = totalmarketValue.add(marketValue);
        if (totalmarketValue.compareTo(new BigDecimal("0")) != 0) {
            marketValuePercent = marketValue.multiply(new BigDecimal("100")).divide(totalmarketValue);
        }
        var dayIncomeStyle = dayIncome == 0 ? "" : (dayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalIncomeStyle = result[k].income == 0 ? "" : (result[k].income > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

        var day20Max = new BigDecimal(result[k].day20Max + "");
        var day20Min = new BigDecimal(result[k].day20Min + "");
        var day10Min = new BigDecimal(result[k].day10Min + "");
        var now = new BigDecimal(result[k].now + "");
        var donchianChennel = "";
        var donchianChennelStyle = "";
        if (now.compareTo(day20Max) > 0) {
            donchianChennel = "破20高";
            donchianChennelStyle = "style=\"color:#c12e2a\"";
        } else if(now.compareTo(day20Min) < 0) {
            donchianChennel = "破20低";
            donchianChennelStyle = "style=\"color:#3e8f3e\"";
        } else if(now.compareTo(day10Min) < 0) {
            donchianChennel = "破10低";
            donchianChennelStyle = "style=\"color:#3e8f3e\"";
        } else {
            donchianChennel = "监控中";
        }
        // 计算股票总成本
        var costPrice = new BigDecimal(result[k].costPrise+"");
        var costPriceValue = new BigDecimal(parseFloat(costPrice.multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        stockTotalCostValue = stockTotalCostValue.add(costPriceValue);

        str += "<tr><td>"
            + "<a href='#' onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" +result[k].name
            + "</td><td " + dayIncomeStyle + ">" + result[k].change
            + "</td><td " + dayIncomeStyle + ">" + result[k].changePercent +"%"
            + "</td><td " + dayIncomeStyle + ">" + dayIncome
            + "</td><td>" + result[k].max
            + "</td><td>" + result[k].min
            + "</td><td " + donchianChennelStyle + ">" + "20最高:" + day20Max + "；20最低：" + day20Min + "；10最低：" + day10Min + "；" + donchianChennel
            + "</td><td>" + result[k].now
            + "</td><td>" + result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + marketValue
            + "</td><td>" + marketValuePercent + "%"
            + "</td><td>" + costPriceValue
            + "</td><td " + totalIncomeStyle + ">" + result[k].incomePercent +"%"
            + "</td><td " + totalIncomeStyle + ">" + result[k].income
            + "</td><td>" + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"修改\" onclick=\"updateStock('" + result[k].code + "','" + result[k].costPrise + "','" + result[k].bonds + "','" + result[k].app + "','" + result[k].hide + "','" + result[k].name + "')\">"
            + "<span class=\"am-icon-pencil-square-o\"></span></button>"
            + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"买卖\" onclick=\"buyOrSell('" + result[k].code + "','" + result[k].name + "')\">"
            + "<span class=\"am-icon-shopping-cart\"></span></button>"
            + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"删除\" onclick=\"deleteStock('" + result[k].code + "')\">"
            + "<span class=\"am-icon-remove\"></span></button>"
            +"</td></tr>";
        totalIncome = totalIncome.add(new BigDecimal(result[k].income));
    }
    var totalDayIncomePercent = new BigDecimal("0");
    var totalIncomePercent = new BigDecimal("0");
    if (totalmarketValue != 0) {
        totalDayIncomePercent = totalDayIncome.multiply(new BigDecimal("100")).divide(totalmarketValue);
        totalIncomePercent = totalIncome.multiply(new BigDecimal("100")).divide(totalmarketValue);
    }
    var totalDayIncomePercentStyle = totalDayIncome == 0 ? "" : (totalDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    var totalIncomePercentStyle = totalIncome == 0 ? "" : (totalIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    str += "<tr><td>合计</td><td colspan='2'></td><td " + totalDayIncomePercentStyle + ">" + totalDayIncomePercent + "%</td><td " + totalDayIncomePercentStyle + ">" + totalDayIncome + "</td><td colspan='6'></td><td colspan='2'>" + totalmarketValue.setScale(2) + "</td><td>"+stockTotalCostValue+"</td><td " + totalIncomePercentStyle + ">" + totalIncomePercent + "%</td><td " + totalIncomePercentStyle + ">" + totalIncome
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
    $("#name").val('');
    $("#code").val('');
    $("#costPrise").val('');
    $("#bonds").val('100');
    $("#app").val('');
    $("#myModal").modal();
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

function updateStock(code, costPrise, bonds, app, hide, name){
    $("#code").val(code);
    $("#costPrise").val(costPrise);
    $("#bonds").val(bonds);
    $("#app").val(app);
    $("#hide").val(hide);
    $("#name").val(name);
    $("#myModal").modal();
}

function submitStockAndFund(){
    var type =$("#type").val();
    var code =$("#code").val();
    var costPrise =$("#costPrise").val();
    var bonds =$("#bonds").val();
    var app = $("#app").val();
        var hide = $("#hide").val();
    var req = {
        "code": code,
        "costPrise": costPrise,
        "bonds": bonds,
        "app": app,
        "hide": hide
    }
    var url = null;
    if(type=="fund"){
        url = "/saveFund";
    }else{
        url = "/saveStock";
    }
    $.ajax({
        url: url,
        type:"post",
        data : JSON.stringify(req),
        dataType:'json',
        contentType: 'application/json',
        success: function (data){
            if(data.code!="00000000"){
                alert("添加失败！");
                $("#myModal").modal( "hide" );
            }else{
                // window.opener.getData();
                location.reload();
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function buyOrSell(code, name) {
    $("#code").val(code);
    $("#nameBuyOrSell").val(name);
    $("#buyOrSellModal").modal();
}


function submitBuyOrSell(){
    var code =$("#code").val();
    var cost =$("#cost").val();
    var price =$("#price").val();
    var handleBonds =$("#handleBonds").val();
    var buyOrSell = $("#buyOrSell").val();
    var req = {
        "date": formatDate(new Date()),
        "code": code,
        "price": price,
        "cost": cost,
        "bonds": handleBonds,
        "type": buyOrSell
    }
    var url = "/buyOrSellStock";
    $.ajax({
        url: url,
        type:"post",
        data : JSON.stringify(req),
        dataType:'json',
        contentType: 'application/json',
        success: function (data){
            if(data.code!="00000000"){
                alert("买卖失败！");
            }
            location.reload();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function formatDate (date) {
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    return y + '-' + m + '-' + d;
}

function buyOrSellChanged() {
    var buyOrSell =$("#buyOrSell").val();
    if(buyOrSell == "1"){
        $("#cost").val(5);
    } else {
        $("#cost").val(0);
    }
}

function enableFilterHideChanged() {
    initData();
}