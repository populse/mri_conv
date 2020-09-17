package dcm;

import java.util.ArrayList;
import MRIFileManager.FileManagerFrame;
import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;
import ij.plugin.DICOM;

public class SearchListRecursive implements DictionDicom {

	private ArrayList<String[]> listA = new ArrayList<>();
	private String[] files;
	private String chemDicom;

	public SearchListRecursive(String[] files, String chemDicom) {
		this.files = files;
		this.chemDicom = chemDicom;
		run();
	}

	public void run() {
		try {
			DICOM dcm;
			String[] listSlice;
			FileManagerFrame.dlg.setVisible(true);
			String title = "Loading : files ";

//			FileManagerFrame.dlg.setTitle(title);
			for (int i = 0; i < files.length; i++) {
				FileManagerFrame.dlg.setTitle(title + i * 100 / files.length + " %");
				dcm = new DICOM();
				dcm.open(chemDicom + PrefParam.separator + files[i]);
				listSlice = new String[21];
				listSlice[1] = dcm.getStringProperty("Image Number");
				if (listSlice[1] != null)
					if (!listSlice[1].isEmpty() && !listSlice[1].contentEquals("0")) {
						listSlice[0] = chemDicom + PrefParam.separator + files[i];
						listSlice[1] = listSlice[1].trim();
						listSlice[2] = dcm.getStringProperty("Echo Numbers(s)");
						listSlice[3] = dcm.getStringProperty("Slice Number");
						listSlice[4] = dcm.getStringProperty("Repetition Time");
						listSlice[5] = dcm.getStringProperty("Echo Time");
						listSlice[6] = dcm.getStringProperty("Inversion Time");
						listSlice[7] = dcm.getStringProperty("Slice Location");
						listSlice[8] = dcm.getStringProperty("Image Type");
						listSlice[8] = new ChangeSyntax().NewSyntaxType(listSlice[8]);
						listSlice[9] = dcm.getStringProperty("Temporal Position");
						listSlice[10] = dcm.getStringProperty("Rescale Intercept");
						listSlice[11] = dcm.getStringProperty("Rescale Slope");
						listSlice[12] = dcm.getStringProperty("Acquisition Time");
						listSlice[13] = dcm.getStringProperty("Image Position (Patient)");
						listSlice[14] = dcm.getStringProperty("Image Orientation (Patient)");
						listSlice[15] = dcm.getStringProperty("0018,9087"); // Diffusion b-value
						String ts = dcm.getStringProperty("0018,0020");
						ts = new ChangeSyntax().NewSyntaxScanSeq(ts);
//						int indSs = Arrays.asList(listScanSeq).indexOf(ts);
						listSlice[16] = ts; // scanning sequence
						listSlice[17] = dcm.getStringProperty("2005,1429");// Label Type (ASL)
						listSlice[18] = dcm.getStringProperty("2005,100E");// Scale Slope Philips
						if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
							listSlice[18] = "1";
						listSlice[19] = dcm.getStringProperty("2005,10B0") + " " + dcm.getStringProperty("2005,10B1") + " "
								+ dcm.getStringProperty("2005,10B2");
						listSlice[20] = dcm.getStringProperty("2005,1413");
						listA.add(listSlice);
					}
				dcm.close();
			}
			FileManagerFrame.dlg.setTitle(title + "100 %");
			FileManagerFrame.dlg.setVisible(false);

		} catch (Exception e) {
			new GetStackTrace(e);
		}
	}

	public ArrayList<String[]> getlistA() {
		return listA;
	}
}