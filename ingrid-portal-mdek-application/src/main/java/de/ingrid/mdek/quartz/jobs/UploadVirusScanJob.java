/*-
 * **************************************************-
 * InGrid Portal MDEK Application
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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import de.ingrid.mdek.upload.storage.validate.Validator;
import de.ingrid.mdek.upload.storage.validate.ValidatorFactory;
import de.ingrid.mdek.upload.storage.validate.VirusFoundException;

public class UploadVirusScanJob extends QuartzJobBean {

    private static final String VIRUSSCAN_VALIDATOR_NAME = "virusscan";

    private static final Logger log = LogManager.getLogger(UploadVirusScanJob.class);

    private Validator virusScanValidator;
    private List<String> scanDirs;
    private String quarantineDir = null;

    /**
     * Set the validator factory.
     * @param validatorFactory
     */
    public void setValidatorFactory(final ValidatorFactory validatorFactory) {
        if (validatorFactory == null || !validatorFactory.getValidatorNames().contains(VIRUSSCAN_VALIDATOR_NAME)) {
            throw new IllegalArgumentException("The provided validator factory does not contain a validator with name "+VIRUSSCAN_VALIDATOR_NAME);
        }
        virusScanValidator = validatorFactory.getValidator(VIRUSSCAN_VALIDATOR_NAME);
    }

    /**
     * Set the directories to scan.
     * @param storage
     */
    public void setScanDirs(final List<String> scanDirs) {
        this.scanDirs = scanDirs;
    }

    /**
     * Set the directory for storing infected files.
     * @param quarantineDir
     */
    public void setQuarantineDir(final String quarantineDir) {
        this.quarantineDir = quarantineDir;
    }

    @Override
    protected void executeInternal(final JobExecutionContext ctx) throws JobExecutionException {
        log.info("Executing UploadVirusScanJob...");
        log.info("Directories to scan: "+String.join(", ", scanDirs));

        try {
            // scan files
            final List<Path> infectedFiles = new ArrayList<>();
            for (final String scanDir : scanDirs) {
                log.debug("Scanning directory \""+scanDir+"\"...");
                Files.walkFileTree(Paths.get(scanDir), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                        if (Files.isDirectory(file)) {
                            log.debug("Scanning directory \""+file+"\"...");
                        }
                        else {
                            log.debug("Testing file \""+file+"\"...");
                            try {
                                virusScanValidator.validate(file.getParent().toString(), file.getFileName().toString(), file);
                            }
                            catch (final VirusFoundException vfex) {
                                infectedFiles.add(file);
                            }
                            catch (final Exception ex) {
                                log.error("Error scanning file \""+file+"\"", ex);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            log.info("Found "+infectedFiles.size()+" infected file(s)");

            // cleanup
            this.doCleanup(infectedFiles);
        }
        catch (final Exception ex) {
            this.logError(ex.toString(), ex);
            log.info("Aborted UploadVirusScanJob");
            return;
        }

        log.info("Finished UploadVirusScanJob");
    }

    /**
     * Cleanup
     * @param infectedFiles
     */
    private void doCleanup(final List<Path> infectedFiles) {
        // move infected files into quarantine
        log.info("Moving infected file(s)...");
        int movedCount = 0;
        for (final Path file : infectedFiles) {
            final Path targetPath = Paths.get(quarantineDir, file.toString());
            log.info("Moving file: \""+file+"\" to \""+targetPath+"\"");
            try {
                Files.createDirectories(targetPath.getParent());
                Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                movedCount++;
            }
            catch (final IOException e) {
                // log error, but keep the job running
                this.logError("File \""+file+"\" could not be moved to quarantine", e);
            }
        }
        log.info("Moved "+movedCount+" infected file(s)");
    }

    /**
     * Log an error
     * @param error
     * @param t
     */
    private void logError(final String error, final Throwable t) {
        log.error("Error while running UploadVirusScanJob: "+error, t);
    }
}
