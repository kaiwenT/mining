$(document).ready(
        function() {
            this.totalPages = 10;
            var paginationDomTemp = "#paginationTemplate";
            var paginationTarget = "#contentSearchResultList";

            var topicIntroDomTemp = "#topicIntroduction";
            var topicIntorTarget = ".content-wrapper__changeable";

            var $originLink = $("a[title='privateTopic']");
            var originPage = 1;
            $originLink.addClass('active');

            var showCensus = {
                domTemp: "#showTopicCensusDetail",
                target: ".content-wrapper__content__result-census__result-wrapper"
            }

            var infoMessageClass = {
                target: $('.content-wrapper__content__info'),
                message: 'content-wrapper__content__info__message',
                child: 'content-wrapper__content__info__child',
                icon: 'content-wrapper__content__info__icon'
            }

            var $originLink = $("a[title='privateTopic']");
            var $paginaionWrapper = $(".content-wrapper__pagination");
            var $listChangableArea = $(".content-wrapper__changeable");
            var $censusChangableArea = $(".content-wrapper__content__result-census");
            $originLink.addClass('active');

            getPageContent( originPage ).always(function(){
                var totalPages = window.totalPages;
                var visiblePages = utilGetVisiblePages( totalPages );
                $('#pagination').twbsPagination({
                    totalPages : totalPages, // need backend data totalPage
                    visiblePages : visiblePages, // set it.
                    onPageClick : function(event, page) {
                        // send page info here, something as ajax call
                        getPageContent(page);
                    },
                    first : '首页',
                    prev : '上一页',
                    next : '下一页',
                    last : '尾页'
                });
                eventBind();
                $(".form_datetime").datepicker();
            });

            function getDataOfPagination(data,page) {
                var page = page;
                var json = {};

                // data of pagination
                var resultList = []
                for(var index in data){
                    var obj = data[index];
                    var createTimeString = utilConvertTimeObject2String( obj.createTime);
                    var modifyTimeString = utilConvertTimeObject2String( obj.lastUpdateTime);
                    var resultElement = {
                            NO : page,
                            topicName : obj.issueName,
                            author : obj.creator,
                            createTime : createTimeString,
                            modifier : obj.lastOperator,
                            modifyTime : modifyTimeString
                    }
                    resultList.push(resultElement);
                }
                json.resultList = resultList;

                return json;
            }

            //==================main template========================
            function handleBarTemplate(tempDom, targetDom, context) {
                // 用jquery获取模板
                var source = $(tempDom).html();
                // 预编译模板
                var template = Handlebars.compile(source);

                // var html = template(context);
                // 输入模板
                $(targetDom).html(template(context));
            }

            //===================get data from mock or ajax===============

            function getPageContent(page) {
                var that = this;
                that.totalPages;
                that.$waitingMask = $(".waiting-mask");
                that.page = page;
                that.url = "http://218.199.92.27:8080/issue/queryOwnIssue";
                that.type = "POST";
                that.dataType = "json";
                that.contentType = "application/json";
                that.json = {
                    pageNo : page,
                    pageSize : 10,
                    issueName : $.trim($('#searchTopicName').val()),
                    user : $.trim($('#searchCreator').val()),
                    createStartTime : $.trim($('#searchStartTime').val()),
                    createEndTime : $.trim($('#searchEndTime').val()),
                    lastUpdateStartTime : $.trim($('#searchModifyStartTime').val()),
                    lastUpdateEndTime : $.trim($('#searchModifyEndTime').val()),
                };
                return $.ajax({
                    type : that.type,
                    url : that.url,
                    dateType : that.dateType,
                    contentType : that.contentType,
                    data : JSON.stringify(that.json),
                    beforeSend : function() {
                        that.$waitingMask.show();
                    },
                    success : function(data) {
                        that.totalPages = data.result.pageTotal;
                        if(data !== undefined && data !== ''){
                            if(data.status === 'OK'){
                                var resultList = getDataOfPagination(data.result.list,that.page);
                                handleBarTemplate(paginationDomTemp, paginationTarget,
                                        resultList);
                                that.$waitingMask.hide();
                            }else{
                                alert(data.result);
                            }
                        }
                    },
                    error : function() {
                        var resultList = mockDataOfPagination(1);
                        handleBarTemplate(paginationDomTemp, paginationTarget,
                                resultList);
                        that.$waitingMask.hide();
                    }
                })
            }

            // function for test
            function mockDataOfPagination(page) {
                var i;
                var page = page;
                var json = {};

                // mock data of pagination
                var resultList = []
                var resultElement = {
                    NO: page,
                    topicName: '白糖时代',
                    author: 'zhangsan',
                    createTime: '20160101',
                    modifier: 'lisi',
                    modifyTime: '20160101'
                }

                for( i = 1; i<9; i++  ){
                    resultList.push(resultElement);
                }

                json.resultList = resultList;

                return json;
            }

            function mockTopicCensusData(){
                var json = {
                    fileList: []
                };
                var file = {
                    topic: "小学女生上吊",
                    url: "http://xx.xx.xx",
                    time: "2011-11-11 11:11:11"
                }
                for( var i = 0 ; i<5; i++){
                    json.fileList.push(file);
                }
                return json;
            }

            function mockResultIntroData(){
                var json = {};
                var fileList = [];
                var file = {
                    fileNO: 1,
                    fileName: "文件名",
                    author: "作者",
                    time: "2011-11-11 11:11:11"
                }
                json.topicName = "多悦小学事件";
                json.author = "高岩";
                json.createTime = "2011-11-11 11:11:11";
                json.modifier = "高岩";
                json.modifyTime = "2011-11-11 11:11:11";
                json.fileList = [];
                for( i=0; i<5; i++ ){
                    json.fileList.push(file);
                }
                return json;
            }

            //===========================events function=============================

            function eventBind() {
                var $navigationUl = $(".content-wrapper__nav__ul");
                var $resultListArea = $("#contentSearchResultList");
                var $changeableArea = $(".content-wrapper__changeable");
                var $censusAreaNavigation = $(".content-wrapper__content__result-census__unit-list-wrapper__ul");
                $navigationUl.on("click", "a", onClickLeftSideBarNavLink);
                $resultListArea.on("click",".result-list", onClickResultListItem);
                $changeableArea.on("click", "#checkTopicCensus", onClickShowTopicCensus);
                $censusAreaNavigation.on("click", "a", onClickCensusNavigationBar);
            }

            /**
             * left sidebar click event
             */
            function onClickLeftSideBarNavLink(event){
                var $waitingMask = $(".waiting-mask");
                var $oldActiveElement = $(".active");
                $waitingMask.show();
                var $this = $(event.currentTarget);
                var title = $this.text();
                var $target = $('.content-wrapper__content__info');
                var $infoSpan = $('<span></span>');
                var $childSpan = $('<span></span');
                var $iconSpan = $('<span></span>');
                var infoClasses = {
                    message: 'content-wrapper__content__info__message',
                    child: 'content-wrapper__content__info__child',
                    icon: 'content-wrapper__content__info__icon',
                }



                var link = "url" + title;

                $target.text(title);
                $oldActiveElement.removeClass("active");
                $this.addClass("active");
                $waitingMask.hide();
            }

            /*
             * click result list item, show the topic introduction page.
             */
            function onClickResultListItem(event) {
                var $waitingMask = $(".waiting-mask");
                $paginaionWrapper.hide();
                $waitingMask.show();
                var mockData = mockResultIntroData();
                handleBarTemplate(topicIntroDomTemp, topicIntorTarget, mockData);
                appendContentInfoMessage( mockData.topicName );
                $("#topicFileList").on("click", ".content-wrapper__topic-intro__file-list__delete", onClickDeleteFile);
                $waitingMask.hide();
            }


            /**
             when click one item in the resultIntor fileList
             the item will be removed at the frontend first.
             */
            function onClickDeleteFile(event){
                var $this = $(this);
                var fileNO = $this.data('fileNo');
                var confirmDelete = confirm("do you want to delete this file?");
                if( confirmDelete ){
                    var parent = $($this.parents()[1]);
                    parent.remove();
                    alert("delete file " + fileNO);
                } else {
                    return;
                }
            }

            /**
             点击事件内的 查看结果统计按钮 响应事件
             */
            function onClickShowTopicCensus(){
                var $waitingMask = $(".waiting-mask");
                $censusChangableArea.show();
                $listChangableArea.hide();
                $paginaionWrapper.hide();
                $waitingMask.show();
                var mockData = mockTopicCensusData();
                handleBarTemplate(showCensus.domTemp, showCensus.target, mockData);
                appendContentInfoMessage( "统计结果展示" );
                $waitingMask.hide();
            }

            /*
             在结果统计页面上方navigation点击事件
             */
            function onClickCensusNavigationBar(){
                var $ul = $(".content-wrapper__content__result-census__unit-list-wrapper__ul");
                var $this = $(this);
                var title = $this.attr('title');
                var domTemp = $("#"+title);
                var link = $this.attr('href');
                var mockData = mockTopicCensusData();
                $censusChangableArea.show();
                $listChangableArea.hide();
                $paginaionWrapper.hide();
                $waitingMask.show();
                $ul.find(".active").removeClass("active");
                $(this.parentNode).addClass("active");
                handleBarTemplate(domTemp, showCensus.target, mockData);
                $waitingMask.hide();
            }


            //=================util functions=========================
            function utilGetVisiblePages( totalPages ){
                if( totalPages < 7 ){
                    return totalPages;
                } else {
                    return 7;
                }
            }

            function utilConvertTimeObject2String( docTime ){
                var year = 1900 + docTime.year;
                var month = docTime.month;
                var day = docTime.date;
                var hour = docTime.hours;
                var minute = docTime.minutes;
                var second = docTime.seconds;

                return year + "年" + month + "月" + day + "日 " + hour + ":" + minute + ":" + second;
            }

            // 创建info-message中的span
            function utilCreateInfoSpan( text, className, isATagNeeded, extraParam){
                var $span = $('<span></span>');
                var $a = $('<a></a>');
                $span.addClass(className);
                if( isATagNeeded ){
                    $a.text(text);
                    // if( extraParam.url ){
                    // 	$a.attr('href', extraParam.url);
                    // }

                    // if( extraParam.id ){
                    // 	$a.attr('data-item-id', extraParam.id);
                    // }
                    $span.append($a);
                } else {
                    $span.text(text);
                }
                return $span;
            }

            /*
             util function. change the lable on the top of result area.
             */
            function appendContentInfoMessage(str) {
                var $iconSpan = utilCreateInfoSpan('>>', infoMessageClass.icon, false);
                var $childSpan = utilCreateInfoSpan(str, infoMessageClass.child, true);
                infoMessageClass.target.append($iconSpan).append($childSpan);
            }

            function cleanContentInfoMessage(target) {

            }
        }
    );