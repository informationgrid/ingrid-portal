-- DB Version erhöhen
UPDATE ingrid_lookup SET item_value = '3.6.1', item_date = SYSDATE WHERE ingrid_lookup.item_key ='ingrid_db_version';

-- Remove PortalU contents
UPDATE fragment_pref_value SET value = 'ingrid.disclaimer' WHERE value = 'portalu.disclaimer';
UPDATE fragment_pref_value SET value = '/WEB-INF/templates/about_links.vm' WHERE value = '/WEB-INF/templates/about_portalu_links.vm';
UPDATE fragment_pref_value SET value = '/WEB-INF/templates/about_partner.vm' WHERE value = '/WEB-INF/templates/about_portalu_partner.vm';
UPDATE fragment_pref_value SET value = 'ingrid.privacy' WHERE value = 'portalu.privacy';
UPDATE fragment_pref_value SET value = 'ingrid.about' WHERE value = 'portalu.about';

UPDATE ingrid_cms SET item_key = 'ingrid.teaser.inform' WHERE item_key = 'portalu.teaser.inform';
UPDATE ingrid_cms SET item_key = 'ingrid.disclaimer' WHERE item_key = 'portalu.disclaimer';
UPDATE ingrid_cms SET item_key = 'ingrid.about' WHERE item_key = 'portalu.about';
UPDATE ingrid_cms SET item_key = 'ingrid.privacy' WHERE item_key = 'portalu.privacy';
UPDATE ingrid_cms SET item_key = 'ingrid.contact.intro.postEmail' WHERE item_key = 'portalu.contact.intro.postEmail';

UPDATE link SET url = 'mailto:info@informationgrid.eu' WHERE url = 'mailto:webmaster@portalu.de';

UPDATE localized_description SET description = 'InGrid Application' WHERE description = 'PortalU Application';
UPDATE localized_display_name SET display_name = 'InGrid Application' WHERE display_name = 'PortalU Application';

UPDATE page SET title = 'About InGrid', short_title = 'About InGrid' WHERE title = 'About PortalU';

UPDATE portlet_preference_value SET pref_value = 'ingrid.cms.default' WHERE pref_value = 'portalu.cms.default';
UPDATE portlet_preference_value SET pref_value = 'ingrid.teaser.inform' WHERE pref_value = 'portalu.teaser.inform';

UPDATE prefs_property_value SET property_value = 'ingrid.cms.default' WHERE property_value = 'portalu.cms.default';
UPDATE prefs_property_value SET property_value = 'ingrid.teaser.inform' WHERE property_value = 'portalu.teaser.inform';
