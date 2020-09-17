package abstractClass;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public abstract class ImageThumb {
	
	private URL imgURL = getClass().getResource("/BlackScreen.jpg");
	
	public abstract Image ImageThumbShow(String pathFile, String note);
	
	public Image ImageThumbShow() {
		Image img = new ImageIcon(imgURL).getImage();
		return img;
	}
}