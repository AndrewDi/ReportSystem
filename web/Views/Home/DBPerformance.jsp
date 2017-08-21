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
                    <label for="startTimeInput" class="control-label" style="padding-left: 10px;">开始时间</label>
                    <input class="form-control" id="startTimeInput" placeholder="开始时间" required>
                </div>
                <div class="form-group">
                    <label for="endTimeInput" class="control-label" style="padding-left: 10px;">结束时间</label>
                    <input class="form-control" id="endTimeInput" placeholder="结束时间" required>
                </div>
                <button type="submit" id="generateReportBtn" class="btn btn-primary" style="padding-left: 10px;">生成报告</button>
            </form>
            <div class="row" id="mainReport" style="visible:false">

            </div>
        </div>
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
<link href="/Css/bootstrap-datetimepicker.css" rel="stylesheet">
<script src="/Javascript/moment.js"></script>
<script src="/Javascript/bootstrap-datetimepicker.js"></script>
<script type="text/javascript">
    function getParms() {
        var query=location.search.substring(1);
        return query;
    }

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
    
    $('#generateReportBtn').onclick(function (event) {
        var startTime=$('#startTimeInput').val();
        var endTime=$('#endTimeInput').val();
    })
</script>
</body>
</html>