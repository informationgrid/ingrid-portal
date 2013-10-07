package de.ingrid.portal.search.detail.idf.part.om;

import de.ingrid.portal.search.detail.idf.part.DetailPartPreparer;

public class RenderElement {

	private String type;
	private String body;
	private DetailPartPreparer preparer;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public DetailPartPreparer getPreparer() {
		return preparer;
	}
	public void setPreparer(DetailPartPreparer preparer) {
		this.preparer = preparer;
	}
}
