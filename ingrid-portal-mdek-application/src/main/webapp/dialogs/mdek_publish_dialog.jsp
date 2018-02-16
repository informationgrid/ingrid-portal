<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2018 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html dir="ltr">

<head>
    <script type="text/javascript">
    var dialogPublish = _container_;

    require([
        "dojo/dom-class",
        "dojo/parser",
        "dijit/form/DateTextBox",
        "dojo/domReady!"
    ], function(domClass) {
        var self = this;
        var usePublicationDate = false;

        // initialize date text box delayed, otherwise an error will occur
        setTimeout(function() {
            datePublish.constraints = {
                selector: 'date',
                min: new Date()
            };
            datePublish.set('value', new Date());
        }, 0);

        dialogPublish.togglePublishInput = function(isChecked) {
            if (isChecked) {
                domClass.remove("publishDateWrapper", "hide");
            } else {
                domClass.add("publishDateWrapper", "hide");
            }
            usePublicationDate = isChecked;
        };

        dialogPublish.cancel = function() {
            dialogPublish.customParams.resultHandler.reject();
        };

        dialogPublish.publish = function() {
            dialogPublish.customParams.resultHandler.resolve(
                usePublicationDate ? datePublish.get('value') : null
            );
        };

        // TODO: show different message
        // var dialogText = this.currentUdk.isMarkedDeleted ? message.get("dialog.object.markedDeleted.finalSaveMessage") : message.get("dialog.object.finalSaveMessage");

    });
    </script>
</head>

<body class="claro">
    <div>
        <fmt:message key="dialog.publish.info" />

        <hr />

        <div class="checkboxContainer">
            <input data-dojo-type="dijit/form/CheckBox"
                   name="publishDateCheck"
                   id="publishDateCheck"
                   onChange="dialogPublish.togglePublishInput(this.checked)" />
            <label class="inActive" for="publishDateCheck"><fmt:message key="dialog.publish.date.check" /></label>
        </div>

        <div id="publishDateWrapper" class="hide">
            <label class="inActive" for="datePublish"><fmt:message key="dialog.publish.date" />:</label>
            <input data-dojo-id="datePublish" type="text" name="datePublish"
                data-dojo-type="dijit/form/DateTextBox" />
        </div>

        <div class="dijitDialogPaneActionBar">
            <button data-dojo-type="dijit/form/Button" type="submit" data-dojo-props="onClick:function(){dialogPublish.cancel();}">
                Cancel
            </button>
            <button data-dojo-type="dijit/form/Button" type="submit" data-dojo-props="onClick:function(){dialogPublish.publish();}">
                OK
            </button>
        </div>
    </div>
</body>
<html>
