-- Tabelle damit Benutzer Kartenausschnitte per TinyUrls abzuspeichern.


CREATE TABLE ingrid_tiny_url (
  id int(11) NOT NULL auto_increment,
  user_ref varchar(254) NOT NULL,
  tiny_key varchar(254) NOT NULL,
  tiny_name varchar(254) NOT NULL,
  tiny_date timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  tiny_config text NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=0 ;


-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  value mediumint
);

-- Zwischenspeichern von der fragment_id von der Seite 'main-maps.psml'
INSERT INTO ingrid_temp (VALUE) VALUES ((SELECT FRAGMENT_ID FROM fragment WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE NAME='main-maps.psml')));
 
-- portlet fragment -> fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- new portlet FRAGMENT_ID
(SELECT value FROM ingrid_temp), -- layout FRAGMENT_ID
NULL, 'ingrid-portal-apps::SaveMapsPortlet', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

INSERT INTO fragment_constraints_ref (CONSTRAINTS_REF_ID, FRAGMENT_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_CONSTRAINTS_REF'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- same portlet FRAGMENT_ID from above
0, 'users');

-- main-maps.psml auf IngridOneColumn umstellen
UPDATE `fragment` SET `NAME` = 'jetspeed-layouts::IngridOneColumn' WHERE `fragment`.`PAGE_ID` =(SELECT `page`.`PAGE_ID` FROM `page`
WHERE `page`.path = '/main-maps.psml');

-- ShowMapsPortlet ohne Column-Layout-Value festlegen
UPDATE `fragment` SET `LAYOUT_COLUMN` = '-1' WHERE `fragment`.`NAME` ='ingrid-portal-apps::ShowMapsPortlet';


	
-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
-- =====================

 -- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';

 -- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT_CONSTRAINTS_REF';

UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT_CONSTRAINT';

-- entfernen der temporären Tabelle
-- =====================
DROP TABLE ingrid_temp;

-- DB Version erhöhen
UPDATE `ingrid_lookup` SET `item_value` = '1.3.0' WHERE `ingrid_lookup`.`item_key` ='ingrid_db_version' LIMIT 1 ;


-- 02.10.2009
-- Erweiterung der ingrid_lookup Tabelle
ALTER TABLE `ingrid_lookup` ADD `item_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ;
UPDATE `ingrid_lookup` SET `item_date` = NOW() WHERE `ingrid_lookup`.`item_key` ='ingrid_db_version' LIMIT 1 ;

-- Lokalisierung von Administration Untermenüs
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.cms', `SHORT_TITLE` = 'ingrid.page.admin.cms' WHERE `page`.`PATH` = '/administration/admin-cms.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.monitor', `SHORT_TITLE` = 'ingrid.page.admin.monitor' WHERE `page`.`PATH` = '/administration/admin-component-monitor.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.partner', `SHORT_TITLE` = 'ingrid.page.admin.partner' WHERE `page`.`PATH` = '/administration/admin-content-partner.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.provider', `SHORT_TITLE` = 'ingrid.page.admin.provider' WHERE `page`.`PATH` = '/administration/admin-content-provider.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.rss', `SHORT_TITLE` = 'ingrid.page.admin.rss' WHERE `page`.`PATH` = '/administration/admin-content-rss.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.homepage', `SHORT_TITLE` = 'ingrid.page.admin.homepage' WHERE `page`.`PATH` = '/administration/admin-homepage.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.iplugs', `SHORT_TITLE` = 'ingrid.page.admin.iplugs' WHERE `page`.`PATH` = '/administration/admin-iplugs.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.profile', `SHORT_TITLE` = 'ingrid.page.admin.profile' WHERE `page`.`PATH` = '/administration/admin-portal-profile.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.statistics', `SHORT_TITLE` = 'ingrid.page.admin.statistics' WHERE `page`.`PATH` = '/administration/admin-statistics.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.usermanagement', `SHORT_TITLE` = 'ingrid.page.admin.usermanagement' WHERE `page`.`PATH` = '/administration/admin-usermanagement.psml'; 
UPDATE `page` SET `TITLE` = 'ingrid.page.admin.wms', `SHORT_TITLE` = 'ingrid.page.admin.wms' WHERE `page`.`PATH` = '/administration/admin-wms.psml'; 

DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-cms.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-component-monitor.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-content-partner.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-content-provider.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-content-rss.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-iplugs.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-homepage.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-portal-profile.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-statistics.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-usermanagement.psml');
DELETE FROM `page_metadata` WHERE `page_metadata`.`NAME` = 'title' AND `page_metadata`.`PAGE_ID` = (SELECT `page`.`page_id` FROM `page` WHERE `page`.`PATH` = '/administration/admin-wms.psml');

-- 19.10.2009
-- Änderung der TinyUrl Tabelle 
ALTER TABLE `ingrid_tiny_url` CHANGE `tiny_config` `tiny_config` MEDIUMTEXT CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

-- Datum in der Lookup Tabelle aktualisieren
UPDATE `ingrid_lookup` SET `item_date` = NOW() WHERE `ingrid_lookup`.`item_key` ='ingrid_db_version' LIMIT 1 ;
