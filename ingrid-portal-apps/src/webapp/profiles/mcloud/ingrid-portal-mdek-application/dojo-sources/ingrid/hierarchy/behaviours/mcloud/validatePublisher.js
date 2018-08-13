/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
    "ingrid/utils/Syslist",
], function(array, declare, topic, UtilSyslist) {

    // issue: 1720
    return declare(null, {
        title: "Pflichteingabe Herausgeber",
        description: "Stellt sicher, dass Herausgeber definiert ist.",
        defaultActive: true,
        type: "SYSTEM",
        category: "mcloud",
        run: function() {
            return topic.subscribe("/onBeforeObjectPublish", function(/*Array*/ notPublishableIDs) {
                // get name of codelist entry for entry-id "10" = "publisher"/"Herausgeber"
                var entryNamePublisher = UtilSyslist.getSyslistEntryName(505, 10);

                // check if entry already exists in table
                var data = UtilGrid.getTableData("generalAddress");
                var containsPublisher = array.some(data, function(item) { if (item.nameOfRelation == entryNamePublisher) return true; });
                if (!containsPublisher)
                    notPublishableIDs.push(["generalAddress", "Es muss ein Herausgeber als Adresse angegeben sein."]);
            });
        }
    })();
});

