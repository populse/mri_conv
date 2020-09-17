package MRIFileManager;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import abstractClass.PrefParam;
import ij.IJ;
import ij.ImageJ;

public class OpenImageJ extends PrefParam {

	private ImageJ ij;

	public OpenImageJ() {

		if (IJ.getInstance() == null) {
//			System.getProperties().setProperty("plugins.dir",
//					System.getProperty("user.dir") + File.separator + "dist" + File.separator);
			System.getProperties().setProperty("plugins.dir",
					UtilsSystem.pathOfJar() + "dist" + File.separator);

			IJ.run("Appearance...", "interpolate auto menu=15 16-bit=Automatic");
			ij = new ImageJ();
//			ij.setAlwaysOnTop(true);
			ij.exitWhenQuitting(false);

			ij.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(final WindowEvent e) {
					for (Window wl : Window.getOwnerlessWindows()) {
						if (!wl.toString().toLowerCase().contains("frame"))
							wl.dispose();
					}
				}

				@Override
				public void windowClosed(final WindowEvent e) {
					for (Window wl : Window.getOwnerlessWindows()) {
						if (!wl.toString().toLowerCase().contains("frame"))
							wl.dispose();
					}
				}
			});
		}
	}
}