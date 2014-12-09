/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
package de.ingrid.mdek.quartz.jobs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.MdekUtils.SpatialReferenceType;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.dwr.services.sns.SNSLocationTopic;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.mdek.job.repository.Pair;
import de.ingrid.utils.IngridDocument;

public class SNSUpdateJobGazetteerTest {
    
    @InjectMocks SNSService service;
    
    @Mock SNSService serviceMock;
    
    @Mock private ConnectionFacade connFacade;
    
    @Mock private JobExecutionContext context;
    
    @Mock private MdekCallerCatalog callerCatalog;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        de.ingrid.external.sns.SNSService snsService = new de.ingrid.external.sns.SNSService();
        snsService.init();
        service.setGazetteerService( snsService );
        
        when(connFacade.getCurrentPlugId()).thenReturn( "test-plug-mock" );
        when(connFacade.getMdekCallerCatalog()).thenReturn( callerCatalog );
        
        when(context.get( Matchers.any() )).thenReturn(-1);

    }
    
    private IngridDocument getTestSpatialReferenceMariental(String snsId) {
        IngridDocument doc = new IngridDocument();
        doc.put( IJobRepository.JOB_INVOKE_SUCCESS, true);
        
        List<Pair> list = new ArrayList<Pair>();
        IngridDocument subDoc = new IngridDocument();
        
        if (snsId == null) {
            snsId = "GEMEINDE0315401015";
        }
        
        List<IngridDocument> locationTerms = new ArrayList<IngridDocument>();
        IngridDocument t = new IngridDocument();
        t.put( MdekKeys.LOCATION_SNS_ID, snsId );
        t.put( MdekKeys.LOCATION_NAME, "Mariental" );
        t.put( MdekKeys.LOCATION_CODE, "03154015" );
        t.put( MdekKeys.SNS_TOPIC_TYPE, "use6Type" );
        t.put( MdekKeys.WEST_BOUNDING_COORDINATE, 10.9477766 );
        t.put( MdekKeys.SOUTH_BOUNDING_COORDINATE, 52.2679426 );
        t.put( MdekKeys.EAST_BOUNDING_COORDINATE, 11.0081856 );
        t.put( MdekKeys.NORTH_BOUNDING_COORDINATE, 52.2972192 );
        
        locationTerms.add( t );
        subDoc.put( MdekKeys.LOCATIONS, locationTerms);
        
        Pair p = new Pair( "key", subDoc);
        list.add( p );
        
        doc.put( IJobRepository.JOB_INVOKE_RESULTS, list );
        
        return doc;
    }
    
    private IngridDocument getTestSpatialReferencesExpired() {
        IngridDocument doc = new IngridDocument();
        doc.put( IJobRepository.JOB_INVOKE_SUCCESS, true);
        
        List<Pair> list = new ArrayList<Pair>();
        IngridDocument subDoc = new IngridDocument();
        
        List<IngridDocument> locationTerms = new ArrayList<IngridDocument>();
        IngridDocument t = new IngridDocument();
        t.put( MdekKeys.LOCATION_SNS_ID, "GEMEINDE1510100000" );
        t.put( MdekKeys.LOCATION_NAME, "Dessau" );
        t.put( MdekKeys.LOCATION_CODE, "15101000" );
        t.put( MdekKeys.SNS_TOPIC_TYPE, "use6Type" );
        t.put( MdekKeys.WEST_BOUNDING_COORDINATE, 12.23218175 );
        t.put( MdekKeys.SOUTH_BOUNDING_COORDINATE, 51.835342 );
        t.put( MdekKeys.EAST_BOUNDING_COORDINATE, 12.23218175 );
        t.put( MdekKeys.NORTH_BOUNDING_COORDINATE, 51.835342 );
        
        locationTerms.add( t );
        subDoc.put( MdekKeys.LOCATIONS, locationTerms);
        
        Pair p = new Pair( "key", subDoc);
        list.add( p );
        
        doc.put( IJobRepository.JOB_INVOKE_RESULTS, list );
        
        return doc;
    }
    
    private IngridDocument getTestFreeTerms() {
        IngridDocument doc = new IngridDocument();
        doc.put( IJobRepository.JOB_INVOKE_SUCCESS, true);
        
        List<Pair> list = new ArrayList<Pair>();
        IngridDocument subDoc = new IngridDocument();
        
        Pair p = new Pair( "key", subDoc);
        list.add( p );
        
        doc.put( IJobRepository.JOB_INVOKE_RESULTS, list );
        
        return doc;
    }

    @Test
    public void testNormalTerm() throws JobExecutionException {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE0315401015" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID), is( "GEMEINDE0315401015" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_NAME), is( "Mariental" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_CODE), is( "03154015" ));
                assertThat( newDoc.getString( MdekKeys.SNS_TOPIC_TYPE), is( "use6Type" ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( getTestSpatialReferenceMariental(null)  );
        
        // START Process
        snsUpdateJob.executeInternal( context );
        
    }
    
    //@Test
    public void testExpiredTermsWithSuccessor() throws Exception {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        //jdm.put( "SNS_SERVICE", serviceMock);
        jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( getTestSpatialReferencesExpired()  );
        
//        SNSTopic expiredTopic = new SNSTopic(SNSTopic.Type.DESCRIPTOR, SNSTopic.Source.UMTHES, "http://umthes.innoq.com/_00010065", "Forschungspolitik", null, null);
//        expiredTopic.setExpired( true );
//        when(serviceMock.getPSI( "http://umthes.innoq.com/_00010065", new Locale("de"))).thenReturn( expiredTopic  );
        
     // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE1510100000" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID ), nullValue());
                assertThat( newDoc.getString( MdekKeys.LOCATION_EXPIRED_AT), is( "2012-11-28" ));
                List<IngridDocument> successors = (List<IngridDocument>) newDoc.get( MdekKeys.SUCCESSORS );
                assertThat( successors.size(), is( 1 ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_SNS_ID), is( "http://iqvoc-gazetteer.innoq.com/_4e9d66f0-1b80-0130-d0e8-482a1437a069" ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_NAME), is( "Dessau-Roßlau" ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_CODE), is( "15001000" ));
                assertThat( successors.get(0).getString( MdekKeys.SNS_TOPIC_TYPE), is( "-location-admin-use6-" ));
//                assertThat( (Double)oldDoc.get( MdekKeys.WEST_BOUNDING_COORDINATE ), is( 10.9477766 ));
//                assertThat( (Double)newDoc.get( MdekKeys.WEST_BOUNDING_COORDINATE ), is( 10.9477766 ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }

    
    //@Test
    public void testExpiredTermsWithExpiredSuccessor() throws Exception {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", serviceMock);
        //jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( getTestSpatialReferencesExpired()  );
        
        SNSLocationTopic topic = new SNSLocationTopic();
        topic.setName( "Mariental" );
        topic.setExpired( true );
        topic.setExpiredDate( "2012-11-28" );
        SNSLocationTopic succTopic = new SNSLocationTopic();
        succTopic.setName( "Altona" );
        succTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b" );
        succTopic.setExpired( true );
        ArrayList<SNSLocationTopic> list = new ArrayList<SNSLocationTopic>();
        list.add( succTopic );
        topic.setSuccessors( list ); // Hamburg - Altona
        when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/GEMEINDE1510100000", new Locale("de"), null)).thenReturn( topic  );
        
     // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE1510100000" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID ), nullValue());
                assertThat( newDoc.getString( MdekKeys.LOCATION_EXPIRED_AT), is( "2012-11-28" ));
                List<IngridDocument> successors = (List<IngridDocument>) newDoc.get( MdekKeys.SUCCESSORS );
                assertThat( successors.size(), is( 0 ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }
    
    //@Test
    public void testExpiredTermsWithExpiredSuccessorAndValidSuccessor() throws Exception {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", serviceMock);
        //jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( getTestSpatialReferencesExpired()  );
        
        SNSLocationTopic topic = new SNSLocationTopic();
        topic.setName( "Mariental" );
        topic.setExpired( true );
        topic.setExpiredDate( "2012-11-28" );
        SNSLocationTopic succTopic = new SNSLocationTopic();
        succTopic.setName( "Altona" );
        succTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b" );
        succTopic.setExpired( true );
        
        SNSLocationTopic succValidTopic = new SNSLocationTopic();
        succValidTopic.setName( "Altona New" );
        succValidTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/id_altona_new" );
        List<SNSLocationTopic> validSuccs = new ArrayList<SNSLocationTopic>();
        validSuccs.add( succValidTopic );
        succTopic.setSuccessors( validSuccs );
        
        ArrayList<SNSLocationTopic> list = new ArrayList<SNSLocationTopic>();
        list.add( succTopic );
        topic.setSuccessors( list ); // Hamburg - Altona
        when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/GEMEINDE1510100000", new Locale("de"), null)).thenReturn( topic  );
        
     // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE1510100000" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID ), nullValue());
                assertThat( newDoc.getString( MdekKeys.LOCATION_EXPIRED_AT), is( "2012-11-28" ));
                List<IngridDocument> successors = (List<IngridDocument>) newDoc.get( MdekKeys.SUCCESSORS );
                assertThat( successors.size(), is( 1 ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_SNS_ID), is( "http://iqvoc-gazetteer.innoq.com/id_altona_new" ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_NAME), is( "Altona New" ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }
    
    //@Test
    public void testExpiredTermsWithExpiredSuccessorAndSeveralValidSuccessors() throws Exception {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", serviceMock);
        //jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( getTestSpatialReferencesExpired()  );
        
        SNSLocationTopic topic = new SNSLocationTopic();
        topic.setName( "Mariental" );
        topic.setExpired( true );
        topic.setExpiredDate( "2012-11-28" );
        SNSLocationTopic succTopic = new SNSLocationTopic();
        succTopic.setName( "Altona" );
        succTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b" );
        succTopic.setExpired( true );
        SNSLocationTopic succTopic2 = new SNSLocationTopic();
        succTopic2.setName( "Altona Other" );
        succTopic2.setTopicId( "http://iqvoc-gazetteer.innoq.com/id_altona_other" );
        
        SNSLocationTopic succValidTopic = new SNSLocationTopic();
        succValidTopic.setName( "Altona New" );
        succValidTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/id_altona_new" );
        List<SNSLocationTopic> validSuccs = new ArrayList<SNSLocationTopic>();
        validSuccs.add( succValidTopic );
        succTopic.setSuccessors( validSuccs );
        
        ArrayList<SNSLocationTopic> list = new ArrayList<SNSLocationTopic>();
        list.add( succTopic );
        list.add( succTopic2 );
        topic.setSuccessors( list ); // Hamburg - Altona
        when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/GEMEINDE1510100000", new Locale("de"), null)).thenReturn( topic  );
        
     // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE1510100000" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID ), nullValue());
                assertThat( newDoc.getString( MdekKeys.LOCATION_EXPIRED_AT), is( "2012-11-28" ));
                List<IngridDocument> successors = (List<IngridDocument>) newDoc.get( MdekKeys.SUCCESSORS );
                assertThat( successors.size(), is( 2 ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_SNS_ID), is( "http://iqvoc-gazetteer.innoq.com/id_altona_new" ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_NAME), is( "Altona New" ));
                assertThat( successors.get(1).getString( MdekKeys.LOCATION_SNS_ID), is( "http://iqvoc-gazetteer.innoq.com/id_altona_other" ));
                assertThat( successors.get(1).getString( MdekKeys.LOCATION_NAME), is( "Altona Other" ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }


    
    //@Test
    public void testValidTermsWithSuccessor() throws Exception {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", serviceMock);
        //jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( getTestSpatialReferenceMariental(null) );
        
        SNSLocationTopic topic = new SNSLocationTopic();
        topic.setName( "Mariental" );
        topic.setTopicId( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015" );
        SNSLocationTopic succTopic = new SNSLocationTopic();
        succTopic.setName( "Altona" );
        succTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b" );
        ArrayList<SNSLocationTopic> list = new ArrayList<SNSLocationTopic>();
        list.add( succTopic );
        topic.setSuccessors( list ); // Hamburg - Altona
        when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015", new Locale("de"), null)).thenReturn( topic  );
        // when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b", new Locale("de"))).thenReturn( succTopic  );
        
        // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                assertThat( old.size(), is( 1 ) );
                assertThat( newTerms.size(), is( 1 ) );
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE0315401015" ));
                
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID), is( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015" ));                
                assertThat( newDoc.getString( MdekKeys.LOCATION_EXPIRED_AT), nullValue() );
                
                List<IngridDocument> successors = (List<IngridDocument>) newDoc.get( MdekKeys.SUCCESSORS );
                assertThat( successors.size(), is( 1 ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_SNS_ID), is( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b" ));
                assertThat( successors.get(0).getString( MdekKeys.LOCATION_NAME), is( "Altona" ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }
    
    //@Test
    public void testValidTermsWithSuccessorReferencingItself() throws Exception {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", serviceMock);
        //jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( getTestSpatialReferenceMariental(null) );
        
        SNSLocationTopic topic = new SNSLocationTopic();
        topic.setName( "Mariental" );
        topic.setTopicId( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015" );
        SNSLocationTopic succTopic = new SNSLocationTopic();
        succTopic.setName( "Mariental" );
        succTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015" );
        ArrayList<SNSLocationTopic> list = new ArrayList<SNSLocationTopic>();
        list.add( succTopic );
        topic.setSuccessors( list ); // Successor is a reference to itself
        when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015", new Locale("de"), null)).thenReturn( topic  );
        
        // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                assertThat( old.size(), is( 1 ) );
                assertThat( newTerms.size(), is( 1 ) );
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE0315401015" ));
                
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID), is( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015" ));                
                assertThat( newDoc.getString( MdekKeys.LOCATION_EXPIRED_AT), nullValue() );
                
                List<IngridDocument> successors = (List<IngridDocument>) newDoc.get( MdekKeys.SUCCESSORS );
                assertThat( successors.size(), is( 0 ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }
    
    @Test
    public void testTermIdNotFoundInsteadBySearchingName() throws Exception {
        SNSLocationUpdateJob snsUpdateJob = new SNSLocationUpdateJob();
        JobDataMap jdm = new JobDataMap();
        //jdm.put( "SNS_SERVICE", serviceMock);
        jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        //jdm.put( "URL_GAZETTEER", ResourceBundle.getBundle("sns").getString("sns.serviceURL.gazetteer"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        // use unknown id so that a search is initiated
        IngridDocument mariental = getTestSpatialReferenceMariental("GEMEINDE0315400015");
        when(callerCatalog.getSpatialReferences( "test-plug-id", new SpatialReferenceType[] { SpatialReferenceType.GEO_THESAURUS }, "test-user-id" )).thenReturn( mariental );
        
//        SNSLocationTopic topic = new SNSLocationTopic();
//        topic.setName( "Mariental" );
//        topic.setTopicId( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015" );
//        topic.setSuccessorId( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b" ); // Hamburg - Altona
//        when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/GEMEINDE0315401015", new Locale("de"))).thenReturn( topic  );
//        SNSLocationTopic succTopic = new SNSLocationTopic();
//        succTopic.setName( "Altona" );
//        succTopic.setTopicId( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b" );
//        when(serviceMock.getLocationPSI( "http://iqvoc-gazetteer.innoq.com/t19b808a_115f54c19e9_-2f5b", new Locale("de"))).thenReturn( succTopic  );
//        
        // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                IngridDocument oldDoc = ((IngridDocument)old.get( 0 ));
                IngridDocument newDoc = ((IngridDocument)newTerms.get( 0 ));
                assertThat( oldDoc.getString( MdekKeys.LOCATION_SNS_ID ), is( "GEMEINDE0315400015" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_SNS_ID), is( "GEMEINDE0315401015" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_NAME), is( "Mariental" ));
                assertThat( newDoc.getString( MdekKeys.LOCATION_CODE), is( "03154015" ));
                assertThat( newDoc.getString( MdekKeys.SNS_TOPIC_TYPE), is( "use6Type" ));
                
                IngridDocument doc = new IngridDocument();
                doc.put( IJobRepository.JOB_INVOKE_SUCCESS, false );
                return doc;
            }})
        .when(callerCatalog).updateSpatialReferences( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }
}
