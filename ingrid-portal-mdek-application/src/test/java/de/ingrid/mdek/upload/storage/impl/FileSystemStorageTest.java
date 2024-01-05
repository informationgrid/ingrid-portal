/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
package de.ingrid.mdek.upload.storage.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import de.ingrid.mdek.upload.ValidationException;

public class FileSystemStorageTest {

    private static final Path DOCS_PATH = Paths.get("target", "ingrid-storage-test");
    private static final Path TEMP_PATH = Paths.get("target", "ingrid-storage-tmp");
    private static final String OBJ_UUID = "5F1AF722-D767-4980-8403-A432173D5684";
    private static final String OBJ_UUID_2 = "173D5684-4980-A432-D767-84035F1AF722";
    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";

    private static final String PLUG_ID = "test-plug-id";

    private static final FileSystemStorage storage = new FileSystemStorage();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // setup storage
        storage.setDocsDir(DOCS_PATH.toString());
        storage.setTempDir(TEMP_PATH.toString());
        storage.setValidators(Arrays.asList(new FileSystemStorage.NameValidator()));
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
        Files.createDirectories(DOCS_PATH);
        FileUtils.deleteDirectory(TEMP_PATH.toFile());
        Files.createDirectories(TEMP_PATH);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
        FileUtils.deleteDirectory(TEMP_PATH.toFile());
    }

    /**
     * Test:
     * - List files
     * @throws Exception
     */
    @Test
    void testList() throws Exception {
        for (int i = 0; i < 10; i++) {
            final String path = Paths.get(PLUG_ID, OBJ_UUID).toString();
            final String file = i + "-test.txt";
            this.storageWriteTestFile(path, file);
        }

        // test
        assertEquals(10, storage.list().length);
    }

    /**
     * Test:
     * - Read a file
     * @throws Exception
     */
    @Test
    void testRead() throws Exception {
        final String path = Paths.get(PLUG_ID, OBJ_UUID).toString();
        final String file = "test.txt";
        this.storageWriteTestFile(path, file);

        // test
        final StringWriter content = new StringWriter();
        try (InputStream data = storage.read(path, file)) {
            IOUtils.copy(data, content);
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
        assertEquals("123", content.toString());
    }

    /**
     * Test:
     * - Write a file
     * @throws Exception
     */
    @Test
    void testWrite() throws Exception {
        final String path = Paths.get(PLUG_ID, OBJ_UUID).toString();
        final String file = "test.txt";
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
    void testWriteZip() throws Exception {
        final String path = Paths.get(PLUG_ID, OBJ_UUID).toString();
        final String file = "test.zip";

        // create zip file
        FileSystemItem[] results;
        final File zipFile = this.createZipFile(file);
        try (final InputStream data = new FileInputStream(zipFile)) {
            results = storage.write(path, file, data, zipFile.length(), true, false);
        }

        // test
        assertEquals(1, results.length);
        assertEquals("test.zip", results[0].getFile());
        assertEquals(path, results[0].getPath());
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
    void testWriteAndExtractZip() throws Exception {
        final String path = Paths.get(PLUG_ID, OBJ_UUID).toString();
        final String file = "test.zip";

        // create zip file
        FileSystemItem[] results;
        final File zipFile = this.createZipFile(file);
        try (final InputStream data = new FileInputStream(zipFile)) {
            results = storage.write(path, file, data, zipFile.length(), true, true);
        }

        // test
        assertEquals(3, results.length);
        assertEquals("file2", results[2].getFile());
        assertEquals(Paths.get(path, "test", "dir1").toString(), results[2].getPath());
        assertEquals("file0", results[1].getFile());
        assertEquals(Paths.get(path, "test", "dir2").toString(), results[1].getPath());
        assertEquals("file1", results[0].getFile());
        assertEquals(Paths.get(path, "test").toString(), results[0].getPath());
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
    void testArchive() throws Exception {
        final String path = Paths.get("test-plug-id", OBJ_UUID).toString();
        final String file = "test.txt";
        this.storageWriteTestFile(path, file);
        storage.archive(path, file);

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
    void testRestore() throws Exception {
        final String path = Paths.get("test-plug-id", OBJ_UUID).toString();
        final String file = "test.txt";
        this.storageWriteTestFile(path, file);
        storage.archive(path, file);
        storage.restore(path, file);

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
    void testDelete() throws Exception {
        final String path = Paths.get("test-plug-id", OBJ_UUID).toString();
        final String file = "test.txt";
        this.storageWriteTestFile(path, file);
        storage.delete(path, file);

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
    void testSanitizeIPlugId() throws Exception {
        org.junit.jupiter.api.Assumptions.assumeTrue(!isWindows());

        final String path = "illegal-plug-id:<5>/" + OBJ_UUID;
        final String file = "test.txt";
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
    void testFilenameConflictWithSpecialDirs() throws Exception {
        final String path = Paths.get(PLUG_ID, OBJ_UUID).toString();

        try {
            final String file = "_trash_";
            this.storageWriteTestFile(path, file);
            fail("Expected an ValidationException to be thrown");
        }
        catch (final ValidationException ex) {
            assertEquals("The file name containes the reserved name '_trash_'.", ex.getMessage());
        }

        try {
            final String file = "_archive_";
            this.storageWriteTestFile(path, file);
            fail("Expected an ValidationException to be thrown");
        }
        catch (final ValidationException ex) {
            assertEquals("The file name containes the reserved name '_archive_'.", ex.getMessage());
        }
    }

    /**
     * Test:
     * - Write a file with a name conflicting with a file in another document
     * @throws Exception
     */
    @Test
    void testFilenameConflictWithOtherDocument() throws Exception {
        final String path1 = Paths.get(PLUG_ID, OBJ_UUID).toString();
        final String path2 = Paths.get(PLUG_ID, OBJ_UUID_2).toString();
        final String file = "test.txt";
        this.storageWriteTestFile(path1, file, "123");
        this.storageWriteTestFile(path2, file, "234");

        // test
        // expectation: both files created with their unique content. no conflict
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path1, file)));
        assertTrue(Files.exists(Paths.get(DOCS_PATH.toString(), path2, file)));
        assertEquals("123", Files.readAllLines(Paths.get(DOCS_PATH.toString(), path1, file)).get(0));
        assertEquals("234", Files.readAllLines(Paths.get(DOCS_PATH.toString(), path2, file)).get(0));
    }

    /**
     * Write a test file using the storage
     * @param path
     * @param file
     * @return FileSystemItem
     * @throws Exception
     */
    private FileSystemItem storageWriteTestFile(final String path, final String file) throws Exception {
        return storageWriteTestFile(path, file, "123");
    }

    /**
     * Write a test file with content using the storage
     * @param path
     * @param file
     * @param content
     * @return FileSystemItem
     * @throws Exception
     */
    private FileSystemItem storageWriteTestFile(final String path, final String file, final String content) throws Exception {
        try (final InputStream data = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
            final FileSystemItem result = storage.write(path, file, data, (long)content.length(), true, false)[0];
            assertEquals(content, new String(Files.readAllBytes(result.getRealPath()), StandardCharsets.UTF_8));
            return result;
        }
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
    private File createZipFile(final String file) throws IOException {
        final File zipFile = Paths.get(TEMP_PATH.toString(), file).toFile();
        final FileOutputStream fos = new FileOutputStream(zipFile);
        final BufferedOutputStream bos = new BufferedOutputStream(fos);
        final ZipOutputStream zos = new ZipOutputStream(bos);
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

    private boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
