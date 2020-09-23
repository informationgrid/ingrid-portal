-- DB Version
UPDATE ingrid_lookup SET item_value = '5.6.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);

-- add accessibility page 
INSERT INTO page (`PAGE_ID`, `CLASS_NAME`, `PARENT_ID`, `PATH`, `NAME`, `VERSION`, `TITLE`, `SHORT_TITLE`, `IS_HIDDEN`, `SKIN`, `DEFAULT_LAYOUT_DECORATOR`, `DEFAULT_PORTLET_DECORATOR`, `SUBSITE`, `USER_PRINCIPAL`, `ROLE_PRINCIPAL`, `GROUP_PRINCIPAL`, `MEDIATYPE`, `LOCALE`, `EXT_ATTR_NAME`, `EXT_ATTR_VALUE`, `OWNER_PRINCIPAL`) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE') , 'org.apache.jetspeed.om.page.impl.PageImpl' , (SELECT folder_id FROM folder where path='/'), '/accessibility.psml', 'accessibility.psml', NULL, 'ingrid.page.accessibility.tooltip', 'ingrid.page.accessibility', '0', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- add fragments to accessibility.psml
INSERT INTO fragment (`FRAGMENT_ID` ,`CLASS_NAME` ,`PARENT_ID` ,`PAGE_ID` ,`NAME` ,`TITLE` ,`SHORT_TITLE` ,`TYPE` ,`SKIN` ,`DECORATOR` ,`STATE` ,`PMODE` ,`LAYOUT_ROW` ,`LAYOUT_COLUMN` ,`LAYOUT_SIZES` ,`LAYOUT_X` ,`LAYOUT_Y` ,`LAYOUT_Z` ,`LAYOUT_WIDTH` ,`LAYOUT_HEIGHT` ,`OWNER_PRINCIPAL`) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', NULL , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridClearLayout', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL),
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'org.apache.jetspeed.om.page.impl.FragmentImpl', (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::AccessibilityPortlet', NULL , NULL , 'portlet', NULL , 'ingrid-teaser', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL);

-- add page_constraints_ref to accessibility.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');

-- add meta_title
INSERT INTO page_metadata (`METADATA_ID` ,`PAGE_ID` ,`NAME` ,`LOCALE` ,`VALUE`) VALUES 
    ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH="/accessibility.psml"), 'meta_title', 'de', 'ingrid.page.accessibility.meta.title');

-- add meta_description
INSERT INTO page_metadata (`METADATA_ID` ,`PAGE_ID` ,`NAME` ,`LOCALE` ,`VALUE`) VALUES 
    ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH="/accessibility.psml"), 'meta_descr', 'de', 'ingrid.page.accessibility.meta.description');

-- add meta_keywords
INSERT INTO page_metadata (`METADATA_ID` ,`PAGE_ID` ,`NAME` ,`LOCALE` ,`VALUE`) VALUES 
    ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH="/accessibility.psml"), 'meta_keywords', 'de', 'ingrid.page.accessibility.meta.keywords');

-- add accessibility to footer menu
INSERT INTO ingrid_temp (`temp_key`, `temp_value`) VALUES
    ('footer_menu_id',(SELECT menu_id FROM folder_menu WHERE NAME = 'footer-menu'));

INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN) VALUES
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'footer_menu_id'), '6', NULL, NULL, NULL, NULL, '/accessibility.psml', '0', '0', '0', NULL, NULL, NULL);

-- add cms accessibility and query shortcut
INSERT INTO ingrid_cms (item_key, item_description, item_changed_by) VALUES ('portal.accessibility', 'Barrierefreiheit', 'admin');
INSERT INTO ingrid_cms_item (fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT id FROM ingrid_cms WHERE item_key  = 'portal.accessibility'), 'de', 'Barrierefreiheit', 'Verwenden Sie die Administration des Portals um Ihren Text zur Barrierefreiheit zu pflegen und darzustellen.', 'admin');
INSERT INTO ingrid_cms_item (fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT id FROM ingrid_cms WHERE item_key  = 'portal.accessibility'), 'en', 'Accessibility', 'Verwenden Sie die Administration des Portals um Ihren Text zur Barrierefreiheit zu pflegen und darzustellen.', 'admin');

-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;