/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

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
		
		String ext = mimeTypeToExtension.get(mimeType);
		
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
		
		mimeTypeToExtension = new HashMap<>();
		fileMimeType = new HashMap<>();
		
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
			    if (log.isErrorEnabled()) {
			        log.error("ERROR while reading MIME Type" + MIME_TYP_BUNDLE + " with type " + type, ex);
			    }
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
        mimeType = fileMimeType.get(extension);

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
