<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>Tables Test</title>

        <script type="text/javascript">
            dojo.require("dijit.Menu");
            dojo.require("dijit.form.CheckBox");
            
            // **************************
            // this does not work because of cross site scripting!!!
            // **************************
            function getTerms() {
                //Look up the node we'll stick the text under.
                var targetNode = dojo.byId("termContainer");

                //The parameters to pass to xhrGet, the url, how to handle it, and the callbacks.
                var xhrArgs = {
                    url: "http://data.uba.de/umt/de/concepts/_00049280.rdf",
                    handleAs: "xml",
                    load: function(data) {
                        termContainer.innerHTML = data;
                        console.debug(data);
                        },
                    error: function(error) {
                        targetNode.innerHTML = "An unexpected error occurred: " + error;
                    }
                }

                //Call the asynchronous xhrGet
                var deferred = dojo.xhrGet(xhrArgs);
            }
            
            dojo.require("dojo.io.script");
            dojo.ready(function(){
                //make the request just as before
                dojo.io.script.get({
                    url: "http://data.uba.de/umt/de/concepts/_00049280.rdf",
                    callbackParamName: "callback"//,
                    //content: {q: "#dojo"}
                }).then(function(data){
                    //we're only interested in data.results, so strip it off and return it
                    console.debug(data);
                });
            });
            
            //dojo.addOnLoad(getTerms);

            
        </script>
        
	</head>
	<body>
	    <h1>Dialog Tests</h1>
        <input type="button" onclick="dialog.showContextHelp(this, 1000, 'The Title Caption')" value="Show Context Help">
        <input type="button" onclick="dialog.show('The Info', 'This is just an info!')" value="Show Info Box">
        <input type="button" onclick="dialog.show('The Warning', 'This is just a warning!', dialog.WARNING)" value="Show Warning Box">
        <input type="button" onclick="dialog.showPage('Address Search', '../dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 585, true, {grid: 'generalAddress'})" value="Address Dialog">
        
        <input type="button" onclick="dialog.showPage('Thesaurus Navigator', '../dialogs/mdek_thesaurus_assist_dialog.jsp?c='+userLocale, 735, 430, true);" value="Thesaurus Navigator">
        <input type="button" onclick="dialog.showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {dstTable: 'thesaurusTerms'});" value="Thesaurus Dialog">

        <div id="termContainer">loading ...</div>
	</body>
</html>
