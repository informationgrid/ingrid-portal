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
package de.ingrid.portal.search.catalog;

import java.util.List;

import de.ingrid.portal.global.IPlugHelperDscEcs;
import de.ingrid.utils.PlugDescription;

/**
 * @author joachim@wemove.com
 *
 */
public class CatalogTreeDataProvider_UDK_5_0 implements CatalogTreeDataProvider {

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#isCorrupt(de.ingrid.utils.PlugDescription)
	 */
	public boolean isCorrupt(PlugDescription plug) {
		return IPlugHelperDscEcs.isCorrupt(plug);
	}

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#getSubEntities(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public List getSubEntities(String docParentId, String plugId, String plugType, Integer maxNumber) {
		return IPlugHelperDscEcs.getSubDocs(docParentId, plugId, plugType, maxNumber);
	}

	/* (non-Javadoc)
	 * @see de.ingrid.portal.search.catalog.CatalogTreeDataProvider#getTopEntities(java.lang.String)
	 */
	public List getTopEntities(String plugId, String plugType) {
		return IPlugHelperDscEcs.getTopDocs(plugId, plugType);
	}

	public boolean hasChildren(String objUuid, String plugId, String plugType) {
		return (IPlugHelperDscEcs.getSubDocs(objUuid, plugId, plugType, null).size() > 0);
	}

}
