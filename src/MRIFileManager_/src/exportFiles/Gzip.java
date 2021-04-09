package exportFiles;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import MRIFileManager.GetStackTrace;

public class Gzip {

	public Gzip(String file, String newFile, String action) {
		if (action.contentEquals("compress"))
			compressGzipFile(file, newFile);
		else if (action.contentEquals("decompress"))
			decompressGzipFile(file, newFile);
	}

	private void decompressGzipFile(String gzipfile, String newFile) {
		try {
			FileInputStream fis = new FileInputStream(gzipfile);
			GZIPInputStream gis = new GZIPInputStream(fis);
			FileOutputStream fos = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			gis.close();
		} catch (IOException e) {
			new GetStackTrace(e, this.getClass().toString());
		}
	}

	private void compressGzipFile(String file, String gzipFile) {
		try {
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(gzipFile);
			GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				gzipOS.write(buffer, 0, len);
			}
			// close resources
			gzipOS.close();
			fos.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}