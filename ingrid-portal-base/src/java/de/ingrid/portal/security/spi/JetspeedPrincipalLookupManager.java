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
package de.ingrid.portal.security.spi;

import de.ingrid.portal.security.JetspeedPrincipalQueryContext;
import de.ingrid.portal.security.JetspeedPrincipalResultList;

/**
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 * 
 */
public interface JetspeedPrincipalLookupManager {

	/**
	 * Retrieves all principals that match the <em>queryContext</em>. It
	 * actually takes care of the SQL generation, querying and building the
	 * {@see JetspeedPrincipalResultList}.
	 * 
	 * The implementation of this method can be database specific.
	 * 
	 * @param queryContext
	 * @return
	 */
	JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext);

}
