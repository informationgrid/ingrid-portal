<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script type="text/javascript">
//var scriptScope = adminGroupScope;

var currentUserGroupDetails = null;
var curSelectedGroupDetails = null;


_container_.addOnLoad(function() {
	showLoadingZone();

	if (currentUser.role == 1) {
		var def = new dojo.Deferred();
		def.callback(null);

	} else {
		var def = getGroupDetailsById(currentUser.groupIds);
	}

	def.addCallback(function(groupDetails) {
		currentUserGroupDetails = groupDetails;

		initObjectTree();
		initAddressTree();
		initGroupList();
		resetAllInputData();
		hidePermissionLists();	
		hideLoadingZone();
	});
});
</script>

</head>

<body>

<!-- CONTENT START -->
<div dojoType="ContentPane" layoutAlign="client">
  
	<div id="contentSection" class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=user-administration-2#user-administration-2', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
		</div>
		<div id="groupAdmin" class="content">

	        <!-- LEFT HAND SIDE CONTENT BLOCK 1 START -->
	        <div class="spacer"></div>
	        <div class="spacer"></div>

	        <div class="inputContainer noSpaceBelow">
				<div class="tableContainer w364 headHiddenRows9">
					<table id="groups" dojoType="ingrid:FilteringTable" minRows="9" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive">
						<thead>
							<tr>
								<th nosort="true" field="name" dataType="String">Name</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
	        <!-- LEFT HAND SIDE CONTENT BLOCK 1 END -->
	
	        <!-- RIGHT HAND SIDE CONTENT BLOCK 1 START -->
	        <div id="groupData" class="inputContainer">
				<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8018)"><fmt:message key="dialog.admin.groups.data" /></label></span>
				<div class="inputContainer field grey noSpaceBelow">
	            	<span class="label"><label for="groupDataName" onclick="javascript:dialog.showContextHelp(arguments[0], 8019, 'Gruppenname')"><fmt:message key="dialog.admin.groups.name" /></label></span>
	            	<span class="input"><input type="text" maxlength="50" id="groupDataName" class="w550" dojoType="ingrid:ValidationTextBox" /></span>
	            	<div class="spacerField"></div>
	      	  	</div>

				<div class="inputContainer">
					<span class="button" style="width:556px; height:20px !important;">
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.groups.save" />" onClick="javascript:adminGroupScope.saveGroup();"><fmt:message key="dialog.admin.groups.save" /></button>
						</span>
						<span style="float:right;">
							<button dojoType="ingrid:Button" title="<fmt:message key="dialog.admin.groups.createGroup" />" onClick="javascript:adminGroupScope.newGroup();"><fmt:message key="dialog.admin.groups.createGroup" /></button>
						</span>
						<span id="adminGroupLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
							<img src="img/ladekreis.gif" />
						</span>
					</span>
				</div>
				<div id="permissionCheckboxContainer" class="inputContainer grey field checkboxContainer" style="display:none;">
					<span class="input"><input type="checkbox" id="userDataCreate" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8020, 'Root-Objekte und -Adressen anlegen')"><fmt:message key="dialog.admin.groups.createRoot" /></label></span>
					<span class="input"><input type="checkbox" id="userDataQS" dojoType="Checkbox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8021, 'Qualit&auml;tssichernder')"><fmt:message key="dialog.admin.groups.qa" /></label></span>
		        </div>
			</div>
			<!-- RIGHT HAND SIDE CONTENT BLOCK 1 END -->
	
	        <!-- CONTENT BLOCK 2 START -->
	        <!-- SPLIT CONTAINER START -->
			<div id="permissionListObjects" style="display:none;">
				<div class="spacer"></div>
				<div class="spacer"></div>
				<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8022)"><fmt:message key="dialog.admin.groups.objectPermissions" /></label></span>
				<div dojoType="ingrid:SplitContainer" id="groupDataObjects" persist="false" orientation="horizontal" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
		            <!-- LEFT HAND SIDE CONTENT BLOCK 2 START -->
		            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:auto;" sizeShare="38">
						<div class="inputContainer grey noSpaceBelow">
							<div dojoType="ContentPane" id="treeContainerObjects">
								<!-- tree components -->
								<div dojoType="ingrid:TreeController" widgetId="treeControllerObjects"></div>
								<div dojoType="ingrid:TreeListener" widgetId="treeListenerObjects"></div>	
								<div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIconsObjects"></div>	
								<div dojoType="ingrid:TreeDecorator" listener="treeListenerObjects"></div>
		                  
								<!-- tree -->
								<div dojoType="ingrid:Tree" listeners="treeControllerObjects;treeListenerObjects;treeDocIconsObjects" widgetId="treeObjects">
<!-- 
									<div dojoType="ingrid:TreeNode" title="Objekte" objectId="o1" isFolder="true" nodeDocType="Objects" nodeAppType="Objekt"></div>
 -->
								</div>
							</div>
							<div class="spacer"></div>
						</div>
					</div>
					<!-- LEFT HAND SIDE CONTENT BLOCK 2 END -->
		
		            <!-- RIGHT HAND SIDE CONTENT BLOCK 2 START -->
		            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:hidden;" sizeshare="62">
 						<div class="selectEntryBtn">
							<button dojoType="ingrid:Button" id="addObjectButton" onClick="javascript:adminGroupScope.addObject();">&nbsp;>&nbsp;</button>
						</div>

						<div id="groupDataObjectsData" class="inputContainer grey field">
							<div class="tableContainer third2 rows7">
								<table id="groupDataRightsObjectsList" dojoType="ingrid:FilteringTable" minRows="7" headClass="fixedHeader" cellspacing="0" class="filteringTable interactive">
									<thead>
										<tr>
											<th nosort="true" sort="asc" field="title" dataType="String" width="358"><fmt:message key="dialog.admin.groups.objectName" /></th>
											<th nosort="true" field="single" dataType="String" width="85"><fmt:message key="dialog.admin.groups.objectSingle" /></th>
											<th nosort="true" field="tree" dataType="String" width="85"><fmt:message key="dialog.admin.groups.objectTree" /></th>
                                            <th nosort="true" field="subnode" dataType="String" width="85"><fmt:message key="dialog.admin.groups.objectSubNode" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
								</table>
							</div>
						</div>
					</div>
					<!-- RIGHT HAND SIDE CONTENT BLOCK 2 END -->
				</div>
			</div>
			<!-- CONTENT BLOCK 2 END -->
	
	        <!-- CONTENT BLOCK 3 START -->
	        <!-- SPLIT CONTAINER START -->
			<div id="permissionListAddresses" style="display:none;">
				<div class="spacer"></div>
				<div class="spacer"></div>
				<span class="label"><label onclick="javascript:dialog.showContextHelp(arguments[0], 8023)"><fmt:message key="dialog.admin.groups.addressPermissions" /></label></span>
				<div dojoType="ingrid:SplitContainer" id="groupDataAddresses" persist="false" orientation="horizontal" layoutAlign="client" templateCssPath="js/dojo/widget/templates/SplitContainer.css">
		            <!-- LEFT HAND SIDE CONTENT BLOCK 3 START -->
		            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:auto;" sizeShare="38">
						<div class="inputContainer grey noSpaceBelow">
							<div dojoType="ContentPane" id="treeContainerAddresses">
								<!-- tree components -->
								<div dojoType="ingrid:TreeController" widgetId="treeControllerAddresses"></div>
								<div dojoType="ingrid:TreeListener" widgetId="treeListenerAddresses"></div>	
								<div dojoType="ingrid:TreeDocIcons" widgetId="treeDocIconsAddresses"></div>	
								<div dojoType="ingrid:TreeDecorator" listener="treeListenerAddresses"></div>
		                  
								<!-- tree -->
								<div dojoType="ingrid:Tree" listeners="treeControllerAddresses;treeListenerAddresses;treeDocIconsAddresses" widgetId="treeAddresses">
								</div>
							</div>
							<div class="spacer"></div>
						</div>
					</div>
					<!-- LEFT HAND SIDE CONTENT BLOCK 3 END -->
		
		            <!-- RIGHT HAND SIDE CONTENT BLOCK 3 START -->
		            <div dojoType="ContentPane" class="inputContainer noSpaceBelow" style="overflow:hidden;" sizeshare="62">
  						<div class="selectEntryBtn">
							<button dojoType="ingrid:Button" id="addAddressButton" onClick="javascript:adminGroupScope.addAddress();">&nbsp;>&nbsp;</button>
						</div>


						<div id="groupDataAddressesData" class="inputContainer grey field">
							<div class="tableContainer third2 rows7">
								<table id="groupDataRightsAddressesList" dojoType="ingrid:FilteringTable" minRows="7" headClass="fixedHeader" cellspacing="0" class="filteringTable interactive">
									<thead>
										<tr>
											<th nosort="true" sort="asc" field="title" dataType="String" width="358"><fmt:message key="dialog.admin.groups.addressName" /></th>
											<th nosort="true" field="single" dataType="String" width="85"><fmt:message key="dialog.admin.groups.addressSingle" /></th>
											<th nosort="true" field="tree" dataType="String" width="85"><fmt:message key="dialog.admin.groups.addressTree" /></th>
                                            <th nosort="true" field="subnode" dataType="String" width="85"><fmt:message key="dialog.admin.groups.addressSubNode" /></th>
										</tr>
									</thead>
									<tbody>
									</tbody>
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
</div>
<!-- CONTENT END -->

</body>
</html>
