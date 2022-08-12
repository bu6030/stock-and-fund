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

        totalFundMarketValue = totalFundMarketValue + parseFloat(result[k].fundMarketValue);
        totalStockMarketValue = totalStockMarketValue + parseFloat(result[k].stockMarketValue);
        totalTotalMarketValue = totalTotalMarketValue + parseFloat(result[k].totalMarketValue);

        var fundIncomePercent = parseFloat(result[k].fundDayIncome).toFixed(4) * 100/parseFloat(result[k].fundMarketValue);
        var stockIncomePercent = parseFloat(result[k].stockDayIncome).toFixed(4) * 100/parseFloat(result[k].stockMarketValue);
        var totalIncomePercent = parseFloat(result[k].totalDayIncome).toFixed(4) * 100/parseFloat(result[k].totalMarketValue);
        // var fundIncomePercent = new BigDecimal("0");
        // fundIncomePercent = (new BigDecimal(totalFundDayIncome+"")).multiply(new BigDecimal("100")).divide(new BigDecimal(totalFundMarketValue+""));
        // var stockIncomePercent = new BigDecimal("0");
        // stockIncomePercent = (new BigDecimal(totalStockDayIncome+"")).multiply(new BigDecimal("100")).divide(new BigDecimal(totalStockMarketValue+""));
        //
        // alert((new BigDecimal(totalStockDayIncome+"")));
        // alert((new BigDecimal(totalStockDayIncome+"")).multiply(new BigDecimal("100")));
        // alert((new BigDecimal(totalStockDayIncome+"")).multiply(new BigDecimal("100")).divide(new BigDecimal(totalStockMarketValue+"")));

        // var totalIncomePercent = new BigDecimal("0");
        // totalIncomePercent = (new BigDecimal(totalTotalDayIncome+"")).multiply(new BigDecimal("100")).divide(new BigDecimal(totalTotalMarketValue+""));
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
    var totalIncomePercent = totalTotalDayIncome * 100 / totalTotalMarketValue;
    var totalFundIncomePercent = totalFundDayIncome * 100 / totalFundMarketValue;
    var totalStockIncomePercent = totalStockDayIncome* 100 / totalStockMarketValue;
    str += "<tr><td>合计"
        + "</td><td>" + parseFloat(totalFundDayIncome).toFixed(2)
        + "</td><td>" + parseFloat(totalFundMarketValue).toFixed(2)
        + "</td><td>" + parseFloat(totalFundIncomePercent).toFixed(2) + "%"
        + "</td><td>" + parseFloat(totalStockDayIncome).toFixed(2)
        + "</td><td>" + parseFloat(totalStockMarketValue).toFixed(2)
        + "</td><td>" + parseFloat(totalStockIncomePercent).toFixed(2) + "%"
        + "</td><td>" + parseFloat(totalTotalDayIncome).toFixed(2)
        + "</td><td>" + parseFloat(totalTotalMarketValue).toFixed(2)
        + "</td><td>" + parseFloat(totalIncomePercent).toFixed(2) + "%"
        +"</td></tr>";
    return str;
}