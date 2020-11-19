package exportFiles;

import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

import MRIFileManager.FileManagerFrame;
import MRIFileManager.ProtocolsBidsYaml;
import MRIFileManager.UtilsSystem;
import abstractClass.Format;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
//import MRIFileManager.GetStackTrace;
import dcm.ListDicomDirSequence2;

public class FillBasketSingle extends PrefParam implements ParamMRI2, Format {

	public FillBasketSingle(FileManagerFrame wind, String format) {

		String seqSel = null;
		String listInBask = "", listInBaskNifti = "", listInBaskBids = "";
		String nrep = namingRepNiftiExport;
		GetListFieldFromFilestmpRep dh = null;
		Boolean continuSave = false, noAll = false;
		Boolean Nifticase = wind.getChoiceExport().getSelectedItem().toString().contentEquals("Nifti-1");

		if (!nrep.contentEquals(separator)) {
			nrep = nrep.substring(1, nrep.length() - 1);
			nrep = nrep.replace(separator, "-");
			dh = new GetListFieldFromFilestmpRep(nrep);
		}

		GetListFieldFromFilestmpRep dg = new GetListFieldFromFilestmpRep(namingFileNiftiExport);

		boolean firstLoopDone = false;
		String[] fieldDeidentification = new String[4];
		String[] protoBids = null;

		for (int i = 0; i < wind.getTabSeq().getSelectedRowCount(); i++) {

			float sizeFileAfterExport = 1;
			listInBask = "";
			listInBaskNifti = "";
			listInBaskBids = "";
			seqSel = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[i], 0).toString();

			hasJsonKnown = true;
			hasMultiOrientationScanMode = false;
			is1d = false;

			if (deidentify && !firstLoopDone) {
				wind.setEnabled(false);
				WindowDeidentification wd = new WindowDeidentification(wind, "", hmInfo.get(seqSel).get("Patient Name"),
						hmInfo.get(seqSel).get("Study Name"), hmInfo.get(seqSel).get("Patient BirthDate"),
						hmInfo.get(seqSel).get("Patient Sex"), hmInfo.get(seqSel).get("Patient Weight"));
				wind.setEnabled(true);
				wind.setAlwaysOnTop(true);
				wind.setAlwaysOnTop(false);
				try {
					if (wd.answUser().contentEquals("ok"))
						fieldDeidentification = wd.getAnswer();
					else
						return;
				} catch (Exception e) {
					return;
				}
				firstLoopDone = true;
			}
			hmInfo.get(seqSel).put("DataAnonymized", "no");
			if (deidentify) {
				HashMap<String, String> tmpHas = hmInfo.get(seqSel);
				tmpHas.put("Patient Name", fieldDeidentification[0]);
				tmpHas.put("Study Name", fieldDeidentification[1]);
				tmpHas.put("Patient BirthDate", fieldDeidentification[2]);
				tmpHas.put("Patient Sex", fieldDeidentification[3]);
				tmpHas.put("Patient Weight", fieldDeidentification[4]);
				tmpHas.put("DataAnonymized", "yes");
			}

			if (formatCurrentInt == Nifti) {
				try {
					if (hmInfo.get(seqSel).get("JsonVersion").contentEquals("ok")) {

						if (hmInfo.get(seqSel).get("Slice Orientation").split(" +").length > 1)
							hasMultiOrientationScanMode = true;
						// OrientationMoreOne = true;

						else if (hmInfo.get(seqSel).get("Scan Mode").contentEquals("1D"))
							is1d = true;
						// ScanMode1D = true;
					} else
						hasJsonKnown = false;
				} catch (Exception e) {
					hasJsonKnown = false;
				}
			}

			else {
				if (hmInfo.get(seqSel).get("Slice Orientation").split(" +").length > 1)
					hasMultiOrientationScanMode = true;
			}

			if ((!hasMultiOrientationScanMode || formatCurrentInt == Bruker) && !is1d && (hasJsonKnown)) {

				if (dh != null)
					for (String ef : dh.getListFieldTrue()) {
						if (ef != null) {
							String tmp = hmInfo.get(seqSel).get(ef);
							tmp = new ReplacecharForbidden().charReplace(tmp);
							listInBaskNifti += tmp;
							listInBaskNifti += separator;
						}
					}

				for (String gk : dg.getListFieldTrue()) {
					if (gk.contains("Seq Number"))
						listInBaskNifti += seqSel;
					else {
						String tmp = hmInfo.get(seqSel).get(gk);
						tmp = new ReplacecharForbidden().charReplace(tmp);
						listInBaskNifti += tmp;
					}
					listInBaskNifti += dg.getSeparateChar();
				}

				listInBaskNifti = listInBaskNifti.substring(0, listInBaskNifti.lastIndexOf(dg.getSeparateChar()));

				/**************************************************
				 * get Structure Bids in term of Parameters IRM
				 **************************************************/
				String tmpPatient = hmInfo.get(seqSel).get("Patient Name"),
						tmpSerialNb = hmInfo.get(seqSel).get("Serial Number"),
						tmpProto = hmInfo.get(seqSel).get("Protocol"), tmpStudy = hmInfo.get(seqSel).get("Study Name");

				tmpPatient = new ReplacecharForBids().charReplace(tmpPatient);
				tmpSerialNb = new ReplacecharForBids().charReplace(tmpSerialNb);
				tmpProto = new ReplacecharForBids().charReplace(tmpProto);
				tmpProto = "acq-" + tmpSerialNb + tmpProto;
				tmpStudy = new ReplacecharForBids().charReplace(tmpStudy);
				tmpStudy = "sub-" + tmpStudy;

				protoBids = new ProtocolsBidsYaml(UtilsSystem.pathOfJar() + "Modalities_BIDS.yml", "Bruker", tmpProto)
						.getSetProtocol();

				listInBaskBids = tmpPatient + separator + tmpStudy + separator + protoBids[0] + separator + tmpStudy
						+ "_" + tmpProto;

				/********************************************************
				 * estimation size of file after exportation
				 *******************************************************/

				for (String hh : hmInfo.get(seqSel).get("Scan Resolution").split(" +"))
					sizeFileAfterExport *= Float.parseFloat(hh);

				sizeFileAfterExport *= 4 * Float.parseFloat(hmInfo.get(seqSel).get("Images In Acquisition"));
				sizeFileAfterExport /= (1024 * 1024);

			}

			else if (formatCurrentInt == Nifti) {

				listInBaskNifti = hmInfo.get(seqSel).get("File Name");
				listInBaskNifti = listInBaskNifti.replace(".nii", "");
				listInBaskNifti = new ReplacecharForbidden().charReplace(listInBaskNifti);
				sizeFileAfterExport = Float.parseFloat(hmInfo.get(seqSel).get("File Size (Mo)"));
			}

			if (Nifticase)
				listInBask = listInBaskNifti;
			else
				listInBask = listInBaskBids;

			/***************************************************************************************************/
			if (!listInBask.isEmpty()) {

				if (formatCurrentInt == Bruker && hmOrderImage.get(seqSel)[4] != null
						&& namingOptionsNiftiExport.substring(0, 1).contentEquals("1")) {
					if (!noAll) {
						String[] listInf = hmOrderImage.get(seqSel)[4].toString().split("\n");
						String descr = listInf[0].substring(0, listInf[0].indexOf(":"));
						descr = descr.trim().replaceAll(" +", "-");
						String label = "";
						for (int d = 1; d < listInf.length; d++) {
							String listInBaskMult = listInBask;
							label = listInf[d].substring(listInf[d].indexOf(" - ") + 3, listInf[d].indexOf(" ["));
							label = label.trim().replaceAll(" +", "-");

							hmInfo.get(seqSel).put("Sequence Name", label);

							if (!Nifticase) {
								listInBaskMult += new ReplacecharForBids().charReplace(descr)
										+ new ReplacecharForBids().charReplace(label) + "_" + protoBids[1];
							} else
								listInBaskMult += descr + "-" + label;
							listInBaskMult = String.format("%-15s %-" + (280 - listInBaskMult.length()) + "s %15s %n",
									format, listInBaskMult,
									"[ " + sizeFileAfterExport / (listInf.length - 1) + " Mo ]");

							int n = 0;

							if (listinBasket.contains(listInBaskMult) && !continuSave) {
								n = JOptionPane.showOptionDialog(null,
										listInBaskMult + "\n" + "This already exits, Do you want to overwrite it? ",
										"Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										new Object[] { "Yes", "Yes to all", "No", "No to all" }, "No");
							}

							if (n == 3)
								noAll = true;

							if (n == 1)
								continuSave = true;

							if (n == 0 || continuSave) {

								listinBasket.removeElement(listInBaskMult);
								listinBasket.add(listinBasket.size(), listInBaskMult);
								HashMap<String, String> tmphmInfo = (HashMap<String, String>) hmInfo.get(seqSel).clone();
								tmphmInfo.put("pathNifti", listInBaskNifti + descr + "-" + label);
								tmphmInfo.put("pathBids", listInBaskBids + new ReplacecharForBids().charReplace(descr)
										+ new ReplacecharForBids().charReplace(label) + "_" + protoBids[1]);
								listBasket_hmInfo.put(listInBaskMult, (HashMap<String, String>) tmphmInfo);
								listBasket_hmOrderImage.put(listInBaskMult, hmOrderImage.get(seqSel));
								if (format.contains("Dicom"))
									listBasket_hmSeq.put(listInBaskMult, hmSeq.get(seqSel));
							}
						}
					} // noAll
				}

				else if (formatCurrentInt == Bruker && hasMultiOrientationScanMode) {
					if (!noAll) {
						String[] listOrientation = hmInfo.get(seqSel).get("Slice Orientation").split(" +");
						String label = "";
						for (int d = 0; d < listOrientation.length; d++) {
							String listInBaskMult = listInBask;
							label = listOrientation[d];

							if (!Nifticase)
								listInBaskMult += label + "_" + protoBids[1];
							else
								listInBaskMult += "-" + label;

							listInBaskMult = String.format("%-15s %-" + (280 - listInBaskMult.length()) + "s %15s %n",
									format, listInBaskMult,
									"[ " + sizeFileAfterExport / (listOrientation.length - 1) + " Mo ]");

							int n = 0;

							if (listinBasket.contains(listInBaskMult) && !continuSave) {
								n = JOptionPane.showOptionDialog(null,
										listInBaskMult + "\n" + "This already exits, Do you want to overwrite it? ",
										"Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
										new Object[] { "Yes", "Yes to all", "No", "No to all" }, "No");
							}

							if (n == 3)
								noAll = true;

							if (n == 1)
								continuSave = true;

							if (n == 0 || continuSave) {
								listinBasket.removeElement(listInBaskMult);
								listinBasket.add(listinBasket.size(), listInBaskMult);

								HashMap<String, String> tmphmInfo = (HashMap<String, String>) hmInfo.get(seqSel).clone();

								tmphmInfo.put("pathNifti", listInBaskNifti + "-" + label);
								tmphmInfo.put("pathBids", listInBaskBids + new ReplacecharForBids().charReplace(label)
										+ "_" + protoBids[1]);

								int NumberImageByOrientation = Integer.parseInt(tmphmInfo.get("Number Of Slice"))
										/ tmphmInfo.get("Slice Orientation").split(" +").length;
								String tmp = tmphmInfo.get("Image Position Patient");
								tmp = String.join(" ", Arrays.copyOfRange(tmp.split(" +"),
										3 * d * NumberImageByOrientation, 3 * (d + 1) * NumberImageByOrientation));
								tmphmInfo.put("Image Position Patient", tmp);

								tmp = tmphmInfo.get("Image Orientation Patient");
								tmp = String.join(" ", Arrays.copyOfRange(tmp.split(" +"),
										9 * d * NumberImageByOrientation, 9 * (d + 1) * NumberImageByOrientation));
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

								// tmp = tmphmInfo.get("Images In Acquisition");
								// tmp = String.valueOf(Integer.parseInt(tmp)/3);
								// tmphmInfo.put("Images In Acquisition",tmp);

								tmp = tmphmInfo.get("Number Of Slice");
								tmp = String.valueOf(Integer.parseInt(tmp) / 3);
								tmphmInfo.put("Number Of Slice", tmp);

								Object[] tmpObj = hmOrderImage.get(seqSel).clone();
								tmpObj[4] = "multiOrientation";
								tmpObj[5] = d * NumberImageByOrientation;

								listBasket_hmInfo.put(listInBaskMult, (HashMap<String, String>) tmphmInfo);
								listBasket_hmOrderImage.put(listInBaskMult, tmpObj);
								// if (format.contains("Dicom"))
								// listBasket_hmSeq.put(listInBaskMult, hmSeq.get(seqSel));
							}
						}
					}
				}

				else {

					if (!noAll) {
						if (!Nifticase)
							listInBask += "_" + protoBids[1];
						listInBask = String.format("%-15s %-" + (280 - listInBask.length()) + "s %15s %n", format,
								listInBask, "[ " + sizeFileAfterExport + " Mo ]");

						int n = 0;

						if (listinBasket.contains(listInBask) && !continuSave) {
							n = JOptionPane.showOptionDialog(null,
									listInBask + "\n" + "This already exits, Do you want to overwrite it? ", "Warning",
									JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
									new Object[] { "Yes", "Yes to all", "No", "No to all" }, "No");
						}

						if (n == 3)
							noAll = true;

						if (n == 1)
							continuSave = true;

						if (n == 0 || continuSave) {
							listinBasket.removeElement(listInBask);
							listinBasket.add(listinBasket.size(), listInBask);
							try {
								if (hmInfo.get(seqSel).get("TypeOfView").contentEquals("simplified")) {
									FileManagerFrame.dlg.setVisible(true);
									FileManagerFrame.dlg.setTitle("Loading : file ");
									// new ListDicomDirSequence().listParamDicom(seqSel,
									// Integer.parseInt(hmInfo.get(seqSel).get("Indice of Frame")));
									new ListDicomDirSequence2().listParamDicom(hmInfo.get(seqSel).get("Serial Number"),
											seqSel, false);
									if (deidentify) {
										HashMap<String, String> tmpHas = hmInfo.get(seqSel);
										tmpHas.put("Patient Name", fieldDeidentification[0]);
										tmpHas.put("Study Name", fieldDeidentification[1]);
										tmpHas.put("Patient BirthDate", fieldDeidentification[2]);
										tmpHas.put("Patient Sex", fieldDeidentification[3]);
										tmpHas.put("Patient Weight", fieldDeidentification[4]);
										tmpHas.put("DataAnonymized", "yes");
									}
									FileManagerFrame.dlg.setVisible(false);
								}
							} catch (Exception e) {
								// new GetStackTrace(e);
							}
							HashMap<String, String> tmphmInfo = (HashMap<String, String>) hmInfo.get(seqSel).clone();
							tmphmInfo.put("pathNifti", listInBaskNifti);
							if (Nifticase && hasJsonKnown)
								tmphmInfo.put("pathBids", listInBaskBids + "_" + protoBids[1]);
							listBasket_hmInfo.put(listInBask, tmphmInfo);
							listBasket_hmOrderImage.put(listInBask, hmOrderImage.get(seqSel));
							if (format.contains("Dicom"))
								listBasket_hmSeq.put(listInBask, hmSeq.get(seqSel));
						}
					} // NoAll
				}
			}
		} // end for

		if (!listinBasket.isEmpty()) {
			wind.getListBasket().setModel(listinBasket);
			wind.getListBasket().updateUI();

			wind.getTreeBasket().setModel(new UpdateTreeBasket(listinBasket.toArray()).returnTreeModel());
			for (int i = 0; i < wind.getTreeBasket().getRowCount(); i++)
				wind.getTreeBasket().expandRow(i);
			// wind.getTreeBasket().updateUI();
			// wind.getTabbedPane().setSelectedIndex(1);
			String tmp = listinBasket.size() == 1 ? " file" : " files";
			wind.getTabbedPane().setTitleAt(1, "Basket " + "(" + listinBasket.size() + tmp + " - "
					+ new CalculTotalSizeBasket().getTotalSize() + " Mo )");
		}

		if (is1d)
			JOptionPane.showMessageDialog(wind,
					"Sequences with Number of Slice Orientation > 1 and Scan Mode = 1D will are not exported!",
					"Warning", JOptionPane.WARNING_MESSAGE);
	}
}
