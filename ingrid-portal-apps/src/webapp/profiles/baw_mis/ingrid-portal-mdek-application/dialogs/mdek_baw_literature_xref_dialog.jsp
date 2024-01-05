<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2024 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
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
    <title>Literatureverweis</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <meta name="author" content="wemove digital solutions"/>
    <meta name="copyright" content="wemove digital solutions GmbH"/>
    <script>

        var dialogLiteratureXref = null;

        require([
            "dijit/registry",
            "dojo/on",
            "ingrid/tree/MetadataTree",
            "ingrid/utils/Store"
        ], function (registry, on, MetadataTree, UtilStore) {

            var customParams = _container_.customParams;
            var dialog = null;
            var treeId = "treeBawLitXref";
            var btnSubmit;

            on(_container_, "Load", function () {
                btnSubmit = registry.byId("btn_baw_lit_xref_submit");
                createTree();
            });

            function createTree() {
                var tree = new MetadataTree({
                    showRoot: false,
                    treeType: "Objects"
                }, treeId);

                tree.excludeFunction = function (item) {
                    return !item.userWritePermission
                           || !item.objectClass
                           || item.objectClass != 2;
                };

                on(tree, "Click", function (node, data) {
                    btnSubmit.set("disabled", tree.excludeFunction(node));
                });
            }

            function submit() {
                var node = registry.byId(treeId).selectedNode;

                var tableId = "bawLiteratureXrefTable";
                var table = registry.byId(tableId);
                var data = table.data;

                var uuid = node.item.id;
                var title = node.item.title;
                var docType = node.item.nodeDocType;
                var link = linkToObject(uuid, title);

                data.push({
                    bawLiteratureNodeDocType: docType,
                    bawLiteratureXrefUuid: uuid,
                    bawLiteratureXrefTitle: title,
                    bawLiteratureXrefLink: link
                });
                UtilStore.updateWriteStore(tableId, data);
            }

            function linkToObject(uuid, title) {
                var onclick = "require('ingrid/menu').handleSelectNodeInTree('" + uuid + "', 'O');";
                return '<a href="#" class="" title="' + title + '" onclick="' + onclick + '">' + title + "</a>";
            }

            function closeDialog() {
                _container_.hide();
            }

            function cancel() {
                closeDialog();
            }

            dialogLiteratureXref = {
                cancel: cancel,
                submit: submit
            };
        })
    </script>
</head>

<body>
<div data-dojo-type="dijit/layout/ContentPane">
    <div id="bawLiteratureTreeContent" class="content">
        <!-- CONTENT START -->
        <div class="inputContainer">
            <div data-dojo-type="dijit/layout/ContentPane" id="treeContainerBawLitXref">
                <div id="treeBawLitXref" style="height: 100%;"></div>
            </div>
            <div class="fill"></div>
        </div>
    </div>
    <!-- CONTENT END -->
    <div>
        <div class="dijitDialogPaneActionBar" style="margin: unset">
            <button data-dojo-type="dijit/form/Button" type="submit" id="btn_baw_lit_xref_submit" disabled="disabled"
                    data-dojo-props="onClick:function(){dialogLiteratureXref.submit();}"><fmt:message
                    key="general.add"/></button>
            <button data-dojo-type="dijit/form/Button" type="button"
                    data-dojo-props="onClick:function(){dialogLiteratureXref.cancel();}" id="cancel"><fmt:message
                    key="general.cancel"/></button>
        </div>
    </div>
</div>

</body>

