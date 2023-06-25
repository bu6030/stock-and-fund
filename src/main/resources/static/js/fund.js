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
        url:"/fund",
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
    var fundTotalCostValue = new BigDecimal("0");
    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        marketValue = new BigDecimal(parseFloat((new BigDecimal(result[k].gsz)).multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        totalmarketValue = totalmarketValue.add(marketValue);
    }


    for(var k in result) {
        if (filteredApp != "ALL" && result[k].app != filteredApp) {
            continue;
        }
        dayIncome = new BigDecimal(parseFloat((new BigDecimal(result[k].gszzl)).multiply((new BigDecimal(result[k].dwjz))).multiply(new BigDecimal(result[k].bonds)).divide(new BigDecimal("100"))).toFixed(2));
        marketValue = new BigDecimal(parseFloat((new BigDecimal(result[k].gsz)).multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        totalDayIncome = totalDayIncome.add(dayIncome);
        // totalmarketValue = totalmarketValue.add(marketValue);
        if (totalmarketValue.compareTo(new BigDecimal("0")) != 0) {
            marketValuePercent = marketValue.multiply(new BigDecimal("100")).divide(totalmarketValue);
        }
        totalIncome = totalIncome.add(new BigDecimal(result[k].income));
        var dayIncomeStyle = dayIncome == 0 ? "" : (dayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalIncomeStyle = result[k].income == 0 ? "" : (result[k].income > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

        // 计算基金总成本
        var costPrice = new BigDecimal(result[k].costPrise+"");
        var costPriceValue = new BigDecimal(parseFloat(costPrice.multiply(new BigDecimal(result[k].bonds))).toFixed(2));
        fundTotalCostValue = fundTotalCostValue.add(costPriceValue);

        str += "<tr><td>"
            + "<a href='#' onclick=\"filterApp('" + result[k].app + "')\">" + getAppName(result[k].app) + "</a>"
            + "</td><td>" + result[k].fundName
            + "</td><td " + dayIncomeStyle + ">" +result[k].gszzl + "%"
            + "</td><td " + dayIncomeStyle + ">" + dayIncome
            + "</td><td>" + result[k].dwjz + "(" + result[k].jzrq + ")"
            + "</td><td>" + result[k].gsz
            + "</td><td>" + result[k].costPrise
            + "</td><td>" + result[k].bonds
            + "</td><td>" + marketValue
            + "</td><td>" + marketValuePercent + "%"
            + "</td><td>" + costPriceValue
            + "</td><td " + totalIncomeStyle + ">" + result[k].incomePercent + "%"
            + "</td><td " + totalIncomeStyle + ">" + result[k].income
            + "</td><td>" + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"修改\" onclick=\"updateFund('" + result[k].fundCode + "','" + result[k].costPrise + "','" + result[k].bonds + "','" + result[k].app + "','" + result[k].fundName + "')\">"
            + "<span class=\"am-icon-pencil-square-o\"></span></button>"
            + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"删除\" onclick=\"deleteFund('" + result[k].fundCode + "')\">"
            + "<span class=\"am-icon-remove\"></span></button>"
            +"</td></tr>";

    }
    var totalDayIncomePercent = new BigDecimal("0");
    var totalIncomePercent = new BigDecimal("0");
    if (totalmarketValue != 0) {
        totalDayIncomePercent = totalDayIncome.multiply(new BigDecimal("100")).divide(totalmarketValue.subtract(totalDayIncome));
        totalIncomePercent = totalIncome.multiply(new BigDecimal("100")).divide(fundTotalCostValue);
    }
    var totalDayIncomePercentStyle = totalDayIncome == 0 ? "" : (totalDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    var totalIncomePercentStyle = totalIncome == 0 ? "" : (totalIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
    str += "<tr><td>合计</td><td></td><td " + totalDayIncomePercentStyle + ">" + totalDayIncomePercent + "%</td><td " + totalDayIncomePercentStyle + ">" + totalDayIncome + "</td><td colspan='4'></td><td colspan='2'>" + totalmarketValue + "</td><td>"+fundTotalCostValue+"</td><td " + totalIncomePercentStyle + ">" + totalIncomePercent + "%</td><td " + totalIncomePercentStyle + ">" + totalIncome
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

function showDialog(type) {
    $("#name").val('');
    $("#code").val('');
    $("#costPrise").val('');
    $("#bonds").val('');
    $("#app").val('');
    $("#myModal").modal();
    // var iHeight = 600;
    // var iWidth = 800;
    // //获得窗口的垂直位置
    // var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    // //获得窗口的水平位置
    // var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    // var url = '/addStockAndFund.html?type='+type;
    // window.open (url, 'newwindow', 'height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
}

function deleteFund(code){
    if(!confirm("确定要删除吗？")){
        return;
    }
    var url = "/deleteFund";
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

function updateFund(code, costPrise, bonds, app, name){
    // var iHeight = 600;
    // var iWidth = 800;
    // //获得窗口的垂直位置
    // var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    // //获得窗口的水平位置
    // var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;

    $("#name").val(name);
    $("#code").val(code);
    $("#costPrise").val(costPrise);
    $("#bonds").val(bonds);
    $("#app").val(app);
    $("#myModal").modal();

    // window.open ('/updateStockAndFund.html?code='+code+'&type=fund', 'newwindow', 'height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
}

function submitStockAndFund(){
    var type =$("#type").val();
    var code =$("#code").val();
    var costPrise =$("#costPrise").val();
    var bonds =$("#bonds").val();
    var app = $("#app").val();
    var req = {
        "code": code,
        "costPrise": costPrise,
        "bonds": bonds,
        "app": app
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