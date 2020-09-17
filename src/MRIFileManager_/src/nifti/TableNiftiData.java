package nifti;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableNiftiData extends PrefParam implements ParamMRI2 {

	private Object[][] data;
	private String repertory;
	private boolean rootDisk = false;
	private String[] listSousRep;

	public TableNiftiData(String repertory) throws IOException {

		this.repertory = repertory;
		listSousRep = listesousrep();

		if (listSousRep.length != 0) {
			data = new Object[listSousRep.length][headerListData.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Nifti data found";

			FileManagerFrame.dlg.setVisible(true);

			for (int i = 0; i < data.length; i++) {
				FileManagerFrame.dlg.setTitle("Loading : data "+i*100/data.length+" %");
				data[i] = new ListNiftiData(this.repertory + separator + listSousRep[i]).listParamDataNifti();
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
			data[0][0] = "no Nifti data found";
		}
	}

	private String[] listesousrep() {

		if (!repertory.contains(PrefParam.separator)) {
			repertory += PrefParam.separator;
			rootDisk = true;
		}

		File listeSousrepertory = new File(repertory);

		String[] listrep = listeSousrepertory.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (isNifTI(dir + separator + name))
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

	private boolean isNifTI(String repertoire1) {
		File sousrep = new File(repertoire1);
		if (sousrep.isDirectory())
			if (searchNifTI(".nii", sousrep))
				return true;
			else
				return false;
		return false;
	}

	public boolean searchNifTI(String extToFind, File searchIn) {
		String[] listOfFiles = searchIn.list();

		boolean find = false;
		int i = 0;

		try {
			while (i < listOfFiles.length && !find) {

				if (listOfFiles[i].endsWith(extToFind)) {
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