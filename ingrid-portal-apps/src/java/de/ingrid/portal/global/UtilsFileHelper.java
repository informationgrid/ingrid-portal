package de.ingrid.portal.global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;

public class UtilsFileHelper {
	
	private final static Log	log		= LogFactory.getLog(UtilsFileHelper.class);
	
	public static String		PNG		= "image/png";
	public static String		JPEG	= "image/jpeg";
	public static String		GIF		= "image/gif";
	public static String		TIFF	= "image/tiff";
	public static String		MIDI	= "audio/x-midi";
	public static String		AIF		= "audio/x-aiff";
	public static String		XMPEG	= "audio/x-mpeg";
	public static String		WAV		= "audio/x-wav";
	public static String		MPEG	= "video/mpeg";
	public static String		MOV		= "video/quicktime";
	public static String		AVI		= "video/x-msvideo";
	
	public static String		MIME	= "mime";
	public static String		FILE_TITLE	= "file_title";
	
	
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
	public static HashMap<String, String> getByteAsFile(byte[] byteFile, String title, String mimetyp) throws IOException {
		
		HashMap<String, String> fileDetails = new HashMap<String, String>();
		File file = null;
		File newFile = null;
		String typ = null;
		String parentTyp = null;
		
		
		if (byteFile != null) {
			if (mimetyp.equals(PNG) || mimetyp.equals(JPEG) || mimetyp.equals(GIF) || mimetyp.equals(TIFF)) {
				if (mimetyp.equals(PNG)) {
					typ = "png";
				} else if (mimetyp.equals(JPEG)) {
					typ = "jpeg";
				} else if (mimetyp.equals(GIF)) {
					typ = "gif";
				} else if (mimetyp.equals(TIFF)) {
					typ = "tiff";
				}
				
				parentTyp = "image";
				
			} else if (mimetyp.equals(MIDI) || mimetyp.equals(XMPEG) || mimetyp.equals(AIF) || mimetyp.equals(WAV)) {
				if (mimetyp.equals(WAV)) {
					typ = "wav";
				} else if (mimetyp.equals(XMPEG)) {
					typ = "mpeg";
				} else if (mimetyp.equals(AIF)) {
					typ = "aif";
				} else if (mimetyp.equals(MIDI)) {
					typ = "mid";
					
				}
				
				parentTyp = "audio";
								
			} else if (mimetyp.equals(MPEG) || mimetyp.equals(MOV) || mimetyp.equals(AVI)) {
				if (mimetyp.equals(MOV)) {
					typ = "mov";
				} else if (mimetyp.equals(AVI)) {
					typ = "avi";
				} else if (mimetyp.equals(MPEG)) {
					typ = "mpeg";
				}
				
				parentTyp = "video";
				
			}
			
			if(typ != null){
				file = new File(System.getProperty("java.io.tmpdir") + "/ingrid-portal-apps/details/" + parentTyp +"/");
				file.mkdirs();
							
				newFile = new File(file.getAbsolutePath() + "/" + generateFilename(parentTyp, title, new String(byteFile).hashCode(), typ));
				newFile.createNewFile();
				
				FileOutputStream fos = new FileOutputStream(newFile);
				fos.write(byteFile);
				fos.close();
				if (log.isDebugEnabled()) {
					log.debug("Create file: " + newFile.getAbsolutePath());
				}		
				
				fileDetails.put("mimetyp", mimetyp);
				fileDetails.put("filename", newFile.getName());
				fileDetails.put("title", title);
				fileDetails.put("path", newFile.getAbsolutePath());
				fileDetails.put("parenttyp", parentTyp);		
			}
			
				
		}
		return fileDetails;
	}
	
	public static String generateFilename(String parentTyp, String title, int byteHashCode, String typ ){
		
		return parentTyp + "_" + title + "_" + byteHashCode + ".".concat(typ);
	}
	
	/**
	 * Check if record include binary data
	 * 
	 * @param record
	 * @param fileList
	 * @return a ArrayList of files
	 * @throws IOException
	 */
	public static List<Object> handle(Record record, List<Object> fileList) throws IOException {
		
		if (fileList == null) {
			fileList = new ArrayList<Object>();
		}
		
		Column[] columns = record.getColumns();
		byte[] bytes = null;
		String mime = null;
		String fileTitle = "unknown";
		for (Column column : columns) {
			if (column.getType().equals(Column.BINARY)) {
				bytes = record.getValueBytes(column);
			} else if (column.getTargetName().equals(MIME)) {
				mime = record.getValueAsString(column);
			} else if (column.getTargetName().equals(FILE_TITLE)) {
				fileTitle = record.getValueAsString(column);
			} 
		}
		if (bytes != null) {
			// handle bytes with mime type and optional title
			fileList.add(getByteAsFile(bytes, fileTitle, mime));
		}
		Record[] subRecords = record.getSubRecords();
		for (Record record2 : subRecords) {
			handle(record2, fileList);
		}
		
		return fileList;
	}

	/**
	 * Get bytes of file  
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
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        
        return bytes;
    }
	
}
