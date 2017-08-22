<%--
  Created by IntelliJ IDEA.
  User: Andrew
  Date: 2017/7/30
  Time: 13:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <jsp:include page="../Common/include.jsp"/>
    <script src="/Javascript/echarts.js"></script>
    <script src="/Javascript/infographic.js"></script>
    <link href="/Css/chartsReport.css" rel="stylesheet"/>
    <link href="/Css/sidebar.css" rel="stylesheet">
    <title>数据库性能容量报告</title>
</head>

<body>
<jsp:include page="../Common/topbar.jsp"/>

<div class="container-fluid">
    <div class="row">
        <jsp:include page="../Common/navbar.jsp"/>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">数据库性能容量报告</h1>
            <h3 class="sub-header" style="color: #336699">选择报告时间段</h3>
            <form class="form-inline">
                <div class="form-group">
                    <label for="startTimeInput" class="control-label" >开始时间</label>
                    <input class="form-control" id="startTimeInput" placeholder="开始时间" required>
                </div>
                <div class="form-group">
                    <label for="endTimeInput" class="control-label" style="padding-left: 10px;">结束时间</label>
                    <input class="form-control" id="endTimeInput" placeholder="结束时间" required>
                </div>
                <button type="submit" id="generateReportBtn" class="btn btn-primary" style="padding-left: 10px;">生成报告</button>
                <button type="button" id="exportReportBtn" class="btn btn-default" style="padding-left: 10px;" disabled>导出报告</button>
            </form>
            <div class="row hidden" id="mainReport">
                <br/>
                <div class="row">
                    <div class="col-md-6 title"  style="color: #336699;padding-top: 40px">
                        1) 数据库平均每秒事物响应时间：
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 chartCaption">说明：数据库事务平均响应时间是衡量数据库性能和吞吐量的一个重要指标，下图中单位为ms，典型的高性能OLTP数据库的事务平均响应时间应处于100ms以下，这里我们仅关注DCOMDB、NSBPMDB、NSEPSPDB和RTSPDB数据库。</div>
                    <div id="rsptChart" class="chartDiv"></div>
                </div>
                <div class="row">
                    <div class="col-md-6 title"  style="color: #336699;padding-top: 80px">
                        2) 数据库平均每秒事务数量：
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 chartCaption">说明：在数据库事务平均响应时间比较稳定的情况下，数据库平均每秒事务数的多少就能直接反应数据库的繁忙程度，这里我们仅关注DCOMDB、NSBPMDB、NSEPSPDB和RTSPDB数据库。</div>
                    <div id="tpsChart" class="chartDiv"></div>
                </div>
                <div class="row">
                    <div class="col-md-6 title"  style="color: #336699;padding-top: 80px">
                        3) 数据库表空间使用率：
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 chartCaption">说明：数据库的表空间使用率能反应数据库实际使用率与数据库设计容量之间的关系，如果表空间使用率过高，则需要考虑是否需要扩容，这里我们仅关注DCOMDB、NSBPMDB、NSEPSPDB和RTSPDB数据库。</div>
                    <div id="tbspChart" class="chartDiv"></div>
                </div>
                <div class="row">
                    <div class="col-md-6 title"  style="color: #336699;padding-top: 80px">
                        4) 数据库内存使用量：
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 chartCaption">说明：数据库的内存使用量是一个反应了数据库是否发生过内存泄露，是否存在应用连接数目剧烈变动的指标。</div>
                    <div id="memUsedChart" class="chartDiv"></div>
                </div>
                <div class="row">
                    <div class="col-md-6 title"  style="color: #336699;padding-top: 80px">
                        5) 数据库当前活动连接数量：
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 chartCaption">说明：数据库当前活动连接数量在OLTP类型数据库中应该是保持一个比较低的水平，虽然数据库中可能存在上百个连接，但是由于都是短而快的事务，所以能够抓取到的当前活动连接数量是比较低的。</div>
                    <div id="concurrentChart" class="chartDiv"></div>
                </div>
                <div class="row">
                    <div class="col-md-6 title"  style="color: #336699;padding-top: 80px">
                        6) 数据库缓冲池命中率：
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-12 chartCaption">说明：数据库缓冲池命中率反应了数据库的缓冲池的效率问题，比较高的命中率表明缓冲池的设计大小合理，使用方面也是比较合理的。</div>
                    <div id="bpfHitRatioChart" class="chartDiv"></div>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
<link href="/Css/bootstrap-datetimepicker.css" rel="stylesheet">
<script src="/Javascript/moment.js"></script>
<script src="/Javascript/bootstrap-datetimepicker.js"></script>
<script type="text/javascript">
    $(function () {
        var timePickerOption={
            format: "YYYY-MM-DD-HH.mm.ss",
            minDate: moment().subtract(3, 'months'),
            maxDate: moment(),
            showTodayButton: true,
            showClear: true,
            showClose: true
        };
        $('#startTimeInput').datetimepicker(timePickerOption);
        $('#endTimeInput').datetimepicker(timePickerOption);
    });

    function render(chart,data,title,maxvalue,ygap) {

        var option = {
            title : {
                text: title,
                x:'center'
            },
            tooltip : {
                trigger: 'axis'
            },
            legend:{
                show:true,
                tooltip:{
                    show:true
                },
                data:['DCOMDB','NSEPSPDB','RTSPDB','NSBPMDB'],
                orient:'horizontal',
                y:'bottom'
            },
            toolbox: {
                show : true,
                feature : {
                    mark : {show: true},
                    dataZoom : {
                        show : true,
                        title : {
                            dataZoom : '区域缩放',
                            dataZoomReset : '区域缩放后退'
                        }
                    },
                    dataView : {show: true, readOnly: true},
                    magicType : {show: false, type: ['line','bar']},
                    restore : {
                        show : true,
                        title : '还原'
                    },
                    saveAsImage : {
                        show : true,
                        title : '保存为图片',
                        type : 'png',
                        lang : ['点击保存']
                    }
                }
            },
            calculable : true,
            xAxis : [
                {
                    type : 'category',
                    boundaryGap : false,
                    data : data.SNAPTIME,
                    name : '时间轴',
                    axisTick : {
                        show:true,
                        interval : 5
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value',
                    axisLabel : {
                        formatter: '{value}'
                    },
                    axisLine : {    // 轴线
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            width: 2
                        }
                    },
                    max:maxvalue,
                    interval:ygap
                }
            ],
            series : [
                {
                    name:'DCOMDB',
                    type:'line',
                    data:data.DCOMDB,
                    axisLine : {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            width: 2
                        }
                    }
                },
                {
                    name:'NSEPSPDB',
                    type:'line',
                    data:data.NSEPSPDB,
                    axisLine : {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            width: 2
                        }
                    }
                },
                {
                    name:'RTSPDB',
                    type:'line',
                    data:data.RTSPDB,
                    axisLine : {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            width: 2
                        }
                    }
                },
                {
                    name:'NSBPMDB',
                    type:'line',
                    data:data.NSBPMDB,
                    axisLine : {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            width: 2
                        }
                    }
                }
            ]
        };
        chart.setOption(option);

        window.addEventListener('resize', function () {
            chart.resize();
        });
    };

    function chartAjax(divid,url,params,title,maxvalue,ygap) {
        echarts.dispose(document.getElementById(divid));
        var chart = echarts.init(document.getElementById(divid),'infographic');
        $.ajax({
            url:url+'?'+params,
            async : true,
            type:'post',
            dataType:'json',
            beforeSend:function () {
                chart.showLoading();
            },
            success:function (data) {
                render(chart,data,title,maxvalue,ygap);
            },
            error:function (msg) {
                alert(msg);
            },
            complete:function (data) {
                chart.hideLoading();
            }
        });
    }

    $('#generateReportBtn').click(function (event) {
        event.preventDefault();
        var startTime=$('#startTimeInput').val();
        var endTime=$('#endTimeInput').val();
        var params='StartTime='+startTime+'&EndTime='+endTime;
        $('#mainReport').attr('class','row'); //显示隐藏div
        chartAjax('rsptChart','/DBPerf/getrspt',params,'数据库事务平均响应时间',100,10);
        chartAjax('tpsChart','/DBPerf/gettps',params,'数据库平均每秒事务数量',null,null);
        chartAjax('tbspChart','/DBPerf/gettbsp',params,'数据库表空间使用率',null,null);
        chartAjax('memUsedChart','/DBPerf/getmemused',params,'数据库内存使用量',null,null);
        chartAjax('concurrentChart','/DBPerf/getconcurrent',params,'数据库当前活动连接数量',null,null);
        chartAjax('bpfHitRatioChart','/DBPerf/getbpfhitratio',params,'数据库缓冲池命中率',null,null);

        $('#exportReportBtn').removeAttr("disabled");
    });

    $('#exportReportBtn').click(function (event) {
        var charts=$.map($.find("div[class='chartDiv']"),function (data,index) {
            var chart=echarts.getInstanceByDom(document.getElementById(data.id));
            return chart.getDataURL("png");
        });
        var titles=$.map($.find("div[class*='title']"),function (data,index) {
            return data.innerText;
        });

        var captions=$.map($.find("div[class*='chartCaption']"),function (data,index) {
            return data.innerText;
        });
        var jsondata={
            charts: charts,
            titles: titles,
            captions: captions
        };
        $.ajax({
            url: '/DBPerf/downloadReport',
            type:'post',
            async:true,
            data: JSON.stringify(jsondata),
            contentType:'application/json',
            dataType:'json',
            success:function (data) {
                window.open("/Download/"+data);
            },
            error:function (msg) {
                console.log(msg);
            },
            complete:function (data) {
            }
        });
    });
</script>
</body>
</html>