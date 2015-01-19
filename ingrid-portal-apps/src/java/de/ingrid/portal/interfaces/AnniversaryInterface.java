/*
 * **************************************************-
 * Ingrid Portal Apps
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
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.portal.interfaces;

import de.ingrid.utils.IngridHitDetail;

/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public interface AnniversaryInterface {

    /**
     * Returns all found Anniversary of a given Date. This method uses a fallback strategy 
     * of no aniversary was found for a particular day, month. It enlarges the search
     * with the following fallbacks:
     * 
     *  1.) today
     *  2.) this month
     *  3.) year
     * 
     * @param date  The date to search anniveraries for.
     * @param lang  The language of the anniversary content.
     * @return The DetailedTopic Array representing the anniversaries.
     */
    IngridHitDetail[] getAnniversaries(java.util.Date date, String lang);

}
