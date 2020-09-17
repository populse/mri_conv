package brukerParavision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import MRIFileManager.Dateformatmodif;
import MRIFileManager.ExtractTxtfromFile;
import abstractClass.ParamMRI2;

public class ListBrukerData implements ParamMRI2 {

	private String chemSubject;
	private String[] paramListData = headerListData;

	public ListBrukerData(String chemSubject) {
		this.chemSubject = chemSubject;
	}

	public Object[] listParamDataBruker() throws IOException {
		Object[] resul = new Object[paramListData.length];

		String txtParam = new ExtractTxtfromFile(chemSubject).getTxt();

		for (int i = 2; i < paramListData.length-1; i++) {
			// resul[i]= SearchParam(dictionaryParamMRI.get(paramListData[i])[0] ,txtParam);
			resul[i] = SearchParam(dictionaryMRISystem.get(paramListData[i]).get("keyName"), txtParam);
		}
		try {
			resul[4] = new Dateformatmodif(resul[4].toString(), dictionaryMRISystem.get(paramListData[4]).get("format"),
				dictionaryJsonSystem.get(paramListData[4]).get("format")).getNewFormatDate();
		} catch (Exception e) {
			resul[4] = "";
		}
		resul[8]="";
		return resul;
	}

	private String SearchParam(String paramToFind, String fichier) {
		BufferedReader lecteurAvecBuffer = null;
		String ligne, ligneb;
		boolean find = false;
		try {
			lecteurAvecBuffer = new BufferedReader(new StringReader(fichier));
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				if (ligne.indexOf(paramToFind) != -1) {
					if (ligne.contains("("))
						ligne = lecteurAvecBuffer.readLine();
					else
						ligne = ligne.substring(ligne.indexOf("=") + 1);
					ligneb = lecteurAvecBuffer.readLine();
					if (!ligneb.contains("##") && !ligneb.contains("$$"))
						ligne += ligneb;
					lecteurAvecBuffer.close();
					find = true;
					ligne = ligne.replaceAll("<", "");
					ligne = ligne.replaceAll(">", "");
					return ligne;
				}
				if (find)
					break;
			}
			lecteurAvecBuffer.close();
		} catch (Exception exc) {
			return "";
		}
		return "";
	}
}