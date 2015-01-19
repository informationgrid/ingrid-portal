/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.portal.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;

/**
 * Utils class for transform byte array as file and create map service files.
 * 
 * @author ktt
 * 
 */
public class UtilsFileHelper {
	
	private final static Logger	log			= LoggerFactory.getLogger(UtilsFileHelper.class);
	
	public static String		MIME		= "mime";
	public static String		FILE_TITLE	= "file_title";
	public static String		MAP			= "map";
	public static String		GML			= "gml";
	public static String		KML			= "kml";
	
	/**
	 * Convert a byte array to file
	 * 
	 * @param byteFile
	 * @param docid
	 * @param title
	 * @param mimetyp
	 * @return HashMap with file details
	 * @throws IOException
	 */
	public static HashMap<String, String> getByteAsFile(byte[] byteFile, String title, String mimetyp, RenderRequest request) throws IOException {
		
		HashMap<String, String> fileDetails = null;
		File directory = null;
		File file = null;
		String typ = null;
		String parentTyp = null;
		
		if (byteFile != null) {
			
			fileDetails = new HashMap<String, String>();
			typ = UtilsMimeType.getFileExtensionOfMimeType(mimetyp);
			parentTyp = UtilsMimeType.getMimeTypeParent(mimetyp);
			
			if (typ != null) {
				directory = new File(System.getProperty("java.io.tmpdir") + "/ingrid-portal-apps/details/" + parentTyp + "/");
				directory.mkdirs();
				
				file = new File(directory.getAbsolutePath() + "/" + generateFilename(parentTyp, title, new String(byteFile).hashCode(), typ));
				file.createNewFile();
				
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(byteFile);
				fos.close();
				if (log.isDebugEnabled()) {
					log.debug("Create file: " + file.getAbsolutePath());
				}
				
				fileDetails.put("mimetyp", mimetyp);
				fileDetails.put("filename", file.getName());
				fileDetails.put("title", title);
				fileDetails.put("path", file.getAbsolutePath());
				fileDetails.put("parenttyp", parentTyp);
				
				// Set file into servlet session
				request.getPortletSession().setAttribute(file.getName(), file.getAbsolutePath(), PortletSession.APPLICATION_SCOPE);
			}
		}
		return fileDetails;
	}
	
	/**
	 * Create mapfile and GML file for temporary map services
	 * 
	 * @param fileTitle
	 * @return path of mapfile
	 * @throws Exception
	 * @throws ConfigurationException
	 */
	public static String createNewService(String fileTitle, String typ, String path) throws ConfigurationException, Exception {
		File directory;
		File file;
		
		directory = new File(path);
		directory.mkdirs();
		
		file = new File(directory.getAbsolutePath() + "/" + generateFilename(Integer.toString(fileTitle.hashCode()), typ));
		if(file.exists()){
			if (log.isDebugEnabled()) {
				log.debug("File: " + file.getName() + " already exist!");
			}
		}else{
			file.createNewFile();
			if (log.isDebugEnabled()) {
				log.debug("Create " + typ + " File: " + file.getAbsolutePath());
			}
		}
		return file.getName();
	}
	
	/**
	 * Write String into file
	 * 
	 * @param path
	 * @param content
	 * @throws IOException
	 */
	public static void writeContentIntoFile(String path, String content) throws IOException {
		if(path != null){
			File file = new File(path);
			if(file.length() < 1){
				if (content != null) {
					Writer writer = null;
					writer = new FileWriter(path);
					writer.write(content);
					writer.close();		
				}else{
					if (log.isErrorEnabled()) {
						log.error("Content is null!");
					}
				}
			}		
		}
	}
	
	/**
	 * Generate a unique filename for binary data
	 * 
	 * @param parentTyp
	 * @param title
	 * @param byteHashCode
	 * @param type
	 * @return filename
	 */
	public static String generateFilename(String parentTyp, String title, int byteHashCode, String type) {
		String filename;
		
		filename = parentTyp + "_" + title + "_" + byteHashCode + ".".concat(type);
		
		if (title == null || type == null|| parentTyp == null) {
			if (log.isErrorEnabled()) {
				log.error("Title: " + title + "or Type: " + type + "or parentTyp: " + parentTyp + " is null!");
			}
		}
		
		return filename;
	}
	
	/**
	 * Generate a filename with type and title
	 * 
	 * @param date
	 * @param title
	 * @param type
	 * @return filename
	 */
	public static String generateFilename(String title, String type) {
		
		String filename;
	
		filename = title.concat(".").concat(type);
		
		if (title == null || type == null) {
			if (log.isErrorEnabled()) {
				log.error("Title: " + title + "or Type: " + type + " is null!");
			}
		}
		return filename;
	}
			
	/**
	 * Check if record include binary data
	 * 
	 * @param record
	 * @param fileList
	 * @return a ArrayList of files
	 * @throws IOException
	 */
	public static List<Object> extractBinaryData(Record record, List<Object> fileList, RenderRequest request) throws IOException {
		
		if (fileList == null) {
			fileList = new ArrayList<Object>();
		}
		
		Column[] columns = record.getColumns();
		byte[] bytes = null;
		String mime = null;
		String fileTitle = "unknown";
		for (Column column : columns) {
			if (column.getType() != null && column.getType().equals(Column.BINARY)) {
				bytes = record.getValueBytes(column);
			} else if (column.getTargetName().equals(MIME)) {
				mime = record.getValueAsString(column);
			} else if (column.getTargetName().equals(FILE_TITLE)) {
				fileTitle = record.getValueAsString(column);
			}
		}
		if (bytes != null) {
			// handle bytes with mime type and optional title
			fileList.add(getByteAsFile(bytes, fileTitle, mime, request));
		}
		Record[] subRecords = record.getSubRecords();
		for (Record record2 : subRecords) {
			extractBinaryData(record2, fileList, request);
		}
		
		return fileList;
	}
	
	/**
	 * Get bytes of file
	 * 
	 * @param file
	 * @return byte array of a file
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		// Get the size of the file
		long length = file.length();
		
		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
		
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		
		// Close the input stream and return bytes
		is.close();
		
		return bytes;
	}
	
	/**
	 * Sort long values in ArrayList
	 * 
	 * @param fileArray
	 */
	public static void sortFileByDate(ArrayList<Long> fileArray) {
		boolean unsort = true;
		long temp;
		
		while (unsort) {
			unsort = false;
			for (int i = 0; i < fileArray.size() - 1; i++)
				if (fileArray.get(i) > fileArray.get(i + 1)) {
					temp = fileArray.get(i);
					fileArray.set(i, fileArray.get(i + 1));
					fileArray.set(i + 1, temp);
					unsort = true;
				}
		}
		
	}
	
	public static HashMap<String, String> getInstallVersion(String path, String title){
		HashMap<String, String> versionMap = new HashMap<String, String>();
		BufferedReader bufferReader;
		try {
			bufferReader = new BufferedReader(new FileReader(path));
			String input = "";
			while((input = bufferReader.readLine()) != null) {
				if(input.startsWith("Implementation-Build")){
					String value = input.split(":")[1].trim();
					if(value.length() > 0){
						versionMap.put("svn_version", value);
					}
				}else if(input.startsWith("Implementation-Version")){
					String value = input.split(":")[1].trim();
					if(value.length() > 0){
						versionMap.put("project_version", value);	
					}
				}else if(input.startsWith("Build-Timestamp")){
					String value = input.split(":")[1].trim();
					if(value.length() > 0){
					    Date date = new Date(Long.valueOf(value));
						versionMap.put("build_time", date.toString());	
					}
				}
			}
		} catch (IOException e) {
			log.error("File not found: " + path);
		}
		
		if(versionMap.size() > 0){
			versionMap.put("title", title);
		}
		return versionMap;
	}
}
