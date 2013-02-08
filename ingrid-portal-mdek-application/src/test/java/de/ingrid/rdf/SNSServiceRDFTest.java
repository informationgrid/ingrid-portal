package de.ingrid.rdf;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/application-context.xml"})
public class SNSServiceRDFTest {

	@Autowired
	SNSServiceRDF service;
	
    @Test
    public final void getRootElementTest() {
        service.getHierarchyTopLevel("http://data.uba.de/umt/de/", new Locale("de"));
    }

}
