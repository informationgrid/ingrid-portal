CREATE TABLE  ingrid_temp (
value NUMBER(10,0)
);

INSERT INTO ingrid_temp (VALUE) 
VALUES (
(SELECT parent_id FROM fragment WHERE name = 'ingrid-portal-apps::SearchCatalogThesaurus'));

INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT value FROM ingrid_temp),
NULL, 'ingrid-portal-apps::SearchCatalogThesaurusResult', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';

DROP TABLE ingrid_temp PURGE; 
