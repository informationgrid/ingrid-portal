-- Create temporary table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);

-- Hide 'main-chronicle.psml'
--UPDATE page SET is_hidden = 1 WHERE path = '/main-chronicle.psml';
UPDATE page SET is_hidden = 0 WHERE path = '/search-catalog-hierarchy.psml';
UPDATE page SET is_hidden = 1 WHERE path = '/portal/main-about-partner.psml';

-- Change '/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',      'portlet', 2, 0, -1, -1, -1, -1, -1);

-- Change '/_role/user/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('role_user_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_role/user/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+4  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+6  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'role_user_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',      'portlet', 2, 0, -1, -1, -1, -1, -1);

-- Change '/_user/template/default-page.psml'
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('user_template_default_page_fragment_id',(SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/_user/template/default-page.psml')));
DELETE FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id');
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+8  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::SearchSimple',        'portlet', 0, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+9  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::RssNewsTeaser',       'portlet', 1, 0, -1, -1, -1, -1, -1);
INSERT INTO fragment (fragment_id, class_name, parent_id, name, type, layout_row, layout_column, layout_x, layout_y, layout_z, layout_width, layout_height) VALUES ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'user_template_default_page_fragment_id'), 'ingrid-portal-apps::CategoryTeaser',      'portlet', 2, 0, -1, -1, -1, -1, -1);

-- Hide '/language.link'
UPDATE link SET is_hidden = 1 WHERE path = '/language.link';

-- Change main menu order
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES ('main_menu_id',(SELECT menu_id FROM folder_menu WHERE name = 'main-menu'));
UPDATE folder_menu SET element_order = 1 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/main-maps.psml';
UPDATE folder_menu SET element_order = 2 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/main-measures.psml';
UPDATE folder_menu SET element_order = 3 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/search-catalog/search-catalog-hierarchy.psml';
UPDATE folder_menu SET element_order = 4 WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id') AND options = '/main-about.psml';

-- Delete temporary table
DROP TABLE IF EXISTS ingrid_temp;

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';

-- update partners
INSERT INTO `ingrid_partner` (`id`, `ident`, `name`, `sortkey`) VALUES
(18, 'kuk', 'Küsteningenieruwesen und Küstenwasserschutz', 18),
(19, 'mus', 'Meeresumweltschutz', 19),
(20, 'mns', 'Meeresnaturschutz ', 20);
 
DELETE FROM `ingrid_partner` WHERE sortkey BETWEEN 1 AND 17;
 
INSERT INTO `ingrid_provider` (`ident`, `name`, `url`, `sortkey`, `sortkey_partner`) VALUES
('kuk_baw', 'Bundesanstalt für Wasserbau (BAW)', 'https://www.baw.de/', 1, 18),
('kuk_lkn', 'Landesbetrieb für Küstenschutz, Nationalpark und Meeresschutz S-H (LKN)', 'https://www.schleswig-holstein.de/DE/Landesregierung/LKN/lkn_node.html', 2, 18),
('kuk_nlwkn', 'Niedersächsischer Landesbetrieb für Wasserwirtschaft, Küsten- und Naturschutz (NLWKN) ', 'https://www.nlwkn.niedersachsen.de/', 3, 18),
('kuk_nlpv', 'Nationalparkverwaltung Niedersächsisches Wattenmeer (NLPV)', 'https://www.nationalpark-wattenmeer.de/nds', 4, 18),
('mus_bsh', 'Bundesamt für Seeschifffahrt und Hydrographie (BSH)', 'https://www.bsh.de/', 1, 19),
('mus_llur', 'Landesamt für Landwirtschaft, Umwelt und ländliche Räume S-H (LLUR) ', 'https://www.schleswig-holstein.de/DE/Landesregierung/LLUR/llur_node.html', 2, 19),
('mus_lung', 'Landesamt für Umwelt, Naturschutz und Geologie M-V (LUNG) ', 'http://www.lung.mv-regierung.de/', 3, 19),
('mns_bfn', 'Bundesamt für Naturschutz (BfN)', 'http://www.bfn.de/', 1, 20);
