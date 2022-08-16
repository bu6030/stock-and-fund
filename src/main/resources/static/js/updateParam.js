function getData() {

}

function submitParam(){
    var type =$("#type").val();
    var code =$("#code").val();
    var name =$("#name").val();
    var req = {
        "code": code,
        "type": type,
        "name": name
    }
    var url = "/param";
    $.ajax({
        url: url,
        type:"post",
        data : JSON.stringify(req),
        dataType:'json',
        contentType: 'application/json',
        success: function (data){
            if(data.code!="00000000"){
                alert("添加失败！");
            }else{
                // window.opener.getData();
                window.opener.location.reload();
            }
            window.close();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest.status);
            console.log(XMLHttpRequest.readyState);
            console.log(textStatus);
        }
    });
}