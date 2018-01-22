package de.ingrid.portal.scheduler.jobs.utils;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;

public class PartnerProviderHandler {
    
    private final static Logger log = Logger.getLogger(PartnerProviderHandler.class);

    public void run(List<CodeList> codelists) {
        for (CodeList codeList : codelists) {
            
            switch ( codeList.getId() ) {
            case "110": // PARTNER
                log.info( "Partner are being updated." );
                deleteAllPartner();
                codeList.getEntries().forEach( entry -> {
                    addPartner(entry);
                });
                break;
            case "111": // PROVIDER
                log.info( "Provider are being updated." );
                deleteAllProvider();
                codeList.getEntries().forEach( entry -> {
                    addProvider(entry);
                });
                break;
            }
            
        }
    }

    private void deleteAllPartner() {
        UtilsDB.deleteDBObjects( UtilsDB.getPartners().toArray() );
    }
    
    private void deleteAllProvider() {
        UtilsDB.deleteDBObjects( UtilsDB.getProviders().toArray() );
    }

    private void addPartner(CodeListEntry entry) {
        IngridPartner iP = new IngridPartner();
        iP.setId(Long.valueOf( entry.getId() ));
        iP.setIdent(entry.getField( "ident" ));
        iP.setName(entry.getField( "name" ));
        iP.setSortkey(Integer.valueOf( entry.getField( "sortkey" ) ));
        
        if (log.isDebugEnabled()) {
            log.debug( "added partner: " + iP.getIdent() );
        }
        
        UtilsDB.saveDBObject( iP );
    }

    private void addProvider(CodeListEntry entry) {
        IngridProvider iP = new IngridProvider();
        iP.setId(Long.valueOf( entry.getId() ));
        iP.setIdent(entry.getField( "ident" ));
        iP.setName(entry.getField( "name" ));
        iP.setSortkeyPartner(Integer.valueOf( entry.getField( "sortkey_partner" ) ));
        iP.setSortkey(Integer.valueOf( entry.getField( "sortkey" ) ));
        iP.setUrl(entry.getField( "url" ));

        if (log.isDebugEnabled()) {
            log.debug( "added provider: " + iP.getIdent() );
        }
        
        UtilsDB.saveDBObject( iP );
    }

}
