package exportFiles;

import javax.swing.JOptionPane;

import MRIFileManager.FileManagerFrame;
import abstractClass.Format;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class FillBasketSingle_origin extends PrefParam implements ParamMRI2, Format {

	public FillBasketSingle_origin(FileManagerFrame wind, String format) {

		String seqSel = null;
		String listInBask = "";
		String nrep = namingRepNiftiExport;
		GetListFieldFromFilestmpRep dh = null;

		// boolean OrientationMoreOne = false, ScanMode1D = false;

		if (!nrep.contentEquals(separator)) {
			nrep = nrep.substring(1, nrep.length() - 1);
			nrep = nrep.replace(separator, "-");
			dh = new GetListFieldFromFilestmpRep(nrep);
		}

		GetListFieldFromFilestmpRep dg = new GetListFieldFromFilestmpRep(namingFileNiftiExport);

		for (int i = 0; i < wind.getTabSeq().getSelectedRowCount(); i++) {

			float sizeFileAfterExport = 1;
			listInBask = "";
			seqSel = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[i], 0).toString();

			hasJsonKnown = true;
			hasMultiOrientationScanMode = false;

			if (formatCurrentInt == Nifti) {

				if (hmInfo.get(seqSel).get("JsonVersion").contentEquals("ok")) {

					if (hmInfo.get(seqSel).get("Slice Orientation").split(" +").length > 1)
						hasMultiOrientationScanMode = true;
					// OrientationMoreOne = true;

					else if (hmInfo.get(seqSel).get("Scan Mode").contentEquals("1D"))
						is1d = true;
					// ScanMode1D = true;
				} else
					hasJsonKnown = false;
			}

			else {
				if (hmInfo.get(seqSel).get("Slice Orientation").split(" +").length > 1)
					hasMultiOrientationScanMode = true;
			}

			if (!hasMultiOrientationScanMode && !is1d && (hasJsonKnown)) {

				if (dh != null)
					for (String ef : dh.getListFieldTrue()) {
						if (ef != null) {
							String tmp = hmInfo.get(seqSel).get(ef);
							tmp = new ReplacecharForbidden().charReplace(tmp);
							listInBask += tmp;
							listInBask += separator;
						}
					}

				for (String gk : dg.getListFieldTrue()) {
					if (gk.contains("Seq. n�"))
						listInBask += seqSel;
					else {
						String tmp = hmInfo.get(seqSel).get(gk);
						tmp = new ReplacecharForbidden().charReplace(tmp);
						listInBask += tmp;
					}
					listInBask += dg.getSeparateChar();
				}

				listInBask = listInBask.substring(0, listInBask.lastIndexOf(dg.getSeparateChar()));

				/********************************************************
				 * estimation size of file after exportation
				 *******************************************************/

				for (String hh : hmInfo.get(seqSel).get("Scan Resolution").split(" +"))
					sizeFileAfterExport *= Float.parseFloat(hh);

				sizeFileAfterExport *= 4 * Float.parseFloat(hmInfo.get(seqSel).get("Images In Acquisition"));
				sizeFileAfterExport /= (1024 * 1024);

			}

			// for export with multi orientation (1 file exported by orientation)
			
//			else if (hasMultiOrientationScanMode && !is1d && (hasJsonKnown)) {
//				
//				for (String orient : hmInfo.get(seqSel).get("Slice Orientation").split(" +")) {
//
//					if (dh != null)
//						for (String ef : dh.getListFieldTrue()) {
//							if (ef != null) {
//								String tmp = hmInfo.get(seqSel).get(ef);
//								tmp = new ReplacecharForbidden().charReplace(tmp);
//								listInBask += tmp;
//								listInBask += separator;
//							}
//						}
//
//					for (String gk : dg.getListFieldTrue()) {
//						if (gk.contains("Seq. n�"))
//							listInBask += seqSel;
//						else {
//							String tmp = hmInfo.get(seqSel).get(gk);
//							tmp = new ReplacecharForbidden().charReplace(tmp);
//							listInBask += tmp;
//						}
//						listInBask += dg.getSeparateChar();
//					}
//
//					listInBask = listInBask.substring(0, listInBask.lastIndexOf(dg.getSeparateChar()));
//					System.out.println(listInBask);
//
//					/********************************************************
//					 * estimation size of file after exportation
//					 *******************************************************/
//
//					for (String hh : hmInfo.get(seqSel).get("Scan Resolution").split(" +"))
//						sizeFileAfterExport *= Float.parseFloat(hh);
//
//					sizeFileAfterExport *= 4 * Float.parseFloat(hmInfo.get(seqSel).get("Images In Acquisition"));
//					sizeFileAfterExport /= (1024 * 1024);
//				}
//			}

			else {

				listInBask = hmInfo.get(seqSel).get("File Name");
				listInBask = listInBask.replace(".nii", "");
				listInBask = new ReplacecharForbidden().charReplace(listInBask);
				sizeFileAfterExport = Float.parseFloat(hmInfo.get(seqSel).get("File Size (Mo)"));

			}

			/***************************************************************************************************/

			listInBask = String.format("%-15s %-" + (300 - listInBask.length()) + "s %15s %n", format, listInBask,
					"[ " + sizeFileAfterExport + " Mo ]");

			int n = 0;

			if (listinBasket.contains(listInBask)) {
				n = JOptionPane.showOptionDialog(null,
						listInBask + "\n" + "This already exits, Do you want to overwrite it? ", "Warning",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new Object[] { "Yes", "No" }, "No");
			}

			if (n == 0) {
				listinBasket.removeElement(listInBask);
				listinBasket.add(listinBasket.size(), listInBask);
				listBasket_hmInfo.put(listInBask, hmInfo.get(seqSel));
				listBasket_hmOrderImage.put(listInBask, hmOrderImage.get(seqSel));
				if (format.contains("Dicom"))
					listBasket_hmSeq.put(listInBask, hmSeq.get(seqSel));
			}

		}

		if (hasMultiOrientationScanMode || is1d)
			JOptionPane.showMessageDialog(wind,
					"Sequences with Number of Slice Orientation > 1 and Scan Mode = 1D will are not exported!",
					"Warning", JOptionPane.WARNING_MESSAGE);

		wind.getListBasket().setModel(listinBasket);
		wind.getListBasket().updateUI();
		// wind.getTabbedPane().setSelectedIndex(1);

		String tmp = listinBasket.size() == 1 ? " file" : " files";
		wind.getTabbedPane().setTitleAt(1, "Basket " + "(" + listinBasket.size() + tmp + " - "
				+ new CalculTotalSizeBasket().getTotalSize() + " Mo )");
	}
}