package brukerParavision;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import MRIFileManager.FileManagerFrame;
import MRIFileManager.GetStackTrace;
import MRIFileManager.OpenImageJ;
import abstractClass.PrefParam;
import abstractClass.SelectionData;
import ij.IJ;
//import ij.ImageJ;

public class SelectionDataBruker extends SelectionData {

//	private ImageJ ij;
	private String dataSelected;
	private FileManagerFrame wind;

	public SelectionDataBruker(FileManagerFrame wind) throws Exception {
//		super();
		this.wind = wind;
		dataSelected = wind.getListPath().getSelectedItem().toString() + PrefParam.separator
				+ wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 1);
		dataSelected = dataSelected.substring(12);
	}

	@Override
	public void popMenuData(JPopupMenu pm) {

		String[] listParamtoChange = { "Patient Name", "Study Name", "Patient Sex", "Patient Weight",
				"Patient BirthDate" };

		JMenu changeItem = new JMenu("Change");

		JMenuItem paramToChange = null;

		for (String df : listParamtoChange) {
			paramToChange = new JMenuItem(df);
			paramToChange.addActionListener(actionListener);
			changeItem.add(paramToChange);
		}

		pm.add(changeItem);

		pm.addSeparator();

		JMenuItem openParamFile = new JMenuItem("see 'subject' file");
		openParamFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showParamFile(dataSelected + PrefParam.separator + "subject");
			}
		});
		pm.add(openParamFile);
	}
	
	@Override
	public void popMenuDataExport(JPopupMenu popMenuExport) {
		// TODO Auto-generated method stub
		
	}

	private void showParamFile(String pathFile) {

		String tm = "";
		boolean fileOk = false;

		tm = "\n******************************** subject **********************************************" + "\n";

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
//			FileManagerFrame.getBugText().setText(
//					FileManagerFrame.getBugText().getText() + "\n----------------\n" + GetStackTrace.getMessage());
		}
		if (fileOk) {
			new OpenImageJ();
			IJ.log(tm);
		}
	}

	ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {

			String cheminSubject = dataSelected + PrefParam.separator + "subject";
			String defautName = null;
			String newName = null;
			int rowSelected = wind.getTabData().getSelectedRow();

			defautName = (String) wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(),
					wind.getTabData().getColumn(arg0.getActionCommand()).getModelIndex());
			newName = (String) JOptionPane.showInputDialog(wind, "new "+arg0.getActionCommand() , dataSelected,
					JOptionPane.QUESTION_MESSAGE, null, null, defautName);
			if (newName == null || defautName.contentEquals(newName))
				return;

			try {
				new ChangeSubject(cheminSubject, newName,arg0.getActionCommand());
				
				String dateDataSel = wind.getTabData().getValueAt(rowSelected, 4).toString();
				wind.getListPath().setSelectedIndex(0);
				wind.getListPath().updateUI();
				for (int i = 0; i < wind.getTabData().getRowCount(); i++)
					if (wind.getTabData().getValueAt(i, 4).toString().contains(dateDataSel)) {
						wind.getTabData().setRowSelectionInterval(i, i);
						break;
					}

			} catch (Exception e) {
				new GetStackTrace(e);
			}
		}
	};
}