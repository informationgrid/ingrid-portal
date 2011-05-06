<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
    <head>
        <link rel="stylesheet" href="css/styles.css">
        <script type="text/javascript" src="dojo-src/dojo/dojo.js" djConfig="parseOnLoad:true">
        </script>
        <script type="text/javascript">
            dojo.require("dijit.form.DateTextBox");
            dojo.require("dijit.form.NumberTextBox");
            dojo.require("dijit.form.FilteringSelect");
            dojo.require("dijit.form.Button");
            dojo.require("dijit.form.Form");
            dojo.require("dijit.form.SimpleTextarea");
            dojo.require("dijit.form.Select");
            
            
            dojo.addOnLoad(function(){
                dijit.byId("box13").set("required", true);
                
                dijit.byId("box13").validate = function() {
                    console.debug("validate text area: required="+this.required + "; value="+this.get("value"));
                    if (this.required && this.get("value") == "") {
                        return false;
                    }
                    return true;
                };
            });
        </script>
    </head>
    <body class="claro">
        <h1>Required fields tests!</h1>
        <br />
        <div style="width:100%;">
            <form dojoType="dijit.form.Form" id="myForm">
                <h2>TextBox</h2>
                <span id="uiElement1" type="optional" class="outer halfWidth">
                    <div>
                        <span class="label">
                            <label for="box1" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Not required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.ValidationTextBox" id="box1" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                <span id="uiElement2" type="optional" class="outer halfWidth required">
                    <div>
                        <span class="label">
                            <label for="box2" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.ValidationTextBox" id="box2" required="true" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                
                <h2>NumberTextBox</h2>
                <span id="uiElement3" type="optional" class="outer halfWidth">
                    <div>
                        <span class="label">
                            <label for="box3" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Not required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.NumberTextBox" id="box3" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                <span id="uiElement4" type="optional" class="outer halfWidth required">
                    <div>
                        <span class="label">
                            <label for="box4" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.NumberTextBox" id="box4" required="true" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                
                <h2>DateTextBox</h2>
                <span id="uiElement5" type="optional" class="outer halfWidth">
                    <div>
                        <span class="label">
                            <label for="box5" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Not required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.DateTextBox" id="box5" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                <span id="uiElement6" type="optional" class="outer halfWidth required">
                    <div>
                        <span class="label">
                            <label for="box6" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.DateTextBox" id="box6" required="true" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                
                <button dojoType="dijit.form.Button" style="float:right;">
                    Validate on surrounding form element
                    <script type="dojo/method" event="onClick" args="evt">
                        alert(dijit.byId("myForm").validate());
                    </script>
                </button>
                <button dojoType="dijit.form.Button" style="float:right;">
                    Fill all valid values
                    <script type="dojo/method" event="onClick" args="evt">
                    </script>
                </button>
                <button dojoType="dijit.form.Button" style="float:right;">
                    Fill partly with valid values
                    <script type="dojo/method" event="onClick" args="evt">
                    </script>
                </button>
                <button dojoType="dijit.form.Button" style="float:right;">
                    Fill with wrong values
                    <script type="dojo/method" event="onClick" args="evt">
                    </script>
                </button>
            </form>
            <form dojoType="dijit.form.Form" id="myForm2">
                <h2>Hidden required field</h2>
                <span id="uiElement10" type="optional" class="outer halfWidth">
                    <div>
                        <span class="label">
                            <label for="box10" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Not required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.DateTextBox" id="box10" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                <span id="uiElement11" type="optional" class="outer halfWidth required" style="display:none;">
                    <div>
                        <span class="label">
                            <label for="box11" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.DateTextBox" id="box11" required="true" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                
                <button dojoType="dijit.form.Button" style="float:right;">
                    Validate on surrounding form element
                    <script type="dojo/method" event="onClick" args="evt">
                        alert(dijit.byId("myForm2").validate());
                    </script>
                </button>
            </form>
            <form dojoType="dijit.form.Form" id="myForm3">
                <h2>Hidden required field</h2>
                <span id="uiElement10" type="optional" class="outer halfWidth">
                    <div>
                        <span class="label">
                            <label for="box12" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Not required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.SimpleTextarea" rows="3" id="box12" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                <span id="uiElement11" type="optional" class="outer halfWidth required" style="display:none;">
                    <div>
                        <span class="label">
                            <label for="box13" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.SimpleTextarea" rows="3" id="box13" required="true" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                
                <button dojoType="dijit.form.Button" style="float:right;">
                    Validate on surrounding form element
                    <script type="dojo/method" event="onClick" args="evt">
                        alert(dijit.byId("myForm3").validate());
                    </script>
                </button>
            </form>
            <form dojoType="dijit.form.Form" id="myForm4">
                <h2>Selectboxes</h2>
                <span id="uiElement14" type="optional" class="outer halfWidth">
                    <div>
                        <span class="label">
                            <label for="box14" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Not required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.Select" id="box14" forceWidth="true" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                <span id="uiElement15" type="optional" class="outer halfWidth required" style="display:none;">
                    <div>
                        <span class="label">
                            <label for="box15" onclick="javascript:dialog.showContextHelp(arguments[0], 1140)">
                                Required field:
                            </label>
                        </span>
                        <span class="input">
                            <input type="text" dojoType="dijit.form.Select" id="box15" required="true" style="width:100%;"/>
                        </span>
                    </div>
                </span>
                
                <button dojoType="dijit.form.Button" style="float:right;">
                    Validate on surrounding form element
                    <script type="dojo/method" event="onClick" args="evt">
                        alert(dijit.byId("myForm4").validate());
                    </script>
                </button>
            </form>
        </div>
    </body>
</html>
