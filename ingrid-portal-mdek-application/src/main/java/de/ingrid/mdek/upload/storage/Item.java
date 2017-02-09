package de.ingrid.mdek.upload.storage;

/**
 * Item represents an item in the storage.
 */
public class Item {

    private String uri;
    private String type;
    private long size;

    /**
     * Constructor
     */
    public Item() {}

    /**
     * Constructor
     * @param location
     * @param type
     * @param size
     */
    public Item(String location, String type, long size) {
        super();
        this.uri = location;
        this.type = type;
        this.size = size;
    }

    /**
     * Set the URI
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Get the URI
     * @return String
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * Set the type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the type
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * Set the size
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Get the size
     * @return long
     */
    public long getSize() {
        return this.size;
    }
}
