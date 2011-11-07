/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.ingrid.portal.security.spi.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;

import de.ingrid.portal.security.spi.JetspeedPrincipalLookupManager;

/**
 * Factory for getting the database specific principal lookup manager. The
 * database platform is determined by the OJB connection repository.
 * 
 * The factory is actually a singleton facade, since it returns always the same
 * instance of the {@see JetspeedPrincipalLookupManager} once it was determined.
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 * 
 */
public class JetspeedPrincipalLookupManagerFactory {

	static final Log log = LogFactory.getLog(JetspeedPrincipalLookupManagerFactory.class);

	private static JetspeedPrincipalLookupManager jppm = null;

	private Map<String, JetspeedPrincipalLookupManager> mappings = null;

	/**
	 * Setter for the lookup manager mapping between OJB database platform
	 * string and the actual lookup manager implementation. Used by Spring.
	 * 
	 * @param mappings
	 */
	public void setMappings(Map<String, JetspeedPrincipalLookupManager> mappings) {
		this.mappings = mappings;
	}

	/**
	 * Returns the instance of the {@see JetspeedPrincipalLookupManager}
	 * corresponding to the database platform specified in the OJB connection
	 * repository. If the platform is not supported the default lookup manager
	 * is used.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JetspeedPrincipalLookupManager getJetspeedPrincipalLookupManager() {
		if (jppm == null) {
			String platform = null;
			ConnectionRepository cr = MetadataManager.getInstance().connectionRepository();
			List<JdbcConnectionDescriptor> jcdList = cr.getAllDescriptor();
			for (int i = 0; i < jcdList.size(); i++) {
				if (platform == null) {
					platform = jcdList.get(i).getDbms();
				} else {
					// if we have more than one descriptor, set platform null to
					// use default behavior
					if (!platform.equals(jcdList.get(i).getDbms())) {
						if (log.isInfoEnabled()) {
							log.info("Found more than one JdbcConnectionDescriptor. Not sure which one to take, so using compatible default behavior.");
						}
						platform = null;
						break;
					}
				}
			}
			for (Map.Entry<String, JetspeedPrincipalLookupManager> e : mappings.entrySet()) {
				if (e.getKey().equalsIgnoreCase(platform)) {
					jppm = e.getValue();
					break;
				}
			}
			if (jppm == null) {
				jppm = mappings.get("default");
			}
		}
		return jppm;
	}

}
