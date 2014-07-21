<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Objekt zuordnen</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<script type="text/javascript">

var pageSelectDataset = _container_;

require(["dojo/on", "dijit/registry", "ingrid/tree/MetadataTree"], function(on, registry, MetadataTree) {
    
    on(_container_, "Load", function() {
        init();
    });
    
    on(_container_, "UnLoad", function(){
        // If the dialog was cancelled via the dialogs close button
        // we need to signal an error (cancel action)
        if (registry.byId("pageDialog").customParams.resultHandler.fired == -1) {
            registry.byId("pageDialog").customParams.resultHandler.reject();
        }
    });
    
    function init() {
        // Deferred object which is called when the Tree has been initialized.
        // After the tree has been initialized, the tree is expanded to the targetNode
        // specified in the customParameter 'jumpToNode' 
        new MetadataTree({showRoot:false, treeType:"ObjectsAndAddresses"}, "selectDatasetTree");
    
    }
    
    pageSelectDataset.selectDatasetForExport = function() {
        var node = registry.byId("selectDatasetTree").selectedNode.item;
        if (node) {
            var retVal = {};
            retVal.uuid = node.id;
            retVal.title = node.title;
            retVal.nodeAppType = node.nodeAppType;
    
            registry.byId("pageDialog").customParams.resultHandler.resolve(retVal);
        }
    
        registry.byId("pageDialog").hide();
    };
});
</script>
</head>

<body>

    <div id="winNavi">
        <a href="javascript:void(0);" onclick="javascript:window.open('mdek_help.jsp?lang='+userLocale+'&hkey=import-export-1#import-export-1', 'Hilfe', 'width=750,height=550,resizable=yes,scrollbars=yes,locationbar=no');" title="<fmt:message key="general.help" />">[?]</a>
    </div>
        <!-- CONTENT START -->
        <div class="inputContainer grey">
            <div data-dojo-type="dijit/layout/ContentPane" id="treeContainerAssignObj" style="height:350px;">
                <div id="selectDatasetTree">
                </div>
            </div>
        </div>
        <!-- <div class="inputContainer">
            <span>
                <fmt:message key="dialog.admin.export.selectNode.note" />
            </span>
        </div>-->
        <div class="inputContainer">
            <span class="button transparent">
                <span style="float:right;">
                    <button data-dojo-type="dijit/form/Button" onclick="pageSelectDataset.selectDatasetForExport()">
                        <fmt:message key="dialog.admin.export.selectNode.select" />
                    </button>
                </span>
            </span>
        </div><!-- CONTENT END -->

</body>
</html>
