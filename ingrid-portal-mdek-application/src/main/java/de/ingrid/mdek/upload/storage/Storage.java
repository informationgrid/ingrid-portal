package de.ingrid.mdek.upload.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Storage defines the interface for classes, that are responsible for
 * storing and retrieving files.
 */
public interface Storage {
	/**
	 * List all files in a path
	 * @param path The path
	 * @return String[]
	 * @throws IOException 
	 */
	String[] list(String path) throws IOException;
	
	/**
	 * Check if a path exists
	 * @param path The path
	 * @return boolean
	 */
	boolean exists(String path);
	
	/**
	 * Check if a path denotes a directory
	 * @param path The path
	 * @return boolean
	 */
	boolean isDirectory(String path);
	
	/**
	 * Get the content of a file
	 * @param file The file
	 * @return InputStream
	 * @throws IOException 
	 */
	InputStream read(String file) throws IOException;

	/**
	 * Write data to a file in a path and extract archives contained in data
	 * @param path The path
	 * @param file The file
	 * @param data The data
	 * @param size The size of the file in bytes (used to verify)
	 * @param replace Boolean indicating whether to replace an existing file or not
	 * @return String[] The list of created files
	 * @throws IOException
	 */
	String[] write(String path, String file, InputStream data, Integer size, boolean replace) throws IOException;
	
	/**
	 * Write a file part
	 * @param id The id of the file
	 * @param index The index of the part
	 * @param data The data
	 * @param size The size of the part in bytes (used to verify)
	 * @throws IOException
	 */
	void writePart(String id, Integer index, InputStream data, Integer size) throws IOException;
	
	/**
	 * Combine all parts belonging to an id to the final file
	 * @param path The path
	 * @param file The file
	 * @param id The id of the file
	 * @param totalParts The number of parts 
	 * @param size The size of the file in bytes (used to verify)
	 * @param replace Boolean indicating whether to replace an existing file or not
	 * @return String[] The list of created files
	 * @throws IOException
	 */
	String[] combineParts(String path, String file, String id, Integer totalParts, Integer size, boolean replace) throws IOException;
	
	/**
	 * Delete a file
	 * @param file The file
	 * @throws IOException
	 */
	void delete(String file) throws IOException;
	
	/**
	 * Create an alias for a path
	 * @param oldPath The original path
	 * @param newPath The alias
	 * @throws IOException
	 */
	void alias(String oldPath, String newPath) throws IOException;
}
