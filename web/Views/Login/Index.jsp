<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <meta name="description" content="">
    <meta name="author" content="">
    <!--<link rel="icon" href="../../favicon.ico"> -->

    <title>登陆Report System</title>

    <!-- Bootstrap core CSS -->
    <link href="/Css/bootstrap.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="/Css/signin.css" rel="stylesheet">

</head>

<body>

<div class="container">
    <form class="form-signin"  action="/Login" method="post">
        <h2 class="form-signin-heading">请登陆</h2>
        <label for="InputUserName" class="sr-only">用户名</label>
        <input id="InputUserName" name="InputUserName" class="form-control" placeholder="用户名" required autofocus>
        <label for="inputPassword" class="sr-only">密码</label>
        <input type="password" id="inputPassword" name="inputPassword" class="form-control" placeholder="密码" required>
        <div class="checkbox">
            <label>
                <input type="checkbox" value="remember-me"> 记住我
            </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
    </form>

</div> <!-- /container -->

    <c:if test="${!empty error}">
        <script type="text/javascript">
            alert("${error}");
        </script>
    </c:if>

</body>
</html>