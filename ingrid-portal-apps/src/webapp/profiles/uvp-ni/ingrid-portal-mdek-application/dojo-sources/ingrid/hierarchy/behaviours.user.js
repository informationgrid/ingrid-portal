/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
    "dojo/_base/lang",
    "ingrid/hierarchy/behaviours",
    "ingrid/hierarchy/behaviours/uvp/initialFolders",
    "ingrid/hierarchy/behaviours/uvp/docTypes",
    "ingrid/hierarchy/behaviours/uvp/generalModifications",
    "ingrid/hierarchy/behaviours/uvp/treeNodes",
    "ingrid/hierarchy/behaviours/uvp/formFields",
    "ingrid/hierarchy/behaviours/uvp/addressMods"
], function(lang, behaviours, initialFolders, docTypes, generalModifications, treeNodes, formFields, addressMods) {

    return lang.mixin(behaviours, {

        /**
         * Create initial folders on first startup and set a flag in the database.
         */
        uvpInitialFolders: initialFolders,

        /**
         * Define which documents can be created according to the parent.
         * Load new syslists containing UVP object classes and categories.
         */
        uvpDocumentTypes: docTypes,

        /**
         * Hide unwanted menu items and other general changes to the IGE.
         */
        uvpGeneralModifications: generalModifications,

        /**
         * Define the behaviour of the root nodes and the initialized folders.
         */
        uvpTreeNodes: treeNodes,

        /**
         * Create fields for all the uvp classes and show only those fields according to the class.
         */
        uvpPhaseField: formFields,

        /**
         * Modify the display of the address form fields.
         */
        uvpAddressModifications: addressMods

    });
});
