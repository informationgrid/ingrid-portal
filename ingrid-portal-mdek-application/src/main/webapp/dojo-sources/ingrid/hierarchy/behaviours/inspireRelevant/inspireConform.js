/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
    "dojo/_base/declare",
    "dojo/on",
    "dijit/registry",
    "ingrid/hierarchy/behaviours/inspireRelevant/utils"
], function(declare, on, registry, utils) {
    
    return declare(null, {
        title : "INSPIRE konform (Regeln)",
        description : "Wenn aktiviert, ...",
        defaultActive : true,
        category: "INSPIRE relevant",
        run : function() {
            
            on(registry.byId("isInspireConform"), "Click", function(isChecked) {
                
                // automatic selection of ISO-category on INSPIRE topic
                // -> is a separate behaviour (see above: inspireIsoConnection)
                
                // add conformity VO 1089/2010
                utils.addConformity();
                
                // only one of a few CRS are required???
                
                // Kodierungsschema
                
                // neues Metadatenelement "Räumliche Darstellungsart"
                
                // beim Geodatenthema "Bodennutzung"
                
                // beim Geodatenthema "Bewirtschaftungsgebiete, Schutzgebiete, geregelte Gebiete und Berichterstattungseinheiten"
                
            
                    
            });
        }
    })();
});
