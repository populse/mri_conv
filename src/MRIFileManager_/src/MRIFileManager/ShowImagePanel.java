package MRIFileManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import abstractClass.Format;
import abstractClass.ParamMRI2;
import ij.ImagePlus;

public class ShowImagePanel implements ParamMRI2, Format {

	ImagePanel panelImage;

	public ShowImagePanel(final FileManagerFrame wind, final ImagePlus imp, final String seqSel) {
		
		wind.getBoxImage().removeAll();
		wind.getBoxImage().updateUI();
		String txtSlid="";

		if (imp!=null)
			try {

				for (int i = 0; i < 3; i++) {
					if (wind.getSlidImage()[i].getChangeListeners().length != 0)
						wind.getSlidImage()[i].removeChangeListener(wind.getSlidImage()[i].getChangeListeners()[0]);
					if (wind.getSlidImage()[i].getKeyListeners().length != 0)
						wind.getSlidImage()[i].removeKeyListener(wind.getSlidImage()[i].getKeyListeners()[0]);

					final int ii = i;

					wind.getSlidImage()[i].setMinimum(1);
					wind.getSlidImage()[i].setMaximum((int) hmOrderImage.get(seqSel)[i + 1]);
					wind.getSlidImage()[i].setValue(1);
					wind.getSlidImage()[i].setEnabled(true);
					txtSlid="1/" + (int) hmOrderImage.get(seqSel)[i + 1];
					wind.getFieldSlid()[i].setText(txtSlid);

					wind.getSlidImage()[i].addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
						}

						@Override
						public void keyReleased(KeyEvent e) {
							if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
								panelImage = new ImagePanel(imp, wind.getSlidImage()[0].getValue(),
										wind.getSlidImage()[1].getValue(), wind.getSlidImage()[2].getValue());
								wind.getBoxImage().removeAll();
								wind.getBoxImage().add(panelImage);
								wind.getBoxImage().updateUI();
								wind.getPreview().updateUI();
								String txtSlid = String.valueOf(wind.getSlidImage()[ii].getValue())
								+ "/" + (int) hmOrderImage.get(seqSel)[ii + 1];
								wind.getFieldSlid()[ii].setText(txtSlid);
							}
						}

						@Override
						public void keyPressed(KeyEvent e) {
						}
					});

					wind.getSlidImage()[i].addChangeListener(new ChangeListener() {

						@Override
						public void stateChanged(ChangeEvent e) {
							JSlider sl = (JSlider) e.getSource();
							if (sl.getValueIsAdjusting()) {
								panelImage = new ImagePanel(imp, wind.getSlidImage()[0].getValue(),
										wind.getSlidImage()[1].getValue(), wind.getSlidImage()[2].getValue());
								wind.getBoxImage().removeAll();
								wind.getBoxImage().add(panelImage);
								wind.getBoxImage().updateUI();
								wind.getPreview().updateUI();
								String txtSlid = String.valueOf(sl.getValue()) + "/"
										+ (int) hmOrderImage.get(seqSel)[ii + 1];
								wind.getFieldSlid()[ii].setText(txtSlid);
							}
						}
					});
				}

				panelImage = new ImagePanel(imp, wind.getSlidImage()[0].getValue(), wind.getSlidImage()[1].getValue(),
						wind.getSlidImage()[2].getValue());
				
				wind.getBoxImage().add(panelImage);

			} catch (Exception e) {
				for (int i = 0; i < 3; i++) {
					if (wind.getSlidImage()[i].getChangeListeners().length != 0)
						wind.getSlidImage()[i].removeChangeListener(wind.getSlidImage()[i].getChangeListeners()[0]);
					if (wind.getSlidImage()[i].getKeyListeners().length != 0)
						wind.getSlidImage()[i].removeKeyListener(wind.getSlidImage()[i].getKeyListeners()[0]);

					wind.getSlidImage()[i].setMinimum(1);
					wind.getSlidImage()[i].setMaximum(1);
					wind.getSlidImage()[i].setValue(1);
					wind.getSlidImage()[i].setEnabled(false);
					wind.getFieldSlid()[i].setText(" 1/1");
				}
				panelImage = new ImagePanel(null);
			}
		else
			wind.resetBoxImage();
		wind.getBoxImage().updateUI();
		wind.getPreview().updateUI();
		System.gc();
	}
}