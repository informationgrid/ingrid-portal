/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.portal.search.catalog;

import java.util.List;

import de.ingrid.utils.PlugDescription;

/**
 * @author joachim@wemove.com
 *
 */
public interface CatalogTreeDataProvider {
	
	/** Check whether plug description is "corrupt" so no top entities can be fetched ! */
	public boolean isCorrupt(PlugDescription plug);

	public List getTopEntities(String plugId, String plugType);

	public List getSubEntities(String docParentId, String plugId, String plugType, Integer maxNumber);
	
	public boolean hasChildren(String objUuid, String plugId, String plugType);
	
}
