-- DB Version
UPDATE ingrid_lookup SET item_value = '5.1.0', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
-- oracle way of: DROP TABLE IF EXISTS

-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

-- BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus, Flyway !):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;
/

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
CREATE TABLE ingrid_temp (
  temp_key VARCHAR2(255),
  temp_value NUMBER(10,0)
);

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
-- oracle way of: DROP TABLE IF EXISTS

-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

--BEGIN execute immediate 'DROP TABLE ingrid_temp2'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus, Flyway !):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

BEGIN execute immediate 'DROP TABLE ingrid_temp2'; exception when others then null; END;
/

CREATE TABLE ingrid_temp2 (
  temp_key VARCHAR2(255),
  temp_value NUMBER(10,0)
);

-- Change about portlet
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('page-about', (SELECT page_id FROM page WHERE path = '/main-about.psml'));
INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('fragment-layout-about', 
(SELECT fragment_id FROM fragment WHERE page_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'page-about')));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('fragment-portlet-about', 
(SELECT fragment_id FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'fragment-layout-about')));
UPDATE fragment SET name = 'ingrid-portal-apps::AboutPortlet' WHERE name = 'ingrid-portal-apps::CMSPortlet' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-about');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('cmsKey-about', 
(SELECT pref_id FROM fragment_pref WHERE name = 'cmsKey' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-about')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'cmsKey-about');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'cmsKey-about');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('sectionStyle-about', 
(SELECT pref_id FROM fragment_pref WHERE name = 'sectionStyle' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-about')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'sectionStyle-about');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'sectionStyle-about');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('titleTag-about', 
(SELECT pref_id FROM fragment_pref WHERE name = 'titleTag' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-about')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleTag-about');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleTag-about');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('helpKey-about', 
(SELECT pref_id FROM fragment_pref WHERE name = 'helpKey' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-about')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'helpKey-about');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'helpKey-about');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('infoTemplate-about', 
(SELECT pref_id FROM fragment_pref WHERE name = 'infoTemplate' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-about')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'infoTemplate-about');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'infoTemplate-about');


-- Change disclaimer portlet
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('page-disclaimer', (SELECT page_id FROM page WHERE path = '/disclaimer.psml'));
INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('fragment-layout-disclaimer', 
(SELECT fragment_id FROM fragment WHERE page_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'page-disclaimer')));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('fragment-portlet-disclaimer', 
(SELECT fragment_id FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'fragment-layout-disclaimer')));
UPDATE fragment SET name = 'ingrid-portal-apps::DisclaimerPortlet' WHERE name = 'ingrid-portal-apps::CMSPortlet' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-disclaimer');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('infoTemplate-disclaimer', 
(SELECT pref_id FROM fragment_pref WHERE name = 'infoTemplate' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-disclaimer')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'infoTemplate-disclaimer');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'infoTemplate-disclaimer');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('sectionStyle-disclaimer', 
(SELECT pref_id FROM fragment_pref WHERE name = 'sectionStyle' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-disclaimer')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'sectionStyle-disclaimer');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'sectionStyle-disclaimer');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('titleKey-disclaimer', 
(SELECT pref_id FROM fragment_pref WHERE name = 'titleKey' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-disclaimer')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleKey-disclaimer');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleKey-disclaimer');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('titleTag-disclaimer', 
(SELECT pref_id FROM fragment_pref WHERE name = 'titleTag' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-disclaimer')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleTag-disclaimer');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleTag-disclaimer');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('cmsKey-disclaimer', 
(SELECT pref_id FROM fragment_pref WHERE name = 'cmsKey' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-disclaimer')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'cmsKey-disclaimer');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'cmsKey-disclaimer');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('articleStyle-disclaimer', 
(SELECT pref_id FROM fragment_pref WHERE name = 'articleStyle' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-disclaimer')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'articleStyle-disclaimer');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'articleStyle-disclaimer');

-- Change sitemap portlet
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('page-sitemap', (SELECT page_id FROM page WHERE path = '/service-sitemap.psml'));
INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('fragment-layout-sitemap', 
(SELECT fragment_id FROM fragment WHERE page_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'page-sitemap')));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('fragment-portlet-sitemap', 
(SELECT fragment_id FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'fragment-layout-sitemap')));
UPDATE fragment SET name = 'ingrid-portal-apps::SitemapPortlet' WHERE name = 'ingrid-portal-apps::InfoPortlet' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-sitemap');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('infoTemplate-sitemap', 
(SELECT pref_id FROM fragment_pref WHERE name = 'infoTemplate' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-sitemap')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'infoTemplate-sitemap');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'infoTemplate-sitemap');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('sectionStyle-sitemap', 
(SELECT pref_id FROM fragment_pref WHERE name = 'sectionStyle' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-sitemap')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'sectionStyle-sitemap');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'sectionStyle-sitemap');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('titleKey-sitemap', 
(SELECT pref_id FROM fragment_pref WHERE name = 'titleKey' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-sitemap')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleKey-sitemap');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleKey-sitemap');

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('titleTag-sitemap', 
(SELECT pref_id FROM fragment_pref WHERE name = 'titleTag' AND fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment-portlet-sitemap')));
DELETE FROM fragment_pref WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleTag-sitemap');
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'titleTag-sitemap');

-- delete temporary table
DROP TABLE ingrid_temp;
DROP TABLE ingrid_temp2;