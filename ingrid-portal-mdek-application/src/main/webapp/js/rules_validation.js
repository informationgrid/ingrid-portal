/*
 * Special validation rules for form items
 */

/*
 * Rules that validate a field's value depending on another field's value
 */

// Applies to: erfassung object -> raumbezug -> höhe -> minimum/maximum = spatialRefAltMin, spatialRefAltMax
// Rule: minimum <= maximum
var minimumField = dojo.widget.byId("spatialRefAltMin");
var maximumField = dojo.widget.byId("spatialRefAltMax");
if (minimumField && maximumField) {
  dojo.event.connect(minimumField, "onblur", function(evt) {applyMinMaxRule(minimumField, maximumField);});
  dojo.event.connect(maximumField, "onblur", function(evt) {applyMinMaxRule(minimumField, maximumField);});
}
function applyMinMaxRule(minimumField, maximumField) {
  var minVal = minimumField.getValue();
  var maxVal = maximumField.getValue();
  if (minVal.length > 0 && maxVal.length > 0 && parseFloat(minVal) > parseFloat(maxVal)) {
    minimumField.updateClass("Invalid");
    maximumField.updateClass("Invalid");
    dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("validation.minmax"), "Minimum", "Maximum"), dialog.WARNING, 
          [{caption:message.get("general.ok"), action:function() {
              minimumField.updateClass("Invalid");
              maximumField.updateClass("Invalid");
            }
          }]);
  }
  else {
    if (minimumField.isValid())
      minimumField.updateClass("Valid");
    if (maximumField.isValid())
      maximumField.updateClass("Valid");
  }
}
