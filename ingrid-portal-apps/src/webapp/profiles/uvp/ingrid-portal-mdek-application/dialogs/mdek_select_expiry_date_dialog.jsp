<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <script type="text/javascript">

        require([
            "dojo/_base/array",
            "dojo/on",
            "dijit/form/Button",
            "dijit/registry",
            "ingrid/utils/Grid",
            "ingrid/utils/String",
            "dojo/domReady!"
        ], function(array, on, Button, registry, UtilGrid, UtilString) {
            var dialog = null;
            var params = null;

            on(_container_, "Load", function() {
                dialog = _container_;
                params = this.customParams;
            });

            new Button({
                onClick: function(){
                    var tableData = UtilGrid.getTableData(params.gridId);
                    var date = registry.byId("expiryDate");
                    var dateString = isNaN(date.value.getTime()) ? "" : UtilString.getDateString(date.value, "dd.MM.yyyy");

                    if (params.selectedRows.length > 0) {
                        array.forEach(params.selectedRows, function(row) {
                            tableData[row]["expires"] = dateString;
                        });
                    }
                    
                    UtilGrid.setTableData(params.gridId, tableData);
                    dialog.hide();
                }
            }, "btnAddExpiryDate").startup();
            
        });
    </script>
</head>
<body>
    <div style="padding: 5px; margin-bottom: 10px; border: 1px solid #ccc;">
        Das hier gesetzte Datum wird in allen ausgewählten Zeilen als "Gültig bis"-Datum gesetzt. Ist kein Datum gesetzt, so werden die Datumsangaben aus den ausgewählten Zeilen entfernt.
    </div>
    <label for="expiryDate">Gültig bis:</label>
    <input type="text" name="expiryDate" id="expiryDate"
        data-dojo-type="dijit/form/DateTextBox" />
        <button id="btnAddExpiryDate" type="button">Übernehmen</button>
</body>
</html>