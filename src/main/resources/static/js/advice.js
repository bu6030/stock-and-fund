// src/main/resources/static/js/advice.js
function initData() {
    $.ajax({
        url: "/advice",
        type: "get",
        data: {},
        dataType: 'json',
        contentType: 'application/x-www-form-urlencoded',
        success: function (data) {
            var result = data.value;
            var str = getTableHtml(result);
            $("#nr").html(str);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
            alert("获取数据失败");
        }
    });
}

function escapeHtml(text) {
    var map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;',
        '\n': '\\n',
        '\r': '\\r'
    };

    return text.replace(/[&<>"'\n\r]/g, function(m) { return map[m]; });
}

function escapeForJavaScript(text) {
    if (!text) return '';
    // 转义单引号和换行符，用于JavaScript字符串
    return text.replace(/'/g, "\\'").replace(/\n/g, "\\n").replace(/\r/g, "\\r");
}

function getTableHtml(result) {
    var str = "";
    for (var k in result) {
        var escapedContent = escapeForJavaScript(result[k].adviceContent || '');
        str += "<tr>" +
            "<td>" + result[k].id + "</td>" +
            "<td>" + escapeHtml(result[k].adviceContent) + "</td>" +
            "<td>" + result[k].adviceDevelopVersion + "</td>" +
            "<td class='no-wrap'>" + result[k].date + "</td>" +
            "<td class='no-wrap'>" +
            "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"修改\" onclick=\"updateAdvice('" +
            result[k].id + "','" + escapedContent + "','" + result[k].adviceDevelopVersion + "')\">" +
            "<span class=\"am-icon-pencil-square-o\"></span></button>" +
            "<button class=\"am-btn am-btn-default am-btn-xs am-text-secondary am-round\" data-am-modal=\"{target: '#my-popups'}\" type=\"button\" title=\"删除\" onclick=\"deleteAdvice('" + result[k].id + "')\">" +
            "<span class=\"am-icon-remove\"></span></button>" +
            "</td>" +
            "</tr>";
    }
    return str;
}

function showDialog() {
    $("#adviceId").val('');
    $("#adviceContent").val('');
    $("#adviceDevelopVersion").val('');
    $("#myModal").modal();
}

function updateAdvice(id, content, version) {
    $("#adviceId").val(id);
    $("#adviceContent").val(content);
    $("#adviceDevelopVersion").val(version);
    $("#myModal").modal();
}

function deleteAdvice(id) {
    if (!confirm("确定要删除这条建议吗？")) {
        return;
    }

    var req = {
        "id": id
    };

    $.ajax({
        url: "/deleteAdvice",
        type: "post",
        data: JSON.stringify(req),
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            if (data.code == "00000000") {
                initData();
            } else {
                alert("删除失败：" + data.message);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
            alert("删除失败");
        }
    });
}

function submitAdvice() {
    var id = $("#adviceId").val();
    var adviceContent = $("#adviceContent").val();
    var adviceDevelopVersion = $("#adviceDevelopVersion").val();

    if (!adviceContent) {
        alert("请输入反馈内容");
        return;
    }

    var req = {
        "id":id,
        "adviceContent": adviceContent,
        "adviceDevelopVersion": adviceDevelopVersion
    };

    $.ajax({
        url: "/updateAdvice",
        type: "post",
        data: JSON.stringify(req),
        dataType: 'json',
        contentType: 'application/json',
        success: function (data) {
            if (data.code == "00000000") {
                $("#myModal").modal("hide");
                initData();
            } else {
                alert("保存失败：" + data.message);
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
            alert("保存失败");
        }
    });
}
