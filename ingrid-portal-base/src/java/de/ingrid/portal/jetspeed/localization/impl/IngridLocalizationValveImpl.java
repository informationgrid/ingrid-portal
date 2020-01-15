/*
 * **************************************************-
 * Ingrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.jetspeed.localization.impl;

import java.util.Locale;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.LocalizationValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ingrid Valve handling ingrid localization for Jetspeed Pipeline.
 * 
 * @author Martin Maidhof
 * @author Joachim Müller
 */
public class IngridLocalizationValveImpl extends AbstractValve implements LocalizationValve {

    private static final Logger log = LoggerFactory.getLogger(IngridLocalizationValveImpl.class);

    private static final String INGRID_LOCALE_REQUEST_KEY = "lang";

    private JetspeedCache contentCache;

    public IngridLocalizationValveImpl() {
        this.contentCache = Jetspeed.getComponentManager().lookupComponent("portletContentCache");
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException {

        try {
            // Check if jvm properties is set for lang
            String language = System.getProperty("lang");
            String paramlanguage = request.getRequestParameter(INGRID_LOCALE_REQUEST_KEY);
            
            if(paramlanguage != null) {
                language = paramlanguage;
            }
            
            if (language != null && language.length() > 0) {
            	
            	// invalidate portlet content cache
                contentCache.clear();

                // Code taken from LocaleSelectorPorltet
                String country = "";
                String variant = "";
                int countryIndex = language.indexOf('_');
                if (countryIndex > -1) {
                    country = language.substring(countryIndex + 1).trim();
                    language = language.substring(0, countryIndex).trim();
                    int vDash = country.indexOf('_');
                    if (vDash > 0) {
                        String cTemp = country.substring(0, vDash);
                        variant = country.substring(vDash + 1);
                        country = cTemp;
                    }
                }

                Locale ingridLocale = new Locale(language, country, variant);
                try {
                    ingridLocale.getISO3Language();
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("INGRID language set: %s", ingridLocale));
                    }
                }catch (java.util.MissingResourceException e) {
                    ingridLocale = null;
                    // not a valid language
                    if(log.isErrorEnabled()){
                        log.error(String.format("Invalid or unrecognized INGRID language: %s", language));
                    }
                }

                if (ingridLocale != null) {
                    // pass it like next
                    // org.apache.jetspeed.localization.impl.LocalizationValveImpl
                    // reads it !
                    request.setLocale(ingridLocale);
                    request.getRequest().getSession().setAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE,
                            ingridLocale);
                }
            }
        } catch (Exception e) {
            log.warn("Problems processing Ingrid Localization valve", e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }

    public String toString()
    {
        return "IngridLocalizationValve";
    }
}
