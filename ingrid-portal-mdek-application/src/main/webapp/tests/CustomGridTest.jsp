<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>Untitled Document</title>
        <link rel="stylesheet" href="styles.css" />
        <link rel="stylesheet" href="../css/slick.grid.css" type="text/css" media="screen" charset="utf-8" />
        <link rel="stylesheet" href="../css/styles.css" />
        <script type="text/javascript" src="../dojo-src/dojo/dojo.js" djConfig="parseOnLoad:false, locale:'de'"></script>
        <script type="text/javascript" src="../dojo-src/custom/layer.js"></script>
        <script type="text/javascript">
            var isRelease = false;
        </script>
        <script type="text/javascript" src="../js/config.js"></script>
        <script type="text/javascript" src="../js/message.js"></script>
        <script type="text/javascript" src="../js/utilities.js"></script>
        
        <script type="text/javascript" src="../js/layoutCreator.js"></script>
        <script type="text/javascript" src="../js/menu.js"></script>
        
        <script type="text/javascript" src="../js/dojo/dijit/CustomGridRowMover.js"></script>
        <script type="text/javascript" src="../js/dojo/dijit/CustomGridRowSelector.js"></script>
        <script type="text/javascript" src="../js/dojo/dijit/CustomGridEditors.js"></script>
        <script type="text/javascript" src="../js/dojo/dijit/CustomGridFormatters.js"></script>
        
        <script type="text/javascript">
            dojo.require("dijit.Menu")
            
            dojo.addOnLoad(function(){
                var gridStructure = [
                    {field: 'name',name: 'i',width: '200px', editable: false, sortable:true},
                    {field: 'description',name: 'label',width: '400px', editable: false, sortable:true}
                ];
                createDataGrid("grid", null, gridStructure, null);
                
                var data = [
                    {name:"abc", description:"description"},
                    {name:"aatete", description:"achso"},
                    {name:"test", description:"aaabbbccc"}
                ];
                UtilGrid.setTableData("grid", data);
            });
            
            function changeData() {
                var data = [];
                data.push(UtilGrid.getTableData("grid")[1]);
                data.push(UtilGrid.getTableData("grid")[0]);
                UtilGrid.setTableData("grid", data);
            }
        </script>
        
	</head>
	<body>
	    <h1>Grid sorting test</h1>
	    <div id="grid"></div>
        
        <input type="button" onclick="changeData()" value="ChangeData">
	</body>
</html>
