<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
    
        <style>
            .row {
                padding: 10px;
                margin-bottom: 10px;
            }
            .row.active {
                background-color: #ADD9F9 !important;
            }
            .row .title {
                font-weight: bold;
            }
        </style>
        <script type="text/javascript">
        
        var pageCatBehaviours = _container_;
            
        require([
            "dojo/_base/array",
            "dojo/_base/lang",
            "dojo/on",
            "dojo/query",
            "dojo/dom",
            "dojo/dom-class",
            "dojo/dom-construct",
            "dojo/Deferred",
            "dijit/registry",
            "ingrid/hierarchy/behaviours",
            "ingrid/message",
            "ingrid/utils/Catalog"
        ], function(array, lang, on, query, dom, domClass, domConstruct, Deferred, registry, behaviour, message, UtilCatalog) {
            
            var def = UtilCatalog.getOverrideBehavioursDef();
            
            on(_container_, "Load", function() {
                renderBehaviours();
                def.then(function(data) {
                    // set all checkboxes active that are activated
                    highlightActiveRows(data);
                });
            });
            
            function highlightActiveRows(ids) {
                array.forEach(ids, function(id) {
                    var check = dom.byId("behaviour_" + id);
                    check.checked = true;
                    domClass.add(check.parentNode.parentNode, "active");
                });
            }
            
            function renderBehaviours() {
                for(var behave in behaviour) {
                    if (!behaviour[behave].title) continue;
                    console.log(behaviour[behave].title);
                    domConstruct.place( renderRow(behaviour[behave], behave), "behaviourContent" );
                }
            }
            
            function renderRow(data, id) {
                var row = domConstruct.toDom(
                    "<div class='row grey'>" +
                        "<div class='left title' title='" + id + "'>" + data.title + "</div>" +
                        "<div class='right'><input type='checkbox' id='behaviour_" + id + "'></div>" +
                        "<div class='clear'>" + data.description + "</div>" +
                    "</div>"
                );
                return row;
            }
            
            function saveBehaviourConfig() {
                // collect all active checkboxes
                var ids = query("#behaviourContent input:checked").map(function(item) {
                    return item.id.substring(10);
                });
                
                var data = {};
                data[UtilCatalog.BEHAVIOURS] = ids.join(",");
                
                // write the active IDs to the backend
                UtilCatalog.storeGenericValuesDef(data).then(function() {
                    query("#behaviourContent .row").removeClass("active");
                    highlightActiveRows(ids);
                }, function(error) {
                    console.error(error);
                    displayErrorMessage(error);
                });
            }

            /*
             *  PUBLIC METHODS
             */
            pageCatBehaviours = {
                saveBehaviourConfig: saveBehaviourConfig
            };
        
        });
            
        </script>
    </head>
    <body>
        <!-- CONTENT START -->
        <div id="contentSection" class="contentBlockWhite">
            <div id="adminCatalog" class="content">
                <!-- LEFT HAND SIDE CONTENT START -->
                <div class="inputContainer">
                    <%-- <div id="winNavi" style="top:0;">
                        <a href="javascript:void(0);" onclick="window.open('mdek_help.jsp?lang='+userLocale+'&hkey=catalog-administration-1#catalog-administration-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no')" title="<fmt:message key="general.help" />">[?]</a>
                    </div> --%>
                </div>
                <div id="behaviourContent" class="clear">
                </div>
                
                <div class="inputContainer">
                    <span class="button">
                        <span style="float:right;">
                            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.admin.catalog.general.save" />" onclick="pageCatBehaviours.saveBehaviourConfig();">
                                <fmt:message key="dialog.admin.catalog.general.save" />
                            </button>
                        </span>
                        <!-- <span id="generalSettingsLoadingZone" style="float:left; margin-top:1px; z-index: 100; visibility:hidden"><img src="img/ladekreis.gif" /></span> -->
                    </span>
                </div>
            </div>
        </div>
    </body>
</html>
