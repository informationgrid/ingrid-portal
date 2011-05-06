dojo.provide("ingrid.dijit.tree.LazyTreeStoreModel");

dojo.require("dijit.tree.ForestStoreModel");

dojo.declare("ingrid.dijit.tree.LazyTreeStoreModel",dijit.tree.ForestStoreModel, 
	{
        newItemsAvailable: false,
        
        manualTreeNotification: false,
        
		mayHaveChildren: function(/*dojo.data.Item*/item){
			if (item.isFolder == "true") {
				return true;
			}
		},
        
        onNewItem: function(/* dojo.data.Item */item, /* Object */ parentInfo){
            // do not handle the notification of the tree here
            // it's better to inform the tree when all children are added to the store
            // so that tree only is informed once
            if (this.manualTreeNotification) {
                this.newItemsAvailable = true;
                return;
            } else {
                this.inherited(arguments);
            }
        },
        
        notfiyOnNewChildren: function(parentInfo, def){
            var _this = this;
            this._requeryTop();
            _this.getChildren(parentInfo.item, dojo.hitch(this, function(children){
                _this.onChildrenChange(parentInfo.item, children);
                def.callback();
            }));
            this.newItemsAvailable = false;
        }
        
	}

);