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
package de.ingrid.portal.global;

import java.util.ArrayList;
import java.util.List;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.comm.ICodeListCommunication;
import de.ingrid.codelists.comm.IngridCLCommunication;
import de.ingrid.codelists.persistency.ICodeListPersistency;
import de.ingrid.codelists.persistency.XmlCodeListPersistency;

public class CodeListServiceFactory {

    private static CodeListService instance;
    
    public static CodeListService instance() {
        if (instance == null) {
            instance = new CodeListService();
            instance.setComm(getCommunication());
            instance.setPersistencies(getPersistencies());
            instance.setDefaultPersistency(0);
            
        }
        return instance;
    }

    private static List<ICodeListPersistency> getPersistencies() {
        XmlCodeListPersistency persistency = new XmlCodeListPersistency();
        persistency.setPathToXml("data/codelists.xml");
        List<ICodeListPersistency> persistencies = new ArrayList<ICodeListPersistency>();
        persistencies.add(persistency);
        return persistencies;
    }

    private static ICodeListCommunication getCommunication() {
        return new IngridCLCommunication();
    }
}
