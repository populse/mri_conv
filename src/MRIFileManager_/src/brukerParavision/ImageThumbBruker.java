package brukerParavision;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URL;

import javax.swing.ImageIcon;

import MRIFileManager.GetStackTrace;
import abstractClass.ImageThumb;
import abstractClass.ParamMRI2;
import ij.ImagePlus;
import ij.gui.ProfilePlot;
import ij.io.FileInfo;
import ij.io.FileOpener;

public class ImageThumbBruker extends ImageThumb implements ParamMRI2 {

	private Image img;
	private ImagePlus imp;
	private ProfilePlot profPlot;

	private URL imgURL = getClass().getResource("/BlackScreen.jpg");

	@Override
	public Image ImageThumbShow(String noSeq, String note) {

		String sliceOrientation = hmInfo.get(noSeq).get("Slice Orientation");
		String readDirection = hmInfo.get(noSeq).get("Read Direction");

		String[] listParamToFind = { "Scan Resolution", "Images In Acquisition", "Byte Order", "Data Type","Spatial Resolution" };
		String[] values = new String[listParamToFind.length];

		values[0] = hmInfo.get(noSeq).get(listParamToFind[0]);

		for (int i = 1; i < values.length-1 ; i++) {
			values[i] = hmInfo.get(noSeq).get(listParamToFind[i]);
			if (values[i] == "") {
				img = new ImageIcon(imgURL).getImage();
				return img;
			}
		}

		String type;// byteOrder;
		int w = Integer.parseInt(values[0].split(" +")[0]);
		int h;
		float resolX = 0, resolY = 0;
		if (values[0].split(" +").length == 1) {
//			resolX = Float.parseFloat(values[4].split(" +")[0]);
			h = 1;
		} else {
			h = Integer.parseInt(values[0].split(" +")[1]);
			values[4] = hmInfo.get(noSeq).get(listParamToFind[4]);
			resolX = Float.parseFloat(values[4].split(" +")[0]);
			resolY = Float.parseFloat(values[4].split(" +")[1]);
		}
		float tmpb;

		int nImage;
		int tmpt;

		if ((sliceOrientation.contentEquals("coronal") && readDirection.contentEquals("H_F"))
				|| (sliceOrientation.contentEquals("sagittal") && readDirection.contentEquals("H_F"))
				|| (sliceOrientation.contentEquals("axial") && readDirection.contentEquals("A_P"))) {
			tmpt = w;
			w = h;
			h = tmpt;
			tmpb = resolX;
			resolX = resolY;
			resolY = tmpb;
		}

		if (values[0].split(" +").length > 2)
			nImage = Integer.parseInt(values[0].split(" +")[2]);
		else
			nImage = Integer.parseInt(values[1]);
		
		int intValue = nImage / 2;
		
		type = values[3];
		int dt;

		if (type.contains("_16BIT_")) {
			dt = 16;
		} else if (type.contains("_32BIT_")) {
			dt = 32;
		} else {
			dt = 8;
		}

		FileInputStream input = null;
		ByteArrayOutputStream output;
		ByteArrayInputStream inputStream = null;

		String tmp = hmInfo.get(noSeq).get("File path");

		try {
			input = new FileInputStream(tmp);
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[w * h * dt / 8];
			int l = input.read(buffer);
			input.getChannel().position(intValue * l);
			l = input.read(buffer);
			output.write(buffer, 0, l);
			input.close();
			output.close();
			byte[] data = output.toByteArray();
			inputStream = new ByteArrayInputStream(data);
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		FileInfo fi = new FileInfo();

		if (type.contains("_16BIT_SGN_INT")) {
			fi.fileType = FileInfo.GRAY16_SIGNED;
		} else if (type.contains("_32BIT_SGN_INT")) {
			fi.fileType = FileInfo.GRAY32_INT;
		} else {
			fi.fileType = FileInfo.GRAY8;
		}

		fi.inputStream = inputStream;
		fi.fileFormat = FileInfo.RAW;
		fi.width = w;
		fi.height = h;
		fi.offset = 0;
		fi.nImages = 1;
		if (values[2].contains("littleEndian"))
			fi.intelByteOrder = true;
		else
			fi.intelByteOrder = false;
		fi.gapBetweenImages = 0;
//		fi.pixelWidth=resolX;
//		fi.pixelHeight=resolY;

		FileOpener fo = new FileOpener(fi);

		imp = fo.open(false);
		imp.resetDisplayRange();

		int scalX = 150, scalY = 150;

		if (imp.getHeight() != 1)
			if (Math.round(w * resolX) != Math.round(h * resolY)) {
				if (Math.round(w * resolX) > Math.round(h * resolY)) {
					scalY = scalY * Math.round(h * resolY) / Math.round(w * resolX);
				} else {
					scalX = scalX * Math.round(w * resolX) / Math.round(h * resolY);
				}
			}

		if (imp != null) {
			if (imp.getHeight() != 1)
				img = imp.getImage();
			else {
				imp.setRoi(0, 0, imp.getWidth(), 1);
				profPlot = new ProfilePlot(imp);
				img = profPlot.getPlot().getImagePlus().getImage();
			}
			img = getScaledImage(img, scalX, scalY);
		}
		return img;
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(150, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		g2.setComposite(AlphaComposite.SrcOut);
		g2.drawImage(srcImg, 75-w/2, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}