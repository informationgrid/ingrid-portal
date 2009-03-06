<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>SNS Update test</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script src='/ingrid-portal-mdek-application/dwr/interface/CatalogManagementService.js'></script>

<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/util.js'> </script>

<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>

<script type="text/javascript">
go = function() {
	var file = dwr.util.getValue("updateFile");

	CatalogManagementService.startSNSUpdateJob(file);
}
</script>


<body>
<input type="file" id="updateFile" size="80" />
<button id="goButton" onClick="go();" dojoType="ingrid:Button">Go</button>
</body>
</html>
