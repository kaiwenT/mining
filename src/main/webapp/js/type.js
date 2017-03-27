// JavaScript Document
//用户信息展示
function typeInforShow(page){
	search_click=false;
	$.ajax({
		type:"post",
		url:"/sourceType/selectAllSourceType",
		data:{
			start:(parseInt(3*page-3)),
			limit:3
		},
		dataType:"json",
		success: function(msg){
			console.log(msg);
			if( msg.status == "OK"){
				//alert("success");	
				var items = msg.result ;
				//console.log(items);
				var cookie_value1;
				var cookie_value2;
				$('.infor_tab02 tr:not(:first)').html("");
				$.each(items,function(idx,item) {
					//alert(msg.tagName);
					cookie_value1="'"+item.id+"'";
					cookie_value2="'"+item.name+"'";
					row= '<tr><td width="169" height="30" align="center" bgcolor="#ffffff">'+(idx+1)+'</td><td width="231" height="30" align="center" bgcolor="#ffffff">'+item.name+'</td><td colspan="2" width="140" height="30" align="center" bgcolor="#ffffff"><img src="images/user_bj.png" onClick="setCookie('+cookie_value1+','+cookie_value2+')" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascripy:;"><img src="images/user_del.png" class="delType" id="'+item.id+'" /></a></td></tr>'
					$('.infor_tab02').append(row);
				});
			}else{
				//alert("fail");
				$('.infor_tab02 tr:not(:first)').html("");
			}
		},
		error: function(){
			
		}
	})	
}
typeInforShow(1)

function setCookie(value1,value2){
	//alert(name+value);
	var cookie_name1="id";
	var cookie_name2="name";
	var Days = 1; //此 cookie 将被保存 1 天
	var exp　= new Date();
	exp.setTime(exp.getTime() +Days*24*60*60*1000);
	document.cookie = cookie_name1 +"="+ escape (value1) + ";expires=" + exp.toGMTString();
	document.cookie = cookie_name2 +"="+ escape (value2) + ";expires=" + exp.toGMTString();
	window.location.href = "type_change.html";
}

/**
 * 根据页码加载数据
 * @param {整型} page 页码
 */
var search_click;
function setViewForPage(page){
		
	if(search_click){
		typeInforSearch(page);
	}else{
		typeInforShow(page);
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
 * @param {Object} base_num
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
 * @param {Object} p_id 页码
 */
function pageNumClick(p_id){
	//background: #0e63ab;
    //color: #fff;
	var button=document.getElementById(p_id);
	var page=button.value;
	if(page!=undefined&&page.length>0){
		setViewForPage(page);
		updateNowPage(page);
		//$(this).addClass("cur").siblings().removeClass("cur");
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
 * @param {Object} action -1上一页，1下一页
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
		if(action==1||action==undefined){//点击下一页 
			updatePageValue(page);
		}
	}else if(page%3==0){//位置：第三个按钮
		setThirdSelected();
		if (action==-1||action==undefined) {//点击上一页 
			updatePageValue(page-2);
		}
	}else{//位置：第二个按钮
		setSecondSelected();
		if(action==undefined){
			updatePageValue(page-1);
		}
	}
}
/**
 * 更新当前页码
 * @param {Object} page 当前页
 */
function updateNowPage(page){
	$("#down_page").attr('name',page);
}

//信息搜索
function typeInforSearch(page){
	search_click=true;
	console.log($("#type_search").val());
	setFirstSelected();
	$.ajax({
		type:"post",
		url:"/sourceType/selectSourceTypeByName",
		data:{
			name:$("#type_search").val(),
			start:(parseInt(3*page-3)),
			limit:0
		},
		dataType:"json",
		success: function(msg){
			console.log(msg);
			if( msg.status == "OK"){
				//alert("success");	
				var items = msg.result ;
				var cookie_value1;
				var cookie_value2;
				console.log(items);
				$('.infor_tab02 tr:not(:first)').html("");
				var cookie_name="'typeName'";
				$.each(items,function(idx,item) {
					//alert(msg.tagName);
					cookie_value1="'"+item.id+"'";
					cookie_value2="'"+item.name+"'";
					row= '<tr><td width="169" height="30" align="center" bgcolor="#ffffff">'+(idx+1)+'</td><td width="231" height="30" align="center" bgcolor="#ffffff">'+item.name+'</td><td colspan="2" width="140" height="30" align="center" bgcolor="#ffffff"><img src="images/user_bj.png" onClick="setCookie('+cookie_value1+','+cookie_value2+')" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascripy:;"><img src="images/user_del.png" class="delType" id="'+item.id+'" /></a></td></tr>'
					$('.infor_tab02').append(row);
				});
			}else{
				//alert("fail");
				$('.infor_tab02 tr:not(:first)').html("");
			}
		},
		error: function(){
			
		}
	})	
}


//用户添加
function typeInforAdd() {
	window.location.href = "type_add.html";
}
function AddtypeInfor(){
	var submit=$("#addType").val();
	console.log(submit);
	$.ajax({
		type:"post",
		url:"/sourceType/insertSourceType",
		data:{
			name:submit
		},
		dataType:"json",
		success: function(msg){
			console.log(msg);
			if( msg.status == "OK"){
				alert("添加成功");
			}else{
				alert("fail");
			}
		},
		error: function(){
			
		}
	})	
}


function clearType(){
	$("#addType").val('');
}


//用户编辑
function getCookie(name) {
	
	console.log(document.cookie);
	var arr =document.cookie.match(new RegExp("(^|)"+name+"=([^;]*)(;|$)"));
	if(arr !=null) 
		return unescape(arr[2]); 
	return null;
}

function ChangetypeInfor(){
	var newId=getCookie("id");
	console.log(newId);
	$.ajax({
		type:"post",
		url:"/sourceType/updateSourceType",
		data:{
			name:$("#new_name_type").val(),
			id:newId
		},
		dataType:"json",
		success: function(msg){
			console.log(msg);
			if( msg.status == "OK"){
				alert("类型信息修改成功");	
				//window.location.href = "type_infor.html";
			}else{
				alert("fail");
			}
		},
		error: function(){
			
		}
	})	
}
function clearNewtype(){
	$("#new_name_type").val('');
}

//用户删除

$(function(){
	$(".infor_tab02").on("click",".delType",function(){
		var typeId = $(this).attr("id");
		console.log(typeId);
		typeInforDel(typeId);
		
		function typeInforDel(typeId){
	
			$.ajax({
				type:"post",
				url:"/sourceType/deleteSourceTypeById",
				data:{
					id:typeId,
				} ,
				dataType:"json",
				success:function(msg){
					//alert("lll");
					console.log(msg);
					if(msg.status=="OK"){
						alert("类型信息删除成功");
						$('.infor_tab02 tr:not(:first)').html("");
						typeInforShow(1)
					}else{
						alert("fail");
					}
				} ,
				error:function(){
					//���������
				}
			});
		}
	})
})