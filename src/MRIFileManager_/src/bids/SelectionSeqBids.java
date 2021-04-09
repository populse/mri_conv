package bids;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import MRIFileManager.ExtractTxtfromFile;
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
import nifti.Nifti1Dataset;

public class SelectionSeqBids extends PrefParam implements ParamMRI2, SelectionSeq {

	private FileManagerFrame wind;
	private String seqSelected;

	public SelectionSeqBids(FileManagerFrame wind) {
		this.wind = wind;
		seqSelected = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRow(), 0).toString();
	}

	@Override
	public void goSelectionSeq() throws Exception {

		try {
			wind.getTreeInfoGeneral()
					.setModel(new TreeInfo2(listParamInfoSystem, hmInfo.get(seqSelected)).getTreeInfo().getModel());
			for (int j = 0; j < wind.getTreeInfoGeneral().getRowCount(); j++)
				wind.getTreeInfoGeneral().expandRow(j);
			wind.getTreeInfoUser()
					.setModel(new TreeInfo2(listParamInfoUser, hmInfo.get(seqSelected)).getTreeInfo().getModel());
			for (int j = 0; j < wind.getTreeInfoUser().getRowCount(); j++)
				wind.getTreeInfoUser().expandRow(j);
		} catch (Exception e1) {
			new GetStackTrace(e1, this.getClass().toString());
		}

		if (PrefParam.previewActived)
			new ShowImagePanel(wind, new OpenBids(seqSelected, false, seqSelected).getImp(), seqSelected);
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

		JMenuItem openParamFile = new JMenuItem("see Nifti header");
		pm.add(openParamFile);
		openParamFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showParamFile(hmSeq.get(seqSelected)[0], "header");
			}
		});

		JMenuItem seeJsonFile = new JMenuItem("see Json file");
		pm.add(seeJsonFile);
		seeJsonFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showParamFile(hmSeq.get(seqSelected)[0], "see Json file");
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
			FileManagerFrame.dlg.setTitle(titleDlg+" "+ (100 * i / numberOfRow)+" %");
			String seqSelected = wind.getTabSeq().getValueAt(wind.getTabSeq().getSelectedRows()[i], 0).toString();
			new OpenBids(seqSelected, true, seqSelected);
		}
		FileManagerFrame.dlg.setVisible(false);
	}

	@Override
	public void showParamFile(String chemFile, String keyword) {
		String tm = "";

		if (keyword.contains("header")) {
			Nifti1Dataset niftiHdr = new Nifti1Dataset(chemFile);
			try {
				niftiHdr.readHeader();
				tm = chemFile + "\n" + niftiHdr.getHeader();
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
		} else if (keyword.contentEquals("see Json file")) {
			String pathFile = chemFile.replace(".nii.gz", ".json");
			if (!new File(pathFile).exists()) {
				JOptionPane.showMessageDialog(wind, "Json doesn't exist");
				return;
			} else {
				tm = new ExtractTxtfromFile(pathFile).getTxt();
			}
			String prettyJson = toPrettyFormat(tm);
			tm = prettyJson;
		}

		new OpenImageJ();
		IJ.log(tm);
	}

	@Override
	public void fillBasket() {
		new FillBasketSingle(wind, "[Bids] ");
	}
	
	public static String toPrettyFormat(String jsonString) {
	      JsonParser parser = new JsonParser();
	      JsonObject json = parser.parse(jsonString).getAsJsonObject();

	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      String prettyJson = gson.toJson(json);

	      return prettyJson;
	}
}