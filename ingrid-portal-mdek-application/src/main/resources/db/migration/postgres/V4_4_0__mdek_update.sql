-- Fix user table for standalone IGE

ALTER TABLE repo_user DROP COLUMN login;
ALTER TABLE repo_user RENAME COLUMN username TO login;