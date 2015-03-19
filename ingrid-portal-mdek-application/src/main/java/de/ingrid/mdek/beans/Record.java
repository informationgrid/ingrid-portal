package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;

public class Record {
    private String identifier;
    private String title;
    private String uuid;
    private List<String> downloadData = new ArrayList<String>();
    private boolean hasDownloadData = false;
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public boolean isHasDownloadData() {
        return hasDownloadData;
    }
    
    public void setHasDownloadData(boolean hasDownloadData) {
        this.hasDownloadData = hasDownloadData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDownloadData() {
        return downloadData;
    }

    public void setDownloadData(List<String> downloadData) {
        this.downloadData = downloadData;
        if (downloadData.size() > 0) this.hasDownloadData = true;
    }
    
    public void addDownloadData(String data) {
        this.downloadData.add( data );
        this.hasDownloadData = true;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
