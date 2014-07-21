define([
    "dojo/_base/declare",
    "ingrid/message",
    "ingrid/utils/Address",
    "dojo/_base/lang",
    "dojo/_base/array"
], function(declare, message, UtilAddress, lang, array) {
    return declare(null, {

        // Iterates over an array of objects/addresses/urls and adds an 'icon' property consisting of a html image tag depending on following properties:
        // objectClass = 0..5   -> udk_classx.gif
        // addressClass = 0..3  -> addr_...gif
        // url != undefined     -> url.gif
        //
        // The list is returned
        addIcons: function(list) {
            for (var i = 0; i < list.length; ++i) {
                if (typeof(list[i].objectClass) != "undefined") {
                    list[i].icon = "<div class='TreeIcon TreeIconClass" + list[i].objectClass + "' ></div>";
                } else if (typeof(list[i].addressClass) != "undefined") {
                    switch (list[i].addressClass) {
                        case 0: // Institution
                            //								list[i].icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                            list[i].icon = "<div class='TreeIcon TreeIconInstitution' ></div>";
                            break;
                        case 1: // Unit
                            //								list[i].icon = "<img src='img/UDK/addr_unit.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                            list[i].icon = "<div class='TreeIcon TreeIconInstitutionUnit' ></div>";
                            break;
                        case 2: // Person
                            //								list[i].icon = "<img src='img/UDK/addr_person.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                            list[i].icon = "<div class='TreeIcon TreeIconInstitutionPerson' ></div>";
                            break;
                        case 3: // Free
                            //								list[i].icon = "<img src='img/UDK/addr_free.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                            list[i].icon = "<div class='TreeIcon TreeIconPersonAddress' ></div>";
                            break;
                        default:
                            //								list[i].icon = "<img src='img/UDK/addr_institution.gif' width=\"16\" height=\"16\" alt=\"Address\" />";
                            list[i].icon = "<div class='TreeIcon TreeIconInstitution' ></div>";
                            break;
                    }
                } else if (typeof(list[i].url) != "undefined") {
                    list[i].icon = "<img src='img/UDK/url.gif' width=\"16\" height=\"16\" alt=\"Url\" />";
                } else {
                    list[i].icon = "noIcon";
                }
            }
            return list;
        },

        // Add object link labels to a passed list.
        // This function iterates over all entries in the list and adds a value: 'linkLabel' to each node
        // which is a href to the menuEventHandler 'selectNodeInTree' function
        // If an entry contains the variable 'pubOnly = true', the text color is set to grey
        // showPermission changes the style of the link if there is no write permission
        addObjectLinkLabels: function(list, isDirectLink, showPermission) {
            var addJump = "";
            var disabledClass;
            for (var i = 0; i < list.length; ++i) {
                // jump to specific element
                isDirectLink && list[i].relationType == 3600 ? addJump = ", \"uiElementN003\"" : "";
                !isDirectLink && list[i].relationType == 3600 ? addJump = ", \"uiElement3345\"" : "";

                disabledClass = "";
                if (showPermission) {
                    if (!list[i].writePermission)
                        disabledClass = " disabled";
                }

                if (list[i].pubOnly) {
                    list[i].linkLabel = "<a class='pubOnly" + disabledClass + "' href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\"" + list[i].uuid + "\", \"O\"" + addJump + ");'" +
                        "title='" + list[i].title + "'>" + list[i].title + "</a>";
                } else {
                    list[i].linkLabel = "<a class='" + disabledClass + "' href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\"" + list[i].uuid + "\", \"O\"" + addJump + ");'" +
                        "title='" + list[i].title + "'>" + list[i].title + "</a>";
                }
            }
            return list;
        },

        // Iterates over an array of addresses and adds a property 'linkLabel' depending on the 'uuid' and 'title' of the address.
        // linkLabel is a html href to directly jump to a given address in the main tree
        // showPermission changes the style of the link if there is no write permission
        addAddressLinkLabels: function(list, showPermission) {
            var disabledClass;
            for (var i = 0; i < list.length; ++i) {
                disabledClass = "";
                if (showPermission) {
                    if (!list[i].writePermission)
                        disabledClass = " disabled";
                }
                list[i].linkLabel = "<a class='" + disabledClass + "' href='#' onclick='require(\"ingrid/menu\").handleSelectNodeInTree(\"" + list[i].uuid + "\", \"A\");'" +
                    "title='" + list[i].title + "'>" + list[i].title + "</a>";
            }
            return list;
        },


        // Iterates over an array of urls and adds a property 'linkLabel' depending on the 'url' and 'name' of the url.
        // linkLabel is a html href to the url target
        addUrlLinkLabels: function(list) {
            for (var i = 0; i < list.length; ++i) {
                if (list[i].url) {
                    if (list[i].url.indexOf("http://") === 0)
                        list[i].linkLabel = "<a href='" + list[i].url + "' target=\"_blank\" title='" + list[i].name + "'>" + list[i].name + "</a>";
                    else
                        list[i].linkLabel = list[i].name;
                }
            }
            return list;
        },

        // Iterates over an array and adds the 'displayDate' property to each object depending on the 'date' property (javscript date object)
        addDisplayDates: function(list) {
            if (list) {
                for (var i = 0; i < list.length; ++i) {
                    //						list[i].displayDate = list[i].date.toLocaleString();
                    //list[i].displayDate = dojo.date.format(list[i].date, {formatLength:"short", datePattern:"dd.MM.yyyy", timePattern:"HH:mm"});
                    list[i].displayDate = dojo.date.locale.format(list[i].date, {
                        selector: "date",
                        datePattern: "dd.MM.yyyy",
                        timePattern: "HH:mm"
                    });
                }
                return list;
            } else {
                return [];
            }
        },


        // Creates table data from a list of values.
        // ["a", "b", "c"] -> [{identifier: "a"}, {identifier: "b"}, {identifier: "c"}]
        // @param list, contains strings to be mapped
        // @param identifier, the property of the object to store the arrayfield in
        // @param ignoreEmptyValues, if true then empty values will be filtered
        listToTableData: function(list, identifier, /*boolean*/ ignoreEmptyValues) {
            var resultList = [];
            if (typeof(identifier) == "undefined")
                identifier = "title";

            array.forEach(list, function(item) {
                // if we want to map all values OR we ignore empty values and only add
                // those who actually have a value in it
                if (!ignoreEmptyValues || (item && lang.trim(item).length > 0)) {
                    var x = {};
                    x[identifier] = item;
                    resultList.push(x);
                }
            });
            return resultList;
        },


        //Creates a list from table data
        // [{identifier: "a"}, {identifier: "b"}, {identifier: "c"}] -> ["a", "b", "c"] 
        tableDataToList: function(tableData, identifier) {
            var resultList = [];
            if (typeof(identifier) == "undefined") {
                identifier = "title";
            }

            // when converted back to a JSON object a list becomes a single value
            // if the list just contains one value
            if (lang.isArray(tableData)) {
                for (var i = 0; i < tableData.length; ++i) {
                    resultList.push(tableData[i][identifier]);
                }
            } else {
                resultList.push(tableData[identifier]);
            }

            return resultList;
        },


        // Add Indices (Id values) to a passed list
        addTableIndices: function(list) {
            if (list) {
                for (var i = 0; i < list.length; ++i) {
                    list[i].Id = i;
                }
                return list;
            } else {
                return [];
            }
        },

        // Add SNS Location labels (entry.label = entry.name + ", " + (entry.topicType || entry.type))
        addSNSLocationLabels: function(list) {
            if (list) {
                array.forEach(list, function(entry) {
                    entry.label = entry.name;
                    if (entry.topicType) { // coming from node data
                        entry.label += ", " + entry.topicType;
                    } else if (entry.type && entry.type.length > 1) { // coming from SNSService.autoClassify()
                        entry.label += ", " + entry.type;
                    }
                });
                return list;

            } else {
                return [];
            }
        },

        // Add SNS topic labels (entry.label = entry.alternateTitle + " / " + entry.title)
        addSNSTopicLabels: function(list) {
            if (list) {
                array.forEach(list, function(entry) {
                    entry.label = entry.title;
                    entry.sourceString = entry.source;
                    if (entry.alternateTitle && entry.alternateTitle != entry.title) {
                        // if UMTHES and GEMET is different then show "UMTHES/GEMET"
                        entry.label = entry.alternateTitle + " / " + entry.title;
                        entry.sourceString = "UMTHES/GEMET";
                    }
                });
                return list;

            } else {
                return [];
            }
        },

        // Mark expired SNS locations
        markExpiredSNSLocations: function(list) {
            if (list) {
                array.forEach(list, function(entry) {
                    if (entry.locationExpiredAt != null) {
                        entry.label = "<span style='color:#FF0000'>" + entry.label + "</span>";
                    }
                });
                return list;

            } else {
                return [];
            }
        },

        getSelectDisplayValue: function(selectbox, value) {
            if (value == "") return value;
            var strValue = value + "";
            var dispValue = "";
            //var pos = Array.indexOf(selectbox.values, strValue); // not understood by any but Firefox
            //var pos = selectbox.values.indexOf(strValue); // not understood by IE

            // there are three possible ways how options and values are stored in select box
            if (selectbox.values) {
                var pos = array.indexOf(selectbox.values, strValue);
                if (pos == -1)
                    dispValue = strValue;
                else
                    dispValue = selectbox.options[pos];
            } else if (selectbox.options) {
                array.some(selectbox.options, function(option) {
                    if (option.value == strValue) {
                        dispValue = option.label;
                        return true;
                    }
                });
            } else {
                return "???";
                array.some(selectbox.store._arrayOfTopLevelItems, function(item) {
                    if (item[1] == strValue) {
                        dispValue = item[0];
                        return true;
                    }
                });
            }
            return dispValue;
        },

        getValueForDisplayValue: function(selectbox, displayValue) {
            if (displayValue == "") return displayValue;
            var strDisplayValue = displayValue + "";
            var foundValue = -1;
            array.some(selectbox.options, function(option) {
                if (option.label == strDisplayValue) {
                    foundValue = option.value;
                    return true;
                }
            });
            return foundValue;
        },

        urlToListEntry: function(url) {
            var urlObject = {};
            urlObject.name = "preview-image";
            urlObject.relationType = 9000;
            urlObject.relationTypeName = "Bildvorschau";
            urlObject.url = url;
            urlObject.urlType = 1;
            return urlObject;
        },

        //see dojo.map in newer dojo releases
        map: function(list, myFunction) {
            var outArr = [];
            for (var i = 0, l = list.length; i < l; ++i) {
                outArr.push(myFunction.call(myFunction, list[i], i, list));
            }
            return outArr; // Array
        }

    })();
});