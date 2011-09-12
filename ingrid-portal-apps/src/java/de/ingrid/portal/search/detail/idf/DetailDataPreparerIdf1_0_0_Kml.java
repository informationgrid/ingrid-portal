package de.ingrid.portal.search.detail.idf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.ConfigurationException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsMapServiceManager;
import de.ingrid.utils.xml.XPathUtils;

//TODO: remove annotation
@SuppressWarnings("unchecked")
public class DetailDataPreparerIdf1_0_0_Kml extends DetailDataPreparerIdf1_0_0{
	
	private final static Logger		log	= LoggerFactory.getLogger(DetailDataPreparerIdf1_0_0_Kml.class);
	
	private final static String		NODE_KML			= "kml:Document";
	
	public DetailDataPreparerIdf1_0_0_Kml(Node node, Context context, RenderRequest request, String iPlugId, RenderResponse response) {
		super(node, context, request, iPlugId, response);
		this.rootNode = node;
		this.context = context;
		this.iPlugId = iPlugId;
		this.response = response;
		messages = (IngridResourceBundle) context.get("MESSAGES");
		sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public void prepare(ArrayList data) {
		initialArrayLists();
		
		if (rootNode != null) {
			if(log.isDebugEnabled()){
				log.debug("Start parsing of: '"+ rootNode.getLocalName() +"'");
			}
			ArrayList<Object> elementsKml = new ArrayList<Object>();
			HashMap<String, Object> kmlDocument;
			Node childNode = XPathUtils.getNode(rootNode, NODE_KML);
			
			kmlDocument = new HashMap<String, Object>();
			List<HashMap<String, String>> kmlDocumentPlacemark = new ArrayList<HashMap<String, String>>();
			String docName =   XPathUtils.getString(childNode,"kml:name").trim();
			kmlDocument.put("kml_name", docName);
			boolean nodeExistPlacemark = XPathUtils.nodeExists(childNode, "kml:Placemark");
			if(nodeExistPlacemark){
				NodeList nodeListPlacemark = XPathUtils.getNodeList(childNode, "kml:Placemark");
				for(int j=0; j<nodeListPlacemark.getLength();j++){
					Node nodePlacemark = nodeListPlacemark.item(j);
					HashMap<String, String> placemark = new HashMap<String, String>();
					String placemarkName = XPathUtils.getString(nodePlacemark, "kml:name").trim();
					placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE, placemarkName);
					String placemarkDescription = XPathUtils.getString(nodePlacemark, "kml:description").trim();
					placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR, placemarkDescription);
					
					String placemarkCoordinates = XPathUtils.getString(nodePlacemark, "kml:Point/kml:coordinates").trim();
					StringTokenizer tokenizer = new StringTokenizer(placemarkCoordinates, " ");
					int iToken = 0;
					while (tokenizer.hasMoreTokens()) {
						switch (iToken) {
							case 0:
								placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_X, tokenizer.nextToken());
								iToken++;
								break;
							case 1:
								placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_Y, tokenizer.nextToken());
								iToken++;
								break;
							default:
								break;
						}
					}
					if(log.isDebugEnabled()){
						log.debug("KML coord x: " + placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_X));
						log.debug("KML coord y: " + placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_Y));
						log.debug("KML coord description: " + placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR));
						log.debug("KML coord title: " + placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE));
					}
					kmlDocumentPlacemark.add(placemark);
				}
				kmlDocument.put("kml_placemark", kmlDocumentPlacemark);
				elementsKml.add(kmlDocument);
			}
			try {
				addKML(data, elementsKml, docName);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void addKML(List elements, List kml, String title) throws UnsupportedEncodingException {
		List<HashMap<String, String>> coords = mergeCoordsByKML(kml);
		String kmlWmsUrl = "";
		
		try {
			
			String mapFilePath = UtilsMapServiceManager.getTmpDirectory().concat(UtilsMapServiceManager.createTemporaryMapService(title, title, coords, "4326"));
			kmlWmsUrl = UtilsMapServiceManager.getConfig().getString("temp_service_server").concat(mapFilePath).concat("&REQUEST=GetCapabilities&SERVICE=WMS&VERSION=1.1.1");
		} catch (ConfigurationException e) {
			if (log.isErrorEnabled()) {
				log.error("ConfigurationException by create temporary service: " + e);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception by create temporary service: " + e);
			}
		}
		
		addSpace(elements);
		HashMap element = new HashMap();
		element.put("type", "kml");
		element.put("title", title);
		if (kmlWmsUrl.length() > 0) {
			element.put("kmlWmsUrl", URLEncoder.encode(kmlWmsUrl, "UTF-8"));
		}
		element.put("body", createTableOfPlacemark(kml));
		elements.add(element);
	}
	
	private ArrayList createTableOfPlacemark(List list) {
		ArrayList kmlTables = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			HashMap kmlTable = new HashMap();
			HashMap table = (HashMap) list.get(i);
			kmlTable.put("type", "table");
			kmlTable.put("subtitle", table.get("kml_name"));
			
			ArrayList head = new ArrayList();
			head.add(messages.getString("kml.placemark.title"));
			head.add(messages.getString("kml.placemark.description"));
			head.add(messages.getString("kml.placemark.longitude"));
			head.add(messages.getString("kml.placemark.latitude"));
			
			kmlTable.put("head", head);
			
			ArrayList body = new ArrayList();
			ArrayList listPlacemark = (ArrayList) table.get("kml_placemark");
			for (int j = 0; j < listPlacemark.size(); j++) {
				HashMap placemark = (HashMap) listPlacemark.get(j);
				ArrayList row = new ArrayList();
				row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_TITLE)));
				row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_DESCR)));
				row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_X)));
				row.add(notNull((String) placemark.get(Settings.RESULT_KEY_WMS_TMP_COORD_Y)));
				
				if (!isEmptyRow(row)) {
					body.add(row);
				}
			}
			kmlTable.put("body", body);
			kmlTables.add(kmlTable);
		}
		
		return kmlTables;
	}
	
	private List mergeCoordsByKML(List kml) {
		List<HashMap<String, String>> pointCoords = new ArrayList<HashMap<String, String>>();
		
		for (int i = 0; i < kml.size(); i++) {
			HashMap kmlDocument = (HashMap) kml.get(i);
			String coordClass = (String) kmlDocument.get("kml_name");
			List<HashMap<String, String>> placemarkList = (List) kmlDocument.get("kml_placemark");
			for (int j = 0; j < placemarkList.size(); j++) {
				HashMap<String, String> placemark = placemarkList.get(j);
				placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_CLASS, coordClass);
				pointCoords.add(placemark);
			}
		}
		
		return pointCoords;
	}
	
}
