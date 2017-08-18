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
    <link href="/Css/sidebar.css" rel="stylesheet">
    <link rel="stylesheet" href="/Css/bootstrap-table.css">
    <title>编辑SSH用户</title>
</head>

<body>
<jsp:include page="../Common/topbar.jsp"/>

<div class="container-fluid">
   <div class="row">
       <jsp:include page="../Common/navbar.jsp"/>
       <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
           <h1 class="page-header">编辑SSH用户</h1>
           <table id="userTable"></table>
       </div>
   </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
<script src="/Javascript/bootstrap-table.js"></script>
<script src="/Javascript/bootstrap-table-zh-CN.js"></script>
<script src="/Javascript/extensions/export/bootstrap-table-export.js"></script>
<script src="/Javascript/tableExport.js"></script>
<script src="/Javascript/bootstrap-editable.js"></script>
<script src="/Javascript/extensions/editable/bootstrap-table-editable.js"></script>
<script type="text/javascript">
    $(function () {
        var cols=[
            {
                field : 'id',
                title : 'ID',
                sortable : true
            },
            {
                field : 'host',
                title : 'Host',
                sortable : true,
                editable:true,
                placeholder:'Required'
            },
            {
                field : 'userName',
                title : 'UserName',
                editable:true
            },
            {
                field : 'passwd',
                title : 'Passwd',
                editable:true
            }
        ];
        $('#userTable').bootstrapTable('destroy');
        $('#userTable').bootstrapTable('showLoading');
        $('#userTable').bootstrapTable({
            cache: false,
            url: '/Settings/getremoteusers',
            classes: 'table table-hover',
            columns: cols,
            sortable: true,
            search : true,
            searchOnEnterKey : true,
            showColumns : true,
            showRefresh : true,
            pagination : false,
            switchable : true,
            idField : 'ID',
            locale : 'zh-CN',
            showExport: true,
            exportDataType: "all",
            exportTypes: ['json', 'txt','csv', 'sql', 'excel'],
            exportOptions: {fileName:'RemoteUsers'},
            sortStable:true,
            pageSize:10,
            showPaginationSwitch:true,
            pagination: true,
            onLoadSuccess: function(){  //加载成功时执行
                $('#userTable').bootstrapTable('hideLoading');
            },
            onLoadError: function(){  //加载失败时执行
                layer.msg("加载数据失败", {time : 1500, icon : 2});
            }
        });
    })
</script>
</body>
</html>