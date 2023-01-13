-- DB Version
UPDATE ingrid_lookup SET item_value = '5.14.1', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);

UPDATE fragment SET decorator = 'ingrid-teaser' WHERE decorator = 'clear' AND type = 'portlet';

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;