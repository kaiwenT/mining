// JavaScript Document
//2.1任务列表显示
function allData (page){
	search_click=false;
    $.ajax({
        type:"post",
        // url:"http://1v5002132k.iask.in:13020/xinheng/issue/queryOwnIssue",
        url:"/issue/queryOwnIssue",
		data:JSON.stringify(GetJsonData(page)),
		dataType:"json",
		contentType:"application/json",
		beforeSend : function(){
		    begin();
		},
        success:function(msg){
            // console.log(msg);
            if(msg.status=="OK"){
                // alert("success") ;
				var items = msg.result.list ;
				var cookie_value1;
				$('.ht_cont tr:not(:first)').html("");
				$.each(items,function(idx,item) {
					// alert(msg.tagName);
					cookie_value1="'"+item.issueId+"'";
					row= '<tr><td height="40" align="center">'+(idx+1)+
					'</td><td height="40" align="center"><a href="javascript:;" onclick="setCookie('+cookie_value1+')">'+item.issueName+
					'</a></td><td height="40" align="center">'+item.creator+
					'</td><td height="40" align="center">'+ new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')+
					'</td><td height="40" align="center">'+item.lastOperator+
					'</td><td height="40" align="center">'+ new Date(item.lastUpdateTime.time).format('yyyy-MM-dd hh:mm:ss')+
					'</td><td height="40" align="center"><img src="images/delete.png" class="'+item.issueId+'" /></td></tr>'
					$('.ht_cont').append(row);
					
				});
				
            }else{
                alert("fail");
            }

        },
        complete:function(){
		    stop();
		} ,
        error:function(){
            // ���������
        }
    });
}
allData (1)
function GetJsonData(page) {
    var json = {
		"issueId":"",
		"issueName":"" ,
		"createStartTime":"2016-01-01",
		"createEndTime":"2017-04-04",
		"user":"",
		"lastUpdateStartTime":"2016-01-01",
		"lastUpdateEndTime":"2017-04-04",
		"pageNo":parseInt(page),
		"pageSize":10
    };
    return json;
}


/**
 * 根据页码加载数据
 * 
 * @param {整型}
 *            page 页码
 */
var search_click;
function setViewForPage(page){
		
	if(search_click){
		searchData(page);
	}else{
		allData(page);
	}
}

/**
 * 省略号点击
 */
function setPageChangeView(){
	var bt_name=parseInt($("#other").attr('name'))+3;
	updatePageValue(bt_name);
	setViewForPage(bt_name);
	setFirstSelected();
	updateNowPage(bt_name);
}
/**
 * 更新页码数据
 * 
 * @param {Object}
 *            base_num
 */
function updatePageValue(base_num){
	var p1=parseInt(base_num);
	var p2=parseInt(base_num)+1;
	var p3=parseInt(base_num)+2;
	$("#p_1").val(p1);
	$("#p_2").val(p2);
	$("#p_3").val(p3);
	$("#other").attr('name',p1);
}
/**
 * 页码点击
 * 
 * @param {Object}
 *            p_id 页码
 */
function pageNumClick(p_id){
	// background: #0e63ab;
    // color: #fff;
	var button=document.getElementById(p_id);
	var page=button.value;
	if(page!=undefined&&page.length>0){
		setViewForPage(page);
		updateNowPage(page);
		// $(this).addClass("cur").siblings().removeClass("cur");
		cleanAllSelected();
		button.style.background='#0e63ab';
		button.style.color='#FFFFFF';
	}
}
/**
 * 设置第一个页码按钮为选中状态
 */
function setFirstSelected(){
	cleanAllSelected();
	$("#p_1").css("background","#0e63ab");
	$("#p_1").css("color","#FFFFFF");
}
function setSecondSelected(){
	cleanAllSelected();
	$("#p_2").css("background","#0e63ab");
	$("#p_2").css("color","#FFFFFF");
}
function setThirdSelected(){
	cleanAllSelected();
	$("#p_3").css("background","#0e63ab");
	$("#p_3").css("color","#FFFFFF");
}
/**
 * 清除所有的选中状态
 */
function cleanAllSelected(){
	$("#p_1").css("background","#CCCCCC");
	$("#p_1").css("color","buttontext");
	$("#p_2").css("background","#CCCCCC");
	$("#p_2").css("color","buttontext");
	$("#p_3").css("background","#CCCCCC");
	$("#p_3").css("color","buttontext");
}
/**
 * 上一页，下一页点击
 * 
 * @param {Object}
 *            action -1上一页，1下一页
 */
function changPageOne(action){
	var now_page=parseInt($("#down_page").attr('name'));
	var page=now_page+action;
	if(page>0){
		updateAllStyleAndData(page,action);
	}
}
/**
 * 跳zhuan
 */
function changePage(){
	var page=$(".go_num").val();
	if(page!=undefined&&page.length>0){
		updateAllStyleAndData(page);
	}
}
function updateAllStyleAndData(page,action){
	updateNowPage(page);
	setViewForPage(page);
	if((page-1)%3==0){// 位置：第一个按钮 123 456 789
		setFirstSelected();
		if(action==1||action==undefined){// 点击下一页
			updatePageValue(page);
		}
	}else if(page%3==0){// 位置：第三个按钮
		setThirdSelected();
		if (action==-1||action==undefined) {// 点击上一页
			updatePageValue(page-2);
		}
	}else{// 位置：第二个按钮
		setSecondSelected();
		if(action==undefined){
			updatePageValue(page-1);
		}
	}
}
/**
 * 更新当前页码
 * 
 * @param {Object}
 *            page 当前页
 */
function updateNowPage(page){
	$("#down_page").attr('name',page);
}



function setCookie(value1){
	// alert(name+value);
	var cookie_name1="id";
	var Days = 1; // 此 cookie 将被保存 1 天
	var exp　= new Date();
	exp.setTime(exp.getTime() +Days*24*60*60*1000);
	document.cookie = cookie_name1 +"="+ escape (value1) + ";expires=" + exp.toGMTString();
	window.location.href = "topic_details.html";
}

function getCookie(name) {
	
	console.log(document.cookie);
	var arr =document.cookie.match(new RegExp("(^|)"+name+"=([^;]*)(;|$)"));
	if(arr !=null) 
		return unescape(arr[2]); 
	return null;
}

// 2.2任务搜索
function searchData(page){
	search_click=true;
	setFirstSelected();
    $.ajax({
        type:"post",
        url:"/issue/queryOwnIssue",
        data:JSON.stringify(SearchJsonData(page)),
        dataType:"json",
        contentType:"application/json",
        beforeSend : function(){
		    begin();
		},
        success:function(msg){
            // console.log(msg);
            if(msg.status=="OK"){
                // alert("success") ;
                var items = msg.result.list ;
                var cookie_value1;
                $('.ht_cont tr:not(:first)').html("");
                $.each(items,function(idx,item) {
                    // alert(msg.tagName);
                    cookie_value1="'"+item.issueId+"'";
                    row= '<tr><td height="40" align="center">'+(idx+1)+
                    '</td><td height="40" align="center"><a href="javascript:;" onclick="setCookie('+cookie_value1+')">'+item.issueName+
                    '</a></td><td height="40" align="center">'+item.creator+
                    '</td><td height="40" align="center">'+ new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')+
                    '</td><td height="40" align="center">'+item.lastOperator+
                    '</td><td height="40" align="center">'+ new Date(item.lastUpdateTime.time).format('yyyy-MM-dd hh:mm:ss')+
                    '</td><td height="40" align="center"><img src="images/delete.png" class="'+item.issueId+'" /></td></tr>'
                    $('.ht_cont').append(row);
                    
                });
				
            }else{
                alert("fail");
            }

        } ,
        complete:function(){
		    stop();
		},
        error:function(){
            
        }
    });
}

function SearchJsonData(page) {
	// var obj = $('#ht_name').val();
	var obj1 = $('#b_time').val();
	var obj2 = $('#o_time').val();
	var obj3 = $('#cj_name').val();
	var obj4 = $('#lb_time').val();
	var obj5 = $('#lo_time').val();
	// console.log(obj);
	// console.log(obj1);
    var json = {
		"issueId":"",
		"issueName": $('#ht_name').val(),
		"createStartTime":obj1,
		"createEndTime":obj2,
		"user":obj3,
		"lastUpdateStartTime":obj4,
		"lastUpdateEndTime":obj5,
		"pageNo":parseInt(page),
		"pageSize":10
    };
    return json;
}

// 2.3管理任务

$(function(){
	$(".ht_cont").on("click","img",function(){
		var issueId = $(this).attr("class");
		console.log(issueId);
		deleteData(issueId);
		function deleteData(issueId){
	
			$.ajax({
				type:"post",
				// url:"http://1v5002132k.iask.in:13020/xinheng/issue/delete",
				url:"/issue/delete",
				data:{
					issueId:issueId,
				} ,
				dataType:"json",
				success:function(msg){
					// alert("lll");
					console.log(msg);
					if(msg.status=="OK"){
						searchData(1)
					}else{
						alert("fail");
					}
		
				} ,
				error:function(){
					
				}
			});
		}
	})
})


