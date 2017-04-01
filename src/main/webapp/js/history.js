/**
 * Created by Administrator on 2016/12/18.
 */

$(document).ready(function(){
    $.ajax({
        type:"post",
        url:"/result/queryResultList",
        success:function(msg){
            var items = msg.result;
            $('.summary_up table tr:not(:first)').html('');
            $.each(items,function(i, item) {
                        rows = '<tr><td height="32" align="center"><span id="'+item.rid+'" onclick="historyData()">'
                                + item.comment
                                + '</span></td><td height="32" align="center">'
                                + item.creator
                                + '</td><td height="32" align="center">'
                                + new Date(item.createTime.time).format('yyyy-MM-dd hh:mm:ss')
                                + '</td><td height="32" align="center"><img src="images/delete.png" onclick="fileDel()" /></td></tr>'
                        $('.summary_up table').append(rows);
                    })
        },
        error : function(msg){
            alert("数据请求失败");
        }
    });
});

function historyData(){
	$(".summary_up tr span").click(function(){
		var historyId=$(this).attr("id");
		$.ajax({
		type:"post",
		url:"/result/getCountResult",
		data:{
		    resultId:historyId
		},
		dataType:"json",
		success: function(msg){
			console.log(msg);
			if( msg.status == "OK"){
				// alert("删除成功");
				var items = msg.result;
                $('.summary_tab table tr:not(:first)').html('');
                $.each(items,function(i, item) {
	                rows = '<tr><td height="32" align="center"><input type="checkbox" class="'
	                        + i + '"/></td><td height="32" align="center"><a href="'
	                        + item[1]
	                        + '">'
	                        + item[2]
	                        + '</a></td><td height="32" align="center">'
	                        + item[3]
	                        + '</td><td height="32" align="center">'
	                        + item[0] + '</td></tr>'
	                $('.summary_tab table').append(rows);
	            })
			}else{
				alert(msg.result);
			}
		},
		error: function(){
			alert("请求失败");
		}
	})	
	})
}

/* 合并 */
function addLayData() {
    var sets = [];
    $(".summary_tab input:checked").each(function(i) {
        sets.push($(this).attr('class'));
    });
    console.log(sets);
    $.ajax({
        type : "post",
        url : "/result/combineSets",
        data : JSON.stringify(sets),
        dataType : "json",
        contentType : "application/json",
        success : function(msg) {
            if (msg.status == "OK") {
                freshData();
            } else {
                alert(msg.result);
            }
        },
        error : function() {
            alert("数据请求失败");
        }
    });
}

function freshData() {
    $
            .ajax({
                type : "post",
                url : "/result/getCountResult",
                success : function(msg) {
                    if (msg.status == 'OK') {
                        var items = msg.result;
                        $('.summary_tab table tr:not(:first)').html('');
                        $
                                .each(
                                        items,
                                        function(i, item) {
                                            rows = '<tr><td height="32" align="center"><input type="checkbox" class="'
                                                    + i
                                                    + '"/></td><td height="32" align="center"><a href="'
                                                    + item[1]
                                                    + '">'
                                                    + item[2]
                                                    + '</a></td><td height="32" align="center">'
                                                    + item[3]
                                                    + '</td><td height="32" align="center">'
                                                    + item[0] + '</td></tr>'
                                            $('.summary_tab table')
                                                    .append(rows);
                                        })

                    } else {
                        alert(msg.result);
                    }
                },
                error : function() {
                    alert("数据请求失败");
                }
            });
}

/* 删除 */
function deleteLayData() {
    var sets = [];
    $(".summary_tab input:checked").each(function(i) {
        sets.push($(this).attr('class'));
    });
    console.log(sets);
    $.ajax({
        type : "post",
        url : "/result/deleteSets",
        data : JSON.stringify(sets),
        dataType : "json",
        contentType : "application/json",
        success : function(msg) {
            if (msg.status == "OK") {
                freshData();
            } else {
                alert(msg.result);
            }
        },
        error : function() {
            alert("数据请求失败");
        }
    });
}
