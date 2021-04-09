package philips;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import ij.ImagePlus;

public class SelectionSeqPhilips extends PrefParam implements ParamMRI2, SelectionSeq {

	private FileManagerFrame wind;
	private String seqSelected;

	public SelectionSeqPhilips(FileManagerFrame wind) {
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
			for (int j = 0; j < wind.getTreeInfoUser().getRowCount(); j++)
				wind.getTreeInfoUser().expandRow(j);
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		if (PrefParam.previewActived)
			try {
				OpenPhilips4 op = new OpenPhilips4(hmInfo.get(seqSelected), hmOrderImage.get(seqSelected), false, false,
						seqSelected);
				ImagePlus ip = op.getImp();
				new ShowImagePanel(wind, ip, seqSelected);
				ip.close();
				ip = null;
				op = null;
				System.gc();
			} catch (Exception e) {
				URL imgURLError = getClass().getResource("/errorFile.jpg");
				new ShowImagePanel(wind, new ImagePlus("", new ImageIcon(imgURLError).getImage()), seqSelected);
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
		/*********************************************************************************/
		JMenuItem openParamFile = new JMenuItem("see PAR/Xml file");
		pm.add(openParamFile);
		openParamFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showParamFile(hmSeq.get(seqSelected)[0], "see PAR/Xml file");
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
			// new FillHmsPhilips(seqSelected);
			try {
				new OpenPhilips4(hmInfo.get(seqSelected), hmOrderImage.get(seqSelected), true, false, seqSelected);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				new GetStackTrace(e, this.getClass().toString());
			}
		}
		FileManagerFrame.dlg.setVisible(false);
	}

	@Override
	public void showParamFile(String chemPar, String keyword) {

		String pathFile = null;

		if (keyword.contentEquals("see PAR/Xml file")) {
			
			String[] listExt = {".PAR", ".par", ".xml", ".XML"};
			
			for (String es : listExt) {
				pathFile = chemPar.replace(".REC", es);
				pathFile = pathFile.replace(".rec", es);
				if (new File(pathFile).exists())
					break;
			}

			if (!new File(pathFile).exists()) {
				JOptionPane.showMessageDialog(wind, "PAR/Xml doesn't exist");
				return;
			}
		}
		
		new OpenImageJ();
		IJ.open(pathFile);
//		try {
//			BufferedInputStream in = new BufferedInputStream(new FileInputStream(pathFile));
//			StringWriter out = new StringWriter();
//			int b;
//			while ((b = in.read()) != -1)
//				out.write(b);
//			out.flush();
//			out.close();
//			in.close();
//			tm = tm + out.toString();
//
//			fileOk = true;
//
//		} catch (Exception e) {
//			new GetStackTrace(e, this.getClass().toString());
//		}
//
//		if (fileOk) {
//			new OpenImageJ();
//			IJ.open(tm);
//		}
	}

	@Override
	public void fillBasket() {
		new FillBasketSingle(wind, "[Philips] ");
	}
}