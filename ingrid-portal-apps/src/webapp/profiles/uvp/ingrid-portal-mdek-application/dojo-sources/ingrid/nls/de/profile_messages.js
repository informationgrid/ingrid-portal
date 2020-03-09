/*-
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    "tree.nodeCut": "Verfahren/Adressen/Teilb&auml;ume ausschneiden",
    "tree.nodeCopySingle": "Verfahren/Adressen kopieren",
    "tree.newNodeName": "Neues Verfahren",

    "dialog.wizard.selectTitle": "Verfahrensschritte auswählen",

    "uvp.title": "UVP Editor",
    "uvp.tree.objectNode": "Verfahren",

    "help.uvp.form.title.title": "Titel",
    "help.uvp.form.title.text": "Angabe einer kurzen prägnanten Bezeichnung des Zulassungsverfahrens -  Pflichtfeld.",
    "help.uvp.form.title.example": "Neubau der U-Bahn-Haltestelle Oldenfelde<br><br>Neubau der A26 Ost ('Hafenpassage') Abschnitt 6a  AK HH-Süderelbe (A7) bis AS HH-Hafen Süd (Moorburg östlich A7)<br><br>Windpark Windhausen",
    "help.uvp.form.description.title": "Allgemeine Vorhabenbeschreibung",
    "help.uvp.form.description.text": "Kurzbeschreibung, z.B. durch teilweise Verwendung des Bekanntmachungstexts -  Pflichtfeld.<br><br>Hier soll das Vorhaben kurz beschrieben werden, damit die Öffentlichkeit erfährt, worum es  bei diesem Verfahren überhaupt geht. Auf Verständlichkeit für fachfremde Dritte ist zu achten. Die Beschreibung bildet auch auf der Portalseite die Grundinformation, die dauernd und unabhängig vom aktuellen Verfahrensstand abgebildet wird. Anders als hier bei der Eingabe ist dort der ganze Text ohne Scroll-Notwendigkeit zu lesen.<br><br>Es bietet sich an, für den Text die entsprechenden Teile aus dem Bekanntmachungstext für die Auslegung zu verwenden. Dies dient auch der Minimierung des Aufwands. Denn dort wird das Vorhaben bereits allgemein verständlich beschrieben. Die Information, dass ein Antrag bei … auf Zulassung dieses Vorhabens gestellt wurde und der Hinweis auf die UVP-Pflicht nach … können daraus ebenfalls übernommen werden. Statt der Übernahme von Teile des Bekanntmachungstextes kommt ggf. auch die Übernahme einer aussagekräftigen Betreffzeile des Bekanntmachungstextes in Betracht.<br><br>Das Feld muss ausgefüllt werden, damit das Zulassungsverfahren abgespeichert werden kann -  Pflichtfeld.",
    "help.uvp.form.addresses.title": "Kontaktdaten der verfahrensführenden Dienststelle",
    "help.uvp.form.addresses.text": "Kontaktdaten der verfahrensführenden Dienststelle', 'Eintrag von Adressverweisen zu Ansprechpartnern.<br><br>Eintrag von Adressverweisen zu Personen oder Institutionen, die weitergehende Informationen zum aktuellen Zulassungsverfahren geben können (Ansprechpartner). Die Eingabe bzw. Änderung erfolgt über den Link 'Adresse hinzufügen'. Hier wird aus den im UVP-Portal bereits vorhandenen Adressen ausgewählt und die ausgewählte Adresse durch Anklicken der Schaltfläche im Kontaktdatenfeld angelegt.<br><br>Sollte die gewünschte Adresse noch nicht in der Auswahlliste vorhanden sein, muss die Eingabe unterbrochen und zwischengespeichert werden und die Adresse separat eingegeben werden.<br><br>Sollte eine Adresse versehentlich falsch ausgewählt worden sein, kann sie nicht gelöscht werden, wenn sie der einzige Eintrag ist. Fügen Sie in diesem Fall zunächst die richtige Adresse in ein weiteres Feld ein. Anschließend kann die falsche Adresse gelöscht werden.",

    "uvp.form.generalDescription": "Allgemeine Vorhabenbeschreibung",
    "uvp.form.planDescription": "Vorhabenbezeichnung",
    "uvp.form.consideration": "Allgemein",
    "uvp.form.consideration.tooltip": "Für die allgemeine Vorhabenbeschreibung sollte der Einfachheit halber der Text der Bekanntmachung verwendet werden.",
    "uvp.form.address": "Kontaktdaten der verfahrensführenden Dienststelle",

    "uvp.form.categories.uvp": "Zulassungsverfahren",
    "uvp.form.categories.uvpInFront": "Vorgelagerte Verfahren",
    "uvp.form.categories.uvpNegative": "Vorprüfungen, negativ",
    "uvp.form.categories.uvpForeign": "Ausländische Vorhaben",

    "uvp.form.applicationReceipt": "Eingang des Antrags",
    "uvp.form.applicationReceipt.helpMessage": "Geben Sie das Datum des Einganges des Antrags an. Das Datum wird im Portal nicht angezeigt und dient nur zur statistischen Zwecken.",
    "uvp.form.applicationReceipt.invalid": "Das Datum muss vor dem Beginn der ersten Auslegung sein.",

    "uvp.form.categoryIds": "UVP-Nummer",
    "uvp.form.categoryIds.helpMessage": "Hier ist die UVP-Nummer einzugeben, die für das Zulassungsverfahren zutrifft (Mehrfach-Nennungen möglich).<br><br>Es werden die bundesrechtlichen und die für Ihr Land maßgeblichen UVP-Nummern angeboten. Sollte wider Erwarten eine Nummer fehlen, wenden Sie sich bitte an die in Ihrem Land für die Betreuung des UVP-Portals zuständigen Ansprechpartner.<br><br>Die UVP-Nummern dienen der Einordnung des Zulassungsverfahrens in die Kategorien, nach denen auf der Portalseite gesucht werden kann. Außerdem sind sie wichtig zur Erfüllung der Berichtspflicht nach Artikel 12 der UVP-Richtlinie 2011/92/EU, nach der alle sechs Jahre die bis dahin durchgeführten UVP-Verfahren ausgezählt und der EU-Kommission unter Zuordnung zu den Kategorien mitgeteilt werden müssen.",
    "uvp.form.preExaminationAccomplished.label": "Vorprüfung durchgeführt",
    "uvp.form.preExaminationAccomplished.yes": "Ja",
    "uvp.form.preExaminationAccomplished.no": "Nein",
    "uvp.form.preExaminationAccomplished.required": "Es muss eine Auswahl getroffen werden.",

    "widget.spatialSearch.helpMessage": "Durch Eingabe einer Adresse, einer bekannten Ortsbezeichnung oder auch von Koordinaten springen die Markierungen der Raumbezugskarte in den Umkreis dieses Bereiches. Mit dem Mauszeiger können die Eckpunkte des Ausschnitts sowie die Positionierung auf der Karte durch Verschieben verändert werden. Auf diese Weise können auch lineare Vorhaben (z.B. Rohrfernleitungen oder Straßenbau) in der Raumbezugskarte annähernd dargestellt werden. Mit der Schaltfläche „Übernehme Ausschnitt“ wird der Raumbezug übernommen. Die Raumbezugskarte wird entsprechender Weise im UVP-Portal abgebildet. Durch die Herstellung des Raumbezugs wird auch die Lage der Markierung auf der allgemeinen Karte des UVP-Portals bestimmt.<br><br>Beispiele:<br><br>Rathausplatz 1<br><br>Alstervorland<br><br>21109",

    "uvp.form.addPhase": "Verfahrensschritt hinzufügen",
    "uvp.form.dialog.addPhase.title": "Verfahrensschritt erstellen",
    "uvp.form.dialog.addPhase.text": "Wählen Sie den zu erstellenden Verfahrensschritt aus:",
    "uvp.form.dialog.addPhase.phase1": "Öffentliche Auslegung",
    "uvp.form.dialog.addPhase.phase2": "Erörterungstermin",
    "uvp.form.dialog.addPhase.phase3": "Zulassungsentscheidung",

    "contextmenu.table.expireClicked": "Gültig bis-Datum festlegen",

    "uvp.form.phase1.rubric": "Öffentliche Auslegung",
    "uvp.form.phase1.rubric.helpMessage": "Öffentliche Auslegung der Unterlagen<br><br>Rechtzeitig vor Beginn der Auslegung müssen die Informationen nach § 19 Abs. 1 UVPG bzw. entsprechenden fachrechtlichen Regelungen (z.B. nach der 9. BImSchV) über die Öffentlichkeitsbeteiligung öffentlich bekannt gemacht werden und in dieses Portal eingestellt werden. Die Informationen zu § 19 Abs. 1 UVPG werden durch den Bekanntmachungstext, mit dem auch die Auslegung der Unterlagen bekannt gemacht wird, gegeben. Im UVP-Portal geschieht die Mitteilung dieser Informationen durch das Hochladen/ Verlinken des Bekanntmachungstextes im Feld „Auslegungsinformationen“.<br><br>Nach § 19 Abs.2 UVPG sind der UVP-Bericht und die das Vorhaben betreffenden entscheidungserheblichen Berichte und Empfehlungen, die der zuständigen Behörde zum Zeitpunkt des Beginns des Beteiligungsverfahrens vorgelegen haben, auszulegen. Im UVP-Portal wird der UVP-Bericht im Feld „UVP-Bericht, ggf. Antragsunterlagen“ hochgeladen/verlinkt.<br><br>In diesem Feld können auch die Antragsunterlagen hochgeladen werden. Dies kann aus Gründen der Transparenz geschehen oder der Arbeitserleichterung dienen, indem die Antragsunterlagen, die bisher auf der Internet-Seite der Dienststelle veröffentlicht wurden, im UVP-Portal dargestellt werden und auf der Internet-Seite der Dienststelle nur noch ein Link auf die entsprechende Seite des UVP-Portals gezeigt wird.<br><br>Die das Vorhaben betreffenden entscheidungserheblichen Berichte und Empfehlungen, die der zuständigen Behörde zum Zeitpunkt des Beginns des Beteiligungsverfahrens vorgelegen haben, werden im Feld Berichte und Empfehlungen hochgeladen, das kein Pflichtfeld ist.",
    "uvp.form.phase1.dateFrom": "Zeitraum der Auslegung von",
    "uvp.form.phase1.dateFrom.helpMessage": "Eintragung des Zeitraums der Auslegung - Pflichtfeld.<br><br>Wichtig: Genau in diesem Zeitraum müssen die Unterlagen auch im UVP-Portal zu lesen sein. Die nach dem UVPG auszulegenden Unterlagen können auch danach in UVP-Portal lesbar bleiben. Die Lesbarkeit wird durch die Eingabe des Auslegungszeitraums nicht beeinflusst.<br><br>Beispiel:<br><br>Vom: TT.MM.JJJJ  bis: TT.MM.JJJJ ",
    "uvp.form.phase1.dateTo": "bis",
    "uvp.form.phase1.dateTo.helpMessage": "Eintragung des Zeitraums der Auslegung - Pflichtfeld.<br><br>Wichtig: Genau in diesem Zeitraum müssen die Unterlagen auch im UVP-Portal zu lesen sein. Die nach dem UVPG auszulegenden Unterlagen können auch danach in UVP-Portal lesbar bleiben. Die Lesbarkeit wird durch die Eingabe des Auslegungszeitraums nicht beeinflusst.<br><br>Beispiel:<br><br>Vom: TT.MM.JJJJ  bis: TT.MM.JJJJ ",
    "uvp.form.phase1.eachTable.publishLater": "Erst mit Beginn des Auslegungszeitraumes veröffentlichen",
    "uvp.form.phase1.eachTable.publishLater.helpMessage": "Wenn diese Checkbox aktiviert ist, werden die Dokumente in der oberen Tabelle erst bei Erreichen des Beginn des Auslegungszeitraums veröffentlicht.",
    "uvp.form.phase1.technicalDocs": "Auslegungsinformationen",
    "uvp.form.phase1.technicalDocs.helpMessage": "Auslegungsinformationen (= Bekanntmachungstext) hochladen/verlinken. Der Namen des Dokuments sollte erkennen lassen, um was es sich handelt – Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>170719 A26O1 Amtl Anz Auslegung",
    "uvp.form.phase1.applicationDocs": "UVP Bericht/Antragsunterlagen",
    "uvp.form.phase1.applicationDocs.helpMessage": "UVP-Bericht/Antragsunterlagen hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt – Pflichtfeld.<br><br>Hier ist der UVP-Bericht § 19 Abs. 2 Nr. 1 UVPG einzustellen.<br><br>Sollen auf freiwilliger Basis zusätzlich auch die anderen Antragsunterlagen in das UVP-Portal gestellt werden, kann dies an dieser Stelle geschehen.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>UVP-Bericht",
    "uvp.form.phase1.reportsRecommendationsDocs": "Berichte und Empfehlungen",
    "uvp.form.phase1.reportsRecommendationsDocs.helpMessage": "Ggf. Berichte und Empfehlungen hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt.<br><br>Wenn zum Zeitpunkt des Beginns des Beteiligungsverfahrens entscheidungserhebliche Berichte und Empfehlungen betreffend das Vorhaben bei der zuständigen Behörde vorgelegen haben, sind diese nach § 19 Abs. 2 Nr. 2 UVPG zur Einsicht für die Öffentlichkeit auszulegen und daher auch in das UVP-Portal einzustellen. Hierbei kann es sich um bereits vorab eingegangene Stellungnahmen der zu beteiligenden Behörden, aber auch von der zuständigen Behörde eingeholte besondere Gutachten zu dem beabsichtigten Vorhaben handeln (so Wagner in: Hoppe/Beckmann, UVPG-Kommentar, § 9 Rdnr. 32). Da solche Unterlagen nicht unbedingt vorliegen, handelt es sich nicht um ein Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>2016-10-10 Zustandsanalyse FFH-Gebiet Oberes Hochtal",
    "uvp.form.phase1.moreDocs": "Weitere Unterlagen",
    "uvp.form.phase1.moreDocs.helpMessage": "Ggf. weitere Unterlagen - auch nach Ende der Auslegung - hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt.<br><br>Abgesehen von den nach § 19 Abs. 2 UVPG auszulegenden Unterlagen kann es weitere Unterlagen geben, deren Veröffentlichung im UVP-Portal sich anbietet, ohne dass hierzu eine Pflicht bestünde. Dies kann z.B. für weitere Informationen im Sinne des § 19 Abs. 3 UVPG, die für die Entscheidung über die Zulässigkeit des Vorhabens von Bedeutung sein können, die der zuständigen Behörde aber erst nach Beginn des Beteiligungsverfahrens vorliegen, gelten. Solche Informationen sind nach den Bestimmungen des Bundes und der Länder über den Zugang zu Umweltinformationen zugänglich zu machen, also nach Antrag auf Zugang zu den Umweltinformationen bei der zuständigen Behörde.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.",
    "uvp.form.phase1.publicationDocs": "Bekanntmachung",

    "uvp.form.phase2.rubric": "Erörterungstermin",
    "uvp.form.phase2.rubric.helpMessage": "Nach § 18 Abs. 1 Satz 4 UVPG muss das Beteiligungsverfahren den Anforderungen des § 73 Abs. 3 Satz 1, Abs. 5 bis 7 Verwaltungsverfahrensgesetz entsprechen. Es ist daher, sofern nichts Abweichendes geregelt ist, ein Erörterungstermin durchzuführen, der mindestens eine Woche vorher ortsüblich bekannt zu machen ist. Die Informationen zum Erörterungstermin müssen im UVP-Portal zu dem Zeitpunkt zu lesen sein, an dem sie öffentlich bekanntgemacht werden.",
    "uvp.form.phase2.dateFrom": "Zeitraum der Erörterung am/vom",
    "uvp.form.phase2.dateFrom.helpMessage": "Eintragung des Zeitraums der Erörterung - Pflichtfeld.<br><br>Wenn der Erörterungstermin an einem Tag stattfinden wird, reicht es aus, das Feld „Am/vom“ auszufüllen. Wird der Erörterungstermin in mehrere Termine aufgeteilt, ist der Zeitraum vom ersten bis zum letzten Termin einzugeben. Eine Listung einzelner Termine ist nicht möglich. Diese Information wird sich aber aus dem Bekanntmachungstext bzw. den Informationen zum Erörterungstermin ergeben, auf die der Nutzer bzw. die Nutzerin des UVP-Portal durch einen Hinweis verwiesen wird.<br><br>Beispiel:<br><br>Am/vom: TT.MM.JJJJ  bis: TT.MM.JJJJ",
    "uvp.form.phase2.dateTo": "bis",
    "uvp.form.phase2.dateTo.helpMessage": "Eintragung des Zeitraums der Erörterung - Pflichtfeld.<br><br>Wenn der Erörterungstermin an einem Tag stattfinden wird, reicht es aus, das Feld „Am/vom“ auszufüllen. Wird der Erörterungstermin in mehrere Termine aufgeteilt, ist der Zeitraum vom ersten bis zum letzten Termin einzugeben. Eine Listung einzelner Termine ist nicht möglich. Diese Information wird sich aber aus dem Bekanntmachungstext bzw. den Informationen zum Erörterungstermin ergeben, auf die der Nutzer bzw. die Nutzerin des UVP-Portal durch einen Hinweis verwiesen wird.<br><br>Beispiel:<br><br>Am/vom: TT.MM.JJJJ  bis: TT.MM.JJJJ",
    "uvp.form.phase2.considerationDocs": "Informationen zum Erörterungstermin",
    "uvp.form.phase2.considerationDocs.helpMessage": "Informationen zum Erörterungstermin hochladen/verlinken. Die Namen der Dokumente sollten erkennen lassen, um was es sich handelt – Pflichtfeld.<br><br>In der Spalte „Gültig bis“ kann im Format TT.MM.JJJJ eingegeben werden, bis zu welchem Tag (diesen eingeschlossen) das hochgeladene/verlinkte Dokument im UVP-Portal lesbar sein soll. Es bleibt auch nach Ablauf der Frist im Editor lesbar und kann ggf. auch wieder veröffentlicht werden. Für die Bekanntmachungstexte und die ausschließlich nach UVPG auszulegenden Unterlagen wird die Ausfüllung dieses Feldes erst am Ende der Darstellung des Zulassungsverfahrens im UVP-Portal in Frage kommen.<br><br>Beispiel:<br><br>170815 Bekanntmachung Ferienpark Garlau",

    "uvp.form.phase3.rubric": "Entscheidung über die Zulassung",
    "uvp.form.phase3.rubric.helpMessage": "Nach § 27 UVPG hat die zuständige Behörde in entsprechender Anwendung des § 74 Absatz 5 Satz 2 des Verwaltungsverfahrensgesetzes die Entscheidung zur Zulassung oder Ablehnung des Vorhabens öffentlich bekannt zu machen sowie in entsprechender Anwendung des § 74 Absatz 4 Satz 2 des Verwaltungsverfahrensgesetzes den Bescheid zur Einsicht auszulegen. Die Entscheidung ist zugleich im UVP-Portal zu veröffentlichen.",
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
    "uvp.form.negative.address": "Kontaktdaten der federführenden Dienststelle",
    "uvp.form.negative.approvalDate": "Datum der Entscheidung",
    "uvp.form.negative.approvalDate.helpMessage": "Eintragung des Datums der Entscheidung - Pflichtfeld.<br><br>Beispiel:<br><br>TT.MM.JJJJ",
    "uvp.form.negative.relevantDocs": "Ergebnis der UVP-Vorprüfung",
    "uvp.form.negative.relevantDocs.helpMessage": "...",

    "ui.toolbar.CutCaption": "Verfahren/Adressen/Teilbaum ausschneiden",
    "ui.toolbar.CopyCaption": "Verfahren/Adressen kopieren",
    "ui.toolbar.DiscardCaption": "Änderungen am aktuellen Verfahren/Adresse verwerfen",
    "ui.toolbar.DelSubTreeCaption": "Ausgewähltes Verfahren/Teilbaum löschen",
    "ui.toolbar.finalDeleteCaption": "Ausgewähltes Verfahren/Teilbaum endgültig löschen",
    "ui.toolbar.PreviousCaption": "Klicken, um zum vorherigen Verfahren/Adresse zu gehen. Gedrückt halten für Verlauf.",
    "ui.toolbar.NextCaption": "Klicken, um zum nächsten Verfahren/Adresse zu gehen. Gedrückt halten für Verlauf.",

    "validation.error.document.table.invalid": "Die Tabelle muss unter 'Dokument' und 'Link' einen Eintrag haben.",

    "uvp.address.form.categories.address": "Adresse",

    "page.breadcrumb.pageUvpStatistic": "UVP Statistik",

    "uvp.error.init": "Bei der Initialisierung von UVP ist ein Problem aufgetreten. Für die Initialisierung muss man sich als Katalogadministrator oder Metadatenadministrator anmelden.",

    "dialog.object.deleteMessage": "Achtung! Die Datensätze werden endgültig gelöscht und können nicht wiederhergestellt werden. Sie werden auch in den Statistiken nicht mehr berücksichtigt, obwohl dies bei abgeschlossenen Verfahren sowie bei negativen Vorprüfungen für die Berichtspflicht gegenüber der EU-Kommission erforderlich ist!<br><br>Sollen die folgenden Objekte ${0} wirklich endg&uuml;ltig gel&ouml;scht werden?",
    "dialog.object.deleteChildrenMessage": "Sollen die folgenden Objekte ${0} und alle untergeordneten Objekte wirklich endg&uuml;ltig gel&ouml;scht werden?<br><br>Die Datensätze werden endgültig gelöscht und können nicht wiederhergestellt werden. Sie werden auch in den Statistiken nicht mehr berücksichtigt, obwohl dies bei abgeschlossenen Verfahren sowie bei negativen Vorprüfungen für die Berichtspflicht gegenüber der EU-Kommission erforderlich ist!",

});
