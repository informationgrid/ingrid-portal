<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Test DataGrid - setStore</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"></meta>
    <style type="text/css">
        @import "dojo-src/dijit/themes/tundra/tundra.css";
        @import "dojo-src/dojo/resources/dojo.css";
        @import "dojo-src/dijit/tests/css/dijitTests.css";
        @import "dojo-src/dojox/grid/resources/Grid.css";
        @import "dojo-src/dojox/grid/resources/tundraGrid.css";
        
        body {
            font-size: 0.9em;
            font-family: Geneva, Arial, Helvetica, sans-serif;
        }
        button {
            margin: 20px;
        }
        .heading {
            font-weight: bold;
            padding-bottom: 0.25em;
        }
        .grid {
            width: 65em;
            padding: 1px;
        }
    </style>
    <script type="text/javascript" src="dojo-src/dojo/dojo.js" 
        djConfig="isDebug:false, parseOnLoad: true"></script>
    <script type="text/javascript">
        dojo.require("dijit.dijit"); // optimize: load dijit layer
        dojo.require("dojox.grid.DataGrid");
        dojo.require("dojo.data.ItemFileWriteStore");
        dojo.require("dojo.parser");
        
        dojo.declare("dojox.grid.SlowFileWriteStore", dojo.data.ItemFileWriteStore, {
            _fetchItems: function(  /*Object*/ keywordArgs, 
                                    /*Function*/ findCallback, 
                                    /*Function*/ errorCallback){
                setTimeout(dojo.hitch(this, function(){
                    dojo.data.ItemFileWriteStore.prototype._fetchItems.call(this, keywordArgs, findCallback, errorCallback);
                }), 10000);
            }
        });
        
        function createStore(type)
        {
            // Create a new datastore and use the Grid's setStore() function to change it.
            var newStore, args = {}, grid = dijit.byId("grid");
            if(type=="slow"){
                // Slow Loading store
                args.url = "dojo-src/dijit/tests/_data/countries.json";
                newStore = new dojox.grid.SlowFileWriteStore(args);
                grid.setStore(newStore, { name: '*' });
            }else if(type=="empty"){
                // Empty result set -- set the Grid's query to something with no results
                args.url = "dojo-src/dijit/tests/_data/countries.json";
                newStore = new dojo.data.ItemFileWriteStore(args);
                grid.setStore(newStore, { name: 'nonexist' });
            }else if(type=="error"){
                // Error result -- set the url to a file that doesn't exist
                args.url = "dojo-src/dijit/tests/_data/countries-no.json";
                newStore = new dojo.data.ItemFileWriteStore(args);
                grid.setStore(newStore);
            }else{//type=="standard"
                args.url = "dojo-src/dijit/tests/_data/countries.json";
                newStore = new dojo.data.ItemFileWriteStore(args);
                //grid.setStore(newStore);
                for (var i=1; i<21; i++) {
                    dijit.byId("grid"+i).setStore(newStore);
                }
            }
        }
    </script>
    <script type="text/javascript" src="support/test_data.js"></script>
</head>
<body class="tundra">
    <h1>dojox.grid.DataGrid using setStore() to change datastores.</h1>
    <p>
        This page demonstrates the ability to change datastores using the
        DataGrid's setStore() function. Click on any of the buttons above
        the Grid to set a new store and refresh the Grid display.
    </p>
    <p>
        <button onclick="createStore('standard');">Standard Store</button>
        <button onclick="createStore('slow');">Slow Loading Store</button>
        <button onclick="createStore('empty');">Empty Store</button>
        <button onclick="createStore('error');">Error Store</button>
    </p>
    <span dojoType="dojo.data.ItemFileWriteStore" 
        jsId="jsonStore" url="dojo-src/dijit/tests/_data/countries.json">
    </span>
    <div class="heading">Grid Test</div>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid1" id="grid1" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid2" id="grid2" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid3" id="grid3" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid4" id="grid4" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid5" id="grid5" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid6" id="grid6" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid7" id="grid7" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid8" id="grid8" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid9" id="grid9" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid10" id="grid10" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid11" id="grid11" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid12" id="grid12" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid13" id="grid13" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid14" id="grid14" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid15" id="grid15" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid16" id="grid16" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid17" id="grid17" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid18" id="grid18" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid19" id="grid19" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
    <table dojoType="dojox.grid.DataGrid"
        jsid="grid20" id="grid20" class="grid" autoHeight="15" noDataMessage="Sorry, there is no data available."
        store="jsonStore" query="{ name: '*' }" rowsPerPage="20" rowSelector="20px">
        <thead>
            <tr>
                <th field="name" width="300px">Country/Continent Name</th>
                <th field="type" width="auto">Type</th>
            </tr>
        </thead>
    </table>
</body>
</html>
