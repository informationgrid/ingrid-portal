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
                createDataGrid("grid", null, gridStructure, addTableData);
                
                // UtilGrid.setTableData("grid", data);
                
                var gridStructureBool = [
                    {field: 'selection',name: 'selection',width: '23px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
                    {field: 'name',name: 'i',width: '200px', editable: false, sortable:true},
                    {field: 'description',name: 'label',width: '400px', editable: false, sortable:true}
                                 ];
                createDataGrid("gridBool", null, gridStructureBool, addTableData);
                //dijit.byId("gridBool").options.autoEdit = true;
                // UtilGrid.setTableData("gridBool", data);

                createDataGrid("gridCMEdit", null, gridStructureBool, addTableData);
                // next function is needed for context menu "gridCMDuplicateGrid"
                selectObjectInTree = function() { console.debug("object in tree selected");};
                createDataGrid("gridCMDuplicateGrid", null, gridStructureBool, addTableData);
                createDataGrid("gridCMGeneralAddress", null, gridStructureBool, addTableData);
                createDataGrid("gridCMEditOperation", null, gridStructureBool, addTableData);
                createDataGrid("gridCMEditLink", null, gridStructureBool, addTableData);

            });

            function addTableData() {
                var def = new dojo.Deferred();
                var data = [
                    {name:"abc", description:"description", selection: true},
                    {name:"aatete", description:"achso", selection: false},
                    {name:"test", description:"aaabbbccc", selection: true}
                ];
                def.callback(data);
                return def;
            }
            
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

        <h2>ContextMenu: None</h2>        
        <div id="gridBool" interactive="true" autoEdit="true" contextMenu="none" class="hideTableHeader"></div>

        <h2>ContextMenu: EDIT</h2>
        <div id="gridCMEdit" interactive="true" autoEdit="true" contextMenu="EDIT" class="hideTableHeader"></div>
        
        <h2>ContextMenu: DUPLICATE_GRID</h2>
        <div id="gridCMDuplicateGrid" interactive="true" autoEdit="true" contextMenu="DUPLICATE_GRID" class="hideTableHeader"></div>

        <h2>ContextMenu: GENERAL_ADDRESS</h2>
        <div id="gridCMGeneralAddress" interactive="true" autoEdit="true" contextMenu="GENERAL_ADDRESS" class="hideTableHeader"></div>

        <h2>ContextMenu: EDIT_OPERATION</h2>
        <div id="gridCMEditOperation" interactive="true" autoEdit="true" contextMenu="EDIT_OPERATION" class="hideTableHeader"></div>

        <h2>ContextMenu: EDIT_LINK</h2>
        <div id="gridCMEditLink" interactive="true" autoEdit="true" contextMenu="EDIT_LINK" class="hideTableHeader"></div>

	</body>
</html>
