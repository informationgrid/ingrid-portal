-- DB Version
UPDATE ingrid_lookup SET item_value = '7.3.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value int
);

-- Insert page for easy-language.psml and sign-language.psml
INSERT INTO page (PAGE_ID, CLASS_NAME, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE') , 'org.apache.jetspeed.om.page.impl.PageImpl', '1', '/easy-language.psml', 'easy-language.psml', NULL, 'ingrid.page.easylanguage.tooltip', 'ingrid.page.easylanguage', '1', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE') , 'org.apache.jetspeed.om.page.impl.PageImpl', '1', '/sign-language.psml', 'sign-language.psml', NULL, 'ingrid.page.signlanguage.tooltip', 'ingrid.page.signlanguage', '1', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- Add fragments for easy-language.psml and sign-language.psml
INSERT INTO fragment (FRAGMENT_ID ,CLASS_NAME ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,OWNER_PRINCIPAL) VALUES
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridClearLayout', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL),
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::EasyLanguagePortlet', NULL , NULL , 'portlet', NULL , NULL, NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL),
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL , (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridClearLayout', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL),
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::SignLanguagePortlet', NULL , NULL , 'portlet', NULL , NULL, NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL);

-- Add page_constraints_ref for easy-language.psml and sign-language.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view'),
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');

-- Add meta title, description, keywords for easy-language.psml and sign-language.psml
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/easy-language.psml'), 'meta_title', 'de', 'ingrid.page.easylanguage.meta.title'),
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/easy-language.psml'), 'meta_descr', 'de', 'ingrid.page.easylanguage.meta.description'),
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/easy-language.psml'), 'meta_keywords', 'de', 'ingrid.page.easylanguage.meta.keywords'),
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/sign-language.psml'), 'meta_title', 'de', 'ingrid.page.signlanguage.meta.title'),
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/sign-language.psml'), 'meta_descr', 'de', 'ingrid.page.signlanguage.meta.description'),
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/sign-language.psml'), 'meta_keywords', 'de', 'ingrid.page.signlanguage.meta.keywords');

-- Delete temporary table.
DROP TABLE IF EXISTS ingrid_temp;

-- Update max keys.
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
