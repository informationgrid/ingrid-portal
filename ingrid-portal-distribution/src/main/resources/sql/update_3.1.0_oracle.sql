-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.1.0', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- reorder main menu
UPDATE folder_menu SET ELEMENT_ORDER = '1' WHERE OPTIONS ='/main-environment.psml';
UPDATE folder_menu SET ELEMENT_ORDER = '3' WHERE OPTIONS ='/main-service.psml';
UPDATE folder_menu SET ELEMENT_ORDER = '5' WHERE OPTIONS ='/main-maps.psml';
UPDATE folder_menu SET ELEMENT_ORDER = '6' WHERE OPTIONS ='/main-chronicle.psml';
UPDATE folder_menu SET ELEMENT_ORDER = '7', OPTIONS = '/main-features.psml' WHERE OPTIONS ='/main-about.psml';
UPDATE folder_menu SET ELEMENT_ORDER = '8' WHERE OPTIONS ='/administration';
UPDATE folder_menu SET ELEMENT_ORDER = '9' WHERE OPTIONS ='/Administrative';

-- move catalog menu to main menu
UPDATE folder_menu SET PARENT_ID = '19', ELEMENT_ORDER = '4' WHERE folder_menu.OPTIONS ='/search-catalog/search-catalog-hierarchy.psml';

-- change sitemap from two-column to one-column layout
UPDATE fragment SET NAME = 'jetspeed-layouts::IngridOneColumn' WHERE PAGE_ID =(SELECT PAGE_ID FROM page WHERE PATH ='/service-sitemap.psml');

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
-- oracle way of: DROP TABLE IF EXISTS ingrid_temp;
--begin
--  execute immediate 'DROP TABLE ingrid_temp';
--  exception when others then null;
--end;
--/
BEGIN
execute immediate 'DROP TABLE ingrid_temp';
exception when others then null;
END;
/
CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

-- choose correct menu for deletion of search submenu
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES 
    ('TO_DELETE', (SELECT f1.MENU_ID FROM folder_menu f1, folder_menu f2 where f1.OPTIONS='/main-search.psml' AND f1.PARENT_ID=f2.MENU_ID AND f2.NAME='sub-menu-search'));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES 
    ('START_PAGE_LAYOUT', (SELECT FRAGMENT_ID FROM fragment WHERE NAME='jetspeed-layouts::IngridTwoColumns' AND PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml' )));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES 
    ('NEWSLETTER_FRAGMENT_ID', (SELECT FRAGMENT_ID FROM fragment WHERE NAME='ingrid-portal-apps::ContactNewsletterPortlet' AND PARENT_ID=(SELECT FRAGMENT_ID FROM fragment WHERE NAME='jetspeed-layouts::IngridTwoColumns' AND PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/service-contact-newsletter.psml' ))));
INSERT INTO ingrid_temp (temp_key, temp_value) VALUES 
    ('CONTACT_TEASER_FRAGMENT_ID', (SELECT FRAGMENT_ID FROM fragment WHERE NAME='ingrid-portal-apps::InfoPortlet' AND LAYOUT_ROW=0 AND LAYOUT_COLUMN=1 AND PARENT_ID=(SELECT FRAGMENT_ID FROM fragment WHERE NAME='jetspeed-layouts::IngridTwoColumns' AND PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/service-contact-newsletter.psml' ))));

DELETE FROM folder_menu WHERE MENU_ID = (SELECT temp_value FROM ingrid_temp where temp_key = 'TO_DELETE');

-- remove service teaser portlet from start page
DELETE FROM fragment WHERE NAME = 'ingrid-portal-apps::ServiceTeaser' AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');

-- reorder portlets on right side
UPDATE fragment SET LAYOUT_ROW='1' WHERE NAME='ingrid-portal-apps::ChronicleTeaser' AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');
UPDATE fragment SET LAYOUT_ROW='2' WHERE NAME='ingrid-portal-apps::WeatherTeaser'   AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');
UPDATE fragment SET LAYOUT_ROW='3' WHERE NAME='ingrid-portal-apps::MeasuresTeaser'  AND PARENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'START_PAGE_LAYOUT');

-- delete all titles/short-titles overwritten in page-metadata
DELETE FROM page_metadata WHERE NAME ='title' OR NAME ='short-title';

-- add localizable page titles
UPDATE page SET TITLE = 'ingrid.page.search.catalog.tooltip' WHERE  page.PATH ='/search-catalog/search-catalog-hierarchy.psml';
UPDATE page SET TITLE = 'ingrid.page.search.tooltip' WHERE          page.PATH ='/main-search.psml';
UPDATE page SET TITLE = 'ingrid.page.service.tooltip' WHERE         page.PATH ='/main-service.psml';
UPDATE page SET TITLE = 'ingrid.page.measures.tooltip' WHERE        page.PATH ='/main-measures.psml';
UPDATE page SET TITLE = 'ingrid.page.envtopics.tooltip' WHERE       page.PATH ='/main-environment.psml';
UPDATE page SET TITLE = 'ingrid.page.maps.tooltip' WHERE            page.PATH ='/main-maps.psml';
UPDATE page SET TITLE = 'ingrid.page.chronicle.tooltip' WHERE       page.PATH ='/main-chronicle.psml';
UPDATE page SET TITLE = 'ingrid.page.about.tooltip' WHERE           page.PATH ='/main-about.psml';

UPDATE page SET TITLE = 'ingrid.page.home.tooltip' WHERE            page.PATH ='/default-page.psml';
UPDATE page SET TITLE = 'ingrid.page.myportal.tooltip' WHERE        page.PATH ='/service-myportal.psml';
UPDATE page SET TITLE = 'ingrid.page.sitemap.tooltip' WHERE         page.PATH ='/service-sitemap.psml';
UPDATE page SET TITLE = 'ingrid.page.help.link.tooltip' WHERE       page.PATH ='/help.psml';
UPDATE page SET TITLE = 'ingrid.page.contact.tooltip' WHERE         page.PATH ='/service-contact.psml';
UPDATE page SET TITLE = 'ingrid.page.disclaimer.tooltip' WHERE      page.PATH ='/disclaimer.psml';
UPDATE page SET TITLE = 'ingrid.page.privacy.tooltip' WHERE         page.PATH ='/privacy.psml';

UPDATE page SET TITLE = 'ingrid.page.newsletter.tooltip',SHORT_TITLE = 'ingrid.page.newsletter' WHERE page.PATH ='/service-contact-newsletter.psml';

UPDATE link SET TITLE = 'ingrid.page.webmaster.tooltip' WHERE       link.PATH ='/webmaster.link';

-- fix search link to correct page in help
UPDATE fragment_pref_value SET VALUE = 'search-1' WHERE PREF_VALUE_ID = (SELECT PREF_ID FROM fragment_pref WHERE NAME='helpKey' AND FRAGMENT_ID=(SELECT FRAGMENT_ID FROM fragment WHERE NAME='ingrid-portal-apps::SearchSimple' AND PARENT_ID=(SELECT FRAGMENT_ID FROM fragment WHERE NAME='jetspeed-layouts::IngridTwoColumns' AND PARENT_ID=(SELECT FRAGMENT_ID FROM fragment WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/main-search.psml')))));

-- move help from contact teaser to newsletter portlet
UPDATE fragment_pref SET FRAGMENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'NEWSLETTER_FRAGMENT_ID') WHERE NAME='helpKey' AND FRAGMENT_ID=(SELECT temp_value FROM ingrid_temp where temp_key = 'CONTACT_TEASER_FRAGMENT_ID');

-- add feature page
INSERT INTO page (PAGE_ID, PARENT_ID, PATH, NAME, VERSION, TITLE, SHORT_TITLE, IS_HIDDEN, SKIN, DEFAULT_LAYOUT_DECORATOR, DEFAULT_PORTLET_DECORATOR, SUBSITE, USER_PRINCIPAL, ROLE_PRINCIPAL, GROUP_PRINCIPAL, MEDIATYPE, LOCALE, EXT_ATTR_NAME, EXT_ATTR_VALUE, OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_PAGE'), '1', '/main-features.psml', 'main-features.psml', NULL, 'ingrid.page.about.tooltip', 'ingrid.page.about', '0', 'orange', 'ingrid', 'ingrid-teaser', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- add meta_title
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key    FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml'), 'meta_title', 'de',                                'ingrid.page.home.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+1  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-search.psml'), 'meta_title', 'de',                                 'ingrid.page.search.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+2  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-extended/search-ext-env-topic-terms.psml'), 'meta_title', 'de',  'ingrid.page.ext.search.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+3  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-history.psml'), 'meta_title', 'de',                              'ingrid.page.search.history.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+4  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-settings.psml'), 'meta_title', 'de',                             'ingrid.page.search.settings.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+5  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-catalog/search-catalog-hierarchy.psml'), 'meta_title', 'de',     'ingrid.page.hierarchy.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+6  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-catalog/search-catalog-thesaurus.psml'), 'meta_title', 'de',     'ingrid.page.thesaurus.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+7  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-service.psml'), 'meta_title', 'de',                                'ingrid.page.service.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+8  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-measures.psml'), 'meta_title', 'de',                               'ingrid.page.measures.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+9  FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-environment.psml'), 'meta_title', 'de',                            'ingrid.page.environment.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+10 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-maps.psml'), 'meta_title', 'de',                                   'ingrid.page.maps.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+11 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-chronicle.psml'), 'meta_title', 'de',                              'ingrid.page.chronicle.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+12 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about.psml'), 'meta_title', 'de',                                  'ingrid.page.about.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+13 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about-partner.psml'), 'meta_title', 'de',                          'ingrid.page.about.partner.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+14 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-myportal.psml'), 'meta_title', 'de',                            'ingrid.page.myportal.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+15 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/privacy.psml'), 'meta_title', 'de',                                     'ingrid.page.privacy.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+16 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/rss-news.psml'), 'meta_title', 'de',                                    'ingrid.page.rss.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+17 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-sitemap.psml'), 'meta_title', 'de',                             'ingrid.page.sitemap.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+18 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-contact.psml'), 'meta_title', 'de',                             'ingrid.page.contact.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+19 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-contact-newsletter.psml'), 'meta_title', 'de',                  'ingrid.page.newsletter.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+20 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/disclaimer.psml'), 'meta_title', 'de',                                  'ingrid.page.disclaimer.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+21 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-detail.psml'), 'meta_title', 'de',                               'ingrid.page.detail.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+22 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-features.psml'), 'meta_title', 'de',                             'ingrid.page.features.meta.title');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+23 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/help.psml'), 'meta_title', 'de',                                        'ingrid.page.help.meta.title');

-- add meta_description
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+24 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml'), 'meta_descr', 'de',                                'ingrid.page.home.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+25 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-search.psml'), 'meta_descr', 'de',                                 'ingrid.page.search.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+26 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-extended/search-ext-env-topic-terms.psml'), 'meta_descr', 'de',  'ingrid.page.ext.search.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+27 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-history.psml'), 'meta_descr', 'de',                              'ingrid.page.search.history.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+28 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-settings.psml'), 'meta_descr', 'de',                             'ingrid.page.search.settings.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+29 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-catalog/search-catalog-hierarchy.psml'), 'meta_descr', 'de',     'ingrid.page.hierarchy.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+30 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-catalog/search-catalog-thesaurus.psml'), 'meta_descr', 'de',     'ingrid.page.thesaurus.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+31 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-service.psml'), 'meta_descr', 'de',                                'ingrid.page.service.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+32 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-measures.psml'), 'meta_descr', 'de',                               'ingrid.page.measures.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+33 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-environment.psml'), 'meta_descr', 'de',                            'ingrid.page.environment.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+34 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-maps.psml'), 'meta_descr', 'de',                                   'ingrid.page.maps.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+35 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-chronicle.psml'), 'meta_descr', 'de',                              'ingrid.page.chronicle.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+36 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about.psml'), 'meta_descr', 'de',                                  'ingrid.page.about.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+37 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about-partner.psml'), 'meta_descr', 'de',                          'ingrid.page.about.partner.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+38 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-myportal.psml'), 'meta_descr', 'de',                            'ingrid.page.myportal.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+39 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/privacy.psml'), 'meta_descr', 'de',                                     'ingrid.page.privacy.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+40 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/rss-news.psml'), 'meta_descr', 'de',                                    'ingrid.page.rss.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+41 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-sitemap.psml'), 'meta_descr', 'de',                             'ingrid.page.sitemap.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+42 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-contact.psml'), 'meta_descr', 'de',                             'ingrid.page.contact.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+43 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-contact-newsletter.psml'), 'meta_descr', 'de',                  'ingrid.page.newsletter.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+44 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/disclaimer.psml'), 'meta_descr', 'de',                                  'ingrid.page.disclaimer.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+45 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-detail.psml'), 'meta_descr', 'de',                               'ingrid.page.detail.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+46 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-features.psml'), 'meta_descr', 'de',                             'ingrid.page.features.meta.description');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+47 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/help.psml'), 'meta_descr', 'de',                                        'ingrid.page.help.meta.description');


-- add meta_keywords
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+48 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml'), 'meta_keywords', 'de',                                'ingrid.page.home.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+49 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-search.psml'), 'meta_keywords', 'de',                                 'ingrid.page.search.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+50 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-extended/search-ext-env-topic-terms.psml'), 'meta_keywords', 'de',  'ingrid.page.ext.search.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+51 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-history.psml'), 'meta_keywords', 'de',                              'ingrid.page.search.history.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+52 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-settings.psml'), 'meta_keywords', 'de',                             'ingrid.page.search.settings.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+53 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-catalog/search-catalog-hierarchy.psml'), 'meta_keywords', 'de',     'ingrid.page.hierarchy.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+54 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-catalog/search-catalog-thesaurus.psml'), 'meta_keywords', 'de',     'ingrid.page.thesaurus.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+55 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-service.psml'), 'meta_keywords', 'de',                                'ingrid.page.service.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+56 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-measures.psml'), 'meta_keywords', 'de',                               'ingrid.page.measures.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+57 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-environment.psml'), 'meta_keywords', 'de',                            'ingrid.page.environment.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+58 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-maps.psml'), 'meta_keywords', 'de',                                   'ingrid.page.maps.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+59 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-chronicle.psml'), 'meta_keywords', 'de',                              'ingrid.page.chronicle.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+60 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about.psml'), 'meta_keywords', 'de',                                  'ingrid.page.about.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+61 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-about-partner.psml'), 'meta_keywords', 'de',                          'ingrid.page.about.partner.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+62 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-myportal.psml'), 'meta_keywords', 'de',                            'ingrid.page.myportal.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+63 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/privacy.psml'), 'meta_keywords', 'de',                                     'ingrid.page.privacy.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+64 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/rss-news.psml'), 'meta_keywords', 'de',                                    'ingrid.page.rss.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+65 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-sitemap.psml'), 'meta_keywords', 'de',                             'ingrid.page.sitemap.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+66 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-contact.psml'), 'meta_keywords', 'de',                             'ingrid.page.contact.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+67 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/service-contact-newsletter.psml'), 'meta_keywords', 'de',                  'ingrid.page.newsletter.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+68 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/disclaimer.psml'), 'meta_keywords', 'de',                                  'ingrid.page.disclaimer.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+69 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/search-detail.psml'), 'meta_keywords', 'de',                               'ingrid.page.detail.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+70 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/main-features.psml'), 'meta_keywords', 'de',                             'ingrid.page.features.meta.keywords');
INSERT INTO page_metadata (METADATA_ID ,PAGE_ID ,NAME ,LOCALE ,VALUE) VALUES 
    ((SELECT max_key+71 FROM ojb_hl_seq where tablename='SEQ_PAGE_METADATA'),(SELECT PAGE_ID FROM page WHERE PATH='/help.psml'), 'meta_keywords', 'de',                                        'ingrid.page.help.meta.keywords');

-- remember footer-menu ID
INSERT INTO ingrid_temp (temp_key, temp_value) values ('footer_menu_id', (SELECT MENU_ID FROM folder_menu where NAME='footer-menu'));    
    
-- add about submenu and in footer the newsletter link
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuDefinitionImpl', NULL , '1', '0', 'sub-menu-about', NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL, '0', NULL, NULL, NULL, NULL, '/main-features.psml', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+2 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), NULL , '1', NULL , NULL , NULL , NULL , '/main-about.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+3 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuSeparatorDefinitionImpl', (SELECT temp_value FROM ingrid_temp where temp_key='footer_menu_id'), NULL , '5', NULL , NULL , NULL , 'separator1' , NULL, NULL , NULL , NULL , NULL , NULL , NULL , NULL);
INSERT INTO folder_menu (MENU_ID ,CLASS_NAME ,PARENT_ID ,FOLDER_ID ,ELEMENT_ORDER ,NAME ,TITLE ,SHORT_TITLE ,TEXT ,OPTIONS ,DEPTH ,IS_PATHS ,IS_REGEXP ,PROFILE ,OPTIONS_ORDER ,SKIN ,IS_NEST) VALUES 
    ((SELECT max_key+4 FROM ojb_hl_seq where tablename='SEQ_FOLDER_MENU'), 'org.apache.jetspeed.om.folder.impl.FolderMenuOptionsDefinitionImpl', (SELECT temp_value   FROM ingrid_temp where temp_key='footer_menu_id'), NULL , '6', NULL , NULL , NULL , NULL , '/service-contact-newsletter.psml', NULL , NULL , NULL , NULL , NULL , NULL , NULL);

-- add fragments to main-features.psml
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_PAGE'), 'jetspeed-layouts::IngridOneColumn', NULL , NULL , 'layout', NULL , NULL , NULL , NULL , '-1', '-1', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);
INSERT INTO fragment (FRAGMENT_ID ,PARENT_ID ,PAGE_ID ,NAME ,TITLE ,SHORT_TITLE ,TYPE ,SKIN ,DECORATOR ,STATE ,PMODE ,LAYOUT_ROW ,LAYOUT_COLUMN ,LAYOUT_SIZES ,LAYOUT_X ,LAYOUT_Y ,LAYOUT_Z ,LAYOUT_WIDTH ,LAYOUT_HEIGHT ,EXT_PROP_NAME_1 ,EXT_PROP_VALUE_1 ,EXT_PROP_NAME_2 ,EXT_PROP_VALUE_2 ,OWNER_PRINCIPAL) VALUES 
    ((SELECT max_key+1 FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), NULL , 'ingrid-portal-apps::ShowFeaturesPortlet', NULL , NULL , 'portlet', NULL , 'ingrid-teaser', NULL , NULL , '0', '0', NULL , '-1', '-1', '-1', '-1', '-1', NULL , NULL , NULL , NULL , NULL);

-- insert help field on page!?
-- INSERT INTO fragment_pref (PREF_ID ,FRAGMENT_ID ,NAME ,IS_READ_ONLY) VALUES 
--    ((SELECT max_key   FROM ojb_hl_seq where tablename='SEQ_FRAGMENT_PREF'), (SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'), 'helpKey', '0');

-- use new templates
UPDATE fragment_pref_value SET value='/WEB-INF/templates/chronicle_info_new.vm' WHERE value='/WEB-INF/templates/chronicle_info.vm';
UPDATE fragment_pref_value SET value='/WEB-INF/templates/environment_info_new.vm' WHERE value='/WEB-INF/templates/environment_info.vm';
UPDATE fragment_pref_value SET value='/WEB-INF/templates/measures_info_new.vm' WHERE value='/WEB-INF/templates/measures_info.vm';
UPDATE fragment_pref_value SET value='/WEB-INF/templates/service_info_new.vm' WHERE value='/WEB-INF/templates/service_info.vm';
UPDATE fragment_pref_value SET value='/WEB-INF/templates/search_simple_info_new.vm' WHERE value='/WEB-INF/templates/search_simple_info.vm';
       
-- set decorator to ingrid-marginal-header
INSERT INTO ingrid_temp (temp_key, temp_value) values ('fragment_tmp_id', '0');
UPDATE ingrid_temp SET temp_value = (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-chronicle.psml')) WHERE temp_key='fragment_tmp_id';
UPDATE fragment SET decorator='ingrid-marginal-header' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment_tmp_id') AND name = 'ingrid-portal-apps::InfoPortlet';
UPDATE ingrid_temp SET temp_value = (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-measures.psml')) WHERE temp_key='fragment_tmp_id';
UPDATE fragment SET decorator='ingrid-marginal-header' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment_tmp_id') AND name = 'ingrid-portal-apps::InfoPortlet';
UPDATE ingrid_temp SET temp_value = (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-environment.psml')) WHERE temp_key='fragment_tmp_id';
UPDATE fragment SET decorator='ingrid-marginal-header' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment_tmp_id') AND name = 'ingrid-portal-apps::InfoPortlet';
UPDATE ingrid_temp SET temp_value = (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-service.psml')) WHERE temp_key='fragment_tmp_id';
UPDATE fragment SET decorator = 'ingrid-marginal-header' WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment_tmp_id') AND name = 'ingrid-portal-apps::InfoPortlet';
UPDATE ingrid_temp SET temp_value = (SELECT fragment_id FROM fragment WHERE page_id = (SELECT page_id FROM page WHERE path = '/main-search.psml')) WHERE temp_key='fragment_tmp_id';
INSERT INTO ingrid_lookup (ID, item_key, item_value) values (0, 'fragment_tmp_id', '0');
UPDATE ingrid_lookup SET item_value = (SELECT fragment_id FROM fragment WHERE parent_id = (SELECT temp_value FROM ingrid_temp WHERE temp_key = 'fragment_tmp_id') AND name = 'jetspeed-layouts::IngridTwoColumns') WHERE item_key='fragment_tmp_id';
UPDATE fragment SET decorator = 'ingrid-marginal-header' WHERE parent_id = (SELECT item_value FROM ingrid_lookup WHERE item_key = 'fragment_tmp_id') AND name = 'ingrid-portal-apps::InfoPortlet';
DELETE FROM ingrid_lookup WHERE item_key = 'fragment_tmp_id';

-- delete temporary table
-- oracle way of: DROP TABLE IF EXISTS ingrid_temp;
--begin
--  execute immediate 'DROP TABLE ingrid_temp';
--  exception when others then null;
--end;
--/
DROP TABLE ingrid_temp;
  
-- update max keys
UPDATE ojb_hl_seq SET max_key=max_key+(4*grab_size), version=version+1 WHERE tablename='SEQ_PAGE_METADATA';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FOLDER_MENU';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_PAGE';
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT';
-- UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1 WHERE tablename='SEQ_FRAGMENT_PREF';
