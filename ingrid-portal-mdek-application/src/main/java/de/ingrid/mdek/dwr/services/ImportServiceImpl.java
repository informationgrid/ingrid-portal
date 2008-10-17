package de.ingrid.mdek.dwr.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;
import org.directwebremoting.io.FileTransfer;

public class ImportServiceImpl {

	private final static Logger log = Logger.getLogger(ImportServiceImpl.class);	

	// Echoes the uploaded file back to the client
	public FileTransfer uploadFile(FileTransfer fileTransfer) throws IOException {
		log.debug("uploadFile called.");
		log.debug("fileName: "+fileTransfer.getName());
		log.debug("mimeType: "+fileTransfer.getMimeType());

		// decompress if zip, otherwise compress
		if (fileTransfer.getMimeType().equals("application/zip")) {
			ByteArrayOutputStream baos = decompress(fileTransfer.getInputStream());
			String fileName = fileTransfer.getName().replace(".zip", "");
			return new FileTransfer(fileName, "application/octet-stream", baos.toByteArray());

		} else {
			ByteArrayOutputStream output = compress(fileTransfer.getInputStream());
			return new FileTransfer(fileTransfer.getName()+".zip", "application/zip", output.toByteArray());
		}
	}

	// Compress (zip) any data on InputStream and write it to a ByteArrayOutputStream
	public static ByteArrayOutputStream compress(InputStream is) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(is);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzout = new GZIPOutputStream(new BufferedOutputStream(out));

		final int BUFFER = 2048;
		int count;
		byte data[] = new byte[BUFFER];
		while((count = bin.read(data, 0, BUFFER)) != -1) {
		   gzout.write(data, 0, count);
		}

		gzout.close();
		return out;
	}

	// Decompress (unzip) data on InputStream (has to contain zipped data) and write it to a ByteArrayOutputStream
	public static ByteArrayOutputStream decompress(InputStream is) throws IOException {
		GZIPInputStream gzin = new GZIPInputStream(new BufferedInputStream(is));
		ByteArrayOutputStream baout = new ByteArrayOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(baout);

		final int BUFFER = 2048;
		int count;
		byte data[] = new byte[BUFFER];
		while((count = gzin.read(data, 0, BUFFER)) != -1) {
		   out.write(data, 0, count);
		}

		out.close();
		return baout;
	}


	public static void channelCopy(final ReadableByteChannel src, final WritableByteChannel dst) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
		while (src.read(buffer) != -1) {
			buffer.flip();
			dst.write(buffer);
			buffer.compact();
		}
		buffer.flip();

		while (buffer.hasRemaining()) {
			dst.write(buffer);
		}
	}
}
