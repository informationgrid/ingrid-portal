/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
    "dojo/_base/array", 
    "dojo/_base/lang",
    "dojo/dom-construct",
    "dojo/dom-class",
    "dojo/query",
    "dojo/on",
    "dojo/request",
    "dojo/json",
    "dijit/registry",
    "dijit/_WidgetBase",
    "dijit/_Templated",
    "ingrid/layoutCreator",
    "ingrid/message"
    // "ingrid/widgets/leaflet",
    // "ingrid/widgets/leaflet-areaselect"
], function(declare, array, lang, construct, domClass, query, on, request, json, registry, _WidgetBase, _Templated, creator, message){

    return declare("NominatimSearch", [_WidgetBase, dijit._Templated], {
        
        prefix: "uvp_",
        
        map: null,
        
        areaSelect: null,
        
        collapseOnEmptyInput: true,

        scrollWheelZoom: true,

        hideOnStartup: false,
        
        templateString: 
            "<span>" +
            "  <span class='outer optional required mapWrapper no-search'>" +
            "    <div data-dojo-attach-point='mapIdNode' class='map'></div>" +
            "    <div class='info'></div>" +
            "    <button data-dojo-type='dijit/form/Button' type='button' data-dojo-attach-event='click:_onAcceptView' class='right'>Übernehme Ausschnitt</button>" +
            // "    <button data-dojo-type='dijit/form/Button' type='button' data-dojo-attach-event='click:_onClose' class='right'>Schließen</button>" +
            "  </span>" +
            "</span>",
            
        widgetsInTemplate: true,
        
        searchInCountries: ["de","fr","es","at","pl","pt","uk"],
            
        // ATTRIBUTES
        mapId: "uvp_map",
        _setMapIdAttr: { node: "mapIdNode", type: "attribute", attribute: "id" },
            
        postCreate: function(){
            construct.place( creator.createDomTextbox({id: this.prefix + "spatial", name: message.get("widget.spatialSearch"), help: message.get("widget.spatialSearch.helpMessage"), isMandatory: false, visible: "show", style: "width:100%"}), this.domNode, "first" );
            on(registry.byId(this.prefix + "spatial"), "keyup", lang.partial(this._handleSpatialSearch, this));
            
            if (!this.collapseOnEmptyInput) {
                query(".mapWrapper", this.domNode).removeClass("no-search");
            }
            if (this.hideOnStartup) {
                domClass.add(this.domNode, "hide");
            }
        },
        
        startup: function() {
            this.map = L.map(this.mapId, {
                scrollWheelZoom: this.scrollWheelZoom
            }).setView([51.505, 8.89], 13);
            // add URL for tiles (by removing the protocol, the protocol will be chosen automatically)
            L.tileLayer('//{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors'
            }).addTo(this.map);
            
            this.areaSelect = L.areaSelect({width:100, height:100});
            this.areaSelect.addTo(this.map);

        },
        
        _handleSpatialSearch: function(ref) {
            var self = ref;
            var q = this.get("value");
            
            if (self.collapseOnEmptyInput) {
                if (q.trim() === "") {
                    // query(".mapWrapper", self.domNode).addClass("hide");
                    // TODO: refactor into a function "expandMap"
                    query(".mapWrapper", self.domNode).addClass("no-search");
                    self.map._onResize();
                    return;
                } else {
                    // query(".mapWrapper", self.domNode).removeClass("hide");
                    // TODO: refactor into a function "shrinkMap"
                    query(".mapWrapper", self.domNode).removeClass("no-search");
                    self.map._onResize();
                }
            }
            
            request("https://nominatim.openstreetmap.org/search/" + q + "?format=json&countrycodes=" + self.searchInCountries).then(
                function(data){
                    var results = json.parse(data);
                    console.log("received data: ", results);
                    if (results[0]) {
                        self._zoomToBoundingBox(results[0].boundingbox);
                    }
                    
                    var info = query(".mapWrapper .info")[0];
                    
                    // clear old results
                    info.innerHTML = "";
                    
                    // append new results
                    array.forEach(results, function(result) {
                        info.appendChild( construct.toDom("<div class='item' data-bounding-box='"+result.boundingbox+"'>" + result.display_name + "</div>") );
                    });
                    
                    if (results.length === 0) {
                        info.appendChild( construct.toDom("<div class='item'>keine Ergebnisse</div>") );
                    } else {
                        query(".mapWrapper .info .item:first-child").addClass("active");
                        query(".mapWrapper .info .item").on("click", function(evt) {
                            var box = evt.target.getAttribute("data-bounding-box");
                            self._zoomToBoundingBox(box.split(','));
                            // TODO: adapt area select
                        });
                    }
                }, function(err){
                    console.error("Error querying nominatim search.", err);
                }
            );
        },
        
        _zoomToBoundingBox: function(box, setRectangle) {
            var self = this;
            var latLonBounds = new L.LatLngBounds( [ box[0], box[2] ], [ box[1], box[3] ] );
            
            // remove previously drawn bounding box
            // this._removeBoundingBox();
            
            if (setRectangle) {
                // query(".mapWrapper", this.domNode).removeClass("hide");
                // this.map._onResize();
                // create an orange rectangle
                this._drawMarkerForBoundingBox(latLonBounds);
                
                setTimeout(function() { 
                    self.map.fitBounds(latLonBounds);
                    var box = self.drawnBBox._path.getBBox();
                    self.areaSelect.setDimensions({width: box.width, height: box.height});
                }, 100);
                return;
            }
            
            this.map.fitBounds(latLonBounds);
        },

        _drawMarkerForBoundingBox: function(latLonBounds) {
            this._removeBoundingBox();
            this.drawnBBox = L.rectangle(latLonBounds, {color: "#ff7800", weight: 1}).addTo(this.map);
        },
        
        _removeBoundingBox: function() {
            if (this.drawnBBox) this.map.removeLayer( this.drawnBBox );
        },

        _onAcceptView: function() {
            var bounds = this.areaSelect.getBounds();
            var self = this;
            this._determineLocationInfo(bounds).then(function(spatialString) {
                self.onData( spatialString );
            });
            this._resetInput();
            this._drawMarkerForBoundingBox(bounds);
        },
        
        _onClose: function() {
            this._resetInput();
            this.onClose();
        },
        
        _resetInput: function() {
            registry.byId(this.prefix + "spatial").set("value", "");
            if (this.collapseOnEmptyInput) {
                query(".mapWrapper", this.domNode).addClass("no-search");
                this.map._onResize();
            }
        },
        
        _determineLocationInfo: function(box) {
            var center = box.getCenter();
            var self = this;
            return request("https://nominatim.openstreetmap.org/reverse?format=json&lat=" + center.lat + "&lon=" + center.lng + "&zoom=18&addressdetails=1").then(
                function(data){
                    var results = json.parse(data);
                    console.log("received nominatim reverse data: ", results);
                    var city = results.address.city ? results.address.city : results.address.town ? results.address.town : results.address.village;
                    return results.address.country + ", " + 
                        results.address.state + ", " +
                        city + ": " + self._formatBoundingBox(box);
                }
            );
        },

        _formatBoundingBox: function(box) {
            var southWest = box.getSouthWest();
            var northEast = box.getNorthEast();
            var coords = [
                southWest.lng.toFixed(4),
                southWest.lat.toFixed(4),
                northEast.lng.toFixed(4),
                northEast.lat.toFixed(4)
            ];
            return coords.join(", ");
        },
        
        // PUBLIC METHODS
        onData: function(bbox, title) {},
        onClose: function() {},
        validate: function() { return true; }
    });

 });
