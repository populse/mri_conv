package nifti;

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;

public class NiftiFileView extends FileView {

	@Override
	public Icon getIcon(File file) {
		File f = new File(file.getPath());

		if (f.isDirectory()) {
			if (searchNifTI("nii", file))
				return PrefParam.iconNifTI;
		}
		return null;
	}

	@Override
	public Boolean isTraversable(File file) {
		if (file.isDirectory())
			if (searchNifTI("nii", file))
				return false;
		return true;
	}

	public boolean searchNifTI(String extToFind, File searchIn) {
		String[] listOfFiles = searchIn.list();
		boolean find = false;
		int i = 0;

		try {
			while (i < listOfFiles.length && !find) {
				if (listOfFiles[i].endsWith(extToFind)) {
					find = true;
					break;
				}
				i++;
			}
		} catch (Exception e) {
			new GetStackTrace(e);
//			FileManagerFrame.getBugText().setText(
//					FileManagerFrame.getBugText().getText() + "\n----------------\n" + GetStackTrace.getMessage());
		}
		return find;
	}
}