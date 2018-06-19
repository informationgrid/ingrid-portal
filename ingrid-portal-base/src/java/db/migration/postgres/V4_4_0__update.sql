-- DB Version
UPDATE ingrid_lookup SET item_value = '4.3.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';


UPDATE page SET IS_HIDDEN = '1' WHERE page.NAME = 'admin-content-partner.psml' OR page.NAME = 'admin-content-provider.psml';