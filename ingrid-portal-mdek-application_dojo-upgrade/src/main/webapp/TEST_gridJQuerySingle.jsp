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
                    <div id="myGrid" style="width:100%;">
                    </div>
                </div>
            </div>
        </span>
    </div>
    

        <div class="options-panel" style="width:320px;margin-left:750px;">
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
        
        <ul id="gridContextMenu" style="display:none;position:absolute">
            <b>Set priority:</b>
            <li data="selAll">Select all</li>
            <li data="deselAll">Deselect all</li>
            <li data="remSel">Remove selected row</li>
            <li data="remClicked">Remove clicked row</li>
        </ul>

        <script src="js/jquery/lib/firebugx.js"></script>

        <script src="js/jquery/jquery-1.5.min.js"></script>
        <script src="js/jquery/lib/jquery-ui-1.8.5.custom.min.js"></script>
        <script src="js/jquery/lib/jquery.event.drag-2.0.min.js"></script>

        <script src="js/jquery/slick.core.js"></script>
        <script src="js/jquery/plugins/slick.cellrangeselector.js"></script>
        <script src="js/jquery/plugins/slick.cellselectionmodel.js"></script>
        <script src="js/jquery/plugins/slick.cellrangedecorator.js"></script>
        <script src="js/jquery/plugins/slick.rowselectionmodel.js"></script>
        <script src="js/jquery/slick.editors.js"></script>
        <script src="js/jquery/slick.editors.custom.js"></script>
        <script src="js/jquery/slick.editor.select.js"></script>
        <script src="js/jquery/slick.editor.combo.js"></script>
        <script src="js/jquery/slick.grid.js"></script>
        <script src="js/jquery/slick.grid.formatters.js"></script>
        
        <script type="text/javascript" src="js/utilities.js"></script>
        <script type="text/javascript" src="js/menu.js"></script>

        <script>
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dijit.form.ComboBox");
            dojo.require("dijit.form.DateTextBox");
            dojo.require("dojo.date.locale");
            
        function setStoreInGrid() {
            var newDate = dojo.date.locale.parse("24.08.1985", {datePattern: "dd.MM.yyyy", selector: "date"})
            var d = {name:"test", type:"type1", duration:"10", percentComplete:30, start:"10.10.1978", finish:newDate, effortDriven: 0};
            var d1 = {name:"test2", type:"type2", duration:"20", percentComplete:50, start:"10.10.1978", finish:newDate, effortDriven: 0};
            var d2 = {name:"test3", type:"type3", duration:"30", percentComplete:60, start:"10.10.1978", finish:newDate, effortDriven: 1};

            //gridManager[0].resetActiveCell();
            gridManager[0].setData([d,d1,d2]);
            
            
            dojo.byId("firstOne").style.display = "block";
            gridManager[0].resizeCanvas();
        }
        
        function requiredFieldValidator(value) {
            if (value == null || value == undefined || !value.length)
                return {valid:false, msg:"This is a required field"};
            else
                return {valid:true, msg:null};
        }
        
        function initTableContextMenu(){
            $("#gridContextMenu").click(function(e){
                if (!$(e.target).is("li")) 
                    return;
                
                var row = $(this).data("row");
                var choice = $(e.target).attr("data");
                
                switch (choice) {
                    case "selAll":
                        var rows = [];
                        var length = clickedSlickGrid.getData().length;
                        for (var i=0; i<length; i++) {
                            rows.push(i);
                        }
                        clickedSlickGrid.setSelectedRows(rows);
                        break;
                    case "deselAll":
                        clickedSlickGrid.setSelectedRows([]);
                        break;
                    case "remSel":
                        var data = clickedSlickGrid.getData();
                        var sortedSelection = clickedSlickGrid.getSelectedRows().sort();
                        var decr = 0; // when removing an element the slected row moved up!
                        dojo.forEach(sortedSelection, function(rowNr) {
                            data.splice(rowNr-(decr++),1);
                        });
                        clickedSlickGrid.setData(data);
                        clickedSlickGrid.render();
                        clickedSlickGrid.setSelectedRows([]);
                        break;
                    case "remClicked":
                        var data = clickedSlickGrid.getData();
                        data.splice(row,1);
                        clickedSlickGrid.setData(data);
                        clickedSlickGrid.render();
                        break;
                }
                
            });
        }

        initTableContextMenu();
        
        var grid;
        var data = [];
        var columns = [
            {id:"title", name:"Title", field:"name", width:120, cssClass:"cell-title", editor:TextCellEditor, validator:requiredFieldValidator},
            {id:"desc", name:"Description", field:"type", width:100, editor:ComboboxEditor},
            {id:"duration", name:"Duration", field:"duration", editor:TextCellEditor},
            {id:"%", name:"% Complete", field:"percentComplete", width:80, resizable:false, formatter:GraphicalPercentCompleteCellFormatter, editor:PercentCompleteCellEditor},
            {id:"start", name:"Start", field:"start", minWidth:60, editor:DateCellEditorToString},
            {id:"finish", name:"Finish", field:"finish", minWidth:60, editor:DateCellEditor, formatter:DateCellFormatter},
            {id:"effort-driven", name:"Effort Driven", width:80, minWidth:20, cssClass:"cell-effort-driven", field:"effortDriven", formatter:BoolCellFormatter, editor:YesNoCheckboxCellEditor}
        ];
        var options = {
            editable: true,
            enableAddRow: true,
            enableCellNavigation: true,
            asyncEditorLoading: false,
            autoEdit: false,
            rerenderOnResize: true,
            //leaveSpaceForNewRows: true,
            visibleRowsInViewport: 4,autoHeight: true
        };


        gridManager = [];
        
        $(function()
        {
                var d = (data[i] = {});
                var i=1;
                d["name"] = "Task " + i;
                d["type"] = "world";
                d["duration"] = "5 days";
                d["percentComplete"] = Math.round(Math.random() * 100);
                d["start"] = "01/01/2009";
                d["finish"] = "01/05/2009";
                d["effortDriven"] = (i % 5 == 0);

                /*var testDiv = document.createElement("div");
                testDiv.setAttribute("style", "width:300px;");
                testDiv.setAttribute("id", "bla");
                document.getElementById("adrThesaurusContent").appendChild(testDiv);
                */
               grid = new Slick.Grid("#myGrid", data, columns, options);
                
                //grid.registerPlugin(new Slick.CellRangeSelector());
                
                //grid.setSelectionModel(new Slick.CellSelectionModel());
                grid.onAddNewRow.subscribe(function(e, args){
                    var item = args.item;
                    var column = args.column;
                    grid.invalidateRow(data.length);
                    grid.getData().push(item);
                    grid.updateRowCount();
                    grid.render();
                });
                
                //grid.setSelectionModel(new Slick.CellSelectionModel());
                grid.setSelectionModel(new Slick.RowSelectionModel());
                
                //createSlickGridMenu(grid, "gridContextMenu");
                
                gridManager.push(grid);
        })
        </script>
    </body>
</html>