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
        <link rel="stylesheet" href="dojo-src/release/dojo/dijit/themes/claro/claro.css">
        <link rel="stylesheet" href="dojo-src/release/dojo/dojox/grid/resources/claroGrid.css">
    
        <link rel="stylesheet" type="text/css" href="css/admin.css"></link>
    
        <script type="text/javascript" src="dojo-src/release/dojo/dojo/dojo.js" djConfig="parseOnLoad:true, locale:'en'"></script>
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
            dojo.require("dojo.parser");
            dojo.require("dijit.form.Button");
            
            
            var locale = "en";
            var previousContent = "welcomeDiv";
            var allUsers = null;
            var availableUsers = null;
        
            var loggedInUser = "<%= request.getSession(true).getAttribute("userName") %>";
            console.debug("user is: " + loggedInUser);

            dojo.connect(window, "onload", function() {
                var parameters = dojo.queryToObject(dojo.doc.location.search.substring(1));
                
                getAllUsers();
                getAllAvailableUsers();
                createCatalogueTable();
                
                dojo.style(previousContent, "display", "block");
                if (parameters.section) {
                    if (parameters.section == "user") 
                        showContent("manageUserDiv");
                    else if (parameters.section == "catalogues")
                        showContent("catOverviewDiv");
                } 
            });
            
            function initUserTable(users) {
                var newStore = new dojo.data.ItemFileWriteStore(
                    {data: {items: addLinks(users)}}
                );
                usersGrid.setStore(newStore);
            }
            
            function addLinks(users) {
                users.forEach(function(u) {
                    u.btn_edit   = "<input type='button' class='table' onclick='editUser(\""+u.login+"\");'   value='edit'>";
                    u.btn_delete = "<input type='button' class='table' onclick='removeUser(\""+u.login+"\");' value='delete'>";
                });
                return users;
            }

            function showLoginError() {
                document.getElementById("error").style.display = "block";
            }

            function getAllUsers() {
                UserRepoManager.getAllUsers(function(users) {
                    allUsers = users;
                    initUserTable(users);
                });
            }

            function getAllAvailableUsers() {
                SecurityService.getAvailableUsers(function(users) {
                    availableUsers = users;
                });
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
                        iplug.btn_delete = "<input type='button' class='table' onclick='removeCatalogue(\""+iplug.iplug+"\", \""+iplug.admin+"\");' value='delete'>";
                        iplug.catAdmin = user.surname + ", " + user.firstName + " (" + user.login + ")";
                        connectedIPlugs.push(iplug);
                    } else {
                        iplug.btn_add = "<input type='button' class='table' onclick='addCatalogueDialog(\""+iplug.iplug+"\");' value='add'>";
                        freeIPlugs.push(iplug);
                    }
                });
                
                result.connectedIPlugs = connectedIPlugs;
                result.freeIPlugs = freeIPlugs;
                return result;
            }

            function createCatalogueTable() {
                // get all connected catalogues
                CatalogManagementService.getConnectedCataloguesInfo({
                    callback: function(iplugs) {
                    var atLeastOneNotConnectedIPlug = false;
                    var atLeastOneConnectedIPlug = false;
                    
                    var tableData = addCatLinks(iplugs);
                    
                    var newStore = new dojo.data.ItemFileWriteStore(
                        {data: {items: tableData.connectedIPlugs}}
                    );
                    connectedCataloguesGrid.setStore(newStore);
                    
                    newStore = new dojo.data.ItemFileWriteStore(
                        {data: {items: tableData.freeIPlugs}}
                    );
                    availableCataloguesGrid.setStore(newStore);
                },
                errback: function(error) {
                    console.debug("There's no connected iplug!");
                }});
                
                
            }

            function addUser() {
                
                dialog.showPage("Add User", "dialogs/admin_editUser.jsp", null, null, true, {
                    user: null
                 });
                
            }
            

            function usernameExists(login) {
                //var userBox = dijit.byId("usersGrid");
                var exists = false;
                
                usersGrid.store.fetch({
                    onItem: function(item) {
                        if (item.login.toString().toLowerCase() == login.toLowerCase()) {
                            exists = true;
                            return;
                        }
                    }
                });
                return exists;
            } 
            
            function editUser(userLogin) {
                var user = getUserInfo(userLogin);
                
                dialog.showPage("Edit User", "dialogs/admin_editUser.jsp", null, null, true, {
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
                dialog.show("Remove User", "Do you really want to remove the user: <br />" + user.surname +", " + user.firstName, dialog.INFO, 
                        [{caption:"OK", action:function(){
                            UserRepoManager.removeUser(user.login[0], function(success) {
                                window.location.search="?section=user&rnd="+Math.random();
                                //window.location.reload();
                                console.debug("success removing user: " + success);
                            });
                        }}, 
                         {caption:"Cancel", action:function(){console.debug("cancel");}}
                        ]);
            }

            function addCatalogueDialog(iplug) {
                dialog.showPage("Choose Catalogue Administrator", "dialogs/admin_chooseCatAdmin.jsp", null, null, true, {iplug:iplug, callback:addCatalogue});
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
                dialog.show("Remove Catalogue", "Do you really want to remove this connection: <br />" + catalogue +" <-> " + login, dialog.INFO, 
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
                         {caption:"Cancel", action:function(){console.debug("cancel");}}
                        ]);
                
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
        </script>
    </head>
    
    <body class="claro">
        <div id="header">
            <img src="img/logo.gif" alt="InGrid Editor">
            <h1>InGrid Editor Administration</h1>
            <span id="logoutContainer"><a href="logout.jsp">Logout</a></span>
        </div>
        <div id="menu">
	        <div class="block"><p>1</p></div><h2>User Management</h2>
	        <ul>
	            <li><a href="#" onclick="showContent('manageUserDiv');">Manage User</a></li>
	            <!--<li><a href="#" onclick="showContent('addUserDiv');">Add User</a></li>-->
	        </ul>
	        <div class="block"><p>2</p></div>
	        <h2>Catalogue Management</h2>
	        <ul>
	            <li><a href="#" onclick="showContent('catOverviewDiv');">Manage Catalogues</a></li>
	            <!--<li><a href="#" onclick="showContent('addCatDiv');">Add Catalogue</a></li>-->
	            <!--<li><a href="#" onclick="showContent('revomeCatDiv');">Remove Catalogue</a></li>-->
            </ul>
        </div>
        <div id="welcomeDiv" class="content">
            <h3>Welcome</h3>
            <div>Here you can manage the users and connected catalogues.</div>
        </div>
        
        <div id="manageUserDiv" class="content">
            <h3>Manage User</h3>
            <div style="width: 96%; height: 400px;">
                <table id="usersGrid" jsId="usersGrid" dojoType="dojox.grid.DataGrid" escapeHTMLInData=false selectable=false selectionMode="single" style="height: 100%; width: 100%;">
                <thead>
                    <tr>
                        <th field="surname" width="100px">Name</th>
                        <th field="firstName" width="100px">First Name</th>
                        <th field="email" width="auto">Email</th>
                        <th field="btn_edit" width="80px">&nbsp;</th>
                        <th field="btn_delete" width="80px">&nbsp;</th>
                    </tr>
                </thead>
            
                </table>
                <div class="td"><input type="button" onclick="addUser()" value="Add User"></div>
            </div>
        </div>
    
        <div id="catOverviewDiv" class="content">
            <h3>Connected Catalogues</h3> 
	        <div style="width: 96%; height: 300px;">
                <table id="connectedCataloguesGrid" jsId="connectedCataloguesGrid" dojoType="dojox.grid.DataGrid" escapeHTMLInData=false selectable=false selectionMode="single" style="height: 100%; width: 100%;">
                <thead>
                    <tr>
                        <th field="iplug" width="200px">iPlug</th>
                        <th field="catAdmin" width="auto">Catalogue Administrator</th>
                        <th field="btn_delete" width="80px">&nbsp;</th>
                    </tr>
                </thead>
                </table>
            </div>
            <h3>Available Catalogues</h3>
            <div style="width: 96%; height: 300px;">
            <table id="availableCataloguesGrid" jsId="availableCataloguesGrid" dojoType="dojox.grid.DataGrid"  escapeHTMLInData=false selectable=false selectionMode="single" style="height: 100%; width: 100%;">
            <thead>
                <tr>
                    <th field="iplug" width="auto">iPlug</th>
                    <th field="btn_add" width="80px">&nbsp;</th>
                </tr>
            </thead>
        
            </table>
        </div>
	    </div>
    </body>
</html>