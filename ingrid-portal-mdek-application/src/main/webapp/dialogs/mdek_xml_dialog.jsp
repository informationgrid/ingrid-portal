<%--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<link rel="stylesheet" href="dojo-sources/release/lib/ingrid/css/github.css" />

<script type="text/javascript">

    require(["dojo/on",
             "dojo/dom",
             "dojo/Deferred",
             "dojo/promise/all",
             "dojo/_base/lang",
             "dojo/dom-style",
             "dojo/dom-attr",
             "ingrid/message"], function(on, dom, Deferred, all, lang, style, domAttr, message) {
        on(_container_, "Load", function() {
            var uuid = this.customParams.uuid;
            var type = this.customParams.type;
            var def1 = new Deferred();
            var def2 = new Deferred();
            
            if (type === "O") {
                ObjectService.getIsoXml( uuid, true, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: lang.partial(handleResult, 'xmlContainerPublished', def1)
                } );
                ObjectService.getIsoXml( uuid, false, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: lang.partial(handleResult, 'xmlContainerWorking', def2)
                } );
            } else if (type === "A") {
                AddressService.getIsoXml( uuid, true, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: lang.partial(handleResult, 'xmlContainerPublished', def1)
                } );
                AddressService.getIsoXml( uuid, false, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: lang.partial(handleResult, 'xmlContainerWorking', def2)
                } );
            }
            var self = this;
            all([def1, def2]).then(function(results) {
                // if a published version exists
                if (results[0]) {
                    var diff = WDiffString( results[0], results[1] );
                    domAttr.set("xmlDiffContainer", {innerHTML: diff});
                } else {
                    domAttr.set("xmlDiffContainer", {innerHTML: message.get("dialog.xml.noPublishedVersion")});
                }
	            self.resize();
            });
            
        });

        function handleResult(container, def, res) {
            if (res) {
                var highlighted = hljs.highlight("xml", res);
                // putting the highlighted code in a html element so you can see
                domAttr.set(container, {innerHTML: highlighted.value});
            } else {
                if (container === "xmlContainerPublished") {
                    domAttr.set(container, {innerHTML: message.get("dialog.xml.noPublishedVersion")});
                } else {
                    domAttr.set(container, {innerHTML: message.get("dialog.xml.problem.generation")});
                }
            }
            def.resolve(res);
        }
        
        function showLoadingZone() {
            style.set( "xmlLoadingZone", "display", "block" );
        }
        
        function hideLoadingZone() {
            style.set( "xmlLoadingZone", "display", "none" );
        }
        
    });
</script>
</head>

<body>
	<div data-dojo-type="dijit/layout/ContentPane">
	    <div id="xmlLoadingZone" style="float: right;">
	        <img id="imageZone" src="img/ladekreis.gif" />
	    </div>
	    <!-- MAIN TAB CONTAINER START -->
	    <div data-dojo-type="dijit/layout/TabContainer" selectedChild="xmlWorkingVersion" style="height:800px; width:100%;" >
	        <!-- TAB 1 START -->
	        <div id="xmlWorkingVersion" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.xml.version.working" />">
	            <pre style="font-size: 12px;"><code id="xmlContainerWorking" style="border: 0;"></code></pre>
	        </div>
	        <!-- TAB 2 START -->
	        <div id="xmlPublishedVersion" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.xml.version.published" />">
	            <pre style="font-size: 12px;"><code id="xmlContainerPublished" style="border: 0;"></code></pre>
	        </div>
	        <!-- TAB 3 START -->
	        <div id="xmlDiffVersion" data-dojo-type="dijit/layout/ContentPane" title="<fmt:message key="dialog.xml.version.diff" />">
	            <pre style="font-size: 12px;"><code id="xmlDiffContainer" style="border: 0;"></code></pre>
	            <div id="diffContentLegend" class="inputContainer field grey">
	                <span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #009933;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.insertedText" /><br/>
	                <span style="font-weight: normal; text-decoration: none; color: #ffffff; background-color: #990033;">&nbsp;&nbsp;&nbsp;&nbsp;</span> - <fmt:message key="dialog.compare.deletedText" /><br/>
	                <fmt:message key="dialog.compare.otherColor" /> - <fmt:message key="dialog.compare.movedText" /></div>
	            </div>
	        </div>
	    </div>
	</div>
</body>
