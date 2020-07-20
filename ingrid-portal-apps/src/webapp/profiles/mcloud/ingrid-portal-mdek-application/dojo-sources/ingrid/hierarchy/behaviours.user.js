/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    "ingrid/hierarchy/behaviours/contactUrl",
    "ingrid/hierarchy/behaviours/mcloud/docTypes",
    "ingrid/hierarchy/behaviours/mcloud/generalModifications",
    "ingrid/hierarchy/behaviours/mcloud/formFields",
    "ingrid/hierarchy/behaviours/mcloud/formFieldsAddress",
    "ingrid/hierarchy/behaviours/mcloud/validatePublisher",
    "ingrid/hierarchy/behaviours/mcloud/validateWKT"
], function(lang, behaviours, contactUrl, docTypes, generalModifications, formFields, addressMods, validatePublisher, validateWKT) {

    return lang.mixin(behaviours, {

        /**
         * Make sure that the communication urls for addresses start with either
         * http:// or https://
         */
        contactUrl: contactUrl,

        /**
         * Define which documents can be created according to the parent.
         * Load new syslists containing UVP object classes and categories.
         */
        mcloudDocumentTypes: docTypes,

        /**
         * Hide unwanted menu items and other general changes to the IGE.
         */
        mcloudGeneralModifications: generalModifications,

        /**
         * Define the behaviour of the root nodes and the initialized folders.
         */
//        mcloudTreeNodes: treeNodes,

        /**
         * Create fields for all the mcloud classes and show only those fields according to the class.
         */
        mcloudFormFields: formFields,

        /**
         * Modify the display of the address form fields.
         */
        mcloudAddressModifications: addressMods,

        // valitation rule to check that publisher exists
        mcloudValidatePublisher: validatePublisher,

        // valitation rule to check that WKT is correct
        mcloudValidateWKT: validateWKT,

        advCompatible : undefined,
        advProductGroup : undefined,
        administrativeArea: undefined,
        conformityFields: undefined,
        inspireGeoservice: undefined,
        inspireIsoConnection: undefined,
        inspireEncodingConnection: undefined,
        inspireConformityConnection: undefined,
        spatialRepresentationInfo: undefined,
        coupledResourceDownloadDataCheck: undefined,
        requireUseConstraints: undefined,
        showFileDescription: undefined,
        encodingSchemeForGeodatasets: undefined,
        dqGriddedDataPositionalAccuracy: undefined,
        openData: undefined

    });
});
