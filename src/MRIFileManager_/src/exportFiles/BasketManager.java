package exportFiles;

import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import MRIFileManager.BasketList;
import MRIFileManager.FileManagerFrame;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class BasketManager extends AbstractAction implements ParamMRI2 {

	private static final long serialVersionUID = 1L;

	private FileManagerFrame wind;

	public BasketManager(FileManagerFrame wind, String command) {
		super(command);
		this.wind = wind;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		if (arg0.getActionCommand().contentEquals("remove selection")) {
			String tmp_row;
			for (int sdg : wind.getTabBasket().getSelectedRows()) {
				tmp_row = wind.getTabBasket().getValueAt(sdg, 0).toString();
				tmp_row += " " + wind.getTabBasket().getValueAt(sdg, 1).toString();
				tmp_row += " [" + wind.getTabBasket().getValueAt(sdg, 2).toString() + "Mo]";
				listinBasket.removeElement(tmp_row);
				}
			}

		else if (arg0.getActionCommand().contentEquals("remove all")) {
			listinBasket.removeAllElements();
		}

		else if ((arg0.getActionCommand().contentEquals("export to ")
				|| arg0.getActionCommand().contentEquals("export to MIA")
				|| arg0.getActionCommand().contentEquals("export to MP3"))
				&& wind.getTabBasket().getRowCount() != 0) {
			new ConvertImage2(wind);
			if (PrefParam.CloseAfterExport) {
				if (PrefParam.ExitSystem)
					System.exit(0);
				else
					wind.dispose();
			}
		}

		else if (arg0.getActionCommand().contentEquals("Change")) {
			final JFileChooser rep = new JFileChooser();
			rep.setAcceptAllFileFilterUsed(false);
			rep.setCurrentDirectory(PrefParam.FilestmpExportNifit);
			rep.setApproveButtonText("Select this directory");
			rep.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			rep.setLocale(Locale.ENGLISH);
//			rep.updateUI();

			switch (rep.showOpenDialog(null)) {
			case JFileChooser.APPROVE_OPTION:
				wind.getpathExportNifti().setText(rep.getSelectedFile().getPath());
				PrefParam.FilestmpExportNifit = rep.getSelectedFile();
				break;
			}
		}

		int tmp_length = listinBasket.size();
		
		if (tmp_length == 0)
			tmp_length = 5;
			
		new BasketList(wind);

		wind.getTreeBasket().setModel(new UpdateTreeBasket(listinBasket.toArray()).returnTreeModel());
		for (int i = 0; i < wind.getTreeBasket().getRowCount(); i++)
			wind.getTreeBasket().expandRow(i);
		// wind.getTreeBasket().updateUI();

		if (listinBasket.size() == 0)
			wind.getTabbedPane().setTitleAt(1, "basket (Empty)");
		else {
			String tmp = listinBasket.size() == 1 ? " file" : " files";
			wind.getTabbedPane().setTitleAt(1, "Basket " + "(" + listinBasket.size() + tmp + " - "
					+ new CalculTotalSizeBasket().getTotalSize() + " Mo )");
		}

		wind.getTabbedPane().updateUI();
	}}