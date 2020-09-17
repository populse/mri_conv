package dcm;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import MRIFileManager.GetStackTrace;
import abstractClass.ImageThumb;
import abstractClass.ParamMRI2;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;

import loci.common.DebugTools;
import loci.formats.ChannelSeparator;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

public class imageThumbDicom extends ImageThumb implements ParamMRI2 {
	
	private ImagePlus imp;

	@Override
	public Image ImageThumbShow(String noSeq, String note) {
		
		Image img;

		String[] listParamToFind = { "Scan Resolution", "Images In Acquisition", "Byte Order", "Data Type"};
		String[] values = new String[listParamToFind.length];

		for (int i = 0; i < listParamToFind.length; i++) {
			values[i] = hmInfo.get(noSeq).get(listParamToFind[i]);
		}


		int intValue;

		try {
			String[] listOff = hmInfo.get(noSeq).get("Offsets Image").split(" +");
			intValue = Integer.parseInt(listOff[listOff.length / 2]);
		} catch (Exception e) {
//			new GetStackTrace(e);
			int nImage = Integer.parseInt(values[1]);
//			Float f = new Float(nImage);
//			intValue = f.intValue();
			intValue = nImage / 2;
		}

		String filesrc = hmSeq.get(noSeq)[0];

		if (!note.contains("JpegLossLess")) {

			if (hmSeq.get(noSeq).length > 1) {
				filesrc = hmSeq.get(noSeq)[hmSeq.get(noSeq).length / 2];

				try {
					// attention with ImageJ 1.52k or more !! (ok with ImageJ 1.52j or less)
					imp = new ImagePlus(filesrc);
				} catch (Exception e) {
					new GetStackTrace(e);
				}
			}

			else {
				imp = new ImagePlus(filesrc);
				imp = new Duplicator().run(imp, intValue, intValue);
			}
		}

		else {
			DebugTools.enableLogging("OFF");
			String id = filesrc;
			ImageProcessorReader r;
			try {
				r = new ImageProcessorReader(new ChannelSeparator(LociPrefs.makeImageReader()));
				r.setGroupFiles(false);
				r.setId(id);
				int width = r.getSizeX();
				int height = r.getSizeY();
				ImageStack stack = new ImageStack(width, height);
				ImageProcessor ip = r.openProcessors(0)[0];
				stack.addSlice("", ip);
				imp = new ImagePlus("", stack);
				r.close();
			} catch (Exception e) {
				new GetStackTrace(e);
			}
		}

		imp.resetDisplayRange();
		int w = imp.getWidth();
		int h = imp.getHeight();
		float resolX = (float)imp.getCalibration().pixelWidth;
		float resolY = (float)imp.getCalibration().pixelHeight;
		img = imp.getImage();
		imp.close();
		
		int scalX=120,scalY=120;
		
		if (Math.round(w*resolX)!=Math.round(h*resolY)) {
			if (Math.round(w*resolX)>Math.round(h*resolY)) {
				scalY=scalY*Math.round(h*resolY)/Math.round(w*resolX);
			}
			else {
				scalX=scalX*Math.round(w*resolX)/Math.round(h*resolY);
			}
		}
		
		img = getScaledImage(img, scalX, scalY);

		System.gc();

		return img;
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(120, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 60-(w/2), 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}