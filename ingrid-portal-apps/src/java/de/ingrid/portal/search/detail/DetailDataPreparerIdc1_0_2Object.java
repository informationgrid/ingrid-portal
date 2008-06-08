/**
 * 
 */
package de.ingrid.portal.search.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.context.Context;

import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.portal.global.IngridResourceBundle;
import de.ingrid.portal.global.Settings;
import de.ingrid.portal.global.UtilsVelocity;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.udk.UtilsDate;

/**
 * @author joachim
 *
 */
public class DetailDataPreparerIdc1_0_2Object implements DetailDataPreparer {

	private Context context;
	private String iPlugId;
	private RenderRequest request;
	private RenderResponse response;
	private IngridResourceBundle messages;
	
	public DetailDataPreparerIdc1_0_2Object(Context context, String iPlugId, RenderRequest request, RenderResponse response) {
		this.context = context;
		this.iPlugId = iPlugId;
		this.request = request;
		this.response = response;
		messages = (IngridResourceBundle)context.get("MESSAGES");
	}
	
	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.detail.DetailDataPreparer#prepare(de.ingrid.utils.dsc.Record)
	 */
	public void prepare(Record record) {
		String str = null;
		HashMap data = new HashMap();
		HashMap general = new HashMap();
		general.put("title", record.getString("t01_object.obj_name"));
		general.put("modTime", UtilsDate.convertDateString(record.getString("t01_object.mod_time").trim(), "yyyyMMddHHmmssSSS", "dd.MM.yyyy"));
		str = record.getString("t01_object.obj_class");
		if (str != null) {
			general.put("udkObjClass", str);
			general.put("udkObjClassName", messages.getString("udk_obj_class_name_".concat(str)));
		}

		data.put("general", general);
		
		ArrayList elements = new ArrayList();

		// alternate name
		addElementEntry(elements, record.getString("t01_object.dataset_alternate_name"), null);
		// udk class
		addElementUdkClass(elements, record.getString("t01_object.obj_class"));
		// description
		addElementEntry(elements, record.getString("t01_object.obj_descr"), null);
		
		// superior objects
		addSuperiorObjects(elements, record.getString("t01_object.obj_uuid"));
		
		
		data.put("elements", elements);
		
		context.put("data", data);
	}

	private void addElementEntry(List elements, String body, String title) {
		if (UtilsVelocity.hasContent(body).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "entry");
			element.put("title", title);
			element.put("body", body);
			elements.add(element);
		}
	}

	private void addElementUdkClass(List elements, String udkClass) {
		if (UtilsVelocity.hasContent(udkClass).booleanValue()) {
			HashMap element = new HashMap();
			element.put("type", "udkClass");
			element.put("udkClass", udkClass);
			element.put("udkClassName", messages.getString("udk_obj_class_name_".concat(udkClass)));
			elements.add(element);
		}
	}
	
    private void addSuperiorObjects(List elements, String objId) {
        ArrayList result = DetailDataPreparerHelper.getHits("children.object_node.obj_uuid:".concat(objId).concat(
                " iplugs:\"".concat(DetailDataPreparerHelper.getPlugIdFromAddressPlugId(iPlugId)).concat("\"")), new String[] {}, null);
        ArrayList linkList = new ArrayList();
        for (int i=0; i<result.size(); i++) {
        	IngridHit hit = (IngridHit)result.get(i);
        	HashMap link = new HashMap();
        	link.put("hasLinkIcon", new Boolean(true));
        	link.put("isExtern", new Boolean(false));
        	link.put("title", ((HashMap)hit.get("detail")).get("title"));
        	PortletURL actionUrl = response.createActionURL();
        	actionUrl.setParameter("cmd", "doShowDocument");
    		actionUrl.setParameter("docid", hit.getId().toString());
    		actionUrl.setParameter("plugid", iPlugId);
    		if (hit.getString("alt_document_id") != null) {
    			actionUrl.setParameter("altdocid", hit.getString("alt_document_id"));
    		}
        	link.put("href", actionUrl.toString());
        	linkList.add(link);
        }
        if (!linkList.isEmpty()) {
        	HashMap element = new HashMap();
        	element.put("type", "linkList");
        	element.put("title", messages.getString("superior_references"));
        	element.put("linkList", linkList);
        	elements.add(element);
        }
    }
	
}
