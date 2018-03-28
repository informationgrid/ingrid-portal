/*-
 * **************************************************-
 * InGrid Portal MDEK Application
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
package de.ingrid.mdek.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

import de.ingrid.mdek.upload.storage.impl.FileSystemStorage;

public class FileSystemStorageMigrator {

    private static final String TEMP_PATH_SUFFIX = "_tmp_";
    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";

    private static final Logger log = Logger.getLogger(FileSystemStorage.class);

    private String docsDir = null;

    /**
     * Constructor. Runs migrations.
    * @param docsDir
     */
    public FileSystemStorageMigrator(String docsDir) {
        log.info("Migrating file system storage...");

        // check document directory
        this.docsDir = docsDir;
        if (!Files.isDirectory(Paths.get(this.docsDir))) {
            log.info("Document directory configured in 'docsDir' does not exist or is no directory. Skipping migrations...");
            return;
        }

        // do migrations
        try {
            this.moveSpecialDirectoriesToRoot();
            this.cleanup();
        }
        catch (Exception ex) {
            String message = "Error migrating file system storage";
            log.error(message, ex);
            throw new RuntimeException(message, ex);
        }
        log.info("Done");
    }

    /**
     * Migration steps
     */

    /**
     * Migration step from <4.3.0 to 4.3.0
     *
     * In former versions FileSystemStorage used to have maintenance directories (_archive_, _trash_)
     * in each directory. Since version 4.3.0 they only exist once in the root directory.
     * This migration step moves all existing maintenance directories to the root directory and
     * adapts the paths inside accordingly.
     *
     * Since documents are not stored in the root directory (but in sub directories containing the
     * iplug and document ids in the path), there was no trash nor archive directory in the root directory in
     * older versions. So it's possible to tell if the migration was already applied by checking
     * the existence of the trash and archive directory in the root directory.
     *
     * Error handling: Files are moved to temporary directories first and the these will be moved
     * to the final locations only after no exception occurred. All exceptions will only be logged,
     * leading to an immediate termination of the migration step, if an exception occurs. Since files
     * are moved to a temporary folder, the migration step will continue at the same point after the
     * exception was fixed and the job is restarted and may be restarted until all files are processed
     * and the temporary folders are moved to the final locations.
     *
     * @return boolean
     * @throws IOException
     */
    private boolean moveSpecialDirectoriesToRoot() throws IOException {
        log.info("Applying step 'moveSpecialDirectoriesToRoot'...");

        // check if there is already a trash directory in the root directory
        if (!Files.isDirectory(Paths.get(this.docsDir, TRASH_PATH)) && !Files.isDirectory(Paths.get(this.docsDir, ARCHIVE_PATH))) {
            // define paths
            Path tmpTrashPath = Paths.get(this.docsDir, TRASH_PATH + TEMP_PATH_SUFFIX);
            Path tmpArchivePath = Paths.get(this.docsDir, ARCHIVE_PATH + TEMP_PATH_SUFFIX);
            Path finalTrashPath = Paths.get(this.docsDir, TRASH_PATH);
            Path finalArchivePath = Paths.get(this.docsDir, ARCHIVE_PATH);

            // create temporary special directories
            Files.createDirectories(tmpTrashPath);
            Files.createDirectories(tmpArchivePath);

            // move existing directories to temporary directories
            Path basePath = Paths.get(this.docsDir);
            Files.walk(basePath)
            .filter(p -> Files.isDirectory(p) && (p.endsWith(TRASH_PATH) || p.endsWith(ARCHIVE_PATH)))
            .forEach(p -> {
                // p is either a trash or archive directory
                Path parentPath = Paths.get(this.stripPath(p.getParent().toString()));
                Path specialPath = p.getFileName();
                try {
                    // process files in the directory
                    Files.walk(p)
                    .filter(f -> Files.isRegularFile(f))
                    .forEach(f -> {
                        // f is a file in an existing trash or archive directory
                        Path newPath = Paths.get(this.docsDir, specialPath.toString() + TEMP_PATH_SUFFIX, parentPath.toString(), p.relativize(f).toString());
                        try {
                            // move file to new special directory
                            log.debug("Moving file " + f + " to " + newPath);
                            Files.createDirectories(newPath.getParent());
                            Files.move(f, newPath, StandardCopyOption.ATOMIC_MOVE);
                        }
                        catch (IOException ex) {
                            log.error("Error moving file " + f + " to " + newPath, ex);
                            throw new UncheckedIOException(ex);
                        }
                    });
                }
                catch (IOException ex) {
                    log.error("Error processing path " + p, ex);
                    throw new UncheckedIOException(ex);
                }
            });

            // move temporary directories to the final locations (archive first)
            Files.move(tmpArchivePath, finalArchivePath, StandardCopyOption.ATOMIC_MOVE);

            Files.move(tmpTrashPath, finalTrashPath, StandardCopyOption.ATOMIC_MOVE);
            return true;
        }
        else {
            log.info("Special directories are already migrated. Skipping migration step...");
        }
        return false;
    }

    /**
     * Helper methods
     */

    /**
     * Clean up the storage
     * @throws IOException
     */
    private void cleanup() throws IOException {
        FileSystemStorage storage = new FileSystemStorage();
        storage.setDocsDir(this.docsDir);
        storage.cleanup();
    }

    /**
     * Remove the document base directory from a path
     *
     * @param path
     * @return String
     */
    private String stripPath(String path) {
        Path basePath = FileSystems.getDefault().getPath(this.docsDir);
        return path.replace(basePath.toString(), "").replaceAll("^[/\\\\]+", "");
    }
}
