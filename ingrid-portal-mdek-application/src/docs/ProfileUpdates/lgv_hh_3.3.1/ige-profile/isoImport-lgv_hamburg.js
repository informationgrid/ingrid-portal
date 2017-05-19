/**
 * Zusätzliches Feld -> Checkbox: "Veröffentlichung gemäß HmbTG"
 */

var keywords = XPATH.getNodeList(source, "//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString");
    
for (var i=0; i<keywords.getLength(); i++ ) {
  var keyword = keywords.item(i).getTextContent();
  if ("hmbtg".equals(keyword)) {
    var targetEl = target.getDocumentElement();
    var additionalValues = XPATH.createElementFromXPath(targetEl, "/igc/data-sources/data-source/data-source-instance/general/general-additional-values");
    var additionalValue = additionalValues.appendChild(targetEl.getOwnerDocument().createElement("general-additional-value"));
    XMLUtils.createOrReplaceTextNode(XPATH.createElementFromXPath(additionalValue, "field-key"), "publicationHmbTG"); 
    XMLUtils.createOrReplaceTextNode(XPATH.createElementFromXPath(additionalValue, "field-data"), true);
    
    // remove as normal keyword
    var terms = XPATH.getNodeList(target, "/igc/data-sources/data-source/data-source-instance/subject-terms/uncontrolled-term");
    for (var j=0; j<terms.getLength(); j++ ) {
      var termNode = terms.item(j);
      if (termNode.getTextContent() === "hmbtg") {
        XPATH.removeElementAtXPath(termNode, ".");
      }
    }
  }
}

/**
 * Zusätzliches Auswahlfeld/Liste: "Informationsgegenstand"
 */
var keywords = XPATH.getNodeList(source, "//gmd:descriptiveKeywords/gmd:MD_Keywords[normalize-space(./gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString)='HmbTG-Informationsgegenstand']/gmd:keyword/gco:CharacterString");
    
var targetEl = target.getDocumentElement();
var additionalValues = XPATH.createElementFromXPath(targetEl, "/igc/data-sources/data-source/data-source-instance/general/general-additional-values");

for (var i=0; i<keywords.getLength(); i++ ) {
    var keyword = keywords.item(i).getTextContent();
    
    var additionalValue = additionalValues.appendChild(targetEl.getOwnerDocument().createElement("general-additional-value"));
    XMLUtils.createOrReplaceAttribute(additionalValue, "line", (i+1)+"");
    XMLUtils.createOrReplaceTextNode(XPATH.createElementFromXPath(additionalValue, "field-key"), "informationHmbTG"); 
    XMLUtils.createOrReplaceAttribute(XPATH.createElementFromXPath(additionalValue, "field-data"), "id", keyword);
    XMLUtils.createOrReplaceTextNode(XPATH.createElementFromXPath(additionalValue, "field-key-parent"), "Informationsgegenstand");

    // remove keyword from general information
    XMLUtils.remove(XPATH.getNode(targetEl, "/igc/data-sources/data-source/data-source-instance/subject-terms/uncontrolled-term[.='" + keyword + "']"));
}
