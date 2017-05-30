-- Create temporary table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value int
);

-- Show '/main-measures.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/main-measures.psml';

-- Show '/main-chronicle.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/main-chronicle.psml';

-- Show '/main-maps.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/main-maps.psml';

-- Show '/service-sitemap.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/service-sitemap.psml';

-- Show '/administration/admin-content-rss.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/administration/admin-content-rss.psml';

-- Show '/administration/admin-homepage.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/administration/admin-homepage.psml';

-- Show '/mdek/mdek_portal_admin.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/mdek/mdek_portal_admin.psml';

-- Show '/main-about-partner.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/main-about-partner.psml';

-- Show '/main-about-data-source.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/main-about-data-source.psml';

-- Show '/main-about.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/main-about.psml';

-- Show '/search-catalog/search-catalog-hierarchy.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/search-catalog/search-catalog-hierarchy.psml';

-- Show '/service-contact.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/service-contact.psml';

-- Show '/disclaimer.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/disclaimer.psml';

-- Hide '/cms/cms-1.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/cms/cms-1.psml';

-- Hide '/cms/cms-2.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/cms/cms-2.psml';

-- Hide '/application/main-application.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/application/main-application.psml';

-- Change '/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',         'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',  'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+3  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',        'portlet', 3, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+4  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::EnvironmentTeaser',    'portlet', 4, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::InfoDefaultPageTeaser','portlet', 5, 0, -1, -1, -1, -1, -1);

-- Change '/_role/user/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('role_user_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+6  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',         'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+7  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',  'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+8  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+9  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',        'portlet', 3, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::EnvironmentTeaser',    'portlet', 4, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::InfoDefaultPageTeaser','portlet', 5, 0, -1, -1, -1, -1, -1);

-- Change '/_user/template/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('user_template_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+12  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',         'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+13  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',  'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+14  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+15  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',        'portlet', 3, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+16  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::EnvironmentTeaser',    'portlet', 4, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+17  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::InfoDefaultPageTeaser','portlet', 5, 0, -1, -1, -1, -1, -1);

-- Change '/main-search.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main_search_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-search.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_search_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, decorator, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+18  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_search_fragment_id'), 'ingrid-portal-apps::SearchSimpleResult',  'portlet', 'ingrid-clear', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, decorator, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+19  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_search_fragment_id'), 'ingrid-portal-apps::SearchSimilar',       'portlet', 'ingrid-clear', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height)            VALUES ((SELECT max_key+20  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_search_fragment_id'), 'ingrid-portal-apps::SearchResult',        'portlet', 2, 0, -1, -1, -1, -1, -1);

-- Create 'tmp_table'
CREATE TABLE IF NOT EXISTS tmp_table (id bigint(20) NOT NULL AUTO_INCREMENT, item_key varchar(255) COLLATE latin1_general_ci DEFAULT NULL, item_value varchar(255) COLLATE latin1_general_ci DEFAULT NULL, item_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id));
-- Use ingrid_lookup table for temporary values (sub fragment) CAUSE CAN'T DO SUBSELECTS ON SAME TABLE (MySQL) !!!
-- Remember id of sub fragment
INSERT INTO ingrid_lookup (item_key, item_value) values ('tmp_layout_fragment_id', '0');
UPDATE ingrid_lookup SET item_value = (SELECT fragment_id FROM fragment WHERE parent_id = (SELECT item_value FROM tmp_table WHERE item_key = 'tmp_id') AND name = 'jetspeed-layouts::IngridTwoColumns') WHERE item_key='tmp_layout_fragment_id';
-- Replace SearchNominatim portlet (BAW) with info portlet. Preferences still there (infoTemplate, titleKey)
UPDATE fragment SET name = 'ingrid-portal-apps::InfoPortlet' WHERE parent_id = (SELECT item_value FROM ingrid_lookup WHERE item_key = 'tmp_layout_fragment_id') AND name = 'ingrid-portal-apps::SearchNominatim';
-- then decorator
UPDATE fragment SET decorator = 'ingrid-marginal-header' WHERE parent_id = (SELECT item_value FROM ingrid_lookup WHERE item_key = 'tmp_layout_fragment_id') AND name = 'ingrid-portal-apps::InfoPortlet';
-- REMOVE SearchBWaStr portlet. NOTICE: We keep orphaned fragment preferences, should not matter
DELETE FROM fragment WHERE NAME = 'ingrid-portal-apps::SearchBWaStr' AND PARENT_ID = (SELECT item_value FROM ingrid_lookup WHERE item_key = 'tmp_layout_fragment_id');
-- Remove temporary values from ingrid_lookup table
DELETE FROM ingrid_lookup WHERE item_key = 'tmp_layout_fragment_id';
-- Revert of replacing main-menu option "main-search" with "default-page" (BAW)
INSERT INTO tmp_table (item_key, item_value) values ('main-menu_id',  (SELECT menu_id FROM folder_menu WHERE name = 'main-menu')); 
UPDATE folder_menu SET options = '/main-search.psml' WHERE options = '/default-page.psml' AND parent_id = (SELECT item_value FROM tmp_table WHERE item_key = 'main-menu_id');
-- DONE with tmp_table
DROP TABLE tmp_table;

-- Set folder "/cms" from display to hidden (only use on profile baw_wsv)
UPDATE folder SET is_hidden = 1 WHERE path = '/cms';

-- Set folder "/application" from display to hidden (only use on profile baw_wsv)
UPDATE folder SET is_hidden = 1 WHERE path = '/application';

-- Show '/language.link'
UPDATE link SET is_hidden = 0 WHERE path = '/language.link';

-- Delete temporary table
DROP TABLE IF EXISTS ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';