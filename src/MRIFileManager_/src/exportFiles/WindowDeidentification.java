package exportFiles;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import MRIFileManager.FileManagerFrame;
import abstractClass.PrefParam;


public class WindowDeidentification extends PrefParam {
	
	JTextField answPatientName,answStudyName,answAge,answSex,answWeight;
	String answButton;
	final JDialog drep;
	
	public WindowDeidentification(FileManagerFrame wind,String direct,String patientName,String studyName,String age,String sex,String weight) {
		
		String title="";
		if (direct!="")
			title = "for "+direct;
			
		drep = new JDialog(wind,"De-identification "+title,true);
		drep.setUndecorated(true);
		drep.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		JPanel panelDialog = new JPanel();
		panelDialog.setLayout(new BoxLayout(panelDialog, BoxLayout.Y_AXIS));
		panelDialog.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		drep.add(panelDialog);

		/************************** Patient Name ******************************************************/

		JPanel panelPatientName = new JPanel();
		panelPatientName.setLayout(new BoxLayout(panelPatientName, BoxLayout.LINE_AXIS));
		JLabel labelPatientName = new JLabel("Patient Name : ", SwingConstants.CENTER);
		answPatientName = new JTextField();
		answPatientName.setEditable(true);
		answPatientName.setPreferredSize(new Dimension(widthScreen / 4, 25));
		answPatientName.setAlignmentX(Component.CENTER_ALIGNMENT);
		answPatientName.setText(patientName);
		
		panelPatientName.add(labelPatientName);
		panelPatientName.add(answPatientName);
		panelPatientName.add(Box.createHorizontalStrut(5));
		panelPatientName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/************************** Study Name ******************************************************/

		JPanel panelStudyName = new JPanel();
		panelStudyName.setLayout(new BoxLayout(panelStudyName, BoxLayout.LINE_AXIS));
		JLabel labelStudyName = new JLabel("Study Name : ", SwingConstants.CENTER);
		answStudyName = new JTextField();
		answStudyName.setEditable(true);
		answStudyName.setPreferredSize(new Dimension(widthScreen / 4, 25));
		answStudyName.setAlignmentX(Component.CENTER_ALIGNMENT);
		answStudyName.setText(studyName);
		
		panelStudyName.add(labelStudyName);
		panelStudyName.add(answStudyName);
		panelStudyName.add(Box.createHorizontalStrut(5));
		panelStudyName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


		/************************** Age ******************************************************/

		JPanel panelAge = new JPanel();
		panelAge.setLayout(new BoxLayout(panelAge, BoxLayout.LINE_AXIS));
		JLabel labelAge = new JLabel("Age :          ", SwingConstants.CENTER);
		answAge = new JTextField();
		answAge.setEditable(true);
		answAge.setPreferredSize(new Dimension(widthScreen / 4, 25));
		answAge.setAlignmentX(Component.CENTER_ALIGNMENT);
		answAge.setText(age);
		
		panelAge.add(labelAge);
		panelAge.add(answAge);
		panelAge.add(Box.createHorizontalStrut(5));
		panelAge.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/************************** Sex ******************************************************/

		JPanel panelSex = new JPanel();
		panelSex.setLayout(new BoxLayout(panelSex, BoxLayout.LINE_AXIS));
		JLabel labelSex = new JLabel("Sex :          ", SwingConstants.CENTER);
		answSex = new JTextField();
		answSex.setEditable(true);
		answSex.setPreferredSize(new Dimension(widthScreen / 4, 25));
		answSex.setAlignmentX(Component.CENTER_ALIGNMENT);
		answSex.setText(sex);
		
		panelSex.add(labelSex);
		panelSex.add(answSex);
		panelSex.add(Box.createHorizontalStrut(5));
		panelSex.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/************************** Weight ******************************************************/

		JPanel panelWeight = new JPanel();
		panelWeight.setLayout(new BoxLayout(panelWeight, BoxLayout.LINE_AXIS));
		JLabel labelWeight = new JLabel("Weight :       ", SwingConstants.CENTER);
		answWeight = new JTextField();
		answWeight.setEditable(true);
		answWeight.setPreferredSize(new Dimension(widthScreen / 4, 25));
		answWeight.setAlignmentX(Component.CENTER_ALIGNMENT);
		answWeight.setText(weight);
		
		panelWeight.add(labelWeight);
		panelWeight.add(answWeight);
		panelWeight.add(Box.createHorizontalStrut(5));
		panelWeight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	
		/************************** button Ok ******************************************************/
		
		JPanel panelOk = new JPanel();
		panelOk.setLayout(new GridBagLayout());
		
		JButton repOk = new JButton("Ok");
		repOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				answButton="ok";
				drep.dispose();
			}
		});
		
		GridBagConstraints c = new GridBagConstraints();
//	    c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 0;
	    c.ipady = 0;
	    c.weightx = 0;
	    c.weighty = 0.5;
	    c.anchor = GridBagConstraints.PAGE_START; //bottom of space
//	    c.insets = new Insets(10,0,0,0);  //top padding

		panelOk.add(repOk,c);
		
		JButton repCancel = new JButton("Cancel");
		repCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				answButton="cancel";
				drep.dispose();
			}
		});
		
//		GridBagConstraints c = new GridBagConstraints();
//	    c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.gridy = 0;
		c.ipadx = 0;
	    c.ipady = 0;
	    c.weightx = 0;
	    c.weighty = 0.5;
	    c.anchor = GridBagConstraints.PAGE_END; //bottom of space
//	    c.insets = new Insets(10,0,0,0);  //top padding

		panelOk.add(repCancel,c);
		
		/******************************************************************************************/
		
		panelDialog.add(panelPatientName);
		panelDialog.add(panelStudyName);
		panelDialog.add(panelAge);
		panelDialog.add(panelSex);
		panelDialog.add(panelWeight);
		panelDialog.add(panelOk);
		
		drep.setSize(widthScreen/2, heightScreen/10);
		drep.setResizable(false);
//		drep.setAlwaysOnTop(true);
		drep.setLocationRelativeTo(null);
		drep.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		drep.pack();
		drep.setVisible(true);
	}
	
	public String answUser() {
		return answButton;
	}
	
	public String[] getAnswer() {
		
		String[] list = new String[5];
		list[0]=answPatientName.getText();
		list[1]=answStudyName.getText();
		list[2]=answAge.getText();
		list[3]=answSex.getText();
		list[4]=answWeight.getText();
		
		return list;
	}

}