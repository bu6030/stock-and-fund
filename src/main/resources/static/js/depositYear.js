var pageSize = 15;

function getData() {
    var beginDate = $("#beginDate").val();
    var endDate = $("#endDate").val();
    var url = "/deposit/year";
    $.ajax({
        url: url,
        type: "get",
        data : {},
        dataType: 'json',
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
    //执行一个laydate实例
    laydate.render({
        elem: '#beginDate' //指定元素
        ,type: 'date'
    });
    laydate.render({
        elem: '#endDate' //指定元素
        ,type: 'date'
    });
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

        var stockDayIncomeStyle = result[k].stockDayIncome == 0 ? "" : (result[k].stockDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var fundDayIncomeStyle = result[k].fundDayIncome == 0 ? "" : (result[k].fundDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalDayIncomeStyle = result[k].totalDayIncome == 0 ? "" : (result[k].totalDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

        str += "<tr><td>" + result[k].date
            + "</td><td " + fundDayIncomeStyle + ">" + parseFloat(result[k].fundDayIncome).toFixed(2)
            + "</td><td " + stockDayIncomeStyle + ">" + parseFloat(result[k].stockDayIncome).toFixed(2)
            + "</td><td " + totalDayIncomeStyle + ">" + parseFloat(result[k].totalDayIncome).toFixed(2)
            +"</td></tr>";

    }

    var newStr = "<tr><td>合计"
        + "</td><td>" + parseFloat(totalFundDayIncome).toFixed(2)
        + "</td><td>" + parseFloat(totalStockDayIncome).toFixed(2)
        + "</td><td>" + parseFloat(totalTotalDayIncome).toFixed(2)
        + "</td></tr>"
        + str;
    return newStr;
}