/*-
 * **************************************************-
 * InGrid Portal MDEK Application
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
package de.ingrid.mdek.upload.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Storage defines the interface for classes, that are responsible for storing and retrieving files.
 */
public interface Storage {
    /**
     * List all files in a path
     *
     * @param path The path
     * @return String[]
     * @throws IOException
     */
    String[] list(String path) throws IOException;

    /**
     * Check if a file exists
     *
     * @param path The path
     * @param file The file
     * @return boolean
     */
    boolean exists(String path, String file);

    /**
     * Get information about a file
     *
     * @param path The path
     * @param file The file
     * @return StorageItem
     * @throws IOException
     */
    StorageItem getInfo(String path, String file) throws IOException;

    /**
     * Get the content of a file
     *
     * @param path The path
     * @param file The file
     * @return InputStream
     * @throws IOException
     */
    InputStream read(String path, String file) throws IOException;

    /**
     * Write data to a file in a path and extract archives contained in data
     *
     * @param path The path
     * @param file The file
     * @param data The data
     * @param size The size of the file in bytes (used to verify)
     * @param replace Boolean indicating whether to replace an existing file or not
     * @param extract Boolean indicating whether to extract archives or not
     * @return StorageItem[] The list of created files
     * @throws IOException
     */
    StorageItem[] write(String path, String file, InputStream data, Integer size, boolean replace, boolean extract) throws IOException;

    /**
     * Write a file part
     *
     * @param id The id of the file
     * @param index The index of the part
     * @param data The data
     * @param size The size of the part in bytes (used to verify)
     * @throws IOException
     */
    void writePart(String id, Integer index, InputStream data, Integer size) throws IOException;

    /**
     * Combine all parts belonging to an id to the final file
     *
     * @param path The path
     * @param file The file
     * @param id The id of the file
     * @param totalParts The number of parts
     * @param size The size of the file in bytes (used to verify)
     * @param replace Boolean indicating whether to replace an existing file or not
     * @param extract Boolean indicating whether to extract archives or not
     * @return StorageItem[] The list of created files
     * @throws IOException
     */
    StorageItem[] combineParts(String path, String file, String id, Integer totalParts, Integer size, boolean replace, boolean extract)
            throws IOException;

    /**
     * Delete a file
     *
     * @param path The path
     * @param file The file
     * @throws IOException
     */
    void delete(String path, String file) throws IOException;
}
