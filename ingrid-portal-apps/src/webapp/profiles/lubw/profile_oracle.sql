-- Create temporary table
BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

CREATE TABLE  ingrid_temp (
    temp_key VARCHAR2(255),
    temp_value NUMBER(10,0)
);

-- Change '/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);

-- Change '/_role/user/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('role_user_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+4  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);

-- Change '/_user/template/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('user_template_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+8  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+9  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);

-- Show '/main-about-data-source.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/main-about-data-source.psml';

-- Show '/help.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/help.psml';

-- Show '/service-sitemap.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/service-sitemap.psml';

-- Show '/search-catalog/search-catalog-hierarchy.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/search-catalog/search-catalog-hierarchy.psml';

-- Show '/easy-language.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/easy-language.psml';

-- Show '/sign-language.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/sign-language.psml';

-- Hide '/main-measures.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-measures.psml';

-- Hide '/language.link'
UPDATE link SET is_hidden = 1 WHERE path = '/language.link';

-- Hide '/rsspage.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/rsspage.psml';

-- Hide '/main-about-partner.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-about-partner.psml';

-- Hide '/application/main-application.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/application/main-application.psml';

-- Hide 'mdek/mdek_portal_admin.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/mdek/mdek_portal_admin.psml';

-- Set folder "/mdek" to hidden
UPDATE folder SET is_hidden = 1 WHERE path = '/mdek';

-- Hide folder "/application" to display
UPDATE folder SET is_hidden = 1 WHERE path = '/application';

-- Change main menu order
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main_menu_id',(SELECT menu_id FROM folder_menu WHERE name = 'main-menu'));
UPDATE folder_menu SET element_order = 1 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/main-maps.psml';
UPDATE folder_menu SET element_order = 2 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/main-measures.psml';
UPDATE folder_menu SET element_order = 3 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/search-catalog/search-catalog-hierarchy.psml';

-- Delete all '/_user/<USER>/default-page.psml'
DELETE FROM page WHERE PATH LIKE '/_user/%/default-page.psml' AND NOT PATH = '/_user/template/default-page.psml';

-- Delete temporary table
DROP TABLE ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
