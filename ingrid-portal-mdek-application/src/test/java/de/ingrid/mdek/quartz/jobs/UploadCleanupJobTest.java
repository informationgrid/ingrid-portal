/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionContext;

import de.ingrid.mdek.MdekKeys;
import de.ingrid.mdek.caller.MdekCallerQuery;
import de.ingrid.mdek.caller.MdekClientCaller;
import de.ingrid.mdek.handler.ConnectionFacade;
import de.ingrid.mdek.job.repository.IJobRepository;
import de.ingrid.mdek.job.repository.Pair;
import de.ingrid.mdek.quartz.jobs.UploadCleanupJob.FileReference;
import de.ingrid.mdek.upload.storage.impl.FileSystemStorage;
import de.ingrid.utils.IngridDocument;

public class UploadCleanupJobTest {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Path DOCS_PATH = Paths.get("target", "ingrid-upload-test");
    private static final String OBJ_UUID = "5F1AF722-D767-4980-8403-A432173D5684";
    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";
    private static final String PLUG_ID = "test-plug-id";
    private static final String FAILING_PLUG_ID = "test-fail-plug-id";
    private static final Integer DEFAULT_FILE_AGE = 7201;

    private static final LocalDateTime JOB_REFERENCE_TIME = LocalDateTime.parse("25.04.2017 12:00:00", dtf);
    private static final Integer JOB_MIN_FILE_AGE = 7200;

	@Mock private ConnectionFacade connectionFacade;
    @Mock private MdekClientCaller mdekClientCaller;
    @Mock private MdekCallerQuery mdekCallerQuery;
    @Mock private JobExecutionContext context;

    private UploadCleanupJob job;
    private FileSystemStorage storage;

    private Logger jobLogger = Logger.getLogger(UploadCleanupJob.class);
    private TestAppender testAppender = new TestAppender();

    public class TestAppender extends AppenderSkeleton {
        public List<LoggingEvent> eventList = new ArrayList<LoggingEvent>();

        @Override
        protected void append(LoggingEvent event) {
            this.eventList.add(event);
        }

        @Override
        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }

        public List<LoggingEvent> getEvents(Level level) {
            return this.eventList.stream().filter(e -> e.getLevel().equals(level)).collect(Collectors.toList());
        }

        public boolean hasIssues() {
            return this.getEvents(Level.FATAL).size() > 0 || this.getEvents(Level.ERROR).size() > 0 ||
                this.getEvents(Level.WARN).size() > 0;
        }
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // setup mdek client
        List<String> plugIds = Stream.of(FAILING_PLUG_ID, PLUG_ID).collect(Collectors.toList());
        when(this.mdekClientCaller.getRegisteredIPlugs()).thenReturn(plugIds);
        when(this.connectionFacade.getMdekClientCaller()).thenReturn(this.mdekClientCaller);
        when(this.connectionFacade.getMdekCallerQuery()).thenReturn(this.mdekCallerQuery);

        // setup storage
        this.storage = new FileSystemStorage();
        this.storage.setDocsDir(DOCS_PATH.toString());
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
        Files.createDirectories(DOCS_PATH);

        // set up job
        this.job = new UploadCleanupJob();
        this.job.setConnectionFacade(this.connectionFacade);
        this.job.setStorage(this.storage);
        this.job.setReferenceDate(JOB_REFERENCE_TIME);
        this.job.setDeleteFileMinAge(JOB_MIN_FILE_AGE);

        // setup logging
        this.jobLogger.addAppender(this.testAppender);
    }

    @After
    public void tearDown() throws Exception {
    	this.jobLogger.removeAppender(this.testAppender);
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
    }

    /**
     * Test:
     * - Simulate no iPLugs available, all files must remain
     * @throws Exception
     */
    @Test
    public void testNoIPlugsAvailable() throws Exception {

        // simulate no iplugs
        when(this.mdekClientCaller.getRegisteredIPlugs()).thenReturn(null);

        // set up files
        String unreferencedFile1 = "UnreferencedFile1";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        // exception is logged
        assertEquals(1, this.testAppender.getEvents(Level.ERROR).size());
    }

    /**
     * Test:
     * - Simulate query returns no result, all files must remain
     * @throws Exception
     */
    @Test
    public void testQueryReturnsNoResults() throws Exception {

        // set up files
        String unreferencedFile1 = "UnreferencedFile1";
        this.createFile(this.getFilePath(FAILING_PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createFile(this.getFilePath(FAILING_PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(FAILING_PLUG_ID, publishedRefs, unpublishedRefs, true);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(FAILING_PLUG_ID), unreferencedFile1));
        assertTrue(this.fileExists(this.getFilePath(FAILING_PLUG_ID), unreferencedFile2));
        // exception is logged
        assertEquals(1, this.testAppender.getEvents(Level.ERROR).size());
    }

    /**
     * Test:
     * - First iplug fails, second works as expected
     * @throws Exception
     */
    @Test
    public void testIndependentIPlugs() throws Exception {

        // set up failing iplug

        // set up files
        String unreferencedFileA1 = "UnreferencedFileA1";
        this.createFile(this.getFilePath(FAILING_PLUG_ID), unreferencedFileA1, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefsA = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefsA = new ArrayList<FileReference>();
        this.setupFileReferences(FAILING_PLUG_ID, publishedRefsA, unpublishedRefsA, true);

        // set up working iplug

        // set up files
        String unreferencedFileB1 = "UnreferencedFileB1";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFileB1, DEFAULT_FILE_AGE);
        String unreferencedFileB2 = "UnreferencedFileB2";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFileB2, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefsB = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefsB = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefsB, unpublishedRefsB);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(FAILING_PLUG_ID), unreferencedFileA1));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFileB1));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFileB2));
        // exception is logged
        assertEquals(1, this.testAppender.getEvents(Level.ERROR).size());
    }

    /**
     * Test:
     * - Delete the unreferenced files
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // set up files
        String unreferencedFile1 = "UnreferencedFile1";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Ignore deleted files
     * @throws Exception
     */
    @Test
    public void testIgnoreDeleted() throws Exception {
        // set up files
        String deletedFile = "DeletedFile1";
        this.createDeletedFile(this.getFilePath(PLUG_ID), deletedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), deletedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Ignore references containing a link instead of a file
     * @throws Exception
     */
    @Test
    public void testIgnoreLinks() throws Exception {
        // NOTE: The test needs at least one file to make the job iterate the references

        // set up files
        String unreferencedFile1 = "UnreferencedFile1";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);

        // set up links
        String link1 = "//test.com/document.pdf";
        String link2 = "http://test.com/document.pdf";
        String link3 = "https://test.com/document.pdf";

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+link1, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+link2, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+link3, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Delete the unreferenced files from archive
     * @throws Exception
     */
    @Test
    public void testDeleteArchived() throws Exception {
        // set up files
        String unreferencedFile1 = "UnreferencedFile1";
        this.createArchivedFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createArchivedFile(this.getFilePath(PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep unreferenced files, if their age is smaller than the minimum age
     * @throws Exception
     */
    @Test
    public void testKeepRecent() throws Exception {
        // set up files
        String unreferencedFile1 = "UnreferencedFile1";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, JOB_MIN_FILE_AGE);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile2, JOB_MIN_FILE_AGE-1);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertFalse(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Archive a published file with expiry date in the past
     * @throws Exception
     */
    @Test
    public void testArchivePublishedExpired() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with expiry date today
     * @throws Exception
     */
    @Test
    public void testKeepPublishedSameDate() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate()
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with expiry date in the future
     * @throws Exception
     */
    @Test
    public void testKeepPublishedFuture() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with no expiry date
     * @throws Exception
     */
    @Test
    public void testKeepPublishedNoDate() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", null
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with expiry date in the future and in the past
     * @throws Exception
     */
    @Test
    public void testKeepPublishedFutureMultipleReferences() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep an unpublished file with expiry date in the future
     * @throws Exception
     */
    @Test
    public void testKeepUnpublishedFuture() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        unpublishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Restore a published, archived file with expiry date today
     * @throws Exception
     */
    @Test
    public void testRestorePublishedArchivedSameDate() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate()
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep a published, archived file with expiry date in the future
     * @throws Exception
     */
    @Test
    public void testRestorePublishedArchivedFuture() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep a published, archived file with no date
     * @throws Exception
     */
    @Test
    public void testRestorePublishedArchivedNoDate() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", null
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep an unpublished, archived file with no date
     * @throws Exception
     */
    @Test
    public void testRestoreUnpublishedArchivedNoDate() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        unpublishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", null
        ));
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Keep a published, archived file with expiry date in the past in the archive
     * @throws Exception
     */
    @Test
    public void testKeepPublishedArchivedExpired() throws Exception {
        // set up files
        String referencedFile = "ReferencedFile";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertTrue(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Test:
     * - Delete empty directories
     * @throws Exception
     */
    @Test
    public void testDeleteEmptyDirectory() throws Exception {
        // set up files
        String unreferencedFile = "UnreferencedFile";
        String unreferencedDir = Paths.get(this.getFilePath(PLUG_ID), "UnreferencedDir").toString();
        this.createFile(unreferencedDir, unreferencedFile, DEFAULT_FILE_AGE);
        String referencedFile = "ReferencedFile";
        String referencedDir = Paths.get(this.getFilePath(PLUG_ID), "ReferencedDir").toString();
        this.createFile(referencedDir, referencedFile, DEFAULT_FILE_AGE);

        // create special directories
        Files.createDirectories(Paths.get(DOCS_PATH.toString(), TRASH_PATH));
        Files.createDirectories(Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH));

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                referencedDir+"/"+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate()
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(referencedDir, referencedFile));
        assertTrue(this.dirExists(referencedDir));
        assertFalse(this.fileExists(unreferencedDir, unreferencedFile));
        assertFalse(this.dirExists(unreferencedDir));
        assertTrue(this.dirExists(TRASH_PATH));
        assertTrue(this.dirExists(ARCHIVE_PATH));
        assertFalse(this.testAppender.hasIssues());
    }

    /**
     * Helper methods
     */

    /**
     * Set up the catalog data for the given file references
     * @param plugId
     * @param publishedRefs
     * @param unpublishedRefs
     */
    private void setupFileReferences(String plugId, List<FileReference> publishedRefs, List<FileReference> unpublishedRefs) {
        this.setupFileReferences(plugId, publishedRefs, unpublishedRefs, false);
    }

    /**
     * Set up the catalog data for the given file references with optional null as query result
     * @param plugId
     * @param publishedRefs
     * @param unpublishedRefs
     * @param nullResults
     */
    private void setupFileReferences(String plugId, List<FileReference> publishedRefs, List<FileReference> unpublishedRefs, boolean nullResults) {
        List<List<FileReference>> allReferences = new ArrayList<List<FileReference>>();
        allReferences.add(publishedRefs);
        allReferences.add(unpublishedRefs);

        // create result lists
        int index = 0;
        for (List<FileReference> references : allReferences) {
            boolean isPublished = index == 0;

            List<IngridDocument> results = new ArrayList<IngridDocument>();
            for (int i=0, count=references.size(); i<count; i++) {
                FileReference reference = references.get(i);
                IngridDocument result = new IngridDocument();
                result.put("fdLink.data", reference.file);
                result.put("fdExpires.data", reference.expiryDate != null ? df.format(reference.expiryDate) : null);
                result.put("fdLink.parentFieldId", (long)i); // prevent npe
                result.put("fdLink.sort", i);
                results.add(result);
            }

            String publishedPattern = "oNode."+(isPublished ? "objIdPublished" : "objId")+ " = obj.id";

            // set up stub for file query
            when(this.mdekCallerQuery.queryHQLToMap(
                    Matchers.eq(plugId),
                    Matchers.matches("select fdLink.data.*"+publishedPattern+".*fdLink.fieldKey = 'link'.*"),
                    Matchers.eq(null),
                    Matchers.eq("")
            )).thenReturn(this.createResponse(nullResults ? null : results));

            // set up stub for expiry sub queries
            for (int i=0, count=references.size(); i<count; i++) {
                List<IngridDocument> subResults = Stream.of(results.get(i)).collect(Collectors.toList());
                when(this.mdekCallerQuery.queryHQLToMap(
                        Matchers.eq(plugId),
                        Matchers.matches("select fdExpires.data.*"+publishedPattern+".*fdExpires.sort = "+i+".*"),
                        Matchers.eq(null),
                        Matchers.eq("")
                )).thenReturn(this.createResponse(nullResults ? null : subResults));
            }
            index++;
        }
    }

    /**
     * Create the response for a call to IMdekCallerQuery.queryHQLToMap()
     * containing the given list of IngridDocument instances
     * @param results
     * @return IngridDocument
     */
    private IngridDocument createResponse(List<IngridDocument> results) {
        IngridDocument response = new IngridDocument();
        List<Pair> pairList = new ArrayList<Pair>();
        pairList.add(new Pair("ignore", response));
        response.put(IJobRepository.JOB_INVOKE_SUCCESS, true);
        response.put(IJobRepository.JOB_INVOKE_RESULTS, pairList);
        response.put(MdekKeys.OBJ_ENTITIES, results);
        return response;
    }

    /**
     * Set the age of a file
     * @param path
     * @param ageInSeconds
     * @throws IOException
     */
    private void setFileAge(Path path, long ageInSeconds) throws IOException {
        LocalDateTime fileTime = LocalDateTime.from(JOB_REFERENCE_TIME).minusSeconds(ageInSeconds);
        Files.setLastModifiedTime(path, FileTime.from(fileTime.atZone( TimeZone.getDefault().toZoneId() ).toInstant()));
    }

    /**
     * Get the path for a file
     * @param plugId
     * @return String
     */
    private String getFilePath(String plugId) {
        return plugId+"/"+OBJ_UUID;
    }

    /**
     * Create a test file
     * @param path
     * @param name
     * @param ageInSeconds
     * @throws IOException
     */
    private void createFile(String path, String name, long ageInSeconds) throws IOException {
        Path basePath = Paths.get(DOCS_PATH.toString(), path);
        this.createFileImpl(basePath, name, ageInSeconds);
        assertTrue(this.fileExists(path, name));
    }

    /**
     * Create an archived test file
     * @param path
     * @param name
     * @param ageInSeconds
     * @throws IOException
     */
    private void createArchivedFile(String path, String name, long ageInSeconds) throws IOException {
        Path basePath = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, path);
        this.createFileImpl(basePath, name, ageInSeconds);
        assertTrue(this.archivedFileExists(path, name));
    }

    /**
     * Create a deleted test file
     * @param path
     * @param name
     * @param ageInSeconds
     * @throws IOException
     */
    private void createDeletedFile(String path, String name, long ageInSeconds) throws IOException {
        Path basePath = Paths.get(DOCS_PATH.toString(), TRASH_PATH, path);
        this.createFileImpl(basePath, name, ageInSeconds);
        assertTrue(this.deletedFileExists(path, name));
    }

    /**
     * Actually create a file
     * @param path
     * @param name
     * @param ageInSeconds
     * @throws IOException
     */
    private void createFileImpl(Path path, String name, long ageInSeconds) throws IOException {
        Files.createDirectories(path);
        Path createdPath = Files.createFile(Paths.get(path.toString(), name));
        this.setFileAge(createdPath, ageInSeconds);
    }

    /**
     * Check existence of a test directory
     * @param path
     * @throws IOException
     */
    private boolean dirExists(String path) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), path));
    }

    /**
     * Check existence of a test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean fileExists(String path, String name) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), path, name));
    }

    /**
     * Check existence of an archived test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean archivedFileExists(String path, String name) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, path, name));
    }

    /**
     * Check existence of an deleted test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean deletedFileExists(String path, String name) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), TRASH_PATH, path, name));
    }
}
