<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Javascript Test</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

</head>
<script type="text/javascript">
function testFunc() {
	var testString = "<u>test, <br>test 2, <br/>test 3,<br /> test ende</u>";
	var resultStr = testString.replace(/<(?!b>|\/b>|i>|\/i>|br>|br\/>|br \/>|p>|\/p>|u>|\/u>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/gi, '');

	document.getElementById("testOutput").innerHTML = resultStr;
}
</script>

<body>
<button onclick="javscript:testFunc();">test</button>
<div id="testOutput"></div>
</body>
</html>
