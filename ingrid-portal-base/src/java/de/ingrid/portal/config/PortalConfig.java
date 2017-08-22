/*-
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
package de.ingrid.portal.config;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to the ingrid portal preferences.
 * 
 * @author joachim@wemove.com
 */
public class PortalConfig extends PropertiesConfiguration {

       // private stuff
    private static PortalConfig instance = null;

    private final static Logger log = LoggerFactory.getLogger(PortalConfig.class);

    public static synchronized PortalConfig getInstance() {
        if (instance == null) {
            try {
                instance = new PortalConfig();
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error(
                            "Error loading the portal config application config file. (bootstrap.properties)",
                            e);
                }
            }
        }
        return instance;
    }

    private PortalConfig() throws Exception {
        super("bootstrap.properties");
        //this.setReloadingStrategy(ReloadingStrategy)
        URL urlProfile = this.getClass().getResource("/bootstrap.profile.properties");
        URL urlOverride = this.getClass().getResource("/bootstrap.override.properties");
        updateProperties(urlProfile);
        updateProperties(urlOverride);
    }
    
    private void updateProperties(URL url) throws ConfigurationException {
        if (url != null) {
            File f = new File(url.getPath());
            PropertiesConfiguration userConfig = new PropertiesConfiguration(f);
            Iterator<String> it = userConfig.getKeys();
            while (it.hasNext()) {
                String key = it.next();
                this.setProperty(key, userConfig.getProperty(key));
            }
        }
    }
}
