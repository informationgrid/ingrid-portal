package de.ingrid.rdf;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.ingrid.external.om.TreeTerm;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/application-context.xml"})
public class SNSServiceRDFTest {

	@Autowired
	SNSServiceRDF service;
	
    @Test
    public final void getRootElementHierarchyTest() {
        TreeTerm[] terms = service.getHierarchyTopLevel("http://boden-params.herokuapp.com/de/scheme.rdf", new Locale("de"));
        String[] expectedTerms = {"Bodenbiologische Parameter", "Bodenchemische Parameter (anorganisch)", "Bodenphysikalische Parameter", "Bodenchemische Parameter (organisch)"};
        assertTrue("Child terms from hierarchy seems empty!", terms.length > 0);
        for (int i = 0; i < terms.length; i++) {
			assertEquals(expectedTerms[i], terms[i].getName());
		}
        
        terms = service.getHierarchyTopLevel("http://boden-params.herokuapp.com/bodenbiologische-parameter.rdf", new Locale("de"));
        String[] expectedTerms2 = {"Bodenbiologische Parameter"};
        assertEquals("Child terms from hierarchy should be 1!", 1, terms.length);
        for (int i = 0; i < expectedTerms2.length; i++) {
			assertEquals(expectedTerms2[i], terms[i].getName());
		}
        
        terms = service.getHierarchyTopLevel("http://boden-params.herokuapp.com/de/concepts/bodenbiologische-parameter.rdf", new Locale("de"));
        String[] expectedTerms3 = {"Bodenbiologische Parameter"};
        assertEquals("Child terms from hierarchy should be 1!", 1, terms.length);
        for (int i = 0; i < expectedTerms3.length; i++) {
			assertEquals(expectedTerms3[i], terms[i].getName());
		}
        
        terms = service.getHierarchyTopLevel("http://boden-exam.herokuapp.com/de/scheme.rdf", new Locale("de"));
        String[] expectedTerms4 = {"Oberflächenwasser"};
        assertTrue("Child terms from hierarchy should not be empty!", terms.length > 0);
        for (int i = 0; i < expectedTerms4.length; i++) {
			assertEquals(expectedTerms4[i], terms[i].getName());
		}
    }

    @Test
    public final void getChildElementHierarchyTest() {
        TreeTerm[] terms = service.getHierarchyNextLevel("http://boden-params.herokuapp.com/bodenbiologische-parameter", new Locale("de"));
        String[] expectedTerms = {"Mineralisation", "Collembolen", "mikrobielle Aktivität", "Fauna"};
        assertTrue("Child terms from hierarchy seems empty!", terms.length > 0);
        // check first terms
        for (int i = 0; i < expectedTerms.length; i++) {
			assertEquals(expectedTerms[i], terms[i].getName());
		}
    }
    
//    @Test
//    public final void getRootElementBySearchTest() {
//        TreeTerm[] terms = service.getHierarchyTopLevel("http://data.uba.de/umt/de/", new Locale("de"));
//        String[] expectedTerms = {"[Erde und Weltraum]", "[Normen, technische Richtlinien]", "[Verkehr und Transport]", "[Chemische Stoffe und Prozesse]"};
//        assertTrue("Child terms from hierarchy seems empty!", terms.length > 0);
//        // check first terms
//        for (int i = 0; i < expectedTerms.length; i++) {
//			assertEquals(expectedTerms[i], terms[i].getName());
//		}
//        
//    }
    
    @Test
    public final void getRootElementWithoutHierarchySupportTest() {
    	TreeTerm[] terms = service.getHierarchyTopLevel("http://data.uba.de/umt/de/concepts/_00049251.rdf", new Locale("de"));
        String[] expectedTerms = {"Atmosphäre", "Klima", "Treibhauseffekt", "Klimasystem"};
        assertTrue("Child terms from hierarchy seems empty!", terms.length > 0);
        // check first terms
        for (int i = 0; i < expectedTerms.length; i++) {
			assertEquals(expectedTerms[i], terms[i].getName());
		}
    }
    
    @Test
    public final void getChildrenWithoutHierarchySupportTest() {
    	TreeTerm[] terms = service.getHierarchyNextLevel("http://data.uba.de/umt/de/concepts/_00049251", new Locale("de"));
        String[] expectedTerms = {"Atmosphäre", "Klima", "Treibhauseffekt", "Klimasystem"};
        assertTrue("Child terms from hierarchy seems empty!", terms.length > 0);
        // check first terms
        for (int i = 0; i < expectedTerms.length; i++) {
			assertEquals(expectedTerms[i], terms[i].getName());
		}
    }
    
}
