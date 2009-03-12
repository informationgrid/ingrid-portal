<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<script type="text/javascript" src="js/detail_helper.js"></script>
<script type="text/javascript">
_container_.addOnLoad(function() {
	var tree = dojo.widget.byId("tree");
	var nodeDataNew = udkDataProxy._getData();
	var nodeOld = dojo.widget.byId(_container_.customParams.selectedNodeId);

	AddressService.getPublishedAddressData(nodeOld.id,
		{
			callback:function(res) { renderNodeData(res, nodeDataNew); },
//			timeout:5000,
			errorHandler:function(message) {dojo.debug("Error in mdek_compare_view_address_dialog.jsp: Error while waiting for published nodeData: " + message); }
		}
	);
});

function renderNodeData(nodeDataOld, nodeDataNew) {
	renderSectionTitel(message.get("dialog.compare.address.address"));
	renderText(detailHelper.renderAddressEntry(nodeDataOld), detailHelper.renderAddressEntry(nodeDataNew));

	renderSectionTitel(message.get("ui.adr.thesaurus.title"));
	renderList(nodeDataOld.thesaurusTermsTable, nodeDataNew.thesaurusTermsTable, message.get("ui.adr.thesaurus.terms"), "title");

	// administrative data
	renderSectionTitel(message.get("dialog.compare.address.administrative"));
	renderTextWithTitle(nodeDataOld.uuid, nodeDataNew.uuid, message.get("dialog.compare.address.id"));
	renderTextWithTitle(catalogData.catalogName, catalogData.catalogName, message.get("dialog.compare.address.catalog"));
}

function renderSectionTitel(val) {
	dojo.byId("diffContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
	dojo.byId("oldContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
	dojo.byId("currentContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
}

function renderTextWithTitle(oldVal, newVal, title) {
	if (!detailHelper.isValid(oldVal) && !detailHelper.isValid(newVal)) {
		return;
	}

	if (oldVal == null) {
		oldVal = "";
	}
	if (newVal == null) {
		newVal = "";
	}

	oldVal += "";
	newVal += "";
	dojo.byId("diffContent").innerHTML += "<strong>" + title + "</strong><p>" + diffString(oldVal, newVal) + "</p>";
	dojo.byId("oldContent").innerHTML += "<strong>" + title + "</strong><p>" + oldVal.replace(/\n/g, "<br />") + "</p>";
	dojo.byId("currentContent").innerHTML += "<strong>" + title + "</strong><p>" + newVal.replace(/\n/g, "<br />") + "</p>";
}

function renderText(oldVal, newVal) {
	if (oldVal == null) {
		oldVal = "";
	}
	if (newVal == null) {
		newVal = "";
	}

	oldVal += "";
	newVal += "";
	var str = diffString(oldVal, newVal);
	// Replace newlines with <br /> and remove EOL chars
	str = str.replace(/\n/g, '<br />').replace(/&para;/g, '');
	dojo.byId("diffContent").innerHTML += "<p>" + str + "</p><br/>";
	dojo.byId("oldContent").innerHTML += "<p>" + oldVal.replace(/\n/g, '<br />') + "</p><br/>";
	dojo.byId("currentContent").innerHTML += "<p>" + newVal.replace(/\n/g, '<br />') + "</p><br/>";
}

function diffString(oldText, newText) {
	return WDiffString(oldText, newText);
}

function buildListHead(title) {
	var t = "<p>";
	if (detailHelper.isValid(title)) {
		t += "<strong>" + title + "</strong><br/>";
	}
	return t;
}

function buildListBody(list, rowProperty, renderFunction) {
	var valList = "";
	for (var i=0; i<list.length; i++) {
		var val = "";
		if (rowProperty) {
			val = list[i][rowProperty];
		} else {
			val = list[i];
		}
		if (renderFunction) {
			val = renderFunction.call(this, val);
		}
		if (val && val != "") {
			valList += val + "<br/>";
		}
	}
	return valList;
}

function buildListBodyForDiff(diff, rowProperty, renderFunction) {
	var diffList = diff.eq.concat(diff.ins.concat(diff.del));

	var valList = "";
	for (var i=0; i<diffList.length; i++) {
		var val = "";
		var prefix = "";
		var suffix = "";
		if (arrayContains(diff.ins, diffList[i])) {
			prefix = "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;'>";
			suffix = "</span>";
		} else if (arrayContains(diff.del, diffList[i])) {
			prefix = "<span style='font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;'>";
			suffix = "</span>";
		}

		if (rowProperty) {
			val = diffList[i][rowProperty];
		} else {
			val = diffList[i];
		}
		if (renderFunction) {
			val = renderFunction.call(this, val);
		}
		if (val && val != "") {
			valList += prefix + val + suffix + "<br/>";
		}
	}
	return valList;
}

function renderList(oldList, newList, title, rowProperty, renderFunction) {
	if (oldList && oldList.length > 0) {
		var t = buildListHead(title);
		var valList = buildListBody(oldList, rowProperty, renderFunction);

		if (valList != "") {
			dojo.byId("oldContent").innerHTML += t + valList + "</p><br/>";
		}
	}	

	if (newList && newList.length > 0) {
		var t = buildListHead(title);
		var valList = buildListBody(newList, rowProperty, renderFunction);

		if (valList != "") {
			dojo.byId("currentContent").innerHTML += t + valList + "</p><br/>";
		}
	}	

	var diff = compareTable(oldList, newList, rowProperty);

	if ((oldList && oldList.length > 0) || (newList && newList.length > 0)) {
		var t = buildListHead(title);
		var valList = buildListBodyForDiff(diff, rowProperty, renderFunction);

		if (valList != "") {
			dojo.byId("diffContent").innerHTML += t + valList + "</p><br/>";
		}
	}
}

// Compare two tables containing objects with 'properties'.
// The result is returned as an object containing the following information:
// { eq:  array of objects that are in table1 and table2,
//   ins: array of objects that are in table2 but not in table1,
//   del: array of objects that are in table1 but not in table2 } 
function compareTable(table1, table2, properties) {
	var diff = {eq:[], ins:[], del:[]};

	// Iterate over all objects in table1 and compare them to the objects in table2
	// If a match is found, add the object to diff.eq
	// Otherwise add it to diff.del
	for (var i in table1) {
		obj1 = table1[i];
		var found = false;

		for (var j in table2) {
			var obj2 = table2[j];
			if (!arrayContains(diff.eq, obj2) && compareObj(obj2, obj1, properties)) {
				diff.eq.push(obj2);
				found = true;
				break;
			}
		}
		if (!found) {
			diff.del.push(obj1);
		}
	}

	// All remaining elements that have not been added to diff.eq or diff.del are new
	// objects and have to be added to diff.ins
	for (var j in table2) {
		var obj = table2[j];
		if (!arrayContains(diff.eq, obj)) {
			diff.ins.push(obj);
		}
	}

	return diff;
}

// Compare two objects according to their properties
function compareObj(obj1, obj2, properties) {
	if (properties == null) {
		return (obj1+"" == obj2+"");
	}
	if (typeof(properties) == "string") {
		properties = [properties];
	}

	for (var i in properties) {
		var prop1 = obj1[properties[i]];
		var prop2 = obj2[properties[i]];
		// Compare as strings so date objects are handled properly
		if (prop1+"" != prop2+"") {
			return false;
		}
	}
	return true;
}

// Returns whether the array 'arr' contains the object 'obj'
function arrayContains(arr, obj) {
	var len = arr.length;
	for (var i = 0; i < len; i++){
		if(arr[i]===obj){return true;}
	}
	return false;
}

</script>
</head>

<body>
  <div dojoType="ContentPane">
    <div id="contentPane" layoutAlign="client" class="contentBlockWhite top">
      <div id="winNavi">
        <a href="#" title="Hilfe">[?]</a>
  	  </div>
  	  <div id="dialogContent" class="content">
        <!-- MAIN TAB CONTAINER START -->
        <div class="spacer"></div>
      	<div id="compareViews" dojoType="ingrid:TabContainer" doLayout="false" class="full" selectedChild="diffView">
          <!-- MAIN TAB 1 START -->
      		<div id="diffView" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.compare.compare" />">
              <div id="diffContent" class="inputContainer field grey"></div>
              <div id="diffContentLegend" class="inputContainer field grey"><span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.insertedText" /><br/>
			  			<span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.deletedText" /></div>
      		</div>
          <!-- MAIN TAB 1 END -->
      		
          <!-- MAIN TAB 2 START -->
      		<div id="oldView" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.compare.original" />">
              <div id="oldContent" class="inputContainer field grey"></div>
      		</div>
          <!-- MAIN TAB 2 END -->

          <!-- MAIN TAB 3 START -->
      		<div id="currentView" dojoType="ContentPane" class="blueTopBorder" label="<fmt:message key="dialog.compare.modified" />">
              <div id="currentContent" class="inputContainer field grey"></div>
      		</div>
          <!-- MAIN TAB 3 END -->

      	</div>
        <!-- MAIN TAB CONTAINER END -->
  
      </div>
    </div>
  </div>
  <!-- CONTENT END -->
</body>
</html>
