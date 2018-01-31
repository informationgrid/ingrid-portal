/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.ingrid.mdek.upload.IllegalFileException;

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
     * - Write a zip file
     * @throws Exception
     */
    @Test
    public void testWriteZip() throws Exception {
        String path = PLUG_ID+"/"+OBJ_UUID;
        String file = "test.zip";

        // create zip file
        File zipFile = this.createZipFile(file);
        InputStream data = new FileInputStream(zipFile);

        FileSystemItem[] results = this.storage.write(path, file, data, (int)zipFile.length(), true, false);

        // test
        assertEquals(results.length, 1);
        assertEquals(results[0].getFile(), "test.zip");
        assertEquals(results[0].getPath(), path);
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, "test.zip")));
        assertFalse(Files.exists(Paths.get(DOCS_PATH.toString(), path, "test", "file1")));
        assertFalse(Files.exists(Paths.get(DOCS_PATH.toString(), path, "test", "dir1", "file2")));
    }

    /**
     * Test:
     * - Write and extract a zip file
     * @throws Exception
     */
    @Test
    public void testWriteAndExtractZip() throws Exception {
        String path = PLUG_ID+"/"+OBJ_UUID;
        String file = "test.zip";

        // create zip file
        File zipFile = this.createZipFile(file);
        InputStream data = new FileInputStream(zipFile);

        FileSystemItem[] results = this.storage.write(path, file, data, (int)zipFile.length(), true, true);

        // test
        assertEquals(results.length, 3);
        assertEquals(results[0].getFile(), "file2");
        assertEquals(results[0].getPath(), path+"/test/dir1");
        assertEquals(results[1].getFile(), "file0");
        assertEquals(results[1].getPath(), path+"/test/dir2");
        assertEquals(results[2].getFile(), "file1");
        assertEquals(results[2].getPath(), path+"/test");
        assertFalse(Files.exists(Paths.get(DOCS_PATH.toString(), path, "test.zip")));
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, "test", "file1")));
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, "test", "dir1", "file2")));
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path, "test", "dir2", "file0")));
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
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, path, file)));
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
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), TRASH_PATH, path, file)));
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
     * Test:
     * - Write a file with a name conflicting with one of the special directories
     * @throws Exception
     */
    @Test
    public void testFilenameConflictWithSpecialDirs() throws Exception {
        String path = PLUG_ID+"/"+OBJ_UUID;

        try {
            String file = "_trash_";
            this.storageWriteTestFile(path, file);
            fail("Expected an IllegalFileException to be thrown");
        }
        catch (IllegalFileException ex) {
            assertEquals(ex.getMessage(), "The file name is invalid.");
        }

        try {
            String file = "_archive_";
            this.storageWriteTestFile(path, file);
            fail("Expected an IllegalFileException to be thrown");
        }
        catch (IllegalFileException ex) {
            assertEquals(ex.getMessage(), "The file name is invalid.");
        }
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

    /**
     * Create a zip file with the following structure:
     * name
     * +- file1
     * +- dir1
     *    +- file2
     * @param file
     * @throws IOException
     * @return File
     */
    private File createZipFile(String file) throws IOException {
        File zipFile = Paths.get(DOCS_PATH.toString(), file).toFile();
        FileOutputStream fos = new FileOutputStream(zipFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ZipOutputStream zos = new ZipOutputStream(bos);
        try {
            zos.putNextEntry(new ZipEntry("file1"));
            zos.putNextEntry(new ZipEntry("dir2/file0"));
            zos.putNextEntry(new ZipEntry("dir1/file2"));
            zos.closeEntry();
        }
        finally {
            zos.close();
        }
        return zipFile;
    }
}
