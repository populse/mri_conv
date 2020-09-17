package MRIFileManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import exportFiles.BasketManager;
import exportFiles.ChoiceFormatExport;
import exportFiles.ExportFilesOption;

public class FileManagerFrame extends JFrame implements ItemListener, ComponentListener {

	private static final long serialVersionUID = 1L;
	private final String versionSoft="20.2.6a";
	
	public static String OS = System.getProperty("os.name").toLowerCase();
	
	private int WScreen, HScreen;
	private int WFrame, HFrame;

	private JFrame winBug, frameDetailSeq, about;
	private static JTextArea textBug;

	private JCheckBoxMenuItem showicon,showpreview,simplifiedDicom;
	private JSplitPane split1, split2, split3, split4, split5, split6;

	private JTabbedPane tabPrincipal = new JTabbedPane();
	private JTabbedPane tabInfoParam = new JTabbedPane();

	private JComboBox<String> listPath, listChoiceExport;

	private JTable tabData, tabSeq, tabDictionnaryMri;
	private JTree treeParamInfoGeneral, treeParamInfoUser, treeBasket;

	private JScrollPane preview, thumb, treeData;

	private Box boxImage, boxThumb;

	private JSlider slidImage1, slidImage2, slidImage3;
	private JLabel c, z, t;

	private JList<String> listOfBasket;
	private JLabel pathExportNifti, pathDictionnary;

	public static JDialog dlg;
	
	private JPopupMenu popupMenuDetailSeq;
	private JCheckBoxMenuItem[] itemSeqDetail;
	private JCheckBox[] ls;

	public static void main(String[] args) {
		new FileManagerFrame().run(args);
	}

	public void run(final String[] arg) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {

				PrefParam.FilestmpExportNifit = null;
				PrefParam.MIA = false;
				PrefParam.CloseAfterExport = false;
				PrefParam.LogExport = true;
				PrefParam.OptionLookAndFeel = true;
				PrefParam.ExitSystem = true;
				PrefParam.projectsDir = "";
				PrefParam.DirectoryDataOnly = "";
				PrefParam.namingOptionsNiftiExport = "00000";
				PrefParam.namingOptionsNiftiExportMIA = "00000";
				PrefParam.labelButtonExport="export to ";
				PrefParam.returnCodeExit=100;
				
				for (String jj : arg) {
					String tm;
					if (jj.contains("[ExportNifti]")) {
						tm = jj.substring(jj.indexOf("]") + 1);
						tm = tm.trim();
						PrefParam.FilestmpExportNifit = new File(tm);
						tm = "";
					} else if (jj.contains("ExportToMIA")) {
						PrefParam.MIA = true;
						tm = jj.substring(jj.indexOf("]") + 1);
						tm = tm.trim();
						PrefParam.namingFileNiftiExportMIA = tm;
						PrefParam.labelButtonExport="export to MIA";
					} else if (jj.contains("ExportToMP3")) {
						PrefParam.MIA = true;
						tm = jj.substring(jj.indexOf("]") + 1);
						tm = tm.trim();
						PrefParam.namingFileNiftiExportMIA = tm;
						PrefParam.labelButtonExport="export to MP3";
					} 
					else if (jj.contains("CloseAfterExport")) {
						PrefParam.CloseAfterExport = true;
					} else if (jj.contains("NoLogExport")) {
						PrefParam.LogExport = false;
					} else if (jj.contains("[LookAndFeel]")) {
						tm = jj.substring(jj.indexOf("]") + 1);
						tm = tm.trim();
						PrefParam.LookFeelCurrent = tm;
						PrefParam.OptionLookAndFeel = false;
					} else if (jj.contains("NoExitSystem")) {
						PrefParam.ExitSystem = false;
					} else if (jj.contains("[ExportOptions]")) {
						tm = jj.substring(jj.indexOf("]") + 1);
						tm = tm.trim();
						for (int i=tm.length();i<5;i++) {
							tm+="0";
						}
						PrefParam.namingOptionsNiftiExportMIA = tm;
					} else if (jj.contains("ProjectsDir")) {
						tm = jj.substring(jj.indexOf("]") + 1);
						tm = tm.trim();
						PrefParam.projectsDir=tm;
					}					
				}

				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

				WScreen = (int) screenSize.getWidth();
				HScreen = (int) screenSize.getHeight();

				PrefParam.widthScreen = WScreen;
				PrefParam.heightScreen = HScreen;

				try {
					init();
					buildBug();
					MriFilesWindow();
					buildBasketWindow();
					buildDetailDisplaySeq();
					buildProgress();
					lookFeel();
					buildAbout();

				} catch (Exception e) {
					new GetStackTrace(e);
				}
			}
		});
	}

	private void init() {

		PrefParam.separator = File.separator;

		URL URLBruker = getClass().getResource("/BrukerLogo32.jpg");
		PrefParam.iconBruker = new ImageIcon(URLBruker);
		URL URLDicom = getClass().getResource("/DicomLogo32.jpg");
		PrefParam.iconDicom = new ImageIcon(URLDicom);
		URL URLPhilips = getClass().getResource("/PhilipsLogo32.jpg");
		PrefParam.iconPhilips = new ImageIcon(URLPhilips);
		URL URLNifti = getClass().getResource("/NifTILogo32.jpg");
		PrefParam.iconNifTI = new ImageIcon(URLNifti);
		URL URLBids = getClass().getResource("/BIDSLogo32.jpg");
		PrefParam.iconBids = new ImageIcon(URLBids);

		PrefParam.lectBruker = " ";
		if (PrefParam.LookFeelCurrent == null)
			PrefParam.LookFeelCurrent = UIManager.getCrossPlatformLookAndFeelClassName();
		PrefParam.urlLookAndFeel[0] = PrefParam.LookFeelCurrent;

		new PrefParamLoad(); // paths file in FilestmpRep.txt

		if (PrefParam.MIA) {
			PrefParam.namingRepNiftiExport = PrefParam.separator;
			PrefParam.namingFileNiftiExport = PrefParam.namingFileNiftiExportMIA;
			PrefParam.namingOptionsNiftiExport = PrefParam.namingOptionsNiftiExportMIA;
			if (!PrefParam.projectsDir.isEmpty()) {
				PrefParam.lectBruker=PrefParam.projectsDir;
				PrefParam.lectDicom=PrefParam.projectsDir;
				PrefParam.lectParRec=PrefParam.projectsDir;
				PrefParam.lectNifTI=PrefParam.projectsDir;
				PrefParam.lectBids=PrefParam.projectsDir;
				PrefParam.lectCurrent=PrefParam.projectsDir;
			}
		}
		new ProtocolsBidsYaml(UtilsSystem.pathOfJar() + "Modalities_BIDS.yml"); 
	}

	private void buildBug() {

		winBug = new JFrame();
		winBug.setTitle("Error window");
		winBug.setLocationRelativeTo(null);
		winBug.setResizable(true);
		winBug.setSize(WScreen / 4, HScreen / 5);

		JPanel panel4 = new JPanel();

		panel4.setLayout(new FlowLayout());
		panel4.setBackground(Color.GRAY);
		textBug = new JTextArea("empty for instant");
		textBug.setEditable(false);

		JScrollPane scrollPaneArea = new JScrollPane(textBug);
		panel4.setLayout(new BorderLayout());
		panel4.add(scrollPaneArea, BorderLayout.CENTER);
		winBug.setContentPane(panel4);
	}

	private void MriFilesWindow() {

		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setLocationRelativeTo(null);
		// setExtendedState(JFrame.MAXIMIZED_BOTH);
		setTitle("MRI Files Manager (IRMaGe) - "+versionSoft);
		setSize(new Dimension(WScreen, HScreen));
		setResizable(true);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		// setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				// if (JOptionPane.showConfirmDialog(null,
				// "Are you sure to close this window?", "Really Closing?",
				// JOptionPane.YES_NO_OPTION,
				// JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				if (PrefParam.ExitSystem)
					System.exit(PrefParam.returnCodeExit);
				else
					dispose();
			}
		});

		setVisible(true);
		addComponentListener(this);

		JMenuBar menu_bar1 = new JMenuBar();

		JMenu file = new JMenu("File");
		JMenu openf = new JMenu("Open");
		JMenu exportf = new JMenu("export");
		JMenu inform = new JMenu("Information");
		JMenu pref = new JMenu("Preference");
		JMenu option = new JMenu("Option");
		JMenu tools = new JMenu("Tools");

		JMenuItem openBruker = new JMenuItem(new ActionsButtonMenu(this, "Open Bruker (PV5,PV6)"));
		openBruker.setIcon(PrefParam.iconBruker);
		JMenuItem openDicom = new JMenuItem(new ActionsButtonMenu(this, "Open Dicom"));
		openDicom.setIcon(PrefParam.iconDicom);
		JMenuItem openParRec = new JMenuItem(new ActionsButtonMenu(this, "Open Philips Achieva"));
		openParRec.setIcon(PrefParam.iconPhilips);
		JMenuItem openNifti = new JMenuItem(new ActionsButtonMenu(this, "Open NifTI"));
		openNifti.setIcon(PrefParam.iconNifTI);
		JMenuItem openBids = new JMenuItem(new ActionsButtonMenu(this, "Open Bids"));
		openBids.setIcon(PrefParam.iconBids);
		
		JMenuItem quit = new JMenuItem(new ActionsButtonMenu(this, "Quit"));

		JMenuItem optionExport = new JMenuItem(new ExportFilesOption(this, "Option export"));
		if (PrefParam.MIA)
			optionExport.setEnabled(false);

		exportf.add(optionExport);

		JMenuItem help = new JMenuItem(new ActionsButtonMenu(this, "Help"));
		JMenuItem about = new JMenuItem(new ActionsButtonMenu(this, "About.."));
		JMenuItem bug = new JMenuItem(new ActionsButtonMenu(this, "Error window"));
		JMenuItem repwork = new JMenuItem(new ActionsButtonMenu(this, "Current working directory"));
		JMenu lookAfeel = new JMenu("Look and Feel");

		ButtonGroup group = new ButtonGroup();
		JCheckBoxMenuItem menuLaF;

		for (String lf : PrefParam.nameLookAndFeel) {
			menuLaF = new JCheckBoxMenuItem(lf);
			menuLaF.addItemListener(this);
			group.add(menuLaF);
			lookAfeel.add(menuLaF);
		}
		
		showpreview = new JCheckBoxMenuItem("show image preview");
		showpreview.setSelected(PrefParam.previewActived);
		showpreview.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrefParam.previewActived=showpreview.getState();
				new PrefParamModif();
			}
		});

		showicon = new JCheckBoxMenuItem("show icons in the file chooser dialog");
		showicon.setSelected(false);
		
		simplifiedDicom = new JCheckBoxMenuItem("simplified view for DICOMDIR (faster)");
		simplifiedDicom.setSelected(true);
		PrefParam.simplifiedViewDicom=true;
		simplifiedDicom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PrefParam.simplifiedViewDicom=simplifiedDicom.getState();
			}
		});
		
		JMenuItem openImagej = new JMenuItem(new ActionsButtonMenu(this, "Open ImageJ"));

		file.add(openf);
		file.addSeparator();
		file.add(exportf);
		file.addSeparator();
		file.add(quit);
		inform.add(help);
		inform.add(about);
		openf.add(openBruker);
		openf.add(openDicom);
		openf.add(openParRec);
		openf.add(openNifti);
		openf.add(openBids);
		pref.add(showpreview);
		pref.add(showicon);
		pref.add(simplifiedDicom);
		option.add(bug);
		option.add(repwork);
		if (PrefParam.OptionLookAndFeel)
			option.add(lookAfeel);
		tools.add(openImagej);

		menu_bar1.add(file);
		menu_bar1.add(inform);
		menu_bar1.add(pref);
		menu_bar1.add(option);
		menu_bar1.add(tools);
		menu_bar1.add(Box.createHorizontalGlue());

		this.setJMenuBar(menu_bar1);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.LIGHT_GRAY);

		JPanel panelHead = new JPanel();
		panelHead.setLayout(new BoxLayout(panelHead, BoxLayout.Y_AXIS));
		panelHead.setBorder(new EmptyBorder(0, 0, 10, 0));
		
		JPanel panelButton = new JPanel();

		JButton boutonBruker = new JButton(new ActionsButtonMenu(this, "Bruker"));
		JButton boutonDicom = new JButton(new ActionsButtonMenu(this, "Dicom"));
		JButton boutonParRec = new JButton(new ActionsButtonMenu(this, "Philips Achieva"));
		JButton boutonNifTI = new JButton(new ActionsButtonMenu(this, "NifTI"));
		JButton boutonBids = new JButton(new ActionsButtonMenu(this, "Bids"));

		boutonBruker.setIcon(PrefParam.iconBruker);
		boutonBruker.setPreferredSize(new Dimension(130, 25));
		boutonDicom.setIcon(PrefParam.iconDicom);
		boutonDicom.setPreferredSize(new Dimension(150, 25));
		boutonParRec.setIcon(PrefParam.iconPhilips);
		boutonParRec.setPreferredSize(new Dimension(180, 25));
		boutonNifTI.setIcon(PrefParam.iconNifTI);
		boutonNifTI.setPreferredSize(new Dimension(110, 25));
		boutonBids.setIcon(PrefParam.iconBids);
		boutonBids.setPreferredSize(new Dimension(110, 25));

		boutonBruker.setToolTipText("Paravision 5 & 6");
		boutonDicom.setToolTipText("<html>" + "DICOMDIR" + "<br>" + "DIRFILE" + "<br>" + "*.dcm, *.ima" + "</html>");
		boutonParRec.setToolTipText("<html>" + "Par/Rec V4, V4.1, V4.2" + "<br>" + "Xml/Rec V5" + "</html>");
		boutonNifTI.setToolTipText("Nifti-1");
		boutonBids.setToolTipText("Bids");

		boutonDicom.setEnabled(true);

		panelButton.add(boutonBruker);
		panelButton.add(boutonDicom);
		panelButton.add(boutonParRec);
		panelButton.add(boutonNifTI);
		panelButton.add(boutonBids);

		listPath = new JComboBox<>();
		listPath.addActionListener(new ActionsButtonMenu(this, "comboboxChanged"));
		listPath.setMaximumSize(new Dimension(700,30));
		listPath.setEnabled(true);
		listPath.setEditable(false);
		
		panelHead.add(panelButton);
		panelHead.add(listPath);

		tabData = new DataList(new Object[20][ParamMRI2.headerListData.length]).getTableData();
		tabData.setSelectionBackground(Color.ORANGE);
		tabData.setEnabled(false);

		tabData.addMouseListener(new ActionSelectionData(this, ""));
		tabData.addKeyListener(new ActionSelectionData(this, ""));
		treeData = new JScrollPane(tabData);

		JPanel panSeq = new JPanel();
		panSeq.setBackground(Color.WHITE);

		tabSeq = new SeqList(new Object[20][ParamMRI2.headerListSeq.length]).getTableSeq();
		tabSeq.setSelectionBackground(Color.ORANGE);
		tabSeq.getTableHeader().addMouseListener(new ActionSelectionSeq(this));
		tabSeq.setEnabled(false);
		tabSeq.addMouseListener(new ActionSelectionSeq(this));
		tabSeq.addKeyListener(new ActionSelectionSeq(this));
		tabSeq.getSelectionModel().addListSelectionListener(new ActionSelectionSeq(this));
		JScrollPane treeSeq = new JScrollPane(tabSeq);

		boxThumb = new Box(BoxLayout.Y_AXIS);
		boxThumb.setBackground(Color.WHITE);

		thumb = new JScrollPane(boxThumb);

		try {
			if (!PrefParam.OptionLookAndFeel)
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			treeParamInfoGeneral = new TreeInfo2(ParamMRI2.listParamInfoSystem, null).getTreeInfo();
			treeParamInfoGeneral.setFocusable(false);
			treeParamInfoUser = new TreeInfo2(ParamMRI2.listParamInfoUser, null).getTreeInfo();
			treeParamInfoUser.setFocusable(false);

			treeParamInfoGeneral.updateUI();
			treeParamInfoUser.updateUI();

			if (!PrefParam.OptionLookAndFeel) {
				UIManager.setLookAndFeel(PrefParam.LookFeelCurrent);
			}
		} catch (Exception e) {

		}


		JScrollPane treeInfoGeneral = new JScrollPane(treeParamInfoGeneral);
		JScrollPane treeInfoUser = new JScrollPane(treeParamInfoUser);

		tabInfoParam.add("Info param.", treeInfoGeneral);
		tabInfoParam.add("User param.", treeInfoUser);

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setMaximumSize(new Dimension(10 * WScreen / 100, 4 * HScreen / 100));

		boxImage = new Box(BoxLayout.Y_AXIS);
		boxImage.setPreferredSize(new Dimension(400, 220));
		
		if (PrefParam.previewActived) {
			boxImage.add(new ImagePanel(null));
		}
		else {
			JLabel labInf = new JLabel("<html><center><br><br><h2 style=\"color:orange;\">Preview disabled</h2><h3 style=\"color:orange;\">(see preference to activate)</h3><br><br></center></html>");
			JPanel panLab = new JPanel();
			panLab.add(labInf);
			boxImage.add(panLab);
		}

		slidImage1 = new JSlider();
		slidImage1.setValue(1);
		slidImage1.setMinimumSize(new Dimension(WScreen * 8 / 100, 3 * HScreen / 100));
		slidImage1.setMaximumSize(new Dimension(WScreen * 8 / 100, 3 * HScreen / 100));
		slidImage1.setEnabled(false);
		slidImage2 = new JSlider();
		slidImage2.setValue(1);
		slidImage2.setMinimumSize(new Dimension(WScreen * 8 / 100, 3 * HScreen / 100));
		slidImage2.setMaximumSize(new Dimension(WScreen * 8 / 100, 3 * HScreen / 100));
		slidImage2.setEnabled(false);
		slidImage3 = new JSlider();
		slidImage3.setValue(1);
		slidImage3.setMinimumSize(new Dimension(WScreen * 8 / 100, 3 * HScreen / 100));
		slidImage3.setMaximumSize(new Dimension(WScreen * 8 / 100, 3 * HScreen / 100));
		slidImage3.setEnabled(false);

		c = new JLabel();
		c.setMinimumSize(new Dimension(3 * WScreen / 100, 3 * HScreen / 100));
		c.setMaximumSize(new Dimension(3 * WScreen / 100, 3 * HScreen / 100));
		c.setText("0");

		z = new JLabel();
		z.setMinimumSize(new Dimension(3 * WScreen / 100, 3 * HScreen / 100));
		z.setMaximumSize(new Dimension(3 * WScreen / 100, 3 * HScreen / 100));
		z.setText("0");

		t = new JLabel();
		t.setMinimumSize(new Dimension(3 * WScreen / 100, 3 * HScreen / 100));
		t.setMaximumSize(new Dimension(3 * WScreen / 100, 3 * HScreen / 100));
		t.setText("0");

		JPanel panPreview = new JPanel();
		panPreview.setLayout(new BoxLayout(panPreview, BoxLayout.Y_AXIS));
		panPreview.setPreferredSize(new Dimension(250,310));
	
		Box boxLab = new Box(BoxLayout.Y_AXIS);
		Box boxSlid = new Box(BoxLayout.Y_AXIS);

		boxLab.add(c);
		boxSlid.add(slidImage1);
		boxLab.add(z);
		boxSlid.add(slidImage2);
		boxLab.add(t);
		boxSlid.add(slidImage3);
		
		JPanel panelSlid = new JPanel();
		panelSlid.setLayout(new BoxLayout(panelSlid, BoxLayout.X_AXIS));
		
		panelSlid.add(boxSlid);
		panelSlid.add(boxLab);

		panPreview.add(boxImage);
		panPreview.add(panelSlid);
		panPreview.setBorder(BorderFactory.createTitledBorder("Image Preview"));
		
		JPanel pan = new JPanel();
		pan.add(panPreview, BorderLayout.CENTER);

		preview = new JScrollPane(pan);

		JPanel panCategory = new JPanel();
		panCategory.setBackground(Color.GRAY);
		JLabel labCat = new JLabel("Category list");
		panCategory.add(labCat);

		JPanel panGroupCategory = new JPanel();
		panGroupCategory.setBackground(Color.GRAY);
		JLabel labGoC = new JLabel("Group of category list");
		panGroupCategory.add(labGoC);

		WFrame = this.getWidth();
		HFrame = this.getHeight();
		
		split1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, treeData, treeSeq);
		split1.setDividerLocation(HFrame * 60 / 100);
		split1.setDividerSize(5);

				
		split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, tabInfoParam, preview);
		split2.setDividerLocation(HFrame * 60 / 100);
		split2.setDividerSize(5);
		
		split3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, split1, split2);
		split3.setDividerLocation(WFrame * 70 / 100);
		split3.setDividerSize(5);

		split4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, thumb, split3);
		split4.setDividerLocation(WFrame * 10 / 100);
		split4.setDividerSize(10);

		panel.add(panelHead, BorderLayout.NORTH);
		panel.add(split4, BorderLayout.CENTER);

		tabPrincipal.add("Home", panel);

		getContentPane().add(tabPrincipal);

	}

	private void buildBasketWindow() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		treeBasket = new JTree(new DefaultTreeModel(new DefaultMutableTreeNode("(your exportation folder)")));
		treeBasket.setExpandsSelectedPaths(false);
		treeBasket.setSelectionModel(null);

		listOfBasket = new JList<String>();
		
		JScrollPane scrollTreeBasket = new JScrollPane(treeBasket); 

		JScrollPane scrollBasket = new JScrollPane(listOfBasket);
		scrollBasket.setPreferredSize(new Dimension(this.getWidth() - 100, this.getHeight() - 300));
		scrollBasket.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel buttonBasket = new JPanel();
		JPanel panelRepNifti = new JPanel();

		JLabel labExport = new JLabel("Exportation Folder : ");
		panelRepNifti.add(labExport);
		pathExportNifti = new JLabel();
		
		if (PrefParam.FilestmpExportNifit != null)
			pathExportNifti.setText(PrefParam.FilestmpExportNifit.toString());
		JScrollPane scrollRepNifti = new JScrollPane(pathExportNifti);
		scrollRepNifti.setPreferredSize(new Dimension(40 * WScreen / 100, 5 * HScreen / 100));
		panelRepNifti.add(scrollRepNifti);
		JButton changeRepNifti = new JButton(new BasketManager(this, "Change"));
		if (PrefParam.MIA)
			changeRepNifti.setEnabled(false);
		panelRepNifti.add(changeRepNifti);
		
		String[] elements = {"Nifti-1","BIDS"};
		listChoiceExport = new JComboBox<String>(elements);
		listChoiceExport.addActionListener(new ChoiceFormatExport(this));
		listChoiceExport.setPrototypeDisplayValue(
				"________________");

		JButton exportNifti = new JButton(new BasketManager(this, PrefParam.labelButtonExport));
		JButton remove = new JButton(new BasketManager(this, "remove selection"));
		JButton removeAll = new JButton(new BasketManager(this, "remove all"));

		buttonBasket.add(exportNifti);
		buttonBasket.add(listChoiceExport);
		buttonBasket.add(remove);
		buttonBasket.add(removeAll);
		
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		panel.add(buttonBasket, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1;
		panel.add(panelRepNifti, c);
		
		split5 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollTreeBasket, scrollBasket);
		split5.setDividerLocation(HFrame * 30 / 100);
		split5.setDividerSize(5);
		
		split6 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, split5, panel);
		split6.setDividerLocation(HFrame * 75 / 100);
		split6.setDividerSize(5);
		
		tabPrincipal.add("Basket (Empty)", split6);
	}

	private void buildProgress() {
		dlg = new JDialog(this, "Progress dialog ...", false);
		dlg.add(BorderLayout.NORTH, new JLabel("Export in progress, wait..."));
		dlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dlg.setSize(400, 75);
		dlg.setLocationRelativeTo(this);
	}

	private void buildAbout() {
	
		String HTMLTEXT = "<html><head><style>"
							+ ".title{font-family:Times ; color:blue ; font-size : 10px ; font-weight : bold ; text-decoration: underline} "
							+ ".name{font-family:tahoma ; color:green ; font-size : 10px ; font-weight : bold}"
							+ "</style></head>"
								+ "<span style=\"font-size : 12px ; font-weight:bold; color:dark\"> Version : "+versionSoft+"</span><br><br>"
								+ "<span class=title>Developer</span> : <br>"
								+ "<span class=name>Olivier MONTIGON (IRMaGe - Grenoble)</span><br><br>"
								+ "<span class=title>Conceptors</span> : <br>"
								+ "<span class=name>Emmanuel BARBIER (GIN5 - Grenoble)</span><br>"
								+ "<span class=name>Eric CONDAMINE (IRMaGe - Grenoble)</span><br>"
								+ "<span class=name>Paul GALLOUX (IRMaGe - Grenoble)</span><br>"
								+ "<span class=name>Benjamin LEMASSON (GIN5 - Grenoble)</span><br>"
								+ "<span class=name>Jan WARNKING (GIN5 - Grenoble)</span><br><br>"
								+ "<span class=title>Contributors</span> : <br>"
								+ "<span class=name>Cl&eacute;ment BROSSARD</span><br>"
								+ "<span class=name>David HARBINE</span><br>"
								+ "<span class=name>Herv&eacute; MATHIEU (IRMaGe - Grenoble)</span><br>"
								+ "<span class=name>Lucie OUVRIER-BUFFET</span><br>"
								+ "<span class=name>Diego ALVES RODRIGUES DE SOUZA</span><br>"
								+ "<span class=name>Cl&eacute;ment ACQUITTER</span><br>"
								+ "</html>"
								;
		
//		String linkText = "<html><head><style type=\"text/css\">"
//							+ ".title{font-family:Times ; color:blue ; font-size : 12px ; font-weight : bold } "
//							+ ".txt{font-family:Times ; color:black ; font-size : 10px ; font-weight : bold } "
//							+ ".link{font-family:Times ; color:blue ; font-size : 10px ; font-weight : bold ; text-decoration: underline } "
//							+ "</style></head>"
//								+ "<span class=title>IRMaGe</span> : <br/>"
//								+ "<span class=txt>CHU Grenoble Alpes<br/>"
//								+ "<span class=txt>CNRS UMS 3552<br/>"
//								+ "<span class=txt>Inserm US 17<br/>"
//								+ "<span class=txt>Universit� Grenoble Alpes<br/>"
//								+ "<span class=link>https://irmage.ujf-grenoble.fr/<br/>"
//								;
		
		String linkText = "<html><head><style>"
				+ ".title{font-family:Times ; color:blue ; font-size : 12px ; font-weight : bold } "
				+ ".txt{font-family:Times ; color:black ; font-size : 10px ; font-weight : bold } "
				+ ".link{font-family:Times ; color:blue ; font-size : 10px ; font-weight : bold ; text-decoration: underline } "
				+ "</style></head>"
					+ "<table style:width:100%>"
					+ "<tr>"
					+ "<th>"
					+ "<span class=title>IRMaGe</span> : <br>"
					+ "<span class=txt>CHU Grenoble Alpes<br>"
					+ "<span class=txt>CNRS UMS 3552<br>"
					+ "<span class=txt>Inserm US 17<br>"
					+ "<span class=txt>Universit&eacute; Grenoble Alpes<br>"
					+ "<a href=\"https://irmage.ujf-grenoble.fr//\"><span class=link>https://irmage.ujf-grenoble.fr<br>"
					+ "</th>"
					+ "<th>"
					+ "<span>&nbsp;&nbsp;&nbsp;"
					+ "</th>"
					+ "<th>"
					+ "<span class=title>GIN</span> : <br>"
					+ "<span class=txt>Grenoble-Institut des Neurosciences<br>"
					+ "<a href=\"https://neurosciences.univ-grenoble-alpes.fr//\"><span class=link>https://neurosciences.univ-grenoble-alpes.fr</a><br>"
					+ "</th>"
					+ "</tr>"
					+ "</table>"
//					+ "<br> <br>"
//					+ "<p style=\"text-align:center\"><a href=\"file://"+UtilsSystem.pathOfJar()+"FilestmpRep.txt//\">see the version history</a></p><br>"
					+ "</html>"
					;

		about = new JFrame();
		about.setTitle("About MRIConv");
		about.setLocationRelativeTo(this);
		about.setResizable(false);
		
		JPanel panelAbout = new JPanel();
		panelAbout.setLayout(new BoxLayout(panelAbout, BoxLayout.Y_AXIS));
		
		JPanel panelTop = new JPanel();
		JPanel panelBottom = new JPanel();
		
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		URL URLLogoIrmage = getClass().getResource("/LogoM.jpg");
		ImageIcon shIrmage = new ImageIcon(new ImageIcon(URLLogoIrmage).getImage().getScaledInstance(WScreen / 8, HScreen / 4, Image.SCALE_DEFAULT));

		JLabel shImg = new JLabel();
		shImg.setIcon(shIrmage);
		left.add(shImg);

		JPanel right = new JPanel();
		JTextPane txtpane = new JTextPane();
		txtpane.setEditable(false);
		txtpane.setContentType("text/html");
		txtpane.setText(HTMLTEXT);
		txtpane.setCaretPosition(0);
		JScrollPane scrp = new JScrollPane(txtpane);
		scrp.setPreferredSize(new Dimension(WScreen / 4, HScreen / 4));
		right.add(new JScrollPane(scrp));
				
		panelTop.add(left);
		panelTop.add(right);
		
		JTextPane txtLink = new JTextPane();
		txtLink.setEditable(false);
		txtLink.setContentType("text/html");
		txtLink.setOpaque(true);
		txtLink.setText(linkText);
		
		panelBottom.add(txtLink);
		
		panelAbout.add(panelTop);
		panelAbout.add(panelBottom);

		about.getContentPane().add(panelAbout);
		about.pack();
	}
	
	private void lookFeel() {
		try {
			UIManager.setLookAndFeel(PrefParam.LookFeelCurrent);
			SwingUtilities.updateComponentTreeUI(this);

		} catch (Exception e) {
			new GetStackTrace(e);
		}
	}

	private void buildDetailDisplaySeq() {

		popupMenuDetailSeq = new JPopupMenu();

		itemSeqDetail = new JCheckBoxMenuItem[ParamMRI2.headerListSeq.length];

		for (int i = 0; i < 5; i++) {
			itemSeqDetail[i] = new JCheckBoxMenuItem(ParamMRI2.headerListSeq[i]);
			if (i == 0)
				itemSeqDetail[i].setEnabled(false);
			itemSeqDetail[i].setSelected(true);
			itemSeqDetail[i].addActionListener(new ChangeSeqDetail(this, ""));
			popupMenuDetailSeq.add(itemSeqDetail[i]);
		}

		JMenuItem otherDetail = new JMenuItem("Other detail");
		otherDetail.addActionListener(new ChangeSeqDetail(this, "showdetail"));
		popupMenuDetailSeq.add(otherDetail);

		/*********************************************************************/
		frameDetailSeq = new JFrame("Detail Sequence");
		frameDetailSeq.setSize(300, 400);
		frameDetailSeq.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frameDetailSeq.setLocationRelativeTo(this);
		frameDetailSeq.setAlwaysOnTop(true);

		ls = new JCheckBox[ParamMRI2.headerListSeq.length];
		JPanel p = new JPanel();
		p.setSize(600, 400);
		p.setBackground(Color.WHITE);
		BoxLayout boxLayout = new BoxLayout(p, BoxLayout.Y_AXIS);
		p.setLayout(boxLayout);
		for (int i = 0; i < ParamMRI2.headerListSeq.length; i++) {
			ls[i] = new JCheckBox(ParamMRI2.headerListSeq[i]);
			if (i < 5)
				if (this.itemSeqDetail[i].isSelected())
					ls[i].setSelected(true);
				else
					ls[i].setSelected(false);
			if (i == 0)
				ls[i].setEnabled(false);
			else
				ls[i].setSelected(true);
			ls[i].addItemListener(new ChangeSeqDetail(this, ""));
			p.add(ls[i]);
		}

		JScrollPane scrollpane = new JScrollPane(p);
		frameDetailSeq.getContentPane().add(scrollpane, BorderLayout.CENTER);

	}

	/***************************** accesseurs ****************************/

	public JTable getTabData() {
		return tabData;
	}

	public JTable getTabSeq() {
		return tabSeq;
	}

	public JTree getTreeInfoGeneral() {
		return treeParamInfoGeneral;
	}

	public JTree getTreeInfoUser() {
		return treeParamInfoUser;
	}

	public JScrollPane getPreview() {
		return preview;
	}

	public JScrollPane getScrollThumb() {
		return thumb;
	}
	
	public Box getBoxThumb() {
		return boxThumb;
	}

	public JScrollPane getScrollTabData() {
		return treeData;
	}

	public JComboBox<String> getListPath() {
		return listPath;
	}
	
	public JComboBox<String> getChoiceExport() {
		return listChoiceExport;
	}

	public static JTextArea getBugText() {
		return textBug;
	}

	public JFrame getFenBug() {
		return winBug;
	}

	public Box getBoxImage() {
		return boxImage;
	}

	public JSlider[] getSlidImage() {
		JSlider[] listSlid = new JSlider[3];
		listSlid[0] = slidImage1;
		listSlid[1] = slidImage2;
		listSlid[2] = slidImage3;
		return listSlid;
	}

	public JLabel[] getFieldSlid() {
		JLabel[] listFieldSlid = new JLabel[3];
		listFieldSlid[0] = c;
		listFieldSlid[1] = z;
		listFieldSlid[2] = t;
		return listFieldSlid;
	}

	public JList<String> getListBasket() {
		return listOfBasket;
	}
	
	public JTree getTreeBasket() {
		return treeBasket;
	}

	public JLabel getpathExportNifti() {
		return pathExportNifti;
	}

	public JCheckBoxMenuItem getIconCheck() {
		return showicon;
	}
	
	public JCheckBoxMenuItem getSimplifiedDicomCheck() {
		return simplifiedDicom;
	}

	public JFrame getFramDetailSeq() {
		return frameDetailSeq;
	}
	
	public JFrame getAboutDialog() {
		return about;
	}

	public JPopupMenu getMenuCheckDisplaySeq() {
		return popupMenuDetailSeq;
	}

	public JCheckBoxMenuItem[] getCheckDisplaySeq() {
		return itemSeqDetail;
	}

	public JCheckBox[] getCheckDisplaySeqinWindow() {
		return ls;
	}

	public JTabbedPane getTabbedPane() {
		return tabPrincipal;
	}

	public JTable getDictionnaryJTable() {
		return tabDictionnaryMri;
	}

	public JLabel getPathDictionary() {
		return pathDictionnary;
	}

	/***************************** reset ****************************/

	public void resetTabSeq() {

		PrefParam.listWidthColumn = new int[tabSeq.getColumnCount()];
		for (int i = 0; i < tabSeq.getColumnCount(); i++)
			PrefParam.listWidthColumn[i] = tabSeq.getColumnModel().getColumn(i).getWidth();

		// getTabSeq().setEnabled(true);
		TableModel model20;
		Object[][] data = { { "", "", "", "", "", "", "", "", "", "", "", "" , "", ""} };
		String[] columnNames = ParamMRI2.headerListSeq;
		model20 = new TableModel(data, columnNames);
		TableRowSorter<TableModel> sorter0 = new TableRowSorter<>(model20);
		tabSeq.setModel(model20);
		tabSeq.setRowSorter(sorter0);

		new ChangeSeqDetail(this);
		tabSeq.setEnabled(false);

	}

	public void resetTabData() {
		TableModel model10;
		Object[][] data = { { "", "", "", "", "", "", "", "" ,""} };
		String[] columNames = ParamMRI2.headerListData;
		model10 = new TableModel(data, columNames);
		TableRowSorter<TableModel> sorter0 = new TableRowSorter<>(model10);
		tabData.setModel(model10);
		tabData.setRowSorter(sorter0);
		tabData.updateUI();
		tabData.setEnabled(false);
	}
	
	public void resetBoxImage() {
		boxImage.removeAll();
		boxImage.updateUI();
		JLabel labInf = new JLabel("<html><center><br><br><h2 style=\"color:orange;\">Preview disabled</h2><h3 style=\"color:orange;\">(see preference to activate)</h3><br><br></center></html>");
		JPanel panLab = new JPanel();
		panLab.add(labInf);
		boxImage.add(panLab);

		for (int i = 0; i < 3; i++) {
			getSlidImage()[i].setEnabled(false);
			getSlidImage()[i].setMinimum(1);
			getSlidImage()[i].setMaximum(1);
			getSlidImage()[i].setValue(1);
			getFieldSlid()[i].setText("0      ");
		}
		getPreview().updateUI();
	}

	public int[] widthColumnJtable(JTable tab) {
		int nb = tab.getColumnCount();
		int[] listW = new int[nb];
		for (int i = 0; i < nb; i++) {
			listW[i] = tab.getColumnModel().getColumn(i).getWidth();
		}
		return listW;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBoxMenuItem check = (JCheckBoxMenuItem) e.getSource();
		if (check.isSelected()) {
			try {
				for (int i = 0; i < PrefParam.nameLookAndFeel.length; i++) {
					if (PrefParam.nameLookAndFeel[i].contains(check.getText())) {
						UIManager.setLookAndFeel(PrefParam.urlLookAndFeel[i]);
						PrefParam.LookFeelCurrent = PrefParam.urlLookAndFeel[i];
						break;
					}
				}
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception e1) {
			}

			new PrefParamModif();
			JOptionPane.showMessageDialog(this, "Please restart MRI Files Manager manually");
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		dlg.setLocationRelativeTo(this);
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}
}
