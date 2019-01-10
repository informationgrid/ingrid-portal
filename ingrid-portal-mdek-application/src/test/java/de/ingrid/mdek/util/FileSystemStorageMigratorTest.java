/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
package de.ingrid.mdek.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileSystemStorageMigratorTest {

    private static final Path DOCS_PATH = Paths.get("target", "ingrid-storage-migration-test");
    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";

    @Before
    public void setUp() throws Exception {
        // setup directories
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
        Files.createDirectories(DOCS_PATH);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(DOCS_PATH.toFile());
    }

    /**
     * Test:
     * - Apply migrations
     * @throws Exception
     */
    @Test
    public void testMigrate() throws Exception {
        Path archivePath = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH);
        Path trashPath = Paths.get(DOCS_PATH.toString(), TRASH_PATH);

    	// define test files
    	Path base1 = Paths.get("plug-1", "doc1");
    	Path archive1 = Paths.get(DOCS_PATH.toString(), base1.toString(), ARCHIVE_PATH);
    	Path archiveFile11 = Paths.get("archiveFile11");
    	Path archiveFile12 = Paths.get("archiveFile12");
    	Path archiveFile13 = Paths.get("subdir", "archiveFile13");
    	Path trash1 = Paths.get(DOCS_PATH.toString(), base1.toString(), TRASH_PATH);
    	Path trashFile11 = Paths.get("trashFile11");
    	Path trashFile12 = Paths.get("trashFile12");
    	Path trashFile13 = Paths.get("subdir", "trashFile13");

    	Path base2 = Paths.get("plug-2", "doc2");
    	Path archive2 = Paths.get(DOCS_PATH.toString(), base2.toString(), ARCHIVE_PATH);
    	Path archiveFile21 = Paths.get("archiveFile21");
    	Path archiveFile22 = Paths.get("archiveFile22");
    	Path archiveFile23 = Paths.get("subdir", "archiveFile23");
    	Path trash2 = Paths.get(DOCS_PATH.toString(), base2.toString(), TRASH_PATH);
    	Path trashFile21 = Paths.get("trashFile21");
    	Path trashFile22 = Paths.get("trashFile22");
    	Path trashFile23 = Paths.get("subdir", "trashFile23");

    	// create test files
    	Files.createDirectories(archive1);
    	Files.createDirectories(Paths.get(archive1.toString(), archiveFile13.getParent().toString()));
    	Files.createFile(Paths.get(archive1.toString(), archiveFile11.toString()));
    	Files.createFile(Paths.get(archive1.toString(), archiveFile12.toString()));
    	Files.createFile(Paths.get(archive1.toString(), archiveFile13.toString()));
    	Files.createDirectories(trash1);
    	Files.createDirectories(Paths.get(trash1.toString(), trashFile13.getParent().toString()));
    	Files.createFile(Paths.get(trash1.toString(), trashFile11.toString()));
    	Files.createFile(Paths.get(trash1.toString(), trashFile12.toString()));
    	Files.createFile(Paths.get(trash1.toString(), trashFile13.toString()));

    	Files.createDirectories(archive2);
    	Files.createDirectories(Paths.get(archive2.toString(), archiveFile23.getParent().toString()));
    	Files.createFile(Paths.get(archive2.toString(), archiveFile21.toString()));
    	Files.createFile(Paths.get(archive2.toString(), archiveFile22.toString()));
    	Files.createFile(Paths.get(archive2.toString(), archiveFile23.toString()));
    	Files.createDirectories(trash2);
    	Files.createDirectories(Paths.get(trash2.toString(), trashFile23.getParent().toString()));
    	Files.createFile(Paths.get(trash2.toString(), trashFile21.toString()));
    	Files.createFile(Paths.get(trash2.toString(), trashFile22.toString()));
    	Files.createFile(Paths.get(trash2.toString(), trashFile23.toString()));

    	// migrate
        new FileSystemStorageMigrator(DOCS_PATH.toString());

        // test
        assertTrue(Files.exists(archivePath) && Files.isDirectory(archivePath));
        assertTrue(Files.exists(trashPath) && Files.isDirectory(trashPath));
        assertFalse(Files.exists(base1));
        assertFalse(Files.exists(base2));

        Path archiveFile11New = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, base1.toString(), archiveFile11.toString());
        assertTrue(Files.exists(archiveFile11New) && Files.isRegularFile(archiveFile11New));
        Path archiveFile12New = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, base1.toString(), archiveFile12.toString());
        assertTrue(Files.exists(archiveFile12New) && Files.isRegularFile(archiveFile12New));
        Path archiveFile13New = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, base1.toString(), archiveFile13.toString());
        assertTrue(Files.exists(archiveFile13New) && Files.isRegularFile(archiveFile13New));

        Path trashFile11New = Paths.get(DOCS_PATH.toString(), TRASH_PATH, base1.toString(), trashFile11.toString());
        assertTrue(Files.exists(trashFile11New) && Files.isRegularFile(trashFile11New));
        Path trashFile12New = Paths.get(DOCS_PATH.toString(), TRASH_PATH, base1.toString(), trashFile12.toString());
        assertTrue(Files.exists(trashFile12New) && Files.isRegularFile(trashFile12New));
        Path trashFile13New = Paths.get(DOCS_PATH.toString(), TRASH_PATH, base1.toString(), trashFile13.toString());
        assertTrue(Files.exists(trashFile13New) && Files.isRegularFile(trashFile13New));

        Path archiveFile21New = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, base2.toString(), archiveFile21.toString());
        assertTrue(Files.exists(archiveFile21New) && Files.isRegularFile(archiveFile21New));
        Path archiveFile22New = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, base2.toString(), archiveFile22.toString());
        assertTrue(Files.exists(archiveFile22New) && Files.isRegularFile(archiveFile22New));
        Path archiveFile23New = Paths.get(DOCS_PATH.toString(), ARCHIVE_PATH, base2.toString(), archiveFile23.toString());
        assertTrue(Files.exists(archiveFile23New) && Files.isRegularFile(archiveFile23New));

        Path trashFile21New = Paths.get(DOCS_PATH.toString(), TRASH_PATH, base2.toString(), trashFile21.toString());
        assertTrue(Files.exists(trashFile21New) && Files.isRegularFile(trashFile21New));
        Path trashFile22New = Paths.get(DOCS_PATH.toString(), TRASH_PATH, base2.toString(), trashFile22.toString());
        assertTrue(Files.exists(trashFile22New) && Files.isRegularFile(trashFile22New));
        Path trashFile23New = Paths.get(DOCS_PATH.toString(), TRASH_PATH, base2.toString(), trashFile23.toString());
        assertTrue(Files.exists(trashFile23New) && Files.isRegularFile(trashFile23New));
    }
}
