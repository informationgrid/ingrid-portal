dojo.provide("ingrid.dijit.CustomDialog");

dojo.require("dijit.Dialog");
dojo.require("dojox.layout.ContentPane");

dojo.declare("ingrid.dijit.CustomDialog", [dojox.layout.ContentPane, dijit._DialogBase], {
    _setup: function(){
        // summary:
        //      Stuff we need to do before showing the Dialog for the first
        //      time (but we defer it until right beforehand, for
        //      performance reasons).
        // tags:
        //      private

        var node = this.domNode;
        var _this = this;

        if(this.titleBar && this.draggable){
            this._moveable = (dojo.isIE == 6) ?
                new dojo.dnd.TimedMoveable(node, { handle: this.titleBar }) :   // prevent overload, see #5285
                new dojo.dnd.Moveable(node, { handle: this.titleBar, timeout: 0 });
            dojo.subscribe("/dnd/move/stop",this,"_endDrag");
        }else{
            dojo.addClass(node,"dijitDialogFixed");
        }

        this.underlayAttrs = {
            dialogId: this.id,
            "class": dojo.map(this["class"].split(/\s/), function(s){ return s+"_underlay"; }).join(" ")
        };

        this._fadeIn = {
            play: function() {
                var underlay = dijit._underlay;
                if(!underlay){
                    underlay = dijit._underlay = new dijit.DialogUnderlay(_this.underlayAttrs);
                }else{
                    underlay.set(_this.underlayAttrs);
                }
    
                var ds = dijit._dialogStack,
                    zIndex = 948 + ds.length*2;
                if(ds.length == 1){ // first dialog
                    underlay.show();
                }
                dojo.style(dijit._underlay.domNode, 'zIndex', zIndex);
                dojo.style(_this.domNode, 'zIndex', zIndex + 1);
                dojo.style(_this.domNode, 'opacity', "1");
            },
            stop: function() {},
            status: function() {return null;}
        },
        
        this._fadeOut = {
            play: function() {
                _this.domNode.style.display = "none";
                
                // Restore the previous dialog in the stack, or if this is the only dialog
                // then restore to original page
                var ds = dijit._dialogStack;
                if(ds.length == 0){
                    dijit._underlay.hide();
                }else{
                    dojo.style(dijit._underlay.domNode, 'zIndex', 948 + ds.length*2);
                    dijit._underlay.set(ds[ds.length-1].underlayAttrs);
                }
    
                // Restore focus to wherever it was before this dialog was displayed
                // !!! comment this to save 200ms in IE
                if(_this.refocus){
                    var focus = _this._savedFocus;
    
                    // If we are returning control to a previous dialog but for some reason
                    // that dialog didn't have a focused field, set focus to first focusable item.
                    // This situation could happen if two dialogs appeared at nearly the same time,
                    // since a dialog doesn't set it's focus until the fade-in is finished.
                    if(ds.length > 0){
                        var pd = ds[ds.length-1];
                        if(!dojo.isDescendant(focus.node, pd.domNode)){
                            pd._getFocusItems(pd.domNode);
                            focus = pd._firstFocusItem;
                        }
                    }
    
                    dijit.focus(focus);
                }
            },
            stop: function() {},
            status: function() {return null;}
        }
    },
    
    _position: function(){
        this.inherited(arguments);
        
        // check if top-position is not negative
        if (dojo.style(this.domNode, 'top') < 0)
            dojo.style(this.domNode, 'top', 0);
    }

});