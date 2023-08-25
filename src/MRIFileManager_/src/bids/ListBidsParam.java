package bids;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

import MRIFileManager.GetStackTrace;
import abstractClass.ListParam2;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import nifti.Nifti1Dataset;
import nifti.SearchParamNifti;

public class ListBidsParam implements ParamMRI2, ListParam2 {
	private String seqSel;
	private String scanResol;

	public ListBidsParam(String seqSel) {
		this.seqSel = seqSel;
	}

	@Override
	public HashMap<String, String> ListParamValueAcq(String it) throws IOException {
		HashMap<String, String> lv = new HashMap<>();
		String chemNifti = hmSeq.get(seqSel)[0];
		File fileNifti = new File(chemNifti);
		lv.put("noSeq", seqSel);
		lv.put("File path", fileNifti.getAbsolutePath());
		lv.put("File Name", fileNifti.getName());
		lv.put("File Size (Mo)", String.valueOf(fileNifti.length() / (1024 * 1024.0)));
		lv.put("Serial Number", "");

		String headerNifti = "";
		Nifti1Dataset niftiParam;
		niftiParam = new Nifti1Dataset(chemNifti);

		try {
			niftiParam.readHeader();
			headerNifti = niftiParam.getHeader();
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

//		HashMap<String, String> listObjectJson = new HashMap<>();
		HashMap<String, HashMap<String, String>> listObjectJson = new HashMap<>();
		HashMap<String, String> listObjectJson2 = new HashMap<>();
		File JsonFile = new File(chemNifti.replace(".nii.gz", ".json"));

		if (!JsonFile.exists()) {
			String[] extensions = new String[] { "json"};
			List<File> files = (List<File>) FileUtils.listFiles(JsonFile.getParentFile().getAbsoluteFile(), extensions, true);
			JsonFile = files.get(0);
		}

		ReadJson2 readjson = null;

		if (JsonFile.exists()) {

			readjson = new ReadJson2(JsonFile.getAbsolutePath());
			if (readjson.isGoodJsonVersion())
				listObjectJson = readjson.getlistObject();
			else
				listObjectJson2 = readjson.getlistObject2();
		}

		lv.put("JsonVersion", "ko");

		if (readjson.isGoodJsonVersion())

			for (String sg : dictionaryMRISystem.keySet()) {
				try {
					if (dictionaryMRISystem.get(sg).get("file").contains("niftiheader"))
						lv.put(sg, new SearchParamNifti(dictionaryMRISystem.get(sg).get("keyName"), headerNifti).result());
					else {
						String tmp;
						tmp = listObjectJson.get(dictionaryMRISystem.get(sg).get("keyName")).get("value");
						if (tmp == null)
							tmp = "";
						else {
							tmp = tmp.replace("[", "");
							tmp = tmp.replace("]", "");
							tmp = tmp.replace("\"", "");
							tmp = tmp.replace(",", " ");
	//						tmp = tmp.replace("ms","");
	//						tmp = tmp.replace("�", "");
						}
						lv.put(sg, tmp);
					}
				} catch (Exception e) {
				}
			}

		else {
			for (String sg : dictionaryMRISystem.keySet()) {
				try {
					if (dictionaryMRISystem.get(sg).get("file").contains("niftiheader"))
						lv.put(sg, new SearchParamNifti(dictionaryMRISystem.get(sg).get("keyName"), headerNifti).result());
					else {
						String tmp;
						tmp = listObjectJson2.get(dictionaryMRISystem.get(sg).get("keyName"));
						if (tmp == null)
							tmp = "";
						else {
							tmp = tmp.replace("[", "");
							tmp = tmp.replace("]", "");
							tmp = tmp.replace("\"", "");
							tmp = tmp.replace(",", " ");
	//						tmp = tmp.replace("ms","");
	//						tmp = tmp.replace("�", "");
						}
						lv.put(sg, tmp);
					}
				} catch (Exception e) {
				}
			}
		}

		lv.put("Patient Name", hmSeq.get(seqSel)[1]);
		lv.put("Study Name", hmSeq.get(seqSel)[2]);
		lv.put("Creation Date", "");
		lv.put("Patient Sex", "");
		lv.put("Patient Weight", "");
		lv.put("Patient BirthDate", "");
		lv.put("Protocol", fileNifti.getParent().substring(fileNifti.getParent().lastIndexOf(PrefParam.separator) + 1));
		lv.put("Sequence Name", fileNifti.getName().substring(0, fileNifti.getName().indexOf(".nii.gz")));

		lv.put("Offset data blob", new SearchParamNifti("File offset to data blob", headerNifti).result());

		for (String sh : dictionaryMRIUser.keySet()) {
			try {
				if (dictionaryMRIUser.get(sh).get("file").contains("niftiheader"))
					lv.put(sh, new SearchParamNifti(dictionaryMRIUser.get(sh).get("keyName"), headerNifti).result());
				else {
					String tmp;
					tmp = listObjectJson.get(dictionaryMRIUser.get(sh).get("keyName")).get("Value");
					if (tmp == null)
						tmp = "";
					else {
						tmp = tmp.replace("[", "");
						tmp = tmp.replace("]", "");
						tmp = tmp.replace("\"", "");
						tmp = tmp.replace(",", " ");
					}
					lv.put(sh, tmp);
				}
			} catch (Exception e) {
//				new GetStackTrace(e);
			}
		}
				
		String tmp;
		
		/**********************************************************
		 * Slice Orientation by default
		 **********************************************************/
		try {
			lv.get("Slice Orientation").isEmpty();
		} catch (Exception e) {
			lv.put("Slice Orientation", "unknow");
		}
		
		/**********************************************************
		 * delete 'ms' in EchoTime, RepetitionTime and InversionTime
		 *********************************************************/
		try {
			tmp = lv.get("Echo Time");
			tmp = tmp.replace("ms", "").trim();
			lv.put("Echo Time", tmp);
		} catch (Exception e) {
		}

		try {
			tmp = lv.get("Repetition Time");
			tmp = tmp.replace("ms", "").trim();
			lv.put("Repetition Time", tmp);
		} catch (Exception e) {
		}

		try {
			tmp = lv.get("Inversion Time");
			tmp = tmp.replace("ms", "").trim();
			lv.put("Inversion Time", tmp);
		} catch (Exception e) {
		}

		/***********************************************************
		 * delete "�" inFlipAngle
		 *********************************************************/
		try {
			tmp = lv.get("Flip Angle");
			tmp = tmp.replace("�", "").trim();
			lv.put("Flip Angle", tmp);
		} catch (Exception e) {
		}

		/********************************
		 * recalculate spatial resol
		 *******************************/
		String spatResol = lv.get("Spatial Resolution");

		lv.put("Spatial Resolution",
				spatResol.split(" +")[0] + " " + spatResol.split(" +")[1] + " " + spatResol.split(" +")[2]);

		/******************************************************************
		 * recalculate scan resol and number of Slice,Echo,Repetition
		 *****************************************************************/

		scanResol = lv.get("Scan Resolution");

		lv.put("Scan Resolution", scanResol.split(" +")[1] + " " + scanResol.split(" +")[2]);

		lv.put("Number Of Slice", scanResol.split(" +")[3]);

		String[] ldim = scanResol.split(" +");

		if (ldim.length == 4)
			lv.put("Images In Acquisition", ldim[3]);
		if (ldim.length == 5) {
			lv.put("Images In Acquisition", String.valueOf(Integer.parseInt(ldim[3]) * Integer.parseInt(ldim[4])));
		}
		if (ldim.length == 6) {
			lv.put("Images In Acquisition",
					String.valueOf(Integer.parseInt(ldim[3]) * Integer.parseInt(ldim[4]) * Integer.parseInt(ldim[5])));
		}
		return lv;
	}

	@Override
	public Object[] ListOrderStackAcq(String dim, String nImage) {
		Object[] lv = new Object[5];
		lv[0] = "xyczt";
		lv[1] = 1;
		lv[2] = 1;
		lv[3] = 1;
		lv[4] = null;

		String[] ldim = scanResol.split(" +");

		if (ldim.length == 4)
			lv[2] = Integer.parseInt(ldim[3]);
		if (ldim.length == 5) {
			lv[2] = Integer.parseInt(ldim[3]);
			lv[3] = Integer.parseInt(ldim[4]);
		}
		if (ldim.length == 6) {
			lv[1] = Integer.parseInt(ldim[3]);
			lv[2] = Integer.parseInt(ldim[4]);
			lv[3] = Integer.parseInt(ldim[5]);
		}
		return lv;
	}

	@Override
	public HashMap<String, String> ListParamValueCal(String it) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] ListOrderStackCal(String dim, String nImage) {
		// TODO Auto-generated method stub
		return null;
	}

}
