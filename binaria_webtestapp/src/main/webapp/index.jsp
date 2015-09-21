<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<shiro:authenticated>
<%response.sendRedirect("main#/?dashboard-view");%>
</shiro:authenticated>
<shiro:notAuthenticated>
<%response.sendRedirect("main");%>
</shiro:notAuthenticated>