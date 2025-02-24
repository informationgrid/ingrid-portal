<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2025 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>Tables Test</title>

        <script type="text/javascript">
            require(["ingrid/dialog", "dojo/parser", "dojo/on", "dojo/_base/array", "dojo/dom", "dojo/_base/lang", "dijit/form/Button", "dijit/form/Select", "dojo/data/ObjectStore",
                     "dojo/store/Memory", "dijit/registry", "ingrid/hierarchy/History", "ingrid/utils/Address", "ingrid/utils/Store", "ingrid/layoutCreator"], 
                     function(dialog, parser, on, array, dom, lang, Button, Select, ObjectStore, Memory, registry, History, UtilAddress, UtilStore, layoutCreator) {
                parser.parse();
                var backBtn = registry.byId("btnBack");
                var nextBtn = registry.byId("btnNext");
                
                on(backBtn, "click", lang.hitch(History, History.goBack));
                on(nextBtn, "click", lang.hitch(History, History.goNext));
                
                History.addPreviousButton( backBtn );
                History.addNextButton( nextBtn );
                
                History.stack = [
                    { id: 1, type: "O", title: "Test 1" },
                    { id: 2, type: "O", title: "Test 2" },
                    { id: 3, type: "O", title: "Test 3" },
                    { id: 4, type: "O", title: "Test 4" },
                    { id: 5, type: "O", title: "Test 5" }
                ];
                History.pointer = 0;
                
                var printStack = function() {
                    var result = "";
                    for (var i=0; i<History.stack.length; i++) {
                        if (History.pointer === i) result += "* ";
                        result += History.stack[i].title + "<br>";
                    }
                    dom.byId('stack').innerHTML = result;
                };
                
                History._callerFunction = function(node) {
                    console.log("At node:", node);
                    printStack();
                }
                
                printStack();

                /* var store = new Memory({
                  data: [
                    { id: "foo", label: "Foo" },
                    { id: "bar", label: "Bar" }
                  ]
                });
                var os = new ObjectStore({ objectStore: store });
	            var dynSelect = new Select({
	                store: os
	            }); */
                
                var storeProps = {
                    data: {
                        identifier: '1',
                        label: '0'
                    }
                };
	            
	            // layoutCreator.createSelectBox("dynSelect", null, storeProps, null);
	            layoutCreator.createFilteringSelect("dynSelect", null, storeProps, null);
	            var dynSelect = registry.byId("dynSelect");
	            var parentUuid = "71FE85D0-67AA-485D-900D-924B10855B21";
	            
	            var loadResponsibleUsers = function(callback) {
	            	SecurityService.getResponsibleUsersForNewObject(parentUuid, false, true, {
	                    callback: function(userList) {
	                        var list = [];
	                        array.forEach(userList, function(user) {
	                            var title = UtilAddress.createAddressTitle(user.address);
	                            var uuid = user.address.uuid;
	                            list.push([title, uuid]);
	                        });
	                        
	                        UtilStore.updateWriteStore("dynSelect", list, {
	                            identifier: "1",
	                            label: "0",
	                            items: list
	                        });
	                        
	                        callback();
	                    },
	                    errorHandler: function(errMsg, err) {
	                        console.debug(errMsg);
	                        console.debug(err);
	                    }
	                });
	            };
	            
	            dynSelect.isLoaded = function() {
	            	return this._isLoaded;
	            }
	            dynSelect._startSearchFromInput = function() {
	            	if (!this.isLoaded()) {
	            		loadResponsibleUsers( lang.hitch( this, function() {
		            		this._isLoaded = true;
		            		this._startSearch(this.focusNode.value);
	            		} ) );
	            	}
	            	this._startSearch(this.focusNode.value);
	            }
	            dynSelect.loadDropDown = function(/*Function*/ loadCallback){
	            	loadResponsibleUsers( lang.hitch( this, function() {
	            		this._isLoaded = true;
	            		this._startSearchAll(); 
	            	} ) );
	    		};
	    		
	    		var list = [["aaaa", "xxxx"]];
	    		UtilStore.updateWriteStore("dynSelect", list, {
                    identifier: "1",
                    label: "0",
                    items: list
                });
	    		
	    		dynSelect.set("value", "xxxx");
	    		
            });
            
            
        </script>
        
	</head>
	<body>
	    <h1>Dialog Tests</h1>
        <input type="button" onclick="require('ingrid/dialog').showContextHelp(this, 1000, 'The Title Caption')" value="Show Context Help">
        <input type="button" onclick="require('ingrid/dialog').show('The Info', 'This is just an info!')" value="Show Info Box">
        <input type="button" onclick="require('ingrid/dialog').show('The Warning', 'This is just a warning!', dialog.WARNING)" value="Show Warning Box">
        <input type="button" onclick="require('ingrid/dialog').showPage('Address Search', '../dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 585, true, {grid: 'generalAddress'})" value="Address Dialog">
        <input type="button" onclick="require('ingrid/dialog').showPage('GetCapabilities Wizzard', '../dialogs/mdek_get_capabilities_wizard_dialog.jsp?c='+userLocale, 755, 585, true)" value="GetCapabilities Dialog">
        <input type="button" onclick="require('ingrid/dialog').showPage('General Object Wizzard', '../dialogs/mdek_create_object_wizard_dialog.jsp?c='+userLocale, 755, 585, true)" value="Object Wizzard">
        
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Navigator', '../dialogs/mdek_thesaurus_assist_dialog.jsp?c='+userLocale, 735, 430, true);" value="Thesaurus Navigator">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'sns', dstTable: 'thesaurusTerms'});" value="SNS Thesaurus Dialog">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://data.uba.de/umt/de/concepts/_00049251.rdf'});" value="RDF Thesaurus Dialog - Leaf">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-params.herokuapp.com/de/scheme.rdf'});" value="RDF Thesaurus Dialog - Hierarchy">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-params.herokuapp.com/bodenchemische-parameter-organisch.rdf'});" value="RDF Thesaurus Dialog - Hierarchy Leaf">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-exam.herokuapp.com/de/scheme.rdf'});" value="RDF Thesaurus Dialog - Hierarchy no root">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-exam.herokuapp.com/'});" value="RDF Thesaurus Dialog - Hierarchy (boden-exam)">
        
        <h1>History-Buttons</h1>
        <div>
            <button id="btnBack" type="button" data-dojo-type="dijit/form/Button">Back</button>
            <button id="btnNext" type="button" data-dojo-type="dijit/form/Button">Next</button>
            <div id="stack"></div>
        </div>

        <div id="termContainer"></div>
        
        <div id="dynSelect"></div>
	</body>
</html>
