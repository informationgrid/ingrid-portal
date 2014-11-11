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
package de.ingrid.portal.search.detail;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Inner class: AddressTypeComperator for address sorting;
 *
 * @author joachim@wemove.com
 */
public class AddressTypeComparator implements Comparator {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public final int compare(Object a, Object b) {
        int ia;
        int ib;
        try {
            ia = Integer.parseInt((String) ((HashMap) a).get("t012_obj_adr.typ"));
            ib = Integer.parseInt((String) ((HashMap) b).get("t012_obj_adr.typ"));
        } catch (Exception e) {
            return 0;
        }

        if (ia > ib)
            return 1;
        else if (ia < ib)
            return -1;
        else
            return 0;
    }
}
