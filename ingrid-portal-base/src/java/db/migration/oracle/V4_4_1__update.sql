-- DB Version
UPDATE ingrid_lookup SET item_value = '4.4.1', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';
