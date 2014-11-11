var profile = (function(){
    return {
        resourceTags: {
            amd: function(filename, mid) {
                if (/diff.js$/.test(filename) || /fullwebjslint.js$/.test(filename)) return false;
                return /\.js$/.test(filename);
            }
        }
    };
})();