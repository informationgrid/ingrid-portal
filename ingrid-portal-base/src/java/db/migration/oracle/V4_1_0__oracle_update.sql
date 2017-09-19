
-- DB Version
UPDATE ingrid_lookup SET item_value = '4.1.0', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

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

UPDATE page SET default_layout_decorator = 'ingrid-untitled', default_portlet_decorator = 'clear' WHERE path = '/search-detail.psml';

-- Add new link '/home.link'
INSERT INTO link (LINK_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, TARGET, URL, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES
((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_LINK'), 1, '/home.link', 'home.link', NULL, 'ingrid.page.home', 'ingrid.page.home', 1, NULL, 'top', '/default-page.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Reorder service menu
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('service-menu', (SELECT menu_id FROM folder_menu WHERE name = 'service-menu'));
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu');
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN) VALUES
 ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu'), '0', NULL, NULL, NULL, NULL, '/language.link', '0', '0', '0', NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN) VALUES
 ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu'), '1', NULL, NULL, NULL, NULL, '/home.link', '0', '0', '0', NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN) VALUES
 ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu'), '2', NULL, NULL, NULL, NULL, '/service-myportal.psml', '0', '0', '0', NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN) VALUES
 ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu'), '3', NULL, NULL, NULL, NULL, '/administration', '0', '0', '0', NULL, NULL, NULL);

-- delete temporary table
DROP TABLE ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_LINK';