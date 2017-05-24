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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.ingrid.mdek.upload.ConflictException;
import de.ingrid.mdek.upload.IllegalFileException;
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;

/**
 * FileSystemStorage manages files in the server file system
 */
public class FileSystemStorage implements Storage {

    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";

    private static final int MAX_FILE_LENGTH = 255;
    private static final Pattern ILLEGAL_CHARS = Pattern.compile(".*[/<>?\":|\\*].*");

    private static final Logger log = Logger.getLogger(FileSystemStorage.class);

    private String docsDir = null;
    private String partsDir = null;

   /**
    * Set the document directory
    *
    * @param docsDir
    */
    public void setDocsDir(String docsDir) {
        this.docsDir = docsDir;
    }

    /**
     * Set the partial upload directory
     *
     * @param partsDir
     */
    public void setPartsDir(String partsDir) {
        this.partsDir = partsDir;
    }

    @Override
    public StorageItem[] list() throws IOException {
        List<StorageItem> files = this.list(this.getRealPath("", this.docsDir));
        return files.toArray(new StorageItem[files.size()]);
    }

    @Override
    public StorageItem[] list(String path) throws IOException {
        List<StorageItem> files = this.list(this.getRealPath(path, this.docsDir));
        return files.toArray(new StorageItem[files.size()]);
    }

    /**
     * List the files in the given path recursively
     *
     * @param path
     * @return List<StorageItem>
     * @throws IOException
     */
    protected List<StorageItem> list(Path path) throws IOException {
        List<StorageItem> files = new ArrayList<StorageItem>();
        Files.walk(path)
            .filter(p -> !p.getParent().toString().endsWith(TRASH_PATH) && Files.isRegularFile(p))
            .forEach(p -> {
                try {
                    files.add(this.getFileInfo(p.toString()));
                }
                catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        return files;
    }

    @Override
    public boolean exists(String path, String file) {
        Path realPath = this.getRealPath(path, file, this.docsDir);
        try {
            FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
            return Files.exists(fileInfo.getRealPath());
        }
        catch (Exception ex) {}
        return false;
    }

    @Override
    public boolean isValidName(String path, String file) {
        return this.isValid(path) && this.isValid(file);
    }

    @Override
    public StorageItem getInfo(String path, String file) throws IOException {
        Path realPath = this.getRealPath(path, file, this.docsDir);
        return this.getFileInfo(realPath.toString());
    }

    @Override
    public InputStream read(String path, String file) throws IOException {
        Path realPath = this.getRealPath(path, file, this.docsDir);
        FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
        return Files.newInputStream(fileInfo.getRealPath());
    }

    @Override
    public FileSystemItem[] write(String path, String file, InputStream data, Integer size, boolean replace, boolean extract)
            throws IOException {
        if (!this.isValidName(path, file)) {
            throw new IllegalFileException("The file is invalid.", path+"/"+file);
        }
        Path realPath = this.getRealPath(path, file, this.docsDir);
        Files.createDirectories(realPath.getParent());

        // copy file
        List<CopyOption> copyOptionList = new ArrayList<CopyOption>();
        if (replace) {
            copyOptionList.add(StandardCopyOption.REPLACE_EXISTING);
        }
        CopyOption[] copyOptions = copyOptionList.toArray(new CopyOption[copyOptionList.size()]);

        try {
            Files.copy(data, realPath, copyOptions);
        }
        catch (FileAlreadyExistsException faex) {
            throw new ConflictException(faex.getMessage(), this.getFileInfo(faex.getFile()));
        }
        if (Files.size(realPath) != size) {
            throw new IOException("The file size is different to the expected size");
        }
        String[] files = new String[] { realPath.toString() };

        if (extract) {
            // extract archives
            try {
                String contentType = Files.probeContentType(realPath);
                if (contentType.contains("zip") || contentType.contains("compressed")) {
                    files = this.uncompress(realPath, copyOptions);
                    // delete archive
                    Files.delete(realPath);
                }
            }
            catch (FileAlreadyExistsException faex) {
                throw new ConflictException(faex.getMessage(), this.getFileInfo(faex.getFile()));
            }
            catch (Exception ex) {
                throw new IOException(ex);
            }
        }

        // prepare result
        FileSystemItem[] result = new FileSystemItem[files.length];
        for (int i = 0, count = files.length; i < count; i++) {
            result[i] = this.getFileInfo(files[i]);
        }
        return result;
    }

    @Override
    public void writePart(String id, Integer index, InputStream data, Integer size) throws IOException {
        String file = id + "-" + index;
        Path realPath = this.getRealPath(file, this.partsDir);
        Files.createDirectories(realPath.getParent());
        try {
            Files.copy(data, realPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (FileAlreadyExistsException faex) {
            throw new ConflictException(faex.getMessage(), this.getFileInfo(faex.getFile()));
        }
        if (Files.size(realPath) != size) {
            throw new IOException("The file size is different to the expected size");
        }
    }

    @Override
    public FileSystemItem[] combineParts(String path, String file, String id, Integer totalParts, Integer size, boolean replace, boolean extract)
            throws IOException {
        if (!this.isValidName(path, file)) {
            throw new IllegalFileException("The file is invalid.", path+"/"+file);
        }
        // combine parts into stream
        Vector<InputStream> streams = new Vector<InputStream>();
        Path[] parts = new Path[totalParts];
        for (int i = 0; i < totalParts; i++) {
            String part = id + "-" + i;
            Path realPath = this.getRealPath(part, this.partsDir);
            streams.add(Files.newInputStream(realPath));
            parts[i] = realPath;
        }

        // delegate to write method
        InputStream data = new SequenceInputStream(streams.elements());
        FileSystemItem[] result = this.write(path, file, data, size, replace, extract);

        // delete parts
        for (Path part : parts) {
            Files.delete(part);
        }
        return result;
    }

    @Override
    public void delete(String path, String file) throws IOException {
        Path realPath = this.getRealPath(path, file, this.docsDir);
        Path trashPath = this.getTrashPath(path, file, this.docsDir);
        // ensure directory
        this.getTrashPath(path, "", this.docsDir).toFile().mkdirs();
        // get the real location of the file
        FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
        Files.move(fileInfo.getRealPath(), trashPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void archive(String path, String file) throws IOException {
        Path realPath = this.getRealPath(path, file, this.docsDir);
        Path archivePath = this.getArchivePath(path, file, this.docsDir);
        // ensure directory
        this.getArchivePath(path, "", this.docsDir).toFile().mkdirs();
        Files.move(realPath, archivePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void restore(String path, String file) throws IOException {
        Path realPath = this.getRealPath(path, file, this.docsDir);
        Path archivePath = this.getArchivePath(path, file, this.docsDir);
        Files.move(archivePath, realPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Uncompress the file denoted by path
     *
     * @param path
     * @param copyOptions
     * @return String[]
     * @throws Exception
     */
    private String[] uncompress(Path path, CopyOption... copyOptions) throws Exception {
        List<Path> result = new ArrayList<Path>();
        try (InputStream fis = FileUtils.openInputStream(path.toFile());
                InputStream bis = new BufferedInputStream(fis)) {
            InputStream bcis = null;
            try {
                bcis = new BufferedInputStream(new CompressorStreamFactory().createCompressorInputStream(bis));
            }
            catch (CompressorException e) {
                // a compressor was not recognized in the stream, in this case
                // we leave the inputStream as-is
            }

            InputStream is = bcis != null ? bcis : bis;
            try (ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(is)) {
                ArchiveEntry entry = ais.getNextEntry();
                Path parent = path.getParent();
                while (entry != null) {
                    Path file = Paths.get(parent.toString(), this.sanitize(entry.getName()));
                    if (entry.isDirectory()) {
                        // handle directory
                        Files.createDirectories(file);
                    }
                    else {
                        // handle file
                        Files.copy(ais, file, copyOptions);
                        result.add(file);
                    }
                    entry = ais.getNextEntry();
                }
            }
            catch (Exception ex) {
                log.error("Failed to uncompress '" + path + "'. Cleaning up...");
                // delete all extracted files, if one file fails
                for (Path file : result) {
                    try {
                        Files.delete(file);
                    }
                    catch (Exception ex1) {
                        log.error("Could not delete '" + file + "' while cleaning up from failed uncompressing.");
                    }
                }
                throw ex;
            }
        }
        return result.stream().map(p -> p.toString()).toArray(size -> new String[size]);
    }

    /**
     * Check if a path is valid
     *
     * @param path
     * @return String
     */
    private boolean isValid(String path) {
        // check against rules provided in https://en.m.wikipedia.org/wiki/Filename
        if (path == null || path.length() == 0 || path.length() > MAX_FILE_LENGTH) {
            return false;
        }
        if (ILLEGAL_CHARS.matcher(path).matches()) {
            return false;
        }
        return true;
    }

    /**
     * Replace forbidden characters from a path
     *
     * @param path
     * @return String
     */
    private String sanitize(String path) {
        // TODO which characters are forbidden?
        return path;
    }

    /**
     * Get the real path of a requested path
     *
     * @param path
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getRealPath(String path, String file, String basePath) {
        return FileSystems.getDefault().getPath(basePath, this.sanitize(path), this.sanitize(file));
    }

    /**
     * Get the real path of a requested path
     *
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getRealPath(String file, String basePath) {
        return FileSystems.getDefault().getPath(basePath, this.sanitize(file));
    }

    /**
     * Get the trash path of a requested path
     *
     * @param path
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getTrashPath(String path, String file, String basePath) {
        return FileSystems.getDefault().getPath(basePath, this.sanitize(path), TRASH_PATH, this.sanitize(file));
    }

    /**
     * Get the archive path of a requested path
     *
     * @param path
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getArchivePath(String path, String file, String basePath) {
        return FileSystems.getDefault().getPath(basePath, this.sanitize(path), ARCHIVE_PATH, this.sanitize(file));
    }

    /**
     * Get the archive path of a requested path
     *
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getArchivePath(String file, String basePath) {
        Path strippedPath = Paths.get(this.stripPath(this.sanitize(file)));
        if (strippedPath.getNameCount() < 2) {
            throw new IllegalArgumentException("Illegal path: "+file);
        }
        boolean isArchivePath = ARCHIVE_PATH.equals(strippedPath.getName(1).toString());
        return FileSystems.getDefault().getPath(basePath, strippedPath.getName(0).toString(),
                !isArchivePath ? ARCHIVE_PATH : "",
                strippedPath.subpath(1, strippedPath.getNameCount()).toString());
    }

    /**
     * Remove the upload base directory from a path
     *
     * @param path
     * @return String
     */
    private String stripPath(String path) {
        Path basePath = FileSystems.getDefault().getPath(this.docsDir);
        return path.replace(basePath.toString(), "").replaceAll("^[/\\\\]+", "");
    }

    /**
     * Get information about a file
     *
     * @param file
     * @return Item
     * @throws IOException
     */
    private FileSystemItem getFileInfo(String file) throws IOException {
        Path filePath = Paths.get(file);
        Path archivePath = this.getArchivePath(file, this.docsDir);
        if (!Files.exists(filePath) && Files.exists(archivePath)) {
            // fall back to archive, if file does not exist
            file = archivePath.toString();
            filePath = archivePath;
        }

        Path strippedPath = Paths.get(this.stripPath(file));
        boolean isArchived = filePath.equals(archivePath);

        String itemPath = strippedPath.getName(0).toString();
        String itemFile = strippedPath.subpath(isArchived ? 2 : 1, strippedPath.getNameCount()).toString();

        String fileType = Files.probeContentType(filePath);
        long fileSize = Files.size(filePath);

        LocalDateTime lastModifiedTime = LocalDateTime.ofInstant(Files.getLastModifiedTime(filePath).toInstant(), ZoneOffset.UTC);

        return new FileSystemItem(this, itemPath, itemFile, fileType, fileSize, lastModifiedTime,
                isArchived, filePath);
    }
}
