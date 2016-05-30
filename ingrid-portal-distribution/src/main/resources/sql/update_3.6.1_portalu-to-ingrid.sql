-- CMS update
UPDATE ingrid_cms SET item_description = 'InGrid-Portal informiert Text' WHERE item_key = 'ingrid.teaser.inform';
UPDATE ingrid_cms SET item_description = 'Über InGrid-Portal' WHERE item_key = 'ingrid.about';

-- CMS update ingrid.about
UPDATE ingrid_cms_item SET item_title = 'Über InGrid-Portal', item_value = '<p>Schreiben Sie ein Porträt zu Ihrem InGrid-Portal. Verwenden Sie hierbei die Administration des InGrid-Portals um die Daten zu pflegen und darzustellen.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.about') AND item_lang = 'de';
UPDATE ingrid_cms_item SET item_title = 'About InGrid-Portal', item_value = '<p>Write a portrait of the InGrid-Portal. To maintain and display your portrait use the InGrid-Portal admininstration interface</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.about') AND item_lang = 'en';

-- CMS update ingrid.teaser.inform
UPDATE ingrid_cms_item SET item_title = '<span style= \'text-transform: none;\'>InGrid-Portal INFO</span>', item_value = '<p>Hier können Sie über die Administration des InGrid-Portals aktuelle Nachrichten pflegen und darstellen.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.teaser.inform') AND item_lang = 'de';
UPDATE ingrid_cms_item SET item_title = '<span style=\'text-transform: none;\'>InGrid-Portal INFO</span>', item_value = '<p>Here you can maintain and display the latest news by using the InGrid-Portal administration interface.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.teaser.inform') AND item_lang = 'en';

-- CMS update ingrid.privacy
UPDATE ingrid_cms_item SET item_value = '<p>Verwenden Sie die Administration des InGrid-Portals um Ihr Datenschutz zu pflegen und im Portal darzustellen.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.privacy') AND item_lang = 'de';
UPDATE ingrid_cms_item SET item_value = '<p>Use the InGrid-Portal admininstration interface to maintain and display your privacy on the portal.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.privacy') AND item_lang = 'en';

-- CMS update ingrid.disclaimer
UPDATE ingrid_cms_item SET item_value = '<p>Verwenden Sie die Administration des InGrid-Portals um Ihr Impressum zu pflegen und im Portal darzustellen.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.disclaimer') AND item_lang = 'de';
UPDATE ingrid_cms_item SET item_value = '<p>Use the InGrid-Portal admininstration interface to maintain and display your disclaimer on the portal.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.disclaimer') AND item_lang = 'en';

-- CMS update ingrid.home.welcome
UPDATE ingrid_cms_item SET item_title = 'Willkommen bei InGrid-Portal', item_value = '<p>Tragen Sie hier einen Willkommens-Gruß an die InGrid-Portal Benutzer.</p>'  WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.home.welcome') AND item_lang = 'de';
UPDATE ingrid_cms_item SET item_title = 'Welcome to InGrid-Portal', item_value = '<p>Enter a welcome greeting to the InGrid-Portal users.</p>'  WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.home.welcome') AND item_lang = 'en';

-- CMS update ingrid.contact.intro.postEmail
UPDATE ingrid_cms_item SET item_value = '.</p><p>Hier können Sie über die Admininstration des InGrid-Portals ihr Kontaktdaten pflegen und darstellen.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.contact.intro.postEmail') AND item_lang = 'de';
UPDATE ingrid_cms_item SET item_value = '.</p><p>Here you can maintain and display your contact details by using the InGrid-Portal admininstration interface.</p>' WHERE fk_ingrid_cms_id = (SELECT id FROM ingrid_cms WHERE item_key = 'ingrid.contact.intro.postEmail') AND item_lang = 'en';
