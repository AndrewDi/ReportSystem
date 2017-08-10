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
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>）
<!DOCTYPE html>
<html lang="zh-CN">
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
                操作系统版本：${BaseInfo.oslevel}
            </div>
        </div>
        <div class="row">
            <div class="col-md-3">
                逻辑CPU数目：${BaseInfo.cpusize}
            </div>
            <div class="col-md-3">
                物理内存大小(MB)：${BaseInfo.memsize}
            </div>
            <div class="col-md-3">
                Swap内存大小(MB)：${BaseInfo.swapsize}
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
                ${BaseInfo.hostname}:${BaseInfo.oslevel}
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
                    <th>Filesystem</th>
                    <th>MB Block</th>
                    <th>Used</th>
                    <th>Available</th>
                    <th>Use%</th>
                    <th>MountPoint</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${fss}" var="fs">
                    <c:choose>
                        <c:when test="${fn:replace(fs[4],'%','')<75}">
                            <tr class="info">
                            <td>${fs[0]}</td>
                            <td>${fs[1]}</td>
                            <td>${fs[2]}</td>
                            <td>${fs[3]}</td>
                            <td>${fs[4]}</td>
                            <td>${fs[5]}</td>
                            <th>True</th>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr class="danger">
                            <td>${fs[0]}</td>
                            <td>${fs[1]}</td>
                            <td>${fs[2]}</td>
                            <td>${fs[3]}</td>
                            <td>${fs[4]}</td>
                            <td>${fs[5]}</td>
                            <th>False</th>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                3) Swap交换区大小使用情况检查：
            </div>
        </div>
        <div class="table-responsive">
            <table class="table table-bordered table-condensed">
                <caption>说明：如果Swap交换区使用率大于20%，则State检查结果为False，并且状态为红色</caption>
                <thead>
                <tr>
                    <th>总大小(MB)</th>
                    <th>已使用(MB)</th>
                    <th>空闲(MB)</th>
                    <th>使用率</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${BaseInfo.swapsize}</td>
                    <td>${BaseInfo.swapsize-BaseInfo.swapfree}</td>
                    <td>${BaseInfo.swapfree}</td>
                    <td>${(BaseInfo.swapsize-BaseInfo.swapfree)/BaseInfo.swapsize}%</td>
                    <c:choose>
                        <c:when test="${(BaseInfo.swapsize-BaseInfo.swapfree)/BaseInfo.swapsize<0.2}">
                            <td class="info">True</td>
                        </c:when>
                        <c:otherwise>
                            <td class="danger">False</td>
                        </c:otherwise>
                    </c:choose>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                4) 实例用户limit设置检查：
            </div>
        </div>
        <h3 class="sub-header" style="color: #336699">2、数据库检查</h3>
        <div class="row">
            <div class="col-md-6"  style="color: #336699">
                1) 表空间使用情况检查：
            </div>
        </div>
    </div>
</div>
<jsp:include page="../Common/bottom.jsp"/>
</body>
</html>
