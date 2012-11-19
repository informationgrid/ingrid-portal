<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<title>Tables Test</title>

        <script type="text/javascript">
            dojo.require("dijit.Menu");
            dojo.require("dijit.form.CheckBox");
            
            dojo.addOnLoad(function(){
                

            });

            
        </script>
        
	</head>
	<body>
	    <h1>Dialog Tests</h1>
        <input type="button" onclick="dialog.showContextHelp(this, 1000, 'The Title Caption')" value="Show Context Help">
        <input type="button" onclick="dialog.show('The Info', 'This is just an info!')" value="Show Info Box">
        <input type="button" onclick="dialog.show('The Warning', 'This is just a warning!', dialog.WARNING)" value="Show Warning Box">
        <input type="button" onclick="dialog.showPage('The Page', '../dialogs/mdek_address_dialog.jsp?c='+userLocale, 755, 585, true, {grid: 'generalAddress'})" value="Show Page Dialog">

	</body>
</html>
