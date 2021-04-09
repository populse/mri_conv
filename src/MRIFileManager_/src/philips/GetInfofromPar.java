package philips;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import MRIFileManager.ExtractTxtfromFile;
import MRIFileManager.GetStackTrace;

public class GetInfofromPar implements DictionParRec, ListPhilipsParamData {

	private String file;
	private boolean all;
	private int Nimage = 0;
	private int offsetAcq, offsetCal;

	private HashMap<String, String> informationParAcq;
	private HashMap<String, String> informationParCal;

	StringBuffer[] listImgNbr = new StringBuffer[8];
	StringBuffer[] listImgNbrAcq = new StringBuffer[8];
	StringBuffer[] listImgNbrCal = new StringBuffer[8];

	// listImgNbr[0] = slice number
	// listImgNbr[1] = echo number
	// listImgNbr[2] = dynamic number
	// listImgNbr[3] = Image Type
	// listImgNbr[4] = diffusion number
	// listImgNbr[5] = gradient number
	// listImgNbr[6] = scanning sequence number
	// listImgNbr[7] = label type (ASL)

	StringBuffer[] listRSI = new StringBuffer[3];
	// listRSI[0] = RI
	// listRSI[1] = RS
	// listRSI[2] = SS

	public GetInfofromPar(String file, boolean all) {
		this.file = file;
		this.all = all;
		run();
	}

	public void run() {

		String txt, txtGI, txtII, strLine = "";
		String[] columDetail;

		informationParAcq = new HashMap<>();
		informationParCal = new HashMap<>();

		informationParAcq.clear();
		informationParCal.clear();

		txt = new ExtractTxtfromFile(file).getTxt();

		String versionPAR = txt.substring(txt.indexOf("# CLINICAL TRYOUT") + 61,
				txt.indexOf("= GENERAL INFORMATION =") - 8);
		
//		if (!versionPAR.trim().contentEquals("V4.2"))
//			return;

		/***************************************
		 * General Information
		 ***************************************/
		txtGI = txt.substring(txt.indexOf("= GENERAL INFORMATION ="));
		txtGI = txtGI.substring(txtGI.indexOf("\n") + 1);
		txtGI = txtGI.substring(txtGI.indexOf("\n") + 1, txtGI.indexOf("# === PIXEL VALUES =") - 3);

		String tmpj;

		try {
			BufferedReader bg = new BufferedReader(new StringReader(txtGI));
			strLine = "";
			while ((strLine = bg.readLine()) != null) {
				tmpj = strLine.substring(4, strLine.indexOf(":")).trim();
				try {
					tmpj = tmpj.replaceAll("\\[(.*?)\\]", "");
					tmpj = tmpj.replaceAll("\\((.*?)\\)", "");
					tmpj = tmpj.replaceAll("\\<(.*?)\\>", "");
					tmpj = tmpj.replaceAll("\\?", "");
				} catch (Exception e) {
					new GetStackTrace(e, this.getClass().toString());
					// FileManagerFrame.getBugText().setText(
					// FileManagerFrame.getBugText().getText() +
					// "\n----------------\n" + GetStackTrace.getMessage());
				}
				informationParAcq.put(tmpj.trim(), strLine.substring(strLine.indexOf(":   ") + 4).trim());
				informationParCal.put(tmpj.trim(), strLine.substring(strLine.indexOf(":   ") + 4).trim());
			}
			switch (versionPAR.trim()) {
			case "V4":
				informationParAcq.put("Max. number of diffusion values","1");
				informationParCal.put("Max. number of diffusion values","1");
				informationParAcq.put("Max. number of gradient orients","1");
				informationParCal.put("Max. number of gradient orients","1");
				informationParAcq.put("Number of label types","0");
				informationParCal.put("Number of label types","0");
				break;
			case "V4.1":
				informationParAcq.put("Number of label types","0");
				informationParCal.put("Number of label types","0");
				break;
			default:
				break;
			}
			
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		if (all) {

			/*********************************
			 * Image Information
			 ***************************************/
			txtII = txt.substring(txt.indexOf("= IMAGE INFORMATION ="));
			txtII = txtII.substring(txtII.indexOf("\n") + 1);
			txtII = txtII.substring(txtII.indexOf("\n") + 1);
			txtII = txtII.substring(txtII.indexOf("\n") + 1, txtII.indexOf("# === END OF DATA DESCRIPTION FILE =") - 4);

			ArrayList<String[]> listColumn = new ArrayList<>();
			ArrayList<String[]> listColumnAcq = new ArrayList<>();
			ArrayList<String[]> listColumnCal = new ArrayList<>();
			ListPhilipsSequence.hasCalcImg = false;
			ListPhilipsSequence.hasAcqImg = false;

			try {
				BufferedReader br = new BufferedReader(new StringReader(txtII));
				strLine = "";
//				if (versionPAR.trim().contentEquals("V4.2"))
				String suff = "";
				switch (versionPAR.trim()) {
				case "V4":
					suff = " 1 1 7 0 0.0 0.0 0.0 1";
					break;
				case "V4.1":
					suff = " 1";
					break;
				default:
					break;
				}
				while ((strLine = br.readLine()) != null) {
					Nimage++;
					strLine += suff;
					columDetail = strLine.trim().split(" +");
					int indAcq = Integer.parseInt(columDetail[5].trim());
					listColumn.add(columDetail);
					if (indAcq < 3 || indAcq == 6 || indAcq == 7) { // acquired_image
						ListPhilipsSequence.hasAcqImg = true;
						listColumnAcq.add(columDetail);
					} else { // calculated_image
						ListPhilipsSequence.hasCalcImg = true;
						listColumnCal.add(columDetail);
					}
				}
				// br.reset();
				br.close();
				columDetail = null;

			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}

			for (int i = 0; i < listImgNbr.length; i++) {
				listImgNbr[i] = new StringBuffer("");
				listImgNbrAcq[i] = new StringBuffer("");
				listImgNbrCal[i] = new StringBuffer("");
			}

			for (int i = 0; i < listRSI.length; i++)
				listRSI[i] = new StringBuffer("");

			String typ;

			for (int i = 0; i < listColumn.size(); i++) {
				listImgNbr[0].append(listColumn.get(i)[0]).append(" "); // slice number
				listImgNbr[1].append(listColumn.get(i)[1]).append(" "); // echo number
				listImgNbr[2].append(listColumn.get(i)[2]).append(" "); // dynamic number
				typ = listColumn.get(i)[4];
				if (typ.contentEquals("-1"))
					typ = "0";
				listImgNbr[3].append(typ).append(" "); // image type
				listImgNbr[6].append(listColumn.get(i)[5]).append(" "); // scan seq number
				listImgNbr[4].append(listColumn.get(i)[41]).append(" ");// diffusion
				listImgNbr[5].append(listColumn.get(i)[42]).append(" ");// gradient number
				listImgNbr[7].append(listColumn.get(i)[48]).append(" ");// label type (ASL)

				if (Integer.parseInt(listColumn.get(i)[5]) < 3) {
					listImgNbrAcq[0].append(listColumn.get(i)[0]).append(" "); // slice number
					listImgNbrAcq[1].append(listColumn.get(i)[1]).append(" "); // echo number
					listImgNbrAcq[2].append(listColumn.get(i)[2]).append(" "); // dynamic number
					typ = listColumn.get(i)[4];
					if (typ.contentEquals("-1"))
						typ = "0";
					listImgNbrAcq[3].append(typ).append(" "); // image type
					listImgNbrAcq[6].append(listColumn.get(i)[5]).append(" "); // scan seq number
					listImgNbrAcq[4].append(listColumn.get(i)[41]).append(" "); // diffusion number
					listImgNbrAcq[5].append(listColumn.get(i)[42]).append(" "); // gradient number
					listImgNbrAcq[7].append(listColumn.get(i)[48]).append(" "); // label type (ASL)

				} else {
					listImgNbrCal[0].append(listColumn.get(i)[0]).append(" "); // slice number
					listImgNbrCal[1].append(listColumn.get(i)[1]).append(" "); // echo number
					listImgNbrCal[2].append(listColumn.get(i)[2]).append(" "); // dynamic number
					typ = listColumn.get(i)[4];
					if (typ.contentEquals("-1"))
						typ = "0";
					listImgNbrCal[3].append(typ).append(" "); // image type
					listImgNbrCal[6].append(listColumn.get(i)[5]).append(" "); // scan seq number
					listImgNbrCal[4].append(listColumn.get(i)[41]).append(" "); // diffusion number
					listImgNbrCal[5].append(listColumn.get(i)[42]).append(" "); // gradient number
					listImgNbrCal[7].append(listColumn.get(i)[48]).append(" "); // label type (ASL)

				}

				listRSI[0].append(listColumn.get(i)[11]).append(" "); // RI
				listRSI[1].append(listColumn.get(i)[12]).append(" "); // RS
				listRSI[2].append(listColumn.get(i)[13]).append(" "); // SS
			}

			if (ListPhilipsSequence.hasAcqImg) {
				Collections.sort(listColumnAcq, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[4].toString()))
								.compareTo(Integer.parseInt(otherStrings[4].toString()));
					}
				});
				Collections.sort(listColumnAcq, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[5].toString()))
								.compareTo(Integer.parseInt(otherStrings[5].toString()));
					}
				});
				Collections.sort(listColumnAcq, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[0].toString()))
								.compareTo(Integer.parseInt(otherStrings[0].toString()));
					}
				});
			}
			
//			for (int i=0; i<listColumnAcq.size(); i++)
//				System.out.println(Arrays.toString(listColumnAcq.get(i)));

//			System.out.println(file);
//			for (String[] kk : listColumnAcq)
//				System.out.println(kk[0]+" , "+kk[1]+" , "+kk[2]+" , "+kk[4]+" , "+kk[5]+" , "+kk[48]);

			if (ListPhilipsSequence.hasCalcImg) {
				Collections.sort(listColumnCal, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[4].toString()))
								.compareTo(Integer.parseInt(otherStrings[4].toString()));
					}
				});
//				Collections.sort(listColumnCal, new Comparator<Object[]>() {
//					@Override
//					public int compare(Object[] strings, Object[] otherStrings) {
//						return ((Integer) Integer.parseInt(strings[5].toString()))
//								.compareTo(Integer.parseInt(otherStrings[5].toString()));
//					}
//				});
				Collections.sort(listColumnCal, new Comparator<Object[]>() {
					@Override
					public int compare(Object[] strings, Object[] otherStrings) {
						return ((Integer) Integer.parseInt(strings[0].toString()))
								.compareTo(Integer.parseInt(otherStrings[0].toString()));
					}
				});
			}
			
//			for (int i=0; i<listColumnCal.size(); i++)
//				System.out.println(Arrays.toString(listColumnCal.get(i)));

//			System.out.println(file);
//			for (String[] kk : listColumnCal)
//				System.out.println(kk[0]+" , "+kk[1]+" , "+kk[2]+" , "+kk[4]+" , "+kk[5]+" , "+kk[48]);

			/**********************************************************************************************************/

			StringBuffer[] imageInfoLine;

			if (ListPhilipsSequence.hasAcqImg) {
				offsetAcq = Integer.parseInt(listColumnAcq.get(listColumnAcq.size() / 2)[6]);
//				imageInfoLine = new StringBuffer[imageInformationParRec.length];
				imageInfoLine = new StringBuffer[imageInformationParRec.length];

				for (int i = 0; i < imageInfoLine.length; i++)
					imageInfoLine[i] = new StringBuffer("");

				StringBuffer tmp;

				for (int i = 0; i < listColumnAcq.size(); i++) {
					for (int j = 0; j < imageInfoLine.length; j++) {
						tmp = new StringBuffer(listColumnAcq.get(i)[Integer.parseInt(imageInformationParRec[j][1])])
								.append(" ");

						if (j == 9 || j == 23)
							tmp.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 1])
									.append("] [");

						else if (j == 15 || j == 16)
							tmp.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 1])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 2])
									.append("] [");

						else if (j == 39)
							tmp.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 1])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 2])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationParRec[27][1])])
									.append("] [");

						imageInfoLine[j].append(tmp);
					}
				}

				for (int i = 0; i < imageInformationParRec.length; i++) {
					informationParAcq.put(imageInformationParRec[i][0], deleteDuplicate(imageInfoLine[i].toString()));
				}

				informationParAcq.put("MetadataRaw", txt);
				listColumnAcq.clear();
				imageInfoLine = null;
				tmp = null;

			}

			/*****************************************************************************************************/
			if (ListPhilipsSequence.hasCalcImg) {
				offsetCal = Integer.parseInt(listColumnCal.get(listColumnCal.size() / 2)[6]);
				imageInfoLine = new StringBuffer[imageInformationParRec.length];
				for (int i = 0; i < imageInfoLine.length; i++)
					imageInfoLine[i] = new StringBuffer("");

				StringBuffer tmp;
				for (int i = 0; i < listColumnCal.size(); i++) {
					for (int j = 0; j < imageInfoLine.length; j++) {
						tmp = new StringBuffer(listColumnCal.get(i)[Integer.parseInt(imageInformationParRec[j][1])])
								.append(" ");

						if (j == 9 || j == 23) {
							tmp.append(listColumnCal.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 1])
									.append("] [");
						} else if (j == 15 || j == 16) {
							tmp.append(listColumnCal.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 1])
									.append(" ")
									.append(listColumnCal.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 2])
									.append("] [");
						}

						else if (j == 39)
							tmp.append(listColumnCal.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 1])
									.append(" ")
									.append(listColumnCal.get(i)[Integer.parseInt(imageInformationParRec[j][1]) + 2])
									.append(" ")
									.append(listColumnCal.get(i)[Integer.parseInt(imageInformationParRec[27][1])])
									.append("] [");

						imageInfoLine[j].append(tmp);
					}
				}

				for (int i = 0; i < imageInformationParRec.length; i++) {
					informationParCal.put(imageInformationParRec[i][0], deleteDuplicate(imageInfoLine[i].toString()));
				}

				informationParCal.put("MetadataRaw", txt);
				listColumnCal.clear();
				imageInfoLine = null;
				tmp = null;

			}

			/*****************************************************************************************************/

		}

	}

	private String deleteDuplicate(String elements) {

		String resul = "";
		String[] list = null;

		if (!elements.contains("]"))
			list = elements.split(" +");
		else {
			list = elements.split("\\] \\[");
		}

		List<String> array = Arrays.asList(list);
		Set<String> hs = new LinkedHashSet<>(array);
		list = Arrays.copyOf(hs.toArray(), hs.toArray().length, String[].class);

		for (String hh : list)
			resul += hh + " ";

		return resul.trim();
	}

	@Override
	public HashMap<String, String> getInfoImageAcq() {
		return informationParAcq;
	}

	@Override
	public StringBuffer[] getListImgNbr() {
		return listImgNbr;
	}

	@Override
	public StringBuffer[] getListRSI() {
		return listRSI;
	}

	@Override
	public HashMap<String, String> getInfoImageCal() {
		return informationParCal;
	}

	@Override
	public int getNImage() {
		return Nimage;
	}

	@Override
	public StringBuffer[] getListImgNbrAcq() {
		return listImgNbrAcq;
	}

	@Override
	public StringBuffer[] getListImgNbrCal() {
		return listImgNbrCal;
	}

	@Override
	public int getMiddleOffsetAcq() {
		return offsetAcq;
	}

	@Override
	public int getMiddleOffsetCal() {
		return offsetCal;
	}

}