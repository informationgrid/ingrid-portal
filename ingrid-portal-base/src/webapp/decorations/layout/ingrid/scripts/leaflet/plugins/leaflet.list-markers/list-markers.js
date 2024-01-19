(function() {

L.Control.ListMarkers = L.Control.extend({

    includes: L.version[0]==='1' ? L.Evented.prototype : L.Mixin.Events,

    options: {
        layer: false,
        collapsed: false,
        label: 'title',
        itemIcon: L.Icon.Default.imagePath+'/marker-icon.png',
        itemArrow: '&#10148;',
        maxZoom: 9,
        position: 'topleft',
        labelZoom: 'Zoom auf Treffer',
        labelDetail: 'Detaildarstellung auf Treffers',
        detailURL: '/trefferanzeige',
        container: null
    },

    initialize: function(options) {
        L.Util.setOptions(this, options);
        this._container = null;
        this._list = null;
        this._layer = this.options.layer || new L.LayerGroup();
    },

    onAdd: function (map) {

        this._map = map;
    
        var container = this._container = L.DomUtil.get(this.options.container);
        this._list = L.DomUtil.create('ul', 'list-markers-ul', container);

        this._initToggle();

        map.on('moveend', this._updateList, this);
            
        this._updateList();

        return container;
    },
    addTo: function (map) {
        this.onRemove(map);
        this._map = map;

        this._container = this.onAdd(map);

        // when adding to the map container, we should stop event propagation
        L.DomEvent.disableScrollPropagation(this._container);
        L.DomEvent.disableClickPropagation(this._container);
        L.DomEvent.on(this._container, 'contextmenu', L.DomEvent.stopPropagation);

        return this;
    },
    onRemove: function(map) {
        map.off('moveend', this._updateList, this);
        this._container = null;
        this._list = null;
    },

    _createItem: function(layer) {

        var li = L.DomUtil.create('li', 'list-markers-li');
        var a = L.DomUtil.create('a', '', li);
        var icon = this.options.itemIcon ? '<img src="'+this.options.itemIcon+'" />' : '';
        var that = this;
        if(layer.options.itemIcon) {
            icon = '<img src="'+layer.options.itemIcon+'"';
            if(layer.options.itemIconWidth || layer.options.itemIconHeight) {
                icon += 'style="';
                if(layer.options.itemIconWidth)
                    icon += 'width:'+layer.options.itemIconWidth+';';
                if(layer.options.itemIconHeight)
                    icon += 'height:'+layer.options.itemIconHeight+';';
                icon += '"';
            }
            icon += ' />';
        }
        a.href = '#';
        L.DomEvent
            .disableClickPropagation(a)
            .on(a, 'click', L.DomEvent.stop, this)
            .on(a, 'click', function(e) {
                that.fire('item-click', {layer: layer });
                //that._addContent(layer);
            }, this)
            .on(a, 'mouseover', function(e) {
                that.fire('item-mouseover', {layer: layer });
            }, this)
            .on(a, 'mouseout', function(e) {
                that.fire('item-mouseout', {layer: layer });
            }, this);

        if( layer.options.hasOwnProperty(this.options.label) ) {
            a.innerHTML = '<span>' + layer.options[this.options.label]+ '</span>' +
            icon;
            a.setAttribute('title', layer.options[this.options.label]);
        } else {
            console.log("propertyName '"+this.options.label+"' not found in marker");
        }

        var controls = L.DomUtil.create('div', 'list-item-controls', li);
        var zoom = L.DomUtil.create('a', '', controls);
        var iconZoom = '<i class="ic-ic-lupe" />';
        zoom.href = '#';
        zoom.title = this.options.labelZoom;
        zoom.innerHTML = iconZoom;

        L.DomEvent
            .disableClickPropagation(zoom)
            .on(zoom, 'click', L.DomEvent.stop, this)
            .on(zoom, 'click', function(e) {
                that.fire('item-zoom-click', {layer: layer });
            }, this);

        var detailLink = L.DomUtil.create('a', '', controls);
        var iconDetail = '<i class="ic-ic-arrow-right" />';
        detailLink.href = this.options.detailURL + '?docuuid=' + layer.options.id;
        detailLink.title = this.options.labelDetail;
        detailLink.target = '_blank';
        detailLink.innerHTML = iconDetail;

        return li;
    },

    _addContent: function(layer) {
        var container = this._container;
        var content = document.getElementsByClassName('list-markers-item-content');
        if(content.length === 0) {
            content = L.DomUtil.create('div', 'list-markers-item-content');
            content.innerHTML = layer.getPopup().getContent();
            container.after(content);
        } else {
            content[0].innerHTML = layer.getPopup().getContent();
        }
    },
    _updateList: function() {
    
        var that = this,
            n = 0,
            listId = [];

        this._list.innerHTML = '';
        this._layer.eachLayer(function(marker) {
            if(marker instanceof L.Marker) {
                if( marker.options.initLatLng && that._map.getBounds().contains(marker.options.initLatLng)) {
                    if (listId.indexOf(marker.options.id) == -1){
                        that._list.appendChild( that._createItem(marker) );
                        listId.push(marker.options.id);
                    }
                    marker.setLatLng(marker.options.initLatLng);
                } else if( that._map.getBounds().contains(marker.getLatLng())) {
                    if (listId.indexOf(marker.options.id) == -1){
                        that._list.appendChild( that._createItem(marker) );
                        listId.push(marker.options.id);
                    }
                } else if (marker.options.features) {
                    var features = marker.options.features.getLayers();
                    if (!marker.options.initLngLat) {
                        marker.options.initLngLat = marker.getLatLng();
                    }
                    features.forEach(function(feature){
                        var mapBounds = that._map.getBounds();
                        if(mapBounds.intersects(feature.getBounds())) {
                            var mapBbox = [
                                mapBounds.getSouthWest().lng, mapBounds.getSouthWest().lat,
                                mapBounds.getNorthEast().lng, mapBounds.getNorthEast().lat
                            ];
                            var mapTurfBbox = turf.bboxPolygon(mapBbox);
                            if(turf.booleanIntersects(mapTurfBbox,feature.feature)) {
                                if (listId.indexOf(marker.options.id) == -1){
                                    that._list.appendChild( that._createItem(marker) );
                                    listId.push(marker.options.id);
                                }
                                if(feature.feature.geometry.type === 'LineString') {
                                    that.lineStringIntersectMarker(that._map, marker, [feature.feature.geometry.coordinates], mapTurfBbox)
                                } else if(feature.feature.geometry.type === 'MultiLineString') {
                                    that.lineStringIntersectMarker(that._map, marker, feature.feature.geometry.coordinates, mapTurfBbox)
                                } else {
                                    if(feature.feature.geometry.geometries) {
                                        that.collectionIntersectMarker(that._map, marker, feature.feature.geometry, mapTurfBbox);
                                    } else {
                                        if(feature.feature.geometry.type === 'Point') {
                                            that.pointIntersectMarker(that._map, marker, [feature.feature.geometry.coordinates], mapTurfBbox)
                                        } else if(feature.feature.geometry.type === 'MultiPoint') {
                                            that.pointIntersectMarker(that._map, marker, feature.feature.geometry.coordinates, mapTurfBbox)
                                        } else {
                                            that.polygonIntersectMarker(that._map, marker, feature.feature.geometry, mapTurfBbox);
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            }
        });
        that.fire('item-updatelist', {layer: layer });
    },
    collectionIntersectMarker: function (map, marker, geometry, mapBbox) {
        var geoms = geometry.geometries;
        var hasFind = false;
        for (i = 0; i < geoms.length; i++) {
            var geom = geoms[i];
            if(geom.type === 'Point') {
                hasFind = this.pointIntersectMarker(map, marker, [geom.coordinates], mapBbox)
            } else if(geom.type === 'MultiPoint') {
                hasFind = this.pointIntersectMarker(map, marker, geom.coordinates, mapBbox)
            } else if(geom.type === 'LineString') {
                hasFind = this.lineStringIntersectMarker(map, marker, [geom.coordinates], mapBbox)
            } else if(geom.type === 'MultiLineString') {
                hasFind = this.lineStringIntersectMarker(map, marker, geom.coordinates, mapBbox)
            } else {
                hasFind = this.polygonIntersectMarker(map, marker, geom, mapBbox);
            }
            if(hasFind){
                break;
            }
        }
    },
    pointIntersectMarker: function (map, marker, coordinates, mapBbox) {
        for (i = 0; i < coordinates.length; i++) {
            var coordinate = coordinates[i];
            var point = turf.point(coordinate);
            if(turf.booleanIntersects(mapBbox,point)) {
                if(!marker.options.initLatLng) {
                    marker.options.initLatLng = marker.getLatLng();
                }
                marker.setLatLng([coordinate[0], coordinate[1]]);
                return true; 
            }
        }
        return false;
    },
    polygonIntersectMarker: function (map, marker, geometry, mapBbox) {
        var intersect = turf.intersect(mapBbox, geometry);
        if(intersect){
            var pointOnFeature = turf.pointOnFeature(intersect);
            if(pointOnFeature) {
                var geom = pointOnFeature.geometry;
                if(!marker.options.initLatLng) {
                    marker.options.initLatLng = marker.getLatLng();
                }
                marker.setLatLng([geom.coordinates[1], geom.coordinates[0]]);
                return true; 
            }
        }
        return false;
    },
    lineStringIntersectMarker: function (map, marker, coordinates, mapBbox) {
        for (i = 0; i < coordinates.length; i++) {
            var coordinate = coordinates[i];
            var split = turf.lineSplit(turf.lineString(coordinate), mapBbox);
            var oddPair;
            if(turf.booleanPointInPolygon(turf.point(coordinate[0]), mapBbox)){
                oddPair = 0;
            } else {
                oddPair = 1;
            }
            var longestSplit;
            var longestDist = 0; 
            for (ii = 0; ii < split.features.length; ii++) {
                var splitedPart = split.features[ii];
                if((ii + oddPair)%2 === 0) {
                    var dist = turf.length(splitedPart, {units: 'meters'});
                    if(longestDist < dist) {
                        longestSplit = splitedPart;
                        longestDist = dist;
                    }
                }
            }
            if(longestSplit) {
                var splitedPart = longestSplit;
                var turfUnitsOption = {
                    units: 'meters'
                };
                var linestringLengh = turf.length(splitedPart.geometry, turfUnitsOption);
                var centerPoint = turf.along(splitedPart.geometry, linestringLengh / 2, turfUnitsOption);
                if(centerPoint.geometry.coordinates){
                    var centerPointCoords = centerPoint.geometry.coordinates;
                    if(!marker.options.initLatLng) {
                        marker.options.initLatLng = marker.getLatLng();
                    }
                    marker.setLatLng([centerPointCoords[1], centerPointCoords[0]]);
                    return true; 
                }
            }
        }
        return false;
    },
    _initToggle: function () {

        var container = this._container;

        container.setAttribute('aria-haspopup', true);

        if (!L.Browser.touch) {
            L.DomEvent
                .disableClickPropagation(container);
        } else {
            L.DomEvent.on(container, 'click', L.DomEvent.stopPropagation);
        }

        if (this.options.collapsed)
        {
            this._collapse();

            if (!L.Browser.android) {
                L.DomEvent
                    .on(container, 'mouseover', this._expand, this)
                    .on(container, 'mouseout', this._collapse, this);
            }
            var link = this._button = L.DomUtil.create('a', 'list-markers-toggle', container);
            link.href = '#';
            link.title = 'List Markers';

            if (L.Browser.touch) {
                L.DomEvent
                    .on(link, 'click', L.DomEvent.stop)
                    .on(link, 'click', this._expand, this);
            }
            else {
                L.DomEvent.on(link, 'focus', this._expand, this);
            }

            this._map.on('click', this._collapse, this);
        }
    },

    _expand: function () {
        this._container.className = this._container.className.replace(' list-markers-collapsed', '');
    },

    _collapse: function () {
        L.DomUtil.addClass(this._container, 'list-markers-collapsed');
    },

    _moveTo: function(latlng) {
        if(this.options.maxZoom)
            this._map.setView(latlng, Math.min(this._map.getZoom(), this.options.maxZoom) );
        else
            this._map.panTo(latlng);
    }
});

L.control.listMarkers = function (options) {
    return new L.Control.ListMarkers(options);
};

L.Map.addInitHook(function () {
    if (this.options.listMarkersControl) {
        this.listMarkersControl = L.control.listMarkers(this.options.listMarkersControl);
        this.addControl(this.listMarkersControl);
    }
});

}).call(this);
