package exportFiles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;


import MRIFileManager.ExtractTxtfromFile;
import MRIFileManager.FileManagerFrame;
import MRIFileManager.GetStackTrace;
import MRIFileManager.PrefParamModif;
import abstractClass.PrefParam;

public class ExportFilesOption extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private FileManagerFrame wind;
	private JFrame frameOptionExport;
	private JPanel panelL, panelR, panelTree, panelOutFormat, panelOther;
	private JTextField nFieldRep, nFieldFile, replaceCharForbidden;
	private String[] listField = {"Directory", "PatientName", "StudyName", "CreationDate", "AcquisitionDate",
								  "SeqNumber", "SerialNumber", "Protocol", "SequenceName",
								  "AcquisitionTime" };
	private String[] listFieldChooseinFile = null, listFieldChooseinRep = null;
	private String[] listnull = null;
	private JComboBox<String>[] listComboRep, listComboFile;
	private JTextField textSeparate;
	private JCheckBox outFormat1, outFormat2, outFormat3, bvecs, choice1, choice2,choice3;
	private Scanner sc;
	private String separateChar = "-";

	public ExportFilesOption(FileManagerFrame wind, String command) {
		super(command);
		this.wind = wind;
	}

	public void optionExport() {

		String txt = "";
		String tmpRepNifti = PrefParam.namingRepNiftiExport;
		String tmpFileNifti = PrefParam.namingFileNiftiExport;
		String tmpOptionsNifti = PrefParam.namingOptionsNiftiExport;

		if (PrefParam.FilestmpRep.exists()) {
			txt = new ExtractTxtfromFile(PrefParam.FilestmpRep.toString()).getTxt();
			sc = new Scanner(txt);
			String tmp = "";
			while (sc.hasNext()) {
				tmp = sc.next();
				if (tmp.contains("[NamingFileNifTI]")) {
					tmpFileNifti = sc.next();
				}
				if (tmp.contains("[NamingRepNifTI]")) {
					tmpRepNifti = sc.next();
				}
				if (tmp.contains("[NamingOptionsNifTI]"))
					tmpOptionsNifti = sc.next();
				for (int i = tmpOptionsNifti.length(); i < 5; i++) {
					tmpOptionsNifti += "0";
				}
			}
		}

		GetListFieldFromFilestmpRep dg = new GetListFieldFromFilestmpRep(tmpFileNifti);
		listFieldChooseinFile = dg.getListField();
		separateChar = dg.getSeparateChar();

		listFieldChooseinRep = tmpRepNifti.split("\\" + PrefParam.separator);

		frameOptionExport = new JFrame("Options export - file naming");
		frameOptionExport.setMinimumSize(new Dimension(600, 800));
		frameOptionExport.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frameOptionExport.setLocationRelativeTo(wind);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JTabbedPane tab = new JTabbedPane();
		tab.setPreferredSize(new Dimension(200, 350));
		tab.setBounds(10, 10, 200, 200);
		// tab.setBackground(Color.LIGHT_GRAY);
		;

		/************************************
		 * tabbedPane for directory
		 **********************************/
		final JLabel foldersLabel = new JLabel("Repertories naming");
		foldersLabel.setBounds(10, 0, 200, 25);

		nFieldRep = new JTextField();
		if (listFieldChooseinRep == null)
			nFieldRep.setText(String.valueOf(listField.length));
		else
			nFieldRep.setText(String.valueOf(listFieldChooseinRep.length - 1));

		if (nFieldRep.getText().contentEquals("-1"))
			nFieldRep.setText("0");

		nFieldRep.setBounds(10, 30, 100, 25);
		
		final JLabel nField_label = new JLabel("Number of Fields (< 10)", SwingConstants.LEFT);
		nField_label.setBounds(120, 30, 200, 25);

		panelL = new JPanel();
		panelL.setLayout(null);
		panelL.setBorder(BorderFactory.createLineBorder(Color.gray));
		panelL.add(foldersLabel);
		panelL.add(nField_label);
		panelL.add(nFieldRep);

		nFieldRep.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (!Pattern.matches("^\\d*$", nFieldRep.getText()))
						nFieldRep.setText("0");
					if (Integer.parseInt(nFieldRep.getText()) > 10)
						nFieldRep.setText("10");
					if (Integer.parseInt(nFieldRep.getText()) < 0)
						nFieldRep.setText("0");
					listFieldChooseinRep = listnull;
					panelL.removeAll();
					panelL.add(foldersLabel);
					panelL.add(nField_label);
					panelL.add(nFieldRep);
					createRemoveComboRep();
					panelTree.removeAll();
					treePreview();

				} catch (Exception e1) {
					new GetStackTrace(e1, this.getClass().toString());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		createRemoveComboRep();

		/************************************
		 * tabbedPane for file
		 **********************************/
		
		final JLabel filesLabel = new JLabel("Files naming");
		filesLabel.setBounds(10, 0, 200, 25);
		
		nFieldFile = new JTextField();
		if (listFieldChooseinFile == null)
			nFieldFile.setText(String.valueOf(listField.length));
		else
			nFieldFile.setText(String.valueOf(listFieldChooseinFile.length));
		nFieldFile.setBounds(10, 30, 100, 25);

		final JLabel nField_label1 = new JLabel("Number of Fields (< 10)", SwingConstants.LEFT);
		nField_label1.setBounds(120, 30, 200, 25);

		panelR = new JPanel();
		panelR.setLayout(null);
		panelR.setBorder(BorderFactory.createLineBorder(Color.gray));
		panelR.add(filesLabel);
		panelR.add(nField_label1);
		panelR.add(nFieldFile);

		nFieldFile.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					if (!Pattern.matches("^\\d*$", nFieldFile.getText()))
						nFieldFile.setText("1");
					if (Integer.parseInt(nFieldFile.getText()) > 10)
						nFieldFile.setText("10");
					if (Integer.parseInt(nFieldFile.getText()) <= 0)
						nFieldFile.setText("1");
					listFieldChooseinFile = listnull;
					panelR.removeAll();
					panelR.add(filesLabel);
					panelR.add(nField_label1);
					panelR.add(nFieldFile);
					createRemoveComboFile();
					panelTree.removeAll();
					treePreview();

				} catch (Exception e1) {
					new GetStackTrace(e1, this.getClass().toString());
//					FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()
//							+ "\n----------------\n" + GetStackTrace.getMessage());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		createRemoveComboFile();
		
		/************************************
		 * tabbedPane for output dimension
		 **********************************/
		boolean bool = false;

		final JLabel Bruker_label = new JLabel("Ouput export : ", SwingConstants.LEFT);
		Bruker_label.setBounds(10, 0, 200, 25);

//		outFormat0 = new JCheckBox("De-identification MRI data");
//		outFormat0.setBounds(50,30,400,25);
//		bool = (tmpOptionsNifti.substring(0,1).contentEquals("0"))?false:true;
//		outFormat0.setSelected(bool);
		
		outFormat1 = new JCheckBox("Separate parametric images (Bruker only)");
		outFormat1.setBounds(50, 50, 400, 25);
		bool = (tmpOptionsNifti.substring(0, 1).contentEquals("0")) ? false : true;
		outFormat1.setSelected(bool);

		outFormat2 = new JCheckBox("Separate type images (magnitude, real, imaginary, phase)");
		outFormat2.setBounds(50, 80, 400, 25);
		bool = (tmpOptionsNifti.substring(1, 2).contentEquals("0")) ? false : true;
		outFormat2.setSelected(bool);
		outFormat2.setEnabled(false);

		outFormat3 = new JCheckBox("Separate scan sequence images (SE, IR, GR .....)");
		outFormat3.setBounds(50, 110, 400, 25);
		bool = (tmpOptionsNifti.substring(2, 3).contentEquals("0")) ? false : true;
		outFormat3.setSelected(bool);
		outFormat3.setEnabled(false);

		panelOutFormat = new JPanel();
		panelOutFormat.setLayout(null);
		panelOutFormat.add(Bruker_label);
//		panelOutFormat.add(outFormat0);
		panelOutFormat.add(outFormat1);
		panelOutFormat.add(outFormat2);
		panelOutFormat.add(outFormat3);

		/************************************
		 * tabbedPane for other
		 **********************************/
		bvecs = new JCheckBox("Generate bvecs,bvals for MrTrix (Bruker, Philips, Dicom Philips)");
		bvecs.setBounds(50, 50, 500, 25);
		bool = (tmpOptionsNifti.substring(3, 4).contentEquals("0")) ? false : true;
		bvecs.setSelected(bool);
		bvecs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panelTree.removeAll();
				treePreview();
			}
		});

		String bin = tmpOptionsNifti.substring(4, 5);
		
		choice1 = new JCheckBox("save bvecs & bvals in 1 file");
		choice1.setBounds(100, 75, 400, 25);
		choice2 = new JCheckBox("save bvecs & bvals in 2 files");
		choice2.setBounds(100, 100, 400, 25);
		choice3 = new JCheckBox("create Nifti with zero transform");
		choice3.setBounds(100, 125, 400, 25);
		choice1.setSelected(false);
		choice2.setSelected(false);
		choice3.setSelected(false);

//		switch (bin) {
//		case "1":
//			choice1.setSelected(true);
//			break;
//		case "2":
//			choice2.setSelected(true);
//			break;
//		case "3":
//			choice1.setSelected(true);
//			choice2.setSelected(true);
//		}

		String n = Integer.toBinaryString(Integer.parseInt(bin));
	
		if (n.length() >= 1)
			choice1.setSelected(String.valueOf((n.charAt(0))).contentEquals("1"));
		if (n.length() >= 2) {
			choice1.setSelected(String.valueOf((n.charAt(1))).contentEquals("1"));
			choice2.setSelected(String.valueOf((n.charAt(0))).contentEquals("1"));
		}
		if (n.length() >= 3) {
			choice1.setSelected(String.valueOf((n.charAt(2))).contentEquals("1"));
			choice2.setSelected(String.valueOf((n.charAt(1))).contentEquals("1"));
			choice3.setSelected(String.valueOf((n.charAt(0))).contentEquals("1"));
		}


		choice1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panelTree.removeAll();
				treePreview();
			}
		});

		choice2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panelTree.removeAll();
				treePreview();
			}
		});
		
		choice3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panelTree.removeAll();
				treePreview();
			}
		});

		panelOther = new JPanel();
		panelOther.setLayout(null);
		panelOther.add(bvecs);
		panelOther.add(choice1);
		panelOther.add(choice2);
		panelOther.add(choice3);

		/**********************
		 * Jtree preview
		 **********************/

		panelTree = new JPanel();
		treePreview();
		
		JPanel panelNaming = new JPanel();
		panelNaming.setLayout(new GridLayout(1,2));
		
		panelNaming.add(panelL);
//		panelNaming.add(separator);
		panelNaming.add(panelR);

//		tab.add("Repertory naming", panelNaming);
		tab.add("Nifti naming", panelNaming);
		tab.add("Output dimension", panelOutFormat);
		tab.add("Other", panelOther);

		JPanel panelButton = new JPanel();
		panelButton.add(buttonApply());
		panelButton.add(buttonCancel());

		panel.add(tab);
		panel.add(panelTree);
		panel.add(panelButton);

		frameOptionExport.getContentPane().add(panel);
		frameOptionExport.pack();
		frameOptionExport.setVisible(true);
	}

	private void treePreview() {

		DefaultMutableTreeNode nd1 = new DefaultMutableTreeNode(null);
		int nRep = Integer.parseInt(nFieldRep.getText());

		String tmp = "";
		for (int i = 0; i < listComboFile.length; i++) {
			tmp += listComboFile[i].getSelectedItem().toString() + textSeparate.getText();
		}
		if (!tmp.contentEquals("")) {
			tmp = tmp.substring(0, tmp.lastIndexOf(textSeparate.getText()));
		}
		
		if (!bvecs.isSelected()) {
			choice1.setSelected(false);
			choice2.setSelected(false);
			choice3.setSelected(false);
		}
		
		if (!choice1.isSelected() && !choice2.isSelected())
			choice3.setSelected(false);

		if (nRep > 0) {
			// nd1.add(new
			// DefaultMutableTreeNode(listComboRep[0].getSelectedItem().toString()));
			// if (nRep > 1) {
			DefaultMutableTreeNode[] nd2 = new DefaultMutableTreeNode[nRep];
			for (int i = 0; i < nRep; i++) {
				nd2[i] = new DefaultMutableTreeNode(listComboRep[nRep - 1 - i].getSelectedItem().toString());
				if (i > 0)
					nd2[i].add(nd2[i - 1]);
				else {
					nd2[i].add(new DefaultMutableTreeNode(tmp + ".nii"));
					nd2[i].add(new DefaultMutableTreeNode(tmp + ".json"));
					if (choice1.isSelected())
						nd2[i].add(new DefaultMutableTreeNode(tmp + "-bvecs-bvals-MRtrix.txt"));
					if (choice2.isSelected()) {
						nd2[i].add(new DefaultMutableTreeNode(tmp + "-bvecs-MRtrix.txt"));
						nd2[i].add(new DefaultMutableTreeNode(tmp + "-bvals-MRtrix.txt"));
					}
					if (choice3.isSelected())
						nd2[i].add(new DefaultMutableTreeNode(tmp + "-zero_transform.nii"));
//					if (bvals.isSelected())
//						nd2[i].add(new DefaultMutableTreeNode(tmp + ".bvals.txt"));
				}
			}
			nd1.add(nd2[nRep - 1]);
			// } else{
			// nd1.add(new DefaultMutableTreeNode(tmp+".nii"));
			// nd1.add(new DefaultMutableTreeNode(tmp+".json"));
			// }
		} else {
			nd1.add(new DefaultMutableTreeNode(tmp + ".nii"));
			nd1.add(new DefaultMutableTreeNode(tmp + ".json"));
			if (choice1.isSelected())
				nd1.add(new DefaultMutableTreeNode(tmp + "-bvecs-bvals-MRtrix.txt"));
			if (choice2.isSelected()) {
				nd1.add(new DefaultMutableTreeNode(tmp + "-bvecs-MRtrix.txt"));
				nd1.add(new DefaultMutableTreeNode(tmp + "-bvals-MRtrix.txt"));
			}
			if (choice3.isSelected())
				nd1.add(new DefaultMutableTreeNode(tmp + "-zero_transform.nii"));
//			if (bvals.isSelected())
//				nd1.add(new DefaultMutableTreeNode(tmp + ".bvals.txt"));
		}

		JTree jt = new JTree(nd1);
		jt.setRootVisible(false);
		for (int i = 0; i < Integer.parseInt(nFieldRep.getText()) + 1; i++)
			jt.expandRow(i);

		// jt.setPreferredSize(new Dimension(500, 200));
		// jt.setBounds(10, 500, 600, 100);

		JScrollPane js = new JScrollPane();
		js.setPreferredSize(new Dimension(580, 200));
		js.getViewport().add(jt);
		js.revalidate();
		js.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		panelTree.add(js);
		panelTree.revalidate();
		panelTree.repaint();

		frameOptionExport.validate();

	}

	private void createRemoveComboRep() {

		listComboRep = new JComboBox[Integer.parseInt(nFieldRep.getText())];
		JLabel[] listLabel = new JLabel[Integer.parseInt(nFieldRep.getText())];
		// panelL.removeAll();

		for (int i = 0; i < Integer.parseInt(nFieldRep.getText()); i++) {
			listComboRep[i] = new JComboBox<>(listField);
			if (listFieldChooseinRep == null)
				listComboRep[i].setSelectedIndex(i);
			else
				listComboRep[i].setSelectedItem(listFieldChooseinRep[i + 1]);
			listLabel[i] = new JLabel("Field " + String.valueOf(i + 1), SwingConstants.LEFT);
			listComboRep[i].setBounds(10, (i + 2) * 30 + 35, 200, 25);
			listLabel[i].setBounds(220, (i + 2) * 30 + 35, 200, 25);
			listComboRep[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					panelTree.removeAll();
					treePreview();
				}
			});
			panelL.add(listComboRep[i]);
			panelL.add(listLabel[i]);
			panelL.revalidate();
		}
		panelL.repaint();
		// frameOptionExport.pack();
		frameOptionExport.validate();
	}

	private void createRemoveComboFile() {
		listComboFile = new JComboBox[Integer.parseInt(nFieldFile.getText())];
		JLabel[] listLabel = new JLabel[Integer.parseInt(nFieldFile.getText())];

		textSeparate = new JTextField();
		textSeparate.setText(separateChar);
		textSeparate.setToolTipText("Warning    < > / \\ * . ? | : are forbidden");
		textSeparate.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {

				if (e.getKeyChar() == ' ' || e.getKeyChar() == '\\' || e.getKeyChar() == '/' || e.getKeyChar() == '.'
						|| e.getKeyChar() == '*' || e.getKeyChar() == '"' || e.getKeyChar() == '?'
						|| e.getKeyChar() == '<' || e.getKeyChar() == '>' || e.getKeyChar() == ':'
						|| e.getKeyChar() == ' ' || e.getKeyChar() == '|') {
					try {
						// textSeparate.setText(textSeparate.getText().replace("
						// +", ""));
						// textSeparate.setText(textSeparate.getText().replace("\\",
						// ""));
						// textSeparate.setText(textSeparate.getText().replace("/",
						// ""));
						// textSeparate.setText(textSeparate.getText().replace(".",
						// ""));
						textSeparate.setText(textSeparate.getText().replace("[<>:?/\\\\*|" + '"' + "]", ""));
						e.consume();

					} catch (Exception e1) {
						new GetStackTrace(e1, this.getClass().toString());
//						FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()
//								+ "\n----------------\n" + GetStackTrace.getMessage());
					}
				}

			}

			@Override
			public void keyReleased(KeyEvent e) {
				panelTree.removeAll();
				treePreview();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		JLabel labelSeparate = new JLabel("separation character");
		
		textSeparate.setBounds(10, 60, 100, 25);
		labelSeparate.setBounds(120, 60, 200, 25);
		panelR.add(textSeparate);
		panelR.add(labelSeparate);

		replaceCharForbidden = new JTextField();
		replaceCharForbidden.setText(" ");

		for (int i = 0; i < Integer.parseInt(nFieldFile.getText()); i++) {
			listComboFile[i] = new JComboBox<>(listField);
			if (listFieldChooseinFile == null)
				listComboFile[i].setSelectedIndex(i);
			else
				listComboFile[i].setSelectedItem(listFieldChooseinFile[i]);
			listLabel[i] = new JLabel("Field " + String.valueOf(i + 1), SwingConstants.LEFT);
			listComboFile[i].setBounds(10, (i + 2) * 30 + 35, 200, 25);
			listLabel[i].setBounds(220, (i + 2) * 30 + 35, 200, 25);
			listComboFile[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					panelTree.removeAll();
					treePreview();
				}
			});
			panelR.add(listComboFile[i]);
			panelR.add(listLabel[i]);
			panelR.revalidate();
		}
		panelR.repaint();
		// frameOptionExport.pack();
		frameOptionExport.validate();
	}

	private JButton buttonApply() {

		JButton buttonOk = new JButton("Apply");
		buttonOk.setBounds(50, 600, 100, 25);
		buttonOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (textSeparate.getText().isEmpty())
					textSeparate.setText("-");
				frameOptionExport.dispose();
				String tmp = "";
				for (int i = 0; i < listComboFile.length; i++) {
					tmp += listComboFile[i].getSelectedItem().toString() + textSeparate.getText();
				}
				if (!tmp.contentEquals("")) {
					tmp = tmp.substring(0, tmp.lastIndexOf(textSeparate.getText()));
				}

				PrefParam.namingFileNiftiExport = tmp;

				String tmp1 = PrefParam.separator;
				for (int i = 0; i < listComboRep.length; i++) {
					tmp1 += listComboRep[i].getSelectedItem().toString() + PrefParam.separator;
				}

				PrefParam.namingRepNiftiExport = tmp1;

				String tmp2 = "";
				tmp2 += (outFormat1.isSelected()) ? "1" : "0";
				tmp2 += (outFormat2.isSelected()) ? "1" : "0";
				tmp2 += (outFormat3.isSelected()) ? "1" : "0";
				tmp2 += (bvecs.isSelected()) ? "1" : "0";

				boolean[] flags = { choice1.isSelected(), choice2.isSelected(), choice3.isSelected() };
				byte b = 0;
				for (int i = 0; i < flags.length; ++i) {
					if (flags[i])
						b |= (1 << i);
				}
				tmp2 += b;

				PrefParam.namingOptionsNiftiExport = tmp2;

				new PrefParamModif();
//				new PrefParamModif("[NamingFileNifTI]", tmp);
//				new PrefParamModif("[NamingRepNifTI]", tmp1);
//				new PrefParamModif("[NamingOptionsNifTI]", tmp2);
			}
		});
		return buttonOk;
	}

	private JButton buttonCancel() {

		JButton buttonCanc = new JButton("Cancel");
		buttonCanc.setBounds(150, 600, 100, 25);
		buttonCanc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frameOptionExport.dispose();
			}
		});

		return buttonCanc;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		optionExport();
	}
}