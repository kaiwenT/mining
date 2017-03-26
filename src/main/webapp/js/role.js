// JavaScript Document
//用户信息展示
function roleInforShow(page){
	search_click=false;
	$.ajax({
		type:"post",
		url:"http://182.140.244.198/role/selectAllRole",
		data:{
			start:(parseInt(3*page-3)),
			limit:3
		},
		dataType:"json",
		success: function(msg){
			if(msg.status=="OK"){
				var items=msg.result;
				var cookie_value1;
				var cookie_value2;
				$('.infor_tab02 tr:not(:first)').html("");
				console.log(msg);
				$.each(items,function(idx,item){
					cookie_value1="'"+item.roleId+"'";
					cookie_value2="'"+item.roleName+"'";
					row = '<tr><td width="169" height="30" align="center" bgcolor="#ffffff">'+(idx+1)+'</td><td width="231" height="30" align="center" bgcolor="#ffffff">'+item.roleName+'</td><td colspan="2" width="140" height="30" align="center" bgcolor="#ffffff"><img src="images/user_bj.png" onClick="setCookie('+cookie_value1+','+cookie_value2+')">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="images/user_del.png" class="delRole" id="'+item.roleId+'" /></td></tr>'
					$('.infor_tab02').append(row);
				});
			}else{
				$('.infor_tab02 tr:not(:first)').html("");
			}
		},
		error: function(){
			
		}
	})	
}
roleInforShow(1)
function setCookie(value1,value2){
	//alert(name+value);
	var cookie_name1="id";
	var cookie_name2="name";
	var Days = 1; //此 cookie 将被保存 1 天
	var exp　= new Date();
	exp.setTime(exp.getTime() +Days*24*60*60*1000);
	document.cookie = cookie_name1 +"="+ escape (value1) + ";expires=" + exp.toGMTString();
	document.cookie = cookie_name2 +"="+ escape (value2) + ";expires=" + exp.toGMTString();
	window.location.href = "role_change.html";
}
/**
 * 根据页码加载数据
 * @param {整型} page 页码
 */
var search_click;
function setViewForPage(page){
	if(search_click){
		roleInforSearch(page);
	}else{
		roleInforShow(page);
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
function roleInforSearch(page){
	var roleInfo = $("#searchRole").val();
	search_click=true;
	setFirstSelected();
	$.ajax({
		type:"post",
		url:"http://182.140.244.198/role/selectOneRoleInfo",
		data:{
			roleName:roleInfo,
			start:(parseInt(page)-1),
			limit:1
		},
		dataType:"json",
		success: function(msg){
			if( msg.status == "OK"){
				$('.infor_tab02 tr:not(:first)').html("");
				var items=msg.result;
				var cookie_value1;
				var cookie_value2;
				$.each(items,function(idx,item){
					cookie_value1="'"+item.roleId+"'";
					cookie_value2="'"+item.roleName+"'";
					row = '<tr><td width="169" height="30" align="center" bgcolor="#ffffff">'+(idx+1)+'</td><td width="231" height="30" align="center" bgcolor="#ffffff">'+item.roleName+'</td><td colspan="2" width="140" height="30" align="center" bgcolor="#ffffff"><img src="images/user_bj.png" onClick="setCookie('+cookie_value1+','+cookie_value2+')">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src="images/user_del.png" class="delRole" id="'+item.roleId+'" /></td></tr>'
					$('.infor_tab02').append(row);
				});
			}else{
				$('.infor_tab02 tr:not(:first)').html("");
				alert("输入角色名有误，请重新输入");
				roleInforShow()
			}
		},
		error: function(){
			
		}
	})	
}


//用户添加
function roleInforAdd() {
	window.location.href = "role_add.html";
}
function addRoleInfo(){
	console.log($(".addRole").val())
	$.ajax({
		type:"post",
		url:"http://182.140.244.198/role/insertRoleInfo",
		data:{
			roleName:$(".addRole").val()
		},
		dataType:"json",
		success: function(msg){
			if( msg.status == "OK"){
				console.log(msg);
				alert("添加成功");
			}else{
			console.log(msg);
				alert("fail");
			}
		},
		error: function(){

		}
	})
}

function clearRole(){
	$(".addRole").val('');
}

//用户编辑
function getCookie(name) {
	
	console.log(document.cookie);
	var arr =document.cookie.match(new RegExp("(^|)"+name+"=([^;]*)(;|$)"));
	if(arr !=null) 
		return unescape(arr[2]); 
	return null;
}


function changeRole(){
	var newId=getCookie("id");
	$.ajax({
		type:"post",
		url:"http://182.140.244.198/role/updateRoleInfo",
		data:{
			roleId:newId,
			roleName:$(".roleChange").val(),
			powerName:''
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
function clearNewrole(){
	$("#new_name_role").val('');
}


//用户删除

$(function(){
	$(".infor_tab02").on("click",".delRole",function(){
		var role_id = $(this).attr("id");
		console.log(role_id);
		roleInforDel(role_id);
		function roleInforDel(role_id){
			$.ajax({
				type:"post",
				url:"http://182.140.244.198/role/deleteRoleInfoById",
				data:{
					roleId:role_id
				} ,
				dataType:"json",
				success:function(msg){
					//alert("lll");
					console.log(msg);
					if(msg.status=="OK"){
						alert("success");
						$('.infor_tab02 tr:not(:first)').html("");
						roleInforShow(1)
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