-- DB Version
UPDATE ingrid_lookup SET item_value = '6.0.1', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value int
);

-- Set folder "/application" to display
UPDATE folder SET is_hidden = 0 WHERE path = '/application';

-- Show '/application/main-application.psml'
UPDATE page SET is_hidden = 0 WHERE path = '/application/main-application.psml';

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;