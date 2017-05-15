/*-
 * **************************************************-
 * InGrid Portal Apps
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
define({
    "tree.nodeCut": "Vorhaben/Adressen/Teilb&auml;ume ausschneiden",
    "tree.nodeCopySingle": "Vorhaben/Adressen kopieren",
    "tree.newNodeName": "Neues Vorhaben",

    "dialog.wizard.selectTitle": "Verfahrensschritte auswählen",

    "uvp.title": "UVP Editor",
    "uvp.tree.objectNode": "Verfahren",

    "uvp.form.generalDescription": "Allgemeine Vorhabenbeschreibung",
    "uvp.form.planDescription": "Vorhabenbezeichnung",
    "uvp.form.consideration": "Allgemein",
    "uvp.form.consideration.tooltip": "Für die allgemeine Vorhabenbeschreibung sollte der Einfachheit halber der Text der Bekanntmachung verwendet werden.",
    "uvp.form.address": "Kontaktdaten der verfahrensführenden Dienststelle",

    "uvp.form.categories.uvp": "Zulassungsverfahren",
    "uvp.form.categories.uvpInFront": "Vorgelagerte Verfahren",
    "uvp.form.categories.uvpNegative": "Negative Vorprüfungen",
    "uvp.form.categories.uvpForeign": "Ausländische Vorhaben",

    "uvp.form.categoryIds": "UVP-Nummer",
    "uvp.form.categoryIds.helpMessage": "Hier ist die UVP-Nummer einzugeben, die für das Zulassungsverfahren zutrifft (Mehrfach-Nennungen möglich).<br><br>Es werden die bundesrechtlichen und die für Ihr Land maßgeblichen UVP-Nummern angeboten. Sollte wider Erwarten eine Nummer fehlen, wenden Sie sich bitte an die in Ihrem Land für die Betreuung des UVP-Portals zuständigen Ansprechpartner.<br><br>Die UVP-Nummern dienen der Einordnung des Zulassungsverfahrens in die Kategorien, nach denen auf der Portalseite gesucht werden kann. Außerdem sind sie wichtig zur Erfüllung der Berichtspflicht nach Artikel 12 der UVP-Richtlinie 2011/92/EU, nach der alle sechs Jahre die bis dahin durchgeführten UVP-Verfahren ausgezählt und der EU-Kommission unter Zuordnung zu den Kategorien mitgeteilt werden müssen.",
    "uvp.form.checkExamination": "Eine Änderung/Erweiterung oder ein kumulierendes Vorhaben, für das eine Vorprüfung durchgeführt wurde",
    "uvp.form.checkExamination.helpMessage": "WICHTIG! Bitte Haken setzen, wenn es sich um ein entsprechendes Zulassungsverfahren handelt.<br><br>Bei Änderungen/Erweiterungen wie auch bei kumulierenden Vorhaben wurde häufig eine Vorprüfung zur Feststellung der UVP-Pflicht durchgeführt. Hierfür gibt es nicht immer eine genau passende UVP-Nummer (z.B. wenn es nur eine UVP-Nummer für UVP-pflichtige  Zulassungsverfahren („X“) gibt). Da wegen der Berichtspflicht nach Art. 12 UVP-Richtlinie die Vorprüfungen zu zählen sind, werden die Zulassungsverfahren, bei denen eine Vorprüfung die UVP-Pflicht ergab, durch diese Checkbox erfasst.",
    
    "widget.spatialSearch.helpMessage": "Durch Eingabe einer Adresse, einer bekannten Ortsbezeichnung oder auch von Koordinaten springen die Markierungen der Raumbezugskarte in den Umkreis dieses Bereiches. Mit dem Mauszeiger können die Eckpunkte des Ausschnitts sowie die Positionierung auf der Karte durch Verschieben verändert werden. Auf diese Weise können auch lineare Vorhaben (z.B. Rohrfernleitungen oder Straßenbau) in der Raumbezugskarte annähernd dargestellt werden. Mit der Schaltfläche „Übernehme Ausschnitt“ wird der Raumbezug übernommen. Die Raumbezugskarte wird entsprechender Weise im UVP-Portal abgebildet. Durch die Herstellung des Raumbezugs wird auch die Lage der Markierung auf der allgemeinen Karte des UVP-Portals bestimmt.<br><br>Beispiele:<br><br>Rathausplatz 1<br><br>Alstervorland<br><br>21109",

    "uvp.form.addPhase": "Verfahrensschritt hinzufügen",
    "uvp.form.dialog.addPhase.title": "Verfahrensschritt erstellen",
    "uvp.form.dialog.addPhase.text": "Wählen Sie den zu erstellenden Verfahrensschritt aus:",
    "uvp.form.dialog.addPhase.phase1": "Öffentliche Auslegung",
    "uvp.form.dialog.addPhase.phase2": "Erörterungstermin",
    "uvp.form.dialog.addPhase.phase3": "Zulassungsentscheidung",

    "uvp.form.phase1.rubric": "Öffentliche Auslegung",
    "uvp.form.phase1.rubric.helpMessage": "Öffentliche Auslegung der Unterlagen<br><br>Rechtzeitig vor Beginn der Auslegung müssen die Informationen nach § 9 Abs. 1a UVPG bzw. entsprechenden fachrechtlichen Regelungen (z.B. nach der 9. BImSchV) über die Öffentlichkeitsbeteiligung öffentlich bekannt gemacht werden und in dieses Portal eingestellt werden. Die Informationen zu § 9 Abs. 1a UVPG werden durch den Bekanntmachungstext, mit dem auch die Auslegung der Unterlagen bekannt gemacht wird, gegeben. Im UVP-Portal geschieht die Mitteilung dieser Informationen durch das Hochladen/ Verlinken des Bekanntmachungstextes im Feld „Auslegungsinformationen“.<br><br>Nach § 9 Abs.1b sind die Unterlagen nach § 6 UVPG und die entscheidungserheblichen Berichte und Empfehlungen betreffend das Zulassungsverfahren, die der zuständigen Behörde zum Zeitpunkt des Beginns des Beteiligungsverfahrens vorgelegen haben, auszulegen. Im UVP-Portal werden die Unterlagen nach § 6 UVPG im Feld „UVP-Bericht, ggf. Antragsunterlagen“ hochgeladen/verlinkt.<br><br>In diesem Feld können auch die Antragsunterlagen hochgeladen werden. Dies kann aus Gründen der Transparenz geschehen oder der Arbeitserleichterung dienen, indem die Antragsunterlagen, die bisher auf der Internet-Seite der Dienststelle veröffentlicht wurden, im UVP-Portal dargestellt werden und auf der Internet-Seite der Dienststelle nur noch ein Link auf die entsprechende Seite des UVP-Portals gezeigt wird.",
    "uvp.form.phase1.dateFrom": "Zeitraum der Auslegung am/vom",
    "uvp.form.phase1.dateFrom.helpMessage": "Eintragung des Zeitraums der Auslegung - Pflichtfeld.<br><br>Wichtig: Genau in diesem Zeitraum müssen die Unterlagen auch im UVP-Portal zu lesen sein. Die nach dem UVPG auszulegenden Unterlagen können auch danach in UVP-Portal lesbar bleiben. Die Lesbarkeit wird durch die Eingabe des Auslegungszeitraums nicht beeinflusst.<br><br>Beispiel:<br><br>Vom: TT.MM.JJJJ  bis: TT.MM.JJJJ ",
    "uvp.form.phase1.dateTo": "bis",
    "uvp.form.phase1.dateTo.helpMessage": "Eintragung des Zeitraums der Auslegung - Pflichtfeld.<br><br>Wichtig: Genau in diesem Zeitraum müssen die Unterlagen auch im UVP-Portal zu lesen sein. Die nach dem UVPG auszulegenden Unterlagen können auch danach in UVP-Portal lesbar bleiben. Die Lesbarkeit wird durch die Eingabe des Auslegungszeitraums nicht beeinflusst.<br><br>Beispiel:<br><br>Vom: TT.MM.JJJJ  bis: TT.MM.JJJJ ",
    "uvp.form.phase1.technicalDocs": "Auslegungsinformationen",
    "uvp.form.phase1.technicalDocs.helpMessage": "Auslegungsinformationen (= Bekanntmachungstext) hochladen/verlinken. Der Namen des Dokuments sollte erkennen lassen, um was es sich handelt – Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>170719 A26O1 Amtl Anz Auslegung",
    "uvp.form.phase1.applicationDocs": "UVP Bericht/Antragsunterlagen",
    "uvp.form.phase1.applicationDocs.helpMessage": "UVP-Bericht/Antragsunterlagen hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt – Pflichtfeld.<br><br>Hier sind die Unterlagen nach § 6 UVPG einzustellen. Das sind die entscheidungserheblichen Unterlagen über die Umweltauswirkungen des Vorhabens, die der Träger des Vorhabens nach § 6 Abs. 1 UVPG der zuständigen Behörde zu Beginn des Verfahrens vorzulegen hat, in dem die Umweltverträglichkeit geprüft wird. Inhalt und Umfang ergeben sich aus § 6 Abs. 2 bis 5 UVPG.<br><br>Sollen auf freiwilliger Basis zusätzlich auch die anderen Antragsunterlagen in das UVP-Portal gestellt werden, kann dies an dieser Stelle geschehen.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>UVP-Bericht",
    "uvp.form.phase1.reportsRecommendationsDocs": "Berichte und Empfehlungen",
    "uvp.form.phase1.reportsRecommendationsDocs.helpMessage": "Ggf. Berichte und Empfehlungen hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt.<br><br>Wenn zum Zeitpunkt des Beginns des Beteiligungsverfahrens entscheidungserhebliche Berichte und Empfehlungen betreffend das Vorhaben bei der zuständigen Behörde vorgelegen haben, sind diese nach § 9 Abs. 1b Nr. 2 UVPG zur Einsicht für die Öffentlichkeit auszulegen und daher auch in das UVP-Portal einzustellen. Hierbei kann es sich um bereits vorab eingegangene Stellungnahmen der zu beteiligenden Behörden, aber auch von der zuständigen Behörde eingeholte besondere Gutachten zu dem beabsichtigten Vorhaben handeln (so Wagner in: Hoppe/Beckmann, UVPG-Kommentar, § 9 Rdnr. 32). Da solche Unterlagen nicht unbedingt vorliegen, handelt es sich nicht um ein Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>2016-10-10 Zustandsanalyse FFH-Gebiet Oberes Hochtal",
    "uvp.form.phase1.moreDocs": "Weitere Unterlagen",
    "uvp.form.phase1.moreDocs.helpMessage": "Ggf. weitere Unterlagen - auch nach Ende der Auslegung - hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt.<br><br>Abgesehen von den nach § 9 Abs. 1b Satz 1 UVPG auszulegenden Unterlagen kann es weitere Unterlagen geben, deren Veröffentlichung im UVP-Portal sich anbietet, ohne dass hierzu eine Pflicht bestünde. Dies kann z.B. für weitere Informationen im Sinne des § 9 Abs. 1b Satz 2 UVPG, die für die Entscheidung über die Zulässigkeit des Vorhabens von Bedeutung sein können, die der zuständigen Behörde aber erst nach Beginn des Beteiligungsverfahrens vorliegen, gelten. Solche Informationen sind nach den Bestimmungen des Bundes und der Länder über den Zugang zu Umweltinformationen zugänglich zu machen, also nach Antrag auf Zugang zu den Umweltinformationen bei der zuständigen Behörde.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.",
    "uvp.form.phase1.publicationDocs": "Bekanntmachung",

    "uvp.form.phase2.rubric": "Erörterungstermin",
    "uvp.form.phase2.rubric.helpMessage": "Nach § 9 Abs. 1 Satz 3 UVPG muss das Beteiligungsverfahren den Anforderungen des § 73 Abs. 3 Satz 2, Abs. 4 bis 7 Verwaltungsverfahrensgesetz entsprechen. Es ist daher, sofern nichts Abweichendes geregelt ist, ein Erörterungstermin durchzuführen, der mindestens eine Woche vorher ortsüblich bekannt zu machen ist. Die Informationen zum Erörterungstermin müssen im UVP-Portal zu dem Zeitpunkt zu lesen sein, an dem sie öffentlich bekanntgemacht werden.",
    "uvp.form.phase2.dateFrom": "Zeitraum der Erörterung am/vom",
    "uvp.form.phase2.dateFrom.helpMessage": "Eintragung des Zeitraums der Erörterung - Pflichtfeld.<br><br>Wenn der Erörterungstermin an einem Tag stattfinden wird, reicht es aus, das Feld „Am/vom“ auszufüllen. Wird der Erörterungstermin in mehrere Termine aufgeteilt, ist der Zeitraum vom ersten bis zum letzten Termin einzugeben. Eine Listung einzelner Termine ist nicht möglich. Diese Information wird sich aber aus dem Bekanntmachungstext bzw. den Informationen zum Erörterungstermin ergeben, auf die der Nutzer bzw. die Nutzerin des UVP-Portal durch einen Hinweis verwiesen wird.<br><br>Beispiel:<br><br>Am/vom: TT.MM.JJJJ  bis: TT.MM.JJJJ",
    "uvp.form.phase2.dateTo": "bis",
    "uvp.form.phase2.dateTo.helpMessage": "Eintragung des Zeitraums der Erörterung - Pflichtfeld.<br><br>Wenn der Erörterungstermin an einem Tag stattfinden wird, reicht es aus, das Feld „Am/vom“ auszufüllen. Wird der Erörterungstermin in mehrere Termine aufgeteilt, ist der Zeitraum vom ersten bis zum letzten Termin einzugeben. Eine Listung einzelner Termine ist nicht möglich. Diese Information wird sich aber aus dem Bekanntmachungstext bzw. den Informationen zum Erörterungstermin ergeben, auf die der Nutzer bzw. die Nutzerin des UVP-Portal durch einen Hinweis verwiesen wird.<br><br>Beispiel:<br><br>Am/vom: TT.MM.JJJJ  bis: TT.MM.JJJJ",
    "uvp.form.phase2.considerationDocs": "Informationen zum Erörterungstermin",
    "uvp.form.phase2.considerationDocs.helpMessage": "Informationen zum Erörterungstermin hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt – Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>170815 Bekanntmachung Ferienpark Garlau",

    "uvp.form.phase3.rubric": "Entscheidung über die Zulassung",
    "uvp.form.phase3.rubric.helpMessage": "Nach § 27 UVPGneu (noch im Gesetzgebungsverfahren) hat die zuständige Behörde in entsprechender Anwendung des § 74 Absatz 5 Satz 2 des Verwaltungsverfahrensgesetzes die Entscheidung zur Zulassung oder Ablehnung des Vorhabens öffentlich bekannt zu machen sowie in entsprechender Anwendung des § 74 Absatz 4 Satz 2 des Verwaltungsverfahrensgesetzes den Bescheid zur Einsicht auszulegen. Die Entscheidung ist zugleich im UVP-Portal zu veröffentlichen.",
    "uvp.form.phase3.approvalDate": "Datum der Entscheidung",
    "uvp.form.phase3.approvalDate.helpMessage": "Eintragung des Datums der Entscheidung - Pflichtfeld.<br><br>Beispiel:<br><br>TT.MM.JJJJ",
    "uvp.form.phase3.dateFrom": "Zeitraum der Auslegung am/vom",
    "uvp.form.phase3.dateTo": "bis",
    "uvp.form.phase3.approvalDocs": "Auslegungsinformationen ",
    "uvp.form.phase3.approvalDocs.helpMessage": "Auslegungsinformationen (= Bekanntmachungstext) hochladen/verlinken. Der Namen des Dokuments sollte erkennen lassen, um was es sich handelt – Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>2017-04-04 Biblis Staatsanzeiger",
    "uvp.form.phase3.designDocs": "Entscheidung",
    "uvp.form.phase3.designDocs.helpMessage": "Entscheidung über die Zulassung, ggf. mit Anlagen hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt - Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>17.03.30 Genehmigung Biblis",

    "uvp.form.table.docs.title": "Dokument",
    "uvp.form.table.docs.link": "Link",
    "uvp.form.table.docs.type": "Typ",
    "uvp.form.table.docs.size": "Größe",
    "uvp.form.table.docs.expires": "Gültig bis",

    "uvp.form.deletePhase": "Verfahrensschritt löschen",
    "uvp.form.deletePhase.confirmText": "Möchten Sie wirklich diesen Verfahrensschritt entfernen?",

    "uvp.form.foreign.address": "Kontakt deutsche Behörde",
    "uvp.form.foreign.generalDescription": "Vorhabenbeschreibung",

    "uvp.form.spatial.address": "Kontaktdaten der verfahrensführenden Behörde",
    "uvp.form.spatial.generalDescription": "Allgemeine Vorhabenbeschreibung",

    "uvp.form.negative.checkExamination": "Eine Änderung/Erweiterung oder ein kumulierendes Vorhaben",

    "ui.toolbar.CutCaption": "Verfahren/Adressen/Teilbaum ausschneiden",
    "ui.toolbar.CopyCaption": "Verfahren/Adressen kopieren",
    "ui.toolbar.DiscardCaption": "Änderungen am aktuellen Verfahren/Adresse verwerfen",
    "ui.toolbar.DelSubTreeCaption": "Ausgewähltes Verfahren/Teilbaum löschen",
    "ui.toolbar.finalDeleteCaption": "Ausgewähltes Verfahren/Teilbaum endgültig löschen",
    "ui.toolbar.PreviousCaption": "Klicken, um zum vorherigen Verfahren/Adresse zu gehen. Gedrückt halten für Verlauf.",
    "ui.toolbar.NextCaption": "Klicken, um zum nächsten Verfahren/Adresse zu gehen. Gedrückt halten für Verlauf.",

    "validation.error.document.table.invalid": "Die Tabelle muss unter 'Dokument' und 'Link' einen Eintrag haben.",

    "uvp.address.form.categories.address": "Adresse"
});
