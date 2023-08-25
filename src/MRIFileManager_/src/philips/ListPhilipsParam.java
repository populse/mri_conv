package philips;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import MRIFileManager.Dateformatmodif;
import abstractClass.ListParam2;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListPhilipsParam extends PrefParam implements ParamMRI2, ListParam2, DictionParRec {

	private String pathPhilips, pathPhilips2;
	private HashMap<String, String> InfoImageAcq, InfoImageCal;
	private StringBuffer[] listImgNbr, listImgNbrAcq, listImgNbrCal, listRSI;
	private ListPhilipsParamData philipsParam;
	private int ind = 0;
	private int NImageTot, offAcq, offCal;

	public ListPhilipsParam(String pathPhilips) {

		this.pathPhilips = pathPhilips;
		
//		pathPhilips2 = pathPhilips.replace(".REC", ".PAR");
//		pathPhilips2 = pathPhilips2.replace(".rec", ".par");
//		ind = 0;
//
//		if (!new File(pathPhilips2).exists()) {
//			pathPhilips2 = pathPhilips.replace(".REC", ".xml");
//			ind = 1;
//		}
//		switch (ind) {
//		case 0:
//			philipsParam = new GetInfofromPar(pathPhilips2, true);
//			break;
//		case 1:
//			philipsParam = new GetInfofromXML(pathPhilips2, true);
//			break;
//		}
		
		String[] listExt = {".PAR", ".par", ".xml", ".XML"};
				
		for (String es : listExt) {
			pathPhilips2 = pathPhilips.replace(".REC", es);
			pathPhilips2 = pathPhilips2.replace(".rec", es);
			if (new File(pathPhilips2).exists())
				break;
		}
		
		if (pathPhilips2.toUpperCase().endsWith(".PAR")) {
			philipsParam = new GetInfofromPar(pathPhilips2, true);
			ind = 0;
		}
		else {
			philipsParam = new GetInfofromXML(pathPhilips2, true);
			ind = 1;
		}

		InfoImageAcq = philipsParam.getInfoImageAcq();
		listImgNbrAcq = philipsParam.getListImgNbrAcq();
		InfoImageCal = philipsParam.getInfoImageCal();
		listImgNbrCal = philipsParam.getListImgNbrCal();
		NImageTot = philipsParam.getNImage();
		listImgNbr = philipsParam.getListImgNbr();
		listRSI = philipsParam.getListRSI();
		offAcq = philipsParam.getMiddleOffsetAcq();
		offCal = philipsParam.getMiddleOffsetCal();

		philipsParam = null; // to free memory ?
		System.gc();
	}

	@Override
	public HashMap<String, String> ListParamValueAcq(String it) throws IOException {
		HashMap<String, String> lv = new HashMap<>();
		String acq = "", rec = "";
		String sufx = "";

		if (ind == 0) {
			acq = InfoImageAcq.get("Acquisition nr");
			rec = InfoImageAcq.get("Reconstruction nr");
		} else {
			acq = InfoImageAcq.get("Aquisition Number");
			rec = InfoImageAcq.get("Reconstruction Number");
		}

		if (acq.length() == 1)
			acq = "0" + acq;
		if (rec.length() == 1)
			rec = "0" + rec;
		
//		System.out.println("acq-rec : " + acq + "-" + rec);
		
		File filePhilips = new File(pathPhilips);

		lv.put("Serial Number", acq + "-" + rec + sufx);
		lv.put("File path", filePhilips.getAbsolutePath());
		lv.put("File Name", filePhilips.getName());
		lv.put("Directory", filePhilips.getParentFile().getName());
		lv.put("File Size (Mo)", String.valueOf(filePhilips.length() / (1024 * 1024.0)));
		lv.put("noSeq", it);

		for (String sg : dictionaryMRISystem.keySet()) {
			String tmp = InfoImageAcq.get(dictionaryMRISystem.get(sg).get("keyName").split(";")[ind].trim());
			if (dictionaryMRISystem.get(sg).get("format") != null
					&& !dictionaryMRISystem.get(sg).get("format").split(";")[ind].trim().contains("~"))
				tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(sg).get("format").split(";")[ind].trim(),
						dictionaryJsonSystem.get(sg).get("format")).getNewFormatDate();
			lv.put(sg, tmp);
		}

		for (String sg : dictionaryMRIUser.keySet()) {
			lv.put(sg, InfoImageAcq.get(dictionaryMRIUser.get(sg).get("keyName").split(";")[ind].trim()));
		}

		lv.put("MetadataRaw", InfoImageAcq.get("MetadataRaw"));

		if (InfoImageAcq.get("index in REC file") != null)
			lv.put("index in REC file", InfoImageAcq.get("index in REC file"));
		else
			lv.put("index in REC file", InfoImageAcq.get("Index"));

		String lvtmp;

		/************************************************
		 * recalculate Image in acq
		 ************************************************/
		lvtmp = lv.get("Images In Acquisition");
		lvtmp = String.valueOf(lvtmp.split(" +").length);
		lv.put("Images In Acquisition", lvtmp);

		/***************** modifify Data type if <0 ********************/
		lvtmp = lv.get("Data Type");
		if (Integer.parseInt(lvtmp.trim()) < 0)
			lvtmp = "0";
		lv.put("Data Type", lvtmp);

		/******** modifiy echo number *********/
		lvtmp = lv.get("Number Of Echo");
		lvtmp = String.valueOf(lvtmp.trim().split(" +").length);
		lv.put("Number Of Echo", lvtmp);

		/*********** redefinite diffusion ****************/
		lvtmp = lv.get("Number Of Diffusion");
		if (!lvtmp.contentEquals("1"))
			lvtmp = String.valueOf(lvtmp.split(" +").length);
		lv.put("Number Of Diffusion", lvtmp);

		/********* to put Diffusion Ao Images number at 0 *************/
		lv.put("Diffusion Ao Images number", "0");

		/***************************************************************
		 * reorganization Direction Diffusion and B-values effective
		 *************************************************************/

		lvtmp = lv.get("Direction Diffusion");
		String[] listVal = lvtmp.split(" +");
		String lvtmpb = "", lvtmpc = "";
		int cnt = 0;
		for (String hh : listVal) {
			if (cnt == 3) {
				lvtmpc += hh + " ";
				cnt = 0;
			} else {
				lvtmpb += hh + " ";
				cnt++;
			}
		}
		lv.put("Direction Diffusion", lvtmpb.trim());
		lv.put("B-values effective", lvtmpc.trim());

		/********* redefinite Slice Thickness, added to Slice Gap *********/
		lvtmp = lv.get("Slice Thickness");
		String sliceGap = lv.get("Slice Gap");
		try {
			lvtmp = String.valueOf(Float.parseFloat(lvtmp) + Float.parseFloat(sliceGap));
		} catch (Exception e) {
			lvtmp = String.valueOf(Float.parseFloat(lvtmp.split(" +")[0]) + Float.parseFloat(sliceGap.split(" +")[0]));
			;
		}
		lv.put("Slice Thickness", lvtmp);

		/************************************************
		 * redefinite Slice Orientation
		 ************************************************/
		try {
			lvtmp = lv.get("Slice Orientation");
			String[] listOrient = { "", "axial", "sagittal", "coronal" };
			for (String gg : lvtmp.split(" +"))
				lvtmp = lvtmp.replace(gg, listOrient[Integer.parseInt(gg)]);
			lv.put("Slice Orientation", lvtmp);
		} catch (Exception e) {
		}

		try {
			lvtmp = lv.get("Slice Orientation");
			lv.put("Slice Orientation", lvtmp.toLowerCase());
		} catch (Exception e) {
		}

		/************************************************
		 * redefinite Image Type
		 ************************************************/
		lvtmp = lv.get("Image Type");

		try {
			for (String gg : lvtmp.split(" +")) {
				if (Integer.parseInt(gg.trim()) < 0) {
					lvtmp = lvtmp.replace(gg, "0");
					gg = "0";
				}
				lvtmp = lvtmp.replace(gg, listType[Integer.parseInt(gg)]);
			}
			lv.put("Image Type", lvtmp);
		} catch (Exception e) {
		}

		// lv.put("MRI parameters", lvtmp);

		/************************************************
		 * redefinite Scanning Sequence
		 ************************************************/
		lvtmp = lv.get("Scanning Sequence");

		try {
			for (String gg : lvtmp.split(" +"))
				lvtmp = lvtmp.replace(gg, listScanSeq[Integer.parseInt(gg)]);
			lv.put("Scanning Sequence", lvtmp);
		} catch (Exception e) {
		}

		/************************************************
		 * convert Label ASL : 1 to 'CONTROL', 2 to 'LABEL'
		 ************************************************/

		try {
			lvtmp = lv.get("Label Type (ASL)");
			lvtmp = lvtmp.replace("1", "CONTROL");
			lvtmp = lvtmp.replace("2", "LABEL");
			lv.put("Label Type (ASL)", lvtmp);
		} catch (Exception e) {

		}

		/****************************************************************************
		 * replace '.' '/' ':' in Creation Date and Acquisition Date by '-' or '_'
		 *****************************************************************************/

		// lvtmp = lv.get("Creation Date");
		// try {
		// lvtmp = lvtmp.toString().replace(".", "-");
		// lvtmp = lvtmp.toString().replace("/", "_");
		// lvtmp = lvtmp.toString().replace(":", "-");
		// lv.put("Creation Date", lvtmp);
		// } catch (Exception e) {
		// }
		//
		// lvtmp = lv.get("Acquisition Date");
		// try {
		// lvtmp = lvtmp.toString().replace(".", "-");
		// lvtmp = lvtmp.toString().replace("/", "_");
		// lvtmp = lvtmp.toString().replace(":", "-");
		// lv.put("Acquisition Date", lvtmp);
		// } catch (Exception e) {
		// }

		InfoImageAcq.clear();

		return lv;
	}

	@Override
	public Object[] ListOrderStackAcq(String dim, String nImage) {

		String[] s1 = new String[8];
		String[] s2 = new String[8];

		for (int h = 0; h < 8; h++) {
			s1[h] = listImgNbrAcq[h].toString();
			s2[h] = listImgNbrAcq[h].toString();
		}

		// listImgNbr[0] = slice number
		// listImgNbr[1] = echo number
		// listImgNbr[2] = dynamic number
		// listImgNbr[3] = Image Type
		// listImgNbr[4] = diffusion number
		// listImgNbr[5] = gradient number
		// listImgNbr[6] = scanning sequence number
		// listImgNbr[7] = ASL

		for (int i = 4; i < 19; i++)
			s1[3] = s1[3].replace(String.valueOf(i), "");

		Set<String> uniqueWords;

		int cnt = 0;

		for (int i = 0; i < s1.length; i++) {
			uniqueWords = new HashSet<>(Arrays.asList(s1[i].split(" +")));
			s1[i] = String.valueOf(uniqueWords.size());
			if (uniqueWords.size() > 1)
				cnt++;
		}

		String ord = "";
		try {
			for (int i = 0; i < s2[0].split(" +").length - 1; i++) {
				for (int j = 0; j < s2.length; j++) {
					String[] ll = s2[j].split(" +");
					if (Integer.parseInt(ll[i]) != Integer.parseInt(ll[i + 1]))
						if (!ord.contains(String.valueOf(j)))
							ord += String.valueOf(j) + " ";
				}

				if (ord.split(" +").length == cnt) {
					ord = ord.trim();
					break;
				}
			}
		} catch (Exception e) {
		}

//		 System.out.println("ord = " + ord + " cnt = " + cnt);

		Object[] lv = new Object[16];

		// "xyczt(default)"
		// "xyctz"
		// "xyzct"
		// "xyztc"
		// "xytcz"
		// "xytzc"
		
//		System.out.println("ord = " + ord);

		if (ord.contentEquals("1 0 3"))
			lv[0] = "xytcz"; // (orig)
		else
			lv[0] = "xyctz"; // (orig)
		
//		System.out.println("order = " + lv[0]);

		if (s1[3].contentEquals("0"))
			s1[3] = "1";

		if (Integer.parseInt(s1[4]) > 1 && Integer.parseInt(s1[5]) > 1)
			s1[4] = "1"; // if number of diffusion >1 and number of gradient diffusion >1

		int firstDim = Integer.parseInt(s1[3]) * Integer.parseInt(s1[7]);
		int secondDim = Integer.parseInt(s1[0]);
		int thirdDim = Integer.parseInt(s1[1]) * Integer.parseInt(s1[2]) * Integer.parseInt(s1[4])
				* Integer.parseInt(s1[5]) * Integer.parseInt(s1[6]);
		
		if (thirdDim == 1 && firstDim > 1) {
			thirdDim = firstDim;
			firstDim = 1;
			lv[0] = "xyctz";
		}
		
//		System.out.println(this+" : "+firstDim+" , "+secondDim+" , "+thirdDim);

		lv[1] = firstDim;
		lv[2] = secondDim;
		lv[3] = thirdDim;
		lv[4] = NImageTot;
		lv[5] = listImgNbr[0];
		lv[6] = listImgNbr[1];
		lv[7] = listImgNbr[2];
		lv[8] = listImgNbr[3];
		lv[9] = listImgNbr[4];
		lv[10] = listImgNbr[5];
		lv[11] = listImgNbr[6];
		lv[12] = listRSI[0];
		lv[13] = listRSI[1];
		lv[14] = listRSI[2];
		lv[15] = offAcq;

//		System.out.println("lv[] = " + lv[0] + " , " + lv[1] + " , " + lv[2] + " , " + lv[3] + " , " + lv[4]);

		return lv;
	}

	@Override
	public HashMap<String, String> ListParamValueCal(String it) throws IOException {
		HashMap<String, String> lv = new HashMap<>();
		String acq = "", rec = "";
		String sufx = "";

		// System.out.println(this+" : "+);

		// InfoImageAcq = philipsParam.getInfoImageCal();
		// listImgNbrAcq = philipsParam.getListImgNbrAcq();
		// listImgNbrCal = philipsParam.getListImgNbrCal();

		sufx = "(calc)";

		if (ind == 0) {
			acq = InfoImageCal.get("Acquisition nr");
			rec = InfoImageCal.get("Reconstruction nr");
		} else {
			acq = InfoImageCal.get("Aquisition Number");
			rec = InfoImageCal.get("Reconstruction Number");
		}

		if (acq.length() == 1)
			acq = "0" + acq;
		if (rec.length() == 1)
			rec = "0" + rec;

		File filePhilips = new File(pathPhilips);

		lv.put("Serial Number", acq + "-" + rec + sufx);
		lv.put("File path", filePhilips.getAbsolutePath());
		lv.put("File Name", filePhilips.getName());
		lv.put("File Size (Mo)", String.valueOf(filePhilips.length() / (1024 * 1024.0)));
		lv.put("noSeq", it);

		for (String sg : dictionaryMRISystem.keySet()) {
			String tmp = InfoImageCal.get(dictionaryMRISystem.get(sg).get("keyName").split(";")[ind].trim());
			if (dictionaryMRISystem.get(sg).get("format") != null
					&& !dictionaryMRISystem.get(sg).get("format").split(";")[ind].trim().contains("~"))
				tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(sg).get("format").split(";")[ind].trim(),
						dictionaryJsonSystem.get(sg).get("format")).getNewFormatDate();
			lv.put(sg, tmp);
			// lv.put(sg,
			// InfoImageCal.get(dictionaryMRISystem.get(sg).get("keyName").split(";")[ind].trim()));
		}

		for (String sg : dictionaryMRIUser.keySet()) {
			lv.put(sg, InfoImageCal.get(dictionaryMRIUser.get(sg).get("keyName").split(";")[ind].trim()));
		}

		lv.put("MetadataRaw", InfoImageCal.get("MetadataRaw"));

		if (InfoImageCal.get("index in REC file") != null)
			lv.put("index in REC file", InfoImageCal.get("index in REC file"));
		else
			lv.put("index in REC file", InfoImageCal.get("Index"));

		String lvtmp;

		/************************************************
		 * recalculate Image in acq
		 ************************************************/
		lvtmp = lv.get("Images In Acquisition");
		lvtmp = String.valueOf(lvtmp.split(" +").length);
		lv.put("Images In Acquisition", lvtmp);

		/***************** modifify Data type if <0 ********************/
		lvtmp = lv.get("Data Type");
		if (Integer.parseInt(lvtmp.trim()) < 0)
			lvtmp = "0";
		lv.put("Data Type", lvtmp);

		/******** modifiy echo number *********/
		lvtmp = lv.get("Number Of Echo");
		lvtmp = String.valueOf(lvtmp.trim().split(" +").length);
		lv.put("Number Of Echo", lvtmp);

		/*********** redefinite diffusion ****************/
		lvtmp = lv.get("Number Of Diffusion");
		if (!lvtmp.contentEquals("1"))
			lvtmp = String.valueOf(lvtmp.split(" +").length);
		lv.put("Number Of Diffusion", lvtmp);

		/********* to put Diffusion Ao Images number at 0 *************/
		lv.put("Diffusion Ao Images number", "0");

		/*********
		 * reorganization Direction Diffusion and B-values effective
		 ****************/

		lvtmp = lv.get("Direction Diffusion");
		String[] listVal = lvtmp.split(" +");
		String lvtmpb = "", lvtmpc = "";
		int cnt = 0;
		for (String hh : listVal) {
			if (cnt == 3) {
				lvtmpc += hh + " ";
				cnt = 0;
			} else {
				lvtmpb += hh + " ";
				cnt++;
			}
		}
		lv.put("Direction Diffusion", lvtmpb.trim());
		lv.put("B-values effective", lvtmpc.trim());

		/********* redefinite Slice Thickness, added to Slice Gap *********/
		lvtmp = lv.get("Slice Thickness");
		String sliceGap = lv.get("Slice Gap");
		try {
			lvtmp = String.valueOf(Float.parseFloat(lvtmp) + Float.parseFloat(sliceGap));
		} catch (Exception e) {
			lvtmp = String.valueOf(Float.parseFloat(lvtmp.split(" +")[0]) + Float.parseFloat(sliceGap.split(" +")[0]));
			;
		}
		lv.put("Slice Thickness", lvtmp);

		/************************************************
		 * redefinite Slice Orientation
		 ************************************************/
		try {
			lvtmp = lv.get("Slice Orientation");
			String[] listOrient = { "", "axial", "sagittal", "coronal" };
			for (String gg : lvtmp.split(" +"))
				lvtmp = lvtmp.replace(gg, listOrient[Integer.parseInt(gg)]);
			lv.put("Slice Orientation", lvtmp);
		} catch (Exception e) {
		}

		try {
			lvtmp = lv.get("Slice Orientation");
			lv.put("Slice Orientation", lvtmp.toLowerCase());
		} catch (Exception e) {
		}

		/************************************************
		 * redefinite Image Type
		 ************************************************/
		lvtmp = lv.get("Image Type");

		try {
			for (String gg : lvtmp.split(" +"))
				lvtmp = lvtmp.replace(gg, listType[Integer.parseInt(gg)]);
			lv.put("Image Type", lvtmp);
		} catch (Exception e) {
		}

		// lv.put("MRI parameters", lvtmp);

		/************************************************
		 * redefinite Scanning Sequence
		 ************************************************/
		lvtmp = lv.get("Scanning Sequence");

		try {
			for (String gg : lvtmp.split(" +"))
				lvtmp = lvtmp.replace(gg, listScanSeq[Integer.parseInt(gg)]);
			lv.put("Scanning Sequence", lvtmp);
		} catch (Exception e) {
		}

		/****************************************************************************
		 * replace '.' '/' ':' in Creation Date and Acquisition Date by '-' or '_'
		 *****************************************************************************/

		// lvtmp = lv.get("Creation Date");
		// try {
		// lvtmp = lvtmp.toString().replace(".", "-");
		// lvtmp = lvtmp.toString().replace("/", "_");
		// lvtmp = lvtmp.toString().replace(":", "-");
		// lv.put("Creation Date", lvtmp);
		// } catch (Exception e) {
		// }
		//
		// lvtmp = lv.get("Acquisition Date");
		// try {
		// lvtmp = lvtmp.toString().replace(".", "-");
		// lvtmp = lvtmp.toString().replace("/", "_");
		// lvtmp = lvtmp.toString().replace(":", "-");
		// lv.put("Acquisition Date", lvtmp);
		// } catch (Exception e) {
		// }

		InfoImageCal.clear();

		return lv;
	}

	@Override
	public Object[] ListOrderStackCal(String dim, String nImage) {
		// String[] s1 = listImgNbrCal.clone();
		// String[] s2 = listImgNbrCal.clone();

		// String[] s1 = Arrays.copyOfRange(listImgNbrCal, 0, 6);
		// String[] s2 = Arrays.copyOfRange(listImgNbrCal, 0, 6);

		String[] s1 = new String[6];
		String[] s2 = new String[6];

		for (int h = 0; h < 6; h++) {
			s1[h] = listImgNbrCal[h].toString();
			s2[h] = listImgNbrCal[h].toString();
		}

		// listImgNbr[0] = slice number
		// listImgNbr[1] = echo number
		// listImgNbr[2] = dynamic number
		// listImgNbr[3] = Image Type
		// listImgNbr[4] = diffusion number
		// listImgNbr[5] = gradient number
		// listImgNbr[6] = scanning sequence number

		String order = "";

		List<String> list = Arrays.asList(s2);

		Collections.sort(list, Collections.reverseOrder());

		for (String e : list) {
			// System.out.println("e : "+e);
			for (int i = 0; i < s1.length; i++) {
				if (s1[i].contentEquals(e) && !order.contains(String.valueOf(i))) {
					order += String.valueOf(i) + " ";
					break;
				}
			}
		}

		// System.out.println(order);

//		for (int i = 4; i < 19; i++)
//			s1[3] = s1[3].replace(String.valueOf(i), "");

//		for (int i = 0; i < 4; i++)
//			s1[3] = s1[3].replace(String.valueOf(i), "");

		Set<String> uniqueWords;

		for (int i = 0; i < s1.length; i++) {
			uniqueWords = new HashSet<>(Arrays.asList(s1[i].split(" +")));
			s1[i] = String.valueOf(uniqueWords.size());
			// System.out.println(s1[i]);

		}
//		System.out.println(this + " s1[3]: " + s1[3] );

		Object[] lv = new Object[16];

		 lv[0] = "xytcz";
//		lv[0] = "xytzc";
		 
		// lv[0] = "xyczt";
		// lv[0] = "xyctz";
		// lv[0] = "xyzct";
		// lv[0] = "xyztc";

		if (s1[3].contentEquals("0"))
			s1[3] = "1";

		if (Integer.parseInt(s1[4]) > 1)
			lv[0] = "xyctz";

		if (Integer.parseInt(s1[3]) > 1 && Integer.parseInt(s1[0]) > 1 && Integer.parseInt(s1[1])
				* Integer.parseInt(s1[2]) * Integer.parseInt(s1[4]) * Integer.parseInt(s1[5]) > 1) {
			if (order.split(" +")[0].contentEquals("0"))
				lv[0] = "xyctz";
		}
		
		int firstDim = Integer.parseInt(s1[3]);
		int secondDim = Integer.parseInt(s1[0]);
		int thirdDim = Integer.parseInt(s1[1]) * Integer.parseInt(s1[2]) * Integer.parseInt(s1[4])
				* Integer.parseInt(s1[5]);
		
		if (thirdDim == 1 && firstDim > 1) {
			thirdDim = firstDim;
			firstDim = 1;
			lv[0] = "xyctz";
		}
		
		lv[1] = firstDim;
		lv[2] = secondDim;
		lv[3] = thirdDim;
		lv[4] = NImageTot;
		lv[5] = listImgNbr[0]; // slice number
		lv[6] = listImgNbr[1]; // echo number
		lv[7] = listImgNbr[2]; // dynamic number
		lv[8] = listImgNbr[3]; // Image Type
		lv[9] = listImgNbr[4]; // diffusion number
		lv[10] = listImgNbr[5]; // gradient number
		lv[11] = listImgNbr[6]; // scanning sequence number
		lv[12] = listRSI[0];
		lv[13] = listRSI[1];
		lv[14] = listRSI[2];
		lv[15] = offCal;
		
//		System.out.println("lv[] = " + lv[0] + " , " + lv[1] + " , " + lv[2] + " , " + lv[3] + " , " + lv[4]);
//		System.out.println("lv[] = " + lv[5] + " , " + lv[6] + " , " + lv[7] + " , " + lv[8] + " , " + lv[9]);
//		System.out.println("lv[] = " + lv[10] + " , " + lv[11] + " , " + lv[12] + " , " + lv[13] + " , " + lv[14] + " , " + lv[15]);
//		System.out.println(this + "lv[] = " + lv[8] + " , " + lv[11]);


		return lv;
	}
}