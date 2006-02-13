package de.ingrid.portal.om;

import java.util.Date;


public class IngridAnniversary {
    
    private String topicId;
    private String topicName;
    private String dateFrom;
    private String dateTo;
    private String administrativeId;
    private Date fetched;

    
    public String getId() {
        return this.topicId;
    }

    public void setId(String id) {
        this.topicId = id;
    }

    /**
     * @return Returns the administrativeId.
     */
    public String getAdministrativeId() {
        return administrativeId;
    }

    /**
     * @param administrativeId The administrativeId to set.
     */
    public void setAdministrativeId(String administrativeId) {
        this.administrativeId = administrativeId;
    }

    /**
     * @return Returns the dateFrom.
     */
    public String getDateFrom() {
        return dateFrom;
    }

    /**
     * @param dateFrom The dateFrom to set.
     */
    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * @return Returns the dateTo.
     */
    public String getDateTo() {
        return dateTo;
    }

    /**
     * @param dateTo The dateTo to set.
     */
    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * @return Returns the fetched.
     */
    public Date getFetched() {
        return fetched;
    }

    /**
     * @param fetched The fetched to set.
     */
    public void setFetched(Date fetched) {
        this.fetched = fetched;
    }

    /**
     * @return Returns the topicId.
     */
    public String getTopicId() {
        return topicId;
    }

    /**
     * @param topicId The topicId to set.
     */
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    /**
     * @return Returns the topicName.
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @param topicName The topicName to set.
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    
    


}
