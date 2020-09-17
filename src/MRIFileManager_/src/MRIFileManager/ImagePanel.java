package MRIFileManager;

import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import ij.ImagePlus;
import ij.gui.ProfilePlot;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Image img;
	private ProfilePlot profPlot;
	private int w, h = 0;
	private ImagePlus imp;

	public ImagePanel(Image img) {

		if (img == null) {
			URL imgURL = getClass().getResource("/BlackScreen.jpg");
			img = new ImageIcon(imgURL).getImage();
		}
		this.img = img;
	}

	public ImagePanel(ImagePlus imp, int c, int z, int t) {

		this.imp = imp;

		// if (imp.getWidth() < 200)
		// w = imp.getWidth();
		// else
		// w = 200;

		// if (imp.getHeight() == 1)
		// h = 1;
		// else
		// h = 2;

		w = imp.getWidth();
		h = imp.getHeight();

		int off;
		off = imp.getStackIndex(c, z, t);

		if (imp != null && h != 1) {
			imp.setPosition(off);
			imp.resetDisplayRange();
			imp.updateImage();
			img = imp.getImage();
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		if (h != 1)
			try {
				if (w != h)
					if (w > h) {
						g.drawImage(img, 10, 105 - 100 *h / w, 200, 200 * h  / w, null);
					} else  {
						g.drawImage(img, 105 - 100 * w  / h, 10, 200 * w / h, 200, null);
					}
				else
					g.drawImage(img, 10, 10, 200, 200, null);

			} catch (Exception e) {
				g.drawImage(img, 10, 10, 200, 200, null);
			}
		else {
			imp.setRoi(0, 0, imp.getWidth(), 1);
			profPlot = new ProfilePlot(imp);
			g.drawImage(profPlot.getPlot().getImagePlus().getImage(), 0, 0, 200, 200, null);
		}
	}
}