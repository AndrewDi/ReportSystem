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
    <link rel="stylesheet" href="/Css/bootstrap-editable.css">
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
       <div id="toolbar" class="btn-group">
           <button id="btn_add" type="button" class="btn btn-default">
               <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
           </button>
           <button id="btn_delete" type="button" class="btn btn-default">
               <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
           </button>
           <button id="btn_test" type="button" class="btn btn-default">
               <span class="glyphicon glyphicon-th-list" aria-hidden="true"></span>测试连通性
           </button>
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
    $('#btn_add').click(function () {
        var index = $('#userTable').bootstrapTable('getData').length;
        $('#userTable').bootstrapTable('insertRow', {
            index: index,
            row: {
                id: index+1
            }
        });
    });
    $('#btn_delete').click(function () {
        var ids = $.map($('#userTable').bootstrapTable('getSelections'),function (row) {
            return row.id;
        });
        if (ids.length ==0 ) {
            alert("请选择一行删除!");
            return;
        };
        $.each(ids,function (index,value) {
            //添加ajax删除行
        });
        $('#userTable').bootstrapTable('remove', {
            field: 'id',
            values: ids
        });
    });

    $(function () {
        var cols=[
            {
                checkbox:true
            },
            {
                field : 'id',
                title : 'ID',
                sortable : true
            },
            {
                field : 'host',
                title : 'Host',
                sortable : true,
                editable:{
                    mode:'inline',
                    type: 'text',
                    disabled:false,
                    title: '主机名',
                    validate: function (v) {
                        if (!v) return '主机名不能为空';

                    }
                }
            },
            {
                field : 'userName',
                title : 'UserName',
                editable:{
                    mode:'inline',
                    type: 'text',
                    disabled:false,
                    title: '用户名',
                    validate: function (v) {
                        if (!v) return '用户名不能为空';

                    }
                }
            },
            {
                field : 'passwd',
                title : 'Passwd',
                editable:{
                    mode:'inline',
                    type: 'password',
                    disabled:false,
                    title: '密码',
                    validate: function (v) {
                        if (!v) return '密码不能为空';

                    }
                }
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
            toolbar: '#toolbar',
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
            },
            onEditableSave: function (field, row, oldValue, $el) {
                $.ajax({
                    type: "post",
                    url: "/Settings/editRemoteusers",
                    data: row,
                    dataType: 'json',
                    success: function (data, status) {
                        if (status == "success") {
                            alert('编辑成功');
                        }
                    },
                    error: function () {
                        alert('编辑失败');
                    },
                    complete: function () {

                    }
                });
            }
        });
    })
</script>
</body>
</html>