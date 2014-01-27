-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.2.0a', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- FIXES for InGrid 3.1.0
-- go into Version 3.2.0
-- ======================
-- "Standardseite von PortalU wird unterschiedlich angezeigt" see https://dev.wemove.com/jira/browse/INGRID31-23
-- "Abschaffung der funktionellen Kategorien" see https://dev.wemove.com/jira/browse/INGRID31-22

-- FIX /default-page.psml
-- -----------------------------

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

-- temp values !
-- PARENT_ID of weather teaser, was page_id before not id of parent layout fragment (worked due to same id of page and parent layout fragment) !
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('START_PAGE_LAYOUT', (SELECT FRAGMENT_ID FROM fragment WHERE NAME='jetspeed-layouts::IngridTwoColumns' AND PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml')));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('WEATHER_FRAGMENT_ID', (SELECT FRAGMENT_ID FROM fragment WHERE NAME='ingrid-portal-apps::WeatherTeaser' AND PARENT_ID=(SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml' )));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('PAGE_ID', (SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml'));

-- fix PARENT_ID of weather teaser, was page_id before not id of parent layout fragment (worked due to same id of page and parent layout fragment) !
UPDATE fragment SET PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT') WHERE FRAGMENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'WEATHER_FRAGMENT_ID');

-- add Preference 'titleKey' to weather portlet
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), -- new FRAGMENT_PREF_ID
(SELECT temp_value FROM ingrid_temp where temp_key = 'WEATHER_FRAGMENT_ID'), -- WeatherTeaser FRAGMENT_ID from anonymous start page
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), -- new FRAGMENT_PREF_VALUE_ID
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), -- FRAGMENT_PREF_ID from above
0, 'teaser.weather.title');


-- FIX /_role/user/default-page.psml (adapt to main /default-page.psml)
-- --------------------------------------------------------------------

-- temp values !
UPDATE ingrid_temp SET temp_value = (SELECT FRAGMENT_ID FROM fragment WHERE NAME='jetspeed-layouts::IngridTwoColumns' AND PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/_role/user/default-page.psml')) WHERE temp_key='START_PAGE_LAYOUT';
UPDATE ingrid_temp SET temp_value = (SELECT PAGE_ID FROM page WHERE PATH='/_role/user/default-page.psml') WHERE temp_key='PAGE_ID';
UPDATE ingrid_temp SET temp_value = (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT') WHERE temp_key='WEATHER_FRAGMENT_ID';

-- Neues Wetter Portlet hinzufügen
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT temp_value FROM ingrid_temp where temp_key = 'WEATHER_FRAGMENT_ID'), -- new portlet FRAGMENT_ID
(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT'), -- layout FRAGMENT_ID
NULL, 'ingrid-portal-apps::WeatherTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 4, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), -- new FRAGMENT_PREF_ID
(SELECT temp_value FROM ingrid_temp where temp_key = 'WEATHER_FRAGMENT_ID'), -- WeatherTeaser FRAGMENT_ID from above
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), -- new FRAGMENT_PREF_VALUE_ID
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), -- FRAGMENT_PREF_ID from above
0, 'teaser.weather.title');

-- remove service teaser portlet from start page
DELETE FROM fragment WHERE NAME = 'ingrid-portal-apps::ServiceTeaser' AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');

-- reorder portlets on right side
UPDATE fragment SET LAYOUT_ROW='1' WHERE NAME='ingrid-portal-apps::ChronicleTeaser' AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');
UPDATE fragment SET LAYOUT_ROW='2' WHERE NAME='ingrid-portal-apps::WeatherTeaser'   AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');
UPDATE fragment SET LAYOUT_ROW='3' WHERE NAME='ingrid-portal-apps::MeasuresTeaser'  AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');

-- add localizable page titles
UPDATE page SET TITLE = 'ingrid.page.home.tooltip' WHERE PAGE_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID');

-- add meta_title
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID'), 'meta_title', 'de', 'ingrid.page.home.meta.title');

-- add meta_description
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID'), 'meta_descr', 'de', 'ingrid.page.home.meta.description');

    -- add meta_keywords
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID'), 'meta_keywords', 'de', 'ingrid.page.home.meta.keywords');


-- FIX /_user/template/default-page.psml (adapt to main /default-page.psml)
-- ------------------------------------------------------------------------

-- temp values !
UPDATE ingrid_temp SET temp_value = (SELECT FRAGMENT_ID FROM fragment WHERE NAME='jetspeed-layouts::IngridTwoColumns' AND PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/_user/template/default-page.psml')) WHERE temp_key='START_PAGE_LAYOUT';
UPDATE ingrid_temp SET temp_value = (SELECT PAGE_ID FROM page WHERE PATH='/_user/template/default-page.psml') WHERE temp_key='PAGE_ID';
UPDATE ingrid_temp SET temp_value = (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT') WHERE temp_key='WEATHER_FRAGMENT_ID';

-- Neues Wetter Portlet hinzufügen
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT temp_value FROM ingrid_temp where temp_key = 'WEATHER_FRAGMENT_ID'), -- new portlet FRAGMENT_ID
(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT'), -- -- layout FRAGMENT_ID
NULL, 'ingrid-portal-apps::WeatherTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 4, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), -- new FRAGMENT_PREF_ID
(SELECT temp_value FROM ingrid_temp where temp_key = 'WEATHER_FRAGMENT_ID'), -- WeatherTeaser FRAGMENT_ID from above
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), -- new FRAGMENT_PREF_VALUE_ID
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), -- FRAGMENT_PREF_ID from above
0, 'teaser.weather.title');

-- remove service teaser portlet from start page
DELETE FROM fragment WHERE NAME = 'ingrid-portal-apps::ServiceTeaser' AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');

-- reorder portlets on right side
UPDATE fragment SET LAYOUT_ROW='1' WHERE NAME='"ingrid-portal-apps::ChronicleTeaser' AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');
UPDATE fragment SET LAYOUT_ROW='2' WHERE NAME='ingrid-portal-apps::WeatherTeaser'   AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');
UPDATE fragment SET LAYOUT_ROW='3' WHERE NAME='ingrid-portal-apps::MeasuresTeaser'  AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');

-- add localizable page titles
UPDATE page SET TITLE = 'ingrid.page.home.tooltip' WHERE PAGE_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID');

-- add meta_title
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID'), 'meta_title', 'de', 'ingrid.page.home.meta.title');

-- add meta_description
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID'), 'meta_descr', 'de', 'ingrid.page.home.meta.description');

    -- add meta_keywords
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT temp_value FROM ingrid_temp where temp_key = 'PAGE_ID'), 'meta_keywords', 'de', 'ingrid.page.home.meta.keywords');

    
-- "Abschaffung der funktionellen Kategorien" see https://dev.wemove.com/jira/browse/INGRID31-22
-- -----------------------------
DROP TABLE ingrid_env_funct_category;


-- CLEAN UP
-- --------

-- delete temporary table
DROP TABLE ingrid_temp;
    
-- update max keys in used sequences
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF_VALUE';
