<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>InGrid-Editor</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script src='/ingrid-portal-mdek-application/dwr/interface/SecurityService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>

<script>

function loadit() {
	SecurityService.getCurrentUser({
		callback: function(userData) {
			forwardToMdekEntry();
		},
		errorHandler: function(errMsg, err) {
			if (errMsg.indexOf("USER_LOGIN_ERROR") != -1) {
				// -- Redirect to the portal login page --
				
				// get the current url without request parameters
				var baseUrl;
				var tempIndex = document.location.href.indexOf("?");
				if (tempIndex == -1) {
					baseUrl = document.location.href;
				} else {
					baseUrl = document.location.href.substring(0, tempIndex);
				}

				// build the target url by replacing index.jsp with a link to the portal login page. Keep all parameters
				var redirectUrl = document.location.href.replace(/\?/, "%3F");
				redirectUrl = redirectUrl.replace(/&/g, "%26");
				redirectUrl = redirectUrl.replace(/index.jsp/, "../ingrid-portal/portal/service-myportal.psml?r="+baseUrl);
	
				// redirect
				document.location.href = redirectUrl;

			} else {
				alert("Die Applikation kann nicht geöffnet werden. Bei der Anmeldung ist folgender Fehler aufgetreten: "+errMsg);
				document.location.href='closeWindow.jsp';
			}
		}
	});
};

function forwardToMdekEntry() {
	// Forward to mdek_entry.jsp with the current queryString (used for the language parameter)
	var queryString = '<%= request.getQueryString() == null ? "" : request.getQueryString() %>';

	if (queryString.length == 0) {
		document.location.href="mdek_entry.jsp";

	} else {
		document.location.href="mdek_entry.jsp?"+queryString;
	}
}

</script>
</head>

<body onload="window.setTimeout('loadit()', 100);">

<div id="splash" style="position: absolute; top: 0px; width: 100%;z-index: 100; height:2000px;background-color:#FFFFFF">
<div style="position: relative; width: 100%;z-index: 100;top:200px">
   <div align="center" style="line-height:16px">
        <div style="width:550px; height:20px; background-color:#156496">&nbsp;</div>
        <div style="width:550px; background-color:#e6f0f5; font-family:Verdana,Helvetica,Arial,sans-serif; font-size:12px; padding: 20px 0px 20px 0px; margin:0px">
          <p style="font-size:24px; font-weight:bold; line-height:16px; margin:16px">InGrid-Editor</p>
<!--        <p style="font-size:16px; font-weight:bold; line-height:16px; margin:16px">Version 1.0.0</p>   -->
          <p style="font-size:12px; font-weight:normal; line-height:16px; margin:16px">Die Anwendung wird geladen...</p>
        </div>
   </div>
</div>
</div>

</body>
</html>
