/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.mdek.quartz.jobs;

import de.ingrid.mdek.upload.storage.validate.Validator;
import de.ingrid.mdek.upload.storage.validate.ValidatorFactory;
import de.ingrid.mdek.upload.storage.validate.VirusFoundException;
import de.ingrid.mdek.upload.storage.validate.VirusScanException;
import de.ingrid.mdek.util.MdekEmailUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class UploadVirusScanJob extends QuartzJobBean {

    private static final String EMAIL_REPORT_SUBJECT = "[IGE] Virus Scan Report";
    private static final String EMAIL_ERROR_REPORT_SUBJECT = EMAIL_REPORT_SUBJECT + " ERROR";
    private static final String VIRUSSCAN_VALIDATOR_NAME = "virusscan";

    private static final Logger log = LogManager.getLogger(UploadVirusScanJob.class);

    private Validator virusScanValidator;
    private List<String> scanDirs;
    private String quarantineDir = null;
    private Report report = null;
    private boolean sendReportEmails = true;
    private EmailService emailService = new EmailServiceImpl();

    static class Report {
        private final StringBuilder content = new StringBuilder();

        public void add(final String entry) {
            content.append(entry).append(System.lineSeparator());
        }

        public String getContent() {
            return content.toString();
        }
    }

    /**
     * Encapsulates email functions for better testability
     */
    interface EmailService {
        void sendReport(String subject, Report report);
    }

    private class EmailServiceImpl implements EmailService {
        @Override
        public void sendReport(final String subject, final Report report) {
        	MdekEmailUtils.sendSystemEmail(subject, report.getContent());
        }
    }

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
     * @param scanDirs
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

    /**
     * Enable/disable email reports.
     * @param enabled
     */
    public void setEmailReports(final boolean enabled) {
        this.sendReportEmails = enabled;
    }

    /**
     * Set the email service.
     * Defaults to instance of EmailServiceImpl if not set explicitly.
     * @param emailService
     */
    public void setEmailService(final EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    protected void executeInternal(final JobExecutionContext ctx) throws JobExecutionException {
        report = new Report();

        log(Level.INFO, "Executing UploadVirusScanJob...", null);
        log(Level.INFO, "Directories to scan: "+String.join(", ", scanDirs), null);

        final List<Path> infectedFiles = new ArrayList<>();
        final List<Exception> exceptions = new ArrayList<>();
        try {
            // scan files
            for (final String scanDir : scanDirs) {
                log(Level.DEBUG, "Scanning directory \""+scanDir+"\"...", null);
                try {
                    virusScanValidator.validate(scanDir, null, 0L, Paths.get(scanDir), false);
                }
                catch (final VirusFoundException vfex) {
                    for (final Path file : vfex.getInfections().keySet()) {
                        infectedFiles.add(file);
                        // print infection information
                        log(Level.INFO, "Infection found: " + file.toString() + " - " + vfex.getInfections().get(file).toString(), null);
                    }
                    // add scan log in case of virus
                    report.add(vfex.getScanReport());
                }
                catch (final VirusScanException vscanex) {
                    String scanReport = vscanex.getScanReport();
                    log(Level.WARN, "Error(s) found during the scan: " + scanReport, null);
                    exceptions.add(vscanex);
                }
                catch (final Exception ex) {
                    log(Level.ERROR, "Error scanning directory \""+scanDir+"\"", ex);
                    exceptions.add(ex);
                }
            }
            log(Level.INFO, "Found "+infectedFiles.size()+" infected file(s)", null);

            // cleanup (only if virus are found)
            if (!infectedFiles.isEmpty()){
                this.doCleanup(infectedFiles);
            }

            log(Level.INFO, "Finished UploadVirusScanJob", null);
        }
        catch (final Exception ex) {
            log(Level.ERROR, "Error while running UploadVirusScanJob", ex);
            log(Level.INFO, "Aborted UploadVirusScanJob", null);
        }

        if (this.sendReportEmails && (!infectedFiles.isEmpty() || !exceptions.isEmpty())) {
            final String subject = !exceptions.isEmpty() ? EMAIL_ERROR_REPORT_SUBJECT : EMAIL_REPORT_SUBJECT;
            this.emailService.sendReport(subject, report);
        }
    }

    /**
     * Cleanup
     * @param infectedFiles
     */
    private void doCleanup(final List<Path> infectedFiles) {
        // move infected files into quarantine
        log(Level.INFO, "Moving infected file(s)...", null);
        int movedCount = 0;
        for (final Path file : infectedFiles) {
            final Path targetPath = Paths.get(quarantineDir, file.toString());
            log(Level.INFO, "Moving file: \""+file+"\" to \""+targetPath+"\"", null);
            try {
                Files.createDirectories(targetPath.getParent());
                Files.move(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                movedCount++;
            }
            catch (final IOException e) {
                // log error, but keep the job running
                log(Level.ERROR, "File \""+file+"\" could not be moved to quarantine", e);
            }
        }
        log(Level.INFO, "Moved "+movedCount+" infected file(s)", null);
    }

    /**
     * Add a message to the log and report
     * @param level
     * @param message
     * @param t
     */
    private void log(final Level level, final String message, final Throwable t) {
        log.log(level, message, t);

        // add to report
        if (level.isMoreSpecificThan(Level.INFO)) {
            report.add(message);
            if (t != null) {
                report.add(t.getMessage());
            }
        }
    }
}
