/**
 * Created by Administrator on 2016/12/18.
 */
function creatInt() {
    var title = $("#chuangjian").val().replace(" ","");
    if(title===undefined || title ==''){
        alert("请输入任务名称");
        return;
    }
    $.ajax({
        type : "post",
        url : "/issue/create",
        data : {
            issueName : $("#chuangjian").val(),
        },
        dataType : "json",
        beforeSend : function(){
		    begin();
		},
        success : function(msg) {
            console.log(msg);
            if (msg.status == "OK") {
                window.location.href = "topic_list.html";
            } else {
                alert("fail");
            }
        },
        complete:function(){
		    stop();
		},
        error : function() {
            alert("数据请求失败");
        }
    });
}
