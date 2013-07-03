<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.thesaurus.terms.link.assistant" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script type="text/javascript">

init = function() {
//	showStatus("<fmt:message key='sns.loadingHint' />");	
	showLoadingZone();
	
	createTables();

	// Build the lists from the main mdek app
	var analyzedFields =
		["objectName", "generalDesc", "generalShortDesc", "spatialRefExplanation", "timeRefExplanation",
		 "extraInfoPurpose", "extraInfoUse", "ref5Explanation", "ref3History", "ref3Explanation",
		 "ref2Explanation", "ref1BasisText", "ref1DataBasisText", "ref4Explanation"];

	var queryTerm = "";
	dojo.forEach(analyzedFields, function(item) {
			var val = dijit.byId(item).getValue();
			if (val) {
				queryTerm += val+" ";
			}
	});

//	SNSService.getSimilarDescriptors(queryTerm, {
	SNSService.getTopicsForText(queryTerm, 100, {
		//preHook:disableUiElements,
		//postHook:enableUiElements,
		callback:function(res) {
			if (res && res.length > 0) {
				// store length since it will be modified with empty row items later
				var resultsNum = res.length;
				var keywordTable = dijit.byId("keywordsList");
				
				// add Inspire Topics
				var inspireTopics = getInspireTopics(res);
				res = res.concat(inspireTopics);
				UtilList.addSNSTopicLabels(res);
				UtilStore.updateWriteStore("keywordsList", res);
				// showStatus("");
				dojo.byId("numberOfTerms").innerHTML = "<fmt:message key='sns.numberOfTerms' />"+" "+resultsNum;
			} else {
				// Show status that no results were found?
				// showStatus("<fmt:message key='sns.noSimilarTermsHint' />");
			}
			hideLoadingZone();
		},
		timeout:0,
		errorHandler:function(msg) {
			if (msg == "SNS_TIMEOUT") {
				dialog.show("<fmt:message key='general.error' />", "<fmt:message key='sns.timeoutError' />", dialog.WARNING);
			}
			hideLoadingZone();
		}
	});
}

dojo.addOnLoad(function() {
	init();
});

function showLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("thesLoadingZone"), "visible");
	dojo.byId('thesLoadingZone').style.visibility = "visible";
}

function hideLoadingZone() {
    //dojo.html.setVisibility(dojo.byId("thesLoadingZone"), "hidden");
	dojo.byId('thesLoadingZone').style.visibility = "hidden";
}

function disableUiElements() {
	dijit.byId("dlgThes_addSelectedButton").disable();
	dijit.byId("dlgThes_addAllButton").disable();
	dijit.byId("dlgThes_removeAllButton").disable();
	dijit.byId("dlgThes_removeSelectedButton").disable();
	dijit.byId("applyChangesButton").disable();
}

function enableUiElements() {
	dijit.byId("dlgThes_addSelectedButton").enable();
	dijit.byId("dlgThes_addAllButton").enable();
	dijit.byId("dlgThes_removeAllButton").enable();
	dijit.byId("dlgThes_removeSelectedButton").enable();
	dijit.byId("applyChangesButton").enable();
}


function showStatus(msg) {
	var status = dojo.byId("statusText");
	if (status) {
		status.innerHTML = msg;
	}
}

function createTables() {
	var keywordsListStructure = [
		{field: 'label',name: 'label',width: '220px'},
		{field: 'sourceString',name: 'sourceString',width: '60px'}
	];
    createDataGrid("keywordsList", null, keywordsListStructure, null);
	
    createDataGrid("resultList", null, keywordsListStructure, null);
	
}



// 'Add selected topics' Button onClick function.
//
// This function moves the selected topics from the selection list (left) to the result list (right)
addSelected = function() {
	var selectedTopics = UtilGrid.getSelectedData("keywordsList");
	if (selectedTopics) {
		dojo.forEach(selectedTopics, function(item) {
			UtilGrid.addTableDataRow("resultList", item);
		});
        UtilGrid.removeTableDataRow("keywordsList", UtilGrid.getSelectedRowIndexes("keywordsList"));
	}
}


// 'Add all topics' Button onClick function.
//
// This function moves all topics from the selection list (left) to the result list (right)
addAll = function() {
	var topics = UtilGrid.getTableData("keywordsList");
	if (topics) {
		/*dojo.forEach(topics, function(item) {
			UtilGrid.addTableDataRow("resultList", item);
		});*/
        UtilGrid.setTableData("resultList", topics);
        UtilGrid.setTableData("keywordsList", []);
	}
}


// 'Remove selected topics' Button onClick function.
//
// This function moves the selected topics from the result list (right) to the selection list (left)
removeSelected = function() {
	var selectedTopics = UtilGrid.getSelectedData("resultList");
	if (selectedTopics) {
		dojo.forEach(selectedTopics, function(item) {
            UtilGrid.addTableDataRow("keywordsList", item);
		});
        UtilGrid.removeTableDataRow("resultList", UtilGrid.getSelectedRowIndexes("resultList"));
	}
}


// 'Remove all topics' Button onClick function.
//
// This function moves all topics from the result list (right) to the selection list (left)
removeAll = function() {
	var topics = UtilGrid.getTableData("resultList");
	if (topics) {
        UtilGrid.setTableData("keywordsList", topics);
        UtilGrid.setTableData("resultList", []);
	}
}


// Finish Button onClick function.
//
// This function copies the descriptor list to the main mdek topic list
applyChanges = function() {
	var resultTopics = UtilGrid.getTableData("resultList");

	if (resultTopics) {
		dojo.forEach(resultTopics, function(topic) {
			if (topic.source == "INSPIRE") {
				UtilThesaurus.addInspireTopics([topic.title]);
			} else {
				UtilThesaurus.addDescriptors([topic], "thesaurusTerms");
				if (dojo.every(UtilGrid.getTableData("thesaurusTerms"), function(item){ return item.title != topic.title; })) {
					UtilGrid.addTableDataRow("thesaurusTerms", topic);
				}
			}
		});
	}
	dijit.byId("pageDialog").hide();
}

</script>

</head>
<body>

  <div id="keywords" class="">
    <div id="winNavi" style="top:0;">
		<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=maintanance-of-objects-5#maintanance-of-objects-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	  </div>
	  <div id="keywordsContent" class="content">

      <!-- LEFT HAND SIDE CONTENT START -->
      <span class="inputContainer field grey" style="padding:10px">

      <span class="outer" style="width:45%;">
          <div>
              <span class="label">
                  <fmt:message key="dialog.thesaurusAssist.result" />
              </span>
              <div class="tableContainer">
                  <div id="keywordsList" autoHeight="10" class="hideTableHeader">
                  </div>
              </div>
          </div>
      </span>

      <span class="outer" style="width:10%;"><div>
          <span class="buttonCol" style="margin:80px -4px 0px;">
			<div dojoType="dijit.form.Button" id="dlgThes_addSelectedButton" onClick="addSelected">&nbsp;>&nbsp;</div>
			<div dojoType="dijit.form.Button" id="dlgThes_addAllButton" onClick="addAll">>></div>
			<div dojoType="dijit.form.Button" id="dlgThes_removeAllButton" onClick="removeAll"><<</div>
			<div dojoType="dijit.form.Button" id="dlgThes_removeSelectedButton" onClick="removeSelected">&nbsp;<&nbsp;</div>
          </span>
          </div>
        </span>
        <span class="outer" style="width:45%;"><div>
          <span class="label"><fmt:message key="dialog.thesaurusAssist.apply" /></span>
    	    <div class="tableContainer">
    	    	<div id="resultList" autoHeight="10" class="hideTableHeader"></div>
			</div>
            </div>
        </span>

        <div class="fill"></div>
        <span id="numberOfTerms"><fmt:message key="dialog.thesaurusAssist.num" /></span>
      </span>
      </div>
      
      <div class="inputContainerFooter">
        <span class="button">
	        <span style="float:right;"><button dojoType="dijit.form.Button" id="applyChangesButton" onClick="applyChanges"><fmt:message key="dialog.thesaurusAssist.apply" /></button></span>

			<span id="thesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
				<img id="thesImageZone" src="img/ladekreis.gif" />
			</span>

			<span id="statusText" style="float:left; padding-top:6px;"></span>
            <div class="fill"></div>
        </span>
  	  </div>
  	  <div class="fill"></div>
  	  <!-- LEFT HAND SIDE CONTENT END -->
    </div>
</body>
</html>
