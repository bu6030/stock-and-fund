function getLargeMarketData() {
    var url = "/stockLargeMarket";
    $.ajax({
        url: url,
        type: "get",
        data : {},
        dataType: 'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data){
            var result = data.value;
            var str = getLargeMarketTableHtml(result);
            $("#stockLargeMarket").html(str);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}

function getLargeMarketTableHtml(result){
    var str = "";
    for(var k in result) {
        var style = result[k].change == 0 ? "style=\"font-size: 18px;\"" : (result[k].change >= 0?"style=\"color:#c12e2a;font-size: 18px;\"":"style=\"color:#3e8f3e;font-size: 18px;\"");
        str += "<a " + style + ">" + result[k].name + " " + result[k].changePercent +"% </a>";
    }
    console.log(str);
    return str;
}