package MRIFileManager;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileView;
import javax.swing.table.TableRowSorter;

import abstractClass.Format;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import bids.TableBidsData;
import brukerParavision.BrukerFileView;
import brukerParavision.TableBrukerData;
import dcm.TableDicomData;
import nifti.NiftiFileView;
import nifti.TableNiftiData;
import philips.PhilipsFileView;
import philips.TablePhilipsData;

class ActionsButtonMenu extends AbstractAction implements ParamMRI2, Format {

	private static final long serialVersionUID = 1L;
	private FileManagerFrame wind;
	private String dialogTitle;
	private String repselected = null;
	private String previouslectCurrent;
	private FileView view;

	public ActionsButtonMenu(FileManagerFrame wind, String command) {
		super(command);
		this.wind = wind;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {

		String cmd = evt.getActionCommand().toString();

		/*****************************************************************************************************************/
		/* button Quit action */

		if (cmd.contains("Quit"))
			quit();
		if (cmd.contains("comboBoxChanged")) {
			comboBoxChanged();
		}
		if (cmd.contains("Help")) {
			String URLHelp = UtilsSystem.pathOfJar();
			URLHelp = URLHelp.substring(0, URLHelp.lastIndexOf(PrefParam.separator));
			URLHelp = URLHelp.substring(0, URLHelp.lastIndexOf(PrefParam.separator));
			URLHelp += PrefParam.separator + "docs" + PrefParam.separator + "index.html";
			if (new File(URLHelp).exists())
				try {
					File htmlFile = new File(URLHelp);
					Desktop.getDesktop().browse(htmlFile.toURI());
				} catch (Exception e) {
				}
			else {
				URLHelp = System.getProperty("user.dir");
				URLHelp = URLHelp.substring(0, URLHelp.lastIndexOf(PrefParam.separator));
				URLHelp += PrefParam.separator + "MRIFileManager" + PrefParam.separator + "docs" + PrefParam.separator
						+ "index.html";
				try {
					File htmlFile = new File(URLHelp);
					Desktop.getDesktop().browse(htmlFile.toURI());
				} catch (Exception e) {
				}
			}
		}
		if (cmd.contains("About"))
			wind.getAboutDialog().setVisible(true);
		if (cmd.contains("Error window"))
			wind.getFenBug().setVisible(true);
		if (cmd.contains("Detailed file window"))
			;
		if (cmd.contains("Current working directory"))
			new WindowRepSelection();

		if (cmd.contains("Open ImageJ"))
			new OpenImageJ();

		if (cmd.contains("Bruker")) {
			dialogTitle = "Choose Bruker Data";
			previouslectCurrent = PrefParam.lectCurrent;
			PrefParam.lectCurrent = PrefParam.lectBruker;
			view = new BrukerFileView();
			selectDirData(cmd);
		}
		if (cmd.contains("Dicom")) {
			dialogTitle = "Choose Dicom Data";
			previouslectCurrent = PrefParam.lectCurrent;
			PrefParam.lectCurrent = PrefParam.lectDicom;
			// view = new DicomFileView();
			selectDirData(cmd);
		}
		if (cmd.contains("Philips Achieva")) {
			dialogTitle = "Choose Philips Data";
			previouslectCurrent = PrefParam.lectCurrent;
			PrefParam.lectCurrent = PrefParam.lectParRec;
			view = new PhilipsFileView();
			selectDirData(cmd);
		}
		if (cmd.contains("NifTI")) {
			dialogTitle = "Choose Nifti Data";
			previouslectCurrent = PrefParam.lectCurrent;
			PrefParam.lectCurrent = PrefParam.lectNifTI;
			view = new NiftiFileView();
			selectDirData(cmd);
		}
		if (cmd.contains("Bids")) {
			dialogTitle = "Choose Bids Data";
			previouslectCurrent = PrefParam.lectCurrent;
			if (!PrefParam.lectBids.isEmpty())
				PrefParam.lectCurrent = PrefParam.lectBids.substring(0,
						PrefParam.lectBids.lastIndexOf(PrefParam.separator));
			else
				PrefParam.lectCurrent = PrefParam.lectBids;
//			view = new BidsFileView();
			selectDirData(cmd);
		}

		// if (cmd.contains("GTK") || cmd.contains("Nimbus"))
		// {changeLookAndFeel();}
		/*****************************************************************************************************************/
		/* button Directory action */
	}

	private void quit() {
		if (PrefParam.ExitSystem)
			System.exit(PrefParam.returnCodeExit);
		else
			wind.dispose();
	}

	private void selectDirData(String form) {

		final JFileChooser rep = new JFileChooser();
		rep.setAcceptAllFileFilterUsed(false);
		rep.setApproveButtonText("Select this directory");
		rep.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		rep.setLocale(Locale.ENGLISH);
		rep.setDialogTitle(dialogTitle);
		rep.setCurrentDirectory(new File(PrefParam.lectCurrent));
		if (wind.getIconCheck().getState())
			rep.setFileView(view);
		rep.updateUI();
		rep.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					File file = rep.getSelectedFile();
					if (file.isDirectory())
						rep.approveSelection();
					else {
						rep.setCurrentDirectory(file);
						rep.rescanCurrentDirectory();
					}
				}
			}
		});

		switch (rep.showOpenDialog(wind)) {

		case JFileChooser.APPROVE_OPTION:
			repselected = rep.getSelectedFile().getPath();

//			System.out.println(this+" repselected 1 = "+repselected);
			
			if (!rep.getSelectedFile().isDirectory())
				repselected = repselected.substring(0, repselected.lastIndexOf(PrefParam.separator));
			else if (repselected.substring(repselected.length() - 1).contentEquals(PrefParam.separator))
				repselected = repselected.substring(0, repselected.length() - 1);
		
//			System.out.println(this+" repselected 2 = "+repselected);

			
			if (form.contains("Bruker")) {
				PrefParam.formatCurrent = "[Bruker]    ";
				PrefParam.formatCurrentInt = 0;
			}
			if (form.contains("Dicom")) {
				PrefParam.formatCurrent = "[Dicom]     ";
				PrefParam.formatCurrentInt = 1;
			}
			if (form.contains("Philips Achieva")) {
				PrefParam.formatCurrent = "[Philips]   ";
				PrefParam.formatCurrentInt = 2;
			}
			if (form.contains("NifTI")) {
				PrefParam.formatCurrent = "[Nifti]     ";
				PrefParam.formatCurrentInt = 3;
			}
			if (form.contains("Bids")) {
				PrefParam.formatCurrent = "[Bids]      ";
				PrefParam.formatCurrentInt = 4;
			}

			fillListPath();
			break;

		case JFileChooser.CANCEL_OPTION:
			PrefParam.lectCurrent = previouslectCurrent;
			break;
		}
		rep.setSelectedFile(null);
	}

	private void comboBoxChanged() {
		wind.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		PrefParam.DirectoryDataOnly = "";

		hmSeq.clear();
		hmInfo.clear();
		hmOrderImage.clear();
		hmData.clear();
		wind.resetTabSeq();
		wind.resetTabData();
		wind.getBoxThumb().removeAll();
		wind.getBoxThumb().updateUI();
		wind.getBoxImage().removeAll();
		wind.getBoxImage().updateUI();
		System.gc();

		repselected = wind.getListPath().getSelectedItem().toString().substring(12);
		PrefParam.formatCurrent = wind.getListPath().getSelectedItem().toString().substring(0, 12);
		String tmp = PrefParam.formatCurrent;
		tmp = tmp.substring(tmp.indexOf("[") + 1, tmp.indexOf("]"));
		PrefParam.formatCurrentInt = format.valueOf(tmp).toInt();
		

//		System.out.println(this+" repselected 3 = "+repselected);

		Object[][] data = null;
		File tmpfile = new File(repselected);

		if (new File(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml").exists()) {
			String tmpa = PrefParam.formatCurrent;
			tmpa = tmpa.replace("[", "");
			tmpa = tmpa.replace("]", "");
			tmpa = tmpa.replace("Bids", "Nifti");
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml", tmpa).loadDictionarySystem();
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_User.yml", tmpa).loadDictionaryUser();
			if (!listParamInfoSystem.isEmpty())
				try {
					switch (PrefParam.formatCurrentInt) {

					case Bruker:
						data = new TableBrukerData(repselected).getData();
						break;
					case Philips:
						FilenameFilter filterParRec = new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) {
								if (name.toUpperCase().endsWith(".REC"))
									return true;
								return false;
							}
						};
						if (tmpfile.list(filterParRec).length != 0) {
							PrefParam.DirectoryDataOnly = repselected.substring(repselected.lastIndexOf(PrefParam.separator)+1);
							repselected = repselected.substring(0, repselected.lastIndexOf(PrefParam.separator));
						}
						data = new TablePhilipsData(repselected).getData();
						break;
					case Dicom:
						data = new TableDicomData(repselected).getData();
						break;
					case Nifti:
						data = new TableNiftiData(repselected).getData();
						break;
					case Bids:
						data = new TableBidsData(repselected).getData();
						break;
					}
				} catch (Exception e) {
					new GetStackTrace(e, this.getClass().toString());
				}
		} else {
			new JOptionPane();
			JOptionPane.showMessageDialog(wind, "Warning : DictionaryMRI_System.yml not found !", "Error",
					JOptionPane.WARNING_MESSAGE);
		}

		wind.getTreeInfoGeneral().setModel(new TreeInfo2(ParamMRI2.listParamInfoSystem, null).getTreeInfo().getModel());
		wind.getTreeInfoUser().setModel(new TreeInfo2(ParamMRI2.listParamInfoUser, null).getTreeInfo().getModel());

		if (!listParamInfoSystem.isEmpty()) {

			wind.getTreeInfoGeneral()
					.setModel(new TreeInfo2(ParamMRI2.listParamInfoSystem, null).getTreeInfo().getModel());
			for (int j = 0; j < wind.getTreeInfoGeneral().getRowCount(); j++)
				wind.getTreeInfoGeneral().expandRow(j);

			wind.getTreeInfoUser().setModel(new TreeInfo2(ParamMRI2.listParamInfoUser, null).getTreeInfo().getModel());
			for (int j = 0; j < wind.getTreeInfoUser().getRowCount(); j++)
				wind.getTreeInfoUser().expandRow(j);

			TableMod model = new TableMod(data, headerListData);
			TableRowSorter<TableMod> sorter = new TableRowSorter<>(model);

			wind.getTabData().removeAll();
			wind.getTabData().updateUI();
			wind.getTabData().setModel(model);
			wind.getTabData().setRowSorter(sorter);
			// wind.getTabData().setForeground(new Color(250, 100, 50));
			wind.getTabData().setEnabled(true);
			wind.getTabData().getRowSorter().toggleSortOrder(4);

			wind.getBoxImage().removeAll();
			if (PrefParam.previewActived)
				wind.getBoxImage().add(new ImagePanel(null));
			else
				wind.resetBoxImage();
			wind.getBoxImage().updateUI();
			wind.getBoxThumb().removeAll();
			wind.getBoxThumb().updateUI();
			wind.getScrollThumb().updateUI();

			for (int i = 0; i < wind.getSlidImage().length; i++) {
				wind.getSlidImage()[i].setEnabled(false);
				wind.getSlidImage()[i].removeAll();
				wind.getFieldSlid()[i].setText(" 0");
			}

			wind.getPreview().updateUI();

			data = null;
			System.gc();

		}
		wind.setCursor(null);
	}

	private void fillListPath() {

		File tmpfile = new File(repselected);

		switch (PrefParam.formatCurrentInt) {
		case Bruker:
			FilenameFilter filterSubject = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.contains("subject"))
						return true;
					return false;
				}
			};
			if (tmpfile.list(filterSubject).length != 0)
				repselected = repselected.substring(0, repselected.lastIndexOf(PrefParam.separator));
			PrefParam.lectBruker = repselected;
			break;

		case Dicom:
			FilenameFilter filterDicom = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.contains("DICOMDIR") || name.contains("DIRFILE"))
						return true;
					return false;
				}
			};
			if (tmpfile.list(filterDicom).length != 0) {
				try {
					repselected = repselected.substring(0, repselected.lastIndexOf(PrefParam.separator));
				} catch (Exception e) {
				}

			}
			PrefParam.lectDicom = repselected;
			break;

		case Philips:
//			FilenameFilter filterParRec = new FilenameFilter() {
//				@Override
//				public boolean accept(File dir, String name) {
//					if (name.toUpperCase().endsWith(".REC"))
//						return true;
//					return false;
//				}
//			};
//			if (tmpfile.list(filterParRec).length != 0) {
//				PrefParam.DirectoryDataOnly = repselected.substring(repselected.lastIndexOf(PrefParam.separator)+1);
//				repselected = repselected.substring(0, repselected.lastIndexOf(PrefParam.separator));//			}
			PrefParam.lectParRec = repselected;
			break;

		case Nifti:
			FilenameFilter filterNifTI = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".nii"))
						return true;
					return false;
				}
			};
			if (tmpfile.list(filterNifTI).length != 0)
				repselected = repselected.substring(0, repselected.lastIndexOf(PrefParam.separator));
			PrefParam.lectNifTI = repselected;
			break;

		case Bids:
//			FilenameFilter filterBids = new FilenameFilter() {
//				@Override
//				public boolean accept(File dir, String name) {
//					if (name.contains("dataset_description.json"))
//						return true;
//					return false;
//				}
//			};
//			if (tmpfile.list(filterBids).length != 0)
//				repselected = repselected.substring(0, repselected.lastIndexOf(PrefParam.separator));
			PrefParam.lectBids = repselected;
			break;
		}

		if (wind.getListPath().getItemCount() != 0) {
			boolean isYet = false;
			int ing = 0;
			for (int g = 0; g < wind.getListPath().getItemCount(); g++)
				if (wind.getListPath().getItemAt(g).equals(PrefParam.formatCurrent + repselected)) {
					isYet = true;
					ing = g;
					break;
				}
			if (!isYet) {
				wind.getListPath().insertItemAt(PrefParam.formatCurrent + repselected, 0);
				wind.getListPath().setSelectedIndex(0);
			} else
				wind.getListPath().setSelectedIndex(ing);
		} else {
			wind.getListPath().addItem(PrefParam.formatCurrent + repselected);
		}
	}
}