package de.ingrid.mdek.mapping;

public interface ImportDataProvider {

	public Integer getInitialKeyFromListId(int id);
	public String getInitialValueFromListId(int id);
	public String getCurrentUserUuid();
}
