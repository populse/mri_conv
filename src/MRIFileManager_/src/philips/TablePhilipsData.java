package philips;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TablePhilipsData extends PrefParam implements ParamMRI2 {

	private Object[][] data;
	private String repertory;
	private boolean rootDisk = false;
	private String[] listSousRep;

	public TablePhilipsData(String repertory) throws IOException {

		this.repertory = repertory;
		if (DirectoryDataOnly.isEmpty())
			listSousRep = listesousrep();
		else
			listSousRep = new String[] {DirectoryDataOnly};

		if (listSousRep.length != 0) {
			data = new Object[listSousRep.length][headerListData.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Philips V4.2 data found";

			try {
				for (int i = 0; i < data.length; i++) {
					data[i] = new ListPhilipsData(this.repertory + separator + listSousRep[i]).listParamDataPhilips();
					data[i][1] = listSousRep[i];
				}
			} catch (Exception e) {
			}
			
		}

		else {
			data = new Object[1][headerListData.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Philips V4.2 data found";
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
				if (isPhilips(dir + separator + name))
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

	private boolean isPhilips(String repertoire1) {
		File sousrep = new File(repertoire1);

		if (sousrep.isDirectory())
			if (searchPhilips(".REC", sousrep))
				return true;
			else
				return false;
		return false;
	}

	public boolean searchPhilips(String extToFind, File searchIn) {
		String[] listOfFiles = searchIn.list();

		boolean find = false;
		int i = 0;

		try {
			while (i < listOfFiles.length && !find) {

				if (listOfFiles[i].toUpperCase().endsWith(extToFind)) {
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