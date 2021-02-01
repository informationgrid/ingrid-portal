/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
define([
    "dojo/_base/array",
    "dojo/_base/declare",
    "dojo/topic",
    "dijit/registry",
    "ingrid/utils/Syslist",
], function(array, declare, topic, registry, UtilSyslist) {

    // issue: 1720
    return declare(null, {
        title: "Validierung WKT",
        description: "Prüft ob die WKT Angabe des Raumbezugs gültig ist",
        defaultActive: true,
        type: "SYSTEM",
        category: "mcloud",
        run: function() {
            var self = this;
            return topic.subscribe("/onBeforeObjectPublish", function(/*Array*/ notPublishableIDs) {
                self.validateWKT(notPublishableIDs);
            });
        },

        validateWKT: function (notPublishableIDs) {
            var boundingPolygon = registry.byId("boundingPolygon").value;
            if(boundingPolygon && !this.isValidWKT(boundingPolygon.toLowerCase())){
                notPublishableIDs.push(["boundingPolygon", "Begrenzungspolygon muss gültiges WKT enthalten!"]);
            }
        },

         isValidWKT: function (wkt) {
            try {
                var pos1 = wkt.indexOf("(");
                if (pos1 <= 0) {
                    return false
                }

                var type = wkt.substring(0, pos1).trim();
                if (type === "geometrycollection") {
                    /*
                    var geometries = wkt.substring(pos1).trim();
                    if(geometries.startsWith("(") && geometries.endsWith(")")) { geometries.substr(1,-1) }
                    else {return false;}
                    */
                    return false;
                }
                else if(type === "point"){
                    var coordinates = wkt.substring(pos1).trim();
                    var re = /^\(\s*[.0-9]+\s+[.0-9]+\s*\)$/;
                    return re.exec(coordinates)
                }
                else if(type === "linestring" || type === "multipoint"){
                    var coordinates = wkt.substring(pos1).trim();
                    var re = /^\(\s*[.0-9]+\s+[.0-9]+\s*(,\s*[.0-9]+\s+[.0-9]+\s*)*\)$/;
                    return re.exec(coordinates)
                }
                else if(type === "polygon" || type === "multilinestring"){
                    var coordinates = wkt.substring(pos1).trim();
                    var re = /^\(\(\s*[.0-9]+\s+[.0-9]+\s*(,\s*[.0-9]+\s+[.0-9]+\s*)*\)\w*(,\s*\(\s*[.0-9]+\s+[.0-9]+\s*(,\s*[.0-9]+\s+[.0-9]+\s*)*\)\s*)*\)$/;
                    return re.exec(coordinates)
                }
                else if(type === "multipolygon"){
                    var coordinates = wkt.substring(pos1).trim();
                    var re = /^\(\(\(\s*[.0-9]+\s+[.0-9]+\s*(,\s*[.0-9]+\s+[.0-9]+\s*)*\)\s*(,\s*\(\s*[.0-9]+\s+[.0-9]+\s*(,\s*[.0-9]+\s+[.0-9]+\s*)*\))*\)\s*(,\s*\(\(\s*[.0-9]+\s+[.0-9]+\s*(,\s*[.0-9]+\s+[.0-9]+\s*)*\)(,\(\s*[.0-9]+\s+[.0-9]+\s*(,\s*[.0-9]+\s+[.0-9]+\s*)*\)\s*)*\s*\))*\s*\)$/;
                    return re.exec(coordinates)
                }
            } catch (e) {
                return false;
            }
         }

        })();
});

