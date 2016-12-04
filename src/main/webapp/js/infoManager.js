$(document)
        .ready(
                function() {
                    var isEventBinded = false;
                    var changableArea = ".content-wrapper__changeable";
                    var $changeableArea = $(changableArea);

                    var $originLink = $("#topicManagePrivateTopic");
                    $originLink.addClass('active');

                    var showCensus = {
                        domTemp : "#showTopicCensusDetail",
                        target : ".content-wrapper__content__result-census__result-wrapper"
                    };

                    var infoMessageClass = {
                        target : $('.content-wrapper__content__info'),
                        message : 'content-wrapper__content__info__message',
                        child : 'content-wrapper__content__info__child',
                        icon : 'content-wrapper__content__info__icon'
                    };

                    var $originLink = $("#originalLink");
                    var $paginaionWrapper = $(".content-wrapper__pagination");
                    var $censusChangableArea = $(".content-wrapper__content__result-census");

                    mainInit();

                    function mainInit(){
                        getPageContent(1);
                        $originLink.addClass('active');
                    }

                    // ==================main template========================
                    function handleBarTemplate(tempDom, targetDom, context) {
                        // 用jquery获取模板
                        var source = $(tempDom).html();
                        // 预编译模板
                        var template = Handlebars.compile(source);

                        // var html = template(context);
                        // 输入模板
                        $(targetDom).html(template(context));
                    }

                    // ===================get data from mock or ajax===============

                    function getPageContent(page) {
                        var that = this;
                        that.totalPages;
                        that.$waitingMask = $(".waiting-mask");
                        that.page = page;
                        that.url = "http://localhost:8080/issue/queryOwnIssue";
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
                            lastUpdateEndTime : $.trim($('#searchModifyEndTime').val())
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
                                var paginationDomTemp = "#paginationTemplate";
                                var paginationTarget = changableArea;
                                that.totalPages = data.result.pageTotal;
                                if (data !== undefined && data !== '') {
                                    if (data.status === 'OK') {
                                        var resultList = dataFormatTransfor(
                                                data.result.list, that.page);
                                        handleBarTemplate(paginationDomTemp,
                                                paginationTarget, resultList);
                                        that.$waitingMask.hide();
                                    } else {
                                        alert(data.result);
                                    }
                                }
                            },
                            error : function() {
                                var paginationDomTemp = "#paginationTemplate";
                                var paginationTarget = changableArea;
                                // 测试用,本地测试,真实数据无法请求到时,调用mock数据,不要删除!
                                that.totalPages = 10;
                                var resultList = mockDataOfPagination(1);
                                handleBarTemplate(paginationDomTemp,
                                        paginationTarget, resultList);
                                that.$waitingMask.hide();
                            },
                            complete: function() {
                                if( !isEventBinded ){
                                    eventBind();
                                }

                                $(".form_datetime").datepicker();

                                if( that.page == 1 ){
                                    paginationFunc(that.totalPages);
                                }

                            }
                        });
                    }

                    // !!!!暂时不要删除,只有在ajax请求真实数据有错时才会调用mock
                    function mockDataOfPagination(page) {
                        var i;
                        var page = page;
                        var json = {};

                        // mock data of pagination
                        var resultList = []
                        var resultElement = {
                            NO : page,
                            topicName : '白糖时代',
                            author : 'zhangsan',
                            createTime : '20160101',
                            modifier : 'lisi',
                            modifyTime : '20160101'
                        }

                        for (i = 1; i < 9; i++) {
                            resultList.push(resultElement);
                        }

                        json.resultList = resultList;

                        return json;
                    }

                    function mockTopicDetailData() {
                        var json = {};
                        var file = [];
                        json.topicName = '多悦小学';
                        json.author = '高岩';
                        json.createTime = '2011-11-1 11:11:11';
                        json.modifier = '高岩';
                        json.modifyTime = '2011-11-1 11:11:11';
                        json.topicDetialList = [];

                        for (var i = 0; i < 10; i++) {
                            file.fileId = 'fileId';
                            file.title = '12岁女生宿舍上吊身亡';
                            file.sourceSite = '新华网';
                            file.pubTime = '2011-11-1 11:11:11';
                            file.numbers = 260;
                            json.topicDetialList.push(file);
                        }

                        return json;
                    }

                    function mockTopicCensusData() {
                        var json = {
                            fileList : []
                        };
                        var file = {
                            topic : "小学女生上吊",
                            url : "http://xx.xx.xx",
                            time : "2011-11-11 11:11:11"
                        }
                        for (var i = 0; i < 5; i++) {
                            json.fileList.push(file);
                        }
                        return json;
                    }

                    function mockResultIntroData() {
                        var json = {};
                        var file = {
                            fileNO : 1,
                            fileName : "文件名",
                            author : "作者",
                            time : "2011-11-11 11:11:11"
                        }
                        json.topicName = "多悦小学事件";
                        json.author = "高岩";
                        json.createTime = "2011-11-11 11:11:11";
                        json.modifier = "高岩";
                        json.modifyTime = "2011-11-11 11:11:11";
                        json.fileList = [];
                        for (i = 0; i < 5; i++) {
                            json.fileList.push(file);
                        }
                        return json;
                    }

                    // ===========================events functions=============================

                    function eventBind() {
                        var $navigationUl = $(".content-wrapper__nav__ul");
                        var $changeableArea = $(changableArea);
                        var $censusAreaNavigation = $(".content-wrapper__content__result-census__unit-list-wrapper__ul");

                        $navigationUl.on("click", ".content-wrapper__nav__ul__element-child__el__link",
                                onClickLeftSideBarNavLink);
                        $changeableArea.on("click", "#searchSubmit",function(){
                            getPageContent(1);
                        });
                        $changeableArea.on("click", ".result-list",
                                onClickResultListItem);
                        $changeableArea.on("click", "#checkTopicDetail",
                                onClickCheckTopicDetail);
                        $changeableArea.on("click", ".topic-detail-result__result-list__item__message-wrapper",
                                onClickShowTopicCensus);
                        $changeableArea
                                .on(
                                        "click",
                                        ".topic-detail-result__operations__checkbox__delete",
                                        onClickDetailDeleteAll);
                        $changeableArea
                                .on(
                                        "click",
                                        ".topic-detail-result__operations__checkbox__btns__delete",
                                        onClickDeleteButton);
                        $changeableArea
                                .on(
                                        "click",
                                        ".topic-detail-result__operations__checkbox__btns__combine",
                                        onClickCombineButton);
                        $censusAreaNavigation.on("click", "a",
                                onClickCensusNavigationBar);
                        isEventBinded = true;
                    }

                    /**
                     * left sidebar click event
                     */
                    function onClickLeftSideBarNavLink(event) {
                        var $waitingMask = $(".waiting-mask");
                        var $oldActiveElement = $(".content-wrapper__nav .active");
                        $waitingMask.show();
                        var $this = $(event.currentTarget);
                        var tempDom = $this.data("handleBarTemplate");
                        var theTargetDom = $this.data("targetDom");
                        var $parent = $($($this.parents()[2]).children()[0])

                        var text = $.trim($this.text());
                        var parentText = $.trim($parent.text());
                        var params = {
                            firstText: parentText,
                            secondText: text
                        }
                        cleanContentInfoMessage(params);

                        $oldActiveElement.removeClass("active");
                        $this.addClass("active");

                        if( !tempDom ){
                            $(".content-wrapper__content__developing").show();
                            $changeableArea.hide();
                            $(".content-wrapper__content__result-census").hide();
                        } else {
                            if( tempDom === "#paginationTemplate" ){
                                $(".content-wrapper__content__result-census").hide();
                                getPageContent(1);
                            }

                            if( tempDom === "#createTopic" ){
                                $(".content-wrapper__content__result-census").hide();
                                showCreateTopicPage();
                            }
                            $(".content-wrapper__content__developing").hide();
                            $changeableArea.show();
                        }

                        $waitingMask.hide();
                    }

                    /*
                     * click result list item, show the topic introduction page.
                     */
                    function onClickResultListItem(event) {
                        var $target = $(event.currentTarget);
                        var issueId = $target.data('issueId');
                        var $waitingMask = $(".waiting-mask");
                        $paginaionWrapper.hide();
                        $.ajax({
                            type : 'POST',
                            url : 'http://localhost:8080/file/queryIssueFiles',
                            dataType : 'json',
                            data : {
                                issueId : issueId,
                            },
                            beforeSend : function(){
                                $waitingMask.show();
                            },
                            success : function(data){
                                var topicIntroDomTemp = "#topicIntroduction";
                                var topicIntorTarget = changableArea;
                                if(data !== undefined && data !== ''){
                                    if(data.status === 'OK'){
                                        var json ={};
                                        var resultList = data.result.list;
                                        json.fileList = [];
                                        for(var index in resultList){
                                            var obj = resultList[index];
                                            var time = utilConvertTimeObject2String(obj.uploadTime);
                                            var author = obj.creator;
                                            var fileName = obj.fileName;
                                            var fileNO = Number(index) + 1;
                                            var element = {
                                                    fileNO : fileNO,
                                                    fileName : fileName,
                                                    author : author,
                                                    time : time
                                            }
                                            json.fileList.push(element);
                                        }
                                        var issue = data.result.issue;
                                        json.topicName = issue.issueName;
                                        json.author = issue.creator;
                                        json.createTime = utilConvertTimeObject2String(issue.createTime);
                                        json.modifier = issue.lastOperator;
                                        json.modifyTime = utilConvertTimeObject2String(issue.lastUpdateTime);
                                        handleBarTemplate(topicIntroDomTemp, topicIntorTarget,
                                                json);
                                        appendContentInfoMessage(json.topicName);
                                        $waitingMask.hide();
                                    }else{
                                        alert(data.result);
                                    }
                                }
                            },
                            error : function(){
                                var topicIntroDomTemp = "#topicIntroduction";
                                var topicIntorTarget = changableArea;
                                var mockData = mockResultIntroData();
                                handleBarTemplate(topicIntroDomTemp, topicIntorTarget,
                                        mockData);
                                appendContentInfoMessage(mockData.topicName);
                                $waitingMask.hide();
                            },
                        });
                        $("#topicFileList")
                                .on(
                                        "click",
                                        ".content-wrapper__topic-intro__file-list__delete",
                                        onClickDeleteFile);
                        
                    }
                    
                    /**
                     * when click one item in the resultIntor fileList the item
                     * will be removed at the frontend first.
                     */
                    function onClickDeleteFile(event) {
                        var $this = $(this);
                        var fileNO = $this.data('fileNo');
                        var confirmDelete = confirm("do you want to delete this file?");
                        if (confirmDelete) {
                            var parent = $($this.parents()[1]);
                            parent.remove();
                            alert("delete file " + fileNO);
                        } else {
                            return;
                        }
                    }

                    /**
                     * 点击事件内 查看结果详情按钮 响应事件
                     */
                    function onClickCheckTopicDetail() {
                        var showResult = {
                            domTemp : "#topicDetailResult",
                            target : changableArea
                        };
                        var $waitingMask = $(".waiting-mask");
                        $censusChangableArea.hide();
                        $paginaionWrapper.hide();
                        $changeableArea.show();
                        $waitingMask.show();
                        $.ajax({
                            type : 'post',
                            url : 'http://localhost:8080/issue/queryModifiedOrigAndCountResult',
                            beforeSend : function() {
                                $waitingMask.show();
                            },
                            success : function(data){
                                if(data !== undefined && data !== ''){
                                    if(data.status === 'OK'){
                                        var json = {};
                                        var issue = data.result.issue;
                                        json.topicName = issue.issueName;
                                        json.author = issue.creator;
                                        json.createTime = utilConvertTimeObject2String(issue.createTime);
                                        json.modifier = issue.lastOperator;
                                        json.modifyTime = utilConvertTimeObject2String(issue.lastUpdateTime);
                                        json.topicDetialList = [];
                                        var list = data.result.list;
                                        for(var index in list){
                                            var obj = list[index];
                                            var ele = {
                                                    title : obj[2],
                                                    id : Number(index),
                                                    sourceSite : obj[1],
                                                    pubTime : obj[3],
                                                    numbers : obj[0]
                                            }
                                            json.topicDetialList.push(ele);
                                        }
                                        handleBarTemplate(showResult.domTemp,
                                                showResult.target, json);
                                            appendContentInfoMessage("查看话题详情");
                                    }else{
                                        alert(data.result);
                                    }
                                }
                            },
                            error : function(data){
                                var mockData = mockTopicDetailData();
                                handleBarTemplate(showResult.domTemp,
                                    showResult.target, mockData);
                                appendContentInfoMessage("查看话题详情");
                            }
                        });
                        $waitingMask.hide();
                    }

                    /**
                     * 点击事件内的 查看结果统计按钮 响应事件
                     */
                    function onClickShowTopicCensus(event) {
                        var $target = $(event.currentTarget);
                        var id = $target.data('indexId');
                        var $waitingMask = $(".waiting-mask");
                        $censusChangableArea.show();
                        $changeableArea.hide();
                        $paginaionWrapper.hide();
                        $.ajax({
                            type : 'post',
                            url : 'http://localhost:8080/issue/queryModifiedClusterResult',
                            beforeSend : function() {
                                $waitingMask.show();
                            },
                            data : {
                                currentset : Number(id) +1
                            },
                            success : function(data){
                                if(data !== undefined && data !== ''){
                                    if(data.status === 'OK'){
                                        var json = {
                                                fileList : []
                                            };
                                        var list = data.result.set;
                                        for(var index in list){
                                            var obj = list[index];
                                            var ele = {
                                                    topic : obj[1],
                                                    id : Number(index),
                                                    url : obj[0],
                                                    time : obj[2]
                                            }
                                            json.fileList.push(ele);
                                        }
                                        handleBarTemplate(showCensus.domTemp,
                                                showCensus.target, json);
                                        appendContentInfoMessage("统计结果展示");
                                    }else{
                                        alert(data.result);
                                    }
                                }
                            },
                            error : function(data){
                                var mockData = mockTopicCensusData();
                                handleBarTemplate(showCensus.domTemp,
                                        showCensus.target, mockData);
                                appendContentInfoMessage("统计结果展示");
                            }
                        });
                        $waitingMask.hide();
                    }

                    /**
                     * 查看结果页面(话题详情页面) 相关事件
                     */
                    function onClickDetailDeleteAll(event) {
                        alert('dfd');
                        var $target = $(event.currentTarget);
                        var $checkBoxes = $(".topic-detail-result__result-list__item__checkbox__delete");
                        var isChecked = false;

                        if ($target.is(":checked"))
                            isChecked = true;

                        $.each($checkBoxes, function() {
                            var $this = $(this);
                            $this.prop('checked', isChecked);
                        });
                    }

                    function onClickDeleteButton() {
                        var $checkBoxes = $(".topic-detail-result__result-list__item__checkbox__delete");
                        var deleteItems = [];

                        $.each($checkBoxes, function() {
                            var $this = $(this);
                            var fileId = $this.data('fileId');

                            if ($this.is(":checked")) {
                                deleteItems.push(fileId);
                                $($this.parents()[1]).remove();
                            }
                        });
                        alert("delete files : " + deleteItems.join(' '));
                        showNoFileMessage();
                    }

                    function onClickCombineButton() {
                        var $checkBoxes = $(".topic-detail-result__result-list__item__checkbox__delete");
                        var combineItems = [];
                        $.each($checkBoxes, function() {
                            var $this = $(this);
                            var fileId = $this.data('fileId');

                            if ($this.is(":checked")) {
                                combineItems.push(fileId);
                            }
                        })
                        alert("combine files : " + combineItems.join(' '));
                        showNoFileMessage();
                    }

                    function showNoFileMessage() {
                        var $items = $(".topic-detail-result__result-list__item");
                        if ($items.length === 0) {
                            $(
                                    ".topic-detail-result__result-list__no-file-message")
                                    .show();
                        } else {
                            $(
                                    ".topic-detail-result__result-list__no-file-message")
                                    .hide();
                        }
                    }

                    /*
                     * 在结果统计页面上方navigation点击事件
                     */
                    function onClickCensusNavigationBar() {
                        var $ul = $(".content-wrapper__content__result-census__unit-list-wrapper__ul");
                        var $this = $(this);
                        var title = $this.attr('title');
                        var domTemp = $("#" + title);
                        var link = $this.attr('href');
                        var mockData = mockTopicCensusData();
                        $censusChangableArea.show();
                        $changeableArea.hide();
                        $paginaionWrapper.hide();
                        $waitingMask.show();
                        $ul.find(".active").removeClass("active");
                        $(this.parentNode).addClass("active");
                        handleBarTemplate(domTemp, showCensus.target, mockData);
                        $waitingMask.hide();
                    }

                    /**
                     * 创建topic页面
                     */
                    function showCreateTopicPage() {
                        $waitingMask.show();
                        var tempDom = $("#createTopic");
                        handleBarTemplate(tempDom, $changeableArea, {});
                        $changeableArea.show();
                        $paginaionWrapper.hide();
                        $censusChangableArea.hide();
                        $waitingMask.hide();
                    }

                    // =================util functions=========================
                    function paginationFunc( tp ) {
                        var totalPages = tp;
                        var visiblePages = utilGetVisiblePages(totalPages);
                        $('#pagination').twbsPagination({
                            totalPages : totalPages, // need backend data
                            // totalPage
                            visiblePages : visiblePages, // set it.
                            onPageClick : function(event, page) {
                                // send page info here, something as ajax call
                                if(page === 1) return;
                                getPageContent(page);
                            },
                            first : '首页',
                            prev : '上一页',
                            next : '下一页',
                            last : '尾页'
                        });
                        $(".content-wrapper__pagination").show();
                        $($(".pagination").children()[2]).addClass("active");
                    }

                    function dataFormatTransfor(data, page) {
                        var page = page;
                        var json = {};

                        // data of pagination
                        var resultList = []
                        for ( var index in data) {
                            var obj = data[index];
                            var createTimeString = utilConvertTimeObject2String(obj.createTime);
                            var modifyTimeString = utilConvertTimeObject2String(obj.lastUpdateTime);
                            var NO = (Number(page) - 1) * 10 + Number(index) + 1;
                            var resultElement = {
                                NO : NO,
                                issueId : obj.issueId,
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

                    function utilGetVisiblePages(totalPages) {
                        if (totalPages < 7) {
                            return totalPages;
                        } else {
                            return 7;
                        }
                    }

                    function utilConvertTimeObject2String(docTime) {
                        var year = 1900 + docTime.year;
                        var month = docTime.month;
                        var day = docTime.date;
                        var hour = docTime.hours;
                        var minute = docTime.minutes;
                        var second = docTime.seconds;

                        return year + "年" + month + "月" + day + "日 " + hour
                                + ":" + minute + ":" + second;
                    }

                    // 创建info-message中的span
                    function utilCreateInfoSpan(text, className, isATagNeeded) {
                        var $span = $('<span></span>');
                        var $a = $('<a></a>');
                        $span.addClass(className);
                        if (isATagNeeded) {
                            $a.text(text);
                            $span.append($a);
                        } else {
                            $span.text(text);
                        }
                        return $span;
                    }

                    /*
                     * util function. change the lable on the top of result
                     * area.
                     */
                    function appendContentInfoMessage(str) {
                        var $iconSpan = utilCreateInfoSpan('>>',
                                infoMessageClass.icon, false);
                        var $childSpan = utilCreateInfoSpan(str,
                                infoMessageClass.child, false);
                        infoMessageClass.target.append($iconSpan).append(
                                $childSpan);
                    }

                    function cleanContentInfoMessage(params) {
                        var $target = $(".content-wrapper__content__info");
                        var firstText = params.firstText || "";
                        var secondText = params.secondText || "";
                        var className = "content-wrapper__content__info__child";
                        var iconClassName = "content-wrapper__content__info__icon";
                        var $messageSpan = utilCreateInfoSpan("当前位置:","content-wrapper__content__info__message", false);
                        var $firstSpan = utilCreateInfoSpan(firstText, className, false);
                        var $iconSpan = utilCreateInfoSpan(">>", iconClassName, false);
                        var $secondSpan = utilCreateInfoSpan(secondText, className, false);

                        $target.empty();
                        $target.append($messageSpan).append($firstSpan).append($iconSpan).append($secondSpan);
                    }
                });