-- DB Version
UPDATE ingrid_lookup SET item_value = '4.1.0', item_date = NOW() WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
DROP TABLE IF EXISTS ingrid_temp;
CREATE TABLE ingrid_temp (
  temp_key varchar(255) NOT NULL,
  temp_value mediumint
);

UPDATE page SET default_layout_decorator = 'ingrid-untitled', default_portlet_decorator = 'clear' WHERE path = '/search-detail.psml';

-- delete temporary table
DROP TABLE IF EXISTS ingrid_temp;
