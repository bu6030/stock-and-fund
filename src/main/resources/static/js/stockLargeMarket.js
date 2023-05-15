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

function getLargeMarketTableHtml(result) {
    var str = "";
    for(var k in result) {
        var style = "style=\"font-size: 16px;font-weight:600;"
            + (result[k].change == 0 ? "" : (result[k].change >= 0?"color:#c12e2a;\"":"color:#3e8f3e;\""));
        str += "<a " + style + ">" + result[k].name + " " + result[k].now + "（" + result[k].change + "&nbsp;&nbsp;" + result[k].changePercent +"%）   </a>";
    }
    console.log(str);
    return str;
}