<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

</head>

<style type="text/css">
	@import url(css/scrolling_table.css);
</style>
<!--[if IE]>
<style type="text/css">
	@import url(css/scrolling_table.ie.css);
</style>
<![endif]-->


<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: false};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>

<script>
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.FilteringTable");
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");
dojo.require("ingrid.widget.ValidationTextbox");

dojo.addOnLoad(function() {
	setValues();
});

function setValues() {
	var data = [{Id: 0, title:"Titel 1", name:"Name 1", version: "Version 1"},
				{Id: 1, title:"Titel 2", name:"Name 2", version: "Version 2"},
				{Id: 2, title:"Titel 3", name:"Name 3", version: "Version 3"},
				{Id: 3, title:"Titel 4", name:"Name 4", version: "Version 4"},
				{Id: 4, title:"Titel 5", name:"Name 5", version: "Version 5"}];

	dojo.widget.byId("testTable").store.setData(data);
}

</script>


<body>
<!-- Filtering Table Widget -->
<div>
<div class="tableContainer">
    <table id="testTable" dojoType="ingrid:FilteringTable" minRows="4" cellspacing="0" class="filteringTable nosort full">
	    <thead>
			<tr>
		    	<th field="title" dataType="String">Titel</th>
		    	<th field="name" dataType="String">Datum</th>
		    	<th field="version" dataType="String">Version</th>
			</tr>
		</thead>
	    <tbody>
	    </tbody>
	</table>
</div>



<div class="tableContainer">
<table class="dataTable" cellspacing="0">
<thead id="mythead">
<tr id="mytr">
<th>Station</th>
<th>Date</th>
<th>Status</th>
<th>Num.</th>
</tr>
</thead>
<tbody>
<tr>
<td>KABC</td>
<td>09/12/2002</td>
<td>Submitted</td>
<td>0</td>

</tr>
<tr>
<td>KCBS</td>
<td>09/11/2002</td>
<td>Lockdown</td>
<td>2</td>
</tr>

<tr>
<td>WFLA</td>
<td>09/11/2002</td>
<td>Submitted</td>
<td>1</td>
</tr>
<tr>
<td>WTSP</td>

<td>09/15/2002</td>
<td>In-Progress</td>
<td>10</td>
</tr>
<tr>
<td>WROC</td>
<td>10/11/2002</td>

<td>Submitted</td>
<td>12</td>
</tr>
<tr>
<td>WPPP</td>
<td>09/16/2002</td>
<td>In-Progress</td>

 <td>0</td>
 </tr>
 <tr>
 <td>WRRR</td>
 <td>09/06/2002</td>
 <td>Submitted</td>
 <td>5</td>

 </tr>
 <tr>
 <td>WTTT</td>
 <td>09/21/2002</td>
 <td>In-Progress</td>
 <td>0</td>
 </tr>

 <tr>
 <td>W000</td>
 <td>11/11/2002</td>
 <td>Submitted</td>
 <td>7</td>
 </tr>
 <tr>
 <td>KABC</td>

 <td>10/01/2002</td>
 <td>Submitted</td>
 <td>10</td>
 </tr>
 <tr>
 <td>KCBS</td>
 <td>10/18/2002</td>

 <td>Lockdown</td>
 <td>2</td>
 </tr>
 <tr>
 <td>WFLA</td>
 <td>10/18/2002</td>
 <td>Submitted</td>

 <td>1</td>
 </tr>
 <tr>
 <td>WTSP</td>
 <td>10/19/2002</td>
 <td>In-Progress</td>
 <td>0</td>

 </tr>
 <tr>
 <td>WROC</td>
 <td>07/18/2002</td>
 <td>Submitted</td>
 <td>2</td>
 </tr>

 <tr>
 <td>WPPP</td>
 <td>10/28/2002</td>
 <td>In-Progress</td>
 <td>10</td>
 </tr>
 <tr>
 <td>WRRR</td>

 <td>10/28/2002</td>
 <td>Submitted</td>
 <td>5</td>
 </tr>
 <tr>
 <td>WTTT</td>
 <td>10/08/2002</td>

 <td>In-Progress</td>
 <td>0</td>
 </tr>
 <tr>
 <td>WIL0</td>
 <td>10/18/2001</td>
 <td>Submitted</td>

 <td>7</td>
 </tr>
 <tr>
 <td>KABC</td>
 <td>04/18/2002</td>
 <td>Submitted</td>
 <td>0</td>

 </tr>
 <tr>
 <td>KCBS</td>
 <td>10/05/2001</td>
 <td>Lockdown</td>
 <td>2</td>
 </tr>

 <tr>
 <td>WFLA</td>
 <td>10/18/2002</td>
 <td>Submitted</td>
 <td>1</td>
 </tr>
 <tr>
 <td>WTSP</td>

 <td>10/19/2002</td>
 <td>In-Progress</td>
 <td>0</td>
 </tr>
 <tr>
 <td>WROC</td>
 <td>12/18/2002</td>

 <td>Submitted</td>
 <td>2</td>
 </tr>
 <tr>
 <td>WPPP</td>
 <td>12/28/2002</td>
 <td>In-Progress</td>

 <td>8</td>
 </tr>
 <tr>
 <td>WRRR</td>
 <td>12/20/2002</td>
 <td>Submitted</td>
 <td>5</td>

 </tr>
 <tr>
 <td>WTTT</td>
 <td>12/11/2002</td>
 <td>In-Progress</td>
 <td>0</td>
 </tr>

 <tr>
 <td>W0VB</td>
 <td>01/18/2003</td>
 <td>Submitted</td>
 <td>17</td>
 </tr>
 <tr>
 <td>KABC</td>

 <td>12/17/2002</td>
 <td>Submitted</td>
 <td>20</td>
 </tr>
 <tr>
 <td>KCBS</td>
 <td>12/16/2002</td>

 <td>Lockdown</td>
 <td>2</td>
 </tr>
 <tr>
 <td>WFAA</td>
 <td>12/18/2002</td>
 <td>Submitted</td>

 <td>1</td>
 </tr>
 <tr>
 <td>WTSP</td>
 <td>12/18/2002</td>
 <td>In-Progress</td>
 <td>0</td>

 </tr>
 <tr>
 <td>WROC</td>
 <td>12/19/2002</td>
 <td>Submitted</td>
 <td>2</td>
 </tr>

 <tr>
 <td>WPPP</td>
 <td>12/06/2002</td>
 <td>In-Progress</td>
 <td>0</td>
 </tr>
 <tr>
 <td>WRRR</td>

 <td>12/28/2002</td>
 <td>Submitted</td>
 <td>5</td>
 </tr>
 <tr>
 <td>WTTT</td>
 <td>12/30/2002</td>

 <td>In-Progress</td>
 <td>0</td>
 </tr>
 <tr>
 <td>UMBA</td>
 <td>12/26/2002</td>
 <td>Submitted</td>

 <td>7</td>
 </tr>
 <tr>
 <td>KABC</td>
 <td>12/18/2002</td>
 <td>Submitted</td>
 <td>0</td>

 </tr>
 <tr>
 <td>KCBS</td>
 <td>12/29/2002</td>
 <td>Lockdown</td>
 <td>2</td>
 </tr>

 <tr>
 <td>WFFF</td>
 <td>12/22/2002</td>
 <td>Submitted</td>
 <td>1</td>
 </tr>
 <tr>
 <td>WTSP</td>

 <td>12/18/2001</td>
 <td>In-Progress</td>
 <td>9</td>
 </tr>
 <tr>
 <td>WROC</td>
 <td>11/19/2001</td>

 <td>Submitted</td>
 <td>2</td>
 </tr>
 <tr>
 <td>WPPP</td>
 <td>11/20/2001</td>
 <td>In-Progress</td>

 <td>0</td>
 </tr>
 <tr>
 <td>WRRR</td>
 <td>10/19/2001</td>
 <td>Submitted</td>
 <td>5</td>

 </tr>
 <tr>
 <td>WTTT</td>
 <td>11/29/2001</td>
 <td>In-Progress</td>
 <td>8</td>
 </tr>

 <tr>
 <td>KPLT</td>
 <td>11/19/2001</td>
 <td>Submitted</td>
 <td>7</td>
 </tr>
 <tr>
 <td>KABC</td>

 <td>11/19/2001</td>
 <td>Submitted</td>
 <td>13</td>
 </tr>
 <tr>
 <td>KBRE</td>
 <td>11/19/2001</td>

 <td>Lockdown</td>
 <td>2</td>
 </tr>
 <tr>
 <td>WFLA</td>
 <td>11/19/2001</td>
 <td>Submitted</td>

 <td>1</td>
 </tr>
 <tr>
 <td>WTSP</td>
 <td>02/19/2003</td>
 <td>In-Progress</td>
 <td>0</td>

 </tr>
 <tr>
 <td>WROC</td>
 <td>02/17/2003</td>
 <td>Submitted</td>
 <td>2</td>
 </tr>

 <tr>
 <td>WPPP</td>
 <td>02/16/2003</td>
 <td>In-Progress</td>
 <td>16</td>
 </tr>
 <tr>
 <td>WRRR</td>

 <td>02/29/2003</td>
 <td>Submitted</td>
 <td>5</td>
 </tr>
 <tr>
 <td>WTTT</td>
 <td>03/19/2003</td>

 <td>In-Progress</td>
 <td>19</td>
 </tr>
 <tr>
 <td>KLTR</td>
 <td>02/10/2003</td>
 <td>Submitted</td>

 <td>7</td>
 </tr>
 <tr>
 <td>KABC</td>
 <td>04/05/2003</td>
 <td>Submitted</td>
 <td>16</td>

 </tr>
 <tr>
 <td>KCBS</td>
 <td>02/19/2003</td>
 <td>Lockdown</td>
 <td>2</td>
 </tr>

 <tr>
 <td>WFLA</td>
 <td>02/16/2003</td>
 <td>Submitted</td>
 <td>1</td>
 </tr>
 <tr>
 <td>WTSP</td>

 <td>02/13/2003</td>
 <td>In-Progress</td>
 <td>5</td>
 </tr>
 <tr>
 <td>WROC</td>
 <td>02/14/2003</td>

 <td>Submitted</td>
 <td>2</td>
 </tr>
 <tr>
 <td>WPPP</td>
 <td>03/19/2003</td>
 <td>In-Progress</td>

 <td>0</td>
 </tr>
 <tr>
 <td>WRRR</td>
 <td>02/19/2002</td>
 <td>Submitted</td>
 <td>5</td>

 </tr>
 <tr>
 <td>WTTT</td>
 <td>02/19/2002</td>
 <td>In-Progress</td>
 <td>0</td>
 </tr>

 <tr>
 <td>WYYD</td>
 <td>02/11/2002</td>
 <td>Submitted</td>
 <td>7</td>
 </tr>
 <tr>
 <td>KABC</td>

 <td>02/19/2002</td>
 <td>Submitted</td>
 <td>11</td>
 </tr>
 <tr>
 <td>KCBS</td>
 <td>02/19/2002</td>

 <td>Lockdown</td>
 <td>12</td>
 </tr>
 <tr>
 <td>WFLA</td>
 <td>05/19/2002</td>
 <td>Submitted</td>

 <td>1</td>
 </tr>
 <tr>
 <td>WTSP</td>
 <td>02/20/2002</td>
 <td>In-Progress</td>
 <td>0</td>

 </tr>
 <tr>
 <td>WROC</td>
 <td>05/20/2002</td>
 <td>Submitted</td>
 <td>2</td>
 </tr>

 <tr>
 <td>WPPP</td>
 <td>02/19/2003</td>
 <td>In-Progress</td>
 <td>13</td>
 </tr>
 <tr>
 <td>WRRR</td>

 <td>02/19/2002</td>
 <td>Submitted</td>
 <td>5</td>
 </tr>

 </tbody>
 </table>

</div>

</body>
</html>
