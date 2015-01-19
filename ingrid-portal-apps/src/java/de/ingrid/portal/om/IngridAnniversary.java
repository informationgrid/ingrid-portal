/*
 * **************************************************-
 * Ingrid Portal Apps
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
package de.ingrid.portal.om;

import java.util.Date;


public class IngridAnniversary {
    
    private Long id;
    private String topicId;
    private String topicName;
    private String dateFrom;
    private Integer dateFromYear;
    private Integer dateFromMonth;
    private Integer dateFromDay;
    private String dateTo;
    private Integer dateToYear;
    private Integer dateToMonth;
    private Integer dateToDay;
    private String administrativeId;
    private Date fetched;
    private Date fetchedFor;
    private String language;

    
    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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
     * @return Returns the dateFromDay.
     */
    public Integer getDateFromDay() {
        return dateFromDay;
    }

    /**
     * @param dateFromDay The dateFromDay to set.
     */
    public void setDateFromDay(Integer dateFromDay) {
        this.dateFromDay = dateFromDay;
    }

    /**
     * @return Returns the dateFromMonth.
     */
    public Integer getDateFromMonth() {
        return dateFromMonth;
    }

    /**
     * @param dateFromMonth The dateFromMonth to set.
     */
    public void setDateFromMonth(Integer dateFromMonth) {
        this.dateFromMonth = dateFromMonth;
    }

    /**
     * @return Returns the dateFromYear.
     */
    public Integer getDateFromYear() {
        return dateFromYear;
    }

    /**
     * @param dateFromYear The dateFromYear to set.
     */
    public void setDateFromYear(Integer dateFromYear) {
        this.dateFromYear = dateFromYear;
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
     * @return Returns the dateToDay.
     */
    public Integer getDateToDay() {
        return dateToDay;
    }

    /**
     * @param dateToDay The dateToDay to set.
     */
    public void setDateToDay(Integer dateToDay) {
        this.dateToDay = dateToDay;
    }

    /**
     * @return Returns the dateToMonth.
     */
    public Integer getDateToMonth() {
        return dateToMonth;
    }

    /**
     * @param dateToMonth The dateToMonth to set.
     */
    public void setDateToMonth(Integer dateToMonth) {
        this.dateToMonth = dateToMonth;
    }

    /**
     * @return Returns the dateToYear.
     */
    public Integer getDateToYear() {
        return dateToYear;
    }

    /**
     * @param dateToYear The dateToYear to set.
     */
    public void setDateToYear(Integer dateToYear) {
        this.dateToYear = dateToYear;
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

    /**
     * @return Returns the fetchedFor.
     */
    public Date getFetchedFor() {
        return fetchedFor;
    }

    /**
     * @param fetchedFor The fetchedFor to set.
     */
    public void setFetchedFor(Date fetchedFor) {
        this.fetchedFor = fetchedFor;
    }




}
