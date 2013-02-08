package de.ingrid.rdf;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

public class RDFReaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void modelTest() {
        String uri = "http://data.uba.de/umt/_00049251";
        RDFReader rdf = new RDFReader();
        
        Model model = rdf.fetchTerm(uri);
        ResIterator rIt = model.listSubjects();
        System.out.println("Subjects");
        String foundUri = null;
        while (rIt.hasNext()) {
            Resource r = rIt.next();
            System.out.println(r.getURI());
            if (r.getURI() != null) {
                foundUri = r.getURI();
                break;            
            }
        }
        assertTrue(uri.equals(foundUri));
        
    }
    
    @Test
    public final void readFromUrlTest() {
        RDFReader rdf = new RDFReader();
        RDFMapper mapper = new RDFMapper();
        
        mapper.mapToTreeTerms(rdf.fetchAllChildren("http://data.uba.de/umt/_00049251"));
    }
    
}
