UPDATE info SET value_name = '5.1.0' WHERE  info.key_name = 'version';

ALTER TABLE repo_user ADD password_change_id VARCHAR2(36 CHAR);
