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
import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListDicomDirSequence implements ParamMRI2, DictionDicom, Runnable {

	private String chemDicom;
//	private HashMap<String, String[]> hmSeqbis = new HashMap<>();
	
	public ListDicomDirSequence() {
	}

	public ListDicomDirSequence(String chemDicom) {
		this.chemDicom = chemDicom;
		run();
	}

	@Override
	public void run() {
		
		String chemD = hmData.get(chemDicom);
		StringBuffer headerDicom = new StringBuffer(new HeaderDicom().getHeaderDicom(chemD));

		int numberOfFrame = 1;

		// 0028,0008 -> Number of Frames
		Pattern pattern = Pattern.compile("(0028,0008)(\\s)(.*)");
		Matcher matcher = pattern.matcher(headerDicom);
		while (matcher.find()) {
			String group = matcher.group();
			group = group.substring(group.indexOf(": ") + 2);
			group = group.replaceAll(" ", "");
			if (Integer.parseInt(group) > 1) {
				numberOfFrame = 2;
				break;
			}
		}

		String[] listImDcm;
		String ImNum = "", fileName = "", ImType = "", ScanSeq = "";
		String noSeq = null;

		/****************************************************************************************************************/

		if (numberOfFrame == 1) { // read file by file
//			listImDcm = headerDicom.toString().split("Image Number: 0");
			listImDcm = headerDicom.toString().split("0020,0011"); // Series Number

			ArrayList<String[]> listPathDicom = new ArrayList<>();

			for (int i = 0; i < listImDcm.length; i++) {
				// 0020,0011 -> Series Number
				noSeq = listImDcm[i].substring(listImDcm[i].indexOf(": ") + 2,listImDcm[i].indexOf("\n"));
//				System.out.println(this + " : " + noSeq);
				// 0004,1500 -> ReferencedFileID
				// 0008,0008 -> Image Type
				// 0020,0013 -> InstanceNumber
				// 0018,0020 -> Scanning Sequence
				Pattern pat = Pattern.compile("(0004,1500|0008,0008|0018,0020|0020,0013)(\\s)(.*)");
				Matcher match = pat.matcher(listImDcm[i]);
				Boolean val = false;
				String[] tmp = null;
				while (match.find()) {
					String resul = match.group();
//					System.out.println(this+" : resul = "+resul);

					if (resul.contains("0004,1500") && !resul.contains("XX_") && !resul.contains("PS_")
							&& resul.contains(":")) {
						fileName = resul.substring(resul.indexOf(": ") + 2).trim();
						fileName = fileName.replace("\\", PrefParam.separator);
						val = true;
					}
					if (resul.contains("0008,0008") && val) {
						ImType = resul.substring(resul.indexOf(": ") + 2);
					}
					if (resul.contains("0018,0020") && val) {
						ScanSeq = resul.substring(resul.indexOf(": ") + 2);
					}
					if (resul.contains("0020,0013") && resul.contains(":") && val && !resul.contains("Image Number: 0")) {
							tmp = new String[4];
							ImNum = resul.substring(resul.indexOf(": ") + 2);
							val = false;
							tmp[0] = ImNum.trim();
							tmp[1] = fileName.trim();
							tmp[2] = ImType.trim();
							tmp[3] = ScanSeq.trim();
							listPathDicom.add(tmp);
//							System.out.println(this + " : " + tmp[0] + " , " + tmp[1] + " , " + tmp[2] + " , " + tmp[3]);
					}
				}

				if (!listPathDicom.isEmpty()) {

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
						String ts = new ChangeSyntax().NewSyntaxScanSeq(listPathDicom.get(h)[3]);
						int indSs = Arrays.asList(listScanSeq).indexOf(ts);

						if (indTp < 5) {
							if (indSs < 4) {
								listPathAcq.add(chemDicom + PrefParam.separator + listPathDicom.get(h)[1].toString());
							} else
								listPathCal.add(chemDicom + PrefParam.separator + listPathDicom.get(h)[1].toString());
						} else
							listPathCal.add(chemDicom + PrefParam.separator + listPathDicom.get(h)[1].toString());
					}

					Object[] list;
					list = listPathAcq.toArray();

					hmSeq.put(noSeq, Arrays.copyOf(list, list.length, String[].class));

					if (!listPathCal.isEmpty()) {
						list = listPathCal.toArray();
						hmSeq.put(noSeq + "(cal)", Arrays.copyOf(list, list.length, String[].class));
					}
					listPathDicom.clear();
				}
			}
		}

		/****************************************************************************************************************/

		else { // read 1 file multi-frame

			// 0004,1430 -> Directory Record Type (Patient, Study, Series,
			// Image, etc...)
			listImDcm = headerDicom.toString().split("0004,1430 (.*) IMAGE");
			String[] tmp;

			for (int i = 1; i < listImDcm.length; i++) {
				fileName = searchParam(new StringBuffer(listImDcm[i]), "0004,1500");
				if (!fileName.contains("XX_") && !fileName.contains("PS_") && !fileName.isEmpty()) {
					fileName = fileName.replace("\\", PrefParam.separator);
					tmp = new String[1];
					tmp[0] = chemDicom + PrefParam.separator + fileName;
					StringBuffer hdr = new StringBuffer(new HeaderDicom().getHeaderDicom(tmp[0]));
					// 0020,0011 -> Series Number
					noSeq = searchParam(hdr, "Series Number");
					hmSeq.put(noSeq, tmp);
				}
			}
		}

		/****************************************************************************************************************/

		listImDcm = null; // to free memory
		System.gc();

		int n = 0;
		FileManagerFrame.dlg.setVisible(true);

		// final Set<String> listHmseq = hmSeq.keySet();
		// Set<String> listHmseq = hmSeq.keySet().stream().collect(Collectors.toSet());
		String[] listHmseq = hmSeq.keySet().toArray(new String[hmSeq.keySet().size()]);
		for (String jj : listHmseq) {
//			System.out.println(this+" : jj = "+jj);
			if (!jj.isEmpty() && !jj.contentEquals("0")) {
				try {
					listParamDicom(jj, numberOfFrame);
				} catch (Exception e) {
					new GetStackTrace(e);
				}
			}
			FileManagerFrame.dlg.setTitle("Loading : sequence " + n * 100 / hmSeq.size() + " %" + " ; " + "files ");
			n++;
		}
		FileManagerFrame.dlg.setVisible(false);
	}

	public void listParamDicom(String noSeq,int numberOfFrame ) {
		
//		System.out.println(this+" : noSeq = "+noSeq+" , numberOFFrame ="+numberOfFrame);

		String ff = hmSeq.get(noSeq)[0];
		
//		System.out.println(this+" : noSeq = "+noSeq+" , "+hmSeq.get(noSeq).length);

		
		HeaderDicom hdrdcm = new HeaderDicom();
				

		StringBuffer hdr = new StringBuffer(hdrdcm.getHeaderDicom(ff));
		// if (noSeq.contentEquals("201")) {
		// String tmp = hdr.substring(hdr.indexOf("0018,0080"));
		// tmp=tmp.substring(0, tmp.indexOf("\n"));
		// }

		HashMap<String, String> listValuesAcq = new HashMap<>();
		HashMap<String, String> listValuesCal = new HashMap<>();
		
		listValuesAcq.put("TypeOfView","elaborate");
		listValuesCal.put("TypeOfView","elaborate");
		
		if (hdrdcm.isJpegLossLess()) {
			listValuesAcq.put("Note", "JpegLossLess");
			listValuesCal.put("Note", "JpegLossLess");
		}
			

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

		hmInfo.put(noSeq, listValuesAcq);

		ArrayList<String[]> listAcq = new ArrayList<>();
		ArrayList<String[]> listCal = new ArrayList<>();

		String[] listSlice = null;
		int offAcq = 0, offCalc = 0;
		
		String title = FileManagerFrame.dlg.getTitle();
		String bvalue_field = "0018,9087";
		if (listValuesAcq.get("Manufacturer").contains("Philips") || listValuesCal.get("Manufacturer").contains("Philips"))
			bvalue_field = "2001,1003";
		
		if (numberOfFrame > 1) { // read 1 file multi-frame

			hdr = new StringBuffer(new HeaderDicom().getHeaderDicom(hmSeq.get(noSeq)[0]));
			String[] list = hdr.toString().split("0008,0008");

			for (int i = 1; i < list.length; i++) {
				StringBuffer hdrtmp = new StringBuffer(list[i]); 
				FileManagerFrame.dlg.setTitle(title + (i + 1) * 100 / list.length + " %");
				listSlice = new String[21]; // for Image Number, Echo Time,
				// Repetition Time, Inversion Time,
				// Slice Orientation
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
					listSlice[15] = searchParam(hdrtmp, bvalue_field); // diffusion
					String ts = searchParam(hdrtmp, "0018,0020");
					ts = new ChangeSyntax().NewSyntaxScanSeq(ts);
					int indSs = Arrays.asList(listScanSeq).indexOf(ts);
					listSlice[16] = ts; // scanning sequence
					listSlice[17] = searchParam(hdrtmp, "2005,1429");// Label Type (ASL)
					listSlice[18] = searchParam(hdrtmp, "2005,100E");// Scale Slope Philips
					if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[18]="1";
					listSlice[19] = searchParam(hdrtmp, "2005,10B0")+" "+searchParam(hdrtmp, "2005,10B1")+" "+searchParam(hdrtmp, "2005,10B2");
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

		} else { // read file by file

			for (int i = 0; i < hmSeq.get(noSeq).length; i++) {
				FileManagerFrame.dlg.setTitle(title + i * 100 / hmSeq.get(noSeq).length + " %");
				hdr = new StringBuffer(new HeaderDicom().getHeaderDicom(hmSeq.get(noSeq)[i]));
				listSlice = new String[21];
				try {
					listSlice[1] = searchParam(new StringBuffer(hdr.toString().split("Image Number: 0 ")[1]),
						"Image Number");
				}
				catch (Exception e) {
					listSlice[1] = searchParam(new StringBuffer(hdr.toString()),"Image Number");
				}
//				listSlice[1] = searchParam(new StringBuffer(hdr.toString()),"Image Number");
//				System.out.println(this+" : listSlice[1] = "+listSlice[1]);
//				if (!listSlice[1].isEmpty() && !listSlice[1].contentEquals("0")) {
				if (listSlice[1].isEmpty())
					listSlice[1]=String.valueOf(i+1);
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
					if (!listSlice[10].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[10]="0";
					listSlice[11] = searchParam(hdr, "Rescale Slope");
					if (!listSlice[11].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[11]="1";
					listSlice[12] = searchParam(hdr, "Acquisition Time");
					listSlice[13] = searchParam(hdr, "Image Position (Patient)");
					listSlice[14] = searchParam(hdr, "Image Orientation (Patient)");
					listSlice[15] = searchParam(hdr, bvalue_field); // diffusion
					listSlice[16] = searchParam(hdr, "0018,0020"); // scanning sequence
					listSlice[17] = searchParam(hdr, "2005,1429"); // Label Type (ASL)
					listSlice[18] = searchParam(hdr, "2005,100E");// Scale Slope Philips
					if (!listSlice[18].matches("[-+]?[0-9]*\\.?[0-9]+"))
						listSlice[18]="1";
					listSlice[19] = searchParam(hdr, "2005,10B0")+" "+searchParam(hdr, "2005,10B1")+" "+searchParam(hdr, "2005,10B2");
					listSlice[20] = searchParam(hdr, "2005,1413"); // gradient orientation number


					listAcq.add(listSlice);
				}
			}
		}
		
		if ((!listAcq.isEmpty()))
			Collections.sort(listAcq, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] strings, Object[] otherStrings) {
					return ((Integer) Integer.parseInt(strings[1].toString()))
							.compareTo(Integer.parseInt(otherStrings[1].toString()));
				}
			});
			new ListDicomParam(noSeq, listAcq, offCalc, "");
		if (!listCal.isEmpty()) {
			Collections.sort(listCal, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] strings, Object[] otherStrings) {
					return ((Integer) Integer.parseInt(strings[1].toString()))
							.compareTo(Integer.parseInt(otherStrings[1].toString()));
				}
			});
			hmSeq.put(noSeq + "(calc)", hmSeq.get(noSeq).clone());
			hmInfo.put(noSeq + "(calc)", listValuesCal);
			new ListDicomParam(noSeq + "(calc)", listCal, offAcq, "");
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