-- Tabelle damit Benutzer Kartenausschnitte per TinyUrls abzuspeichern.
CREATE TABLE  INGRID_TINY_URL
   (	ID NUMBER(11,0), 
	USER_REF VARCHAR2(254), 
	TINY_KEY VARCHAR2(254), 
	TINY_NAME VARCHAR2(254), 
	TINY_DATE TIMESTAMP (6), 
	TINY_CONFIG CLOB, 
	 CONSTRAINT INGRID_TINY_URL_PK PRIMARY KEY (ID) ENABLE
   );

-- Sequence erstellt um das autoincrement zu erstatten
CREATE SEQUENCE   INGRID_TINY_URL_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 1 NOCACHE  ORDER  NOCYCLE;
   
 -- portlet fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT FRAGMENT_ID FROM fragment WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE NAME='main-maps.psml')),
NULL, 'ingrid-portal-apps::SaveMapsPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

INSERT INTO fragment_constraints_ref (CONSTRAINTS_REF_ID, FRAGMENT_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINTS_REF'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- same portlet FRAGMENT_ID from above
0, 'users');

-- main-maps.psml auf IngridOneColumn umstellen
UPDATE fragment SET NAME = 'jetspeed-layouts::IngridOneColumn' WHERE PAGE_ID = (SELECT PAGE_ID FROM page WHERE path = '/main-maps.psml');

-- ShowMapsPortlet ohne Column-Layout-Value festlegen
UPDATE fragment SET LAYOUT_COLUMN = '-1' WHERE NAME ='ingrid-portal-apps::ShowMapsPortlet';


-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
-- =====================
 
 -- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';

 -- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT_CONSTRAINTS_REF';

-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '1.3.0' WHERE item_key ='ingrid_db_version';


-- 02.10.2009

-- Erweiterung der ingrid_lookup Tabelle
ALTER TABLE ingrid_lookup ADD (ITEM_DATE TIMESTAMP NULL);
UPDATE ingrid_lookup SET item_date=SYSDATE WHERE item_key ='ingrid_db_version';

-- Lokalisierung von Administration Untermenüs
UPDATE page SET TITLE = 'ingrid.page.admin.cms', SHORT_TITLE = 'ingrid.page.admin.cms' WHERE PATH = '/administration/admin-cms.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.monitor', SHORT_TITLE = 'ingrid.page.admin.monitor' WHERE PATH = '/administration/admin-component-monitor.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.partner', SHORT_TITLE = 'ingrid.page.admin.partner' WHERE PATH = '/administration/admin-content-partner.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.provider', SHORT_TITLE = 'ingrid.page.admin.provider' WHERE PATH = '/administration/admin-content-provider.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.rss', SHORT_TITLE = 'ingrid.page.admin.rss' WHERE PATH = '/administration/admin-content-rss.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.homepage', SHORT_TITLE = 'ingrid.page.admin.homepage' WHERE PATH = '/administration/admin-homepage.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.iplugs', SHORT_TITLE = 'ingrid.page.admin.iplugs' WHERE PATH = '/administration/admin-iplugs.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.profile', SHORT_TITLE = 'ingrid.page.admin.profile' WHERE PATH = '/administration/admin-portal-profile.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.statistics', SHORT_TITLE = 'ingrid.page.admin.statistics' WHERE PATH = '/administration/admin-statistics.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.usermanagement', SHORT_TITLE = 'ingrid.page.admin.usermanagement' WHERE PATH = '/administration/admin-usermanagement.psml'; 
UPDATE page SET TITLE = 'ingrid.page.admin.wms', SHORT_TITLE = 'ingrid.page.admin.wms' WHERE PATH = '/administration/admin-wms.psml'; 

DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-cms.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-component-monitor.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-content-partner.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-content-provider.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-content-rss.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-iplugs.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-homepage.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-portal-profile.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-statistics.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-usermanagement.psml');
DELETE FROM page_metadata WHERE NAME = 'title' AND PAGE_ID = (SELECT page_id FROM page WHERE PATH = '/administration/admin-wms.psml');

