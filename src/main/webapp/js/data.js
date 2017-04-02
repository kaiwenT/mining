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
        mediaAttenHtml += parse_media(time, mediaAtten);
        var media = json[time].media;
        mediaHtml += parse_media(time, media);
    }
    console.log(mediaHtml);
    $('.media_count').append(mediaHtml);
    $('.media_atten').append(mediaAttenHtml);
    $('.info_count').append(typeHtml);
    $('.info_atten').append(netAttenHtml);

}

function parse_type(time, json) {
    var row = '<tr><td>' + time + '</td><td>' + parseData(json['论坛'])
            + '</td><td>' + parseData(json['新闻']) + '</td><td>'
            + parseData(json['博客']) + '</td><td>' + parseData(json['报纸'])
            + '</td><td>' + parseData(json['微信']) + '</td><td>'
            + parseData(json['贴吧']) + '</td><td>' + parseData(json['问答'])
            + '</td><td>' + parseData(json['手机']) + '</td><td>'
            + parseData(json['视频']) + '</td><td>' + parseData(json['微博'])
            + '</td><td>' + parseData(json['其他']) + '</td></tr>';
    return row;
}

function parse_media(time, json) {
    var row = '<tr><td>' + time + '</td><td>' + parseData(json['中央'])
            + '</td><td>' + parseData(json['省级']) + '</td><td>'
            + parseData(json['其他']) + '</td><td>' + parseData(json['未知'])
            + '</td></tr>';
    return row;
}

function parseData(json) {
    if (json === undefined) {
        return 0;
    } else {
        return json;
    }
}
$(function(){
    var myChart = echarts.init(document.getElementById('zx_type'));
    // 指定图表的配置项和数据
    var option = {
        title : {
            text : '销量柱状图', // 标题文本内容
        },
        toolbox : { // 可视化的工具箱
            show : true,
            feature : {
                dataView : { // 数据视图
                    show : true
                },
                restore : { // 重置
                    show : true
                },
                dataZoom : { // 数据缩放视图
                    show : true
                },
                saveAsImage : {// 保存图片
                    show : true
                },
                magicType : {// 动态类型切换
                    type : [ 'bar', 'line','pie' ]
                }
            }
        },
        tooltip : { // 弹窗组件
            show : true
        },
        xAxis : {
            data : [ "衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子" ]
        },
        yAxis : {},
        series : [ {
            name : '销量',
            type : 'bar',
            data : [ 5, 20, 36, 10, 10, 20 ]
        }, {
            name : '测试',
            type : 'bar',
            data : [ 2, 23, 16, 14, 1, 22 ]
        } ]

    };

    myChart.setOption(option);
});

function paintbt() {

}
