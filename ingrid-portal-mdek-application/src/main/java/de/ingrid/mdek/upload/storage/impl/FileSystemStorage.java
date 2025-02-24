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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import de.ingrid.mdek.upload.ConflictException;
import de.ingrid.mdek.upload.ValidationException;
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;
import de.ingrid.mdek.upload.storage.validate.IllegalNameException;
import de.ingrid.mdek.upload.storage.validate.Validator;

/**
 * FileSystemStorage manages files in the server file system
 *
 * The storage has a common archive and trash directory in
 * the document root (docsDir):
 *
 * -- <docsDir>
 *   +-- _archive_
 *   +-- _trash_
 *   +-- dirA
 *       +-- dirAA
 *           +-- ...
 *       +-- fileA.1
 *       +-- ...
 *   +-- ...
 */
public class FileSystemStorage implements Storage {

    private static final String ARCHIVE_PATH = "_archive_";
    private static final String TRASH_PATH = "_trash_";

    private static final String UNKNOWN_MIME_TYPE = "";

    private static final int MAX_FILE_LENGTH = 255;
    private static final Pattern ILLEGAL_FILE_CHARS = Pattern.compile("[/<>?\":|\\*]");
    private static final Pattern ILLEGAL_PATH_CHARS = Pattern.compile("[<>?\":|\\*]");
    private static final Pattern ILLEGAL_FILE_NAME = Pattern.compile(".*"+ILLEGAL_FILE_CHARS.pattern()+".*");

    private static final String TMP_FILE_PREFIX = "upload";

    private static final Logger log = LogManager.getLogger(FileSystemStorage.class);

    private static final TikaConfig tika;
    static {
        TikaConfig obj = null;
        try {
            obj = new TikaConfig();
        }
        catch (final Exception ex) {
            log.warn("Initialization of mime type detection failed", ex);
        }
        finally {
            tika = obj;
        }
    }

    private String docsDir = null;
    private String partsDir = null;
    private String tempDir = null;
    private List<Validator> validators = new ArrayList<>();

    /**
     * Filename validator class
     */
    public static class NameValidator implements Validator {
        @Override
        public void initialize(final Map<String, String> configuration) throws IllegalArgumentException {}

        @Override
        public void validate(final String path, final String file, final long size, final Path data, final boolean isArchiveContent) throws ValidationException {
            final Path filePath = Paths.get(path, file);
            final Iterator<Path> it = filePath.iterator();
            while(it.hasNext()) {
                final String pathPart = it.next().toString();
                // check if names conflict with special directories
                if (TRASH_PATH.equals(pathPart) || ARCHIVE_PATH.equals(pathPart)) {
                    throw new IllegalNameException("The file name containes the reserved name '" + pathPart + "'.", path+"/"+file,
                            IllegalNameException.ErrorReason.RESERVED_WORD, pathPart);
                }
            }
            // we only reject invalid filenames, because the path will be sanitized
            // NOTE file could be a path, if extracted from an archive
            final String filename = Paths.get(file).getFileName().toString();
            if (!this.isValidName(filename, ILLEGAL_FILE_NAME)) {
                throw new IllegalNameException("The file name '" + file + "' contains illegal characters.", path+"/"+file,
                        IllegalNameException.ErrorReason.ILLEGAL_CHAR, filename);
            }

        }

        /**
         * Check if a path is valid
         *
         * @param path
         * @param illegalChars
         * @return String
         */
        private boolean isValidName(final String path, final Pattern illegalChars) {
            boolean isValid = true;
            // check against rules provided in https://en.m.wikipedia.org/wiki/Filename
            if (path == null || path.length() == 0 || path.length() > MAX_FILE_LENGTH) {
                isValid = false;
            }
            if (illegalChars.matcher(path).matches()) {
                isValid = false;
            }
            return isValid;
        }
    }

   /**
    * Set the document directory
    *
    * @param docsDir
    */
    public void setDocsDir(final String docsDir) {
        this.docsDir = docsDir;
    }

    /**
     * Set the partial upload directory
     *
     * @param partsDir
     */
    public void setPartsDir(final String partsDir) {
        this.partsDir = partsDir;
    }

    /**
     * Set the temporary upload directory
     *
     * @param tempDir
     */
    public void setTempDir(final String tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * Set the validators
     *
     * @param validators
     */
    public void setValidators(final List<Validator> validators) {
        this.validators = validators;
    }

    @Override
    public StorageItem[] list() throws IOException {
        final List<StorageItem> files = this.list(this.getRealPath("", this.docsDir));
        return files.toArray(new StorageItem[files.size()]);
    }

    @Override
    public StorageItem[] list(final String path) throws IOException {
        final List<StorageItem> files = this.list(this.getRealPath(path, this.docsDir));
        return files.toArray(new StorageItem[files.size()]);
    }

    /**
     * List the documents for the given path
     *
     * @param path
     * @return List<StorageItem>
     * @throws IOException
     */
    private List<StorageItem> list(final Path path) throws IOException {
        final List<StorageItem> files = new ArrayList<>();

        // add files from documents
        files.addAll(this.listFiles(path));

        // add files from archive
        final Path archivePath = this.getArchivePath(this.stripPath(path.toString()), "", this.docsDir);
        files.addAll(this.listFiles(archivePath));

        return files;
    }

    /**
     * List the files in the given path recursively
     *
     * @param path
     * @return List<StorageItem>
     * @throws IOException
     */
    private List<StorageItem> listFiles(final Path path) throws IOException {
        final List<StorageItem> files = new ArrayList<>();
        if (path.toFile().exists()) {
            try (Stream<Path> stream = Files.walk(path)) {
                stream
                .filter(p -> !p.getParent().endsWith(TRASH_PATH) && p.toFile().isFile())
                .forEach(p -> {
                    try {
                        files.add(this.getFileInfo(p.toString()));
                    }
                    catch (final IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
        }
        return files;
    }

    @Override
    public boolean exists(final String path, final String file) {
        boolean result;
        final Path realPath = this.getRealPath(path, file, this.docsDir);
        try {
            final FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
            result = fileInfo.getRealPath().toFile().exists();
        }
        catch (final Exception ex) {
            result = false;
        }
        return result;
    }

    @Override
    public void validate(final String path, final String file, final long size) {
        final String validatePath = Paths.get(this.docsDir, path).toString();
        for (final Validator validator : this.validators) {
            validator.validate(validatePath, file, size, null, false);
        }
    }

    @Override
    public StorageItem getInfo(final String path, final String file) throws IOException {
        final Path realPath = this.getRealPath(path, file, this.docsDir);
        return this.getFileInfo(realPath.toString());
    }

    @Override
    public InputStream read(final String path, final String file) throws IOException {
        final Path realPath = this.getRealPath(path, file, this.docsDir);
        final FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
        return Files.newInputStream(fileInfo.getRealPath());
    }

    @Override
    public FileSystemItem[] write(final String path, final String file, final InputStream data, final Long size, final boolean replace, final boolean extract) throws IOException {
        // file name and content validation
        // NOTE: we write the data to a temporary file before calling the validators
        // in order to allow multiple access to the streamed data
        Files.createDirectories(Paths.get(this.tempDir));
        final Path tmpFile = Files.createTempFile(Paths.get(this.tempDir), TMP_FILE_PREFIX, null);
        Files.copy(data, tmpFile, StandardCopyOption.REPLACE_EXISTING);
        final String validatePath = Paths.get(this.docsDir, path).toString();
        try {
            for (final Validator validator : this.validators) {
                validator.validate(validatePath, file, size, tmpFile, false);
            }
        }
        catch (final ValidationException ex) {
            // remove temporary file, if validation failed
            Files.delete(tmpFile);
            throw ex;
        }

        // copy file
        final Path realPath = this.getRealPath(path, file, this.docsDir);
        Files.createDirectories(realPath.getParent());

        final List<CopyOption> copyOptionList = new ArrayList<>();
        if (replace) {
            copyOptionList.add(StandardCopyOption.REPLACE_EXISTING);
        }
        final CopyOption[] copyOptions = copyOptionList.toArray(new CopyOption[copyOptionList.size()]);

        try {
            Files.move(tmpFile, realPath, copyOptions);
        }
        catch (final FileAlreadyExistsException faex) {
            final StorageItem[] items = { this.getFileInfo(faex.getFile()) };
            throw new ConflictException(faex.getMessage(), items, items[0].getNextName());
        }
        if (Files.size(realPath) != size) {
            throw new IOException("The file size is different to the expected size");
        }
        String[] files = new String[] { realPath.toString() };

        if (extract && this.isArchive(realPath)) {
            // extract archive
            try {
                files = this.extract(realPath, copyOptions);
                try {
                    for (String extractedFile : files) {
                        Path extractedPath = Paths.get(extractedFile);
                        Path basePath = realPath.getParent();
                        for (final Validator validator : this.validators) {
                            validator.validate(basePath.toString(), basePath.relativize(extractedPath).toString(), 0, extractedPath, true);
                        }
                    }
                }
                catch (final ValidationException ex) {
                    // remove extracted files, if validation failed
                    for (String extractedFile : files) {
                        Files.delete(Paths.get(extractedFile));
                    }
                    throw ex;
                }
            }
            catch (final FileAlreadyExistsException faex) {
                // get files from existing archive
                final List<StorageItem> items = this.list(this.getExtractPath(realPath));
                throw new ConflictException(faex.getMessage(), items.toArray(new StorageItem[items.size()]), this.getFileInfo(realPath.toString()).getNextName());
            }
            catch (final ValidationException ex) {
                throw ex;
            }
            catch (final Exception ex) {
                throw new IOException(ex);
            }
            finally {
                // delete archive
                if (Files.exists(realPath)) {
                    Files.delete(realPath);
                }
            }
        }

        // prepare result
        final FileSystemItem[] result = new FileSystemItem[files.length];
        for (int i = 0, count = files.length; i < count; i++) {
            result[i] = this.getFileInfo(files[i]);
        }
        return result;
    }

    @Override
    public void writePart(final String id, final Integer index, final InputStream data, final Integer size) throws IOException {
        final String file = id + "-" + index;
        final Path realPath = this.getRealPath(file, this.partsDir);
        Files.createDirectories(realPath.getParent());
        try {
            Files.copy(data, realPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (final FileAlreadyExistsException faex) {
            final StorageItem[] items = { this.getFileInfo(faex.getFile()) };
            throw new ConflictException(faex.getMessage(), items, items[0].getNextName());
        }
        if (Files.size(realPath) != size) {
            throw new IOException("The file size is different to the expected size");
        }
    }

    @Override
    public FileSystemItem[] combineParts(final String path, final String file, final String id, final Integer totalParts, final Long size, final boolean replace, final boolean extract) throws IOException {
        // file name validation (content validation is done in write() method)
        final String validatePath = Paths.get(this.docsDir, path).toString();
        for (final Validator validator : this.validators) {
            validator.validate(validatePath, file, size, null, false);
        }
        // combine parts into stream
        final Vector<InputStream> streams = new Vector<>();
        final Path[] parts = new Path[totalParts];
        for (int i = 0; i < totalParts; i++) {
            final String part = id + "-" + i;
            final Path realPath = this.getRealPath(part, this.partsDir);
            streams.add(Files.newInputStream(realPath));
            parts[i] = realPath;
        }

        // delegate to write method
        FileSystemItem[] result = null;
        // this also closes InputStreams of streams
        try (InputStream data = new SequenceInputStream(streams.elements())) {
            result = this.write(path, file, data, size, replace, extract);
        }

        // delete parts
        for (final Path part : parts) {
            Files.delete(part);
        }
        return result;
    }

    @Override
    public void delete(final String path, final String file) throws IOException {
        final Path realPath = this.getRealPath(path, file, this.docsDir);
        final Path trashPath = this.getTrashPath(path, file, this.docsDir);
        // ensure directory
        this.getTrashPath(path, "", this.docsDir).toFile().mkdirs();
        // get the real location of the file
        final FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
        Files.move(fileInfo.getRealPath(), trashPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void archive(final String path, final String file) throws IOException {
        final Path realPath = this.getRealPath(path, file, this.docsDir);
        final Path archivePath = this.getArchivePath(path, file, this.docsDir);
        // ensure directory
        this.getArchivePath(path, "", this.docsDir).toFile().mkdirs();
        Files.move(realPath, archivePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void restore(final String path, final String file) throws IOException {
        final Path realPath = this.getRealPath(path, file, this.docsDir);
        final Path archivePath = this.getArchivePath(path, file, this.docsDir);
        // ensure directory
        this.getRealPath(path, "", this.docsDir).toFile().mkdirs();
        Files.move(archivePath, realPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void cleanup() throws IOException {
        // delete empty directories
        final Path trashPath = Paths.get(this.docsDir, TRASH_PATH);
        final Path archivePath = Paths.get(this.docsDir, ARCHIVE_PATH);

        // run as long as there are empty directories
        boolean hasEmptyDirs = true;
        while (hasEmptyDirs) {
            // collect empty directories
            try (Stream<Path> stream = Files.walk(Paths.get(this.docsDir))) {
                final List<Path> emptyDirs = stream
                .filter(p -> {
                    boolean isEmptyDir = false;
                    try {
                        if (p.toFile().isDirectory()) {
                            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(p)) {
                                isEmptyDir = !dirStream.iterator().hasNext() && !(p.equals(trashPath) || p.equals(archivePath));
                            }
                        }
                    }
                    catch (final IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                    return isEmptyDir;
                })
                .collect(Collectors.toList());

                // delete directories
                for (final Path emptyDir : emptyDirs) {
                    Files.delete(emptyDir);
                }

                hasEmptyDirs = !emptyDirs.isEmpty();
            }
        }
    }

    /**
     * Check if the file denoted by path is an archive
     *
     * @param path
     * @return
     * @throws IOException
     */
    private boolean isArchive(final Path path) throws IOException {
        final String mimeType = getMimeType(path);
        return mimeType.contains("zip") || mimeType.contains("compressed");
    }

    /**
     * Extract the archive file denoted by path into a directory with the same name.
     *
     * @param path
     * @param copyOptions
     * @return String[]
     * @throws Exception
     */
    private String[] extract(final Path path, final CopyOption... copyOptions) throws Exception {
        final List<Path> result = new ArrayList<>();

        // get the directory name from the archive name
        final Path dir = this.getExtractPath(path);
        Files.createDirectories(dir);

        final int bufferSize = 1024;
        // NOTE: UTF8 encoded ZIP file entries can be interpreted when the constructor is provided
        // with a non-UTF-8 encoding.
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(path.toString()), bufferSize),
                Charset.forName("Cp437")
            )) {
            ZipEntry zipEntry = zis.getNextEntry();
            while(zipEntry != null){
                final Path file = Paths.get(dir.toString(), this.sanitize(zipEntry.getName(), ILLEGAL_PATH_CHARS));
                if (zipEntry.isDirectory()) {
                    // handle directory
                    Files.createDirectories(file);
                }
                else {
                    // handle file
                    Files.createDirectories(file.getParent());
                    Files.copy(zis, file, copyOptions);
                    result.add(file);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        catch (final Exception ex) {
            log.error("Failed to extract archive '" + path + "'. Cleaning up...", ex);
            // delete all extracted files, if one file fails
            for (final Path file : result) {
                try {
                    Files.delete(file);
                }
                catch (final Exception ex1) {
                    log.error("Could not delete '" + file + "' while cleaning up from failed extraction.");
                }
            }
            throw ex;
        }

        // delete archive
        Files.delete(path);
        return result.stream().map(p -> p.toString()).toArray(size -> new String[size]);
    }

    /**
     * Get the path where files from the given archive should be extracted to
     * @param path
     * @return Path
     */
    private Path getExtractPath(final Path path) {
        String filename = path.getName(path.getNameCount()-1).toString();
        if (filename.indexOf('.') > 0) {
            filename = filename.substring(0, filename.lastIndexOf('.'));
        }
        return Paths.get(path.getParent().toString(), this.sanitize(filename, ILLEGAL_PATH_CHARS));
    }

    /**
     * Replace forbidden characters from a path
     *
     * @param path
     * @param illegalChars
     * @return String
     */
    private String sanitize(final String path, final Pattern illegalChars) {
        return illegalChars.matcher(path).replaceAll("_");
    }

    /**
     * Get the real path of a requested path
     *
     * @param path
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getRealPath(final String path, final String file, final String basePath) {
        return FileSystems.getDefault().getPath(basePath,
            this.sanitize(path, ILLEGAL_PATH_CHARS), this.sanitize(file, ILLEGAL_FILE_CHARS));
    }

    /**
     * Get the real path of a requested path
     *
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getRealPath(final String file, final String basePath) {
        return FileSystems.getDefault().getPath(basePath, this.sanitize(file, ILLEGAL_PATH_CHARS));
    }

    /**
     * Get the trash path of a requested path
     *
     * @param path
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getTrashPath(final String path, final String file, final String basePath) {
        return FileSystems.getDefault().getPath(basePath, TRASH_PATH,
            this.sanitize(path, ILLEGAL_PATH_CHARS), this.sanitize(file, ILLEGAL_FILE_CHARS));
    }

    /**
     * Get the archive path of a requested path
     *
     * @param path
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getArchivePath(final String path, final String file, final String basePath) {
        return FileSystems.getDefault().getPath(basePath, ARCHIVE_PATH,
            this.sanitize(path, ILLEGAL_PATH_CHARS), this.sanitize(file, ILLEGAL_FILE_CHARS));
    }

    /**
     * Get the archive path of a requested path
     *
     * @param file
     * @param basePath
     * @return Path
     */
    private Path getArchivePath(final String file, final String basePath) {
        final Path strippedPath = Paths.get(this.stripPath(this.sanitize(file, ILLEGAL_PATH_CHARS)));
        if (strippedPath.getNameCount() < 2) {
            throw new IllegalArgumentException("Illegal path: "+file);
        }
        final int nameCount = strippedPath.getNameCount();
        final boolean isArchivePath = ARCHIVE_PATH.equals(strippedPath.getName(0).toString());
        return FileSystems.getDefault().getPath(basePath,
                !isArchivePath ? ARCHIVE_PATH : "",
                strippedPath.subpath(0, nameCount).toString());
    }

    /**
     * Remove the upload base directory from a path
     *
     * @param path
     * @return String
     */
    private String stripPath(final String path) {
        final Path basePath = FileSystems.getDefault().getPath(this.docsDir);
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
        final Path archivePath = this.getArchivePath(file, this.docsDir);
        if (!filePath.toFile().exists() && archivePath.toFile().exists()) {
            // fall back to archive, if file does not exist
            file = archivePath.toString();
            filePath = archivePath;
        }

        final Path strippedPath = Paths.get(this.stripPath(file));
        final boolean isArchived = filePath.equals(archivePath);

        final int nameCount = strippedPath.getNameCount();
        final String itemPath = strippedPath.subpath((isArchived ? 1 : 0), nameCount-1).toString();
        final String itemFile = strippedPath.getName(nameCount-1).toString();

        final String mimeType = getMimeType(filePath);
        final long fileSize = Files.size(filePath);

        // get last modified date of file and take care of timezone correctly, since LocalDateTime does not store time zone information (#745)
        final LocalDateTime lastModifiedTime = LocalDateTime.ofInstant(Files.getLastModifiedTime(filePath).toInstant(), TimeZone.getDefault().toZoneId());

        return new FileSystemItem(this, itemPath, itemFile, mimeType, fileSize, lastModifiedTime,
                isArchived, filePath);
    }

    /**
     * Get the mime type of a file
     *
     * @param path
     * @return String
     * @throws IOException
     */
    private String getMimeType(final Path path) throws IOException {
        if (tika == null) {
            return UNKNOWN_MIME_TYPE;
        }
        try (TikaInputStream stream = TikaInputStream.get(path)) {
            final Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, path.toFile().toString());
            final MediaType mediaType = tika.getDetector().detect(stream, metadata);
            return mediaType.getBaseType().toString();
        }
    }
}
