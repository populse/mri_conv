package bids;

import java.io.IOException;

import abstractClass.ParamMRI2;

public class ListBidsSequence implements ParamMRI2 {
	
	private String[] listParamSeq = headerListSeq;
	private String noSeq;
	private String pathBids;
	private String[] PatientStudyName;

	public ListBidsSequence(String[] PatientStudyName,String pathBids, String noSeq) {
		this.pathBids = pathBids;
		this.noSeq = noSeq;
		this.PatientStudyName=PatientStudyName;
	}

	public String[] ListSeqBids() throws IOException {
		
		String[] resul = new String[listParamSeq.length];
		resul[0] = noSeq;
		
		String[] hmValue = new String[3];
		hmValue[0] = pathBids;
		hmValue[1] = PatientStudyName[0];
		hmValue[2] = PatientStudyName[1];
		hmSeq.put(noSeq, hmValue);
		new FillHmsBids(noSeq);

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