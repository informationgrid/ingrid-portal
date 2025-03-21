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
package de.ingrid.mdek.upload.storage.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;

/**
 * FileSystemItem represents an item in the server file system
 */
public class FileSystemItem implements StorageItem {

    // separator used in URI paths
    public static final String URI_PATH_SEPARATOR = "/";

    private Storage storage;
    private String path;
    private String file;
    private String type;
    private long size;
    private LocalDateTime lastModifiedTime;
    private boolean isArchived;
    private Path realPath;

    /**
     * Constructor
     */
    public FileSystemItem() {}

    /**
     * Constructor
     * @param storage
     * @param path
     * @param file
     * @param type
     * @param size
     * @param lastModifiedTime
     * @param isArchived
     * @param realPath
     */
    public FileSystemItem(final Storage storage, final String path, final String file, final String type, final long size,
            final LocalDateTime lastModifiedTime, final boolean isArchived, final Path realPath) {
        this.storage = storage;
        this.path = path;
        this.file = file;
        this.type = type;
        this.size = size;
        this.lastModifiedTime = lastModifiedTime;
        this.isArchived = isArchived;
        this.realPath = realPath;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getFile() {
        return this.file;
    }

    @Override
    public String getUri() {
        final FileSystem fileSystem = FileSystems.getDefault();
        final String separator = fileSystem.getSeparator();

        String uri = fileSystem.getPath(this.path, this.file).toString().replace(separator, URI_PATH_SEPARATOR);
        try {
            uri = URLEncoder.encode(uri, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~")
                    .replaceAll("\\%2F", "/")
                    .replaceAll("\\%5C", "/");
        }
        catch (final UnsupportedEncodingException e) {}
        return uri;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public LocalDateTime getLastModifiedDate() {
        return this.lastModifiedTime;
    }

    @Override
    public boolean isArchived() {
        return this.isArchived;
    }

    @Override
    public String getNextName() {
        if (this.storage.exists(this.path, this.file)) {
            final List<String> parts = new LinkedList<>(Arrays.asList(this.file.split("\\.")));
            final String extension = parts.size() > 1 ? parts.remove(parts.size()-1) : "";
            final String filename = String.join(".", parts);
            String tmpFile = this.file;
            int i = 0;
            while (this.storage.exists(this.path, tmpFile)) {
                i++;
                tmpFile = filename + "-" + i;
                if (extension.length() > 0) {
                    tmpFile += "." + extension;
                }
            }
            return tmpFile;
        }
        return this.file;
    }

    /**
     * Get the real absolute path of the file in the file system
     * @return Path
     */
    public Path getRealPath() {
        return this.realPath;
    }
}
