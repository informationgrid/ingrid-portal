-- DB Version
UPDATE ingrid_lookup SET item_value = '4.0.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);

DROP TABLE IF EXISTS ingrid_temp2;
CREATE TABLE ingrid_temp2 (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);

-- clean fragments
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/disclaimer.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/help.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-about-partner.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-about.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-chronicle.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-maps.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-measures.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-search.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/myportal-create-account.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/myportal-password-forgotten.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/privacy.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/rss-news.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-detail.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/service-contact.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/service-myportal.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/service-sitemap.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/myportal-edit-account.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/myportal-personalize.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/service-myportal.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-cms.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-component-monitor.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-content-partner.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-content-provider.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-content-rss.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-homepage.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-iplugs.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-portal-profile.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-statistics.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-usermanagement.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-wms.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-catalog/search-catalog-hierarchy.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/mdek/mdek_portal_admin.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/cms/cms-1.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/cms/cms-2.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-about-data-source.psml'); 

-- delete unused fragments
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-service.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-history.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-result-js.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-save.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-settings.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/service-contact-newsletter.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-law-topic-terms.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-adr-area-partner.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-adr-place-reference.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-adr-topic-mode.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-adr-topic-terms.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-area-contents.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-area-partner.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-area-sources.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-place-geothesaurus.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-place-map.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-time-constraint.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-topic-terms.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-env-topic-thesaurus.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-res-topic-attributes.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-res-topic-terms.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-catalog/search-catalog-thesaurus.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-law-topic-thesaurus.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-extended/search-ext-law-area-partner.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-features.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-environment.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/privacy.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application-0.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application-1.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application-2.psml');
DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/cms/cms-0.psml');

DELETE FROM fragment WHERE name = 'ingrid-portal-apps::IngridWelcomePortlet';
DELETE FROM fragment WHERE name = 'ingrid-portal-apps::ServiceTeaser';
DELETE FROM fragment WHERE name = 'ingrid-portal-apps::ChronicleTeaser';
DELETE FROM fragment WHERE name = 'ingrid-portal-apps::WeatherTeaser';
DELETE FROM fragment WHERE name = 'ingrid-portal-apps::MeasuresTeaser';

-- default-page.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('default-page', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml')));

UPDATE fragment SET layout_row = 1, layout_column = 0 WHERE name = 'ingrid-portal-apps::IngridInformPortlet'; 
UPDATE fragment SET layout_row = 3 WHERE name = 'ingrid-portal-apps::RssNewsTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page');  
UPDATE fragment SET layout_row = 4 WHERE name = 'ingrid-portal-apps::EnvironmentTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page'); 
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::SearchSimple' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page');
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::IngridInformPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page'); 
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::RssNewsTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page');
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::EnvironmentTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page');

INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page'), 'ingrid-portal-apps::CategoryTeaser', 'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+1   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default-page'), 'ingrid-portal-apps::InfoDefaultPageTeaser', 'portlet', 5, 0, -1, -1, -1, -1, -1);

-- /_role/user/default-page.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('/_role/user/default-page', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml')));

UPDATE fragment SET layout_row = 1, layout_column = 0 WHERE name = 'ingrid-portal-apps::IngridInformPortlet'; 
UPDATE fragment SET layout_row = 3 WHERE name = 'ingrid-portal-apps::RssNewsTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page');  
UPDATE fragment SET layout_row = 4 WHERE name = 'ingrid-portal-apps::EnvironmentTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page'); 
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::SearchSimple' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page');
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::IngridInformPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page'); 
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::RssNewsTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page');
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::EnvironmentTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page');

INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+3  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page'), 'ingrid-portal-apps::CategoryTeaser', 'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+4   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/default-page'), 'ingrid-portal-apps::InfoDefaultPageTeaser', 'portlet', 5, 0, -1, -1, -1, -1, -1);

-- /_user/template/default-page.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('/_user/template/default-page', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml')));

UPDATE fragment SET layout_row = 1, layout_column = 0 WHERE name = 'ingrid-portal-apps::IngridInformPortlet'; 
UPDATE fragment SET layout_row = 3 WHERE name = 'ingrid-portal-apps::RssNewsTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page');  
UPDATE fragment SET layout_row = 4 WHERE name = 'ingrid-portal-apps::EnvironmentTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page'); 
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::SearchSimple' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page');
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::IngridInformPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page'); 
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::RssNewsTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page');
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::EnvironmentTeaser' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page');

INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page'), 'ingrid-portal-apps::CategoryTeaser', 'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+6   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_user/template/default-page'), 'ingrid-portal-apps::InfoDefaultPageTeaser', 'portlet', 5, 0, -1, -1, -1, -1, -1);

-- main-measures.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-measures', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-measures.psml')));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-measures') AND name = 'ingrid-portal-apps::MeasuresResult';
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-measures') AND name = 'ingrid-portal-apps::InfoPortlet';

-- search-catalog-hierarchy.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('search-catalog-hierarchy', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/search-catalog/search-catalog-hierarchy.psml')));

-- main-chronicle.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-chronicle', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-chronicle.psml')));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-chronicle') AND name = 'ingrid-portal-apps::InfoPortlet';
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::ChronicleResult' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-chronicle');

-- main-about.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-about', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-about.psml')));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-about') AND name = 'ingrid-portal-apps::InfoPortlet';

-- main-about-partner.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-about-partner', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-about-partner.psml')));

-- main-about-data-source.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-about-data-source', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-about-data-source.psml')));

-- main-search.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-search', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-search.psml')));

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('main-search-two', (SELECT fragment_id FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-search') AND name = 'jetspeed-layouts::IngridTwoColumns'));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'main-search-two') AND name = 'ingrid-portal-apps::InfoPortlet';

UPDATE fragment SET decorator = 'ingrid-clear', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-search'), layout_row = 0, layout_column = 0, name= 'ingrid-portal-apps::SearchSimpleResult' WHERE name = 'ingrid-portal-apps::SearchSimple' AND parent_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'main-search-two'); 
UPDATE fragment SET decorator = 'ingrid-clear', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-search'), layout_row = 1, layout_column = 0 WHERE name = 'ingrid-portal-apps::SearchSimilar' AND parent_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'main-search-two'); 

INSERT INTO ingrid_temp2 (temp_key, temp_value) VALUES ('main-search-one', (SELECT fragment_id FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-search') AND name = 'jetspeed-layouts::IngridOneColumn'));

UPDATE fragment SET decorator = NULL, parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-search'), layout_row = 2, layout_column = 0 WHERE name = 'ingrid-portal-apps::SearchResult' AND parent_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'main-search-one'); 

DELETE FROM fragment WHERE fragment_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'main-search-two');
DELETE FROM fragment WHERE fragment_id = (SELECT temp_value FROM ingrid_temp2 WHERE temp_key = 'main-search-one');

-- myportal-create-account.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('myportal-create-account', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/myportal-create-account.psml')));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'myportal-create-account') AND name = 'ingrid-portal-apps::InfoPortlet';

-- myportal-password-forgotten.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('myportal-password-forgotten', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/myportal-password-forgotten.psml')));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'myportal-password-forgotten') AND name = 'ingrid-portal-apps::InfoPortlet';

-- service-contact.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('service-contact', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/service-contact.psml')));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-contact') AND name = 'ingrid-portal-apps::InfoPortlet';
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::Contact' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-contact'); 

-- disclaimer.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('disclaimer', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/disclaimer.psml')));

DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'disclaimer') AND name = 'ingrid-portal-apps::InfoPortlet';
UPDATE fragment SET decorator = NULL WHERE name = 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'disclaimer'); 
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+2   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'disclaimer'), 'ingrid-portal-apps::PrivacyPortlet', 'portlet', 1, 0, -1, -1, -1, -1, -1);

-- service-myportal.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('service-myportal', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/service-myportal.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-myportal') AND name = 'ingrid-portal-apps::InfoPortlet';

-- service-sitemap.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('service-sitemap', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/service-sitemap.psml')));

-- /_role/user/service-myportal.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('/_role/user/service-myportal', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/service-myportal.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/service-myportal') AND name = 'ingrid-portal-apps::InfoPortlet';

-- /_role/user/myportal-personalize
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('/_role/user/myportal-personalize', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/myportal-personalize.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-personalize') AND name = 'ingrid-portal-apps::InfoPortlet';
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-personalize') AND name = 'ingrid-portal-apps::MyPortalPersonalizeOverviewPortlet';
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-personalize') AND name = 'ingrid-portal-apps::MyPortalPersonalizePartnerPortlet';
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-personalize') AND name = 'ingrid-portal-apps::MyPortalPersonalizeSourcesPortlet';
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-personalize') AND name = 'ingrid-portal-apps::MyPortalPersonalizeSearchSettingsPortlet';

-- /_role/user/myportal-edit-account.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('/_role/user/myportal-edit-account', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/myportal-edit-account.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-edit-account') AND name = 'ingrid-portal-apps::MyPortalEditAdvancedInfoPortlet';
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-edit-account') AND name = 'ingrid-portal-apps::MyPortalEditAboutInfoPortlet';
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/_role/user/myportal-edit-account') AND name = 'ingrid-portal-apps::MyPortalEditNewsletterInfoPortlet';

-- /administration/admin-usermanagement.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('/administration/admin-usermanagement', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-usermanagement.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/administration/admin-usermanagement') AND name = 'ingrid-portal-apps::AdminUserMigrationPortlet';

-- /administration/admin-content-provider.psml
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('/administration/admin-content-provider', (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/administration/admin-content-provider.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = '/administration/admin-content-provider') AND name = 'ingrid-portal-apps::AdminUserMigrationPortlet';

-- update pages
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-teaser' WHERE path = '/help.psml'; 
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-clear' WHERE path = '/main-maps.psml'; 
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-clear' WHERE path = '/main-measures.psml'; 
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-teaser' WHERE path = '/service-sitemap.psml'; 
UPDATE page SET default_layout_decorator = 'ingrid-clear', default_portlet_decorator = 'clear', title = 'ingrid.page.search.detail.tooltip', short_title = 'ingrid.page.search.detail' WHERE path = '/search-detail.psml'; 
UPDATE page SET title = 'ingrid.page.partner.tooltip', short_title = 'ingrid.page.partner' WHERE path = '/main-about-partner.psml'; 

-- delete unused pages
DELETE FROM page WHERE path = '/main-service.psml';
DELETE FROM page WHERE path = '/search-history.psml';
DELETE FROM page WHERE path = '/search-result-js.psml';
DELETE FROM page WHERE path = '/search-save.psml';
DELETE FROM page WHERE path = '/search-settings.psml';
DELETE FROM page WHERE path = '/service-contact-newsletter.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-law-topic-terms.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-adr-area-partner.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-adr-place-reference.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-adr-topic-mode.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-adr-topic-terms.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-area-contents.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-area-partner.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-area-sources.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-place-geothesaurus.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-place-map.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-time-constraint.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-topic-terms.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-env-topic-thesaurus.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-res-topic-attributes.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-res-topic-terms.psml';
DELETE FROM page WHERE path = '/search-catalog/search-catalog-thesaurus.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-law-topic-thesaurus.psml';
DELETE FROM page WHERE path = '/search-extended/search-ext-law-area-partner.psml';
DELETE FROM page WHERE path = '/main-features.psml';
DELETE FROM page WHERE path = '/main-environment.psml';
DELETE FROM page WHERE path = '/privacy.psml';
DELETE FROM page WHERE path = '/administration/admin-wms.psml';
DELETE FROM page WHERE path = '/_role/user/myportal-personalize.psml';
DELETE FROM page WHERE path = '/application/main-application-0.psml';
DELETE FROM page WHERE path = '/application/main-application-1.psml';
DELETE FROM page WHERE path = '/application/main-application-2.psml';
DELETE FROM page WHERE path = '/cms/cms-0.psml';

-- delete user default pages
DELETE FROM page WHERE name = 'default-page.psml' AND path LIKE '/_user/%/default-page.psml' AND path NOT LIKE '%/template/%';

-- menu clean
-- DELETE FROM folder_menu WHERE class_name = 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl';
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('service-menu', (SELECT menu_id FROM folder_menu WHERE name = 'service-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('footer-menu', (SELECT menu_id FROM folder_menu WHERE name = 'footer-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu-about', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu-about'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-menu', (SELECT menu_id FROM folder_menu WHERE name = 'main-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu-catalog', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu-catalog'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu-start', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu-start'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu-application', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu-application'));

-- service-menu
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/default-page.psml';

UPDATE folder_menu SET element_order = '0' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/language.link';
UPDATE folder_menu SET element_order = '1' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/service-myportal.psml';
UPDATE folder_menu SET element_order = '2', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/administration';

-- footer-menu
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/service-contact-newsletter.psml';
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/webmaster.link';
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/privacy.psml';

UPDATE folder_menu SET element_order = '0', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/help.psml';
UPDATE folder_menu SET element_order = '1', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') WHERE options = '/service-contact.psml';
UPDATE folder_menu SET element_order = '2', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/service-sitemap.psml';
UPDATE folder_menu SET element_order = '3' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/disclaimer.psml';

-- sub-menu-about
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-about') AND options = '/main-features.psml';

UPDATE folder_menu SET element_order = '0' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-about') AND options = '/main-about.psml';
UPDATE folder_menu SET element_order = '1' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-about') AND options = '/main-about-partner.psml';
UPDATE folder_menu SET element_order = '2' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-about') AND options = '/main-about-data-source.psml';

-- main-menu
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/main-environment.psml';
UPDATE folder_menu SET options = '/main-about.psml' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/main-features.psml';

UPDATE folder_menu SET element_order = '0' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/main-search.psml';
UPDATE folder_menu SET element_order = '1' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/main-measures.psml';
UPDATE folder_menu SET element_order = '2' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/search-catalog/search-catalog-hierarchy.psml';
UPDATE folder_menu SET element_order = '3' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/main-maps.psml';
UPDATE folder_menu SET element_order = '4' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/main-chronicle.psml';
UPDATE folder_menu SET element_order = '5' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/main-about.psml';
UPDATE folder_menu SET element_order = '6' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/application/main-application.psml';
UPDATE folder_menu SET element_order = '7', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-start') AND options = '/cms/cms-1.psml';
UPDATE folder_menu SET element_order = '8', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-start') AND options = '/cms/cms-2.psml';
UPDATE folder_menu SET element_order = '9' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/mdek';

-- sub-menu
UPDATE folder_menu SET folder_id = (SELECT folder_id FROM folder WHERE path = '/') WHERE name = 'sub-menu';
UPDATE folder_menu SET element_order = '0' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-usermanagement.psml';
UPDATE folder_menu SET element_order = '1' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-homepage.psml';
UPDATE folder_menu SET element_order = '2' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-content-rss.psml';
UPDATE folder_menu SET element_order = '3' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-content-partner.psml';
UPDATE folder_menu SET element_order = '4' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-content-provider.psml';
UPDATE folder_menu SET element_order = '5' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-iplugs.psml';
UPDATE folder_menu SET element_order = '6' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-cms.psml';
UPDATE folder_menu SET element_order = '7' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-statistics.psml';
UPDATE folder_menu SET element_order = '8' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-portal-profile.psml';
UPDATE folder_menu SET element_order = '9' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-component-monitor.psml';
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-wms.psml';

-- sub-menu-catalog
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-catalog') AND options = '/search-catalog/search-catalog-thesaurus.psml';

-- sub-menu-start
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-start');
DELETE FROM folder_menu WHERE menu_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-start');

-- sub-menu-application
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-application');
DELETE FROM folder_menu WHERE menu_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-application');

-- cms update
UPDATE ingrid_cms SET item_description = 'Anleitungen' WHERE item_key = 'portal.cms.portlet.1';
UPDATE ingrid_cms SET item_description = 'Veranstaltungen' WHERE item_key = 'portal.cms.portlet.2';
DELETE FROM ingrid_cms_item WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'portal.cms.portlet.0');
DELETE FROM ingrid_cms WHERE item_key = 'portal.cms.portlet.0';

-- fragments preferences
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY) VALUES
((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT fragment_id  FROM fragment WHERE name= 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'disclaimer')),
'sectionStyle', 0),
((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT fragment_id  FROM fragment WHERE name= 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'disclaimer')),
'articleStyle', 0),
((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT fragment_id  FROM fragment WHERE name= 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-about')),
'sectionStyle', 0),
((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT fragment_id  FROM fragment WHERE name= 'ingrid-portal-apps::InfoPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-sitemap')),
'sectionStyle', 0),
((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT fragment_id  FROM fragment WHERE name= 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'disclaimer')),
'titleTag', 0),
((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT fragment_id  FROM fragment WHERE name= 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-about')),
'titleTag', 0),
((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT fragment_id  FROM fragment WHERE name= 'ingrid-portal-apps::CMSPortlet' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-sitemap')),
'titleTag', 0);

INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE) VALUES
((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'block--pad-top'),
((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'content ob-container ob-box-narrow ob-box-center'),
((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'block--padded'),
((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'block--padded'),
((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'h1'),
((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'h1'),
((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'h1');

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;
DROP TABLE IF EXISTS ingrid_temp2;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF_VALUE';
