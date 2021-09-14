package dcm;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class TableDicomSequence extends PrefParam implements ParamMRI2 {

	private Object[][] data;
	private String[] listParamSeq = headerListSeq;

	public TableDicomSequence(String repertory, String formatDicom) {

		try {
			// switch (formatDicom) {
			// case "DIRFILE":
			// new ListDirfileSequence(repertory);
			// break;
			//
			// case "DICOMDIR":
			// new ListDicomDirSequence(repertory);
			// break;
			//
			// case "DCM":
			// new ListDcmSequence(repertory);
			// break;
			// }
			if (formatDicom.contains("DIRFILE"))
				new ListDirfileSequence(repertory);
			else if (formatDicom.contains("DICOMDIR")) {
				if (simplifiedViewDicom)
					new ListDicomDirSequenceSimplified(repertory);
				else
					new ListDicomDirSequence2(repertory);
			} else if (formatDicom.contains("DCM"))
				new ListDcmSequence(repertory, false);
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
			hmInfo.clear();
		}

		if (!hmInfo.isEmpty()) {
			data = new Object[hmInfo.size()][listParamSeq.length];
			int k = 0;
			for (String jj : hmInfo.keySet()) {
				data[k][0] = jj;
				for (int i = 1; i < listParamSeq.length; i++) {
					data[k][i] = hmInfo.get(jj).get(listParamSeq[i]);
				}
				k++;
			}

		} else {
			data = new Object[1][headerListSeq.length];
			for (int i = 0; i < data[0].length; i++)
				data[0][i] = "";
			data[0][0] = "no Dicom sequence found";
		}
	}

	public Object[][] getSequence() {
		return data;
	}
}