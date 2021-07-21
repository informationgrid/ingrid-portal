-- DB Version
UPDATE ingrid_lookup SET item_value = '5.9.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value int
);

/*
// No primary key on 'V4_0_1__initial.sql'
ALTER TABLE ingrid_rss_store DROP PRIMARY KEY;
// link column is 'character varying(4000)'
ALTER TABLE ingrid_rss_store MODIFY COLUMN link varchar(2084) NOT NULL;
*/
ALTER TABLE ingrid_rss_source ALTER COLUMN url TYPE varchar(2084) NOT NULL;

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;