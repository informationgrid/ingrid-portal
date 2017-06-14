-- Create temporary table
BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

CREATE TABLE  ingrid_temp (
    temp_key VARCHAR2(255),
    temp_value NUMBER(10,0)
);

-- Hide 'main-measures.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-measures.psml';

-- Hide 'main-chronicle.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-chronicle.psml';

-- Hide 'main-about-data-source.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/main-about-data-source.psml';

-- Hide 'help.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/help.psml';

-- Hide 'search-catalog/search-catalog-hierarchy.psml'
UPDATE page SET is_hidden = 1 WHERE path = '/search-catalog/search-catalog-hierarchy.psml';

-- Change '/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet', 'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',      'portlet', 2, 0, -1, -1, -1, -1, -1);

-- Change '/_role/user/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('role_user_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+3  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+4  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet', 'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',      'portlet', 2, 0, -1, -1, -1, -1, -1);

-- Change '/_user/template/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('user_template_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+6  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+7  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::IngridInformPortlet', 'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+8  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',      'portlet', 2, 0, -1, -1, -1, -1, -1);

-- Change '/main-search.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main_search_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-search.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_search_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, decorator, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+9   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_search_fragment_id'), 'ingrid-portal-apps::SearchSimpleResult',  'portlet', 'ingrid-clear', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height)            VALUES ((SELECT max_key+10  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_search_fragment_id'), 'ingrid-portal-apps::SearchResult',        'portlet', 1, 0, -1, -1, -1, -1, -1);

-- Hide '/language.link'
UPDATE link SET is_hidden = 1 WHERE path = '/language.link';

-- Delete temporary table
DROP TABLE ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';