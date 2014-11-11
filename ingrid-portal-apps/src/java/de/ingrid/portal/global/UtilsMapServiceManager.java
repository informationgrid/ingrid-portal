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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.ingrid.portal.interfaces.IBUSInterface;
import de.ingrid.portal.interfaces.impl.IBUSInterfaceImpl;
import de.ingrid.portal.interfaces.impl.WMSInterfaceImpl;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

public class UtilsMapServiceManager {
	private static final Logger		log	= LoggerFactory.getLogger(UtilsMapServiceManager.class);
	
	private static Configuration 	config;
	private static String tmpDirectory;	
	
	/**
	 * Delete oldest map and gml files if limit is arrives
	 * 
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static void countTempServiceNumber() throws ConfigurationException, Exception {
		
		File path = new File(getTmpDirectory(getConfig().getString("temp_service_path", null)));
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
	 * @param title
	 * @param coords
	 * @param coordTypeForMapFile
	 * @return path to map or gml file
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static String createTemporaryService(String title, List<HashMap<String, String>> coords, String fileType) throws ConfigurationException, Exception {
		String fileName = "";
		
		countTempServiceNumber();
		
		if(fileType.endsWith(UtilsFileHelper.KML)){
			URL url = Thread.currentThread().getContextClassLoader().getResource("../templates/pattern/temporary_service_kml.vm");
			String path = url.getPath();
			
				// Create file
				fileName = UtilsFileHelper.createNewService(title, UtilsFileHelper.KML, getConfig().getString("temp_service_path", null));
				UtilsFileHelper.writeContentIntoFile(getTmpDirectory(getConfig().getString("temp_service_path", null)).concat(fileName), mergeTemplateKML(path, coords, title, fileName));	
		} 
		return fileName;
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
	public static String getTmpDirectory(String path) throws ConfigurationException, Exception {
		if(tmpDirectory == null){
			setTmpDirectory(path.concat("/"));
		}
		return tmpDirectory;
	}

	public static String getKMLTmpDirectory() throws ConfigurationException, Exception {
		if(tmpDirectory == null){
			getTmpDirectory(getConfig().getString("temp_service_path", null));
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
	 * 
	 * Merge values to gml or map template (velocity macro)
	 * 
	 * @param path
	 * @param coords
	 * @param title - title of search hit
	 * @param coordClasses - coords classifications
	 * @param fileName - mapfile name
	 * @param coordClass - null if merge map
	 * @param coordType - EPSG code
	 * @return string value to create map or gml file
	 * @throws ConfigurationException
	 * @throws ConfigurationException
	 * @throws Exception
	 */
	public static String mergeTemplateKML(String path, List<HashMap<String, String>> coords, String title, String fileName) throws ConfigurationException, Exception {
		
		StringWriter sw;
		String templatePath;
		
		VelocityContext context = new VelocityContext();
		context.put("name", title);
		context.put("placemarks", coords);
		
		sw = new StringWriter();
		templatePath = path;
		
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

	public static HashMap createKmlFromIDF(String iPlugId, int documentId) throws ConfigurationException, Exception {
		IBUSInterface ibus = IBUSInterfaceImpl.getInstance();
		IngridHit ingridHit = new IngridHit();
		String kmlFile = "";
		HashMap data = new HashMap(); 
		Record record;
		
		ingridHit.setDocumentId(documentId);
		ingridHit.setPlugId(iPlugId);
		record = ibus.getRecord(ingridHit);
		
		if (record != null) {
			String idfString = record.getString("data");
			if(idfString != null){
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				DocumentBuilder db;
				db = dbf.newDocumentBuilder();
				Document idfDoc = db.parse(new InputSource(new StringReader(idfString)));
				
				Node node;
				XPathUtils.getXPathInstance(new IDFNamespaceContext());
				
				Element rootNode = idfDoc.getDocumentElement();
				if(rootNode != null){
					if(rootNode.hasChildNodes()){
						Node kmlNode = XPathUtils.getNode(rootNode, "./idf:body/kml:kml");
						evaluateKmlNode(data, kmlNode);
					}
				}
				
				if(data != null){
					try {
						kmlFile = UtilsMapServiceManager.createTemporaryService((String) data.get("coord_class"), (ArrayList) data.get("placemarks"), UtilsFileHelper.KML);
					} catch (ConfigurationException e) {
						log.error("ConfigurationException" + e);
					} catch (Exception e) {
						log.error("Exception" + e);
					}
				}
			}
		}
		
		if(kmlFile != null && kmlFile.length() > 0){
			data.put("kml_url", "./kml/" + kmlFile);
			data.put("kml_path", getTmpDirectory(getConfig().getString("temp_service_path", null)));
		}
		return data;
	}

	private static void evaluateKmlNode(HashMap data, Node node) {
		
		if(node.getNamespaceURI().equals(IDFNamespaceContext.NAMESPACE_URI_KML)){
			Node documentNode = XPathUtils.getNode(node, "./kml:Document");
			if(documentNode != null){
				Node titleNode = XPathUtils.getNode(documentNode, "./kml:name");
				String title = "";
				// Titel
				if(titleNode != null){
					title = titleNode.getTextContent().trim();
					data.put("coord_class", title);
				}
				
				//Placemark
				NodeList placemarkNodeList = XPathUtils.getNodeList(documentNode, "./kml:Placemark");
				if(placemarkNodeList != null){
					ArrayList<HashMap<String, String>> placemarks = new ArrayList<HashMap<String, String>>();
					for(int i=0; i < placemarkNodeList.getLength(); i++){
						Node pmNode = placemarkNodeList.item(i);
						HashMap<String, String> placemark = new HashMap<String, String>();
						placemark.put("coord_class", title);
						
						// Name
						Node pmNameNode = XPathUtils.getNode(pmNode, "./kml:name");
						if(pmNameNode != null){
							String name = pmNameNode.getTextContent().trim();
							if(name != null){
								placemark.put("coord_title", name);
							}
						}
						
						// Description
						Node pmDescrNode = XPathUtils.getNode(pmNode, "./kml:description");
						if(pmDescrNode != null){
							String description = pmDescrNode.getTextContent().trim();
							if(description != null){
								placemark.put("coord_descr", description);
							}
						}
						
						// Point
						Node pmPointNode = XPathUtils.getNode(pmNode, "./kml:Point");
						if(pmPointNode != null){
							if(pmPointNode.hasChildNodes()){
								Node coordinatesNode = XPathUtils.getNode(pmPointNode, "./kml:coordinates");
								if(coordinatesNode != null){
									String coordinates = coordinatesNode.getTextContent().trim();
									if(coordinates != null){
										String[] coords = coordinates.split(",");
										placemark.put("coord_x", coords[0]);
										placemark.put("coord_y", coords[1]);
									}
								}
							}
						}
						placemarks.add(placemark);
					}
					data.put("placemarks", placemarks);
				}
			}
		}
	}
	
	/**
	 * get coordinates details from record and fill coordList if coordinates exist
	 * 
	 * @param record 
	 * @param coordList
	 * @param unknown
	 */
	public static void getCoordinatesDetailsFromRecord(Record record, ArrayList<HashMap<String, String>> coordList){
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
			}
				
			if (coordTitle != null && !coordTitle.equals("")) {
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE, coordTitle);
			}
				
			if (coordDescr != null && !coordDescr.equals("")) {
				coords.put(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR, coordDescr);
			}
			coordList.add(coords);
		}
		
		subRecords = record.getSubRecords();
		if(subRecords != null){
			for(int i = 0; i < subRecords.length; i++){
				getCoordinatesDetailsFromRecord(subRecords[i], coordList);	
			}
		}
	}
}
