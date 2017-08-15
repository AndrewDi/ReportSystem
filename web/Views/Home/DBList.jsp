<%--
  Created by IntelliJ IDEA.
  User: Andrew
  Date: 2017/7/30
  Time: 13:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>数据库列表</title>
    <jsp:include page="../Common/include.jsp"/>
    <link href="/Css/dashboard.css" rel="stylesheet">
</head>

<body>
<jsp:include page="../Common/topbar.jsp"/>

<div class="container-fluid">
    <div class="row">
        <jsp:include page="../Common/navbar.jsp"/>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <h1 class="page-header">数据库列表</h1>
            <div class="table-responsive">
                <c:if test="${!empty data}">
                <table class="table table-hover table-bordered table-condensed">
                    <thead>
                    <th>DBCONN_INT</th>
                    <th>DATABASE_NAME</th>
                    <th>INSTANCE_NAME</th>
                    <th>HOST</th>
                    <th>PORT</th>
                    <th>OS_TYPE</th>
                    <th>VERSION</th>
                    <th>Install_PATH</th>
                    <th class="text-center">操作</th>
                    </thead>
                    <tbody>
                    <c:forEach items="${data}" var="db">
                        <c:if test="${!empty db.DBCONN_INT}">
                        <tr>
                            <td>${db.DBCONN_INT}</td>
                            <td>${db.DATABASE_NAME}</td>
                            <td>${db.INSTANCE_NAME}</td>
                            <td>${db.HOST}</td>
                            <td>${db.PORT}</td>
                            <td>${db.OS_TYPE}</td>
                            <td>${db.VERSION}</td>
                            <td>${db.INSTANCE_PATH}</td>
                            <td class="text-center">
                                <button class="btn btn-default" type="button" data-dbname="${db.DATABASE_NAME}"  data-dbint="${db.DBCONN_INT}" data-toggle="modal" data-target="#dateTimeModal">生成报告</button>
                                <button class="btn btn-primary" type="button">合规检查</button>
                                <button class="btn btn-primary" type="button">下载报告</button>
                            </td>
                        </tr>
                        </c:if>
                    </c:forEach>
                    </tbody>
                </table>
                </c:if>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="dateTimeModal" tabindex="-1" role="dialog" aria-labelledby="dateTimeModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="dateTimeModalLabel">请选择巡检的时间范围</h4>
            </div>
            <div class="modal-body">
                    <div class="row">
                        <div class="col-sm-12">
                            <label class="checkbox-inline">
                                <input type="radio" name="dateTimeRadio" value="month"> 近一个月
                            </label>
                            <label class="checkbox-inline">
                                <input type="radio" name="dateTimeRadio" value="twoweek"> 近两周
                            </label>
                            <label class="checkbox-inline">
                                <input type="radio" name="dateTimeRadio" value="week"> 近一周
                            </label>
                            <label class="checkbox-inline">
                                <input type="radio" name="dateTimeRadio" value="twoday"> 近两天
                            </label>
                            <label class="checkbox-inline">
                                <input type="radio" name="dateTimeRadio" value="day" checked> 近一天
                            </label>
                            <label class="checkbox-inline">
                                <input type="radio" name="dateTimeRadio" value="manual"> 自定义
                            </label>
                        </div>
                    </div>
                <br/>
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="startTimeInput" class="col-sm-2 control-label">开始时间</label>
                        <div class="col-sm-6">
                            <input class="form-control" id="startTimeInput" placeholder="开始时间" disabled>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="endTimeInput" class="col-sm-2 control-label">结束时间</label>
                        <div class="col-sm-6">
                            <input class="form-control" id="endTimeInput" placeholder="结束时间" disabled>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" id="modal-submit" class="btn btn-primary" onclick="onModalSubmit(this)" >确定</button>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
<link href="/Css/bootstrap-datetimepicker.css" rel="stylesheet">
<script src="/Javascript/moment.js"></script>
<script src="/Javascript/bootstrap-datetimepicker.js"></script>
<script type="text/javascript">

    $('#dateTimeModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var dbname = button.data('dbname');
        var dbint=button.data('dbint');
        var modal = $(this);
        $('#modal-submit').attr("data-dbname",dbname);
        $('#modal-submit').attr("data-dbint",dbint);
    });

    $('input:radio[name="dateTimeRadio"]').change(function (e) {
       var radioValue= $(e.target).val();
       var endTimeInput= $('#endTimeInput');
       var startTimeInput = $('#startTimeInput');
       if(radioValue=="manual"){
           endTimeInput.removeAttr("disabled");
           startTimeInput.removeAttr("disabled");
       }else {
           endTimeInput.attr("disabled","disabled");
           startTimeInput.attr("disabled","disabled");
       }
    });
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

    function onModalSubmit(e) {
        var dbname=e.getAttribute("data-dbname");
        var dbint=e.getAttribute("data-dbint");
        var startTime="";
        var endTime="";
        var radioVal = $('input:radio[name="dateTimeRadio"]:checked').val();
        var dateFormat='YYYY-MM-DD-HH.mm.ss';
        switch (radioVal){
            case "manual":
                startTime=$('#startTimeInput').val();
                endTime=$('#endTimeInput').val();
                break;
            case "month":
                startTime=moment().subtract(1, 'months').format(dateFormat);
                endTime=moment().format(dateFormat);
                break;
            case "twoweek":
                startTime=moment().subtract(14, 'days').format(dateFormat);
                endTime=moment().format(dateFormat);
                break;
            case "week":
                startTime=moment().subtract(7, 'days').format(dateFormat);
                endTime=moment().format(dateFormat);
                break;
            case "twoday":
                startTime=moment().subtract(2, 'days').format(dateFormat);
                endTime=moment().format(dateFormat);
                break;
            case "day":
                startTime=moment().subtract(1, 'days').format(dateFormat);
                endTime=moment().format(dateFormat);
                break;
        };
        console.log('dbname:'+dbname);
        console.log('id:'+dbint);
        console.log('startTime:'+startTime);
        console.log('endTime:'+endTime);
        window.open("/Home/DBReport?DBName="+dbname+"&DBConnID="+dbint+"&StartTime="+startTime+"&EndTime="+endTime);
        $('#dateTimeModal').modal('hide');
    };
</script>
</body>
</html>