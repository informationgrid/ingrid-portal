UPDATE info SET value_name = '4.7.0' WHERE  info.key_name = 'version';

-- Fix user table for standalone IGE
BEGIN execute immediate 'DROP TABLE repo_user'; exception when others then null; END;
/

CREATE TABLE repo_user (
   login VARCHAR2(255 CHAR) NOT NULL,
   version NUMBER(10,0) NOT NULL,
   password VARCHAR2(255 CHAR) DEFAULT NULL,
   first_name VARCHAR2(255 CHAR) DEFAULT NULL,
   surname VARCHAR2(255 CHAR) DEFAULT NULL,
   email VARCHAR2(255 CHAR) DEFAULT NULL);
