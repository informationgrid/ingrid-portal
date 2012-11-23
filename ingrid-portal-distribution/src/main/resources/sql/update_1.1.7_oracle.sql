-- create hibernate sequence
create sequence hibernate_sequence;

-- alter table PARAMETER, allow NULL values as parameter_value, because of the automatic conversion from '' to NULL values in oracle
ALTER TABLE PARAMETER MODIFY "PARAMETER_VALUE" VARCHAR2(2000) NULL;

-- import ingrid specific permissions
INSERT INTO security_permission VALUES (3, 'de.ingrid.portal.security.permission.IngridPortalPermission', 'admin', '*', to_date ('2008-02-11', 'yyyy-mm-dd'), to_date ('2008-02-11', 'yyyy-mm-dd'));
INSERT INTO security_permission VALUES (4, 'de.ingrid.portal.security.permission.IngridPortalPermission', 'admin.portal', '*', to_date ('2008-02-11', 'yyyy-mm-dd'), to_date ('2008-02-11', 'yyyy-mm-dd'));
INSERT INTO security_permission VALUES (5, 'de.ingrid.portal.security.permission.IngridPortalPermission', 'admin.portal.partner', '*', to_date ('2008-02-11', 'yyyy-mm-dd'), to_date ('2008-02-11', 'yyyy-mm-dd'));
INSERT INTO security_permission VALUES (6, 'de.ingrid.portal.security.permission.IngridPortalPermission', 'admin.portal.partner.provider.index', '*', to_date ('2008-02-11', 'yyyy-mm-dd'), to_date ('2008-02-11', 'yyyy-mm-dd'));
INSERT INTO security_permission VALUES (7, 'de.ingrid.portal.security.permission.IngridPortalPermission', 'admin.portal.partner.provider.catalog', '*', to_date ('2008-02-11', 'yyyy-mm-dd'), to_date ('2008-02-11', 'yyyy-mm-dd'));
-- add ingrid admin permission to admin portal user
INSERT INTO principal_permission VALUES (6, 3);

-- remove DWR test portlet
DELETE FROM folder_menu WHERE OPTIONS = '/administration/dwr-test.psml';
DELETE FROM page_constraints_ref WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE NAME='dwr-test.psml');
DELETE FROM page_metadata WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE NAME='dwr-test.psml');
DELETE FROM fragment WHERE PARENT_ID=(SELECT FRAGMENT_ID FROM fragment WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE NAME='dwr-test.psml'));
DELETE FROM fragment WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE NAME='dwr-test.psml');
DELETE FROM page WHERE NAME='dwr-test.psml';


-- pages anlegen/aendern -> page
-- =====================

-- neue Seiten anlegen

-- search-ext-law-topic-terms.psml
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
(SELECT FOLDER_ID FROM folder WHERE NAME = 'search-extended'), -- folder id
'/search-extended/search-ext-law-topic-terms.psml', 'search-ext-law-topic-terms.psml', NULL, 'Search Extended Law', 'Search Extended Law', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- search-ext-law-topic-thesaurus.psml
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
(SELECT FOLDER_ID FROM folder WHERE NAME = 'search-extended'), -- folder id
'/search-extended/search-ext-law-topic-thesaurus.psml', 'search-ext-law-topic-thesaurus.psml', NULL, 'Search Extended Law', 'Search Extended Law', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- search-ext-law-area-partner.psml
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
(SELECT FOLDER_ID FROM folder WHERE NAME = 'search-extended'), -- folder id
'/search-extended/search-ext-law-area-partner.psml', 'search-ext-law-area-partner.psml', NULL, 'Search Extended Law', 'Search Extended Law', 0, 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- page constraints referentiell -> page_constraints_ref
-- =====================

-- search-ext-law-topic-terms.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
0, 'public-view');

-- search-ext-law-topic-thesaurus.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'),
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
0, 'public-view');

-- search-ext-law-area-partner.psml
INSERT INTO page_constraints_ref (CONSTRAINTS_REF_ID, PAGE_ID, APPLY_ORDER, NAME) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE_CONSTRAINTS_REF'),
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
0, 'public-view');


-- page fragments -> fragment
-- =====================

-- page: search-ext-law-topic-terms.psml

-- layout fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
NULL, 
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment ingrid-portal-apps::SearchSimple
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'searchSimple.title.extended');

-- Preference 'helpKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'helpKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'ext-search-subject-1');


-- portlet fragment ingrid-portal-apps::SearchExtLawTopicTerms
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchExtLawTopicTerms', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment ingrid-portal-apps::InfoPortlet
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'infoTemplate'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'infoTemplate', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, '/WEB-INF/templates/search_extended/search_ext_law_topic_terms_info.vm');

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'searchExtEnvTopicTerms.info.title');

-- page: search-ext-law-topic-thesaurus.psml

-- layout fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
NULL, 
(SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment ingrid-portal-apps::SearchSimple
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'searchSimple.title.extended');

-- Preference 'helpKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'helpKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+5 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'ext-search-subject-1');


-- portlet fragment ingrid-portal-apps::SearchExtLawTopicThesaurus
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchExtLawTopicThesaurus', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment ingrid-portal-apps::InfoPortlet
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'infoTemplate'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'infoTemplate', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+6 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, '/WEB-INF/templates/search_extended/search_ext_law_topic_thesaurus_info.vm');

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+7 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'searchExtEnvTopicThesaurus.info.title');

-- page: search-ext-law-area-partner.psml

-- layout fragment
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
NULL, 
(SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_PAGE'), -- page id
'jetspeed-layouts::IngridTwoColumns', NULL, NULL, 'layout', NULL, NULL, NULL, NULL, -1, -1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment ingrid-portal-apps::SearchSimple
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchSimple', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'searchSimple.title.extended');

-- Preference 'helpKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'helpKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+9 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'ext-search-area-1');


-- portlet fragment ingrid-portal-apps::SearchExtLawAreaPartner
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchExtLawAreaPartner', NULL, NULL, 'portlet', NULL, 'clear', NULL, NULL, 1, 0, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- portlet fragment ingrid-portal-apps::InfoPortlet
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT max_key+8 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::InfoPortlet', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- Preference 'infoTemplate'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'infoTemplate', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, '/WEB-INF/templates/search_extended/search_ext_law_area_partner_info.vm');

-- Preference 'titleKey'
INSERT INTO fragment_pref (PREF_ID, FRAGMENT_ID, NAME, IS_READ_ONLY)
VALUES (
(SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
(SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
'titleKey', 0);
INSERT INTO fragment_pref_value (PREF_VALUE_ID, PREF_ID, VALUE_ORDER, VALUE)
VALUES (
(SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF_VALUE'),
(SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'),
0, 'searchExtEnvAreaPartner.info.title');

-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen

-- page
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_PAGE';
-- page_constraints_ref
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_PAGE_CONSTRAINTS_REF';
-- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT_PREF';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT_PREF_VALUE';
