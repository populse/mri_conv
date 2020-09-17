package dcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MRIFileManager.Dateformatmodif;
import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListDicomDirSequenceSimplified implements ParamMRI2, DictionDicom, Runnable {

	private String chemDicom;
	private HashMap<String, String[]> hmSeqbis = new HashMap<>();
	private HashMap<String, String> listNumberOfFrame = new HashMap<>();

	public ListDicomDirSequenceSimplified(String chemDicom) {
		this.chemDicom = chemDicom;
		run();
	}

	@Override
	public void run() {

		String chemDicomdir = hmData.get(chemDicom);

		StringBuffer headerDicom = new StringBuffer(new HeaderDicom().getHeaderDicom(chemDicomdir));

		String[] listImDcm;
		String noSerial = null;
		
		ArrayList<String[]> listPathDicom = new ArrayList<>();
				

		/****************************************************************************************************************/

		listImDcm = headerDicom.toString().split("0020,0011"); // Series Number

		for (int i = 0; i < listImDcm.length; i++) {

			// get series number
			noSerial = listImDcm[i].substring(listImDcm[i].indexOf(": ") + 2, listImDcm[i].indexOf("\n"));

			// 0004,1500 -> path file listParam[1]
			// 0008,0008 -> Image Type listParam[2]
			// 0008,0020 -> Study Date listParam[3]
			// 0008,0021 -> Series Date listParam[4]
			// 0008,0030 -> Study Time listParam[5]
			// 0008,0031 -> Series Time listParam[6]
			// 0008,1030 -> Study Description listParam[7]
			// 0008,103E -> Series Description listParam[8]
			// 0018,0020 -> Scanning Sequence listParam[9]
			// 0028,0008 -> Number of Frames listParam[10]
			// 0020,0013 -> Image Number listParam[0]

			Pattern pat = Pattern.compile(
					"(0004,1500|0008,0008|0008,0020|0008,0021|0008,0030|0008,0031|0008,1030|0008,103E|0018,0020|0020,0013|0028,0008)(\\s)(.*)");
			Matcher match = pat.matcher(listImDcm[i]);
			Boolean val = false;
			String[] listParam = null;
			String[] initialize = { "", "", "", "", "", "", "", "", "", "", "" };
			listParam = new String[11];
			listParam = initialize;

			String tmp;
			while (match.find()) {
				String resul = match.group();
				// System.out.println(this + " : resul = " + resul);

				if (resul.contains("0004,1500") && val && listParam[0] != "" && listParam[1] != ""
						&& listParam[2] != "") {
					listParam[10] = "1";
					listPathDicom.add(listParam.clone());
					val = false;
				}
				if (resul.contains("0004,1500") && !resul.contains("XX_") && !resul.contains("PS_")
						&& resul.contains(":")) {
					listParam = new String[11];
					listParam = initialize;
					tmp = resul.substring(resul.indexOf(": ") + 2).trim();
					listParam[1] = tmp.replace("\\", PrefParam.separator);
					val = true;
				}
				if (resul.contains("0008,0008") && val) {
					listParam[2] = resul.substring(resul.indexOf(": ") + 2);
				}
				if (resul.contains("0008,0020") && val) {
					listParam[3] = resul.substring(resul.indexOf(": ") + 2);
				}
				if (resul.contains("0008,0021") && val) {
					listParam[4] = resul.substring(resul.indexOf(": ") + 2);
				}
				if (resul.contains("0008,0030") && val) {
					listParam[5] = resul.substring(resul.indexOf(": ") + 2);
				}
				if (resul.contains("0008,0031") && val) {
					listParam[6] = resul.substring(resul.indexOf(": ") + 2);
				}
				if (resul.contains("0008,1030") && val) {
					listParam[7] = resul.substring(resul.indexOf(": ") + 2);
				}
				if (resul.contains("0008,103E") && val) {
					listParam[8] = resul.substring(resul.indexOf(": ") + 2);
				}
				if (resul.contains("0018,0020") && val) {
					listParam[9] = resul.substring(resul.indexOf(": ") + 2).trim();
				}

				if (resul.contains("0020,0013") && resul.contains(":") && val && !resul.contains("Image Number: 0")) {
					listParam[0] = resul.substring(resul.indexOf(": ") + 2).trim();
				}
				if (resul.contains("0028,0008") && val && !listParam[0].contentEquals("0")) {
					listParam[10] = resul.substring(resul.indexOf(": ") + 2).trim();
					// System.out.println("listParam[10] = " + listParam[10]);
					// System.out.print("listParam : ");
					// for (String hh : listParam)
					// System.out.print(hh + " ");
					// System.out.println(" ");
					listPathDicom.add(listParam.clone());
					val = false;
				}
				if (resul.contains("0004,1500") && val && listParam[0] != "" && listParam[1] != ""
						&& listParam[2] != "") {
					listParam[10] = "1";
					listPathDicom.add(listParam.clone());
					val = false;
				}
			}

			if (!listPathDicom.isEmpty()) {

				if (listPathDicom.size() > 2)
					Collections.sort(listPathDicom, new Comparator<Object[]>() {
						@Override
						public int compare(Object[] strings, Object[] otherStrings) {
							return ((Integer) Integer.parseInt(strings[0].toString()))
									.compareTo(Integer.parseInt(otherStrings[0].toString()));
						}
					});

				ArrayList<String> listPathAcq = new ArrayList<>();
				ArrayList<String> listPathCal = new ArrayList<>();

				for (int h = 0; h < listPathDicom.size(); h++) {

					String tp = new ChangeSyntax().NewSyntaxType(listPathDicom.get(h)[2]);
					int indTp = Arrays.asList(listType).indexOf(tp);
					String ts = new ChangeSyntax().NewSyntaxScanSeq(listPathDicom.get(h)[9]);
					int indSs = Arrays.asList(listScanSeq).indexOf(ts);

					if (indTp < 5) {
						if (indSs < 4) {
							listPathAcq.add(chemDicom + PrefParam.separator + listPathDicom.get(h)[1].toString());
						} else {
							listPathCal.add(chemDicom + PrefParam.separator + listPathDicom.get(h)[1].toString());
						}
					} else {
						listPathCal.add(chemDicom + PrefParam.separator + listPathDicom.get(h)[1].toString());
					}
				}

				Object[] list;
				
				String[] value = hmSeqbis.get(noSerial);
				
				if (value!=null)
					noSerial+="bis";
				
				
				if (listPathCal.isEmpty()) {
					list = listPathAcq.toArray();
					hmSeqbis.put(noSerial, Arrays.copyOf(list, list.length, String[].class));
					if (listPathAcq.size() == 1)
						listNumberOfFrame.put(noSerial, listPathDicom.get(0)[10].trim()); // in case number Of Frame >1

				} else {
					list = listPathCal.toArray();
					noSerial += "(calc)";
					hmSeqbis.put(noSerial, Arrays.copyOf(list, list.length, String[].class));
					if (listPathCal.size() == 1)
						listNumberOfFrame.put(noSerial, listPathDicom.get(0)[10].trim()); // in case number Of Frame >1
				}
				listPathDicom.clear();
			}
		}

		/****************************************************************************************************************/

		listImDcm = null; // to free memory
		System.gc();

		int n = 0;
		FileManagerFrame.dlg.setVisible(true);

		// final Set<String> listHmseq = hmSeq.keySet();
		// Set<String> listHmseq = hmSeq.keySet().stream().collect(Collectors.toSet());
		String[] listHmseq = hmSeqbis.keySet().toArray(new String[hmSeqbis.keySet().size()]);
		String prefixSeq=("000000").substring(0, String.valueOf(listHmseq.length).length());

		for (String jj : listHmseq) {
			// System.out.println(this+" : jj = "+jj);
			if (!jj.isEmpty() && !jj.contentEquals("0")) {
				try {
					listParamDicom(jj,(prefixSeq + n).substring(String.valueOf(n).length()));
				} catch (Exception e) {

				}
			}
			FileManagerFrame.dlg.setTitle("Loading : sequence " + n * 100 / hmSeq.size() + " %" + " ; " + "files ");
			n++;
		}
		FileManagerFrame.dlg.setVisible(false);

	}

	private void listParamDicom(String noSerial,String noSeq) {

		// System.out.println(this + " : noSeq = " + noSeq + " , " + hmSeq.get(noSeq)[0]
		// + " , " + hmSeq.get(noSeq)[1]);
		hmSeq.put(noSeq, hmSeqbis.get(noSerial));

		String[] listOfParamToShow = { "Protocol", "Sequence Name", "Acquisition Time", "Acquisition Date",
				"Study Date", "Study Time", "Rows", "Columns", "Scan Mode", "Images In Acquisition" };

		String[] listOfParamToReset = { "Echo Time", "Repetition Time", "Inversion Time",
				"Flip Angle","Direction Diffusion","B-values effective"};

		String ff = hmSeq.get(noSeq)[0];

		StringBuffer hdr = new StringBuffer(new HeaderDicom().getHeaderDicom(ff));

		HashMap<String, String> listValuesAcq = new HashMap<>();
		HashMap<String, String> listValuesCal = new HashMap<>();

		listValuesAcq.put("TypeOfView", "simplified");
		listValuesCal.put("TypeOfView", "simplified");
		
		listValuesAcq.put("Serial Number", noSerial);
		listValuesCal.put("Serial Number", noSerial);

		for (String kk : dictionaryMRISystem.keySet()) {
			
			String tmp_param = dictionaryMRISystem.get(kk).get("keyName");
			
			
			String tmp = searchParam(hdr, tmp_param);

			if (dictionaryMRISystem.get(kk).get("format") != null)
				tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(kk).get("format"),
						dictionaryJsonSystem.get(kk).get("format")).getNewFormatDate();

			listValuesAcq.put(kk, tmp);
			listValuesCal.put(kk, tmp);
		}

		for (String kk : dictionaryMRIUser.keySet()) {
			listValuesAcq.put(kk, searchParam(hdr, dictionaryMRIUser.get(kk).get("keyName")));
			listValuesCal.put(kk, searchParam(hdr, dictionaryMRIUser.get(kk).get("keyName")));
		}

		for (String kk : listOfParamToReset) {
			listValuesAcq.put(kk, "(simplified view mode)");
			listValuesCal.put(kk, "(simplified view mode)");
		}

		for (String lm : listOfParamToShow) {
			String tmp_param = dictionaryMRISystem.get(lm).get("keyName");
			String tmp = searchParam(hdr, tmp_param);
			if (dictionaryMRISystem.get(lm).get("format") != null)
				tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(lm).get("format"),
						dictionaryJsonSystem.get(lm).get("format")).getNewFormatDate();
			listValuesAcq.put(lm, tmp);
			listValuesCal.put(lm, tmp);
		}
		
		listValuesAcq.put("Slice Orientation", "");
		listValuesCal.put("Slice Orientation", "");
		
//		System.out.println(this+" : hmSeq "+noSeq +" length = "+hmSeq.get(noSeq).length+" ; listNumberOfFrame = "+listNumberOfFrame.get(noSeq));

		if (hmSeq.get(noSeq).length > 1) {
			listValuesAcq.put("Images In Acquisition", String.valueOf(hmSeq.get(noSeq).length));
			listValuesCal.put("Images In Acquisition", String.valueOf(hmSeq.get(noSeq).length));
			listValuesAcq.put("Indice of Frame", "1");
			listValuesCal.put("Indice of Frame", "1");
		} else {
			listValuesAcq.put("Images In Acquisition", listNumberOfFrame.get(noSerial));
			listValuesCal.put("Images In Acquisition", listNumberOfFrame.get(noSerial));
			if (listNumberOfFrame.get(noSerial).contentEquals("1")) {
				listValuesAcq.put("Indice of Frame", "1");
				listValuesCal.put("Indice of Frame", "1");
			} else {
				listValuesAcq.put("Indice of Frame", "2");
				listValuesCal.put("Indice of Frame", "2");
			}
		}

		if (listValuesAcq.get("Images In Acquisition").isEmpty()) {
			listValuesAcq.put("Images In Acquisition", "1");
			listValuesCal.put("Images In Acquisition", "1");
			listValuesAcq.put("Indice of Frame", "1");
			listValuesCal.put("Indice of Frame", "1");
		}

		// listValuesAcq.put("Slice Orientation",
		// new DicomOrientation(listValuesAcq.get("Slice
		// Orientation")).getOrientationDicom());

		listValuesAcq = new DicomDictionaryAdjustement().valuesAdjustement(listValuesAcq);
		listValuesCal = new DicomDictionaryAdjustement().valuesAdjustement(listValuesCal);

		hmInfo.put(noSeq, listValuesAcq);

	}

	private String searchParam(StringBuffer txt, String paramToFind) {
		String resul = "";
		int indx = txt.indexOf(paramToFind);
		try {
			if (indx != -1) {
				resul = txt.substring(indx);
				resul = resul.substring(resul.indexOf(":") + 1, resul.indexOf("\n"));
			}
		} catch (Exception e) {
			resul = "";
		}
		return resul.trim();
	}
}