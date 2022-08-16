var pageSize = 15;

function getData() {
    $.ajax({
        url:"/deposit",
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
    var totalTotalDayIncome = 0;
    var totalFundDayIncome = 0;
    var totalStockDayIncome = 0;

    var totalFundMarketValue = 0;
    var totalStockMarketValue = 0;
    var totalTotalMarketValue = 0;
    for(var k in result) {
        totalFundDayIncome = totalFundDayIncome + parseFloat(result[k].fundDayIncome);
        totalStockDayIncome = totalStockDayIncome + parseFloat(result[k].stockDayIncome);
        totalTotalDayIncome = totalTotalDayIncome + parseFloat(result[k].totalDayIncome);

        var fundIncomePercent = parseFloat(result[k].fundDayIncome).toFixed(4) * 100/parseFloat(result[k].fundMarketValue);
        var stockIncomePercent = parseFloat(result[k].stockDayIncome).toFixed(4) * 100/parseFloat(result[k].stockMarketValue);
        var totalIncomePercent = parseFloat(result[k].totalDayIncome).toFixed(4) * 100/parseFloat(result[k].totalMarketValue);

        str += "<tr><td>" + result[k].date
            + "</td><td>" + parseFloat(result[k].fundDayIncome).toFixed(2)
            + "</td><td>" + parseFloat(result[k].fundMarketValue).toFixed(2)
            + "</td><td>" + parseFloat(fundIncomePercent).toFixed(2)  + "%"

            + "</td><td>" + parseFloat(result[k].stockDayIncome).toFixed(2)
            + "</td><td>" + parseFloat(result[k].stockMarketValue).toFixed(2)
            + "</td><td>" + parseFloat(stockIncomePercent).toFixed(2)  + "%"

            + "</td><td>" + parseFloat(result[k].totalDayIncome).toFixed(2)
            + "</td><td>" + parseFloat(result[k].totalMarketValue).toFixed(2)
            + "</td><td>" + parseFloat(totalIncomePercent).toFixed(2)  + "%"
            +"</td></tr>";

    }

    str += "<tr><td>合计"
        + "</td><td>" + parseFloat(totalFundDayIncome).toFixed(2)
        + "</td><td>"
        + "</td><td>"
        + "</td><td>" + parseFloat(totalStockDayIncome).toFixed(2)
        + "</td><td>"
        + "</td><td>"
        + "</td><td>" + parseFloat(totalTotalDayIncome).toFixed(2)
        + "</td><td>"
        + "</td><td>"
        +"</td></tr>";
    return str;
}


function deposit(){
    if(!confirm("确定要提前统计当日盈利吗？")){
        return;
    }
    var url = "/deposit";
    var req = {}
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

function deleteDeposit(){
    if(!confirm("确定要删除当日盈利吗？")){
        return;
    }
    var url = "/deposit";
    var req = {}
    $.ajax({
        url: url,
        type:"delete",
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