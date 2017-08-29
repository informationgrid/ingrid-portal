UPDATE info SET value_name = '4.0.4' WHERE  info.key_name = 'version';

DELETE FROM help_messages WHERE gui_id = 10025;
DELETE FROM help_messages WHERE gui_id >= 5300 AND gui_id <= 5313;

INSERT INTO `help_messages` (`id`, `version`, `gui_id`, `entity_class`, `language`, `name`, `help_text`, `sample`) VALUES
(320, 0, 10025, -1, 'de', 'Zugriffsbeschränkungen', 'Das Feld „Zugriffsbeschränkungen“ beschreibt, die Art der Zugriffsbeschränkung. Bei frei nutzbaren Daten bzw. Services soll der Eintrag „keine“ ausgewählt werden (ISO: accessConstraints).', ''),
(1640, 0, 5300, -1, 'de', 'Raster/Gridformat', '', ''),
(1641, 0, 5300, -1, 'en', '', '', ''),
(1642, 0, 5301, -1, 'de', 'Verfügbarkeit von Transformationsparametern', '', ''),
(1643, 0, 5301, -1, 'en', '', '', ''),
(1644, 0, 5302, -1, 'de', 'Anzahl der Dimensionen', '', ''),
(1645, 0, 5302, -1, 'en', '', '', ''),
(1646, 0, 5303, -1, 'de', 'Achsenbezeichnung', '', ''),
(1647, 0, 5303, -1, 'en', '', '', ''),
(1648, 0, 5304, -1, 'de', 'Elementanzahl', '', ''),
(1649, 0, 5304, -1, 'en', '', '', ''),
(1650, 0, 5305, -1, 'de', 'Zellengeometrie', '', ''),
(1651, 0, 5305, -1, 'en', '', '', ''),
(1652, 0, 5306, -1, 'de', '', '', ''),
(1653, 0, 5306, -1, 'en', '', '', ''),
(1654, 0, 5307, -1, 'de', 'Kontrollpunktverfügbarkeit', '', ''),
(1655, 0, 5307, -1, 'en', '', '', ''),
(1656, 0, 5308, -1, 'de', 'Kontrollpunktbeschreibung', '', ''),
(1657, 0, 5308, -1, 'en', '', '', ''),
(1658, 0, 5309, -1, 'de', 'Eckpunkte', '', ''),
(1659, 0, 5309, -1, 'en', '', '', ''),
(1660, 0, 5310, -1, 'de', 'Punkt im Pixel', '', ''),
(1661, 0, 5310, -1, 'en', '', '', ''),
(1662, 0, 5311, -1, 'de', 'Passpunktverfügbarkeit', '', ''),
(1663, 0, 5311, -1, 'en', '', '', ''),
(1664, 0, 5312, -1, 'de', 'Verfügbarkeit der Orientierungsparameter', '', ''),
(1665, 0, 5312, -1, 'en', '', '', ''),
(1666, 0, 5313, -1, 'de', 'Georeferenzierungsparameter', '', ''),
(1667, 0, 5313, -1, 'en', '', '', '');
