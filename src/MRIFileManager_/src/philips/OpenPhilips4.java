package philips;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.StackWindow;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.measure.Calibration;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;

public class OpenPhilips4 implements ParamMRI2, DictionParRec {

	private ImagePlus imp;
	private int c, z, t;

	public OpenPhilips4(HashMap<String, String> infoIm, Object[] orderIm, Boolean show, Boolean convert, String title) {

		String tmp = null, order;
		int w, h;
		float pxw, pxh, pxz;
		int nimages;

		/****************************************************
		 * create imp
		 *****************************************************/
		tmp = infoIm.get("Scan Resolution");
		w = Integer.parseInt(tmp.split(" +")[0]);
		h = Integer.parseInt(tmp.split(" +")[1]);

		pxw = Float.parseFloat(infoIm.get("Spatial Resolution").split(" +")[0]);
		pxh = Float.parseFloat(infoIm.get("Spatial Resolution").split(" +")[1]);
		pxz = Float.parseFloat(infoIm.get("Slice Thickness"));

		order = orderIm[0].toString();
		c = (int) orderIm[1];
		z = (int) orderIm[2];
		t = (int) orderIm[3];

		nimages = c * z * t;

		tmp = infoIm.get("File path");
		File file = new File(tmp);

		int[] pos = Arrays.stream(infoIm.get("index in REC file").split(" +")).mapToInt(Integer::parseInt).toArray();
		int max = Arrays.stream(pos).max().getAsInt();

		InputStream fis = null;

		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
			new GetStackTrace(e);
		}
		tmp = infoIm.get("Data Type");

		System.gc();

		FileInfo fi = new FileInfo();
		if (tmp.contains("16"))
			fi.fileType = FileInfo.GRAY16_UNSIGNED;
		else if (tmp.contains("32"))
			fi.fileType = FileInfo.GRAY32_FLOAT;
		else {
			fi.fileType = FileInfo.GRAY8;
		}

		fi.fileFormat = FileInfo.RAW;
		fi.inputStream = fis;
		fi.width = w;
		fi.height = h;
		fi.offset = 0;
		fi.gapBetweenImages = 0;
		fi.intelByteOrder = true;
		fi.unit = "mm";
		fi.valueUnit = "mm";
		fi.nImages = max + 1;
		fi.pixelWidth = pxw;
		fi.pixelHeight = pxh;
		fi.pixelDepth = pxz;

		imp = new FileOpener(fi).open(false);

		try {
			fis.close();
		} catch (Exception e) {

		}
		fi = null;
		Calibration cal = imp.getCalibration();

		if (convert || show) {
			imp = convertToGray32(imp);
			String[] RI = orderIm[12].toString().split(" +");
			String[] RS = orderIm[13].toString().split(" +");
			String[] SS = orderIm[14].toString().split(" +");
			ImageStack ims = new ImageStack(w, h);
			for (int i = 0; i < nimages; i++) {
				imp.setSlice(pos[i] + 1);
				imp.getProcessor().multiply(1 / Double.parseDouble(SS[pos[i]]));
				if (Double.parseDouble(RS[pos[i]]) != 0.0)
					imp.getProcessor().add(Double.parseDouble(RI[pos[i]])
							/ (Double.parseDouble(RS[pos[i]]) * Double.parseDouble(SS[pos[i]])));
				ims.addSlice(imp.getProcessor());

			}
			imp = new ImagePlus(title, ims);
			imp.setCalibration(cal);

		} else {
			ImageStack ims = new ImageStack(w, h);
			for (int i = 0; i < nimages; i++) {
				imp.setSlice(pos[i] + 1);
				ims.addSlice(imp.getProcessor());
			}
			imp = new ImagePlus(title, ims);
			imp.setCalibration(cal);

			try {
				Thread.sleep(200);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		imp = HyperStackConverter.toHyperStack(imp, c, z, t, order, "grayscale");

		if (show) {

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (imp.getNChannels() > 1)
						for (int n = 0; n < imp.getNChannels(); n++) {
							imp.setC(n + 1);
							imp.resetDisplayRange();
						}
					else {
						imp.setSlice(z / 2);
						imp.resetDisplayRange();
					}
					new StackWindow(imp);
				}
			});
			System.gc();
		}

		imp.close();

	}

	public ImagePlus getImp() {
		imp.setTitle("");
		return imp;
	}

	private ImagePlus convertToGray32(ImagePlus imgtmp) {

		int width = imgtmp.getWidth();
		int height = imgtmp.getHeight();
		int nSlices = imgtmp.getStackSize();

		if (imgtmp.getType() != ImagePlus.GRAY32) {
			ImageStack stack1 = imgtmp.getStack();
			ImageStack stack2 = new ImageStack(width, height);
			String label;
			int inc = nSlices / 20;
			if (inc < 1)
				inc = 1;
			ImageProcessor ip1, ip2;
			Calibration cal = imgtmp.getCalibration();
			for (int i = 1; i <= nSlices; i++) {
				label = stack1.getSliceLabel(1);
				ip1 = stack1.getProcessor(1);
				ip1.setCalibrationTable(cal.getCTable());
				ip2 = ip1.convertToFloat();
				stack1.deleteSlice(1);
				stack2.addSlice(label, ip2);
				if ((i % inc) == 0) {
					IJ.showProgress((double) i / nSlices);
					IJ.showStatus("Converting to 32-bits: " + i + PrefParam.separator + nSlices);
				}
			}
			IJ.showProgress(1.0);
			imgtmp.setStack(null, stack2);
			imgtmp.setCalibration(imgtmp.getCalibration()); // update
															// calibration
		}
		return imgtmp;
	}
}