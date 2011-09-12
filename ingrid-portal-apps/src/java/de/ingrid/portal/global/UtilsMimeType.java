package de.ingrid.portal.global;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils class for get file extension
 * or getting a parent type by a MIME type
 * 
 * @author ktt
 */
public class UtilsMimeType {
	
	private static final Logger			log					= LoggerFactory.getLogger(UtilsFileHelper.class);
	private static final String			MIME_TYP_BUNDLE		= "mimetype";
	
	private static Map<String, String>	mimeTypeToExtension	= null;
	private static Map<String, String>	fileMimeType		= null;
	
	/**
	 * Get the Extension of a file by checking of MIME type
	 * 
	 * @param mimeType
	 * @return extension of the MIME type (e.g. image/jpeg -> jpg)
	 */
	public static String getFileExtensionOfMimeType(String mimeType) {
		readMimeTypeBundle();
		
		String ext = (String) mimeTypeToExtension.get(mimeType);
		
		if (ext == null) {
			ext = "dat";
			if (log.isInfoEnabled()) {
				log.info("Extension is not define in bundle for " + mimeType + ", file extension set to .dat");
			}
		}
		return ext;
	}
	
	/**
	 * Reading of MIME type bundle
	 */
	private static synchronized void readMimeTypeBundle() {
		if (mimeTypeToExtension != null)
			return;
		
		mimeTypeToExtension = new HashMap<String, String>();
		fileMimeType = new HashMap<String, String>();
		
		ResourceBundle bundle = ResourceBundle.getBundle(MIME_TYP_BUNDLE);
		for (Enumeration e = bundle.getKeys(); e.hasMoreElements();) {
			String type = (String) e.nextElement();
			try {
				String[] extensions = splitString(bundle.getString(type));
				
				if (mimeTypeToExtension.get(type) == null) {
					mimeTypeToExtension.put(type, extensions[0]);
				}
				
				for (int i = 0; i < extensions.length; i++)
                {
					if (fileMimeType.get (extensions[i]) == null)
					{
						fileMimeType.put (extensions[i], type);
					}
                }
				
			}
			catch (MissingResourceException ex) {
				log.error("ERROR while reading MIME Type" + MIME_TYP_BUNDLE + " with type " + type, ex);
			}
		}
	}
	
	/**
	 * Splitting of reading MIME type bundle string
	 * 
	 * @param str
	 * @return a array of strings
	 */
	private static String[] splitString(String str) {
		String[] temp;
		
		String delimiter = "=";
		temp = str.split(delimiter);
		
		return temp;
	}
	
	/**
	 * Get parent of MIME type (e.g. image, audio, video ...)
	 * 
	 * @param mimetyp
	 * @return parent type of MIME type (e.g. image/jpeg -> image)
	 */
	public static String getMimeTypeParent(String mimetyp) {
		String[] temp;
		
		String delimiter = "/";
		temp = mimetyp.split(delimiter);
		
		return temp[0];
	}
	
	/**
	 * Get MIME Type by file extension 
	 * 
	 * @param file
	 * @return MIME type of file
	 * 
	 */
	public static String getMimeTypByFile(File file){
		String mimeType = null;
        
        // Check ours first.
        readMimeTypeBundle();

        String extension = getExtensionOfFile(file.getName());
        mimeType = (String) fileMimeType.get(extension);

        return mimeType;
	}
	
	
	/**
	 * Splitting of extension from filename
	 * 
	 * @param filename
	 * @return file extension
	 * 
	 */
	private static String getExtensionOfFile(String filename){
		String[] temp;
		
		String delimiter = "\\.";
		temp = filename.split(delimiter);
		
		return temp[1];
	}
}
