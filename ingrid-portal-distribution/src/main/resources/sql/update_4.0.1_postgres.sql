-- DB Version
UPDATE ingrid_lookup SET item_value = '4.0.1', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value int
);

-- add cms key
INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'portal.cms.portlet.3', 'Anwendungen Übersicht', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.3'), 'de', 'CMSPortlet3', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.3'), 'en', 'CMSPortlet3', '', 'admin');

-- Change portlet iFrame to CMS of page '/application/main-application.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('fragment_tmp', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application.psml')));

UPDATE fragment SET name = 'ingrid-portal-apps::CMSPortlet3', decorator = 'clear' WHERE name = 'ingrid-portal-apps::IFramePortalPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment_tmp');

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
