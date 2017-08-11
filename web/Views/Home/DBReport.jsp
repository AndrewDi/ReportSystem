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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>）
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <jsp:include page="../Common/include.jsp"/>
    <script src="/Javascript/echarts.js"></script>
    <script src="/Javascript/infographic.js"></script>
    <link href="/Css/chartsReport.css" rel="stylesheet"></link>
    <title>数据库巡检报告</title>
</head>
<body>
<div class="container">
    <div class="row">
        <h1 class="page-header">数据库巡检报告</h1>
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
            <table class="table table-bordered table-condensed">
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
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                3) Swap交换区大小使用情况检查：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-condensed">
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
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                4) 实例用户limit设置检查：
            </div>
        </div>
        <h3 class="sub-header" style="color: #336699">2、数据库检查</h3>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                1) 表空间使用情况检查：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-condensed">
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
            <table class="table table-bordered table-condensed">
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
                    <c:when test="${bp.BP_HIT_RATIO<0.75}">
                        <tr class="warn">
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
                        <c:when test="${bp.BP_HIT_RATIO<0.75}">
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
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
<script type="text/javascript">

    function rander(divid,data,title,ytitle) {

        var chart = echarts.init(document.getElementById(divid),'infographic');
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

    function getParms() {
        var query=location.search.substring(1);
        return query;
    }
    $(document).ready(function () {
        $.ajax({
            url:'/Home/getrspt?'+getParms(),
            async : true,
            type:'post',
            dataType:'json',
            success:function (data) {
                rander('rsptChart',data,'数据库事务平均响应时间','RSPT');
            },
            error:function (msg) {
                alert(msg);
            }
        });
        $.ajax({
            url:'/Home/gettps?'+getParms(),
            async : true,
            type:'post',
            dataType:'json',
            success:function (data) {
                rander('tpsChart',data,'数据库平均每秒事务数','TPS');
            },
            error:function (msg) {
                alert(msg);
            }
        });
        $.ajax({
            url:'/Home/getconcurrent?'+getParms(),
            async : true,
            type:'post',
            dataType:'json',
            success:function (data) {
                rander('concurrentChart',data,'数据库并发执行应用数量','并发执行数量');
            },
            error:function (msg) {
                alert(msg);
            }
        });
    });
</script>
</body>
</html>
