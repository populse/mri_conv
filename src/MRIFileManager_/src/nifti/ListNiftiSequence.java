package nifti;

import java.io.IOException;

import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListNiftiSequence extends PrefParam implements ParamMRI2 {

	private String[] listParamSeq = headerListSeq;
	private String noSeq;
	private String pathNifti;

	public ListNiftiSequence(String pathNifti, String noSeq) {

		this.pathNifti = pathNifti;
		this.noSeq = noSeq;

	}

	public String[] ListSeqNifti() throws IOException {

		String[] resul = new String[listParamSeq.length];
		resul[0] = noSeq;

		/**********************
		 * Hashmap hm add
		 *******************************************************/
		String[] hmValue = new String[1];
		hmValue[0] = pathNifti;
		hmSeq.put(noSeq, hmValue);
		new FillHmsNifti2(noSeq);

		for (int i = 1; i < resul.length; i++) {
			resul[i] = hmInfo.get(noSeq).get(listParamSeq[i]);
			if (resul[i] == null)
				resul[i] = "";
			else {
				resul[i] = resul[i].replace("[", "");
				resul[i] = resul[i].replace("]", "");
				resul[i] = resul[i].replace("\"", "");
				resul[i] = resul[i].replace(",", " ");
			}
		}
		return resul;
	}
}