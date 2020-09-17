package abstractClass;

import java.awt.Cursor;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.TableRowSorter;

import MRIFileManager.ChangeSeqDetail;
import MRIFileManager.FileManagerFrame;
import MRIFileManager.GetStackTrace;
import MRIFileManager.TableModel;
import MRIFileManager.ThumbnailList;
import MRIFileManager.TreeInfo2;
import bids.ListBidsSequence;
import bids.SearchBids;
import bids.TableBidsSequence;
import brukerParavision.ListBrukerSequence;
import brukerParavision.Search2dseq;
import brukerParavision.TableBrukerSequence;
import dcm.ListDcmSequence;
import dcm.ListDicomDirSequence2;
import dcm.ListDicomDirSequenceSimplified;
import dcm.ListDirfileSequence;
import dcm.TableDicomSequence;
import exportFiles.FillBasketMultiple;
import nifti.ListNiftiSequence;
import nifti.SearchNifti;
import nifti.TableNiftiSequence;
import philips.ListPhilipsSequence;
import philips.SearchPhilips;
import philips.TablePhilipsSequence;

public abstract class SelectionData extends PrefParam implements ParamMRI2, Format {

	public abstract void popMenuData(JPopupMenu popMenu);

	public abstract void popMenuDataExport(JPopupMenu popMenuExport);

	public void goSelectionData(FileManagerFrame wind) {
		wind.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Object[][] seq = null;

		hmSeq.clear();
		hmInfo.clear();
		hmOrderImage.clear();

		wind.getBoxThumb().removeAll();
		wind.getBoxThumb().updateUI();
		wind.getScrollThumb().updateUI();
		wind.getBoxImage().removeAll();
		wind.getPreview().updateUI();
		wind.resetTabSeq();

		wind.getTreeInfoGeneral().setModel(new TreeInfo2(ParamMRI2.listParamInfoSystem, null).getTreeInfo().getModel());
		for (int j = 0; j < wind.getTreeInfoGeneral().getRowCount(); j++)
			wind.getTreeInfoGeneral().expandRow(j);

		wind.getTreeInfoUser().setModel(new TreeInfo2(ParamMRI2.listParamInfoUser, null).getTreeInfo().getModel());
		for (int j = 0; j < wind.getTreeInfoUser().getRowCount(); j++)
			wind.getTreeInfoUser().expandRow(j);

		try {
			wind.getTabData().getValueAt(0, 1).toString();
		} catch (Exception e) {
			new GetStackTrace(e);
			return;
		}

		// if (wind.getTabData().getValueAt(0, 1).toString().isEmpty()) {
		try {

			switch (formatCurrentInt) {

			case Bruker:
				seq = new TableBrukerSequence(wind.getListPath().getSelectedItem().toString().substring(12) + separator
						+ wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 1)).getSequence();
				break;

			case Philips:
				if (DirectoryDataOnly.isEmpty())
					seq = new TablePhilipsSequence(wind.getListPath().getSelectedItem().toString().substring(12)
							+ separator + wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 1))
									.getSequence();
				else
					seq = new TablePhilipsSequence(wind.getListPath().getSelectedItem().toString().substring(12)).getSequence();
				break;

			case Dicom:
				seq = new TableDicomSequence(
						wind.getListPath().getSelectedItem().toString().substring(12) + separator
								+ wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 1),
						wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 8).toString()).getSequence();
				break;

			case Nifti:
				seq = new TableNiftiSequence(wind.getListPath().getSelectedItem().toString().substring(12) + separator
						+ wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 1)).getSequence();
				break;

			case Bids:
				seq = new TableBidsSequence(wind.getListPath().getSelectedItem().toString().substring(12),
						wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 2).toString(),
						wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 3).toString()).getSequence();
				break;
			}

		} catch (Exception e) {
			new GetStackTrace(e);
		}

		TableModel model = new TableModel(seq, ParamMRI2.headerListSeq);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
		wind.getTabSeq().setModel(model);
		wind.getTabSeq().setRowSorter(sorter);
		wind.getTabSeq().setEnabled(true);

		wind.getTabSeq().getRowSorter().toggleSortOrder(0);
		new ChangeSeqDetail(wind);
		for (int i = 0; i < wind.getTabSeq().getColumnCount(); i++) {
			wind.getTabSeq().getColumnModel().getColumn(i).setPreferredWidth(listWidthColumn[i]);
			wind.getTabSeq().getColumnModel().getColumn(i).setWidth(listWidthColumn[i]);
		}
		wind.getTabSeq().updateUI();

		// wind.getTreeInfoGeneral().setModel(new
		// TreeInfo2(ParamMRI2.listParamInfoSystem, null).getTreeInfo().getModel());
		// for (int j = 0; j < wind.getTreeInfoGeneral().getRowCount(); j++)
		// wind.getTreeInfoGeneral().expandRow(j);
		//
		// wind.getTreeInfoUser().setModel(new TreeInfo2(ParamMRI2.listParamInfoUser,
		// null).getTreeInfo().getModel());
		// for (int j = 0; j < wind.getTreeInfoUser().getRowCount(); j++)
		// wind.getTreeInfoUser().expandRow(j);

		// hmInfo.clear();
		// hmOrderImage.clear();

		wind.getTabSeq().setRowSelectionInterval(0, 0);

//		Thread t = new Thread(new ThumbnailList(wind));
//		t.start();
//		try {
//			t.join();
//		} catch (InterruptedException e) {
//			new GetStackTrace(e);
//		}

		new ThumbnailList(wind);

		wind.getTabData().setFocusCycleRoot(true);
	}

	public void exportMultiSelectionData(FileManagerFrame wind) {

		List<String> listSeq = null;
		String path, direct = "";

		// JOptionPane.showMessageDialog(wind,
		// "Sequences with Number of Slice Orientation > 1 and Scan Mode = 1D will not
		// be exported !", "Warning",
		// JOptionPane.WARNING_MESSAGE);
		int cnt = 0;
		FileManagerFrame.dlg.setVisible(true);
		String title = "Loading : basket ";
		hasMultiOrientationScanMode = false;
		is1d = false;
		switch (formatCurrentInt) {
		case Bruker:
			for (int hh : wind.getTabData().getSelectedRows()) {
				FileManagerFrame.dlg.setTitle(title + cnt * 100 / wind.getTabData().getSelectedRows().length + " %");
				hmSeq.clear();
				hmInfo.clear();
				hmOrderImage.clear();
				direct = wind.getTabData().getValueAt(hh, 1).toString();
				path = wind.getListPath().getSelectedItem().toString() + separator + direct;
				path = path.substring(12);
				listSeq = new Search2dseq(path).getList2dseq();

				for (int ii = 0; ii < listSeq.size(); ii++)
					try {
						new ListBrukerSequence(listSeq.get(ii), String.valueOf(ii)).ListSeqBruker();
					} catch (IOException e) {
						hmInfo.clear();
					}
				if (!hmInfo.isEmpty())
					new FillBasketMultiple(wind, "[Bruker] ", direct);
				cnt++;
			}
			break;

		case Philips:
			for (int hh : wind.getTabData().getSelectedRows()) {
				FileManagerFrame.dlg.setTitle(title + cnt * 100 / wind.getTabData().getSelectedRows().length + " %");
				hmSeq.clear();
				hmInfo.clear();
				hmOrderImage.clear();
				if (DirectoryDataOnly.isEmpty())
					direct = separator + wind.getTabData().getValueAt(hh, 1).toString();
				path = wind.getListPath().getSelectedItem().toString() + direct;
				path = path.substring(12);
				listSeq = new SearchPhilips(path).getList();
				String prefixSeq = ("000000").substring(0, String.valueOf(listSeq.size()).length());
				int iterNoSeq = 0;
				for (int ii = 0; ii < listSeq.size(); ii++)
					try {
						new ListPhilipsSequence(path + separator + listSeq.get(ii),
								(prefixSeq + iterNoSeq).substring(String.valueOf(iterNoSeq).length()))
										.ListSeqPhilipsAcq();
						if (ListPhilipsSequence.hasAcqImg) {
							iterNoSeq++;
						}
						if (ListPhilipsSequence.hasCalcImg) {
							iterNoSeq++;
						}
					} catch (IOException e) {
						hmInfo.clear();
					}
				if (!hmInfo.isEmpty())
					new FillBasketMultiple(wind, "[Philips] ", direct);
				cnt++;
			}
			break;

		case Nifti:
			for (int hh : wind.getTabData().getSelectedRows()) {
				FileManagerFrame.dlg.setTitle(title + cnt * 100 / wind.getTabData().getSelectedRows().length + " %");
				hmSeq.clear();
				hmInfo.clear();
				hmOrderImage.clear();
				direct = wind.getTabData().getValueAt(hh, 1).toString();
				path = wind.getListPath().getSelectedItem().toString() + separator + direct;
				path = path.substring(12);
				listSeq = new SearchNifti(path).getList();
				String prefixSeq = ("000000").substring(0, String.valueOf(listSeq.size()).length());

				for (int ii = 0; ii < listSeq.size(); ii++)
					try {
						new ListNiftiSequence(path + separator + listSeq.get(ii),
								(prefixSeq + ii).substring(String.valueOf(ii).length())).ListSeqNifti();
					} catch (IOException e) {
						hmInfo.clear();
					}
				if (!hmInfo.isEmpty())
					new FillBasketMultiple(wind, "[Nifti] ", direct);
				cnt++;
			}
			break;

		case Bids:
			for (int hh : wind.getTabData().getSelectedRows()) {
				FileManagerFrame.dlg.setTitle(title + cnt * 100 / wind.getTabData().getSelectedRows().length + " %");
				hmSeq.clear();
				hmInfo.clear();
				hmOrderImage.clear();
				direct = wind.getTabData().getValueAt(hh, 2).toString() + separator
						+ wind.getTabData().getValueAt(hh, 3).toString();
				path = wind.getListPath().getSelectedItem().toString() + separator + direct;
				path = path.substring(12);
				listSeq = new SearchBids(path).getList();
				String prefixSeq = ("000000").substring(0, String.valueOf(listSeq.size()).length());

				for (int ii = 0; ii < listSeq.size(); ii++)
					try {
						new ListBidsSequence(
								new String[] { wind.getTabData().getValueAt(hh, 2).toString(),
										wind.getTabData().getValueAt(hh, 3).toString() },
								listSeq.get(ii), (prefixSeq + ii).substring(String.valueOf(ii).length())).ListSeqBids();
					} catch (IOException e) {
						hmInfo.clear();
					}
				if (!hmInfo.isEmpty())
					new FillBasketMultiple(wind, "[Bids] ", direct);
				cnt++;
			}
			break;

		case Dicom:
			for (int hh : wind.getTabData().getSelectedRows()) {
				FileManagerFrame.dlg.setTitle(title + cnt * 100 / wind.getTabData().getSelectedRows().length + " %");
				hmSeq.clear();
				hmInfo.clear();
				hmOrderImage.clear();
				direct = wind.getTabData().getValueAt(hh, 1).toString();
				path = wind.getListPath().getSelectedItem().toString() + separator + direct;
				path = path.substring(12);
				String formatDicom = wind.getTabData().getValueAt(hh, 8).toString();

				try {
//					switch (formatDicom) {
//					case "DIRFILE":
//						new ListDirfileSequence(path);
//						break;
//
//					case "DICOMDIR":
//						new ListDicomDirSequence(path);
//						break;
//
//					case "DCM":
//						new ListDcmSequence(path);
//						break;
//					}
					if (formatDicom.contains("DIRFILE"))
						new ListDirfileSequence(path);
					else if (formatDicom.contains("DICOMDIR")) {
						if (simplifiedViewDicom)
							new ListDicomDirSequenceSimplified(path);
						else
							new ListDicomDirSequence2(path);
					} else if (formatDicom.contains("DCM"))
						new ListDcmSequence(path);
				} catch (Exception e) {
					hmInfo.clear();
				}
				if (!hmInfo.isEmpty())
					new FillBasketMultiple(wind, "[Dicom] ", direct);
				cnt++;
			}
			break;

		}

		FileManagerFrame.dlg.setVisible(false);

		if (hasMultiOrientationScanMode || is1d)
			JOptionPane.showMessageDialog(wind,
					"Sequences with Number of Slice Orientation > 1 and Scan Mode = 1D will are not exported!",
					"Warning", JOptionPane.WARNING_MESSAGE);

		//
		// for (int hh : wind.getTabData().getSelectedRows())
		// System.out.println(wind.getListPath().getSelectedItem().toString()+separator+wind.getTabData().getValueAt(hh,
		// 1));
	}
}