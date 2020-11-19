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

public class ListDicomDirSequence2 implements ParamMRI2, DictionDicom, Runnable {

	private String chemDicom;
	private HashMap<String, String[]> hmSeqbis = new HashMap<>();
	private HashMap<String, String> listNumberOfFrame = new HashMap<>();
	private boolean windowLess = false;

	// constructor for windowless mode
	public ListDicomDirSequence2(String chemDicom, Boolean wl) {
		windowLess = wl;
		this.chemDicom = chemDicom;
		run();
	}
	
	public ListDicomDirSequence2() {
		
	}

	public ListDicomDirSequence2(String chemDicom) {
		this.chemDicom = chemDicom;
		run();
	}

	@Override
	public void run() {
		
		String chemDicomdir = chemDicom;

		if (!windowLess)
			chemDicomdir= hmData.get(chemDicom);
		else
			chemDicom = chemDicom.substring(0, chemDicom.lastIndexOf(PrefParam.separator));
		
		System.out.println(this+" : chemDicomdir = "+chemDicomdir);

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

				if (value != null)
					noSerial += "bis";

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
		if (!windowLess)
			FileManagerFrame.dlg.setVisible(true);

		// final Set<String> listHmseq = hmSeq.keySet();
		// Set<String> listHmseq = hmSeq.keySet().stream().collect(Collectors.toSet());
		String[] listHmseq = hmSeqbis.keySet().toArray(new String[hmSeqbis.keySet().size()]);
		String prefixSeq = ("000000").substring(0, String.valueOf(listHmseq.length).length());

		for (String jj : listHmseq) {
			// System.out.println(this+" : jj = "+jj);
			if (!jj.isEmpty() && !jj.contentEquals("0")) {
				try {
					listParamDicom(jj, (prefixSeq + n).substring(String.valueOf(n).length()), true);
				} catch (Exception e) {

				}
			}
			if (!windowLess)
				FileManagerFrame.dlg.setTitle("Loading : sequence " + n * 100 / hmSeq.size() + " %" + " ; " + "files ");
			n++;
		}
		if (!windowLess)
			FileManagerFrame.dlg.setVisible(false);
	}

	public void listParamDicom(String noSerial, String noSeq, boolean newhmSeq) {

		if (newhmSeq)
			hmSeq.put(noSeq, hmSeqbis.get(noSerial));

		String ff = hmSeq.get(noSeq)[0];

//		System.out.println(this+" : noSeq = "+noSeq+" , "+hmSeq.get(noSeq).length);

		HeaderDicom hdrdcm = new HeaderDicom();

		StringBuffer hdr = new StringBuffer(hdrdcm.getHeaderDicom(ff));
		// if (noSeq.contentEquals("201")) {
		// String tmp = hdr.substring(hdr.indexOf("0018,0080"));
		// tmp=tmp.substring(0, tmp.indexOf("\n"));
		// }

		HashMap<String, String> listValuesAcq = new HashMap<>();

		listValuesAcq.put("TypeOfView", "elaborate");

		listValuesAcq.put("Serial Number", noSerial);

		if (hdrdcm.isJpegLossLess()) {
			listValuesAcq.put("Note", "JpegLossLess");
		}

		for (String kk : dictionaryMRISystem.keySet()) {
			String tmp_param = dictionaryMRISystem.get(kk).get("keyName");
			String tmp = searchParam(hdr, tmp_param);

			if (dictionaryMRISystem.get(kk).get("format") != null && !tmp.isEmpty())
				tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(kk).get("format"),
						dictionaryJsonSystem.get(kk).get("format")).getNewFormatDate();
			listValuesAcq.put(kk, tmp);
		}

		for (String kk : dictionaryMRIUser.keySet()) {
			listValuesAcq.put(kk, searchParam(hdr, dictionaryMRIUser.get(kk).get("keyName")));
		}

		if (newhmSeq) {

			if (hmSeq.get(noSeq).length > 1) {
				listValuesAcq.put("Images In Acquisition", String.valueOf(hmSeq.get(noSeq).length));
				listValuesAcq.put("Indice of Frame", "1");
			} else {
				listValuesAcq.put("Images In Acquisition", listNumberOfFrame.get(noSerial));
				if (listNumberOfFrame.get(noSerial).contentEquals("1")) {
					listValuesAcq.put("Indice of Frame", "1");
				} else {
					listValuesAcq.put("Indice of Frame", "2");
				}
			}

			if (listValuesAcq.get("Images In Acquisition").isEmpty()) {
				listValuesAcq.put("Images In Acquisition", "1");
				listValuesAcq.put("Indice of Frame", "1");
			}
		} else {
			listValuesAcq.put("Images In Acquisition", hmInfo.get(noSeq).get("Images In Acquisition"));
			listValuesAcq.put("Indice of Frame", hmInfo.get(noSeq).get("Indice of Frame"));
		}
		hmInfo.put(noSeq, listValuesAcq);

		ArrayList<String[]> listAcq = new ArrayList<>();

		String[] listSlice = null;
//		int offAcq = 0;
		int offCalc = 0;
		String title = "";

		if (!windowLess)
			title = FileManagerFrame.dlg.getTitle();

		if (listValuesAcq.get("Indice of Frame").contentEquals("2")) { // read 1 file multi-frame

			hdr = new StringBuffer(new HeaderDicom().getHeaderDicom(hmSeq.get(noSeq)[0]));
			String[] list = hdr.toString().split("0008,0008");
//			String[] list = hdr.toString().split("0004,1430");

			for (int i = 0; i < list.length; i++) {
				StringBuffer hdrtmp = new StringBuffer(list[i]);
				if (!windowLess)
					FileManagerFrame.dlg.setTitle(title + i * 100 / list.length + " %");
				listSlice = new String[21];
				listSlice[1] = searchParam(hdrtmp, "Image Number");

				if (!listSlice[1].isEmpty() && !listSlice[1].trim().contentEquals("0")) {
					listSlice[0] = "";
					listSlice[1] = listSlice[1].trim();
					listSlice[2] = searchParam(hdrtmp, "Echo Numbers(s)");
					listSlice[3] = searchParam(hdrtmp, "Slice Number");
					listSlice[4] = searchParam(hdrtmp, "Repetition Time");
					listSlice[5] = searchParam(hdrtmp, "Echo Time");
					listSlice[6] = searchParam(hdrtmp, "Inversion Time");
					listSlice[7] = searchParam(hdrtmp, "Slice Location");
					String tp = searchParam(hdrtmp, "Image Type");
					tp = new ChangeSyntax().NewSyntaxType(tp);
					listSlice[8] = tp;
					listSlice[9] = searchParam(hdrtmp, "Temporal Position");
					listSlice[10] = searchParam(hdrtmp, "Rescale Intercept");
					if (listSlice[10].isEmpty())
						listSlice[10] = searchParam(hdrtmp, "Real World Value Intercept");
					if (!listSlice[10].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[10] = "0";
					listSlice[11] = searchParam(hdrtmp, "Rescale Slope");
					if (listSlice[11].isEmpty())
						listSlice[11] = searchParam(hdrtmp, "Real World Value Slope");
					if (!listSlice[11].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[11] = "1";
					listSlice[12] = searchParam(hdrtmp, "Acquisition Time");
					listSlice[13] = searchParam(hdrtmp, "Image Position (Patient)");
					listSlice[14] = searchParam(hdrtmp, "Image Orientation (Patient)");
					listSlice[15] = searchParam(hdrtmp, "0018,9087"); // diffusion
					String ts = searchParam(hdrtmp, "0018,0020");
					ts = new ChangeSyntax().NewSyntaxScanSeq(ts);
					listSlice[16] = ts; // scanning sequence
					listSlice[17] = searchParam(hdrtmp, "2005,1429");// Label Type (ASL)
					listSlice[18] = searchParam(hdrtmp, "2005,100E");// Scale Slope Philips
					if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[18] = "1";
					listSlice[19] = searchParam(hdrtmp, "2005,10B0") + " " + searchParam(hdrtmp, "2005,10B1") + " "
							+ searchParam(hdrtmp, "2005,10B2") ;
					listSlice[20] = searchParam(hdrtmp, "2005,1413"); // gradient orientation number

					listAcq.add(listSlice);
				}
			}

		} else { // read file by file

			for (int i = 0; i < hmSeq.get(noSeq).length; i++) {
				if (!windowLess)
					FileManagerFrame.dlg.setTitle(title + i * 100 / hmSeq.get(noSeq).length + " %");
				hdr = new StringBuffer(new HeaderDicom().getHeaderDicom(hmSeq.get(noSeq)[i]));
				listSlice = new String[21];
				try {
					listSlice[1] = searchParam(new StringBuffer(hdr.toString().split("Image Number: 0 ")[1]),
							"Image Number");
				} catch (Exception e) {
					listSlice[1] = searchParam(new StringBuffer(hdr.toString()), "Image Number");
				}
//				listSlice[1] = searchParam(new StringBuffer(hdr.toString()),"Image Number");
//				System.out.println(this+" : listSlice[1] = "+listSlice[1]);
//				if (!listSlice[1].isEmpty() && !listSlice[1].contentEquals("0")) {
				if (listSlice[1].isEmpty())
					listSlice[1] = String.valueOf(i + 1);
				if (!listSlice[1].contentEquals("0")) {
					listSlice[0] = "";
					listSlice[1] = listSlice[1].trim();
					listSlice[2] = searchParam(hdr, "Echo Numbers(s)");
					listSlice[3] = searchParam(hdr, "Slice Number");
					listSlice[4] = searchParam(hdr, "Repetition Time");
					listSlice[5] = searchParam(hdr, "Echo Time");
					listSlice[6] = searchParam(hdr, "Inversion Time");
					listSlice[7] = searchParam(hdr, "Slice Location");
					listSlice[8] = searchParam(hdr, "Image Type");
					listSlice[8] = new ChangeSyntax().NewSyntaxType(listSlice[8]);
					listSlice[9] = searchParam(hdr, "Temporal Position");
					listSlice[10] = searchParam(hdr, "Rescale Intercept");
					if (listSlice[10].isEmpty())
						listSlice[10] = searchParam(hdr, "Real World Value Intercept");
					if (!listSlice[10].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[10] = "0";
					listSlice[11] = searchParam(hdr, "Rescale Slope");
					if (listSlice[11].isEmpty())
						listSlice[11] = searchParam(hdr, "Real World Value Slope");
					if (!listSlice[11].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[11] = "1";
					listSlice[12] = searchParam(hdr, "Acquisition Time");
					listSlice[13] = searchParam(hdr, "Image Position (Patient)");
					listSlice[14] = searchParam(hdr, "Image Orientation (Patient)");
					listSlice[15] = searchParam(hdr, "0018,9087"); // diffusion
					listSlice[16] = searchParam(hdr, "0018,0020"); // scanning
																	// sequence
					listSlice[17] = searchParam(hdr, "2005,1429"); // Label Type (ASL)
					listSlice[18] = searchParam(hdr, "2005,100E");// Scale Slope Philips
					if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[18] = "1";
					listSlice[19] = searchParam(hdr, "2005,10B0") + " " + searchParam(hdr, "2005,10B1") + " "
							+ searchParam(hdr, "2005,10B2");
					listSlice[20] = searchParam(hdr, "2005,1413"); // gradient orientation number

					listAcq.add(listSlice);
				}
			}
		}

		if ((!listAcq.isEmpty())) {
			Collections.sort(listAcq, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] strings, Object[] otherStrings) {
					return ((Integer) Integer.parseInt(strings[1].toString()))
							.compareTo(Integer.parseInt(otherStrings[1].toString()));
				}
			});
			new ListDicomParam(noSeq, listAcq, offCalc, "");
		}
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

		// if (paramToFind.contentEquals("Image Number") &&
		// resul.trim().contentEquals("0")) {
		// txt = new StringBuffer(txt.substring(txt.indexOf("Image Number: 0")+15));
		// resul = searchParam(txt, "Image Number");
		// }

//		System.out.println(this+" paramToFind = "+paramToFind+", result = "+resul);

		return resul.trim();
	}
}