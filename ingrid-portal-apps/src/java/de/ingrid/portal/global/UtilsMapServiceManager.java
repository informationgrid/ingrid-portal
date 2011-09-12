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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;

public class UtilsMapServiceManager {
	private static final Logger		log	= LoggerFactory.getLogger(UtilsMapServiceManager.class);
	
	private static final String EPSG_WGS84 = "4326";
	private static final String EPSG_GK2 = "31466";
	private static final String EPSG_GK3 = "31467";
	private static final String EPSG_GK4 = "31468";
	private static final String EPSG_GK5 = "31469";
	private static final String EPSG_ETRS89_UTM31N = "25831";
	private static final String EPSG_ETRS89_UTM32N = "25832";
	private static final String EPSG_ETRS89_UTM33N = "25833";
	private static final String EPSG_ETRS89_UTM34N = "25834";
	
	// Extent values for map file (look: temporary_service_map.vm -> $extentValue)
	private static final String EXTENT_MAP_VALUE_WGS84 = "5.7 47.2 15.2 55.1";
	private static final String EXTENT_MAP_VALUE_GK2 = "2477313.9769 5229085.1837 3086438.5573 6146679.8967";
	private static final String EXTENT_MAP_VALUE_GK3 = "3250040.4269 5234325.3069 3895577.6631 6125505.7752";
	private static final String EXTENT_MAP_VALUE_GK4 = "4022820.1798 5248326.7032 4704335.6598 6112602.4899";
	private static final String EXTENT_MAP_VALUE_GK5 = "4795707.7336 5271157.5756 5512899.2999 6107936.0574";
	private static final String EXTENT_MAP_VALUE_ETRS89_UTM31N = "-204149.4531 5269499.1171 512761.8588 6105937.6815";
	private static final String EXTENT_MAP_VALUE_ETRS89_UTM32N = "32250063.8076 5232673.7266 32895350.5034 6123491.3426";
	private static final String EXTENT_MAP_VALUE_ETRS89_UTM33N = "32795850.5469 5269499.1171 33512761.8588 6105937.6815";
	private static final String EXTENT_MAP_VALUE_ETRS89_UTM34N = "250063.8076 5269499.1171 895350.5034 6123491.3426";
	
	private static Configuration 	config;
	private static String tmpDirectory;	
	
	/**
	 * Delete oldest map and gml files if limit is arrives
	 * 
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static void countTempServiceNumber() throws ConfigurationException, Exception {
		
		File path = new File(getTmpDirectory());
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
	 * @param coordTypeForMapFile
	 * @return path to map or gml file
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static String createTemporaryMapService(String hitTitle, String fileTitle, List<HashMap<String, String>> pointCoords, String coordTypeForMapFile) throws ConfigurationException, Exception {
		String mapFileName = null;
		ArrayList<HashMap<String, String>> coordClasses;
		coordClasses = new ArrayList<HashMap<String, String>>();
		
		if(coordTypeForMapFile == null || coordTypeForMapFile.equals("null")){
			coordTypeForMapFile = "4326";
		}
		
		countTempServiceNumber();
		
		// Create Map file
		mapFileName = UtilsFileHelper.createNewMapService(fileTitle, UtilsFileHelper.MAP);
		
		// Check for different point classes and create GML files
		coordClasses = checkHitClassification(pointCoords, mapFileName);
		
		// Put Map data to file
		URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/temporary_service_map.vm");
		String templatePath = url.getPath();
		
		UtilsFileHelper.writeContentIntoFile(getTmpDirectory().concat(mapFileName), mergeTemplateMap(templatePath, pointCoords, hitTitle, coordClasses, mapFileName, coordTypeForMapFile));
		
		// Put GML data to file
		for (int i = 0; i < coordClasses.size(); i++) {
			url = Thread.currentThread().getContextClassLoader().getResource("../templates/temporary_service_gml.vm");
			templatePath = url.getPath();
			
			UtilsFileHelper.writeContentIntoFile(coordClasses.get(i).get("coordGMLPath"), mergeTemplateMap(templatePath, pointCoords, hitTitle, coordClasses, mapFileName, coordClasses.get(i).get(
					"coordClassName"), coordTypeForMapFile));
		}
		
		return mapFileName;
	}
	
	/**
	 * Set values of wms property file
	 * 
	 * @param config
	 */
	public static void setConfig(Configuration config) {
		UtilsMapServiceManager.config = config;
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
	 * Get tmp directory
	 * 
	 * @return tmp directory
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static String getTmpDirectory() throws ConfigurationException, Exception {
		if(tmpDirectory == null){
			setTmpDirectory(System.getProperty("java.io.tmpdir").concat(getConfig().getString("temp_service_path", null)).concat("/"));
		}
		return tmpDirectory;
	}

	/**
	 * Set tmp directory
	 * 
	 * @param tmpDirectory
	 */
	public static void setTmpDirectory(String tmp_directory) {
		UtilsMapServiceManager.tmpDirectory = tmp_directory;
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
				coordClassDetail.put("coordGMLPath", getTmpDirectory().concat(UtilsFileHelper.createNewMapService(Integer.toString(mapFile.concat(coordClassName).hashCode()), UtilsFileHelper.GML)));
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
	 * Merge values only to map template
	 * 
	 * @param pathToTemplateFile
	 * @param coordPointDetails
	 * @param hitTitle - title of search hit
	 * @param coordClasses - coords classifications
	 * @param mapFileName - mapfile name
	 * @param coordType - EPSG code
	 * @return string value to create only map file
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	
	public static String mergeTemplateMap(String pathToTemplateFile, List<HashMap<String, String>> coordPointDetails, String hitTitle, ArrayList<HashMap<String, String>> coordClasses,
			String mapFileName, String coordType) throws ConfigurationException, Exception {
		
		return mergeTemplateMap(pathToTemplateFile, coordPointDetails, hitTitle, coordClasses, mapFileName, null, coordType);
	}
	
	/**
	 * 
	 * Merge values to gml or map template (velocity macro)
	 * 
	 * @param pathToTemplateFile
	 * @param coordPointDetails
	 * @param hitTitle - title of search hit
	 * @param coordClasses - coords classifications
	 * @param mapFileName - mapfile name
	 * @param coordClass - null if merge map
	 * @param coordType - EPSG code
	 * @return string value to create map or gml file
	 * @throws ConfigurationException
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static String mergeTemplateMap(String pathToTemplateFile, List<HashMap<String, String>> coordPointDetails, String hitTitle, ArrayList<HashMap<String, String>> coordClasses,
			String mapFileName, String coordClass, String coordType) throws ConfigurationException, Exception {
		
		StringWriter sw;
		String templatePath;
		
		VelocityContext context = new VelocityContext();
		context.put("hitTitle", hitTitle);
		context.put("coordType", coordType);
		if(coordClass != null){
			context.put("coordClass", coordClass);
		}
		context.put("coordClasses", coordClasses);
		context.put("coordPointDetails", coordPointDetails);
		context.put("pathToSymbolDirectory", UtilsMapServiceManager.getConfig().getString("temp_service_symbol", null));
		context.put("pathToFontDirectory", UtilsMapServiceManager.getConfig().getString("temp_service_font", null));
		context.put("pathToMapServer", UtilsMapServiceManager.getConfig().getString("temp_service_server", null));
		context.put("pathToHTMLTemplate", UtilsMapServiceManager.getConfig().getString("temp_service_html_temp", null));
		context.put("pathToMapFile", getTmpDirectory().concat(mapFileName));
		context.put("extentValue", getExtentValueForEPSG(coordType));
		
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
		Record record;
		
		ingridHit.setDocumentId(documentId);
		ingridHit.setPlugId(iPlugId);
		record = ibus.getRecord(ingridHit);
		
		if (record != null) {
			getCoordinatesDetailsFromRecord(record, coordList, unknown);
		}
		return coordList;
	}

	/**
	 * get coordinates details from record and fill coordList if coordinates exist
	 * 
	 * @param record 
	 * @param coordList
	 * @param unknown
	 */
	public static void getCoordinatesDetailsFromRecord(Record record, ArrayList<HashMap<String, String>> coordList, String unknown){
		HashMap<String, String> coords;
		Column[] columns;
		Record[] subRecords;
		
		String coordX = null;
		String coordY = null;
		String coordClass = null;
		String coordTitle = null;
		String coordDescr = null;
				
		columns = record.getColumns();
		for (Column column : columns) {
			if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_X)) {
				coordX = record.getValueAsString(column);
			}else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_Y)) {
				coordY = record.getValueAsString(column);
			}else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS)) {
				coordClass = record.getValueAsString(column);
			}else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE)) {
				coordTitle = record.getValueAsString(column);
			}else if (column.getTargetName().equals(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR)) {
				coordDescr = record.getValueAsString(column);
			}
		}
				
		if (coordX != null && coordY != null) {
			coords = new HashMap<String, String>();
			coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_X, coordX);
			coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_Y, coordY);
				
			if (coordClass != null && !coordClass.equals("")) {
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS, coordClass);
			}else{
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS, unknown);
			}
				
			if (coordTitle != null && !coordTitle.equals("")) {
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE, coordTitle);
			}else{
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE, unknown);
			}
				
			if (coordDescr != null && !coordDescr.equals("")) {
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR, coordDescr);
			}else{
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR, unknown);
			}
			coordList.add(coords);
		}
		
		subRecords = record.getSubRecords();
		if(subRecords != null){
			for(int i = 0; i < subRecords.length; i++){
				getCoordinatesDetailsFromRecord(subRecords[i], coordList, unknown);	
			}
		}
	}
	
	/**
	 * Get extent value for map file in used EPSG Code
	 * 
	 * @param epsg
	 * 
	 * @return extent value
	 */
	public static String getExtentValueForEPSG(String epsg){
		String extentValue;
		
		if (epsg.equals(EPSG_WGS84)) {
			extentValue = EXTENT_MAP_VALUE_WGS84;
		}else if (epsg.equals(EPSG_GK2)) {
			extentValue = EXTENT_MAP_VALUE_GK2;
		}else if (epsg.equals(EPSG_GK3)) {
			extentValue = EXTENT_MAP_VALUE_GK3;
		}else if (epsg.equals(EPSG_GK4)) {
			extentValue = EXTENT_MAP_VALUE_GK4;
		}else if (epsg.equals(EPSG_GK5)) {
			extentValue = EXTENT_MAP_VALUE_GK5;
		}else if (epsg.equals(EPSG_ETRS89_UTM31N)) {
			extentValue = EXTENT_MAP_VALUE_ETRS89_UTM31N;
		}else if (epsg.equals(EPSG_ETRS89_UTM32N)) {
			extentValue = EXTENT_MAP_VALUE_ETRS89_UTM32N;
		}else if (epsg.equals(EPSG_ETRS89_UTM33N)) {
			extentValue = EXTENT_MAP_VALUE_ETRS89_UTM33N;
		}else if (epsg.equals(EPSG_ETRS89_UTM34N)) {
			extentValue = EXTENT_MAP_VALUE_ETRS89_UTM34N;
		}else{
			extentValue = EXTENT_MAP_VALUE_WGS84;
		}
		return extentValue;
	}
}
