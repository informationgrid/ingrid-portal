
-- Update 2.1 to 2.1.2
-- =========================

-----------------------------------------------------------------------------
-- ADMIN_ACTIVITY
-----------------------------------------------------------------------------
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

-----------------------------------------------------------------------------
-- USER_ACTIVITY
-----------------------------------------------------------------------------
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
