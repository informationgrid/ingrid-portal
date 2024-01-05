/*-
 * **************************************************-
 * InGrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
package de.ingrid.mdek.upload.storage.validate;

import de.ingrid.mdek.upload.ValidationException;

import java.nio.file.Path;
import java.util.Map;

/**
 * when an error -that do not throw an exception- is found during a scan
 * record all logs output of the scan
 */
public class VirusScanException extends ValidationException {
    private static final int STATUS_CODE = 419;

    private static final long serialVersionUID = 1L;
    private static final String SCAN_EXCEPTION_KEY = "scanReport";

    public VirusScanException(final String message, final String file, final String scanReport) {
        super(message, file, STATUS_CODE);
        this.data.put(SCAN_EXCEPTION_KEY, scanReport);
    }

    /**
     * Get the whole log returned by the virus scan
     * @return String
     */
    @SuppressWarnings("unchecked")
    public String getScanReport() {
        return (String)this.data.get(SCAN_EXCEPTION_KEY);
    }
}
