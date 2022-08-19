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
        var style = result[k].totalDayIncome >= 0?"style=\"color:#c12e2a\"":"style=\"color:#3e8f3e\"";
        totalFundDayIncome = totalFundDayIncome + parseFloat(result[k].fundDayIncome);
        totalStockDayIncome = totalStockDayIncome + parseFloat(result[k].stockDayIncome);
        totalTotalDayIncome = totalTotalDayIncome + parseFloat(result[k].totalDayIncome);

        var fundIncomePercent = parseFloat(result[k].fundDayIncome).toFixed(4) * 100/parseFloat(result[k].fundMarketValue);
        var stockIncomePercent = parseFloat(result[k].stockDayIncome).toFixed(4) * 100/parseFloat(result[k].stockMarketValue);
        var totalIncomePercent = parseFloat(result[k].totalDayIncome).toFixed(4) * 100/parseFloat(result[k].totalMarketValue);

        str += "<tr " + style + "><td>" + result[k].date
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
    let date = new Date();
    let year = date.getFullYear();
    var yearStartDate = year + '-01-01';
    var yearEndDate = year + '-12-31';
    $("#beginDate").val(yearStartDate);
    $("#endDate").val(yearEndDate);
    getData();
}

function getMonthStartDate() {
    let date = new Date();
    date.setDate(1); // 将当前时间的日期设置成第一天
    let year= date.getFullYear() ; // 得到当前年份
    let month = date.getMonth()  + 1; // 得到当前月份（0-11月份，+1是当前月份）
    month = month > 10 ? month :'0' + month; // 补零
    let day = date.getDate(); // 得到当前天数，实际是本月第一天，因为前面setDate(1) 设置过了
    return year + '-' + month + '-' + day; // 这里传入的是字符串
}

function getMonthEndDate(){
    let date  = new Date();
    let year = date.getFullYear();
    let month = date.getMonth() +1;
    // 这里传入的是整数时间，返回的是下个月的第一天，因为月份是0-11
    let nextMonthFirthDay = new Date(year,month,1); // 下个月的第一天
    let oneDay = 1000*60 * 60 * 24; // 一天的时间毫秒数
    let endDay = new Date(nextMonthFirthDay - oneDay);
    let day = endDay.getDate(); // 本月最后一天
    // 这里传入的是字符串格式的时间，返回的是传入字符串的时间
    return year + '-' + month + '-' + day;
}

function getCurrentWeekFirstDay() {
    let date = new Date();
    let weekFirstDay = new Date(date - (date.getDay() - 1) * 86400000)
    let firstMonth = Number(weekFirstDay.getMonth()) + 1

    if (firstMonth < 10) {
        firstMonth = '0' + firstMonth
    }
    let weekFirstDays = weekFirstDay.getDate();
    if (weekFirstDays < 10) {
        weekFirstDays = '0' + weekFirstDays;
    }
    return weekFirstDay.getFullYear() + '-' + firstMonth + '-' + weekFirstDays;
}

function getCurrentWeekLastDay() {
    let date = new Date();
    let weekFirstDay = new Date(date - (date.getDay() - 1) * 86400000)
    let weekLastDay = new Date((weekFirstDay / 1000 + 6 * 86400) * 1000)
    let lastMonth = Number(weekLastDay.getMonth()) + 1
    if (lastMonth < 10) {
        lastMonth = '0' + lastMonth
    }
    let weekLastDays = weekLastDay.getDate();
    if (weekLastDays < 10) {
        weekLastDays = '0' + weekLastDays;
    }
    return weekFirstDay.getFullYear() + '-' + lastMonth + '-' + weekLastDays;
}