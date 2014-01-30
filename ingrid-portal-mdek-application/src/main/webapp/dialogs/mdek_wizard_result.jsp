<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
<script type="text/javascript">
    console.log("container: ", _container_);
    var scopeWizardResults = _container_;


    dojo.connect(_container_, "onLoad", function(){
        scopeWizardResults.hideResults();
        scopeWizardResults.createDOMElements();
    });
    
    scopeWizardResults.createDOMElements = function() {
        
        // initialize data grids
        
        var assistantDescriptorTableStructure = [
            {field: 'selection',name: 'selection',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: 'label',width: '600px'},
            {field: 'source',name: 'source',width: '60px'}
        ];
        createDataGrid("assistantDescriptorTable", null, assistantDescriptorTableStructure, null);
        
        var assistantSpatialRefTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: "<fmt:message key='dialog.wizard.create.spatial.name' />",width: '310px'},
            {field: 'longitude1',name: "<fmt:message key='dialog.wizard.create.spatial.longitude1' />",width: '90px'},
            {field: 'latitude1',name: "<fmt:message key='dialog.wizard.create.spatial.latitude1' />",width: '90px'},
            {field: 'longitude2',name: "<fmt:message key='dialog.wizard.create.spatial.longitude2' />",width: '90px'},
            {field: 'latitude2',name: "<fmt:message key='dialog.wizard.create.spatial.latitude2' />",width: '80px'}
        ];
        createDataGrid("assistantSpatialRefTable", null, assistantSpatialRefTableStructure, null);
        
        var assistantTimeRefTableStructure = [
            {field: 'selection',name: "<fmt:message key='dialog.wizard.create.time.select' />",width: '60px'},
            //{field: 'name',name: "<fmt:message key='dialog.wizard.create.time.description' />",width: '346px'},
            {field: 'date1',name: "<fmt:message key='dialog.wizard.create.time.firstDate' />",width: '95px', formatter: DateCellFormatter},
            {field: 'date2',name: "<fmt:message key='dialog.wizard.create.time.secondDate' />",width: '95px', formatter: DateCellFormatter},
            {field: 'type',name: "<fmt:message key='dialog.wizard.create.time.type' />",width: '80px'}
        ];
        createDataGrid("assistantTimeRefTable", null, assistantTimeRefTableStructure, null);
        
        var assistantTimeEventsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'date',name: "<fmt:message key='dialog.wizard.create.time.date' />",width: '150px', formatter: DateCellFormatter},
            {field: 'type',name: "<fmt:message key='dialog.wizard.create.time.type' />",width: '150px', formatter: dojo.partial(SyslistCellFormatter, 502)}
        ];
        createDataGrid("assistantTimeEventsTable", null, assistantTimeEventsTableStructure, null);
        
        var assistantCrsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'id',name: "<fmt:message key='dialog.wizard.create.crs.id' />",width: '100px'},
            {field: 'name',name: "<fmt:message key='dialog.wizard.create.crs.name' />",width: '560px'}
        ];
        createDataGrid("assistantCRSTable", null, assistantCrsTableStructure, null);

        var assistantKeywordsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: "<fmt:message key='dialog.wizard.create.keywords.label' />",width: '660px'}
        ];
        createDataGrid("assistantKeywordsTable", null, assistantKeywordsTableStructure, null);
        
        var assistantConformityTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'specification',name: "<fmt:message key='dialog.wizard.create.conformity.specification' />", width: '560px'}, // 6005
            {field: 'level',name: "<fmt:message key='dialog.wizard.create.conformity.level' />", width: '100px', formatter: dojo.partial(SyslistCellFormatter, 6000)}
        ];
        createDataGrid("assistantConformityTable", null, assistantConformityTableStructure, null);
        
        var assistantOnlineResourcesTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'url',name: "<fmt:message key='dialog.wizard.create.links.name' />",width: '500px'},
            {field: 'relationType',name: "<fmt:message key='dialog.wizard.create.links.type' />",width: '160px', formatter: dojo.partial(SyslistCellFormatter, 2000)}
        ];
        createDataGrid("assistantOnlineResourcesTable", null, assistantOnlineResourcesTableStructure, null);
        createDataGrid("assistantResourceLocatorsTable", null, assistantOnlineResourcesTableStructure, null);
        
        var assistantAddressTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'firstname',name: "<fmt:message key='dialog.wizard.create.address.firstname' />",width: '200px'},
            {field: 'lastname',name: "<fmt:message key='dialog.wizard.create.address.lastname' />",width: '200px'},
            {field: 'email',name: "<fmt:message key='dialog.wizard.create.address.email' />",width: '140px'},
            {field: 'action',name: "<fmt:message key='dialog.wizard.create.datasets.action' />", width: '120px'}
        ];
        createDataGrid("assistantAddressTable", null, assistantAddressTableStructure, null);
        
        var assistantAccessTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'label',name: "<fmt:message key='dialog.wizard.create.keywords.label' />", width: '300px'}
        ];
        createDataGrid("assistantAccessTable", null, assistantAccessTableStructure, null);
        
        var assistantDatasetsTableStructure = [
            {field: 'selection',name: '&nbsp;',width: '30px', formatter: BoolCellFormatter, type: YesNoCheckboxCellEditor, editable: true},
            {field: 'title',name: "<fmt:message key='dialog.wizard.create.datasets.label' />", width: '540px'},
            {field: 'action',name: "<fmt:message key='dialog.wizard.create.datasets.action' />", width: '120px'}
        ];
        createDataGrid("assistantDatasetsTable", null, assistantDatasetsTableStructure, null);
        
        
        // connect all checkboxes to the behavior to select all table rows
	    dojo.query( ".allSelector input" ).forEach( function( item ) {
            var descriptorCheckbox = dijit.byId( item.id );
            dojo.connect( descriptorCheckbox, "onClick", function() {
                scopeWizardResults.addAllFunction( item.id, item.id.substring( 0, item.id.length - 8 ) );
            } );
        } );

        // initialize select box
        var storeProps = {
            data: {
                identifier: '1',
                label: '0'
            }
        };
        createSelectBox( "assistantServiceType", null, storeProps, function() {
            return UtilSyslist.getSyslistEntry( 5100 );
        } );
    }

    scopeWizardResults.addAllFunction = function(checkboxId, tableId) {
        // get checkbox value
        var value = dijit.byId( checkboxId ).checked;
        // get data drom the spatial store
        var data = UtilGrid.getTableData( tableId );
        dojo.forEach( data, function(d) {
            d.selection = value ? 1 : 0;
        } );
        UtilGrid.getTable( tableId ).invalidate();
    };
    
    scopeWizardResults.checkAll = function(checkAll) {
        // click on each check box to set to wanted state 
        dojo.query("#resultContainer .dijitCheckBoxInput").forEach( function(box) {
            if (box.checked !== checkAll) {
                box.click();
            }
        });
        
        // time ref table has radio buttons where we will select the first one
        var eventList = UtilGrid.getTableData( "assistantTimeRefTable" );
        if ( eventList.length > 0 ) {
            if (checkAll)
                dojo.byId( "objectWiz_" + eventList[0].topicId ).checked = true;
            else
                dojo.byId( "objectWiz_" + eventList[0].topicId ).checked = false;
        }
    };

    scopeWizardResults.showResults = function(showDescription, showHtmlContent, isService) {
        console.debug( "showResults" );
        dojo.style( "resultContainer", "display", "block" );
        dojo.style( "resultButtonContainer", "display", "block" );

        if (showDescription) {
            dojo.removeClass( "assistantDescriptionContainer", "hide" );
        } else {
            dojo.addClass( "assistantDescriptionContainer", "hide" );
        }

        if (showHtmlContent) {
            dojo.removeClass( "assistantHtmlContentContainer", "hide" );
        } else {
            dojo.addClass( "assistantHtmlContentContainer", "hide" );
        }
        
        if (!isService) {
            dojo.addClass( "assistantFeesContainer", "hide" );
            dojo.addClass( "assistantAccessConstraintsContainer", "hide" );
            dojo.addClass( "assistantServiceTypeContainer", "hide" );
            dojo.addClass( "assistantDatasetsContainer", "hide" );
            dojo.addClass( "assistantResourceLocatorsContainer", "hide" );
            dojo.addClass( "assistantConformitiesContainer", "hide" );
            
        }
    }

    scopeWizardResults.resetInputFields = function() {
        dijit.byId( "assistantHtmlTitleCheckbox" ).setValue( false );
        dijit.byId( "assistantHtmlTitle" ).setValue( "" );
        dijit.byId( "assistantDescriptionCheckbox" ).setValue( false );
        dijit.byId( "assistantDescription" ).setValue( "" );
        dijit.byId( "assistantFeesCheckbox" ).setValue( false );
        dijit.byId( "assistantFees" ).setValue( "" );
        UtilGrid.setTableData( "assistantAccessTable", [] );
        dijit.byId( "assistantAccessTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantDescriptorTable", [] );
        dijit.byId( "assistantDescriptorTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantSpatialRefTable", [] );
        dijit.byId( "assistantSpatialRefTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantTimeRefTable", [] );
        dijit.byId( "assistantTimeEventsTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantTimeEventsTable", [] );
        dijit.byId( "assistantCRSTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantCRSTable", [] );
        dijit.byId( "assistantOnlineResourcesTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantOnlineResourcesTable", [] );
        dijit.byId( "assistantResourceLocatorsTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantResourceLocatorsTable", [] );
        dijit.byId( "assistantKeywordsTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantKeywordsTable", [] );
        dijit.byId( "assistantConformityTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantConformityTable", [] );
        UtilGrid.setTableData( "assistantAddressTable", [] );
        dijit.byId( "assistantAddressTableCheckbox" ).setValue( false );
        UtilGrid.setTableData( "assistantDatasetsTable", [] );
        dijit.byId( "assistantDatasetsTableCheckbox" ).setValue( false );
    };
    
    scopeWizardResults.toggleContainers = function() {
        // all grids
        dojo.query("#resultsContainer .ui-widget").forEach(function(grid) {
            var length = dijit.byId(grid.id).data.length;
            console.log("Grid " + grid.id + ": " + length);
            var node = UtilDOM.findParentNodeWithClass( grid, "outer" );
            if (length === 0) {
                // hide
                dojo.addClass(node, "hide");
            } else {
                // show
                dojo.removeClass(node, "hide");
            }
        });
    };
    
    // Updates the input fields with values from the given topic map
    scopeWizardResults.updateInputFields = function(topicMap, fields, isWebsite) {
        dijit.byId( "assistantHtmlTitle" ).setValue( fields.title );
        dijit.byId( "assistantHtmlContent" ).setValue( fields.content );
        scopeWizardResults.isWebsite = isWebsite;
        scopeWizardResults.databean = topicMap;

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

        dijit.byId( "assistantDescriptionCheckbox" ).setValue( false );
        dijit.byId( "assistantHtmlContentCheckbox" ).setValue( false );

        // Description
        if (indexedDocument.description != null)
            dijit.byId( "assistantDescription" ).setValue( dojo.trim( "" + indexedDocument.description ) );

        // Fees
        if (topicMap.fees != null)
            dijit.byId( "assistantFees" ).setValue( dojo.trim( "" + topicMap.fees ) );

        // Access
        if (topicMap.accessConstraints != null)
            UtilStore.updateWriteStore( "assistantAccessTable", UtilList.listToTableData(
                    topicMap.accessConstraints, "label" ) );

        // Inspire Topics
        var inspireTopics = getInspireTopics( thesaTopicList );
        console.debug( inspireTopics.length + " inspire topics found" );
        thesaTopicList = thesaTopicList.concat( inspireTopics );
        console.debug( "go on" );
        // Thesaurus Topics
        if (thesaTopicList != null) {
            for ( var i in thesaTopicList) {
                // Add checkbox to entry
                thesaTopicList[i].selection = 0;
            }
            UtilStore.updateWriteStore( "assistantDescriptorTable", thesaTopicList );
        }

        // Location Topics
        if (locationTopicList != null) {
            UtilList.addSNSLocationLabels( locationTopicList );
            for ( var i in locationTopicList) {
                // Add checkbox to entry
                locationTopicList[i].selection = 0;

                // Prepare bb for display in the table
                if (locationTopicList[i].boundingBox) {
                    locationTopicList[i].longitude1 = locationTopicList[i].boundingBox[0];
                    locationTopicList[i].latitude1 = locationTopicList[i].boundingBox[1];
                    locationTopicList[i].longitude2 = locationTopicList[i].boundingBox[2];
                    locationTopicList[i].latitude2 = locationTopicList[i].boundingBox[3];
                }
            }
            UtilStore.updateWriteStore( "assistantSpatialRefTable", locationTopicList );
        }
        console.debug( "event topics" );
        // Event Topics
        if (eventTopicList != null) {
            for ( var i in eventTopicList) {
                console.debug( "prepare: " + i );
                eventTopicList[i].selection = "<input type='radio' name='objectWiz_eventTopic' id='objectWiz_"+eventTopicList[i].topicId+"'>";

                if (null != eventTopicList[i].at) {
                    eventTopicList[i].date1 = eventTopicList[i].at;
                    eventTopicList[i].type = "am";

                } else if (null != eventTopicList[i].from && null != eventTopicList[i].to) {
                    eventTopicList[i].date1 = eventTopicList[i].from;
                    eventTopicList[i].date2 = eventTopicList[i].to;
                    eventTopicList[i].type = "von - bis";

                } else if (null != eventTopicList[i].from && null == eventTopicList[i].to) {
                    eventTopicList[i].date1 = eventTopicList[i].from;
                    eventTopicList[i].type = "seit";

                } else if (null == eventTopicList[i].from && null != eventTopicList[i].to) {
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
            dijit.byId( "assistantServiceType" ).set( "value", topicMap.dataServiceType );

        }
        
        // Events (creation, published, ...)
        if (eventsList != null) {
            UtilStore.updateWriteStore( "assistantTimeEventsTable", eventsList );
        }

        // CRS
        if (crsList != null) {
            UtilStore.updateWriteStore( "assistantCRSTable", crsList );
        }

        // Online Resources
        if (onlineResourcesList != null) {
            UtilStore.updateWriteStore( "assistantOnlineResourcesTable", onlineResourcesList );
        }

        // Resource Locators
        console.log( "resource locators: ", resourceLocatorsList );
        if (resourceLocatorsList != null) {
            UtilStore.updateWriteStore( "assistantResourceLocatorsTable", resourceLocatorsList );
        }

        // Keywords
        if (keywordsList != null) {
            UtilStore.updateWriteStore( "assistantKeywordsTable", 
                    UtilList.listToTableData( keywordsList, "label", true ) );
        }

        // Conformities
        if (conformitiesList != null) {
            UtilStore.updateWriteStore( "assistantConformityTable", conformitiesList );
        }

        if (crsUnknownList != null && crsUnknownList.length > 0) {
            var text = "<ul>";
            dojo.forEach( crsUnknownList, function(crs) {
                text += "<li>" + crs.name + "</li>";
            } );
            text += "</ul>";
            dialog.show( message.get( 'general.hint' ), dojo.string.substitute( 
                    message.get( 'dialog.wizard.getCap.crs.unknown' ), [ text ] ), dialog.INFO );
        }

        // Addresses
        if (addressesList != null) {
            // determine if the address already exists and will be linked or if it will be created
            dojo.forEach(addressesList, function(res) {
                if (res.uuid) {
                    res.action = '<fmt:message key="dialog.wizard.create.asLink" />';
                } else {
                    res.action = '<fmt:message key="dialog.wizard.create.asNew" />';
                }
            });
            UtilStore.updateWriteStore( "assistantAddressTable", addressesList );
        }
        
        // Coupled Resources
        if (topicMap.coupledResources != null) {
            dojo.forEach(topicMap.coupledResources, function(res) {
                if (res.uuid) {
                    res.action = '<fmt:message key="dialog.wizard.create.asLink" />';
                    res.actionId = 'LINK';
                } else {
                    res.action = '<fmt:message key="dialog.wizard.create.asNew" />';
                    res.actionId = 'CREATE';
                }
            });
            UtilStore.updateWriteStore( "assistantDatasetsTable", topicMap.coupledResources );
        }

        // only show input fields with content!
        scopeWizardResults.toggleContainers();
        
        console.debug( "finished update" );
    }

    scopeWizardResults.hideResults = function() {
        dojo.style( "resultContainer", "display", "none" );
        dojo.style( "resultButtonContainer", "display", "none" );
    }

    scopeWizardResults.addValuesToObject = function(/*boolean*/applyAll) {
        // close dialog in the beginning before other popups prevent this!
        this.closeThisDialog();

        // If selected, add description
        var desc = "";
        if (applyAll || dijit.byId( "assistantDescriptionCheckbox" ).checked) {
            desc += dijit.byId( "assistantDescription" ).getValue();
            desc += "\n\n";
        }
        if (applyAll || dijit.byId( "assistantHtmlContentCheckbox" ).checked) {
            desc += dijit.byId( "assistantHtmlContent" ).getValue();
        }
        dijit.byId( "generalDesc" ).setValue( dojo.trim( desc ) );

        // Title
        if (applyAll || dijit.byId( "assistantHtmlTitleCheckbox" ).checked) {
            dijit.byId( "objectName" ).setValue( dojo.trim( dijit.byId( "assistantHtmlTitle" ).getValue() ) );
        }

        // Fees
        if (applyAll || dijit.byId( "assistantFeesCheckbox" ).checked) {
            dijit.byId( "availabilityUseConstraints" ).setValue( dojo.trim( dijit.byId( "assistantFees" ).getValue() ) );
        }

        // Access Constraints
        var accessList = UtilGrid.getTableData( "assistantAccessTable" );
        var accessToAdd = [];
        dojo.forEach( accessList, function(ac) {
            if (applyAll || ac.selection == 1) {
                accessToAdd.push( {
                    title: ac.label
                } );
            }
        } );
        dijit.byId( "availabilityAccessConstraints" ).setData( accessToAdd );

        // Service Type
        if (applyAll || dijit.byId( "assistantServiceTypeCheckbox" ).checked) {
            dijit.byId( "ref3ServiceType" ).setValue( dijit.byId( "assistantServiceType" ).getValue() );
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
        //var spatialStore = dijit.byId("spatialRefAdminUnit").store;
        var locationList = UtilGrid.getTableData( "assistantSpatialRefTable" );
        for ( var i in locationList) {
            // If checkbox is selected
            if (applyAll || locationList[i].selection == 1) {
                var topic = locationList[i];
                var topicId = locationList[i].topicId;
                var targetGrid = "spatialRefAdminUnit";
                if ( !topicId ) targetGrid = "spatialRefLocation";
                
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
                            topic.type ? topic.name + ", " + topic.type : topic.name );
                    if (topic.boundingBox) {
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "longitude1",
                                topic.boundingBox[0] );
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "latitude1",
                                topic.boundingBox[1] );
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "longitude2",
                                topic.boundingBox[2] );
                        UtilGrid.updateTableDataRowAttr( targetGrid, storedTopicIdx, "latitude2",
                                topic.boundingBox[3] );
                    }
                    // Update elements that aren't displayed in the table
                    storedTopic.nativeKey = topic.nativeKey;
                    storedTopic.name = topic.name;
                    storedTopic.topicType = topic.type;
                    storedTopic.topicTypeId = topic.typeId;
                } else {
                    // The topic was not found in the result list. Create a new key and
                    // add the topic to the list
                    //var key = UtilStore.getNewKey(spatialStore);
                    if (topic.boundingBox) {
                        UtilGrid.addTableDataRow( targetGrid, {
                            topicId: topic.topicId,
                            label: topic.type ? topic.name + ", " + topic.type : topic.name,
                            name: topic.name,
                            longitude1: topic.boundingBox[0],
                            latitude1: topic.boundingBox[1],
                            longitude2: topic.boundingBox[2],
                            latitude2: topic.boundingBox[3],
                            nativeKey: topic.nativeKey,
                            topicType: topic.type,
                            topicTypeId: topic.typeId
                        } );
                    } else {
                        UtilGrid.addTableDataRow( targetGrid, {
                            topicId: topic.topicId,
                            label: topic.type ? topic.name + ", " + topic.type : topic.name,
                            name: topic.name,
                            nativeKey: topic.nativeKey,
                            topicType: topic.type,
                            topicTypeId: topic.typeId
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
            if (dojo.byId( "objectWiz_" + eventJS.topicId ).checked) {
                if (eventJS.type == "von - bis")
                    eventJS.type = "von";

                dijit.byId( "timeRefType" ).setValue( eventJS.type );

                if (eventJS.type == "bis") {
                    dijit.byId( "timeRefDate1" ).setValue( eventJS.to );

                } else if (eventJS.type == "am") {
                    dijit.byId( "timeRefDate1" ).setValue( eventJS.at );
                    dijit.byId( "timeRefDate2" ).setValue( eventJS.at );

                } else {
                    dijit.byId( "timeRefDate1" ).setValue( eventJS.from );
                    dijit.byId( "timeRefDate2" ).setValue( eventJS.to );
                }
            }
        }

        // add Events (created, published, ...)
        var datesList = UtilGrid.getTableData( "assistantTimeEventsTable" );
        UtilGrid.setTableData( "timeRefTable", datesList );

        // add Keywords
        var keywords = [];
        dojo.forEach( UtilGrid.getTableData( "assistantKeywordsTable" ), function(row) {
            if (applyAll || row.selection == 1) {
                keywords.push( row.label );
            }
        } );
        if (keywords.length > 0) {
            var defKeywords = igeEvents.addKeywords( keywords, {
                id: "getCapabilitiesWizard",
                _termListWidget: "thesaurusTerms"
            } );
        }

        // CRS
        var crsData = UtilGrid.getTableData( "assistantCRSTable" );
        var crsToAdd = [];
        dojo.forEach( crsData, function(crs) {
            if (applyAll || crs.selection == 1) {
                crsToAdd.push( {
                    title: crs.name
                } );
            }
        } );
        UtilStore.updateWriteStore( "ref1SpatialSystem", crsToAdd );

        // Conformities
        var conformitiesData = UtilGrid.getTableData( "assistantConformityTable" );
        var conformitiesToAdd = [];
        dojo.forEach( conformitiesData, function(conformity) {
            if (applyAll || conformity.selection == 1) {
                conformitiesToAdd.push( conformity );
            }
        } );
        UtilStore.updateWriteStore( "extraInfoConformityTable", conformitiesToAdd );

        // Addresses
        var defs = [];
        var addressData = UtilGrid.getTableData( "assistantAddressTable" );
        var addressesToAdd = [];
        // get localized syslist values
        var sysEmail = UtilSyslist.getSyslistEntryName( 4430, 3 ); // E-Mail
        var sysPhone = UtilSyslist.getSyslistEntryName( 4430, 1 ); // Phone
        var sysGermany = UtilSyslist.getSyslistEntryName( 6200, 276 ) // Germany
        dojo.forEach( addressData, function(address) {
            if (applyAll || address.selection == 1) {
                var def = new dojo.Deferred();
                defs.push( def )
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
                            UtilList.addAddressTitles( [ savedData ] );
                            UtilList.addAddressLinkLabels( [ savedData ] );
                            savedData.nameOfRelation = sysRelationName;
                            console.debug( savedData );
                            addressesToAdd.push( savedData ); //{uuid: data.uuid, name, givenName, writePermission: true})
                            def.callback();
                        } );
                    } );
        
                } else {
                    // the address already exists so just link to it!
                    // for the link we just need the uuid and the nameOfRelation; other attributes
                    // are needed for correctly showing data in table
                    def.callback();
                    address.nameOfRelation = sysRelationName;
                    address.givenName = address.firstname;
                    address.name = address.lastname;
                    address.addressClass = address.type;
                    UtilList.addIcons( [ address ] );
                    UtilList.addAddressTitles( [ address] );
                    UtilList.addAddressLinkLabels( [ address] );
                    addressesToAdd.push( address );
                }
            }
        } );

        // wait for all addresses being added and update address table then
        var defList = new dojo.DeferredList( defs );
        defList.then( function() {
            console.debug( "update address table now!" );
            UtilStore.updateWriteStore( "generalAddress", addressesToAdd );
            // reload sub tree of newly created addresses
            // WARNING: this removes focus of selected node and new node cannot be clicked again!?
            /*var freeAddresses = dijit.byId( "addressFreeRoot" );
            if ( freeAddresses ) {
                menuEventHandler.reloadSubTreeByNode( freeAddresses );
                UtilTree.selectNode( "dataTree", "newNode" );
            }*/
        } );

        // add versions and operations automatically of a capabilities document!
        if (scopeWizardResults.databean) {
            var serviceType = scopeWizardResults.databean.serviceType;
            
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
	                dijit.byId( "ref3Explanation" ).setValue( "WCS Service" );
	
	            }
	
	            // Versions
	            if (scopeWizardResults.databean.versions) {
	                UtilStore.updateWriteStore( "ref3ServiceVersion", UtilList
	                        .listToTableData( scopeWizardResults.databean.versions ) );
	            }
	
	            // Operations
	            // Prepare the operation table for display.
	            // Add table indices to the main obj and paramList
	            // Add table indices and convert to tableData: platform, addressList and dependencies
	            var operations = scopeWizardResults.databean.operations;
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
        dojo.forEach( links, function(link) {
            if (applyAll || link.selection == 1) {
                onlineResources.push( scopeWizardResults.createNewUrl( link.url, link.url ) );
            }
        } );

        // Resource Locators
        links = UtilGrid.getTableData( "assistantResourceLocatorsTable" );
        var resourceLocators = [];
        dojo.forEach( links, function(link) {
            if (applyAll || link.selection == 1) {
                resourceLocators.push( scopeWizardResults.createNewUrl( link.url, link.url, link.relationTypeName ) );
            }
        } );

        // Add the url to the link table if we created a new object with the wizard
        // not needed for the getCapabilities wizard!
        var urlData = [];
        if (scopeWizardResults.isWebsite) {
            var newUrl = scopeWizardResults.createNewWebUrl();
            urlData.push( newUrl );
        }

        var allLinks = onlineResources.concat( resourceLocators.concat( urlData ) );

        UtilList.addIcons( allLinks );
        UtilList.addUrlLinkLabels( allLinks );
        UtilStore.updateWriteStore( "linksTo", allLinks );
        
        
        // add references to datasets/layers or create new objects
        var datasets = UtilGrid.getTableData( "assistantDatasetsTable" );
        var coupledResources = [];
        var createDatasetsDeferreds = [];
        dojo.forEach( datasets, function( dataset ) {
            if (applyAll || dataset.selection == 1) {
                if (dataset.actionId == "CREATE") {
                    createDatasetsDeferreds.push( scopeWizardResults.createDataset( dataset ) );
                    
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
            dojo.forEach( resources, function(obj) {
                addCoupledResourceInfo( obj );
            } );
            return resources;
        };

        if ( createDatasetsDeferreds.length === 0 ) {
            console.log( "Adding all coupled resources as links: ", coupledResources );
            addCoupledResourcesInfo( coupledResources );
            UtilStore.updateWriteStore( "ref3BaseDataLink", coupledResources );
            setTimeout(igeEvents.refreshTabContainers, 500);
            
        } else {
            UtilDWR.enterLoadingState();
            UtilUI.initBlockerDivInfo( "layers", createDatasetsDeferreds.length, message.get("general.add.layers"));
            
            var creates = new dojo.DeferredList( createDatasetsDeferreds );
	        creates.then( function(resourcesDefs) {
	            console.log("all layers created!");
	            addCoupledResourcesInfo( coupledResources );
	            dojo.forEach( resourcesDefs, function(res) {
	                // add additional information to show reference in table
	                var cr = addCoupledResourceInfo( res[1] );
	                coupledResources.push( cr );
	            } );
	            console.log( "Adding coupled resources: ", coupledResources );
	            UtilStore.updateWriteStore( "ref3BaseDataLink", coupledResources );
	            var parentId = dojo.byId("newNode").parentNode.parentNode.id;
	            var newNode = itemToJS(dijit.byId("dataTree").model.store, dijit.byId("newNode").item);
	            menuEventHandler.reloadSubTreeByNode( dijit.byId( parentId ) );
	            setTimeout(function() {
	                igeEvents.refreshTabContainers();
	                // add new node again, which was removed after tree has been reloaded
	                var tree = dijit.byId("dataTree");
                    var treeItem = tree.model.store.newItem(newNode, {
                        parent: dijit.byId( currentUdk.parentUuid ).item,
                        attribute: "children"
                    });
                    UtilTree.selectNode("dataTree", treeItem.id[0]);
                    udkDataProxy.setDirtyFlagNow();
                    if (defKeywords) {
                        defKeywords.then( function() {
		                    UtilDWR.exitLoadingState();
                        });
                    } else {
		                UtilDWR.exitLoadingState();
                    }
	            }, 500);
	        } );
        }
    }
    
    scopeWizardResults.prepareTopics = function( keywords, topics ) {
        var conTopics = [];
        dojo.forEach( topics, function( topic, i ) {
            if (topic && topic.length != 0) {
                conTopics.push( topic[0] );                
            } else {
                conTopics.push( { label: keywords[i], title: keywords[i], source: "FREE", sourceString: "FREE" } );
            }
        });
        return conTopics;
    };
    
    scopeWizardResults.createDataset = function(data) {
        var def = new dojo.Deferred();

        // call this function as long as object has not been created due to USER_HAS_RUNNING_JOBS errors!
        var createFunction = function(def) {
	        ObjectService.createNewNode( currentUdk.parentUuid, function(objNode) {
	            objNode.ref1ObjectIdentifier = data.ref1ObjectIdentifier;
	            objNode.objectClass = 1;
	            objNode.objectName = data.title;
	            objNode.generalDescription = data.generalDescription;
	            objNode.ref1SpatialSystemTable = data.ref1SpatialSystemTable;
	            
	            objNode.spatialRefLocationTable = data.spatialRefLocationTable;
	            console.log("data of layer: ", data);
	            var keywords = UtilList.tableDataToList( data.thesaurusTermsTable, "title" );
	            igeEvents.analyzeKeywords(keywords).then( function(topics) {
	                console.log( "Found topics for layer:", topics );
	                objNode.thesaurusTermsTable = scopeWizardResults.prepareTopics( keywords, topics );;
	                console.log( "converted topics for layer:", objNode.thesaurusTermsTable );
	                // save the object as WORKING copy to create a UUID
	                var response = ObjectService.saveNodeData( objNode, true, false, {
	                    callback: function(bean) {
		                    
		                    // update tree node
		                    // NO -> is done by reload subtree later!
		                    /*var newNode = _createNewNode( bean );
		                    console.log("new node is: ", newNode);
		                    newNode.title = data.title;
		                    newNode.id = bean.uuid;
		                    newNode.isFolder = false;
		                    var tree = dijit.byId("dataTree");
		                    tree.model.store.newItem(newNode, {
		                        parent: dijit.byId( currentUdk.parentUuid ).item,
		                        attribute: "children"
		                    });*/
	                        UtilUI.updateBlockerDivInfo( "layers" );
		                    def.callback( bean );
		                },
	                    errorHandler:function(message) { console.log("errorHandler::"+message); UtilUI.updateBlockerDivInfo( "layers" ); displayErrorMessage(err);},
	                    exceptionHandler:function(errorString, exception) {
	                        // try to create the dataset a second later if backend was busy
	                        if (errorString.indexOf( "[USER_HAS_RUNNING_JOBS]" ) != -1) {
	                            console.log( "[USER_HAS_RUNNING_JOBS] ... try again in 1s" );
	                            setTimeout( function() { createFunction( def ); }, 1000);
	                        } else {
	                            console.log( "exceptionHandler::"+errorString );
	                            displayErrorMessage(err);
	                        }
	                    }
	                });
	            } );
	        } );
        };
        // initial call
        createFunction(def);

        return def;
    }
        
    scopeWizardResults.createNewUrl = function(uri, title, relName) {
        var newUrl = {};

        //newUrl.Id = 0;
        newUrl.relationType = -1;
        newUrl.relationTypeName = relName ? relName : "Informationen im Internet";
        var urlName = dojo.trim( dijit.byId( "assistantHtmlTitle" ).getValue() );
        newUrl.name = title ? title : urlName.length != 0 ? urlName : "Internet-Verweis";
        newUrl.url = dojo.trim( uri );
        newUrl.urlType = "1"; // Internet
        newUrl.description = "";

        return newUrl;
    }

    scopeWizardResults.createNewWebUrl = function() {
        var newUrl = {};

        //newUrl.Id = 0;
        newUrl.relationType = 9999;
        newUrl.relationTypeName = "unspezifischer Verweis";
        var urlName = dojo.trim( dijit.byId( "assistantHtmlTitle" ).getValue() );
        newUrl.name = urlName.length != 0 ? urlName : "Internet-Verweis";
        newUrl.url = dojo.trim( dijit.byId( "assistantURL" ).getValue() );
        newUrl.urlType = "";
        newUrl.description = "";

        return newUrl;
    }

    scopeWizardResults.closeThisDialog = function() {
        console.log( "hiding wizard:", thisDialog );
        thisDialog.hide();
    }
</script>
</head>
    <body>

        <div id="resultContainer" class="inputContainer" style="/* margin-top: 115px; margin-bottom: 20px; overflow: auto;*/">
            <span class="label"><h2><fmt:message key="dialog.wizard.create.result" /></h2></span>
            
            <span class="outer">
                <div>
                    <span id="assistantTitleContainer">
                        <div class="checkboxContainer option">
                            <span class="input" style="margin-bottom: 5px;"><input type="checkbox" name="assistantHtmlTitleCheckbox" id="assistantHtmlTitleCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8066)"><fmt:message key="dialog.wizard.create.addTitle" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input type="text" id="assistantHtmlTitle" dojoType="dijit.form.ValidationTextBox" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>

            <span class="outer">
                <div>
                    <span id="assistantDescriptionContainer">
                        <div class="checkboxContainer option">
                            <span class="input"><input type="checkbox" name="assistantDescriptionCheckbox" id="assistantDescriptionCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8067)"><fmt:message key="dialog.wizard.create.addDescription" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input type="text" mode="textarea" id="assistantDescription" dojoType="dijit.form.SimpleTextarea" rows="5" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>

            <span id="assistantHtmlContentContainer" class="outer">
                <div>
                    <span>
                        <div class="checkboxContainer option">
                            <span class="input"><input type="checkbox" name="assistantHtmlContentCheckbox" id="assistantHtmlContentCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 8068)"><fmt:message key="dialog.wizard.create.addContent" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input"><input type="text" mode="textarea" id="assistantHtmlContent" dojoType="dijit.form.SimpleTextarea" rows="5" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div>
                    <span id="assistantFeesContainer">
                        <div class="checkboxContainer">
                            <span class="input"><input type="checkbox" name="assistantFeesCheckbox" id="assistantFeesCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 10200)"><fmt:message key="dialog.wizard.create.addFees" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input type="text" mode="textarea" id="assistantFees" dojoType="dijit.form.SimpleTextarea" rows="5" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div id="assistantAccessConstraintsContainer">
                    <span class="label"><label for="assistantAccessTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10201)"><fmt:message key="dialog.wizard.create.accessConstraints" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                         <div id="assistantAccessTable" interactive="true" autoEdit="true" class="hideTableHeader"></div> 
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive input allSelector"><input type="checkbox" name="assistantAccessTableCheckbox" id="assistantAccessTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllAccess" /></label>
                    </div>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div>
                    <span id="assistantServiceTypeContainer">
                        <div class="checkboxContainer">
                            <span class="input"><input type="checkbox" name="assistantServiceTypeCheckbox" id="assistantServiceTypeCheckbox" dojoType="dijit.form.CheckBox" /><label onclick="javascript:dialog.showContextHelp(arguments[0], 10202)"><fmt:message key="dialog.wizard.create.serviceType" /></label></span>
                        </div>
                        <div class="inputContainer">
                            <span class="input" style="margin-bottom: 5px;"><input id="assistantServiceType" style="width:100%;" /></span> 
                        </div>
                    </span>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantDescriptorTable" onclick="javascript:dialog.showContextHelp(arguments[0], 8069)"><fmt:message key="dialog.wizard.create.descriptors" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantDescriptorTable" interactive="true" autoEdit="true" class="hideTableHeader"></div>
                    </div>

                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantDescriptorTableCheckbox" id="assistantDescriptorTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllDescriptors" /></label></span>
                    </div>
                </div>
            </span>

            <span class="outer">
                <div>
                    <span class="label"><label for="assistantSpatialRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 8071)"><fmt:message key="dialog.wizard.create.spatial" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantSpatialRefTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantSpatialRefTableCheckbox" id="assistantSpatialRefTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllSpatialRefs" /></label></span>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantOnlineResourcesTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10203)"><fmt:message key="dialog.wizard.create.onlineResources" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantOnlineResourcesTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantOnlineResourcesTableCheckbox" id="assistantOnlineResourcesTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllOnlineResources" /></label></span>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div id="assistantResourceLocatorsContainer">
                    <span class="label"><label for="assistantResourceLocatorsTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10204)"><fmt:message key="dialog.wizard.create.resourceLocators" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantResourceLocatorsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantResourceLocatorsTableCheckbox" id="assistantResourceLocatorsTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllResourceLocators" /></label></span>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantCRSTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10205)"><fmt:message key="dialog.wizard.create.crs" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantCRSTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantCRSTableCheckbox" id="assistantCRSTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllCrs" /></label></span>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantKeywordsTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10206)"><fmt:message key="dialog.wizard.create.keywords" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantKeywordsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantKeywordsTableCheckbox" id="assistantKeywordsTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllKeywords" /></label></span>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div id="assistantConformitiesContainer">
                    <span class="label"><label for="assistantConformityTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10207)"><fmt:message key="dialog.wizard.create.conformity" /></label></span>
                    <div class="tableContainer" style="margin-bottom: 5px;">
                        <div id="assistantConformityTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantConformityTableCheckbox" id="assistantConformityTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllConformities" /></label></span>
                    </div>
                </div>
            </span>
            
            <span class="outer halfWidth">
                <div>
                    <span class="label"><label for="assistantTimeRefTable" onclick="javascript:dialog.showContextHelp(arguments[0], 10208)"><fmt:message key="dialog.wizard.create.timeSpan" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantTimeRefTable"></div>
                    </div>
                </div>
            </span>
            <span class="outer halfWidth">
                <div>
                    <span class="label"><label for="assistantTimeEventsTable" onclick="javascript:dialog.showContextHelp(arguments[0], 8073)"><fmt:message key="dialog.wizard.create.timeEvents" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantTimeEventsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantTimeEventsTableCheckbox" id="assistantTimeEventsTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllTimeEvents" /></label></span>
                    </div>
                </div>
            </span>
            <span class="outer">
                <div>
                    <span class="label"><label for="assistantAddAddress" onclick="javascript:dialog.showContextHelp(arguments[0], 10209)"><fmt:message key="dialog.wizard.create.address" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantAddressTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantAddressTableCheckbox" id="assistantAddressTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllAddresses" /></label></span>
                    </div>
                </div>
            </span>
            
            <span class="outer">
                <div id="assistantDatasetsContainer">
                    <span class="label"><label for="assistantAddDatasets" onclick="javascript:dialog.showContextHelp(arguments[0], 10210)"><fmt:message key="dialog.wizard.create.datasets" /></label></span>
                    <div class="tableContainer">
                        <div id="assistantDatasetsTable" interactive="true" autoEdit="true"></div>
                    </div>
                    <div class="checkboxContainer">
                        <label class="inActive"><span class="input allSelector"><input type="checkbox" name="assistantDatasetsTableCheckbox" id="assistantDatasetsTableCheckbox" dojoType="dijit.form.CheckBox" /><fmt:message key="dialog.wizard.create.selectAllDatasets" /></label></span>
                    </div>
                </div>
            </span>
        </div> <!-- RESULT CONTAINER END -->

        <div id="resultButtonContainer" class="inputContainer" style="/*position: absolute; bottom: 4px; width: 100%; right: 0px; background-color: white; z-index: 1000;*/">
            <span class="button" style="height:20px !important;">
                <span style="float:right;">
                    <button id="createObjWizardAcceptButton" dojoType="dijit.form.Button" onClick="javascript:scopeWizardResults.addValuesToObject();"><fmt:message key="dialog.wizard.create.apply" /></button>
                </span>
                <!-- <span style="float:right;">
                    <button id="createObjWizardAcceptAllButton" dojoType="dijit.form.Button" onClick="javascript:scopeWizardResults.addValuesToObject(true);"><fmt:message key="dialog.wizard.create.applyAll" /></button>
                </span> -->
                <span style="float:left;">
                    <button id="createObjWizardCancelButton" dojoType="dijit.form.Button" onClick="javascript:scopeWizardResults.closeThisDialog();"><fmt:message key="dialog.wizard.create.cancel" /></button>
                </span>
            </span>
        </div>

        <!-- LEFT HAND SIDE CONTENT END -->
    </body>
</html>