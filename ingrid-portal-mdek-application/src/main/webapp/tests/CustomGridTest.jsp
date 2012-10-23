<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>Tables Test</title>

        <script type="text/javascript">
            dojo.require("dijit.Menu");
            dojo.require("dijit.form.CheckBox");
            
            dojo.addOnLoad(function(){
                var gridStructure = [
                    {field: 'name',name: 'i',width: '200px', editable: false, sortable:true},
                    {field: 'description',name: 'label',width: '400px', editable: false, sortable:true}
                ];
                createDataGrid("grid", null, gridStructure, null);
                
                var data = [
                    {name:"abc", description:"description", selection: true},
                    {name:"aatete", description:"achso", selection: false},
                    {name:"test", description:"aaabbbccc", selection: true}
                ];
                UtilGrid.setTableData("grid", data);
                
                var gridStructureBool = [
                    {field: 'selection',name: 'selection',width: '23px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
                    {field: 'name',name: 'i',width: '200px', editable: false, sortable:true},
                    {field: 'description',name: 'label',width: '400px', editable: false, sortable:true}
                                 ];
                createDataGrid("gridBool", null, gridStructureBool, null);
                //dijit.byId("gridBool").options.autoEdit = true;
                UtilGrid.setTableData("gridBool", data);
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
        
        <div id="gridBool" interactive="true" autoEdit="true" class="hideTableHeader"></div>
	</body>
</html>
