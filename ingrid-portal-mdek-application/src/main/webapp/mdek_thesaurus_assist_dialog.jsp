<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Verschlagwortungsassistent</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script type="text/javascript">
_container_.addOnLoad(function() {
	init();
});

function showLoadingZone() {
    dojo.html.setVisibility(dojo.byId("thesLoadingZone"), "visible");
}

function hideLoadingZone() {
    dojo.html.setVisibility(dojo.byId("thesLoadingZone"), "hidden");
}

function disableUiElements() {
	dojo.widget.byId("addSelectedButton").disable();
	dojo.widget.byId("addAllButton").disable();
	dojo.widget.byId("removeAllButton").disable();
	dojo.widget.byId("removeSelectedButton").disable();
	dojo.widget.byId("applyChangesButton").disable();
}

function enableUiElements() {
	dojo.widget.byId("addSelectedButton").enable();
	dojo.widget.byId("addAllButton").enable();
	dojo.widget.byId("removeAllButton").enable();
	dojo.widget.byId("removeSelectedButton").enable();
	dojo.widget.byId("applyChangesButton").enable();
}


function showStatus(msg) {
	var status = dojo.byId("statusText");
	if (status) {
		status.innerHTML = msg;
	}
}


init = function() {
//	showStatus(message.get("sns.loadingHint"));	
	showLoadingZone();

	// Build the lists from the main mdek app
	var analyzedFields =
		["objectName", "generalDesc", "generalShortDesc", "spatialRefExplanation", "timeRefExplanation",
		 "extraInfoPurpose", "extraInfoUse", "ref5Explanation", "ref3History", "ref3Explanation",
		 "ref2Explanation", "ref1BasisText", "ref1DataBasisText", "ref4Explanation"];

	var queryTerm = "";

	dojo.lang.forEach(analyzedFields, function(item) {
			var val = dojo.widget.byId(item).getValue();
			if (val) {
				queryTerm += val+" ";
			}
	});

//	SNSService.getSimilarDescriptors(queryTerm, {
	SNSService.getTopicsForText(queryTerm, 100, {
		preHook:disableUiElements,
		postHook:enableUiElements,
		callback:function(res) {
			if (res && res.length > 0) {
				var keywordTable = dojo.widget.byId("keywordsList");
				
				// add Inspire Topics
				var inspireTopics = getInspireTopics(res);
				res = res.concat(inspireTopics);
				
				UtilList.addSNSTopicLabels(res);
				UtilList.addTableIndices(res);
				keywordTable.store.setData(res);
				// showStatus("");
				dojo.byId("numberOfTerms").innerHTML = message.get("sns.numberOfTerms")+" "+res.length;
			} else {
				// Show status that no results were found?
				// showStatus(message.get("sns.noSimilarTermsHint"));
			}
			hideLoadingZone();
		},
		timeout:0,
		errorHandler:function(msg) {
			if (msg == "SNS_TIMEOUT") {
				dialog.show(message.get("general.error"), message.get("sns.timeoutError"), dialog.WARNING);
			}
			hideLoadingZone();
		}
	});
}

// moved to init.js so that it can be called from other methods also
/*getInspireTopics = function(topics) {
	var inspireArray = new Array();
	dojo.lang.forEach(topics, function(topic) {
		if (topic.inspireList.length > 0) {
			dojo.lang.forEach(topic.inspireList, function(inspireTopic) {			
				var obj = new Object();
				obj.title = inspireTopic;
				obj.source = "INSPIRE";
				inspireArray.push(obj);
			});
		}
	});
	return inspireArray;
}*/

// 'Add selected topics' Button onClick function.
//
// This function moves the selected topics from the selection list (left) to the result list (right)
addSelected = function() {
	var keywordTable = dojo.widget.byId("keywordsList"); 
	var resultTable = dojo.widget.byId("resultList");

	var selectedTopics = keywordTable.getSelectedData();
	if (selectedTopics) {
		dojo.lang.forEach(selectedTopics, function(item) {
			keywordTable.store.removeData(item);
			resultTable.store.addData(item);
		});
	}
}


// 'Add all topics' Button onClick function.
//
// This function moves all topics from the selection list (left) to the result list (right)
addAll = function() {
	var keywordTable = dojo.widget.byId("keywordsList"); 
	var resultTable = dojo.widget.byId("resultList");

	var topics = keywordTable.store.getData();
	if (topics) {
		dojo.lang.forEach(topics, function(item) {
			keywordTable.store.removeData(item);
			resultTable.store.addData(item);
		});
	}
}


// 'Remove selected topics' Button onClick function.
//
// This function moves the selected topics from the result list (right) to the selection list (left)
removeSelected = function() {
	var keywordTable = dojo.widget.byId("keywordsList"); 
	var resultTable = dojo.widget.byId("resultList");

	var selectedTopics = resultTable.getSelectedData();
	if (selectedTopics) {
		dojo.lang.forEach(selectedTopics, function(item) {
			resultTable.store.removeData(item);
			keywordTable.store.addData(item);
		});
	}
}


// 'Remove all topics' Button onClick function.
//
// This function moves all topics from the result list (right) to the selection list (left)
removeAll = function() {
	var keywordTable = dojo.widget.byId("keywordsList"); 
	var resultTable = dojo.widget.byId("resultList");

	var topics = resultTable.store.getData();
	if (topics) {
		dojo.lang.forEach(topics, function(item) {
			resultTable.store.removeData(item);
			keywordTable.store.addData(item);
		});
	}
}


// Finish Button onClick function.
//
// This function copies the descriptor list to the main mdek topic list
applyChanges = function() {
	var resultTopics = dojo.widget.byId("resultList").getData();
	var destStore = dojo.widget.byId("thesaurusTerms").store;
	var destInspireStore = dojo.widget.byId("thesaurusInspire").store;

	if (resultTopics) {
		dojo.lang.forEach(resultTopics, function(topic) {
			if (topic.source == "INSPIRE") {
				addInspireTopics([topic.title]);
			} else {
				addDescriptors([topic], destStore);
				/*if (dojo.lang.every(destStore.getData(), function(item){ return item.topicId != topic.topicId; })) {
					topic.Id = UtilStore.getNewKey(destStore);
					destStore.addData( topic );
				}*/
			}
		});
	}
	_container_.closeWindow();
}

</script>

</head>
<body>

<div dojoType="ContentPane">

  <div id="keywords" class="contentBlockWhite top fullBlock">
    <div id="winNavi">
		<a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?hkey=maintanance-of-objects-5#maintanance-of-objects-5', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
	  </div>
	  <div id="keywordsContent" class="content">

      <!-- LEFT HAND SIDE CONTENT START -->
      <div class="spacer"></div>
      <div class="spacer"></div>
      <div class="inputContainer field grey fullField noSpaceBelow">

        <span class="entry first">
          <span class="label"><fmt:message key="dialog.thesaurusAssist.result" /></span>
    	    <div class="tableContainer headHiddenRows10 third">
    	    <table id="keywordsList" dojoType="ingrid:FilteringTable" minRows="10" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive relativePos">
    	      <thead>
    		      <tr>
          			<th nosort="true" field="label" dataType="String">Name</th>
          			<th nosort="true" field="sourceString" dataType="String">Typ</th>
    		      </tr>
    	      </thead>
    	      <colgroup>
    			<col width="75%">
    			<col width="25%">
  			  </colgroup>
    	      <tbody>
    	      </tbody>
    	    </table>
        	</div>
        </span>

        <span class="entry">
          <span class="buttonCol" style="margin:80px -4px 0px;">
			<button dojoType="ingrid:Button" id="addSelectedButton" onClick="addSelected">&nbsp;>&nbsp;</button>
			<button dojoType="ingrid:Button" id="addAllButton" onClick="addAll">>></button>
			<button dojoType="ingrid:Button" id="removeAllButton" onClick="removeAll"><<</button>
			<button dojoType="ingrid:Button" id="removeSelectedButton" onClick="removeSelected">&nbsp;<&nbsp;</button>
          </span>
        </span>

        <span class="entry">
          <span class="label"><fmt:message key="dialog.thesaurusAssist.apply" /></span>
    	    <div class="tableContainer headHiddenRows10 third">
    	    <table id="resultList" dojoType="ingrid:FilteringTable" minRows="10" headClass="hidden" cellspacing="0" class="filteringTable nosort interactive relativePos">
    	      <thead>
    		      <tr>
          			<th nosort="true" field="label" dataType="String">Name</th>
          			<th nosort="true" field="sourceString" dataType="String">Typ</th>
    		      </tr>
    	      </thead>
    	      <colgroup>
    			<col width="75%">
    			<col width="25%">
  			  </colgroup>
    	      <tbody>
    	      </tbody>
    	    </table>
			</div>
        </span>

        <div class="fill spacer"></div>
        <span id="numberOfTerms"><fmt:message key="dialog.thesaurusAssist.num" /></span>
        <div class="spacerField"></div>
      </div>
      
      <div class="inputContainer noSpaceBelow">
        <span class="button w644" style="height:20px !important;">
	        <span style="float:right;"><button dojoType="ingrid:Button" id="applyChangesButton" onClick="applyChanges"><fmt:message key="dialog.thesaurusAssist.apply" /></button></span>

			<span id="thesLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden">
				<img id="thesImageZone" src="img/ladekreis.gif" />
			</span>

			<span id="statusText" style="float:left; padding-top:6px;"></span>
        </span>
  	  </div>
      <!-- LEFT HAND SIDE CONTENT END -->

    </div>
  </div>
</div>
</body>
</html>
