var profile = (function(){
    var copyOnly = function(filename, mid){
        var list = {
            // "ingrid/IgeActions": true,
            // "ingrid/IgeEvents": true,
            // "ingrid/MenuActions": true
        };
        return (mid in list) || /(png|jpg|jpeg|gif|tiff)$/.test(filename);
    };
    return {
        resourceTags: {

            copyOnly: function(filename, mid){
                return copyOnly(filename, mid);
            },

            amd: function(filename, mid) {
                return /\.js$/.test(filename) && !copyOnly(filename, mid);
            }
        }
    };
})();