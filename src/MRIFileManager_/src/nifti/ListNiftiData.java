package nifti;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListNiftiData extends PrefParam implements ParamMRI2 {

	private String pathNifti;
	private String[] paramListData = headerListData;

	public ListNiftiData(String pathNifti) {
		this.pathNifti = pathNifti;
	}

	public Object[] listParamDataNifti() throws IOException {
		Object[] resul = new Object[paramListData.length];
		String tmp;
		HashMap<String, HashMap<String, String>> listTagJson;

		tmp = searchNifTI(".nii", pathNifti);
//		File f = new File(tmp);
		tmp = tmp.replace(".nii", ".json");

//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		Date d = new Date(f.lastModified());

		resul[0] = iconNifTI;

		if (new File(tmp).exists()) {
			listTagJson = new ReadJson2(tmp).getlistObject();
			for (int i = 2; i < paramListData.length-1; i++) {
				try {
					tmp = listTagJson.get(dictionaryMRISystem.get(paramListData[i]).get("keyName")).get("value");
					tmp = tmp.replace("[", "");
					tmp = tmp.replace("]", "");
					tmp = tmp.replace("\"", "");
					tmp = tmp.replace(",", " ");
				}
				catch (Exception e){
					tmp ="";
				}
//				if (tmp == null)
//					tmp = "";
//				else {
//					tmp = tmp.replace("[", "");
//					tmp = tmp.replace("]", "");
//					tmp = tmp.replace("\"", "");
//					tmp = tmp.replace(",", " ");
//				}
				resul[i] = tmp;
			}
		} else {
			for (int i = 2; i < paramListData.length-1; i++)
				resul[i] = "";
		}
//		Date d = new Date(resul[4].toString());
//		resul[4] = sdf.format(d);
		resul[8]="";

		return resul;
	}

	private String searchNifTI(String extToFind, String searchIn) {

		String fileNifTI = null;
		String[] listOfFiles = new File(searchIn).list();

		int i = 0;

		try {
			while (i < listOfFiles.length) {
				if (listOfFiles[i].endsWith(extToFind)) {
					fileNifTI = listOfFiles[i];
					break;
				}
				i++;
			}
		} catch (Exception e) {

		}
		return searchIn + separator + fileNifTI;
	}
}