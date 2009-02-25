<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Developer login</title>
<% request.getSession(true).setAttribute("userName", request.getParameter("user")); %>
<script>
function onLoad() {
	document.location.href="<%= request.getParameter("page") != null ? request.getParameter("page") : "mdek_entry.jsp" %>";
}
</script>

</head>
<body onload="onLoad();">
</body>
</html>