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
        position: 'bottomleft'
    },

    initialize: function(options) {
        L.Util.setOptions(this, options);
        this._container = null;
        this._list = null;
        this._layer = this.options.layer || new L.LayerGroup();
    },

    onAdd: function (map) {

        this._map = map;
    
        var container = this._container = L.DomUtil.create('div', 'list-markers');
        this._list = L.DomUtil.create('ul', 'list-markers-ul', container);

        this._initToggle();

        map.on('moveend', this._updateList, this);
            
        this._updateList();

        return container;
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

        if( layer.options.hasOwnProperty(this.options.label) )
        {
            a.innerHTML = '<span>'+layer.options[this.options.label]+'</span> <b>'+this.options.itemArrow+'</b>' + icon;
            a.setAttribute('title', layer.options[this.options.label]);
        
        }
        else
            console.log("propertyName '"+this.options.label+"' not found in marker");
        
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
            n = 0;

        this._list.innerHTML = '';
        this._layer.eachLayer(function(layer) {
            if(layer instanceof L.Marker)
                if( that._map.getBounds().contains(layer.getLatLng())) {
                    that._list.appendChild( that._createItem(layer) );
                } else if (layer.options.features) {
                    var features = layer.options.features.getLayers();
                    features.forEach(function(feature){
                        if(that._map.getBounds().intersects(feature.getBounds())) {
                            var latLngs = feature.getLatLngs();
                            latLngs.every(function(latLng){
                                if(latLng.lat && latLng.lng){
                                    if( that._map.getBounds().contains(latLng)) {
                                        that._list.appendChild( that._createItem(layer) );
                                        return false;
                                    }
                                    return true;
                                } else {
                                    var contains = false;
                                    latLng.every(function(childLatLng) {
                                        if(childLatLng.lat && childLatLng.lng){
                                            if( that._map.getBounds().contains(childLatLng)) {
                                                that._list.appendChild( that._createItem(layer) );
                                                contains = true;
                                                return false;
                                            }
                                        }
                                        return true;
                                    });
                                    return !contains;
                                }
                            });
                        }
                    })
                }
        });
        that.fire('item-updatelist', {layer: layer });
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
