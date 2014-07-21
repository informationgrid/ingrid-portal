
define([
    "dojo/_base/declare",
    "dojo/topic"
], function(declare, topic){
    return declare(null, {
    
        publishAndContinue: function(evt) {
            var params = { abort: false };
            // do some externally defined actions which can abort the closing of the dialog
            console.log("Publishing event: '" + evt + "' with parameter 'abort'");
            topic.publish(evt, [params]);
            return !params.abort;
        }
    })();
});