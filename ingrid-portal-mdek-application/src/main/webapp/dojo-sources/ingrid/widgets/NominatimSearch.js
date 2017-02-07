
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
    "dijit/form/Button"
    // "ingrid/widgets/leaflet",
    // "ingrid/widgets/leaflet-areaselect"
], function(declare, array, lang, construct, domClass, query, on, request, json, registry, _WidgetBase, _Templated, creator, Button){

    return declare("NominatimSearch", [_WidgetBase, dijit._Templated], {
        
        prefix: "uvp_",
        
        map: null,
        
        areaSelect: null,
        
        collapseOnEmptyInput: true,
        
        templateString: 
            "<span>" +
            "  <span class='outer optional required mapWrapper'>" +
            "    <div data-dojo-attach-point='mapIdNode' class='map'></div>" +
            "    <div class='info'></div>" +
            "    <button data-dojo-type='dijit/form/Button' type='button' data-dojo-attach-event='click:_onAcceptView'>Übernehme Ausschnitt</button>" +
            "    <button data-dojo-type='dijit/form/Button' type='button' data-dojo-attach-event='click:_onClose' class='right'>Schließen</button>" +
            "  </span>" +
            "</span>",
            
        widgetsInTemplate: true,
        
        searchInCountries: ["de","fr","es","at","pl","pt","uk"],
            
        // ATTRIBUTES
        mapId: "uvp_map",
        _setMapIdAttr: { node: "mapIdNode", type: "attribute", attribute: "id" },
            
        postCreate: function(){
            construct.place( creator.createDomTextbox({id: this.prefix + "spatial", name: "Suche nach einem Raumbezug", help: "...", isMandatory: false, visible: "show", style: "width:100%"}), this.domNode, "first" );
            on(registry.byId(this.prefix + "spatial"), "keyup", lang.partial(this._handleSpatialSearch, this));
            
            if (this.collapseOnEmptyInput) {
                query(".mapWrapper", this.domNode).addClass("hide");
            }
        },
        
        startup: function() {
            this.map = L.map(this.mapId).setView([51.505, 8.89], 13);
            L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
            }).addTo(this.map);
            
            this.areaSelect = L.areaSelect({width:100, height:100});
            this.areaSelect.addTo(this.map);

        },
        
        _handleSpatialSearch: function(ref) {
            var self = ref;
            var q = this.get("value");
            
            if (self.collapseOnEmptyInput) {
                if (q.trim() === "") {
                    query(".mapWrapper", self.domNode).addClass("hide");
                    return;
                } else {
                    query(".mapWrapper", self.domNode).removeClass("hide");
                    self.map._onResize();
                }
            }
            
            request("http://nominatim.openstreetmap.org/search/" + q + "?format=json&countrycodes=" + self.searchInCountries).then(
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
                            console.log("target clicked: ", box);
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
            if (this.drawnBBox) this.map.removeLayer( this.drawnBBox );
            
            if (setRectangle) {
                query(".mapWrapper", this.domNode).removeClass("hide");
                this.map._onResize();
                // create an orange rectangle
                this.drawnBBox = L.rectangle(latLonBounds, {color: "#ff7800", weight: 1}).addTo(this.map);
                
                setTimeout(function() { 
                    self.map.fitBounds(latLonBounds);
                    var box = self.drawnBBox._path.getBBox();
                    self.areaSelect.setDimensions({width: box.width, height: box.height});
                }, 100);
                return;
            }
            
            /*
            // remove previously drawn bounding box
            if (this.drawnBBox) this.map.removeLayer( this.drawnBBox );
            
            // create an orange rectangle
            this.drawnBBox = L.rectangle(latLonBounds, {color: "#ff7800", weight: 1}).addTo(this.map);
            */
            
            this.map.fitBounds(latLonBounds);
        },
        
        closeView: function() {
            query(".mapWrapper", this.domNode).addClass("hide");
        },
        
        _onAcceptView: function() {
            var bounds = this.areaSelect.getBounds();
            var self = this;
            this._determineLocationInfo(bounds).then(function(spatialString) {
                self.onData( spatialString );
            });
            this._resetInput();
        },
        
        _onClose: function() {
            this._resetInput();
        },
        
        _resetInput: function() {
            registry.byId(this.prefix + "spatial").set("value", "");
            query(".mapWrapper", this.domNode).addClass("hide");
        },
        
        _determineLocationInfo: function(box) {
            var center = box.getCenter();
            return request("http://nominatim.openstreetmap.org/reverse?format=json&lat=" + center.lat + "&lon=" + center.lng + "&zoom=18&addressdetails=1").then(
                function(data){
                    var results = json.parse(data);
                    console.log("received nominatim reverse data: ", results);
                    var city = results.address.city ? results.address.city : results.address.town ? results.address.town : results.address.village;
                    return results.address.country + ", " + results.address.state + ", " + city + ": " + box.toBBoxString().replace(/,/g, ', ');
                }
            );
        },
        
        // PUBLIC METHODS
        onData: function(bbox, title) {},
        validate: function() { return true; }
    });

 });