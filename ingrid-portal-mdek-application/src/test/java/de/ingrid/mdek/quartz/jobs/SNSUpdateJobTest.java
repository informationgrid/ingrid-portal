package de.ingrid.mdek.quartz.jobs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
import de.ingrid.mdek.MdekUtils.SearchtermType;
import de.ingrid.mdek.caller.MdekCallerCatalog;
import de.ingrid.mdek.dwr.services.sns.SNSService;
import de.ingrid.mdek.dwr.services.sns.SNSTopic;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.mdek.job.repository.Pair;
import de.ingrid.utils.IngridDocument;

public class SNSUpdateJobTest {
    
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
        service.setThesaurusService( snsService );
        
        when(connFacade.getCurrentPlugId()).thenReturn( "test-plug-mock" );
        when(connFacade.getMdekCallerCatalog()).thenReturn( callerCatalog );
        
        when(context.get( Matchers.any() )).thenReturn(-1);

    }
    
    private IngridDocument getTestSnsTerms() {
        IngridDocument doc = new IngridDocument();
        doc.put( IJobRepository.JOB_INVOKE_SUCCESS, true);
        
        List<Pair> list = new ArrayList<Pair>();
        IngridDocument subDoc = new IngridDocument();
        
        List<IngridDocument> thesTerms = new ArrayList<IngridDocument>();
        IngridDocument t = new IngridDocument();
        t.put( MdekKeys.TERM_TYPE, SearchtermType.UMTHES.getDbValue() );
        t.put( MdekKeys.TERM_NAME, "Forschungspolitik old" );
        t.put( MdekKeys.TERM_SNS_ID, "uba_thes_10065" );
        thesTerms.add( t );
        subDoc.put( MdekKeys.SUBJECT_TERMS, thesTerms);
        
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
    public void testExecuteInternalJobExecutionContext() throws JobExecutionException {
//        String[] changedTopics = new String[1];
//        changedTopics[0] = "_00010918";
        
        SNSUpdateJob snsUpdateJob = new SNSUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", service);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        jdm.put( "URL_THESAURUS", ResourceBundle.getBundle("sns").getString("sns.serviceURL.thesaurus"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List old = (List) args[1];
                List newTerms = (List) args[2];
                assertThat( (String)((IngridDocument)old.get( 0 )).get( "term-sns-id" ), is( "uba_thes_10065" ));
                assertThat( (String)((IngridDocument)old.get( 0 )).get( "term-name" ), is( "Forschungspolitik old" ));
                assertThat( (String)((IngridDocument)newTerms.get( 0 )).get( "term-sns-id" ), is( "http://umthes.innoq.com/_00010065" ));
                assertThat( (String)((IngridDocument)newTerms.get( 0 )).get( "term-name" ), is( "Forschungspolitik" ));
                
                return null;
            }})
        .when(callerCatalog).updateSearchTerms( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        when(callerCatalog.getSearchTerms( "test-plug-id", new SearchtermType[] { SearchtermType.UMTHES, SearchtermType.GEMET }, "test-user-id" )).thenReturn( getTestSnsTerms()  );
        when(callerCatalog.getSearchTerms( "test-plug-id", new SearchtermType[] { SearchtermType.FREI },                         "test-user-id" )).thenReturn( getTestFreeTerms() );
        
        // START Process
        snsUpdateJob.executeInternal( context );
        
    }
    
    @Test
    public void testExpiredTerms() throws Exception {
        SNSUpdateJob snsUpdateJob = new SNSUpdateJob();
        JobDataMap jdm = new JobDataMap();
        jdm.put( "SNS_SERVICE", serviceMock);
        jdm.put( "CONNECTION_FACADE", connFacade);
        jdm.put( "PLUG_ID", "test-plug-id");
        jdm.put( "USER_ID", "test-user-id");
        jdm.put( "LOCALE", new Locale("de"));
        jdm.put( "URL_THESAURUS", ResourceBundle.getBundle("sns").getString("sns.serviceURL.thesaurus"));
        when(context.getMergedJobDataMap()).thenReturn( jdm );
        
        when(callerCatalog.getSearchTerms( "test-plug-id", new SearchtermType[] { SearchtermType.UMTHES, SearchtermType.GEMET }, "test-user-id" )).thenReturn( getTestSnsTerms()  );
        when(callerCatalog.getSearchTerms( "test-plug-id", new SearchtermType[] { SearchtermType.FREI },                         "test-user-id" )).thenReturn( getTestFreeTerms() );
        
        SNSTopic expiredTopic = new SNSTopic(SNSTopic.Type.DESCRIPTOR, SNSTopic.Source.UMTHES, "http://umthes.innoq.com/_00010065", "Forschungspolitik", null, null);
        expiredTopic.setExpired( true );
        when(serviceMock.getPSI( "http://umthes.innoq.com/_00010065", new Locale("de"))).thenReturn( expiredTopic  );
        
     // CHECK RESULTS
        Mockito.doAnswer( new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                List<IngridDocument> oldTerms = (List) args[1];
                List<IngridDocument> newTerms = (List) args[2];
                
                assertThat(oldTerms.size(), is(2));
                assertThat(oldTerms.get(1), not( nullValue() ));
                assertThat(newTerms.size(), is(2));
                assertThat(newTerms.get(1), nullValue()); // will be deleted if null and inserted as a free term
                return null;
            }})
        .when(callerCatalog).updateSearchTerms( Matchers.anyString(), Matchers.anyList(), Matchers.anyList(), Matchers.anyString());
        
        // START Process
        snsUpdateJob.executeInternal( context );
    }

}
