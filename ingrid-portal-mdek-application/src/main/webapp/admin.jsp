<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<fmt:setLocale value="<%= request.getParameter("lang") == null ? "de" : request.getParameter("lang") %>" scope="session" />
<fmt:setBundle basename="messages" scope="session"/>

<%

    String currentUser = (String)request.getSession(true).getAttribute("userName");
    if (!"admin".equals(currentUser)) {
        String destination ="login.jsp";
        response.sendRedirect(response.encodeRedirectURL(destination));
    }
%>

<html dir="ltr">
    
    <head>
    
        <link rel="stylesheet" href="dojo-src/release/dojo/dojo/resources/dojo.css">
        <link rel="stylesheet" href="dojo-src/release/dojo/dojox/grid/resources/Grid.css">
        <link rel="stylesheet" href="dojo-src/release/dojo/dijit/themes/claro/claro.css">
        <!--<link rel="stylesheet" href="dojo-src/release/dojo/dojox/grid/resources/claroGrid.css">-->
    
        <link rel="stylesheet" type="text/css" href="css/admin.css"></link>
    
        <script type="text/javascript" src="dojo-src/release/dojo/dojo/dojo.js" djConfig="parseOnLoad:true, locale:'de'"></script>
        <script type="text/javascript" src="dojo-src/release/dojo/custom/layer.js"></script>
        
        <script type='text/javascript' src='dwr/engine.js'></script>
        <script type='text/javascript' src='dwr/util.js'></script>
        <script type='text/javascript' src='js/message.js'></script>
        <script type='text/javascript' src='js/dialog.js'></script>
        <script src='dwr/interface/SecurityService.js'></script>
        <script src='dwr/interface/UserRepoManager.js'></script>
        <script src='dwr/interface/CatalogManagementService.js'></script>
        
        <script type="text/javascript">
            dojo.require("dijit.dijit");
            dojo.require("dojox.grid.DataGrid");
            dojo.require("dojo.data.ItemFileWriteStore");
            dojo.require("dijit.form.Select");
            dojo.require("dojo.data.ItemFileReadStore");
            dojo.require("dojo.parser");
            dojo.require("dijit.form.Button");
            
            
            var locale = "de";
            var previousContent = "welcomeDiv";
            var allUsers = null;
            var availableUsers = null;
            var allIgeUsers = null;
            var allCatalogues = null;
            var connectedIPlugs = null;
        
            var loggedInUser = "<%= request.getSession(true).getAttribute("userName") %>";
            console.debug("user is: " + loggedInUser);

            dojo.connect(window, "onload", function() {
                var parameters = dojo.queryToObject(dojo.doc.location.search.substring(1));
                
                
                var def = getAllUsers();
                def.addCallback(getAllAvailableUsers);
                def.addCallback(getAllIgeUsers);
                def.addCallback(createCatalogueTable);
                
                def.addCallback(function() {                    
                    initUserTable(allUsers);
                    initCatalogueSelection();
                });
                
                
                
                dojo.style(previousContent, "display", "block");
                if (parameters.section) {
                    if (parameters.section == "user") 
                        showContent("manageUserDiv");
                    else if (parameters.section == "catalogues")
                        showContent("catOverviewDiv");
                } 
                // prevent sorting of button-columns
                usersGrid.canSort = function(col){ if(Math.abs(col) == 5 || Math.abs(col) == 6) { return false; } else { return true; } };
                connectedCataloguesGrid.canSort = function(col){ if(Math.abs(col) == 3) { return false; } else { return true; } };
                availableCataloguesGrid.canSort = function(col){ if(Math.abs(col) == 2) { return false; } else { return true; } };
                
                dojo.connect(window, "onresize", function() { 
//                    usersGrid.resize();
                    connectedCataloguesGrid.resize();
                    availableCataloguesGrid.resize();
                });
            });
            
            function initUserTable(users) {
                var newStore = new dojo.data.ItemFileWriteStore(
                    {data: {items: addLinks(users)}}
                );
                usersGrid.setStore(newStore);
            }
            
            function addLinks(users) {
                users.forEach(function(u) {
                    u.btn_edit   = "<input type='button' class='table' onclick='editUser(\""+u.login+"\");'   value='Bearbeiten'>";
                    u.btn_delete = "<input type='button' class='table' onclick='removeUser(\""+u.login+"\");' value='Entfernen'>";
                    if (allIgeUsers[u.login]) {
                        if(iplugIsConnected(allIgeUsers[u.login])) {
                            u.btn_login  = "<input type='button' class='table' onclick='loginAs(\""+u.login+"\");' value='Login' title='In Katalog \""+allIgeUsers[u.login]+"\" einloggen.'>";
                        } else {
                            u.btn_login  = "";
                        }
                        u.connected_to = allIgeUsers[u.login];
                    } else {
                        u.btn_login  = "";
                        u.connected_to = "";
                    }
                });
                return users;
            }
            
            function iplugIsConnected(plugid) {
                if (dojo.some(connectedIPlugs, function(item) { return item.iplug == plugid; })) {
                    return true;
                }
                return false;
            }

            function initCatalogueSelection() {
                var preparedData = prepareDataForSelect(allCatalogues);
                preparedData.push({id: "all", name: "alle Kataloge"});
                store = new dojo.data.ItemFileReadStore(
                        {data: {identifier: "id", label: "name", items: preparedData}}
                );
                store.fetch();
                
                var catChoice = dijit.byId("selectCatalogue");//new dijit.form.Select({store: store}, "selectCatalogue");
                catChoice.setStore(store);
                dojo.connect(catChoice, "onChange", function(value) {
                    if (value == "all") {
                        usersGrid.setQuery({})
                    } else {
                        usersGrid.setQuery({connected_to: value})
                    }
                });
                
            }
            
            function showLoginError() {
                document.getElementById("error").style.display = "block";
            }

            function getAllUsers() {
                var def = new dojo.Deferred();
                UserRepoManager.getAllUsers(function(users) {
                    allUsers = users;
                    def.resolve();
                });
                return def;
            }

            function getAllAvailableUsers() {
                var def = new dojo.Deferred();
                SecurityService.getAvailableUsers(function(users) {
                    availableUsers = users;
                    def.resolve();
                });
                return def;
            }
            
            function getAllIgeUsers() {
                var def = new dojo.Deferred();
                SecurityService.getIgeUsers(function(data) {
                    allIgeUsers = data;
                    allCatalogues = [];//{label: "all", value: "alle Kataloge"}];
                    for(var key in allIgeUsers) {
                        // only add iplugs that are not already in that array
                        if (allCatalogues.indexOf(allIgeUsers[key]) == -1) {
//                             allCatalogues.push({label: allIgeUsers[key], value:allIgeUsers[key]});
                            allCatalogues.push(allIgeUsers[key]);
                        }
                    }
                    def.resolve();
                });
                return def;
            }
            
            function getUserInfo(userLogin) {
                var foundUser = null;
                dojo.some(allUsers, function(user) {
                    if (user.login == userLogin) {
                        foundUser = user;
                        return true;
                    }
                });
                return foundUser;
            }

            function formatUser(user) {
                var formatString = "user";
                allUsers.some(function(u) {
                    if (u.login == user) {
                        formatString = u.surname + ", " + u.firstName + " ("+u.login+")";
                        return true;
                    }
                });
                return formatString;
            }
            
            function addCatLinks(iplugs) {
                var result = {};
                var connectedIPlugs = [], freeIPlugs = [];
                
                dojo.forEach(iplugs, function(iplug) {
                    var user = getUserInfo(iplug.admin);
                    if (user) {
                        iplug.btn_delete = "<input type='button' class='table' onclick='removeCatalogue(\""+iplug.iplug+"\", \""+iplug.admin+"\");' value='Entfernen'>";
                        iplug.catAdmin = user.surname + ", " + user.firstName + " (" + user.login + ")";
                        connectedIPlugs.push(iplug);
                    } else {
                        iplug.btn_add = "<input type='button' class='table' onclick='addCatalogueDialog(\""+iplug.iplug+"\");' value='Hinzufügen'>";
                        freeIPlugs.push(iplug);
                    }
                });
                
                result.connectedIPlugs = connectedIPlugs;
                result.freeIPlugs = freeIPlugs;
                return result;
            }

            function createCatalogueTable() {
                var def = new dojo.Deferred();
                // get all connected catalogues
                CatalogManagementService.getConnectedCataloguesInfo({
                    callback: function(iplugs) {
                    var atLeastOneNotConnectedIPlug = false;
                    var atLeastOneConnectedIPlug = false;
                    
                    connectedIPlugs = iplugs;
                    
                    if (iplugs) {
                        console.debug(iplugs);
                    }
                    var tableData = addCatLinks(iplugs);
                    
                    var newStore;
                    if (tableData.connectedIPlugs.length > 0) {
                        newStore = new dojo.data.ItemFileWriteStore(
                            {data: {items: tableData.connectedIPlugs}}
                        );
                        connectedCataloguesGrid.setStore(newStore);
                    } else {
                        dojo.style(connectedCataloguesGrid.domNode,"display", "none");
                        dojo.style("infoConnectedCat", "display", "block");
                    }
                    
                    if (tableData.freeIPlugs.length > 0) {
                        newStore = new dojo.data.ItemFileWriteStore(
                            {data: {items: tableData.freeIPlugs}}
                        );
                        availableCataloguesGrid.setStore(newStore);
                    } else {
                        dojo.style(availableCataloguesGrid.domNode,"display", "none");
                        dojo.style("infoNoFreeCat", "display", "block");
                    }
                    def.callback();
                },
                errback: function(error) {
                    console.debug("There's no connected iplug!");
                }});
                
                return def;
            }

            function addUser() {
                
                dialog.showPage("Benutzer hinzufügen", "dialogs/admin_editUser.jsp", null, null, true, {
                    user: null
                 });
                
            }
            

            function usernameExists(login) {
                //var userBox = dijit.byId("usersGrid");
                var exists = false;
                
                // after a clean installation the store might be null!
                if (usersGrid.store) {
                    usersGrid.store.fetch({
                        onItem: function(item) {
                            if (item.login.toString().toLowerCase() == login.toLowerCase()) {
                                exists = true;
                                return;
                            }
                        }
                    });
                }
                return exists;
            } 
            
            function editUser(userLogin) {
                var user = getUserInfo(userLogin);
                
                dialog.showPage("Benutzer bearbeiten", "dialogs/admin_editUser.jsp", null, null, true, {
                   user: user
                });
            }
            
            function updateUserData(user) {
                UserRepoManager.addUser(user, function(success) {
                    window.location.search ="?section=user&rnd="+Math.random();
                    //window.location.reload();
                    console.debug("success adding user: " + success);
                });
            }

            function removeUser(userLogin) {
                var user = getUserInfo(userLogin);
                dialog.show("Benutzer entfernen", "Möchten Sie wirklich diesen Benutzer entfernen: <br />" + user.surname +", " + user.firstName, dialog.INFO, 
                        [{caption:"OK", action:function(){
                            UserRepoManager.removeUser(user.login[0], function(success) {
                                window.location.search="?section=user&rnd="+Math.random();
                                //window.location.reload();
                                console.debug("success removing user: " + success);
                            });
                        }}, 
                         {caption:"Abbrechen", action:function(){console.debug("cancel");}}
                        ]);
            }

            function addCatalogueDialog(iplug) {
                if (availableUsers.length > 0) {
                    dialog.showPage("Benutzerwahl", "dialogs/admin_chooseCatAdmin.jsp", null, null, true, {iplug:iplug, callback:addCatalogue});
                } else {
                    dialog.show("Information", "Es gibt keinen freien Benutzer für eine Verbindung zu diesem Katalog. Bitte legen Sie zuerst einen neuen Benutzer an!");
                }
            } 
            
            function addCatalogue(catalogue, login) {
                SecurityService.createCatAdmin(catalogue, login, {
                    callback: function(result) {
                                  console.debug(result);
                                  if (result == true) {
                                      window.location.search ="?section=catalogues&rnd="+Math.random();
                                      //window.location.reload();
                                  }
                              }

                });
            }

            function removeCatalogue(catalogue, login) {
                dialog.show("Katalog entfernen", "Möchten Sie die Verbindung wirklich entfernen: <br />" + catalogue +" <-> " + login, dialog.INFO, 
                        [{caption:"OK", action:function(){
                            SecurityService.removeCatAdmin(catalogue, login, {
                                callback: function(result) {
                                              console.debug(result);
                                              if (result == true) {
                                                  window.location.search ="?section=catalogues&rnd="+Math.random();
                                                  //window.location.reload();
                                              }
                                          }
                            });
                        }}, 
                         {caption:"Abbrechen", action:function(){console.debug("cancel");}}
                        ]);
                
            }
            
            function loginAs(login) {
                console.debug("Not yet implemented!");
                SecurityService.forceUserLogin(login, function() {
                    window.location.reload();
                    //window.open("start.jsp", "_blank");
                });
            }

            function showContent(divId) {
                if (previousContent == divId) return;
                
                dojo.style(divId, "display", "block");
                if (previousContent) dojo.style(previousContent, "display", "");
                previousContent = divId;
                //if (divId == "xxx")
                    usersGrid.resize();
                    connectedCataloguesGrid.resize();
                    availableCataloguesGrid.resize();
            }
            
            function prepareDataForSelect(data){
                var preparedData = [];
                dojo.forEach(data, function(item) {
                    preparedData.push({name:item, id:item});
                });
                return preparedData;
            }
        </script>
    </head>
    
    <body class="claro">
        <div id="header">
            <img src="img/logo.gif" alt="InGrid Editor">
            <h1>InGrid Editor Administration</h1>
            <span id="logoutContainer"><a href="logout.jsp">Logout</a></span>
        </div>
        <div id="menu">
	        <div class="block"><p>1</p></div><h2>Benutzerverwaltung</h2>
	        <ul>
	            <li><a href="#" onclick="showContent('manageUserDiv');">Benutzer verwalten</a></li>
	        </ul>
	        <div class="block"><p>2</p></div>
	        <h2>Katalogverwaltung</h2>
	        <ul>
	            <li><a href="#" onclick="showContent('catOverviewDiv');">Kataloge verwalten</a></li>
            </ul>
        </div>
        <div id="welcomeDiv" class="content">
            <div class="contentBorder">
                <h3>Willkommen</h3>
                <div style="padding:10px;">Hier können Sie die Benutzer und angeschlossenen Kataloge verwalten.</div>
            </div>
        </div>
        
        <div id="manageUserDiv" class="content">
            <div class="contentBorder">
                <h3>Benutzer verwalten</h3>
                <div style="padding:10px; overflow: auto;">
                    Filterung nach Katalog: <div id="selectCatalogue" dojoType="dijit.form.Select"></div>
                    <table id="usersGrid" jsId="usersGrid" dojoType="dojox.grid.DataGrid" autoHeight="10" autoWidth=true escapeHTMLInData=false selectable="false" selectionMode="single" style="margin-bottom: 5px;">
                        <thead>
                            <tr>
                                <th field="login" width="70px;">Login</th>
                                <th field="surname" width="100px;">Nachname</th>
                                <th field="firstName" width="100px;">Vorname</th>
                                <th field="email" width="200px">E-Mail</th>
<!--                                 <th field="connected_to" width="200px">iPlug</th> -->
                                <th field="btn_edit" width="80px;">&nbsp;</th>
                                <th field="btn_delete" width="80px;">&nbsp;</th>
                                <th field="btn_login" width="80px;">&nbsp;</th>
                            </tr>
                        </thead>
                    </table>
                    <input type="button" onclick="addUser()" value="Benutzer hinzufügen">
                </div>
            </div>
        </div>
    
        <div id="catOverviewDiv" class="content">
            <div class="contentBorder">
                <h3>Kataloge verwalten</h3> 
    	        <div style="padding: 10px; overflow: auto;"">
                    <table id="connectedCataloguesGrid" jsId="connectedCataloguesGrid" dojoType="dojox.grid.DataGrid" autoHeight="10" autoWidth=true escapeHTMLInData=false selectable=false selectionMode="single" style="margin-bottom: 5px;">
                        <thead>
                            <tr>
                                <th field="iplug" width="200px">iPlug</th>
                                <th field="catAdmin" width="200px">Katalogadministrator</th>
                                <th field="btn_delete" width="80px">&nbsp;</th>
                            </tr>
                        </thead>
                    </table>
                    <p id="infoConnectedCat" style="display:none;">Keine freien Kataloge verbunden!</p>
                </div>
                <h3>Verfügbare Kataloge</h3>
                <div style="padding: 10px; overflow: auto;"">
                    <table id="availableCataloguesGrid" jsId="availableCataloguesGrid" dojoType="dojox.grid.DataGrid"  autoHeight="10" autoWidth=true escapeHTMLInData=false selectable=false selectionMode="single" style="height: 100%; width: 100%;  margin-bottom: 5px;">
                        <thead>
                            <tr>
                                <th field="iplug" width="200px">iPlug</th>
                                <th field="btn_add" width="80px">&nbsp;</th>
                            </tr>
                        </thead>
                    </table>
                    <p id="infoNoFreeCat" style="display:none;">Keine freien Kataloge verbunden!</p>
                </div>
            </div>
	    </div>
    </body>
</html>