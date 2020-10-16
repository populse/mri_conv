package brukerParavision;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import MRIFileManager.Dateformatmodif;
import MRIFileManager.ExtractTxtfromFile;
import MRIFileManager.GetStackTrace;
import abstractClass.ListParam2;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListBrukerParam extends PrefParam implements ParamMRI2, ListParam2 {

	private String chem2dseq, chemVisupars, seqSel, serialNumber;

	public ListBrukerParam(String chem2dseq, String seqSel, String serialNumber) {
		this.chem2dseq = chem2dseq;
		this.seqSel = seqSel;
		this.serialNumber = serialNumber;
		chemVisupars = chem2dseq.substring(0, chem2dseq.indexOf("2dseq") - 1) + separator + "visu_pars";
	}

	@Override
	public HashMap<String, String> ListParamValueAcq(String it) throws IOException {
		HashMap<String, String> lv = new HashMap<>();
		ConcatenatFileBruker contFile = new ConcatenatFileBruker(chem2dseq);
		String txtParam = contFile.getTxtCont();
		File file2dseq = new File(chem2dseq);
		lv.put("noSeq", seqSel);
		lv.put("File path", file2dseq.getAbsolutePath());
		lv.put("File Name", file2dseq.getName());
		lv.put("File Size (Mo)", String.valueOf(file2dseq.length() / (1024 * 1024.0)));
		lv.put("Serial Number", serialNumber);
		lv.put("fileExists", contFile.missingFiles());

		for (String sg : dictionaryMRISystem.keySet()) {
			String tmp = new SearchParamBruker2(dictionaryMRISystem.get(sg).get("keyName"), txtParam).result();
			String tmp_param = dictionaryMRISystem.get(sg).get("keyName");
			if (tmp_param.contains("(or)")) {
				for (String hh:tmp_param.split(" \\(or\\) ")) {
					tmp = new SearchParamBruker2(hh.trim(), txtParam).result();
					if (!tmp.isEmpty())
						break;
				}
			}

			if (dictionaryMRISystem.get(sg).get("format")!=null) 
				tmp = new Dateformatmodif(tmp, dictionaryMRISystem.get(sg).get("format"),dictionaryJsonSystem.get(sg).get("format")).getNewFormatDate();
			lv.put(sg, tmp);
		}

		for (String sh : dictionaryMRIUser.keySet())
			lv.put(sh, new SearchParamBruker2(dictionaryMRIUser.get(sh).get("keyName"), txtParam).result());

		/************************************************
		 * recalculate number Of Slice
		 ************************************************/
		if (!lv.get("Scan Mode").contains("1")) {
			String chemVisu = chem2dseq.substring(0, chem2dseq.lastIndexOf(PrefParam.separator) + 1) + "visu_pars";
			chemVisu = new ExtractTxtfromFile(chemVisu).getTxt();
			chemVisu = chemVisu.substring(chemVisu.indexOf("$VisuCorePosition="));
			chemVisu = chemVisu.substring(chemVisu.indexOf("( ") + 2, chemVisu.indexOf(","));

			lv.put("Number Of Slice", chemVisu);
		}
		
		/************************************************
		 * recalculate spatial resol
		 ************************************************/
		String scanResol = lv.get("Scan Resolution");
		String fov = lv.get("FOV");

		if (!lv.get("Scan Mode").contentEquals("1")) {
			float x, y, z;
			String tmp;

			x = Float.parseFloat(fov.split(" +")[0]) * 10 / Float.parseFloat(scanResol.split(" +")[0]);
			y = Float.parseFloat(fov.split(" +")[1]) * 10 / Float.parseFloat(scanResol.split(" +")[1]);
			tmp = String.valueOf(x) + " " + String.valueOf(y);

			if (fov.split(" +").length == 3) {
				z = Float.parseFloat(fov.split(" +")[2]) * 10 / Float.parseFloat(scanResol.split(" +")[2]);
				tmp += " " + String.valueOf(z);
			}

			lv.put("Spatial Resolution", tmp);
		}
		
		/********************************************* 
		 * redefine Diffusion Ao Images number 
		 ********************************************/
		
		if (lv.get("Diffusion Ao Images number").isEmpty())
			lv.put("Diffusion Ao Images number", "0");

		lv.put("MetadataRaw", txtParam);
		
		/**********************************************************
		 * redefine Slice Thickness and Slice separation if empty
		 **********************************************************/
		
		if (lv.get("Slice Thickness").isEmpty())
			lv.put("Slice Thickness", "1.0");
		
		if (lv.get("Slice Separation").isEmpty())
			lv.put("Slice Separation", lv.get("Slice Thickness"));
		
		/**********************************************************
		 * redefine Slice Orientation if slice packages
		 **********************************************************/
		String listSO = lv.get("Slice Orientation");
		listSO = Arrays.asList(listSO.split(" +")).stream().distinct().collect(Collectors.joining(" "));
		lv.put("Slice Orientation", listSO);
		
		return lv;
	}

	@Override
	public Object[] ListOrderStackAcq(String dim, String nImage) {
		Object[] lv = new Object[6];
		lv[0] = "xyczt";
		lv[1] = 1;
		lv[2] = 1;
		lv[3] = 1;
		lv[4] = null;
		lv[5] = 0; // offset for multiOrientation
		String exp = "\\)\\s\\(";
		String lab, val;

		String[][] listStackParam = null;
		try {
			listStackParam = new StackOrderImage(chemVisupars).listStack();
		} catch (IOException e) {
			new GetStackTrace(e);
//			FileManagerFrame.getBugText().setText(
//					FileManagerFrame.getBugText().getText() + "\n----------------\n" + GetStackTrace.getMessage());
		}

		String[] listlabel = new String[listStackParam[2][1].split(exp).length];

		if (listStackParam[2][1] != "") {
			for (int i = 0; i < listlabel.length; i++) {
				lab = listStackParam[2][1].split(exp)[i].split(",")[1];
				val = listStackParam[2][1].split(exp)[i].split(",")[0];
				val = val.replace("(", "");
				listlabel[i] = lab + " : " + val;
			}
		}

		int orderDescDim = 0;

		if (listStackParam[1][1] != "")
			orderDescDim = Integer.parseInt(listStackParam[1][1]);
		String tmp = listStackParam[2][1];

		// System.out.println(seqSel+" : "+dim+" , "+orderDescDim + " , " +
		// tmp);
		// for (String hh : listlabel)
		// System.out.print(hh+" ");
		// System.out.println(" ");

		/*************************************
		 * 2d
		 **************************************************************/
		if (dim.contains("2") && !listStackParam[0][1].equals("1")) {
//			System.out.println("orderDescDim = "+orderDescDim+" , tmp = "+tmp);
			
			if (orderDescDim == 1) {
				if (tmp.contains("FG_SLICE")) {
					lv[0] = "xyczt";
					lv[1] = 1;
					lv[2] = Integer.parseInt(listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					lv[3] = 1;
				} else {
				lv[1] = 1;
				lv[2] = 1;
				lv[3] = Integer.parseInt(listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
				}
			}

			if (orderDescDim == 2) {

				if (tmp.contains("FG_SLICE")) {
					lv[1] = 1;
					if (listlabel[0].contains("FG_SLICE")) {
						lv[0] = "xyczt";
						lv[2] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					}
					if (listlabel[1].contains("FG_SLICE")) {
						lv[0] = "xyctz";
						lv[2] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					}
					if (listlabel[0].contains("FG_MOVIE")) { // to verify !!
						System.out.println("FG_MOVIE, FG_Slice");
						lv[0] = "xyczt";
					}
				}

				else if (tmp.contains("FG_CYCLE")) {
					lv[1] = 1;
					if (listlabel[0].contains("FG_CYCLE")) {
						lv[0] = "xyczt";
						lv[2] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					}
					if (listlabel[1].contains("FG_CYCLE")) {
						lv[0] = "xyctz";
						lv[2] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					}
				}

				else if (tmp.contains("FG_ECHO")) {
					lv[1] = 1;
					if (listlabel[0].contains("FG_ECHO")) {
						lv[0] = "xyczt";
						lv[2] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					}
					if (listlabel[1].contains("FG_ECHO")) {
						lv[0] = "xyctz";
						lv[2] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					}
				}

				else if (tmp.contains("FG_ISA")) {
					lv[1] = 1;
					if (listlabel[0].contains("FG_ISA")) {
						lv[0] = "xyctz";
						lv[2] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					}
					if (listlabel[1].contains("FG_ISA")) {
						lv[0] = "xyczt";
						lv[2] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					} else {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					}
				}
				else if (tmp.contains("FG_IRMODE")) {
					lv[1] = 1;
					lv[2] = 1;
					lv[3] = Integer.parseInt(
							listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length())) * 
							Integer.parseInt(
							listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					}
			}

			if (orderDescDim == 3) {
//				lv[0] = "xyztc";
				lv[0] = "xyczt";
				lv[1] = Integer.parseInt(listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
				lv[2] = Integer.parseInt(listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
				lv[3] = Integer.parseInt(listlabel[2].substring(listlabel[2].indexOf(": ") + 2, listlabel[2].length()));

				// if (listlabel[0].contains("FG_SLICE")) {
				// lv[0] = "xyzct";
				// lv[1] =
				// Integer.parseInt(listlabel[1].substring(listlabel[1].indexOf(":
				// ") + 2, listlabel[1].length()));
				// lv[2] =
				// Integer.parseInt(listlabel[0].substring(listlabel[0].indexOf(":
				// ") + 2, listlabel[0].length()));
				// lv[3] =
				// Integer.parseInt(listlabel[2].substring(listlabel[2].indexOf(":
				// ") + 2, listlabel[2].length()));
				// }

			}
		}

		/*************************************
		 * 3d
		 **************************************************************/

		if (dim.contains("3")) {
			lv[0] = "xyzct";
			lv[1] = 1;
			lv[2] = Integer.parseInt(nImage);
			lv[3] = 1;

			if (orderDescDim == 1) {
				lv[0] = "xyczt";
				lv[3] = Integer.parseInt(listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
			}

			if (orderDescDim == 2) {
				if (tmp.contains("FG_CYCLE")) {
					if (listlabel[0].contains("FG_CYCLE")) {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					} else {
						lv[0] = "xyczt";
						lv[1] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					}

					if (listlabel[1].contains("FG_CYCLE")) {
						lv[0] = "xyctz";
						lv[3] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					} else {
						lv[0] = "xyczt";
						lv[1] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					}
				}

				else if (tmp.contains("FG_ECHO")) {
					if (listlabel[0].contains("FG_ECHO")) {
						lv[0] = "xyczt";
						lv[3] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					} else {
						lv[0] = "xyczt";
						lv[1] = Integer.parseInt(
								listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
					}

					if (listlabel[1].contains("FG_ECHO")) {
						lv[0] = "xyctz";
						lv[3] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					} else {
						lv[0] = "xyczt";
						lv[1] = Integer.parseInt(
								listlabel[1].substring(listlabel[1].indexOf(": ") + 2, listlabel[1].length()));
					}
				}

				else {
//					lv[0] = "xyczt";
					lv[0] = "xyzct";
					lv[1] = Integer
							.parseInt(listlabel[0].substring(listlabel[0].indexOf(": ") + 2, listlabel[0].length()));
				}
			}
		}

		String[][] listElements = null;
		try {
			listElements = new ListElementFrame(chemVisupars).listElement();
		} catch (Exception e) {
			new GetStackTrace(e);
//			FileManagerFrame.getBugText().setText(GetStackTrace.getMessage());
		}

		String ch1, ch2, chb;

		chb = tmp;
		ch1 = listElements[0][1];
		ch2 = listElements[1][1];
		String txt = null;

		if (ch1 != null && ch2 != null) {
			if (tmp.contains("FG_ISA")) {
				tmp = tmp.substring(tmp.indexOf("FG_ISA, ") + 8);
				tmp = tmp.substring(0, tmp.indexOf(","));
				txt = tmp + " :\n";

				for (int i = 0; i < Integer.parseInt(chb.substring(chb.indexOf("(") + 1, chb.indexOf(","))); i++) {
					txt += "t:" + (i + 1) + "/" + chb.substring(chb.indexOf("(") + 1, chb.indexOf(",")) + " - "
							+ ch2.substring(ch2.indexOf("<") + 1, ch2.indexOf(">")) + " " + "["
							+ ch1.substring(ch1.indexOf("<") + 1, ch1.indexOf(">")) + "] \n";
					ch1 = ch1.substring(ch1.indexOf(">") + 1);
					ch2 = ch2.substring(ch2.indexOf(">") + 1);
				}
				txt += "\n";
			}
			if (tmp.contains("FG_DTI")) {
				tmp = tmp.substring(tmp.indexOf("FG_DTI, ") + 8);
				tmp = tmp.substring(0, tmp.indexOf(","));
				txt = tmp + " :\n";

				for (int i = 0; i < Integer.parseInt(chb.substring(chb.indexOf("(") + 1, chb.indexOf(","))); i++) {
					txt += "t:" + (i + 1) + "/" + chb.substring(chb.indexOf("(") + 1, chb.indexOf(",")) + " - "
							+ ch2.substring(ch2.indexOf("<") + 1, ch2.indexOf(">")) + " " + "["
							+ ch1.substring(ch1.indexOf("<") + 1, ch1.indexOf(">")) + "] \n";
					ch1 = ch1.substring(ch1.indexOf(">") + 1);
					ch2 = ch2.substring(ch2.indexOf(">") + 1);
				}
				txt += "\n";
			}
		}
		
		lv[4] = txt;
//		System.out.println("seq n "+seqSel);
//		System.out.println("lv = "+lv[0]+" , "+lv[1]+" , "+lv[2]+" , "+lv[3]+" , "+lv[4]+" , ");

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