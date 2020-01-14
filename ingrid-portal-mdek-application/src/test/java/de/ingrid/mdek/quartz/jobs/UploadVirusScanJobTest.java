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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.quartz.JobExecutionContext;

import de.ingrid.mdek.upload.storage.validate.Validator;
import de.ingrid.mdek.upload.storage.validate.ValidatorFactory;
import de.ingrid.mdek.upload.storage.validate.impl.ExternalCommand;
import de.ingrid.mdek.upload.storage.validate.impl.RemoteServiceVirusScanValidator;
import de.ingrid.mdek.upload.storage.validate.impl.VirusScanValidator;

public class UploadVirusScanJobTest extends BaseJobTest {

    private static final Path DOCS_PATH = Paths.get("target", "ingrid-upload-test");
    private static final Path QUARANTINE_PATH = Paths.get("target", "ingrid-upload-quarantine");

    private static final String VIRUS_CONTENT = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

    private static final String VIRUSSCAN_VALIDATOR_NAME = "virusscan";
    private static final Map<String, String> VIRUSSCAN_VALIDATOR_CONFIG_LOCAL = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
    {
        put("command", "savscan -f -all -archive -mime %FILE%");
        put("virusPattern", "(?m)^>>> Virus '([^']+)' found in file (.+)$");
        put("cleanPattern", "(?m)^No viruses were discovered.$");
    }};
    private static final Map<String, String> VIRUSSCAN_VALIDATOR_CONFIG_REMOTE = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
    {
        put("url", "http://localhost:3000/v1/");
    }};

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @InjectMocks private static VirusScanValidator localVirusScanValidator;
    @Mock private ExternalCommand scanner;

    @InjectMocks private static RemoteServiceVirusScanValidator remoteVirusScanValidator;
    @Mock CloseableHttpClient serviceClient;

    @Mock private ValidatorFactory validatorFactory;
    @Mock private JobExecutionContext context;

    private UploadVirusScanJob job;

    private class MockedServiceExecution implements Answer<CloseableHttpResponse> {
        private final int numFiles;
        private final Path[] infected;

        public MockedServiceExecution(final int numFiles, final Path... infected) {
            this.numFiles = numFiles;
            this.infected = infected;
        }

        @Override
        public CloseableHttpResponse answer(final InvocationOnMock invocation) throws Throwable {
            final CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);

            // status
            Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "OK!"));

            // response entity
            HttpEntity entity = Mockito.mock(HttpEntity.class);
            final Object[] args = invocation.getArguments();
            final String method = ((HttpUriRequest) args[0]).getMethod();
            String serviceResponse = "";
            if (HttpPost.METHOD_NAME.equals(method)) {
                // first post request (initiate)
                serviceResponse = getServiceResponse(false, numFiles, new Path[] {});
            }
            else {
                // second get request (finished)
                serviceResponse = getServiceResponse(true, numFiles, infected);
            }
            entity = EntityBuilder.create().setText(serviceResponse).build();
            Mockito.when(httpResponse.getEntity()).thenReturn(entity);

            return httpResponse;
        }
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);
        new FieldSetter(localVirusScanValidator, localVirusScanValidator.getClass().getDeclaredField("scanner")).set(scanner);
        new FieldSetter(remoteVirusScanValidator, remoteVirusScanValidator.getClass().getDeclaredField("serviceClient")).set(serviceClient);

        localVirusScanValidator.initialize(VIRUSSCAN_VALIDATOR_CONFIG_LOCAL);
        remoteVirusScanValidator.initialize(VIRUSSCAN_VALIDATOR_CONFIG_REMOTE);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        FileUtils.deleteDirectory(DOCS_PATH.toFile());
    }

    @Override
    protected Class<?> getJobClassUnderTest() {
        return UploadVirusScanJob.class;
    }

    /**
     * Test:
     * - Local scanner setup, no virus contained in files, all files stay in place
     * @throws Exception
     */
    @Test
    public void testLocalNoVirusFound() throws Exception {
        // set up job
        setupJob(localVirusScanValidator);

        // set up files
        final Path file1 = this.createFile(Paths.get("dir1", "File1"), "");
        final Path file2 = this.createFile(Paths.get("dir 2", "File2Ä"), "");
        final Path file3 = this.createFile(Paths.get("dir 2", "File,3Ö"), "");

        // set up scan report expectations
        final String scanCommand = VIRUSSCAN_VALIDATOR_CONFIG_LOCAL.get("command").replace("%FILE%", DOCS_PATH.toString());
        when(this.scanner.execute(scanCommand)).thenReturn(this.getScanReportClean(3));

        // run job
        this.job.executeInternal(this.context);

        // test
        Mockito.verify(this.scanner, times(1)).execute(any());

        assertTrue(this.fileExists(file1));
        assertTrue(this.fileExists(file2));
        assertTrue(this.fileExists(file3));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Local scanner setup, virus contained in files, must be moved to quarantine
     *
     * CAUTION: This test will create a local file containing the test virus string.
     *          If you are running a On-Access scanner that prevents creating this file,
     *          the test will fail.
     *
     * @throws Exception
     */
    @Test
    public void testLocalVirusFound() throws Exception {
        // set up job
        setupJob(localVirusScanValidator);

        // set up files
        final Path file1 = this.createFile(Paths.get("dir1", "File1"), "");
        final Path file2 = this.createFile(Paths.get("dir 2", "File2Ä"), VIRUS_CONTENT);
        final Path file3 = this.createFile(Paths.get("dir 2", "File,3Ö"), VIRUS_CONTENT);

        // set up scan report expectations
        final String scanCommand = VIRUSSCAN_VALIDATOR_CONFIG_LOCAL.get("command").replace("%FILE%", DOCS_PATH.toString());
        when(this.scanner.execute(scanCommand)).thenReturn(this.getScanReportInfected(3, file2, file3));

        // run job
        this.job.executeInternal(this.context);

        // test
        final Path file2InQuarantine = Paths.get(QUARANTINE_PATH.toString(), file2.toString());
        final Path file3InQuarantine = Paths.get(QUARANTINE_PATH.toString(), file3.toString());

        Mockito.verify(this.scanner, times(1)).execute(any());

        assertTrue(this.fileExists(file1));
        assertTrue(!this.fileExists(file2));
        assertTrue(this.fileExists(file2InQuarantine));
        assertTrue(!this.fileExists(file3));
        assertTrue(this.fileExists(file3InQuarantine));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Remote scanner setup, no virus contained in files, all files stay in place
     * @throws Exception
     */
    @Test
    public void testRemoteNoVirusFound() throws Exception {
        // set up job
        setupJob(remoteVirusScanValidator);

        // set up files
        final Path file1 = this.createFile(Paths.get("dir1", "File1"), "");
        final Path file2 = this.createFile(Paths.get("dir 2", "File2Ä"), "");
        final Path file3 = this.createFile(Paths.get("dir 2", "File,3Ö"), "");

        // set up scan response expectations
        Mockito.doAnswer(new MockedServiceExecution(3, new Path[] {})).when(serviceClient).execute(Mockito.any(HttpUriRequest.class));

        // run job
        this.job.executeInternal(this.context);

        // test
        assertTrue(this.fileExists(file1));
        assertTrue(this.fileExists(file2));
        assertTrue(this.fileExists(file3));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Test:
     * - Remote scanner setup, virus contained in files, must be moved to quarantine
     *
     * CAUTION: This test will create a local file containing the test virus string.
     *          If you are running a On-Access scanner that prevents creating this file,
     *          the test will fail.
     *
     * @throws Exception
     */
    @Test
    public void testRemoteVirusFound() throws Exception {
        // set up job
        setupJob(remoteVirusScanValidator);

        // set up files
        final Path file1 = this.createFile(Paths.get("dir1", "File1"), "");
        final Path file2 = this.createFile(Paths.get("dir 2", "File2Ä"), VIRUS_CONTENT);
        final Path file3 = this.createFile(Paths.get("dir 2", "File,3Ö"), VIRUS_CONTENT);

        // set up scan response expectations
        Mockito.doAnswer(new MockedServiceExecution(3, new Path[] {file2, file3})).when(serviceClient).execute(Mockito.any(HttpUriRequest.class));

        // run job
        this.job.executeInternal(this.context);

        // test
        final Path file2InQuarantine = Paths.get(QUARANTINE_PATH.toString(), file2.toString());
        final Path file3InQuarantine = Paths.get(QUARANTINE_PATH.toString(), file3.toString());

        assertTrue(this.fileExists(file1));
        assertTrue(!this.fileExists(file2));
        assertTrue(this.fileExists(file2InQuarantine));
        assertTrue(!this.fileExists(file3));
        assertTrue(this.fileExists(file3InQuarantine));
        assertFalse(this.getTestAppender().hasIssues());
    }

    /**
     * Helper methods
     */

    /**
     * Set up virus scan job with the given virus scan validator instance
     * @param validator
     */
    private void setupJob(final Validator validator) {
        // setup virus scan validation
        when(this.validatorFactory.getValidatorNames()).thenReturn(Stream.of(VIRUSSCAN_VALIDATOR_NAME).collect(Collectors.toSet()));
        when(this.validatorFactory.getValidator(VIRUSSCAN_VALIDATOR_NAME)).thenReturn(validator);

        // set up job
        this.job = new UploadVirusScanJob();
        this.job.setValidatorFactory(this.validatorFactory);
        this.job.setScanDirs(Stream.of(DOCS_PATH.toString()).collect(Collectors.toList()));
        this.job.setQuarantineDir(QUARANTINE_PATH.toString());
        // static method call on MdekEmailUtils cannot be mocked by mockito
        this.job.setEmailReports(false);
    }

    /**
     * Create a test file
     * @param path
     * @param content
     * @return Path
     * @throws IOException
     */
    private Path createFile(final Path path, final String content) throws IOException {
        final Path finalPath = Paths.get(DOCS_PATH.toString(), path.toString());
        Files.createDirectories(finalPath.getParent());
        Files.write(finalPath, content.getBytes());
        assertTrue(fileExists(finalPath));
        return finalPath;
    }

    /**
     * Check existence of a test file
     * @param path
     * @param name
     * @throws IOException
     */
    private boolean fileExists(final Path path) throws IOException {
        return Files.exists(path);
    }

    /**
     * Get the scan report in case of infection
     * @param numFiles
     * @param files
     * @return String
     */
    private String getScanReportInfected(final int numFiles, final Path... infected) {
        final StringJoiner joiner = new StringJoiner(System.getProperty("line.separator"));
        joiner.add("Full Scanning");
        joiner.add("");
        for (final Path file : infected) {
            joiner.add(">>> Virus 'EICAR-AV-Test' found in file "+file.toString());
        }
        joiner.add("");
        joiner.add(numFiles+" files scanned in 5 seconds.");
        joiner.add(infected.length+" viruses were discovered.");
        joiner.add(infected.length+" files out of "+numFiles+" were infected.");
        joiner.add("End of Scan.");
        return joiner.toString();
    }

    /**
     * Get the scan report in case of no infection
     * @param numFiles
     * @return String
     */
    private String getScanReportClean(final int numFiles) {
        return String.join(System.getProperty("line.separator"),
                "Full Scanning",
                "",
                "",
                "",
                numFiles+" files scanned in 5 seconds.",
                "No viruses were discovered.",
                "End of Scan."
        );
    }

    /**
     * Get the response of the remote service
     * @param finished
     * @param numFiles
     * @param infected
     * @return String
     */
    private String getServiceResponse(final boolean finished, final int numFiles, final Path... infected) {
        final boolean isInfected = infected.length > 0;
        final String id = "03970ddd-a8dd-4428-bbca-ee7578558ede";
        final String date = LocalDateTime.now().format(dateFormatter);
        final String result = finished ? (isInfected ? "infected" : "ok") : "undefined";
        final String report = (finished ? (isInfected ? getScanReportInfected(numFiles, infected) : getScanReportClean(numFiles)) : "").replace(System.getProperty("file.separator"), "/");

        final List<String> infectionDetails = new ArrayList<>();
        for (final Path infection : infected) {
            infectionDetails.add("{\"location\":\"" + infection.toString().replace(System.getProperty("file.separator"), "/") + "\",\"virus\":\"EICAR-AV-Test\"}");
        }
        final String infections = "[" +  (isInfected ? infectionDetails.stream().collect(Collectors.joining(",")) : "") + "]";
        return "{" + String.join(",",
                "\"id\":\"" + id + "\"",
                "\"date\":\"" + date + "\"",
                "\"resource\":\"" + "/"+DOCS_PATH.toString().replace("\\", "/") + "\"",
                "\"type\":\"async\"",
                "\"scan\":{\"status\":\"" + (finished ? "finished" : "running") + "\",\"result\":\"" + result + "\",\"report\":\"" + report.replace("\n", "\\n").replace("\r", "") + "\",\"infections\":" + infections + "},\"complete\":" + (finished ? "true" : "false")
        ) + "}";
    }
}
