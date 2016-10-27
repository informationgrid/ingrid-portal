-- DB Version
UPDATE ingrid_lookup SET item_value = '4.0.1', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
-- oracle way of: DROP TABLE IF EXISTS

-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

-- BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;
-- /

-- !!!!!!!! -------------------------------------------------

CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

-- add cms key
INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'portal.cms.portlet.3', 'Anwendungen Übersicht', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.3'), 'de', 'CMSPortlet3', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.cms.portlet.3'), 'en', 'CMSPortlet3', '', 'admin');

-- Change portlet iFrame to CMS of page '/application/main-application.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('fragment_tmp', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application.psml')));

UPDATE fragment SET name = 'ingrid-portal-apps::CMSPortlet3' WHERE name = 'ingrid-portal-apps::IFramePortalPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment_tmp');

-- delete temporary table
DROP TABLE ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';