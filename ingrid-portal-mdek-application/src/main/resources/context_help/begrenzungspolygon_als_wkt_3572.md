---
# ID des GUI Elements
guid: 3572
# title, used as window title
title: Begrenzungspolygon als WKT
---

# Begrenzungspolygon als WKT

Begrenzungspolygon als WKT.<br />Obwohl das ISO-Element Polygon heißt, können in diesem Feld auch andere Geometrietypen angegeben werden. Genauer gesagt, werden diese WKT-Klassen unterstützt:
* POINT: Ein einziger Punkt z.B. POINT(10 10)
* MULTIPOINT: Eine Punktsammlung z.B. MULTIPOINT((0 0), (10 10), (1.3 9.5))
* LINESTRING: Eine einzige Linie z.B. LINESTRING(10 10, 20 20, 10 40)
* MULTILINE: Eine Liniensammlung z.B. MULTILINE((10 10, 20 20, 10 40), (5.1 9.3, 3.6 -1.8), (0 0, 1 0, 1 1, 0 1))
* POLYGON: Ein einziges Polygon z.B. POLYGON((0 0, 0 10, 10 10, 10 0, 0 0), (5 5, 5 7, 7 7, 7 5, 5 5))
* MULTIPOLYGON: Eine Sammlung der Polygone z.B. MULTIPOLYGON(((0 0, 0 10, 10 10, 10 0, 0 0),(5 5, 5 7, 7 7, 7 5, 5 5)), (15 20, 25 30, 33 25, 15 20)))
* GEOMETRYCOLLECTION: Eine Sammlung von o.g. Geometrien z.B. GEOMETRYCOLLECTION(POINT(10 10), LINESTRING(10 10, 20 20, 10 40), POLYGON((0 0, 0 10, 10 10, 10 0, 0 0)))

Die Koordinaten müssen zwingend im WGS84 Koordinatenreferenzsystem angegeben werden.
