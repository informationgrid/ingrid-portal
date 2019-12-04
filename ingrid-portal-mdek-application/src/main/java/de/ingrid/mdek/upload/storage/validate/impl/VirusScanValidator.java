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
package de.ingrid.mdek.upload.storage.validate.impl;

import java.nio.file.Path;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.ingrid.mdek.upload.ValidationException;
import de.ingrid.mdek.upload.storage.validate.Validator;
import de.ingrid.mdek.upload.storage.validate.VirusFoundException;

/**
 * Validator implementation that runs a virus scan on the file.
 *
 * The implementation uses an external command to run the virus scan.
 *
 * Required configuration
 *   - command: External command to be executed. The string *must* contain a %FILE% parameter that will be replaced with the file to scan.
 *   - infectedPattern: Regular expression pattern applied to the command result that matches, if a virus infection is found
 *   - cleanPattern: Regular expression pattern applied to the command result that matches, if no virus infection is found
 *
 *   NOTE: If neither infectedPattern nor cleanPattern match, an error is assumed and will be logged. Validation does NOT fail in this case.
 */
public class VirusScanValidator implements Validator {

    private static final String CONFIG_KEY_COMMAND          = "command";
    private static final String CONFIG_KEY_INFECTED_PATTERN = "infectedPattern";
    private static final String CONFIG_KEY_CLEAN_PATTERN    = "cleanPattern";

    private static final String PLACEHOLDER_FILE = "%FILE%";

    private String command;
    private Pattern infectedPattern;
    private Pattern cleanPattern;

    private ExternalCommand scanner = new ExternalCommand();

    private static final Logger log = LogManager.getLogger(VirusScanValidator.class);

    @Override
    public void initialize(final Map<String, String> configuration) throws IllegalArgumentException {
        // check required configuration parameters
        for (final String parameter : new String[] {CONFIG_KEY_COMMAND, CONFIG_KEY_INFECTED_PATTERN, CONFIG_KEY_CLEAN_PATTERN}) {
            if (!configuration.containsKey(parameter)) {
                throw new IllegalArgumentException("Configuration value '"+parameter+"' is required.");
            }
        }

        // command parameter
        final String command = configuration.get(CONFIG_KEY_COMMAND);
        if (!command.contains(PLACEHOLDER_FILE)) {
            throw new IllegalArgumentException("Configuration value 'command' *must* contain a '+PLACEHOLDER_FILE+' substring. The configuration value is: '"+command+"'.");
        }
        this.command = command;

        // pattern parameters
        final String infectedPattern = configuration.get(CONFIG_KEY_INFECTED_PATTERN);
        this.infectedPattern = Pattern.compile(".*"+infectedPattern+".*", Pattern.DOTALL);
        final String cleanPattern = configuration.get(CONFIG_KEY_CLEAN_PATTERN);
        this.cleanPattern = Pattern.compile(".*"+cleanPattern+".*", Pattern.DOTALL);
    }

    @Override
    public void validate(final String path, final String file, final Path data) throws ValidationException {
        if (data == null) {
            return;
        }
        try {
            // scan file
            final String result = runScan(data);

            // analyze result
            if (infectedPattern.matcher(result).matches()) {
                log.warn("Virus found: "+result);
                throw new VirusFoundException("Virus found.", path+"/"+file);
            }
            else if (!cleanPattern.matcher(result).matches()) {
                log.error("Virus scan failed: "+result);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Scan result: " + result);
                }
            }
        }
        catch (final CommandExecutionException e) {
            log.error("Virus scan failed: ", e);
        }
    }

    /**
     * Set the scanner
     * @param scanner
     */
    public void setScanner(final ExternalCommand scanner) {
        this.scanner = scanner;
    }

    /**
     * Scan the given file
     * @param file
     * @return String
     * @throws CommandExecutionException
     */
    private String runScan(final Path file) throws CommandExecutionException {
        final String command = this.command.replace(PLACEHOLDER_FILE, file.toString());
        if (log.isDebugEnabled()) {
            log.debug("Run command: " + command);
        }
        return scanner.execute(command);
    }
}
