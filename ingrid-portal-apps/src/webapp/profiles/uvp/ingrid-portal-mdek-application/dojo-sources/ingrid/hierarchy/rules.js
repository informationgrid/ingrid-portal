/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
/*
 * Special rules for form items
 */

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// removed stuff not needed anymore
// see svn log, "CLEAN UP: REMOVED NOT NEEDED JAVASCRIPT FROM rules_*.js"
// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

// NOTICE: Most of these functions are "called from" Profile XML !

define(["dojo/_base/declare", "dojo/_base/array", "dojo/Deferred", "dojo/_base/lang", "dojo/dom-style", "dojo/topic", "dojo/query", "dojo/on", "dojo/aspect", "dojo/dom", "dojo/dom-class",
    "dijit/registry", "dojo/cookie",
    "ingrid/message", "ingrid/dialog",
    "ingrid/utils/Grid", "ingrid/utils/UI", "ingrid/utils/List", "ingrid/utils/Syslist"
], function(declare, array, Deferred, lang, style, topic, query, on, aspect, dom, domClass, registry, cookie, message, dialog, UtilGrid, UtilUI, UtilList, UtilSyslist) {

    var Rules = declare(null, {

        openDataLinkCheck: null,

        COOKIE_HIDE_OPEN_DATA_HINT: "ingrid.open.data.hint",

        applyRule1: function() {},

        applyRule2: function() {},

        applyRule3: function(value) {},

        applyRule5: function() {},

        // If one of the fields contains data, all fields are mandatory
        applyRule6: function() {},


        // If INSPIRE theme make additional fields mandatory
        applyRule7: function() {},

        applyRuleThesaurusInspire: function() {},

        applyRuleServiceType: function() {},

        _updateAtomFieldVisibility: function(value) {},

        applyRuleDownloadService: function() {}

    })();

    // add global function objects for backward compatibility
    applyRuleThesaurusInspire = Rules.applyRuleThesaurusInspire;
    applyRule1 = Rules.applyRule1;
    applyRule2 = Rules.applyRule2;
    applyRule3 = Rules.applyRule3;
    applyRule5 = Rules.applyRule5;
    applyRule6 = Rules.applyRule6;
    applyRule7 = lang.hitch(Rules, Rules.applyRule7); // method has this reference!

    return Rules;
});
