package de.ingrid.portal.search.detail.idf.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.IngridSysCodeList;
import de.ingrid.portal.global.Settings;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XPathUtils;

public class DetailPartPreparerIdfKml extends DetailPartPreparer{

	@Override
	public void init(Node node, String iPlugId, RenderRequest request, RenderResponse response, Context context) {
		this.rootNode = node;
		this.iPlugId = iPlugId;
		this.request = request;
		this.response = response;
		this.uuid = this.request.getParameter("docuuid");
		this.templateName = "/WEB-INF/templates/detail/part/detail_part_preparer_kml.vm";
		this.namespaceUri = IDFNamespaceContext.NAMESPACE_URI_IDF;
		this.localTagName = "kml";
		this.context = context;
		this.messages = (IngridResourceBundle) context.get("MESSAGES");
		this.sysCodeList = new IngridSysCodeList(request.getLocale());
	}
	
	public HashMap getKML(){
		HashMap element = new HashMap();
		if (rootNode != null) {
			ArrayList<Object> elementsKml = new ArrayList<Object>();
			HashMap<String, Object> kmlDocument;
			Node childNode = XPathUtils.getNode(rootNode, "./kml:Document");
			
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
					if(placemarkCoordinates.indexOf(" ") > -1){
						String [] coordinates = placemarkCoordinates.split(" ");
						placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_X, coordinates[0]);
						placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_Y, coordinates[1]);
						kmlDocumentPlacemark.add(placemark);
					}else if(placemarkCoordinates.indexOf(",") > -1){
						String [] coordinates = placemarkCoordinates.split(",");
						placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_X, coordinates[0]);
						placemark.put(Settings.RESULT_KEY_WMS_TMP_COORD_Y, coordinates[1]);
						kmlDocumentPlacemark.add(placemark);
					}
				}
				kmlDocument.put("kml_placemark", kmlDocumentPlacemark);
				elementsKml.add(kmlDocument);
			}
			element = addKmlToContext(elementsKml, docName);
		}
		return element;
	}
	
	private HashMap addKmlToContext(List kml, String title) {
		List<HashMap<String, String>> coords = mergeCoordsByKML(kml, title);
		
		HashMap element = new HashMap();
		element.put("type", "kml");
		element.put("title", title);
		String plugId = this.request.getParameter("plugid");
		String docId = this.request.getParameter("docid");
		if (plugId != null && docId != null) {
			element.put("docId", docId);
			element.put("plugId", plugId);
		}
		element.put("body", createTableOfPlacemark(kml));
		return element;
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
	
	private List mergeCoordsByKML(List kml, String title) {
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
