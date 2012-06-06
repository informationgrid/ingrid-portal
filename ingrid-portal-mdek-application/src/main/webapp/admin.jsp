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
        <link rel="stylesheet" href="dojo-src/release/dojo/dojox/grid/resources/Grid.css">
        <link rel="stylesheet" href="dojo-src/release/dojo/dojox/grid/resources/claroGrid.css">
    
        <link rel="stylesheet" type="text/css" href="css/admin.css"></link>
    
        <script type="text/javascript" src="dojo-src/release/dojo/dojo/dojo.js" djConfig="parseOnLoad:true, locale:'en'"></script>
        
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
        
            var loggedInUser = "<%= request.getSession(true).getAttribute("userName") %>";
            console.debug("user is: " + loggedInUser);
            getAllUsers();
            getAllAvailableUsers();
            createCatalogueTable();

            dojo.connect(window, "onload", function() {
                dojo.style(previousContent, "display", "block");
            });
            
            function initUserTable(users) {
                var newStore = new dojo.data.ItemFileWriteStore(
                    {data: {items: addLinks(users)}}
                );
                usersGrid.setStore(newStore);
            }
            
            function addLinks(users) {
                users.forEach(function(u) {
//                    u.firstName = "<a href='#' onclick='editUser();'>" + u.firstName + "</a>";
//                    u.surname = "<a href='#' onclick='editUser();'>" + u.surname + "</a>";
//                    u.email = "<a href='#' onclick='editUser();'>" + u.email + "</a>";
                    u.edit = "<a href='#' onclick='editUser("+JSON.stringify(u)+");'>edit</a>";
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
                    createUserTable(users, "usersChoice", "userChoiceAdd");
                });
            }

            function createUserTable(users, id, parentId) {
                console.debug(users);
                var element = dojo.create("select", {id: id});
                dojo.forEach(users, function(user) {
                    dojo.place(dojo.create("option", { value: user, innerHTML: formatUser(user) }), element);

                });
                dojo.place(element, parentId);
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

            function createCatalogueTable() {
                // get all connected catalogues
                CatalogManagementService.getConnectedCataloguesInfo({
                    callback: function(iplugs) {
                    var atLeastOneNotConnectedIPlug = false;
                    var atLeastOneConnectedIPlug = false;
                    if (iplugs && iplugs.length > 0) {
                        var element = dojo.create("select", {id:"catalogueSelectAdd"});
                        var elementDisconnect = dojo.create("select", {id:"catalogueSelectDisconnect"});
                        dojo.forEach(iplugs, function(iplug) {
                            // add to table only if iplug is connected to a user/catAdmin
                            if (iplug.admin) {
	                            var row = dojo.create("div", {'class':'tr'});
	                            dojo.create("div", {'class':'td', innerHTML:iplug.iplug}, row);
	                            dojo.create("div", {'class':'td', innerHTML:formatUser(iplug.admin)}, row);
	                            dojo.place(row, "cataloguesView");

	                            // fill select box for catalogues to disconnect
	                            dojo.place(dojo.create("option", { value: iplug.admin, innerHTML: iplug.iplug }), elementDisconnect);
	                            atLeastOneConnectedIPlug = true;
                            } else {
                                // select box shows only not connected catalogues
                                dojo.place(dojo.create("option", { innerHTML: iplug.iplug }), element);
                                atLeastOneNotConnectedIPlug = true;
                            }
                        });
                        dojo.place(element, "catalogueChoiceAdd");
                        dojo.place(elementDisconnect, "catalogueChoiceDisconnect");

                        if (atLeastOneNotConnectedIPlug) {
                            dojo.style("containerAddCatalogueEmpty", "display", "none");
                            dojo.style("containerAddCatalogue", "display", "");
                        }
                        if (atLeastOneConnectedIPlug) {
                            dojo.style("containerDisconnectCatalogueEmpty", "display", "none");
                            dojo.style("containerDisconnectCatalogue", "display", "");
                        }
                    }
                },
                errback: function(error) {
                    console.debug("There's no connected iplug!");
                }});
                
                
            }

            function addUser() {
                
                var user = {};
                user.login = dojo.byId("login").value;
                user.password = dojo.byId("password").value;
                user.firstName = dojo.byId("firstName").value;
                user.surname = dojo.byId("surname").value;
                user.email = dojo.byId("email").value;
                
                var errors = isValidUserData(user, dojo.byId("password_again").value);
                errors += isValidNewUserData(user);
                

                // check if username already exists
                if (errors.length == 0) { 
	                UserRepoManager.addUser(user, function(success) {
	                    window.location.reload();
	                    console.debug("success adding user: " + success);
	                });
                } else {
                    dojo.style("errorAddUser", "display", "");
                    dojo.byId("errorAddUser").innerHTML = errors;
                }
            }
            
            function isValidNewUserData(user) {
                var errors = "";
                if (user.password == "") {
                    errors += "<p>Password must not be empty!</p>";
                }
                
                if (usernameExists(user.login))
                    errors += "<p>Login already exists! Please choose another one.</p>";
                
                return errors;
            }
            
            function isValidUserData(user, passwordRepeat) {
                var errors = "";
                
                if (user.login == "" || user.firstName == "" || user.surname == "" || user.email == "")
                    errors += "<p>All fields have to be filled!</p>";
                
                if (user.password != passwordRepeat)
                    errors += "<p>Password does not match! Please type again.</p>";
                
                return errors;
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
            
            function editUser(u) {
                
                dialog.showPage("test", "dialogs/admin_editUser.jsp", null, null, true, {
                   user: u,
                   callback: updateUserData
                });
            }
            
            function updateUserData(user) {
                UserRepoManager.addUser(user, function(success) {
                    window.location.reload();
                    console.debug("success adding user: " + success);
                });
            }

            function removeUser() {
                var selection = usersGrid.selection.getSelected();
                if (selection.length > 0) {
                    var name = selection[0].login[0];
                    // dialog if user shall be deleted
                    dialog.show("Remove User", "Do you really want to remove the user: <br />" + selection[0].surname +", " + selection[0].firstName, dialog.INFO, 
                            [{caption:"OK", action:function(){
                                UserRepoManager.removeUser(name, function(success) {
                                    window.location.reload();
                                    console.debug("success removing user: " + success);
                                });
                            }}, 
                             {caption:"Cancel", action:function(){console.debug("cancel");}}
                            ]);
                }
            }

            function addCatalogue() {
                var catalogue = dojo.byId("catalogueSelectAdd").value;
                var login = dojo.byId("usersChoice").value;

                SecurityService.createCatAdmin(catalogue, login, {
                    callback: function(result) {
                                  console.debug(result);
                                  if (result == true) {
                                      window.location.reload();
                                  }
                              }

                });
            }

            function removeCatalogue() {
                var box = dojo.byId("catalogueSelectDisconnect");
                var login = box.value;
                var catalogue = box.options[box.selectedIndex].text;
                SecurityService.removeCatAdmin(catalogue, login, {
                    callback: function(result) {
                                  console.debug(result);
                                  if (result == true) {
                                      window.location.reload();
                                  }
                              }

                });
            }

            function showContent(divId) {
                if (previousContent == divId) return;
                
                dojo.style(divId, "display", "block");
                if (previousContent) dojo.style(previousContent, "display", "");
                previousContent = divId;
                //if (divId == "xxx")
                    usersGrid.resize();
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
	            <li><a href="#" onclick="showContent('addUserDiv');">Add User</a></li>
	        </ul>
	        <div class="block"><p>2</p></div>
	        <h2>Catalogue Management</h2>
	        <ul>
	            <li><a href="#" onclick="showContent('catOverviewDiv');">Show Catalogues</a></li>
	            <li><a href="#" onclick="showContent('addCatDiv');">Add Catalogue</a></li>
	            <li><a href="#" onclick="showContent('revomeCatDiv');">Remove Catalogue</a></li>
            </ul>
        </div>
        <div id="welcomeDiv" class="content">
            <h3>Welcome</h3>
            <div>Here you can manage the users and connected catalogues.</div>
        </div>
        
        <div id="manageUserDiv" class="content">
            <h3>Manage User</h3>
            <div style="width: 96%; height: 400px;">
                <table id="usersGrid" jsId="usersGrid" dojoType="dojox.grid.DataGrid" rowHeight="25" escapeHTMLInData=false selectable=false selectionMode="single" style="height: 100%; width: 100%;"><!--keepSelection=true data-dojo-props="autoHeight:true, selectionMode:'single'" >-->
                <thead>
                    <tr>
                        <th field="surname" width="200px">Name</th>
                        <th field="firstName" width="200px">First Name</th>
                        <th field="email" width="auto">Email</th>
                        <th field="edit" width="30px">&nbsp;</th>
                    </tr>
                </thead>
            
                </table>
                <div class="td"><input type="button" onclick="removeUser()" value="Remove User"></div>
            </div>
        </div>
    
        <div id="addUserDiv" class="content">
            <h3>Add User</h3>
	        <div class="table container">
	            <div class="tr">
	                <div class="td bold">Login:</div><div class="td"><input id="login" type="text"></div>
                </div>
                <div class="tr">
	                <div class="td bold">Password:</div><div class="td"><input id="password" type="password"></div>
	            </div>
	            <div class="tr">
                    <div class="td bold">Password (repeat):</div><div class="td"><input id="password_again" type="password"></div>
                </div>
                <div class="tr">
                    <div class="td">&nbsp;</div>
                </div>
	            <div class="tr">
	                <div class="td bold">First Name:</div><div class="td"><input id="firstName" type="text"></div>
	            </div>
	            <div class="tr">
	                <div class="td bold">Last Name:</div><div class="td"><input id="surname" type="text"></div>
	            </div>
                <div class="tr">
                    <div class="td bold">Email:</div><div class="td"><input id="email" type="text"></div>
                </div>
                <div class="tr">
	                <div class="td"><input type="button" onclick="addUser()" value="Add User"></div>
	            </div>
	        </div>
	        <span id="errorAddUser" class="error" style="display:none;"></span>
	    </div>
        
        <div id="catOverviewDiv" class="content">
            <h3>Show Catalogues</h3>
	        <div id="cataloguesView" class="table container">
	            <div class="tr bold">
	                <div class="td">ID</div>
	<!--                 <div class="td">Katalogname</div> -->
	<!--                 <div class="td">Partner</div> -->
	<!--                 <div class="td">Anbieter</div> -->
	<!--                 <div class="td">Administrator</div> -->
	                <div class="td">Login</div>
	            </div>
	        </div>
	    </div>
        
        <div id="addCatDiv" class="content"> 
            <h3>Add Catalogue</h3>
	        <div id="containerAddCatalogue" class="table container" style="display:none;">
	            <div class="tr bold">
	                <div class="td">Catalogue</div>
	                <div class="td">User</div>
	            </div>
	            <div class="tr">
	                <div id="catalogueChoiceAdd" class="td"></div>
	                <div id="userChoiceAdd" class="td"></div>
	                <div class="td"><input type="button" onclick="addCatalogue()" value="Add Catalogue"></div>
	            </div>
	        </div>
	        <div id="containerAddCatalogueEmpty">There's no free catalogue available.</div>
        </div>
        
        <div id="revomeCatDiv" class="content">
            <h3>Remove Catalogue</h3>
	        <div id="containerDisconnectCatalogue" class="table container" style="display:none;">
	            <div class="tr bold">
	                <div class="td">Catalogue</div>
	            </div>
	            <div class="tr">
	                <div id="catalogueChoiceDisconnect" class="td"></div>
	                <div class="td"><input type="button" onclick="removeCatalogue()" value="Remove Catalogue"></div>
	            </div>
	        </div>
	        <div id="containerDisconnectCatalogueEmpty">There's no connected catalogue available.</div>
        </div>
    </body>
</html>