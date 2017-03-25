e/**
 * Created by Administrator on 2016/12/18.
 */

//4.1根据时间汇总

function accordTimeData(){
	var value = $(".summary_time input[name = 'timeradio']:checked").val();
	var end = new Date;
	var strEnd = "" + end.getFullYear() + "-";
	strEnd += (end.getMonth()+1) + "-";
	strEnd += end.getDate();
	//console.log(strEnd);
	/*var  month = enddate.getMonth()+1;
	if(month.toString().length == 1){
		month = '0'+ month;
	}*/
	
	if( value == "1" ){
		$(".summary_cont").css('display','block');
		var start = new Date( end.setDate(end.getDate()-7));
		var strStart = "" + start.getFullYear() + "-";
		strStart += (start.getMonth()+1) + "-";
		strStart += start.getDate();
		console.log(strStart);
	}else if( value == "2" ){
		$(".summary_cont").css('display','block');
		var start = new Date( end.setMonth(end.getMonth()-1));
		var strStart = "" + start.getFullYear() + "-";
		strStart += (start.getMonth()+1) + "-";
		strStart += start.getDate();
		//console.log(strStart);
	}else if( value == "3" ){
		$(".summary_cont").css('display','block');
		var start = new Date( end.setMonth(end.getMonth()-3));
		var strStart = "" + start.getFullYear() + "-";
		strStart += (start.getMonth()+1) + "-";
		strStart += start.getDate();
		//console.log(strStart);
	}else if( value == "4"){
		$(".summary_cont").css('display','block');
        var strEnd = $(".lol_end").val();
        var strStart = $(".lol_begin").val();
		console.log(strEnd);
		console.log(strStart);
    }else{
			
	}
	//console.log(value);
		
	$.ajax({
		type:"post",
		//url:"http://1v5002132k.iask.in:13020/xinheng/issue/miningByTime",
		url:"http://182.140.244.198:8080/issue/miningByTime",
		data:{
			startTime: strStart,
			endTime: strEnd
		} ,
		dataType:"json",
		success:function(msg){
			//console.log(msg);
			if(msg.status == "OK"){
				//alert("success") ;
				var news = msg.result;
				//console.log(msg)
				$('.summary_tab table').html('');
				$.each(news,function(i,item){
					rows= '<tr><td width="280"><div class="tab_tit"><input type="checkbox" class="choose" />'+'标题：'+item[0]+'</div></td><td width="120" align="center"><div class="tab_from">'+'网站：'+item[1]+'</div></td><td width="230" align="center">'+'发布时间：'+item[2]+'</td><td width="80" align="center">'+'数量：'+item[3]+'</td></tr>'
					$('.summary_tab table').append( rows );
				})
			}else{
				alert("fail");
			}

		} ,
		error:function(){
			//���������
		}
	});
}



//4.2根据文件汇总
function accordFileData(){
	
	$(".summary_cont").css('display','block');
	
    $.ajax({
        type:"post",
        //url:"http://1v5002132k.iask.in:13020/xinheng/issue/miningByFile",
        url:"http://182.140.244.198:8080/issue/miningByFile",
        data:{
        	fileIds: "01346567-d049-4f90-818c-080effc5164a"
        } ,
        dataType:"json",
        success:function(msg){
            console.log(msg);
            if(msg.status=="OK"){
                //alert("success") ;
				var items = msg.result;
				$.each(items,function(i,item){
					
                    rows = '<tr><td height="32" align="center"><input type="checkbox" /></td><td height="32" align="center">'+item[1]+'</td><td height="32" align="center">'+高岩+'</td><td height="32" align="center">'+item[2]+'</td></tr>'
					$('.summary_up table').append( rows );
				})
            }else{
                alert("fail");
            }
        } ,
        error:function(){
            //���������
        }
    });
}

//�ϲ�����е���
function addLayData(){alert("lll");
    $.ajax({
        type:"post",
        //url:"http://1v5002132k.iask.in:13020/xinheng/result/combineSets",
        url:"http://182.140.244.198:8080/result/combineSets",
        data:{
            "startTime":" ",
            "endTime":" "
        } ,
        dataType:"json",
        success:function(msg){
            console.log(msg);
            if(msg.status=="OK"){
                alert("success") ;
            }else{
                alert("fail");
            }

        } ,
        error:function(){
            //���������
        }
    });
}

//ɾ������е���
function deleteLayData(){
    $.ajax({
        type:"post",
        //url:"http://1v5002132k.iask.in:13020/xinheng/result/combineSets",
        url:"http://182.140.244.198:8080/result/combineSets",
        data:{
            "startTime":" ",
            "endTime":" "
        } ,
        dataType:"json",
        success:function(msg){
            console.log(msg);
            if(msg.status=="OK"){
                alert("success") ;
            }else{
                alert("fail");
            }

        } ,
        error:function(){
            //���������
        }
    });
}