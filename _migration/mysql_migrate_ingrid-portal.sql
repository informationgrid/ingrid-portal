
-- Update 2.1 to 2.1.2
-- =========================

-----------------------------------------------------------------------------
-- ADMIN_ACTIVITY
-----------------------------------------------------------------------------
drop table if exists ADMIN_ACTIVITY;

CREATE TABLE ADMIN_ACTIVITY
(
  ACTIVITY VARCHAR(40),
  CATEGORY VARCHAR(40),
  ADMIN VARCHAR(80),
  USER_NAME VARCHAR(80),
  TIME_STAMP TIMESTAMP,
  IPADDRESS VARCHAR(80),
  ATTR_NAME VARCHAR(40),
  ATTR_VALUE_BEFORE VARCHAR(80),
  ATTR_VALUE_AFTER VARCHAR(80),
  DESCRIPTION VARCHAR(128));

-----------------------------------------------------------------------------
-- USER_ACTIVITY
-----------------------------------------------------------------------------
drop table if exists USER_ACTIVITY;

CREATE TABLE USER_ACTIVITY
(
  ACTIVITY VARCHAR(40),
  CATEGORY VARCHAR(40),
  USER_NAME VARCHAR(80),
  TIME_STAMP TIMESTAMP,
  IPADDRESS VARCHAR(80),
  ATTR_NAME VARCHAR(40),
  ATTR_VALUE_BEFORE VARCHAR(80),
  ATTR_VALUE_AFTER VARCHAR(80),
  DESCRIPTION VARCHAR(128));


-- Update 2.1.2 to 2.1.3
-- =========================

