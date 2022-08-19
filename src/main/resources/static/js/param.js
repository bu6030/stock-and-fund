var pageSize = 15;
var filteredApp = "ALL";

function getData() {
    var userId = $("#userId").val();
    var personName = $("#personName").val();
    var accountId = $("#accountId").val();
    $.ajax({
        url:"/param",
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

    for(var k in result) {
        str += "<tr><td>" + result[k].type
            + "</td><td>" + result[k].code
            + "</td><td>" + result[k].name
            + "</td><td>" + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"修改\" onclick=\"updateParam('" + result[k].code + "','" + result[k].type + "')\">"
            + "<span class=\"am-icon-pencil-square-o\"></span></button>"
            + "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"删除\" onclick=\"deleteParam('" + result[k].code + "','" + result[k].type + "')\">"
            + "<span class=\"am-icon-remove\"></span></button>"
            +"</td></tr>";

    }
    return str;
}


function showDialog(type){
    var iHeight = 600;
    var iWidth = 800;
    //获得窗口的垂直位置
    var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    //获得窗口的水平位置
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    var url = '/addParam.html?type='+type;

    window.open (url, 'newwindow', 'height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
}

function deleteParam(code, type){
    if(!confirm("确定要删除吗？")){
        return;
    }
    var url = "/param";
    var req = {
        "code" : code,
        "type": type
    }
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

function updateParam(code, type){
    var iHeight = 600;
    var iWidth = 800;
    //获得窗口的垂直位置
    var iTop = (window.screen.availHeight - 30 - iHeight) / 2;
    //获得窗口的水平位置
    var iLeft = (window.screen.availWidth - 10 - iWidth) / 2;
    window.open ('/updateParam.html?code='+code+'&type='+type, 'newwindow', 'height='+iHeight+', width='+iWidth+', top='+iTop+', left='+iLeft+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
}