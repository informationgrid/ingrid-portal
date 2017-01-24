package de.ingrid.mdek.upload.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Storage defines the interface for classes, that are responsible for
 * storing and retrieving files.
 */
public interface Storage {
	/**
	 * List all files in a directory
	 * @param file The directory
	 * @return File[]
	 */
	File[] list(Path path);
	
	/**
	 * Check if a file exists
	 * @param file The file
	 * @return boolean
	 */
	boolean exists(File file);
	
	/**
	 * Get the data from a file
	 * @param file The file
	 * @return InputStream
	 * @throws FileNotFoundException 
	 */
	InputStream read(File file) throws FileNotFoundException;

	/**
	 * Write data to a file and extract archives
	 * @param file The file
	 * @param data The data
	 * @param replace Boolean indicating whether to replace an existing file or not
	 * @return File[]
	 * @throws Exception 
	 */
	File[] write(File file, InputStream data, boolean replace) throws Exception;
	
	/**
	 * Delete a file
	 * @param file The file
	 */
	void delete(File file);
	
	/**
	 * Create a alias for a directory
	 * @param oldFile The original path
	 * @param newFile The alias
	 */
	void alias(Path oldPath, Path newPath);
}
