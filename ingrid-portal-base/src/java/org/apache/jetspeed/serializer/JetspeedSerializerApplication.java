/*
 * **************************************************-
 * Ingrid Portal Base
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.serializer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.components.jndi.SpringJNDIStarter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * Jetspeed Serializer Application
 * 
 * invoke with mandatory 
 * <p>-E filename or -I filename to denote the export or the import file</p>
 * <p>-I filename | directory, if a directory will process all XML files of pattern "*seed.xml"</p>
 * 
 * invoke with (optional) parameters as
 * <p>-p propertyFilename : overwrite the default filename defined in System.getProperty JetSpeed.Serializer.Configuration</p> 
 * <p>-a ApplicationPath : overwrite the default ./ or ApplicationPath property in properties file)</p>
 * <p>-b bootPath : directory to Spring boot files,   overwrite the default assembly/boot/ or bootPath  property in properties file)</p>  
 * <p>-c configPath : directory to Spring config files,   overwrite the default assembly/ or configPath property in properties file)</p>
 * 
 * <p>-O optionstring : overwrite defrault "ALL,REPLACE"</p>
 * <p>optionstring: 
 *      ALL - extract/import all (with exception of PREFERENCES)
 *      USER - extract/import users
 *      CAPABILITIES - extract/import capabilities
 *      PROFILE = extract/import profile settings (for export requires USER) 
 *      PREFS = extract/import  portlet preferences (ignored if any of the above is set)
 *      
 *      NOOVERWRITE = don't overwrite existing file (for export)
 *      BACKUP = backup before process
 * </p>
 * <p>
 * -dc driverClass, for example com.mysql.jdbc.Driver
 * </p>
 * <p>
 * -ds url, ruls according to the driver used, URL needs to point to the correct
 * database
 * </p>
 * <p>
 * -du user, user with create/drop etc. rights on the database
 * </p>
 * <p>
 * -dp password
 * </p>
 * 
 * <p>
 * -l log4j-level, ERROR (default), WARN, INFO 
 * </p>
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JetspeedSerializerApplication
{
    public static final String JNDI_DS_NAME = "jetspeed";    
    
    public static void main(String[] args)
    {
        String propertyFileName = null;
        
        String fileName = null; // XML filename - mandatory on command line        
        String applicationPath = null; // configuration.getProperties("applicationPath");
        String bootConfigFiles = null; // configuration.getProperties("bootConfigFiles");
        String configFiles = null; // configuration.getProperties("configFiles");

        String name = null;
        
        String options = null;
        
        PropertiesConfiguration configuration = null;
        
        String defaultIndent = null;

    	String driverClass = null; // jdbc driver
    	String url = null; // jdbc url to database
    	String user = null; // user
    	String password = null; // password

        String logLevel = null;
        
        boolean doImport = false;
        boolean doExport = false;
 
        if (args == null)
            throw new IllegalArgumentException("Either import or export have to be defined (-I or -E follwoed by the filename");

        
        // Parse all the command-line arguments
        for (int n = 0; n < args.length; n++)
        {
            if (args[n].equals("-p"))
                propertyFileName = args[++n];
            else if (args[n].equals("-a"))
                applicationPath = args[++n];
            else if (args[n].equals("-b"))
                bootConfigFiles = args[++n];
            else if (args[n].equals("-c"))
                configFiles = args[++n];
            else if (args[n].equals("-E"))
            {
                doExport = true;
                fileName = args[++n];
            } 
            else if (args[n].equals("-I"))
            {
                doImport = true;
                fileName = args[++n];
            } 
            else if (args[n].equals("-N"))
            {
                name = args[++n];
            }
            else if (args[n].equals("-l"))
                logLevel = args[++n];
            else if (args[n].equals("-O"))
                options = args[++n];
            else if (args[n].equals("-dc"))
                driverClass = args[++n];
            else if (args[n].equals("-ds"))
                url = args[++n];
            else if (args[n].equals("-du"))
            {
                if (((n + 1) >= args.length) || args[n + 1].startsWith("-d"))
                {
                    user = "";
                } else
                {
                    user = args[++n];
                }
            } 
            else if (args[n].equals("-dp"))
            {
                if (((n + 1) >= args.length) || args[n + 1].startsWith("-d"))
                {
                    password = "";
                } else
                {
                    password = args[++n];
                }
            } 
            else
            {
                throw new IllegalArgumentException("Unknown argument: "
                        + args[n]);
            }
        }
        
        /** The only required argument is the filename for either export or import*/
        if ((!doImport) && (!doExport))
          throw new IllegalArgumentException("Either import or export have to be defined (-I or -E follwoed by the filename");

        /** But not both*/
        if ((doImport) && (doExport))
            throw new IllegalArgumentException("Only one - either import or export - can be requested");

        if (name == null) name = fileName;
        
        /** get system property definition */
        if (propertyFileName == null)
            propertyFileName = System.getProperty(
                "org.apache.jetspeed.xml.importer.configuration",
                null);
 
        if (propertyFileName != null)
        {    
            try
            {
                configuration = new PropertiesConfiguration(propertyFileName);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }
            if (configuration != null)
            {
                /** only read what was not defined on the command line */
            
                if (applicationPath == null) 
                    applicationPath = configuration.getString("applicationPath");
                if (bootConfigFiles == null)  
                    bootConfigFiles = configuration.getString("bootConfigFiles");
                if (configFiles == null) 
                    configFiles = configuration.getString("configFiles");
                if (options == null) 
                    options = configuration.getString("options");
                if (defaultIndent == null) 
                    defaultIndent = configuration.getString("defaultIndent");

        		if (driverClass == null)
    				driverClass = configuration.getString("driverClass");
    			if (url == null)
    				url = configuration.getString("url");
    			if (user == null)
    				user = configuration.getString("user");
    			if (password == null)
    				password = configuration.getString("password");
    			if (logLevel == null)
    				logLevel = configuration.getString("loglevel");
    				
    	
            }
        }

        // if we still miss some settings, use hardoced defaults
        if (applicationPath == null) 
            applicationPath = "./";
        if (bootConfigFiles == null) 
            bootConfigFiles = "assembly/boot/";
        if (configFiles == null) 
            configFiles = "assembly/";
		if (logLevel == null) 
            logLevel = "ERROR";
      

        bootConfigFiles = bootConfigFiles + "*.xml";
        configFiles = configFiles + "*.xml";
     
        // ok - we are ready to rumble....
        
        /** create the instruction map */
        
        Map settings = null;
        int processHelper = 1; // default process SEED
        if (options != null)
        {
            settings = new HashMap();
            settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_OVERWRITE_EXISTING, Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, Boolean.FALSE);            
            String[] optionSet = getTokens(options);
            
            processHelper = 0;
            
            for (int i = 0; i < optionSet.length; i++)
            {
                String o = optionSet[i];
                if (o.equalsIgnoreCase("all"))
                {
                    settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.TRUE);
                    settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, Boolean.TRUE);
                    settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER, Boolean.TRUE);
                    settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, Boolean.FALSE);
                    processHelper = 1;
                }
                else
                if (o.equalsIgnoreCase("user"))
                {
                    settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.TRUE);
                    processHelper = 1;
                }
                else 
                    if (o.equalsIgnoreCase("PREFS"))
                    {
                        settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, Boolean.TRUE);
                		processHelper = 2;
                    }
                    else 
                        if (o.equalsIgnoreCase("CAPABILITIES"))
                        {
                            settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, Boolean.TRUE);
                            processHelper = 1;
                        }
                        else 
                            if (o.equalsIgnoreCase("PROFILE"))
                            {
                                settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER, Boolean.TRUE);
                                processHelper = 1;
                            }
                            else 
                                if (o.equalsIgnoreCase("NOOVERWRITE"))
                                    settings.put(JetspeedSerializer.KEY_OVERWRITE_EXISTING, Boolean.FALSE);
                                else 
                                    if (o.equalsIgnoreCase("BACKUP"))
                                        settings.put(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, Boolean.TRUE);
                
            }
            
        }
        JetspeedSerializer serializer = null;

		if (driverClass == null)
			driverClass = System.getProperty(
					"org.apache.jetspeed.database.driverClass",
					"com.mysql.jdbc.Driver");
		if (url == null)
			url = System.getProperty("org.apache.jetspeed.database.url",
					"jdbc:mysql://localhost/j2test");
		if (user == null)
			user = System.getProperty("org.apache.jetspeed.database.user",
					"user");
		if (password == null)
			password = System.getProperty(
					"org.apache.jetspeed.database.password", "password");

		if (driverClass == null)
			throw new IllegalArgumentException(
					"Can't proceed without a valid driver");
		if (url == null)
			throw new IllegalArgumentException(
					"Can't proceed without a valid url to the target database");
		if (user == null)
			throw new IllegalArgumentException(
					"Can't proceed without a valid database user");

        
        
        HashMap context = new HashMap();
 
		context.put(SpringJNDIStarter.DATASOURCE_DRIVER, driverClass);
		context.put(SpringJNDIStarter.DATASOURCE_URL, url);
		context.put(SpringJNDIStarter.DATASOURCE_USERNAME, user);
		context.put(SpringJNDIStarter.DATASOURCE_PASSWORD, password);
        
		Logger  logger = Logger.getLogger("org.springframework");
		Level level = logger.getLevel();
		if (logLevel.equalsIgnoreCase("INFO"))
			logger.setLevel(Level.INFO);
		else
			if (logLevel.equalsIgnoreCase("WARN"))
				logger.setLevel(Level.WARN);
			else
				logger.setLevel(Level.ERROR);
				
/**
 * set the application root
 */
        System.out.println("APP ROOT is " + applicationPath);
		System.setProperty("applicationRoot",applicationPath);
		System.setProperty("portal.name","jetspped");
        SpringJNDIStarter starter = new SpringJNDIStarter(context,applicationPath,getTokens(bootConfigFiles),getTokens(configFiles));
        
        System.out.println("starter framework created " + starter);
        
        
        try
        {
            starter.setUp();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("starter framework established " + starter);
        String[] importList = null;

        if (doImport)
        	importList = parseFiles(fileName);
    	
        if ((doImport) && (importList != null) && (importList.length > 0))
        {
            for (int i = 0; i < importList.length; i++)
            {
                try
                {
                    System.out.println("processing import  " + importList[i]);
                    if (processHelper == 2)
                    {
                        serializer = new JetspeedSerializerSecondaryImpl(starter.getComponentManager());
                    }
                    else
                        serializer = new JetspeedSerializerImpl(starter.getComponentManager());
                    serializer.importData(importList[i], settings);
                    System.out.println("processing import  " + importList[i] + " done");
                    
                } 
                catch (Exception e)
                {
                    System.err.println("Failed to process XML import for " + importList[i] + ":" + e);
                    e.printStackTrace();
                }
             }
        }
        if (doExport)
        {
        	try
	        {
		        System.out.println("processing export to  " + fileName);
		        if (processHelper == 2)
		        {
		        	serializer = new JetspeedSerializerSecondaryImpl(starter.getComponentManager());
		        }
		        else
		        	serializer = new JetspeedSerializerImpl(starter.getComponentManager());

                serializer.exportData(name, fileName, settings);
            } 
            catch (Exception e)
            {
                System.err.println("Failed to process XML export of " + fileName + ": " + e);
                e.printStackTrace();
            }

        }
        try
        {
            if (serializer != null)
                serializer.closeUp();
           starter.tearDown();
           logger.setLevel(level);;
        }
        catch (Exception e1)
        {
            System.out.println("starter framework teardown caused exception "  + e1.getLocalizedMessage());
            e1.printStackTrace();
            
        }            
        System.out.println("DONE performing " + (doExport?"export":"import")+ " with " + fileName);
    }
    
        
       
	/**
	 * process provided filename or directory name
	 * 
	 * @return one or more files to be processed
	 */
	static private String[] parseFiles(String schemaDirectory)
	{
		String[] fileList = null;
		try
		{
			File dir = new File(schemaDirectory);
			if (!(dir.exists()))
            {
				return fileList;
            }
			if (!(dir.isDirectory()))
			{
				fileList = new String[1];
				fileList[0] = schemaDirectory;
				return fileList;
			}
			// 	Handling a directory
			File[] files = dir.listFiles(
				    new FilenameFilter() {
				        public boolean accept(File dir, String name) 
				        			{String n = name.toLowerCase();
	   								return n.endsWith("seed.xml");
				        }
				    });
			if (files == null)
				return fileList;

			fileList = new String[files.length];
			for (int i = 0; i < files.length; i++)
            {
				fileList[i] = files[i].getAbsolutePath();
            }
			return fileList;
		} 
        catch (Exception e)
		{
			e.printStackTrace(); 
			throw new IllegalArgumentException(
					"Processing the schema-directory " + schemaDirectory
							+ " caused exception "
							+ e.getLocalizedMessage());
		}

		
	}

    
        private static  String[] getTokens(String _line)
        {
            if ((_line == null) || (_line.length() == 0))
                return null;
            
            StringTokenizer st = new StringTokenizer(_line, ",");
            ArrayList list = new ArrayList();

            while (st.hasMoreTokens())
                list.add(st.nextToken());
            String[] s = new String[list.size()];
            for (int i=0; i<list.size(); i++)
                s[i] = (String)list.get(i);
            return s;
        }

        
}