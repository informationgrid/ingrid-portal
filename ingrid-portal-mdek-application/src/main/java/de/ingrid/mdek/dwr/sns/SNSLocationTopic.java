package de.ingrid.mdek.dwr.sns;

public class SNSLocationTopic {

	public String id;
	public String type; 
	public String name;
	public String qualifier;
	public String nativeKey;
	
	// The coordinates are stored as:
	// 		lower left corner longitude, lower left corner latitude, 
	// 		upper right corner longitude, upper right corner latitude 
	public float[] boundingBox = new float[4];

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
		this.boundingBox[0] = bottomLeftLong;
		this.boundingBox[1] = bottomLeftLat;
		this.boundingBox[2] = upperRightLong;
		this.boundingBox[3] = upperRightLat;
	}


	public String toString() {
		String result = "[";
		result += "ID: "+this.id;
		result += ", Name: "+this.name;
		result += ", Type: "+this.type;
		result += ", Qualifier: "+this.qualifier;
		result += ", WGS84Box: "+this.boundingBox[0]+","+this.boundingBox[1]+" "+this.boundingBox[2]+","+this.boundingBox[3];
		result += "]";
		return result;
	}
	public String getNativeKey() {
		return nativeKey;
	}
	public void setNativeKey(String nativeKey) {
		this.nativeKey = nativeKey;
	}

}
