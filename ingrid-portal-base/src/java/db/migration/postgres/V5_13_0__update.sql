-- DB Version
UPDATE ingrid_lookup SET item_value = '5.13.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value int
);

-- Change '/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',         'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',  'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+3  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',        'portlet', 3, 0, -1, -1, -1, -1, -1);

-- Change '/_role/user/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('role_user_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',         'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+6  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+7  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',  'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+8  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',        'portlet', 3, 0, -1, -1, -1, -1, -1);

-- Change '/_user/template/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('user_template_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+10  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',         'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+11  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+12  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',  'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+13  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',        'portlet', 3, 0, -1, -1, -1, -1, -1);

-- Change main menu
INSERT INTO ingrid_temp (temp_key, temp_value) values ('main_menu_id',  (SELECT menu_id FROM folder_menu WHERE name = 'main-menu')); 
UPDATE folder_menu SET options = '/main-search.psml' WHERE options = '/default-page.psml' AND parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id');
UPDATE folder_menu SET element_order = 3 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/search-catalog/search-catalog-hierarchy.psml';
UPDATE folder_menu SET element_order = 2 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/main-measures.psml';
UPDATE folder_menu SET element_order = 1 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/main-maps.psml';

-- Delete 'ingrid-portal-apps::InfoDefaultPageTeaser'
DELETE FROM fragment WHERE name = 'ingrid-portal-apps::InfoDefaultPageTeaser';

-- Delete chronicle
DELETE FROM folder_menu WHERE options = '/main-chronicle.psml';

INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main_chronicle_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-chronicle.psml')));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main_chronicle_search_fragment_id',(SELECT fragment_id FROM fragment WHERE name = 'ingrid-portal-apps::ChronicleSearch'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main_chronicle_result_fragment_id',(SELECT fragment_id FROM fragment WHERE name = 'ingrid-portal-apps::ChronicleResult'));
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT pref_id FROM fragment_pref WHERE fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_chronicle_search_fragment_id'));
DELETE FROM fragment_pref_value WHERE pref_id = (SELECT pref_id FROM fragment_pref WHERE fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_chronicle_result_fragment_id'));
DELETE FROM fragment_pref WHERE fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_chronicle_search_fragment_id');
DELETE FROM fragment_pref WHERE fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_chronicle_result_fragment_id');
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_chronicle_fragment_id');
DELETE FROM fragment WHERE fragment_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_chronicle_fragment_id');
DELETE FROM page_metadata WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-chronicle.psml');
DELETE FROM page WHERE path = '/main-chronicle.psml';

-- Update layout of disclaimer 
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('disclaimer_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/disclaimer.psml')));
UPDATE fragment SET name = 'ingrid-portal-apps::DisclaimerPortlet' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'disclaimer_fragment_id');
TRUNCATE TABLE fragment_pref;
TRUNCATE TABLE fragment_pref_value;

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';