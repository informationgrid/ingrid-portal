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

define(["dojo/_base/declare", "dojo/_base/array", "dojo/_base/lang", "dojo/query", "dojo/topic", "dojo/dom", "dojo/dom-class", "dojo/dom-style", "dijit/registry",
        "ingrid/utils/Grid"], 
        function(declare, array, lang, query, topic, dom, domClass, style, registry, UtilGrid) {
    return declare(null, {
        
        /* IDs of UI Elements for checking etc. */
//        dqUiTableElements: ["dq109Table", "dq112Table", "dq113Table", "dq114Table", "dq115Table",
//            "dq120Table", "dq125Table", "dq126Table", "dq127Table"],
        
        resetRequiredFields: function() {
            // get all labels within object- and address form and remove class, which makes them red
            query(".important", "hierarchyContent").removeClass("important");
            style.set(registry.byId("bShowNextError").domNode, "visibility", "hidden");
        },
        
        setErrorLabel: function(id){
            if (array.indexOf(["objectName", "objectClass", "objectOwner"], id) != -1) {
                domClass.add(id + "Label", "important");
                return;
            }
            
            var domWidget = dom.byId(id);
            while (domWidget) {
                if (domClass.contains(domWidget, "outer")) {
                    domClass.add(domWidget, "important");
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
            var notPublishableIDs = [];
            
            // check first general validity
            this.checkValidityOfInputElements(notPublishableIDs);
            
            var widgets = query(".rubric:not(.hide) .required:not(.hide) .dijitTextBox:not(.noValidate), .rubric:not(.hide) .required:not(.hide) .dijitSelect:not(.noValidate)", "contentFrameBodyObject").map(function(item) {return item.getAttribute("widgetid");});
            widgets = widgets.concat(query(".dijitTextBox, .dijitSelect", "sectionTopObject").map(function(item) {return item.getAttribute("widgetid");}));
            var grids = query(".rubric:not(.hide) .required:not(.hide) .ui-widget:not(.noValidate)", "contentFrameBodyObject").map(function(item) {return item.id;});
            
            array.forEach(widgets, function(w) {
                if (lang.trim(registry.byId(w).get("displayedValue")).length === 0) {
                    notPublishableIDs.push(w);
                }
            });
            array.forEach(grids, function(id) {
                var grid = UtilGrid.getTable(id);
                if (UtilGrid.getTableData(id).length === 0) {
                    notPublishableIDs.push(id);
                } else if (!grid.validateNonEmptyRows()) {
                    notPublishableIDs.push(id);
                }
            });
            
            topic.publish("/onBeforeObjectPublish", notPublishableIDs);
            
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
                if (!widget.validate()) invalidInputs.push(widget.id);
            });
            grids.forEach(function(g) {
                var grid = UtilGrid.getTable(g.id);
                // check if grid has empty rows
                if (!grid.validateNonEmptyRows()) invalidInputs.push(g.id);

                // validate against a user defined validation function
                if (grid.validate) {
                    var res = grid.validate();
                    if (!res) invalidInputs.push(g.id);
                }
            });
            
            //invalidExtInputs = invalidInputs.concat(invalidGrids);
            if (invalidInputs.length > 0) {
                console.debug("invalid fields:");
                console.debug(invalidInputs);
                array.forEach(invalidInputs, function(id){
                    this.setErrorLabel(id);
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
        
        
            /*
            var isValid = function(widgetId) {
                var widget = registry.byId(widgetId);

                // check SlickGrid
                if (widget == undefined) {
                    widget = gridManager[widgetId];
                    //return true;
                }

                // check if function isValid() exists, if not then also check if a
                // validator function exists (for textareas!)
                var widgetIsValid = widget.isValid ? widget.isValid() : widget.validator ? widget.validator() : true;
                var widgetIsInRange = widget.isInRange ? widget.isInRange() : true;
                var widgetIsEmpty = widget.isEmpty ? widget.isEmpty() : false;

                if (widget.required && widgetIsEmpty) {
                    console.debug("Widget " + widgetId + " is required but empty.");
                    return "INVALID_REQUIRED_BUT_EMPTY";
                }

                if (!widgetIsEmpty && (!widgetIsValid || !widgetIsInRange)) {
                    if (!widgetIsValid) {
                        console.debug("Widget " + widgetId + " contains invalid input.");
                        return "INVALID_INPUT_INVALID";
                    }
                    if (!widgetIsInRange) {
                        console.debug("Widget " + widgetId + " is out of range.");
                        return "INVALID_INPUT_OUT_OF_RANGE";
                    }
                }

                // Check if any input element contains invalid html tags
                if (widget instanceof dijit.form.ValidationTextBox) {
                    var val = "" + widget.getValue();
                    var evilTag = /<(?!b>|\/b>|i>|\/i>|u>|\/u>|p>|\/p>|br>|br\/>|br \/>|strong>|\/strong>|ul>|\/ul>|ol>|\/ol>|li>|\/li>)[^>]*>/i;
                    if (val.search(evilTag) != -1) {
                        return "INVALID_INPUT_HTML_TAG_INVALID";
                    }
                }

                return "VALID";
            };


            var checkValidityOfWidgets = function(widgetIdList) {
                for (var i in widgetIdList) {
                    var res = isValid(widgetIdList[i]);
                    if (res != "VALID") {
                        return res;
                    }
                }
                return "VALID";
            };

            // check if the errorClass was set in one of the input elements
            //if (query(".importantBackground", "contentFrameBodyAddress").length > 0)
            //  return "INVALID_INPUT_INVALID";

            var addressClass = UtilAddress.getAddressClass();

            var valid = checkValidityOfWidgets(adrUiInputElements);
            if (valid != "VALID") {
                return valid;
            }
            valid = checkValidityOfWidgets(this["adrClass" + addressClass + "UiInputElements"]);
            if (valid != "VALID") {
                return valid;
            }

            return "VALID";
            */
        }
    })();
});