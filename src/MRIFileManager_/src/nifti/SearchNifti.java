package nifti;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import abstractClass.PrefParam;

public class SearchNifti {

	private String rep;
	private List<String> listSeq = new ArrayList<>();

	public SearchNifti(String rep) {
		this.rep = rep;
		search();
	};

	public void search() {

		String[] listOfFiles = new File(rep).list();
		
		Nifti1Dataset niftiParam;

		for (String lg : listOfFiles)
			if (lg.toLowerCase().endsWith(".nii") || lg.toLowerCase().endsWith(".nii.gz") ) {
				try {
					niftiParam = new Nifti1Dataset(rep + PrefParam.separator + lg);
					niftiParam.readHeader();
					listSeq.add(lg);
				} catch (OutOfMemoryError e) {
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
	}

	public List<String> getList() {
		return listSeq;
	}
}