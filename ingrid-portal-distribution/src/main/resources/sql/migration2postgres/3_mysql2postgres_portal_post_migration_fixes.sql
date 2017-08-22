
-- Execute on migrated POSTGRES portal database (from MYSQL) to add stuff removed from migration or not migrated


-- ADD CURRENT_TIMESTAMP as default

ALTER TABLE ingrid_anniversary ALTER COLUMN fetched SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_cms ALTER COLUMN item_changed SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_cms_item ALTER COLUMN item_changed SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_lookup ALTER COLUMN item_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_newsletter_data ALTER COLUMN created SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_principal_pref ALTER COLUMN modified_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_rss_source ALTER COLUMN lastupdate SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_rss_store ALTER COLUMN published_date SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE ingrid_tiny_url ALTER COLUMN tiny_date SET DEFAULT CURRENT_TIMESTAMP;


-- ADD MISSING CONSTRAINTS / INDEXES

CREATE INDEX ix_folder_1 ON folder (parent_id);

ALTER TABLE folder_constraint ADD CONSTRAINT fk_folder_constraint_1 FOREIGN KEY (folder_id) REFERENCES folder (FOLDER_ID) ON DELETE CASCADE;
CREATE INDEX ix_folder_constraint_1 ON folder_constraint (folder_id);

ALTER TABLE folder_constraints_ref ADD CONSTRAINT fk_folder_constraints_ref_1 FOREIGN KEY (folder_id) REFERENCES folder (FOLDER_ID) ON DELETE CASCADE;
CREATE INDEX ix_folder_constraints_ref_1 ON folder_constraints_ref (folder_id);

ALTER TABLE folder_menu ADD CONSTRAINT fk_folder_menu_1 FOREIGN KEY (parent_id) REFERENCES folder_menu (menu_id) ON DELETE CASCADE;
ALTER TABLE folder_menu ADD CONSTRAINT fk_folder_menu_2 FOREIGN KEY (folder_id) REFERENCES folder (folder_id) ON DELETE CASCADE;
CREATE INDEX ix_folder_menu_1 ON folder_menu (parent_id);
CREATE INDEX ix_folder_menu_2 ON folder_menu (folder_id);
CREATE INDEX un_folder_menu_1 ON folder_menu (folder_id, name);

ALTER TABLE folder_menu_metadata ADD CONSTRAINT fk_folder_menu_metadata_1 FOREIGN KEY (menu_id) REFERENCES folder_menu (menu_id) ON DELETE CASCADE;
CREATE INDEX ix_folder_menu_metadata_1 ON folder_menu_metadata (menu_id);

ALTER TABLE folder_metadata ADD CONSTRAINT fk_folder_metadata_1 FOREIGN KEY (folder_id) REFERENCES folder (folder_id) ON DELETE CASCADE;
CREATE INDEX ix_folder_metadata_1 ON folder_metadata (folder_id);

ALTER TABLE folder_order ADD CONSTRAINT fk_folder_order_1 FOREIGN KEY (folder_id) REFERENCES folder (folder_id) ON DELETE CASCADE;
CREATE INDEX ix_folder_order_1 ON folder_order (folder_id);

ALTER TABLE fragment ADD CONSTRAINT fk_fragment_1 FOREIGN KEY (parent_id) REFERENCES fragment (fragment_id) ON DELETE CASCADE;
ALTER TABLE fragment ADD CONSTRAINT fk_fragment_2 FOREIGN KEY (page_id) REFERENCES page (page_id) ON DELETE CASCADE;
CREATE INDEX ix_fragment_1 ON fragment (parent_id);
CREATE INDEX ix_fragment_2 ON fragment (fragment_string_refid);
CREATE INDEX ix_fragment_3 ON fragment (fragment_string_id);
CREATE INDEX un_fragment_1 ON fragment (page_id);

ALTER TABLE fragment_constraint ADD CONSTRAINT fk_fragment_constraint_1 FOREIGN KEY (fragment_id) REFERENCES fragment (fragment_id) ON DELETE CASCADE;
CREATE INDEX ix_fragment_constraint_1 ON fragment_constraint (fragment_id);

ALTER TABLE fragment_constraints_ref ADD CONSTRAINT fk_fragment_constraints_ref_1 FOREIGN KEY (fragment_id) REFERENCES fragment (fragment_id) ON DELETE CASCADE;
CREATE INDEX ix_fragment_constraints_ref_1 ON fragment_constraints_ref (fragment_id);

ALTER TABLE fragment_pref ADD CONSTRAINT fk_fragment_pref_1 FOREIGN KEY (fragment_id) REFERENCES fragment (fragment_id) ON DELETE CASCADE;
CREATE INDEX ix_fragment_pref_1 ON fragment_pref (fragment_id);

ALTER TABLE fragment_pref_value ADD CONSTRAINT fk_fragment_pref_value_1 FOREIGN KEY (pref_id) REFERENCES fragment_pref (pref_id) ON DELETE CASCADE;
CREATE INDEX ix_fragment_pref_value_1 ON fragment_pref_value (pref_id);

ALTER TABLE fragment_prop ADD CONSTRAINT fk_fragment_prop_1 FOREIGN KEY (fragment_id) REFERENCES fragment (fragment_id) ON DELETE CASCADE;
CREATE INDEX ix_fragment_prop_1 ON fragment_prop (fragment_id);

CREATE INDEX ix_ingrid_anniversary_1 ON ingrid_anniversary (fetched_for);
CREATE INDEX ix_ingrid_anniversary_2 ON ingrid_anniversary (language);

CREATE INDEX ix_email_1 ON ingrid_newsletter_data (email);

CREATE INDEX ix_ingrid_partner_1 ON ingrid_partner (sortkey);

CREATE INDEX ix_ingrid_provider_1 ON ingrid_provider (sortkey_partner);
CREATE INDEX ix_ingrid_provider_2 ON ingrid_provider (sortkey);
CREATE INDEX ix_ingrid_provider_3 ON ingrid_provider (ident);

CREATE INDEX ix_ingrid_rss_store_1 ON ingrid_rss_store (published_date);
CREATE INDEX ix_ingrid_rss_store_2 ON ingrid_rss_store (language);

ALTER TABLE link ADD CONSTRAINT fk_link_1 FOREIGN KEY (parent_id) REFERENCES folder (folder_id) ON DELETE CASCADE;
CREATE INDEX ix_link_1 ON link (parent_id);

ALTER TABLE link_constraint ADD CONSTRAINT fk_link_constraint_1 FOREIGN KEY (link_id) REFERENCES link (link_id) ON DELETE CASCADE;
CREATE INDEX ix_link_constraint_1 ON link_constraint (link_id);

ALTER TABLE link_constraints_ref ADD CONSTRAINT fk_link_constraints_ref_1 FOREIGN KEY (link_id) REFERENCES link (link_id) ON DELETE CASCADE;
CREATE INDEX ix_link_constraints_ref_1 ON link_constraints_ref (link_id);

ALTER TABLE link_metadata ADD CONSTRAINT fk_link_metadata_1 FOREIGN KEY (link_id) REFERENCES link (link_id) ON DELETE CASCADE;
CREATE INDEX ix_link_metadata_1 ON link_metadata (link_id);

ALTER TABLE page ADD CONSTRAINT fk_page_1 FOREIGN KEY (parent_id) REFERENCES folder (folder_id) ON DELETE CASCADE;
CREATE INDEX ix_page_1 ON page (parent_id);

ALTER TABLE page_constraint ADD CONSTRAINT fk_page_constraint_1 FOREIGN KEY (page_id) REFERENCES page (page_id) ON DELETE CASCADE;
CREATE INDEX ix_page_constraint_1 ON page_constraint (page_id);

ALTER TABLE page_constraints_ref ADD CONSTRAINT fk_page_constraints_ref_1 FOREIGN KEY (page_id) REFERENCES page (page_id) ON DELETE CASCADE;
CREATE INDEX ix_page_constraints_ref_1 ON page_constraints_ref (page_id);

ALTER TABLE page_menu ADD CONSTRAINT fk_page_menu_1 FOREIGN KEY (parent_id) REFERENCES page_menu (menu_id) ON DELETE CASCADE;
ALTER TABLE page_menu ADD CONSTRAINT fk_page_menu_2 FOREIGN KEY (page_id) REFERENCES page (page_id) ON DELETE CASCADE;
CREATE INDEX ix_page_menu_1 ON page_menu (parent_id);
CREATE INDEX ix_page_menu_2 ON page_menu (page_id);
CREATE INDEX un_page_menu_1 ON page_menu (page_id, name);

ALTER TABLE page_menu_metadata ADD CONSTRAINT fk_page_menu_metadata_1 FOREIGN KEY (menu_id) REFERENCES page_menu (menu_id) ON DELETE CASCADE;
CREATE INDEX ix_page_menu_metadata_1 ON page_menu_metadata (menu_id);

ALTER TABLE page_metadata ADD CONSTRAINT fk_page_metadata_1 FOREIGN KEY (page_id) REFERENCES page (page_id) ON DELETE CASCADE;
CREATE INDEX ix_page_metadata_1 ON page_metadata (page_id);

ALTER TABLE page_sec_constraint_def ADD CONSTRAINT fk_page_sec_constraint_def_1 FOREIGN KEY (constraints_def_id) REFERENCES page_sec_constraints_def (constraints_def_id) ON DELETE CASCADE;
CREATE INDEX ix_page_sec_constraint_def_1 ON page_sec_constraint_def (constraints_def_id);

ALTER TABLE page_sec_constraints_def ADD CONSTRAINT fk_page_sec_constraints_def_1 FOREIGN KEY (page_security_id) REFERENCES page_security (page_security_id) ON DELETE CASCADE;
CREATE INDEX ix_page_sec_constraints_def_1 ON page_sec_constraints_def (page_security_id);

ALTER TABLE page_sec_constraints_ref ADD CONSTRAINT fk_page_sec_constraints_ref_1 FOREIGN KEY (page_security_id) REFERENCES page_security (page_security_id) ON DELETE CASCADE;
CREATE INDEX ix_page_sec_constraints_ref_1 ON page_sec_constraints_ref (page_security_id);

ALTER TABLE page_security ADD CONSTRAINT fk_page_security_1 FOREIGN KEY (parent_id) REFERENCES folder (folder_id) ON DELETE CASCADE;

ALTER TABLE portlet_preference_value ADD CONSTRAINT fk_portlet_preference FOREIGN KEY (pref_id) REFERENCES portlet_preference (id) ON DELETE CASCADE;
CREATE INDEX ix_portlet_preference ON portlet_preference_value (pref_id);

ALTER TABLE principal_permission ADD CONSTRAINT fk_principal_permission_1 FOREIGN KEY (permission_id) REFERENCES security_permission (permission_id) ON DELETE CASCADE;
ALTER TABLE principal_permission ADD CONSTRAINT fk_principal_permission_2 FOREIGN KEY (principal_id) REFERENCES security_principal (principal_id) ON DELETE CASCADE;
CREATE INDEX ix_principal_permission_1 ON principal_permission (permission_id);
CREATE INDEX ix_principal_permission_2 ON principal_permission (principal_id);

ALTER TABLE qrtz_blob_triggers ADD CONSTRAINT fk_qrtz_blob_triggers_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers (trigger_name, trigger_group);
-- add unique constraint although this is the primary key ? ok, but skip in following qrtz tables ...
ALTER TABLE qrtz_blob_triggers ADD CONSTRAINT un_qrtz_blob_triggers_1 UNIQUE (trigger_name, trigger_group);

ALTER TABLE qrtz_cron_triggers ADD CONSTRAINT fk_qrtz_cron_triggers_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers (trigger_name, trigger_group);

CREATE INDEX idx_qrtz_ft_job_group ON qrtz_fired_triggers (job_group);
CREATE INDEX idx_qrtz_ft_job_name ON qrtz_fired_triggers (job_name);
CREATE INDEX idx_qrtz_ft_job_req_recovery ON qrtz_fired_triggers (requests_recovery);
CREATE INDEX idx_qrtz_ft_job_stateful ON qrtz_fired_triggers (is_stateful);
CREATE INDEX idx_qrtz_ft_trig_group ON qrtz_fired_triggers (trigger_group);
CREATE INDEX idx_qrtz_ft_trig_inst_name ON qrtz_fired_triggers (instance_name);
CREATE INDEX idx_qrtz_ft_trig_name ON qrtz_fired_triggers (trigger_name);
CREATE INDEX idx_qrtz_ft_trig_nm_gp ON qrtz_fired_triggers (trigger_name, trigger_group);
CREATE INDEX idx_qrtz_ft_trig_volatile ON qrtz_fired_triggers (is_volatile);

CREATE INDEX idx_qrtz_j_req_recovery ON qrtz_job_details (requests_recovery);

ALTER TABLE qrtz_job_listeners ADD CONSTRAINT fk_qrtz_job_listeners_1 FOREIGN KEY (job_name, job_group) REFERENCES qrtz_job_details (job_name, job_group);

ALTER TABLE qrtz_simple_triggers ADD CONSTRAINT fk_qrtz_simple_triggers_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers (trigger_name, trigger_group);

ALTER TABLE qrtz_trigger_listeners ADD CONSTRAINT fk_qrtz_trigger_listeners_1 FOREIGN KEY (trigger_name, trigger_group) REFERENCES qrtz_triggers (trigger_name, trigger_group);

ALTER TABLE qrtz_triggers ADD CONSTRAINT fk_qrtz_triggers_1 FOREIGN KEY (job_name, job_group) REFERENCES qrtz_job_details (job_name, job_group);
CREATE INDEX idx_qrtz_t_next_fire_time ON qrtz_triggers (next_fire_time);
CREATE INDEX idx_qrtz_t_nft_st ON qrtz_triggers (next_fire_time, trigger_state);
CREATE INDEX idx_qrtz_t_state ON qrtz_triggers (trigger_state);
CREATE INDEX idx_qrtz_t_volatile ON qrtz_triggers (is_volatile);

ALTER TABLE rule_criterion ADD CONSTRAINT fk_rule_criterion_1 FOREIGN KEY (rule_id) REFERENCES profiling_rule (rule_id) ON DELETE CASCADE;
CREATE INDEX ix_rule_criterion_1 ON rule_criterion (rule_id);
CREATE INDEX ix_rule_criterion_2 ON rule_criterion (rule_id, fallback_order);

ALTER TABLE security_attribute ADD CONSTRAINT fk_principal_attr FOREIGN KEY (principal_id) REFERENCES security_principal (principal_id) ON DELETE CASCADE;
CREATE INDEX ix_name_lookup ON security_attribute (attr_name);
CREATE INDEX ix_principal_attr ON security_attribute (principal_id);

ALTER TABLE security_credential ADD CONSTRAINT fk_security_credential_1 FOREIGN KEY (principal_id) REFERENCES security_principal (principal_id) ON DELETE CASCADE;
CREATE INDEX ix_security_credential_1 ON security_credential (principal_id);

ALTER TABLE security_principal ADD CONSTRAINT fk_security_domain_1 FOREIGN KEY (domain_id) REFERENCES security_domain (domain_id) ON DELETE CASCADE;
CREATE INDEX ix_security_domain_1 ON security_principal (domain_id);

ALTER TABLE security_principal_assoc ADD CONSTRAINT fk_from_principal_assoc FOREIGN KEY (from_principal_id) REFERENCES security_principal (principal_id) ON DELETE CASCADE;
ALTER TABLE security_principal_assoc ADD CONSTRAINT fk_to_principal_assoc FOREIGN KEY (to_principal_id) REFERENCES security_principal (principal_id) ON DELETE CASCADE;
CREATE INDEX ix_from_principal_assoc ON security_principal_assoc (from_principal_id);
CREATE INDEX ix_to_principal_assoc ON security_principal_assoc (to_principal_id);
CREATE INDEX ix_to_principal_assoc_lookup ON security_principal_assoc (assoc_name, to_principal_id);

ALTER TABLE sso_site ADD CONSTRAINT fk_security_domain_2 FOREIGN KEY (domain_id) REFERENCES security_domain (domain_id) ON DELETE CASCADE;
CREATE INDEX ix_security_domain_2 ON sso_site (domain_id);


-- ADDED AFTER 4.0.3 RELEASE to be always executed when migrating
-- fix from update_4.0.2.1_postgres.sql
-- fix qrtz boolean columns on postgres, needs boolean instead of character !
-- e.g. see https://issues.liferay.com/browse/LEP-6855

ALTER TABLE qrtz_fired_triggers ALTER is_volatile TYPE boolean USING CASE is_volatile WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_fired_triggers ALTER is_stateful TYPE boolean USING CASE is_stateful WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_fired_triggers ALTER requests_recovery TYPE boolean USING CASE requests_recovery WHEN '1' THEN TRUE ELSE FALSE END;

ALTER TABLE qrtz_job_details ALTER is_durable TYPE boolean USING CASE is_durable WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_job_details ALTER is_volatile TYPE boolean USING CASE is_volatile WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_job_details ALTER is_stateful TYPE boolean USING CASE is_stateful WHEN '1' THEN TRUE ELSE FALSE END;
ALTER TABLE qrtz_job_details ALTER requests_recovery TYPE boolean USING CASE requests_recovery WHEN '1' THEN TRUE ELSE FALSE END;

ALTER TABLE qrtz_triggers ALTER is_volatile TYPE boolean USING CASE is_volatile WHEN '1' THEN TRUE ELSE FALSE END;
