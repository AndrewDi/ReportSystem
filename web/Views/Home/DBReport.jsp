<%--
  Created by IntelliJ IDEA.
  User: Andrew
  Date: 2017/8/8
  Time: 13:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <jsp:include page="../Common/include.jsp"/>
    <script src="/Javascript/echarts-all.js"></script>
    <title>数据库巡检报告</title>
</head>
<body>
<div class="container">
    <div class="row">
        <h1 class="page-header">数据库巡检报告</h1>
        <h3 class="sub-header" style="color: #336699">基本信息</h3>
        <c:if test="${!empty BaseInfo}">
        <div class="row">
            <div class="col-md-3">
                数据库名称：${BaseInfo.DATABASE_NAME}
            </div>
            <div class="col-md-2">
                数据库版本：${BaseInfo.VERSION}
            </div>
        </div>
        <div class="row">
            <div class="col-md-3">
                操作系统类型：${BaseInfo.OS_TYPE}
            </div>
            <div class="col-md-4">
                操作系统版本：6100-07-02-1150
            </div>
        </div>
        <div class="row">
            <div class="col-md-3">
                逻辑CPU数目：32
            </div>
            <div class="col-md-3">
                物理内存大小(MB)：32768
            </div>
            <div class="col-md-3">
                Swap内存大小(MB)：32768
            </div>
        </div>
        </c:if>
        <h3 class="sub-header" style="color: #336699">1、操作系统检查</h3>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                1) 操作系统版本 OS Version(检查是否EOS):
            </div>
        </div>
        <div class="row">
            <div class="col-md-6">
                bkeeperdb1:6100-07-02-1150
            </div>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                2) 文件系统使用情况
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-condensed">
                <caption>说明：如果文件系统使用率大于75%，则State检查结果为False，并且状态为红色</caption>
                <thead>
                <tr>
                    <th>MountPoint</th>
                    <th>GB Block</th>
                    <th>Free</th>
                    <th>%Used</th>
                    <th>IUsed</th>
                    <th>%IUsed</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <tr class="info">
                    <td>/</td>
                    <td>10.00</td>
                    <td>2.00</td>
                    <td>49%</td>
                    <td>4312432</td>
                    <td>50%</td>
                    <td>True</td>
                </tr>
                <tr class="danger">
                    <td>/tmp</td>
                    <td>10.00</td>
                    <td>2.00</td>
                    <td>49%</td>
                    <td>4312432</td>
                    <td>50%</td>
                    <td>False</td>
                </tr>
                <tr class="info">
                    <td>/opt</td>
                    <td>10.00</td>
                    <td>2.00</td>
                    <td>49%</td>
                    <td>4312432</td>
                    <td>50%</td>
                    <td>True</td>
                </tr>
                <tr class="info">
                    <td>/usr</td>
                    <td>10.00</td>
                    <td>2.00</td>
                    <td>49%</td>
                    <td>4312432</td>
                    <td>50%</td>
                    <td>True</td>
                </tr>

                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                3) Swap交换区大小使用情况检查：
            </div>
        </div>

    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
</body>
</html>
