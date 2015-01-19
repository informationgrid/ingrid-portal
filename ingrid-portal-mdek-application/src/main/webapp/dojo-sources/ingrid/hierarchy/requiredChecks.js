/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Functions for checking the validity of entered values.
 */

define(["dojo/_base/declare",
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/query",
        "dojo/topic",
        "dojo/dom",
        "dojo/dom-class",
        "dojo/dom-style",
        "dojo/dom-construct",
        "dijit/registry",
        "ingrid/message",
        "ingrid/utils/Grid"],
        function(declare, array, lang, query, topic, dom, domClass, style, domConstruct, registry, message, UtilGrid) {
    return declare(null, {

        resetRequiredFields: function() {
            // get all labels within object- and address form and remove class, which makes them red
            query(".important", "hierarchyContent").removeClass("important");
            query(".errorInfoBlock", "hierarchyContent").forEach(function(item) { item.remove(); });
            style.set(registry.byId("bShowNextError").domNode, "visibility", "hidden");
        },

        errorInfoField: domConstruct.toDom("<div class='errorInfoBlock'><div>CONTENT</div></div>"),

        setErrorLabel: function(id, message){
            if (array.indexOf(["objectName", "objectClass", "objectOwner"], id) != -1) {
                domClass.add(id + "Label", "important");
                return;
            }

            var domWidget = dom.byId(id);
            while (domWidget) {
                if (domClass.contains(domWidget, "outer")) {
                    // mark the field label red
                    domClass.add(domWidget, "important");

                    // show error information below the field
                    if (message) {
                        var box = lang.clone(this.errorInfoField);
                        box.firstChild.innerHTML = message;
                        domWidget.appendChild( box );
                    }
                    return;
                }
                domWidget = domWidget.parentNode;
            }
        },

        isObjectPublishable: function() {
            // get all required input elements and set the required field
            // after validation of the form reset required field
            // reset must be done when saved normally also or object is reloaded!
            // array.forEach(query(".required .input .dijit"), function(e) { console.debug(e.widgetId); registry.byId(e.getAttribute("widgetid")).set("required", false); })
            var publishable       = true;
            var notPublishableIDs = []
                , notSaveableIDs = [];

            // check first general validity
            this.checkValidityOfInputElements(notSaveableIDs);

            var widgets = query(".rubric:not(.hide) .required:not(.hide) .dijitTextBox:not(.noValidate), .rubric:not(.hide) .required:not(.hide) .dijitSelect:not(.noValidate)", "contentFrameBodyObject").map(function(item) {return item.getAttribute("widgetid");});
            widgets = widgets.concat(query(".dijitTextBox, .dijitSelect", "sectionTopObject").map(function(item) {return item.getAttribute("widgetid");}));
            var grids = query(".rubric:not(.hide) .required:not(.hide) .ui-widget:not(.noValidate)", "contentFrameBodyObject").map(function(item) {return item.id;});

            array.forEach(widgets, function(w) {
                if (lang.trim(registry.byId(w).get("displayedValue")).length === 0) {
                    notPublishableIDs.push(w);
                    notPublishableIDs.push( [w, message.get( "validation.error.empty.field" )] );
                }
            });
            array.forEach(grids, function(id) {
                var grid = UtilGrid.getTable(id);
                if (UtilGrid.getTableData(id).length === 0) {
                    notPublishableIDs.push( [id, message.get( "validation.error.empty.table" )] );
                }
                // this check is already done in checkValidityOfInputElements()
                /*else if (!grid.validateNonEmptyRows()) {
                    notPublishableIDs.push( [id, message.get( "validation.error.incomplete.tablerow" )] );
                }*/
            });

            topic.publish("/onBeforeObjectPublish", notPublishableIDs);

            console.debug("not publishable IDs:");
            console.debug(notPublishableIDs);
            if (notPublishableIDs.length > 0) {
                array.forEach(notPublishableIDs, function(id){
                    if (id instanceof Array) {
                        this.setErrorLabel(id[0], id[1]); // [id, message]
                    } else {
                        this.setErrorLabel(id);
                    }
                }, this);
            }

            // merge problematic fields to be recognized by the "show next error"-button
            notPublishableIDs = notPublishableIDs.concat( notSaveableIDs );

            if (notPublishableIDs.length > 0) {
                publishable = false;
                this.showErrorButton(notPublishableIDs);
            }

            return publishable;

        },


        isAddressPublishable: function(){
            var publishable       = true;
            var notPublishableIDs = [];

            this.resetRequiredFields();

            var widgets = query(".rubric:not(.hide) .required .dijitTextBox, .rubric:not(.hide) .required .dijitSelect", "contentFrameBodyAddress").map(function(item) {return item.getAttribute("widgetid");});
            widgets = widgets.concat(query(".dijitTextBox, .dijitSelect", "sectionTopAddress").map(function(item) {return item.getAttribute("widgetid");}));
            var grids = query(".rubric:not(.hide) .required .ui-widget", "contentFrameBodyAddress").map(function(item) {return item.id;});

            array.forEach(widgets, function(w) {
                if (lang.trim(registry.byId(w).get("displayedValue")).length === 0) {
                    notPublishableIDs.push(w);
                }
            });
            array.forEach(grids, function(g) {
                if (UtilGrid.getTableData(g).length === 0) {
                    notPublishableIDs.push(g);
                }
            });

            topic.publish("/onBeforeAddressPublish", notPublishableIDs);

            console.debug("not publishable IDs:");
            console.debug(notPublishableIDs);
            if (notPublishableIDs.length > 0) {
                array.forEach(notPublishableIDs, function(id){
                    this.setErrorLabel(id);
                }, this);
                publishable = false;
                this.showErrorButton(notPublishableIDs);
            }

            return publishable;

        },

        checkValidityOfInputElements: function(/*Array*/invalidExtInputs) {
            this.resetRequiredFields();

            var widgets = query(".rubric:not(.hide) span:not(.hide) .dijitTextBox, .rubric:not(.hide) span:not(.hide) .dijitSelect", "contentFrameBodyObject");
            widgets = widgets.concat(query(".dijitTextBox, .dijitSelect", "sectionTopObject"));
            var grids   = query(".rubric:not(.hide) span:not(.hide) .input .ui-widget", "contentFrameBodyObject");

            var invalidInputs = invalidExtInputs ? invalidExtInputs : [];
            widgets.forEach(function(w) {
                var widget = registry.getEnclosingWidget(w);
                if (!widget.validate()) invalidInputs.push( [widget.id, widget.message] );
            });
            grids.forEach(function(g) {
                var grid = UtilGrid.getTable(g.id);
                // check if grid has empty rows
                if (!grid.validateNonEmptyRows()) invalidInputs.push( [g.id, message.get("validation.error.empty.rows")] );

                // validate against a user defined validation function
                if (grid.validate) {
                    var res = grid.validate();
                    if (!res) invalidInputs.push( [g.id, grid.message] );
                }
            });

            //invalidExtInputs = invalidInputs.concat(invalidGrids);
            if (invalidInputs.length > 0) {
                console.debug("invalid fields:");
                console.debug(invalidInputs);
                array.forEach(invalidInputs, function(id){
                    if (id instanceof Array) {
                        this.setErrorLabel(id[0], id[1]); // [id, message]
                    } else {
                        this.setErrorLabel(id);
                    }
                }, this);
                this.showErrorButton(invalidInputs);
                return "INVALID_INPUT_INVALID";
            }
            return "VALID";

        },

        showErrorButton: function(ids) {
            var errorButton = registry.byId("bShowNextError");
            var unique = {};

            //get rid of duplicates
            var filteredIDs = array.filter(ids, function(value) {
                if (!unique[value]) {
                    unique[value] = true;
                    return true;
                }
                return false;
            });

            errorButton.invalidIds = filteredIDs;
            errorButton.pos = 0;
            style.set(errorButton.domNode, "visibility", "visible");

        },

        checkValidityOfAddressInputElements: function() {
            console.debug("Checking validity of address ui elements...");

            var widgets = query(".rubric:not(.hide) .input .dijitTextBox, .input .dijitSelect", "contentFrameBodyAddress");
            widgets = widgets.concat(query(".dijitTextBox, .dijitSelect", "sectionTopAddress"));
            var grids   = query(".rubric:not(.hide) .input .ui-widget", "contentFrameBodyAddress");

            var invalidInputs = [];
            widgets.forEach(function(w) {
                var res = registry.getEnclosingWidget(w).validate();
                if (!res) invalidInputs.push(w.id);
            });
            var invalidGrids = [];
            grids.forEach(function(g) {
                if (UtilGrid.getTable(g.id).validate) {
                    var res = UtilGrid.getTable(g.id).validate();
                    if (!res) invalidGrids.push(g.id);
                }
            });

            invalidInputs = invalidInputs.concat(invalidGrids);
            if (invalidInputs.length > 0) {
                console.debug("invalid fields:");
                console.debug(invalidInputs);
                return "INVALID_INPUT_INVALID";
            }
            return "VALID";
        }

    })();
});