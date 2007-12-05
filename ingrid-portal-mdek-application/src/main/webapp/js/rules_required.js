/*
 * Special rules for form items
 */


/*
 * Rules that change the required state of a field depending on another field's value
 */

// RULE 1

// Applies to: erfassung object -> geoinfo/karte -> fachbezug -> digitale repräsentation Id = ref1Representation
// Rule: If value = 'Vector' then Vektorformat (below) is required
var representationTable = dojo.widget.byId("ref1Representation");
if (representationTable) {
  dojo.event.connect(representationTable, "onValueChanged", function(obj, field) {applyRule1(obj[field]);});
  dojo.event.connect(representationTable, "onValueAdded", function(obj) {applyRule1(obj['representation']);});
}
function applyRule1(value) {
  var labelNode = dojo.byId("ref1VFormatLabel");
  var containerNode = labelNode.parentNode;
  if (value == "Vektor")
    setRequiredState(labelNode, containerNode, true);
  else
    setRequiredState(labelNode, containerNode, false);
}


// RULE 2

// Applies to: erfassung object -> verschlagwortung -> umweltthemen Ids = thesaurusEnvTopics, thesaurusEnvCats
// Rule: If value != leer then Umweltthemen, Umweltkategorien is required
var thesaurusEnvTopicsTable = dojo.widget.byId("thesaurusEnvTopics");
var thesaurusEnvCatsTable = dojo.widget.byId("thesaurusEnvCats");
if (thesaurusEnvTopicsTable) {
  dojo.event.connect(thesaurusEnvTopicsTable, "onValueChanged", function(obj, field) {applyRule2();});
  dojo.event.connect(thesaurusEnvTopicsTable, "onValueAdded", function(obj) {applyRule2();});
  dojo.event.connect(thesaurusEnvTopicsTable, "onValueDeleted", function(obj) {applyRule2();});
}
if (thesaurusEnvCatsTable) {
  dojo.event.connect(thesaurusEnvCatsTable, "onValueChanged", function(obj, field) {applyRule2();});
  dojo.event.connect(thesaurusEnvCatsTable, "onValueAdded", function(obj) {applyRule2();});
  dojo.event.connect(thesaurusEnvCatsTable, "onValueDeleted", function(obj) {applyRule2();});
}
function applyRule2() {
  var required = false;
  if (dojo.widget.byId("thesaurusEnvTopics").hasData() || dojo.widget.byId("thesaurusEnvCats").hasData())
    required = true;

  var labelNode1 = dojo.byId("thesaurusEnvTopicsLabel");
  var labelNode2 = dojo.byId("thesaurusEnvCatsLabel");
  var containerNode = labelNode1.parentNode.parentNode.parentNode;
  setRequiredState(labelNode1, containerNode, required);
  setRequiredState(labelNode2, containerNode, required);
}


// RULE 3

// Applies to: erfassung object -> zeitbezug -> Zeitbezug des Dateninhaltes Id = timeRefType
// Rule: If value == 'von' then timeRefDate2 is visible
var refTypeList = dojo.widget.byId("timeRefType");
if (refTypeList) {
  dojo.event.connect(refTypeList, "onValueChanged", function(value) {applyRule3(value);});
}
function applyRule3(value) {
dojo.debug(value);
  var datePickerNode = dojo.byId("timeRefDate2Editor");
  if (datePickerNode) {
    if (value.indexOf("von") == 0)
      datePickerNode.style.display = "block";
    else
      datePickerNode.style.display = "none";
  }
}
// initialize
applyRule3("");


// RULE 4

// Applies to: erfassung adresse -> adresse und aufgaben -> postfach/plz (postfach) Ids = addressPOBox, addressZipPOBox
// Rule: If value != leer then other fields is required, strasse, plz are not required
var addressPOBoxField = dojo.byId("addressPOBox");
var addressZipPOBoxField = dojo.byId("addressZipPOBox");
if (addressPOBoxField) {
  dojo.event.connect(addressPOBoxField, "onkeyup", function(evt) {applyRule4();});
}
if (addressZipPOBoxField) {
  dojo.event.connect(addressZipPOBoxField, "onkeyup", function(evt) {applyRule4();});
}
function applyRule4() {
  var poBoxRequired = false;
  if (dojo.byId("addressPOBox").value.length > 0 || 
      dojo.byId("addressZipPOBox").value.length > 0)
    poBoxRequired = true;

  // set strasse, plz required state
  setRequiredState(dojo.byId("addressStreetLabel"), null, !poBoxRequired);
  setRequiredState(dojo.byId("addressZipCodeLabel"), null, !poBoxRequired);
  // set postfach, plz (postfach) required state
  setRequiredState(dojo.byId("addressPOBoxLabel"), null, poBoxRequired);
  setRequiredState(dojo.byId("addressZipPOBoxLabel"), null, poBoxRequired);
}
