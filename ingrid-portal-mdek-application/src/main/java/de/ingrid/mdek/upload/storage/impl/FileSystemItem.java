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
package de.ingrid.mdek.upload.storage.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;

/**
 * FileSystemItem represents an item in the server file system
 */
public class FileSystemItem implements StorageItem {

    private Storage storage;
    private String path;
    private String file;
    private String type;
    private long size;

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
     */
    public FileSystemItem(Storage storage, String path, String file, String type, long size) {
        this.storage = storage;
        this.path = path;
        this.file = file;
        this.type = type;
        this.size = size;
    }

    @Override
    public String getUri() {
        String uri = Paths.get(this.path, this.file).toString();
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
        catch (UnsupportedEncodingException e) {}
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
    public String getNextName() {
        if (this.storage.exists(this.path, this.file)) {
            List<String> parts = new LinkedList<String>(Arrays.asList(this.file.split("\\.")));
            String extension = parts.size() > 1 ? parts.remove(parts.size()-1) : "";
            String filename = String.join(".", parts);
            String file = this.file;
            int i = 0;
            while (this.storage.exists(this.path, file)) {
                i++;
                file = filename + "-" + i;
                if (extension.length() > 0) {
                    file += "." + extension;
                }
            }
            return file;
        }
        return this.file;
    }
}
