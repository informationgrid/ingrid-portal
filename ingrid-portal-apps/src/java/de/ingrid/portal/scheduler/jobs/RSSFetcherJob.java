/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.portal.scheduler.jobs;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateManager;
import de.ingrid.portal.om.IngridRSSSource;
import de.ingrid.portal.om.IngridRSSStore;

/**
 * Quartz job for fetching all RSS feeds in database table ingrid_rss_source.
 * All RSS entries not older than one month will be added to the database table
 * ingrid_rss_store. Entries in ingrid_rss_store that are older than one month
 * will be deleted.
 * 
 * 
 * @author joachim@wemove.com
 */
public class RSSFetcherJob implements Job {

    protected final static Log log = LogFactory.getLog(RSSFetcherJob.class);

    HibernateManager fHibernateManager = null;

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {

        try {
            fHibernateManager = HibernateManager.getInstance();
            Session session = this.fHibernateManager.getSession();

            ArrayList feeds = new ArrayList();
            SyndFeed feed = null;
            URL feedUrl = null;
            SyndFeedInput input = null;

            // get rss sources from database
            List rssSources = session.createCriteria(IngridRSSSource.class).list();
            Iterator it = rssSources.iterator();
            while (it.hasNext()) {
                IngridRSSSource rssSource = (IngridRSSSource) it.next();
                feedUrl = new URL(rssSource.getUrl());
                input = new SyndFeedInput();
                feed = input.build(new XmlReader(feedUrl));
                if (feed.getLanguage() == null) {
                    feed.setLanguage(rssSource.getLanguage());
                }
                if (feed.getAuthor() == null || feed.getAuthor().length() == 0) {
                    feed.setAuthor(rssSource.getProvider());
                }
                
                feeds.add(feed);
            }

            Date publishedDate = null;
            SyndEntry entry = null;
            int cnt = 0;

            Calendar cal;
            // work on all feeds
            for (int i = 0; i < feeds.size(); i++) {
                feed = (SyndFeed) feeds.get(i);
                it = feed.getEntries().iterator();
                // work on all rss items of the feed
                while (it.hasNext()) {
                    entry = (SyndEntry) it.next();
                    publishedDate = entry.getPublishedDate();
                    // check for published date in the entry
                    if (publishedDate == null) {
                        // for rss feeds prior rss 1.0 or feeds with pubDate in
                        // item tag
                        // use the publish Date of the feed itself
                        publishedDate = feed.getPublishedDate();
                    }
                    if (publishedDate != null) {
                        cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH, -1);
                        // drop items that are older than one month
                        if (publishedDate.after(cal.getTime())) {
                            // check if this entry already exists
                            List rssEntries = session.createCriteria(IngridRSSStore.class).add(
                                    Restrictions.eq("link", entry.getLink())).add(Restrictions.eq("language", feed.getLanguage()))
                                    .list();
                            if (rssEntries.isEmpty()) {
                                if (entry.getAuthor() == null || entry.getAuthor().length() == 0) {
                                    if (feed.getTitle() != null && feed.getTitle().length() > 0) {
                                        entry.setAuthor(feed.getTitle());
                                    } else {
                                        entry.setAuthor(feed.getAuthor());
                                    }
                                }

                                entry.setTitle(UtilsString.htmlescape(entry.getTitle()));

                                IngridRSSStore rssEntry = new IngridRSSStore();
                                rssEntry.setTitle(entry.getTitle());
                                rssEntry.setDescription(entry.getDescription().getValue());
                                rssEntry.setLink(entry.getLink());
                                rssEntry.setLanguage(feed.getLanguage());
                                rssEntry.setPublishedDate(publishedDate);
                                rssEntry.setAuthor(entry.getAuthor());

                                session.beginTransaction();
                                session.save(rssEntry);
                                session.getTransaction().commit();

                                cnt++;
                            }
                            rssEntries = null;
                        }

                    }
                }
                feed = null;
            }

            if (cnt > 0) {
                log.info("Number of RSS entries added: " + cnt);
            }

            // remove old entries
            cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);

            List deleteEntries = session.createCriteria(IngridRSSStore.class).add(
                    Restrictions.lt("publishedDate", cal.getTime())).list();
            it = deleteEntries.iterator();
            session.beginTransaction();
            while (it.hasNext()) {
                session.delete((IngridRSSStore) it.next());
            }
            session.getTransaction().commit();
            deleteEntries = null;
            feeds = null;
            // since the working on rss feeds can be memory consuming
            // do GC after job is finished
            System.gc();
        } catch (Exception e) {
            log.error("Error executing quartz job RSSFetcherJob.", e);
            throw new JobExecutionException("Error executing quartz job RSSFetcherJob.", e, true);
        }

    }

}
