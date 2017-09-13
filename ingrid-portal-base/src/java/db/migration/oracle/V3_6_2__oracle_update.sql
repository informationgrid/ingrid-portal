-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.6.2', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Clean up of ingrid tables, remains from the past
-- !!!!!!!! -------------------------------------------------
-- !!! DIFFERENT SYNTAX FOR JDBC <-> Scripting !  Choose your syntax, default is JDBC version

-- !!! JDBC VERSION (installer):
-- !!! All in one line and DOUBLE SEMICOLON at end !!! Or causes problems when executing via JDBC in installer (ORA-06550) !

--BEGIN execute immediate 'DROP TABLE ingrid_codelist'; exception when others then null; END;;
--BEGIN execute immediate 'DROP TABLE ingrid_codelist_domain'; exception when others then null; END;;
--BEGIN execute immediate 'DROP TABLE ingrid_sys_codelist'; exception when others then null; END;;
--BEGIN execute immediate 'DROP TABLE ingrid_sys_codelist_domain'; exception when others then null; END;;

-- !!! SCRIPT VERSION (SQL Developer, SQL Plus, Flyway !):
-- !!! SINGLE SEMICOLON AND "/" in separate line !

BEGIN execute immediate 'DROP TABLE ingrid_codelist'; exception when others then null; END;
/
BEGIN execute immediate 'DROP TABLE ingrid_codelist_domain'; exception when others then null; END;
/
BEGIN execute immediate 'DROP TABLE ingrid_sys_codelist'; exception when others then null; END;
/
BEGIN execute immediate 'DROP TABLE ingrid_sys_codelist_domain'; exception when others then null; END;
/

-- !!!!!!!! -------------------------------------------------
