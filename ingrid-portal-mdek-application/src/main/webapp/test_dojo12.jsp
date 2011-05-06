<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>MDEK Demo V4</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />

<link rel="StyleSheet" href="dojo-release-1.2.0/dojo/resources/dojo.css" type="text/css" />
<link rel="StyleSheet" href="dojo-release-1.2.0/dijit/themes/tundra/tundra.css" type="text/css" />
<link rel="StyleSheet" href="dojo-release-1.2.0/dojox/grid/resources/Grid.css" type="text/css" />
<link rel="StyleSheet" href="dojo-release-1.2.0/dojox/grid/resources/tundraGrid.css" type="text/css" />


<script type="text/javascript" src="dojo-release-1.2.0/dojo/dojo.js" djConfig="parseOnLoad:true, isDebug:true"></script>

<script>
dojo.require("dojo.parser");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.DateTextBox");
dojo.require("dijit.form.NumberTextBox");
dojo.require("dijit.form.Textarea");
dojo.require("dijit.form.ValidationTextBox");

dojo.require("dijit.layout.BorderContainer");
dojo.require("dijit.layout.ContentPane");

dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dojox.grid.DataGrid");
</script>

<script>
/*
ingrid:ValidationTextbox 44x

ingrid:Combobox 13x
ingrid:Select 29x

ingrid:RealNumberTextbox 12x
IntegerTextbox 3x

ingrid:DropdownDatePicker 6x

ingrid:FilteringTable 46x


45x dijit.form.ValidationTextBox
45x dijit.form.ComboBox
15x dijit.form.NumberTextBox
10x dijit.form.DateTextBox
40x dojox.grid.DataGrid
*/

var testStores = [];

function testSetValue() {
	var testValues = [];
	var testDates = [];
	var testNums = [];

	for (var i = 0; i < 50; ++i) {
		testValues.push(generateRandomString(20));
	}
	for (var i = 0; i < 10; ++i) {
		testDates.push(new Date);
	}
	for (var i = 0; i < 20; ++i) {
		testNums.push(Math.random()*30000);
	}
	for (var i = 0; i < 40; ++i) {
		var data = [];
		for (var j = 0; j < 20; ++j) {
			data.push({field1:generateRandomString(20), field2:Math.random()*30000});
		}
		testStores.push(new dojo.data.ItemFileWriteStore({data:{items:data}}));
	}

	var startTime = new Date();

	// set values for the ValidationTextboxes
	for (var i = 1; i <= 45; ++i) {
		dijit.byId("textInput"+i).attr("value", testValues[i]);
	}
	console.log("setValue duration for 45 Textboxes: "+(new Date() - startTime)+" ms");
	startTime = new Date();

	// set values for the ComboBoxes
	for (var i = 1; i <= 45; ++i) {
		dijit.byId("comboBox"+i).attr("value", testValues[i]);
	}
	console.log("setValue duration for 45 Comboboxes: "+(new Date() - startTime)+" ms");
	startTime = new Date();

	// set values for the DateBoxes
	for (var i = 1; i <= 10; ++i) {
		dijit.byId("dateInput"+i).attr("value", testDates[i]);
	}
	console.log("setValue duration for 10 DateInputs: "+(new Date() - startTime)+" ms");
	startTime = new Date();

	// set values for the NumberBoxes
	for (var i = 1; i <= 15; ++i) {
		dijit.byId("numberInput"+i).attr("value", testNums[i]);
	}
	console.log("setValue duration for 15 NumberInputs: "+(new Date() - startTime)+" ms");
	startTime = new Date();

	// set values for the Grids
	for (var i = 1; i <= 20; ++i) {
		dijit.byId("gridNode"+i).setStore(testStores[i], {field1: '*'});
	}
	console.log("setValue duration for 20 Grids: "+(new Date() - startTime)+" ms");
	startTime = new Date();
}

function testModifyStores() {
	var startTime = new Date();

	for (var i = 0; i < testStores.length; ++i) {
//		var newData = [];
//		for (var j = 0; j < 20; ++j) {
//			data.push({field1:generateRandomString(20), field2:Math.random()*30000});
//		}
		testStores[i].fetch({
			query: {field1: '*'},
			onComplete: function(items) { console.log("deleting table "+i); dojo.forEach(items, testStores[i].deleteItem); }
		});
	}
	console.log("Deleting stores took "+(new Date() - startTime)+" ms");
}

function testClearValue() {
	// clear values for the ValidationTextboxes
	for (var i = 1; i <= 45; ++i) {
		dijit.byId("textInput"+i).attr("value", "");
	}

	// clear values for the ComboBoxes
	for (var i = 1; i <= 45; ++i) {
		dijit.byId("comboBox"+i).attr("value", "");
	}

	// clear values for the DateBoxes
	for (var i = 1; i <= 10; ++i) {
		dijit.byId("dateInput"+i).attr("value", "");
	}

	// clear values for the NumberBoxes
	for (var i = 1; i <= 15; ++i) {
		dijit.byId("numberInput"+i).attr("value", "");
	}
}

function testGetValue() {
	// get values from the ValidationTextboxes
	var resultArray = [];
	for (var i = 1; i <= 45; ++i) {
		resultArray.push(dijit.byId("textInput"+i).attr("value"));
	}
	for (var i = 1; i <= 45; ++i) {
		resultArray.push(dijit.byId("comboBox"+i).attr("value"));
	}
	for (var i = 1; i <= 10; ++i) {
		resultArray.push(dijit.byId("dateInput"+i).attr("value"));
	}
	for (var i = 1; i <= 15; ++i) {
		resultArray.push(dijit.byId("numberInput"+i).attr("value"));
	}
	alert(resultArray);
}


function generateRandomString(strLength) {
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var string_length = strLength;
	var str = '';
	for (var i=0; i<string_length; i++) {
		var rnum = Math.floor(Math.random() * chars.length);
		str += chars.substring(rnum,rnum+1);
	}
	return str;
}

</script>

</head>

<body class="tundra">

	<div dojoType="dijit.layout.BorderContainer" design="headline" style="width: 100%; height: 1100px;">

		<div dojoType="dijit.layout.ContentPane" region="left" splitter="true" style="background-color:grey;width:800px;height:1400px;">

			<div id="testSetValueButton" dojoType="dijit.form.Button" onclick="testSetValue();">setValue Test</div>
			<div id="testClearValueButton" dojoType="dijit.form.Button" onclick="testClearValue();">clearValue Test</div>
			<div id="getValueButton" dojoType="dijit.form.Button" onclick="testGetValue();">getValue Test</div>
			<div id="modifyTableValuesButton" dojoType="dijit.form.Button" onclick="testModifyStores();">modify stores Test</div>

		</div>

		<div dojoType="dijit.layout.ContentPane" region="center" style="background-color:grey;width:800px;height:1400px;">
			<div id="textInput1" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput2" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput3" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput4" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput5" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput6" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput7" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput8" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput9" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput10" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput11" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput12" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput13" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput14" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput15" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput16" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput17" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput18" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput19" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput20" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput21" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput22" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput23" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput24" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput25" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput26" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput27" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput28" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput29" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput30" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput31" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput32" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput33" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput34" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput35" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput36" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput37" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput38" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput39" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput40" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput41" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput42" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput43" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput44" dojoType="dijit.form.ValidationTextBox"></div>
			<div id="textInput45" dojoType="dijit.form.ValidationTextBox"></div>

			<div id="button1" dojoType="dijit.form.Button">test</div>
			<div id="button2" dojoType="dijit.form.Button">test</div>
			<div id="button3" dojoType="dijit.form.Button">test</div>
			<div id="button4" dojoType="dijit.form.Button">test</div>
			<div id="button5" dojoType="dijit.form.Button">test</div>
			<div id="button6" dojoType="dijit.form.Button">test</div>
			<div id="button7" dojoType="dijit.form.Button">test</div>
			<div id="button8" dojoType="dijit.form.Button">test</div>
			<div id="button9" dojoType="dijit.form.Button">test</div>
			<div id="button10" dojoType="dijit.form.Button">test</div>
			<div id="button11" dojoType="dijit.form.Button">test</div>
			<div id="button12" dojoType="dijit.form.Button">test</div>

			<select id="comboBox1" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox2" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox3" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox4" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox5" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox6" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox7" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox8" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox9" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox10" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox11" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox12" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox13" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox14" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox15" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox16" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox17" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox18" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox19" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox20" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox21" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox22" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox23" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox24" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox25" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox26" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox27" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox28" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox29" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox30" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox31" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox32" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox33" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox34" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox35" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox36" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox37" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox38" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox39" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox40" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox41" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox42" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox43" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox44" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>
			<select id="comboBox45" dojoType="dijit.form.ComboBox" autocomplete="false" value="California"><option selected="selected">California</option><option >Illinois</option><option >New York</option><option >Texas</option></select>

			<div id="dateInput1" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput2" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput3" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput4" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput5" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput6" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput7" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput8" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput9" dojoType="dijit.form.DateTextBox"></div>
			<div id="dateInput10" dojoType="dijit.form.DateTextBox"></div>

			<div id="numberInput1" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput2" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput3" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput4" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput5" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput6" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput7" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput8" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput9" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput10" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput11" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput12" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput13" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput14" dojoType="dijit.form.NumberTextBox"></div>
			<div id="numberInput15" dojoType="dijit.form.NumberTextBox"></div>

			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode1" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode2" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode3" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode4" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode5" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode6" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode7" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode8" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode9" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode10" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode11" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode12" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode13" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode14" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode15" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode16" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode17" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode18" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode19" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode20" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
<!-- 
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode21" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode22" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode23" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode24" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode25" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode26" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode27" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode28" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode29" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode30" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode31" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode32" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode33" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode34" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode35" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode36" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode37" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode38" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode39" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
			<div style="height:300px;width:465px;overflow:auto"><table id="gridNode40" jsId="grid" dojoType="dojox.grid.DataGrid"><thead><tr><th field="field1" width="200px">Test Field 1</th><th field="field2" width="200px">Test Field 2</th></tr></thead></table></div>
 -->
		</div>
	</div>

</body>
</html>
