// JavaScript Document
//3.1话题详情展示
function dataShow(){
	var newId=getCookie("id");
	console.log(newId);
    $.ajax({
        type:"post",
//      url:"http://1v5002132k.iask.in:13020/xinheng/file/queryIssueFiles",
//        url:"http://182.140.244.198:8080/file/queryIssueFiles",
        url:"http://182.140.244.198:8080/file/queryIssueFiles",
        data:{
            //issueId:"d6093f1f-0c95-4538-a007-e85e8bc85526",
            issueId:newId
        },
        dataType:"json",
        success:function(msg){
            console.log(msg);
            if(msg.status=="OK"){
                //alert("success") ;
				var items = msg.result.issue ;
				$(function() {
					name = '<span>'+items.issueName+'</span>',
					admin = '<span>'+items.creator+'</span>',
					ct = '<span>'+items.createTime.year+'年'+items.createTime.month+'月'+items.createTime.date+'日'+'&nbsp;'+items.createTime.hours+':'+items.createTime.seconds+'</span>',
					lo = '<span>'+items.lastOperator+'</span>',
					lut = '<span>'+items.lastUpdateTime.year+'年'+items.lastUpdateTime.month+'月'+items.lastUpdateTime.date+'日'+'&nbsp;'+items.lastUpdateTime.hours+':'+items.lastUpdateTime.seconds+'</span>'
					
					$('.ckht_list li').eq(0).append( name ),
					$('.ckht_list li').eq(1).append( admin ),
					$('.ckht_list li').eq(2).append( ct ),
					$('.ckht_list li').eq(3).append( lo ),
					$('.ckht_list li').eq(4).append( lut );
				});
				var json = {
				
				  "status": "OK",
				  "result": {
					"list":[
					  {
						"creator": "gaoyan",
						"fileName": "多悦小学.xlsx",
						"issueId": "25651b78-7c89-4de6-989b-0928b9277e37",
						"size": 341,
						"sourceType": "新闻",
						"lineNumber": 1060,
						"uploadTime": {
						  "date": 14,
						  "hours": 18,
						  "seconds": 28,
						  "month": 11,
						  "timezoneOffset": -480,
						  "year": 116,
						  "minutes": 40,
						  "time": 1481712028000,
						  "day": 3
						},
						"fileId": "118027da-3eac-4fc4-8313-3c4e5d277890"
					  }]
				  }
					
				}
				var tabs = json.result.list ; 
				console.log(json);
				$.each(tabs,function( i,item){
					row ='<tr><td width="257" align="center" valign="middle">'+item.fileName+'</td><td width="95" align="center" valign="middle">'+item.creator+'</td><td width="173" align="center" valign="middle">'+item.uploadTime.year+'-'+item.uploadTime.month+'-'+item.uploadTime.date+'&nbsp;'+item.uploadTime.hours+':'+item.uploadTime.seconds+'</td><td align="center" valign="middle"><a href="javascript:;" class="btn_sc"><img src="images/julei.png" /></a><a href="javascript:;" class="btn_jl"><img src="images/delete.png" onclick="" /></a></td></tr>'
					$('.files_list table').append( row );
				});
				
            }else{
                alert("fail");
            }

        } ,
        error:function(){
            //���������
        }
    });
}
dataShow()
