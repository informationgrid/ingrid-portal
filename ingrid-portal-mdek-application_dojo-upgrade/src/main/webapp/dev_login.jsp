<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Developer login</title>
<!-- Set the userName in the session to init the mdek app -->
<% request.getSession(true).setAttribute("userName", request.getParameter("user")); %>

</head>
<!-- redirect to the page specified by the parameter 'page' or 'mdek_entry.jsp' if no page parameter is specified -->
<!-- also set the debug and lang parameter to the given values -->
<c:redirect url='<%= request.getParameter("page") != null ? request.getParameter("page") : "mdek_entry.jsp" %>'>
	<c:if test="<%= request.getParameter("debug") != null %>">
		<c:param name="debug" value="<%= request.getParameter("debug") %>" />
	</c:if>
	<c:if test="<%= request.getParameter("lang") != null %>">
		<c:param name="lang" value="<%= request.getParameter("lang") %>" />
	</c:if>
</c:redirect>
</html>