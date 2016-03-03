-- DB Version
UPDATE ingrid_lookup SET item_value = '3.7.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

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
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application-0.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application-1.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/application/main-application-2.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/cms/cms-0.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/cms/cms-1.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/cms/cms-2.psml'); 
UPDATE fragment SET name = 'jetspeed-layouts::IngridClearLayout' WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-about-data-source.psml'); 


-- delete unused fragments
DELETE FROM fragment WHERE page_id IS NULL;

DELETE FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-service.psml');
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

-- update pages
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-teaser' WHERE path = '/help.psml'; 
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-teaser' WHERE path = '/main-maps.psml'; 
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-teaser' WHERE path = '/service-sitemap.psml'; 
UPDATE page SET default_layout_decorator = 'ingrid', default_portlet_decorator = 'ingrid-teaser' WHERE path = '/search-detail.psml'; 

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

-- menu clean
-- DELETE FROM folder_menu WHERE class_name = 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('service-menu', (SELECT menu_id FROM folder_menu WHERE name = 'service-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('footer-menu', (SELECT menu_id FROM folder_menu WHERE name = 'footer-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu-about', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu-about'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main-menu', (SELECT menu_id FROM folder_menu WHERE name = 'main-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('sub-menu-catalog', (SELECT menu_id FROM folder_menu WHERE name = 'sub-menu-catalog'));

-- service-menu
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/default-page.psml';

UPDATE folder_menu SET element_order = '0' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/language.link';
UPDATE folder_menu SET element_order = '1' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/service-myportal.psml';
UPDATE folder_menu SET element_order = '2', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/administration';

-- footer-menu
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/service-contact-newsletter.psml';
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/webmaster.link';

UPDATE folder_menu SET element_order = '0', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/help.psml';
UPDATE folder_menu SET element_order = '1', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') WHERE options = '/service-contact.psml';
UPDATE folder_menu SET element_order = '2' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/privacy.psml';
UPDATE folder_menu SET element_order = '3', parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'service-menu') AND options = '/service-sitemap.psml';
UPDATE folder_menu SET element_order = '4' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer-menu') AND options = '/disclaimer.psml';

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
UPDATE folder_menu SET element_order = '6' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/application/main-application';
UPDATE folder_menu SET element_order = '7' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main-menu') AND options = '/mdek';

-- sub-menu
UPDATE folder_menu SET folder_id = (SELECT folder_id FROM folder WHERE path = '/') WHERE name = 'sub-menu';
UPDATE folder_menu SET element_order = '0' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-usermanagement.psml';
UPDATE folder_menu SET element_order = '1' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-homepage.psml';
UPDATE folder_menu SET element_order = '2' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-content-rss.psml';
UPDATE folder_menu SET element_order = '3' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-content-partner.psml';
UPDATE folder_menu SET element_order = '4' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-content-provider.psml';
UPDATE folder_menu SET element_order = '5' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-iplugs.psml';
UPDATE folder_menu SET element_order = '6' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-cms.psml';
UPDATE folder_menu SET element_order = '7' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-wms.psml';
UPDATE folder_menu SET element_order = '8' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-statistics.psml';
UPDATE folder_menu SET element_order = '9' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-portal-profile.psml';
UPDATE folder_menu SET element_order = '10' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu') AND options = '/administration/admin-component-monitor.psml';

-- sub-menu-catalog
DELETE FROM folder_menu WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'sub-menu-catalog') AND options = '/search-catalog/search-catalog-thesaurus.psml';

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;
