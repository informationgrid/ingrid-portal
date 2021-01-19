<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <meta name="author" content="wemove digital solutions"/>
    <meta name="copyright" content="wemove digital solutions GmbH"/>

    <link rel="stylesheet" href="dojo-sources/ingrid/css/github.css"/>

    <script type="text/javascript">

        require(["dojo/on",
            "dojo/Deferred",
            "dojo/promise/all",
            "dojo/_base/lang",
            "dojo/dom-style",
            "dojo/dom-attr",
            "ingrid/message",
            "ingrid/hierarchy/behaviours/DOI/doiExport"
        ], function (on, Deferred, all, lang, domStyle, domAttr, message, doiExport) {
            on(_container_, "Load", function () {
                try {
                    var result = doiExport.run();
                    handleResult(result);
                    setTimeout(function () {
                        _container_.resize();
                    });
                } catch (e) {
                    handleError(e.message);
                } finally {
                    hideLoadingZone();
                }
            });

            function handleResult(res) {
                var highlighted = hljs.highlight("xml", res);
                // putting the highlighted code in a html element so you can see
                domAttr.set("xmlContainer", {innerHTML: highlighted.value});

                var copyText = document.getElementsByClassName("copyfrom")[0];
                copyText.value = res;
            }

            function handleError(error) {
                domAttr.set("xmlError", {innerHTML: error});
                domStyle.set("xmlError", "display", "block")
            }

            function showLoadingZone() {
                domStyle.set("xmlLoadingZone", "display", "block");
            }

            function hideLoadingZone() {
                domStyle.set("xmlLoadingZone", "display", "none");
            }

            function copyToClipboard() {

                /* Get the text field */
                var copyText = document.getElementsByClassName("copyfrom")[0];

                /* Select the text field */
                copyText.select();
                copyText.setSelectionRange(0, 99999); /*For mobile devices*/

                /* Copy the text inside the text field */
                document.execCommand("copy");

            }

            copy = copyToClipboard;

        });
    </script>
</head>

<body>
<div data-dojo-type="dijit/layout/ContentPane">
    <div id="xmlLoadingZone" style="float: right;">
        <img id="imageZone" src="img/ladekreis.gif"/>
    </div>

    <div id="container">
        <button data-dojo-type="dijit/form/Button" onclick="copy()"><fmt:message key="ui.copyClipboard" /></button>
        <textarea class='copyfrom' tabindex='-1' aria-hidden='true' style="position: absolute;left: -9999px;"></textarea>
        <pre style="font-size: 12px; max-height: 600px; overflow: auto">
<code id="xmlContainer" style="border: 0;"></code>
        </pre>
    </div>

    <div id="xmlError" class="error" style="display: none;"></div>
</div>
</body>
