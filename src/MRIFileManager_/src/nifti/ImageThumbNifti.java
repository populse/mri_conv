package nifti;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.net.URL;

import javax.swing.ImageIcon;

import MRIFileManager.GetStackTrace;
import abstractClass.ImageThumb;
import abstractClass.ParamMRI2;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;

public class ImageThumbNifti extends ImageThumb implements ParamMRI2 {

	private Image img;
	private ImagePlus imp;

	private URL imgURL = getClass().getResource("/BlackScreen.jpg");

	@Override
	public Image ImageThumbShow(String noSeq, String note) {

		String[] listParamToFind = { "Scan Resolution", "Images In Acquisition", "Byte Order", "Data Type",
				"Spatial Resolution" };
		String[] values = new String[listParamToFind.length];

		float offheader = Float.parseFloat(hmInfo.get(noSeq).get("Offset data blob"));

		for (int i = 0; i < values.length; i++) {
			values[i] = hmInfo.get(noSeq).get(listParamToFind[i]);
			if (values[i] == "") {
				img = new ImageIcon(imgURL).getImage();
				return img;
			}
		}

		String tmp;
		int w, h, nImage = 1, bitPerPixel;
		float resolX, resolY;

		FileInfo fi = new FileInfo();

		tmp = values[0].toString();
		String fileNii = hmInfo.get(noSeq).get("File path");
		w = Integer.parseInt(tmp.split(" +")[0]);
		h = Integer.parseInt(tmp.split(" +")[1]);

		tmp = values[4].toString();
		resolX = Float.parseFloat(tmp.split(" +")[0]);
		resolY = Float.parseFloat(tmp.split(" +")[1]);

		nImage = Integer.parseInt(values[1]);

//		Float f = new Float(nImage);
		Float f = (float) nImage;
		int intValue = f.intValue();
		intValue = intValue / 2;

		tmp = values[3];

		if (tmp.contains("NIFTI_TYPE_INT16")) {
			fi.fileType = FileInfo.GRAY16_SIGNED;
			bitPerPixel = 16;
		} else if (tmp.contains("NIFTI_TYPE_UINT16")) {
			fi.fileType = FileInfo.GRAY16_UNSIGNED;
			bitPerPixel = 16;
		} else if (tmp.contains("NIFTI_TYPE_FLOAT32")) {
			fi.fileType = FileInfo.GRAY32_FLOAT;
			bitPerPixel = 32;
		} else if (tmp.contains("NIFTI_TYPE_FLOAT64")) {
			fi.fileType = FileInfo.GRAY64_FLOAT;
			bitPerPixel = 64;
		} else {
			fi.fileType = FileInfo.GRAY8;
			bitPerPixel = 8;
		}
		// ********************************************
		FileInputStream filein;
		DataInputStream input = null;
		try {
			filein = new FileInputStream(fileNii);
			input = new DataInputStream(filein);
			input.skip(w * h * (nImage / 2) * bitPerPixel / 8);
		} catch (Exception e) {
			new GetStackTrace(e);
		}

		fi.inputStream = input;
		// *******************************************

//		fi.fileName = fileNii;
		fi.width = w;
		fi.height = h;
		fi.offset = (int) offheader;
		fi.nImages = 1;

		if (values[2].contains("little"))
			fi.intelByteOrder = true;
		else
			fi.intelByteOrder = false;

		FileOpener fo = new FileOpener(fi);

		imp = fo.open(false);
		imp.resetDisplayRange();
		
		if (bitPerPixel == 8)
			imp.setDisplayRange(imp.getStatistics().min, imp.getStatistics().max);
		else
			imp.resetDisplayRange();
		

		if (imp != null)
			img = imp.getImage();

		int scalX = 120, scalY = 120;

		if (Math.round(w * resolX) != Math.round(h * resolY)) {
			if (Math.round(w * resolX) > Math.round(h * resolY)) {
				scalY = scalY * Math.round(h * resolY) / Math.round(w * resolX);
			} else {
				scalX = scalX * Math.round(w * resolX) / Math.round(h * resolY);
			}
		}

		img = getScaledImage(img, scalX, scalY);

		return img;
	}

	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(120, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 60 - (w / 2), 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}