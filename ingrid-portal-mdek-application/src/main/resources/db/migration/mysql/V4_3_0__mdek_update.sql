UPDATE info SET value_name = '4.3.0' WHERE  info.key_name = 'version';

DELETE FROM help_messages WHERE gui_id = 10028;

INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1670, 0, 10028, -1, 'de', 'Konformität (freie Eingabe)', 'Hier können frei vom Benutzer definierbar Konformitätsangaben eingegeben werden. Beispiele sind Konformitätsangaben, die nicht in INSPIRE definiert sind.', '');
INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(1671, 0, 10028, -1, 'en', 'Conformity (free entry)', 'Freely definable conformity statements can be entered in this field. Examples are conformity statements that are not defined in INSPIRE.', '');

UPDATE help_messages
SET help_text = '<p>Hier kann angegeben werden, zu welcher Spezifikation die beschriebenen Daten konform sind.</p><p>Einträge in dieses Feld erfolgen über den Link "Konformität hinzufügen". Es ist möglich aus Vorgabelisten auszuwählen oder freie Eingaben zu tätigen.</p><p>Sind die zu beschreibenden Daten INSPIRE-relevant, muss die zutreffende Durchführungsbestimmung der INSPIRE-Richtlinie angegeben werden. (INSPIRE-Pflichtfeld)</p><p>Dieses Feld wird bei Eintragungen in "INSPIRE-Themen" oder "Art des Dienstes" automatisch befüllt. Es muss dann nur der "Grad der Konformität" manuell angepasst werden.</p><p>(bei Aktivierung der Checkbox "AdV-konform")<br />Bitte entsprechend den Empfehlungen des AdV-Metadatenprofils nur die Werte "konform" und "nicht konform" für "Grad der Konformität" verwenden.</p>'
WHERE gui_id = 10024 AND language = 'de';

UPDATE help_messages
SET help_text = '<p>Specify the relevant specification (obligatory for INSPIRE-relevant metadata) and whether the dataset/service is conform or not.</p><p>Use the link "add conformity" for new entries. It''s possible to select from lists or to insert a free entry.</p><p>Specify the relevant Commission Regulation for INSPIRE-relevant metadata and whether the dataset/service is conform or not.</p><p>When inserting an entry in "INSPIRE-Topics" (dataset) or a "Type of Service" (service), the specification will be filled in automatically.</p><p>(if Checkbox "AdV-konform" is on)<br />Please only use the values "conform" and "not conform" for the "Degree of Conformity"-field, according to the recommendations of the AdV-Metadata profiles.</p>'
WHERE gui_id = 10024 AND language = 'en';

UPDATE help_messages
SET help_text = '<p>Dieses Feld definiert, wenn aktiviert, dass ein Metadatensatz für das INSPIRE-Monitoring vorgesehen ist.</p><p>Folgende Eigenschaften ändern sich bei Aktivierung:</p><ul><li>Hinzufügen des Schlagwortes "inspireidentifiziert" bei ISO XML Generierung</li><li>verpflichtende Angabe eines INSPIRE Themas bei Klasse "Informationssystem" (6)</li><li>WebServices wird als Defaultwert für die unterstützte Plattform bei einer neuen Operation verwendet</li><li>"Konformität" wird zu einem Pflichteingabe gemacht. "Konformität"-Tabelle wird sichtbar gemacht und ein Standardeintrag wird in diese Tabelle hinzugefügt.</li></ul>'
WHERE gui_id = 6000 AND language = 'de';

UPDATE help_messages
SET help_text = '<p>If clicked, this record is relevant for INSPIRE-Monitoring.</p><p>The following properties change, when activated:</p><ul><li>The keyword "inspireidentifiziert" is added to when generating the ISO XML view.</li><li>INSPIRE topic becomes a mandatory field for the class "information system" (6)</li><li>WebServices becomes the default value for the supported platform when adding a new operation</li><li>"Conformity" becomes a required field. The "Conformity" table is made visible and a standard confomity-entry is added to this table.</li></ul>'
WHERE gui_id = 6000 AND language = 'en';

