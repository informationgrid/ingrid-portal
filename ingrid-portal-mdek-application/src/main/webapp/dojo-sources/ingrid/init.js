/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
    "dojo/_base/unload",
    "dojo/dom",
    "dojo/has",
    "dojo/_base/array",
    "dojo/_base/lang",
    "dojo/Deferred",
    "dojo/on",
    "dojo/keys",
    "dojo/topic",
    "dojo/dom-construct",
    "dojo/_base/window",
    "ingrid/dialog",
    "ingrid/message",
    "dijit/layout/StackContainer",
    "dijit/layout/BorderContainer",
    "dijit/layout/ContentPane",
    "dojox/layout/ContentPane",
    "dijit/registry",
    "ingrid/menu",
    "ingrid/layoutCreator",
    "ingrid/MenuActions",
    "ingrid/IgeActions",
    "ingrid/utils/PageNavigation",
    "ingrid/utils/Security",
    "ingrid/utils/Address",
    "ingrid/utils/Catalog",
    "ingrid/utils/String",
    "ingrid/utils/General",
    "ingrid/hierarchy/dirty",
    "ingrid/hierarchy/behaviours.user",
    "dojo/_base/sniff"
], function(declare, unload, dom, has, array, lang, Deferred, on, keys, topic, DomConstruct, wnd, dialog, message, StackContainer, BorderContainer, ContentPane, XContentPane, registry,
        igeMenuBar, layoutCreator, menuEventHandler, IgeActions, PageNavigation, UtilSecurity, UtilAddress, UtilCatalog, UtilString, UtilGeneral, dirty, behaviours) {
    return declare(null, {

        global: this,

        start: function() {

            console.log("start");
            this.create();

            // show an info message for browser IE7 that it shouldn't be used
            if (has("ie") == 7) {
                dialog.show("Info", message.get("general.info.browser"), dialog.INFO);
            }

            // remember the session id for later comparison if session has expired
            require(["dojo/cookie"], function(cookie) {
                UtilGeneral.initialJSessionId = cookie("JSESSIONID");
            });

            var self = this;
            var userDeferred = this.initCurrentUser()
                .then(this.initCurrentUserPermissions)
                .then(this.initCurrentGroups)
                .then(function() {

                    var deferred2 = self.initCatalogData()
                        .then(lang.hitch(self, self.fetchSysLists));



                    // get guiIds that are going to be configured for visibility
                    self.initGeneralEventListener(); // for release activate!

                    // wait for page rendered before
                    require(["dojo/domReady!"], function() {
                        // create the main layout with toolbar, splitter, tree, ...
                        self.createBaseLayout();

                        // create the containers where external pages shall be loaded into
                        self.createMenuPages();

                        self.addUpdateUserLink();

                        self.initSessionKeepalive();

                        // select a page initially
                        deferred2.then(function() {
                            layoutCreator.createSelectBox("languageBox", null, {
                                data: {
                                    identifier: 'id',
                                    label: 'label'
                                }
                            }, null, "js/data/languageCode.json");

                            // execute additional system behaviours
                            UtilCatalog.getOverrideBehavioursDef().then(function(data) {

                                // mark behaviours with override values
                                array.forEach(data, function(item) {
                                    if (behaviours[item.id]) {
                                        behaviours[item.id].override = item.active;
                                        if (item.params) {
                                            array.forEach(item.params, function(p) {
                                                var behaviourParam = array.filter(behaviours[item.id].params, function(param) { return param.id === p.id; })[0];
                                                lang.mixin(behaviourParam, p);
                                            });
                                        }
                                    }
                                });
                                for (var behave in behaviours) {
                                    if (!behaviours[behave] || !behaviours[behave].title) continue;
                                    // run behaviour if
                                    // 1) it's a system behaviour
                                    // 2) activated by default and not overridden
                                    // 3) activate if explicitly overridden
                                    if (behaviours[behave].type === "SYSTEM" &&
                                            (
                                                (behaviours[behave].defaultActive && behaviours[behave].override === undefined)
                                                || behaviours[behave].override === true
                                            )) {
                                        console.debug("execute system behaviour: " + behave);
                                        behaviours[behave].run();
                                    }
                                }

                                // create the menu bar after system behaviours are run, so that they can have an influence
                                // on the menu structure
                                igeMenuBar.create(registry.byId("menubarPane"));

                                var ids = [];
                                topic.publish("/collectAdditionalSyslistsToLoad", ids);
                                self.fetchSysListsByIds(ids)
                                    .then(function() {
                                        topic.publish("/additionalSyslistsLoaded");
                                    });

                            }, function(error) {
                                console.error("Error executing behvaiour:", error);
                            });


                            // the connect has to be called delayed, otherwise onChange will be
                            // triggered immediately and the page would be switching always
                            // -> not when set initially?! (see declaration of selectbox!)
                            setTimeout(function() {
                                on(registry.byId("languageBox"), "Change", menuEventHandler.switchLanguage);
                            }, 2000);
                            registry.byId("stackContainer").selectChild(registry.byId("pageDashboard"), false);
                            self.jumpToNodeOnInit();

                            // create an iframe which will be used for printing
                            DomConstruct.create("iframe", {
                                id: 'printFrame',
                                name: 'printFrame',
                                style: {
                                    position: "absolute",
                                    left: "-1000px",
                                    height: "0",
                                    border: "0",
                                    zoom: "2"
                                }
                            }, wnd.body());
                            setTimeout(self.initPrintFrame, 4000);
                        })
                        .then(null, function(err) {
                            console.error(err);
                            dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING, null, null, null, err ? err.stack : null);
                        });
                    });
                });
        },

        addUpdateUserLink: function() {

            SecurityService.isPortalConnected(function(isConnectedToPortal) {
                if (!isConnectedToPortal) {
                    var accountLink = DomConstruct.toDom("<a href='#' style='float: right;' onclick='require(\"ingrid/dialog\").showPage(\"Benutzer bearbeiten\", \"dialogs/admin_editUser.jsp\", null, null, true, { user: \"" + UtilSecurity.currentUser.userData.portalLogin + "\"})'>Account bearbeiten</a>");
                    var separator = DomConstruct.toDom("<span style=\"float:right; padding:0 5px 0 5px;\">|</span>");
                    DomConstruct.place(accountLink, "logout", "after");
                    DomConstruct.place(separator, "logout", "after");
                }
            });

        },

        initCatalogData: function() {
            var deferred = new Deferred();

            CatalogService.getCatalogData({
                //preHook: UtilDWR.enterLoadingState,
                //postHook: UtilDWR.exitLoadingState,
                callback: function(res) {
                    // Update catalog Data in udkDataProxy
                    UtilCatalog.catalogData = res;
                    dom.byId("currentCatalogName").innerHTML = UtilCatalog.catalogData.catalogName;
                    deferred.resolve();
                },
                errorHandler: function(mes, obj) {
                    //UtilDWR.exitLoadingState();
                    displayErrorMessage(obj);
                    dialog.show(message.get("general.error"), obj, dialog.WARNING);
                    console.debug(mes);
                    deferred.reject();
                }
            });

            return deferred.promise;
        },

        initCurrentUser: function() {
            var def = new Deferred();

            SecurityService.getCurrentUser({
                callback: function(user) {
                    UtilSecurity.currentUser = user;
                    var roleName = UtilSecurity.getRoleName(user.role);
                    var title = UtilAddress.createAddressTitle(user.address);
                    dom.byId("currentUserName").innerHTML = title;
                    dom.byId("currentUserRole").innerHTML = "· " + roleName + " ·";

                    def.resolve();
                },
                errorHandler: function(mes) {
                    dialog.show(message.get("general.error"), message.get("init.error.userNotFound"), dialog.WARNING);
                    console.error("Error: " + mes);
                    def.reject(mes);
                }
            });

            return def.promise;
        },

        create: function() {

            this.initGeneralEventListener = function() {
                // Disable backspace default behaviour (browser back button)
                on(window, "keydown", function(evt) {
                    //console.debug("key down: " + evt.keyCode);
                    if (evt.keyCode == keys.BACKSPACE) {
                        if (!(evt.target instanceof HTMLInputElement) && !(evt.target instanceof HTMLTextAreaElement)) {
                            // dojo.debug("Preventing backspace default behaviour on "+evt.target);
                            evt.preventDefault();
                        }

                    } else if (evt.keyCode == keys.F5) {
                        dialog.show(message.get("general.hint"), message.get("dialog.browserFunctionDisabled"), dialog.INFO);
                        evt.preventDefault();
                    }
                });

                // Catch the window close event
                if (selenium === false || selenium === 'false') {
                    // remember the event to switch it off for selenium tests (must be global)
                    eventWindowUnload = on(window, "beforeunload", function(event) {
                        event = event || window.event;

                        if (!UtilGeneral.sessionExpired) {
                            event.returnValue = message.get("general.closeWindow");
                            return message.get("general.closeWindow");
                        }
                    });
                }
            };

            this.createMenuPages = function() {
                var sc = new StackContainer({
                    style: "width: 100%;",
                    id: "stackContainer"
                }).placeAt(registry.byId("contentPane").domNode);

                var hierarchy = new XContentPane({
                    id: "pageHierarchy",
                    title: "hierarchy",
                    layoutAlign: "client",
                    href: "mdek_hierarchy.jsp?c=" + userLocale,
                    preload: false,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var research = new XContentPane({
                    id: "pageResearch",
                    title: "research",
                    layoutAlign: "client",
                    href: "mdek_research_search.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var researchThesaurus = new XContentPane({
                    id: "pageResearchThesaurus",
                    title: "researchThesaurus",
                    layoutAlign: "client",
                    href: "mdek_research_thesaurus.jsp?c=" + userLocale,
                    //preload: "false",
                    scriptHasHooks: true,
                    style: " overflow:hidden;",
                    executeScripts: true
                });

                var researchDB = new XContentPane({
                    id: "pageResearchDB",
                    title: "researchDB",
                    layoutAlign: "client",
                    href: "mdek_research_database.jsp?c=" + userLocale,
                    //preload: "false",
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var statistics = new XContentPane({
                    id: "pageStatistics",
                    title: "statistic",
                    layoutAlign: "client",
                    href: "mdek_statistics.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var quality = new XContentPane({
                    id: "pageQualityEditor",
                    title: "quality",
                    layoutAlign: "client",
                    href: "mdek_qa_editor.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });
                var qualityAssurance = new XContentPane({
                    id: "pageQualityAssurance",
                    title: "quality",
                    layoutAlign: "client",
                    href: "mdek_qa_assurance.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                //------------------------------------------
                var userManagement = new XContentPane({
                    id: "userManagement",
                    title: "userManagement",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_user_administration.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var catalogSettings = new XContentPane({
                    id: "catalogSettings",
                    title: "catalogSettings",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catalog_settings.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var generalSettings = new XContentPane({
                    id: "generalSettings",
                    title: "generalSettings",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_general_settings.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                /*
                var behaviourSettings = new XContentPane({
                    id: "behaviourSettings",
                    title: "behaviourSettings",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catalog_behaviours.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });
                */

                var groupManagement = new XContentPane({
                    id: "groupManagement",
                    title: "groupManagement",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_group_administration.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var permissionOverview = new XContentPane({
                    id: "permissionOverview",
                    title: "permissionOverview",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_permission_overview.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminImport = new XContentPane({
                    id: "adminImport",
                    title: "adminImport",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_import.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminExport = new XContentPane({
                    id: "adminExport",
                    title: "adminExport",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_export.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminAnalysis = new XContentPane({
                    id: "adminAnalysis",
                    title: "adminAnalysis",
                    layoutAlign: "client",
                    style: "padding: 0px; width: 1000px;",
                    href: "admin/mdek_admin_catman_analysis.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminDublet = new XContentPane({
                    id: "adminDublet",
                    title: "adminDublet",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catman_duplicates.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminURL = new XContentPane({
                    id: "adminURL",
                    title: "adminURL",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catman_urls.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    //parseOnLoad: false,
                    executeScripts: true
                });

                var adminCodeLists = new XContentPane({
                    id: "adminCodeLists",
                    title: "adminCodeLists",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catman_codelists.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminFormFields = new XContentPane({
                    id: "adminFormFields",
                    title: "adminFormFields",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catman_form_fields.jsp?c=" + userLocale,
                    executeScripts: true,
                    scriptHasHooks: true
                });

                var adminSearchTerms = new XContentPane({
                    id: "adminSearchTerms",
                    title: "adminSearchTerms",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catman_search_terms.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminLocations = new XContentPane({
                    id: "adminLocations",
                    title: "adminLocations",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catman_locations.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var adminDeleteAddress = new XContentPane({
                    id: "adminDeleteAddress",
                    title: "adminDeleteAddress",
                    layoutAlign: "client",
                    style: "padding: 0px;",
                    href: "admin/mdek_admin_catman_delete_address.jsp?c=" + userLocale,
                    scriptHasHooks: true,
                    executeScripts: true
                });

                var dashboard = new XContentPane({
                    id: "pageDashboard",
                    title: "dashboard1",
                    layoutAlign: "client",
                    href: "mdek_dashboard.jsp?c=" + userLocale,
                    //preload: "false",
                    scriptHasHooks: true,
                    executeScripts: true,
                    parseOnLoad: false
                });

                // the first child also will be selected and shown!!!
                sc.addChild(dashboard);


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
                //sc.addChild(behaviourSettings);
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

                sc.startup();
            };

            this.createBaseLayout = function() {
                // create BorderContainer (Splitpane)
                var main = new BorderContainer({
                    design: "headline",
                    gutters: false,
                    toggleSplitterClosedThreshold: "100px",
                    toggleSplitterOpenSize: "80px",
                    style: "width:100%; heigth: 100%;",
                    liveSplitters: false
                }, "contentContainer");

                // the top header of the page containing logo, menu and toolbar
                new BorderContainer({
                    design: "headline",
                    gutters: false,
                    region: "top",
                    //toggleSplitterClosedThreshold: "100px",
                    //toggleSplitterOpenSize: "80px",
                    style: "height: 82px;",
                    liveSplitters: false
                }, "headerContainer");

                //===========================================================
                // top pane - logo
                new ContentPane({
                    id: "logoPane",
                    splitter: false,
                    region: "top",
                    style: "height: 29px;"
                }, "logoContainer");

                // top pane - menu
                new ContentPane({
                    id: "menubarPane",
                    splitter: false,
                    region: "center",
                    style: "height: 32px;"
                }, "menuContainer");

                // show info on which menu page we are
                new ContentPane({
                    id: "menubarInfoPane",
                    splitter: false,
                    region: "bottom",
                    style: "height: 21px;"
                }, "menuInfoContainer");

                //===========================================================
                // (exchangable) content pane
                new XContentPane({
                    id: "contentPane",
                    //splitter: true,
                    region: "center",
                    style: "padding: 0px;"
                }).placeAt(main.domNode);

                //===========================================================

                main.startup();
            };

            this.initPrintFrame = function() {
                var cssLink1 = document.createElement("link");
                cssLink1.href = "/ingrid-portal-mdek-application/dojo-sources/ingrid/css/slick.grid.css";
                cssLink1.rel = "stylesheet";
                cssLink1.type = "text/css";
                var cssLink2 = document.createElement("link");
                cssLink2.href = "/ingrid-portal-mdek-application/dojo-sources/ingrid/css/styles.css";
                cssLink2.rel = "stylesheet";
                cssLink2.type = "text/css";
                // IE has problems here!
                if (parent.printFrame.document.head) {
                    parent.printFrame.document.head.appendChild(cssLink1);
                    parent.printFrame.document.head.appendChild(cssLink2);
                } else {
                    parent.printFrame.document.childNodes[0].childNodes[0].appendChild(cssLink1);
                    parent.printFrame.document.childNodes[0].childNodes[0].appendChild(cssLink2);
                }

            };

            this.fetchSysListsByIds = function(lstIds) {
                var def = new Deferred();

                // Setting the language code to "de". Uncomment the previous block to enable language specific settings depending on the browser language
                var languageCode = UtilCatalog.getCatalogLanguage();
                // console.debug("LanguageShort is: " + languageCode);

                CatalogService.getSysListsRemoveMetadata(lstIds, languageCode, {
                    callback: function(res) {
                        if (!window.sysLists) sysLists = {};
                        lang.mixin(sysLists, res);

                        // only if not sorted in backend, e.g. INSPIRE Themes (6100) !
                        array.forEach(lstIds, function(id) {
                            if (id != 6100) {
                                sysLists[id].sort(function(a, b) {
                                    return UtilString.compareIgnoreCase(a[0], b[0]);
                                });
                            }
                        });

                        def.resolve();
                    },
                    errorHandler: function(mes) {
                        console.error("Error: " + mes);
                        dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
                        def.reject(mes);
                    }
                });

                return def.promise;
            };

            this.fetchSysLists = function() {

                var lstIds = [100, 101, 102, 502, 505, 509, 510, 514, 515, 518, 520, 523, 525, 526, 527, 528, 600, 601, 1100, 1230, 1320, 1350, 1370, 1400, 1410, 2000, 2100,
                    3535, 3555, 3385, 3571, 4300, 4305, 4430, 5100, 5105, 5110, 5120, 5130, 5151, 5152, 5153, 5154, 5180, 5200, 5300, 6000, 6005, 6006, 6010, 6100, 6200, 6250, 6300, 6350, 6360,
                    6400, 6500, 7109, 7112, 7113, 7114, 7115, 7120, 7125, 7126, 7127, 7128, 8000, 8010, 99999999
                ];

                return this.fetchSysListsByIds(lstIds);
            };

            this.initCurrentUserPermissions = function() {
                var def = new Deferred();

                SecurityService.getUserPermissions(UtilSecurity.currentUser.addressUuid, {
                    //preHook: UtilDWR.enterLoadingState,
                    //postHook: UtilDWR.exitLoadingState,
                    callback: function(permissionList) {
                        UtilSecurity.currentUserPermissions = permissionList;
                        def.resolve();
                    },
                    errorHandler: function(mes) {
                        //UtilDWR.exitLoadingState();
                        dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
                        console.error("Error: " + mes);
                        def.reject(mes);
                    }
                });

                return def.promise;
            };


            this.initCurrentGroups = function() {
                var def = new Deferred();

                var getCurrentGroupNames = function() {
                    var def = new Deferred();

                    SecurityService.getGroups(true, {
                        //preHook: UtilDWR.enterLoadingState,
                        //postHook: UtilDWR.exitLoadingState,
                        callback: function(groupList) {
                            var userGroupIds = UtilSecurity.currentUser.groupIds;
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
                            def.resolve(groupNames);
                        },
                        errorHandler: function(mes) {
                            //UtilDWR.exitLoadingState();
                            dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
                            console.error("Error: " + mes);
                            def.reject(mes);
                        }
                    });

                    return def.promise;
                };

                getCurrentGroupNames()
                .then(function(groupNames) {
                    UtilSecurity.currentGroups = [];
                    for (var i = 0; i < groupNames.length; i++) {
                        SecurityService.getGroupDetails(groupNames[i], {
                            //preHook: UtilDWR.enterLoadingState,
                            //postHook: UtilDWR.exitLoadingState,
                            callback: function(group) {
                                console.debug("Add group details to currentGroups.");
                                UtilSecurity.currentGroups.push(group);
                            },

                            errorHandler: function(mes) {
                                //UtilDWR.exitLoadingState();
                                dialog.show(message.get("general.error"), message.get("init.loadError"), dialog.WARNING);
                                console.error("Error: " + mes);
                                def.reject(mes);
                            }
                        });
                    }
                    def.resolve(UtilSecurity.currentGroups);
                });

                return def.promise;
            };

            // Init session keepalive and autosave
            this.initSessionKeepalive = function() {

                //Init session keepalive
                var sessionKeepaliveDef = UtilCatalog.getSessionRefreshIntervalDef();

                sessionKeepaliveDef.then(function(sessionKeepaliveInterval) {
                    if (sessionKeepaliveInterval > 0) {
                        var interval = sessionKeepaliveInterval * 60 * 1000;
                        setInterval(lang.hitch(UtilGeneral, UtilGeneral.refreshSession), interval);
                    } else {
                        UtilityService.getSessionTimoutInterval({
                            callback: function(res) {
                                UtilGeneral.sessionTimeoutInterval = res;
                                lang.hitch(UtilGeneral, UtilGeneral.sessionValid);
                            }
                        });
                    }
                });

                // Init autosave
                UtilCatalog.getAutosaveIntervalDef()
                .then(function(autosaveInterval) {
                    // If the autosave Interval is set...
                    if (autosaveInterval > 0) {
                        // Calculate the time in milliseconds
                        var autosaveIntervalTime = autosaveInterval * 60 * 1000;

                        // autosaveTimer holds a reference to the 'timeout' object used by the javascript functions setTimeout, clearTimeout, ...
                        var autosaveTimer = null;
                        // autosave function that is executed every n minutes
                        var autosaveFunction = function() {
                            console.debug('autosave called.');
                            if (dirty.dirtyFlag &&
                                    registry.byId('dataTree').selectedNode &&
                                    registry.byId('dataTree').selectedNode.item.userWritePermission) {

                                menuEventHandler.handleSave();
                            }

                        };

                        // Functions to manipulate the autosaveTimer.
                        var clearAutosaveInterval = function() {
                            console.debug("clear autosave interval called.");
                            if (autosaveTimer) {
                                clearTimeout(autosaveTimer);
                                autosaveTimer = null;
                            }
                        };

                        var setAutosaveInterval = function() {
                            if (autosaveTimer) {
                                clearAutosaveInterval();
                            }
                            autosaveTimer = setInterval(autosaveFunction, autosaveIntervalTime);
                        };

                        var resetAutosaveInterval = function() {
                            clearAutosaveInterval();
                            setAutosaveInterval();
                        };

                        // -- Connect the events with the autosaveTimer --
                        // Reset the timer: when a dataset is loaded, stored, published or created
                        topic.subscribe("/loadRequest", resetAutosaveInterval);
                        topic.subscribe("/saveRequest", resetAutosaveInterval);
                        topic.subscribe("/publishObjectRequest", resetAutosaveInterval);
                        topic.subscribe("/createObjectRequest", resetAutosaveInterval);
                        topic.subscribe("/publishAddressRequest", resetAutosaveInterval);
                        topic.subscribe("/createAddressRequest", resetAutosaveInterval);
                    }
                });
            };

            // needed for finding duplicated entries in Inspire list
            this.inspireArrayContains = function(array, value) {
                var i, listed = false;
                for (i = 0; i < array.length; i++) {
                    if (array[i].title === value) {
                        listed = true;
                        break;
                    }
                }
                return listed;
            };

            this.jumpToNodeOnInit = function() {
                if (this.global.initJumpToNodeId.length !== 0 && this.global.initJumpToNodeType.length !== 0) {
                    require("ingrid/menu").handleSelectNodeInTree(this.global.initJumpToNodeId, this.global.initJumpToNodeType);
                    return true;
                }
                return false;
            };

            this.fetchGuiIdList = function() {
                var deferred = new Deferred();

                // Get all gui ids
                CatalogService.getSysGuis(null, {
                    callback: function(sysGuiList) {
                        sysGuis = sysGuiList;
                    },
                    errorHandler: function(errMsg, err) {
                        console.error(errMsg);
                        deferred.reject(err);
                    }
                });

                return deferred;
            };
        }

    })();
});
