package MRIFileManager;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import abstractClass.Format;
import abstractClass.PrefParam;
import abstractClass.SelectionSeq;
import bids.SelectionSeqBids;
import brukerParavision.SelectionSeqBruker;
import dcm.SelectionSeqDicom;
import nifti.SelectionSeqNifti;
import philips.SelectionSeqPhilips;

public class ActionSelectionSeq extends PrefParam implements Format, MouseListener, KeyListener, ListSelectionListener {

	private FileManagerFrame wind;
	private SelectionSeq seleseq;

	public ActionSelectionSeq(FileManagerFrame wind) {
		this.wind = wind;
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (wind.getTabSeq().getSelectedRow() >= 0 && !arg0.getValueIsAdjusting())
			selseq();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_O)
			openImage();
		if (arg0.isControlDown() && arg0.getKeyCode() == KeyEvent.VK_B)
			fillBasket();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_DOWN || arg0.getKeyCode() == KeyEvent.VK_UP
				|| arg0.getKeyCode() == KeyEvent.VK_ENTER)
			selseq();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		if (wind.getTabSeq().getSelectedRow() >= 0 && e.getClickCount() == 2)
			openImage();
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {

		// dimSeqTab=wind.getTabSeq().getColumnModel();

		if (!e.getSource().toString().contains("JTableHeader")) {

			JTable target = (JTable) e.getSource();

			// if (SwingUtilities.isLeftMouseButton(e) &&
			// wind.getTabSeq().isEnabled())
			// selseq();

			if (SwingUtilities.isRightMouseButton(e)
					&& (wind.getTabSeq().isEnabled() && target.getSelectedRow() == target.rowAtPoint(e.getPoint())
							|| target.getSelectedRowCount() > 1)) {

				JPopupMenu popmenu = new JPopupMenu();
				// if (formatCurrent.contains("Bruker"))
				// seleseq = new SelectionSeqBruker(wind);
				// if (formatCurrent.contains("Nifti"))
				// seleseq = new SelectionSeqNifti(wind);
				formatDecl();

				seleseq.popMenuSeq(popmenu);
				popmenu.show(e.getComponent(), e.getX(), e.getY());
			}
		} else {
			if (SwingUtilities.isRightMouseButton(e))
				wind.getMenuCheckDisplaySeq().show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private void formatDecl() {
		switch (formatCurrentInt) {
		case Bruker:
			seleseq = new SelectionSeqBruker(wind);
			break;
		case Dicom:
			seleseq = new SelectionSeqDicom(wind);
			break;
		case Philips:
			seleseq = new SelectionSeqPhilips(wind);
			break;
		case Nifti:
			seleseq = new SelectionSeqNifti(wind);
			break;
		case Bids:
			seleseq = new SelectionSeqBids(wind);
			break;
		}
	}

	private void selseq() {
		wind.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		formatDecl();
		try {
			if (!OptionLookAndFeel)
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

			seleseq.goSelectionSeq();

			if (!OptionLookAndFeel) {
				wind.getTreeInfoGeneral().updateUI();
				wind.getTreeInfoUser().updateUI();
				UIManager.setLookAndFeel(PrefParam.LookFeelCurrent);
			}
		} catch (Exception e) {

		}
		wind.setCursor(null);
	}

	private void openImage() {
		wind.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		formatDecl();
		seleseq.openImage();
		wind.setCursor(null);
	}

	private void fillBasket() {
		formatDecl();
		seleseq.fillBasket();
	}
}