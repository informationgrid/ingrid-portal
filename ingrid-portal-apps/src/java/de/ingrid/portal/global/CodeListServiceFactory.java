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
