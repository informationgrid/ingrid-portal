package de.ingrid.mdek.handler;

import de.ingrid.mdek.caller.IMdekCaller;
import de.ingrid.mdek.caller.IMdekCallerAddress;
import de.ingrid.mdek.caller.IMdekCallerCatalog;
import de.ingrid.mdek.caller.IMdekCallerObject;
import de.ingrid.mdek.caller.IMdekCallerQuery;
import de.ingrid.mdek.caller.IMdekCallerSecurity;

public interface ConnectionFacade {
	public IMdekCaller getMdekCaller();
	public IMdekCallerObject getMdekCallerObject();
	public IMdekCallerAddress getMdekCallerAddress();
	public IMdekCallerQuery getMdekCallerQuery();
	public IMdekCallerCatalog getMdekCallerCatalog();
	public IMdekCallerSecurity getMdekCallerSecurity();

	// Move to a helper class? This will be replaced by the user management soon
	public String getCurrentPlugId();
}
