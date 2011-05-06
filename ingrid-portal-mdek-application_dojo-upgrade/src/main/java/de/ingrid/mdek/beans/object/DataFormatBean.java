package de.ingrid.mdek.beans.object;

public class DataFormatBean {
	private String name;
	private String version;
	private String compression;
	private String pixelDepth;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCompression() {
		return compression;
	}
	public void setCompression(String compression) {
		this.compression = compression;
	}
	public String getPixelDepth() {
		return pixelDepth;
	}
	public void setPixelDepth(String pixelDepth) {
		this.pixelDepth = pixelDepth;
	}
}
