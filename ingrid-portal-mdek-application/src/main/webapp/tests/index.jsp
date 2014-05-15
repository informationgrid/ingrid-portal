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
<link rel="stylesheet" href="../css/slick.grid.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="../css/imageReferences.css" />

<script type="text/javascript">
            var userLocale = 'de';
</script>

<!-- <link rel="stylesheet" href="../css/styles.css" /> -->
<link rel="stylesheet" href="../dojo-src/dijit/themes/claro/claro.css" />
<link rel="stylesheet" href="../dojo-src/dojo/resources/dojo.css" />
<link rel="stylesheet" href="../dojo-src/dijit/themes/claro/Common.css" />
<link rel="stylesheet" href="../dojo-src/dijit/themes/claro/form/Common.css" />
<link rel="stylesheet" href="../dojo-src/dojox/layout/resources/FloatingPane.css" />
<link rel="stylesheet" href="../dojo-src/dojox/layout/resources/ResizeHandle.css" />
<link rel="stylesheet" href="../dojo-src/dojox/highlight/resources/highlight.css" />

<link rel="stylesheet" href="../css/main.css" />
<link rel="stylesheet" href="../css/tree.css" />

<link rel="stylesheet" href="../css/imageReferences.css" />
<script type="text/javascript" src="../dojo-src/dojo/dojo.js" djConfig="parseOnLoad:false, locale:'de'"></script>
<script type="text/javascript" src="../dojo-src/custom/layer.js"></script>
<script type="text/javascript">
    var isRelease = false;
</script>


<script type='text/javascript' src='../dwr/engine.js'></script>
<script type='text/javascript' src='../dwr/util.js'></script>

<script src='../dwr/interface/CatalogService.js'></script>
<script src='../dwr/interface/TreeService.js'></script>
<script src='../dwr/interface/ObjectService.js'></script>
<script src='../dwr/interface/AddressService.js'></script>
<script src='../dwr/interface/SecurityService.js'></script>
<script src='../dwr/interface/SNSService.js'></script>
<script src='../dwr/interface/QueryService.js'></script>
<script src='../dwr/interface/HelpService.js'></script>
<script src='../dwr/interface/CTService.js'></script>
<script src='../dwr/interface/HttpService.js'></script>
<script src='../dwr/interface/GetCapabilitiesService.js'></script>
<script src='../dwr/interface/ImportService.js'></script>
<script src='../dwr/interface/ExportService.js'></script>
<script src='../dwr/interface/CatalogManagementService.js'></script>
<script src='../dwr/interface/UtilityService.js'></script>

<script type="text/javascript" src="../js/config.js"></script>
<script type="text/javascript" src="../js/message.js"></script>
<script type="text/javascript" src="../js/utilities.js"></script>
<script type="text/javascript" src="../js/dialog.js"></script>
<script type="text/javascript" src="../js/error_handler.js"></script>
<script type="text/javascript" src="../js/menuEventHandler.js"></script>
<script type="text/javascript" src="../js/eventSubscriber.js"></script>

<script type="text/javascript" src="../js/layoutCreator.js"></script>
<script type="text/javascript" src="../js/debugFunctions.js"></script>
<script type="text/javascript" src="../js/menu.js"></script>
<script type="text/javascript" src="../js/toolbar.js"></script>

<script type="text/javascript" src="../js/dojo/dijit/CustomGrid.js"></script>
<script type="text/javascript" src="../js/dojo/dijit/CustomTree.js"></script>

<script type="text/javascript" src="../js/rules_required.js"></script>
<script type="text/javascript" src="../js/rules_validation.js"></script>
<script type="text/javascript" src="../js/rules_checker.js"></script>

<script type="text/javascript" src="../js/tree.js"></script>

<script type="text/javascript" src="../js/dojo/dijit/CustomGridRowMover.js"></script>
<script type="text/javascript" src="../js/dojo/dijit/CustomGridRowSelector.js"></script>
<script type="text/javascript" src="../js/dojo/dijit/CustomGridEditors.js"></script>
<script type="text/javascript" src="../js/dojo/dijit/CustomGridFormatters.js"></script>

<script type="text/javascript" src="../js/dojo/dijit/ThesaurusTree.js"></script>


<script type="text/javascript">
dojo.require("ingrid.dijit.tree.LazyTreeStoreModel");
dojo.require("dijit.layout.TabContainer");

scrollBarWidth = 17;

dojo.addOnLoad(function() {
    // create BorderContainer (Splitpane)
    var main = new dijit.layout.BorderContainer({ 
        design: "headline",
        gutters: false,
        toggleSplitterClosedThreshold: "100px",
        toggleSplitterOpenSize: "80px",
        style: "width:100%; heigth: 100%;",
        liveSplitters: false }, "contentContainer");
    var contentPane = new dojox.layout.ContentPane({
        id: "contentPane",
        //splitter: true,
        region: "center",
        style: "padding: 10px;"
        }).placeAt(main.domNode);
    
    dojo.require("dijit.layout.StackContainer");
    
    // top pane - menu 
    var topMenuPane = new dijit.layout.ContentPane({
        id: "menubarPane",
        splitter: false,
        region: "top",
        style: "height: 32px;"
        }, "menuContainer");
    
    var menu = new dijit.MenuBar({}).placeAt(topMenuPane.domNode);
    menu.addChild(new dijit.MenuBarItem({
        label: "Tables",
        onClick: function(){
            dijit.byId("stackContainer").selectChild(dijit.byId("pageTable"));
        }
    }));
    menu.addChild(new dijit.MenuBarItem({
        label: "Dialogs",
        onClick: function(){
            dijit.byId("stackContainer").selectChild(dijit.byId("pageDialogTest"));
        }
    }));
    menu.addChild(new dijit.MenuBarItem({
        label: "Additional Fields",
        onClick: function(){
            dijit.byId("stackContainer").selectChild(dijit.byId("pageAdditionalTest"));
        }
    }));

    sc = new dijit.layout.StackContainer({
        style: "width: 100%;",
        id: "stackContainer"
    }).placeAt(dijit.byId("contentPane").domNode);
    
    var tableTests = new dojox.layout.ContentPane({
        id: "pageTable",
        title: "tables",
        layoutAlign: "client",
        href: "CustomGridTest.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: true
    });

    var dialogTests = new dojox.layout.ContentPane({
        id: "pageDialogTest",
        layoutAlign: "client",
        href: "DialogTest.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: true
    });
    
    var additionalTests = new dojox.layout.ContentPane({
        id: "pageAdditionalTest",
        layoutAlign: "client",
        href: "AdditionalTest.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: true
    });

    sc.addChild(dialogTests);
    sc.addChild(tableTests);
    sc.addChild(additionalTests);
    sc.startup();

    //main.startup();
    
    sc.selectChild(dijit.byId("pageTable"));
    
    fetchSysLists();
});

//get an array of Inspire topics
function getInspireTopics(topics) {
    var inspireArray = new Array();
    dojo.forEach(topics, function(topic) {
        if (topic.inspireList.length > 0) {
            dojo.forEach(topic.inspireList, function(inspireTopic) {
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

function fetchSysLists() {
    var def = new dojo.Deferred();

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
            dojo.forEach(lstIds, function(id) {
                if (id != 6100) {
                    sysLists[id].sort(function(a, b) {
                        return UtilString.compareIgnoreCase(a[0], b[0]);
                    });
                }
            });
            
            def.callback();
        },
        errorHandler: function(mes){
            //UtilDWR.exitLoadingState();
            //displayErrorMessage(err);
            console.debug("Error: " + mes);
            dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
            console.debug("Error: " + mes);
            def.errback(mes);
        }
    });

    return def;
}
</script>
</head>
<body class="claro">
    <div id="contentContainer" class="contentSection">
        <div id="menuContainer"></div>
    </div>

</body>
</html>
