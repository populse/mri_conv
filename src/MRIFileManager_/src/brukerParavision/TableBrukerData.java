package brukerParavision;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableBrukerData extends PrefParam implements ParamMRI2 {

	private Object[][] data;
	private String repertory;
	private boolean rootDisk = false;
	private String[] listSousRep;

	public TableBrukerData(String repertory) throws IOException {

		this.repertory = repertory;
		listSousRep = listesousrep();

		if (listSousRep.length != 0) {
			data = new Object[listSousRep.length][headerListData.length];
			for (int i = 0; i < data[0].length; i++) 
				data[0][i] = "";
			data[0][0] = "no Bruker subject file found";
			
			FileManagerFrame.dlg.setVisible(true);
			
			for (int i = 0; i < data.length; i++) {
				FileManagerFrame.dlg.setTitle("Loading : data "+i*100/data.length+" %");
				data[i] = new ListBrukerData(this.repertory + separator + listSousRep[i] + separator + "subject")
						.listParamDataBruker();
				data[i][1] = listSousRep[i];
				data[i][0] = iconBruker;
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
			data[0][0] = "no Bruker subject file found";
		}
	}

	private String[] listesousrep() {

		if (!repertory.contains(separator)) {
			repertory += separator;
			rootDisk = true;
		}

		File listeSousRepertoire = new File(repertory);

		String[] listrep = listeSousRepertoire.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (isBruker(dir + separator + name))
					return true;
				return false;
			}
		});

		if (rootDisk) {
			repertory = repertory.substring(0, repertory.length() - 1);
			rootDisk = false;
		}

		return listrep;
	}

	private boolean isBruker(String repertoire1) {
		File sousrep = new File(repertoire1);

		if (sousrep.isDirectory())
			// if (searchSubject("subject", sousrep) &&
			// searchSubject("AdjStatePerStudy", sousrep)) return true;
			if (searchSubject("subject", sousrep))
			return true;
			else
			return false;
		return false;
	}

	private boolean searchSubject(String fileToFind, File searchIn) {
		String[] listOfFiles = searchIn.list();
		boolean find = false;
		int i = 0;
		try {
			while (i < listOfFiles.length && !find) {

				if (listOfFiles[i].contentEquals(fileToFind)) {
					File fich = new File(searchIn.toString() + separator + listOfFiles[i]);
					if (fich.length() != 0)
						find = true;
				}
				i++;
			}
		} catch (Exception e) {
			find = false;
		}
		return find;
	}

	public Object[][] getData() {
		return data;
	}
}