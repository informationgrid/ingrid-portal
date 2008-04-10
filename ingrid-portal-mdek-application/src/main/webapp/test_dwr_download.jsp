<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script src='/ingrid-portal-mdek-application/dwr/interface/QueryService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>


<script type="text/javascript">
// Save as CSV values link 'onclick' function
saveAsCSV = function() {
	var query = "select distinct addr from AddressNode as aNode inner join aNode.t02AddressWork addr order by addr.adrType, addr.institution, addr.lastname, addr.firstname";

	QueryService.queryHQLToCSV(query, {
		callback: function(data) { window.open(data); },
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}		
	});
}
</script>
</head>

<body>
	<a href="javascript:void(0);" onclick="javascript:saveAsCSV();" title="Download Test">Download Test</a>
</body>
</html>
