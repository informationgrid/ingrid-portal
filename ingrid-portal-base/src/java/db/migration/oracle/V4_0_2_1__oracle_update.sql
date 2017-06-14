-- DB Version
UPDATE ingrid_lookup SET item_value = '4.0.2.1', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- no database changes, but we increase version to keep track with postgres where patches have to be applied !
