<%--
  Created by IntelliJ IDEA.
  User: Andrew
  Date: 2017/8/8
  Time: 13:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <jsp:include page="../Common/include.jsp"/>
    <script src="/Javascript/echarts.js"></script>
    <script src="/Javascript/infographic.js"></script>
    <link href="/Css/chartsReport.css" rel="stylesheet"/>
    <title>数据库巡检报告</title>
</head>
<body>
<div class="container">
    <div id="mainReport" class="row">
        <h1 class="page-header">数据库巡检报告</h1>
        <small>数据来源时间段：${BaseInfo.startTime}~${BaseInfo.endTime}</small>
        <h3 class="sub-header" style="color: #336699">基本信息</h3>
        <c:if test="${!empty BaseInfo}">
        <div class="row">
            <div class="col-md-3">
                数据库名称：${BaseInfo.DATABASE_NAME}
            </div>
            <div class="col-md-2">
                数据库版本：${BaseInfo.VERSION}
            </div>
        </div>
        <div class="row">
            <div class="col-md-3">
                操作系统类型：${BaseInfo.OS_TYPE}
            </div>
            <div class="col-md-4">
                操作系统版本：${BaseInfo.oslevel}
            </div>
        </div>
        <div class="row">
            <div class="col-md-3">
                逻辑CPU数目：${BaseInfo.cpusize}
            </div>
            <div class="col-md-3">
                物理内存大小(MB)：${BaseInfo.memsize}
            </div>
            <div class="col-md-3">
                Swap内存大小(MB)：${BaseInfo.swapsize}
            </div>
        </div>
        </c:if>
        <h3 class="sub-header" style="color: #336699">1、操作系统检查</h3>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                1) 操作系统版本 OS Version(检查是否EOS):
            </div>
        </div>
        <div class="row">
            <div class="col-md-6">
                ${BaseInfo.hostname}:${BaseInfo.oslevel}
            </div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                2) 文件系统使用情况
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：如果文件系统使用率大于75%，则State检查结果为False，并且状态为红色</caption>
                <thead>
                <tr>
                    <th>Filesystem</th>
                    <th>MB Block</th>
                    <th>Used</th>
                    <th>Available</th>
                    <th>Use%</th>
                    <th>MountPoint</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${fss}" var="fs">
                    <c:choose>
                        <c:when test="${fn:replace(fs[4],'%','')<75}">
                            <tr class="info">
                            <td>${fs[0]}</td>
                            <td>${fs[1]}</td>
                            <td>${fs[2]}</td>
                            <td>${fs[3]}</td>
                            <td>${fs[4]}</td>
                            <td>${fs[5]}</td>
                            <td>True</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr class="danger">
                            <td>${fs[0]}</td>
                            <td>${fs[1]}</td>
                            <td>${fs[2]}</td>
                            <td>${fs[3]}</td>
                            <td>${fs[4]}</td>
                            <td>${fs[5]}</td>
                            <td>False</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <c:if test="${BaseInfo.swapsize!='unknow'}">
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                3) Swap交换区大小使用情况检查：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：如果Swap交换区使用率大于20%，则State检查结果为False，并且状态为红色</caption>
                <thead>
                <tr>
                    <th>总大小(MB)</th>
                    <th>已使用(MB)</th>
                    <th>空闲(MB)</th>
                    <th>使用率</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${BaseInfo.swapsize}</td>
                    <td>${BaseInfo.swapsize-BaseInfo.swapfree}</td>
                    <td>${BaseInfo.swapfree}</td>
                    <td>${(BaseInfo.swapsize-BaseInfo.swapfree)/BaseInfo.swapsize}%</td>
                    <c:choose>
                        <c:when test="${(BaseInfo.swapsize-BaseInfo.swapfree)/BaseInfo.swapsize<0.2}">
                            <td class="info">True</td>
                        </c:when>
                        <c:otherwise>
                            <td class="danger">False</td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                </tbody>
            </table>
        </div>
        </c:if>
        <c:if test="${!empty ulimits}">
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                4) 实例用户limit设置检查：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：</caption>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Soft</th>
                    <th>Hard</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="ulimit" items="${ulimits}">
                    <tr>
                        <td>${ulimit[0]}</td>
                        <td>${ulimit[1]}</td>
                        <td>${ulimit[2]}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        </c:if>
        <h3 class="sub-header" style="color: #336699">2、数据库检查</h3>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                1) 表空间使用情况检查：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：如果tbsp.TBSP_STATE!='NORMAL'||(tbsp.TBSP_TYPE=='DMS'&&tbsp.TBSP_UTILIZATION_PERCENT>75&&tbsp.TBSP_MAX_SIZE>0)，则State检查结果为False，并且状态为红色</caption>
                <thead>
                <tr>
                    <th>表空间名称</th>
                    <th>表空间类型</th>
                    <th>状态</th>
                    <th>Total(KB)</th>
                    <th>Used(KB)</th>
                    <th>Free(KB)</th>
                    <th>Percent</th>
                    <th>Auto Resize</th>
                    <th>MaxSize</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="tbsp" items="${tbspace}">
                    <c:choose>
                        <c:when test="${tbsp.TBSP_STATE!='NORMAL'||(tbsp.TBSP_TYPE=='DMS'&&tbsp.TBSP_UTILIZATION_PERCENT>75&&tbsp.TBSP_MAX_SIZE>0)}">
                            <tr class="danger">
                        </c:when>
                        <c:otherwise>
                            <tr class="info">
                        </c:otherwise>
                    </c:choose>
                    <td>${tbsp.TBSP_NAME}</td>
                    <td>${tbsp.TBSP_TYPE}</td>
                    <td>${tbsp.TBSP_STATE}</td>
                    <td>${tbsp.TBSP_TOTAL_SIZE_KB}</td>
                    <td>${tbsp.TBSP_USED_SIZE_KB}</td>
                    <td>${tbsp.TBSP_FREE_SIZE_KB}</td>
                    <td>${tbsp.TBSP_UTILIZATION_PERCENT}</td>
                    <td>${tbsp.TBSP_AUTO_RESIZE_ENABLED==1? 'Enable':'Disable'}</td>
                    <td>${tbsp.TBSP_MAX_SIZE==-1? 'Ulimited':tbsp.TBSP_MAX_SIZE}</td>
                    <c:choose>
                        <c:when test="${tbsp.TBSP_STATE!='NORMAL'||(tbsp.TBSP_TYPE=='DMS'&&tbsp.TBSP_UTILIZATION_PERCENT>75&&tbsp.TBSP_MAX_SIZE>0)}">
                            <td>False</td>
                        </c:when>
                        <c:otherwise>
                            <td>True</td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                2) 缓冲池使用情况检查：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：如果缓冲池命中率低于75%，则State检查结果为False，并且状态为Warning</caption>
                <thead>
                <tr>
                    <th>Bufferpool_Name</th>
                    <th>PageSize</th>
                    <th>TotalSize</th>
                    <th>Self_Tuning</th>
                    <th>BP_Hit_Ratio</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="bp" items="${bpfs}">
                <c:choose>
                    <c:when test="${bp.BP_HIT_RATIO<0.75||bp.SELF_TUNING_ENABLED==1}">
                        <tr class="danger">
                    </c:when>
                    <c:otherwise>
                        <tr class="info">
                    </c:otherwise>
                </c:choose>
                    <td>${bp.BP_NAME}</td>
                    <td>${bp.PAGESIZE}</td>
                    <td>${bp.BP_CUR_BUFFSZ}</td>
                    <td>${bp.SELF_TUNING_ENABLED==1? 'Enable':'Disable'}</td>
                    <td>${bp.BP_HIT_RATIO}</td>
                    <c:choose>
                        <c:when test="${bp.BP_HIT_RATIO<0.75||bp.SELF_TUNING_ENABLED==1}">
                            <td>False</td>
                        </c:when>
                    <c:otherwise>
                        <td>True</td>
                    </c:otherwise>
                    </c:choose>
                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                3) 数据库平均每秒事物响应时间：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：数据库事务平均响应时间是衡量数据库性能和吞吐量的一个重要指标，下图中单位为ms，典型的高性能OLTP数据库的事务平均响应时间应处于100ms以下</div>
            <div id="rsptChart" class="chartDiv"></div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                4) 数据库平均每秒事物数目：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：在数据库事务平均响应时间比较稳定的情况下，数据库平均每秒事务数的多少就能直接反应数据库的繁忙程度</div>
            <div id="tpsChart" class="chartDiv" ></div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                5) 数据库并发执行应用数量：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：数据库当前活动连接数量在OLTP类型数据库中应该是保持一个比较低的水平，虽然数据库中可能存在上百个连接，但是由于都是短而快的事务，所以能够抓取到的当前活动连接数量是比较低的</div>
            <div id="concurrentChart" class="chartDiv" ></div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699;padding-top: 20px">
            6) 数据库平均每秒逻辑读行数：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：数据库平均每秒逻辑读反应的数据库的繁忙程度</div>
            <div id="avgLogReadsChart" class="chartDiv" ></div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                7) 数据库平均锁等待时间：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：数据库平均锁等待时间反应数据库整体的锁等待情况</div>
            <div id="avgLockWaitTimeChart" class="chartDiv" ></div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                8) 数据库锁升级情况：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：数据库锁升级反映了数据库是否发生过严重锁升级情况</div>
            <div id="lockEscalsChart" class="chartDiv" ></div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                9) 数据库每秒排序情况：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：数据库每秒排序情况反映了数据库排序的频繁程度</div>
            <div id="totalSortsChart" class="chartDiv" ></div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                10) 数据库每秒排序溢出情况：
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 chartCaption">说明：如果数据库排序溢出比较高，且保持比较长时间段，则可能需要调整SQL</div>
            <div id="sortOverflowChart" class="chartDiv" ></div>
        </div>
        <h3 class="sub-header" style="color: #336699">3、数据库Top SQL</h3>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                1) 平均执行时间前20的SQL：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：按照一段时间内SQL的执行情况进行倒序排序，排名前20的SQL如下</caption>
                <thead>
                <tr>
                    <th>SNAPTIME</th>
                    <th>NUM_EXECS</th>
                    <th>AVG_EXEC_TIME</th>
                    <th>AVG_ROW_READ</th>
                    <th>SQL_TEXT</th>
                </tr>
                </thead>
                <tbody id="topSlowSqlTbody">
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                2) 平均逻辑读前20的SQL：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：按照一段时间内SQL的逻辑读情况进行倒序排序，排名前20的SQL如下</caption>
                <thead>
                <tr>
                    <th>SNAPTIME</th>
                    <th>NUM_EXECS</th>
                    <th>AVG_EXEC_TIME</th>
                    <th>AVG_ROW_READ</th>
                    <th>SQL_TEXT</th>
                </tr>
                </thead>
                <tbody id="topRowsReadSqlTbody">
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                3) 平均锁等待时间前20的SQL：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-hover table-bordered table-condensed">
                <caption>说明：按照一段时间内SQL的锁等待时间进行倒序排序，排名前20的SQL如下</caption>
                <thead>
                <tr>
                    <th>SNAPTIME</th>
                    <th>NUM_EXECS</th>
                    <th>AVG_EXEC_TIME</th>
                    <th>AVG_LOCK_WAIT_TIME</th>
                    <th>SQL_TEXT</th>
                </tr>
                </thead>
                <tbody id="topLockWaitSqlTbody">
                </tbody>
            </table>
        </div>
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
<script type="text/javascript">

    function render(chart,data,title,ytitle) {

        var option = {
            title : {
                text: title,
                x:'center'
            },
            tooltip : {
                trigger: 'axis'
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
                        type : 'jpeg',
                        lang : ['点击保存']
                    }
                }
            },
            calculable : true,
            xAxis : [
                {
                    type : 'category',
                    boundaryGap : false,
                    data : data.xaxis,
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
                    }
                }
            ],
            series : [
                {
                    name:ytitle,
                    type:'line',
                    data:data.yaxis,
                    axisLine : {
                        show: true,
                        lineStyle: {
                            type: 'dashed',
                            width: 2
                        }
                    },
                    axisTick : {    // 轴标记
                        show:true,
                        length: 10,
                        lineStyle: {
                            color: 'green',
                            type: 'solid',
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

    function chartAjax(divid,url,title,ytitle) {
        var chart = echarts.init(document.getElementById(divid),'infographic');
        $.ajax({
            url:url+'?'+getParms(),
            async : true,
            type:'post',
            dataType:'json',
            beforeSend:function () {
                chart.showLoading();
            },
            success:function (data) {
                render(chart,data,title,ytitle);
            },
            error:function (msg) {
                alert(msg);
            },
            complete:function (data) {
                chart.hideLoading();
            }
        });
    }

    function getParms() {
        var query=location.search.substring(1);
        return query;
    }

    function tableAjax(divid,url,cols) {
        var tbody=window.document.getElementById(divid);
        $.ajax({
            url:url+'?'+getParms(),
            async : true,
            type:'post',
            dataType:'json',
            beforeSend:function () {
                //cl.show();
                var tr = document.createElement("tr");
                //var td= document.createElement("td");
                //tr.appendChild(td);
                tr.className="center-block";
                tr.innerHTML="Loading...Please Wait";
                tbody.appendChild(tr);
            },
            success:function (data) {
                var str = "";
                for(var i in data){
                    str += "<tr  style='word-break: break-all;font-size: small;'>";
                    for(var id in cols) {
                        str += "<td>" + data[i][cols[id]] + "</td>"
                    }
                    str += "</tr>";
                }
                tbody.innerHTML = str;
            },
            error:function (msg) {
                alert(msg);
            },
            complete:function (data) {
            }
        });
    }

    $(function () {
        chartAjax('rsptChart','/Home/getrspt','数据库事务平均响应时间','RSPT');
        chartAjax('tpsChart','/Home/gettps','数据库平均每秒事务数','TPS');
        chartAjax('concurrentChart','/Home/getconcurrent','数据库并发执行应用数量','并发执行数量');
        chartAjax('avgLogReadsChart','/Home/getavglogreads','数据库平均每秒逻辑读行数','数据库每秒逻辑读');
        chartAjax('avgLockWaitTimeChart','/Home/getavglockwaittime','数据库平均锁等待时间','数据库平均锁等待时间');
        chartAjax('lockEscalsChart','/Home/getlockescals','数据库锁升级情况','数据库锁升级次数');
        chartAjax('totalSortsChart','/Home/gettotalsorts','数据库平均排序情况','数据库平均排序次数');
        chartAjax('sortOverflowChart','/Home/getsortoverflow','数据库平均排序溢出','数据库平均排序溢出次数');

        tableAjax('topSlowSqlTbody','/Home/gettopslowsql',['SNAPTIME','NUM_EXEC_WITH_METRICS','AVG_EXEC_TIME','AVG_ROW_READ','SQL_TEXT']);
        tableAjax('topRowsReadSqlTbody','/Home/gettoprowsreadsql',['SNAPTIME','NUM_EXEC_WITH_METRICS','AVG_EXEC_TIME','AVG_ROW_READ','SQL_TEXT']);
        tableAjax('topLockWaitSqlTbody','/Home/gettoplockwaitsql',['SNAPTIME','NUM_EXEC_WITH_METRICS','AVG_EXEC_TIME','AVG_LOCK_WAIT_TIME','SQL_TEXT']);

    });
</script>
</body>
</html>