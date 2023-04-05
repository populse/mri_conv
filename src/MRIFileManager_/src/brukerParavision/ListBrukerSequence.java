package brukerParavision;

import java.io.IOException;

import MRIFileManager.ExtractTxtfromFile;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListBrukerSequence extends PrefParam implements ParamMRI2 {

	private String[] listParamSeq = headerListSeq;
	private String chemSeq, noSeq, directory;

	public ListBrukerSequence(String directory, String chemSeq, String noSeq) {
		this.directory = directory;
		this.chemSeq = chemSeq;
		this.noSeq = noSeq;
	}

	public Object[] ListSeqBruker() throws IOException {

		String[] hmValue = new String[2];
		hmValue[0] = chemSeq.replace("*", "");
		hmValue[1] = "";

		String noSerial = chemSeq.substring(0, chemSeq.indexOf("pdata") - 1);
		noSerial = noSerial.substring(noSerial.lastIndexOf(separator) + 1);

		String noReco = chemSeq.substring(chemSeq.indexOf("pdata") + 6);
		noReco = noReco.substring(0, noReco.lastIndexOf(separator));

		Object[] resul = new String[listParamSeq.length];
		resul[0] = noSeq;
		if (Integer.parseInt(noSerial) < 10)
			noSerial = "0" + noSerial;
		resul[1] = noSerial;
		if (chemSeq.contains("*"))
			resul[1] += "-" + noReco;

		/**********************
		 * Hashmap hm add
		 *******************************************************/

		hmSeq.put(noSeq, hmValue);
		new FillHmsBruker(directory, noSeq, resul[1].toString());

		if (!hmInfo.get(noSeq).get("fileExists").isEmpty()) {
			resul[2] = hmInfo.get(noSeq).get("fileExists");
			for (int i = 3; i < resul.length; i++) {
				resul[i] = "";
			}
		}
		else {

			for (int i = 1; i < resul.length; i++) {
				resul[i] = hmInfo.get(noSeq).get(listParamSeq[i]);
			}

			String txtParam = new ExtractTxtfromFile(
					chemSeq.substring(0, chemSeq.indexOf("2dseq") - 1) + separator + "visu_pars").getTxt();

			String tmp = new SearchParamBruker2("##$VisuSeriesComment", txtParam).result();
			if (!tmp.isEmpty()) {
				tmp = tmp.replaceAll("<", "");
				tmp = tmp.replaceAll(">", "");
				resul[2] = tmp;
			}
			/********************************************************
			 * Format modification
			 *******************************************************/
			// resul[3] = new MRIFileManager.Dateformatmodif(resul[3].toString(),
			// dictionaryMRISystem.get(listParamSeq[3]).get("format"),
			// dictionaryJsonSystem.get(listParamSeq[3]).get("format")).getNewFormatDate();
			// resul[4] = new MRIFileManager.Dateformatmodif(resul[4].toString(),
			// dictionaryMRISystem.get(listParamSeq[4]).get("format"),
			// dictionaryJsonSystem.get(listParamSeq[4]).get("format")).getNewFormatDate();
			resul[7] = resul[7] + "D";
		}

		return resul;
	}
}