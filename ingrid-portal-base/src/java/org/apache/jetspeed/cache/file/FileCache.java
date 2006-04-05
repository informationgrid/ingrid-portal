/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.cache.file;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FileCache keeps a cache of files up-to-date with a most simple eviction policy.
 * The eviction policy will keep n items in the cache, and then start evicting
 * the items ordered-by least used first. The cache runs a thread to check for
 * both evictions and refreshes.
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 *  @version $Id: FileCache.java 331199 2005-11-07 04:34:55Z rwatler $
 */

public class FileCache implements java.util.Comparator
{
    protected long scanRate = 300;  // every 5 minutes
    protected int maxSize = 100; // maximum of 100 items
    protected List listeners = new LinkedList();

    private FileCacheScanner scanner = null;
    private Map cache = null;

    private final static Log log = LogFactory.getLog(FileCache.class);

    /**
     * Default constructor. Use default values for scanReate and maxSize
     *
     */
    public FileCache()
    {
        cache = new HashMap();
        this.scanner = new FileCacheScanner();
        this.scanner.setDaemon(true);
    }

    /**
     * Set scanRate and maxSize
     *
     * @param scanRate how often in seconds to refresh and evict from the cache
     * @param maxSize the maximum allowed size of the cache before eviction starts
     */
    public FileCache(long scanRate, 
                     int maxSize)
    {
        cache = new HashMap();

        this.scanRate = scanRate;
        this.maxSize = maxSize;
        this.scanner = new FileCacheScanner();
        this.scanner.setDaemon(true);
    }

    /**
     * Set all parameters on the cache
     *
     * @param initialCapacity the initial size of the cache as passed to HashMap
     * @param loadFactor how full the hash table is allowed to get before increasing
     * @param scanRate how often in seconds to refresh and evict from the cache
     * @param maxSize the maximum allowed size of the cache before eviction starts
     */
    public FileCache(int initialCapacity, 
                     int loadFactor, 
                     long scanRate, 
                     int maxSize)
    {
        cache = new HashMap(initialCapacity, loadFactor);

        this.scanRate = scanRate;
        this.maxSize = maxSize;
        this.scanner = new FileCacheScanner();
        this.scanner.setDaemon(true);
    }

    /**
     * Set the new refresh scan rate on managed files.
     *
     * @param scanRate the new scan rate in seconds
     */
    public void setScanRate(long scanRate)
    {
        this.scanRate= scanRate;
    }

    /**
     * Get the refresh scan rate 
     *
     * @return the current refresh scan rate in seconds
     */
    public long getScanRate()
    {
        return scanRate;
    }

    /**
     * Set the new maximum size of the cache 
     *
     * @param maxSize the maximum size of the cache
     */
    public void setMaxSize(int maxSize)
    {
        this.maxSize = maxSize;
    }

    /**
     * Get the maximum size of the cache 
     *
     * @return the current maximum size of the cache
     */
    public int getMaxSize()
    {
        return maxSize;
    }

    /**
     * Gets an entry from the cache given a key
     *
     * @param key the key to look up the entry by
     * @return the entry
     */
    public FileCacheEntry get(String key)
    {
        return (FileCacheEntry) cache.get(key);
    }

    /**
     * Gets an entry from the cache given a key
     *
     * @param key the key to look up the entry by
     * @return the entry
     */
    public Object getDocument(String key)
    {
        FileCacheEntry entry = (FileCacheEntry) cache.get(key);
        if (entry != null)
        {
            return entry.getDocument();
        }
        return null;
    }

    /**
     * Puts a file entry in the file cache
     *
     * @param file The file to be put in the cache
     * @param document the cached document
     */
    public void put(File file, Object document)
        throws java.io.IOException
    {
        if(!file.exists())
        {
            throw new FileNotFoundException("File to cache: "+file.getAbsolutePath()+" does not exist.");
        }
        FileCacheEntry entry = new FileCacheEntryImpl(file, document);
        cache.put(file.getCanonicalPath(), entry);
    }

    /**
     * Puts a file entry in the file cache
     *
     * @param path the full path name of the file
     * @param document the cached document
     */
    public void put(String key, Object document, File rootFile)
        throws java.io.IOException
    {
        File file = new File(rootFile, key);
        if(!file.exists())
        {
            throw new FileNotFoundException("File to cache: "+file.getAbsolutePath()+" does not exist.");
        }
        FileCacheEntry entry = new FileCacheEntryImpl(file, document);
        cache.put(key, entry);
    }

    /**
     * Removes a file entry from the file cache
     *
     * @param key the full path name of the file
     * @return the entry removed
     */
    public Object remove(String key)
    {
        return cache.remove(key);
    }


    /**
     * Add a File Cache Event Listener 
     *
     * @param listener the event listener
     */
    public void addListener(FileCacheEventListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Start the file Scanner running at the current scan rate.
     *
     */
    public void startFileScanner()
    {
        try
        {

            this.scanner.start();
        }
        catch (java.lang.IllegalThreadStateException e)
        {
            log.error("Exception starting scanner", e);
        }
    }

    /**
     * Stop the file Scanner 
     *
     */
    public void stopFileScanner()
    {
        this.scanner.setStopping(true);
    }

    /**
     * Evicts entries based on last accessed time stamp
     *
     */
    protected void evict()        
    {
        synchronized (cache)
        {
            if (this.getMaxSize() >= cache.size())
            {
                return;
            }
    
            List list = new LinkedList(cache.values());
            Collections.sort(list, this);
    
            int count = 0;
            int limit = cache.size() - this.getMaxSize();
    
            for (Iterator it = list.iterator(); it.hasNext(); )
            {
                if (count >= limit)
                {
                    break;
                }
    
                FileCacheEntry entry = (FileCacheEntry) it.next();
                String key = null;
                try
                {
                    key = entry.getFile().getCanonicalPath();
                }                    
                catch (java.io.IOException e)
                {
                    log.error("Exception getting file path: ", e);
                }
                // notify that eviction will soon take place
                for (Iterator lit = this.listeners.iterator(); lit.hasNext(); )
                {
                    FileCacheEventListener listener = 
                        (FileCacheEventListener) lit.next();
                    try
                    {
                        listener.evict(entry);
                    }
                    catch (Exception e1)
                    {
                        log.warn("Unable to evict cache entry.  "+e1.toString(), e1);
                    }                                    
                }
                cache.remove(key);
    
                count++;
            }        
        }
    }

    /**
     * Evicts all entries
     *
     */
    public void evictAll()        
    {
        synchronized (cache)
        {
            // evict all cache entries
            List list = new LinkedList(cache.values());
            for (Iterator it = list.iterator(); it.hasNext(); )
            {
                // evict cache entry
                FileCacheEntry entry = (FileCacheEntry) it.next();
                // notify that eviction will soon take place
                for (Iterator lit = this.listeners.iterator(); lit.hasNext(); )
                {
                    FileCacheEventListener listener = 
                        (FileCacheEventListener) lit.next();
                    try
                    {
                        listener.evict(entry);
                    }
                    catch (Exception e1)
                    {
                        log.warn("Unable to evict cache entry.  "+e1.toString(), e1);
                    }                                    
                }
                // remove from cache by key
                String key = null;
                try
                {
                    key = entry.getFile().getCanonicalPath();
                }                    
                catch (java.io.IOException e)
                {
                    log.error("Exception getting file path: ", e);
                }
                cache.remove(key);
            }        
        }
    }

    /**
     * Comparator function for sorting by last accessed during eviction
     *
     */
    public int compare(Object o1, Object o2)
    {
        FileCacheEntry e1 = (FileCacheEntry)o1;
        FileCacheEntry e2 = (FileCacheEntry)o2;
        if (e1.getLastAccessed() < e2.getLastAccessed())
        {
            return -1;
        }
        else if (e1.getLastAccessed() == e2.getLastAccessed())
        {
            return 0;
        }
        return 1;
    }

    /**
     * inner class that runs as a thread to scan the cache for updates or evictions
     *
     */
    protected class FileCacheScanner extends Thread
    {
        private boolean stopping = false;

        public void setStopping(boolean flag)
        {
            this.stopping = flag;
        }

        /**
         * Run the file scanner thread
         *
         */
        public void run()
        {
            boolean done = false;
    
            try
            {
                while(!done)
                {
                    try
                    {
                        int count = 0;
                        for (Iterator it = FileCache.this.cache.values().iterator(); it.hasNext(); )
                        {
                            FileCacheEntry entry = (FileCacheEntry) it.next();
                            Date modified = new Date(entry.getFile().lastModified());
    
                            if (modified.after(entry.getLastModified()))
                            {                            
                                for (Iterator lit = FileCache.this.listeners.iterator(); lit.hasNext(); )
                                {
                                    FileCacheEventListener listener = 
                                        (FileCacheEventListener) lit.next();
                                    try
                                    {
                                        listener.refresh(entry);
                                    }
                                    catch (Exception e1)
                                    {
                                        log.warn("Unable to refresh cached document:  "+e1.toString(), e1);
                                    }                                    
                                    entry.setLastModified(modified);
                                }
                            }
                            count++;
                        }
                        if (count > FileCache.this.getMaxSize())
                        {
                            FileCache.this.evict();
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("FileCache Scanner: Error in iteration...", e);
                    }
    
                    sleep(FileCache.this.getScanRate() * 1000);                

                    if (this.stopping)
                    {
                        this.stopping = false;
                        done = true;
                    }
                }
            }
            catch (InterruptedException e)
            {
                log.error("FileCacheScanner: recieved interruption, exiting.", e);
            }
        }
    } // end inner class:  FileCacheScanner


    /**
     * get an iterator over the cache values
     *
     * @return iterator over the cache values
     */
    public Iterator getIterator()
    {
        return cache.values().iterator();
    }

    /**
      * get the size of the cache
      *
      * @return the size of the cache
      */
    public int getSize()
    {
        return cache.size();
    }
}

