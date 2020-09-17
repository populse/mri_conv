package philips;

import java.io.File;
import java.io.IOException;

import MRIFileManager.Dateformatmodif;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListPhilipsData extends PrefParam implements ParamMRI2 {

	private String chemPar;
	private String[] paramListData = headerListData;

	public ListPhilipsData(String chemPar) {
		this.chemPar = chemPar;
	}

	public Object[] listParamDataPhilips() throws IOException {
		Object[] resul = new Object[paramListData.length];

//		File f = new File(searchPhilips(".REC", chemPar));
//
//		String tmpFile = f.getAbsolutePath().replace(".REC", ".PAR");
//		tmpFile = tmpFile.replace(".rec", ".par");
//
//		File fp = new File(tmpFile);
//
//		ListPhilipsParamData infoParam = null;
//		int ind = 0;
//
//		if (fp.exists()) {
//			infoParam = new GetInfofromPar(fp.getAbsolutePath(), false);
//			formatPhilips = "PAR";
//		} else {
//			infoParam = new GetInfofromXML(f.getAbsolutePath().replace(".REC", ".xml"), false);
//			ind = 1;
//			formatPhilips = "XML";
//		}

		//new
		String f = searchPhilips(".REC", chemPar);
		String tmpFile = "";
		int ind = 0;
		ListPhilipsParamData infoParam = null;

		String[] listExt = {".PAR", ".par", ".xml", ".XML"};
				
		for (String es : listExt) {
			tmpFile = f.replace(".REC", es);
			tmpFile = tmpFile.replace(".rec", es);
			if (new File(tmpFile).exists())
				break;
		}

		if (tmpFile.toUpperCase().endsWith(".PAR")) {
			infoParam = new GetInfofromPar(tmpFile, false);
			formatPhilips = "PAR";
		}
		else {
			infoParam = new GetInfofromXML(tmpFile, false);
			formatPhilips = "XML";
			ind = 1;
		}
		// end new
		
		
		resul[0] = iconPhilips;
		resul[1] = chemPar;

		String tmp = null;

		for (int i = 2; i < resul.length - 1; i++) {
			resul[i] = "nc";
			try {
				tmp = dictionaryMRISystem.get(paramListData[i]).get("keyName").split(";")[ind].trim();
				resul[i] = infoParam.getInfoImageAcq().get(tmp);
			} catch (Exception e) {

			}
			if (resul[i] == null)
				resul[i] = "nc";
		}

		resul[4] = new Dateformatmodif(resul[4].toString(),
				dictionaryMRISystem.get(paramListData[4]).get("format").split(";")[ind].trim(),
				dictionaryJsonSystem.get(paramListData[4]).get("format")).getNewFormatDate();
		resul[8] = "";

//		try {
//		resul[4]=resul[4].toString().replace(".", "-");
//		resul[4]=resul[4].toString().replace("/", "_");
//		resul[4]=resul[4].toString().replace(":", "-");
//		}
//		catch (Exception e) {
//			
//		}

		if (resul[2].toString().contentEquals("nc"))
			resul[0] = "No Philips V4.2 found";

		return resul;
	}

	private String searchPhilips(String extToFind, String searchIn) {

		String filePhilips = null;
		String[] listOfFiles = new File(searchIn).list();

		int i = 0;

		try {
			while (i < listOfFiles.length) {
				if (listOfFiles[i].toUpperCase().endsWith(extToFind)) {
					filePhilips = listOfFiles[i];
					break;
				}
				i++;
			}
		} catch (Exception e) {

		}
		return searchIn + separator + filePhilips;
	}
}