var pageSize = 15;

function getData() {
    var beginDate = $("#beginDate").val();
    var endDate = $("#endDate").val();
    var url = "/deposit?beginDate="+beginDate+"&endDate="+endDate;
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

        var fundIncomePercent = parseFloat(result[k].fundDayIncome).toFixed(4) * 100/parseFloat(result[k].fundMarketValue);
        var stockIncomePercent = parseFloat(result[k].stockDayIncome).toFixed(4) * 100/parseFloat(result[k].stockMarketValue);
        var totalIncomePercent = parseFloat(result[k].totalDayIncome).toFixed(4) * 100/parseFloat(result[k].totalMarketValue);
        var stockDayIncomeStyle = result[k].stockDayIncome == 0 ? "" : (result[k].stockDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var fundDayIncomeStyle = result[k].fundDayIncome == 0 ? "" : (result[k].fundDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");
        var totalDayIncomeStyle = result[k].totalDayIncome == 0 ? "" : (result[k].totalDayIncome > 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"");

        str += "<tr><td>" + result[k].date
            + "</td><td " + fundDayIncomeStyle + ">" + parseFloat(fundIncomePercent).toFixed(2)  + "%"
            + "</td><td " + fundDayIncomeStyle + ">" + parseFloat(result[k].fundDayIncome).toFixed(2)
            + "</td><td>" + parseFloat(result[k].fundMarketValue).toFixed(2)

            + "</td><td " + stockDayIncomeStyle + ">" + parseFloat(stockIncomePercent).toFixed(2)  + "%"
            + "</td><td " + stockDayIncomeStyle + ">" + parseFloat(result[k].stockDayIncome).toFixed(2)
            + "</td><td>" + parseFloat(result[k].stockMarketValue).toFixed(2)

            + "</td><td " + totalDayIncomeStyle + ">" + parseFloat(totalIncomePercent).toFixed(2)  + "%"
            + "</td><td " + totalDayIncomeStyle + ">" + parseFloat(result[k].totalDayIncome).toFixed(2)
            + "</td><td>" + parseFloat(result[k].totalMarketValue).toFixed(2)

            +"</td></tr>";

    }

    str += "<tr><td>合计"
        + "</td><td>"
        + "</td><td>" + parseFloat(totalFundDayIncome).toFixed(2)
        + "</td><td>"
        + "</td><td>"
        + "</td><td>" + parseFloat(totalStockDayIncome).toFixed(2)
        + "</td><td>"
        + "</td><td>"
        + "</td><td>" + parseFloat(totalTotalDayIncome).toFixed(2)
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

function getWeekDeposit() {
    var weekStartDate = getCurrentWeekFirstDay();
    var weekEndDate = getCurrentWeekLastDay;
    $("#beginDate").val(weekStartDate);
    $("#endDate").val(weekEndDate);
    getData();
}

function getMonthDeposit() {
    var monthStartDate = getMonthStartDate();
    var monthEndDate = getMonthEndDate();
    $("#beginDate").val(monthStartDate);
    $("#endDate").val(monthEndDate);
    getData();
}

function getYearDeposit() {
    var date = new Date();
    var year = date.getFullYear();
    var yearStartDate = year + '-01-01';
    var yearEndDate = year + '-12-31';
    $("#beginDate").val(yearStartDate);
    $("#endDate").val(yearEndDate);
    getData();
}

function getMonthStartDate() {
    var date = new Date();
    date.setDate(1); // 将当前时间的日期设置成第一天
    var year= date.getFullYear() ; // 得到当前年份
    var month = date.getMonth()  + 1; // 得到当前月份（0-11月份，+1是当前月份）
    month = month > 10 ? month :'0' + month; // 补零
    var day = date.getDate(); // 得到当前天数，实际是本月第一天，因为前面setDate(1) 设置过了
    day = day > 10 ? day :'0' + day; // 补零
    return year + '-' + month + '-' + day; // 这里传入的是字符串
}

function getMonthEndDate(){
    var date  = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() +1;
    // 这里传入的是整数时间，返回的是下个月的第一天，因为月份是0-11
    var nextMonthFirthDay = new Date(year,month,1); // 下个月的第一天
    var oneDay = 1000*60 * 60 * 24; // 一天的时间毫秒数
    var endDay = new Date(nextMonthFirthDay - oneDay);
    var day = endDay.getDate(); // 本月最后一天
    // 这里传入的是字符串格式的时间，返回的是传入字符串的时间
    return year + '-' + month + '-' + day;
}

function getCurrentWeekFirstDay() {
    var date = new Date();
    var weekFirstDay = new Date(date - (getWeekDay(date) - 1) * 86400000);
    var firstMonth = Number(weekFirstDay.getMonth()) + 1;

    if (firstMonth < 10) {
        firstMonth = '0' + firstMonth;
    }
    var weekFirstDays = weekFirstDay.getDate();
    if (weekFirstDays < 10) {
        weekFirstDays = '0' + weekFirstDays;
    }
    return weekFirstDay.getFullYear() + '-' + firstMonth + '-' + weekFirstDays;
}

function getCurrentWeekLastDay() {
    var date = new Date();
    var weekFirstDay = new Date(date - (getWeekDay(date) - 1) * 86400000);
    var weekLastDay = new Date((weekFirstDay / 1000 + 6 * 86400) * 1000);
    var lastMonth = Number(weekLastDay.getMonth()) + 1;
    if (lastMonth < 10) {
        lastMonth = '0' + lastMonth;
    }
    var weekLastDays = weekLastDay.getDate();
    if (weekLastDays < 10) {
        weekLastDays = '0' + weekLastDays;
    }
    return weekFirstDay.getFullYear() + '-' + lastMonth + '-' + weekLastDays;
}

// 当周日时，day为0，因此获取当周周期时间时，会到下周周期
function getWeekDay(date){
    return date.getDay() == 0 ? 7:date.getDay();
};