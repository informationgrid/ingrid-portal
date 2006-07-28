/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.jetspeed.localization.impl;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.LocalizationValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Ingrid Valve handling ingrid localization for Jetspeed Pipeline.
 *
 * @author Martin Maidhof
 */
public class IngridLocalizationValveImpl extends AbstractValve implements LocalizationValve {
    private static final Log log = LogFactory.getLog(IngridLocalizationValveImpl.class);

    private static final String INGRID_LOCALE__REQUEST_KEY = "lang";

    public IngridLocalizationValveImpl() {
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException {

        try {
            String language = request.getRequestParameter(INGRID_LOCALE__REQUEST_KEY);

            if (language != null && language.length() > 0) {
                // Code taken from LocaleSelectorPorltet
                String country = "";
                String variant = "";
                int countryIndex = language.indexOf('_');
                if (countryIndex > -1) {
                    country = language.substring(countryIndex + 1).trim();
                    language = language.substring(0, countryIndex).trim();
                    int vDash = country.indexOf("_");
                    if (vDash > 0) {
                        String cTemp = country.substring(0, vDash);
                        variant = country.substring(vDash + 1);
                        country = cTemp;
                    }
                }

                Locale ingridLocale = new Locale(language, country, variant);
                if (ingridLocale.getLanguage().length() == 0) {
                    // not a valid language
                    ingridLocale = null;
                    log.error("Invalid or unrecognized INGRID language: " + language);
                } else {
                    log.info("INGRID language set: " + ingridLocale);
                }

                if (ingridLocale != null) {
                    // pass it like next org.apache.jetspeed.localization.impl.LocalizationValveImpl reads it !
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
}