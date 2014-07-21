define([
    "dojo/_base/declare",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/on",
    "dojo/window",
    "dojo/query",
    "dojo/string",
    "dojo/dom",
    "dojo/dom-class",
    "dojo/dom-style",
    "dojo/dom-construct",
    "dojo/dom-geometry",
    "dijit/registry",
    "dijit/Tooltip",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/utils/Grid",
    "ingrid/utils/Dom",
    "ingrid/utils/Syslist"
], function(declare, array, lang, on, wnd, query, string, dom, domClass, style, construct, geometry, registry, Tooltip, message, dialog, UtilGrid, UtilDOM, UtilSyslist) {
    return declare(null, {
        // This variable serves as a cache to store whether ui elements
        // have been activated or not
        _uiElementsActive: true,

        global: this,

        getDescriptionForGuiId: function(guiId) {
            return message.get( "ui." + guiId + ".description" );
        },

        // Check if the nodeId is for a container node (objectRoot,
        // addressRoot, addressFreeRoot)
        isContainerNodeId: function(nodeId) {
            return nodeId == "objectRoot" || nodeId == "addressRoot" || nodeId == "addressFreeRoot";
        },

        // Check if the nodeId belongs to a newly created node without
        // an id
        isNewNodeId: function(nodeId) {
            return nodeId == "newNode";
        },

        // click info has to be resetted after using it (for datagrids)
        resetSelectedData: function() {
            this.global.selGrid = null;
            this.global.selRowNode = null;
        },

        setMandatory: function(/* html node to process */containerNode) {
            if (containerNode) {
                this.removeVisibilityClasses( containerNode );
                domClass.add( containerNode, "required" );
                domClass.add( containerNode, "show" );
                // make sure tab containers and alike are displayed
                // correctly!
                // igeEvents.refreshTabContainers();
            }
        },
        setOptional: function(/* html node to process */containerNode) {
            if (containerNode) {
                this.removeVisibilityClasses( containerNode );
                domClass.add( containerNode, "optional" );
            }
        },
        setShow: function(/* html node to process */containerNode) {
            if (containerNode) {
                this.removeVisibilityClasses( containerNode );
                domClass.add( containerNode, "show" );
            }
        },
        setHide: function(/* html node to process */containerNode) {
            if (containerNode) {
                this.removeVisibilityClasses( containerNode );
                domClass.add( containerNode, "hide" );
            }
        },
        removeVisibilityClasses: function(/* html node to process */containerNode) {
            if (containerNode) {
                domClass.remove( containerNode, "required" );
                domClass.remove( containerNode, "optional" );
                domClass.remove( containerNode, "show" );
                domClass.remove( containerNode, "hide" );
            }
        },

        getSectionElement: function(node) {
            return UtilDOM.findParentNodeWithClass( node, "rubric" );
        },

        toggleFunctionalLink: function(linkTab, event, object, selectedTabWidget) {
            var selectedTab = selectedTabWidget.id;
            if (selectedTab == linkTab)
                style.set( linkTab + "Header", "display", "block" );
            else
                style.set( linkTab + "Header", "display", "none" );

        },

        setWidgetStateError: function(widget, /* Boolean */error) {
            // first set the widget correctly for false validation
            if (error) {
                widget.required = true;
                // then this
                widget.state = "Error";
                widget._setStateClass();
                // or
                // widget._maskValidSubsetError = false;
                // widget.validate(false);
            } else {
                // to reset use the following
                widget.required = false;
                widget.validate( false );
            }
        },

        // get current mode of section element, to be passed to
        // toggleFields for refresh !
        getCurrentExpandModeOfSectionElement: function(sectionElement) {
            if (domClass.contains( sectionElement, "expanded" )) {
                return "showAll";
            }
            return "showRequired";
        },

        disableHtmlLink: function(elementId) {
            var element = dom.byId( elementId );

            if (element.onClick) {
                element._disabledOnClick = element.onClick;
                element.onClick = null;

            } else if (element.onclick) {
                element._disabledOnClick = element.onclick;
                element.onclick = null;
            }
            // add style for disabled look
            domClass.add( element.parentNode, "disabled" );
        },

        enableHtmlLink: function(elementId) {
            var element = dom.byId( elementId );

            if (element._disabledOnClick) {
                element.onclick = element._disabledOnClick;
                element._disabledOnClick = null;
            }
            // add style for disabled look
            domClass.remove( element.parentNode, "disabled" );
        },

        setVisibleBlockDiv: function(visible) {
            var div = "loadBlockDiv";
            if (div !== undefined)
                style.set( div, "display", visible ? "block" : "none" );
        },

        updateBlockerDivInfo: function(id) {
            var waitInfo = dom.byId( "waitInfo" );

            if (!id || !waitInfo.data[id])
                return;
            waitInfo.data[id].current++;
            dom.byId( "waitInfo_" + id ).innerHTML = string.substitute( waitInfo.data[id].text,
                [ waitInfo.data[id].current, waitInfo.data[id].max ]
            );

            // if maximum is reached, remove html div and data element
            if (waitInfo.data[id].current == waitInfo.data[id].max) {
                construct.destroy( "waitInfo_" + id );
                delete waitInfo.data[id];
            }

            // if no elements are present, hide info
            var keys = [];
            for ( var k in waitInfo.data)
                keys.push( k );
            if (keys.length === 0) {
                style.set( "waitInfo", "display", "none" );
                this.exitLoadingState();
            } else {
                style.set( "waitInfo", "display", "table" );
                this.enterLoadingState();
            }
        },

        initBlockerDivInfo: function(/* STRING */id, /* INTEGER */max, /* STRING */text, /* INTEGER */current) {
            var waitInfo = dom.byId( "waitInfo" );
            var curr = current ? current : 0;
            if (!waitInfo.data)
                waitInfo.data = {};
            waitInfo.data[id] = {
                max: max,
                current: curr,
                text: text
            };
            style.set( "waitInfo", "display", "table" );
            var newText = string.substitute( waitInfo.data[id].text, [ waitInfo.data[id].current,
                    waitInfo.data[id].max ] );
            // remove div if the id already exists
            construct.destroy( "waitInfo_" + id );
            construct.create( "div", {
                id: "waitInfo_" + id,
                innerHTML: newText
            }, "waitInfo" );
        },

        showNextError: function(id, message) {
            var mightBeHidden = dojo.query( ".rubric:not(.expanded) #" + id );
            if (mightBeHidden.length > 0) {
                domClass.add( this.getSectionElement( mightBeHidden[0] ), "expanded" );
            }
            var pos = geometry.position( id );
            // leave some space after the element to scroll to so that
            // we can show tooltip
            pos.h += 50;
            wnd.scrollIntoView( id, pos );
            if (message)
                this.showToolTip( id, message );
            else {
                message = registry.byId( id ).invalidMessage;
                if (message && message !== "$_unset_$") {
                    this.showToolTip( id, message );
                } else {
                    this.showToolTip( id, "Error" );
                }
            }

        },

        changeLabel: function(uiElementId, text) {
            var label = dojo.query( "#" + uiElementId + " label" )[0];
            if (label) {
                label.innerHTML = text + '<span class="requiredSign">*</span>';
            } else {
                console.debug( "ERROR: Label in container '" + uiElementId + "' could not be found!" );
            }
        },

        enableElement: function(element) {
            // convert id to dijit if necessary
            if (!(element instanceof Object)) {
                element = registry.byId( element );
            }
            element.set( "disabled", false );
        },

        disableElement: function(element) {
            // convert id to dijit if necessary
            if (!(element instanceof Object)) {
                element = registry.byId( element );
            }
            element.set( "disabled", true );
        },

        setComboBySyslistValue: function(boxId, entryId) {
            var box = registry.byId( boxId );
            box.store.fetch( {
                onComplete: function(data) {
                    dojo.some( data, function(item) {
                        if (item[1] == entryId) {
                            box.set( 'value', item[0] );
                            return true;
                        }
                    } );
                }
            } );
        },

        // function to add a new entry to the conformity table if not
        // already there
        updateEntryToConformityTable: function(entryId, deleteEntry) {
            var entry = null;
            if (deleteEntry) {
                // remove all automatically added entries
                var itemIndexes = [];
                entry = UtilSyslist.getSyslistEntryName( 6005, entryId );
                array.forEach( UtilGrid.getTableData( "extraInfoConformityTable" ), function(row, index) {
                    if (row.specification == entry)
                        itemIndexes.push( index );
                } );
                UtilGrid.removeTableDataRow( "extraInfoConformityTable", itemIndexes );

            } else {
                // get name of codelist entry for entry-id "2"
                entry = UtilSyslist.getSyslistEntryName( 6005, entryId );

                // check if entry already exists in table
                var exists = dojo.some( UtilGrid.getTableData( "extraInfoConformityTable" ), function(row) {
                    return row.specification === entry;
                } );

                // add entry to table if it doesn't already exist
                if (!exists)
                    UtilGrid.addTableDataRow( "extraInfoConformityTable", {
                        specification: entry,
                        level: 1 // konform
                    } );

            }
        },

        unmarkGridCells: function(gridId) {
            var errorCells = query("#" + gridId + " .slick-cell.importantBackground");
            array.forEach(errorCells, function(cell) {
                domClass.remove(cell, "importantBackground");
            });
        },

        /**
         * Marks a cell as error or resets it.
         * 
         * @param  {String} type   [when "Error" then the cell be marked red, otherwise normal]
         * @param  {String} gridId [the grid id to mark the cell]
         * @param  {int}    row    [the row number]
         * @param  {int[]}  cells  [the column numbers, e.g. [1,3,4]]
         */
        markCells: function(type, gridId, row, cells) {
            UtilGrid.getTable(gridId).scrollRowIntoView(row);
            array.forEach(cells, function(cell) {
                var cellDom = query("#" + gridId + " .slick-row[row$=" + row + "] .c" + cell)[0];
                if (type == "ERROR")
                    domClass.add(cellDom, "importantBackground");
                else
                    domClass.remove(cellDom, "importantBackground");
            });
        },

        toggleInfo: function(element) {
            var toggleContent = document.getElementById( element + "Content" );
            var arrow = document.getElementById( element ).getElementsByTagName( "img" )[1];
            if (toggleContent.style.display == "block" || toggleContent.style.display == "") {
                toggleContent.style.display = "none";
                arrow.src = "img/ic_info_expand.gif";
            } else {
                toggleContent.style.display = "block";
                arrow.src = "img/ic_info_deflate.gif";
            }
        },
        
        /*
         * Toggle a table display
         */
        switchTableDisplay: function(onTableId, offTableId, /* boolean */isOn) {
            if (isOn) {
                style.set( offTableId, "display", "none");
                style.set( onTableId, "display", "block");
                registry.byId( onTableId ).invalidate();
            } else {
                style.set( offTableId, "display", "block");
                style.set( onTableId, "display", "none");
                registry.byId( offTableId ).invalidate();
            }
        },
        
        enterLoadingState: function(/*optional*/otherLoadingDiv) {
            style.set(otherLoadingDiv ? otherLoadingDiv : "loadingZone", "visibility", "visible");
            style.set("blockInputDiv", "visibility", "visible");

            //this.setVisibleBlockDiv(true);
            style.set( "loadBlockDiv", "display", "block" );
            //UtilDWR.onEnterLoadingState();
        },

        // dwr postHook Function that has to be called after the dwr call returned 
        exitLoadingState: function(/*optional*/otherLoadingDiv) {
            style.set(otherLoadingDiv ? otherLoadingDiv : "loadingZone", "visibility", "hidden");
            style.set("blockInputDiv", "visibility", "hidden");
            
            //this.setVisibleBlockDiv(false);
            style.set( "loadBlockDiv", "display", "none" );
            //UtilDWR.onExitLoadingState();
        },

        showToolTip: function(gridId, msg) {
            Tooltip.show(msg, dom.byId(gridId), ["below"], false);
            setTimeout(function() {
                var eventWndScroll;
                var eventWndClick = on(dom.byId("contentContainer"), "Click", function() {
                    Tooltip.hide(dom.byId(gridId));
                    eventWndClick.remove();
                    eventWndScroll.remove();
                });
                eventWndScroll = on(dom.byId("contentContainer"), (!dojo.isMozilla ? "mousewheel" : "DOMMouseScroll"), function() {
                    Tooltip.hide(dom.byId(gridId));
                    eventWndScroll.remove();
                    eventWndClick.remove();
                });
                setTimeout(function() {
                    Tooltip.hide(dom.byId(gridId));
                    eventWndScroll.remove();
                    eventWndClick.remove();
                }, 5000);
            }, 500);
        }

    } )();
} );