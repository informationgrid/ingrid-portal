/*
 * **************************************************-
 * Ingrid Portal MDEK Application
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
package de.ingrid.mdek.handler;

import javax.servlet.http.HttpServletRequest;

import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;
import de.ingrid.mdek.caller.IMdekClientCaller;

public interface ConnectionFacade {
	public IMdekClientCaller getMdekClientCaller();
	public IMdekCallerObject getMdekCallerObject();
	public IMdekCallerAddress getMdekCallerAddress();
	public IMdekCallerQuery getMdekCallerQuery();
	public IMdekCallerCatalog getMdekCallerCatalog();
	public IMdekCallerSecurity getMdekCallerSecurity();

	// Move to a helper class? This will be replaced by the user management soon
	public String getCurrentPlugId();
	public String getCurrentPlugId(HttpServletRequest req);
}
