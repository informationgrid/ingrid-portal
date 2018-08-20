-- Create temporary table
BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

CREATE TABLE  ingrid_temp (
    temp_key VARCHAR2(255),
    temp_value NUMBER(10,0)
);

BEGIN execute immediate 'DROP TABLE ingrid_temp2'; exception when others then null; END;;

CREATE TABLE  ingrid_temp2 (
    temp_key VARCHAR2(255),
    temp_value NUMBER(10,0)
);

-- Hide '/cms/cms-1.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/cms/cms-1.psml';

-- Hide '/cms/cms-2.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/cms/cms-2.psml';

-- Show '/application/main-application.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/application/main-application.psml';

-- Hide '/main-measures.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-measures.psml';

-- Hide '/main-chronicle.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-chronicle.psml';

-- Hide '/main-about.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-about.psml';

-- Hide '/search-catalog/search-catalog-hierarchy.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/search-catalog/search-catalog-hierarchy.psml';

-- Hide '/mdek/mdek_portal_admin.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/mdek/mdek_portal_admin.psml';

-- Change '/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',                  'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::ShortcutSearchQueryPortlet',    'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',           'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+3  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',                'portlet', 3, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+4  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',                 'portlet', 4, 0, -1, -1, -1, -1, -1);

-- Change '/_role/user/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('role_user_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',                'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+6  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::ShortcutSearchQueryPortlet',  'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+7  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',         'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+8  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',              'portlet', 3, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+9  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',               'portlet', 4, 0, -1, -1, -1, -1, -1);

-- Change '/_user/template/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('user_template_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+10  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',               'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+11  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::ShortcutSearchQueryPortlet', 'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+12  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet',        'portlet', 2, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+13  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',              'portlet', 3, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+14  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',             'portlet', 4, 0, -1, -1, -1, -1, -1);

-- Create 'tmp_table'
CREATE TABLE IF NOT EXISTS tmp_table (id bigint(20) NOT NULL AUTO_INCREMENT, item_key varchar(255) COLLATE latin1_general_ci DEFAULT NULL, item_value varchar(255) COLLATE latin1_general_ci DEFAULT NULL, item_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id));
-- Use ingrid_lookup table for temporary values (sub fragment) CAUSE CAN'T DO SUBSELECTS ON SAME TABLE (MySQL) !!!
-- Remember id of sub fragment
INSERT INTO ingrid_lookup (item_key, item_value) values ('tmp_layout_fragment_id', '0');
UPDATE ingrid_lookup SET item_value = (SELECT fragment_id FROM fragment WHERE parent_id = (SELECT item_value FROM tmp_table WHERE item_key = 'tmp_id') AND name = 'jetspeed-layouts::IngridTwoColumns') WHERE item_key='tmp_layout_fragment_id';
-- Change decorator in sub fragment -->
UPDATE fragment SET decorator = 'ingrid-marginal-teaser' WHERE parent_id = (SELECT item_value FROM ingrid_lookup WHERE item_key = 'tmp_layout_fragment_id') AND name = 'ingrid-portal-apps::InfoPortlet';
-- Also replace info portlet with SearchNominatim portlet. Preferences can be kept (infoTemplate, titleKey), not used. -->
UPDATE fragment SET name = 'ingrid-portal-apps::SearchNominatim' WHERE parent_id = (SELECT item_value FROM ingrid_lookup WHERE item_key = 'tmp_layout_fragment_id') AND name = 'ingrid-portal-apps::InfoPortlet';
-- Remove temporary values from ingrid_lookup table -->
DELETE FROM ingrid_lookup WHERE item_key = 'tmp_layout_fragment_id';
-- DONE with tmp_table
DROP TABLE tmp_table;

-- Set folder "/cms" from display to hidden (only use on profile baw_wsv)
UPDATE folder SET is_hidden = 0 WHERE path = '/cms';

-- Set folder "/application" from display to hidden (only use on profile baw_wsv)
UPDATE folder SET is_hidden = 0 WHERE path = '/application';

-- Show '/language.link'
UPDATE link   SET is_hidden = 1 WHERE path = '/language.link';

-- Delete temporary table
DROP TABLE ingrid_temp;
DROP TABLE ingrid_temp2;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF_VALUE';
