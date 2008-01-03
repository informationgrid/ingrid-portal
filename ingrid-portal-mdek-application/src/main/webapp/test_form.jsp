<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
</head>

<script
	src='/ingrid-portal-mdek-application/dwr/interface/EntryService.js'></script>
<script src='/ingrid-portal-mdek-application/dwr/engine.js'></script>
<script type="text/javascript">
	var djConfig = {isDebug: true, debugAtAllCosts: false};
</script>
<script type="text/javascript" src="dojo-0.4.1-ingrid/dojo.js"></script>
<script type="text/javascript" src="js/config.js"></script>

<!-- <script type="text/javascript" src="js/objectload.js"></script>  -->


<script>
dojo.require("dojo.event.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.Form");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Textbox");
dojo.require("ingrid.widget.Form");
dojo.require("ingrid.widget.Select");
dojo.require("ingrid.widget.ValidationTextbox");

dojo.addOnLoad(function()
{
  dojo.widget.byId('objectClass').setValue('Class0');
});

// Diese callback Funktion wird aufgerufen wenn ein neuer Knoten im Baum ausgewählt wurde  
function buttonFunc()
{
  var formWidget = dojo.widget.byId('headerFormObject');
  var selectWidget = dojo.widget.byId('objectClass');
  dojo.debug("Form content before setting values: " + dojo.json.serialize(formWidget.getValues()));

  var headerObjectData = {
    objectName: 'test Name',
    objectClass: 'Class2',
//    objectClass_selected: '',
  };
  
//  dojo.widget.byId('headerFormObject').setValues(headerObjectData);
  selectWidget.setValue('Class2');

  dojo.debug("Form content after setting values: " + dojo.json.serialize(formWidget.getValues()));

//  dojo.widget.byId('objectClass').setValue(nodeData.nodeDocType);
//  dojo.widget.byId('objectClass').setValue('Class'+ 1);
//  dojo.widget.byId('objectOwner').setValue('1');
//  dojo.widget.byId('objectClass').setValue('Class2');
//  dojo.widget.byId('last_editor').setValue('test last editor');

//   var testValues = dojo.widget.byId('headerFormObject').getValues();
//   dojo.debug("Object is: " + dojo.json.serialize(testValues));

}
</script>


<body>

<!-- input Form -->
<form dojoType="ingrid:Form" id="headerFormObject">
  <table cellspacing="0">
    <tbody>
      <tr>
        <td class="label"><label for="objectName">Objektname</label></td>
    	<td colspan="2"><input type="text" id="objectName" name="objectName" class="w550" dojoType="ingrid:ValidationTextBox" /></td>
      </tr>
      <tr>
        <td class="label col1"><label for="objectClass">Objektklasse</label></td>
    	<td class="col2">
          <select dojoType="ingrid:Select" style="width:386px;" id="objectClass" name="objectClass">
            <!-- TODO: fill in jsp -->
            <option value="Class0">Organisationseinheit/Fachaufgabe</option>
            <option value="Class1">Geo-Information/Karte</option>
            <option value="Class2">Dokument/Bericht/Literatur</option>
            <option value="Class3">Dienst/Anwendung/Informationssystem</option>
            <option value="Class4">Vorhaben/Projekt/Programm</option>
            <option value="Class5">Datensammlung/Datenbank</option>
          </select>
        </td>
      </tr>
      <tr>
        <td class="label"><label for="objectOwner">Verantwortlicher</label></td>
        <td><input dojoType="ingrid:Select" dataUrl="js/data/owner.js" style="width:386px;" id="objectOwner" name="objectOwner" mode="remote" /></td>
        <td class="note"><strong>Status:</strong> in Bearbeitung</td>
      </tr>
    </tbody>
  </table>
</form>


<button dojoType="Button" widgetID="myButton" onclick="buttonFunc">Set Class</button>
</body>
</html>
