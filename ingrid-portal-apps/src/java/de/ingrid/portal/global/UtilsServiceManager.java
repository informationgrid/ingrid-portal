package de.ingrid.portal.global;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;

public class UtilsServiceManager {
	private static final Log		log	= LogFactory.getLog(UtilsServiceManager.class);
	
	private static Configuration	config;
	public static String tmp_directory;	
	
	/**
	 * Delete oldest map and gml files if limit is arrives
	 * 
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static void countTempServiceNumber() throws ConfigurationException, Exception {
		tmp_directory = System.getProperty("java.io.tmpdir").concat(getConfig().getString("temp_service_path", null)).concat("/");
		File path = new File(tmp_directory);
		ArrayList<Long> fileArray = new ArrayList<Long>();
		if (path.exists()) {
			if (path.list().length > Integer.parseInt(getConfig().getString("temp_service_limit", null))) {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						fileArray.add(files[i].lastModified());
					}
				}
				
				UtilsFileHelper.sortFileByDate(fileArray);
				for (int i = 0; i < files.length; i++) {
					if (files[i].lastModified() == fileArray.get(0)) {
						files[i].delete();
						if(log.isDebugEnabled()){
							log.debug("Delete temporay service " + files[i].getName());
						}
					}
				}
				
				if (path.list().length > Integer.parseInt(getConfig().getString("temp_service_limit", null))) {
					countTempServiceNumber();
				}
			}
		}
	}
	
	/**
	 * Create map and there gml files (per classification) for temporary service
	 * 
	 * @param hitTitle
	 * @param pointCoords
	 * @return path to map or gml file
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static String createTemporaryMapService(String hitTitle, String fileTitle, List<HashMap<String, String>> pointCoords) throws ConfigurationException, Exception {
		String mapFileName = null;
		ArrayList<HashMap<String, String>> coordClasses;
		coordClasses = new ArrayList<HashMap<String, String>>();
		
		countTempServiceNumber();
		
		// Create Map file
		mapFileName = UtilsFileHelper.createNewMapService(fileTitle, UtilsFileHelper.MAP);
		
		// Check for different point classes and create GML files
		coordClasses = checkHitClassification(pointCoords, mapFileName);
		
		// Put Map data to file
		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/temporary_service_map.vm");
		String templatePath = url.getPath();
		
		UtilsFileHelper.writeContentIntoFile(tmp_directory.concat(mapFileName), mergeTemplateMap(templatePath, pointCoords, hitTitle, coordClasses, mapFileName, ""));
		
		// Put GML data to file
		for (int i = 0; i < coordClasses.size(); i++) {
			url = Thread.currentThread().getContextClassLoader().getResource("../templates/temporary_service_gml.vm");
			templatePath = url.getPath();
			
			UtilsFileHelper.writeContentIntoFile(coordClasses.get(i).get("coordGMLPath"), mergeTemplateMap(templatePath, pointCoords, hitTitle, coordClasses, mapFileName, coordClasses.get(i).get(
					"coordClassName")));
		}
		
		return mapFileName;
	}
	
	/**
	 * Set values of wms property file
	 * 
	 * @param config
	 */
	public static void setConfig(Configuration config) {
		UtilsServiceManager.config = config;
	}
	
	/**
	 * Get values of wms property file
	 * 
	 * @return config
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static Configuration getConfig() throws ConfigurationException, Exception {
		
		if (config == null) {
			setConfig(new PropertiesConfiguration(WMSInterfaceImpl.getResourceAsStream("/wms_interface.properties")));
		}
		
		return config;
	}
	
	/**
	 * Check classifications of point in a hit have
	 * 
	 * @param coordPointDetails
	 * @param mapFile
	 * @return list of different classifications
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static ArrayList<HashMap<String, String>> checkHitClassification(List<HashMap<String, String>> coordPointDetails, String mapFile) throws ConfigurationException, Exception {
		ArrayList<HashMap<String, String>> coordClassesDetail = new ArrayList<HashMap<String, String>>();
		List<String> coordClasses = new ArrayList<String>();
		HashMap<String, String> coordClassDetail;
		Boolean foundInList = false;
		int colorCount = 0;
		
		for (int i = 0; i < coordPointDetails.size(); i++) {
			
			String coordClassName = coordPointDetails.get(i).get(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS);
			
			if (coordClasses == null) {
				coordClasses = new ArrayList<String>();
			}
			
			if (coordClassesDetail == null) {
				coordClassesDetail = new ArrayList<HashMap<String, String>>();
			}
			
			foundInList = checkExistingClassifications(coordClassName, coordClasses);
			
			if (!foundInList) {
				colorCount = colorCount + 1;
				coordClasses.add(coordClassName);
				
				coordClassDetail = new HashMap<String, String>();
				coordClassDetail.put("coordClassName", coordClassName);
				coordClassDetail.put("coordClassColor", getConfig().getString("temp_service_color_" + colorCount, null));
				coordClassDetail.put("coordGMLPath", tmp_directory.concat(UtilsFileHelper.createNewMapService(Integer.toString(mapFile.concat(coordClassName).hashCode()), UtilsFileHelper.GML)));
				coordClassesDetail.add(coordClassDetail);
				if (colorCount > 15) {
					colorCount = 0;
				}
			}
		}
		return coordClassesDetail;
	}
	
	/**
	 * Check for different classifications of a point
	 * 
	 * @param value
	 * @param coordClasses
	 * @return if not in array return true
	 */
	public static Boolean checkExistingClassifications(String value, List<String> coordClasses) {
		Iterator<String> it = coordClasses.iterator();
		boolean found = false;
		
		while (it.hasNext() && !found) {
			if (it.next().equals(value)) {
				found = true;
			}
		}
		return found;
	}
	
	/**
	 * Merge values to gml or map template (velocity macro)
	 * 
	 * @param pathToTemplateFile
	 * @param coordPointDetails
	 * @param hitTitle
	 * @param coordClasses
	 * @param mapFileName
	 * @param coordClassForGML
	 * @return string value to create map or gml file
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static String mergeTemplateMap(String pathToTemplateFile, List<HashMap<String, String>> coordPointDetails, String hitTitle, ArrayList<HashMap<String, String>> coordClasses,
			String mapFileName, String coordClassForGML) throws ConfigurationException, Exception {
		
		StringWriter sw;
		String templatePath;
		
		VelocityContext context = new VelocityContext();
		context.put("hitTitle", hitTitle);
		context.put("coordClass", coordClassForGML);
		context.put("coordClasses", coordClasses);
		context.put("coordPointDetails", coordPointDetails);
		context.put("pathToSymbolDirectory", UtilsServiceManager.getConfig().getString("temp_service_symbol", null));
		context.put("pathToFontDirectory", UtilsServiceManager.getConfig().getString("temp_service_font", null));
		context.put("pathToMapServer", UtilsServiceManager.getConfig().getString("temp_service_server", null));
		context.put("pathToHTMLTemplate", UtilsServiceManager.getConfig().getString("temp_service_html_temp", null));
		context.put("pathToMapFile", tmp_directory.concat(mapFileName));
		
		sw = new StringWriter();
		templatePath = pathToTemplateFile;
		
		try {
			BufferedReader templateReader = new BufferedReader(new InputStreamReader(new FileInputStream(templatePath), "UTF8"));
			Velocity.init();
			Velocity.evaluate(context, sw, "TemporaryService", templateReader);
		} catch (Exception e) {
			log.error("failed to merge velocity template: " + templatePath, e);
		}
		String buffer = sw.getBuffer().toString();
		return buffer;
	}
	
	/**
	 * Get the point and there details/values like classification, name, description
	 * 
	 * @param iPlugId
	 * @param documentId
	 * @param unknown
	 * @return list of points and there details
	 */
	public static ArrayList<HashMap<String, String>> getCoordinatesDetails(String iPlugId, int documentId, String unknown) {
		IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
		IngridHit ingridHit = new IngridHit();
		ArrayList<HashMap<String, String>> coordList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> coords;
		Record record;
		Record[] records;
		Column[] columns;
		
		ingridHit.setDocumentId(documentId);
		ingridHit.setPlugId(iPlugId);
		record = ibus.getRecord(ingridHit);
		
		if (record != null) {
			records = record.getSubRecords();
			
			for (int i = 0; i <= records.length - 1; i++) {
				String coordX = null;
				String coordY = null;
				String coordClass = null;
				String coordTitle = null;
				String coordDescr = null;
				
				columns = records[i].getColumns();
				for (Column column : columns) {
					if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_X)) {
						coordX = records[i].getValueAsString(column);
					}

					else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_Y)) {
						coordY = records[i].getValueAsString(column);
					}

					else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS)) {
						coordClass = records[i].getValueAsString(column);
					}

					else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE)) {
						coordTitle = records[i].getValueAsString(column);
					}

					else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR)) {
						coordDescr = records[i].getValueAsString(column);
					}
				}
				
				if (coordX != null && coordY != null) {
					coords = new HashMap<String, String>();
					coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_X, coordX);
					coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_Y, coordY);
					
					if (coordClass != null && !coordClass.equals("")) {
						coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS, coordClass);
					} else {
						coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS, unknown);
					}
					
					if (coordTitle != null && !coordTitle.equals("")) {
						coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE, coordTitle);
					} else {
						coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE, unknown);
					}
					
					if (coordDescr != null && !coordDescr.equals("")) {
						coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR, coordDescr);
					} else {
						coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR, unknown);
					}
					coordList.add(coords);
				}
			}
		}else{
			if(log.isDebugEnabled()){
				log.debug("Record is null!");
			}
		}
		return coordList;
	}
	
}
