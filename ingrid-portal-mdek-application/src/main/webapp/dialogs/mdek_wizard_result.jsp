<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
<script type="text/javascript">
var dialogWizardResults;

require([
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/on",
    "dojo/topic",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/query",
    "dojo/string",
    "dojo/Deferred",
    "dojo/DeferredList",
    "dijit/registry",
    "ingrid/grid/CustomGridFormatters",
    "ingrid/grid/CustomGridEditors",
    "ingrid/layoutCreator",
    "ingrid/utils/Address",
    "ingrid/utils/Syslist",
    "ingrid/utils/Store",
    "ingrid/utils/Grid",
    "ingrid/utils/List",
    "ingrid/utils/Thesaurus",
    "ingrid/utils/Dom",
    "ingrid/utils/UI",
    "ingrid/utils/Events",
    "ingrid/utils/Tree",
    "ingrid/utils/String",
    "ingrid/IgeEvents",
    "ingrid/dialog",
    "ingrid/message",
    "ingrid/MenuActions",
    "ingrid/hierarchy/dirty"
], function(lang, array, on, topic, dom, domClass, style, query, string, Deferred, DeferredList, registry, gridFormatters, gridEditors, layoutCreator, UtilAddress, UtilSyslist, UtilStore, UtilGrid, UtilList, UtilThesaurus, UtilDOM, UtilUI, UtilEvents, UtilTree, UtilString, igeEvents, dialog, message, menuEventHandler, dirty) {

    on(_container_, "Load", function(){
        hideResults();
        createDOMElements();

        console.log("Publishing event: '/afterInitDialog/WizardResults'");
        topic.publish("/afterInitDialog/WizardResults");
    });
    
    function createDOMElements() {
        
        // initialize data grids
        
        var assistantDescriptorTableStructure = [
            {field: 'selection',name: 'selection',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: 'label',width: '600px'},
            {field: 'source',name: 'source',width: '60px'}
        ];
        layoutCreator.createDataGrid("assistantDescriptorTable", null, assistantDescriptorTableStructure, null);
        
        var assistantSpatialRefTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: "<fmt:message key='dialog.wizard.create.spatial.name' />",width: '310px'},
            {field: 'longitude1',name: "<fmt:message key='dialog.wizard.create.spatial.longitude1' />",width: '90px'},
            {field: 'latitude1',name: "<fmt:message key='dialog.wizard.create.spatial.latitude1' />",width: '90px'},
            {field: 'longitude2',name: "<fmt:message key='dialog.wizard.create.spatial.longitude2' />",width: '90px'},
            {field: 'latitude2',name: "<fmt:message key='dialog.wizard.create.spatial.latitude2' />",width: '80px'}
        ];
        layoutCreator.createDataGrid("assistantSpatialRefTable", null, assistantSpatialRefTableStructure, null);
        
        var assistantTimeRefTableStructure = [
            {field: 'selection',name: "<fmt:message key='dialog.wizard.create.time.select' />",width: '60px'},
            //{field: 'name',name: "<fmt:message key='dialog.wizard.create.time.description' />",width: '346px'},
            {field: 'date1',name: "<fmt:message key='dialog.wizard.create.time.firstDate' />",width: '95px', formatter: gridFormatters.DateCellFormatter},
            {field: 'date2',name: "<fmt:message key='dialog.wizard.create.time.secondDate' />",width: '95px', formatter: gridFormatters.DateCellFormatter},
            {field: 'type',name: "<fmt:message key='dialog.wizard.create.time.type' />",width: '80px'}
        ];
        layoutCreator.createDataGrid("assistantTimeRefTable", null, assistantTimeRefTableStructure, null);
        
        var assistantTimeEventsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'date',name: "<fmt:message key='dialog.wizard.create.time.date' />",width: '150px', formatter: gridFormatters.DateCellFormatter},
            {field: 'type',name: "<fmt:message key='dialog.wizard.create.time.type' />",width: '150px', formatter: lang.partial(gridFormatters.SyslistCellFormatter, 502)}
        ];
        layoutCreator.createDataGrid("assistantTimeEventsTable", null, assistantTimeEventsTableStructure, null);
        
        var assistantCrsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'id',name: "<fmt:message key='dialog.wizard.create.crs.id' />",width: '100px'},
            {field: 'name',name: "<fmt:message key='dialog.wizard.create.crs.name' />",width: '560px'}
        ];
        layoutCreator.createDataGrid("assistantCRSTable", null, assistantCrsTableStructure, null);

        var assistantKeywordsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: "<fmt:message key='dialog.wizard.create.keywords.label' />",width: '660px'}
        ];
        layoutCreator.createDataGrid("assistantKeywordsTable", null, assistantKeywordsTableStructure, null);
        
        var assistantConformityTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'specification',name: "<fmt:message key='dialog.wizard.create.conformity.specification' />", width: '560px'}, // 6005
            {field: 'level',name: "<fmt:message key='dialog.wizard.create.conformity.level' />", width: '100px', formatter: lang.partial(gridFormatters.SyslistCellFormatter, 6000)}
        ];
        layoutCreator.createDataGrid("assistantConformityTable", null, assistantConformityTableStructure, null);
        
        var assistantOnlineResourcesTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'url',name: "<fmt:message key='dialog.wizard.create.links.name' />",width: '500px'},
            {field: 'relationType',name: "<fmt:message key='dialog.wizard.create.links.type' />",width: '160px', formatter: lang.partial(gridFormatters.SyslistCellFormatter, 2000)}
        ];
        layoutCreator.createDataGrid("assistantOnlineResourcesTable", null, assistantOnlineResourcesTableStructure, null);
        layoutCreator.createDataGrid("assistantResourceLocatorsTable", null, assistantOnlineResourcesTableStructure, null);
        
        var assistantAddressTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'firstname',name: "<fmt:message key='dialog.wizard.create.address.firstname' />",width: '200px'},
            {field: 'lastname',name: "<fmt:message key='dialog.wizard.create.address.lastname' />",width: '200px'},
            {field: 'email',name: "<fmt:message key='dialog.wizard.create.address.email' />",width: '140px'},
            {field: 'action',name: "<fmt:message key='dialog.wizard.create.datasets.action' />", width: '120px'}
        ];
        layoutCreator.createDataGrid("assistantAddressTable", null, assistantAddressTableStructure, null);
        
        var assistantAccessTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: "<fmt:message key='dialog.wizard.create.keywords.label' />", width: '300px'}
        ];
        layoutCreator.createDataGrid("assistantAccessTable", null, assistantAccessTableStructure, null);
        
        var assistantDatasetsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: gridFormatters.BoolCellFormatter, type: gridEditors.YesNoCheckboxCellEditor, editable: true},
            {field: 'title',name: "<fmt:message key='dialog.wizard.create.datasets.label' />", width: '540px'},
            {field: 'action',name: "<fmt:message key='dialog.wizard.create.datasets.action' />", width: '120px'}
        ];
        layoutCreator.createDataGrid("assistantDatasetsTable", null, assistantDatasetsTableStructure, null);
        
        
        // connect all checkboxes to the behavior to select all table rows
        query( ".allSelector input" ).forEach( function( item ) {
            var descriptorCheckbox = registry.byId( item.id );
            on( descriptorCheckbox, "change", function() {
                addAllFunction( item.id, item.id.substring( 0, item.id.length - 8 ) );
            } );
        } );

        // initialize select box
        var storeProps = {
            data: {
                identifier: '1',
                label: '0'
            }
        };
        layoutCreator.createSelectBox( "assistantServiceType", null, storeProps, function() {
            return UtilSyslist.getSyslistEntry( 5100 );
        } );
    }

    function addAllFunction(checkboxId, tableId) {
        // get checkbox value
        var value = registry.byId( checkboxId ).checked;
        // get data from the spatial store
        var data = UtilGrid.getTableData( tableId );
        array.forEach( data, function(d) {
            d.selection = value ? 1 : 0;
        } );
        UtilGrid.getTable( tableId ).invalidate();
    }
    
    function checkAll(value) {
        // click on each check box to set to wanted state 
        query("#resultContainer .dijitCheckBoxInput").forEach( function(box) {
            if (box.checked !== value) {
                //box.click();
                registry.byId(box.id).set("value", value);
            }
        });
        
        // time ref table has radio buttons where we will select the first one
        var eventList = UtilGrid.getTableData( "assistantTimeRefTable" );
        if ( eventList.length > 0 ) {
            if (checkAll)
                dom.byId( "objectWiz_" + eventList[0].topicId ).checked = true;
            else
                dom.byId( "objectWiz_" + eventList[0].topicId ).checked = false;
        }
    }

    function showResults(showDescription, showHtmlContent, isService) {
        console.debug( "showResults" );
        style.set( "resultContainer", "display", "block" );
        style.set( "resultButtonContainer", "display", "block" );

        if (showDescription) {
            domClass.remove( "assistantDescriptionContainer", "hide" );
        } else {
            domClass.add( "assistantDescriptionContainer", "hide" );
        }

        if (showHtmlContent) {
            domClass.remove( "assistantHtmlContentContainer", "hide" );
        } else {
            domClass.add( "assistantHtmlContentContainer", "hide" );
        }
        
        if (!isService) {
            domClass.add( "assistantFeesContainer", "hide" );
            domClass.add( "assistantAccessConstraintsContainer", "hide" );
            domClass.add( "assistantServiceTypeContainer", "hide" );
            domClass.add( "assistantDatasetsContainer", "hide" );
            domClass.add( "assistantResourceLocatorsContainer", "hide" );
            domClass.add( "assistantConformitiesContainer", "hide" );
        }

        // reset table size
        query(".ui-widget", "resultsContainer").forEach(function(grid) { registry.byId(grid.id).reinitLastColumn(true); });
    }

    function resetInputFields() {
        registry.byId( "assistantHtmlTitleCheckbox" ).setValue( false );
        registry.byId( "assistantHtmlTitle" ).setValue( "" );
        registry.byId( "assistantDescriptionCheckbox" ).setValue( false );
        registry.byId( "assistantDescription" ).setValue( "" );
        registry.byId( "assistantFeesCheckbox" ).setValue( false );
        registry.byId( "assistantFees" ).setValue( "" );
        UtilGrid.setTableData( "assistantAccessTable", [] );
        registry.byId( "assistantAccessTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantDescriptorTable", [] );
        registry.byId( "assistantDescriptorTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantSpatialRefTable", [] );
        registry.byId( "assistantSpatialRefTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantTimeRefTable", [] );
        registry.byId( "assistantTimeEventsTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantTimeEventsTable", [] );
        registry.byId( "assistantCRSTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantCRSTable", [] );
        registry.byId( "assistantOnlineResourcesTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantOnlineResourcesTable", [] );
        registry.byId( "assistantResourceLocatorsTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantResourceLocatorsTable", [] );
        registry.byId( "assistantKeywordsTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantKeywordsTable", [] );
        registry.byId( "assistantConformityTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantConformityTable", [] );
        UtilGrid.setTableData( "assistantAddressTable", [] );
        registry.byId( "assistantAddressTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantDatasetsTable", [] );
        registry.byId( "assistantDatasetsTableCheckbox" ).setValue( false );
    }
    
    function toggleContainers() {
        // all grids
        query("#resultsContainer .ui-widget").forEach(function(grid) {
            var length = registry.byId(grid.id).data.length;
            //console.log("Grid " + grid.id + ": " + length);
            var node = UtilDOM.findParentNodeWithClass( grid, "outer" );
            if (length === 0) {
                // hide
                domClass.add(node, "hide");
            } else {
                // show
                domClass.remove(node, "hide");
            }
        });
    }
    
    // Updates the input fields with values from the given topic map
    function updateInputFields(topicMap, fields, isWebsite) {
        registry.byId( "assistantHtmlTitle" ).setValue( UtilString.stripTags( fields.title ) );
        registry.byId( "assistantHtmlContent" ).setValue( fields.content );
        dialogWizardResults.isWebsite = isWebsite;
        dialogWizardResults.databean = topicMap;

        var indexedDocument = topicMap.indexedDocument;
        var thesaTopicList = topicMap.thesaTopics;
        var locationTopicList = topicMap.locationTopics;
        var eventTopicList = topicMap.eventTopics;
        var eventsList = topicMap.events;
        var crsList = topicMap.CRS;
        var crsUnknownList = topicMap.CRSunknown;
        var onlineResourcesList = topicMap.onlineResources;
        var resourceLocatorsList = topicMap.resourceLocators;
        var keywordsList = topicMap.keywords;
        var conformitiesList = topicMap.conformities;
        var addressesList = topicMap.addresses;

        UtilList.addSNSTopicLabels( thesaTopicList );

        registry.byId( "assistantDescriptionCheckbox" ).setValue( false );
        registry.byId( "assistantHtmlContentCheckbox" ).setValue( false );

        // Description
        if (indexedDocument.description)
            registry.byId( "assistantDescription" ).setValue( lang.trim( "" + indexedDocument.description ) );

        // Fees
        if (topicMap.fees)
            registry.byId( "assistantFees" ).setValue( lang.trim( "" + topicMap.fees ) );

        // Access
        if (topicMap.accessConstraints)
            UtilStore.updateWriteStore( "assistantAccessTable", UtilList.listToTableData(
                    topicMap.accessConstraints, "label" ) );

        // Inspire Topics
        var inspireTopics = UtilThesaurus.getInspireTopics( thesaTopicList );
        console.debug( inspireTopics.length + " inspire topics found" );
        thesaTopicList = thesaTopicList.concat( inspireTopics );
        console.debug( "go on" );
        // Thesaurus Topics
        if (thesaTopicList) {
            for ( var i in thesaTopicList) {
                // Add checkbox to entry
                thesaTopicList[i].selection = 0;
            }
            UtilStore.updateWriteStore( "assistantDescriptorTable", thesaTopicList );
        }

        // Location Topics
        if (locationTopicList) {
            UtilList.addSNSLocationLabels( locationTopicList );
            for ( var i in locationTopicList) {
                // Add checkbox to entry
                locationTopicList[i].selection = 0;

                // Prepare bb for display in the table
                if (locationTopicList[i].boundingBox) {
                    locationTopicList[i].longitude1 = locationTopicList[i].boundingBox[1];
                    locationTopicList[i].latitude1 = locationTopicList[i].boundingBox[0];
                    locationTopicList[i].longitude2 = locationTopicList[i].boundingBox[3];
                    locationTopicList[i].latitude2 = locationTopicList[i].boundingBox[2];
                }
            }
            UtilStore.updateWriteStore( "assistantSpatialRefTable", locationTopicList );
        }
        console.debug( "event topics" );
        // Event Topics
        if (eventTopicList) {
            for ( var i in eventTopicList) {
                console.debug( "prepare: " + i );
                eventTopicList[i].selection = "<input type='radio' name='objectWiz_eventTopic' id='objectWiz_"+eventTopicList[i].topicId+"'>";

                if (eventTopicList[i].from && eventTopicList[i].to) {
                    eventTopicList[i].date1 = eventTopicList[i].from;
                    eventTopicList[i].date2 = eventTopicList[i].to;
                    eventTopicList[i].type = "von - bis";

                } else if (eventTopicList[i].from && eventTopicList[i].to) {
                    eventTopicList[i].date1 = eventTopicList[i].from;
                    eventTopicList[i].type = "seit";

                } else if (eventTopicList[i].from && eventTopicList[i].to) {
                    eventTopicList[i].date1 = eventTopicList[i].to;
                    eventTopicList[i].type = "bis";
                }
            }
            console.debug( "update store" );
            console.debug( eventTopicList );
            UtilStore.updateWriteStore( "assistantTimeRefTable", eventTopicList );
            console.debug( "update store finished" );
        }

        // Service Type
        if (topicMap.dataServiceType) {
            registry.byId( "assistantServiceType" ).set( "value", topicMap.dataServiceType );

        }
        
        // Events (creation, published, ...)
        if (eventsList) {
            UtilStore.updateWriteStore( "assistantTimeEventsTable", eventsList );
        }

        // CRS
        if (crsList) {
            UtilStore.updateWriteStore( "assistantCRSTable", crsList );
        }

        // Online Resources
        if (onlineResourcesList) {
            UtilStore.updateWriteStore( "assistantOnlineResourcesTable", onlineResourcesList );
        }

        // Resource Locators
        console.log( "resource locators: ", resourceLocatorsList );
        if (resourceLocatorsList) {
            UtilStore.updateWriteStore( "assistantResourceLocatorsTable", resourceLocatorsList );
        }

        // Keywords
        if (keywordsList) {
            UtilStore.updateWriteStore( "assistantKeywordsTable",
                    UtilList.listToTableData( keywordsList, "label", true ) );
        }

        // Conformities
        if (conformitiesList) {
            UtilStore.updateWriteStore( "assistantConformityTable", conformitiesList );
        }

        if (crsUnknownList && crsUnknownList.length > 0) {
            var text = "<ul>";
            array.forEach( crsUnknownList, function(crs) {
                text += "<li>" + crs.name + "</li>";
            } );
            text += "</ul>";
            dialog.show( message.get( 'general.hint' ), string.substitute(
                    message.get( 'dialog.wizard.getCap.crs.unknown' ), [ text ] ), dialog.INFO );
        }

        // Addresses
        if (addressesList) {
            // determine if the address already exists and will be linked or if it will be created
            array.forEach(addressesList, function(res) {
                if (res.uuid) {
                    res.action = '<fmt:message key="dialog.wizard.create.asLink" />';
                } else {
                    res.action = '<fmt:message key="dialog.wizard.create.asNew" />';
                }
            });
            UtilStore.updateWriteStore( "assistantAddressTable", addressesList );
        }
        
        // Coupled Resources
        if (topicMap.coupledResources) {
            array.forEach(topicMap.coupledResources, function(res) {
                if (res.uuid) {
                    res.action = '<fmt:message key="dialog.wizard.create.asLink" />';
                    res.actionId = 'LINK';
                } else {
                    res.action = '<fmt:message key="dialog.wizard.create.asNew" />';
                    res.actionId = 'CREATE';
                }
            });
            if (topicMap.coupledResources)
                UtilStore.updateWriteStore( "assistantDatasetsTable", topicMap.coupledResources );
        }

        // only show input fields with content!
        toggleContainers();
        
        console.debug( "finished update" );
    }

    function hideResults() {
        style.set( "resultContainer", "display", "none" );
        style.set( "resultButtonContainer", "display", "none" );
    }

    function addValuesToObject(/*boolean*/applyAll) {
        if (!UtilEvents.publishAndContinue("/onBeforeDialogAccept/WizardResults")) return;

        // close dialog in the beginning before other popups prevent this!
        // just hide dialog to prevent destorying the data from the tables
        dialogWizardResults.thisDialog.domNode.style.visibility = "hidden";

        // If selected, add description
        var desc = "";
        var aDescr = registry.byId( "assistantDescriptionCheckbox" ).checked;
        var aHtmlContent = registry.byId( "assistantHtmlContentCheckbox" ).checked;
        if (applyAll || aDescr) {
            desc += registry.byId( "assistantDescription" ).getValue();
            desc += "\n\n";
        }
        if (applyAll || aHtmlContent) {
            desc += registry.byId( "assistantHtmlContent" ).getValue();
        }
        if (aDescr || aHtmlContent) {
            registry.byId( "generalDesc" ).setValue( lang.trim( desc ) );
        }

        // Title
        if (applyAll || registry.byId( "assistantHtmlTitleCheckbox" ).checked) {
            registry.byId( "objectName" ).setValue( lang.trim( registry.byId( "assistantHtmlTitle" ).getValue() ) );
        }

        // Fees
        if (applyAll || registry.byId( "assistantFeesCheckbox" ).checked) {
            // write value to "Nutzungsbedingungen" (useConstraints) (REDMINE-273)
            var value = registry.byId( "assistantFees" ).getValue();
            if (value !== "") {
                UtilStore.updateWriteStore("availabilityUseAccessConstraints", UtilList.listToTableData( [value] ));
            }
        }

        // Access Constraints
        var accessList = UtilGrid.getTableData( "assistantAccessTable" );
        var accessToAdd = [];
        array.forEach( accessList, function(ac) {
            if (applyAll || ac.selection == 1) {
                accessToAdd.push( {
                    title: ac.label
                } );
            }
        } );
        
        if (accessToAdd.length > 0) {
            registry.byId( "availabilityAccessConstraints" ).setData( accessToAdd );
        }

        // Service Type
        if (applyAll || registry.byId( "assistantServiceTypeCheckbox" ).checked) {
            registry.byId( "ref3ServiceType" ).setValue( registry.byId( "assistantServiceType" ).getValue() );
        }

        // Add Thesaurus Topics 
        var thesaList = UtilGrid.getTableData( "assistantDescriptorTable" );
        for ( var i in thesaList) {
            // If checkbox is selected
            if (applyAll || thesaList[i].selection == 1) {
                if (thesaList[i].source == "INSPIRE") {
                    // if it's an Inspire topic that's not already in the list
                    UtilThesaurus.addInspireTopics( [ thesaList[i].title ] );
                } else {
                    // and if the descriptor isn't already in the target list
                    if (dojo.every( UtilGrid.getTableData( "thesaurusTerms" ), function(item) {
                        return (thesaList[i].topicId != item.topicId);
                    } )) {
                        // add descriptor to store
                        UtilGrid.addTableDataRow( "thesaurusTerms", {
                            topicId: thesaList[i].topicId,
                            label: thesaList[i].title,
                            title: thesaList[i].title,
                            source: thesaList[i].source,
                            sourceString: thesaList[i].source
                        } );
                    }
                }
            }
        }

        // Add Location Topics
        //var spatialStore = registry.byId("spatialRefAdminUnit").store;
        var locationList = UtilGrid.getTableData( "assistantSpatialRefTable" );
        for ( var i in locationList) {
            // If checkbox is selected
            if (applyAll || locationList[i].selection == 1) {
                var topicItem = locationList[i];
                var topicId = locationList[i].topicId;
                var type = locationList[i].type;
                var targetGrid = "spatialRefAdminUnit";
                if ( type === "F" || !topicId ) targetGrid = "spatialRefLocation";
                
                var storeData = UtilGrid.getTableData( targetGrid );
                var storedTopicIdx = null;
                var storedTopic = null;
                // Check if the topic is already in the spatial ref list
                for ( var j in storeData) {
                    if (storeData[j].topicId == topicId) {
                        storedTopic = storeData[j];
                        storedTopicIdx = j;
                        continue;
                    }
                }

                // If the topic was found, update the bounding box and continue
                if (storedTopic) {
                    UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "label",
                            topicItem.type ? topicItem.name + ", " + topicItem.type : topicItem.name );
                    if (topicItem.boundingBox) {
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "longitude1",
                                topicItem.boundingBox[1] );
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "latitude1",
                                topicItem.boundingBox[0] );
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "longitude2",
                                topicItem.boundingBox[3] );
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "latitude2",
                                topicItem.boundingBox[2] );
                    }
                    // Update elements that aren't displayed in the table
                    storedTopic.nativeKey = topicItem.nativeKey;
                    storedTopic.name = topicItem.name;
                    storedTopic.topicType = topicItem.type;
                    storedTopic.topicTypeId = topicItem.typeId;
                } else {
                    // The topic was not found in the result list. Create a new key and
                    // add the topic to the list
                    //var key = UtilStore.getNewKey(spatialStore);
                    if (topicItem.boundingBox) {
                        UtilGrid.addTableDataRow( targetGrid, {
                            topicId: topicItem.topicId,
                            label: topicItem.type ? topicItem.name + ", " + topicItem.type : topicItem.name,
                            name: topicItem.name,
                            longitude1: topicItem.boundingBox[1],
                            latitude1: topicItem.boundingBox[0],
                            longitude2: topicItem.boundingBox[3],
                            latitude2: topicItem.boundingBox[2],
                            nativeKey: topicItem.nativeKey,
                            topicType: topicItem.type,
                            topicTypeId: topicItem.typeId
                        } );
                    } else {
                        UtilGrid.addTableDataRow( targetGrid, {
                            topicId: topicItem.topicId,
                            label: topicItem.type ? topicItem.name + ", " + topicItem.type : topicItem.name,
                            name: topicItem.name,
                            nativeKey: topicItem.nativeKey,
                            topicType: topicItem.type,
                            topicTypeId: topicItem.typeId
                        } );
                    }
                }
            }
        }

        // Add Event Topic
        var eventList = UtilGrid.getTableData( "assistantTimeRefTable" );
        for ( var i in eventList) {
            var eventJS = eventList[i];
            // If radio button is selected
            if (dom.byId( "objectWiz_" + eventJS.topicId ).checked) {
                if (eventJS.type == "von - bis")
                    eventJS.type = "von";

                registry.byId( "timeRefType" ).setValue( eventJS.type );

                if (eventJS.type == "bis") {
                    registry.byId( "timeRefDate1" ).setValue( eventJS.to );

                } else {
                    registry.byId( "timeRefDate1" ).setValue( eventJS.from );
                    registry.byId( "timeRefDate2" ).setValue( eventJS.to );
                }
            }
        }

        // add Events (created, published, ...)
        var datesList = UtilGrid.getTableData( "assistantTimeEventsTable" );
        for ( var i in datesList) {
            // If checkbox is selected
            if (applyAll || datesList[i].selection == 1) {
                UtilGrid.addTableDataRow( "timeRefTable", datesList[i] );
            }
        }

        // add Keywords
        var keywords = [];
        var defKeywords;
        array.forEach( UtilGrid.getTableData( "assistantKeywordsTable" ), function(row) {
            if (applyAll || row.selection == 1) {
                keywords.push( row.label );
            }
        } );
        if (keywords.length > 0) {
            defKeywords = igeEvents.addKeywords( keywords, {
                id: "getCapabilitiesWizard",
                _termListWidget: "thesaurusTerms"
            } );
        } else {
            defKeywords = new Deferred();
            defKeywords.resolve();
        }

        // CRS
        var crsData = UtilGrid.getTableData( "assistantCRSTable" );
        var crsToAdd = [];
        array.forEach( crsData, function(crs) {
            if (applyAll || crs.selection == 1) {
                crsToAdd.push( {
                    title: crs.name
                } );
            }
        } );
        if (crsToAdd.length > 0) {
            UtilStore.updateWriteStore( "ref1SpatialSystem", crsToAdd );
        }

        // Conformities
        var conformitiesData = UtilGrid.getTableData( "assistantConformityTable" );
        var conformitiesToAdd = [];
        array.forEach( conformitiesData, function(conformity) {
            if (applyAll || conformity.selection == 1) {
                conformitiesToAdd.push( conformity );
            }
        } );
        if (conformitiesToAdd.length > 0) {
            UtilStore.updateWriteStore( "extraInfoConformityTable", conformitiesToAdd );
        }

        // Addresses
        var defs = [];
        var addressData = UtilGrid.getTableData( "assistantAddressTable" );
        var addressesToAdd = [];
        // get localized syslist values
        var sysEmail = UtilSyslist.getSyslistEntryName( 4430, 3 ); // E-Mail
        var sysPhone = UtilSyslist.getSyslistEntryName( 4430, 1 ); // Phone
        var sysGermany = UtilSyslist.getSyslistEntryName( 6200, 276 ); // Germany
        array.forEach( addressData, function(address) {
            if (applyAll || address.selection == 1) {
                var def = new Deferred();
                defs.push( def );
                // get localized syslist values
                var sysRelationName = UtilSyslist.getSyslistEntryName( 505, 1 ); // Anbieter
                // if uuid is set, this means that an address was found by first, lastname and email, see REDMINE-125
                // create a new address if none exists with the given data
                if (!address.uuid) {
                    AddressService.createNewAddress( null, function(data) {
                        data.givenName = address.firstname;
                        data.name = address.lastname;
                        data.addressClass = 3; // Free Address
                        data.communication = [ {
                            medium: sysEmail,
                            value: address.email
                        } ];
                        data.organisation = address.organisation;
                        data.street = address.street;
                        data.city = address.city;
                        if ( address.country && address.country.toLowerCase() == "de" )
                            data.countryName = sysGermany;
                        else
                            data.countryName = address.country;
                        data.postalCode = address.postcode;
                        if (address.phone && address.phone.length > 0) {
                            data.communication.push( {
                                medium: sysPhone,
                                value: address.phone
                            } );
                        }
                        AddressService.saveAddressData( data, true, false, function(savedData) {
                            console.debug( "Address created ... create reference within new object!" );
                            console.debug( savedData );
                            UtilList.addIcons( [ savedData ] );
                            UtilAddress.addAddressTitles( [ savedData ] );
                            UtilList.addAddressLinkLabels( [ savedData ] );
                            savedData.nameOfRelation = sysRelationName;
                            console.debug( savedData );
                            addressesToAdd.push( savedData ); //{uuid: data.uuid, name, givenName, writePermission: true})
                            def.resolve();
                        } );
                    } );
        
                } else {
                    // the address already exists so just link to it!
                    // for the link we just need the uuid and the nameOfRelation; other attributes
                    // are needed for correctly showing data in table
                    def.resolve();
                    address.nameOfRelation = sysRelationName;
                    address.givenName = address.firstname;
                    address.name = address.lastname;
                    address.addressClass = address.type;
                    UtilList.addIcons( [ address ] );
                    UtilAddress.addAddressTitles( [ address] );
                    UtilList.addAddressLinkLabels( [ address] );
                    addressesToAdd.push( address );
                }
            }
        } );

        // wait for all addresses being added and update address table then
        var defAddresses = new DeferredList( defs ).then( function() {
            console.debug( "update address table now!" );
            if (addressesToAdd.length > 0) {
                UtilStore.updateWriteStore( "generalAddress", addressesToAdd );
            }
            // reload sub tree of newly created addresses
            // WARNING: this removes focus of selected node and new node cannot be clicked again!?
            // -> added parameter to prevent selection of node!
            var freeAddresses = registry.byId( "addressFreeRoot" );
            if ( freeAddresses ) {
                menuEventHandler.reloadSubTreeByNode( freeAddresses, true );
            }
        } );

        // add versions and operations automatically of a capabilities document!
        if (dialogWizardResults.databean) {
            var serviceType = dialogWizardResults.databean.serviceType;
            
            if (serviceType) {
                if (serviceType.indexOf( "CSW" ) != -1) {
                    UtilStore.updateWriteStore( "ref3ServiceTypeTable", UtilList.listToTableData( [ 207 ] ) );
    
                } else if (serviceType.indexOf( "WMS" ) != -1) {
                    UtilStore.updateWriteStore( "ref3ServiceTypeTable", UtilList.listToTableData( [ 202 ] ) );
    
                } else if (serviceType.indexOf( "WFS" ) != -1) {
                    UtilStore.updateWriteStore( "ref3ServiceTypeTable", UtilList.listToTableData( [ 201 ] ) );
    
                } else if (serviceType.indexOf( "WCTS" ) != -1) {
    
                } else if (serviceType.indexOf( "WCS" ) != -1) { // WCS does not exist yet
                    UtilStore.updateWriteStore( "ref3ServiceTypeTable", UtilList.listToTableData( [ 203 ] ) );
                    registry.byId( "ref3Explanation" ).setValue( "WCS Service" );
    
                }
    
                // Versions
                if (dialogWizardResults.databean.versions) {
                    UtilStore.updateWriteStore( "ref3ServiceVersion", UtilList
                            .listToTableData( dialogWizardResults.databean.versions ) );
                }
    
                // Operations
                // Prepare the operation table for display.
                // Add table indices to the main obj and paramList
                // Add table indices and convert to tableData: platform, addressList and dependencies
                var operations = dialogWizardResults.databean.operations;
                if (operations) {
                    for (var i = 0; i < operations.length; ++i) {
                        operations[i].platform = UtilList.listToTableData( operations[i].platform );
                        operations[i].addressList = UtilList.listToTableData( operations[i].addressList );
                        operations[i].dependencies = UtilList.listToTableData( operations[i].dependencies );
                    }
                }
                UtilStore.updateWriteStore( "ref3Operation", operations );
            }
        }

        // Online Resources
        var links = UtilGrid.getTableData( "assistantOnlineResourcesTable" );
        var onlineResources = [];
        array.forEach( links, function(link) {
            if (applyAll || link.selection == 1) {
                onlineResources.push( createNewUrl( link.url, link.url ) );
            }
        } );

        // Resource Locators
        links = UtilGrid.getTableData( "assistantResourceLocatorsTable" );
        var resourceLocators = [];
        array.forEach( links, function(link) {
            if (applyAll || link.selection == 1) {
                resourceLocators.push( createNewUrl( link.url, link.url, link.relationTypeName ) );
            }
        } );

        // Add the url to the link table if we created a new object with the wizard
        // not needed for the getCapabilities wizard!
        var urlData = [];
        if (dialogWizardResults.isWebsite) {
            urlData.push( createNewWebUrl() );
        }

        var allLinks = onlineResources.concat( resourceLocators.concat( urlData ) );

        if (allLinks.length > 0) {
            UtilList.addIcons( allLinks );
            UtilList.addUrlLinkLabels( allLinks );
            UtilStore.updateWriteStore( "linksTo", allLinks );
        }

        // wait for adding keywords AND addresses to be finished
        defKeywords.then( function() {
            defAddresses.then( function() {
            
                // add references to datasets/layers or create new objects
                var datasets = UtilGrid.getTableData( "assistantDatasetsTable" );
                var coupledResources = [];
                var promise = new Deferred().resolve();
                var resolveArray = [];

                // calc correct length
                var length = 0;
                array.forEach( datasets, function( dataset ) { if ((applyAll || dataset.selection == 1) && dataset.actionId == "CREATE") length++; } );
                UtilUI.initBlockerDivInfo( "layers", length, message.get("general.add.layers"));

                array.forEach( datasets, function( dataset ) {
                    if (applyAll || dataset.selection == 1) {
                        if (dataset.actionId == "CREATE") {
                            promise = promise.then( function(result) {
                                if (result) {
                                    resolveArray.push( result );
                                }
                                return dialogWizardResults.createDataset( dataset, addressesToAdd );
                            } );
                            
                        } else {
                            coupledResources.push( dataset );
                        }
                    }
                } );
                
                var addCoupledResourceInfo = function( obj ) {
                    obj.relationType = "3600";
                    obj.relationTypeName = "Verweis zu Daten";
                    // remove thesaurus entries for links, which might not be complete
                    // -> not needed for references!
                    obj.thesaurusTermsTable = [];
                    UtilList.addObjectLinkLabels([obj], true);
                    UtilList.addIcons([obj]);
                    return obj;
                };
                
                var addCoupledResourcesInfo = function( resources ) {
                    array.forEach( resources, function(obj) {
                        addCoupledResourceInfo( obj );
                    } );
                    return resources;
                };
        
                promise.then( function(lastResult) {
                    if (lastResult) {
                        resolveArray.push( lastResult );
                    }

                    UtilUI.enterLoadingState();
                    console.log("all layers created!");
                    addCoupledResourcesInfo( coupledResources );
                    array.forEach( resolveArray, function(res) {
                        // add additional information to show reference in table
                        var cr = addCoupledResourceInfo( res );
                        coupledResources.push( cr );
                    } );
                    console.log( "Adding coupled resources: ", coupledResources );
                    UtilStore.updateWriteStore( "ref3BaseDataLink", coupledResources );
                    igeEvents.refreshTabContainers();

                    UtilUI.exitLoadingState();

                    topic.publish("/afterCloseDialog/WizardResults");

                    closeThisDialog();
                } );

            });
        });
    }
    
    function prepareTopics( keywords, topics ) {
        var conTopics = [];
        array.forEach( topics, function( topic, i ) {
            if (topic && topic.length !== 0) {
                conTopics.push( topic[0] );
            } else {
                conTopics.push( { label: keywords[i], title: keywords[i], source: "FREE", sourceString: "FREE" } );
            }
        });
        return conTopics;
    }
    
    function createDataset(data, addresses) {
        var def = new Deferred();
        var parentNode = UtilTree.getNodeById("dataTree", currentUdk.parentUuid);

        // call this function as long as object has not been created due to USER_HAS_RUNNING_JOBS errors!
        var createFunction = function(def) {
            ObjectService.createNewNode( currentUdk.parentUuid, function(objNode) {
                objNode.ref1ObjectIdentifier = data.ref1ObjectIdentifier;
                objNode.objectClass = 1;
                objNode.objectName = data.title;
                objNode.generalDescription = data.generalDescription;
                objNode.ref1SpatialSystemTable = data.ref1SpatialSystemTable;
                if (addresses.length > 0) {
                    console.log("Adding addresses to layer: ", addresses);
                    objNode.generalAddressTable = addresses;
                }
                
                objNode.spatialRefLocationTable = data.spatialRefLocationTable;
                console.log("data of layer: ", data);
                var keywords = UtilList.tableDataToList( data.thesaurusTermsTable, "title" );
                igeEvents.analyzeKeywords(keywords).then( function(topics) {
                    // loading state might have ended after analyzing keywords
                    UtilUI.enterLoadingState();
                    console.log( "Found topics for layer:", topics );
                    objNode.thesaurusTermsTable = prepareTopics( keywords, topics );
                    console.log( "converted topics for layer:", objNode.thesaurusTermsTable );
                    // save the object as WORKING copy to create a UUID
                    ObjectService.saveNodeData( objNode, true, false, {
                        callback: function(bean) {
                            UtilTree.addNode("dataTree", parentNode, {
                                id: bean.uuid,
                                title: bean.title,
                                nodeAppType: bean.nodeAppType,
                                nodeDocType: bean.nodeDocType
                            });
                            UtilUI.updateBlockerDivInfo( "layers" );
                            def.resolve( bean );
                        },
                        errorHandler:function(message) { 
                            console.log("errorHandler::"+message); 
                            UtilUI.removeBlockerDivInfo( "layers" ); 
                            UtilUI.exitLoadingState(); 
                            displayErrorMessage(message);
                        },
                        exceptionHandler:function(errorString) {
                            // try to create the dataset a second later if backend was busy
                            if (errorString.indexOf( "[USER_HAS_RUNNING_JOBS]" ) != -1) {
                                console.log( "[USER_HAS_RUNNING_JOBS] ... try again in 1s" );
                                setTimeout( function() { createFunction( def ); }, 1000);
                            } else {
                                console.log( "exceptionHandler::"+errorString );
                                displayErrorMessage(errorString);
                            }
                            UtilUI.removeBlockerDivInfo( "layers" );
                            UtilUI.exitLoadingState();
                        }
                    });
                } );
            } );
        };
        // initial call
        createFunction(def);

        return def;
    }
        
    function createNewUrl(uri, title, relName) {
        var newUrl = {};

        //newUrl.Id = 0;
        newUrl.relationType = -1;
        newUrl.relationTypeName = relName ? relName : "Informationen im Internet";
        var urlName = lang.trim( registry.byId( "assistantHtmlTitle" ).getValue() );
        newUrl.name = title ? title : urlName.length !== 0 ? urlName : "Internet-Verweis";
        newUrl.url = lang.trim( uri );
        newUrl.urlType = "1"; // Internet
        newUrl.description = "";

        return newUrl;
    }

    function createNewWebUrl() {
        var newUrl = {};

        //newUrl.Id = 0;
        newUrl.relationType = 9999;
        newUrl.relationTypeName = "unspezifischer Verweis";
        var urlName = lang.trim( registry.byId( "assistantHtmlTitle" ).getValue() );
        newUrl.name = urlName.length !== 0 ? urlName : "Internet-Verweis";
        newUrl.url = lang.trim( registry.byId( "assistantURL" ).getValue() );
        newUrl.urlType = "";
        newUrl.description = "";

        return newUrl;
    }

    function closeThisDialog() {
        // console.log( "hiding wizard:", thisDialog );
        dialogWizardResults.thisDialog.hide();
    }

    dialogWizardResults = {
        closeThisDialog: closeThisDialog,
        addValuesToObject: addValuesToObject,
        updateInputFields: updateInputFields,
        showResults: showResults,
        checkAll: checkAll,
        createDataset: createDataset,
        resetInputFields: resetInputFields,
        hideResults: hideResults,
        thisDialog: null
    };

});

</script>
</head>
    <body>

        <div id="resultContainer" class="inputContainer" style="/* margin-top: 115px; margin-bottom: 20px; overflow: auto;*/">
            <span class="label"><h2><fmt:message key="dialog.wizard.create.result" /></h2></span>
            
            <span class="outer">
                <div>
                    <span id="assistantTitleContainer">
                        <div class="checkboxContainer option">
                            <span class="input" style="margin-bottom: 5px;"><input type="checkbox" name="assistantHtmlTitleCheckbox" id="assistantHtmlTitleCheckbox" data-dojo-type="dijit/form/CheckBox" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8066)"><fmt:message key="dialog.wizard.create.addTitle" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input type="text" id="assistantHtmlTitle" data-dojo-type="dijit/form/ValidationTextBox" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>

            <span class="outer">
                <div>
                    <span id="assistantDescriptionContainer">
                        <div class="checkboxContainer option">
                            <span class="input"><input type="checkbox" name="assistantDescriptionCheckbox" id="assistantDescriptionCheckbox" data-dojo-type="dijit/form/CheckBox" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8067)"><fmt:message key="dialog.wizard.create.addDescription" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input type="text" mode="textarea" id="assistantDescription" data-dojo-type="dijit/form/SimpleTextarea" rows="5" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>

            <span id="assistantHtmlContentContainer" class="outer">
                <div>
                    <span>
                        <div class="checkboxContainer option">
                            <span class="input"><input type="checkbox" name="assistantHtmlContentCheckbox" id="assistantHtmlContentCheckbox" data-dojo-type="dijit/form/CheckBox" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8068)"><fmt:message key="dialog.wizard.create.addContent" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input"><input type="text" mode="textarea" id="assistantHtmlContent" data-dojo-type="dijit/form/SimpleTextarea" rows="5" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div>
                    <span id="assistantFeesContainer">
                        <div class="checkboxContainer">
                            <span class="input"><input type="checkbox" name="assistantFeesCheckbox" id="assistantFeesCheckbox" data-dojo-type="dijit/form/CheckBox" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10200)"><fmt:message key="dialog.wizard.create.addFees" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input type="text" mode="textarea" id="assistantFees" data-dojo-type="dijit/form/SimpleTextarea" rows="5" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div id="assistantAccessConstraintsContainer">
                    <span class="label"><label for="assistantAccessTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10201)"><fmt:message key="dialog.wizard.create.accessConstraints" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                         <div id="assistantAccessTable" interactive="true" autoEdit="true" class="hideTableHeader"></div> 
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive input allSelector"><input type="checkbox" name="assistantAccessTableCheckbox" id="assistantAccessTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllAccess" /></label>
                    </div>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div>
                    <span id="assistantServiceTypeContainer">
                        <div class="checkboxContainer">
                            <span class="input"><input type="checkbox" name="assistantServiceTypeCheckbox" id="assistantServiceTypeCheckbox" data-dojo-type="dijit/form/CheckBox" /><label onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10202)"><fmt:message key="dialog.wizard.create.serviceType" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input id="assistantServiceType" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantDescriptorTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8069)"><fmt:message key="dialog.wizard.create.descriptors" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantDescriptorTable" interactive="true" autoEdit="true" class="hideTableHeader"></div>
                    </div>

                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantDescriptorTableCheckbox" id="assistantDescriptorTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllDescriptors" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>

            <span class="outer">
                <div>
                    <span class="label"><label for="assistantSpatialRefTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8071)"><fmt:message key="dialog.wizard.create.spatial" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantSpatialRefTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantSpatialRefTableCheckbox" id="assistantSpatialRefTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllSpatialRefs" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantOnlineResourcesTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10203)"><fmt:message key="dialog.wizard.create.onlineResources" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantOnlineResourcesTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantOnlineResourcesTableCheckbox" id="assistantOnlineResourcesTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllOnlineResources" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div id="assistantResourceLocatorsContainer">
                    <span class="label"><label for="assistantResourceLocatorsTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10204)"><fmt:message key="dialog.wizard.create.resourceLocators" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantResourceLocatorsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantResourceLocatorsTableCheckbox" id="assistantResourceLocatorsTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllResourceLocators" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantCRSTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10205)"><fmt:message key="dialog.wizard.create.crs" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantCRSTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantCRSTableCheckbox" id="assistantCRSTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllCrs" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantKeywordsTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10206)"><fmt:message key="dialog.wizard.create.keywords" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantKeywordsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantKeywordsTableCheckbox" id="assistantKeywordsTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllKeywords" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div id="assistantConformitiesContainer">
                    <span class="label"><label for="assistantConformityTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10207)"><fmt:message key="dialog.wizard.create.conformity" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantConformityTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantConformityTableCheckbox" id="assistantConformityTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllConformities" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div>
                    <span class="label"><label for="assistantTimeRefTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10208)"><fmt:message key="dialog.wizard.create.timeSpan" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantTimeRefTable"></div>
                    </div>
                </div>
            </span>
            <span class="outer halfWidth">
                <div>
                    <span class="label"><label for="assistantTimeEventsTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 8073)"><fmt:message key="dialog.wizard.create.timeEvents" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantTimeEventsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantTimeEventsTableCheckbox" id="assistantTimeEventsTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllTimeEvents" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantAddressTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10209)"><fmt:message key="dialog.wizard.create.address" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantAddressTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantAddressTableCheckbox" id="assistantAddressTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllAddresses" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div id="assistantDatasetsContainer">
                    <span class="label"><label for="assistantDatasetsTable" onclick="require('ingrid/dialog').showContextHelp(arguments[0], 10210)"><fmt:message key="dialog.wizard.create.datasets" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantDatasetsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive">
                            <span class="input allSelector"><input type="checkbox" name="assistantDatasetsTableCheckbox" id="assistantDatasetsTableCheckbox" data-dojo-type="dijit/form/CheckBox" /><fmt:message key="dialog.wizard.create.selectAllDatasets" />
                            </span>
                        </label>
                    </div>
                </div>
            </span>
        </div> <!-- RESULT CONTAINER END -->

        <div id="resultButtonContainer" class="inputContainer" style="/*position: absolute; bottom: 4px; width: 100%; right: 0px; background-color: white; z-index: 1000;*/">
            <span class="button" style="height:20px !important;">
                <span style="float:right;">
                    <button id="createObjWizardAcceptButton" data-dojo-type="dijit/form/Button" onclick="dialogWizardResults.addValuesToObject()"><fmt:message key="dialog.wizard.create.apply" /></button>
                </span>
                <!-- <span style="float:right;">
                    <button id="createObjWizardAcceptAllButton" data-dojo-type="dijit/form/Button" onclick="javascript:scopeWizardResults.addValuesToObject(true);"><fmt:message key="dialog.wizard.create.applyAll" /></button>
                </span> -->
                <span style="float:left;">
                    <button id="createObjWizardCancelButton" data-dojo-type="dijit/form/Button" onclick="dialogWizardResults.closeThisDialog()"><fmt:message key="dialog.wizard.create.cancel" /></button>
                </span>
            </span>
        </div>

        <!-- LEFT HAND SIDE CONTENT END -->
    </body>
</html>
