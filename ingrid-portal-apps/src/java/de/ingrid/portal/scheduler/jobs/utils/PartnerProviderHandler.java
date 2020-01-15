/*-
 * **************************************************-
 * InGrid Portal Apps
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
package de.ingrid.portal.scheduler.jobs.utils;

import java.util.List;

import org.apache.log4j.Logger;

import de.ingrid.codelists.model.CodeList;
import de.ingrid.codelists.model.CodeListEntry;
import de.ingrid.portal.global.UtilsDB;
import de.ingrid.portal.om.IngridPartner;
import de.ingrid.portal.om.IngridProvider;

public class PartnerProviderHandler {
    
    private static final Logger log = Logger.getLogger(PartnerProviderHandler.class);

    public void run(List<CodeList> codelists) {
        for (CodeList codeList : codelists) {
            
            if ( codeList.getId().equals("110") ) {
                log.info( "Partner are being updated." );
                deleteAllPartner();
                codeList.getEntries().forEach( entry -> 
                    addPartner(entry)
                );
            } else if( codeList.getId().equals("111")) { // PROVIDER
                log.info( "Provider are being updated." );
                deleteAllProvider();
                codeList.getEntries().forEach( entry -> 
                    addProvider(entry)
                );
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
