package brukerParavision;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

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

public class SelectionSeqBruker extends PrefParam implements ParamMRI2, SelectionSeq {

	private FileManagerFrame wind;
	private String seqSelected;

	public SelectionSeqBruker(FileManagerFrame wind) {
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
		}

		catch (Exception e1) {
			new GetStackTrace(e1);
		}

		if (PrefParam.previewActived)
			new ShowImagePanel(wind,
					new OpenBruker(hmInfo.get(seqSelected), hmOrderImage.get(seqSelected), false, false, seqSelected)
							.getImp(),
					seqSelected);
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
		String[] listShowParam = { "see 'method' file", "see 'acqp' file", "see 'reco' file", "see 'visu_pars' file" };

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
		JMenuItem openParamFile;

		for (final String df : listShowParam) {
			openParamFile = new JMenuItem(df);
			openParamFile.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showParamFile(hmSeq.get(seqSelected)[0], df);

				}
			});

			pm.add(openParamFile);
		}
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
			FileManagerFrame.dlg.setTitle(titleDlg+" "+ (100 * i / numberOfRow)+" %");
			String seqSelected = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[i], 0).toString();
			// new FillHmsBruker(seqSelected);
			// new OpenBruker(hmInfo.get(seqSelected),
			// hmOrderImage.get(seqSelected), true, false, seqSelected);
			new OpenBruker(hmInfo.get(seqSelected), hmOrderImage.get(seqSelected), true, false, seqSelected);
		}
		FileManagerFrame.dlg.setVisible(false);
	}

	@Override
	public void showParamFile(String chemReco, String keyword) {

		String pathFile = null;
		String tm = "";
		boolean fileOk = false;

		chemReco = chemReco.substring(0, chemReco.lastIndexOf(PrefParam.separator) + 1);

		if (keyword.contains("visu_pars")) {
			pathFile = chemReco + "visu_pars";
			tm = "\n******************************** Seq no " + seqSelected
					+ " - visu_pars **********************************************" + "\n";
		}

		if (keyword.contains("reco")) {
			pathFile = chemReco + "reco";
			tm = "\n******************************** Seq no " + seqSelected
					+ " - reco ***************************************************" + "\n";
		}

		if (keyword.contains("acqp")) {
			pathFile = chemReco.substring(0, chemReco.indexOf("pdata")) + "acqp";
			tm = "\n******************************** Seq no " + seqSelected
					+ " - acqp ***************************************************" + "\n";
		}

		if (keyword.contains("method")) {
			pathFile = chemReco.substring(0, chemReco.indexOf("pdata")) + "method";
			tm = "\n******************************** Seq no " + seqSelected
					+ " - method *************************************************" + "\n";
		}

		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(pathFile));
			StringWriter out = new StringWriter();
			int b;
			while ((b = in.read()) != -1)
				out.write(b);
			out.flush();
			out.close();
			in.close();
			tm = tm + out.toString();
			fileOk = true;

		} catch (IOException e) {
			new GetStackTrace(e);
		}

		if (fileOk) {
			new OpenImageJ();
			IJ.log(tm);
		}
	}

	@Override
	public void fillBasket() {
		new FillBasketSingle(wind, "[Bruker] ");
	}
}