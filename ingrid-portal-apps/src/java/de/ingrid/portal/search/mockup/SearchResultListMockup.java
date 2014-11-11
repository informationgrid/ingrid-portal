/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Created on 01.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search.mockup;

import de.ingrid.portal.search.SearchResultList;
import de.ingrid.utils.IngridDocument;

/**
 * @author joachim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchResultListMockup {

    public static SearchResultList getRankedSearchResultList() {

        SearchResultList srs = new SearchResultList();
        int COUNT = 22;

        for (int i = 0; i < COUNT; i++) {
            IngridDocument rsr = new IngridDocument();
            rsr.put("title", "BMVEL - Blei und Trinkwasser");
            rsr.put("type", "WEBSITE");
            rsr.put("abstract", "Problem, Ma\u00dfnahmen, weitere Informationen");
            rsr.put("provider", "Bundesministerium f\u00fcr Verbraucherschutz, Ern\u00e4hrung und Landwirtschaft (BMVEL)");
            rsr.put("source", "Webseiten");
            rsr.put("url", "http://www.verbraucherministerium.de/index-000068C0A9C31E01BB1301A5C0A8E066.html");
            rsr.put("ranking", "0.99");
            // for catalogue tests
            rsr.put("date", "01.01.2006");
            rsr.put("service_rubric", "Presse");
            srs.add(rsr);

            rsr = new IngridDocument();
            rsr.put("title", "BMU - Gesundheit und Umwelt - Lebensmittelsicherheit - Verbraucherschutz D");
            rsr.put("type", "WEBSITE");
            rsr
                    .put(
                            "abstract",
                            "Bundesministerium f\u00fcr Umwelt, Naturschutz und Reaktorsicherheit - Zentrales Dach- und Rahmengesetz im Bereich des Lebensmittelrechts ist das Lebensmittel- und Bedarfsgegenst\u00e4ndegesetz (LMBG), dessen Ziel es ist, den Verbraucherinnen und Verbraucher vor Gesundheitsgef\u00e4hrdungen und T\u00e4uschungen im Verkehr ...");
            rsr.put("provider", "Bundesumweltministerium");
            rsr.put("source", "Webseiten");
            rsr.put("url", "http://www.verbraucherministerium.de/index-000068C0A9C31E01BB1301A5C0A8E066.html");
            //            "http://www.bmu.de/gesundheit_und_umwelt/lebensmittelsicherheit/verbraucherschutz_d/doc/2429.php");
            rsr.put("ranking", "0.98");
            // for catalogue tests
            rsr.put("date", "02.01.2006");
            rsr.put("service_rubric", "Publikationen");
            srs.add(rsr);

            rsr = new IngridDocument();
            rsr.put("title", "K 25 (und Beibl\u00e4tter) Geologische Karte 1:25 000");
            rsr.put("type", "UDK");
            rsr.put("abstract", "Das HLUG erstellt das Geologische Kartenwerk (GK 25) von Hessen.");
            rsr.put("provider", "Hessisches Landesamt f\u00fcr Umwelt und Geologie (HLUG)");
            rsr.put("source", "UDK Umweltdatenkatalog");
            rsr.put("map", "map describing data");
            rsr.put("ranking", "0.97");
            // for catalogue tests
            rsr.put("date", "03.01.2006");
            rsr.put("url", "http://www.wemove.com");
            rsr.put("service_rubric", "Veranstaltungen, Presse");
            srs.add(rsr);
        }
        srs.setNumberOfHits(3 * COUNT);

        return srs;

    }

    /**
     * @return
     */
    public static SearchResultList getUnrankedSearchResultList() {

        SearchResultList srs = new SearchResultList();

        IngridDocument rsr = new IngridDocument();
        rsr.put("title", "ULIDAT");
        rsr.put("type", "WEBSITE");
        rsr.put("abstract", "Umweltliteraturdatenbank");
        rsr.put("provider", "Umweltbundesamt");
        rsr
                .put(
                        "url",
                        "http://doku.uba.de/cgi-bin/g2kadis?WEB=JA&ADISDB=AK&%24VTIV1=J&THEMA1=Blei+Tanne+Waldsterben+&TITEL=Blei+Tanne+Waldsterben+&THEMA2=Blei+Tanne+Waldsterben+&GEJAHR=&GEJAHV=&GEJAHB=&%24TMPLG=(DW0720*TH=TTDWDE,L=L020,Q=SWVF/DW0720*+o+THA710*TH=THESAW,L=L0710,Q=SWVF/THA710*+o+A0712*Q=BIBDE1*+o+CASNR*Q=CASNR1*+o+STOFFN*Q=STOFF1*+o+A0720P*Q=A0720P*+o+A0720*Q=A0720*)");
        rsr.put("hits", "18211");
        srs.add(rsr);

        rsr = new IngridDocument();
        rsr.put("title", "UFORDAT");
        rsr.put("type", "WEBSITE");
        rsr.put("abstract", "Umweltforschungsdatenbank");
        rsr.put("provider", "Umweltbundesamt");
        rsr
                .put(
                        "url",
                        "http://doku.uba.de/cgi-bin/g2kadis?WEB=JA&ADISDB=VH&%24VTIV1=J&THEMA1=Blei+Tanne+Waldsterben+&TITEL=Blei+Tanne+Waldsterben+&THEMA2=Blei+Tanne+Waldsterben+&VHBEG=&VHBEGF=&STATUS=G,X&VERSTA=x,f&%24TMPLG=(DW0720*TH=TTDWDE,L=L0720,Q=SWVF/DW0720*+o+THKEY*TH=THESAW,L=L0710,Q=SWVF/THKEY*+o+AUTDD*Q=AUTDD1*+o+CASNR*Q=CASNR1*+o+STOFFN*Q=STOFF1*+o+A0720P*Q=A0720P*+o+A0720*Q=A0720*)+u+STATUS*Q=STATUS*+u+VERSTA*Q=VERSTA*");
        rsr.put("hits", "2236");
        srs.add(rsr);
        srs.add(rsr);
        srs.add(rsr);
        srs.add(rsr);
        srs.add(rsr);
        srs.add(rsr);
        srs.add(rsr);
        srs.add(rsr);
        srs.add(rsr);

        srs.setNumberOfHits(200);

        return srs;
    }
}
