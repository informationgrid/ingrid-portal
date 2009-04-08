<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript">
var scriptScope = this;

_container_.addOnLoad(function() {
	initObjectTree();
	initAddressTree();
});


// This method is executed when an object in the tree is selected 
// The argument is an event with an attached treeNode (event.node)
scriptScope.displayPermissionsForObject = function(event) {
	var uuid = event.node.uuid;
	var objPermissionStore = dojo.widget.byId("rightsObjectsUserList").store;

	if (uuid == null || uuid == "objectRoot") {
		objPermissionStore.clearData();
		return;
	}

	SecurityService.getUsersWithWritePermissionForObject(uuid, false, true, {
		callback: function(userList) {
			objPermissionStore.clearData();
			for (var i in userList) {
				addPermissionsToUser(userList[i]);
				userList[i].title = UtilAddress.createAddressTitle(userList[i].address);

				objPermissionStore.addData(userList[i]);
			}
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}


// This method is executed when an address in the tree is selected 
// The argument is an event with an attached treeNode (event.node)
scriptScope.displayPermissionsForAddress = function(event) {
	var uuid = event.node.uuid;
	var adrPermissionStore = dojo.widget.byId("rightsAddressesUserList").store;

	if (uuid == null || uuid == "addressRoot" || uuid == "addressFreeRoot") {
		adrPermissionStore.clearData();
		return;
	}

	SecurityService.getUsersWithWritePermissionForAddress(uuid, false, true, {
		callback: function(userList) {
			adrPermissionStore.clearData();
			for (var i in userList) {
				addPermissionsToUser(userList[i]);
				userList[i].title = UtilAddress.createAddressTitle(userList[i].address);

				adrPermissionStore.addData(userList[i]);
			}
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}


function initObjectTree() {
	var tree = dojo.widget.byId("permissionOverviewTreeObj");
	var treeController = dojo.widget.byId("permissionOverviewTreeControllerObj");

	// initially load data (first hierarchy level) from the server
	// Display all nodes starting from the rootNode
	TreeService.getSubTree(null, null, function (str) {
		// Setting the uuid as widgetId is bad. This leads to widget collisions if we display multiple trees
		// Remove the id and use uuid instead
		str[0].uuid = str[0].id;
		str[0].id = null;
		tree.setChildren([str[0]]);
	});

	treeController.loadRemote = function(node, sync) {
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		TreeService.getSubTree(node.uuid, node.nodeAppType, {
  			callback:function(res) {
				// Setting the uuid as widgetId is bad. This leads to widget collisions if we display multiple trees
				// Remove the id and use uuid instead
				for (var i in res) {
					res[i].uuid = res[i].id;
					res[i].id = null;
				}
  				deferred.callback(res);
  			},
			errorHandler:function(message) {
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});

		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});
		return deferred;
	};

	var treeListener = dojo.widget.byId("permissionOverviewTreeListenerObj");
	dojo.event.topic.subscribe(treeListener.eventNames.select, scriptScope, "displayPermissionsForObject");
}


function initAddressTree() {
	var tree = dojo.widget.byId("permissionOverviewTreeAdr");
	var treeController = dojo.widget.byId("permissionOverviewTreeControllerAdr");

	// initially load data (first hierarchy level) from server
	// Display all address nodes starting from the rootNode
	TreeService.getSubTree(null, null, function (str) {
		// Setting the uuid as widgetId is bad. This leads to widget collisions if we display multiple trees
		// Remove the id and use uuid instead
		str[1].uuid = str[1].id;
		str[1].id = null;
		tree.setChildren([str[1]]);
	});

	treeController.loadRemote = function(node, sync) {
		var _this = this;

		var params = {
			node: this.getInfo(node),
			tree: this.getInfo(node.tree)
		};

		var deferred = new dojo.Deferred();

		TreeService.getSubTree(node.uuid, node.nodeAppType, {
  			callback:function(res) {
				// Setting the uuid as widgetId is bad. This leads to widget collisions if we display multiple trees
				// Remove the id and use uuid instead
				for (var i in res) {
					res[i].uuid = res[i].id;
					res[i].id = null;
				}
  				deferred.callback(res);
  			},
			errorHandler:function(message) {
				deferred.errback(new dojo.RpcError(message, this));
			}
  		});

		deferred.addCallback(function(res) { return _this.loadProcessResponse(node,res); });
		deferred.addErrback(function(res) { dialog.show(message.get("general.error"), message.get("tree.loadError"), dialog.WARNING); dojo.debug(res); return res;});
		return deferred;
	};

	var treeListener = dojo.widget.byId("permissionOverviewTreeListenerAdr");
	dojo.event.topic.subscribe(treeListener.eventNames.select, scriptScope, "displayPermissionsForAddress");
}

// Helper function that adds html tags to a user object, for display in the result table.
function addPermissionsToUser(user) {
	for (var i in user.permissions) {
		if (user.permissions[i] == "WRITE_SINGLE") {
			user.writeSingle = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";

		} else if (user.permissions[i] == "WRITE_TREE") {
			user.writeTree = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";

		} else if (user.permissions[i] == "QUALITY_ASSURANCE") {
			user.qa = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";

//		} else if (user.permission[i] == "CREATE_ROOT") {
//			user.writeSingle = "<div style='text-align: center;'><img src='img/ic_check.gif' width='16' height='16' alt='vorhanden' /></div>";
		}
	}
}

</script>


</head>
<body>
  <div dojoType="ContentPane" layoutAlign="client">

    <div id="contentSection" class="contentBlockWhite top">
      <div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:dialog.showContextHelp(arguments[0], 8024)" title="Hilfe">[?]</a>
  	  </div>
  	  <div id="rightsAdmin" class="content">

        <!-- CONTENT BLOCK 1 START -->
        <!-- SPLIT CONTAINER START -->
        <div>
          <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8025)"><fmt:message key="dialog.admin.permissions.objectPermissions" /></label></span>
          <div dojoType="SplitContainer" id="rightsObjects" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:auto;" sizeShare="24">
              <div class="inputContainer grey noSpaceBelow">
              	<div dojoType="ContentPane" id="permissionOverviewTreeContainerObj">
                  <!-- tree components -->
                  <div dojoType="ingrid:TreeController" widgetId="permissionOverviewTreeControllerObj"></div>
                  <div dojoType="ingrid:TreeListener" widgetId="permissionOverviewTreeListenerObj"></div>	
                  <div dojoType="ingrid:TreeDocIcons" widgetId="permissionOverviewTreeDocIconsObj"></div>	
                  <div dojoType="ingrid:TreeDecorator" listener="permissionOverviewTreeListenerObj"></div>
                  
                  <!-- tree -->
                  <div dojoType="ingrid:Tree" listeners="permissionOverviewTreeControllerObj;permissionOverviewTreeListenerObj;permissionOverviewTreeDocIconsObj" widgetId="permissionOverviewTreeObj">
                  </div>
              	</div>
                <div class="spacer"></div>
              </div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->

            <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:hidden;" sizeshare="76">
       
              <div id="rightsObjectsUser" class="inputContainer grey field">
				<div class="tableContainer rows10 full">
	          	    <table id="rightsObjectsUserList" dojoType="ingrid:FilteringTable" minRows="10" cellspacing="0" class="filteringTable nosort relativePos">
	          	      <thead>
	          		      <tr>
	                			<th nosort="true" field="title" dataType="String" width="359"><fmt:message key="dialog.admin.permissions.userName" /></th>
	                			<th nosort="true" field="roleName" dataType="String" width="145"><fmt:message key="dialog.admin.permissions.role" /></th>
	                			<th nosort="true" field="writeSingle" dataType="String" width="60" style="text-align:center;"><fmt:message key="dialog.admin.permissions.single" /></th>
	                			<th nosort="true" field="writeTree" dataType="String" width="60" style="text-align:center;"><fmt:message key="dialog.admin.permissions.tree" /></th>
	                			<th nosort="true" field="qa" dataType="String" width="60" style="text-align:center;"><fmt:message key="dialog.admin.permissions.qa" /></th>
	          		      </tr>
	          	      </thead>
	          	      <tbody>
	          	      </tbody>
	          	    </table>
					</div>
            	</div>
            </div>
            <!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
          </div>
        </div>
        <!-- CONTENT BLOCK 1 END -->

        <div class="spacer"></div>

        <!-- CONTENT BLOCK 2 START -->
        <!-- SPLIT CONTAINER START -->
        <div>
          <span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8026)"><fmt:message key="dialog.admin.permissions.addressPermissions" /></label></span>
          <div dojoType="SplitContainer" id="rightsAddresses" orientation="horizontal" sizerWidth="15" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
            <!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:auto;" sizeShare="24">
              <div class="inputContainer grey noSpaceBelow">
              	<div dojoType="ContentPane" id="permissionOverviewTreeContainerAdr">
                  <!-- tree components -->
                  <div dojoType="ingrid:TreeController" widgetId="permissionOverviewTreeControllerAdr"></div>
                  <div dojoType="ingrid:TreeListener" widgetId="permissionOverviewTreeListenerAdr"></div>	
                  <div dojoType="ingrid:TreeDocIcons" widgetId="permissionOverviewTreeDocIconsAdr"></div>	
                  <div dojoType="ingrid:TreeDecorator" listener="permissionOverviewTreeListenerAdr"></div>
                  
                  <!-- tree -->
                  <div dojoType="ingrid:Tree" listeners="permissionOverviewTreeControllerAdr;permissionOverviewTreeListenerAdr;permissionOverviewTreeDocIconsAdr" widgetId="permissionOverviewTreeAdr">
               	  </div>
                </div>
                <div class="spacer"></div>
              </div>
            </div>
            <!-- LEFT HAND SIDE CONTENT BLOCK 2 END -->

            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:hidden;" sizeshare="76">
       
              <div id="rightsAddressesUser" class="inputContainer grey field">
				<div class="tableContainer rows10 full">
	          	    <table id="rightsAddressesUserList" dojoType="ingrid:FilteringTable" minRows="10" cellspacing="0" class="filteringTable nosort relativePos">
	          	      <thead>
	          		      <tr>
	                			<th nosort="true" field="title" dataType="String" width="359"><fmt:message key="dialog.admin.permissions.userName" /></th>
	                			<th nosort="true" field="roleName" dataType="String" width="145"><fmt:message key="dialog.admin.permissions.role" /></th>
	                			<th nosort="true" field="writeSingle" dataType="String" width="60" style="text-align:center;"><fmt:message key="dialog.admin.permissions.single" /></th>
	                			<th nosort="true" field="writeTree" dataType="String" width="60" style="text-align:center;"><fmt:message key="dialog.admin.permissions.tree" /></th>
	                			<th nosort="true" field="qa" dataType="String" width="60" style="text-align:center;"><fmt:message key="dialog.admin.permissions.qa" /></th>
	          		      </tr>
	          	      </thead>
	          	      <tbody>
	          	    </table>
            		</div>
            	</div>
            </div>
            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->
          </div>
      </div>
      <!-- CONTENT BLOCK 2 END -->
    </div>
  </div>
  <!-- CONTENT END -->

</div>

</body>
</html>
