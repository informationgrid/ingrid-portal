package de.ingrid.mdek.upload.storage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

/**
 * FileSystemStorage manages files in the server file system
 */
public class FileSystemStorage implements Storage {

	private final static Logger log = Logger.getLogger(FileSystemStorage.class);
	
	private String uploadBaseDir = null;

	/**
	 * Constructor
	 * @param uploadBaseDir
	 */
	public FileSystemStorage(@Value("${upload.basedir}") String uploadBaseDir) {
		this.uploadBaseDir = uploadBaseDir;
	}

	@Override
	public String[] list(String path) throws IOException {
		Path realPath = getRealPath(path);
        List<String> files = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(realPath)) {
            for (Path entry: stream) {
            	if (Files.isRegularFile(entry)) {
            		files.add(this.stripPath(entry.toString()));
            	}
            }
        }
        return files.toArray(new String[files.size()]);
    }

	@Override
	public boolean exists(String path) {
		Path realPath = getRealPath(path);
		return Files.exists(realPath);
	}

	@Override
	public boolean isDirectory(String path) {
		Path realPath = getRealPath(path);
		return Files.isDirectory(realPath);
	}

	@Override
	public InputStream read(String file) throws IOException {
		Path realPath = getRealPath(file);
		return Files.newInputStream(realPath);
	}

	@Override
	public String[] write(String path, String file, InputStream data, boolean replace) throws IOException {
		Path filePath = Paths.get(path, file);
		Path realPath = getRealPath(this.sanitize(filePath.toString()));
		Files.createDirectories(realPath.getParent());
		
		// copy file
		List<CopyOption> copyOptionList = new ArrayList<CopyOption>();
		if (replace) {
			copyOptionList.add(StandardCopyOption.REPLACE_EXISTING);
		}
		CopyOption[] copyOptions = copyOptionList.toArray(new CopyOption[copyOptionList.size()]);
		
		Files.copy(data, realPath, copyOptions);
		String[] result = new String[] { realPath.toString() };

		// extract archives
		try {
			String contentType = Files.probeContentType(realPath);
			if (contentType.contains("zip") || contentType.contains("compressed")) {
				result = this.uncompress(realPath, copyOptions);
				// delete archive
				Files.delete(realPath);
			}
		} catch (Exception ex) {
			log.error("Post processing of '"+realPath+"' failed due to the following exception:\n"+ex.toString());
		}
		
		// prepare result
		for (int i=0, count=result.length; i<count; i++) {
			result[i] = this.stripPath(result[i]);
		}
		return result;
	}

	@Override
	public void delete(String file) throws IOException {
		Path realPath = getRealPath(file);
		Files.delete(realPath);
	}

	@Override
	public void alias(String oldPath, String newPath) throws IOException {
		// TODO create a symbolic link
	}

	/**
	 * Uncompress the file denoted by path
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
			} catch (CompressorException e) {
				// a compressor was not recognized in the stream, in this case
				// we leave the inputStream as-is
			}

			InputStream is = bcis != null ? bcis : bis;
			try (ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream(is)) {
				ArchiveEntry entry = ais.getNextEntry();
				Path parent = path.getParent();
				while (entry != null) {
					Path file = Paths.get(this.sanitize(parent.toString()), this.sanitize(entry.getName()));
					if (entry.isDirectory()) {
						// handle directory
						Files.createDirectories(file);
					} else {
						// handle file
						Files.copy(ais, file, copyOptions);
						result.add(file);
					}
					entry = ais.getNextEntry();
				}
			}
			catch (Exception ex) {
				log.error("Failed to uncompress '"+path+"'. Cleaning up...");
				// delete all extracted files, if one file fails
				for (Path file : result) {
					try {
						Files.delete(file);
					}
					catch (Exception ex1) {
						log.error("Could not delete '"+file+"' while cleaning up from failed uncompressing.");
					}
				}
				throw ex;
			}
		}
		return (String[])result.stream().map(p -> p.toString()).toArray();
	}
	
	/**
	 * Replace forbidden characters from a path
	 * @param path
	 * @return String
	 */
	private String sanitize(String path) {
		// TODO remove forbidden characters
		return path;
	}
	
	/**
	 * Get the real path of a requested path
	 * @param file
	 * @return Path
	 */
	private Path getRealPath(String file) {
		return FileSystems.getDefault().getPath(this.uploadBaseDir, file);
	}
	
	/**
	 * Remove the upload base directory from a path
	 * @param path
	 * @return String
	 */
	private String stripPath(String path) {
		Path basePath = FileSystems.getDefault().getPath(this.uploadBaseDir);
		return path.replace(basePath.toString(), "");
	}
}
