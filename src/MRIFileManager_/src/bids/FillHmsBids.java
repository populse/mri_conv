package bids;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;

public class FillHmsBids implements ParamMRI2 {

	public FillHmsBids(String seqSel) {

		try {
//			ListNiftiParam2 listNiftiPar = new ListNiftiParam2(seqSel);
//			HashMap<String, String> tmp = listNiftiPar.ListParamValueAcq("");
//			File tmpFile = new File(hmSeq.get(seqSel)[0]);
//			tmp.put("Patient Name",hmSeq.get(seqSel)[1]);
//			tmp.put("Study Name",hmSeq.get(seqSel)[2]);
//			tmp.put("Creation Date"," ");
//			tmp.put("Patient Sex"," ");
//			tmp.put("Patient Weight"," ");
//			tmp.put("Patient BirthDate"," ");
//			tmp.put("Protocol",
//					tmpFile.getParent().substring(tmpFile.getParent().lastIndexOf(PrefParam.separator) + 1));
//			tmp.put("Sequence Name", tmpFile.getName().substring(0, tmpFile.getName().indexOf(".nii.gz")));
//			hmInfo.put(seqSel, tmp);
//			hmOrderImage.put(seqSel, listNiftiPar.ListOrderStackAcq("", ""));
			
			ListBidsParam listBidsPar = new ListBidsParam(seqSel);
			hmInfo.put(seqSel, listBidsPar.ListParamValueAcq(""));
			hmOrderImage.put(seqSel, listBidsPar.ListOrderStackAcq("", ""));
			
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
	}
}