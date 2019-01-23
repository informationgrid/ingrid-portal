/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.portal.interfaces.om;


/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class WMSSearchDescriptor {

    public final static int WMS_SEARCH_BBOX = 1;

    public final static int WMS_SEARCH_COMMUNITY_CODE = 2;

    private int type;

    private String typeOfCoordinates;

    private double minX;

    private double minY;

    private double maxX;

    private double maxY;

    private String communityCode;

    /**
     * @return Returns the communityCode.
     */
    public String getCommunityCode() {
        return communityCode;
    }

    /**
     * @param communityCode
     *            The communityCode to set.
     */
    public void setCommunityCode(String communityCode) {
        this.communityCode = communityCode;
    }

    /**
     * @return Returns the maxX.
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * @param maxX
     *            The maxX to set.
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * @return Returns the maxY.
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * @param maxY
     *            The maxY to set.
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     * @return Returns the minX.
     */
    public double getMinX() {
        return minX;
    }

    /**
     * @param minX
     *            The minX to set.
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * @return Returns the minY.
     */
    public double getMinY() {
        return minY;
    }

    /**
     * @param minY
     *            The minY to set.
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return Returns the typeOfCoordinates.
     */
    public String getTypeOfCoordinates() {
        return typeOfCoordinates;
    }

    /**
     * @param typeOfCoordinates
     *            The typeOfCoordinates to set.
     */
    public void setTypeOfCoordinates(String typeOfCoordinates) {
        this.typeOfCoordinates = typeOfCoordinates;
    }

}
