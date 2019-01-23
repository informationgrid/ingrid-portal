-- DB Version
UPDATE ingrid_lookup SET item_value = '4.6.0', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- remove NOT NULL constraint to also add provider not having a url
-- in MySQL and Postgres '' is inserted in Oracle NULL causing an exception
-- catch exception if already null
BEGIN execute immediate 'ALTER TABLE ingrid_provider MODIFY url NULL'; exception when others then null; END;
/
