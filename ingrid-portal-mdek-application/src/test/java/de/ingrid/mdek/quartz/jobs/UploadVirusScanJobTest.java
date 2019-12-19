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
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionContext;

import de.ingrid.mdek.upload.storage.validate.ValidatorFactory;
import de.ingrid.mdek.upload.storage.validate.impl.ExternalCommand;
import de.ingrid.mdek.upload.storage.validate.impl.VirusScanValidator;

public class UploadVirusScanJobTest extends BaseJobTest {

    private static final Path DOCS_PATH = Paths.get("target", "ingrid-upload-test");
    private static final Path QUARANTINE_PATH = Paths.get("target", "ingrid-upload-quarantine");

    private static final String VIRUS_CONTENT = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*";

    private static final String VIRUSSCAN_VALIDATOR_NAME = "virusscan";
    private static final Map<String, String> VIRUSSCAN_VALIDATOR_CONFIG = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
    {
        put("command", "savscan -f -archive %FILE%");
        put("virusPattern", "(?m)^>>> Virus '([^']+)' found in file (.+)$");
        put("cleanPattern", "(?m)^No viruses were discovered.$");
    }};

    @InjectMocks private VirusScanValidator virusScanValidator;
    @Mock private ExternalCommand scanner;
    @Mock private ValidatorFactory validatorFactory;
    @Mock private JobExecutionContext context;

    private UploadVirusScanJob job;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        // setup virus scan validation
        this.virusScanValidator.initialize(VIRUSSCAN_VALIDATOR_CONFIG);
        when(this.validatorFactory.getValidatorNames()).thenReturn(Stream.of(VIRUSSCAN_VALIDATOR_NAME).collect(Collectors.toSet()));
        when(this.validatorFactory.getValidator(VIRUSSCAN_VALIDATOR_NAME)).thenReturn(this.virusScanValidator);

        // set up job
        this.job = new UploadVirusScanJob();
        this.job.setValidatorFactory(this.validatorFactory);
        this.job.setScanDirs(Stream.of(DOCS_PATH.toString()).collect(Collectors.toList()));
        this.job.setQuarantineDir(QUARANTINE_PATH.toString());
        // static method call on MdekEmailUtils cannot be mocked by mockito
        this.job.setEmailReports(false);
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
     * - Virus contained in files, must be moved to quarantine
     * @throws Exception
     */
    @Test
    public void testVirusFound() throws Exception {
        // set up files
        final Path file1 = this.createFile(Paths.get("dir1", "File1"), "");
        final Path file2 = this.createFile(Paths.get("dir2", "File2"), VIRUS_CONTENT);
        final Path file3 = this.createFile(Paths.get("dir2", "File3"), VIRUS_CONTENT);

        // set up scan report expectations
        final String scanCommand = VIRUSSCAN_VALIDATOR_CONFIG.get("command").replace("%FILE%", DOCS_PATH.toString());
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
     * - No virus contained in files, all files stay in place
     * @throws Exception
     */
    @Test
    public void testNoVirusFound() throws Exception {
        // set up files
        final Path file1 = this.createFile(Paths.get("dir1", "File1"), "");
        final Path file2 = this.createFile(Paths.get("dir2", "File2"), "");
        final Path file3 = this.createFile(Paths.get("dir2", "File3"), "");

        // set up scan report expectations
        final String scanCommand = VIRUSSCAN_VALIDATOR_CONFIG.get("command").replace("%FILE%", DOCS_PATH.toString());
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
     * Helper methods
     */

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
}
