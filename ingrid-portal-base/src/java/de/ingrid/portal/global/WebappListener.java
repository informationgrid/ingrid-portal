package de.ingrid.portal.global;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
            flyway.setLocations( "db/migration/" + dbType );

            flyway.setDataSource( ds );
            flyway.migrate();
        } catch (FlywayException e) {
            // the database probably isn't setup for flyway
            // find out which version we have to set for a baseline
            String version = getVersionFromDB( ds );

            if (version != null) {
                flyway.setBaselineVersionAsString( version );
                flyway.baseline();
                flyway.migrate();
            } else {
                log.error( "Could not determine version of portal database for flyway baseline" );
            }

        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
