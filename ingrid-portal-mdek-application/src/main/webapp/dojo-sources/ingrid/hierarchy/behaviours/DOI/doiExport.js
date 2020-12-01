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

define([
    "dojo/_base/declare",
    "dojo/dom-construct",
    "dojo/_base/array",
    "ingrid/message"
], function (declare, construct, array, message) {
    return declare(null, {

        run: function () {

            var wrapper = this.addHeader();

            this.getIdentifier(wrapper);
            this.getCreators(wrapper);
            this.getTitles(wrapper);
            this.getPublisher(wrapper);
            this.getPublicationYear(wrapper);
            // this.getSubjects(wrapper);
            this.getContributors(wrapper);
            this.addDates(wrapper);
            this.getLanguage(wrapper);
            this.getResourceType(wrapper);
            this.getAlternateIdentifiers(wrapper);
            this.getRelatedIdentifiers(wrapper);
            // this.getSizes(wrapper);
            // this.getFormats(wrapper);
            this.getVersion(wrapper);
            this.getRightsList(wrapper);
            this.getDescriptions(wrapper);
            this.getGeoLocations(wrapper);
            // this.getFundingReferences(wrapper);

            var result = this.getXmlHeader() + wrapper.outerHTML;
            return this.formatXml(result);

        },

        getXmlHeader: function () {

            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

        },

        addHeader: function () {
            return this.create("resource", {
                "xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
                "xmlns": "http://datacite.org/schema/kernel-4",
                "xsi:schemaLocation": "http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4.1/metadata.xsd"
            });

        },

        getIdentifier: function (parent) {

            this.create("identifier", {
                identifierType: "DOI",
                innerHTML: this.getDOIValue()
            }, parent);

        },

        getCreators: function (parent) {

            var authors = array.filter(currentUdk.generalAddressTable, function (address) {
                return address.typeOfRelation === 11
            });

            if (authors.length > 0) {
                var creators = this.create("creators", null, parent);
                for (var i=0; i<authors.length; i++) {
                    this.addAddressInfo("creator", authors[i], null, creators);
                }
            } else {
                throw new Error(message.get("doi.error.noAuthor"));
            }

        },

        getTitles: function (parent) {

            var titles = this.create("titles", null, parent);
            titles.appendChild(this.create("title", {innerHTML: currentUdk.objectName}));
            if (currentUdk.generalShortDescription) {
                titles.appendChild(this.create("title", {innerHTML: currentUdk.generalShortDescription}));
            }

        },

        getPublisher: function (parent) {

            var publisher = array.filter(currentUdk.generalAddressTable, function (address) {
                return address.typeOfRelation === 10
            });
            if (publisher.length === 1) {
                this.create("publisher", {innerHTML: this.getNameFromAddress(publisher[0])}, parent);
            } else {
                throw new Error(message.get("doi.error.noPublisher"));
            }

        },

        getPublicationYear: function (parent) {
            var publications = array.filter(currentUdk.timeRefTable, function (date) {
                return date.type === 2;
            });
            if (publications.length > 0) {
                this.create("publicationYear", {innerHTML: publications[0].date.getFullYear()}, parent);
            } else {
                throw new Error(message.get("doi.error.noPublicationDate"));
            }
        },

        /*getSubjects: function(parent) {

            var subjects = this.create("subjects");

        },*/

        getContributors: function (parent) {

            var addresses = array.filter(currentUdk.generalAddressTable, function (address) {
                return address.typeOfRelation !== 10 && address.typeOfRelation !== 11
            });

            if (addresses.length > 0) {
                var contributors = this.create("contributors", null, parent);
                for (var i = 0; i < addresses.length; i++) {
                    this.addAddressInfo("contributor", addresses[i], "Other", contributors);
                }
                return contributors;
            }

        },

        addDates: function (parent) {


            var dates = array.filter(currentUdk.timeRefTable, function (date) {
                return date.type !== 2;
            });
            if (dates.length > 0) {
                var element = this.create("dates", null, parent);
                for (var i = 0; i < dates.length; i++) {
                    this.create("date", {
                        dateType: dates[i].type === 1 ? "Created" : "Updated",
                        innerHTML: dates[i].date.toISOString().substring(0, 10)
                    }, element);
                }
            }

        },

        getLanguage: function (parent) {

            var containsGerman = array.some(currentUdk.extraInfoLangDataTable, function (lang) {
                return lang === 150
            });

            return this.create("language", {innerHTML: containsGerman ? "de-DE" : "en-US"}, parent);

        },

        getResourceType: function (parent) {

            var attributes = {
                resourceTypeGeneral: this.getDOIType().value
            }
            if (this.getDOIType().listId === "-1") {
                attributes.resourceTypeGeneral = "Other";
                attributes.innerHTML = this.getDOIType().value;
            }

            return this.create("resourceType", attributes, parent);

        },

        getAlternateIdentifiers: function (parent) {

            this.create("alternateIdentifiers", null, parent)
                .appendChild(
                    this.create("alternateIdentifier", {
                        alternateIdentifierType: "UUID",
                        innerHTML: currentUdk.uuid
                    }));

        },

        getRelatedIdentifiers: function (parent) {

            if (currentUdk.ref2PublishedISBN) {
                this.create("relatedIdentifiers", null, parent)
                    .appendChild(
                        this.create("relatedIdentifier", {
                            relatedIdentifierType: "ISBN",
                            relationType: "Describes",
                            innerHTML: currentUdk.ref2PublishedISBN
                        }));
            }

        },

        getVersion: function (parent) {

            return this.create("version", {innerHTML: "4.1"}, parent);

        },

        getRightsList: function (parent) {

            // currentUdk.availabilityAccessConstraints
            // currentUdk.availabilityUseConstraints // single
            var constraints = currentUdk.availabilityUseAccessConstraints;

            if (constraints.length > 0) {
                var element = this.create("rightsList", null, parent);
                for (var i = 0; i < constraints.length; i++) {
                    this.create("rights", {
                        innerHTML: constraints[i].title
                    }, element);
                }
                return element;
            }

        },

        getDescriptions: function (parent) {

            var element = this.create("descriptions", null, parent);
            var description = currentUdk.generalDescription;
            this.create("description", {
                descriptionType: "Abstract",
                innerHTML: description
            }, element);

        },

        getGeoLocations: function (parent) {

            var element = this.create("geoLocations", null, parent);
            var freeSpatial = currentUdk.spatialRefLocationTable;
            var spatialRefs = currentUdk.spatialRefAdminUnitTable;
            var allSpatialRefs = spatialRefs.concat(freeSpatial);
            for (var i = 0; i < allSpatialRefs.length; i++) {
                this.create("geoLocation", null, element)
                    .appendChild(this.create("geoLocationPlace", {innerHTML: allSpatialRefs[i].name})).parentNode
                    .appendChild(this.create("geoLocationBox")
                        .appendChild(this.create("westBoundLongitude", {innerHTML: allSpatialRefs[i].longitude1})).parentNode
                        .appendChild(this.create("eastBoundLongitude", {innerHTML: allSpatialRefs[i].longitude2})).parentNode
                        .appendChild(this.create("southBoundLatitude", {innerHTML: allSpatialRefs[i].latitude1})).parentNode
                        .appendChild(this.create("northBoundLatitude", {innerHTML: allSpatialRefs[i].latitude2})).parentNode)
            }

            // TODO: handle WKT

            return element;

        },

        addAddressInfo: function (addressType, address, role, parent) {
            var fullName = this.getNameFromAddress(address);
            var type = null;
            if (role) {
                type = {};
                type[addressType + "Type"] = role;
            }
            var wrapper = this.create(addressType, type, parent);
            this.create(addressType + "Name", {
                nameType: address.addressClass === 2 ? "Personal" : "Organizational",
                innerHTML: fullName
            }, wrapper);

            if (address.addressClass === 2) {
                this.create("givenName", {innerHTML: address.givenName}, wrapper);
                this.create("familyName", {innerHTML: address.name}, wrapper);
            }
            // .parentNode.appendChild(this.create("nameIdentifier")).parentNode
            // .appendChild(this.create("affiliation"));


        },

        create: function (tag, attributes, parent) {
            return construct.create(document.createElementNS(null, tag), attributes, parent);
        },

        getNameFromAddress: function (publisher) {

            return publisher.addressClass === 0
                ? publisher.organisation
                : publisher.name + ", " + publisher.givenName;

        },

        getDOIValue: function () {
            var doi = array.filter(currentUdk.additionalFields, function (field) {
                return field.identifier === "doiId"
            });
            if (doi.length === 1) {
                return doi[0].value;
            } else {
                throw new Error(message.get("doi.error.noDOIId"));
            }
        },

        getDOIType: function () {
            var doiType = array.filter(currentUdk.additionalFields, function (field) {
                return field.identifier === "doiType"
            });
            if (doiType.length === 1) {
                return doiType[0];
            } else {
                throw new Error(message.get("doi.error.noDOIType"));
            }
        },

        formatXml: function (xml) {
            var reg = /(>)\s*(<)(\/*)/g; // updated Mar 30, 2015
            var wsexp = / *(.*) +\n/g;
            var contexp = /(<.+>)(.+\n)/g;
            xml = xml.replace(reg, '$1\n$2$3').replace(wsexp, '$1\n').replace(contexp, '$1\n$2');
            var formatted = '';
            var lines = xml.split('\n');
            var indent = 0;
            var lastType = 'other';
            // 4 types of tags - single, closing, opening, other (text, doctype, comment) - 4*4 = 16 transitions
            var transitions = {
                'single->single': 0,
                'single->closing': -1,
                'single->opening': 0,
                'single->other': 0,
                'closing->single': 0,
                'closing->closing': -1,
                'closing->opening': 0,
                'closing->other': 0,
                'opening->single': 1,
                'opening->closing': 0,
                'opening->opening': 1,
                'opening->other': 1,
                'other->single': 0,
                'other->closing': -1,
                'other->opening': 0,
                'other->other': 0
            };

            for (var i = 0; i < lines.length; i++) {
                var ln = lines[i];

                // Luca Viggiani 2017-07-03: handle optional <?xml ... ?> declaration
                if (ln.match(/\s*<\?xml/)) {
                    formatted += ln + "\n";
                    continue;
                }
                // ---

                var single = Boolean(ln.match(/<.+\/>/)); // is this line a single tag? ex. <br />
                var closing = Boolean(ln.match(/<\/.+>/)); // is this a closing tag? ex. </a>
                var opening = Boolean(ln.match(/<[^!].*>/)); // is this even a tag (that's not <!something>)
                var type = single ? 'single' : closing ? 'closing' : opening ? 'opening' : 'other';
                var fromTo = lastType + '->' + type;
                lastType = type;
                var padding = '';

                indent += transitions[fromTo];
                for (var j = 0; j < indent; j++) {
                    padding += '  ';
                }
                if (fromTo === 'opening->closing')
                    formatted = formatted.substr(0, formatted.length - 1) + ln + '\n'; // substr removes line break (\n) from prev loop
                else
                    formatted += padding + ln + '\n';
            }

            return formatted;
        }
    })();
});
