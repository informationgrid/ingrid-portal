/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
import org.apache.logging.log4j.Level;
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

public class UploadCleanupJobTest extends BaseJobTest {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String PATH_SEPARATOR = UploadCleanupJob.PATH_SEPARATOR;

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

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        // setup mdek client
        final List<String> plugIds = Stream.of(FAILING_PLUG_ID, PLUG_ID).collect(Collectors.toList());
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
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        FileUtils.deleteDirectory(DOCS_PATH.toFile());
    }

    @Override
    protected Class<?> getJobClassUnderTest() {
        return UploadCleanupJob.class;
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
        final String unreferencedFile1 = "Unreferenced File1 Ä";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        final String unreferencedFile2 = "Unreferenced File2 Ö";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        // exception is logged
        assertEquals(1, this.getTestAppender().getEvents(Level.ERROR).size());
    }

    /**
     * Test:
     * - Simulate query returns no result, all files must remain
     * @throws Exception
     */
    @Test
    public void testQueryReturnsNoResults() throws Exception {

        // set up files
        final String unreferencedFile1 = "Unreferenced File1 Ä";
        this.createFile(this.getFilePath(FAILING_PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        final String unreferencedFile2 = "Unreferenced File2 Ö";
        this.createFile(this.getFilePath(FAILING_PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(FAILING_PLUG_ID, publishedRefs, unpublishedRefs, true);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(FAILING_PLUG_ID), unreferencedFile1));
        assertTrue(this.fileExists(this.getFilePath(FAILING_PLUG_ID), unreferencedFile2));
        // exception is logged
        assertEquals(1, this.getTestAppender().getEvents(Level.ERROR).size());
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
        final String unreferencedFileA1 = "Unreferenced FileA1 Ä";
        this.createFile(this.getFilePath(FAILING_PLUG_ID), unreferencedFileA1, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefsA = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefsA = new ArrayList<FileReference>();
        this.setupFileReferences(FAILING_PLUG_ID, publishedRefsA, unpublishedRefsA, true);

        // set up working iplug

        // set up files
        final String unreferencedFileB1 = "Unreferenced FileB1 Ö";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFileB1, DEFAULT_FILE_AGE);
        final String unreferencedFileB2 = "Unreferenced FileB2 Ü";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFileB2, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefsB = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefsB = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefsB, unpublishedRefsB);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(FAILING_PLUG_ID), unreferencedFileA1));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFileB1));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFileB2));
        // exception is logged
        assertEquals(1, this.getTestAppender().getEvents(Level.ERROR).size());
    }

    /**
     * Test:
     * - Delete the unreferenced files
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // set up files
        final String unreferencedFile1 = "Unreferenced File1 Ä";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        final String unreferencedFile2 = "Unreferenced File2 Ö";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Ignore deleted files
     * @throws Exception
     */
    @Test
    public void testIgnoreDeleted() throws Exception {
        // set up files
        final String deletedFile = "Deleted File1 Ä";
        this.createDeletedFile(this.getFilePath(PLUG_ID), deletedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), deletedFile));
        assertFalse(this.getTestAppender().hasIssues());
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
        final String unreferencedFile1 = "Unreferenced File1 Ä";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);

        // set up links
        final String link1 = "//test.com/document.pdf";
        final String link2 = "http://test.com/document.pdf";
        final String link3 = "https://test.com/document.pdf";

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                link1, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        publishedRefs.add(this.job.new FileReference(
                link2, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        publishedRefs.add(this.job.new FileReference(
                link3, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Delete the unreferenced files from archive
     * @throws Exception
     */
    @Test
    public void testDeleteArchived() throws Exception {
        // set up files
        final String unreferencedFile1 = "Unreferenced File1 Ä";
        this.createArchivedFile(this.getFilePath(PLUG_ID), unreferencedFile1, DEFAULT_FILE_AGE);
        final String unreferencedFile2 = "Unreferenced File2 Ö";
        this.createArchivedFile(this.getFilePath(PLUG_ID), unreferencedFile2, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Keep unreferenced files, if their age is smaller than the minimum age
     * @throws Exception
     */
    @Test
    public void testKeepRecent() throws Exception {
        // set up files
        final String unreferencedFile1 = "Unreferenced File1 Ä";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile1, JOB_MIN_FILE_AGE);
        final String unreferencedFile2 = "Unreferenced File2 Ö";
        this.createFile(this.getFilePath(PLUG_ID), unreferencedFile2, JOB_MIN_FILE_AGE-1);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertTrue(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile1));
        assertFalse(this.deletedFileExists(this.getFilePath(PLUG_ID), unreferencedFile2));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Archive a published file with expiry date in the past
     * @throws Exception
     */
    @Test
    public void testArchivePublishedExpired() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with expiry date today
     * @throws Exception
     */
    @Test
    public void testKeepPublishedSameDate() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate()
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with expiry date in the future
     * @throws Exception
     */
    @Test
    public void testKeepPublishedFuture() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with no expiry date
     * @throws Exception
     */
    @Test
    public void testKeepPublishedNoDate() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", null
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Keep a published file with expiry date in the future and in the past
     * @throws Exception
     */
    @Test
    public void testKeepPublishedFutureMultipleReferences() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Keep an unpublished file with expiry date in the future
     * @throws Exception
     */
    @Test
    public void testKeepUnpublishedFuture() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        unpublishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Restore a published, archived file with expiry date today
     * @throws Exception
     */
    @Test
    public void testRestorePublishedArchivedSameDate() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate()
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Restore a published, archived file with expiry date in the future
     * @throws Exception
     */
    @Test
    public void testRestorePublishedArchivedFuture() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().plusDays(1)
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Restore a published, archived file with no date
     * @throws Exception
     */
    @Test
    public void testRestorePublishedArchivedNoDate() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", null
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Restore an unpublished, archived file with no date
     * @throws Exception
     */
    @Test
    public void testRestoreUnpublishedArchivedNoDate() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        unpublishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", null
        ));
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Keep a published, archived file with expiry date in the past in the archive
     * @throws Exception
     */
    @Test
    public void testKeepPublishedArchivedExpired() throws Exception {
        // set up files
        final String referencedFile = "Referenced File Ä";
        this.createArchivedFile(this.getFilePath(PLUG_ID), referencedFile, DEFAULT_FILE_AGE);

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                this.getFilePath(PLUG_ID)+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate().minusDays(1)
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(PLUG_ID, publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertTrue(this.archivedFileExists(this.getFilePath(PLUG_ID), referencedFile));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Delete empty directories
     * @throws Exception
     */
    @Test
    public void testDeleteEmptyDirectory() throws Exception {
        // set up files
        final String unreferencedFile = "Unreferenced File Ä";
        final String unreferencedDir = this.getFilePath(PLUG_ID)+PATH_SEPARATOR+"UnreferencedDir";
        this.createFile(unreferencedDir, unreferencedFile, DEFAULT_FILE_AGE);
        final String referencedFile = "Referenced File Ö";
        final String referencedDir = this.getFilePath(PLUG_ID)+PATH_SEPARATOR+"ReferencedDir";
        this.createFile(referencedDir, referencedFile, DEFAULT_FILE_AGE);

        // create special directories
        Files.createDirectories(Paths.get(DOCS_PATH.toString(), TRASH_PATH));
        Files.createDirectories(Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH));

        // setup file references
        final List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                referencedDir+PATH_SEPARATOR+referencedFile, "", JOB_REFERENCE_TIME.toLocalDate()
        ));
        final List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
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
        assertFalse(this.getTestAppender().hasIssues());
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
    private void setupFileReferences(final String plugId, final List<FileReference> publishedRefs, final List<FileReference> unpublishedRefs) {
        this.setupFileReferences(plugId, publishedRefs, unpublishedRefs, false);
    }

    /**
     * Set up the catalog data for the given file references with optional null as query result
     * @param plugId
     * @param publishedRefs
     * @param unpublishedRefs
     * @param nullResults
     */
    private void setupFileReferences(final String plugId, final List<FileReference> publishedRefs, final List<FileReference> unpublishedRefs, final boolean nullResults) {
        final List<List<FileReference>> allReferences = new ArrayList<List<FileReference>>();
        allReferences.add(publishedRefs);
        allReferences.add(unpublishedRefs);

        // create result lists
        int index = 0;
        for (final List<FileReference> references : allReferences) {
            final boolean isPublished = index == 0;

            final List<IngridDocument> results = new ArrayList<IngridDocument>();
            for (int i=0, count=references.size(); i<count; i++) {
                final FileReference reference = references.get(i);
                final IngridDocument result = new IngridDocument();
                result.put("fdLink.data", reference.file);
                result.put("fdExpires.data", reference.expiryDate != null ? df.format(reference.expiryDate) : null);
                result.put("fdLink.parentFieldId", (long)i); // prevent npe
                result.put("fdLink.sort", i);
                results.add(result);
            }

            final String publishedPattern = "oNode."+(isPublished ? "objIdPublished" : "objId")+ " = obj.id";

            // set up stub for file query
            when(this.mdekCallerQuery.queryHQLToMap(
                    Matchers.eq(plugId),
                    Matchers.matches("select fdLink.data.*"+publishedPattern+".*fdLink.fieldKey = 'link'.*"),
                    Matchers.eq(null),
                    Matchers.eq("")
            )).thenReturn(this.createResponse(nullResults ? null : results));

            // set up stub for expiry sub queries
            for (int i=0, count=references.size(); i<count; i++) {
                final List<IngridDocument> subResults = Stream.of(results.get(i)).collect(Collectors.toList());
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
    private IngridDocument createResponse(final List<IngridDocument> results) {
        final IngridDocument response = new IngridDocument();
        final List<Pair> pairList = new ArrayList<Pair>();
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
    private void setFileAge(final Path path, final long ageInSeconds) throws IOException {
        final LocalDateTime fileTime = LocalDateTime.from(JOB_REFERENCE_TIME).minusSeconds(ageInSeconds);
        Files.setLastModifiedTime(path, FileTime.from(fileTime.atZone( TimeZone.getDefault().toZoneId() ).toInstant()));
    }

    /**
     * Get the path for a file
     * @param plugId
     * @return String
     */
    private String getFilePath(final String plugId) {
        return plugId+PATH_SEPARATOR+OBJ_UUID;
    }

    /**
     * Create a test file
     * @param path
     * @param name
     * @param ageInSeconds
     * @throws IOException
     */
    private void createFile(final String path, final String name, final long ageInSeconds) throws IOException {
        final Path basePath = Paths.get(DOCS_PATH.toString(), path);
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
    private void createArchivedFile(final String path, final String name, final long ageInSeconds) throws IOException {
        final Path basePath = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, path);
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
    private void createDeletedFile(final String path, final String name, final long ageInSeconds) throws IOException {
        final Path basePath = Paths.get(DOCS_PATH.toString(), TRASH_PATH, path);
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
    private void createFileImpl(final Path path, final String name, final long ageInSeconds) throws IOException {
        Files.createDirectories(path);
        final Path createdPath = Files.createFile(Paths.get(path.toString(), name));
        this.setFileAge(createdPath, ageInSeconds);
    }

    /**
     * Check existence of a test directory
     * @param path
     * @throws IOException
     */
    private boolean dirExists(final String path) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), path));
    }

    /**
     * Check existence of a test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean fileExists(final String path, final String name) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), path, name));
    }

    /**
     * Check existence of an archived test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean archivedFileExists(final String path, final String name) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, path, name));
    }

    /**
     * Check existence of an deleted test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean deletedFileExists(final String path, final String name) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), TRASH_PATH, path, name));
    }
}
