/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
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

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Path DOCS_PATH = Paths.get("target", "ingrid-upload-test");
    private static final String OBJ_UUID = "5F1AF722-D767-4980-8403-A432173D5684";
    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";
    private static final String PLUG_ID = "test-plug-id";
    private static final LocalDate TEST_DATE = LocalDate.parse("25.04.2017", df);

	@Mock private ConnectionFacade connectionFacade;
    @Mock private MdekClientCaller mdekClientCaller;
    @Mock private MdekCallerQuery mdekCallerQuery;
    @Mock private JobExecutionContext context;

    private UploadCleanupJob job;
    private FileSystemStorage storage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // setup mdek client
        List<String> plugIds = Stream.of(PLUG_ID).collect(Collectors.toList());
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
        this.job.setReferenceDate(TEST_DATE);
    }

    @After
    public void tearDown() throws Exception {
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
        this.createFile(OBJ_UUID, unreferencedFile1);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createFile(OBJ_UUID, unreferencedFile2);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, unreferencedFile1));
        assertTrue(this.fileExists(OBJ_UUID, unreferencedFile2));
    }

    /**
     * Test:
     * - Simulate query returns no result, all files must remain
     * @throws Exception
     */
    @Test
    public void testQueryReturnsNoResults() throws Exception {

        // set up stub for file query
        when(this.mdekCallerQuery.queryHQLToMap(
                Matchers.eq(PLUG_ID),
                Matchers.matches("select fdLink.data, fdLink.parentFieldId, fdLink.sort.*"),
                Matchers.eq(null),
                Matchers.eq("")
        )).thenReturn(this.createResponse(null));

        // set up files
        String unreferencedFile1 = "UnreferencedFile1";
        this.createFile(OBJ_UUID, unreferencedFile1);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createFile(OBJ_UUID, unreferencedFile2);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs, true);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, unreferencedFile1));
        assertTrue(this.fileExists(OBJ_UUID, unreferencedFile2));
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
        this.createFile(OBJ_UUID, unreferencedFile1);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createFile(OBJ_UUID, unreferencedFile2);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(OBJ_UUID, unreferencedFile1));
        assertFalse(this.fileExists(OBJ_UUID, unreferencedFile2));
        assertTrue(this.deletedFileExists(OBJ_UUID, unreferencedFile1));
        assertTrue(this.deletedFileExists(OBJ_UUID, unreferencedFile2));
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
        this.createArchivedFile(OBJ_UUID, unreferencedFile1);
        String unreferencedFile2 = "UnreferencedFile2";
        this.createArchivedFile(OBJ_UUID, unreferencedFile2);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.archivedFileExists(OBJ_UUID, unreferencedFile1));
        assertFalse(this.archivedFileExists(OBJ_UUID, unreferencedFile2));
        assertTrue(this.deletedFileExists(OBJ_UUID, unreferencedFile1));
        assertTrue(this.deletedFileExists(OBJ_UUID, unreferencedFile2));
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
        this.createFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE.minusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.archivedFileExists(OBJ_UUID, referencedFile));
        assertFalse(this.fileExists(OBJ_UUID, referencedFile));
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
        this.createFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE.plusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", null
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE.minusDays(1)
        ));
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE.plusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        unpublishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE.plusDays(1)
        ));
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createArchivedFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createArchivedFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE.plusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createArchivedFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", null
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createArchivedFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        unpublishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", null
        ));
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(OBJ_UUID, referencedFile));
        assertFalse(this.archivedFileExists(OBJ_UUID, referencedFile));
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
        this.createArchivedFile(OBJ_UUID, referencedFile);

        // setup file references
        List<FileReference> publishedRefs = new ArrayList<FileReference>();
        publishedRefs.add(this.job.new FileReference(
                OBJ_UUID+"/"+referencedFile, "", TEST_DATE.minusDays(1)
        ));
        List<FileReference> unpublishedRefs = new ArrayList<FileReference>();
        this.setupFileReferences(publishedRefs, unpublishedRefs);

        // run job
        this.job.executeInternal(this.context);

        // test
        assertFalse(this.fileExists(OBJ_UUID, referencedFile));
        assertTrue(this.archivedFileExists(OBJ_UUID, referencedFile));
    }

    /**
     * Helper methods
     */

    private void setupFileReferences(List<FileReference> publishedRefs, List<FileReference> unpublishedRefs) {
        this.setupFileReferences(publishedRefs, unpublishedRefs, false);
    }

    /**
     * Set up the catalog data for the given file references
     * @param publishedRefs
     * @param unpublishedRefs
     */
    private void setupFileReferences(List<FileReference> publishedRefs, List<FileReference> unpublishedRefs, boolean nullResults) {
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
                    Matchers.eq(PLUG_ID),
                    Matchers.matches("select fdLink.data.*"+publishedPattern+".*fdLink.fieldKey = 'link'.*"),
                    Matchers.eq(null),
                    Matchers.eq("")
            )).thenReturn(this.createResponse(nullResults ? null :  results));

            // set up stub for expiry sub queries
            for (int i=0, count=references.size(); i<count; i++) {
                List<IngridDocument> subResults = Stream.of(results.get(i)).collect(Collectors.toList());
                when(this.mdekCallerQuery.queryHQLToMap(
                        Matchers.eq(PLUG_ID),
                        Matchers.matches("select fdExpires.data.*"+publishedPattern+".*fdExpires.sort = "+i+".*"),
                        Matchers.eq(null),
                        Matchers.eq("")
                )).thenReturn(this.createResponse(nullResults ? null :  subResults));
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
     * Create a test file
     * @param path
     * @param name
     * @throws IOException
     */
    private void createFile(String path, String name) throws IOException {
        Files.createDirectories(Paths.get(DOCS_PATH.toString(), path));
        Files.createFile(Paths.get(DOCS_PATH.toString(), path, name));
        assertTrue(this.fileExists(path, name));
    }

    /**
     * Create an archived test file
     * @param path
     * @param name
     * @throws IOException
     */
    private void createArchivedFile(String path, String name) throws IOException {
        Files.createDirectories(Paths.get(DOCS_PATH.toString(), path, ARCHIVE_PATH));
        Files.createFile(Paths.get(DOCS_PATH.toString(), path, ARCHIVE_PATH, name));
        assertTrue(this.archivedFileExists(path, name));
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
        return Files.exists(Paths.get(DOCS_PATH.toString(), path, ARCHIVE_PATH, name));
    }

    /**
     * Check existence of an deleted test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean deletedFileExists(String path, String name) throws IOException {
        return Files.exists(Paths.get(DOCS_PATH.toString(), path, TRASH_PATH, name));
    }
}
