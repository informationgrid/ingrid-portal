-- ===================================================================================================================================================================
--  Suche/Datenkataloge/Thesaurus-Navigator: Result Portlet in PSML integriert
--  AM ??.??.20?? IN KST LIVE DB EINGESPIELT (Portal version 1.1.6)
-- ===================================================================================================================================================================

-- Temp Table um fragment id von Layout zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  value mediumint
);
INSERT INTO ingrid_temp (VALUE) 
VALUES (
(SELECT parent_id FROM fragment WHERE name = 'ingrid-portal-apps::SearchCatalogThesaurus'));

-- Result Portlet fragment (parent id aus temporaerer Tabelle)
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='seq_fragment'),
(SELECT value FROM ingrid_temp), -- layout fragment (parent)
NULL, 'ingrid-portal-apps::SearchCatalogThesaurusResult', NULL, NULL, 'portlet', NULL, NULL, NULL, NULL, 0, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='seq_fragment';

-- und temporaere Tabelle löschen
DROP TABLE IF EXISTS ingrid_temp;
