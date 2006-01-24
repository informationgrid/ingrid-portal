package de.ingrid.portal.scheduler.jobs;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.jetspeed.scheduler.JobEntry;
import org.apache.jetspeed.scheduler.ScheduledJob;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RSSFetcherJob extends ScheduledJob {

    public void run(JobEntry arg0) throws Exception {

        
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)envContext.lookup("jdbc/jetspeed");
            Connection conn = ds.getConnection();
            PreparedStatement selectStmt;
            ResultSet rs;

            selectStmt = conn.prepareStatement("SELECT url FROM ingrid_rss_source WHERE lang='de'");
            rs = selectStmt.executeQuery();
            
            Vector feeds = new Vector();
            SyndFeed feed = null;
            URL feedUrl = null;
            SyndFeedInput input = null;
            
            while (rs.next()) {
                feedUrl = new URL(rs.getString("url"));
                input = new SyndFeedInput();
                feed = input.build(new XmlReader(feedUrl));
                feeds.add(feed);
            }
            rs.close();
            
            Date publishedDate = null;
            SyndEntry entry = null;
            String sqlDate = null;
            String sql;
            SimpleDateFormat df;
                
            for (int i=0; i<feeds.size(); i++) {
                feed = (SyndFeed)feeds.get(i);
                Iterator it = feed.getEntries().iterator();
                while (it.hasNext()) {
                    entry = (SyndEntry)it.next();
                    selectStmt = conn.prepareStatement("SELECT link FROM ingrid_rss_store WHERE language='de' AND link='" + entry.getLink() + "'");
                    rs = selectStmt.executeQuery();
                    if (!rs.next()) {
                        publishedDate = entry.getPublishedDate();
                        if (publishedDate == null) {
                            publishedDate = new Date();
                        }
                        df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
                        sqlDate = df.format( publishedDate );
                        entry.setTitle(new String(convert(entry.getTitle().getBytes(), "ISO-8859-1", "UTF-8")));
                        
                        
                        sql = "INSERT INTO ingrid_rss_store (title, description, language, link, published_date, author) VALUES ('"+ entry.getTitle() + "', '" +entry.getDescription().getValue() + "', '" +"de" + "', '" +entry.getLink() + "', '" + sqlDate + "', '" + entry.getAuthor() +  "')";
                        selectStmt = conn.prepareStatement(sql);
                        selectStmt.execute();
                    }
                    rs.close();
                }
            }
            
            // remove old entries
            Calendar myCal = Calendar.getInstance();
            myCal.add(Calendar.MONTH, -1);
            
            df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
            sqlDate = df.format( myCal.getTime() );
            
            sql = "DELETE FROM ingrid_rss_store WHERE published_date < '"+sqlDate+"'";
            selectStmt = conn.prepareStatement(sql);
            selectStmt.execute();
            
            conn.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static byte[] convert(byte[] data, String srcEncoding, String targetEncoding) {
        // First, decode the data using the source encoding.
        // The String constructor does this (Javadoc).
        String str = null;
        try {
            str = new String(data, srcEncoding);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Next, encode the data using the target encoding.
        // The String.getBytes() method does this.
        byte[] result = null;
        try {
            result = str.getBytes(targetEncoding);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }


}
