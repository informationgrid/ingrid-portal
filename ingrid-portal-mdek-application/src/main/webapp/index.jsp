<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2015 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>InGrid-Editor</title>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type='text/javascript' src='dwr/engine.js'></script>
<script type='text/javascript' src='dwr/util.js'></script>
<script src='dwr/interface/SecurityService.js'></script>

<script>
// Example: http://localhost:8080/ingrid-portal-mdek-application/index.jsp?nodeType=O&nodeId=7937CA1A-3F3A-4D36-9EBA-E2F55190811A

function localizeLoadingMessage() {
	var userLocale = '<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>';
	if (userLocale != "de") {
		// Assume english if it's not 'de'
		document.getElementById("loadingMessage").innerHTML = "The application is loading...";
	}
}

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

				// in case we access the page from "/" and redirected to the welcome file automatically
			    if (redirectUrl.indexOf("index.jsp") == -1)
			        redirectUrl += "/index.jsp";
				
				SecurityService.isPortalConnected(function(response) {
					if (response == true)
					    redirectUrl = redirectUrl.replace(/index.jsp/, "../portal/service-myportal.psml?r="+baseUrl);
					else
					    redirectUrl = redirectUrl.replace(/index.jsp/, "/login.jsp?r="+baseUrl);
				    
	                // redirect
	                document.location.href = redirectUrl;
				});

			} else {
				alert("Die Applikation kann nicht geöffnet werden. Bei der Anmeldung ist folgender Fehler aufgetreten: "+errMsg);
				document.location.href='closeWindow.jsp';
			}
		}
	});
};

function forwardToMdekEntry() {
	// Forward to start.jsp with the current queryString (used for the language parameter)
	var queryString = '<%= request.getQueryString() == null ? "" : request.getQueryString() %>';

	if (queryString.length == 0) {
		document.location.href="start.jsp";

	} else {
		document.location.href="start.jsp?"+queryString;
	}
}

</script>
</head>

<body onload="localizeLoadingMessage(); window.setTimeout('loadit()', 100);">

<div id="splash" style="position: absolute; top: 0px; width: 100%;z-index: 100; height:2000px;background-color:#FFFFFF">
<div style="position: relative; width: 100%;z-index: 100;top:200px">
   <div align="center" style="line-height:16px">
        <div style="width:550px; height:20px; background-color:#156496">&nbsp;</div>
        <div style="width:550px; background-color:#e6f0f5; font-family:Verdana,Helvetica,Arial,sans-serif; font-size:12px; padding: 20px 0px 20px 0px; margin:0px">
          <p style="font-size:24px; font-weight:bold; line-height:16px; margin:16px">InGrid-Editor</p>
          <p id="loadingMessage" style="font-size:12px; font-weight:normal; line-height:16px; margin:16px">Die Anwendung wird geladen...</p>
        </div>
   </div>
</div>
</div>

</body>
</html>
