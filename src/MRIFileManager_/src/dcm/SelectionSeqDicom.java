package dcm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import MRIFileManager.FileManagerFrame;
import MRIFileManager.GetStackTrace;
import MRIFileManager.OpenImageJ;
import MRIFileManager.ShowImagePanel;
import MRIFileManager.ThumbnailList;
import MRIFileManager.TreeInfo2;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import abstractClass.SelectionSeq;
import exportFiles.FillBasketSingle;
import ij.IJ;

public class SelectionSeqDicom extends PrefParam implements ParamMRI2, SelectionSeq {

	private FileManagerFrame wind;
	private String seqSelected;

	public SelectionSeqDicom(FileManagerFrame wind) {
		this.wind = wind;
		seqSelected = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRow(), 0).toString();
	}

	@Override
	public void goSelectionSeq() {

		try {
			wind.getTreeInfoGeneral()
					.setModel(new TreeInfo2(listParamInfoSystem, hmInfo.get(seqSelected)).getTreeInfo().getModel());
			for (int j = 0; j < wind.getTreeInfoGeneral().getRowCount(); j++)
				wind.getTreeInfoGeneral().expandRow(j);
			wind.getTreeInfoUser()
					.setModel(new TreeInfo2(listParamInfoUser, hmInfo.get(seqSelected)).getTreeInfo().getModel());
			for (int j = 0; j < wind.getTreeInfoGeneral().getRowCount(); j++)
				wind.getTreeInfoUser().expandRow(j);
		} catch (Exception e1) {
			new GetStackTrace(e1, this.getClass().toString());
		}

		if (PrefParam.previewActived)
			try {
				new ShowImagePanel(wind, new OpenDicom(hmInfo.get(seqSelected), hmOrderImage.get(seqSelected),
						hmSeq.get(seqSelected), seqSelected, false).getImp(), seqSelected);
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
		else
			new ShowImagePanel(wind, null, seqSelected);

		String seqSel;

		if (wind.getTabSeq().getSelectedRowCount() == 1) {
			seqSel = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[0], 0).toString();
			ThumbnailList.list.setSelectedValue(seqSel, true);
		} else {
			int[] ls = new int[wind.getTabSeq().getSelectedRowCount()];
			for (int i = 0; i < wind.getTabSeq().getSelectedRowCount(); i++) {
				seqSel = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[i], 0).toString();
				for (int g = 0; g < ThumbnailList.list.getModel().getSize(); g++)
					if (ThumbnailList.list.getModel().getElementAt(g) == seqSel)
						ls[i] = g;
			}
			ThumbnailList.list.setSelectedIndices(ls);
		}

	}

	@Override
	public void popMenuSeq(JPopupMenu pm) {

		/*********************************************************************************/

		JMenuItem addBasketAnonym = new JMenuItem("Anonymize and add to basket");
		pm.add(addBasketAnonym);

		addBasketAnonym.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deidentify = true;
				fillBasket();
				deidentify = false;
			}
		});

		/*********************************************************************************/
		JMenuItem sampleOpen = new JMenuItem("Open image(s) Ctrl+o");
		pm.add(sampleOpen);

		sampleOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openImage();
			}
		});
		/*********************************************************************************/
		pm.addSeparator();

		JMenuItem showTags = new JMenuItem("see Header Dicom");
//		pm.add(showTags);

		showTags.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showParamFile(seqSelected.toString(), "see Header Dicom");
			}
		});
		/*********************************************************************************/
		pm.addSeparator();
		/*********************************************************************************/
		JMenuItem addBasket = new JMenuItem("Add to basket Ctrl+b");
		pm.add(addBasket);

		addBasket.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fillBasket();
			}
		});
	}

	@Override
	public void openImage() {
		new OpenImageJ();
		String titleDlg = "Opening image ";
		FileManagerFrame.dlg.setVisible(true);
		int numberOfRow = wind.getTabSeq().getSelectedRowCount();
		for (int i = 0; i < numberOfRow; i++) {
			FileManagerFrame.dlg.setTitle(titleDlg + " " + (100 * i / numberOfRow) + " %");
			String seqSelected = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[i], 0).toString();
			try {
				if (hmInfo.get(seqSelected).get("TypeOfView").contentEquals("simplified")) {
//				FileManagerFrame.dlg.setVisible(true);
//				FileManagerFrame.dlg.setTitle("Loading : file ");
					new ListDicomDirSequence2().listParamDicom(hmInfo.get(seqSelected).get("Serial Number"),
							seqSelected, false);
//				FileManagerFrame.dlg.setVisible(false);
				}
			} catch (Exception e) {
			}

			Thread t = null;
			try {
				t = new Thread(new OpenDicom(hmInfo.get(seqSelected), hmOrderImage.get(seqSelected),
						hmSeq.get(seqSelected), seqSelected, true));
				t.start();
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		FileManagerFrame.dlg.setVisible(false);
	}

	@Override
	public void showParamFile(String noSeq, String keyword) {
		String listTags;
		listTags = hmTagDicom.get(noSeq);
		if (listTags != null) {
			new OpenImageJ();
			IJ.log(listTags);
		}
	}

	@Override
	public void fillBasket() {
//		for (int i = 0; i < wind.getTabSeq().getSelectedRowCount(); i++) {
//			String seqSelected = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[i], 0).toString();
//			new ListDicomDirSequenceStreaming(seqSelected, 1);
//
//		}
		new FillBasketSingle(wind, "[Dicom] ");
	}
}