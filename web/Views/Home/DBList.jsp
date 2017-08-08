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
                <table class="table table-hover table-condensed">
                    <thead>
                    <th>DBCONN_INT</th>
                    <th>DBCONN_NAME</th>
                    <th>操作</th>
                    </thead>
                    <tbody>
                    <c:forEach items="${data}" var="db">
                        <tr>
                            <td>${db.DBCONN_INT}</td>
                            <td>${db.DBCONN_NAME}</td>
                            <td>
                                <button class="btn btn-default" type="button" target="_blank">预览报告</button>
                                <button class="btn btn-primary" type="button">下载报告</button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                </c:if>
            </div>
        </div>
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
</body>
</html>