/**
 * Created by Administrator on 2016/12/18.
 */
function creatInt (){
    $.ajax({
        type:"post",
        //url:"http://1v5002132k.iask.in:13020/xinheng/issue/create",
//        url:"/issue/create",
        url:"/issue/create",
		data:{
            issueName: $("#chuangjian").val(),
        } ,
		dataType:"json",
        success:function(msg){
            console.log(msg);

            if(msg.status=="OK"){
                alert("创建成功") ;
				window.location.href = "topic_list.html";
            }else{
                alert("fail");
            }

        } ,
        error:function(){
            //���������
            alert("fail");
        }
    });
}
