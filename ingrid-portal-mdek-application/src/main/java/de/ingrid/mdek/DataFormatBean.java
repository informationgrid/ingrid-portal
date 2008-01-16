package de.ingrid.mdek;

public class DataFormatBean {
	public String name;
	public String version;
	public String compression;
	public String pixelDepth;

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
