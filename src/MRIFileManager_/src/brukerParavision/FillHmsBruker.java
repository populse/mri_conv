package brukerParavision;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;

public class FillHmsBruker implements ParamMRI2 {

	public FillHmsBruker(String seqSel, String serialNumber) {

		String dim, nImage;

		try {
			ListBrukerParam listBrukPar = new ListBrukerParam(hmSeq.get(seqSel)[0], seqSel, serialNumber);
			hmInfo.put(seqSel, listBrukPar.ListParamValueAcq(""));
			dim = hmInfo.get(seqSel).get("Scan Mode");
			if (dim.contains("3"))
				nImage = hmInfo.get(seqSel).get("Scan Resolution").split(" +")[2];
			else
				nImage = hmInfo.get(seqSel).get("Images In Acquisition");
			hmOrderImage.put(seqSel, listBrukPar.ListOrderStackAcq(dim, nImage));
//			System.out.println("Nslices = "+hmInfo.get(seqSel).get("Number Of Slice"));


		} catch (Exception e) {
			new GetStackTrace(e);
		}
	}
}