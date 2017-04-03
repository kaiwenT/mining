/**
 * Created by Administrator on 2016/12/18.
 */
/* 搜索 */
function fileSearch() {
    var value = $(".summary_time input[name = 'timeradio']:checked").val();
    var end = new Date;
    var strEnd = "" + end.getFullYear() + "-";
    strEnd += (end.getMonth() + 1) + "-";
    strEnd += end.getDate() + 1;

    if (value == "1") {
        $(".summary_cont").css('display', 'block');
        var start = new Date(end.setDate(end.getDate() - 7));
        var strStart = "" + start.getFullYear() + "-";
        strStart += (start.getMonth() + 1) + "-";
        strStart += start.getDate();
        console.log(strStart);
    } else if (value == "2") {
        $(".summary_cont").css('display', 'block');
        var start = new Date(end.setMonth(end.getMonth() - 1));
        var strStart = "" + start.getFullYear() + "-";
        strStart += (start.getMonth() + 1) + "-";
        strStart += start.getDate();
        // console.log(strStart);
    } else if (value == "3") {
        $(".summary_cont").css('display', 'block');
        var start = new Date(end.setMonth(end.getMonth() - 3));
        var strStart = "" + start.getFullYear() + "-";
        strStart += (start.getMonth() + 1) + "-";
        strStart += start.getDate();
        // console.log(strStart);
    } else if (value == "4") {
        $(".summary_cont").css('display', 'block');
        var strEnd = $(".lol_end").val();
        var strStart = $(".lol_begin").val();
        console.log(strEnd);
        console.log(strStart);
    } else {

    }
    // console.log(value);
    $
            .ajax({
                type : "post",
                url : "/file/searchFileByCon",
                data : {
                    startTime : strStart,
                    endTime : strEnd
                },
                dataType : "json",
                beforeSend : function() {
                    begin();
                },
                success : function(msg) {
                    // console.log(msg);
                    if (msg.status == "OK") {
                        // alert("success") ;
                        var items = msg.result;
                        // console.log(msg)
                        $('.summary_up table tr:not(:first)').html('');
                        $
                                .each(
                                        items,
                                        function(i, item) {
                                            rows = '<tr><td height="32" align="center"><input type="checkbox"  name="groupIds"  class="'
                                                    + item.fileId
                                                    + '" /></td><td height="32" align="center">'
                                                    + item.fileName
                                                    + '</td><td height="32" align="center">'
                                                    + item.creator
                                                    + '</td><td height="32" align="center">'
                                                    + new Date(
                                                            item.uploadTime.time)
                                                            .format('yyyy-MM-dd hh:mm:ss')
                                                    + '</td></tr>'
                                            $('.summary_up table').append(rows);
                                        })
                    } else {
                        alert(msg.result);
                    }
                },
                complete : function() {
                    stop();
                },
                error : function() {
                    alert("数据请求失败");
                }
            });
}

// 汇总

function fileSummary() {
    $('.summary_tab table tr:not(:first)').html('');
    var fileIds = [];
    $(".summary_up input:checked").each(function(i) {
        fileIds.push($(this).attr("class"));
    });
    console.log(fileIds);
    $
            .ajax({
                type : "post",
                url : "/issue/miningByFile",
                data : JSON.stringify(fileIds),
                dataType : "json",
                contentType : "application/json",
                beforeSend : function() {
                    begin();
                },
                success : function(msg) {
                    // console.log(msg);
                    if (msg.status == "OK") {
                        var items = msg.result;
                        $
                                .each(
                                        items,
                                        function(i, item) {
                                            rows = '<tr><td height="32" align="center"><input type="checkbox" class="'
                                                    + i
                                                    + '"/></td><td height="32" align="center"><a href="'
                                                    + item[1]
                                                    + '" target="_blank">'
                                                    + item[2]
                                                    + '</a></td><td height="32" align="center">'
                                                    + item[3]
                                                    + '</td><td height="32" align="center"><a href="javascript:;" onclick="toPaint('
                                                    + i
                                                    + ',\''
                                                    + item[2].replace(/\"/g," ").replace(/\'/g," ")
                                                    + '\')">'
                                                    + item[0]
                                                    + '</a></td></tr>';
                                            $('.summary_tab table')
                                                    .append(rows);
                                        })
                    } else {
                        alert(msg.result);
                    }

                },
                complete : function() {
                    stop();
                },
                error : function() {
                    alert("数据请求失败");
                }
            });
}

function toPaint(currentSet, title) {
    setCookie('currentSet', currentSet);
    setCookie('title', title);
    window.location.href = "/data_results.html";
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
        beforeSend : function() {
            begin();
        },
        success : function(msg) {
            if (msg.status == "OK") {
                freshData();
            } else {
                alert(msg.result);
            }
        },
        complete : function() {
            stop();
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
                beforeSend : function() {
                    begin();
                },
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
                                                    + '" target="_blank">'
                                                    + item[2]
                                                    + '</a></td><td height="32" align="center">'
                                                    + item[3]
                                                    + '</td><td height="32" align="center"><a href="javascript:;" onclick="toPaint('
                                                    + i
                                                    + ',\''
                                                    + item[2].replace(/\"/g," ").replace(/\'/g," ")
                                                    + '\')">'
                                                    + item[0]
                                                    + '</a></td></tr>';
                                            $('.summary_tab table')
                                                    .append(rows);
                                        })

                    } else {
                        alert(msg.result);
                    }
                },
                complete : function() {
                    stop();
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
        beforeSend : function() {
            begin();
        },
        success : function(msg) {
            if (msg.status == "OK") {
                freshData();
            } else {
                alert(msg.result);
            }
        },
        complete : function() {
            stop();
        },
        error : function() {
            alert("数据请求失败");
        }
    });
}

$(function(){
	$("#chooseAll").click(function(){
		if (this.checked) {  
            $(".summary_tab tr :checkbox").prop("checked", true);  
        } else {  
            $(".summary_tab tr :checkbox").prop("checked", false);  
        }  
   }); 
})
