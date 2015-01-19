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
/**
 * 
 */
package de.ingrid.portal.search.detail;

import de.ingrid.utils.dsc.Record;

/**
 * Interface for all DetailData Preparer
 * 
 * 
 * @author joachim
 *
 */
public interface DetailDataPreparer {

    /** number of Object references (AOR) to show initially and to add when "more" is pressed */
    final static int NUM_OBJ_REFS_PER_PAGE = 20;

    /** request parameter name of maximum number of direct object references to show */
    final static String REQUEST_PARAM_NAME_MAX_OBJ_REFS = "maxORs";
    /** request parameter name of maximum number of subordinated object references to show */
    final static String REQUEST_PARAM_NAME_MAX_SUB_OBJ_REFS = "maxSubORs";

	public void prepare(Record record) throws Throwable;
	
}
