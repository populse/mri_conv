package dcm;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Pattern;

import MRIFileManager.Dateformatmodif;
import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListDirfileSequence implements ParamMRI2, DictionDicom {

	private String chemDicom;
	private boolean windowLess = false;


	// constructor for windowless mode
	public ListDirfileSequence(String chemDicom, Boolean wl) {
		windowLess = wl;
		this.chemDicom = chemDicom;
		run();
	}

	public ListDirfileSequence(String chemDicom) {
		this.chemDicom = chemDicom;
		run();
	}

	public void run() {

		// repertory of first dirfile

		String chemD = chemDicom;

		if (!windowLess)
			chemD= hmData.get(chemDicom);
		else
			chemDicom = chemDicom.substring(0, chemDicom.lastIndexOf(PrefParam.separator));

		StringBuffer headerDicom = new StringBuffer(new HeaderDicom().getHeaderDicom(chemD));

		String[] listDirfile = headerDicom.toString().split("0004,1500");

		String stb;

		if (!windowLess)
			FileManagerFrame.dlg.setVisible(true);
		String prefixSeq=("000000").substring(0, String.valueOf(listDirfile.length).length());

		for (int i = 1; i < listDirfile.length; i++) {
			if (!windowLess)
				FileManagerFrame.dlg
						.setTitle("Loading : sequence " + i * 100 / listDirfile.length + " %");
			stb = listDirfile[i];
			stb = stb.substring(stb.indexOf(": ") + 2, stb.indexOf("\n"));
			stb = stb.replace("\\", PrefParam.separator);
			stb = getTruePath(chemDicom, stb); // list of dirfile (2e)

			headerDicom = new StringBuffer(new HeaderDicom().getHeaderDicom(chemDicom + PrefParam.separator + stb));
			// searchListParam(headerDicom, chemDicom, String.valueOf(i - 1));
			searchListParam(headerDicom, chemDicom, chemDicom + PrefParam.separator + stb,(prefixSeq + i).substring(String.valueOf(i).length()) );
		}
		if (!windowLess)
			FileManagerFrame.dlg.setVisible(false);
	}

	private void searchListParam(StringBuffer hdr, String pathDicom, String pathFileDicom, String noSeq) {

		int IndiceofnumberOfFrame = 1;

		String noSeries="";

		HashMap<String, String> listValuesAcq = new HashMap<>();
		HashMap<String, String> listValuesCal = new HashMap<>();

		String pathImDcm = searchParam(hdr, "0004,1500");
		pathImDcm = pathImDcm.replace("\\", PrefParam.separator);
		pathImDcm = getTruePath(pathDicom, pathImDcm);
		StringBuffer hdr2 = null;

		if (!pathImDcm.isEmpty()) {
			pathImDcm = getTruePath(pathDicom, pathImDcm);
			HeaderDicom hdrdcm = new HeaderDicom();
			hdr2 = new StringBuffer(hdrdcm.getHeaderDicom(pathDicom + PrefParam.separator + pathImDcm));
			if (hdrdcm.isJpegLossLess()) {
				listValuesAcq.put("Note","JpegLossLess");
				listValuesCal.put("Note","JpegLossLess");
			}

		} else {
			hdr2 = hdr;
			IndiceofnumberOfFrame = 2;
//			System.out.println(this+" : no path dicom in dirfile");
		}

		for (String kk : dictionaryMRISystem.keySet()) {
			if (dictionaryMRISystem.get(kk).get("file") != null) {
				if (dictionaryMRISystem.get(kk).get("file").contains("dirfile")) {
					String tmp = searchParam(hdr, dictionaryMRISystem.get(kk).get("keyName"));

					if (dictionaryMRISystem.get(kk).get("format") != null)
						tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(kk).get("format"),
								dictionaryJsonSystem.get(kk).get("format")).getNewFormatDate();
					listValuesAcq.put(kk, tmp);
					listValuesCal.put(kk, tmp);
					// listValuesAcq.put(kk, searchParam(hdr,
					// dictionaryMRISystem.get(kk).get("keyName")));
					// listValuesCal.put(kk, searchParam(hdr,
					// dictionaryMRISystem.get(kk).get("keyName")));
				}
			} else {
				String tmp = searchParam(hdr2, dictionaryMRISystem.get(kk).get("keyName"));
				if (dictionaryMRISystem.get(kk).get("format") != null)
					tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(kk).get("format"),
							dictionaryJsonSystem.get(kk).get("format")).getNewFormatDate();
				listValuesAcq.put(kk, tmp);
				listValuesCal.put(kk, tmp);
				// listValuesAcq.put(kk, searchParam(hdr2,
				// dictionaryMRISystem.get(kk).get("keyName")));
				// listValuesCal.put(kk, searchParam(hdr2,
				// dictionaryMRISystem.get(kk).get("keyName")));
			}
		}
		
		for (String kk : dictionaryMRIUser.keySet()) {
			listValuesAcq.put(kk, searchParam(hdr2, dictionaryMRIUser.get(kk).get("keyName")));
			listValuesCal.put(kk, searchParam(hdr2, dictionaryMRIUser.get(kk).get("keyName")));

		}


		ArrayList<String[]> listAcq = new ArrayList<>();
		ArrayList<String[]> listCal = new ArrayList<>();
		int offAcq = 0, offCalc = 0;
		ArrayList<String> tmpAcq = new ArrayList<>();
		ArrayList<String> tmpCal = new ArrayList<>();

		String[] listSlice = null;
		
//		FileManagerFrame.dlg.setVisible(true);
		String title = "";
		if (! windowLess)
			title = FileManagerFrame.dlg.getTitle()+ " ; " + "files ";
		String bvalue_field = "0018,9087";
		if (listValuesAcq.get("Manufacturer").contains("Philips") || listValuesCal.get("Manufacturer").contains("Philips"))
			bvalue_field = "2001,1003";

		if (IndiceofnumberOfFrame == 1) { // 1 file by slice

			noSeries = searchParam(hdr2, "Series Number");
			listValuesAcq.put("Serial Number", noSeries);
			listValuesCal.put("Serial Number", noSeries);


			String[] listImDcm = hdr.toString().split("0004,1430");

			// String[] tmp = new String[listImDcm.length - 1];

			String fileName;

			for (int i = 1; i < listImDcm.length; i++) {
				StringBuffer hdrtmp = new StringBuffer(listImDcm[i]); 
				listSlice = new String[21];
				if (!windowLess)
					FileManagerFrame.dlg.setTitle(title + i * 100 / listImDcm.length + " %");
				fileName = searchParam(hdrtmp, "0004,1500");
				fileName=fileName.replace("\\", PrefParam.separator);
				listSlice[0] = pathDicom + PrefParam.separator + getTruePath(pathDicom, fileName);
				listSlice[1] = searchParam(hdrtmp, "Image Number").trim();
				listSlice[2] = searchParam(hdrtmp, "Echo Numbers(s)");
				listSlice[3] = searchParam(hdrtmp, "Slice Number");
				listSlice[4] = searchParam(hdrtmp, "Repetition Time");
				listSlice[5] = searchParam(hdrtmp, "Echo Time");
				listSlice[6] = searchParam(hdrtmp, "Inversion Time");
				listSlice[7] = searchParam(hdrtmp, "Slice Location");
				String tp = searchParam(hdrtmp, "Image Type");
				tp = new ChangeSyntax().NewSyntaxType(tp);
				int indTp = Arrays.asList(listType).indexOf(tp);
				listSlice[8] = tp;
				listSlice[9] = searchParam(hdrtmp, "Temporal Position");
				listSlice[10] = searchParam(hdrtmp, "Rescale Intercept");
				if (!listSlice[10].matches("[-+]?[0-9]*\\.?[0-9]+"))
					listSlice[10]="0";
				listSlice[11] = searchParam(hdrtmp, "Rescale Slope");
				if (!listSlice[11].matches("[-+]?[0-9]*\\.?[0-9]+"))
					listSlice[11]="1";
				listSlice[12] = searchParam(hdrtmp, "Acquisition Time");
				listSlice[13] = searchParam(hdrtmp, "Image Position (Patient)");
				listSlice[14] = searchParam(hdrtmp, "Image Orientation (Patient)");
				listSlice[15] = searchParam(hdrtmp, bvalue_field); // Diffusion-b-value
				String ts = searchParam(hdrtmp, "0018,0020");
				ts = new ChangeSyntax().NewSyntaxScanSeq(ts);
				// int indSs = Arrays.asList(listScanSeq).indexOf(ts);
				listSlice[16] = ts; // scanning sequence
				listSlice[17] = searchParam(hdrtmp, "2005,1429");//Label Type (ASL)
				listSlice[18] = searchParam(hdrtmp, "2005,100E");//Scale Slope Philips
				if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
					listSlice[18]="1";
//				System.out.println(this);
//				System.out.println("diffusion alors = "+listSlice[15]);
				if (!listSlice[15].isEmpty() )
					listSlice[19] = searchParam(hdrtmp, "2005,10B0")+" "+searchParam(hdrtmp, "2005,10B1")+" "+searchParam(hdrtmp, "2005,10B2");
				else
						listSlice[19] ="";
				listSlice[20] = searchParam(hdrtmp, "2005,1413"); // gradient orientation number

				if (indTp < 6) {
					// if (indSs < 4) {
					offCalc = listCal.size();
					listAcq.add(listSlice);
					// tmpAcq.add(listSlice[0]);
					// } else {
					// offAcq = listAcq.size();
					// listCal.add(listSlice);
					// tmpCal.add(listSlice[0]);
					// }
				} else {
					offAcq = listAcq.size();
					listCal.add(listSlice);
					// tmpCal.add(listSlice[0]);
				}
			}

			listImDcm = null;
			System.gc();

			if ((!listAcq.isEmpty())) {
				Collections.sort(listAcq, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[1].toString()))
								.compareTo(Integer.parseInt(otherStrings[1].toString()));
					}
				});
				for (String[] kk : listAcq) {
					tmpAcq.add(kk[0]);
				}
				listValuesAcq.put("Images In Acquisition",String.valueOf(listAcq.size()));
				hmSeq.put(noSeq, tmpAcq.toArray(new String[0]));
				hmInfo.put(noSeq, listValuesAcq);
				new ListDicomParam(noSeq, listAcq, offCalc, "");
			}
			if (!listCal.isEmpty()) {
				Collections.sort(listCal, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[1].toString()))
								.compareTo(Integer.parseInt(otherStrings[1].toString()));
					}
				});

				for (String[] ll : listCal) {
					tmpCal.add(ll[0]);
				}
				listValuesCal.put("Images In Acquisition",String.valueOf(listValuesCal.size()));
				hmSeq.put(noSeq + "(calc)", tmpCal.toArray(new String[0]));
				hmInfo.put(noSeq + "(calc)", listValuesCal);
				new ListDicomParam(noSeq + "(calc)", listCal, offAcq, "");
			}
		} else { // multislice by file

			noSeries = searchParam(hdr2, "Series Number");
			listValuesAcq.put("Serial Number", noSeries);
			listValuesCal.put("Serial Number", noSeries);						
			String[] list = hdr2.toString().split("0008,0008");

			for (int i = 1; i < list.length; i++) {
				StringBuffer hdrtmp = new StringBuffer(list[i]); 
				listSlice = new String[21]; 
				if (!windowLess)
					FileManagerFrame.dlg.setTitle(title + i * 100 / list.length + " %");
				
				listSlice[1] = searchParam(hdrtmp, "Image Number");

				if (!listSlice[1].isEmpty() && !listSlice[1].contentEquals("0")) {
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
					int indTp = Arrays.asList(listType).indexOf(tp);
					listSlice[8] = tp;
					listSlice[9] = searchParam(hdrtmp, "Temporal Position");
					listSlice[10] = searchParam(hdrtmp, "Rescale Intercept");
					if (!listSlice[10].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[10]="0";
					listSlice[11] = searchParam(hdrtmp, "Rescale Slope");
					if (!listSlice[11].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[11]="1";
					listSlice[12] = searchParam(hdrtmp, "Acquisition Time");
					listSlice[13] = searchParam(hdrtmp, "Image Position (Patient)");
					listSlice[14] = searchParam(hdrtmp, "Image Orientation (Patient)");
					listSlice[15] = searchParam(hdrtmp, bvalue_field); // Diffusion-b-value
					String ts = searchParam(hdrtmp, "0018,0020");
					ts = new ChangeSyntax().NewSyntaxScanSeq(ts);
					int indSs = Arrays.asList(listScanSeq).indexOf(ts);
					listSlice[16] = ts; // scanning sequence
					listSlice[17] = searchParam(hdrtmp, "2005,1429");//Label Type (ASL)
					listSlice[18] = searchParam(hdrtmp, "2005,100E");//Scale Slope Philips
					if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[18]="1";
					if (!listSlice[15].isEmpty() )
						listSlice[19] = searchParam(hdrtmp, "2005,10B0")+" "+searchParam(hdrtmp, "2005,10B1")+" "+searchParam(hdrtmp, "2005,10B2");
					else
						listSlice[19] ="";
					listSlice[20] = searchParam(hdrtmp, "2005,1413"); // gradient orientation number


					if (indTp < 6) {
						if (indSs < 4) {
							offCalc = listCal.size();
							listAcq.add(listSlice);
						} else {
							offAcq = listAcq.size();
							listCal.add(listSlice);
						}
					} else {
						offAcq = listAcq.size();
						listCal.add(listSlice);
					}
				}
			}

			list = null;
			System.gc();

			String[] tmp = new String[1];
			tmp[0] = pathFileDicom;

			if ((!listAcq.isEmpty())) {
				Collections.sort(listAcq, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[1].toString()))
								.compareTo(Integer.parseInt(otherStrings[1].toString()));
					}
				});
				for (String[] kk : listAcq) {
					tmpAcq.add(kk[0]);
				}
				listValuesAcq.put("Images In Acquisition",String.valueOf(listAcq.size()));
				hmSeq.put(noSeq, tmp);
				hmInfo.put(noSeq, listValuesAcq);
				new ListDicomParam(noSeq, listAcq, offCalc, "");
			}
			if (!listCal.isEmpty()) {
				Collections.sort(listCal, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[1].toString()))
								.compareTo(Integer.parseInt(otherStrings[1].toString()));
					}
				});

				for (String[] ll : listCal) {
					tmpCal.add(ll[0]);
				}
				listValuesCal.put("Images In Acquisition",String.valueOf(listValuesCal.size()));
				hmSeq.put(noSeq + "(calc)", tmp);
				hmInfo.put(noSeq + "(calc)", listValuesCal);
				new ListDicomParam(noSeq + "(calc)", listCal, offAcq, "");
			}

		} // end else		
//		FileManagerFrame.dlg.setVisible(false);

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

	private String getTruePath(String chemDcm, String pathDirfile) {

		String pattern = Pattern.quote(System.getProperty("file.separator"));

		for (int i = 0; i < pathDirfile.split(pattern).length; i++) {
			if (!new File(chemDcm + PrefParam.separator + pathDirfile).exists())
				pathDirfile = pathDirfile.substring(pathDirfile.indexOf(PrefParam.separator) + 1);
			else
				break;
		}
		return pathDirfile;
	}
}