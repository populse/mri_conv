package philips;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import MRIFileManager.GetStackTrace;

public class GetInfofromXML implements DictionParRec, ListPhilipsParamData {

	private HashMap<String, String> informationXmlAcq = new HashMap<>();
	private HashMap<String, String> informationXmlCal = new HashMap<>();

	private int Nimage = 0;
	private int offsetAcq, offsetCal;

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
	// listImgNbr[0] = RI
	// listImgNbr[1] = RS
	// listImgNbr[2] = SS

	public GetInfofromXML(String file, boolean all) {

		Document doc = null;
		SAXBuilder sxBuild = new SAXBuilder();

		try {
			doc = sxBuild.build(new File(file));
		} catch (Exception e) {
			new GetStackTrace(e);
//			FileManagerFrame.getBugText().setText(
//					FileManagerFrame.getBugText().getText() + "\n----------------\n" + GetStackTrace.getMessage());
		}
		Iterator<Element> ite1, ite2;
		Element classElement = doc.getRootElement();
		List<Element> stdlist = classElement.getChildren();

		/*****************************************
		 * GENERAL INFORMATION
		 ***********************************************/
		Element seriesInfo = stdlist.get(0);
		List<Element> list1 = seriesInfo.getChildren();
		ite1 = list1.iterator();

		while (ite1.hasNext()) {
			Element std1 = ite1.next();
			informationXmlAcq.put(std1.getAttributeValue("Name"), std1.getValue());
			informationXmlCal.put(std1.getAttributeValue("Name"), std1.getValue());
		}

		String mp;

		mp = informationXmlAcq.get("FOV AP");
		mp += " " + informationXmlAcq.get("FOV FH") + " " + informationXmlAcq.get("FOV RL");
		informationXmlAcq.put("FOV AP", mp);
		informationXmlCal.put("FOV AP", mp);

		mp = informationXmlAcq.get("Angulation AP");
		mp += " " + informationXmlAcq.get("Angulation FH") + " " + informationXmlAcq.get("Angulation RL");
		informationXmlAcq.put("Angulation AP", mp);
		informationXmlCal.put("Angulation AP", mp);

		mp = informationXmlAcq.get("Off Center AP");
		mp += " " + informationXmlAcq.get("Off Center FH") + " " + informationXmlAcq.get("Off Center RL");
		informationXmlAcq.put("Off Center AP", mp);
		informationXmlCal.put("Off Center AP", mp);

		if (all) {

			for (int i = 0; i < listImgNbr.length; i++) {
				listImgNbr[i] = new StringBuffer("");
				listImgNbrAcq[i] = new StringBuffer("");
				listImgNbrCal[i] = new StringBuffer("");
			}

			for (int i = 0; i < listRSI.length; i++)
				listRSI[i] = new StringBuffer("");

			/*****************************************
			 * IMAGE INFORMATION
			 ***********************************************/

			Element imageArray = stdlist.get(1);
			List<Element> list2 = imageArray.getChildren();
			ite1 = list2.iterator();
			String columnDetail = "";
			ArrayList<String[]> listColumn = new ArrayList<>();
			ArrayList<String[]> listColumnAcq = new ArrayList<>();
			ArrayList<String[]> listColumnCal = new ArrayList<>();
			ListPhilipsSequence.hasCalcImg = false;
			ListPhilipsSequence.hasAcqImg = false;

			while (ite1.hasNext()) {
				Nimage++;
				Element std2 = ite1.next();
				List<Element> list3 = std2.getChildren();
				columnDetail = "";

				for (int h = 0; h < list3.size(); h++) {
					if (list3.get(h).getChildren().size() != 0) {
						List<Element> list4 = list3.get(h).getChildren();
						ite2 = list4.iterator();
						while (ite2.hasNext()) {
							Element std4 = ite2.next();
							columnDetail = columnDetail + std4.getValue() + " ";
						}
					} else {
						columnDetail = columnDetail + list3.get(h).getValue() + " ";
					}
				}

				String[] column = columnDetail.split((" +"));
				column[7] = String.valueOf(Arrays.asList(listType).indexOf(column[7]));
				int indAcq = Arrays.asList(listScanSeq).indexOf(column[8]);
				column[8] = String.valueOf(indAcq);
				listColumn.add(column);

				if (indAcq < 3 || indAcq == 6 || indAcq == 7) {
					ListPhilipsSequence.hasAcqImg = true;
					listColumnAcq.add(column);
				} else {
					ListPhilipsSequence.hasCalcImg = true;
					listColumnCal.add(column);
				}
			}

//			for (int k = 0; k < listImgNbr.length; k++)
//				listImgNbr[k] = new StringBuffer("");

			String lab;

			for (int k = 0; k < listColumn.size(); k++) {
				listImgNbr[0].append(listColumn.get(k)[0]).append(" "); // slice number
				listImgNbr[1].append(listColumn.get(k)[1]).append(" "); // echo number
				listImgNbr[2].append(listColumn.get(k)[2]).append(" "); // dynamic number
				listImgNbr[3].append(listColumn.get(k)[7]).append(" "); // image type
				listImgNbr[4].append(listColumn.get(k)[4]).append(" "); // diffusion number
				listImgNbr[5].append(listColumn.get(k)[5]).append(" "); // gradient number
				listImgNbr[6].append(listColumn.get(k)[8]).append(" "); // sequence number
				lab = String.valueOf(Arrays.asList(listLabelXml).indexOf(listColumn.get(k)[6]));
				listImgNbr[7].append(lab).append(" "); // Label Type

				if (Integer.parseInt(listColumn.get(k)[8]) < 3) {
					listImgNbrAcq[0].append(listColumn.get(k)[0]).append(" "); // slice number
					listImgNbrAcq[1].append(listColumn.get(k)[1]).append(" "); // echo number
					listImgNbrAcq[2].append(listColumn.get(k)[2]).append(" "); // dynamic number
					listImgNbrAcq[3].append(listColumn.get(k)[7]).append(" "); // image type
					listImgNbrAcq[4].append(listColumn.get(k)[4]).append(" "); // diffusion number
					listImgNbrAcq[5].append(listColumn.get(k)[5]).append(" "); // gradient number
					listImgNbrAcq[6].append(listColumn.get(k)[8]).append(" "); // scan seq number
					lab = String.valueOf(Arrays.asList(listLabelXml).indexOf(listColumn.get(k)[6]));
					listImgNbrAcq[7].append(lab).append(" "); // Label Type

				} else {
					listImgNbrCal[0].append(listColumn.get(k)[0]).append(" "); // slice number
					listImgNbrCal[1].append(listColumn.get(k)[1]).append(" "); // echo number
					listImgNbrCal[2].append(listColumn.get(k)[2]).append(" "); // dynamic number
					listImgNbrCal[3].append(listColumn.get(k)[7]).append(" "); // image type
					listImgNbrCal[4].append(listColumn.get(k)[4]).append(" "); // diffusion number
					listImgNbrCal[5].append(listColumn.get(k)[5]).append(" "); // gradient number
					listImgNbrCal[6].append(listColumn.get(k)[8]).append(" "); // scan seq number
					lab = String.valueOf(Arrays.asList(listLabelXml).indexOf(listColumn.get(k)[6]));
					listImgNbrCal[7].append(lab).append(" "); // Label Type

				}

				listRSI[0].append(listColumn.get(k)[14]).append(" "); // RI
				listRSI[1].append(listColumn.get(k)[15]).append(" "); // RS
				listRSI[2].append(listColumn.get(k)[16]).append(" "); // SS
			}
			
			Collections.sort(listColumnAcq, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] strings, Object[] otherStrings) {
					return ((Integer) Integer.parseInt(strings[2].toString()))
							.compareTo(Integer.parseInt(otherStrings[2].toString()));
				}
			});
			Collections.sort(listColumnAcq, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] strings, Object[] otherStrings) {
					return ((Integer) Integer.parseInt(strings[0].toString()))
							.compareTo(Integer.parseInt(otherStrings[0].toString()));
				}
			});

//			System.out.println(file);
//			for (String[] kk : listColumnAcq)
//				System.out.println(kk[0]+" , "+kk[1]+" , "+kk[2]+" , "+kk[7]+" , "+kk[8]+" , "+kk[6]);
			
			Collections.sort(listColumnCal, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] strings, Object[] otherStrings) {
					return ((Integer) Integer.parseInt(strings[2].toString()))
							.compareTo(Integer.parseInt(otherStrings[2].toString()));
				}
			});
			Collections.sort(listColumnCal, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] strings, Object[] otherStrings) {
					return ((Integer) Integer.parseInt(strings[0].toString()))
							.compareTo(Integer.parseInt(otherStrings[0].toString()));
				}
			});

//			System.out.println(file);
//			for (String[] kk : listColumnCal)
//			System.out.println(kk[0]+" , "+kk[1]+" , "+kk[2]+" , "+kk[7]+" , "+kk[8]+" , "+kk[6]);

			/*****************************************************************************************************/

			StringBuffer[] imageInfoLine;

			if (ListPhilipsSequence.hasAcqImg) {
				offsetAcq = Integer.parseInt(listColumnAcq.get(listColumnAcq.size() / 2)[9]);
				imageInfoLine = new StringBuffer[imageInformationXmlRec.length];

				for (int i = 0; i < imageInfoLine.length; i++)
					imageInfoLine[i] = new StringBuffer("");

				for (int i = 0; i < listColumnAcq.size(); i++)
					for (int j = 0; j < imageInfoLine.length; j++) {
						StringBuffer tmp = new StringBuffer(
								listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1])]).append(" ");
						if (j == 12 || j == 24)
							tmp.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 1])
									.append("] [");
						else if (j == 42 || j == 45)
							tmp.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 1])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 2])
									.append("] [");
						else if (j == 39)
							tmp.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 1])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 2])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[29][1])])
									.append("] [");
						
						imageInfoLine[j].append(tmp);
					}

				for (int i = 0; i < imageInformationXmlRec.length; i++)
					informationXmlAcq.put(imageInformationXmlRec[i][0], deleteDuplicate(imageInfoLine[i].toString()));

			}

			/*****************************************************************************************************/
			if (ListPhilipsSequence.hasCalcImg) {
				offsetCal = Integer.parseInt(listColumnCal.get(listColumnCal.size() / 2)[9]);
				imageInfoLine = new StringBuffer[imageInformationXmlRec.length];
				for (int i = 0; i < imageInfoLine.length; i++)
					imageInfoLine[i] = new StringBuffer("");

				for (int i = 0; i < listColumnCal.size(); i++)
					for (int j = 0; j < imageInfoLine.length; j++) {
						StringBuffer tmp = new StringBuffer(
								listColumnCal.get(i)[Integer.parseInt(imageInformationXmlRec[j][1])]).append(" ");
						if (j == 12 || j == 24)
							tmp.append(listColumnCal.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 1])
									.append("] [");
						else if (j == 42 || j == 45)
							tmp.append(listColumnCal.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 1])
									.append(" ")
									.append(listColumnCal.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 2])
									.append("] [");
						else if (j == 39)
							tmp.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 1])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[j][1]) + 2])
									.append(" ")
									.append(listColumnAcq.get(i)[Integer.parseInt(imageInformationXmlRec[29][1])])
									.append("] [");

						imageInfoLine[j].append(tmp);

					}

				for (int i = 0; i < imageInformationXmlRec.length; i++)
					informationXmlCal.put(imageInformationXmlRec[i][0], deleteDuplicate(imageInfoLine[i].toString()));

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
		return informationXmlAcq;
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
		return informationXmlCal;
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