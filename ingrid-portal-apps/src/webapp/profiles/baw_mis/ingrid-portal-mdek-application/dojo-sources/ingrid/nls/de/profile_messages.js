/*
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
    "dialog.simulation.parameter.title": "Simulationsparameter/-Größe",
    "dialog.lfs.link.parameter.title": "Links zum Langfristspeicher",
    "dialog.lfs.move.warn": "Die ausgewählten Dateien aus dem LFS-Eingang werden hiermit in die LFS-Ablage verschoben. Die Dateien können danach nicht mehr geändert, gelöscht oder ergänzt werden.",

    "ui.obj.baw.auftragsnummer.title": "PSP-Element",
    "ui.obj.baw.auftragsnummer.help": "PSP-Element",
    "ui.obj.baw.auftragstitel.title": "Auftragstitel",
    "ui.obj.baw.auftragstitel.help": "Titel des BAW/WSV Auftrags",
    "ui.obj.baw.bwastr.table.column.km_end": "Km-bis",
    "ui.obj.baw.bwastr.table.column.km_start": "Km-von",
    "ui.obj.baw.bwastr.table.column.name": "BWaStr.-Streckenname",
    "ui.obj.baw.bwastr.table.help": "Streckenabschnitte der Bundeswasserstrassen nach VV/WSV 1103",
    "ui.obj.baw.bwastr.table.title": "Streckenabschnitte",
    "ui.obj.baw.hierarchy.level.name.title": "Bezeichnung der Hierarchieebene",
    "ui.obj.baw.hierarchy.level.name.help": "Bezeichnung der Hierarchieebene",
    "ui.obj.baw.keyword.catalogue.row.title": "Schlagwort",
    "ui.obj.baw.keyword.catalogue.table.help": "BAW-Schlagwortkatalog 2012",
    "ui.obj.baw.keyword.catalogue.table.title": "BAW-Schlagwortkatalog 2012",
    "ui.obj.baw.simulation.model.type.table.title": "Simulationsmodellart",
    "ui.obj.baw.simulation.model.type.table.help": "Art des Simulationsmodells",
    "ui.obj.baw.simulation.parameter.dialog.table.column.value": "Wert",
    "ui.obj.baw.simulation.parameter.table.title": "Simulationsparameter/-Größen",
    "ui.obj.baw.simulation.parameter.table.help": "Simulationsparameter/-Größen",
    "ui.obj.baw.simulation.parameter.table.column.name": "Name",
    "ui.obj.baw.simulation.parameter.table.column.role": "Rolle",
    "ui.obj.baw.simulation.parameter.table.column.value": "Wert/Wertebereich",
    "ui.obj.baw.simulation.parameter.table.column.units": "Maßeinheit",
    "ui.obj.baw.simulation.parameter.table.new.row": "Simulationsparameter/-Größe hinzufügen",
    "ui.obj.baw.simulation.process.title": "Simulationsverfahren",
    "ui.obj.baw.simulation.process.help": "Verfahren für die numersiche Simulation",
    "ui.obj.baw.simulation.timestep.title": "Zeitliche Genauigkeit",
    "ui.obj.baw.simulation.timestep.help": "Zeitschrittgröße der Simulation in Sekunden",
    "ui.obj.baw.simulation.spatial.dimensionality.title": "Räumliche Dimensionalität",
    "ui.obj.baw.simulation.spatial.dimensionality.help": "Räumliche Dimensionalität der Simulation",
    "ui.obj.baw.lfs.link.table.title": "Langfristspeicher",
    "ui.obj.baw.lfs.link.table.help": "Dateien in den Langfristspeicher verschieben oder Verweis zu einem bestehenden Verzeichnis/Datei im Langfristspeicher erstellen.<br><br>Verzeichnis/Datei in den Langfristspeicher verschieben und automatisch einen Link erzeugen:<br><ol><li>Prüfen Sie sorgfältig die Dateiinhalte und -Namen vom zu archivierenden Verzeichnis/Dateie.</li><li>Kopieren Sie im Dateiexplorer das Verzeichnis / die Dateie in einen von den Eingangsbereichen unter \\\\lfs-ka.all.baw.de\\Eingang oder \\\\lfs-hh.all.baw.de\\Eingang</li><li>3. Betätigen Sie den Link [...]</li><li>Befüllen Sie das Formular in der Dialogbox und wählen Sie die im Schritt 2 kopierten Verzeichnisse/Dateien im Eingangsbereich aus.</li><li>Klicken Sie auf [...]. Das Verzeichnis/Datei wird aus dem Eingangsbereich in den Langfristspeicher verschoben und ein Link automatisch in diese Tabelle eingetragen.</li></ol><br>Link zu einem bestehenden Verzeichnis /Datei im Langfristspeicher erzeugen:<br><ol><li>Klicken Sie auf den Link [...]</li><li>Wählen Sie die Option 'LFS-Ablage' aus.</li><li>Befüllen Sie das Formular aus und wählen Sie eine Datei aus dem Langfristspeicher aus.</li><li>4. Klicken Sie auf [...]. Ein Link zur ausgewählten Datei wird damit erzeugt und in diese Tabelle eingetragen.</li></ol><br>Link Eigenschaften bearbeiten:<br><ol><li>Klicken Sie mit der rechten Maustaste auf die zu bearbeitende Zeile.</li><li>Klicken Sie auf 'Zeile bearbeiten'.</li><li>Bearbeiten Sie die Einträge im Formular.</li><li>Klicken Sie auf [...].</li></ol><br>Wichtige Hinweise:<br><ol><li>Nach den Verschieben in den Langfristspeicher sind keinerlei Änderungen möglich. Das gilt auch wenn eine Zeile in dieser Tabelle bearbeitet oder gelöscht wird oder dieser Metadatensatz gelöscht wird. Prüfen Sie deswegen die Dateiinhalte und -Namen sorgfältig, bevor Sie den Verschiebevorgang starten.</li><li>Der Verschiebevorgang erfolgt über einen Dienst, der unabhängig von diesem Metadateneditor läuft. Auch ohne Veröffentlichung des aktuellen Metadatensatz, sind nach dem Verschiben die Dateien im Langfristspeicher verfügbar und können nicht mehr gelöscht werden.</li></ol>",
    "ui.obj.baw.lfs.link.table.new.row": "Hinzufügen",
    "ui.obj.baw.lfs.link.table.new.row.tooltip": "Einträge können erst dann hinzugefügt werden, wenn die Felder PSP-Element und Streckenabschnitte ausgefüllt wurden.",
    "ui.obj.baw.lfs.link.table.column.link": "Link",
    "ui.obj.baw.lfs.link.table.column.name": "Name",
    "ui.obj.baw.lfs.link.table.column.fileFormat": "Dateiformat",

    "ui.sysList.3950000": "BAW - Räumliche Dimensionalität",
    "ui.sysList.3950001": "BAW - Simulationsverfahren",
    "ui.sysList.3950002": "BAW - Bezeichnung der Hierarchieebene",
    "ui.sysList.3950003": "BAW - Modellart",
    "ui.sysList.3950004": "BAW - Simulationsparameter/-Größe Rolle",
    "ui.sysList.3950005": "BAW - Schlagwortkatalog 2012",
    "ui.sysList.3950010": "BAW - VV-WSV 1103",
    "ui.sysList.3950099": "BAW - Datenformat",

    "validation.baw.address.role.pointOfContact": "Ein Eintrag für die Institution 'Bundesanstalt für Wasserbau' als 'Ansprechpartner' muss vorhanden sein.",
    "validation.baw.bwastr_km.entry.missing": "Km-von und Km-bis müssen entweder beide definiert sein oder sollen beide fehlen.",
    "validation.baw.bwastr_name.missing": "Für jeden Streckenabschnitt muss mindestens den Streckenname angegeben werden."
});

