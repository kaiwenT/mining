// JavaScript Document
//结果汇总
$(function() {
    $('.data_xq_tit li').mouseover(
            function(event) {
                $(this).addClass('stop').siblings().removeClass('stop');
                // 和事件源索引号相同的div显示 -- 事件源的索引号 == $(this).index()
                $('.data_xq_cont').eq($(this).index()).addClass('stop_zs')
                        .siblings().removeClass('stop_zs');
            });
});

function paint() {
    var currentSet = getCookie('currentSet');
    var json = {
        interval : 2,
        currentSet : currentSet
    };
    $.ajax({
        type : "post",
        url : "/result/statisticSingleSet",
        data : JSON.stringify(json),
        dataType : "json",
        contentType : "application/json",
        success : function(msg) {
            parse(msg.result.time);
        },
        error : function() {
            alert("请求失败");
        }
    });
}
paint();

function parse(json) {
    var netAttenHtml = '';
    var typeHtml = '';
    var mediaAttenHtml = '';
    var mediaHtml = '';
    for ( var time in json) {
        var netAtten = json[time].netizenAttention;
        netAttenHtml += parse_type(time, netAtten);
        var infoType = json[time].infoType;
        typeHtml += parse_type(time, infoType);
        var mediaAtten = json[time].mediaAttention;
        mediaAttenHtml += parse_media(time,mediaAtten);
        var media = json[time].media;
        mediaHtml += parse_media(time,media);
    }
    console.log(mediaHtml);
    $('.media_count').append(mediaHtml);
    $('.media_atten').append(mediaAttenHtml);
    $('.info_count').append(typeHtml);
    $('.info_atten').append(netAttenHtml);
    
}

function parse_type(time, json) {
    var row = '<tr><td>' + time + '</td><td>' + json['论坛'] + '</td><td>'
            + json['新闻'] + '</td><td>' + json['博客'] + '</td><td>' + json['报纸']
            + '</td><td>' + json['微信'] + '</td><td>' + json['贴吧'] + '</td><td>'
            + json['问答'] + '</td><td>' + json['手机'] + '</td><td>' + json['视频']
            + '</td><td>' + json['微博'] + '</td></tr>';
    return row;
}

function parse_media(time, json) {
    var row = '<tr><td>' + time + '</td><td>' + json['中央'] + '</td><td>'
            + json['省级'] + '</td><td>' + json['其他'] + '</td><td>' + json['未知']
            + '</td></tr>';
    return row;
}
