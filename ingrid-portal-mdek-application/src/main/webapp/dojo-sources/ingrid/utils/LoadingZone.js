define([
        "dojo/_base/declare",
        "dojo/dom",
        "dojo/dom-style"
    ], function(declare, dom, style){
        return declare(null, {
            show: function(otherLoadingDiv) {
                style.set( dom.byId( otherLoadingDiv ? otherLoadingDiv : "generalLoadingZone" ), "visibility", "visible" );
            },
            
            hide: function(otherLoadingDiv) {
                style.set( dom.byId( otherLoadingDiv ? otherLoadingDiv : "generalLoadingZone" ), "visibility", "hidden" );
            }
        })();
    }
);