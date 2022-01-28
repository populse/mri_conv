package MRIFileManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import abstractClass.Format;
import abstractClass.PrefParam;

public class ActionSelectionBasket extends PrefParam implements MouseListener, KeyListener, Format {

	private FileManagerFrame wind;
	private String command;

	public ActionSelectionBasket(FileManagerFrame wind, String command) {
		this.wind = wind;
		this.command = command;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (command.contentEquals("TreeBasket"))
			if (SwingUtilities.isRightMouseButton(arg0)) {
				JMenuItem treecollapse = new JMenuItem("Collapse all");
				treecollapse.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						for (int i = wind.getTreeBasket().getRowCount() - 1; i >= 1; i--) {
							wind.getTreeBasket().collapseRow(i);
						}
					}
				});
				JMenuItem expandcollapse = new JMenuItem("Expand all");
				expandcollapse.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						for (int i = wind.getTreeBasket().getRowCount() - 1; i >= 1; i--) {
							wind.getTreeBasket().expandRow(i);
						}
					}
				});

				JPopupMenu popmenu = new JPopupMenu();

				try {
					popmenu.add(treecollapse);
					popmenu.add(expandcollapse);
				} catch (Exception e1) {
					wind.setCursor(null);
				}
				popmenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());

			}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}