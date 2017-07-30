<%--
  Created by IntelliJ IDEA.
  User: Andrew
  Date: 2017/7/30
  Time: 14:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=GBK"
         pageEncoding="GBK"%>
<%@ page import="java.lang.Exception"%>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%
    Exception e = (Exception)request.getAttribute("exception");
    out.print(e.getMessage());
%>
</body>
</html>
