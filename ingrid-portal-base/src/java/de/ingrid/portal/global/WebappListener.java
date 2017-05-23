package de.ingrid.portal.global;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
import org.springframework.beans.factory.annotation.Autowired;

@WebListener
public class WebappListener implements ServletContextListener {
    
    private static Logger log = LogManager.getLogger( WebappListener.class );
    
    @Autowired
    private BasicDataSource dataSource;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info( "INITIALIZE DATABASE" );
        
        Flyway flyway = new Flyway();
        DataSource ds = null;
        try {
            InitialContext initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            ds = (DataSource)envContext.lookup("jdbc/jetspeed");
            flyway.setDataSource(ds);
            flyway.migrate();
        } catch (FlywayException e) {
            // the database probably isn't setup for flyway
            // find out which version we have to set for a baseline
            String version = getVersionFromDB(ds);

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

    public BasicDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


}
