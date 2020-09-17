package bids;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.swing.ImageIcon;

import MRIFileManager.GetStackTrace;
import abstractClass.ImageThumb;
import abstractClass.ParamMRI2;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;

public class ImageThumbBids extends ImageThumb implements ParamMRI2 {

	private Image img;
	private ImagePlus imp;
	private URL imgURL = getClass().getResource("/BlackScreen.jpg");

	@Override
	public Image ImageThumbShow(String noSeq, String note) {
		img = new ImageIcon(imgURL).getImage();

		String[] listParamToFind = { "Scan Resolution", "Images In Acquisition", "Byte Order", "Data Type",
				"Spatial Resolution","Bits per voxel"};
		String[] values = new String[listParamToFind.length];

		for (int i = 0; i < values.length; i++) {
			values[i] = hmInfo.get(noSeq).get(listParamToFind[i]);
			if (values[i] == "") {
				img = new ImageIcon(imgURL).getImage();
				return img;
			}
		}

		float offheader = Float.parseFloat(hmInfo.get(noSeq).get("Offset data blob"));

		String fileNiiGz = hmSeq.get(noSeq)[0];

		String tmp;

		int w, h, nImage = 1, bitPerPixel;
		float resolX, resolY;

		w = Integer.parseInt(values[0].split(" +")[0]);
		h = Integer.parseInt(values[0].split(" +")[1]);
		nImage = Integer.parseInt(values[1]);

		resolX = Float.parseFloat(values[4].split(" +")[0]);
		resolY = Float.parseFloat(values[4].split(" +")[1]);

		FileInfo fi = new FileInfo();

		tmp = values[3];
//		System.out.println("datatype = "+tmp);
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
			bitPerPixel=64;
		}
		else {
			fi.fileType = FileInfo.GRAY8;
			bitPerPixel=8;
		}

		FileInputStream filein;
		DataInputStream input = null;
		try {
			filein = new FileInputStream(fileNiiGz);
			GZIPInputStream gzipin = new GZIPInputStream(filein);
			input = new DataInputStream(gzipin);
			input.skip(w * h * (nImage / 2) * bitPerPixel / 8);
		} catch (Exception e) {
			new GetStackTrace(e);
		}

		fi.inputStream = input;
		fi.width = w;
		fi.height = h;
		fi.offset = (int) offheader;
		fi.nImages = 1;

		if (values[2].contains("little"))
			fi.intelByteOrder = true;
		else
			fi.intelByteOrder = false;

		FileOpener fo = new FileOpener(fi);
		fi = null;

		imp = fo.open(false);
		fo = null;

		if (bitPerPixel == 8)
			imp.setDisplayRange(imp.getStatistics().min, imp.getStatistics().max);
		else
			imp.resetDisplayRange();

		img = imp.getImage();
		imp.close();
		imp = null;

		int scalX = 150, scalY = 150;

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
		BufferedImage resizedImg = new BufferedImage(150, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 75 - (w / 2), 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}
}