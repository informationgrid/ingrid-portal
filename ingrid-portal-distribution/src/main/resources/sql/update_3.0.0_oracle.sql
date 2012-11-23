-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.0.0' WHERE item_key ='ingrid_db_version';

-- Datum in der Lookup Tabelle aktualisieren
UPDATE ingrid_lookup SET item_date=SYSDATE WHERE item_key ='ingrid_db_version';

-- 10.02.2010
-- Aktualisierung der Kontaktdaten für PortalU
UPDATE ingrid_cms_item SET item_value='.</p><p>Unsere Postadresse:</p><p>Niedersächsisches Umweltministerium<br />Koordinierungsstelle PortalU<br />Archivstrasse 2<br />D-30169 Hannover<br /></p><p>Nehmen Sie online Kontakt mit uns auf! Wir werden Ihnen schnellstmöglichst per E-Mail antworten. Die eingegebenen Informationen und Daten werden nur zur Bearbeitung Ihrer Anfrage gespeichert und genutzt. Beachten Sie bitte, dass die Datenübermittlung über das Kontaktformular unverschlüsselt erfolgt. Für vertrauliche Nachrichten nutzen Sie bitte den herkömmlichen Postweg.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key='portalu.contact.intro.postEmail') AND item_lang='de';

-- 15.03.2011
-- Entfernen der Statistik im Metadaten Portlet
DELETE FROM fragment WHERE name = 'ingrid-portal-mdek::MdekQuickViewPortlet';
DELETE FROM portlet_definition WHERE name = 'MdekQuickViewPortlet';
DELETE FROM portlet_entity WHERE portlet_name = 'MdekQuickViewPortlet';
DELETE FROM prefs_node WHERE full_path like '%/portlet_application/ingrid-portal-mdek/portlets/MdekQuickViewPortlet%';
