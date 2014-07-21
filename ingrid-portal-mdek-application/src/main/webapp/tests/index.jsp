<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<% request.getSession(true).setAttribute("userName", "ige"); %>
<% request.getSession(true).setAttribute("currLang", request.getParameter("lang") == null ? "de" : request.getParameter("lang")); %>

<fmt:setLocale value="<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>" scope="session" />
<fmt:setBundle basename="messages" scope="session"/>

<html dir="ltr">

<head>
<title>InGrid Editor</title>
<link rel="shortcut icon" href="../img/iconLogo.gif" type="image/x-icon">
<link rel="stylesheet" href="../dojo-sources/ingrid/css/slick.grid.css" type="text/css" media="screen" charset="utf-8" />

<script type="text/javascript">
            var userLocale = 'de';
</script>

<!-- <link rel="stylesheet" href="../css/styles.css" /> -->
<!-- <link rel="stylesheet" href="../dojo-sources/dijit/themes/claro/claro.css" />
<link rel="stylesheet" href="../dojo-sources/dojo/resources/dojo.css" />
<link rel="stylesheet" href="../dojo-sources/dijit/themes/claro/Common.css" />
<link rel="stylesheet" href="../dojo-sources/dijit/themes/claro/form/Common.css" />
<link rel="stylesheet" href="../dojo-sources/dojox/layout/resources/FloatingPane.css" />
<link rel="stylesheet" href="../dojo-sources/dojox/layout/resources/ResizeHandle.css" />
<link rel="stylesheet" href="../dojo-sources/dojox/highlight/resources/highlight.css" /> -->

<!-- <link rel="stylesheet" href="../dojo-sources/ingrid/css/main.css" />
<link rel="stylesheet" href="../dojo-sources/ingrid/css/tree.css" /> -->

<link rel="stylesheet" href="../dojo-sources/ingrid/css/imageReferences.css" />
<link rel="stylesheet" href="../dojo-sources/ingrid/css/styles.css" />
<script type="text/javascript">
    var isRelease = false;
    var dojoConfig = {
            async: true,
            debug: true,
            parseOnLoad: false,
            useDeferredInstrumentation: true,
            locale: "de",
            packages: [
                //{ name: "ingrid", location: ingridJsPath+"dojo" }
                { name: "dwr", location: "../../dwr" },
                { name: "global", location: "../../js" }
            ]
        };
    
    catalogData = "de";
</script>
<script type="text/javascript" src="../dojo-sources/dojo/dojo.js"></script>

<script type='text/javascript' src='../dwr/engine.js'></script>
<script type='text/javascript' src='../dwr/util.js'></script>

<script type='text/javascript' src='../dwr/interface/SNSService.js'></script>
<script type='text/javascript' src='../dwr/interface/TreeService.js'></script>


<script type="text/javascript">
require([
    "dijit/layout/StackContainer",
    "dijit/layout/BorderContainer",
    "dijit/layout/TabContainer",
    "dojo/_base/array",
    "dojo/Deferred",
    "dijit/layout/ContentPane",
    "dojox/layout/ContentPane",
    "dijit/MenuBar",
    "dijit/MenuBarItem",
    "ingrid/utils/Catalog",
    "ingrid/utils/String",
    "ingrid/dialog",
    "ingrid/message",
    "dijit/registry",
    "dwr/interface/CatalogService",
    "dojo/domReady!"
], function(StackContainer, BorderContainer, TabContainer, array, Deferred, ContentPane, ContentPaneX, MenuBar, MenuBarItem, UtilCatalog, UtilString, dialog, message, registry) {
    
    // create BorderContainer (Splitpane)
    var main = new BorderContainer({ 
        design: "headline",
        gutters: false,
        toggleSplitterClosedThreshold: "100px",
        toggleSplitterOpenSize: "80px",
        style: "width:100%;",//" heigth: 100%;",
        liveSplitters: false }, "contentContainer");
    var contentPane = new ContentPaneX({
        id: "contentPane",
        //splitter: true,
        region: "center",
        style: "padding: 10px;"
        }).placeAt(main.domNode);
    
    // top pane - menu 
    var topMenuPane = new ContentPane({
        id: "menubarPane",
        splitter: false,
        region: "top",
        style: "height: 32px;"
        }, "menuContainer");
    
    var menu = new MenuBar({}).placeAt(topMenuPane.domNode);
    menu.addChild(new MenuBarItem({
        label: "Tables",
        onClick: function(){
            registry.byId("stackContainer").selectChild(registry.byId("pageTable"));
        }
    }));
    menu.addChild(new MenuBarItem({
        label: "Dialogs",
        onClick: function(){
            registry.byId("stackContainer").selectChild(registry.byId("pageDialogTest"));
        }
    }));
    menu.addChild(new MenuBarItem({
        label: "Additional Fields",
        onClick: function(){
            registry.byId("stackContainer").selectChild(registry.byId("pageAdditionalTest"));
        }
    }));
    menu.addChild(new MenuBarItem({
        label: "Trees",
        onClick: function(){
            registry.byId("stackContainer").selectChild(registry.byId("pageTreeTest"));
        }
    }));

    sc = new StackContainer({
        style: "width: 100%;",
        id: "stackContainer"
    }).placeAt(registry.byId("contentPane").domNode);
    
    var tableTests = new ContentPaneX({
        id: "pageTable",
        title: "tables",
        layoutAlign: "client",
        href: "CustomGridTest.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: true
    });

    var dialogTests = new ContentPaneX({
        id: "pageDialogTest",
        layoutAlign: "client",
        href: "DialogTest.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: true
    });
    
    var additionalTests = new ContentPaneX({
        id: "pageAdditionalTest",
        layoutAlign: "client",
        href: "AdditionalTest.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: true
    });
    
    var treeTests = new ContentPaneX({
        id: "pageTreeTest",
        layoutAlign: "client",
        href: "Tree.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: true
    });

    sc.addChild(dialogTests);
    sc.addChild(tableTests);
    sc.addChild(additionalTests);
    sc.addChild(treeTests);
    sc.startup();

    //main.startup();
    
    sc.selectChild(registry.byId("pageDialogTest"));
    
	//get an array of Inspire topics
	var getInspireTopics = function(topics) {
	    var inspireArray = new Array();
	    array.forEach(topics, function(topic) {
	        if (topic.inspireList.length > 0) {
	            array.forEach(topic.inspireList, function(inspireTopic) {
	                // exclude multiple same entries
	                if (!inspireArrayContains(inspireArray,inspireTopic)) {         
	                    var obj = new Object();
	                    obj.title = inspireTopic;
	                    obj.label = inspireTopic;
	                    obj.source = "INSPIRE";
	                    inspireArray.push(obj);
	                }
	            });
	        }
	    });
	    return inspireArray;
	}
	
	var fetchSysLists = function() {
	    var def = new Deferred();
	
	    // Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
	    var languageCode = UtilCatalog.getCatalogLanguage();
	    console.debug("LanguageShort is: " + languageCode);
	    
	    var lstIds = [100, 101, 102, 502, 505, 510, 515, 518, 520, 523, 525, 526, 527, 528, 1100, 1230, 1320, 1350, 1370, 1400, 1410, 2000,
	        3535, 3555, 3385, 3571, 4300, 4305, 4430, 5100, 5105, 5110, 5120, 5130, 5180, 5200, 5300, 6000, 6005, 6010, 6020, 6100, 6200, 6300,
	        6400, 6500, 7109, 7112, 7113, 7114, 7115, 7120, 7125, 7126, 7127, 8000, 99999999];
	
	    CatalogService.getSysListsRemoveMetadata(lstIds, languageCode, {
	        //preHook: UtilDWR.enterLoadingState,
	        //postHook: UtilDWR.exitLoadingState,
	        callback: function(res){
	            sysLists = res;
	            
	            // only if not sorted in backend, e.g. INSPIRE Themes (6100) !
	            array.forEach(lstIds, function(id) {
	                if (id != 6100) {
	                    sysLists[id].sort(function(a, b) {
	                        return UtilString.compareIgnoreCase(a[0], b[0]);
	                    });
	                }
	            });
	            
	            def.resolve();
	        },
	        errorHandler: function(mes){
	            //UtilDWR.exitLoadingState();
	            //displayErrorMessage(err);
	            console.debug("Error: " + mes);
	            dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
	            console.debug("Error: " + mes);
	            def.reject(mes);
	        }
	    });
	
	    return def;
	}
	
	fetchSysLists();

});
</script>
</head>
<body class="claro">
    <div id="contentContainer" class="contentSection">
        <div id="menuContainer"></div>
    </div>

</body>
</html>
