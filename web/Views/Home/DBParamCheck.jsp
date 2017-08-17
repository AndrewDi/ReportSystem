<%--
  Created by IntelliJ IDEA.
  User: Andrew
  Date: 2017/8/17
  Time: 11:10
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
    <link rel="stylesheet" href="/Css/bootstrap-table.css">
    <title>数据库合规检查</title>
</head>
<body>
<div class="container">
    <div class="row">
        <h1 class="page-header">数据库合规检查</h1>
        <h3 class="sub-header" style="color: #336699">1、数据库配置检查（DBCFG）</h3>
        <table id="dbcfgTable"></table>
        <h3 class="sub-header" style="color: #336699">2、数据库管理器配置检查（DBMCFG）</h3>
        <table id="dbmcfgTable"></table>
        <h3 class="sub-header" style="color: #336699">3、注册变量检查（DB2SET）</h3>
        <table id="db2setTable"></table>
        <h3 class="sub-header" style="color: #336699">4、自定义变量检查（Ext）</h3>
        <table id="extTable"></table>
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
<script src="/Javascript/bootstrap-table.js"></script>
<script src="/Javascript/bootstrap-table-zh-CN.js"></script>
<script src="/Javascript/extensions/export/bootstrap-table-export.js"></script>
<script src="/Javascript/extensions/toolbar/bootstrap-table-toolbar.js"></script>
<script src="/Javascript/extensions/natural-sorting/bootstrap-table-natural-sorting.js"></script>
<script type="text/javascript">
    function getParms() {
        var query=location.search.substring(1);
        return query;
    }

    function buildTable(divid,url) {
        var cols=[
            {
                field : 'id',
                title : 'ID'
            },
            {
                field : 'name',
                title : 'Name'
            },
            {
                field : 'rule',
                title : 'Rule'
            },
            {
                field : 'params',
                title : 'Current'
            },
            {
                field : 'required',
                title : 'Required'
            },
            {
                field : 'evalResult',
                title : 'EvalResult'
            },
            {
                field : 'fixResult',
                title : 'Recommand'
            }
        ];
        $('#'+divid).bootstrapTable({
            cache: false,
            url: url+'?'+getParms(),
            classes: 'table table-hover ',
            columns: cols,
            sortable: true,
            search : true,
            searchOnEnterKey : true,
            showColumns : true,
            showRefresh : true,
            pagination : false,
            switchable : true,
            idField : 'id',
            locale : 'zh-CN',
            showExport: true,
            exportDataType: "all",
            pagination: true,
            rowStyle: function (row, index) {
                //这里有5个取值代表5中颜色['active', 'success', 'info', 'warning', 'danger'];
                var strclass = "";
                if (row.evalResult == false) {
                    strclass = 'danger';//还有一个active
                }
                else if (row.evalResult == true) {
                    strclass = 'info';
                }
                else {
                    strclass = 'warning';
                }
                return { classes: strclass }
            },
        });
    }
    $(function () {
        buildTable('dbcfgTable','/Rule/dbcfgcheck');
        buildTable('dbmcfgTable','/Rule/dbmcfgcheck');
        buildTable('db2setTable','/Rule/db2setcheck');
    })
</script>
</body>
</html>
