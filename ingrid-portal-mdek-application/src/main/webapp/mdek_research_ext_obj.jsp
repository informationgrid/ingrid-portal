<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<style type="text/css">
/* If the select box (AND/OR select) is opened for the first time a scrollbar is shown for the main tab.
   to prevent this behaviour, the overflow is made invisible */
div.dojoTabPaneWrapper { overflow:visible; }
</style>


<script type="text/javascript">
    var pageResearchSearchExtObj = _container_;
    require([
        "dojo/on",
        "dojo/dom",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/Deferred",
        "dojo/aspect",
        "dojo/keys",
        "dojo/dom-style",
        "dojo/query",
        "dojo/string",
        "dijit/registry",
        "dijit/form/CheckBox",
        "ingrid/layoutCreator",
        "ingrid/dialog",
        "ingrid/utils/Syslist",
        "ingrid/utils/List",
        "ingrid/utils/Store",
        "ingrid/utils/Grid",
        "ingrid/utils/LoadingZone",
        "ingrid/utils/PageNavigation"
    ], function(on, dom, lang, array, Deferred, aspect, keys, style, query, string, registry, CheckBox, layoutCreator, dialog, UtilSyslist, UtilList, UtilStore, UtilGrid, LoadingZone, navigation) {
        
        var resultsPerPage = 10;
        //var pageNav = new PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("objSearchExtResultsInfo"), pagingSpan:dom.byId("objSearchExtResultsPaging") });
        var pageNav = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan:dom.byId("objSearchResultsInfo"), pagingSpan:dom.byId("objSearchResultsPaging") });
        
        var currentQuery = {    // Map of strings defining the query. See 'ObjectExtSearchParamsBean.java' for more details.
            queryTerm: null,            // search query
            relation: null,             // relation that should be used to analyze the query (AND / OR)
            searchType: null,           // search whole word or substring (exact/like)
            objClasses: null,           // array of ints that represent the object classes that should be included in the search
            thesaurusTerms: null,       // array of topicIds - descriptors and non-descriptors
            thesaurusRelation: null,    // relation that should be used to combine the thesaurus terms (AND / OR)
            geoThesaurusTerms: null,    // array of areaIds - locations
            geoThesaurusRelation: null, // relation that should be used to combine the location topics (AND / OR)
            customLocation: null,       // custom location id from the catalog
            timeBegin: null,            // Time reference. Date object begin.
            timeEnd: null,              // Time reference. Date object end.
            timeAt: null,               // Time reference. Date object.
            timeIntersects: null,       // Flag signaling if the entered interval should be cut by the result 
            timeContains: null          // Flag signaling if the entered interval should be included in the result
        };
        
        
        createDomElements();
        on(_container_, "Load", function(){
            init();
        });
        
        function init() {
            console.debug("init");
            
            // set labels for object classes from codelist 8000
            for (var i=0; i<7; i++) {
                query("label[for='objTopicObjectClass"+i+"']")[0].innerHTML = UtilSyslist.getSyslistEntryName( 8000, i );
            }
            
            // Initially select the first tabs on load
            navInnerTab('objTopic', 0, 3);
            navInnerTab('objSpace', 0, 3);
            navInnerTab('objTime', 0, 1);
        
            // Initialize select boxes
            registry.byId("objTopicInputBool").setValue(0);
            registry.byId("thesaurusTermsRelation").setValue(0);
            registry.byId("geoThesaurusTermsRelation").setValue(0);
        
            // Init the 'custom location' select box 
            //var languageCode = UtilCatalog.getCatalogLanguage();
        
            // Pressing 'enter' on the thesaurus input field is equal to a button click
            on(dom.byId("objTopicThesaurus"), "keypress", function(event) {
                if (event.keyCode == keys.ENTER) {
                    findTopics();
                }
            });
        
            // Pressing 'enter' on the thesaurus input field is equal to a button click
            on(dom.byId("objLocationTopic"), "keypress", function(event) {
                if (event.keyCode == keys.ENTER) {
                    findLocationTopics();
                }
            });
        
            // Pressing 'enter' on the date fields is equal to a new search
            on(dom.byId("objTimeRef1From"), "keypress", function(event) {
                if (event.keyCode == keys.ENTER) {
                    startNewSearch();
                }
            });
            on(dom.byId("objTimeRef1To"), "keypress", function(event) {
                if (event.keyCode == keys.ENTER) {
                    startNewSearch();
                }
            });
        
            on(registry.byId("objTimeRef1From"), "onInvalidInput", function(input) {
                registry.byId("objTimeRef1From").set("value", null);
                dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.research.ext.obj.invalidDate' />", [input]), dialog.WARNING);
        
            });
            on(registry.byId("objTimeRef1To"), "onInvalidInput", function(input) {
                registry.byId("objTimeRef1To").set("value", null);
                dialog.show("<fmt:message key='general.error' />", string.substitute("<fmt:message key='dialog.research.ext.obj.invalidDate' />", [input]), dialog.WARNING);
            });
        
        
            on(registry.byId("objTimeRef1"), "onChange", function(value) {displayTimeToInput(value);});
            registry.byId("objTimeRef1").setValue("am");
        
            aspect.after(pageNav, "onPageSelected", startSearch);
        }
        
        function createDomElements() {
            var objExtSearchThesaurusTermsStructure = [
                {field: 'label',name: 'Thesaurus Suchbegriffe',width: '330px'}
            ];
            layoutCreator.createDataGrid("objExtSearchThesaurusTerms", null, objExtSearchThesaurusTermsStructure, null);
            
            var objExtSearchLocationTermsStructure = [
                {field: 'title',name: 'Geo-Thesaurus Suchbegriffe',width: '330px'}
            ];
            layoutCreator.createDataGrid("objExtSearchLocationTerms", null, objExtSearchLocationTermsStructure, null);
            
            var storeProps = {data: {identifier: '1',label: '0'}};
            layoutCreator.createFilteringSelect("objSpaceGeoUnit", null, storeProps, function(){
                return UtilSyslist.getSyslistEntry(1100);
            });
        }
        
        // ************************** General Search functions **************************
        
        // Starts a search with the current parameters stored in 'currentQuery' and 'pageNav'
        function startSearch() {
            console.debug("query: ");
            console.debug(currentQuery);
            
            pageResearchSearch.lastObjSearchType = "queryObjectsExtended";
        
            queryObjectsExtended(pageNav.getStartHit(), resultsPerPage)
            .then(function(res) {
                updateResultTable(res);
                updatePageNavigation(res);
            });
        }
        
        function queryObjectsExtended(start, howMany) {
            var def = new Deferred();
            
            QueryService.queryObjectsExtended(currentQuery, start, howMany, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback: def.resolve,
                errorHandler: function(errMsg, err) {displayErrorMessage(err);}
            });
            
            return def;
        }
        
        function updateResultTable(res) {
            var resultList = res.resultList;
            UtilList.addObjectLinkLabels(resultList);
            UtilList.addIcons(resultList);
        
            UtilStore.updateWriteStore("objectSearchResults", resultList);
        }
        
        function updatePageNavigation(res) {
            pageNav.setTotalNumHits(res.totalNumHits);
            pageNav.updateDomNodes();
        }
        
        
        // Search button function. Creates a new query and starts a new search
        function startNewSearch() {
            readQueryFromInput();
            pageNav.reset();
            startSearch();
        }
        
        // Subnavigation. 'subSectionIndex' specifies the index of the tab that should be selected
        function navInnerTab(sectionName, subSectionIndex, numSubSections){
            for (var i = 0; i < numSubSections; i++) {
                if (i == subSectionIndex) {
                    style.set(sectionName + i, "display", "block");
                } else {
                    style.set(sectionName + i, "display", "none");
                }
            }
            
            if (sectionName == "objTopic" && subSectionIndex === 2 && numSubSections === 3) {
                registry.byId("objExtSearchThesaurusTerms").reinitLastColumn();
            }
        }
        
        // Read the input fields and write the query to 'currentQuery'
        function readQueryFromInput() {
            currentQuery.queryTerm = lang.trim(registry.byId("objSearchInput").getValue());
            currentQuery.relation = registry.byId("objTopicInputBool").getValue();
            currentQuery.searchType = dom.byId("objMode1").checked ? 0 : 1; // Convert to int
            
            // Read obj class checkboxes
            currentQuery.objClasses = [];
            for (var i = 0; i < 7; ++i) {
                if (registry.byId("objTopicObjectClass" + i).checked)
                    currentQuery.objClasses.push(i);
            }
        
            // Read the Descriptors from the thesaurus table
            currentQuery.thesaurusTerms = [];
            array.forEach(UtilGrid.getTableData("objExtSearchThesaurusTerms"), function(item){
                currentQuery.thesaurusTerms.push(item.topicId);
            });
            currentQuery.thesaurusRelation = registry.byId("thesaurusTermsRelation").getValue();
        
            // Read the Location topics from the thesaurus table
            currentQuery.geoThesaurusTerms = [];
            array.forEach(UtilGrid.getTableData("objExtSearchLocationTerms"), function(item){
                currentQuery.geoThesaurusTerms.push(item.topicId);
            });
            currentQuery.geoThesaurusRelation = registry.byId("geoThesaurusTermsRelation").getValue();
        
            currentQuery.customLocation = registry.byId("objSpaceGeoUnit").getValue();
        
            // Time ref
            currentQuery.timeBegin = null;
            currentQuery.timeEnd = null;
            currentQuery.timeAt = null;
        
            var timeType = registry.byId("objTimeRef1").getValue();
            var timeFrom = registry.byId("objTimeRef1From").getValue();
            var timeTo = registry.byId("objTimeRef1To").getValue();
            if (timeFrom == "") { timeFrom = null; }
            if (timeTo == "") { timeTo = null; }
        
            if (timeType == "am") {
                currentQuery.timeAt = timeFrom;
        
            } else if (timeType == "seit") {
                currentQuery.timeBegin = timeFrom;
        
            } else if (timeType == "bis") {
                currentQuery.timeEnd = timeFrom;
        
            } else if (timeType == "von") {
                currentQuery.timeBegin = timeFrom;
                currentQuery.timeEnd = timeTo;
            }
        
            currentQuery.timeIntersects = registry.byId("objTimeRefExtend1").checked;
            currentQuery.timeContains = registry.byId("objTimeRefExtend2").checked;
        
        }
        
        // Reset-Button function. Reset all the input fields to their initial values and reset 'currentQuery'
        function resetInput() {
            dom.byId("objMode2").checked = true;
            registry.byId("objSearchInput").setValue("");
            registry.byId("objTopicInputBool").setValue(0);
            selectAllObjectClasses();
            registry.byId("objTopicThesaurus").setValue("");
            UtilGrid.setTableData("objExtSearchThesaurusTerms", []);
            registry.byId("thesaurusTermsRelation").setValue(0);
            registry.byId("objLocationTopic").setValue("");
            UtilGrid.setTableData("objExtSearchLocationTerms", []);
            registry.byId("geoThesaurusTermsRelation").setValue(0);
            registry.byId("objSpaceGeoUnit").setValue("");
        //  dom.byId("objTimeRef1").checked = false;
        //  dom.byId("objTimeRef2").checked = false;
        //  dom.byId("objTimeRef3").checked = true;
        
            registry.byId("objTimeRef1").setValue("am");
            registry.byId("objTimeRef1From").set("value", null);
            registry.byId("objTimeRef1To").set("value", null);
        //  registry.byId("objTimeRef2On").set("value", null);
        
        /*
            // Text input fields 
            registry.byId("objTimeRef1From").setValue("");
            registry.byId("objTimeRef1To").setValue("");
            registry.byId("objTimeRef2On").setValue("");
        */
            registry.byId("objTimeRefExtend1").setValue(false);
            registry.byId("objTimeRefExtend2").setValue(false);
        
            readQueryFromInput();
        };
        
        function displayTimeToInput(value) {
            var timeTo = dom.byId("objTimeRef1ToEditor");
            value == "von" ? style.set(timeTo, "display", "block") : style.set(timeTo, "display", "none");
        }
        
        // ************************** Thesaurus Search functions (Thema/fachwoerterbuch) **************************
        
        // Button function which searches for descriptors/nondescriptors from a given search query
        // A list of checkboxes/labels is created and put into the div with id: 'objExtSearchThesaurusResults'
        function findTopics() {
            var term = dojo.string.trim(registry.byId("objTopicThesaurus").getValue());
        
            // Exit if we search for an empty string
            if (term.length == 0) {
                return;
            }
        
            clearThesaurusResults();
        
            // SNSService.findTopics(term, userLocale, {
            SNSService.getSimilarTerms(term.split(" "), userLocale, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback:function(topics) {
                    if (topics) {
                        UtilList.addSNSTopicLabels(topics);
                    }
        
                    if (array.some(topics, function(item) { return item.type == "DESCRIPTOR"; } )) {
                        addResultTextElement("<fmt:message key='dialog.research.ext.obj.similarTerms' />");
                        array.forEach(topics, function(item){
            //              if (item.type == "DESCRIPTOR" || item.type == "NON_DESCRIPTOR") {
                            if (item.type == "DESCRIPTOR") {
                                addDescriptorCheckbox(item);
                            }
                        });
                        style.set("objExtSearchAddTopicButtonSpan", "display", "block");
        
                    } else {
                        showNoThesaurusResults();
                    }
                },
                timeout:20000,
                errorHandler:function(err) {
                    LoadingZone.hide();
                    showErrorResults();
                    console.debug("Error while executing SNSService.findTopics");
                }
            });
        }
        
        function addSelectedTopics() {
            var resultDiv = dom.byId("objExtSearchThesaurusResults");
        
            array.forEach(resultDiv.childNodes, function(divElement) {
                var checkBoxDiv = divElement.firstChild;
                if (checkBoxDiv && checkBoxDiv.firstChild.id) {
                    var checkBox = registry.byId(checkBoxDiv.firstChild.id);
                } else {
                    return;
                }
        
                if (checkBox.checked) {
                    // If the checked item is already contained in the result list then do nothing
                    if (array.some(UtilGrid.getTableData("objExtSearchThesaurusTerms"), function(item){ return (checkBox.id == item.topicId); }))
                        return;
        
                    // Otherwise add the item to the result list
                    UtilGrid.addTableDataRow("objExtSearchThesaurusTerms", {topicId: checkBox.id, label: checkBox.label} );
                }
            });
        }
        
        function addDescriptorCheckbox(descriptor, disableLink) {
            var resultDiv = dom.byId("objExtSearchThesaurusResults");
        
            var checkBox = new CheckBox({
                id: descriptor.topicId,
                label: descriptor.label
            });
        
            var divElement = document.createElement("div");
        
            if (disableLink) {
                var labelElement = document.createTextNode(descriptor.label);
                resultDiv.appendChild(divElement);
                divElement.appendChild(checkBox.domNode);
                divElement.appendChild(labelElement);
                return;
            }
        
            var linkElement = document.createElement("a"); 
            linkElement.setAttribute("href", "javascript:void(0);");
            linkElement.descriptor = descriptor;
            linkElement.onclick = function() {
                findAssociatedTopics(this.descriptor);
            };
            linkElement.innerHTML = descriptor.label;
        
            resultDiv.appendChild(divElement);
            divElement.appendChild(checkBox.domNode);
        //  divElement.appendChild(labelElement);
            divElement.appendChild(linkElement);
        }
        
        function addResultTextElement(text) {
            var resultDiv = dom.byId("objExtSearchThesaurusResults");
            var divElement = document.createElement("div");
            var labelElement = document.createElement("b");
            var labelText = document.createTextNode(text);
            resultDiv.appendChild(divElement);
            divElement.appendChild(labelElement);
            labelElement.appendChild(labelText);
        
            divElement.style['marginTop'] = "5px";
        }
        
        
        function findAssociatedTopics(descriptor) {
        //  console.debug("findAssociatedTopics: "+descriptor.topicId);
        
            clearThesaurusResults();
        
            SNSService.getTopicsForTopic(descriptor.topicId, userLocale, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback:function(topic) {
                    if (topic) {
                        UtilList.addSNSTopicLabels( [topic] );
                        UtilList.addSNSTopicLabels( topic.synonyms );
                        UtilList.addSNSTopicLabels( topic.parents );
                        UtilList.addSNSTopicLabels( topic.children );
                        UtilList.addSNSTopicLabels( topic.descriptors );
                    }
        
                    if (topic.topicId != descriptor.topicId) {
                        addResultTextElement("<fmt:message key='dialog.research.ext.obj.synonym' />");
                        addDescriptorCheckbox(descriptor, true);
                        addResultTextElement("<fmt:message key='dialog.research.ext.obj.userSynonyms' />");
                        addDescriptorCheckbox(topic);
                    
                    } else {
                        addResultTextElement("<fmt:message key='dialog.research.ext.obj.descriptor' />");
                        addDescriptorCheckbox(descriptor, true);
            
                        if (topic.synonyms.length > 0) {
                            addResultTextElement("<fmt:message key='dialog.research.ext.obj.synonyms' />");
                            array.forEach(topic.synonyms, function(item){
                                addDescriptorCheckbox(item);
                            });
                        }
            
                        if (topic.parents.length > 0) {
                            addResultTextElement("<fmt:message key='dialog.research.ext.obj.parentTerms' />");
                            array.forEach(topic.parents, function(item){
                                addDescriptorCheckbox(item);
                            });
                        }
            
                        if (topic.children.length > 0) {
                            addResultTextElement("<fmt:message key='dialog.research.ext.obj.childTerms' />");
                            array.forEach(topic.children, function(item){
                                addDescriptorCheckbox(item);
                            });
                        }
        
                        if (topic.descriptors.length > 0) {
                            addResultTextElement("<fmt:message key='dialog.research.ext.obj.descriptors' />");
                            array.forEach(topic.descriptors, function(item){
                                addDescriptorCheckbox(item);
                            });
                        }
                    }
                    
                    style.set("objExtSearchAddTopicButtonSpan", "display", "block");
                },
                timeout:20000,
                errorHandler:function(err) {
                    LoadingZone.hide();
                    showErrorResults();
                    console.debug("Error while executing SNSService.getTopicsForTopic");
                }
            });
        }
        
        // Deletes the checkboxes contained in the div with id: 'objExtSearchThesaurusResults'
        function clearThesaurusResults() {
            var resultDiv = dom.byId("objExtSearchThesaurusResults");
        
            while (resultDiv.hasChildNodes()) {
                var divElement = resultDiv.removeChild(resultDiv.firstChild);
                var checkBox = divElement.firstChild;
                if (checkBox && checkBox.firstChild.id) {
                    registry.byId(checkBox.firstChild.id).destroy();
                }
            }
        }
        
        function showNoThesaurusResults() {
            addResultTextElement("<fmt:message key='sns.noResultHint' />");
            style.set("objExtSearchAddTopicButtonSpan", "display", "none");
        }
        
        // ************************** Thesaurus location Search functions (Raum/Geothesaurus-Raumbezug) **************************
        function findLocationTopics() {
            var queryTerm = dojo.string.trim(registry.byId("objLocationTopic").getValue());
        
            if (queryTerm.length === 0)
                return;
        
            clearLocationResults();
        
            SNSService.getLocationTopics(queryTerm, "beginsWith", null, userLocale, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback: setLocationResultList,
                timeout: 20000,
                errorHandler: function(err) {
                    LoadingZone.hide();
                    showErrorResults();
                    console.debug("Error while executing SNSService.getLocationTopics");
                }
            });
        }
        
        function setLocationResultList(topicList) {
            if (topicList === null || topicList.length === 0) {
                showNoLocationResults();
                return;
            }
        
            style.set("objExtSearchAddLocationTopicButtonSpan", "display", "block");
        
            array.forEach(topicList, function(topic) {
                addLocationTopicCheckbox(topic);
            });
        }
        
        function addLocationTopicCheckbox(topic) {
            var checkboxDiv = dom.byId("objExtSearchLocationResults");
            var label = topic.name;
            if (label === null) return;
            if (topic.type !== null) {
                label += ", "+topic.type;
            }
            var checkBox = new CheckBox({
                    id: topic.topicId,
                    name: label,
                    topic: topic
                });
        
            var divElement = document.createElement("div");
        
            var linkElement = document.createElement("a");
            linkElement.setAttribute("href", "javascript:void(0);");
            linkElement.topicId = topic.topicId;
            linkElement.onclick = function() {
                findAssociatedLocations(this.topicId);
            };
            linkElement.innerHTML = label;
        
            checkboxDiv.appendChild(divElement);
            divElement.appendChild(checkBox.domNode);
            divElement.appendChild(linkElement);
        }
        
        function findAssociatedLocations(topicId) {
            clearLocationResults();
        
            SNSService.getLocationTopicsById(topicId, userLocale, {
                preHook: LoadingZone.show,
                postHook: LoadingZone.hide,
                callback: setLocationResultList,
                timeout: 20000,
                errorHandler: function(err) {
                    LoadingZone.hide();
                    showErrorResults();
                    console.debug("Error while executing SNSService.getLocationTopics");
                }
            });
        }
        
        function addSelectedLocationTopics() {
            var resultDiv = dom.byId("objExtSearchLocationResults");
        
            array.forEach(resultDiv.childNodes, function(divElement) {
                var checkBoxDiv = divElement.firstChild;
                if (checkBoxDiv && checkBoxDiv.firstChild.id) {
                    var checkBox = registry.byId(checkBoxDiv.firstChild.id);
                } else {
                    return;
                }
        
                if (checkBox.checked) {
                    // If the checked item is already contained in the result list then do nothing
                    if (array.some(UtilGrid.getTableData("objExtSearchLocationTerms"), function(item){ return (checkBox.id == item.topicId); }))
                        return;
        
                    // Otherwise add the item to the result list
                    UtilGrid.addTableDataRow("objExtSearchLocationTerms", {topicId: checkBox.id, title: checkBox.name} );
                }
            });
        }
        
        function showNoLocationResults() {
            addLocationResultTextElement("<fmt:message key='sns.noResultHint' />");
            style.set("objExtSearchAddLocationTopicButtonSpan", "display", "none");
        }
        
        function showErrorResults() {
            addLocationResultTextElement("<fmt:message key='sns.error' />");
            style.set("objExtSearchAddLocationTopicButtonSpan", "display", "none");
        }
        
        function clearLocationResults() {
            var resultDiv = dom.byId("objExtSearchLocationResults");
        
            while (resultDiv.hasChildNodes()) {
                var divElement = resultDiv.removeChild(resultDiv.firstChild);
                var checkBox = divElement.firstChild;
                if (checkBox && checkBox.firstChild.id) {
                    registry.byId(checkBox.firstChild.id).destroy();
                }
            }
        }
        
        function addLocationResultTextElement(text) {
            var resultDiv = dom.byId("objExtSearchLocationResults");
            var divElement = document.createElement("div");
            var labelElement = document.createElement("b");
            var labelText = document.createTextNode(text);
            resultDiv.appendChild(divElement);
            divElement.appendChild(labelElement);
            labelElement.appendChild(labelText);
        
            divElement.style['marginTop'] = "5px";
        }
        
        // ************************** Object class functions (Thema/Objektklassen) **************************
        
        // Button function to check all existing object class checkboxes
        function selectAllObjectClasses() {
            var checkBoxId = "objTopicObjectClass";
        
            for (var i = 0; i < 7; ++i) {
                registry.byId(checkBoxId + i).setValue(true);
            }
        }
        
        // Button function to uncheck all existing object class checkboxes
        function deselectAllObjectClasses() {
            var checkBoxId = "objTopicObjectClass";
        
            for (var i = 0; i < 7; ++i) {
                registry.byId(checkBoxId + i).setValue(false);
            }
        }

        /**
         * PUBLIC METHODS
         */
        pageResearchSearchExtObj = {
            navInnerTab: navInnerTab,
            selectAllObjectClasses: selectAllObjectClasses,
            deselectAllObjectClasses: deselectAllObjectClasses,
            findTopics: findTopics,
            addSelectedTopics: addSelectedTopics,
            findLocationTopics: findLocationTopics,
            addSelectedLocationTopics: addSelectedLocationTopics,
            startNewSearch: startNewSearch,
            resetInput: resetInput,
            queryObjectsExtended: queryObjectsExtended
        };
        
    });
</script>
</head>

<body>
<!-- EXTENDED SEARCH START -->
  <!-- EXTENDED SEARCH TAB CONTAINER START -->
    <div id="obj" data-dojo-type="dijit/layout/TabContainer" doLayout="false" selectedChild="objTopic">
    <!-- EXTENDED SEARCH TAB 1 START -->
        <div id="objTopic" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder grey" title="<fmt:message key="dialog.research.ext.obj.theme" />">
      <!-- EXTENDED SEARCH TAB 1 SUB 1 START -->
        <div id="objTopic0">
        <div class="tabContainerSubNavi">
            <ul>
              <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.obj.mode" />"><fmt:message key="dialog.research.ext.obj.mode" /></a></li>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objTopic', 1, 3)" title="<fmt:message key="dialog.research.ext.obj.objClasses" />"><fmt:message key="dialog.research.ext.obj.objClasses" /></a></li>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objTopic', 2, 3)" title="<fmt:message key="dialog.research.ext.obj.thesaurus" />"><fmt:message key="dialog.research.ext.obj.thesaurus" /></a></li>
            </ul>
          </div>
        <div class="spacer"></div>
        <div class="inputContainer field inTabWithMenu noSpaceBelow">
          <span class="note"><b><fmt:message key="dialog.research.ext.obj.description" /></b></span>
          <div class="spacer"></div>

          <span class="label noSpaceBelow"><label class="inActive" for="objTopicInputBool"><fmt:message key="dialog.research.ext.obj.contains" /></label>
            <select data-dojo-type="dijit/form/Select" id="objTopicInputBool"><option value="0"><fmt:message key="dialog.research.ext.obj.contains.all" /></option><option value="1"><fmt:message key="dialog.research.ext.obj.contains.one" /></option></select>
          </span>

          <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7046)"><fmt:message key="dialog.research.ext.obj.mode" /></label></span>
          <div class="checkboxContainer">
            <input type="radio" name="objMode" id="objMode1" class="radio entry first" />
            <label class="inActive entry closer w116" for="objMode1"><fmt:message key="dialog.research.ext.obj.full" /></label>
            <input type="radio" name="objMode" id="objMode2" class="radio entry" checked />
            <label class="inActive entry closer" for="objMode2"><fmt:message key="dialog.research.ext.obj.substring" /></label>
          </div>
          <div class="fill"></div>
          <div class="spacerField"></div>
         </div>
        </div>
      <!-- EXTENDED SEARCH TAB 1 SUB 1 END -->
      
      <!-- EXTENDED SEARCH TAB 1 SUB 2 START -->
          <div id="objTopic1">
        <div class="tabContainerSubNavi">
            <ul>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objTopic', 0, 3)" title="<fmt:message key="dialog.research.ext.obj.mode" />"><fmt:message key="dialog.research.ext.obj.mode" /></a></li>
              <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.obj.objClasses" />"><fmt:message key="dialog.research.ext.obj.objClasses" /></a></li>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objTopic', 2, 3)" title="<fmt:message key="dialog.research.ext.obj.thesaurus" />"><fmt:message key="dialog.research.ext.obj.thesaurus" /></a></li>
            </ul>
          </div>
        <div class="spacer"></div>
        <div class="inputContainer field inTabWithMenu noSpaceBelow">
          <div class="checkboxContainer half">
            <span class="input"><input type="checkbox" name="objTopicObjectClass0" id="objTopicObjectClass0" checked="true" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTopicObjectClass0">Class0</label></span>
            <span class="input"><input type="checkbox" name="objTopicObjectClass1" id="objTopicObjectClass1" checked="true" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTopicObjectClass1">Class1</label></span>
            <span class="input noSpaceBelow"><input type="checkbox" name="objTopicObjectClass2" id="objTopicObjectClass2" checked="true" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTopicObjectClass2">Class2</label></span>
          </div>
          <div class="checkboxContainer">
            <span class="input"><input type="checkbox" name="objTopicObjectClass3" id="objTopicObjectClass3" checked="true" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTopicObjectClass3">Class3</label></span>
            <span class="input"><input type="checkbox" name="objTopicObjectClass4" id="objTopicObjectClass4" checked="true" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTopicObjectClass4">Class4</label></span>
            <span class="input"><input type="checkbox" name="objTopicObjectClass5" id="objTopicObjectClass5" checked="true" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTopicObjectClass5">Class5</label></span>
            <span class="input"><input type="checkbox" name="objTopicObjectClass6" id="objTopicObjectClass6" checked="true" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTopicObjectClass6">Class6</label></span>
          </div>
        </div>
        <div class="spacerField" style="height:28px !important;">

          <span style="float:left;">
            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.research.ext.obj.clearSelection" />" onclick="pageResearchSearchExtObj.deselectAllObjectClasses()"><fmt:message key="dialog.research.ext.obj.clearSelection" /></button>
          </span>
          <span style="float:left;">
            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.research.ext.obj.selectAll" />" onclick="pageResearchSearchExtObj.selectAllObjectClasses();"><fmt:message key="dialog.research.ext.obj.selectAll" /></button>
          </span>

        </div>
      </div>
      <!-- EXTENDED SEARCH TAB 1 SUB 2 END -->
      
      <!-- EXTENDED SEARCH TAB 1 SUB 3 START -->
      <div id="objTopic2" style="display:none;">
        <div class="tabContainerSubNavi">
          <ul>
            <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objTopic', 0, 3)" title="<fmt:message key="dialog.research.ext.obj.mode" />"><fmt:message key="dialog.research.ext.obj.mode" /></a></li>
            <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objTopic', 1, 3)" title="<fmt:message key="dialog.research.ext.obj.objClasses" />"><fmt:message key="dialog.research.ext.obj.objClasses" /></a></li>
            <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.obj.thesaurus" />"><fmt:message key="dialog.research.ext.obj.thesaurus" /></a></li>
          </ul>
        </div>
        <div class="spacer"></div>
          <div class="inputContainer field inTabWithMenu grey noSpaceBelow">
          <span class="label"><label for="objTopicThesaurus" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7047)"><fmt:message key="dialog.research.ext.obj.thesaurusText" /></label></span>
          <div class="input spaceBelow">
            <span class="inputButtonInLineLeft">
              <input type="text" id="objTopicThesaurus" name="objTopicThesaurus" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;" />
            </span>

            <span class="inputButtonInLineRight">
              <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.research.ext.obj.thesaurusSearch" />" onclick="pageResearchSearchExtObj.findTopics()"><fmt:message key="dialog.research.ext.obj.thesaurusSearch" /></button>
            </span>
          </div>
        </div>

        <div class="inputContainer field grey">
          <span class="half" style="float:left;">
            <div id="objExtSearchThesaurusResults" class="checkboxContainer" style="overflow: auto; height: 225px;">
            </div>
          </span>

          <span id="objExtSearchAddTopicButtonSpan" style="float:left; margin-top:95px; display:none;">
            <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.research.ext.obj.apply" />" onclick="pageResearchSearchExtObj.addSelectedTopics()"><fmt:message key="dialog.research.ext.obj.apply" /> -&gt;</button>
          </span>

          <span style="float:right;">
              <span class="label"><label for="objExtSearchThesaurusTerms" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7048)"><fmt:message key="dialog.research.ext.obj.searchTerms" /></label></span>
              <div class="tableContainer spaceBelow">
                  <div id="objExtSearchThesaurusTerms" autoHeight="8" minRows="8" class="hideTableHeader"></div>
              </div>

            <span class="label">
              <label class="inActive" for="thesaurusTermsRelation"><fmt:message key="dialog.research.ext.obj.contains" /></label>
              <select data-dojo-type="dijit/form/Select" id="thesaurusTermsRelation"><option value="0"><fmt:message key="dialog.research.ext.obj.contains.all" /></option><option value="1"><fmt:message key="dialog.research.ext.obj.contains.one" /></option></select>
            </span>

          </span>

        </div>
        
        <div class="spacer"></div>
      </div>
      <!-- EXTENDED SEARCH TAB 1 SUB 3 END -->
    </div>
    <!-- EXTENDED SEARCH TAB 1 END -->

    <!-- EXTENDED SEARCH TAB 2 START -->
        <div id="objSpace" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder grey" title="<fmt:message key="dialog.research.ext.obj.location" />">
      <!-- EXTENDED SEARCH TAB 2 SUB 1 START -->
          <div id="objSpace0">
        <div class="tabContainerSubNavi">
            <ul>
              <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.obj.snsLocation" />"><fmt:message key="dialog.research.ext.obj.snsLocation" /></a></li>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objSpace', 1, 3)" title="<fmt:message key="dialog.research.ext.obj.customLocation" />"><fmt:message key="dialog.research.ext.obj.customLocation" /></a></li>
<!-- 
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objSpace', 2, 3)" title="<fmt:message key="dialog.research.ext.obj.restrictmap" />"><fmt:message key="dialog.research.ext.obj.restrictmap" /></a></li>
 -->
            </ul>
          </div>
        <div class="spacer"></div>

        <div class="inputContainer field inTabWithMenu grey noSpaceBelow">
          <span class="label"><label for="objLocationTopic" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7049)"><fmt:message key="dialog.research.ext.obj.thesaurusLocationText" /></label></span>
          <div class="input spaceBelow">
            <span class="inputButtonInLineLeft">
              <input type="text" id="objLocationTopic" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;"/>
            </span>

            <span class="inputButtonInLineRight">
              <button data-dojo-type="dijit/form/Button" id="researchExtObjThesSearch" title="<fmt:message key="dialog.research.ext.obj.thesaurusLocationSearch" />" onclick="pageResearchSearchExtObj.findLocationTopics()"><fmt:message key="dialog.research.ext.obj.thesaurusLocationSearch" /></button>
            </span>
          </div>
        </div>

        <div class="inputContainer field grey">
          <span class="half" style="float:left;">
            <div id="objExtSearchLocationResults" class="checkboxContainer" style="overflow: auto; height: 225px;">
            </div>
          </span>

          <span id="objExtSearchAddLocationTopicButtonSpan" style="float:left; margin-top:95px; display:none;">
            <button data-dojo-type="dijit/form/Button" id="researchExtObjApplyTerms" title="<fmt:message key="dialog.research.ext.obj.apply" />" onclick="pageResearchSearchExtObj.addSelectedLocationTopics()"><fmt:message key="dialog.research.ext.obj.apply" /> -&gt;</button>
          </span>

          <span style="float:right;">
              <span class="label"><label for="objExtSearchLocationTerms" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7050)"><fmt:message key="dialog.research.ext.obj.searchTerms" /></label></span>
              <div class="tableContainer spaceBelow">
                  <div id="objExtSearchLocationTerms" autoHeight="8" minRows="8" class="hideTableHeader"></div>
              </div>

            <span class="label">
              <label class="inActive" for="geoThesaurusTermsRelation"><fmt:message key="dialog.research.ext.obj.contains" /></label>
              <select data-dojo-type="dijit/form/Select" id="geoThesaurusTermsRelation"><option value="0"><fmt:message key="dialog.research.ext.obj.contains.all" /></option><option value="1"><fmt:message key="dialog.research.ext.obj.contains.one" /></option></select>
            </span>

          </span>

        </div>

        <div class="spacer"></div>
        </div>
      <!-- EXTENDED SEARCH TAB 2 SUB 1 END -->
      
      <!-- EXTENDED SEARCH TAB 2 SUB 2 START -->
          <div id="objSpace1">
        <div class="tabContainerSubNavi">
            <ul>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objSpace', 0, 3)" title="<fmt:message key="dialog.research.ext.obj.snsLocation" />"><fmt:message key="dialog.research.ext.obj.snsLocation" /></a></li>
              <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.obj.customLocation" />"><fmt:message key="dialog.research.ext.obj.customLocation" /></a></li>
<!-- 
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objSpace', 2, 3)" title="<fmt:message key="dialog.research.ext.obj.restrictmap" />"><fmt:message key="dialog.research.ext.obj.restrictmap" /></a></li>
 -->
            </ul>
          </div>
        <div class="spacer"></div>
        <div class="inputContainer field inTabWithMenu">
          <span class="label"><label for="objSpaceGeoUnit" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7051)"><fmt:message key="dialog.research.ext.obj.customLocation" />:</label></span>
          <span class="input"><input listId="1100" style="width:782px;" id="objSpaceGeoUnit" /></span>
          </div>
        <div class="spacerField"></div>
        </div>
      <!-- EXTENDED SEARCH TAB 2 SUB 2 END -->
      
      <!-- EXTENDED SEARCH TAB 2 SUB 3 START -->
          <div id="objSpace2">
        <div class="tabContainerSubNavi">
            <ul>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objSpace', 0, 3)" title="<fmt:message key="dialog.research.ext.obj.customLocation" />"><fmt:message key="dialog.research.ext.obj.customLocation" /></a></li>
              <li><a href="#" onclick="pageResearchSearchExtObj.navInnerTab('objSpace', 1, 3)" title="<fmt:message key="dialog.research.ext.obj.customLocation" />"><fmt:message key="dialog.research.ext.obj.customLocation" /></a></li>
              <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.obj.restrictmap" />"><fmt:message key="dialog.research.ext.obj.restrictmap" /></a></li>
            </ul>
          </div>
        <div class="spacer"></div>
        <div class="inputContainer field inTabWithMenu noSpaceBelow xScroll yNoScroll">
          <span><img src="img/map.gif" width="820" height="586" alt="Platzhalter Karte" /></span>
          <div class="spacer"></div>
          <span class="label">Selektiertes Gebiet <strong>Verwaltungseinheit</strong> oder <strong>geogr. Daten</strong></span>
          <div class="spacer"></div>
          <span class="label"><strong>Art der Einschr&auml;nkung</strong></span>
          <div class="checkboxContainer noSpaceBelow">
            <span class="input"><input type="checkbox" name="objSpaceMapConstraint0" widgetId="objSpaceMapConstraint0" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objSpaceMapConstraint0">Der Raumbezug der Ergebnisse liegt innerhalb des gew&auml;hlten Kartenausschnittes</label></span>
            <span class="input"><input type="checkbox" name="objSpaceMapConstraint1" widgetId="objSpaceMapConstraint1" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objSpaceMapConstraint1">Der Raumbezug der Ergebnisse schneidet den gew&auml;hlten Kartenausschnittes</label></span>
            <span class="input"><input type="checkbox" name="objSpaceMapConstraint2" widgetId="objSpaceMapConstraint2" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objSpaceMapConstraint2">Der Raumbezug der Ergebnisse umschlie&szlig;t den gew&auml;hlten Kartenausschnittes</label></span>
          </div>
          <div class="spacerField"></div>
          </div>
        </div>
      <!-- EXTENDED SEARCH TAB 2 SUB 3 END -->
        </div>
    <!-- EXTENDED SEARCH TAB 2 END -->

    <!-- EXTENDED SEARCH TAB 3 START -->
        <div id="objTime" data-dojo-type="dijit/layout/ContentPane" class="blueTopBorder grey" title="<fmt:message key="dialog.research.ext.obj.time" />">
      <!-- EXTENDED SEARCH TAB 3 SUB 1 START -->
          <div id="objTime0">
        <div class="tabContainerSubNavi">
            <ul>
              <li><a nohref="nohref" class="current" title="<fmt:message key="dialog.research.ext.obj.timeLimit" />"><fmt:message key="dialog.research.ext.obj.timeLimit" /></a></li>
            </ul>
          </div>
        <div class="spacer"></div>
        <div class="inputContainer field inTabWithMenu grey">
          <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7052)"><fmt:message key="dialog.research.ext.obj.timeRef" />:</label></span>
          <span class="note"><fmt:message key="dialog.research.ext.obj.timeIn" /></span>
          <div class="spacer"></div>
          <div class="inputContainer spaceBelow" style="width:50%;">

            <span class="outer" style="width:33%;">
              <div>
                  <span class="label hidden"><label for="objTimeRef1">Typ</label></span>
                  <span class="input">
                    <select data-dojo-type="dijit/form/Select" autoComplete="false" style="width:100%;" id="objTimeRef1"><option value="am"><fmt:message key="dialog.research.ext.obj.content.time.at" /></option><option value="seit"><fmt:message key="dialog.research.ext.obj.content.time.since" /></option><option value="bis"><fmt:message key="dialog.research.ext.obj.content.time.until" /></option><option value="von"><fmt:message key="dialog.research.ext.obj.content.time.fromto" /></option></select>
                  </span>
              </div>
            </span>
            <span class="outer" style="width:33%;">
              <div>
                  <span class="label hidden"><label for="objTimeRef1From">Datum 1 [TT.MM.JJJJ]</label></span>
                  <span class="input"><div data-dojo-type="dijit/form/DateTextBox" id="objTimeRef1From"  displayFormat="dd.MM.yyyy" name="timeRefDate1" style="width:100%;"></div><br />TT.MM.JJJJ</span>
              </div>
            </span>
            <span class="outer" id="objTimeRef1ToEditor" style="display:none;width:33%;">
              <div>
                  <span class="label hidden"><label for="objTimeRef1To">Datum 2 [TT.MM.JJJJ]</label></span>
                  <span class="input"><div data-dojo-type="dijit/form/DateTextBox" id="objTimeRef1To" displayFormat="dd.MM.yyyy" name="timeRefDate2" style="width:100%;"></div><br />TT.MM.JJJJ</span>
              </div>
            </span>

          </div>
          <div class="inputContainer">
          </div>

          <div class="inputContainer noSpaceBelow">
            <span class="label"><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 7053)"><fmt:message key="dialog.research.ext.obj.timeExt" /></label></span>
            <div class="checkboxContainer">
              <span class="input"><input type="checkbox" name="objTimeRefExtend1" id="objTimeRefExtend1" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTimeRefExtend1"><fmt:message key="dialog.research.ext.obj.timeIntersect" /></label></span>
              <span class="input"><input type="checkbox" name="objTimeRefExtend2" id="objTimeRefExtend2" data-dojo-type="dijit/form/CheckBox" /><label class="inActive" for="objTimeRefExtend2"><fmt:message key="dialog.research.ext.obj.timeContains" /></label></span>
            </div>
          </div>
          </div>
        <div class="spacer"></div>
      </div>
      <!-- EXTENDED SEARCH TAB 3 SUB 1 END -->
        </div>
    <!-- EXTENDED SEARCH TAB 3 END -->
    </div>
  <!-- EXTENDED SEARCH TAB CONTAINER END -->

  <div class="inputContainer">
    <span class="button">
      <span style="float:right;">
        <button data-dojo-type="dijit/form/Button" id="researchExtObjSearch" title="<fmt:message key="dialog.research.ext.obj.search" />" onclick="pageResearchSearchExtObj.startNewSearch()"><fmt:message key="dialog.research.ext.obj.search" /></button>
      </span>
      <span style="float:right;">
        <button data-dojo-type="dijit/form/Button" title="<fmt:message key="dialog.research.ext.obj.reset" />" onclick="pageResearchSearchExtObj.resetInput()"><fmt:message key="dialog.research.ext.obj.reset" /></button>
      </span>
      <span id="objectSearchExtLoadingZone" style="float:left; margin-top:5px; z-index: 100; visibility:hidden">
        <img src="img/ladekreis.gif" />
      </span>
    </span>
  </div>


<!-- EXTENDED SEARCH END -->
</body>