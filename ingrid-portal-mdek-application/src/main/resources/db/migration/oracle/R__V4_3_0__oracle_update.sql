UPDATE info SET value_name = '4.3.0' WHERE  info.key_name = 'version';

DELETE FROM help_messages WHERE gui_id = 10028;

INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1670, 0, 10028, -1, 'de', 'Konformität (freie Eingabe)', 'Hier können frei vom Benutzer definierbar Konformitätsangaben eingegeben werden. Beispiele sind Konformitätsangaben, die nicht in INSPIRE definiert sind.', '');
INSERT INTO help_messages (id, version, gui_id, entity_class, language, name, help_text, sample) VALUES
(1671, 0, 10028, -1, 'en', 'Conformity (free entry)', 'Freely definable conformity statements can be entered in this field. Examples are conformity statements that are not defined in INSPIRE.', '');

UPDATE help_messages
SET help_text = 'Hier muss angegeben werden, zu welcher Durchführungsbestimmung der INSPIRE-Richtlinie bzw. zu welcher anderweitigen Spezifikation die beschriebenen Daten konform sind. (INSPIRE-Pflichtfeld)<br /><br />Die erste Spalte dieser Tabelle zeigt, ob die Konformität von INSPIRE definiert ist (Häkchen gesetzt) oder ein freier Eintrag ist (Häkchen nicht gesetzt). Die zweite Spalte enthält die Konformität-Spezifikation (Checklisten 6005 und 6006), die dritte Spalte den Grad der Konformität (Checkliste 6000) und die letzte Spalte das Datum der Veröffentlichung des Konformitäts-Thesaurus.<br/><br/>Dieses Feld wird bei Eintragungen in "INSPIRE-Themen" oder "Art des Dienstes" automatisch befüllt. Es muss dann nur der Grad der Konformität manuell eingetragen werden.<br /><br />Neue Konformitätsangaben können über den link "Konformität hinzufügen" gemacht werden.'
WHERE gui_id = 10024 AND language = 'de';

UPDATE help_messages
SET help_text = 'Specify the conformity-specification for the dataset/service along with the degree of conformance. This is an obligatory field for INSPIRE-relevant metadata.<br /><br />The first column shows if the conformity specification is defined by INSPIRE (checkbox active) or is a freely defined specification (checkbox not active). The second column shows the conformity-specification (checklists 6005 and 6006), the third column the degree of conformity (checklist 6000) and the last column the date of publication of the conformity thesaurus.<br/><br/>When inserting an entry in "INSPIRE-Topics" (Geodatensatz) or a "Type of Service" (Geodatendienst), the specification will be filled in automatically.<br /><br /> New entries to this table can be added using the link above the table to the right.'
WHERE gui_id = 10024 AND language = 'en';

UPDATE help_messages
SET help_text = '<p>Dieses Feld definiert, wenn aktiviert, dass ein Metadatensatz für das INSPIRE-Monitoring vorgesehen ist.</p><p>Folgende Eigenschaften ändern sich bei Aktivierung:</p><ul><li>Hinzufügen des Schlagwortes "inspireidentifiziert" bei ISO XML Generierung</li><li>verpflichtende Angabe eines INSPIRE Themas bei Klasse "Informationssystem" (6)</li><li>WebServices wird als Defaultwert für die unterstützte Plattform bei einer neuen Operation verwendet</li><li>"Konformität" wird zu einem Pflichteingabe gemacht. "Konformität"-Tabelle wird sichtbar gemacht und ein Standardeintrag wird in diese Tabelle hinzugefügt.</li></ul>'
WHERE gui_id = 6000 AND language = 'de';

UPDATE help_messages
SET help_text = '<p>If clicked, this record is relevant for INSPIRE-Monitoring.</p><p>The following properties change, when activated:</p><ul><li>The keyword "inspireidentifiziert" is added to when generating the ISO XML view.</li><li>INSPIRE topic becomes a mandatory field for the class "information system" (6)</li><li>WebServices becomes the default value for the supported platform when adding a new operation</li><li>"Conformity" becomes a required field. The "Conformity" table is made visible and a standard confomity-entry is added to this table.</li></ul>'
WHERE gui_id = 6000 AND language = 'en';

