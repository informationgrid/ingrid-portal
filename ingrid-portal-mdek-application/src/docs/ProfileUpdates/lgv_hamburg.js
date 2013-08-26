// default values when creating new objects of class 1 - geodata (REDMINE-119)
dojo.subscribe("/onObjectClassChange", function(data) { 
    if (currentUdk.uuid === "newNode" && data.objClass === "Class1") { 
        dijit.byId("ref1BasisText").set("value", "keine Angabe");
        dijit.byId("ref1DataSet").set("value", "5"); // "Datensatz"
        UtilGrid.setTableData("thesaurusInspire", [{title: 99999}]); // "Kein INSPIRE-Thema"
        UtilGrid.setTableData("extraInfoConformityTable", [{specification:"INSPIRE-Richtlinie", level:3}]); // "nicht evaluiert"
        UtilGrid.setTableData("ref1SpatialSystem", [{title:"EPSG 25832: ETRS89 / UTM Zone 32N"}]);
        dijit.byId("availabilityDataFormatInspire").set("value", "Geographic Markup Language (GML)");
    }
});