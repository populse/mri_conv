package dcm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;

import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableDicomData extends PrefParam implements ParamMRI2 {

	private Object[][] data;
	private boolean rootDisk = false;
	private String[] listSousRep;

	private int numberOfDir = 2, count;

	public TableDicomData(String repertory) throws IOException {

		listSousRep = listesousrep(repertory);
		count = repertory.split("\\" + PrefParam.separator).length;

		if (listSousRep.length != 0) {

			data = new Object[listSousRep.length][headerListData.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Dicom file found";

			FileManagerFrame.dlg.setVisible(true);

			for (int i = 0; i < data.length; i++) {
				FileManagerFrame.dlg.setTitle("Loading : data " + i * 100 / data.length + " %");
				data[i] = new ListDicomData(repertory + separator + listSousRep[i]).listParamDataDicom();
				data[i][0] = iconDicom;
				data[i][1] = listSousRep[i];
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			FileManagerFrame.dlg.setVisible(false);

		}

		else {
			data = new Object[1][headerListData.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Dicom file found";
		}

	}

	private String[] listesousrep(String repertory) {

		String[] listrep;
		
		if (!repertory.contains(separator)) {
			// repertory += separator;
			rootDisk = true;
		}

		if (new File(repertory + separator + "DICOMDIR").exists()
				|| new File(repertory + separator + "DICOMDIR").exists()) {
			listrep= new String[1];
			listrep[0]="";

		}

		else {
			File listeSousRepertoire = new File(repertory);

			listrep = listeSousRepertoire.list(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (!new File(dir + separator + name).isHidden())
						if (isDicom(dir + separator + name))
							return true;
					return false;
				}
			});
		}

		if (rootDisk) {
			// repertory = repertory.substring(0, repertory.length() - 1);
			rootDisk = false;
		}

		return listrep;
	}

	private boolean isDicom(String repertoire1) {

		File sousrep = new File(repertoire1);

		if (sousrep.isDirectory())
			if (searchDicom(sousrep))
				return true;
			else
				return false;
		return false;
	}

	private boolean searchDicom(File searchIn) {

		if (searchIn.isDirectory() && searchIn.canRead()) {
			for (File temp : searchIn.listFiles()) {
				if (!temp.isDirectory() && temp.toString().split("\\" + File.separator).length <= (count + numberOfDir))
					searchDicom(temp);
				else {
					if (temp.toString().toLowerCase().contains("dicomdir"))
						return true;

					if (temp.toString().toLowerCase().contains("dirfile"))
						return true;

					else if (!temp.toString().endsWith("SR"))
						try {
							RandomAccessFile raf = new RandomAccessFile(temp, "r");
							raf.seek(128);
							if (raf.readLine().substring(0, 4).contains("DICM")) {
								raf.close();
								return true;
							}
							raf.close();
						} catch (Throwable ed) {
							// break;
						}
				}
			}
		}
		return false;
	}

	public Object[][] getData() {
		return data;
	}
}