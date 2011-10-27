<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<!--<script type="text/javascript" src="js/detail_helper.js"></script>-->
<script type="text/javascript">

dojo.addOnLoad(function() {
// NOTICE: connect again onLoad does NOT work in IE7 ! Why again ?
//	dojo.connect(dijit.byId("pageDialog"), "onLoad", function(){
		if (dijit.byId("pageDialog").customParams.useDirtyData == true) {
			// get dirty data from proxy
			renderNodeData(udkDataProxy._getData());
		} else {
			// make sure the address forms are already loaded since they are needed
			// for syslist values for example
			ingridAddressLayout.create();
			
			// Construct an MdekDataBean from the available data
			var node = dijit.byId(dijit.byId("pageDialog").customParams.selectedNode);
			
			AddressService.getAddressData(node.id[0], "false",
				{
					callback:function(res) { renderNodeData(res); },
	//				timeout:5000,
					errorHandler:function(message) {
						displayErrorMessage(new Error(message));
	//					dojo.debug("Error in mdek_detail_view_adress_dialog.html: Error while waiting for nodeData: " + message);
					}
				}
			);
		}
//	});
});

function renderNodeData(nodeData) {
	renderSectionTitel("<fmt:message key='dialog.compare.address.address' />");
	nodeData.organisation = getOrganisations(nodeData);
	renderText(detailHelper.renderAddressEntry(nodeData).replace(/\n/g, '<br />'));
	
	// administrative data
	renderSectionTitel("<fmt:message key='dialog.compare.address.administrative' />");
	renderTextWithTitle(nodeData.uuid, "<fmt:message key='dialog.compare.address.id' />");
	renderTextWithTitle(catalogData.catalogName, "<fmt:message key='dialog.compare.address.catalog' />");

}

function getOrganisations(nodeData) {
	console.debug("addressClass: " + nodeData.addressClass);
    if (nodeData.addressClass == 0) {
        id = "headerAddressType0Unit";
    } else if (nodeData.addressClass == 1) {
        return dijit.byId("headerAddressType1Institution").getValue() + "\n" + dijit.byId("headerAddressType1Unit").getValue();
    } else if (nodeData.addressClass == 2) {
        id = "headerAddressType2Institution";
    } else if (nodeData.addressClass == 3) {
        id = "headerAddressType3Institution";
    }
	console.debug("id = " + id);
    return dijit.byId(id).getValue();
}

function renderSectionTitel(val) {
	dojo.byId("detailViewContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/>";
}

function renderTextWithTitle(val, title) {
	if (detailHelper.isValid(val)) {
		// Replace newlines with <br/>
		val = val.replace(/\n/g, "<br/>");
		if (detailHelper.isValid(title)) {
			dojo.byId("detailViewContent").innerHTML += "<p><strong>" + title + "</strong><br/>" + val + "</p><br/>";
		} else {
			dojo.byId("detailViewContent").innerHTML += "<p>" + val + "</p><br/>";
		}
	}
}

function renderText(val) {
	if (val && val.length>0) {
		// Replace newlines with <br/>
		val = val.replace(/\n/g, "<br/>");
		dojo.byId("detailViewContent").innerHTML += "<p>" + val + "</p><br/>";
	}
}


</script>
</head>

<body>
  <div dojoType="dijit.layout.ContentPane">
    <div id="contentPane" layoutAlign="client" class="contentBlockWhite top">
      <div id="winNavi">
        <a href="javascript:printDivContent('detailViewContent')" title="<fmt:message key="dialog.detail.print" />">[<fmt:message key="dialog.detail.print" />]</a>
  	  </div>
  	  <div id="dialogContent" class="content">
        <!-- MAIN TAB CONTAINER START -->
        <div class="spacer"></div>
      	<div id="detailViewContainer" dojoType="dijit.layout.TabContainer" doLayout="false" class="full" selectedChild="detailView">
          <!-- MAIN TAB 1 START -->
      		<div id="detailView" dojoType="dijit.layout.ContentPane" class="blueTopBorder" style="height: 500px;" title="<fmt:message key="dialog.detail.title" />">
              <div id="detailViewContent" class="detailViewContentContainer w652 grey"></div>
      		</div>
          <!-- MAIN TAB 1 END -->
      	</div>
        <!-- MAIN TAB CONTAINER END -->
      </div>
    </div>
  </div>
  <!-- CONTENT END -->
</body>
</html>
