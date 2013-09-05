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
                    {field: 'name',name: 'name (header)',width: '200px', editable: false, sortable:true},
                    {field: 'description',name: 'description (header)',width: '400px', editable: false, sortable:true}
                ];                
                createDataGrid("grid", null, gridStructure, addTableData);
                
                // UtilGrid.setTableData("grid", data);

                // FilterSelectBox for grid
                var selectBoxProps = {searchAttr: 'name', data: {identifier: 'id', label: 'name'}};
                createSelectBox("filterSelect", null, selectBoxProps, addSelectBoxData);

                // filter table content on change of relation type filter
                dojo.connect(dijit.byId("filterSelect"), "onChange", function(filterKey) {
                    console.debug("filterTable id: " + filterKey);
                    if (filterKey == "0") {
                        UtilGrid.getTable("grid").setRowFilter(null);
                    } else {
                        UtilGrid.getTable("grid").setRowFilter({ id: filterKey });
                    }

                    console.debug("Table FULL Data ->");
                    console.debug(UtilGrid.getTable("grid").getData(true));
                    console.debug("Table FILTERED Data ->");
                    console.debug(UtilGrid.getTable("grid").getData());

                    UtilGrid.getTable("grid").invalidate();
                });

                
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
                    {id: "1", name:"abc", description:"description", selection: true},
                    {id: "2", name:"aatete", description:"achso", selection: false},
                    {id: "3", name:"test", description:"aaabbbccc", selection: true}
                ];
                def.callback(data);
                return def;
            }
            
            function addSelectBoxData() {
                var def2 = new dojo.Deferred();

                var def = addTableData();
                def.addCallback(function(tableData) {
                    var newItems = [
                        {id: "0", name:" Kein Filter", description:"entfernt Filter auf Rows", selection: false},
                        {id: "100", name:"irgendwas nicht in Tabelle :)", description:"fuehrt zu leerer Tabelle", selection: false}
                    ];
                    var selectBoxData = newItems.concat(tableData);
                    def2.callback(selectBoxData);
                });
                return def2;
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
        <select autoComplete="false" id="filterSelect"></select>

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
