package bids;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;

public class BidsFileView extends FileView{
	
	@Override
	public Icon getIcon(File file) {
		File f = new File(file.getPath());

		if (f.isDirectory()) {
			if (searchBids("dataset_description.json", file))
				return PrefParam.iconBids;
		}
		return null;
	}

	@Override
	public Boolean isTraversable(File file) {
		if (file.isDirectory())
			if (searchBids("dataset_description.json", file))
				return false;
		return true;
	}
	
	public boolean searchBids(String fileToFind, File searchIn) {
		String[] listOfFiles = searchIn.list();
		boolean find = false;
		int i = 0;

		try {
			while (i < listOfFiles.length && !find) {
				if (listOfFiles[i].contentEquals(fileToFind)) {
					find = true;
					break;
				}
				i++;
			}
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
//			FileManagerFrame.getBugText().setText(
//					FileManagerFrame.getBugText().getText() + "\n----------------\n" + GetStackTrace.getMessage());
		}
		return find;
	}

}
