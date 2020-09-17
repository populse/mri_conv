import java.io.File;
import java.io.IOException;

import abstractClass.ListParam2;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import abstractClass.convertNifti;
import bids.ListBidsParam;
import brukerParavision.ConvertBrukerToNifti;
import brukerParavision.ListBrukerParam;
import exportFiles.ConvertImage2;
import exportFiles.GetListFieldFromFilestmpRep;
import exportFiles.ReplacecharForbidden;
import philips.ConvertPhilipsToNifti;
import philips.ListPhilipsParam;
import philips.ListPhilipsSequence;

public class MriToNifti extends PrefParam implements ParamMRI2 {

	public MriToNifti() {
	}

	public void convertToNifti(String file, String naming, String seqSel, String serialNumber, String repWork) {

		ListParam2 lv = null;
		String dim, nImage, repertoryExport, seqSel2 = "";
		boolean secondExport = false;

		listinBasket.removeAllElements();
		listBasket_hmInfo.clear();
		listBasket_hmOrderImage.clear();
		listBasket_hmSeq.clear();

		try {
			if (formatCurrent.contains("Bruker")) {
				lv = new ListBrukerParam(file, seqSel, serialNumber);
				listBasket_hmInfo.put(seqSel, lv.ListParamValueAcq(""));
				dim = listBasket_hmInfo.get(seqSel).get("Scan Mode");
				if (dim.contains("3"))
					nImage = listBasket_hmInfo.get(seqSel).get("Scan Resolution").split(" +")[2];
				else
					nImage = listBasket_hmInfo.get(seqSel).get("Images In Acquisition");
				listBasket_hmOrderImage.put(seqSel, lv.ListOrderStackAcq(dim, nImage));
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
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (listBasket_hmInfo.get(seqSel).get("Slice Orientation").split(" +").length == 1) {
			String tmprepWork;
			System.out.print(file + " ..... ");
			repertoryExport = replaceFieldNameNifit(naming, seqSel);
			String[] lm = createRepertories(repWork, repertoryExport);
			tmprepWork = lm[0];
			repertoryExport = lm[1];
			repertoryExport = new ReplacecharForbidden().charReplace(repertoryExport);
			convertNifti conv = null;
			if (formatCurrent.contains("Bruker")) {
				conv = new ConvertBrukerToNifti();
				new ConvertImage2(2, 1, conv, seqSel, tmprepWork, repertoryExport);
			} else if (formatCurrent.contains("Philips")) {
				conv = new ConvertPhilipsToNifti();
				new ConvertImage2(1, 1, conv, seqSel, tmprepWork, repertoryExport);
				if (secondExport) {
					System.out.print(file + " (2) ..... ");
					repertoryExport = replaceFieldNameNifit(naming, seqSel2);
					String[] lm2 = createRepertories(repWork, repertoryExport);
					tmprepWork = lm2[0];
					repertoryExport = lm2[1];
					repertoryExport = new ReplacecharForbidden().charReplace(repertoryExport);
					new ConvertImage2(1, 1, conv, seqSel2, tmprepWork, repertoryExport);
				}
			}
			else if (formatCurrent.contains("Bids")) {
				new ConvertImage2(seqSel, tmprepWork, repertoryExport);
			}

		} else
			System.out.println(file + " ..... no exported because number of orientations > 1");
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

	public String replaceFieldNameNifit(String namingFile, String seqSel) {

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
				if (gk.contains("Seq Number"))
					nameNifti += seqSel;
				else {
					tmp = listBasket_hmInfo.get(seqSel).get(gk);
					tmp = new ReplacecharForbidden().charReplace(tmp);
					nameNifti += tmp;
				}
				nameNifti += dg.getSeparateChar();
			}
		}
		return nameNifti;
	}
}