<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
<script type="text/javascript">

require([
    "dojo/on",
    "dojo/dom",
    "dojo/topic",
    "ingrid/hierarchy/detail_helper",
    "dijit/registry",
    "ingrid/IgeActions",
    "ingrid/utils/Catalog"
], function(on, dom, topic, detailHelper, registry, IgeActions, UtilCatalog) {

        on(_container_, "Load", function() {
            var nodeDataNew = IgeActions._getData();
            var nodeOldId = this.customParams.selectedNodeId;

            AddressService.getPublishedAddressData(nodeOldId, {
                callback: function(res) {
                    renderNodeData(res, nodeDataNew);

                    console.log("Publishing event: '/afterInitDialog/AddressCompare'");
                    topic.publish("/afterInitDialog/AddressCompare");
                },
                errorHandler: function(message) {
                    console.debug("Error in mdek_compare_view_address_dialog.jsp: Error while waiting for published nodeData: " + message);
                    displayErrorMessage(message);
                }
            });
        });

        function renderNodeData(nodeDataOld, nodeDataNew) {
            renderSectionTitel("<fmt:message key='dialog.compare.address.address' />");
            renderText(detailHelper.renderAddressEntry(nodeDataOld), detailHelper.renderAddressEntry(nodeDataNew));
            // also compare checkbox in compare view !
            renderTextWithTitle(nodeDataOld.hideAddress ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", nodeDataNew.hideAddress ? "<fmt:message key='general.yes' />" : "<fmt:message key='general.no' />", "<fmt:message key='ui.adr.general.hideAddress' />");

            renderSectionTitel("<fmt:message key='ui.adr.thesaurus.title' />");
            renderList(nodeDataOld.thesaurusTermsTable, nodeDataNew.thesaurusTermsTable, "<fmt:message key='ui.adr.thesaurus.terms' />", "title");

            // administrative data
            renderSectionTitel("<fmt:message key='dialog.compare.address.administrative' />");
            renderTextWithTitle(nodeDataOld.uuid, nodeDataNew.uuid, "<fmt:message key='dialog.compare.address.id' />");
            renderTextWithTitle(UtilCatalog.catalogData.catalogName, UtilCatalog.catalogData.catalogName, "<fmt:message key='dialog.compare.address.catalog' />");
        }

        function renderSectionTitel(val) {
            dom.byId("diffContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
            dom.byId("oldContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
            dom.byId("currentContent").innerHTML += "<br/><h2><u>" + val + "</u></h2><br/><br/>";
        }

        function renderTextWithTitle(oldVal, newVal, title) {
            if (!detailHelper.isValid(oldVal) && !detailHelper.isValid(newVal)) {
                return;
            }

            if (oldVal === null) {
                oldVal = "";
            }
            if (newVal === null) {
                newVal = "";
            }

            oldVal += "";
            newVal += "";
            dom.byId("diffContent").innerHTML += "<strong>" + title + "</strong><p>" + diffString(oldVal, newVal) + "</p><br/>";
            dom.byId("oldContent").innerHTML += "<strong>" + title + "</strong><p>" + oldVal.replace(/\n/g, "<br />") + "</p><br/>";
            dom.byId("currentContent").innerHTML += "<strong>" + title + "</strong><p>" + newVal.replace(/\n/g, "<br />") + "</p><br/>";
        }

        function renderText(oldVal, newVal) {
            if (oldVal === null) {
                oldVal = "";
            }
            if (newVal === null) {
                newVal = "";
            }

            oldVal += "";
            newVal += "";
            var str = diffString(oldVal, newVal);

            //    console.debug(oldVal);
            //    console.debug(newVal);
            //    console.debug(str);

            // Replace newlines with <br /> and remove EOL chars
            // and fix html tags for rendering (e.g. "&lt;strong&gt;" -> "<strong>")
            str = str.replace(/\n/g, '<br />').replace(/&para;/g, '').replace(/&lt;/g, '<').replace(/&gt;/g, '>');
            //    console.debug(str);
            dom.byId("diffContent").innerHTML += "<p>" + str + "</p><br/>";
            dom.byId("oldContent").innerHTML += "<p>" + oldVal.replace(/\n/g, '<br />') + "</p><br/>";
            dom.byId("currentContent").innerHTML += "<p>" + newVal.replace(/\n/g, '<br />') + "</p><br/>";
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
            for (var i = 0; i < list.length; i++) {
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
            for (var i = 0; i < diffList.length; i++) {
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
            var t, valList;
            if (oldList && oldList.length > 0) {
                t = buildListHead(title);
                valList = buildListBody(oldList, rowProperty, renderFunction);

                if (valList != "") {
                    dom.byId("oldContent").innerHTML += t + valList + "</p><br/>";
                }
            }

            if (newList && newList.length > 0) {
                t = buildListHead(title);
                valList = buildListBody(newList, rowProperty, renderFunction);

                if (valList != "") {
                    dom.byId("currentContent").innerHTML += t + valList + "</p><br/>";
                }
            }

            var diff = compareTable(oldList, newList, rowProperty);

            if ((oldList && oldList.length > 0) || (newList && newList.length > 0)) {
                t = buildListHead(title);
                valList = buildListBodyForDiff(diff, rowProperty, renderFunction);

                if (valList != "") {
                    dom.byId("diffContent").innerHTML += t + valList + "</p><br/>";
                }
            }
        }

        // Compare two tables containing objects with 'properties'.
        // The result is returned as an object containing the following information:
        // { eq:  array of objects that are in table1 and table2,
        //   ins: array of objects that are in table2 but not in table1,
        //   del: array of objects that are in table1 but not in table2 } 
        function compareTable(table1, table2, properties) {
            var diff = {
                eq: [],
                ins: [],
                del: []
            };

            // Iterate over all objects in table1 and compare them to the objects in table2
            // If a match is found, add the object to diff.eq
            // Otherwise add it to diff.del
            var i, j;
            for (i in table1) {
                var obj1 = table1[i];
                var found = false;

                for (j in table2) {
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
            for (j in table2) {
                var obj = table2[j];
                if (!arrayContains(diff.eq, obj)) {
                    diff.ins.push(obj);
                }
            }

            return diff;
        }

        // Compare two objects according to their properties
        function compareObj(obj1, obj2, properties) {
            if (properties === null) {
                return (obj1 + "" == obj2 + "");
            }
            if (typeof(properties) == "string") {
                properties = [properties];
            }

            for (var i in properties) {
                var prop1 = obj1[properties[i]];
                var prop2 = obj2[properties[i]];
                // Compare as strings so date objects are handled properly
                if (prop1 + "" != prop2 + "") {
                    return false;
                }
            }
            return true;
        }

        // Returns whether the array 'arr' contains the object 'obj'
        function arrayContains(arr, obj) {
            var len = arr.length;
            for (var i = 0; i < len; i++) {
                if (arr[i] === obj) {
                    return true;
                }
            }
            return false;
        }
    });

</script>
</head>

<body>
  <div data-dojo-type="dijit/layout/ContentPane" class="">
      <div id="winNavi">
            <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=hierarchy-maintenance-3#hierarchy-maintenance-3', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
      </div>
        <!-- MAIN TAB CONTAINER START -->
        <div id="compareViews" data-dojo-type="dijit/layout/TabContainer" selectedChild="diffView" style="height:600px; width:100%;" >
          <!-- MAIN TAB 1 START -->
            <div id="diffView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.compare.compare" />">
              <div id="diffContent" class="inputContainer field grey"></div>
              <div id="diffContentLegend" class="inputContainer field grey"><span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.insertedText" /><br/>
                        <span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.deletedText" /></div>
            </div>
          <!-- MAIN TAB 1 END -->
            
          <!-- MAIN TAB 2 START -->
            <div id="oldView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.compare.original" />">
              <div id="oldContent" class="inputContainer field grey"></div>
            </div>
          <!-- MAIN TAB 2 END -->

          <!-- MAIN TAB 3 START -->
            <div id="currentView" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder" title="<fmt:message key="dialog.compare.modified" />">
              <div id="currentContent" class="inputContainer field grey"></div>
            </div>
          <!-- MAIN TAB 3 END -->

        </div>
        <!-- MAIN TAB CONTAINER END -->
  </div>
  <!-- CONTENT END -->
</body>
</html>
