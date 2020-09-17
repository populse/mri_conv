package MRIFileManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import abstractClass.Format;
import abstractClass.ImageThumb;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import abstractClass.SelectionSeq;
import bids.ImageThumbBids;
import bids.SelectionSeqBids;
import brukerParavision.ImageThumbBruker;
import brukerParavision.SelectionSeqBruker;
import dcm.SelectionSeqDicom;
import dcm.imageThumbDicom;
import nifti.ImageThumbNifti;
import nifti.SelectionSeqNifti;
import philips.ImageThumbPhilips;
import philips.SelectionSeqPhilips;

public class ThumbnailList implements ParamMRI2, Format {

	private FileManagerFrame wind;
	public static JList<String> list = new JList<>();
	private Map<String, ImageIcon> imageMap;
	private SelectionSeq seleseq;

	public ThumbnailList(FileManagerFrame wind) {
		this.wind = wind;
		run();
	}

	public void run() {

		imageMap = new HashMap<>();
		
		ArrayList<String> listName2 = new ArrayList<>();
		ImageIcon imc;

		ImageThumb imThub = null;

		switch (PrefParam.formatCurrentInt) {
		case Bruker:
			imThub = new ImageThumbBruker();
			break;
		case Dicom:
			imThub = new imageThumbDicom();
			break;
		case Philips:
			imThub = new ImageThumbPhilips();
			break;
		case Nifti:
			imThub = new ImageThumbNifti();
			break;
		case Bids:
			imThub = new ImageThumbBids();
			break;
		}

		FileManagerFrame.dlg.setVisible(true);
		String title = "Loading : thumbnail ";
		
		int nbRow = wind.getTabSeq().getModel().getRowCount();
		
		for (int l = 0; l < nbRow; l++) {
			FileManagerFrame.dlg.setTitle(title + (100 * l /  nbRow) + " %");
			
			listName2.add(wind.getTabSeq().getModel().getValueAt(l, 0).toString());
			try {
				imc = new ImageIcon(imThub.ImageThumbShow(wind.getTabSeq().getModel().getValueAt(l, 0).toString(),wind.getTabData().getValueAt(wind.getTabData().getSelectedRow(), 8).toString()));
				imageMap.put(listName2.get(l), imc);
			} catch (Exception e1) {
				imageMap.put(listName2.get(l), new ImageIcon(imThub.ImageThumbShow()));
			}
		}
		FileManagerFrame.dlg.setTitle(title + "100 %");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		FileManagerFrame.dlg.setVisible(false);

		Collections.sort(listName2);
		String[] listName = new String[nbRow];
		for (int i = 0; i < listName.length; i++)
			listName[i] = listName2.get(i);

		list = new JList<>(listName);
		list.setCellRenderer(new ImageListRenderer(wind));
		list.setSelectionBackground(Color.ORANGE);
		list.setSelectedIndex(0);
		list.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				JPopupMenu jp = new JPopupMenu();
				if (SwingUtilities.isRightMouseButton(e)) {
					JMenuItem sampleOpen = new JMenuItem("Open image(s) Ctrl+o");
					jp.add(sampleOpen);

					sampleOpen.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							openImage();
						}
					});
					jp.show(e.getComponent(), e.getX(), e.getY());
				}

				if (SwingUtilities.isLeftMouseButton(e))
					selectSeq();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					openImage();
			}
		});

		list.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP
						|| e.getKeyCode() == KeyEvent.VK_ENTER)
					selectSeq();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_O)
					openImage();
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A)
					wind.getTabSeq().selectAll();

			}
		});

		wind.getBoxThumb().add(list);
		wind.getScrollThumb().updateUI();
	}

	public void selectSeq() {
		if (list.getSelectedValuesList().size() == 1)
			for (int i = 0; i < wind.getTabSeq().getRowCount(); i++) {
				if (wind.getTabSeq().getValueAt(i, 0) == list.getSelectedValuesList().get(0).toString()) {
					wind.getTabSeq().setRowSelectionInterval(i, i);
					break;
				}
			}
		else
			for (String gj : list.getSelectedValuesList()) {
				for (int i = 0; i < wind.getTabSeq().getRowCount(); i++) {
					if (wind.getTabSeq().getValueAt(i, 0) == gj) {
						wind.getTabSeq().addRowSelectionInterval(i, i);
						break;
					}
				}
			}
		wind.getTabSeq().validate();
		wind.getTabSeq().updateUI();
	}

	public void openImage() {
		wind.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		switch (PrefParam.formatCurrentInt) {
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
		default:
			break;
		}

		seleseq.openImage();
		wind.setCursor(null);

	}

	public class ImageListRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;
		Font font = new Font("helvitica", Font.BOLD, 14);
		FileManagerFrame wind;

		public ImageListRenderer(FileManagerFrame wind) {
			this.wind = wind;
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			 label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
			label.setIcon(imageMap.get(value));
			label.setHorizontalTextPosition(SwingConstants.CENTER);
			label.setVerticalTextPosition(SwingConstants.BOTTOM);
			label.setFont(font);
			return label;
		}
	}
}