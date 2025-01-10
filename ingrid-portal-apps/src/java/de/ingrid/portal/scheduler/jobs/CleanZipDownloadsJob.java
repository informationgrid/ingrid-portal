/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.portal.scheduler.jobs;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import com.sun.syndication.io.ParsingFeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import de.ingrid.codelists.CodeListService;
import de.ingrid.codelists.model.CodeList;
import de.ingrid.portal.config.PortalConfig;
import de.ingrid.portal.global.CodeListServiceFactory;
import de.ingrid.portal.global.UtilsString;
import de.ingrid.portal.hibernate.HibernateUtil;
import de.ingrid.portal.om.IngridRSSSource;
import de.ingrid.portal.om.IngridRSSStore;
import de.ingrid.portal.scheduler.jobs.utils.PartnerProviderHandler;

/**
 * Quartz job for fetching all RSS feeds in database table ingrid_rss_source.
 * All RSS entries not older than one month will be added to the database table
 * ingrid_rss_store. Entries in ingrid_rss_store that are older than one month
 * will be deleted.
 * 
 * 
 * @author joachim@wemove.com
 */
public class CleanZipDownloadsJob extends IngridMonitorAbstractJob {

    protected static final Logger log = LoggerFactory.getLogger(CleanZipDownloadsJob.class);

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap  = context.getJobDetail().getJobDataMap();
        int status          = 0;
        String statusCode   = null;

        startTimer();

        log.info("Executing CleanZipDownloadsJob ...");
        String dirMain = PortalConfig.getInstance().getString(PortalConfig.PORTAL_DETAIL_UVP_ZIP_PATH, "./");
        Long deleteTime = PortalConfig.getInstance().getLong(PortalConfig.PORTAL_DETAIL_UVP_ZIP_DELETE_MIN, 262800);
        Date startDate = new Date();
        File dirDownload = new File(dirMain + "/downloads");

        if(dirDownload.isDirectory() && dirDownload.exists()) {
            status = STATUS_OK;
            statusCode = STATUS_CODE_NO_ERROR;
            removeOldFiles(dirDownload, startDate, deleteTime, false);
        } else {
            status = STATUS_OK;
            statusCode = STATUS_CODE_ERROR_NO_HITS;
        }
        log.info("CleanZipDownloadsJob finished!");

        computeTime(dataMap, stopTimer());
        updateJobData(context, status, statusCode);
        updateJob(context);
    }
    
    private void removeOldFiles(File dirFolder, Date startDate, long deleteTime, boolean deleteFolder) {
        if(dirFolder.exists()) {
            File[] listFiles = dirFolder.listFiles();
            for (File file : listFiles) {
                if(file.isDirectory()) {
                    removeOldFiles(file, startDate, deleteTime, true);
                } else{
                    long startTime = startDate.getTime();
                    long fileTime = file.lastModified();
                    long lastUpdateTimeInMin = (startTime - fileTime) / 60000;
                    if(lastUpdateTimeInMin > deleteTime) {
                        if(file.delete()) {
                            log.debug("Delete file: " + file.getAbsolutePath());
                        }
                    }
                }
            }
            if(deleteFolder) {
                listFiles = dirFolder.listFiles();
                if(listFiles.length == 0) {
                    if(dirFolder.delete()) {
                        log.debug("Delete directory: " + dirFolder.getAbsolutePath());
                    }
                }
            }
        }
    }
}
