// JavaScript Document
//结果汇总
$(function() {
	$('.data_xq_tit li').mouseover(function(event) {
		
		$(this).addClass('stop').siblings().removeClass('stop');
		//和事件源索引号相同的div显示 -- 事件源的索引号 == $(this).index()
		$('.data_xq_cont').eq($(this).index()).addClass('stop_zs').siblings().removeClass('stop_zs');
	});
});
//数据汇总
$(function() {
	$('.summary_l li').click(function(event) {
		
		$(this).addClass('now').siblings().removeClass('now');
		//和事件源索引号相同的div显示 -- 事件源的索引号 == $(this).index()
		$('.summary_r').eq($(this).index()).addClass('btn_hz').siblings().removeClass('btn_hz');
		$('.summary_wrap').eq($(this).index()).addClass('summary_show').siblings().removeClass('summary_show');
	});
});