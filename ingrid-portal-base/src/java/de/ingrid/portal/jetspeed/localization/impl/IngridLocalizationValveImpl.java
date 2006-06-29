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

            if (language != null) {
                String[] localeArray = language.split("[-|_]");
                String country = "";
                String variant = "";
                for (int i = 0; i < localeArray.length; i++) {
                    if (i == 0) {
                        language = localeArray[i];
                    } else if (i == 1) {
                        country = localeArray[i];
                    } else if (i == 2) {
                        variant = localeArray[i];
                    }
                }

                Locale preferedLocale = new Locale(language, country, variant);

                request.setLocale(preferedLocale);
                request.setSessionAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, preferedLocale);
            }

        } catch (Exception e) {
            log.warn("Problems processing Ingrid Localization valve", e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }
}