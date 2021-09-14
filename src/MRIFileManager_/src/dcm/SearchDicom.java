package dcm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.RandomAccessFile;

import com.pixelmed.dicom.DicomFileUtilities;

import abstractClass.PrefParam;

public class SearchDicom {
	
	StringBuffer[] listDicom;
	
	public SearchDicom(String pathFile) {
		
		final File dir = new File(pathFile);
		File[] files;
		
		FilenameFilter fileNameFilter;
		fileNameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (!new File(dir + PrefParam.separator + name).isHidden())
					try {
						return (DicomFileUtilities.isDicomOrAcrNemaFile(dir + PrefParam.separator + name));
					} catch (Throwable ed) {
					}
				return false;
			}
		};
		files = dir.listFiles(fileNameFilter);
		
		// try if Dicom is corrupted !
		if (files.length == 0) {

			fileNameFilter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (!new File(dir + PrefParam.separator + name).isHidden())
						try {
							RandomAccessFile raf = new RandomAccessFile(dir + PrefParam.separator + name, "r");
							raf.seek(128);
							if (raf.readLine().substring(0, 4).contains("DICM")) {
								raf.close();
								return true;
							}
							raf.close();
						} catch (Throwable ed) {
						}
					return false;
				}
			};
		}

		listDicom = new StringBuffer[files.length];
		for (int i = 0; i < files.length; i++)
			listDicom[i] = new StringBuffer(files[i].toString());

	}

	public StringBuffer[] listDicom() {
		return listDicom;
	}
}
