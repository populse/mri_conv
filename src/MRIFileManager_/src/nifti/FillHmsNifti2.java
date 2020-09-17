package nifti;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;

public class FillHmsNifti2 implements ParamMRI2 {

	public FillHmsNifti2(String seqSel) {

			try {
				ListNiftiParam2 listNiftiPar = new ListNiftiParam2(seqSel);
				hmInfo.put(seqSel, listNiftiPar.ListParamValueAcq(""));
				hmOrderImage.put(seqSel, listNiftiPar.ListOrderStackAcq("",""));
			} catch (Exception e) {
				new GetStackTrace(e);
			}
	}
}