/*-
 * **************************************************-
 * InGrid Portal MDEK Application
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
package de.ingrid.mdek.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MdekDbMigrator {

    private static Logger log = LogManager.getLogger( MdekDbMigrator.class );
    
    public MdekDbMigrator(ComboPooledDataSource dataSource) {
        log.debug( "Migrate mdek DB" );

        Flyway flyway = new Flyway();
        try {
            String dbType = getDbType( dataSource );
            flyway.setLocations( "db/migration/" + dbType );

            // do NOT check changes in older migration files (checksum)
            flyway.setValidateOnMigrate( false );
            log.info( "flyway validateOnMigrate = FALSE" );

            flyway.setDataSource( dataSource );
            flyway.migrate();
        } catch (FlywayException e) {
            String errorMsg = e.getMessage();
            
            if (errorMsg != null && errorMsg.contains( "without metadata table" )) {
                log.warn( "DB migration failed for 'mdek'" );
                
                // the database probably isn't setup for flyway
                // find out which version we have to set for a baseline
                String version = getVersionFromDB( dataSource );
                
                if (version == null) {
                    log.warn( "Could not determine version of portal database for flyway baseline. Will clean database and migrate from initial version." );
                    flyway.clean();
                    flyway.migrate();
                } else {
                    flyway.setBaselineVersionAsString( version );
                    flyway.baseline();
                    flyway.migrate();
               }
            } else {
                log.error( "Could not migrate database 'mdek'", e );
                throw new RuntimeException( "Could not migrate database 'mdek'", e );
            }
        }
    }

    private String getDbType(DataSource ds) {
        String url = ((ComboPooledDataSource) ds).getJdbcUrl();
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
            PreparedStatement statement = ds.getConnection().prepareStatement( "SELECT value_name FROM info WHERE key_name='version'" );
            ResultSet resultset;
            resultset = statement.executeQuery();
            if (resultset.next()) {
                return resultset.getString( "value_name" );
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
