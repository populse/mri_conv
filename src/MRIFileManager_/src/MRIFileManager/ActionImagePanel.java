package MRIFileManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import abstractClass.ActionImgPanel;
import abstractClass.Format;
import abstractClass.PrefParam;
import brukerParavision.ActionImgPanelBruker;
import dcm.ActionImgPanelDicom;
import nifti.ActionImgPanelNifti;
import philips.ActionImgPanelPhilips;

public class ActionImagePanel extends PrefParam implements Format,MouseListener {
	
//	private FileManagerFrame wind;
	private ActionImgPanel actPanel;
	
	public ActionImagePanel(FileManagerFrame wind) {
//		this.wind = wind;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			JPopupMenu popmenu = new JPopupMenu();
			formatDecl();
			actPanel.popMenuSeq(popmenu);
			popmenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	
	}
	
	private void formatDecl() {
		switch (formatCurrentInt) {
		case Bruker:
			actPanel = new ActionImgPanelBruker();
			break;
		case Dicom:
			actPanel = new ActionImgPanelDicom();
			break;
		case Philips:
			actPanel = new ActionImgPanelPhilips();
			break;
		case Nifti:
			actPanel = new ActionImgPanelNifti();
			break;
		}
	}
}