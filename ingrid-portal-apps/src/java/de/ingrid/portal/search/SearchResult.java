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
 * Created on 01.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.portal.search;

import java.util.HashMap;

/**
 * @author joachim
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchResult {

	/**
	 * Comment for <code>resultTitle</code>
	 */
	String resultTitle;

	/**
	 * Comment for <code>resultAbstract</code>
	 */
	String resultAbstract;

	/**
	 * Comment for <code>resultProvider</code>
	 */
	String resultProvider;

	/**
	 * Comment for <code>resultDataSource</code>
	 */
	String resultDataSource;
	
	/**
	 * Comment for <code>type</code>
	 */
	String type;

	
	/**
	 * Comment for <code>metaData</code>
	 */
	HashMap metaData = new HashMap();
	
	/**
	 * @return Returns the resultAbstract.
	 */
	public String getResultAbstract() {
		return resultAbstract;
	}
	/**
	 * @param resultAbstract The resultAbstract to set.
	 */
	public void setResultAbstract(String resultAbstract) {
		this.resultAbstract = resultAbstract;
	}
	/**
	 * @return Returns the resultDataSource.
	 */
	public String getResultDataSource() {
		return resultDataSource;
	}
	/**
	 * @param resultDataSource The resultDataSource to set.
	 */
	public void setResultDataSource(String resultDataSource) {
		this.resultDataSource = resultDataSource;
	}
	/**
	 * @return Returns the resultProvider.
	 */
	public String getResultProvider() {
		return resultProvider;
	}
	/**
	 * @param resultProvider The resultProvider to set.
	 */
	public void setResultProvider(String resultProvider) {
		this.resultProvider = resultProvider;
	}
	/**
	 * @return Returns the resultTitle.
	 */
	public String getResultTitle() {
		return resultTitle;
	}
	/**
	 * @param resultTitle The resultTitle to set.
	 */
	public void setResultTitle(String resultTitle) {
		this.resultTitle = resultTitle;
	}
	/**
	 * @return Returns a metaData.
	 */
	public Object getMetaData(Object key) {
		return metaData.get(key);
	}
	/**
	 * @param key
	 * @param value
	 */
	public void setMetaData(Object key, Object value) {
		this.metaData.put(key, value);
	}
	
	
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
}
