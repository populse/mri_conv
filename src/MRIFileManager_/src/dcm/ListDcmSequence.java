package dcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import MRIFileManager.Dateformatmodif;
import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import ij.plugin.DICOM;


public class ListDcmSequence implements ParamMRI2, DictionDicom {

	private String chemDicom;
	private Boolean windowlessMode;

	public ListDcmSequence(String chemDicom, Boolean windowlessMode) {
		this.chemDicom = chemDicom;
		this.windowlessMode = windowlessMode;
		run();
	}

	public void run() {

		if (!windowlessMode) {
			FileManagerFrame.dlg.setVisible(true);
			FileManagerFrame.dlg.setTitle("Search list of Dicom ...");
		}

		StringBuffer[] listDicom = new SearchDicom(chemDicom).listDicom();

		if (!windowlessMode)
			FileManagerFrame.dlg.setTitle("Loading Dicom headers ...");

		HashMap<String, ArrayList<String>> listFilesBySerieNumber = new HashMap<>();
		ArrayList<String> tmpList = new ArrayList<>();
		String noSeries;
		HeaderDicom hdrdcm = new HeaderDicom();
		StringBuffer hdr;

		for (StringBuffer lf : listDicom) {
			hdr = new StringBuffer(hdrdcm.getHeaderDicom(lf.toString()));
			noSeries = searchParam(hdr, "Series Number");
			tmpList = new ArrayList<>();
			tmpList = listFilesBySerieNumber.get(noSeries);
			if (tmpList != null) {
				tmpList.add(lf.toString());
			} else {
				tmpList = new ArrayList<>();
				tmpList.add(lf.toString());
			}
			listFilesBySerieNumber.put(noSeries, tmpList);
		}

		String prefixSeq = ("000000").substring(0, String.valueOf(listFilesBySerieNumber.size()).length());
		String ind;
		int c = 0;
		for (String hh : listFilesBySerieNumber.keySet()) {
			ind = (prefixSeq + c).substring(String.valueOf(c).length());
			startList(listFilesBySerieNumber.get(hh).toArray(), String.valueOf(ind));
			c++;
		}
	}

	public void startList(Object[] files, String nos) {

		String numberFrames;
		HashMap<String, String> listValuesAcq = new HashMap<>();
		HashMap<String, String> listValuesCal = new HashMap<>();

		StringBuffer hdr;

		HeaderDicom hdrdcm = new HeaderDicom();

		hdr = new StringBuffer(hdrdcm.getHeaderDicom(files[0].toString()));

		if (hdrdcm.isJpegLossLess()) {
			listValuesAcq.put("Note", "JpegLossLess");
			listValuesCal.put("Note", "JpegLossLess");
		}

		for (String kk : dictionaryMRISystem.keySet()) {
			String tmp = searchParam(hdr, dictionaryMRISystem.get(kk).get("keyName"));
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

		String noSeries = searchParam(hdr, "Series Number");

		String noSeq = nos;

		numberFrames = searchParam(hdr, "Number of Frames");

		if (numberFrames.isEmpty())
			numberFrames = "1";

		ArrayList<String[]> listAcq = new ArrayList<>();
		ArrayList<String[]> listCal = new ArrayList<>();
		ArrayList<String> tmpAcq = new ArrayList<>();
		ArrayList<String> tmpCal = new ArrayList<>();
		ArrayList<String> offsetImageAcq = new ArrayList<>();
		ArrayList<String> offsetImageCal = new ArrayList<>();
		int offAcq = 0, offCalc = 0;

		if (!windowlessMode)
			FileManagerFrame.dlg.setVisible(true);
		String title = "Loading : files ";
//		String hdrRecorded = "";
		String bvalue_field = "0018,9087";
		if (listValuesAcq.get("Manufacturer").contains("Philips")
				|| listValuesCal.get("Manufacturer").contains("Philips"))
			bvalue_field = "2001,1003";

		if (Integer.parseInt(numberFrames) == 1) { // one image by file

			String[] listSlice;
			StringBuffer hdrDcm;

			for (int i = 0; i < files.length; i++) {
				if (!windowlessMode)
					FileManagerFrame.dlg.setTitle(title + (i + 1) * 100 / files.length + " %");
				hdrDcm = new StringBuffer(new HeaderDicom().getHeaderDicom(files[i].toString()));
//				hdrRecorded+=hdrDcm+"\n";
				listSlice = new String[21];
				listSlice[1] = searchParam(hdrDcm, "Image Number").trim();

				if (listSlice[1].contentEquals("0")) {
					listSlice[1] = searchParam(
							new StringBuffer(hdrDcm.substring(hdrDcm.indexOf("Image Number: 0") + 15)), "Image Number");
				}

				if (listSlice[1].isEmpty()) {
					try {
						listSlice[1] = searchParam(new StringBuffer(hdrDcm.substring(hdrDcm.indexOf("InstanceNumber"))),
								"InstanceNumber");
					} catch (Exception e) {

					}
				}

				if (listSlice[1].isEmpty())
					listSlice[1] = String.valueOf(i);

				if (listSlice[1] != null)
					if (!listSlice[1].isEmpty()) {
						String tp, ts;
						listSlice[0] = files[i].toString();
						listSlice[1] = listSlice[1].trim();
						listSlice[2] = searchParam(hdrDcm, "Echo Numbers(s)");
						listSlice[3] = searchParam(hdrDcm, "Slice Number");
						listSlice[4] = searchParam(hdrDcm, "Repetition Time");
						listSlice[5] = searchParam(hdrDcm, "Echo Time");
						listSlice[6] = searchParam(hdrDcm, "Inversion Time");
						listSlice[7] = searchParam(hdrDcm, "Slice Location");
						tp = searchParam(hdrDcm, "Image Type");
						tp = new ChangeSyntax().NewSyntaxType(tp);
						int indTp = Arrays.asList(listType).indexOf(tp);
						listSlice[8] = tp;
						listSlice[9] = searchParam(hdrDcm, "Temporal Position");
						listSlice[10] = searchParam(hdrDcm, "Rescale Intercept");
						if (!listSlice[10].matches("[-+]?[0-9]*\\.?[0-9]+"))
							listSlice[10] = "0";
						listSlice[11] = searchParam(hdrDcm, "Rescale Slope");
						if (!listSlice[11].matches("[-+]?[0-9]*\\.?[0-9]+"))
							listSlice[11] = "1";
						listSlice[12] = searchParam(hdrDcm, "Acquisition Time");
						listSlice[13] = searchParam(hdrDcm, "Image Position (Patient)");
						listSlice[14] = searchParam(hdrDcm, "Image Orientation (Patient)");
						listSlice[15] = searchParam(hdrDcm, bvalue_field); // Diffusion-b-value
						ts = searchParam(hdrDcm, "0018,0020"); // type of data taken (SE, IR, GR, EP, RM)
						ts = new ChangeSyntax().NewSyntaxScanSeq(ts);
						int indSs = Arrays.asList(listScanSeq).indexOf(ts);
						listSlice[16] = ts; // scanning sequence
						listSlice[17] = searchParam(hdrDcm, "2005,1429");// Label Type (ASL)
						listSlice[18] = searchParam(hdrDcm, "2005,100E");// Scale Slope Philips
						if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
							listSlice[18] = "1";
						listSlice[19] = searchParam(hdrDcm, "2005,10B0") + " " + searchParam(hdrDcm, "2005,10B1") + " "
								+ searchParam(hdrDcm, "2005,10B2");
						listSlice[20] = searchParam(hdrDcm, "2005,1413");

						if (indTp < Arrays.asList(listType).indexOf("OTHER")) {
							if (indSs < 4) {
								offCalc = listCal.size();
								listAcq.add(listSlice);
								offsetImageAcq.add(listSlice[1]);
								// tmpAcq.add(listSlice[0]);
							} else {
								offAcq = listAcq.size();
								listCal.add(listSlice);
								offsetImageCal.add(listSlice[1]);
								// tmpCal.add(listSlice[0]);
							}
						} else {
							offAcq = listAcq.size();
							listCal.add(listSlice);
							offsetImageCal.add(listSlice[1]);
							// tmpCal.add(listSlice[0]);
						}
					}
			}

			listValuesAcq.put("Serial Number", noSeries);
			listValuesCal.put("Serial Number", noSeries);

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
				listValuesAcq.put("Images In Acquisition", String.valueOf(listAcq.size()));
				hmSeq.put(noSeq, tmpAcq.toArray(new String[0]));
				hmInfo.put(noSeq, listValuesAcq);
				new ListDicomParam(noSeq, listAcq, offCalc, "", offsetImageAcq);
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
				listValuesCal.put("Images In Acquisition", String.valueOf(listCal.size()));
				hmSeq.put(noSeq + "(calc)", tmpCal.toArray(new String[0]));
				hmInfo.put(noSeq + "(calc)", listValuesCal);
				new ListDicomParam(noSeq + "(calc)", listCal, offAcq, "", offsetImageCal);
			}
//			hmTagDicom.put(noSeq, hdrRecorded);
		}

		else { // multiframe/

			// A completer !!

			// DicomInputStream dcm;
			// Attributes attrs, attrs2;
			// Tag tg = new Tag();
			//
			// try {
			// dcm = new DicomInputStream(new File(chemDicom + PrefParam.separator +
			// files[0]));
			// attrs = dcm.readFileMetaInformation();
			// attrs2 = dcm.readDataset(-1, -1);
			//
			// System.out.println(attrs2);
			// System.out.println(chemDicom + PrefParam.separator + files[0]+" :
			// "+attrs2.getStrings(Tag.ImagePositionPatient));
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// }


			for (int k = 0; k < files.length; k++) {
				if (!windowlessMode)
					FileManagerFrame.dlg.setTitle(title + (k + 1) * 100 / files.length + " %");

				String currentDicomPath = files[k].toString();

				listValuesAcq = new HashMap<>();
				listValuesCal = new HashMap<>();

				listAcq = new ArrayList<>();
				listCal = new ArrayList<>();
				offsetImageAcq = new ArrayList();
				offsetImageCal = new ArrayList();
				tmpAcq = new ArrayList<>();
				tmpCal = new ArrayList<>();

				DICOM dcm;
				dcm = new DICOM();
				StringBuffer hdrDcm = new StringBuffer(dcm.getInfo(currentDicomPath));
//				hmTagDicom.put(noSeq, hdrDcm.toString());

				dcm.close();
				numberFrames = searchParam(hdrDcm, "Number of Frames");
				noSeries = searchParam(hdrDcm, "Series Number");

				listValuesAcq.put("Serial Number", noSeries);
				listValuesCal.put("Serial Number", noSeries);

				for (String kk : dictionaryMRISystem.keySet()) {
					String tmp = searchParam(hdrDcm, dictionaryMRISystem.get(kk).get("keyName"));
					if (dictionaryMRISystem.get(kk).get("format") != null)
						tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(kk).get("format"),
								dictionaryJsonSystem.get(kk).get("format")).getNewFormatDate();
					listValuesAcq.put(kk, tmp);
					listValuesCal.put(kk, tmp);
				}

				for (String kk : dictionaryMRIUser.keySet()) {
					listValuesAcq.put(kk, searchParam(hdrDcm, dictionaryMRIUser.get(kk).get("keyName")));
					listValuesCal.put(kk, searchParam(hdrDcm, dictionaryMRIUser.get(kk).get("keyName")));
				}

				String[] list = hdrDcm.toString().split("0008,9007");

				String[] listSlice = null;

				for (int i = 0; i < list.length; i++) {
					StringBuffer hdrtmp = new StringBuffer(list[i]);
					listSlice = new String[21];
					listSlice[1] = searchParam(hdrtmp, "Image Number");
					if (listSlice[1].isEmpty())
						listSlice[1] = String.valueOf(i + 1);
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
							listSlice[10] = "0";
						listSlice[11] = searchParam(hdrtmp, "Rescale Slope");
						if (!listSlice[11].matches("[-+]?[0-9]*\\.?[0-9]+"))
							listSlice[11] = "1";
						listSlice[12] = searchParam(hdrtmp, "Acquisition Time");
						listSlice[13] = searchParam(hdrtmp, "Image Position (Patient)");
						listSlice[14] = searchParam(hdrtmp, "Image Orientation (Patient)");
						listSlice[15] = searchParam(hdrtmp, bvalue_field); // Diffusion-b-value
						String ts = searchParam(hdrtmp, "0018,0020");
						ts = new ChangeSyntax().NewSyntaxScanSeq(ts);
						int indSs = Arrays.asList(listScanSeq).indexOf(ts);
						listSlice[16] = ts; // scanning sequence
						listSlice[17] = searchParam(hdrtmp, "2005,1429");// Label Type (ASL)
						listSlice[18] = searchParam(hdrtmp, "2005,100E");// Scale Slope Philips
						if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
							listSlice[18] = "1";
						listSlice[19] = searchParam(hdrtmp, "2005,10B0") + " " + searchParam(hdrtmp, "2005,10B1") + " "
								+ searchParam(hdrtmp, "2005,10B2");
						listSlice[20] = searchParam(hdrtmp, "2005,1413");

						if (indTp < Arrays.asList(listType).indexOf("OTHER")) {
							if (indSs < 4) {
								offCalc = listCal.size();
								listAcq.add(listSlice);
								offsetImageAcq.add(listSlice[1]);
							} else {
								offAcq = listAcq.size();
								listCal.add(listSlice);
								offsetImageCal.add(listSlice[1]);
							}
						} else {
							offAcq = listAcq.size();
							listCal.add(listSlice);
							offsetImageCal.add(listSlice[1]);
						}
					}
				}

//				for (String[] kk : listAcq) {
//					tmpAcq.add(kk[0]);
//				}
//				for (String[] ll : listCal) {
//					tmpCal.add(ll[0]);
//				}

				String[] tmp = new String[1];
				tmp[0] = currentDicomPath;

				if ((!listAcq.isEmpty())) {
					Collections.sort(listAcq, new Comparator<Object[]>() {
						@Override
						public int compare(Object[] strings, Object[] otherStrings) {
							return ((Integer) Integer.parseInt(strings[1].toString()))
									.compareTo(Integer.parseInt(otherStrings[1].toString()));
						}
					});
					listValuesAcq.put("Images In Acquisition", String.valueOf(listAcq.size()));
					hmSeq.put(noSeq, tmp);
					hmInfo.put(noSeq, listValuesAcq);
					new ListDicomParam(noSeq, listAcq, offCalc, numberFrames, offsetImageAcq);
				}
				if (!listCal.isEmpty()) {
					Collections.sort(listCal, new Comparator<Object[]>() {
						@Override
						public int compare(Object[] strings, Object[] otherStrings) {
							return ((Integer) Integer.parseInt(strings[1].toString()))
									.compareTo(Integer.parseInt(otherStrings[1].toString()));
						}
					});
					listValuesCal.put("Images In Acquisition", String.valueOf(listCal.size()));
					hmSeq.put(noSeq + "(calc)", tmp);
					hmInfo.put(noSeq + "(calc)", listValuesCal);
					new ListDicomParam(noSeq + "(calc)", listCal, offAcq, numberFrames, offsetImageCal);
				}
			}
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
		return resul.trim();
	}
}