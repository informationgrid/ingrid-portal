/**
 * 
 */
package de.ingrid.mdek.upload.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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

	/**
	 * @see de.ingrid.mdek.upload.storage.Storage#list(java.nio.file.Path)
	 */
	@Override
	public File[] list(Path path) {
		path = getRealPath(path.toFile());
		return path.toFile().listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File pathname) {
		        return pathname.isFile();
		    }
		});
	}

	/**
	 * @see de.ingrid.mdek.upload.storage.Storage#exists(java.io.File)
	 */
	@Override
	public boolean exists(File file) {
		java.nio.file.Path path = getRealPath(file);
		return path.toFile().exists();
	}

	/**
	 * @see de.ingrid.mdek.upload.storage.Storage#read(java.io.File)
	 */
	@Override
	public InputStream read(File file) throws FileNotFoundException {
		java.nio.file.Path path = getRealPath(file);
		return new FileInputStream(path.toFile());
	}

	/**
	 * @see de.ingrid.mdek.upload.storage.Storage#write(java.io.File,
	 *      java.io.InputStream)
	 */
	@Override
	public File[] write(File file, InputStream data, boolean replace) throws Exception {
		file = this.sanitize(file);
		
		java.nio.file.Path path = getRealPath(file);
		Files.createDirectories(path.getParent());
		
		// copy file
		List<CopyOption> copyOptionList = new ArrayList<CopyOption>();
		if (replace) {
			copyOptionList.add(StandardCopyOption.REPLACE_EXISTING);
		}
		CopyOption[] copyOptions = copyOptionList.toArray(new CopyOption[copyOptionList.size()]);
		
		Files.copy(data, path, copyOptions);
		File[] result = new File[] { file };

		// extract archives
		try {
			String contentType = Files.probeContentType(path);
			if (contentType.contains("zip") || contentType.contains("compressed")) {
				result = this.uncompress(path, copyOptions);
				// delete archive
				path.toFile().delete();
			}
		} catch (Exception ex) {
			log.error("Post processing of '"+path+"' failed due to the following exception:\n"+ex.toString());
		}
		
		// prepare result
		for (int i=0, count=result.length; i<count; i++) {
			result[i] = this.stripPath(result[i]);
		}
		return result;
	}

	/**
	 * @see de.ingrid.mdek.upload.storage.Storage#delete(java.io.File)
	 */
	@Override
	public void delete(File file) {
		java.nio.file.Path path = getRealPath(file);
		path.toFile().delete();
	}

	/**
	 * @see de.ingrid.mdek.upload.storage.Storage#alias(java.nio.file.Path, jjava.nio.file.Path)
	 */
	@Override
	public void alias(Path oldPath, Path newPath) {
		// TODO create a symbolic link
	}

	/**
	 * Uncompress the file denoted by path
	 * @param path
	 * @param copyOptions
	 * @return File[]
	 * @throws Exception
	 */
	private File[] uncompress(java.nio.file.Path path, CopyOption... copyOptions) throws Exception {
		List<File> result = new ArrayList<File>(); 
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
				File parent = path.getParent().toFile();
				while (entry != null) {
					File file = new File(parent, entry.getName());
					file = this.sanitize(file);
					if (entry.isDirectory()) {
						// handle directory
						file.mkdirs();
					} else {
						// handle file
						Files.copy(ais, file.toPath(), copyOptions);
						result.add(file);
					}
					entry = ais.getNextEntry();
				}
			}
			catch (Exception ex) {
				log.error("Failed to uncompress '"+path+"'. Cleaning up...");
				// delete all extracted files, if one file fails
				for (File file : result) {
					try {
						file.delete();
					}
					catch (Exception ex1) {
						log.error("Could not delete '"+file+"' while cleaning up from failed uncompressing.");
					}
				}
				throw ex;
			}
		}
		return result.toArray(new File[result.size()]);
	}
	
	/**
	 * Replace forbidden characters from a path
	 * @param file
	 * @return File
	 */
	private File sanitize(File file) {
		// TODO remove forbidden characters
		return file;
	}
	
	/**
	 * Get the real path of a requested path
	 * @param file
	 * @return Path
	 */
	private Path getRealPath(File file) {
		return FileSystems.getDefault().getPath(this.uploadBaseDir, file.getPath());
	}
	
	/**
	 * Remove the upload base directory from a path
	 * @param file
	 * @return File
	 */
	private File stripPath(File file) {
		Path basePath = FileSystems.getDefault().getPath(this.uploadBaseDir);
		return new File(file.getPath().replace(basePath.toString(), ""));
	}
}
