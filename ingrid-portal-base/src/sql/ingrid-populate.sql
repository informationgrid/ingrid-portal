INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (1, 'bund', 'Bund', 1);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (2, 'bw', 'Baden-Württemberg', 2);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (3, 'by', 'Bayern', 3);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (4, 'be', 'Berlin', 4);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (5, 'bb', 'Brandenburg', 5);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (6, 'hb', 'Bremen', 6);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (7, 'hh', 'Hamburg', 7);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (8, 'he', 'Hessen', 8);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (9, 'mv', 'Mecklenburg-Vorpommern', 9);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (10, 'ni', 'Niedersachsen', 10);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (11, 'nw', 'Nordrhein-Westfalen', 11);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (12, 'rp', 'Rheinland-Pfalz', 12);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (13, 'sl', 'Saarland', 13);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (14, 'sn', 'Sachsen', 14);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (15, 'st', 'Sachsen-Anhalt', 15);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (16, 'sh', 'Schleswig-Holstein', 16);
INSERT INTO ingrid_partner (id, ident, name, sortkey) VALUES (17, 'th', 'Thüringen', 17);


INSERT INTO ingrid_rss_source (id, provider, description, url, lang) VALUES (1, 'UBA', 'RSS-UBA', 'http://www.uba.de/rss/ubapresseinfo.xml', 'de');
INSERT INTO ingrid_rss_source (id, provider, description, url, lang) VALUES (2, 'BFN', 'BFN-UBA', 'http://www.bfn.de/6.100.html', 'de');
INSERT INTO ingrid_rss_source (id, provider, description, url, lang) VALUES (3, 'MUF RLP', 'MUF RLP', 'http://www.muf.rlp.de/rss/rss_1_20.xml', 'de');
INSERT INTO ingrid_rss_source (id, provider, description, url, lang) VALUES (4, 'BFN', 'BfN-Skripten', 'http://www.bfn.de/rss/skripten.xml', 'de');

INSERT INTO PRINCIPAL_RULE_ASSOC VALUES ('admin', 'page', 'role-fallback');
