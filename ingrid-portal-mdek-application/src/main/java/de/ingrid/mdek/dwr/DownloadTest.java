package de.ingrid.mdek.dwr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

public class DownloadTest {

	private final static Logger log = Logger.getLogger(DownloadTest.class);

	public FileTransfer downloadCSV() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        String testCSV = "";
        testCSV += "id;spalte 1;spalte2\n";
        testCSV += "1;Test Inhalt Spalte 1;Test Inhalt Spalte 2\n";
        testCSV += "2;Noch ein Test 1;Noch ein Test 2\n";
        buffer.write(testCSV.getBytes());

        return new FileTransfer("values.csv", "text/comma-separated-values", buffer.toByteArray());
	}
}
