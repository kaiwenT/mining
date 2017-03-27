// JavaScript Document
//3.1话题详情展示
function dataShow(){
	var newId=getCookie("id");
	console.log(newId);
	$('.files_list table').html("");
    $.ajax({
        type:"post",
// url:"http://1v5002132k.iask.in:13020/xinheng/file/queryIssueFiles",
        url:"/file/queryIssueFiles",
        data:{
            issueId:newId
        },
        dataType:"json",
        success:function(msg){
            console.log(msg);
            if(msg.status=="OK"){
				var items = msg.result.issue;
				$(function() {
					name = '<span>'+items.issueName+'</span>',
					admin = '<span>'+items.creator+'</span>',
					ct = '<span>'+new Date(items.createTime.time).format('yyyy-MM-dd hh:mm:ss')+'</span>',
					lo = '<span>'+items.lastOperator+'</span>',
					lut ='<span>'+new Date(items.lastUpdateTime.time).format('yyyy-MM-dd hh:mm:ss')+'</span>'
					
					$('.ckht_list li').eq(0).append( name ),
					$('.ckht_list li').eq(1).append( admin ),
					$('.ckht_list li').eq(2).append( ct ),
					$('.ckht_list li').eq(3).append( lo ),
					$('.ckht_list li').eq(4).append( lut );
				});
				var tabs = msg.result.list; 
				$.each(tabs,function(i,item){
					cookie_value1="'"+item.fileId+"'";
					row ='<tr><td width="257" align="center" valign="middle">'+item.fileName+
					'</td><td width="95" align="center" valign="middle">'+item.creator+
					'</td><td width="173" align="center" valign="middle">'+new Date(items.createTime.time).format('yyyy-MM-dd hh:mm:ss')+
					'</td><td align="center" valign="middle"><img src="images/julei.png" class="btn_sc" onClick="setCookie('+cookie_value1+')" /><a href="javascript:;" class="btn_jl"><img src="images/delete.png" onclick="" /></a></td></tr>'
					$('.files_list table').append(row);
				});				
            }else{
                alert("查询失败");
            }

        } ,
        error:function(){
            // ���������
        }
    });
}
dataShow()
function setCookie(value1){
	// alert(name+value);
	var cookie_name1="id";
	var Days = 1; // 此 cookie 将被保存 1 天
	var exp　= new Date();
	exp.setTime(exp.getTime() +Days*24*60*60*1000);
	document.cookie = cookie_name1 +"="+ escape (value1) + ";expires=" + exp.toGMTString();
	window.location.href = "summary_file.html";
}
