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
    "ingrid/hierarchy/behaviours/bkg/general",
    "ingrid/hierarchy/behaviours/bkg/useConstraintsField",
    "ingrid/hierarchy/behaviours/bkg/modifyOldUseConstraintsField",
    "ingrid/hierarchy/behaviours/bkg/opendata"
], function(lang, behaviours, general, useConstraintsField, oldUseField, openData) {

    return lang.mixin(behaviours, {

        /**
         * 
         */
        bkgGeneral: general,

        /**
         * 
         */
        bkgNewUseConstraintsField: useConstraintsField,

        /**
         * 
         */
        bkgOldUseConstraintsField: oldUseField,

        /**
         *
         */
        openData: openData
    });
});
