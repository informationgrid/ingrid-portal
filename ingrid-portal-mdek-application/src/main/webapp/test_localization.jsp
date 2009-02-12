<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="<%= request.getParameter("lang") %>" scope="session" />
<fmt:setBundle basename="TestBundle" scope="session" />

<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Localization Test</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>
<script type="text/javascript" src="js/dialog.js"></script>

<script type="text/javascript">
var userLocale = '<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>';

openDialog = function() {
	dialog.showPage("<fmt:message key="key" />", "test_localization_dialog.jsp", 360, 240, true);
}
</script>


<body>
<fmt:message key="key" /> <br />
<fmt:message key="invalidKey" />
<button dojoType="ingrid:Button" title="Open Dialog" onClick="openDialog">Open Dialog</button>
</body>
</html>
