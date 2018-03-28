/*-
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import de.ingrid.portal.config.PortalConfig;

@WebListener
public class WebappListener implements ServletContextListener {

    private static Logger log = LogManager.getLogger( WebappListener.class );

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.debug( "INITIALIZE DATABASE" );

        Flyway flyway = new Flyway();
        DataSource ds = null;
        try {
            InitialContext initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup( "java:/comp/env" );
            ds = (DataSource) envContext.lookup( "jdbc/jetspeed" );

            String dbType = getDbType( ds );
            Map<String, String> placeholders = getPlaceHolderValues();

            flyway.setLocations( "db/migration/" + dbType, "db/optional/" + dbType );
            flyway.setPlaceholders( placeholders );
            
            flyway.setDataSource( ds );
            flyway.migrate();
        } catch (FlywayException e) {
            
            String errorMsg = e.getMessage();
            
            if (errorMsg != null && errorMsg.contains( "without metadata table" )) {
                log.warn( "DB migration failed for 'ingrid_portal'" );
            
                // the database probably isn't setup for flyway
                // find out which version we have to set for a baseline
                String version = getVersionFromDB( ds );
    
                if (version != null) {
                    flyway.setBaselineVersionAsString( version );
                    flyway.baseline();
                    flyway.migrate();
                } else {
                    log.error( "Could not determine version of portal database for flyway baseline" );
                    //flyway.clean();
                    //flyway.migrate();
                }
            } else {
                log.error( "Could not migrate database 'ingrid_portal'", e );
                throw new RuntimeException( "Could not migrate database 'ingrid_portal'", e );
            }

        } catch (NamingException e) {
            log.error( "Error migrating the database", e );
        }
    }

    private Map<String, String> getPlaceHolderValues() {
        boolean showMetadataMenu = PortalConfig.getInstance().getBoolean( "portal.showMetadataMenu" );

        Map<String, String> map = new HashMap<String, String>();
        map.put( "hideMetadataMenu", showMetadataMenu ? "0" : "1" );
        
        return map;
    }

    private String getDbType(DataSource ds) {
        String url = ((BasicDataSource) ds).getUrl();
        String type = null;
        if (url.contains( "jdbc:mysql" )) {
            type = "mysql";
        } else if (url.contains( "jdbc:postgresql" )) {
            type = "postgres";
        } else if (url.contains( "jdbc:oracle" )) {
            type = "oracle";
        }

        return type;
    }

    private String getVersionFromDB(DataSource ds) {
        try {
            PreparedStatement statement = ds.getConnection().prepareStatement( "SELECT item_value FROM ingrid_lookup WHERE item_key='ingrid_db_version'" );
            ResultSet resultset;
            resultset = statement.executeQuery();
            if (resultset.next()) {
                return resultset.getString( "item_value" );
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

}
