var file_array=new Array();
var now_count=0;
$(function(){
    //阻止浏览器默认行。 
    $(document).on({ 
        dragleave:function(e){    //拖离 
            e.preventDefault(); 
        }, 
        drop:function(e){  //拖后放 
            e.preventDefault(); 
        }, 
        dragenter:function(e){    //拖进 
            e.preventDefault(); 
        }, 
        dragover:function(e){    //拖来拖去 
            e.preventDefault(); 
        } 
    }); 
    //用javascript来侦听drop事件，首先要判断拖入的文件是否符合要求，包括图片类型、大小等，
    //然后获取本地图片信息，实现预览，最后上传
    var box = document.getElementById('drop_area'); //拖拽区域 
    box.addEventListener("drop",function(e){ 
        e.preventDefault(); //取消默认浏览器拖拽效果 
        var fileList = e.dataTransfer.files; //获取文件对象 
        //检测是否是拖拽文件到页面的操作 
        if(fileList.length == 0){ 
            return false; 
        } 
        //检测文件是不是图片 
        console.log(fileList[0].type);
        if(fileList[0].type.indexOf('excel') === -1){ 
           // alert("您拖的不是Excel文件！"); 
            //return false; 
        } 
         
        //拖拉图片到浏览器，可以实现预览功能 
        //var img = window.webkitURL.createObjectURL(fileList[0]); 
        var filename = fileList[0].name; //图片名称 
        var filesize = Math.floor((fileList[0].size)/1024);  
        if(filesize>500){ 
            alert("上传大小不能超过500K."); 
            return false; 
        }
        var str = "文件名称："+filename+"&nbsp;&nbsp;大小："+filesize+"KB&nbsp;&nbsp;"+"</p>"; 
        $("#preview").html(str); 
         
        //上传 
        xhr = new XMLHttpRequest(); 
        xhr.onreadystatechange=state_Change(filename);
        
        xhr.open("post", "http://182.140.244.198:8080/file/getColumnTitle", true); 
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest"); 
         
        var fd = new FormData(); 
        fd.append('mypic', fileList[0]); 
        xhr.send(fd); 
    },false); 
    function state_Change(filename){
    	console.log(xhr.readyState+", "+xhr.status);
    	if (xhr.readyState==4&&xhr.status==200) {// 200 = OK
    		alert(xhr.responseText);
    		var msg=xhr.responseText;
    		if(msg.status=="OK"){
    			file_array[parseInt(now_count)]=FileList[0];
    			now_count=parseInt(now_count)+1;
    			reSetView(msg,filename);
    		}
    		
    	}
    	else{
    		alert("Problem retrieving XML data");
    		//reSetView(data12,filename);
    	}
    };
}); 
function reSetView(msg,filename){
	var result=msg.result;
    var array=new Array();
    array=result;
    console.log(result.length+", "+array.length);
    var trable_spinner=getSpinner(array);//下拉框
    var date_=new Date();
    var now_time=date_.toLocaleDateString();//当前日期
    var li_context='<li>文件：<input type="text" class="files_name" value="'+filename+'" /> URL：'+trable_spinner+'标题：'+trable_spinner+' 时间：<input type="text" class="files_tname" value="'+now_time+'" /> 类型：<select><option> 微博</option><option> 新闻</option></select><img src="images/delete.png"  class="btn_up_del02" /><img src="images/up.png" class="btn_up_del01" /></li>'
    			$("#file_ul").append(li_context);
}
function getSpinner(array){
	var item="<select>";
	for(var i=0;i<array.length;i++){
		item+='<option>'+array[i]+'</option>';
	}
	item+='</select>';
	return item;
}

var data12={
  "status": "OK",
  "result": [
    "属性",
    "标题",
    "链接",
    "来源/发布人",
    "发布时间",
    "网站",
    "频道",
    "点击数",
    "回复数",
    "类型",
    "记者/作者",
    "内容长度",
    "分词",
    "摘要/内容",
    "发布日期"
  ]
}


