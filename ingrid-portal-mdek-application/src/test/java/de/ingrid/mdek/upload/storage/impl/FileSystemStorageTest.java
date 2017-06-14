/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class FileSystemStorageTest {

    private static final Path DOCS_PATH = Paths.get("target", "ingrid-storage-test");
    private static final String OBJ_UUID = "5F1AF722-D767-4980-8403-A432173D5684";
    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";

    private static final String PLUG_ID = "test-plug-id";

    private FileSystemStorage storage;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // setup storage
        this.storage = new FileSystemStorage();
        this.storage.setDocsDir(DOCS_PATH.toString());
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
        Files.createDirectories(DOCS_PATH);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
    }

    /**
     * Test:
     * - Write a file
     * @throws Exception
     */
    @Test
    public void testWrite() throws Exception {
        String path = PLUG_ID+"/"+OBJ_UUID;
        String file = "test.txt";
        this.storageWriteTestFile(path, file);

        // test
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, file)));
    }

    /**
     * Test:
     * - Archive a file
     * @throws Exception
     */
    @Test
    public void testArchive() throws Exception {
        String path = "test-plug-id/"+OBJ_UUID;
        String file = "test.txt";
        this.storageWriteTestFile(path, file);
        this.storage.archive(path, file);

        // test
        assertFalse(Files.exists(Paths.get(DOCS_PATH.toString(), path, file)));
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, ARCHIVE_PATH, file)));
    }

    /**
     * Test:
     * - Restore an archived file
     * @throws Exception
     */
    @Test
    public void testRestore() throws Exception {
        String path = "test-plug-id/"+OBJ_UUID;
        String file = "test.txt";
        this.storageWriteTestFile(path, file);
        this.storage.archive(path, file);
        this.storage.restore(path, file);

        // test
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, file)));
        assertFalse(Files.exists(Paths.get(DOCS_PATH.toString(), path, ARCHIVE_PATH, file)));
    }

    /**
     * Test:
     * - Delete a file
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        String path = "test-plug-id/"+OBJ_UUID;
        String file = "test.txt";
        this.storageWriteTestFile(path, file);
        this.storage.delete(path, file);

        // test
        assertFalse(Files.exists(Paths.get(DOCS_PATH.toString(), path, file)));
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, TRASH_PATH, file)));
    }

    /**
     * Test:
     * - Sanitize the iPlug id
     * @throws Exception
     */
    @Test
    public void testSanitizeIPlugId() throws Exception {
        String path = "illegal-plug-id:<5>/"+OBJ_UUID;
        String file = "test.txt";
        this.storageWriteTestFile(path, file);

        // test
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), "illegal-plug-id__5_/", OBJ_UUID, file)));
    }

    /**
     * Write a test file using the storage
     * @param path
     * @param file
     * @return FileSystemItem
     * @throws Exception
     */
    private FileSystemItem storageWriteTestFile(String path, String file) throws Exception {
        String content = "123";

        InputStream data = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        FileSystemItem result = this.storage.write(path, file, data, content.length(), true, false)[0];
        assertTrue(content.equals(new String(Files.readAllBytes(result.getRealPath()), StandardCharsets.UTF_8)));
        return result;
    }
}
