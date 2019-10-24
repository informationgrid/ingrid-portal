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
package de.ingrid.mdek.upload.storage.validate;

import java.nio.file.Path;
import java.util.Map;

import de.ingrid.mdek.upload.ValidationException;

/**
 * Validator instances are used to validate files before adding them to the storage
 */
public interface Validator {
    /**
     * Initialization method called with configuration values
     * @param properties
     * @throws IllegalArgumentException
     */
    void initialize(Map<String, String> configuration) throws IllegalArgumentException;

    /**
     * Validate the path, file and data
     * NOTE: Since this method could be called in different stages of the upload process one or more parameters might be null.
     * E.g. in an early stage only the file path and name could be validated while the file data is still unknown.
     * @param path
     * @param file
     * @param data
     */
    void validate(final String path, final String file, Path data) throws ValidationException;
}
