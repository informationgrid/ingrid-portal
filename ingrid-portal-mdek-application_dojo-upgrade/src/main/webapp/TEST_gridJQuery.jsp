<!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <title>SlickGrid example 3: Editing</title>
        <link rel="stylesheet" href="js/jquery/slick.grid.css" type="text/css" media="screen" charset="utf-8" />
        <link rel="stylesheet" href="js/jquery/css/smoothness/jquery-ui-1.8.5.custom.css" type="text/css" media="screen" charset="utf-8" />
        <link rel="stylesheet" href="js/jquery/examples/examples.css" type="text/css" media="screen" charset="utf-8" />
        <link rel="stylesheet" href="css/styles.css" />
        <style>
        .cell-title {
            font-weight: bold;
        }

        .cell-effort-driven {
            text-align: center;
        }
        </style>
        <script type="text/javascript" src="dojo-src/dojo/dojo.js" djConfig="parseOnLoad:false"></script>
        
    </head>
    <body class="claro">
    <div id="adrThesaurusContent" class="content">
    <div class="inputContainer">
        <span id="firstOne" class="outer optional" style="display:none;">
            <div>
                <span class="label">
                    <label>
                        label
                    </label>
                </span>
                <span class="functionalLink">
                    <img src="img/ic_fl_popup.gif" width="10" height="9" alt="Popup" /><a href="javascript:dialog.showPage(message.get('dialog.thesaurusNavigator.title'), 'dialogs/mdek_thesaurus_dialog.jsp', 1010, 430, true, {dstTable: 'thesaurusTermsAddress'});" title="<fmt:message key='dialog.popup.thesaurus.terms.link.navigator' /> [Popup]" ><fmt:message key="ui.adr.thesaurus.terms.link.navigator" /></a>
                </span>
                <div class="input tableContainer">
                    <div id="myGrid1" style="width:100%;height:100px;" autoHeight="3" minRows="3" class="hideTableHeader">
                    </div>
                </div>
            </div>
        </span>
    </div>
    <div class="inputContainer">
        <div class="input">
            <span id="uiElementN019" class="outer required">
                <div class="input">
                    <input type="text" maxLength="255" id="thesaurusFreeTermInputAddress" />
                    <div style="position:relative; float:right;">
                        <button id="thesaurusFreeTermsAddressAddButton">
                            <fmt:message key="ui.adr.thesaurus.terms.custom.buttonAdd" />
                        </button>
                    </div>
                </div>
            </span>
        </div>
    </div>
    </div>
        <div style="width:600px;float:left;display:none;">
            <div id="myGrid2" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;display:none;">
            <div id="myGrid3" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;display:none;">
            <div id="myGrid4" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;display:none;">
            <div id="myGrid5" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;display:none;">
            <div id="myGrid6" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;display:none;">
            <div id="myGrid7" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid8" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid9" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid10" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid11" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid12" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid13" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid14" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid15" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid16" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid17" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid18" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid19" style="width:100%;height:100px;"></div>
        </div>
        <div style="width:600px;float:left;">
            <div id="myGrid20" style="width:100%;height:100px;"></div>
        </div>

        <div class="options-panel" style="width:320px;margin-left:650px;">
            <h2>Demonstrates:</h2>
            <ul>
                <li>adding basic keyboard navigation and editing</li>
                <li>custom editors and validators</li>
                <li>auto-edit settings</li>
            </ul>

            <h2>Options:</h2>
            <button onclick="grid.setOptions({autoEdit:true})">Auto-edit ON</button> 
            &nbsp;
            <button onclick="grid.setOptions({autoEdit:false})">Auto-edit OFF</button>
            &nbsp;
            <button onclick="setStoreInGrid()">setStore</button>
        </div>

        <script src="js/jquery/lib/firebugx.js"></script>

        <script src="js/jquery/lib/jquery-1.4.3.min.js"></script>
        <script src="js/jquery/lib/jquery-ui-1.8.5.custom.min.js"></script>
        <script src="js/jquery/lib/jquery.event.drag-2.0.min.js"></script>

        <script src="js/jquery/slick.core.js"></script>
        <script src="js/jquery/plugins/slick.cellrangeselector.js"></script>
        <script src="js/jquery/plugins/slick.cellselectionmodel.js"></script>
        <script src="js/jquery/slick.editors.js"></script>
        <script src="js/jquery/slick.editor.select.js"></script>
        <script src="js/jquery/slick.editor.combo.js"></script>
        <script src="js/jquery/slick.grid.js"></script>
        
        <script type="text/javascript" src="js/utilities.js"></script>

        <script>
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dijit.form.ComboBox");
            dojo.require("dojo.date.locale");
            
        function setStoreInGrid() {
            var d = {};

                d["name"] = "Test";
                d["type"] = "world";
                d["duration"] = "1 days";
                d["percentComplete"] = 22;
                d["start"] = "10/10/1978";
                d["finish"] = "01/05/2009";
                d["effortDriven"] = 0;
            
            //gridManager[0].resetActiveCell();
            gridManager[0].setData([d]);
            //gridManager[0].setSelectedRows(selectedRows);
            gridManager[0].render();
            dojo.byId("firstOne").style.display = "block";
        }
        
        function requiredFieldValidator(value) {
            if (value == null || value == undefined || !value.length)
                return {valid:false, msg:"This is a required field"};
            else
                return {valid:true, msg:null};
        }

        var grid;
        var data = [];
        var columns = [
            {id:"title", name:"Title", field:"name", width:120, cssClass:"cell-title", editor:TextCellEditor, validator:requiredFieldValidator},
            {id:"desc", name:"Description", field:"type", width:100, editor:ComboboxEditor},
            {id:"duration", name:"Duration", field:"duration", editor:TextCellEditor},
            {id:"%", name:"% Complete", field:"percentComplete", width:80, resizable:false, formatter:GraphicalPercentCompleteCellFormatter, editor:PercentCompleteCellEditor},
            {id:"start", name:"Start", field:"start", minWidth:60, editor:DateCellEditor},
            {id:"finish", name:"Finish", field:"finish", minWidth:60, editor:DateCellEditor},
            {id:"effort-driven", name:"Effort Driven", width:80, minWidth:20, maxWidth:80, cssClass:"cell-effort-driven", field:"effortDriven", formatter:BoolCellFormatter, editor:YesNoCheckboxCellEditor}
        ];
        var options = {
            editable: true,
            enableAddRow: true,
            enableCellNavigation: true,
            asyncEditorLoading: false,
            autoEdit: false,
            rerenderOnResize: true
        };


        gridManager = [];
        
        $(function()
        {
            for (var i=0; i<20; i++) {
                var d = (data[i] = {});

                d["name"] = "Task " + i;
                d["type"] = "world";
                d["duration"] = "5 days";
                d["percentComplete"] = Math.round(Math.random() * 100);
                d["start"] = "01/01/2009";
                d["finish"] = "01/05/2009";
                d["effortDriven"] = (i % 5 == 0);
            }

            for (var j = 1; j < 21; j++) {
                grid = new Slick.Grid("#myGrid" + j, data, columns, options);
                
                //grid.registerPlugin(new Slick.CellRangeSelector());
                
                //grid.setSelectionModel(new Slick.CellSelectionModel());
                
                grid.onAddNewRow.subscribe(function(e, args){
                    var item = args.item;
                    var column = args.column;
                    grid.invalidateRow(data.length);
                    data.push(item);
                    grid.updateRowCount();
                    grid.render();
                });
                gridManager.push(grid);
            }
        })
        </script>
    </body>
</html>