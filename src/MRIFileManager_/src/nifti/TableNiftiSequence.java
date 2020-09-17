package nifti;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableNiftiSequence extends PrefParam implements ParamMRI2 {

	private Object[][] data;
	

	public TableNiftiSequence(String repertory) throws IOException {

		List<String> listNifti = searchNifti(repertory);

		if (listNifti != null) {
			String prefixSeq=("000000").substring(0, String.valueOf(listNifti.size()).length());

			data = new Object[listNifti.size()][headerListSeq.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Nifti file(s) found";

			FileManagerFrame.dlg.setVisible(true);
			String title = "Loading : sequence ";
			for (int i = 0; i < data.length; i++) {
				FileManagerFrame.dlg.setTitle(title + i * 100 / data.length + " %");
				try {
					data[i] = new ListNiftiSequence(repertory + separator + listNifti.get(i), (prefixSeq + i).substring(String.valueOf(i).length())).ListSeqNifti();
				} catch (Exception e) {
					data[i][0] = String.valueOf(i);
					data[i][1] = "(error)";
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			FileManagerFrame.dlg.setVisible(false);

		} else {
			data = new Object[1][headerListSeq.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = i;
			data[0][0] = "no Nifti file(s) found";
		}
	}

	private List<String> searchNifti(String rep) {
		String[] listOfFiles = new File(rep).list();
		List<String> listNifti = new ArrayList<>();

		Nifti1Dataset niftiParam;

		for (String lg : listOfFiles)
			if (lg.endsWith(".nii")) {
				try {
					niftiParam = new Nifti1Dataset(rep + separator + lg);
					niftiParam.readHeader();
					listNifti.add(lg);
				} catch (OutOfMemoryError e) {
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}

		return listNifti;
	}

	public Object[][] getSequence() {
		return data;
	}
}