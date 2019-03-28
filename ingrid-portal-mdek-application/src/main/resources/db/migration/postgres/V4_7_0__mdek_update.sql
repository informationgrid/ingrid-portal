UPDATE info
SET value_name = '4.7.0'
WHERE info.key_name = 'version';

-- Fix user table for standalone IGE
DROP TABLE IF EXISTS repo_user;

CREATE TABLE repo_user
(
    login      character varying(255) NOT NULL,
    version    integer                NOT NULL,
    password   character varying(255) DEFAULT NULL,
    first_name character varying(255) DEFAULT NULL,
    surname    character varying(255) DEFAULT NULL,
    email      character varying(255) DEFAULT NULL,
    PRIMARY KEY (login)
);
