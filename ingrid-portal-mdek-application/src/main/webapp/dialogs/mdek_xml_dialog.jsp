<!--
  **************************************************-
  Ingrid Portal MDEK Application
  ==================================================
  Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
  -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<link rel="stylesheet" href="dojo-sources/ingrid/css/github.css" />

<script type="text/javascript">

    require(["dojo/on",
             "dojo/dom",
             "dojo/dom-style",
             "dojo/dom-attr",
             "dojox/highlight",
             "dojox/highlight/languages/xml",
             "dojox/highlight/languages/html"], function(on, dom, style, domAttr, highlight, xml, html) {
        on(_container_, "Load", function() {
            var uuid = this.customParams.uuid;
            var type = this.customParams.type;
            
            if (type === "O") {
                ObjectService.getIsoXml( uuid, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: handleResult
                } );
            } else if (type === "A") {
                AddressService.getIsoXml( uuid, {
                    preHook: showLoadingZone,
                    postHook: hideLoadingZone,
                    callback: handleResult
                } );
            }
            
        });

        function handleResult(res) {
            if (res) {
                var highlighted = hljs.highlight("xml", res);
                // putting the highlighted code in a html element so you can see
                domAttr.set('xmlContainer', {innerHTML: highlighted.value});
            } else {
                domAttr.set('xmlContainer', {innerHTML: message.get("dialog.xml.")});
            }
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
    <div id="xmlLoadingZone" style="display:none;">
        <img id="imageZone" src="img/ladekreis.gif" />
    </div>
    <pre style="height: 700px; font-size: 12px; overflow: auto;">
        <code id="xmlContainer" style="border: 0;"></code>
    </pre>
</body>
