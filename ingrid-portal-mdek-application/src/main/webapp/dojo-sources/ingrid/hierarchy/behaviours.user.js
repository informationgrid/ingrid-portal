/*
 * **************************************************-
 * InGrid Portal MDEK Application
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
    "dojo/_base/lang",
    "dojo/dom-class",
    "dojo/query",
    "ingrid/hierarchy/behaviours",
    "ingrid/widgets/UvpPhases"
], function (lang, domClass, query, behaviours, UvpPhases) {

    return lang.mixin(behaviours, {

        uvpPhaseField: {
            title: "UVP Phasen Feld",
            description: "Hinzufügen von dynamischen Feldern",
            defaultActive: true,
            run: function () {
                new UvpPhases().placeAt("generalContent");
                // hide all rubrics
                query(".rubric", "contentFrameBodyObject").forEach(function (item) {
                    if (item.id !== "general") {
                        domClass.add(item, "hide");
                    }
                });
            }
        }

    });
});
