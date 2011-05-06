<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <title>MDEK Demo V4</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
        <meta name="author" content="wemove digital solutions" />
        <meta name="copyright" content="wemove digital solutions GmbH" />
    </head>
    <link rel="stylesheet" href="css/styles.css" />
    <script type="text/javascript" src="dojo-src/dojo/dojo.js" djConfig="parseOnLoad:false">
    </script>
	
	<script src='/ingrid-portal-mdek-application/dwr/interface/CatalogService.js'></script>

    <script type='text/javascript' src='dwr/engine.js'></script>
	
	<script type="text/javascript" src="js/utilities.js"></script>
    <script type="text/javascript" src="js/layoutCreator.js">
    </script>
    <script>
        dojo.require("dojo.data.ItemFileWriteStore");
        dojo.require("dojox.grid.DataGrid");
		dojo.require("dojo.DeferredList");
		
		var sysLists = {};
		
		var def1 = getSysList();
        
        dojo.addOnLoad(function(){
            var myTableStructure = [{
                field: 'title',
                name: 'title',
                width: '100%',
                type: dojox.grid.cells.Select,
                options: ["titel 1", "titel 2", "titel 3"], // will be filled later, when syslists are loaded
                values: [1, 2, 3],
                editable: true,
		        formatter: function(value){
		            return UtilSyslist.getSyslistEntryName(5200, value);
		        }
            }];
            var def2 = createDataGrid("myTable", null, myTableStructure, tableData);
			
			var myTable2Structure = [{
                field: 'title',
                name: 'title',
                width: '100%',
                type: dojox.grid.cells.Select,
                options: ["titel 4", "titel 5", "titel 6"], // will be filled later, when syslists are loaded
                values: [4, 5, 6],
                editable: true,
		        formatter: function(value){
		            return UtilSyslist.getSyslistEntryName(5200, value);
		        }
            }];
            var def3 = createDataGrid("myTable2", null, myTable2Structure, tableData);
			
			var defList = new dojo.DeferredList([def1, def2]);
			
			defList.addCallback(function() {
				console.debug("fill options");
				var sysTableElements = [["myTable", 5200, 0], ["myTable2", 5200, 0]];
                dojo.forEach(sysTableElements, function(el){
                    dojo.forEach(sysLists[el[1]], function(item){
                        dijit.byId(el[0]).structure[el[2]].options.push(item[0]);
                        dijit.byId(el[0]).structure[el[2]].values.push(item[1]);
                    });
                });
			});
        });
		
		function getSysList(){
			var def = new dojo.Deferred();
			var lstIds = [5200];
			CatalogService.getSysLists(lstIds, "de", {
				callback: function(res){
					sysLists = res;
					console.debug("syslists fetched");
					def.callback();
				},
				errorHandler: function(mes){
					console.debug("Error: " + mes);
					console.debug("Error: " + mes);
					def.errback(mes);
				}
			});
			
			return def;
		}
        
        function tableData(){
            var def = new dojo.Deferred();
            
            var data = [{
                title: "titel 1"
            }, {
                title: "titel 2"
            }];
            def.callback(data);
            return def;
        }
    </script>
    <body>
        <!-- DataGrid Widget -->
        <div id="myTable">
        </div>
		<div id="myTable2">
        </div>
    </body>
</html>
