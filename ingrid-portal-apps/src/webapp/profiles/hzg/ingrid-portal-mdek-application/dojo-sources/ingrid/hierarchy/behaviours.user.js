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
    "dojo/_base/lang",
    "ingrid/hierarchy/behaviours",
    "ingrid/hierarchy/behaviours/hzg/hzgUiGeneral",
    "ingrid/hierarchy/behaviours/hzg/platformLinkType",
    "ingrid/hierarchy/behaviours/sensorml/sensorObjectClass",
    "ingrid/hierarchy/behaviours/sensorml/sensorUI"
    ], function(lang, behaviours, hzgUiGeneral, platformLinkType, platform, sensorUI) {

    return lang.mixin(behaviours, {
        hzgUiGeneral: hzgUiGeneral,

        // Link Type syslist
        platformLinkType: platformLinkType,

        // Plattform-Metadaten
        observationPlatform: platform,
        sensorUI: sensorUI
    });
});
