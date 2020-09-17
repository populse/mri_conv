package philips;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import abstractClass.ActionImgPanel;

public class ActionImgPanelPhilips implements ActionImgPanel{

	@Override
	public void popMenuSeq(JPopupMenu pm) {
		JMenuItem seeSeq1 = new JMenuItem("Seq1");
		JMenuItem seeSeq2 = new JMenuItem("Seq2");
		pm.add(seeSeq1);
		pm.add(seeSeq2);
	}
}