-- Trigger erstellen um die ID bei jedem Insert hochzuzählen
CREATE OR REPLACE TRIGGER  INGRID_TINY_URL_T1
	BEFORE INSERT ON INGRID_TINY_URL
	FOR EACH ROW
    WHEN (new.id IS NULL)
	BEGIN
        SELECT INGRID_TINY_URL_SEQ.NEXTVAL INTO :new.id FROM dual;
    END;
 /