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
    "ingrid/hierarchy/behaviours/baw_doi/bawDoiPublicationRestriction",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisAuftrag",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisCodelists",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisDefaultValues",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisHierarchyLevelName",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisKeywordCatalogue",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisLiterature",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisMessdaten",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisNewObjectTypes",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisSimulationMdFields",
    "ingrid/hierarchy/behaviours/baw_mis/bawMisWaterwaysTable",
    "ingrid/hierarchy/behaviours/baw_mis/bawUiGeneral",
    "ingrid/hierarchy/behaviours/baw_mis/bawValidationRules"
], function(lang, behaviours,  bawDoiPublicationRestriction, bawMisAuftrag, bawMisCodelists,
            bawMisDefaultValues, bawMisHierarchyLevelName, bawMisKeywordCatalogue, bawMisLiterature, bawMisMessdaten,
            bawMisNewObjectTypes, bawMisSimulationMdFields, bawMisWaterwaysTable, bawUiGeneral, bawValidationRules) {

    return lang.mixin(behaviours, {

        /**
         * Allow publication of objects only for Cat-admin
         */
        bawDoiPublicationRestriction: bawDoiPublicationRestriction,

        /**
         * BAW-Auftragsinformationen
         */
        bawMisAuftrag: bawMisAuftrag,

        /**
         * Codelist changes for BAW-MIS profile
         */
        bawMisCodelists: bawMisCodelists,

        /**
         * Default values for certain fields
         */
        bawMisDefaultValues: bawMisDefaultValues,

        /**
         * heirarchyLevelName Field
         */
        bawMisHierarchyLevelName: bawMisHierarchyLevelName,

        /**
         * BAW-Keyword Catalogue (2012)
         */
        bawMisKeywordCatalogue: bawMisKeywordCatalogue,

        /**
         * Allowed object types in New Object dialog box
         */
        bawMisNewObjectTypes: bawMisNewObjectTypes,

        /**
         * IGE fields for simulation-related metadata
         */
        bawMisSimulationMdFields: bawMisSimulationMdFields,

        /**
         * IGE-Fields for measurement data
         */
        bawMisMessdaten: bawMisMessdaten,

        /**
         * New table for federal waterways
         */
        bawMisWaterwaysTable: bawMisWaterwaysTable,

        /**
         * UI changes for BAW-MIS profile
         */
        bawUiGeneral: bawUiGeneral,

        /**
         * Literature class and cross-references
         */
        bawMisLiterature: bawMisLiterature,

        /**
         * Extra validation rules for BAW-MIS
         */
        bawValidationRules: bawValidationRules

    });
});

