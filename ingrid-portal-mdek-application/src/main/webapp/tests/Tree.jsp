<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>Tables Test</title>

        <script type="text/javascript">
            require([
                "dojo/aspect",
                "dojo/_base/window",
                "dojo/_base/array",
                "dojo/store/Memory",
                "dojo/store/Observable",
                "dijit/Tree",
                "dijit/tree/ObjectStoreModel",
                "ingrid/tree/MetadataTree",
                "ingrid/tree/ThesaurusTree",
                "ingrid/utils/Tree",
                "dwr/interface/SecurityService",
                "dojo/domReady!"
            ], function(aspect, win, array, Memory, Observable, Tree, ObjectStoreModel, MetadataTree, ThesaurusTree, UtilTree) {
                
                var clickedTree = function(item, node, event) {
                    console.log("Clicked in tree, item:", item);
                    console.log("node:", node);
                    console.log("event:", event);
                };

                thesTree = new ThesaurusTree({
                    service: "sns"
                    // rootUrl: customParams.rootUrl,
                }, "treeThes");
                
                tree = new MetadataTree({ showRoot: false }, "tree2");
                metaStore = tree.model.store;
                
                treeObjects = new MetadataTree({ showRoot: false, treeType: "Objects", onClick: clickedTree }, "tree3");
                objectsStore = treeObjects.model.store;

                treeAddresses = new MetadataTree({ showRoot: false, treeType: "Addresses" }, "tree4");
                addressesStore = treeAddresses.model.store;
                
                treeUsers = new MetadataTree({ showRoot: false, treeType: "Users" }, "tree5");
                usersStore = treeUsers.model.store;
            });
            
        </script>
        
	</head>
	<body>
	    <h1>Tree Tests</h1>

        <h1>ThesaurusTree</h1>
        <div id="treeThes"></div>
        
        <h1>MetadataTree - Users</h1>
        <div id="tree5"></div>
        <button onclick="usersStore.add({id: 'new1', title:'new root user', parent: 'UsersRoot'});">
            Add root node to user tree
        </button>
        <button onclick="usersStore.add({id: 'new2', title:'new sub-user', parent: 'TreeNode_167269'});">
            Add sub user to tree
        </button>
        <button onclick="usersStore.remove('new1');">
            Delete root user node
        </button>


        <h1>MetadataTree - Objects and Addresses</h1>
        <div id="tree2"></div>
        <button onclick="metaStore.add({id: 'new1', title:'neuer knoten', parent: 'ObjectsAndAddressesRoot'});">
            Add root node to meta tree
        </button>
        <button onclick="metaStore.add({id: 'new2', title:'new Object', nodeAppType: 'O', parent: 'objectRoot'});">
            Add node to objects - meta tree
        </button>
        <button onclick="metaStore.add({id: 'new3', title:'new Address', nodeAppType: 'A', parent: 'addressRoot'});">
            Add node to addresses - meta tree
        </button>


        <h1>MetadataTree - Objects</h1>
        <div id="tree3"></div>
        <button onclick="objectsStore.add({id: 'new1', title:'neuer knoten', nodeAppType: 'O', parent: 'objectRoot'});">
            Add root node to meta tree
        </button>
        <button onclick="objectsStore.add({id: 'new2', title:'new Object', nodeAppType: 'O', parent: '71FE85D0-67AA-485D-900D-924B10855B21'});">
            Add node to "All Object classes" - meta tree
        </button>
        <button onclick="objectsStore.add({id: 'new3', title:'new Object', nodeAppType: 'O', parent: '8020DFE1-1208-487B-BA1A-F054AFCFF611'});">
            Add node to object leaf - meta tree
        </button>

        <h1>MetadataTree - Addresses</h1>
        <div id="tree4"></div>
        <button onclick="addressesStore.add({id: 'new1', title:'new Root', parent: 'AddressesRoot'});">
            Add root node to meta tree
        </button>
        <button onclick="addressesStore.add({id: 'new2', title:'new Address', nodeAppType: 'A', parent: 'addressRoot'});">
            Add node to addresses - meta tree
        </button>

	</body>
</html>
