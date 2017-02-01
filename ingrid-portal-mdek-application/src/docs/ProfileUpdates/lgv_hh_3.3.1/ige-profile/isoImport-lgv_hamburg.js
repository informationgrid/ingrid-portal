/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 * Zusätzliches Feld -> Checkbox: "Veröffentlichung gemäß HmbTG"
 */

var keywords = XPATH.getNodeList(source, "//gmd:identificationInfo//gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString");
    
for (var i=0; i<keywords.getLength(); i++ ) {
  var keyword = keywords.item(i).getTextContent();
  if ("#opendata_hh#".equals(keyword)) {
    var targetEl = target.getDocumentElement();
    var additionalValues = XPATH.createElementFromXPath(targetEl, "/igc/data-sources/data-source/data-source-instance/general/general-additional-values");
    var additionalValue = additionalValues.appendChild(targetEl.getOwnerDocument().createElement("general-additional-value"));
    XMLUtils.createOrReplaceTextNode(XPATH.createElementFromXPath(additionalValue, "field-key"), "publicationHmbTG"); 
    XMLUtils.createOrReplaceTextNode(XPATH.createElementFromXPath(additionalValue, "field-data"), true); 
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
}
