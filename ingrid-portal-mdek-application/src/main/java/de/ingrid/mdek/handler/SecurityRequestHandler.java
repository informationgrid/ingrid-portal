package de.ingrid.mdek.handler;

import java.util.List;

import de.ingrid.mdek.beans.security.Group;

public interface SecurityRequestHandler {

	public List<Group> getGroups();
	public Group getGroupDetails(String name);
	public Group createGroup(Group group, boolean refetch);
	public Group storeGroup(Group group, boolean refetch);
}