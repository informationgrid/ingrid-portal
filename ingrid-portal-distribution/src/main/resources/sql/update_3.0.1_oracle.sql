-- DB Version erh�hen
UPDATE ingrid_lookup SET item_value = '3.0.1' WHERE item_key ='ingrid_db_version';

-- Datum in der Lookup Tabelle aktualisieren
UPDATE ingrid_lookup SET item_date=SYSDATE WHERE item_key ='ingrid_db_version';

-- 22.06.2011
-- Neues Wetter Portlet hinzuf�gen
INSERT INTO fragment (FRAGMENT_ID, PARENT_ID, PAGE_ID, NAME, TITLE, SHORT_TITLE, TYPE, SKIN, DECORATOR, STATE, PMODE, LAYOUT_ROW, LAYOUT_COLUMN, LAYOUT_SIZES, LAYOUT_X, LAYOUT_Y, LAYOUT_Z, LAYOUT_WIDTH, LAYOUT_HEIGHT, EXT_PROP_NAME_1, EXT_PROP_VALUE_1, EXT_PROP_NAME_2, EXT_PROP_VALUE_2, OWNER_PRINCIPAL) 
VALUES (
(SELECT max_key FROM ojb_hl_seq where tablename='SEQ_FRAGMENT'),
(SELECT FRAGMENT_ID FROM fragment WHERE PAGE_ID=(SELECT PAGE_ID FROM page WHERE PATH='/default-page.psml')),
NULL, 'ingrid-portal-apps::WeatherTeaser', NULL, NULL, 'portlet', NULL, 'ingrid-marginal-teaser', NULL, NULL, 4, 1, NULL, -1, -1, -1, -1, -1, NULL, NULL, NULL, NULL, NULL);

-- am Ende ALLE BENUTZTEN SEQUENZEN hoch setzen
-- =====================

 -- fragment
UPDATE ojb_hl_seq SET max_key=max_key+grab_size, version=version+1
WHERE tablename='SEQ_FRAGMENT';
