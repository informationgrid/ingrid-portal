<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <title><fmt:message key="dialog.popup.subSensor.link" /></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="author" content="wemove digital solutions" />
    <meta name="copyright" content="wemove digital solutions GmbH" />
    <script type="text/javascript">
        var dialogSelectSubSensor = null;

        require([
            "dijit/registry",
            "dojo/on",
            "ingrid/tree/MetadataTree",
            "ingrid/utils/Store",
        ], function(registry, on, MetadataTree, UtilStore) {
            var customParams = _container_.customParams;
            var dialog = null;
            var btnSubmit;

            on(_container_, "Load", function() {
                btnSubmit = registry.byId("btn_subsensor_submit");
                init();
            });

            function init() {
                createTree();
            }

            function createTree() {
                var tree = new MetadataTree({
                    showRoot: false,
                    treeType: "Objects"
                }, "treeSubSensor");

                tree.excludeFunction = function(item) {
                    return !item.userWritePermission
                        || !item.objectClass
                        || item.objectClass != 20
                        || item.id === currentUdk.uuid;
                }

                on(tree, "Click", function(node, data) {
                    btnSubmit.set("disabled", tree.excludeFunction(node));
                });
            }

            function submit() {
                var node = registry.byId("treeSubSensor").selectedNode;

                var tableId = "subSensorDataGrid";
                var table = registry.byId(tableId);
                var data = table.data;

                var uuid = node.item.id;
                var title = node.item.title;
                data.push({
                    subSensorObjectUuid: uuid,
                    subSensorObjectName: title,
                    subSensorObjectLink: linkToObject(uuid, title)
                });
                UtilStore.updateWriteStore(tableId, data);
                //dialogSelectSubSensor.hide();
            }

            function linkToObject(uuid, title) {
                var onclick = "require('ingrid/menu').handleSelectNodeInTree('" + uuid + "', 'O');";
                return '<a href="#" class="" title="' + title + '" onclick="' + onclick + '">' + title + "</a>";
            }

            /**
             * PUBLIC METHODS
             */

            dialogSelectSubSensor = {
                submit: submit
            };
        });
    </script>
</head>
<body>
<div id="subSensorDialog" data-dojo-type="dijit/layout/ContentPane">
    <div id="subSensorContent" class="content">
        <!-- CONTENT START -->
        <div class="inputContainer">
            <div data-dojo-type="dijit/layout/ContentPane" id="treeContainerSubSensor" style="height: 300px;">
                <div id="treeSubSensor" style="height: 100%;"></div>
            </div>
        </div>
        <div class="fill"></div>
        <!-- CONTENT END -->
    </div>
    <div class="dijitDialogPaneActionBar">
        <button data-dojo-type="dijit/form/Button" type="submit" disabled="disabled" id="btn_subsensor_submit" onclick="dialogSelectSubSensor.submit()">
            <fmt:message key="general.ok" />
        </button>
    </div>
</div>
</body>
</html>

