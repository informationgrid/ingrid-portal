UPDATE info SET value_name = '4.7.0' WHERE  info.key_name = 'version';

-- Fix user table for standalone IGE
-- since it's only used now in mCLOUD we restart new with the table
DROP TABLE IF EXISTS repo_user;

CREATE TABLE repo_user (
      login      varchar(255) NOT NULL,
      version    int          NOT NULL,
      password   varchar(255) DEFAULT NULL,
      first_name varchar(255) DEFAULT NULL,
      surname    varchar(255) DEFAULT NULL,
      email      varchar(255) DEFAULT NULL,
      PRIMARY KEY (login)
);
