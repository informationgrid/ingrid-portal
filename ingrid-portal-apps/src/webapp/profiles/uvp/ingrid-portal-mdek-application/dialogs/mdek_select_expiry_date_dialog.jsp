<%--
  **************************************************-
  InGrid Portal Apps
  ==================================================
  Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
                var date = registry.byId("expiryDate");
                date.constraints.min = new Date();
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
        Das hier gesetzte Datum wird in allen ausgew�hlten Zeilen als "G�ltig bis"-Datum gesetzt. Ist kein Datum gesetzt, so werden die Datumsangaben aus den ausgew�hlten Zeilen entfernt.
    </div>
    <label for="expiryDate">G�ltig bis:</label>
    <input type="text" name="expiryDate" id="expiryDate"
        data-dojo-type="dijit/form/DateTextBox" />
        <button id="btnAddExpiryDate" type="button">�bernehmen</button>
</body>
</html>
