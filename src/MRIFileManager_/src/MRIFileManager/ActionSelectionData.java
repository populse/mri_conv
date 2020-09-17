package MRIFileManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import abstractClass.Format;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import abstractClass.SelectionData;
import bids.SelectionDataBids;
import brukerParavision.SelectionDataBruker;
import dcm.SelectionDataDicom;
import nifti.SelectionDataNifti;
import philips.SelectionDataPhilips;

public class ActionSelectionData extends PrefParam implements MouseListener, KeyListener, Format {

	private FileManagerFrame wind;
	private SelectionData seledata;

	public ActionSelectionData(FileManagerFrame wind, String command) {
		this.wind = wind;
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		JTable target = (JTable) e.getSource();

		if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1
				&& !e.getSource().toString().contains("JTableHeader") && wind.getTabData().isEnabled()
				&& (target.getSelectedRow() == target.rowAtPoint(e.getPoint()) || target.getSelectedRowCount() > 1)
				&& target.getSelectedRow() != -1 && !wind.getTabData().getValueAt(0, 0).toString().contains("found")) {

			if (wind.getTabData().getSelectedRowCount() == 1)
				goSelectionData("single");
			else if (wind.getTabData().getSelectedRowCount() > 1) {
				resetAffich();
			}
		}

		if (SwingUtilities.isRightMouseButton(e) && !e.getSource().toString().contains("JTableHeader")
				&& wind.getTabData().isEnabled()) {

			JMenuItem addToBasketAnonymous = new JMenuItem("anonymize and add all sequences to basket");
			addToBasketAnonymous.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
//					if (!formatCurrent.contains("Bids")) {
						deidentify = true;
						goSelectionData("multi");
						deidentify = false;
//					} else 
//						JOptionPane.showMessageDialog(wind, "This function will be available in the next version.\nBids is still in beta version.", "Message", JOptionPane.YES_NO_OPTION);
				}
			});

			JMenuItem addtobasket = new JMenuItem("add all sequences to basket (Ctrl+shift+B)");
			addtobasket.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
//					if (!formatCurrent.contains("Bids"))
						goSelectionData("multi");
//					else
//						JOptionPane.showMessageDialog(wind, "This function will be available in the next version.\nBids is still in beta version.", "Message", JOptionPane.YES_NO_OPTION);

				}
			});

			if (target.getSelectedRow() == target.rowAtPoint(e.getPoint())
					&& wind.getTabData().getSelectedRowCount() == 1) {

				JPopupMenu popmenu = new JPopupMenu();

				try {
					// if (formatCurrent.contains("Bruker"))
					popmenu.add(addToBasketAnonymous);
					popmenu.addSeparator();
					seledata.popMenuData(popmenu);
					popmenu.addSeparator();
					popmenu.add(addtobasket);

				} catch (Exception e1) {
					wind.setCursor(null);
				}

				popmenu.show(e.getComponent(), e.getX(), e.getY());
			}

			else if (wind.getTabData().getSelectedRowCount() > 1) {
				JPopupMenu popmenuexport = new JPopupMenu();
				try {
					popmenuexport.add(addToBasketAnonymous);
					popmenuexport.addSeparator();
					popmenuexport.add(addtobasket);
					seledata.popMenuDataExport(popmenuexport);

				} catch (Exception e1) {
					wind.setCursor(null);
				}
				popmenuexport.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP
				|| e.getKeyCode() == KeyEvent.VK_ENTER) {
			goSelectionData("single");
		}

		if (e.isShiftDown() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_B) {
			goSelectionData("multi");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private void goSelectionData(String code) {

		try {
			switch (formatCurrentInt) {
			case Bruker:
				seledata = new SelectionDataBruker(wind);
				break;
			case Philips:
				seledata = new SelectionDataPhilips(wind);
				break;
			case Dicom:
				seledata = new SelectionDataDicom(wind);
				break;
			case Nifti:
				seledata = new SelectionDataNifti(wind);
				break;
			case Bids:
				seledata = new SelectionDataBids(wind);
				break;
			default:
				break;
			}

		} catch (Exception e1) {
			wind.setCursor(null);
			new GetStackTrace(e1);
		}

		if (code.contentEquals("single"))
			seledata.goSelectionData(wind);
		else
			seledata.exportMultiSelectionData(wind);
	}

	private void resetAffich() {
		wind.getTreeInfoGeneral().setModel(new TreeInfo2(ParamMRI2.listParamInfoSystem, null).getTreeInfo().getModel());
		for (int j = 0; j < wind.getTreeInfoGeneral().getRowCount(); j++)
			wind.getTreeInfoGeneral().expandRow(j);
		wind.getTreeInfoUser().setModel(new TreeInfo2(ParamMRI2.listParamInfoUser, null).getTreeInfo().getModel());
		for (int j = 0; j < wind.getTreeInfoUser().getRowCount(); j++)
			wind.getTreeInfoUser().expandRow(j);
		wind.resetTabSeq();
		wind.getBoxImage().removeAll();
		wind.getPreview().updateUI();
		wind.getBoxThumb().removeAll();
		wind.getBoxThumb().updateUI();
		wind.getScrollThumb().updateUI();
	}
}