/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
define([
    "dojo/_base/lang",
    "ingrid/hierarchy/behaviours",
    "ingrid/hierarchy/behaviours/nokis/docTypes",
    "ingrid/hierarchy/behaviours/nokis/geometryContext",
    "ingrid/hierarchy/behaviours/nokis/thesaurus"
], function(lang, behaviours, docTypes, geometryContext, thesaurus) {

    return lang.mixin(behaviours, {


        /**
         * Define which documents can be created according to the parent.
         * Load new syslists containing Nokis stuff.
         */
        nokisDocumentTypes: docTypes,
        geometryContext: geometryContext,
        thesaurus: thesaurus

    });
});
