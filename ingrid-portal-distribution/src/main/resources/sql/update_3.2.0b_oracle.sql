-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.2.0b', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Update for profile BAW_WSV
-- ==========================
-- 
-- Add application pages.
-- * Add application (/application/main-application.psml) to main menu. (default hidden)
-- * Add application sub menu (including three other pages) to application main menu.
-- * Add CMS shortcut teaser portlet for default page to access to applications.
-- 
-- Add IFrame portlet to integrate external services.
-- * Including a edit mode to change portlet preferences as user admin.
-- * Add 'admin' page_constraints_ref to get the edit mode for the iFrame portlets.
-- * Add fragment_pref to set default preferences for iFrame portlets.
--
-- Add CMS shortcut portlet for default page to define and access search querys.
--
-- Add meta_title, meta_description, meta_keyword to new added pages.
-- -----------------------------

-- Temp Table
-- oracle way of: DROP TABLE IF EXISTS

-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

-- BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;
-- /

-- !!!!!!!! -------------------------------------------------

CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

-- add folder
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES
 ('folder_id',(SELECT folder_id FROM folder WHERE parent_id IS NULL AND name = '/' AND path = '/'));

INSERT INTO folder (FOLDER_ID, PARENT_ID, PATH, NAME, TITLE, SHORT_TITLE, IS_HIDDEN) VALUES 
 ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'folder_id'), '/application', 'application', 'ingrid.page.application', 'ingrid.page.application', 1); 

-- add application pages 
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE')  , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), '/application/main-application.psml', 'main-application.psml', NULL, 'ingrid.page.application.tooltip', 'ingrid.page.application', '1', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), '/application/main-application-1.psml', 'main-application-1.psml', NULL, 'ingrid.page.application.1.tooltip', 'ingrid.page.application.1', '0', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), '/application/main-application-0.psml', 'main-application-0.psml', NULL, 'ingrid.page.application.0.tooltip', 'ingrid.page.application.0', '0', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER'), '/application/main-application-2.psml', 'main-application-2.psml', NULL, 'ingrid.page.application.2.tooltip', 'ingrid.page.application.2', '0', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- add fragments to main-application.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::ApplicationOverviewPortlet', NULL , NULL , 'portlet', NULL , 'ingrid-teaser', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add fragments to main-application-1.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::IFramePortalPortlet', NULL , NULL , 'portlet', NULL , 'ingrid-teaser', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add fragments to main-application-0.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::IFramePortalPortlet', NULL , NULL , 'portlet', NULL , 'ingrid-teaser', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add fragments to main-application-2.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::IFramePortalPortlet', NULL , NULL , 'portlet', NULL , 'ingrid-teaser', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- add fragment_pref to main-application-1.psml
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'HEIGHT', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'SRC', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'titleKey', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'WIDTH', '0');

-- add fragment_pref to main-application-0.psml
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'HEIGHT', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'SRC', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'titleKey', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'WIDTH', '0');
   
-- add fragment_pref to main-application-2.psml
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'HEIGHT', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'SRC', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'titleKey', '0');
INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
   ((SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'WIDTH', '0');
   
-- add fragment_pref_value to main-application-1.psml
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', '450');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', ' ');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', 'ingrid.application.1.title');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', '883');

-- add fragment_pref_value to main-application-0.psml
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', '450');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES
   ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', ' ');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES
   ((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', 'ingrid.application.0.title');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES
   ((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', '883');
   
-- add fragment_pref_value to main-application-2.psml
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', '450');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', ' ');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', 'ingrid.application.2.title');
INSERT INTO fragment_pref_value (PREF_VALUE_ID ,PREF_ID ,VALUE_ORDER ,VALUE) VALUES 
   ((SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'), (SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), '0', '883');

-- add page_constraints_ref to main-application.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');

-- add page_constraints_ref to main-application-1.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 1, 'admin');
	
-- add page_constraints_ref to main-application-0.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 1, 'admin');

-- add page_constraints_ref to main-application-2.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 0, 'public-view');
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) VALUES 
	((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'), (SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_PAGE'), 1, 'admin');

-- add meta_title
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application.psml'), 'meta_title', 'de', 'ingrid.page.application.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-1.psml'), 'meta_title', 'de', 'ingrid.page.application.1.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-0.psml'), 'meta_title', 'de', 'ingrid.page.application.0.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+3  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-2.psml'), 'meta_title', 'de', 'ingrid.page.application.2.meta.title');
	
-- add meta_description
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application.psml'), 'meta_descr', 'de', 'ingrid.page.application.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-1.psml'), 'meta_descr', 'de', 'ingrid.page.application.1.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-0.psml'), 'meta_descr', 'de', 'ingrid.page.application.0.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-2.psml'), 'meta_descr', 'de', 'ingrid.page.application.2.meta.description');

-- add meta_keywords
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application.psml'), 'meta_keywords', 'de', 'ingrid.page.application.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-1.psml'), 'meta_keywords', 'de', 'ingrid.page.application.1.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-0.psml'), 'meta_keywords', 'de', 'ingrid.page.application.0.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/application/main-application-2.psml'), 'meta_keywords', 'de', 'ingrid.page.application.2.meta.keywords');


-- add application to main menu
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES
 ('main_menu_id',(SELECT menu_id FROM folder_menu WHERE NAME = 'main-menu'));

UPDATE folder_menu SET ELEMENT_ORDER = '9' WHERE folder_menu.OPTIONS ='/administration' AND folder_menu.PARENT_ID = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id');
UPDATE folder_menu SET ELEMENT_ORDER = '10' WHERE folder_menu.OPTIONS ='/Administrative' AND folder_menu.PARENT_ID = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id');
UPDATE folder_menu SET ELEMENT_ORDER = '11' WHERE folder_menu.OPTIONS ='/mdek' AND folder_menu.PARENT_ID = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id');

INSERT INTO folder_menu (MENU_ID, CLASS_NAME, PARENT_ID, ELEMENT_ORDER, NAME, TITLE, SHORT_TITLE, TEXT, OPTIONS, DEPTH, IS_PATHS, IS_REGEXP, PROFILE, OPTIONS_ORDER, SKIN) VALUES
 ((SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'main_menu_id'), '8', NULL, NULL, NULL, NULL, '/application/main-application.psml', '0', '0', '0', NULL, NULL, NULL);

-- add application submenu
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+1   FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL , (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'folder_id') , '0', 'sub-menu-application', NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL, '0', NULL, NULL, NULL, NULL, '/application/main-application.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL , '1', NULL , NULL , NULL , NULL , '/application/main-application-0.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL , '2', NULL , NULL , NULL , NULL , '/application/main-application-1.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL , '3', NULL , NULL , NULL , NULL , '/application/main-application-2.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);

-- add cms application and query shortcut
INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'portal.teaser.shortcut', 'Anwendungen', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.teaser.shortcut'), 'de', 'Anwendungen', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.teaser.shortcut'), 'en', 'Application', '', 'admin');

INSERT INTO ingrid_cms (id, item_key, item_description, item_changed_by) VALUES ((SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'portal.teaser.shortcut.query', 'Schnellsuche', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.teaser.shortcut.query'), 'de', 'Schnellsuche', '', 'admin');
INSERT INTO ingrid_cms_item (id, fk_ingrid_cms_id, item_lang, item_title, item_value, item_changed_by) VALUES ((SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), (SELECT id FROM ingrid_cms WHERE item_key  = 'portal.teaser.shortcut.query'), 'en', 'Quick Search', '', 'admin');


-- delete temporary table
DROP TABLE ingrid_temp;
    
-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF_VALUE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER';
