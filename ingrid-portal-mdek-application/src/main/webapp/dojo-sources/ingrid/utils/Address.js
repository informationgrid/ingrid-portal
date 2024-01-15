/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */

define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom",
    "dojo/_base/array",
    "dojo/aspect",
    "dijit/registry",
    "ingrid/utils/List",
    "ingrid/utils/Store",
    "ingrid/utils/UI",
    "ingrid/utils/PageNavigation",
    "ingrid/message"
], function(declare, lang, dom, array, aspect, registry, UtilList, UtilStore, UtilUI, navigation, message){
        return declare(null, {
        
            objectAddressRefPageNav: null,
            
            // Get the address class for an address type ('institution', 'person', etc.)
            // If no parameter is specified, the class of the currently selected address is returned
            getAddressClass : function(addressType /*optional*/) {
                if (typeof(addressType) == "undefined") {
                    addressType = registry.byId("addressType").get( "value" );
                }

                if (addressType == message.get('address.type.institution'))
                    return 0;
                else if (addressType == message.get('address.type.unit'))
                    return 1;
                else if (addressType == message.get('address.type.person'))
                    return 2;
                else if (addressType == message.get('address.type.custom'))
                    return 3;
                else if (addressType == message.get('address.type.folder')) // folder
                    return 1000;
                else {
                    console.error("Error in getAddressClass! Invalid addressType: "+addressType);
                    return -1;
                }
            },

            // Get the address type for an address class (0-3)
            getAddressType : function(addressClass) {
                if (addressClass == 0)
                    return message.get('address.type.institution');
                else if (addressClass == 1)
                    return message.get('address.type.unit');
                else if (addressClass == 2)
                    return message.get('address.type.person');
                else if (addressClass == 3)
                    return message.get('address.type.custom');
                else if (addressClass == 1000) // folder
                    return message.get('address.type.folder');
                else {
                    console.debug("Error in getAddressType! Invalid addressClass: "+addressClass);
                    return "";
                }
            },
            
            // Iterates over an array of addresses, extracts their titles and adds them as properties 'title'
            // See UtilAddress.createAddressTitle for more info
            addAddressTitles : function(list) {
                for (var i = 0; i < list.length; ++i) {
                    list[i].title = this.createAddressTitle(list[i]);
                }
                return list;
            },

            // Initialize the object->address reference table with a given array of links 'linkList'. The number of references is specified with
            // the parameter 'numReferences'.
            // Sets the label and link to their initial values. Further links are loaded from the backend via navObjectAddressReferences
            initObjectAddressReferenceTable : function(linkList, numReferences) {
                var tableLabel = dom.byId("associatedObjNameLabel");
                var tableLink = dom.byId("associatedObjNameLink");

                // Add table specific data to the list
                UtilList.addObjectLinkLabels(linkList);  
                UtilList.addIcons(linkList);
                
                if (this.objectAddressRefPageNav) {
                    this.objectAddressRefPageNav.reset();   

                } else {
                    this.objectAddressRefPageNav = new navigation.PageNavigation({ resultsPerPage: 20, infoSpan:dom.byId("associatedObjNameInfo"), pagingSpan:dom.byId("associatedObjNamePaging") });
                    aspect.after(this.objectAddressRefPageNav, "onPageSelected", lang.hitch(this, this.navObjectAddressReferences));
                }

                this.objectAddressRefPageNav.setTotalNumHits(numReferences);
                UtilStore.updateWriteStore("associatedObjName", linkList);
                this.objectAddressRefPageNav.updateDomNodes();
            },

            navObjectAddressReferences : function() {
                var curPos = this.objectAddressRefPageNav.getStartHit();
                var totalNumHits = this.objectAddressRefPageNav.totalNumHits;
                var self = this;

                // TODO Do we need to get the uuid from somewhere else?
                AddressService.fetchAddressObjectReferences(currentUdk.uuid, curPos, 20, {
                        //preHook: UtilUI.enterLoadingState,
                        //postHook: UtilUI.exitLoadingState,
                        callback: function(adr){
                            var unpubLinkTable = adr.linksFromObjectTable;
                            var pubLinkTable = adr.linksFromPublishedObjectTable;
                            array.forEach(pubLinkTable, function(link) { link.pubOnly = true; } );
                            var linkTable = pubLinkTable.concat(unpubLinkTable);

                            UtilList.addTableIndices(linkTable);
                            UtilList.addObjectLinkLabels(linkTable);  
                            UtilList.addIcons(linkTable);

                            UtilGrid.setTableData("associatedObjName", linkTable);
                            self.objectAddressRefPageNav.updateDomNodes();
                        },
                        errorHandler:function(message) {
                            UtilUI.exitLoadingState();
                            console.error("Error: " + message);
                        }
                });
            },

            // "PRIVATE" utility function for getting plain title !
            _getAddressTitle : function(adr) {
                var title = "";
                switch (adr.addressClass) {
                    case 0: // Institution
                        title = adr.organisation;
                        break;
                    case 1: // Unit
                        title = adr.organisation;
                        break;
                    case 2: // Person
                        if (adr.name) title += adr.name;
                        if (adr.givenName) title += ", "+adr.givenName;
                        break;
                    case 3: // Freie Adresse
                        if (adr.name) title += adr.name;
                        if (adr.givenName) title += ", "+adr.givenName;
                        if (adr.organisation) title += " ("+adr.organisation+")";
                        break;
                    case 100: // HIDDEN IGE USER ADDRESS
                        if (adr.name) title += adr.name;
                        if (adr.givenName) title += ", "+adr.givenName;
                        if (title.length == 0) {
                            title = adr.organisation;
                        }
                        break;
                    case 1000: // FOLDER
                        title = adr.name;
                        break;
                    default:
                        break;
                }
                
                return title;
            },

            // Checks if a title can be constructed for the given adr
            hasValidTitle : function(adr) {
                var title = this._getAddressTitle(adr);
                if (title == null || title == "")
                    return false;
                else
                    return true;
            },

            // Builds an address title from a given MdekAddressBean.
            // example arguments:
            //   adr: {addressClass: 0, organisation: "testOrga" }
//                  returns: "testOrga"
            //   adr: {addressClass: 1, organisation: "testOrga" }
//                  returns: "testOrga"
            //   adr: {addressClass: 2, name: "Name", givenName: "Vorname" }
//                  returns: "Name, Vorname"
            //   adr: {addressClass: 3, name: "Name", givenName: "Vorname", organisation: "testOrga" }
//                  returns: "Name, Vorname (testOrga)"
            createAddressTitle : function(adr) {
                var title = this._getAddressTitle(adr);
                if (title == null || title == "")
                    return message.get("tree.newAddressName");
                else
                    return lang.trim(title);
                    //return dojo.string.escape("html", dojo.string.trim(title));
                    // FIXME: escape function doesn't exist anymore
            },

            // Returns first found value for the passed array of mediums (pass medium in de and en if from syslist, e.g. ["Telefon", "telephone"] !)
            getAddressCommunicationValue : function(addr, mediumList) {
                if (addr.communication) {
                    for (var i=0; i<addr.communication.length; i++) {
                        var commMedium = addr.communication[i].medium;
                        var commValue = addr.communication[i].value;
                        for (var j=0; j<mediumList.length; j++) {
                            if (commMedium == mediumList[j] && commValue) {
                                return commValue;
                            }
                        }
                    }
                }
                return null;
            },

            // returns a 'linkLabel' depending on the 'uuid' and 'title' of the address.
            // linkLabel is a html href to directly jump to a given address in the main tree
            createAddressLinkLabel : function(adrUuid, adrTitle) {
                return "<a href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\""+adrUuid+"\", \"A\");'"+
                    "title='"+adrTitle+"'>"+adrTitle+"</a>";
            },

            determineInstitution : function(nodeData) {
                var institution = "";
                for (var i = nodeData.parentInstitutions.length-1; i >= 0; --i) {
                    if (nodeData.parentInstitutions[i].addressClass == 0) {
                        // Only display the first institution we encounter and break
                        institution = nodeData.parentInstitutions[i].organisation+"\n"+institution;
                        break;

                    } else if (nodeData.parentInstitutions[i].addressClass == 1) {
                        institution = "\t"+nodeData.parentInstitutions[i].organisation+"\n"+institution;
                    }
                }
                return lang.trim(institution);
            }
    })();
});
