/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.portal.global;

import de.ingrid.portal.search.IPlugVersionInspector;
import de.ingrid.portal.search.catalog.CatalogTreeDataProvider;
import de.ingrid.portal.search.catalog.CatalogTreeDataProviderFactory;
import de.ingrid.utils.PlugDescription;

import java.util.*;

/**
 * Helper class dealing with all aspects of iPlugs (PlugDescription).
 *
 * @author joachim@wemove.com
 */
public class IPlugHelper {

    /**
     * Return true if the given iPlug has a specific data type.
     * 
     * @param iPlug The PlugDescription to work on.
     * @param dataType The data type to search for
     * @return True if the iPlug has the data type, false if not.
     */
    public static boolean hasDataType(PlugDescription iPlug, String dataType) {
        String[] dataTypes = iPlug.getDataTypes();
        for (int i=0; i<dataTypes.length; i++) {
            if (dataTypes[i].equalsIgnoreCase(dataType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if the given iPlug has a specific partner.
     * 
     * @param iPlug The PlugDescription to work on.
     * @param partnerId The partner to search for (pass id)
     * @return True if the iPlug has the partner, false if not.
     */
    public static boolean hasPartner(PlugDescription iPlug, String partnerId) {
        String[] partners = iPlug.getPartners();
        for (int i=0; i<partners.length; i++) {
            if (partners[i].equals(partnerId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns only iPlugs of given type from given iPlugs  
     * 
     * @param iPlugs iPlugs of different types which will be filtered
     * @param plugTypes The DataTypes to look for
     * @return Array containing filtered iPlugs or empty
     */
    public static PlugDescription[] filterIPlugsByType(PlugDescription[] iPlugs, String[] plugTypes) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < iPlugs.length; i++) {
            PlugDescription plug = iPlugs[i];
            for (int j = 0; j < plugTypes.length; j++) {
            	if (hasDataType(plug, plugTypes[j])) {
            		result.add(plug);
            		break;
                }
            }
        }

        return (PlugDescription[]) result.toArray(new PlugDescription[result.size()]);
    }

    /**
     * Returns only iPlugs of given Partner from given iPlugs  
     * 
     * @param iPlugs iPlugs of different partners which will be filtered
     * @param partners The partners to look for
     * @return Array containing filtered iPlugs or empty
     */
    public static PlugDescription[] filterIPlugsByPartner(PlugDescription[] iPlugs, List partners) {
        ArrayList result = new ArrayList();
        for (int i = 0; i < iPlugs.length; i++) {
            PlugDescription plug = iPlugs[i];
            for (int j = 0; j < partners.size(); j++) {
            	if (hasPartner(plug, (String)partners.get(j))) {
            		result.add(plug);
            		break;
                }
            }
        }

        return (PlugDescription[]) result.toArray(new PlugDescription[result.size()]);
    }

    /**
     * Remove all ECS iPlugs where plug description is corrupt, meaning field mapping is invalid.  
     * 
     * @param iPlugs iPlugs to be filtered, can be of all types
     * @return Array containing filtered iPlugs or empty
     */
    public static PlugDescription[] filterCorruptECSIPlugs(PlugDescription[] iPlugs) {
        ArrayList result = new ArrayList();

        String[] ecsPlugTypes = new String[]{Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS, Settings.QVALUE_DATATYPE_IPLUG_DSC_ECS_ADDRESS};
        for (int i = 0; i < iPlugs.length; i++) {
            PlugDescription plug = iPlugs[i];
            boolean isECSPlug = false;
            for (int j = 0; j < ecsPlugTypes.length; j++) {
            	if (hasDataType(plug, ecsPlugTypes[j])) {
            		isECSPlug = true;
            		break;
                }
            }
            if (isECSPlug) {
                CatalogTreeDataProvider ctdp = CatalogTreeDataProviderFactory.getDetailDataPreparer(IPlugVersionInspector.getIPlugVersion(plug));
            	if (ctdp.isCorrupt(plug)) {
            		continue;
            	}
            }

            result.add(plug);	
        }

        return (PlugDescription[]) result.toArray(new PlugDescription[result.size()]);
    }

    public static PlugDescription[] sortPlugs(PlugDescription[] iPlugs, Comparator plugComparator) {
        List plugList = Arrays.asList(iPlugs);
        Collections.sort(plugList, plugComparator);

        return (PlugDescription[]) plugList.toArray(new PlugDescription[plugList.size()]);
    }
}
