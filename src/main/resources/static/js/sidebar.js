function sidebar() {
    var userId = $("#userId").val();
    var req = {
        "userId" : userId
    }
    $.ajax({
        url: "/v1/getAccountList.do",
        type: "post",
        data: {
            "req": JSON.stringify(req)
        },
        dataType: 'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data) {
            var result = data.result;
            $("#totalFee").html("资产:" + data.totalFee);
            var moneyList = "";
            var bankcardList = "";
            var creditcardList = "";
            var financingList = "";
            for (var k in result) {
                if (result[k].ifDisplay == 1) {
                    if (result[k].accountType == "1") {
                        moneyList += "<li ><a onclick=\"setAccountId(" + result[k].accountId + ")\">" + result[k].accountName + "&nbsp;&nbsp;" + result[k].feeValue + "</a></li>";
                    } else if (result[k].accountType == "2") {
                        bankcardList += "<li ><a onclick=\"setAccountId(" + result[k].accountId + ")\">" + result[k].accountName + "&nbsp;&nbsp;" + result[k].feeValue + "</a></li>";
                    } else if (result[k].accountType == "3") {
                        creditcardList += "<li ><a onclick=\"setAccountId(" + result[k].accountId + ")\">" + result[k].accountName + "&nbsp;&nbsp;" + result[k].feeValue + "</a></li>";
                    } else if (result[k].accountType == "4") {
                        financingList += "<li ><a onclick=\"setAccountId(" + result[k].accountId + ")\">" + result[k].accountName + "&nbsp;&nbsp;" + result[k].feeValue + "</a></li>";
                    }
                }
            }
            $("#moneyList").html(moneyList);
            $("#bankcardList").html(bankcardList);
            $("#creditcardList").html(creditcardList);
            $("#financingList").html(financingList);
            var borrowList = "";
            if(data.loanFee!=0)
                borrowList += "<li ><a >我的借入款：" + data.loanFee + "<\/a><\/li>";
            if(data.borrowFee!=0)
                borrowList += "<li ><a >我的借出款：" + data.borrowFee + "<\/a><\/li>";
            $("#borrowList").html(borrowList);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}