<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>Tables Test</title>

        <script type="text/javascript">
            require(["ingrid/dialog", "dojo/parser"], function(dialog, parser) {
                parser.parse();
            });
        </script>
        
	</head>
	<body>
	    <h1>Dialog Tests</h1>
        <input type="button" onclick="require('ingrid/dialog').showContextHelp(this, 1000, 'The Title Caption')" value="Show Context Help">
        <input type="button" onclick="require('ingrid/dialog').show('The Info', 'This is just an info!')" value="Show Info Box">
        <input type="button" onclick="require('ingrid/dialog').show('The Warning', 'This is just a warning!', dialog.WARNING)" value="Show Warning Box">
        <input type="button" onclick="require('ingrid/dialog').showPage('Address Search', '../dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 585, true, {grid: 'generalAddress'})" value="Address Dialog">
        <input type="button" onclick="require('ingrid/dialog').showPage('GetCapabilities Wizzard', '../dialogs/mdek_get_capabilities_wizard_dialog.jsp?c='+userLocale, 755, 585, true)" value="GetCapabilities Dialog">
        <input type="button" onclick="require('ingrid/dialog').showPage('General Object Wizzard', '../dialogs/mdek_create_object_wizard_dialog.jsp?c='+userLocale, 755, 585, true)" value="Object Wizzard">
        
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Navigator', '../dialogs/mdek_thesaurus_assist_dialog.jsp?c='+userLocale, 735, 430, true);" value="Thesaurus Navigator">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'sns', dstTable: 'thesaurusTerms'});" value="SNS Thesaurus Dialog">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://data.uba.de/umt/de/concepts/_00049251.rdf'});" value="RDF Thesaurus Dialog - Leaf">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-params.herokuapp.com/de/scheme.rdf'});" value="RDF Thesaurus Dialog - Hierarchy">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-params.herokuapp.com/bodenchemische-parameter-organisch.rdf'});" value="RDF Thesaurus Dialog - Hierarchy Leaf">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-exam.herokuapp.com/de/scheme.rdf'});" value="RDF Thesaurus Dialog - Hierarchy no root">
        <input type="button" onclick="require('ingrid/dialog').showPage('Thesaurus Dialog', '../dialogs/mdek_thesaurus_dialog.jsp?c='+userLocale, 1010, 430, true, {service: 'rdf', dstTable: 'thesaurusTerms', rootUrl: 'http://boden-exam.herokuapp.com/'});" value="RDF Thesaurus Dialog - Hierarchy (boden-exam)">

        <div id="termContainer"></div>
	</body>
</html>
