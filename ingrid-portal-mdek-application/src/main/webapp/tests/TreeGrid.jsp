<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<title>Tables Test</title>

<link rel="stylesheet" href="../dojo-sources/dojox/grid/resources/Grid.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="../dojo-sources/dojox/grid/resources/claroGrid.css" type="text/css" media="screen" charset="utf-8" />

<script type="text/javascript">
    require(["ingrid/grid/CustomGrid", 
             "ingrid/grid/NodeBrowser", 
             "dwr/interface/SecurityService", 
             "dwr/interface/QueryService",
             "dojo/domReady!" ], 
            function(CustomGrid, NodeBrowser) {

        

        nodebrowser = new NodeBrowser({
            id : "browser",
            type: "Addresses"
        }, "browser");

    });
</script>

</head>
<body>
    <div data-dojo-type="dijit/layout/ContentPane" style="height: 600px;">
        <h1>Node Browser</h1>
        <div id="browser" class="hideTableHeader"></div>

        <!-- <h1>Tree Grid Tests</h1>
            <span dojoType="dojo.data.ItemFileWriteStore" data-dojo-id="jsonStore" data="dataItems">
            </span>
    
            <div dojoType="dijit.tree.ForestStoreModel" data-dojo-id="continentModel" store="jsonStore" query="{type:'planet'}"
                rootId="continentRoot" rootLabel="Continents" childrenAttrs="children">
            </div>
            
            <table data-dojo-id="grid" dojoType="dojox/grid/TreeGrid" class="grid" treeModel="continentModel">
                <thead>
                    <tr>
                        <th field="name" width="auto">Name</th>
                        <th field="population" width="auto">Population</th>
                        <th field="timezone" width="auto">Timezone</th>
                    </tr>
                </thead>
            </table> -->
</body>
</html>
