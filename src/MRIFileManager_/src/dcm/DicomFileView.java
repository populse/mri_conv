package dcm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;

public class DicomFileView extends FileView {

	@Override
	public Icon getIcon(File file) {

		if (file.isDirectory()) {
			try {
				if (searchDicom(file))
					return PrefParam.iconDicom;
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
		}
		return null;
	}

	@Override
	public Boolean isTraversable(File file) {
		if (file.isDirectory()) {
			try {
				if (searchDicom(file))
					return false;
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
		}
		return true;
	}

	public boolean searchDicom(File searchIn) throws IOException {
		File[] listOfFiles = searchIn.listFiles();

		boolean find = false;
		int i = 0;
		try {
			while (i < listOfFiles.length && !find) {
				if (listOfFiles[i].isFile() && listOfFiles[i].canRead()) {
					try {
						RandomAccessFile raf = new RandomAccessFile(listOfFiles[i], "r");
						raf.seek(128);
						if (raf.readLine().substring(0, 4).contains("DICM"))
							find = true;
						raf.close();
						if (find)
							break;
					} catch (Throwable ed) {
						break;
					}
					
					if (!find) 
						if (listOfFiles[i].toString().equalsIgnoreCase("dicomdir"))
							find = true;
						else if (listOfFiles[i].toString().equalsIgnoreCase("dirfile"))
							find = false;
				}
				i++;
			}
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
		return find;
	}

}
