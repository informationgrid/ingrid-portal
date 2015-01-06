
-- Update 2.1 to 2.1.2
-- =========================

-- ---------------------------------------------------------------------------
-- ADMIN_ACTIVITY
-- ---------------------------------------------------------------------------
CREATE TABLE ADMIN_ACTIVITY
(
  ACTIVITY VARCHAR2(40),
  CATEGORY VARCHAR2(40),
  ADMIN VARCHAR2(80),
  USER_NAME VARCHAR2(80),
  TIME_STAMP TIMESTAMP,
  IPADDRESS VARCHAR2(80),
  ATTR_NAME VARCHAR2(40),
  ATTR_VALUE_BEFORE VARCHAR2(80),
  ATTR_VALUE_AFTER VARCHAR2(80),
  DESCRIPTION VARCHAR2(128));

-- ---------------------------------------------------------------------------
-- USER_ACTIVITY
-- ---------------------------------------------------------------------------
CREATE TABLE USER_ACTIVITY
(
  ACTIVITY VARCHAR2(40),
  CATEGORY VARCHAR2(40),
  USER_NAME VARCHAR2(80),
  TIME_STAMP TIMESTAMP,
  IPADDRESS VARCHAR2(80),
  ATTR_NAME VARCHAR2(40),
  ATTR_VALUE_BEFORE VARCHAR2(80),
  ATTR_VALUE_AFTER VARCHAR2(80),
  DESCRIPTION VARCHAR2(128));


-- Update 2.1.2 to 2.1.3
-- =========================

CREATE INDEX IX_PREFS_NODE_1 ON PREFS_NODE (PARENT_NODE_ID); 
-- Schon vorhanden !?
PROMPT ! Create INDEX, may cause ERROR if already there ...
CREATE INDEX IX_PREFS_NODE_2 ON PREFS_NODE (FULL_PATH); 
PROMPT ! Create INDEX, may cause ERROR if already there ...
CREATE INDEX IX_FKPPV_1 ON PREFS_PROPERTY_VALUE (NODE_ID); 

PROMPT ! Delete old CONSTRAINT FK_PREFS_NODE_1, may cause ERROR if not exists ...
ALTER TABLE PREFS_NODE DROP CONSTRAINT FK_PREFS_NODE_1;
PROMPT ! Create new CONSTRAINT FK_PREFS_NODE_1 with cascade delete ...
ALTER TABLE PREFS_NODE ADD CONSTRAINT FK_PREFS_NODE_1 FOREIGN KEY (PARENT_NODE_ID) REFERENCES PREFS_NODE (NODE_ID) ON DELETE CASCADE; 

ALTER TABLE PREFS_PROPERTY_VALUE ADD CONSTRAINT FK_PREFS_PROPERTY_VALUE_1 FOREIGN KEY (NODE_ID) REFERENCES PREFS_NODE (NODE_ID) ON DELETE CASCADE;


-- Update 2.1.3 to 2.1.4
-- =========================

PROMPT ! Allow null in PARAMETER_VALUE, may cause ERROR if already nullable ...
ALTER TABLE PARAMETER MODIFY (PARAMETER_VALUE null);

ALTER TABLE FRAGMENT ADD FRAGMENT_STRING_ID VARCHAR2(80);
ALTER TABLE FRAGMENT ADD CONSTRAINT FRAGMENT_STRING_ID_UNIQUE UNIQUE (FRAGMENT_STRING_ID);


-- Changes needed for export/import via jetspeed installer to avoid exception
-- ==========================================================================

-- Substitute empty string to avoid treatment as null with Oracle
-- (leads to error: Einfügen von NULL in (."FRAGMENT_PREF_VALUE"."VALUE") nicht möglich
UPDATE FRAGMENT_PREF_VALUE SET VALUE = '""' where VALUE=' ';
