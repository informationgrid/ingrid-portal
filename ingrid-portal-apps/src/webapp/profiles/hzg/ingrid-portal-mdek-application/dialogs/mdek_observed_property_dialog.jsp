<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Observed Property</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<script type="text/javascript">

    var dialogObservedProperty = null;

    require([
        "dojo/_base/array",
        "dojo/_base/lang",
        "dojo/on",
        "dojo/keys",
        "dojo/dom",
        "dojo/promise/all",
        "dojo/topic",
        "dijit/registry",
        "dijit/form/CheckBox",
        "ingrid/layoutCreator",
        "ingrid/utils/Grid",
        "ingrid/utils/Events"
    ], function(array, lang, on, keys, dom, all, topic, registry, CheckBox, layoutCreator, UtilGrid, UtilEvents) {

            var caller = {};
            var dialog = null;
            on(_container_, "Load", function() {
                dialog = this;
                if (this.customParams) {
                    caller = this.customParams;
                }

                /*
                createDOMElements()
                    .then(init);
                */
            });

            function createDOMElements() {
                var promises = [];
                promises.push(layoutCreator.createDomTextbox({
                    id: "observedPropertyNameInDialog",
                    style: "width: 100%",
                    //name: "Name", // TODO localisation
                    help: "Name of the observed property",
                    visible: 'show'
                }));

                promises.push(layoutCreator.createDomTextarea({
                    id: "observedPropertyXmlDescInDialog",
                    style: "width: 100%",
                    rows: 10,
                    //name: "XML Description", // TODO localisation
                    help: "XML Description",
                    visible: 'show'
                }));

                return all(promises);
            }

            function init() {
            }

            function submit() {
                _container_.hide();
            }

            function cancel() {
                _container_.hide();
            }


            dialogObservedProperty = {
                cancel: cancel,
                submit: submit
            };
    });
</script>
</head>

<body>
    <div id="observedPropertyContentPane" data-dojo-type="dijit/layout/ContentPane">
        <!-- CONTENT START -->
        <div class="inputContainer">
            <span class="outer required">
                <div>
                    <span class="label">
                        <label for="observedPropertyNameInDialog">
                            <fmt:message key="dialog.observedProperty.name" />*
                        </label>
                    </span>
                    <span class="input">
                        <input id="observedPropertyNameInDialog" style="width: 100%" required="true" data-dojo-type="dijit/form/ValidationTextBox" />
                    </span>
                </div>
            </span>
            <span class="outer required">
                <div style="height: 200px;">
                    <span class="label">
                        <label for="observedPropertyXmlDescInDialog">
                            <fmt:message key="dialog.observedProperty.xmlDescription" />*
                        </label>
                    </span>
                    <span class="input">
                        <input type="text" mode="textarea" id="observedPropertyXmlDescInDialog" class="textAreaFull" data-dojo-type="dijit/form/SimpleTextarea" />
                    </span>
                </div>
            </span>
            <div class="fill"></div>
        </div>
        <!-- CONTENT END -->
        <div style="margin: 0px; padding-right: 10px; padding-bottom: 10px;">
            <div class="dijitDialogPaneActionBar">
                <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogObservedProperty.submit();}" id="ok"><fmt:message key="general.ok" /></button>
                <button data-dojo-type="dijit/form/Button" type="button" data-dojo-props="onClick:function(){dialogObservedProperty.cancel();}" id="cancel"><fmt:message key="general.cancel" /></button>
            </div>
        </div>
    </div>

</body>
</html>
