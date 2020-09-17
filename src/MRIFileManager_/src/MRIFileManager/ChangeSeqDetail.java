package MRIFileManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import abstractClass.ParamMRI2;

public class ChangeSeqDetail implements ActionListener,ItemListener {
	
	private FileManagerFrame wind;
	private String command;
	
	public ChangeSeqDetail(FileManagerFrame wind,String command) {
		this.wind=wind;
		this.command=command;

	}
	
	public ChangeSeqDetail(FileManagerFrame wind) {
		this.wind=wind;
		adddeletecolumn();
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		for (int i=1;i<5;i++) {
			if (wind.getCheckDisplaySeqinWindow()[i].isSelected()) wind.getCheckDisplaySeq()[i].setSelected(true);
			else
				 wind.getCheckDisplaySeq()[i].setSelected(false);
		}
		adddeletecolumn();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (command.contains("showdetail")) {
			wind.getFramDetailSeq().setVisible(true);
		}
		else 
			for (int i=1;i<5;i++) {
				if (wind.getCheckDisplaySeq()[i].isSelected()) wind.getCheckDisplaySeqinWindow()[i].setSelected(true);
				else
					wind.getCheckDisplaySeqinWindow()[i].setSelected(false);
			}
	}
	
	private void adddeletecolumn() {
		for (int i=1;i<ParamMRI2.headerListSeq.length;i++) {
			if (!wind.getCheckDisplaySeqinWindow()[i].isSelected()) {
				wind.getTabSeq().getColumnModel().getColumn(i).setMinWidth(0);
				wind.getTabSeq().getColumnModel().getColumn(i).setMaxWidth(0);
				wind.getTabSeq().getColumnModel().getColumn(i).setWidth(0);
			}
			else {
				wind.getTabSeq().getColumnModel().getColumn(i).setMinWidth(15);
				wind.getTabSeq().getColumnModel().getColumn(i).setPreferredWidth(75);
				wind.getTabSeq().getColumnModel().getColumn(i).setMaxWidth(2147483647);
			}
			}
		wind.getTabSeq().getColumnModel().getColumn(0).setMinWidth(15);
		wind.getTabSeq().getColumnModel().getColumn(0).setPreferredWidth(50);
		wind.getTabSeq().getColumnModel().getColumn(0).setMaxWidth(2147483647);
		
		wind.getTabSeq().updateUI();
	}
}