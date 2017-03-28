-- DB Version
UPDATE ingrid_lookup SET item_value = '4.0.2', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
-- oracle way of: DROP TABLE IF EXISTS

-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

-- BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;
-- /

-- !!!!!!!! -------------------------------------------------

CREATE TABLE  ingrid_temp (
	temp_key VARCHAR2(255),
	temp_value NUMBER(10,0)
);

UPDATE page SET default_layout_decorator = 'ingrid-untitled', default_portlet_decorator = 'clear' WHERE path = '/search-detail.psml';

-- delete temporary table
DROP TABLE ingrid_temp;