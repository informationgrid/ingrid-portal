-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.6.2', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Clean up of ingrid tables, remains from the past
DROP TABLE IF EXISTS ingrid_codelist;
DROP TABLE IF EXISTS ingrid_codelist_domain;
DROP TABLE IF EXISTS ingrid_sys_codelist;
DROP TABLE IF EXISTS ingrid_sys_codelist_domain;
