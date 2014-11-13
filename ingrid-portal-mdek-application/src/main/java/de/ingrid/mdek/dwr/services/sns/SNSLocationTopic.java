/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
package de.ingrid.mdek.dwr.services.sns;

import java.util.List;

public class SNSLocationTopic {

	private String topicId;
	private String type; 
	private String typeId;	// e.g. use4Type, use6Type, ... 
	private String name;
	private String qualifier;
	private String nativeKey;
	private boolean isExpired;
	private String expiredDate;
	private List<SNSLocationTopic> successors;
	
	// The coordinates are stored as:
	// 		lower left corner longitude, lower left corner latitude, 
	// 		upper right corner longitude, upper right corner latitude 
	public float[] boundingBox;

	public String getTopicId() {
		return topicId;
	}
	public void setTopicId(String id) {
		this.topicId = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public float[] getBoundingBox() {
		return boundingBox;
	}
	public void setBoundingBox(float[] boundingBox) {
		this.boundingBox = boundingBox;
	}
	public void setBoundingBox(float bottomLeftLong, float bottomLeftLat, float upperRightLong, float upperRightLat) {
		this.boundingBox = new float[4];
		this.boundingBox[0] = bottomLeftLong;
		this.boundingBox[1] = bottomLeftLat;
		this.boundingBox[2] = upperRightLong;
		this.boundingBox[3] = upperRightLat;
	}


	public String toString() {
		String result = "[";
		result += "ID: "+this.topicId;
		result += ", Name: "+this.name;
		result += ", Type ID: "+this.typeId;
		result += ", Type: "+this.type;
		result += ", Qualifier: "+this.qualifier;
		result += ", Native Key: "+this.nativeKey;
		if (this.boundingBox != null && this.boundingBox.length == 4) {
			result += ", WGS84Box: "+this.boundingBox[0]+","+this.boundingBox[1]+" "+this.boundingBox[2]+","+this.boundingBox[3];
		}
		result += "]";
		return result;
	}
	public String getNativeKey() {
		return nativeKey;
	}
	public void setNativeKey(String nativeKey) {
		this.nativeKey = nativeKey;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
    public boolean isExpired() {
        return isExpired;
    }
    public void setExpired( boolean isExpired ) {
        this.isExpired = isExpired;
    }
    public String getExpiredDate() {
        return expiredDate;
    }
    public void setExpiredDate( String expiredDate ) {
        this.expiredDate = expiredDate;
    }
    public List<SNSLocationTopic> getSuccessors() {
        return successors;
    }
    public void setSuccessors( List<SNSLocationTopic> successors ) {
        this.successors = successors;
    }
}
