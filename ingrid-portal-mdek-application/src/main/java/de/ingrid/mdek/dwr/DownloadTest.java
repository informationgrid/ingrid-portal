package de.ingrid.mdek.dwr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.directwebremoting.io.FileTransfer;

public class DownloadTest {

	public FileTransfer downloadCSV() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        String testCSV = "";
        testCSV += "id,spalte 1,spalte2\n";
        testCSV += "1,Test Inhalt Spalte 1,Test Inhalt Spalte 2\n";
        testCSV += "2,Noch ein Test 1,Noch ein Test 2\n";
        buffer.write(testCSV.getBytes());

        byte[] data = buffer.toByteArray();
        return new FileTransfer("values.csv", "text/comma-separated-values", data);
	}
}
