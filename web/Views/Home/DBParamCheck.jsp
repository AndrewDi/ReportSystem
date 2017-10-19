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
<script src="/Javascript/tableExport.js"></script>
<script type="text/javascript">
    function funcUrl(name,value,type){
        var loca = window.location;
        var baseUrl = type==undefined ? loca.origin + loca.pathname + "?" : "";
        var query = loca.search.substr(1);
        // 如果没有传参,就返回 search 值 不包含问号
        if (name==undefined) { return query }
        // 如果没有传值,就返回要查询的参数的值
        if (value==undefined){
            var val = query.match(new RegExp("(^|&)"+ name +"=([^&]*)(&|$)"));
            return val!=null ? decodeURI(val[2]) : null;
        };
        var url;
        if (query=="") {
            // 如果没有 search 值,则返回追加了参数的 url
            url = baseUrl + name + "=" + value;
        }else{
            // 如果没有 search 值,则在其中修改对应的值,并且去重,最后返回 url
            var obj = {};
            var arr = query.split("&");
            for (var i = 0; i < arr.length; i++) {
                arr[i] = arr[i].split("=");
                obj[arr[i][0]] = arr[i][1];
            };
            obj[name] = value;
            url = baseUrl + JSON.stringify(obj).replace(/[\"\{\}]/g,"").replace(/\:/g,"=").replace(/\,/g,"&");
        };
        return url;
    }

    function getParms() {
        var query=location.search.substring(1);
        return query;
    }
    function getDBName() {
        var dbname = funcUrl("DBName");
        return dbname;
    }
    function buildTable(divid,url,title) {
        var cols=[
            {
                field : 'id',
                title : 'ID',
                sortable : true
            },
            {
                field : 'name',
                title : 'Name',
                sortable : true
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
                title : 'Required',
                sortable : true
            },
            {
                field : 'evalResult',
                title : 'EvalResult',
                sortable : true
            },
            {
                field : 'fixResult',
                title : 'Recommand'
            }
        ];
        $('#'+divid).bootstrapTable('destroy');
        $('#'+divid).bootstrapTable('showLoading');
        $('#'+divid).bootstrapTable({
            cache: false,
            url: url+'?'+getParms(),
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
            exportOptions: {fileName:title},
            sortStable:true,
            pageSize:10,
            showPaginationSwitch:true,
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
            onLoadSuccess: function(){  //加载成功时执行
                $('#'+divid).bootstrapTable('hideLoading');
            },
            onLoadError: function(){  //加载失败时执行
                layer.msg("加载数据失败", {time : 1500, icon : 2});
            }
        });
    }
    $(function () {
        buildTable('dbcfgTable','/Rule/dbcfgcheck',funcUrl("DBName")+'.dbcfg');
        buildTable('dbmcfgTable','/Rule/dbmcfgcheck',funcUrl("DBName")+'.dbmcfg');
        buildTable('db2setTable','/Rule/db2setcheck',funcUrl("DBName")+'.db2set');
        buildTable('extTable','/Rule/extcheck',funcUrl("DBName")+'.ext');
    })
</script>
</body>
</html>
