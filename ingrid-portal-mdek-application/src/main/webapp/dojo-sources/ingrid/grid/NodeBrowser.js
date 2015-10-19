define( [
    'ingrid/grid/CustomGrid',
    'ingrid/grid/CustomGridFormatters',
    "ingrid/utils/PageNavigation",
    'ingrid/tree/DwrStore',
    'ingrid/utils/Address',
    'dojo/_base/declare',
    'dojo/_base/lang',
    "dojo/Deferred",
    "dojo/topic",
    'dojo/on',
    'dijit/_Widget',
    'dojo/dom-construct',
    'ingrid/hierarchy/GeneralTopics'], function (CustomGrid, GridFormatters, navigation, DwrStore, UtilAddress, declare, lang, Deferred, topic, on, _Widget, construct) {

    var BreadCrumb = declare( "ingrid.BreadCrumb", _Widget, {
        /**
         * An array of objects with a label and a data container, so that the
         * caller function (navHandler) knows what to do.
         * example: [{label:'Root', data: {id:42}}]
         */
        path: [],

        navHandler: null,
        
        postCreate: function () {
            construct.create( "div", {
                'class': 'breadcrumb',
                innerHTML: 'Root'
            }, this.domNode );
        },

        push: function (value) {
            this.path.push( value );
            this.render();
        },

        popToItem: function (pos) {
            while (this.path.length >= pos+2) {
                this.path.pop();
            }
            this.render();
        },
        
        clear: function() {
            this.path = [];
        },

        render: function () {
            // clear all
            this.domNode.innerHTML = "";

            // render parents
            for (var i = 0; i < this.path.length; i++) {
                if (i != this.path.length - 1) {
                    var link = construct.toDom( '<a href="#">' + this.path[ i ].label + '</a>' );
                    on( link, 'click', lang.partial( this.navHandler, this.path[ i ].data, i ) );
                    this.domNode.appendChild( link );
                    this.domNode.appendChild( construct.toDom( '<span> &gt; </span>' ) );
                }
            }

            // render current node
            this.domNode.appendChild( construct.toDom( this.path[ this.path.length - 1 ].label ) );
        }
    } );
    
    var Filter = declare('ingrid.NodeBrowser._Filter', _Widget, {
        
        filterValue: "",
        
        onChange: null,
        
        postCreate: function () {
            var self = this;
            var input = construct.create( "input", {
                'class': 'filter'
            }, this.domNode );
            
            on( input, 'keyup', function() {
                self.onChange(input.value);
            } );
        }
    });

    return declare( "ingrid.NodeBrowser", CustomGrid, {

        type: 'Objects',
        
        pager: null,
        
        resultsPerPage: 10,

        crumb: null,

        wrapperNode: null,

        columns: [ {
            field: 'nodeDocType',
            name: "&nbsp;",
            width: 25,
            formatter: GridFormatters.renderIconClass
        }, {
            id: 'colFolder',
            field: 'isFolder',
            name: '&nbsp',
            width: 28,
            formatter: GridFormatters.renderFolderIcon
        }, {
            id: 'colParent',
            field: 'parentLink',
            name: '&nbsp',
            width: 25
            //hidden: true
            //formatter: GridFormatters.renderFolderIcon
        }, {
            field: 'title',
            name: 'Titel',
            width: 200
        } ],

        customOptions: {
            headerHeight: 18,
            rowHeight: 22,
            enableCellNavigation: true,
            forceFitColumns: false,
            autoHeight: true,
            visibleRowsInViewport: 20
        },

        store: null,

        postMixInProperties: function () {
            this.store = new DwrStore( {
                data: [],
                storeType: this.type,
                getChildren: function (object) {
                    return this.query( {
                        parent: object.id,
                        nodeAppType: object.nodeAppType
                    } );
                }
            } );

            this.inherited( arguments );
        },

        postCreate: function () {
            // create two children for bread crumb and grid
            var filterContainer = construct.create( "div" );
            var crumbContainer = construct.create( "div" );
            var gridContainer = construct.create( "div" );
            var pagingInfo = construct.create( "span" );
            var pagingPager = construct.create( "span", { style: 'float: right' } );
            construct.place( filterContainer, this.domNode );
            construct.place( crumbContainer, this.domNode );
            construct.place( pagingInfo, this.domNode );
            construct.place( pagingPager, this.domNode );
            construct.place( gridContainer, this.domNode );

            // set the dom node to the grid container to be rendered inside the child
            this.wrapperNode = this.domNode;
            this.domNode = gridContainer;

            this.filter = new Filter({onChange: lang.hitch( this, this.onFilterData )}, filterContainer);
            
            this._initPager( this.resultsPerPage, pagingInfo, pagingPager );
            
            // create bread crumb
            this.crumb = new BreadCrumb( {navHandler: lang.hitch( this, this.onCrumbClick )}, crumbContainer );
            this.openNode();
            

            // create grid
            this.inherited( arguments );
            this.hideColumn( 'colParent' );
        },
        
        _initPager: function(resultsPerPage, infoSpan, pagingSpan ) {
            this.pager = new navigation.PageNavigation({ resultsPerPage: resultsPerPage, infoSpan: infoSpan, pagingSpan: pagingSpan });
        },
        
        _updateData: function(data) {
            this.setData( data );
            this.pager.setTotalNumHits(data.length);
            this.pager.updateDomNodes();
        },
        
        onFilterData: function(value) {
            this.crumb.clear();
            
            if (value === "") {
                this.hideColumn('colParent');
                this.showColumn('colFolder');
                this.openNode();
                return;
            }
            
            this.crumb.push({label: 'Filter', data: {query: value}});
            
            var query = this._prepareQuery(value);
            var startHit = 0;
            var resultsPerPage = 50;
            var self = this;
            QueryService.queryHQL(query, startHit, resultsPerPage, {
                //preHook: showLoadingZone,
                //postHook: hideLoadingZone,
                callback: function(res) { 
                    var data = self._prepareResult(res, self);
                    self.hideColumn('colFolder');
                    self.showColumn('colParent');
                    lang.hitch(self, lang.partial(self._updateData, data))();
                },
                errorHandler: function(errMsg, err) {
                    displayErrorMessage(err);
                }
            });
        },

        _prepareQuery: function(value) {
            var query = null;
            
            if (this.type === "Objects") {
                query = "FROM ObjectNode oNode JOIN oNode.t01ObjectWork obj WHERE obj.objName LIKE '%"+value+"%'";
            } else {
                // TODO: exclude login user
                // TODO: also search by organisation/institution
                query = "FROM AddressNode aNode JOIN aNode.t02AddressWork addr WHERE " +
                		"addr.firstname LIKE '%"+value+"%'" + 
                		"OR addr.lastname LIKE '%"+value+"%'";
            }
            return query;
        },
        
        _prepareResult: function(res, self) {
            var data = null;
            if (self.type === "Objects") {
                data = res.objectSearchResult.resultList;
            } else {
                data = res.addressSearchResult.resultList;
                // prepare title for grid
                for (var i=0; i<data.length; i++) {
                    data[i].title = UtilAddress.createAddressTitle(data[i]);
                }
            }
            
            // generate link to parent for tree view
            for (var i=0; i<data.length; i++) {
                data[i].parentLink = "<a href='#' title='Zum übergeordneten Element' onclick='require(\"dijit\/registry\").byId(\""+this.id+"\").jumpToNode(\"" + data[i].parentUuid + "\")'>&lt;</a>";
            }
            
            return data;
        },
        
        onCrumbClick: function (item, pos) {
            var self = this;
            console.log( 'Navigate crumb:', item );
            // if we click on a filter in the crumb then reexecute filter
            if (item.query) {
                this.onFilterData( item.query );

            } else {
                this.store.getChildren( item ).then( function (data) {
                    lang.hitch(self, lang.partial(self._updateData, data))();
                } );
                this.crumb.popToItem( pos );
            }
        },

        jumpToNode: function(uuid) {
            var self = this;
            var node = this.store._getRootNode();
            node.id = uuid === "null" ? node.id : uuid;
            
            this.hideColumn('colParent');
            this.showColumn('colFolder');
            
            //this.crumb.push( {label: item.title, data: item} );
            this.store.getChildren( node ).then( function (data) {
                lang.hitch(self, lang.partial(self._updateData, data))();
            } );
            this._getPathToNode(uuid).then(function(path) {
                // set bread crumb
                console.log(path);
                var nodeAppType = self.type === 'Objects' ? 'O' : 'A';
                var rootNode = self.store._getRootNode();
                //self.crumb.clear();
                self.crumb.push({label: rootNode.title, data: rootNode});
                for (var i=0; i<path.length; i++) {
                    self.crumb.push({label: path[i], data: {id: path[i], nodeAppType: nodeAppType }});
                }
            });
        },
        
        _getPathToNode: function(uuid) {
            var getPathDef = new Deferred();
            
            if (uuid === 'null') {
                getPathDef.resolve( [] );
                
            } else {
                
                if (this.type == "Objects") {
                    topic.publish("/getObjectPathRequest", {
                        id: uuid,
                        resultHandler: getPathDef
                    });
    
                } else {
                    topic.publish("/getAddressPathRequest", {
                        id: uuid,
                        resultHandler: getPathDef
                    });
                }
            }
            return getPathDef;
        },
        
        openNode: function (row) {
            var self = this;
            var node = this.store._getRootNode();
            var item = node;
            if (row !== undefined) {
                item = this.getDataItem( row );
                node.id = item.id;
            }
            this.crumb.push( {label: item.title, data: item} );
            this.store.getChildren( node ).then( function (data) {
                lang.hitch(self, lang.partial(self._updateData, data))();
            } );
        }
    } );
} );