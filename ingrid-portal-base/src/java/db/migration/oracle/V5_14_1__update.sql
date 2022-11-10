-- DB Version
UPDATE ingrid_lookup SET item_value = '5.14.1', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
-- oracle way of: DROP TABLE IF EXISTS

-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

-- BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus, Flyway !):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

BEGIN execute immediate 'DROP TABLE ingrid_temp'; exception when others then null; END;
/

-- Temp Table um values aus folder_menu zwischen zu speichern (subselect in insert auf gleiche Tabelle nicht moeglich, s.u.)
CREATE TABLE ingrid_temp (
  temp_key VARCHAR2(255),
  temp_value NUMBER(10,0)
);

UPDATE fragment SET decorator = 'ingrid-teaser' WHERE decorator = 'clear' AND type = 'portlet';

-- delete temporary table
DROP TABLE ingrid_temp;