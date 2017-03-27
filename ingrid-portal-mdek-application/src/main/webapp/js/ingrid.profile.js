var profile = (function(){
    return {
        resourceTags: {
            amd: function(filename, mid) {
                if (/leaflet.js$/.test(filename) || /leaflet-areaselect.js$/.test(filename) || /diff.js$/.test(filename) || /fullwebjslint.js$/.test(filename) || /highlight.js$/.test(filename)) return false;
                return /\.js$/.test(filename);
            }
        }
    };
})();