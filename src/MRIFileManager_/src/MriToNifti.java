import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import abstractClass.ListParam2;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import abstractClass.convertNifti;
import bids.ListBidsParam;
import brukerParavision.ConvertBrukerToNifti;
import brukerParavision.ListBrukerParam;
import dcm.ConvertDicomToNifti;
import exportFiles.ConvertImage2;
import exportFiles.GetListFieldFromFilestmpRep;
import exportFiles.ReplacecharForbidden;
import philips.ConvertPhilipsToNifti;
import philips.ListPhilipsParam;
import philips.ListPhilipsSequence;

public class MriToNifti extends PrefParam implements ParamMRI2 {

	public MriToNifti() {

		listinBasket.removeAllElements();
		listBasket_hmInfo.clear();
		listBasket_hmOrderImage.clear();
		listBasket_hmSeq.clear();
	}

	public void convertToNifti(String naming, String repWork, String constr) {

		for (String seq : hmSeq.keySet()) {
			listBasket_hmSeq.put(seq, hmSeq.get(seq));
			listBasket_hmInfo.put(seq, hmInfo.get(seq));
			listBasket_hmOrderImage.put(seq, hmOrderImage.get(seq));
			listinBasket.add(listinBasket.size(), seq);
		}

		convertNifti conv = null;
		String tmprepWork = "", repertoryExport = "";
		String[] lm = null;
		conv = new ConvertDicomToNifti();
		for (String seq : listBasket_hmSeq.keySet()) {
			System.out.print('\n' + listBasket_hmSeq.get(seq)[0] + '\n');
			if (listBasket_hmInfo.get(seq).get("Slice Orientation").split(" +").length == 1) {
			repertoryExport = replaceFieldNameNifti(naming, seq);
			lm = createRepertories(repWork, repertoryExport);
			tmprepWork = lm[0];
			repertoryExport = lm[1];
			repertoryExport = new ReplacecharForbidden().charReplace(repertoryExport);
			new ConvertImage2(0, 2, conv, seq, tmprepWork, repertoryExport, constr);
			}
			else
				System.out.println(" ..... not exported because number of orientations > 1");
		}
		conv = null;
	}

	public void convertToNifti(String file, String naming, String seqSel, String serialNumber, String repWork) {
		
		ListParam2 lv = null;
		String dim, nImage, repertoryExport = "", seqSel2 = "";
		boolean secondExport = false, multiOrientation = false;
		
		System.out.print('\n' + file + '\n');

//		listinBasket.removeAllElements();
//		listBasket_hmInfo.clear();
//		listBasket_hmOrderImage.clear();
//		listBasket_hmSeq.clear();

		try {
			if (formatCurrent.contains("Bruker")) {
				String directory;
				String[] field_file;
				if (file.endsWith("2dseq")) {
					field_file = file.split(PrefParam.separator);
					directory = field_file[field_file.length-5];
				}
				else
					directory = file.substring(file.lastIndexOf(PrefParam.separator)+1);
				lv = new ListBrukerParam(directory, file, seqSel, serialNumber);
				listBasket_hmInfo.put(seqSel, lv.ListParamValueAcq(""));
				dim = listBasket_hmInfo.get(seqSel).get("Scan Mode");
				if (dim.contains("3"))
					nImage = listBasket_hmInfo.get(seqSel).get("Scan Resolution").split(" +")[2];
				else
					nImage = listBasket_hmInfo.get(seqSel).get("Images In Acquisition");
				listBasket_hmOrderImage.put(seqSel, lv.ListOrderStackAcq(dim, nImage));

				if (listBasket_hmInfo.get(seqSel).get("Slice Orientation").split(" +").length > 1) {
					multiOrientation = true;
					String[] listOrientation = listBasket_hmInfo.get(seqSel).get("Slice Orientation").split(" +");
					String label = "", tmpseqSel = "";

					for (int d = 0; d < listOrientation.length; d++) {
						label = listOrientation[d];
						tmpseqSel = seqSel + "-" + label;
						HashMap<String, String> tmphmInfo = (HashMap<String, String>) listBasket_hmInfo.get(seqSel).clone();
						Object[] tmpObj = listBasket_hmOrderImage.get(seqSel).clone();

						int NumberImageByOrientation = Integer.parseInt(tmphmInfo.get("Number Of Slice"))
								/ tmphmInfo.get("Slice Orientation").split(" +").length;
						String tmp = tmphmInfo.get("Image Position Patient");
						tmp = String.join(" ", Arrays.copyOfRange(tmp.split(" +"), 3 * d * NumberImageByOrientation,
								3 * (d + 1) * NumberImageByOrientation));
						tmphmInfo.put("Image Position Patient", tmp);
						tmp = tmphmInfo.get("Image Orientation Patient");
						tmp = String.join(" ", Arrays.copyOfRange(tmp.split(" +"), 9 * d * NumberImageByOrientation,
								9 * (d + 1) * NumberImageByOrientation));
						tmphmInfo.put("Image Orientation Patient", tmp);

						tmp = tmphmInfo.get("Slice Separation");
						tmp = tmp.split(" +")[d];
						tmphmInfo.put("Slice Separation", tmp);

						tmp = tmphmInfo.get("Read Direction");
						tmp = tmp.split(" +")[d];
						tmphmInfo.put("Read Direction", tmp);

						tmp = tmphmInfo.get("Slice Orientation");
						tmp = tmp.split(" +")[d];
						tmphmInfo.put("Slice Orientation", tmp);

						tmp = tmphmInfo.get("Number Of Slice");
						tmp = String.valueOf(Integer.parseInt(tmp) / 3);
						tmphmInfo.put("Number Of Slice", tmp);

						tmpObj[4] = "multiOrientation";
						tmpObj[5] = d * NumberImageByOrientation;

						listBasket_hmInfo.put(tmpseqSel, (HashMap<String, String>) tmphmInfo);
						listBasket_hmOrderImage.put(tmpseqSel, tmpObj);
						listinBasket.add(listinBasket.size(), tmpseqSel);
					}
					listBasket_hmInfo.remove(seqSel);
					listBasket_hmOrderImage.remove(seqSel);
				} else
					listinBasket.add(listinBasket.size(), seqSel);

			} else if (formatCurrent.contains("Philips")) {
				seqSel2 = seqSel;
				lv = new ListPhilipsParam(file);
				if (ListPhilipsSequence.hasAcqImg && ListPhilipsSequence.hasCalcImg) {
					seqSel2 = seqSel + "b";
					secondExport = true;
				}
				if (ListPhilipsSequence.hasAcqImg) {
//					ListPhilipsSequence.noSeqCurrent = seqSel;
					listBasket_hmInfo.put(seqSel, lv.ListParamValueAcq(seqSel));
					listBasket_hmOrderImage.put(seqSel, lv.ListOrderStackAcq("", ""));
					listinBasket.add(listinBasket.size(), seqSel);
				}
				if (ListPhilipsSequence.hasCalcImg) {
//					ListPhilipsSequence.noSeqCurrentCal = seqSel2;
					listBasket_hmInfo.put(seqSel2, lv.ListParamValueCal(seqSel2));
					listBasket_hmOrderImage.put(seqSel2, lv.ListOrderStackCal("", ""));
					listinBasket.add(listinBasket.size(), seqSel2);
				}
			} else if (formatCurrent.contains("Bids")) {
				lv = new ListBidsParam(seqSel);
				listBasket_hmInfo.put(seqSel, lv.ListParamValueAcq(""));
				listBasket_hmOrderImage.put(seqSel, lv.ListOrderStackAcq("", ""));
				listinBasket.add(listinBasket.size(), seqSel);

			} else if (formatCurrent.contains("Dicom")) {
				System.out.println("dicom to nifti");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

//		if (listBasket_hmInfo.get(seqSel).get("Slice Orientation").split(" +").length == 1) {
		String tmprepWork = "";
//		System.out.print(file + " ..... ");
		if (!multiOrientation) {
			repertoryExport = replaceFieldNameNifti(naming, seqSel);
			String[] lm = createRepertories(repWork, repertoryExport);
			tmprepWork = lm[0];
			repertoryExport = lm[1];
			repertoryExport = new ReplacecharForbidden().charReplace(repertoryExport);
		}
		convertNifti conv = null;

		if (formatCurrent.contains("Bruker")) {
			if (multiOrientation) {
				for (Object ls : listinBasket.toArray()) {
					String tmpls = ls.toString();
					repertoryExport = replaceFieldNameNifti(naming, tmpls);
					String[] lm = createRepertories(repWork, repertoryExport);
					tmprepWork = lm[0];
					repertoryExport = lm[1];
					repertoryExport = new ReplacecharForbidden().charReplace(repertoryExport);
					repertoryExport += "-"+listBasket_hmInfo.get(tmpls).get("Slice Orientation");
					conv = new ConvertBrukerToNifti();
					new ConvertImage2(2, 1, conv, tmpls, tmprepWork, repertoryExport, "Bruker");
					conv = null;
				}
			} else {
				conv = new ConvertBrukerToNifti();
				new ConvertImage2(2, 1, conv, seqSel, tmprepWork, repertoryExport, "Bruker");
			}
		}

		else if (formatCurrent.contains("Philips")) {
			if (listBasket_hmInfo.get(seqSel).get("Slice Orientation").split(" +").length == 1) {
				conv = new ConvertPhilipsToNifti();
				new ConvertImage2(1, 1, conv, seqSel, tmprepWork, repertoryExport, "Philips");
				if (secondExport) {
					repertoryExport = replaceFieldNameNifti(naming, seqSel2);
					String[] lm2 = createRepertories(repWork, repertoryExport);
					tmprepWork = lm2[0];
					repertoryExport = lm2[1];
					repertoryExport = new ReplacecharForbidden().charReplace(repertoryExport);
					new ConvertImage2(1, 1, conv, seqSel2, tmprepWork, repertoryExport, "Philips");
				}
				conv = null;
			} else
				System.out.println(file + " ..... no exported because number of orientations > 1");
		}

		else if (formatCurrent.contains("Bids")) {
			if (listBasket_hmInfo.get(seqSel).get("Slice Orientation").split(" +").length == 1)
				new ConvertImage2(seqSel, tmprepWork, repertoryExport);
			else
				System.out.println(file + " ..... no exported because number of orientations > 1");
		}

//		} else
//			System.out.println(file + " ..... no exported because number of orientations > 1");
	}

	private String[] createRepertories(String pathRoot, String newRep) {

		String[] ls = new String[2];
		ls[0] = pathRoot;
		ls[1] = newRep;

		if (newRep.contains(separator)) {

			ls[1] = newRep.substring(newRep.lastIndexOf(separator));

			newRep = newRep.substring(0, newRep.lastIndexOf(separator));

			File f = new File(pathRoot + separator + newRep);

			if (!f.exists())
				f.mkdirs();

			ls[0] = pathRoot + separator + newRep;
		}

		return ls;
	}

	public String replaceFieldNameNifti(String namingFile, String seqSel) {

		String tmp = namingFile, nameNifti = "";
		GetListFieldFromFilestmpRep dh = null;
		
		if (tmp.contains(separator)) {
			tmp = tmp.substring(0, tmp.lastIndexOf(separator));
			tmp = tmp.replace(separator, "-");
			dh = new GetListFieldFromFilestmpRep(tmp);
			tmp = namingFile.substring(namingFile.lastIndexOf(separator) + 1);
		}
		
		GetListFieldFromFilestmpRep dg = new GetListFieldFromFilestmpRep(tmp);

		if (dh != null)
			for (String ef : dh.getListFieldTrue()) {
				if (ef != null) {
					tmp = listBasket_hmInfo.get(seqSel).get(ef);
					tmp = new ReplacecharForbidden().charReplace(tmp);
					nameNifti += tmp;
					nameNifti += separator;
				}
			}
		for (String gk : dg.getListFieldTrue()) {
			if (!gk.isEmpty()) {
				if (gk.contains("Seq Number")) {
					String tmpseqSel = seqSel;
					if (seqSel.contains("axial") || seqSel.contains("axial") || seqSel.contains("axial"))
						tmpseqSel = seqSel.substring(0, seqSel.indexOf("-"));
					nameNifti += tmpseqSel;
				}
				else {
					tmp = listBasket_hmInfo.get(seqSel).get(gk);
					tmp = new ReplacecharForbidden().charReplace(tmp);
					nameNifti += tmp;
				}
				nameNifti += dg.getSeparateChar();
			}
		}
		
		nameNifti = nameNifti.substring(0, nameNifti.lastIndexOf(dg.getSeparateChar()));

		return nameNifti;
	}

}