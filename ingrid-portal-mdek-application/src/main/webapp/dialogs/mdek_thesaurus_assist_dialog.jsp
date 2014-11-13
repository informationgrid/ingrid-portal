<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
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
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title><fmt:message key="dialog.popup.thesaurus.terms.link.assistant" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script type="text/javascript">

var pageThesAssistDlg;

require([
    "dojo/_base/array",
    "dojo/on",
    "dojo/dom",
    "dojo/topic",
    "dojo/dom-style",
    "dijit/registry",
    "ingrid/utils/Grid",
    "ingrid/utils/List",
    "ingrid/utils/Store",
    "ingrid/utils/Thesaurus",
    "ingrid/utils/Events",
    "ingrid/dialog",
    "ingrid/layoutCreator"
], function(array, on, dom, topic, style, registry, UtilGrid, UtilList, UtilStore, UtilThesaurus, UtilEvents, dialog, layoutCreator) {

        on(_container_, "Load", function() {
            init();

			console.log("Publishing event: '/afterInitDialog/ThesaurusAssistant'");
		    topic.publish("/afterInitDialog/ThesaurusAssistant");
        });

        function init() {
            //  showStatus("<fmt:message key='sns.loadingHint' />");    
            showLoadingZone();

            createTables();

            // Build the lists from the main mdek app
            var analyzedFields =
                ["objectName", "generalDesc", "generalShortDesc", "spatialRefExplanation", "timeRefExplanation",
                "extraInfoPurpose", "extraInfoUse", "ref5Explanation", "ref3History", "ref3Explanation",
                "ref2Explanation", "ref1BasisText", "ref1DataBasisText", "ref4Explanation"
            ];

            var queryTerm = "";
            array.forEach(analyzedFields, function(item) {
                var val = registry.byId(item).getValue();
                if (val) {
                    queryTerm += val + " ";
                }
            });

	SNSService.getTopicsForText(queryTerm, 100, userLocale, {
                preHook: disableUiElements,
                postHook: enableUiElements,
                callback: function(res) {
                    if (res && res.length > 0) {
                        // store length since it will be modified with empty row items later
                        var resultsNum = res.length;

                        // add Inspire Topics
                        var inspireTopics = UtilThesaurus.getInspireTopics(res);
                        res = res.concat(inspireTopics);
                        UtilList.addSNSTopicLabels(res);
                        UtilStore.updateWriteStore("keywordsList", res);
                        showStatus("");
                        dom.byId("numberOfTerms").innerHTML = "<fmt:message key='sns.numberOfTerms' />" + " " + resultsNum;
                    } else {
                        // Show status that no results were found?
                        showStatus("<fmt:message key='sns.noSimilarTermsHint' />");
                    }
                    hideLoadingZone();
                },
                timeout: 0,
                errorHandler: function(msg) {
                    if (msg == "SNS_TIMEOUT") {
                        dialog.show("<fmt:message key='general.error' />", "<fmt:message key='sns.timeoutError' />", dialog.WARNING);
                    }
                    hideLoadingZone();
                }
            });
        }

        function showLoadingZone() {
            //dojo.html.setVisibility(dom.byId("thesLoadingZone"), "visible");
            style.set("thesLoadingZone", "visibility", "visible");
        }

        function hideLoadingZone() {
            style.set("thesLoadingZone", "visibility", "hidden");
        }

        function disableUiElements() {
            registry.byId("dlgThes_addSelectedButton").set("disabled", true);
            registry.byId("dlgThes_addAllButton").set("disabled", true);
            registry.byId("dlgThes_removeAllButton").set("disabled", true);
            registry.byId("dlgThes_removeSelectedButton").set("disabled", true);
            registry.byId("applyChangesButton").set("disabled", true);
        }

        function enableUiElements() {
            registry.byId("dlgThes_addSelectedButton").set("disabled", false);
            registry.byId("dlgThes_addAllButton").set("disabled", false);
            registry.byId("dlgThes_removeAllButton").set("disabled", false);
            registry.byId("dlgThes_removeSelectedButton").set("disabled", false);
            registry.byId("applyChangesButton").set("disabled", false);
        }


        function showStatus(msg) {
            var status = dom.byId("statusText");
            if (status) {
                status.innerHTML = msg;
            }
        }

        function createTables() {
            var keywordsListStructure = [{
                field: 'label',
                name: 'label',
                width: '220px'
            }, {
                field: 'sourceString',
                name: 'sourceString',
                width: '60px'
            }];
            layoutCreator.createDataGrid("keywordsList", null, keywordsListStructure, null);

            layoutCreator.createDataGrid("resultList", null, keywordsListStructure, null);

        }



        // 'Add selected topics' Button onClick function.
        //
        // This function moves the selected topics from the selection list (left) to the result list (right)
        function addSelected() {
            var selectedTopics = UtilGrid.getSelectedData("keywordsList");
            if (selectedTopics) {
                array.forEach(selectedTopics, function(item) {
                    UtilGrid.addTableDataRow("resultList", item);
                });
                UtilGrid.removeTableDataRow("keywordsList", UtilGrid.getSelectedRowIndexes("keywordsList"));
            }
        }


        // 'Add all topics' Button onClick function.
        //
        // This function moves all topics from the selection list (left) to the result list (right)
        function addAll() {
            var topics = UtilGrid.getTableData("keywordsList");
            if (topics) {
                array.forEach(topics, function(item) {
                    UtilGrid.addTableDataRow("resultList", item);
                });
                UtilGrid.setTableData("keywordsList", []);
            }
        }


        // 'Remove selected topics' Button onClick function.
        //
        // This function moves the selected topics from the result list (right) to the selection list (left)
        function removeSelected() {
            var selectedTopics = UtilGrid.getSelectedData("resultList");
            if (selectedTopics) {
                array.forEach(selectedTopics, function(item) {
                    UtilGrid.addTableDataRow("keywordsList", item);
                });
                UtilGrid.removeTableDataRow("resultList", UtilGrid.getSelectedRowIndexes("resultList"));
            }
        }


        // 'Remove all topics' Button onClick function.
        //
        // This function moves all topics from the result list (right) to the selection list (left)
        function removeAll() {
            var topics = UtilGrid.getTableData("resultList");
            if (topics) {
                array.forEach(topics, function(item) {
                    UtilGrid.addTableDataRow("keywordsList", item);
                });
                UtilGrid.setTableData("resultList", []);
            }
        }


        // Finish Button onClick function.
        //
        // This function copies the descriptor list to the main mdek topic list
        function applyChanges() {
    		if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/ThesaurusAssistant")) return;

            var resultTopics = UtilGrid.getTableData("resultList");

            if (resultTopics) {
                array.forEach(resultTopics, function(topic) {
                    if (topic.source == "INSPIRE") {
                        UtilThesaurus.addInspireTopics([topic.title]);
                    } else {
                        UtilThesaurus.addDescriptors([topic], "thesaurusTerms");
                        if (dojo.every(UtilGrid.getTableData("thesaurusTerms"), function(item) {
                            return item.title != topic.title;
                        })) {
                            UtilGrid.addTableDataRow("thesaurusTerms", topic);
                        }
                    }
                });
            }
            registry.byId("pageDialog").hide();
        }

        /**
         * PUBLIC METHODS
         */
        
        pageThesAssistDlg = {
            addSelected: addSelected,
            addAll: addAll,
            removeSelected: removeSelected,
            removeAll: removeAll,
            applyChanges: applyChanges
        };
    }
);
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
			<div data-dojo-type="dijit/form/Button" id="dlgThes_addSelectedButton" onclick="pageThesAssistDlg.addSelected()">&nbsp;>&nbsp;</div>
			<div data-dojo-type="dijit/form/Button" id="dlgThes_addAllButton" onclick="pageThesAssistDlg.addAll()">>></div>
			<div data-dojo-type="dijit/form/Button" id="dlgThes_removeAllButton" onclick="pageThesAssistDlg.removeAll()"><<</div>
			<div data-dojo-type="dijit/form/Button" id="dlgThes_removeSelectedButton" onclick="pageThesAssistDlg.removeSelected()">&nbsp;<&nbsp;</div>
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
	        <span style="float:right;"><button data-dojo-type="dijit/form/Button" id="applyChangesButton" onclick="pageThesAssistDlg.applyChanges()"><fmt:message key="dialog.thesaurusAssist.apply" /></button></span>

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
