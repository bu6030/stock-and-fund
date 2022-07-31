function getData() {

}

function submitStockAndFund(){
    var type =$("#type").val();
    var code =$("#code").val();
    var costPrise =$("#costPrise").val();
    var bonds =$("#bonds").val();
    var app = $("#app").val();
    var req = {
        "code": code,
        "costPrise": costPrise,
        "bonds": bonds,
        "app": app
    }
    var url = null;
    if(type=="fund"){
        url = "/saveFund";
    }else{
        url = "/saveStock";
    }
    $.ajax({
        url: url,
        type:"post",
        data : JSON.stringify(req),
        dataType:'json',
        contentType: 'application/json',
        success: function (data){
            if(data.code!="00000000"){
                alert("添加失败！");
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