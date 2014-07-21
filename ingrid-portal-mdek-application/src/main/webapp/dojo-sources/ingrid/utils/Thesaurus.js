define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/_base/array",
    "dojo/string",
    "dojo/Deferred",
    "ingrid/message",
    "ingrid/dialog",
    "ingrid/utils/List",
    "ingrid/utils/UI",
    "ingrid/utils/Grid"
], function(declare, lang, array, string, Deferred, message, dialog, UtilList, UtilUI, UtilGrid) {

        return declare(null, {

            // parse a string and extract a list of terms.
            // whitespace and newlines are used as delimeters, composite terms are enclosed in double quotes
            // e.g the input:
            // Wasser Umwelt
            // "Inhalierbarer Feinstaub" Atomkraft
            // results in:
            // ["Wasser", "Umwelt", "Inhalierbarer Feinstaub", "Atomkraft"]
            parseQueryTerm: function(queryTerm) {
                var resultTerms = [];

                // Iterate over all characters in the query term
                // 1. If we read a newline, store the current term and continue
                // 2. If we read a whitespace and are not currently in a composite term (term enclosed in "),
                //    then store the current term and continue
                // 3. If we read double quote("), we have to check if we are at the start or end of a composite term
                //    If we are at the end, write the term and switch the flag
                //    Otherwise simply switch the flag
                // All other chars are handled as valid query chars
                // In the end we have to write the remainder from currentTerm since the query string doesn't always end
                // with a newline or whitespace

                // Helper function to only add valid terms (no empty strings) to the list 
                var addTermToResultList = function(term) {
                    var trimmedTerm = lang.trim(term); // "." are not transferred correctly when using regex! dojo.regexp.escapeString(term));
                    if (trimmedTerm && trimmedTerm.length !== 0) {
                        resultTerms.push(trimmedTerm);
                    }
                };

                var currentTerm = "";
                var readingCompositeTerm = false;
                for (var index = 0; index < queryTerm.length; index++) {
                    if (queryTerm.substr(index, 1) == "\n" || (queryTerm.substr(index, 1) == " " && !readingCompositeTerm)) {
                        addTermToResultList(currentTerm);
                        currentTerm = "";
                    } else if (queryTerm.substr(index, 1) == "\"") {
                        if (readingCompositeTerm) {
                            addTermToResultList(currentTerm);
                            currentTerm = "";
                        }
                        readingCompositeTerm = !readingCompositeTerm;
                    } else if (queryTerm.substr(index, 1) == "," && !readingCompositeTerm) {
                        // ignore comma if it's not within a phrase
                    } else {
                        currentTerm += queryTerm.substr(index, 1);
                    }
                }
                addTermToResultList(currentTerm);

                return resultTerms;
            },

            // Callback to find sns topics for a given topic (SNSService.findTopcis)
            findTopicsDef: function(term) {
                var def = new Deferred();
                SNSService.findTopics(term, userLocale, {
                    callback: function(res) {
                        UtilList.addSNSTopicLabels(res);
                        UtilUI.updateBlockerDivInfo("keywords");
                        def.resolve(res);
                    },
                    errorHandler: function(errMsg, err) {
                        console.debug("Error while calling findTopics: " + errMsg);
                        def.errback(err);
                    }
                });
                return def;
            },

            // Main function to analyze and add the found topic results to the store given in 'store'
            handleFindTopicsResult: function(termList, snsTopics, grid) {
                // termList is the list of input terms ('tokenized' user input)
                // snsTopics is a list of SNS findTopic results. The first entry in snsTopcis is the result for the
                // first term in termList and so on...

                // 1. Check if one of the input terms is an INSPIRE topic. If so, add it to the INSPIRE topic list.
                //    Show an info dialog with the INSPIRE topics that were added (TODO not implemented yet)
                // 2. Add all descriptors to the list of search terms. No dialog needed.
                // 3. If the descriptor is a synonym, add all non-descriptors to the list of search terms.
                //    Ask the user if the corresponding descriptor should be added to the list of search terms as well
                // 4. Add all other terms from termList that were not found (or are of type TOP_TERM / NODE_LABEL)

                var def = new Deferred();
                def.resolve();
                console.debug("term list: " + termList);
                var self = this;

                var isDescriptor = function(t) { return t.type == "DESCRIPTOR"; };
                var isNonDescriptor = function(t) { return t.type == "NON_DESCRIPTOR"; };
                var convertToLowerCase = function(t) { return t.title.toLowerCase() == queryTerm.toLowerCase(); };

                for (var index = 0; index < termList.length; ++index) {
                    var queryTerm = termList[index];
                    var curSnsTopics = snsTopics[index];

                    console.debug("query term: " + queryTerm);

                    if (curSnsTopics && curSnsTopics.length !== 0) {
                        // Try to find the queryTerm in the list of sns terms first
                        var snsTopicsEqualToTerm = array.filter(curSnsTopics, convertToLowerCase);
                        if (snsTopicsEqualToTerm.length !== 0) {
                            // Check if one of the found terms is a descriptor / nonDescriptor
                            var snsDescriptorsEqualToTerm = array.filter(snsTopicsEqualToTerm, isDescriptor);
                            var snsNonDescriptorsEqualToTerm = array.filter(snsTopicsEqualToTerm, isNonDescriptor);

                            if (snsDescriptorsEqualToTerm.length !== 0) {
                                // If the term was found as a descriptor, add all descriptors to the search term list 
                                self.addDescriptors(snsDescriptorsEqualToTerm, grid);

                            } else if (snsNonDescriptorsEqualToTerm.length !== 0) {
                                // otherwise, if the term was found as a synonym
                                // Add it to the list as a free term and ask the user if the corresponding descriptor(s)
                                // should be added as well

                                // create a closure with fixed arguments. Otherwise we end up adding only the last
                                // nonDescriptor since snsNonDescriptorsEqualToTerm gets overwritten in every iteration
                                // see test_delayedFunctions.jsp for more information
                                (function(fixedDescs, fixedStore) {
                                    def = def.then(function() {
                                        return self.addNonDescriptorsDef(fixedDescs, fixedStore);
                                    });
                                })(snsNonDescriptorsEqualToTerm, grid);

                            } else {
                                // Found topic is of type TOP_TERM or NODE_LABEL. Simply add it to the list as a free term
                                self.addFreeTerm(queryTerm, grid);
                            }

                            // add synonyms for inspire if available
                            def = def.then(function() {
                                return self.addInspireSynonyms(snsTopicsEqualToTerm);
                            });
                            //def.addCallback(function() { return addInspireSynonyms(curSnsTopics); });

                        } else {
                            // Results were returned by the SNS that did not contain the queryTerm
                            // e.g. ('Inhalierbarer' results in 'Inhalierbarer Feinstaub' and 'PM10')
                            // Add the search term as free term
                            self.addFreeTerm(queryTerm, grid);
                        }

                    } else {
                        // No results were returned by the sns. Add the queryTerm as free term to the search term list
                        self.addFreeTerm(queryTerm, grid);
                    }

                }
            },

            // if there's a term that's a synonym to an inspire topic then add it to the list in case the user
            // acjnowledges it
            addInspireSynonyms: function(curSnsTopics) {
                //var inspireList; 
                //dojo.debug("SNS Topics length: " + curSnsTopics.length);
                var def = new Deferred();
                def.resolve();
                var self = this;
                array.forEach(curSnsTopics, function(snsTopic) {
                    //dojo.debug("InspireList: " + snsTopic.inspireList.length); 
                    if (snsTopic.inspireList.length > 0) {
                        def.then(function() {
                            var closeDialogDef = new Deferred();
                            var displayText = string.substitute(message.get("dialog.addInspireTopics.question"), [snsTopic.title, snsTopic.inspireList.join(",")]);
                            dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.INFO, [{
                                caption: message.get("general.no"),
                                action: function() {
                                    closeDialogDef.resolve();
                                    return;
                                }
                            }, {
                                caption: message.get("general.yes"),
                                action: function() {
                                    closeDialogDef.resolve();
                                    self.addInspireTopics(snsTopic.inspireList);
                                    return;
                                }
                            }]);
                            return closeDialogDef;
                        });
                    }
                });
                return def;
            },

            //Add a list of non descriptors (synonyms) store if they don't already exist
            // Ask the user (popup dialog) if the corresponding descriptors should be added as well
            addNonDescriptorsDef: function(nonDescriptors, grid) {
                var deferred = new Deferred();
                deferred.resolve();
                //deferred.addErrback(handleFindTopicsError);

                array.forEach(nonDescriptors, function(d) {
                    //console.debug("add non descriptor: " + snsTopicToString(d));
                    this.addFreeTerm(d.label, grid);

                    deferred.addCallback(function() {
                        return this.getTopicsForTopicDef(d.topicId);
                    });
                    deferred.addCallback(function(topic) {
                        var closeDialogDef = new Deferred();

                        //console.debug("Add descriptor for synonym '" + snsTopicToString(d) + "' ? " + snsTopicToString(topic));
                        //var descriptors = topic.descriptors;
                        UtilList.addSNSTopicLabels(topic.descriptors);

                        // Show the dialog
                        var displayText = string.substitute(message.get("dialog.addDescriptors.message"), [d.label, dojo.map(topic.descriptors, function(item) {
                            return item.title;
                        })]);
                        //			var displayText = "Synonym: " + snsTopicToString(d) + " Deskriptor: " + snsTopicToString(topic);
                        dialog.show(message.get("dialog.addDescriptor.title"), displayText, dialog.INFO, [{
                            caption: message.get("general.no"),
                            action: function() {
                                closeDialogDef.resolve();
                                return;
                            }
                        }, {
                            caption: message.get("general.ok"),
                            action: function() {
                                closeDialogDef.resolve();
                                this.addDescriptors(topic.descriptors, grid);
                            }
                        }]);

                        return closeDialogDef;
                    });

                }, this);
                return deferred;
            },

            // Add a 'free' term to store if it doesn't already exist
            addFreeTerm: function(queryTerm, grid) {
                console.debug("add free term: " + queryTerm);
                if (array.every(UtilGrid.getTableData(grid), function(item) {
                    if (!item.title) return true;
                    return item.title.toLowerCase() != queryTerm.toLowerCase();
                })) {
                    // If every term in store is != to the queryTerm, add it
                    UtilGrid.addTableDataRow(grid, {
                        label: queryTerm,
                        title: queryTerm,
                        source: "FREE",
                        sourceString: "FREE"
                    });
                }
            },

            //Add a list of descriptors to store if they don't already exist
            addDescriptors: function(descriptors, grid) {
                array.forEach(descriptors, function(d) {
                    //console.debug("add descriptor: " + snsTopicToString(d));
                    if (array.every(UtilGrid.getTableData(grid), function(item) {
                        return item.title != d.title;
                    })) {
                        // If every topicId in store is != to the topicId of the descriptor, add it
                        UtilGrid.addTableDataRow(grid, d);
                    }
                });
            },
            // !!!!!!!!!! use above one!
            addDescriptorsToGrid: function(descriptors, grid) {
                array.forEach(descriptors, function(d) {
                    //console.debug("add descriptor: " + snsTopicToString(d));
                    if (array.every(grid.getData(), function(item) {
                        return item.title != d.title;
                    })) {
                        // If every topicId in store is != to the topicId of the descriptor, add it
                        UtilGrid.addTableDataRow(grid, d);
                    }
                });
            },

            //Callback to find an sns descriptor for a synonym (nonDescriptor)
            getTopicsForTopicDef: function(topicId) {
                var def = new Deferred();
                SNSService.getTopicsForTopic(topicId, userLocale, {
                    preHook: UtilUI.enterLoadingState,
                    postHook: UtilUI.exitLoadingState,
                    callback: function(res) {
                        UtilList.addSNSTopicLabels([res]);
                        def.resolve(res);
                    },
                    errorHandler: function(errMsg, err) {
                        dojo.debug("Error while calling getTopicsForTopic: " + errMsg);
                        def.errback(err);
                    }
                });
                return def;
            },

            /* ---- Helper functions for the free terms search button ---- */

            getInspireTopicId: function(topic) {
                var id = null;
                array.some(sysLists[6100], function(list) {
                    id = list[1];
                    return list[0] == topic;
                });
                return id;
            },

            // get an array of Inspire topics
            getInspireTopics: function(topics) {
                var inspireArray = [];
                array.forEach(topics, function(topic) {
                    if (topic.inspireList.length > 0) {
                        array.forEach(topic.inspireList, function(inspireTopic) {
                            // exclude multiple same entries
                            if (!this.inspireArrayContains(inspireArray, inspireTopic)) {
                                var obj = {};
                                obj.title = inspireTopic;
                                obj.label = inspireTopic;
                                obj.source = "INSPIRE";
                                inspireArray.push(obj);
                            }
                        }, this);
                    }
                }, this);
                return inspireArray;
            },

            // check if term is in Inspire Syslist
            isInspireTopic: function(topic) {
                var topicLow = topic.toLowerCase();
                var result = null;
                array.some(sysLists[6100], function(list) {
                    if (list[0].toLowerCase() == topicLow) {
                        result = list[0];
                        return true;
                    } else return false;
                });
                return result;
            },

            //Add inspire topics to the inspire table if they don't already exist
            // inspireTopics: Array of Strings
            addInspireTopics: function(inspireTopics) {
                try {
                    array.forEach(inspireTopics, function(t) {
                        var inspireEntryId = this.getInspireTopicId(t);
                        // add it only if there's a valid value (here are NO free entries!)
                        if (inspireEntryId !== null) {
                            console.debug("adding entry: [" + inspireEntryId + ", " + t + "]");
                            // if item does not already exist in table 
                            if (array.every(UtilGrid.getTableData("thesaurusInspire"), function(item) {
                                return item.title != inspireEntryId;
                            })) {
                                UtilGrid.addTableDataRow("thesaurusInspire", {
                                    title: inspireEntryId
                                });
                            }
                        } else {
                            dojo.debug(t + " is not a valid INSPIRE topic!");
                            var displayText = dojo.substitute(message.get("dialog.addInspireTopics.error"), [t]);
                            dialog.show(message.get("dialog.addInspireTopics.title"), displayText, dialog.WARNING);
                        }
                    }, this);
                } catch (error) {
                    console.debug("error: " + error);
                    console.debugShallow(error);
                }
            }
        })();
    });