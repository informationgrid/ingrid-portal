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

            flyway.setDataSource( dataSource );
            flyway.migrate();
        } catch (FlywayException e) {
            // the database probably isn't setup for flyway
            // find out which version we have to set for a baseline
            String version = getVersionFromDB( dataSource );

            if (version != null) {
                flyway.setBaselineVersionAsString( version );
                flyway.baseline();
                flyway.migrate();
            } else {
                log.error( "Could not determine version of portal database for flyway baseline" );
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
