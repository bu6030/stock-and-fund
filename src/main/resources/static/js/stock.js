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
            + "</td><td class='no-wrap' onclick=\"getStockHistory('" + result[k].code + "')\">" + result[k].name
            + "</td><td " + dayIncomeStyle + ">" + result[k].change
            + "</td><td " + dayIncomeStyle + ">" + result[k].changePercent +"%"
            + "</td><td " + dayIncomeStyle + ">" + dayIncome
            + "</td><td>" + result[k].max
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
            + "</td><td class='no-wrap'>" + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"修改\" onclick=\"updateStock('" + result[k].code + "','" + result[k].costPrise + "','" + result[k].bonds + "','" + result[k].app + "','" + result[k].hide + "','" + result[k].name + "')\">"
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
    str += "<tr><td>合计</td><td colspan='2'></td><td " + totalDayIncomePercentStyle + ">" + totalDayIncomePercent + "%</td><td " + totalDayIncomePercentStyle + ">" + totalDayIncome + "</td><td colspan='10'></td><td colspan='2'>" + totalmarketValue.setScale(2) + "</td><td>"+stockTotalCostValue+"</td><td " + totalIncomePercentStyle + ">" + totalIncomePercent + "%</td><td " + totalIncomePercentStyle + ">" + totalIncome
        +"</td><td></td></tr>";
    return str;
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
    showBuyOrSellCost(code);
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

function searchStock() {
    $("#search-stock-select").find("option").remove();
    let stockName = $("#input-stock-name-search").val();
    if (stockName != "" && stockName != null) {
        var stocksArr = searchStockByName(stockName);
        for (var k in stocksArr) {
            var values = stocksArr[k].split("~");
            var market = "";
            if (values[0] == 'sh') {
                market = "沪A"
            } else if (values[0] == 'sz') {
                market = "深A"
            } else if (values[0] == 'hk') {
                market = "港股"
            } else {
                market = "其他"
            }
            var option = $("<option></option>").val(values[0] + values[1]).text(A2U(values[2]) + " " + values[0] + values[1] + " （" + market + "）");
            $("#search-stock-select").append(option);
        }
        $("#input-stock-name-search").val("");
        if (stocksArr.length > 0) {
            $("#search-stock-modal").modal();
        }
    }
}

function searchStockByName(name) {
    var stocksArr;
    $.ajax({
        url: "/stock/search?name=" + name,
        type: "get",
        data: {},
        async: false,
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data) {
            var data1 = data.value;
            if (data1.indexOf("v_hint=\"N\";") != -1) {
                alert("不存在该股票");
                $("#stock-name").val("");
                return;
            }
            if (data1.indexOf("v_cate_hint") != -1) {
                data1 = data1.substring(data1.indexOf("\n")+1);
            }
            data1 = data1.replace("v_hint=\"", "").replace(" ", "");
            stocksArr = data1.split("^");
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
    return stocksArr;
}

function A2U(str) {
    return unescape(str.replace(/\\u/gi, '%u'));
}

function clickSearchStockSelect() {
    let stockCode = $("#search-stock-select").val();
    $("#code").val(stockCode);
    $("#costPrise").val(0);
    $("#bonds").val(0);
    submitStockAndFund();
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

function showBuyOrSell() {
    $("#history-modal").modal('hide');
    let code = $("#buy-or-sell-stock-code").val();
    let name = $("#buy-or-sell-stock-name").val();
    $.ajax({
        url:"/buyOrSellStock?code=" + code,
        type:"get",
        data :{},
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
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

function showBuyOrSellCost(code) {
    $.ajax({
        url:"/buyOrSellStock?code=" + code,
        type:"get",
        data :{},
        dataType:'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data) {
            var result = data.value;
            var str = "";
            if (result.length == 0) {
                return;
            }
            var cost = result[0].cost;
            var bonds = result[0].bonds;
            $("#cost").val(cost);
            $("#handleBonds").val(bonds);
            $("#buyOrSellModal").modal();
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