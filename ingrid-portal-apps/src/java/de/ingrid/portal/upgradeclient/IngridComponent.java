package de.ingrid.portal.upgradeclient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ingrid.portal.scheduler.jobs.IngridAbstractStateJob;

public class IngridComponent implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6373648235772800709L;
    
    public static final String PARTNER_INFO  = "PARTNER_INFO";
    
    public static final String PROVIDER_INFO = "PROVIDER_INFO";
    
    private String id;
    private String name;
    private String type;
    private String version;
    private String versionAvailable;
    private String downloadLink;
    
    private Date emailSent;
    private String infoText;
    private List<String> emails;
    private boolean hasBeenSent;
    private boolean isIPlug;
    private boolean wasUnknown;
    private String status;
    
    private Map<String, Object> extraInfo = new HashMap<String, Object>();
    
    public IngridComponent(String id, String type) {
        this.setId(id);
        this.setName(id);
        this.setType(type);
        
        this.emails = new ArrayList<String>();
        setStatus(IngridAbstractStateJob.STATUS_IS_MANUALLY_CONNECTED);
        setIPlug(false);
        setHasBeenSent(false);
        //this.setVersion(version);
        //this.setVersionAvailable(versionAvailable);
        //this.setDate(date);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setEmailSentDate(Date date) {
        this.emailSent = date;
    }

    public Date getEmailSentDate() {
        return emailSent;
    }
/*
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
*/
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setVersionAvailable(String versionAvailable) {
        this.versionAvailable = versionAvailable;
    }

    public String getVersionAvailable() {
        return versionAvailable;
    }

    public void setIPlug(boolean isIPlug) {
        this.isIPlug = isIPlug;
    }

    public boolean isIPlug() {
        return isIPlug;
    }

    public void addEmail(String email) {
        this.emails.add(email);
    }

    public List<String> getEmails() {
        return emails;
    }
    
    public void removeEmail(String[] emails) {
        for (String email : emails) {
            this.emails.remove(email);
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setWasUnknown(boolean wasUnknown) {
        this.wasUnknown = wasUnknown;
    }

    public boolean wasUnknown() {
        return wasUnknown;
    }

    public void setInfo(String text) {
        infoText = text;
    }
    
    public String getInfo() {
        return infoText;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }

    public void setHasBeenSent(boolean hasBeenSent) {
        this.hasBeenSent = hasBeenSent;
    }

    public boolean hasBeenSent() {
        return hasBeenSent;
    }
    
    public void addExtraInfo(String key, Object value) {
        extraInfo.put(key, value);
    }
    
    public Object getExtraInfo(String key) {
        return extraInfo.get(key);
    }
    
    public void removeExtraInfo(String key) {
        extraInfo.remove(key);
    }
}
