<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<% request.getSession(true).setAttribute("userName", "mdek"); %>
<html dir="ltr">
    
    <head>
        <link rel="stylesheet" type="text/css" href="dojo/dijit/themes/claro/claro.css"/>
        <link rel="StyleSheet" href="css/main.css" type="text/css" />
        <style type="text/css">
            body, html { font-family:helvetica,arial,sans-serif; font-size:90%; }
        </style>
        <style type="text/css">
            @import "dojo/dojox/grid/resources/Grid.css"; @import "dojo/dojox/grid/resources/claroGrid.css";
            @import "dojo/dojox/grid/enhanced/resources/claroEnhancedGrid.css";
            @import "dojo/dojox/grid/enhanced/resources/EnhancedGrid_rtl.css";
            @import "dojo/dojo/resources/dojo.css";
            .dojoxGrid table { margin: 0; } html, body { width: 100%; height: 100%;
            margin: 0; }
            .myGrid { width: 100%; height:100px; }
            #contentSection { width: 100%; height:100%; }
        </style>
    </head>
    <body class="claro">
		<!-- SPLIT/BORDER CONTAINER START --> 
		<div id="contentSection" class="contentSection">
		</div>
        

    </body>
    
    
    <!--<script type='text/javascript' src='/dojo_test/dwr/interface/TestService.js'></script>-->
	<script src='/ingrid-portal-mdek-application/dwr/interface/TreeService.js'></script>
	
    <script type='text/javascript' src='../dwr/engine.js'></script>
    <script type="text/javascript" src="dojo/dojo/dojo.js.uncompressed.js" djConfig="parseOnLoad: false">
    </script>
    <script type="text/javascript">
        dojo.require("dojox.grid.cells.dijit");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.BorderContainer");
		dojo.require("dojox.layout.FloatingPane");

        dojo.require("dojox.grid.EnhancedGrid");
        dojo.require("dojox.data.CsvStore");

        dojo.require("dojo.data.ItemFileWriteStore");
        dojo.require("dojo.data.ItemFileReadStore");
        dojo.require("dijit.Tree");
		
		dojo.require("dijit.form.DateTextBox");
		
		dojo.require("dijit.form.Select");
		
		dojo.require("dijit.layout.TabContainer");
		
		dojo.require("dijit.form.ValidationTextBox");
		
		dojo.require("dijit.Toolbar");


    </script>
    <script type="text/javascript">
        var currentUdk = {}; 
		var rightPane;
		
		function openNode(item,node) {
			alert(item.id);
			updateTables();
		}
		
		function updateTables() {
			
			// check if they already exist and create them if not
			if (!dojo.byId('table1')) {
				var movieStore = new dojox.data.CsvStore({
					url: 'data/movies.csv'
				});
				
				// set the layout structure:
				var movieLayout = [{
					field: 'Title',
					name: 'Title of Movie',
					width: '200px'
				}, {
					field: 'Year',
					name: 'Year',
					width: '50px'
				}, {
					field: 'Producer',
					name: 'Producer',
					width: 'auto'
				}];
				
				for (var i = 1; i <= 45; i++) {
					var grid = new dojox.grid.EnhancedGrid({
						id: 'table' + i,
						query: {
							Title: '*'
						},
						store: movieStore,
						clientSort: true,
						rowSelector: '20px',
						structure: movieLayout,
						style: "width: 600px; height: 150px;",
						plugins: {}
					}).placeAt(rightPane.domNode);
					
					// append the new grid to the div "gridContainer4":
					//rightPane.domNode.appendChild(grid.domNode);
					//dojo.body().appendChild(aDiv);
					grid.startup();
				}
			} else {
				for (var i = 1; i <= 45; i++) {
					dojo.byId('table'+i).style.display = "none";
				}
			}
		}
        
        dojo.addOnLoad(function() {
			
            // create BorderContainer (Splitpane)
            var main = new dijit.layout.BorderContainer({ 
                design: "headline",
                gutters: true,
                liveSplitters: false }, "contentSection");
				
//===========================================================
			// top pane - menu 
            var topPane = new dijit.layout.ContentPane({
            	splitter: true,
            	region: "top",
				style: "height: 50px;"
                }).placeAt(main);
				
			
			var toolbar = new dijit.Toolbar({id: 'myToolBar'}).placeAt(topPane.domNode);
				
	        dojo.forEach(["Cut", "Copy", "Paste"], function(label) {
	            var button = new dijit.form.Button({
	                // note: should always specify a label, for accessibility reasons.
	                // Just set showLabel=false if you don't want it to be displayed normally
	                label: label,
	                showLabel: true,
	                iconClass: "dijitEditorIcon dijitEditorIcon" + label
	            });
	            toolbar.addChild(button);
	        });

//===========================================================
			// left pane - TreeContainer 
            var leftPane = new dijit.layout.ContentPane({
            	splitter: true,
            	region: "leading",
				style: "width: 200px;"
                }).placeAt(main);
				
			/*
			var data = {
				identifier: 'id',
				label: 'name',
				items: [
					{id: 'objects', name:'node 1', type:'root'},
                	{id: 'addresses', name:'node 2', type:'root'}
                ]};*/
    		
        	// create tree
        	var store = new dojo.data.ItemFileWriteStore({
                //url: "data/countries.json"
                data: {items: []} //data
            });

            var treeModel = new dijit.tree.ForestStoreModel({
                store: store,
                rootId: "root",
                rootLabel: "Data",
				labelAttr: "title",
                childrenAttrs: ["children"]
            });

            var myTree = new dijit.Tree({
				id: 'firstTree',
                model: treeModel,
                showRoot: false,
                openOnClick: false
            	}).placeAt(leftPane.domNode);
				
			dojo.connect(myTree, "onClick", openNode);

//===========================================================
            // right pane - content 
            rightPane = new dijit.layout.ContentPane({
            	splitter: true,
            	region: "center",
            	widgetId: "content",
            	sizeMin: "734",
				style: "overflow-x:hidden;overflow-y:scroll;",
                }).placeAt(main);;
			
				
			new dijit.form.CheckBox({
                name: "checkBox",
                value: "agreed",
                checked: false
            }).placeAt(rightPane.domNode);

			/*
			for (var i = 1; i <= 41; i++){
				new dijit.layout.ContentPane({
					id: 'contentPane' + i,
	            	region: "center",
					style: "width: 50px; height: 10px;"
	                }).placeAt(rightPane.domNode);
			};
			*/
			new dojox.layout.FloatingPane({
	            title: "A floating pane",
	            resizable: true,
	            dockable: true,
	            style: "position:absolute;top:0;left:0;width:100px;height:100px;visibility:hidden;",
	            id: "pFloatingPane"
	        }).placeAt(rightPane.domNode);
			
			for (var i = 1; i <= 2; i++) {
				new dijit.form.Button({
					label: "Click me!",
					onClick: function(){
						// Do something:
						alert("Thank you!");
					}
				}).placeAt(rightPane.domNode);
			}
			
			for (var i = 1; i <= 14; i++) {
				var store = {
					identifier:"abbreviation",
					label: "name",
					items: [
					{name:"Alaska", label:"Alaska",abbreviation:"AK"},
					{name:"NewYork", label:"New York",abbreviation:"NY"}						
				]};
				
				var stateStore = new dojo.data.ItemFileReadStore({
		            data: store
		        });

				new dijit.form.ComboBox({
		            name: "state",
		            value: "Alaska",
		            store: stateStore,
		            searchAttr: "name"
		        }).placeAt(rightPane.domNode);
			}
			
			for (var i = 1; i <= 6; i++) {
				new dijit.form.DateTextBox({
					value: new Date(2007, 11, 25)
				}).placeAt(rightPane.domNode);
			}
			
			/*
			for (var i = 1; i <= 30; i++) {
				new dijit.form.Select({
					id    : "myselect"+i,
        			name  : "myselect"+i
				}).placeAt(rightPane.domNode);
			}
			
			
			for (var i = 1; i <= 11; i++) {
				var tc = new dijit.layout.TabContainer({
					style: "height: 100px; width: 400px;"
				}).placeAt(rightPane.domNode);
				
				var cp1 = new dijit.layout.ContentPane({
					title: "Food",
					content: "We offer amazing food"
				});
				tc.addChild(cp1);
				
				var cp2 = new dijit.layout.ContentPane({
					title: "Drinks",
					content: "We are known for our drinks."
				});
				tc.addChild(cp2);
				
				tc.startup();
			}
			
			for (var i = 1; i <= 69; i++) {
				new dijit.form.ValidationTextBox({
					
				}).placeAt(rightPane.domNode);

			}*/
			
			
			// initially load data (first hierarchy level) from server 
			TreeService.getSubTree(null, null, 
				function (str) {
					var treeStore = dijit.byId('firstTree').model.store;
					dojo.forEach(str, function(entry) {
						treeStore.newItem(entry);
					});
					//var tree = dijit.byId('firstTree');
					//tree.setChildItems(str);
				}
			);
			
			
            main.startup();
		});

        
    </script>
    
    
</html>