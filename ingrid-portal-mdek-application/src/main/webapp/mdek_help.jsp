<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<style type="text/css">
div.left { float:left; width:15.75em; padding:0; }
div.right { float:left; margin-left:2.625em; width:15.75em; padding:0; }
</style>

</head>

<body>
    <a href="mdek_help.jsp?hkey=index">Index</a>
    <a href="javascript:window.close()">Schliessen</a>

	<div>
		<!-- include HelpServlet -->
		<jsp:include page="help/help.html" flush="true"/>
	</div>
</body>
</html>