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
</head>

<body>
<jsp:include page="../Common/topbar.jsp"/>

<div class="container-fluid">
   <div class="row">
       <jsp:include page="../Common/navbar.jsp"/>
       <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
           <h1 class="page-header">Dashboard</h1>
       </div>
   </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
</body>
</html>