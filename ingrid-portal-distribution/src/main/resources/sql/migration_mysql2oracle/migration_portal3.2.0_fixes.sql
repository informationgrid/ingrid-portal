PROMPT ! CHANGE SCHEMA AFTER MIGRATION VIA SQL DEVELOPER !
PROMPT ---------------------------------------------------

PROMPT ! CHANGE COLUMN DATA TYPES
PROMPT --------------------------

PROMPT ! Change custom_portlet_mode.description from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE custom_portlet_mode ADD description2 VARCHAR2(4000 CHAR);
UPDATE custom_portlet_mode SET description2 = description;
ALTER TABLE custom_portlet_mode DROP COLUMN description;
ALTER TABLE custom_portlet_mode RENAME COLUMN description2 TO description;

PROMPT ! Change custom_window_state.description from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE custom_window_state ADD description2 VARCHAR2(4000 CHAR);
UPDATE custom_window_state SET description2 = description;
ALTER TABLE custom_window_state DROP COLUMN description;
ALTER TABLE custom_window_state RENAME COLUMN description2 TO description;

PROMPT ! Change ingrid_principal_pref.pref_value from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE ingrid_principal_pref ADD pref_value2 VARCHAR2(4000 CHAR);
UPDATE ingrid_principal_pref SET pref_value2 = pref_value;
ALTER TABLE ingrid_principal_pref DROP COLUMN pref_value;
ALTER TABLE ingrid_principal_pref RENAME COLUMN pref_value2 TO pref_value;

-- NOTICE: Keep CLOB if description > 4000 in Feed wanted ! BUT THEN ojdbc6.jar DRIVER HAS TO BE USED FOR Oracle 11g !
PROMPT ! Change ingrid_rss_store.description from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE ingrid_rss_store ADD description2 VARCHAR2(4000 CHAR);
UPDATE ingrid_rss_store SET description2 = description;
ALTER TABLE ingrid_rss_store DROP COLUMN description;
ALTER TABLE ingrid_rss_store RENAME COLUMN description2 TO description;

PROMPT ! Change language.keywords from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE language ADD keywords2 VARCHAR2(4000 CHAR);
UPDATE language SET keywords2 = keywords;
ALTER TABLE language DROP COLUMN keywords;
ALTER TABLE language RENAME COLUMN keywords2 TO keywords;

PROMPT ! Change localized_description.description from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE localized_description ADD description2 VARCHAR2(4000 CHAR);
UPDATE localized_description SET description2 = description;
ALTER TABLE localized_description DROP COLUMN description;
ALTER TABLE localized_description RENAME COLUMN description2 TO description;

PROMPT ! Change localized_display_name.display_name from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE localized_display_name ADD display_name2 VARCHAR2(4000 CHAR);
UPDATE localized_display_name SET display_name2 = display_name;
ALTER TABLE localized_display_name DROP COLUMN display_name;
ALTER TABLE localized_display_name RENAME COLUMN display_name2 TO display_name;

PROMPT ! Change media_type.description from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE media_type ADD description2 VARCHAR2(4000 CHAR);
UPDATE media_type SET description2 = description;
ALTER TABLE media_type DROP COLUMN description;
ALTER TABLE media_type RENAME COLUMN description2 TO description;

PROMPT ! Change pa_metadata_fields.column_value from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE pa_metadata_fields ADD column_value2 VARCHAR2(4000 CHAR);
UPDATE pa_metadata_fields SET column_value2 = column_value;
ALTER TABLE pa_metadata_fields DROP COLUMN column_value;
ALTER TABLE pa_metadata_fields RENAME COLUMN column_value2 TO column_value;

PROMPT ! Change parameter.parameter_value from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE parameter ADD parameter_value2 VARCHAR2(4000 CHAR);
UPDATE parameter SET parameter_value2 = parameter_value;
ALTER TABLE parameter DROP COLUMN parameter_value;
ALTER TABLE parameter RENAME COLUMN parameter_value2 TO parameter_value;

PROMPT ! Change pd_metadata_fields.column_value from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE pd_metadata_fields ADD column_value2 VARCHAR2(4000 CHAR);
UPDATE pd_metadata_fields SET column_value2 = column_value;
ALTER TABLE pd_metadata_fields DROP COLUMN column_value;
ALTER TABLE pd_metadata_fields RENAME COLUMN column_value2 TO column_value;

PROMPT ! Change portlet_content_type.modes from CLOB to VARCHAR2(4000 CHAR) ...
ALTER TABLE portlet_content_type ADD modes2 VARCHAR2(4000 CHAR);
UPDATE portlet_content_type SET modes2 = modes;
ALTER TABLE portlet_content_type DROP COLUMN modes;
ALTER TABLE portlet_content_type RENAME COLUMN modes2 TO modes;

PROMPT ! Change ingrid_rss_store.link from VARCHAR(255) to VARCHAR2(4000 CHAR) ...
ALTER TABLE INGRID_RSS_STORE ADD link2 VARCHAR2(4000 CHAR);
UPDATE INGRID_RSS_STORE SET link2 = link;
ALTER TABLE INGRID_RSS_STORE DROP COLUMN link;
ALTER TABLE INGRID_RSS_STORE RENAME COLUMN link2 TO link;

PROMPT ! CHANGE COLUMN CONSTRAINTS (columns nullable, ...)
PROMPT ---------------------------------------------------

-- for avoiding Error when creating NEW User
ALTER TABLE SECURITY_CREDENTIAL MODIFY (PREV_AUTH_DATE null);
ALTER TABLE SECURITY_CREDENTIAL MODIFY PREV_AUTH_DATE DEFAULT NULL;
ALTER TABLE SECURITY_CREDENTIAL MODIFY (LAST_AUTH_DATE null);
ALTER TABLE SECURITY_CREDENTIAL MODIFY LAST_AUTH_DATE DEFAULT NULL;

-- for avoiding Error when adding NEW Feed
ALTER TABLE INGRID_RSS_SOURCE MODIFY (LASTUPDATE null);

commit;

PROMPT ! ARBITRARY FIXES !
PROMPT -------------------

PROMPT ! Create Missing HIBERNATE_SEQUENCE used for native ID generation (used in Hibernate model in portal)
CREATE SEQUENCE HIBERNATE_SEQUENCE;

PROMPT ! Substitute ' ' in language table to avoid treatment as null with Oracle
PROMPT ! (leads to error at startup when registering PA "ingrid-portal-apps" -> field with null value for lucene)
UPDATE language SET keywords = '""' where keywords=' ';

commit;

PROMPT ! DELETE AND RECREATE QUARTZ TABLES, NEW DEFAULT JOBS WILL BE CREATED
PROMPT ---------------------------------------------------------------------

-- copied from quartz-1.7.3\docs\dbTables\tables_oracle.sql

--
-- A hint submitted by a user: Oracle DB MUST be created as "shared" and the 
-- job_queue_processes parameter  must be greater than 2, otherwise a DB lock 
-- will happen.   However, these settings are pretty much standard after any
-- Oracle install, so most users need not worry about this.
--
-- Many other users (including the primary author of Quartz) have had success
-- runing in dedicated mode, so only consider the above as a hint ;-)
--

delete from qrtz_job_listeners;
delete from qrtz_trigger_listeners;
delete from qrtz_fired_triggers;
delete from qrtz_simple_triggers;
delete from qrtz_cron_triggers;
delete from qrtz_blob_triggers;
delete from qrtz_triggers;
delete from qrtz_job_details;
delete from qrtz_calendars;
delete from qrtz_paused_trigger_grps;
delete from qrtz_locks;
delete from qrtz_scheduler_state;

drop table qrtz_calendars;
drop table qrtz_fired_triggers;
drop table qrtz_trigger_listeners;
drop table qrtz_blob_triggers;
drop table qrtz_cron_triggers;
drop table qrtz_simple_triggers;
drop table qrtz_triggers;
drop table qrtz_job_listeners;
drop table qrtz_job_details;
drop table qrtz_paused_trigger_grps;
drop table qrtz_locks;
drop table qrtz_scheduler_state;


CREATE TABLE qrtz_job_details
  (
    JOB_NAME  VARCHAR2(200) NOT NULL,
    JOB_GROUP VARCHAR2(200) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    JOB_CLASS_NAME   VARCHAR2(250) NOT NULL, 
    IS_DURABLE VARCHAR2(1) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    IS_STATEFUL VARCHAR2(1) NOT NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NOT NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP)
);
CREATE TABLE qrtz_job_listeners
  (
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    JOB_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP)
);
CREATE TABLE qrtz_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    JOB_NAME  VARCHAR2(200) NOT NULL, 
    JOB_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    DESCRIPTION VARCHAR2(250) NULL,
    NEXT_FIRE_TIME NUMBER(13) NULL,
    PREV_FIRE_TIME NUMBER(13) NULL,
    PRIORITY NUMBER(13) NULL,
    TRIGGER_STATE VARCHAR2(16) NOT NULL,
    TRIGGER_TYPE VARCHAR2(8) NOT NULL,
    START_TIME NUMBER(13) NOT NULL,
    END_TIME NUMBER(13) NULL,
    CALENDAR_NAME VARCHAR2(200) NULL,
    MISFIRE_INSTR NUMBER(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (JOB_NAME,JOB_GROUP) 
	REFERENCES QRTZ_JOB_DETAILS(JOB_NAME,JOB_GROUP) 
);
CREATE TABLE qrtz_simple_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    REPEAT_COUNT NUMBER(7) NOT NULL,
    REPEAT_INTERVAL NUMBER(12) NOT NULL,
    TIMES_TRIGGERED NUMBER(10) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_cron_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    CRON_EXPRESSION VARCHAR2(120) NOT NULL,
    TIME_ZONE_ID VARCHAR2(80),
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_blob_triggers
  (
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    BLOB_DATA BLOB NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
        REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_trigger_listeners
  (
    TRIGGER_NAME  VARCHAR2(200) NOT NULL, 
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    TRIGGER_LISTENER VARCHAR2(200) NOT NULL,
    PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER),
    FOREIGN KEY (TRIGGER_NAME,TRIGGER_GROUP) 
	REFERENCES QRTZ_TRIGGERS(TRIGGER_NAME,TRIGGER_GROUP)
);
CREATE TABLE qrtz_calendars
  (
    CALENDAR_NAME  VARCHAR2(200) NOT NULL, 
    CALENDAR BLOB NOT NULL,
    PRIMARY KEY (CALENDAR_NAME)
);
CREATE TABLE qrtz_paused_trigger_grps
  (
    TRIGGER_GROUP  VARCHAR2(200) NOT NULL, 
    PRIMARY KEY (TRIGGER_GROUP)
);
CREATE TABLE qrtz_fired_triggers 
  (
    ENTRY_ID VARCHAR2(95) NOT NULL,
    TRIGGER_NAME VARCHAR2(200) NOT NULL,
    TRIGGER_GROUP VARCHAR2(200) NOT NULL,
    IS_VOLATILE VARCHAR2(1) NOT NULL,
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    FIRED_TIME NUMBER(13) NOT NULL,
    PRIORITY NUMBER(13) NOT NULL,
    STATE VARCHAR2(16) NOT NULL,
    JOB_NAME VARCHAR2(200) NULL,
    JOB_GROUP VARCHAR2(200) NULL,
    IS_STATEFUL VARCHAR2(1) NULL,
    REQUESTS_RECOVERY VARCHAR2(1) NULL,
    PRIMARY KEY (ENTRY_ID)
);
CREATE TABLE qrtz_scheduler_state 
  (
    INSTANCE_NAME VARCHAR2(200) NOT NULL,
    LAST_CHECKIN_TIME NUMBER(13) NOT NULL,
    CHECKIN_INTERVAL NUMBER(13) NOT NULL,
    PRIMARY KEY (INSTANCE_NAME)
);
CREATE TABLE qrtz_locks
  (
    LOCK_NAME  VARCHAR2(40) NOT NULL, 
    PRIMARY KEY (LOCK_NAME)
);
INSERT INTO qrtz_locks values('TRIGGER_ACCESS');
INSERT INTO qrtz_locks values('JOB_ACCESS');
INSERT INTO qrtz_locks values('CALENDAR_ACCESS');
INSERT INTO qrtz_locks values('STATE_ACCESS');
INSERT INTO qrtz_locks values('MISFIRE_ACCESS');
create index idx_qrtz_j_req_recovery on qrtz_job_details(REQUESTS_RECOVERY);
create index idx_qrtz_t_next_fire_time on qrtz_triggers(NEXT_FIRE_TIME);
create index idx_qrtz_t_state on qrtz_triggers(TRIGGER_STATE);
create index idx_qrtz_t_nft_st on qrtz_triggers(NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_volatile on qrtz_triggers(IS_VOLATILE);
create index idx_qrtz_ft_trig_name on qrtz_fired_triggers(TRIGGER_NAME);
create index idx_qrtz_ft_trig_group on qrtz_fired_triggers(TRIGGER_GROUP);
create index idx_qrtz_ft_trig_nm_gp on qrtz_fired_triggers(TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_trig_volatile on qrtz_fired_triggers(IS_VOLATILE);
create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(INSTANCE_NAME);
create index idx_qrtz_ft_job_name on qrtz_fired_triggers(JOB_NAME);
create index idx_qrtz_ft_job_group on qrtz_fired_triggers(JOB_GROUP);
create index idx_qrtz_ft_job_stateful on qrtz_fired_triggers(IS_STATEFUL);
create index idx_qrtz_ft_job_req_recovery on qrtz_fired_triggers(REQUESTS_RECOVERY);

commit;

PROMPT ! DONE !
