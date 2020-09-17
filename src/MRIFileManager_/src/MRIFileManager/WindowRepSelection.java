package MRIFileManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import abstractClass.PrefParam;

public class WindowRepSelection extends PrefParam {

	public WindowRepSelection() {

		new PrefParamLoad();

		final JFileChooser repch = new JFileChooser();

		repch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		repch.setApproveButtonText("Select this directory");
		
		// repch.setLocation(wind.getFenDefRep().getX(),
		// wind.getFenDefRep().getY()+wind.getFenDefRep().getHeight());
		repch.setLocale(Locale.ENGLISH);
		repch.updateUI();

		int WScreen, HScreen;

		WScreen = widthScreen;
		HScreen = heightScreen;

		final JDialog drep = new JDialog();
		drep.setModal(true);
		// drep.setLayout(new GridLayout(1,0));
		JPanel panelDialog = new JPanel();
		panelDialog.setLayout(new BoxLayout(panelDialog, BoxLayout.Y_AXIS));
		drep.add(panelDialog);

		/******************************************************
		 * Bruker
		 ******************************************************/

		JPanel panelBruker = new JPanel();
		panelBruker.setLayout(new BoxLayout(panelBruker, BoxLayout.LINE_AXIS));
		JLabel labelBruker = new JLabel("Bruker directory :          ", iconBruker, SwingConstants.CENTER);
		final JTextField repBruker = new JTextField();
		repBruker.setEditable(false);
		repBruker.setPreferredSize(new Dimension(WScreen / 5, HScreen / 35));
		repBruker.setAlignmentX(Component.CENTER_ALIGNMENT);
		repBruker.setText(lectBruker);

		JScrollPane jspBruker = new JScrollPane(repBruker);
		jspBruker.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JButton buttonRepBruker = new JButton("Change");
		buttonRepBruker.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repch.setCurrentDirectory(new File(lectBruker));
				repch.setDialogTitle("Bruker directory ?");
				if (repch.showOpenDialog(drep) == JFileChooser.APPROVE_OPTION) {
					repBruker.setText(repch.getSelectedFile().getPath());
				}
			}
		});

		/**************************
		 * Dicom
		 ******************************************************/

		JPanel panelDicom = new JPanel();
		panelDicom.setLayout(new BoxLayout(panelDicom, BoxLayout.LINE_AXIS));
		JLabel labelDicom = new JLabel("Dicom directory :   ", iconDicom, SwingConstants.CENTER);
		final JTextField repDicom = new JTextField();
		repDicom.setEditable(false);
		repDicom.setPreferredSize(new Dimension(WScreen / 5, HScreen / 35));
		repDicom.setAlignmentX(Component.CENTER_ALIGNMENT);
		repDicom.setText(lectDicom);

		JScrollPane jspDicom = new JScrollPane(repDicom);
		jspDicom.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JButton buttonRepDicom = new JButton("Change");
		buttonRepDicom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repch.setCurrentDirectory(new File(lectDicom));
				repch.setDialogTitle("Dicom directory ?");
				if (repch.showOpenDialog(drep) == JFileChooser.APPROVE_OPTION) {
					repDicom.setText(repch.getSelectedFile().getPath());
				}

			}
		});

		/************************
		 * ParRec
		 *************************************************/

		JPanel panelParRec = new JPanel();
		panelParRec.setLayout(new BoxLayout(panelParRec, BoxLayout.LINE_AXIS));
		JLabel labelParRec = new JLabel("Par/Rec directory :         ", iconPhilips, SwingConstants.CENTER);
		final JTextField repParRec = new JTextField();
		repParRec.setEditable(false);
		repParRec.setPreferredSize(new Dimension(WScreen / 5, HScreen / 35));
		repParRec.setAlignmentX(Component.CENTER_ALIGNMENT);
		repParRec.setText(lectParRec);

		JScrollPane jspParRec = new JScrollPane(repParRec);
		jspParRec.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JButton buttonRepParRec = new JButton("Change");
		buttonRepParRec.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repch.setCurrentDirectory(new File(lectParRec));
				repch.setDialogTitle("ParRec directory ?");
				if (repch.showOpenDialog(drep) == JFileChooser.APPROVE_OPTION) {
					repParRec.setText(repch.getSelectedFile().getPath());
				}

			}
		});

		/**************************
		 * Nifti
		 ******************************************************/

		JPanel panelNifTI = new JPanel();
		panelNifTI.setLayout(new BoxLayout(panelNifTI, BoxLayout.LINE_AXIS));
		JLabel labelNifTI = new JLabel("NifTI directory :              ", iconNifTI, SwingConstants.CENTER);
		final JTextField repNifTI = new JTextField();
		repNifTI.setEditable(false);
		repNifTI.setPreferredSize(new Dimension(WScreen / 5, HScreen / 35));
		repNifTI.setAlignmentX(Component.CENTER_ALIGNMENT);
		repNifTI.setText(lectNifTI);

		JScrollPane jspNifTI = new JScrollPane(repNifTI);
		jspNifTI.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JButton buttonRepNifTI = new JButton("Change");
		buttonRepNifTI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repch.setCurrentDirectory(new File(lectNifTI));
				repch.setDialogTitle("NifTI directory ?");
				if (repch.showOpenDialog(drep) == JFileChooser.APPROVE_OPTION) {
					repNifTI.setText(repch.getSelectedFile().getPath());
				}
			}
		});
		
		/**************************
		 * Bids
		 ******************************************************/

		JPanel panelBids = new JPanel();
		panelNifTI.setLayout(new BoxLayout(panelNifTI, BoxLayout.LINE_AXIS));
		JLabel labelBids = new JLabel("Bids directory :          ", iconBids, SwingConstants.CENTER);
		final JTextField repBids = new JTextField();
		repBids.setEditable(false);
		repBids.setPreferredSize(new Dimension(WScreen / 5, HScreen / 35));
		repBids.setAlignmentX(Component.CENTER_ALIGNMENT);
		repBids.setText(lectBids);

		JScrollPane jspBids = new JScrollPane(repBids);
		jspBids.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JButton buttonRepBids = new JButton("Change");
		buttonRepBids.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repch.setCurrentDirectory(new File(lectBids));
				repch.setDialogTitle("Bids directory ?");
				if (repch.showOpenDialog(drep) == JFileChooser.APPROVE_OPTION) {
					repBids.setText(repch.getSelectedFile().getPath());
				}
			}
		});

		/*************************** export *******************************************/
		JPanel panelExport = new JPanel();
		panelExport.setLayout(new BoxLayout(panelExport, BoxLayout.LINE_AXIS));
		JLabel labelExport = new JLabel("Export directory :        ");
		final JTextField repExport = new JTextField();
		repExport.setEditable(false);
		repExport.setPreferredSize(new Dimension(WScreen / 5, HScreen / 35));
		repExport.setAlignmentX(Component.CENTER_ALIGNMENT);
		repExport.setText(outExport);

		JScrollPane jspExport = new JScrollPane(repExport);
		jspExport.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JButton buttonRepExport = new JButton("Change");
		buttonRepExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repch.setCurrentDirectory(new File(outExport));
				repch.setDialogTitle("Export directory ?");
				if (repch.showOpenDialog(drep) == JFileChooser.APPROVE_OPTION) {
					repExport.setText(repch.getSelectedFile().getPath());
				}
			}
		});

		/***************************************************************************************/
		panelBruker.add(labelBruker);
		panelBruker.add(jspBruker);
		panelBruker.add(Box.createHorizontalStrut(5));
		panelBruker.add(buttonRepBruker);
		panelBruker.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panelDicom.add(labelDicom);
		panelDicom.add(jspDicom);
		panelDicom.add(Box.createHorizontalStrut(5));
		panelDicom.add(buttonRepDicom);
		panelDicom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panelParRec.add(labelParRec);
		panelParRec.add(jspParRec);
		panelParRec.add(Box.createHorizontalStrut(5));
		panelParRec.add(buttonRepParRec);
		panelParRec.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panelNifTI.add(labelNifTI);
		panelNifTI.add(jspNifTI);
		panelNifTI.add(Box.createHorizontalStrut(5));
		panelNifTI.add(buttonRepNifTI);
		panelNifTI.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		panelBids.add(labelBids);
		panelBids.add(jspBids);
		panelBids.add(Box.createHorizontalStrut(5));
		panelBids.add(buttonRepBids);
		panelBids.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panelExport.add(labelExport);
		panelExport.add(jspExport);
		panelExport.add(Box.createHorizontalStrut(5));
		panelExport.add(buttonRepExport);
		// panelExport.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panelExport.setBorder(BorderFactory.createTitledBorder("Output Directory"));

		/****************************************************************/
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createTitledBorder("Input Directory"));

		inputPanel.add(panelBruker);
		inputPanel.add(panelDicom);
		inputPanel.add(panelParRec);
		inputPanel.add(panelNifTI);
		inputPanel.add(panelBids);

		JPanel outputPanel = new JPanel(new BorderLayout());
		outputPanel.setLayout(new GridBagLayout());
		outputPanel.setBorder(BorderFactory.createTitledBorder("Export Directory"));
		GridBagConstraints c = new GridBagConstraints();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.PAGE_START;

		outputPanel.add(panelExport, c);

		/****************************************************************/

		JPanel panelOk = new JPanel();
		panelOk.setLayout(new GridBagLayout());
		panelOk.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JButton repOk = new JButton("OkRep");
		repOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				lectBruker = repBruker.getText();
				lectDicom = repDicom.getText();
				lectParRec = repParRec.getText();
				lectNifTI = repNifTI.getText();
				lectBids = repBids.getText();
				outExport = repExport.getText();
				new PrefParamModif();
				drep.dispose();
			}
		});

		// c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.gridy = 0;
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.anchor = GridBagConstraints.PAGE_START; // bottom of space
		// c.insets = new Insets(10,0,0,0); //top padding

		panelOk.add(repOk, c);

		/****************************************************************/
		panelDialog.add(inputPanel);
		panelDialog.add(panelExport, c);
		panelDialog.add(panelOk);
		/*
		 * Dimension screen = Toolkit.getDefaultToolkit().getScreenSize(); int
		 * WScreen,HScreen; WScreen=(int) screen.getWidth(); HScreen=(int)
		 * screen.getHeight();
		 */
		// drep.setSize(WScreen/2, HScreen/10);
		drep.setResizable(true);
		drep.setAlwaysOnTop(true);
		drep.setLocationRelativeTo(null);
		drep.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		drep.pack();
		drep.setVisible(true);
	}
}