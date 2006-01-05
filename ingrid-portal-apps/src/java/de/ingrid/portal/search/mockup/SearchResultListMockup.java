/*
 * Created on 01.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search.mockup;

import de.ingrid.portal.search.RankedSearchResult;
import de.ingrid.portal.search.SearchResult;
import de.ingrid.portal.search.SearchResultList;

/**
 * @author joachim
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchResultListMockup {

    public static SearchResultList getRankedSearchResultList() {
    	
    	SearchResultList srs = new SearchResultList();
    	RankedSearchResult rsr = new RankedSearchResult();
    	rsr.setResultTitle("BMVEL - Blei und Trinkwasser");
    	rsr.setType("WEBSITE");
    	rsr.setResultAbstract("Problem, Maßnahmen, weitere Informationen");
    	rsr.setResultProvider("Bundesministerium für Verbraucherschutz, Ernährung und Landwirtschaft (BMVEL)");
    	rsr.setMetaData("source", "Webseiten");
    	rsr.setMetaData("url", "http://www.verbraucherministerium.de/index-000068C0A9C31E01BB1301A5C0A8E066.html");
    	rsr.setResultRanking(0.99);
    	srs.add(rsr);
    	
    	rsr = new RankedSearchResult();
    	rsr.setResultTitle("BMU - Gesundheit und Umwelt - Lebensmittelsicherheit - Verbraucherschutz D");
    	rsr.setType("WEBSITE");
    	rsr.setResultAbstract("Bundesministerium für Umwelt, Naturschutz und Reaktorsicherheit - Zentrales Dach- und Rahmengesetz im Bereich des Lebensmittelrechts ist das Lebensmittel- und Bedarfsgegenständegesetz (LMBG), dessen Ziel es ist, den Verbraucherinnen und Verbraucher vor Gesundheitsgefährdungen und Täuschungen im Verkehr ...");
    	rsr.setResultProvider("Bundesumweltministerium");
    	rsr.setMetaData("source", "Webseiten");
    	rsr.setMetaData("url", "http://www.bmu.de/gesundheit_und_umwelt/lebensmittelsicherheit/verbraucherschutz_d/doc/2429.php");
    	rsr.setResultRanking(0.98);
    	srs.add(rsr);
    	
    	rsr = new RankedSearchResult();
    	rsr.setResultTitle("K 25 (und Beiblätter) Geologische Karte 1:25 000");
    	rsr.setType("UDK");
    	rsr.setResultAbstract("Das HLUG erstellt das Geologische Kartenwerk (GK 25) von Hessen.");
    	rsr.setResultProvider("Hessisches Landesamt für Umwelt und Geologie (HLUG)");
    	rsr.setMetaData("source", "UDK Umweltdatenkatalog");
    	rsr.setMetaData("map", "map describing data");
    	rsr.setResultRanking(0.97);
    	srs.add(rsr);
    	
    	srs.setNumberOfHits(3);
    	
    	
    	return srs;
    	
    	
    }

	/**
	 * @return
	 */
	public static SearchResultList getUnrankedSearchResultList() {
    	
		SearchResultList srs = new SearchResultList();
    	SearchResult rsr = new SearchResult();
    	rsr.setResultTitle("ULIDAT");
    	rsr.setType("WEBSITE");
    	rsr.setResultAbstract("Umweltliteraturdatenbank");
    	rsr.setResultProvider("Umweltbundesamt");
    	rsr.setMetaData("url", "http://doku.uba.de/cgi-bin/g2kadis?WEB=JA&ADISDB=AK&%24VTIV1=J&THEMA1=Blei+Tanne+Waldsterben+&TITEL=Blei+Tanne+Waldsterben+&THEMA2=Blei+Tanne+Waldsterben+&GEJAHR=&GEJAHV=&GEJAHB=&%24TMPLG=(DW0720*TH=TTDWDE,L=L020,Q=SWVF/DW0720*+o+THA710*TH=THESAW,L=L0710,Q=SWVF/THA710*+o+A0712*Q=BIBDE1*+o+CASNR*Q=CASNR1*+o+STOFFN*Q=STOFF1*+o+A0720P*Q=A0720P*+o+A0720*Q=A0720*)");
    	rsr.setMetaData("noOfHits", "18211");
    	srs.add(rsr);
    	
    	rsr = new RankedSearchResult();
    	rsr.setResultTitle("UFORDAT");
    	rsr.setType("WEBSITE");
    	rsr.setResultAbstract("Umweltforschungsdatenbank");
    	rsr.setResultProvider("Umweltbundesamt");
    	rsr.setMetaData("url", "http://doku.uba.de/cgi-bin/g2kadis?WEB=JA&ADISDB=VH&%24VTIV1=J&THEMA1=Blei+Tanne+Waldsterben+&TITEL=Blei+Tanne+Waldsterben+&THEMA2=Blei+Tanne+Waldsterben+&VHBEG=&VHBEGF=&STATUS=G,X&VERSTA=x,f&%24TMPLG=(DW0720*TH=TTDWDE,L=L0720,Q=SWVF/DW0720*+o+THKEY*TH=THESAW,L=L0710,Q=SWVF/THKEY*+o+AUTDD*Q=AUTDD1*+o+CASNR*Q=CASNR1*+o+STOFFN*Q=STOFF1*+o+A0720P*Q=A0720P*+o+A0720*Q=A0720*)+u+STATUS*Q=STATUS*+u+VERSTA*Q=VERSTA*");
    	rsr.setMetaData("noOfHits", "2236");
    	srs.add(rsr);
    	
    	srs.setNumberOfHits(2);
    	
    	return srs;
	}
}
