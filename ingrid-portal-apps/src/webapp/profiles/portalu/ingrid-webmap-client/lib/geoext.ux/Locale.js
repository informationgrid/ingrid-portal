function i18n(key, arrInsertValues) {
    return Local.getLocalizedString(key, Local.languageCode, arrInsertValues);
}

// "Local" is a simple "static" object containing methods and localization strings
Local = {

    // Default locale code - set based on cookie at the bottom of this script
    languageCode: null,
    languageCodeDefault: 'en',

    supportedLanguages: {
		en: {},
		de: {}
	},
    
    getLocalizedString: function(key, languageCode, arrInsertValues) {
	if (!this.localizedStrings[key]) {
            // return key if key is undefined in localization list
            return key;
        }
        if (!this.localizedStrings[key][languageCode]) {
            // return default language string or empty string if the string for the specified language is undefined
            return this.formatString(this.localizedStrings[key][this.languageCodeDefault] || '', arrInsertValues);
        }
        // give 'em what they asked for
        return (this.formatString(this.localizedStrings[key][languageCode], arrInsertValues));
    },


    // returns a localized string formatted to replace values {0},{1} etc with values from the passed array
    formatString: function(string, arrInsertValues) {
        var formattedString = string;
        if (arrInsertValues && arrInsertValues.constructor.toString().indexOf("Array") != -1) {
            for (var i = 0; i < arrInsertValues.length; i++) {
                formattedString = formattedString.replace('{' + i + '}', arrInsertValues[i]);
            }
        }
        return formattedString;
    },

    localizedStrings: {
    	tAbbrechen: { en: 'Cancel', de: 'Abbrechen' },
    	tOk: { en: 'Ok', de: 'Ok' },
    	tNein: { en: 'No', de: 'Nein' },
    	tJa: { en: 'Yes', de: 'Ja' },
    	tAdresse: { en: 'Address', de: 'Adresse' },
    	tAdministrativeAuswahl: { en: 'Select administrative entity', de: 'Administrative Auswahl' },
    	tAdministrativeEinheit : { en: 'Administrative entity', de: 'Administrative Einheit' },
    	tAktiveDienste: { en: 'Active services', de: 'Aktive Dienste' },
    	tAnsprechpartner: { en: 'Point of contact', de: 'Ansprechpartner' },
    	tAlleZuAufklappen: { en: 'Expand/Collaps all', de: 'Alle auf/zuklappen' },
    	tAuswahlZuSuchanfrageHinzufuegen: { en: 'Add to query', de: 'Auswahl zu Suchanfrage hinzuf&uuml;gen' },
    	tBeimSpeichernDerDatenIstEinFehlerAufgetreten: { en: 'Error storing the Data.', de: 'Beim Speichern der Daten ist ein Fehler aufgetreten.' },
    	tBeschreibung: { en: 'Description', de: 'Beschreibung' },
    	tBitteCapabilitiesExternerService: { en: '<p class="accordion-lable">Enter the GetCapabilities-URL of an external web map service (WMS):</p>', de: '<p class="accordion-lable">Laden Sie einen externen WMS-Dienst:</p>' },
    	tBundesland: { en: 'County', de: 'Bundesland' },
    	tChooseMapServices: { en: 'Viewing Data', de: 'Kartendienste ausw&auml;hlen' },
    	tCouldNotObtainAdminInfo: { en: 'Could not obtain administrative information.', de: 'Konnte keine administrativen Informationen abrufen.' },
    	tDasLaderDerKonfigurationIstFehlgeschlagen: { en: 'Error while loading the configuration.', de: 'Das Laden der Konfiguration ist fehlgeschlagen.' },
    	tDatum: { en: 'Date', de: 'Datum' },
    	tDatumDerRegistrierung: { en: 'Date of registration', de: 'Datum der Registrierung' },
    	tDieAenderungenWurdenGespeichert: {en:'The changes have been saved successfully.', de: 'Die &Auml;nderungen wurden gespeichert.'},
    	tDienste : { en: 'Services', de: 'Dienste' },
    	tDienstEntfernen: { en: 'Remove service', de: 'Dienst Entfernen' },
    	tDienstHinzufuegen : { en: 'Add service', de: 'Dienst hinzuf&uuml;gen' },
    	tDisableWelcome: {en: 'Do not show this dialog next time', de: 'Diesen Dialog nicht mehr anzeigen'},
    	tDrehung : { en: 'Rotation', de: 'Drehung' },
    	tDrucken : { en: 'Print', de: 'Drucken' },
    	tFax : { en: 'Fax', de: 'Fax' },
    	tEbenen : { en: 'Layer', de: 'Ebenen' },
    	tEbenenAktivieren : { en: 'Activate layers of this service.', de: 'Alle Layer aktivieren' },
    	tEbenenZoomen : { en: 'Zoom to extent of this service.', de: 'Zoom to Layer' },
    	tEmail : { en: 'E-Mail', de: 'E-Mail' },
    	tErweiterteEinstellungen : { en: 'Extended Preferences', de: 'Einstellungen' },
        tError: { en: 'Error', de: 'Fehler' },
    	tErrorLoadingCapability : { en: 'Error loading capability.', de: 'Das Laden des Capabilities Dokuments ist fehlgeschlagen.' },
    	tErrorLoadingProjectionDef : { en: 'Error loading projection definitions.', de: 'Das Laden der Projektionsdefinition ist fehlgeschlagen.' },
    	tErrorListingMap : { en: 'Error listing map.', de: 'Die Karten konnten nicht gelistet werden.' },
    	tErrorPrintingMap : { en: 'Error printing map.', de: 'Beim Drucken der Karte ist ein Fehler aufgetreten.' },
    	tErrorRemovingMap : { en: 'Error deleting map.', de: 'Beim L&ouml;schen der Karte ist ein Fehler aufgetreten.' },
    	tErrorSavingMap : { en: 'Error saving map.', de: 'Beim Speichern der Karte ist ein Fehler aufgetreten.' },
    	tErrorSearching : { en: 'Error while performing search.', de: 'Beim Suchen ist ein Fehler aufgetreten.' },
    	tFeatureInfo: { en: 'Feature Info', de: 'Feature Info' },
    	tFlaeche : { en: 'Area', de: 'Fl&auml;che' },
    	tFormat : { en: 'Format', de: 'Format' },
    	tFuerTransparenzErst : { en: 'Select a layer for transparency first.', de: 'F&uuml;r Transparenzfunktion einen Layer markieren.' },
    	tFuerMetadatenErst : { en: 'Display metadata.', de: 'Anzeigen der Metadaten.' },
        tGebietAuswaehlen : { en: 'Select area', de: 'Gebiet ausw&auml;hlen' },
        tGebuehren : { en: 'Fees', de: 'Geb&uuml;hren' },
        tHilfe : { en: 'Help', de: 'Hilfe' },
        tHinweis : { en: 'Hint', de: 'Hinweis' },
        tHintZoomToView : { en: '<p class="hint">You may have to zoom into the map to see the layers of the external service. The provider of the service is responsible for the display of the service. PortalU has no influence here.</p>', de: '<p class="hint">Nutzen Sie zur Darstellung einzelner Layer die Zoom-Funktion. F&uuml;r die Inhalte des Dienstes ist der jeweilige Betreiber verantwortlich.</p>' },
		tHinzufuegen : { en: 'Add', de:'Hinzuf&uuml;gen'},
		tIdAuswaehlen : { en: 'Select an ID of an administrative unit', de: 'ID einer Administrativen Einheit ausw&auml;hlen' },
        tInfo : { en: 'Info', de: 'Info' },
        tKarteHerunterladen: { en: 'Save map as WMC document', de: 'Karte als WMC abspeichern' },
        tKarteDrucken: { en: 'Print map', de: 'Karte drucken' },
        tKarteGespeichert: { en: 'Map succesfully stored', de: 'Karte erfolgreich gespeichert' },
        tKarteKonnteNichtSpeichern: { en: 'Map could not be stored', de: 'Konnte Karte nicht speichern' },         
        tKarteLaden: { en: 'Load map', de: 'Karte laden' },
        tKarteSpeichern: { en: 'Save map', de: 'Karte speichern' },
        tKarteVerschieben: { en: 'Move map', de: 'Karte verschieben' },
        tKarteZoomen: { en: 'Zoom to initial map', de: 'Auf initiale Kartenausdehnung zoomen' },
        tKeineKartenVorhanden: { en: 'No maps available', de: 'Keine Karten vorhanden' },
        tKommentar: { en: 'Comment', de: 'Kommentar' },
		tKonnteKarteNichtSpeichern: { en: 'Error saving map.', de: 'Beim Speichern der Karte ist ein Fehler aufgetreten.' },
        tKoordinatensysteme: { en: 'Coordinate system', de: 'Koordinatensysteme' },
        tKurzUrl: { en: 'Short-Url', de: 'Kurz-Url' },
        tKurzUrlMessage: { en: 'With this short-url the saved map can be accessed directly:', de: 'Mit dieser Url kann die gespeicherte Karte direkt aufgerufen werden:' },
        tLaden : { en: 'Load', de: 'Laden' },
        tLadeDruckkonfiguration : { en: 'Loading print configuration...', de: 'Lade Druckkonfiguration...' },
        tLand : { en: 'Country', de: 'Land' },
        tLayers: { en: 'Layers', de: 'Layers' },
        tLayerTransparenz : { en: 'Layer transparency', de: 'Layer Transparenz' },
        tLegende : { en: 'Legend', de: 'Legende' },
        tloeschen: { en: 'delete', de: 'l&ouml;schen' },
        tMaszstab : { en: 'Scale', de: 'Ma&szlig;stab' },
        tMessen : { en: 'Measure Tool', de: 'Messen' },
        tMetadaten : { en: 'Metadata', de: 'Metadaten' },
        tMsgServiceAdded: { en: '<p><strong>Service added successfully</strong></p> Please choose a layer for display.', de: '<p><strong>Dienst erfolgreich hinzugef&uuml;gt</strong></p> Sie k&ouml;nnen nun beim neu hinzugef&uuml;gten Dienst aus der Liste verf&uuml;gbarer Layer ausw&auml;hlen.' },
        tMsgCannotRemoveBaselayer: { en: '<p><strong>The base layer cannot be removed.</strong></p>', de: '<p><strong>Basisdienst kann nicht entfernt werden.</strong></p>' },
        tMsgServiceRemoved: { en: '<p><strong>Service removed successfully!</strong></p>', de: '<p><strong>Dienst erfolgreich entfernt!</strong></p>' },
        tNextHint: { en: 'Next &raquo;', de: 'N&auml;chster &raquo;' },
        tObjektinformationen : { en: 'Object information', de: 'Objektinformationen' },
        tOrganisation : { en: 'Organisation', de: 'Organisation' },
        tPDFErstellen: { en: 'Create PDF', de: 'PDF Erstellen' },
        tPleaseWait: { en: 'Please wait', de: 'Bitte warten' },
        tPleaseWaitCapabilities: { en: 'Loading Capabilities. Please wait ...', de: 'Dienst wird geladen...' },
        tPleaseWaitSearch: { en: 'Searching term. Please wait ...', de: 'Suche wird durchgef&uuml;hrt. Bitte warten ...' },
        tPLZ: { en: 'ZIP', de: 'PLZ' },
        tPreviousHint: { en: '&laquo; Previous', de: '&laquo; Vorheriger' },
        tPrintAndSaveFunctions: { en: 'Map printing', de: 'Druck- und Speicherfunktionen' },
        tProjectionNotSupported: { en: 'The spatial projection (EPSG) is not supported by the base map.', de: 'Das geladene Raumbezugssystem (EPSG) wird von der Basiskarte nicht unterst&uuml;tzt.' },
        tRaumbezugAuszerhalbDesFeldes: { en: 'Spatial reference outside field', de: 'Raumbezug au&szlig;erhalb des Feldes' },
        tRaumbezugssystem: { en: 'Spatial reference system', de: 'Raumbezugssystem' },
        tRegistrierendeStelle: { en: 'Registering authority', de: 'Registrierende Stelle' },
        tRequestingData: { en: 'Requesting data', de: 'Daten werden angefordert' },
        tServiceAlreadyAdded: { en: 'Service was already added.', de: 'Der Dienst wurde bereits hinzugef&uuml;gt.' },
        tSollDieKarteGeloeschtWerden: { en: 'Really delete map?', de: 'Soll die Karte wirklich gel&ouml;scht werden?' },
        tSpeichern : { en: 'Save', de: 'Speichern' },
        tStadt : { en: 'City', de: 'Stadt' },
        tStrecke : { en: 'Distance', de: 'Strecke' },
        tSuccessMapSaved : { en: 'The map has been saved.', de: 'Die Karte wurde gespeichert.' },
        tSuccessSearching : { en: 'Search successful.', de: 'Suche erfolgreich.' },
        tSuchbegriffEingeben : { en: '<p>Enter search query</p>', de: '<p><b>Suchraum:</b> <br>Neben den unter "Themen" und "Anbieter" eingebundenen Kartendiensten werden auch Angaben zu Kartendiensten aus Umweltdatenkataloge bei der Suche berücksichtigt.</p>' },
        tSuche : { en: 'Search', de: 'Suche' },
        tSuchergebnisseLoeschen : { en: 'Delete search results', de: 'Suchergebnisse l&ouml;schen' },
        tSuchergebnisse: { en: 'Search results', de: 'Suchergebnisse' },
        tSuchen : { en: 'Search', de: 'Suchen' },
        tTelefon: { en: 'Phone', de: 'Telefon' },
        tTextOf: { en: 'of', de: 'von' },
        tTextHints: { en: 'Tip', de: 'Tipp' },
        tTipsAndTricks: { en: 'Tips and Tricks', de: 'Tipps und Tricks' },
        tTitle: { en: 'Layer Title', de: 'Layer Titel' },
        tUmDerSucheEinenRaumbezugHinzuzufuegenBitteEineAuswahlTreffen: { en: '<p><strong>With the following actions you can pick a spatial reference to which the search results will be limited:</strong><br/><br/>' 
				+'<strong>- "Move map"</strong> <span style="margin-bottom:-3px;border:0;padding-left:17px;" class="iconDefault"> : Move the map, while pressing the left mouse button.</span><br/><br/>' 
				+'<strong>- "Select area"</strong> <span style="margin-bottom:-3px;border:0;padding-left:17px;" class="iconSelectCoordinates"> : Choose an area which limits the search results. Consider the options at the bottom of the map also.</span><br/><br/>' 
				+'<strong>- "Select an ID of an administrative unit"</strong> <span style="margin-bottom:-3px;border:0;padding-left:17px;" class="iconInfo"> : With this option you can click on any position in the map and receive the official are area identification as area-ID.</span> </p>', 
				de: '<p><strong>Mit folgenden Aktionen k&ouml;nnen Sie einen Raumbezug ausw&auml;hlen, auf welchen dann die Suchergebnisse eingeschr&auml;nkt werden:</strong><br/><br/>' 
				+'<strong>- "Karte verschieben"</strong> <span style="margin-bottom:-3px;border:0;padding-left:17px;" class="iconDefault"> : Verschieben Sie den Kartenausschnitt, indem Sie bei gedr&uuml;ckter linker Maustaste die Karte bewegen.</span><br/><br/>' 
				+'<strong>- "Gebiet ausw&auml;hlen"</strong> <span class="iconSelectCoordinates" style="margin-bottom:-3px;border:0;padding-left:17px;"> : W&auml;hlen Sie ein Gebiet aus, welches r&auml;umlich die Suchergebnisse beschr&auml;nkt. Beachten Sie auch die Optionen unterhalb der Karte.</span><br/><br/>' 
				+'<strong>- "ID einer Administrativen Einheit ausw&auml;hlen"</strong> <span style="margin-bottom:-3px;border:0;padding-left:17px;" class="iconInfo"> : Mit dieser Option k&ouml;nnen Sie an beliebiger Stelle in die Karte klicken und erhalten die verf&uuml;gbaren offiziellen Gebietsbezeichnungen als Area-ID.</span> </p>' },
        tUnknownViewConfiguration: { en: 'Unknown view configuration.', de: 'Die View-Konfiguration ist unbekannt.' },
        tUnsupportedSpatialReferenceSystem: { en: 'The spatial reference system ({0}) is not supported by the service ({1}).', de: 'Das Raumbezugssystem ({0}) wird von den geladenen Diensten ({1}) nicht unterst&uuml;tzt.' },
        tUseMapServices: { en: 'Map navigation', de: 'Kartendienste bedienen' },
        tVor: { en: 'Forward', de: 'Vor' },
        tWarning: { en: 'Warning', de: 'Warnung' },
        tWelcomeTextAdd: { en: 'a) Add a selected service from the <strong>provider/topic list</strong> to the current map viewer <br />b) Add an <strong>external service (WMS)</strong> by entering the GetCapabilities URL', de: 'a) L&auml;dt einen <strong>markierten</strong> Kartendienst aus der <strong>Anbieter-/Themenliste</strong> in den Viewer <br />b) L&auml;dt einen <strong>externen Kartendienst</strong> mithilfe der GetCapabilities-URL in den Viewer' },
        tWelcomeTextRemove: { en: 'Delete a selected service from the current map viewer', de: 'Entfernt einen markierten Kartendienst in der Rubrik <strong>Aktive Dienste</strong> aus der aktuellen Kartenansicht'},
        tWelcomeTextRemoveAll: { en: 'Delete the last search results in the <strong>hit list</strong>', de: 'L&ouml;scht die <strong>Ergebnisliste</strong> der Suchanfrage'},
        tWelcomeTextTransparency: { en: 'Apply layer <strong>transparency</strong> using the slider control', de: 'Steuert die Layer-Transparenz mithilfe eines Schiebereglers'},
        tWelcomeTextMetadata: { en: 'Display information (metadata) about selected map services or layers', de: 'Zeigt Informationen (Metadaten) zu Kartendiensten oder Layer an'},
        tWelcomeTextExpand: { en: 'Open and close all layers in the tree structure by one click', de: 'a) Klappt alle Dienste eines <strong>Partners</strong> oder eines <strong>Themas</strong> auf / zu<br/>b) Klappt die  Layer aller  <strong>Aktiven Dienste</strong> auf / zu'},
        tWelcomeTextZoomLayerExtent: { en: 'Zoom to a layer’s visible area', de: 'Zoomt auf den sichtbaren Bereich eines Kartendienstes oder Layers'},
        
        tWelcomeTextInfo: { en: 'A click on the map reveals all available information for that location', de: 'Zeigt die Objektinformationen (Sachattribute) aller aktiven Layer an'},
        tWelcomeTextZoomPrev: { en: 'Go back to the previous extent', de: 'Nimmt schrittweise die vorangegangenen Aktionen zur&uuml;ck'},
        tWelcomeTextZoomNext: { en: 'Go forward to the next extent', de: 'F&uuml;hrt eine zur&uuml;ckgenommene Aktion erneut aus'},
        tWelcomeTextZoom: { en: 'Zoom to the full extent of the map', de: 'Zoomt auf die Originalgr&ouml;&szlig;e der Hintergrundkarte'},
        tWelcomeTextMeassure: { en: 'Measure distance or area on the map', de: 'Misst die Entfernungen oder Fl&auml;chen auf der Karte'},
        
        tWelcomeTextPrint: { en: 'Print the current map to PDF', de: 'Druckt den aktuellen Kartenausschnitt als PDF'},
        tWelcomeTextLoad: { en: 'Open an existing map (Access to this feature requires a personal account)', de: 'L&auml;dt die zuvor gespeicherte Karten (<strong>Mein PortalU</strong> Login erforderlich)'},
        tWelcomeTextSave: { en: 'Saves the current map (Access to this feature requires a personal account)', de: 'Speichert die aktuelle Kartenansicht (<strong>Mein PortalU</strong> Login erforderlich)'},
        tWelcomeTextDownload: { en: 'Saves the current map as XML file (Web Map Context)', de: 'Speichert die Karte als WMC (Web Map Context)'},
        
        tWelcomeTextTipp0: { en: '<h1>You want to see an area of the map in greater detail?</h1><p>Hold the Shift key and drag a zoom box on the map with the left mouse key.</p>', de: '<h1>Sie wollen eine detailliertere Ansicht eines bestimmten Kartenbereichs?</h1><p>Mit gedr&uuml;ckter SHIFT- und linker Maustaste k&ouml;nnen Sie ein Zoom-Rechteck auf den gew&uuml;nschten Bereich aufziehen.</p>'},
        tWelcomeTextTipp1: { en: '<h1>Moving around the map faster?</h1><p>You can navigate in the map by moving the red rectangle in the overview map located in the lower right of the map viewer. Or you can pan the map by pressing the CTRL key and dragging with the left mouse key.</p>', de: '<h1>Sie wollen in der Karte schneller navigieren?</h1><p>Mithilfe des roten Rechtecks im &Uuml;bersichtfenster unten rechts im Kartenviewer k&ouml;nnen Sie im aktuellen Kartenausschnitt navigieren. Alternativ k&ouml;nnen Sie auch mit gedr&uuml;ckter linker Maustaste den Ausschnitt im Kartenfenster verschieben.</p>'},
        tWelcomeTextTipp2: { en: '<h1>You want to visualize your own or external data in our map viewer?</h1><p>All you need is a valid GetCapabilities URL. Our map client supports WMS 1.1.0, 1.1.1 and 1.3.0. Add the GetCapabilities URL by using the plus symbol <span class="iconTable iconAdd" style="display: inline-block;"></span> from the toolbar.</p>', de: '<h1>Sie wollen eigene Kartendienste in unserem Kartenviewer visualisieren?</h1><p>Externe Kartendienste lassen sich &uuml;ber eine GetCapabilities-URL einbinden. Unterst&uuml;tzt werden die Standards 1.1.0, 1.1.1 und 1.3.0. &Ouml;ffnen Sie mit dem <span class="iconTable iconAdd" style="display: inline-block;"></span> das Fenster <strong>Dienst hinzuf&uuml;gen</strong> und geben eine valide GetCapabilities-URL ein.</p>'},
        tWelcomeTextTipp3: { en: '<h1>You have added a digital map, but you can’t see any data?</h1><p>Some layers are only displayed at certain scales. The layers are displayed, when the scale of the current map view and the layer scale match. Otherwise the names of the layers are greyed out (flag active services) and the data are not drawn. You can make the data visible by zooming into the map or by using the feature Zoom-To-Layer <span class="iconTable iconZoomLayerExtent" style="display: inline-block;"></span>.</p>', de: '<h1>Sie haben einen Kartendienst (WMS) geladen, aber keine Informationen werden angezeigt?</h1><p>Die Informationen mancher Layer werden nur innerhalb eines bestimmten Ma&szlig;stabbereichs gezeichnet. Ma&szlig;stabsabh&auml;ngige Layer erkennen Sie an der ausgegrauten Farbgebung. &Auml;ndern Sie den Ma&szlig;stab mithilfe des Navigation-Tools im Kartenfenster oder den Ma&szlig;stabseinstellung unter "Einstellungen".</p>'},
        tWelcomeTextTipp4: { en: '<h1>You are searching maps on a particular topic?</h1><p>Use the search field below the list of topics to find further maps. By entering a keyword, all connected catalogues from our partners will be searched for suitable maps (WMS).</p>', de: '<h1>Sie suchen Karten zu einem bestimmten Umweltthema?</h1><p>Finden Sie eine passende Karte mit Hilfe der Suchfunktion unterhalb der Anbieter- und Themenliste. Mit der Suchanfrage werden s&auml;mtliche angeschlossene Kataloge aus Bund und L&auml;ndern nach passenden Kartendiensten (WMS) durchsucht.</p>'},
        tWelcomeTextTipp5: { en: '<h1>You want to save the current map and load it at a later stage?</h1><p>Take advantage of a personal PortalU Login: Register on <a href="/personalisierungsoptionen"><strong>my PortalU</strong></a> and make use of various options to personalize PortalU.</p>', de: '<h1>Sie wollen eine Kartenansicht speichern und zu einem sp&auml;teren Zeitpunkt wieder aufrufen?</h1><p>Nutzen Sie die Vorteile eines pers&ouml;nlichen PortalU-Logins: Auf der Seite des Men&uuml;reiters <a href="/personalisierungsoptionen"><strong>Mein PortalU</strong></a> k&ouml;nnen Sie sich als Benutzer registrieren. Damit steht Ihnen eine Vielzahl von M&ouml;glichkeiten offen, die Funktionen des Portals nach Ihren Bed&uuml;rfnissen zusammenzustellen.</p>'},
        tWelcomeTextTitle: { en: 'WELCOME TO THE MAP VIEWER', de: 'WILLKOMMEN BEIM KARTENVIEWER'},
        tWelcomeTextSubtitle: { en: 'Our map viewer contains a selection of digital maps (WMS) from public organisations in Germany. More than 200 digital maps are ordered by provider and by topic. Beyond that you can use the search function to find further maps.<br/>The base map (web map service WebAtlasDE.light, &#169; GeoBasis-DE / BKG) is provided by the Federal Agency for Cartography and Geodesy (BKG).', de: 'Unser Kartenviewer enth&auml;lt eine Auswahl digitaler Kartendienste (WMS) von Umweltbeh&ouml;rden aus Bund und L&auml;ndern. Sie haben die Wahl: Mehr als 200 digitale Karten stellen wir zur Zeit &uuml;ber die Themen- und Anbieterliste bereit. Weitere Kartendienste k&ouml;nnen Sie &uuml;ber die Suchmaske recherchieren.<br/>Die Hintergrundkarte (Kartendienst WebAtlasDE.light, &#169; GeoBasis-DE / BKG) wird durch das Bundesamt f&uuml;r Kartographie und Geod&auml;sie (BKG) bereitgestellt.'},
        tWelcomeTextLink1: { en: 'Where does the data come from', de: 'Woher kommen die Daten'},
        tWelcomeTextLink2: { en: 'Detailed help for the map viewer', de: 'Ausf&uuml;hrliche Hilfe zum Kartenviewer'},
        tWmsId: { en: 'WMS ID', de: 'WMS ID' },
        tWmsTitle: { en: 'WMS Title', de: 'WMS Titel' },
        tWmsAbstract: { en: 'WMS Abstract', de: 'WMS Zusammenfassung' },
        tZeigePunktkoordinaten: { en: 'Show point coordinates', de: 'Zeige Punktkoordinaten' },
        tZoomeAufServiceOderLayer: { en: 'Zoom to service or layer extent', de: 'Zoome auf Service oder Layer' },
        tZugriffsbeschraenkung: { en: 'Access restriction', de: 'Zugriffsbeschr&auml;nkung' },
        tZumSpeichernErstEinloggen: { en: 'Login to save.', de: 'Zum Speichern erst einloggen.' },
        tZumEntfernenErstEinenDienstMarkieren: { en: 'To remove a service first select one.', de: 'Zum Entfernen erst einen Dienst markieren.' },
        tZurueck: { en: 'Back', de: 'Zur&uuml;ck' },
        tZusammenfassung: { en: 'Layer Abstract', de: 'Layer Zusammenfassung' },
        Themen: {en: 'Topics', de: 'Themen'},
		Anbieter: {en: 'Provider', de: 'Anbieter'},
		tNominatimSearch: {en: 'Nominatim search ...', de: 'Ortssuche ...'},
		tNominatimLoading: {en: 'Loading ...', de: 'Laden ...'},
		tStaedte: {en: 'Cities', de: 'St&auml;dte'},
		tNoTitle: {en: 'No title', de: 'Kein Titel'},
		tGruppenLayerAnzeigen: {en: 'Display group layer', de: 'Gruppenlayer anzeigen'},
		tMailToMapLink: {en: 'Link to map', de: 'Link zur Karte'},
		tSettingsToolBar: {en: 'Settings', de: 'Einstellungen'},
		tLegendToolBar: {en: 'Legend', de: 'Legende'},
		tTransparenz: {en: 'Transparency', de: 'Transparenz'},
		tInformation: {en: 'Information', de: 'Information'},
		tLoeschen: {en: 'Delete', de: 'L&ouml;schen'},
		tZoomToLayerExtent: {en: 'Zoom to layer', de: 'Zoom auf Layer'},
		tExternServicePanel: {en: 'External Service', de: 'Externer Dienst'},
		tUrlEingabe: {en: 'Add URL', de: 'GetCapabilties-URL hinzufügen'},
		tSuchbegriff: {en: 'Add search term', de: 'Suchbegriff hinzufügen'},
		tPfichtfeld: {en: 'This field is required', de: 'Pflichtfeld'},
		tOptionen: {en: 'Menu', de: 'Menü'},
		tServiceBereichAufUndZuKlappen: {en: 'Expand and collapse service-area', de: 'Service-Bereich ein- und ausblenden'},
		tLoadingFailServiceWFS: {en: 'WFS not supported.', de: 'WFS wird nicht unterst&uuml;tzt.'},
		tLoadingFailServiceException: {en: 'ServiceException.', de: 'GetCapabilities-Anfrage fehlerhaft: ServiceException.'},
		tLoadingFailServiceCSW: {en: 'CSW not supported.', de: 'CSW wird nicht unterst&uuml;tzt.'},
		tLoadingFailServiceNoContent: {en: 'No service content.', de: 'GetCapabilities-Anfrage: Keine Response-Inhalt vorhanden.'},
		tLoadDialogMail: {en: 'Sending saved map as mail.', de: 'Gespeicherte Karte als Email versenden.'},
		tLoadDialogDelete: {en: 'Delete saved map.', de: 'Gespeicherte Karte l&ouml;schen.'},
		tLoadDialogLink: {en: 'Open saved map in a new tab.', de: 'Gespeicherte Karte im neuen Tab &ouml;ffnen.'},
		tLoadingServiceTaskTitle: {en: 'Service seems to be unresponsive.', de: 'Dienst evtl. nicht erreichbar.'},
		tLoadingServiceTaskMessage: {en: 'Cancel loading capabilities?', de: 'M&ouml;chten Sie den Ladenvorgang abbrechen?'}
    }
}

// initialize localizing
if (!(typeof languageCode === 'undefined')) {
	Local.languageCode = languageCode;
}
if (!(Local.languageCode)) {
	var getLang = decodeURIComponent((location.search.match(RegExp("[?|&]languageCode=(.+?)(&|$)"))||[,null])[1]);
	if (getLang) {
		Local.languageCode = getLang;
	}	
}
if (!(Local.languageCode)) {
	Local.languageCode = Local.languageCodeDefault;
}
if (!Local.supportedLanguages[Local.languageCode]) {
	Local.languageCode = Local.languageCodeDefault;
}


