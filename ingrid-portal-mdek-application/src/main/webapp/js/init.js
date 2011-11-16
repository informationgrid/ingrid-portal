var currentUser  = null;
var currentGroup = null;
var catalogData  = null;

var sysLists     = null;
var sysGuis      = null;

var scrollBarWidth = 17;

// the default timeout for a session, which will be requested during init
var sessionTimeoutInterval = 30;

var initialJSessionId = -1;

var sessionExpired = false;

// this is defined in start.jsp where language is transported within url request parameter 
//var userLocale = ...

//dojo.require("dijit.form.Select");

// bugfix which should be part of dojo 1.6
// onChange handler is called when changed programmatically (not good)
/*dijit.form.Select.extend({
   _updateSelection: function() {
        this.value = this._getValueFromOpts();
        var val = this.value;
        if(!dojo.isArray(val)){
            val = [val];
        }
        if(val && val[0]){
            dojo.forEach(this._getChildren(), function(child){
                var isSelected = dojo.some(val, function(v){
                    return child.option && (v === child.option.value);
                });
                dojo.toggleClass(child.domNode, this.baseClass + "SelectedOption", isSelected);
                dijit.setWaiState(child.domNode, "selected", isSelected);
            }, this);
        }
   }
});*/

// change focus behaviour since target can be null under certain circumstances
// see: http://bugs.jquery.com/ticket/7910
/*var _beforeactivate = jQuery.event.special.change.filters.beforeactivate;

jQuery.event.special.change.filters.focus = jQuery.event.special.change.filters.beforeactivate = function(e) {
    e.target = e.target || e.currentTarget || e.originalTarget;
    _beforeactivate(e);
}*/

dojo.addOnLoad(function() {
    // show an info message for browser IE7 that it shouldn't be used
    if (dojo.isIE == 7) {
        dialog.show("Info", message.get("general.info.browser"), dialog.INFO);
    }
    
    // remember the session id for later comparison if session has expired
    initialJSessionId = dojo.cookie("JSESSIONID");
    
	var deferred2 = initCatalogData();
	
	var deferred = initCurrentUser();
	
    deferred.addCallback(initCurrentUserPermissions);
	deferred.addCallback(initCurrentGroups);
    
	deferred2.addCallback(fetchSysLists);
	
	// get guiIds that are going to be configured for visibility
	//fetchGuiIdList();
    initGeneralEventListener(); // for release activate!
    
	// create the main layout with toolbar, splitter, tree, ...
	createBaseLayout();
	
	// create the containers where external pages shall be loaded into
	createMenuPages();
    
    // create the menu bar
    deferred.then(function() { igeMenuBar.create(dijit.byId("menubarPane"));});
	
	deferred.then(initPageHeader);
	
	deferred.then(initSessionKeepalive);
	
	// select a page initially
	deferred2.then(function() {
        createSelectBox("languageBox", null, {data: {identifier: 'id',label: 'label'}}, null, "js/data/languageCode.json");
        // the connect has to be called delayed, otherwise onChange will be 
        // triggered immediately and the page would be switching always
        // -> not when set initially?! (see declaration of selectbox!)
        setTimeout(function() {dojo.connect(dijit.byId("languageBox"), "onChange", menuEventHandler.switchLanguage);}, 2000);
        //if (!jumpToNodeOnInit())
            dijit.byId("stackContainer").selectChild(dijit.byId("pageDashboard"), false);
            jumpToNodeOnInit();
        
        // create an iframe which will be used for printing    
        dojo.create("iframe", {id: 'printFrame', name: 'printFrame', style: {position:"absolute", left:"-1000px", height: "0", border:"0"}}, dojo.body());

        //console.debug("Cookie:");
        //console.debug(document.cookie);
	});
});

function initPrintFrame() {
    var cssLink1 = document.createElement("link") 
    cssLink1.href = "js/jquery/slick.grid.css"; 
    cssLink1.rel = "stylesheet"; 
    cssLink1.type = "text/css";
    var cssLink2 = document.createElement("link") 
    cssLink2.href = "css/styles.css"; 
    cssLink2.rel = "stylesheet"; 
    cssLink2.type = "text/css";
    parent.printFrame.document.head.appendChild(cssLink1);
    parent.printFrame.document.head.appendChild(cssLink2);
}

function initCatalogData() {
	var deferred = new dojo.Deferred();

	CatalogService.getCatalogData({
		//preHook: UtilDWR.enterLoadingState,
		//postHook: UtilDWR.exitLoadingState,
		callback: function(res) {
			// Update catalog Data in udkDataProxy
			catalogData = res;
			dojo.byId("currentCatalogName").innerHTML = catalogData.catalogName;
			deferred.callback();
		},
		errorHandler:function(mes, obj){
			//UtilDWR.exitLoadingState();
			displayErrorMessage(obj);
			dialog.show(message.get("general.error"), obj, dialog.WARNING);
			console.debug(mes);
			deferred.errback();
		}
	});

	return deferred;
}

function fetchSysLists() {
	var def = new dojo.Deferred();

	// Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
	var languageCode = UtilCatalog.getCatalogLanguage();
	console.debug("LanguageShort is: " + languageCode);
	
	var lstIds = [100, 101, 102, 502, 505, 510, 515, 518, 520, 523, 525, 526, 527, 528, 1100, 1230, 1320, 1350, 1370, 1400, 1410,
        3535, 3555, 3385, 3571, 4300, 4305, 4430, 5100, 5105, 5110, 5120, 5130, 5200, 5300, 6000, 6005, 6010, 6020, 6100, 6200, 6300,
        7109, 7110, 7112, 7113, 7114, 7115, 7117, 7120, 7125, 7126, 7127, 99999999];

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

function initCurrentUser() {
	var def = new dojo.Deferred();

	SecurityService.getCurrentUser({
		callback: function(user) {
			currentUser = user;
			var roleName = UtilSecurity.getRoleName(user.role);
            var title = UtilAddress.createAddressTitle(user.address);
            dojo.byId("currentUserName").innerHTML = title
            dojo.byId("currentUserRole").innerHTML = "· "+roleName+ " ·";
			
			def.callback();
		},
		errorHandler:function(mes){
			dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
			console.debug("Error: "+mes);
			def.errback(mes);
		}
	});

	return def;
}

function initCurrentUserPermissions() {
    var def = new dojo.Deferred();

    SecurityService.getUserPermissions(currentUser.addressUuid, {
        //preHook: UtilDWR.enterLoadingState,
        //postHook: UtilDWR.exitLoadingState,
        callback: function(permissionList) {
            currentUserPermissions = permissionList;
            def.callback();
        },
        errorHandler:function(mes){
            //UtilDWR.exitLoadingState();
            dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
            console.debug("Error: "+mes);
            def.errback(mes);
        }
    });

    return def;     
}


function initCurrentGroups() {
    var def = new dojo.Deferred();
    var getGroupNameDef = getCurrentGroupNames();

    getGroupNameDef.addCallback(function(groupNames) {
        
        for (var i=0;i<groupNames.length; i++) {
            SecurityService.getGroupDetails(groupNames[i], {
                //preHook: UtilDWR.enterLoadingState,
                //postHook: UtilDWR.exitLoadingState,
                callback: function(group) {
                    console.debug("Add group details to currentGroups.");
                    currentGroups.push(group);
                },

                errorHandler:function(mes){
                    //UtilDWR.exitLoadingState();
                    dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
                    console.debug("Error: "+mes);
                    def.errback(mes);
                }
            });
        }
        def.callback();
    });

    return def;
}

function getCurrentGroupNames() {
    var def = new dojo.Deferred();

    SecurityService.getGroups(true, {
        //preHook: UtilDWR.enterLoadingState,
        //postHook: UtilDWR.exitLoadingState,
        callback: function(groupList) {
            var userGroupIds = currentUser.groupIds;
            var groupNames = [];
            for (var i = 0; i < groupList.length; ++i) {
                for (var j = 0; j < userGroupIds.length; ++j) {
                    if (groupList[i].id == userGroupIds[j]) {
                        console.debug("Add group name to current users group names: " + groupList[i].name);
                        groupNames.push(groupList[i].name);
                        break;
                    }
                }
            }
            def.callback(groupNames);
        },
        errorHandler:function(mes){
            //UtilDWR.exitLoadingState();
            dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
            console.debug("Error: "+mes);
            def.errback(mes);
        }
    });

    return def;     
}

function initGeneralEventListener() {
    // Disable backspace default behaviour (browser back button)
        dojo.connect(null, "onkeydown", function(evt){
            //console.debug("key down: " + evt.keyCode);
            if (evt.keyCode == dojo.keys.BACKSPACE) {
                if (!(evt.target instanceof HTMLInputElement) && !(evt.target instanceof HTMLTextAreaElement)) {
//                  dojo.debug("Preventing backspace default behaviour on "+evt.target);
                    evt.preventDefault();
                }

            } else if (evt.keyCode == dojo.keys.F5) {         
                dialog.show(message.get("general.hint"), message.get("dialog.browserFunctionDisabled"), dialog.INFO);
                evt.preventDefault();
            }
        });
    //}


    // Catch the window close event
    if(selenium == false || selenium == 'false'){
        window.onbeforeunload = function(event){
            var event = event || window.event;
            
            if (!sessionExpired) {
                //console.debug("unload");
                if (dojo.isIE) {
                    // Catch clicks on the upper left and upper right corner. Also catch clicks on the app's 'close' button.
                    if (event.clientY < 0 ||
                    (event.clientY < 23 && event.clientX > document.documentElement.clientWidth - 172 && event.clientX < document.documentElement.clientWidth - 9)) {
                        event.returnValue = message.get("general.closeWindow");
                        return message.get("general.closeWindow");
                    }
                    
                }
                else {
                    return message.get("general.closeWindow");
                }
            }
        }
    }
}

function createBaseLayout() {
	// create BorderContainer (Splitpane)
    var main = new dijit.layout.BorderContainer({ 
        design: "headline",
        gutters: false,
		toggleSplitterClosedThreshold: "100px",
		toggleSplitterOpenSize: "80px",
		style: "width:100%; heigth: 100%;",
        liveSplitters: false }, "contentContainer");

	// the top header of the page containing logo, menu and toolbar
	var header = new dijit.layout.BorderContainer({ 
        design: "headline",
        gutters: false,
		region: "top",
		//toggleSplitterClosedThreshold: "100px",
		//toggleSplitterOpenSize: "80px",
		style: "height: 82px;",
        liveSplitters: false }, "headerContainer");
		
	//===========================================================
	// top pane - logo 
	var logoPane = new dijit.layout.ContentPane({
		id: "logoPane",
    	splitter: false,
    	region: "top",
		style: "height: 29px;"
        }, "logoContainer");
        
	// top pane - menu 
	var topMenuPane = new dijit.layout.ContentPane({
		id: "menubarPane",
    	splitter: false,
    	region: "center",
		style: "height: 32px;"
        }, "menuContainer");
	
	// show info on which menu page we are
    var topMenuInfoPane = new dijit.layout.ContentPane({
        id: "menubarInfoPane",
        splitter: false,
        region: "bottom",
        style: "height: 21px;"
        }, "menuInfoContainer");
	
	//===========================================================
	// (exchangable) content pane 
    var contentPane = new dojox.layout.ContentPane({
		id: "contentPane",
    	//splitter: true,
    	region: "center",
		style: "padding: 0px;"
        }).placeAt(main.domNode);
		
	//===========================================================
	
	main.startup();
}

function createMenuPages() {
	var sc = new dijit.layout.StackContainer({
        style: "width: 100%;",
        id: "stackContainer"
    }).placeAt(dijit.byId("contentPane").domNode);
	
	var hierarchy = new dojox.layout.ContentPane({
		id: "pageHierarchy",
		title: "hierarchy",
		layoutAlign: "client",
		//style: "padding: 0px; width: 100%; height: 100%;",
		href: "mdek_hierarchy.jsp?c="+userLocale,
		//preload: "false",
		scriptHasHooks: true,
        //doLayout: true,
		executeScripts: true
	});
	
	var research = new dojox.layout.ContentPane({
		id: "pageResearch",
		title: "research",
		layoutAlign: "client",
		href: "mdek_research_search.jsp?c="+userLocale,
		//preload: "false",
		scriptHasHooks: true,
        executeScripts: "true"
	});
    
    var researchThesaurus = new dojox.layout.ContentPane({
        id: "pageResearchThesaurus",
        title: "researchThesaurus",
        layoutAlign: "client",
        href: "mdek_research_thesaurus.jsp?c="+userLocale,
        //preload: "false",
        scriptHasHooks: true,
        style:" overflow:hidden;",
        executeScripts: "true"
    });
    
    var researchDB = new dojox.layout.ContentPane({
        id: "pageResearchDB",
        title: "researchDB",
        layoutAlign: "client",
        href: "mdek_research_database.jsp?c="+userLocale,
        //preload: "false",
        scriptHasHooks: true,
        executeScripts: "true"
    });
	
	var statistics = new dojox.layout.ContentPane({
		id: "pageStatistics",
		title: "statistic",
		layoutAlign: "client",
		href: "mdek_statistics.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var quality = new dojox.layout.ContentPane({
		id: "pageQualityEditor",
		title: "quality",
		layoutAlign: "client",
		href: "mdek_qa_editor.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
    var qualityAssurance = new dojox.layout.ContentPane({
        id: "pageQualityAssurance",
        title: "quality",
        layoutAlign: "client",
        href: "mdek_qa_assurance.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: "true"
    });
	
	//------------------------------------------
	var userManagement = new dojox.layout.ContentPane({
		id: "userManagement",
		title: "userManagement",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_user_administration.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var catalogSettings = new dojox.layout.ContentPane({
		id: "catalogSettings",
		title: "catalogSettings",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_catalog_settings.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	/*var fieldSettings = new dojox.layout.ContentPane({
		id: "fieldSettings",
		title: "fieldSettings",
		layoutAlign: "client",
		style: "padding: 0px; width: 864px !important; height: 100%;",
		href: "admin/mdek_admin_catalog_field_settings.jsp",
		executeScripts: "true"
	});*/
	
	var generalSettings = new dojox.layout.ContentPane({
		id: "generalSettings",
		title: "generalSettings",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_general_settings.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var groupManagement = new dojox.layout.ContentPane({
		id: "groupManagement",
		title: "groupManagement",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_group_administration.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var permissionOverview = new dojox.layout.ContentPane({
		id: "permissionOverview",
		title: "permissionOverview",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_permission_overview.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminImport = new dojox.layout.ContentPane({
		id: "adminImport",
		title: "adminImport",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_import.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminExport = new dojox.layout.ContentPane({
		id: "adminExport",
		title: "adminExport",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_export.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminAnalysis = new dojox.layout.ContentPane({
		id: "adminAnalysis",
		title: "adminAnalysis",
		layoutAlign: "client",
		style: "padding: 0px; width: 1000px;",
		href: "admin/mdek_admin_catman_analysis.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminDublet = new dojox.layout.ContentPane({
		id: "adminDublet",
		title: "adminDublet",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_catman_duplicates.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminURL = new dojox.layout.ContentPane({
		id: "adminURL",
		title: "adminURL",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_catman_urls.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminCodeLists = new dojox.layout.ContentPane({
		id: "adminCodeLists",
		title: "adminCodeLists",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_catman_codelists.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	/*var adminAdditionalFields = new dojox.layout.ContentPane({
		id: "adminAdditionalFields",
		title: "adminAdditionalFields",
		layoutAlign: "client",
		style: "padding: 0px; width: 1000px;",
		href: "admin/mdek_admin_catman_additional_fields.jsp",
		executeScripts: "true"
	});*/
	
	var adminFormFields = new dojox.layout.ContentPane({
        id: "adminFormFields",
        title: "adminFormFields",
        layoutAlign: "client",
        style: "padding: 0px;",
        href: "admin/mdek_admin_catman_form_fields.jsp?c="+userLocale,
        executeScripts: true,
		scriptHasHooks: true
    });
	
	var adminSearchTerms = new dojox.layout.ContentPane({
		id: "adminSearchTerms",
		title: "adminSearchTerms",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_catman_search_terms.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminLocations = new dojox.layout.ContentPane({
		id: "adminLocations",
		title: "adminLocations",
		layoutAlign: "client",
		style: "padding: 0px;",
		href: "admin/mdek_admin_catman_locations.jsp?c="+userLocale,
		scriptHasHooks: true,
        executeScripts: "true"
	});
	
	var adminDeleteAddress = new dojox.layout.ContentPane({
        id: "adminDeleteAddress",
        title: "adminDeleteAddress",
        layoutAlign: "client",
        style: "padding: 0px;",
        href: "admin/mdek_admin_catman_delete_address.jsp?c="+userLocale,
        scriptHasHooks: true,
        executeScripts: "true"
    });
	
	var dashboard = new dojox.layout.ContentPane({
		id: "pageDashboard",
		title: "dashboard1",
		layoutAlign: "client",
		href: "mdek_dashboard.jsp?c="+userLocale,
		//preload: "false",
		scriptHasHooks: true,
        executeScripts: "true"
	});
	

	/*var controller = new dijit.layout.StackController({
        containerId: "stackContainer"
   	}).placeAt("menubarPane");*/
	
	sc.addChild(hierarchy);
	sc.addChild(research);
    sc.addChild(researchThesaurus);
    sc.addChild(researchDB);
	sc.addChild(statistics);
	sc.addChild(quality);
    sc.addChild(qualityAssurance);
	
	sc.addChild(userManagement);
	sc.addChild(catalogSettings);
	//sc.addChild(fieldSettings);
	sc.addChild(generalSettings);
	sc.addChild(groupManagement);
	sc.addChild(permissionOverview);
	sc.addChild(adminImport);
	sc.addChild(adminExport);
	sc.addChild(adminAnalysis);
	sc.addChild(adminDublet);
	sc.addChild(adminURL);
	sc.addChild(adminCodeLists);
	//sc.addChild(adminAdditionalFields);
	sc.addChild(adminFormFields);
	sc.addChild(adminSearchTerms);
	sc.addChild(adminLocations);
	sc.addChild(adminDeleteAddress);
	
	sc.addChild(dashboard);
	
	sc.startup();
}

function initPageHeader() {
	// Display the current user and role
	var roleName = UtilSecurity.getRoleName(currentUser.role);
	var title = UtilAddress.createAddressTitle(currentUser.address);
	/*dojo.byId("currentUserName").innerHTML = title;
	dojo.byId("currentUserRole").innerHTML = roleName;

	// Display the current catalog name
	dojo.byId("currentCatalogName").innerHTML = catalogData.catalogName;*/
}

// Init session keepalive and autosave
function initSessionKeepalive() {

	//Init session keepalive
	var sessionKeepaliveDef = UtilCatalog.getSessionRefreshIntervalDef();

	sessionKeepaliveDef.addCallback(function(sessionKeepaliveInterval) {
		if (sessionKeepaliveInterval > 0) {
            var interval = sessionKeepaliveInterval * 60 * 1000;
            setInterval("UtilGeneral.refreshSession();", interval);
        } else {
            UtilityService.getSessionTimoutInterval({
                callback: function(res) {
                    sessionTimeoutInterval = res;
                    UtilGeneral.sessionValid();
                }
            });
        }
	});

	// Init autosave
	var autosaveDef = UtilCatalog.getAutosaveIntervalDef();

	autosaveDef.addCallback(function(autosaveInterval) {
		// If the autosave Interval is set...
		if (autosaveInterval > 0) {
			// Calculate the time in milliseconds
			var autosaveIntervalTime = autosaveInterval * 60 * 1000;
	
			// autosaveTimer holds a reference to the 'timeout' object used by the javascript functions setTimeout, clearTimeout, ...
			var autosaveTimer = null;
			// autosave function that is executed every n minutes
			var autosaveFunction = "console.debug('autosave called.'); " +
										"if (udkDataProxy.dirtyFlag && " +
										"dijit.byId('dataTree').selectedNode && " +
										"dijit.byId('dataTree').selectedNode.item.userWritePermission[0]) {" +
											"menuEventHandler.handleSave(); " +
									"}";

			// Functions to manipulate the autosaveTimer.
			var clearAutosaveInterval = function() {
				console.debug("clear autosave interval called.");
				if (autosaveTimer) {
					clearTimeout(autosaveTimer);
					autosaveTimer = null;
				}
			}
	
			var setAutosaveInterval = function() {
				console.debug("set autosave interval called.");
				if (autosaveTimer) {
					clearAutosaveInterval();
				}
				autosaveTimer = setInterval(autosaveFunction, autosaveIntervalTime);
			}
	
			var resetAutosaveInterval = function() {
				console.debug("reset autosave interval called.");
				clearAutosaveInterval();
				setAutosaveInterval();
			}

			// -- Connect the events with the autosaveTimer --
			// Reset the timer: when a dataset is loaded, stored, published or created
			dojo.connect(udkDataProxy, "handleLoadRequest", resetAutosaveInterval);
			dojo.connect(udkDataProxy, "handleSaveRequest", resetAutosaveInterval);
			dojo.connect(udkDataProxy, "handlePublishObjectRequest", resetAutosaveInterval);
			dojo.connect(udkDataProxy, "handleCreateObjectRequest", resetAutosaveInterval);
			dojo.connect(udkDataProxy, "handlePublishAddressRequest", resetAutosaveInterval);
			dojo.connect(udkDataProxy, "handleCreateAddressRequest", resetAutosaveInterval);
		}
	});
}

// needed for finding duplicated entries in Inspire list
function inspireArrayContains(array, value) {
	var i, listed = false;
	for (i=0; i<array.length; i++) {
		if (array[i].title === value) {
			listed = true;
			break;
		}
	}
	return listed;
};

// get an array of Inspire topics
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

function toggleInfo(element)
{
  var toggleContent = document.getElementById(element + "Content");
  var arrow = document.getElementById(element).getElementsByTagName('img')[1];
  if (toggleContent.style.display == "block" || toggleContent.style.display == "")
  {
    toggleContent.style.display = "none";
    arrow.src = "img/ic_info_expand.gif";
  }
  else
  {
    toggleContent.style.display = "block";
    arrow.src = "img/ic_info_deflate.gif";
  }
}

/*
 * Toggle a table display
 */
function switchTableDisplay(onTableId, offTableId, /* boolean */isOn)
{
  if (isOn) {
    dojo.byId(offTableId).style.display = "none";
    dojo.byId(onTableId).style.display = "block";
    dijit.byId(onTableId).invalidate();
  }
  else {
    dojo.byId(onTableId).style.display = "none";
    dojo.byId(offTableId).style.display = "block";
    dijit.byId(offTableId).invalidate();
  }
}

function printDivContent(divId, /*optional*/frame, /*optional*/divElement) {
	if (!frame) {
	  frame = parent.printFrame;
	}
    if (!divId) 
        frame.document.body.innerHTML = divElement.innerHTML;
    else
	    frame.document.body.innerHTML = dojo.byId(divId).innerHTML;
	frame.focus();
	frame.print();
}

function jumpToNodeOnInit() {
    if (initJumpToNodeId.length != 0 && initJumpToNodeType.length != 0) {
        menuEventHandler.handleSelectNodeInTree(initJumpToNodeId, initJumpToNodeType);
        return true;
    }
    return false;
}

function fetchGuiIdList() {
	var deferred = new dojo.Deferred();

	// Get all gui ids
	CatalogService.getSysGuis(null, {
		callback: function(sysGuiList) {
			sysGuis = sysGuiList;
		},
		errorHandler: function(errMsg, err) {
			console.debug(errMsg);
			deferred.errback(err);			
		}
	});

	return deferred;
}


