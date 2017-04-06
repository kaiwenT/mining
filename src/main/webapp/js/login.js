/**
 * 
 */
function login() {
        var username = $("#form-username").val().replace(' ','');
        var passwd = $("#form-password").val().replace(' ','');
        if(username ===undefined || username ==='' || passwd === undefined || passwd ==='' ){
            alert('请输入用户名和密码');
            return;
        }
        $.ajax({
            url:'/login',
            type : 'post',
            data : {
                username : username,
                password :  passwd
            },
            success: function(msg){
                if(msg.status ==='OK'){
                    window.location.href="/topic_list.html";
                }else{
                    alert('用户名和密码错误');
                }
            },
            error : function(){
                alert('数据请求失败');
            }
        });
    }