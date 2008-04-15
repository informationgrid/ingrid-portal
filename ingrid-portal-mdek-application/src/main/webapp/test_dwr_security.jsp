<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script src='/ingrid-portal-mdek-application/dwr/interface/SecurityService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>

<script type="text/javascript">
	var djConfig = {isDebug: true, /* use with care, may lead to unexpected errors! */debugAtAllCosts: false, debugContainerId: "dojoDebugOutput"};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>
<script type="text/javascript" src="js/message.js"></script>
<script type="text/javascript" src="js/includes.js"></script>


<script type="text/javascript">
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.FilteringTable");
dojo.require("ingrid.widget.FilteringTable");
dojo.require("ingrid.widget.TableContextMenu");


getGroups = function() {
	SecurityService.getGroups( {
		callback: function(groupList) {
			dojo.widget.byId("groupTable").store.setData(groupList);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

getGroupDetails = function() {
	var selectedData = dojo.widget.byId("groupTable").getSelectedData();

	if (selectedData.length != 1) {
		return;
	}

	var group = selectedData[0];

	SecurityService.getGroupDetails(group.name, {
		callback: function(data) {
			updateGroup(group, data);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}

createGroup = function() {
	var group = { name:"testGroup" }

	SecurityService.createGroup(group, true, {
		callback: function(data) {
			dojo.widget.byId("groupTable").store.addData(data);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});	
}

storeGroup = function() {
	var selectedData = dojo.widget.byId("groupTable").getSelectedData();

	if (selectedData.length != 1) {
		return;
	}

	var group = selectedData[0];
	if (group.name.indexOf("_test") != -1) {
		group.name = group.name.substring(0, group.name.indexOf("_test"));
	} else {
		group.name = group.name+"_test";
	}

	SecurityService.storeGroup(group, true, {
		callback: function(data) {
			updateGroup(group, data);
		},
		errorHandler: function(errMsg, err) {
			dojo.debug(errMsg);
			dojo.debugShallow(err);
		}
	});
}


function updateGroup(group, newData) {
	var groupStore = dojo.widget.byId("groupTable").store;
	groupStore.update(group, "id", newData.id);
	groupStore.update(group, "name", newData.name);
	groupStore.update(group, "lastEditor", newData.lastEditor);
	groupStore.update(group, "creationTime", newData.creationTime);
	groupStore.update(group, "modificationTime", newData.modificationTime);
}


testSecurity = function() {
	SecurityService.testSecurity();
}


</script>
</head>

<body>
	<button dojoType="Button" onclick="javascript:getGroups();">Get Groups</button>
	<button dojoType="Button" onclick="javascript:getGroupDetails();">Get Group Details</button>
	<button dojoType="Button" onclick="javascript:createGroup();">Create Group</button>
	<button dojoType="Button" onclick="javascript:storeGroup();">Store Group</button>
	<button dojoType="Button" onclick="javascript:testSecurity();">Test</button>

	<table id="groupTable" valueField="id" dojoType="ingrid:FilteringTable" defaultDateFormat="%d.%m.%Y %H:%m:%S" minRows="4" cellspacing="0" class="filteringTable interactive readonly">
		<thead>
			<tr>
				<th field="id" dataType="String" width="120">Identifier</th>
 				<th field="name" dataType="String" width="200">Name</th>
 				<th field="lastEditor" dataType="String" width="200">Ge&auml;ndert von</th>
 				<th field="creationTime" dataType="Date" width="200">Erstellt am</th>
 				<th field="modificationTime" dataType="Date" width="200">Ge&auml;ndert am</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

</body>
</html>
