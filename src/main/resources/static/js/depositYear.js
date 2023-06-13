var pageSize = 15;

function getData() {
    var beginDate = $("#beginDate").val();
    var endDate = $("#endDate").val();

    var type = $("#type").val();
    var url;
    if("month" == type){
        url = "/deposit/month";
    } else {
        url = "/deposit/year";
    }

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
    let totalIncomes = [];
    let days = [];

    for(var k in result) {
        totalFundDayIncome = totalFundDayIncome + parseFloat(result[k].fundDayIncome);
        totalStockDayIncome = totalStockDayIncome + parseFloat(result[k].stockDayIncome);
        totalTotalDayIncome = totalTotalDayIncome + parseFloat(result[k].totalDayIncome);

        var stockDayIncomeStyle = result[k].stockDayIncome == 0 ? "" : (result[k].stockDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var fundDayIncomeStyle = result[k].fundDayIncome == 0 ? "" : (result[k].fundDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalDayIncomeStyle = result[k].totalDayIncome == 0 ? "" : (result[k].totalDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

        totalIncomes.push(parseFloat(result[k].totalDayIncome).toFixed(2));
        days.push(result[k].date);

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

    // 展示报表
    var type = "收益汇总";
    var dataType = ['收益汇总'];
    var seriesData = [];
    seriesData[0] = {
        name: "合计",
        type: 'bar',
        data: totalIncomes.reverse()
    };
    var color = ['#2378f7'];
    setDetailChart(type, dataType, days.reverse(), seriesData, color);

    return newStr;
}

function setDetailChart(type, dataType, dataAxis, seriesData, color){
    // 基于准备好的dom，初始化echarts实例
    var myChart = echarts.init(document.getElementById('detailContainer'));

    option = {
        tooltip : {
            trigger: 'axis'
        },
        title: {
            text: type
        },
        legend: {
            data:dataType
        },
        toolbox: {
            show : true,
            feature : {
                mark : {show: true},
                dataView : {show: true, readOnly: false},
                magicType : {show: true, type: [ 'bar', 'line', 'stack', 'tiled']},
                restore : {show: true},
                saveAsImage : {show: true}
            }
        },
        // color : ['#8cc540','#009f5d','#019fa0','#019fde','#007cdc','#887ddd','#cd7bdd','#ff5675','#ff1244','#ff8345','#f8bd0b','#d1d2d4'],
        color : color,
        calculable : true,
        xAxis : [
            {
                type : 'category',
                data : dataAxis
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : seriesData
    };

    myChart.setOption(option);
}