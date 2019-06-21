/*-
 * **************************************************-
 * InGrid Portal MDEK Application
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
package de.ingrid.mdek.upload.storage.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
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
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.kohsuke.file_leak_detector.Listener;
import org.kohsuke.file_leak_detector.Listener.Record;

import com.sun.tools.attach.VirtualMachine;

import de.ingrid.mdek.upload.ConflictException;
import de.ingrid.mdek.upload.IllegalFileException;
import de.ingrid.mdek.upload.storage.Storage;
import de.ingrid.mdek.upload.storage.StorageItem;

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

    private static final Logger log = Logger.getLogger(FileSystemStorage.class);

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

    // file handle logging
    private final static boolean FILEHANDLE_LOGGING_ENABLED = true;
    private static boolean isFileHandleLogInitialized;
    // file-leak-detector agent will write open/close operations into this log
    private static StringWriter fileHandleLog = new StringWriter();
    private static List<Record> alreadyOpenedFileHandles;

    /**
     * Constructor
     */
    public FileSystemStorage() {
        if (FILEHANDLE_LOGGING_ENABLED) {
            loadFileLeakDetectorAgent();
        }
    }

    /**
     * Install file-leak-detector agent
     */
    private static void loadFileLeakDetectorAgent() {
        if (isFileHandleLogInitialized) {
            return;
        }
        log.info("Loading file-leak-detector agent");
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        Path jarFilePath = Paths.get("lib", "file-leak-detector-1.13.jar");
        log.info("Loading agent from "+jarFilePath.toAbsolutePath().toString());
        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(jarFilePath.toAbsolutePath().toString(), "noexit");
            vm.detach();
        }
        catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        Listener.makeStrong();
        Listener.TRACE = new PrintWriter(fileHandleLog);
        isFileHandleLogInitialized = true;
        log.info("Agent file-leak-detector is"+(Listener.isAgentInstalled() ? "" : " NOT")+" installed");
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

    @Override
    public StorageItem[] list() throws IOException {
        resetFileHandleLog();
        try {
            final List<StorageItem> files = this.list(this.getRealPath("", this.docsDir));
            return files.toArray(new StorageItem[files.size()]);
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public StorageItem[] list(final String path) throws IOException {
        resetFileHandleLog();
        try {
            final List<StorageItem> files = this.list(this.getRealPath(path, this.docsDir));
            return files.toArray(new StorageItem[files.size()]);
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
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
        resetFileHandleLog();
        try {
            boolean result;
            final Path realPath = this.getRealPath(path, file, this.docsDir);
            try {
                final FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
                result = fileInfo.getRealPath().toFile().exists();
            }
            catch (Exception ex) {
                result = false;
            }
            return result;
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public boolean isValidName(final String path, final String file) {
        // check if names conflict with special directories
        boolean result = true;
        final Path filePath = Paths.get(path, file);
        final Iterator<Path> it = filePath.iterator();
        while(it.hasNext()) {
            final String pathPart = it.next().toString();
            if (TRASH_PATH.equals(pathPart) || ARCHIVE_PATH.equals(pathPart)) {
                result = false;
                break;
            }
        }
        // we only reject invalid filenames, because the path will be sanitized
        result = result && this.isValid(file, ILLEGAL_FILE_NAME);
        return result;
    }

    @Override
    public StorageItem getInfo(final String path, final String file) throws IOException {
        resetFileHandleLog();
        try {
            final Path realPath = this.getRealPath(path, file, this.docsDir);
            return this.getFileInfo(realPath.toString());
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public InputStream read(final String path, final String file, final CompletableFuture<Void> caller) throws IOException {
        resetFileHandleLog();
        try {
            final Path realPath = this.getRealPath(path, file, this.docsDir);
            final FileSystemItem fileInfo = this.getFileInfo(realPath.toString());

            final InputStream result = Files.newInputStream(fileInfo.getRealPath());

            // finish read handler
            if (caller != null) {
                caller.handle((res, ex) -> {
                    try {
                        result.close();
                    }
                    catch (Exception e) {}
                    checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
                    return res;
                });
            }
            return result;
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public FileSystemItem[] write(final String path, final String file, final InputStream data, final Integer size, final boolean replace, final boolean extract)
            throws IOException {
        resetFileHandleLog();
        try {
            if (!this.isValidName(path, file)) {
                throw new IllegalFileException("The file name is invalid.", path+"/"+file);
            }
            final Path realPath = this.getRealPath(path, file, this.docsDir);
            Files.createDirectories(realPath.getParent());

            // copy file
            final List<CopyOption> copyOptionList = new ArrayList<>();
            if (replace) {
                copyOptionList.add(StandardCopyOption.REPLACE_EXISTING);
            }
            final CopyOption[] copyOptions = copyOptionList.toArray(new CopyOption[copyOptionList.size()]);

            try {
                Files.copy(data, realPath, copyOptions);
            }
            catch (FileAlreadyExistsException faex) {
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
                }
                catch (FileAlreadyExistsException faex) {
                    // get files from existing archive
                    List<StorageItem> items = this.list(this.getExtractPath(realPath));
                    throw new ConflictException(faex.getMessage(), items.toArray(new StorageItem[items.size()]), this.getFileInfo(realPath.toString()).getNextName());
                }
                catch (Exception ex) {
                    throw new IOException(ex);
                }
                finally {
                    // delete archive
                    Files.delete(realPath);
                }
            }

            // prepare result
            final FileSystemItem[] result = new FileSystemItem[files.length];
            for (int i = 0, count = files.length; i < count; i++) {
                result[i] = this.getFileInfo(files[i]);
            }
            return result;
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public void writePart(final String id, final Integer index, final InputStream data, final Integer size) throws IOException {
        resetFileHandleLog();
        try {
            final String file = id + "-" + index;
            final Path realPath = this.getRealPath(file, this.partsDir);
            Files.createDirectories(realPath.getParent());
            try {
                Files.copy(data, realPath, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (FileAlreadyExistsException faex) {
                final StorageItem[] items = { this.getFileInfo(faex.getFile()) };
                throw new ConflictException(faex.getMessage(), items, items[0].getNextName());
            }
            if (Files.size(realPath) != size) {
                throw new IOException("The file size is different to the expected size");
            }
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public FileSystemItem[] combineParts(final String path, final String file, final String id, final Integer totalParts, final Integer size, final boolean replace, final boolean extract)
            throws IOException {
        resetFileHandleLog();
        try {
            if (!this.isValidName(path, file)) {
                throw new IllegalFileException("The file name is invalid.", path+"/"+file);
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
            for (Path part : parts) {
                Files.delete(part);
            }
            return result;
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public void delete(final String path, final String file) throws IOException {
        resetFileHandleLog();
        try {
            final Path realPath = this.getRealPath(path, file, this.docsDir);
            final Path trashPath = this.getTrashPath(path, file, this.docsDir);
            // ensure directory
            this.getTrashPath(path, "", this.docsDir).toFile().mkdirs();
            // get the real location of the file
            final FileSystemItem fileInfo = this.getFileInfo(realPath.toString());
            Files.move(fileInfo.getRealPath(), trashPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public void archive(final String path, final String file) throws IOException {
        resetFileHandleLog();
        try {
            final Path realPath = this.getRealPath(path, file, this.docsDir);
            final Path archivePath = this.getArchivePath(path, file, this.docsDir);
            // ensure directory
            this.getArchivePath(path, "", this.docsDir).toFile().mkdirs();
            Files.move(realPath, archivePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public void restore(final String path, final String file) throws IOException {
        resetFileHandleLog();
        try {
            final Path realPath = this.getRealPath(path, file, this.docsDir);
            final Path archivePath = this.getArchivePath(path, file, this.docsDir);
            // ensure directory
            this.getRealPath(path, "", this.docsDir).toFile().mkdirs();
            Files.move(archivePath, realPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
        }
    }

    @Override
    public void cleanup() throws IOException {
        resetFileHandleLog();
        try {
            // delete empty directories
            final Path trashPath = Paths.get(this.docsDir, TRASH_PATH);
            final Path archivePath = Paths.get(this.docsDir, ARCHIVE_PATH);

            // run as long as there are empty directories
            boolean hasEmptyDirs = true;
            while (hasEmptyDirs) {
                // collect empty directories
                try (Stream<Path> stream = Files.walk(Paths.get(this.docsDir))) {
                    List<Path> emptyDirs = stream
                    .filter(p -> {
                        boolean isEmptyDir = false;
                        try {
                            if (p.toFile().isDirectory()) {
                                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(p)) {
                                    isEmptyDir = !dirStream.iterator().hasNext() && !(p.equals(trashPath) || p.equals(archivePath));
                                }
                            }
                        }
                        catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                        return isEmptyDir;
                    })
                    .collect(Collectors.toList());

                    // delete directories
                    for (Path emptyDir : emptyDirs) {
                        Files.delete(emptyDir);
                    }

                    hasEmptyDirs = !emptyDirs.isEmpty();
                }
            }
        }
        finally {
            checkFileHandles(this.getClass().getName()+":"+Thread.currentThread().getStackTrace()[1].getLineNumber());
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
            log.error("Failed to extract archive '" + path + "'. Cleaning up...");
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
     * Check if a path is valid
     *
     * @param path
     * @param illegalChars
     * @return String
     */
    private boolean isValid(final String path, final Pattern illegalChars) {
        // check against rules provided in https://en.m.wikipedia.org/wiki/Filename
        if (path == null || path.length() == 0 || path.length() > MAX_FILE_LENGTH) {
            return false;
        }
        if (illegalChars.matcher(path).matches()) {
            return false;
        }
        return true;
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

    /**
     * File leak detection
     */

    /**
     * Empty the file handle log
     */
    protected void resetFileHandleLog() {
        fileHandleLog.getBuffer().setLength(0);
        alreadyOpenedFileHandles = Listener.getCurrentOpenFiles();
    }

    /**
     * Get open files handles since last call to resetFileHandleLog()
     * @return List<Path>
     */
    protected List<Record> getOpenFileHandles() {
        final List<Record> paths = new ArrayList<>();
        for (Record record : Listener.getCurrentOpenFiles()) {
            if (!alreadyOpenedFileHandles.contains(record)) {
                paths.add(record);
            }
        }
        return paths;
    }

    /**
     * Check open file handles
     * @param prefix
     */
    protected void checkFileHandles(final String prefix) {
        log.debug("Checking open file handles at: "+prefix);
        final List<Record> records = getOpenFileHandles();
        if (records.size() > 0) {
            log.warn("Detected unclosed file handle(s):");
            for (final Record record : records) {
                log.warn("-> "+record);
            }
            String logStr = fileHandleLog.toString();
            if (!logStr.isEmpty()) {
                log.warn("-> Attached log:");
                log.warn("-> "+fileHandleLog.toString());
            }
        }
        else {
            log.debug("-> No open files found");
        }
    }

    /**
     * Check if there are open file handles from last method call
     * @return boolean
     */
    public boolean hasOpenFileHandles() {
        return getOpenFileHandles().size() > 0;
    }
}
