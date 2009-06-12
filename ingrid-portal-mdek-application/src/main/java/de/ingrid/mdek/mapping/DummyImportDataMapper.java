package de.ingrid.mdek.mapping;

import java.io.InputStream;

public class DummyImportDataMapper implements ImportDataMapper {

	public InputStream convert(InputStream in) {
		// no mapping needed here
		return in;
	}

}
