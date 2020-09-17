package nifti;

import java.awt.EventQueue;

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

public class OpenNifti implements ParamMRI2 {

	private ImagePlus imp;

	public OpenNifti(String noSeq, Boolean show, String title) {

		String tmp = null, order;
		int w, h;
		float pxw, pxh, pxz;
		double slope = 1.0, intercept = 0.0;
		int c, z, t;
		boolean signed16bit = false;

		float offheader = Float.parseFloat(hmInfo.get(noSeq).get("Offset data blob"));

		tmp = hmInfo.get(noSeq).get("Scan Resolution");
		w = Integer.parseInt(tmp.split(" ")[0]);
		h = Integer.parseInt(tmp.split(" ")[1]);

		pxw = Float.parseFloat(hmInfo.get(noSeq).get("Spatial Resolution").split(" +")[0]);
		pxh = Float.parseFloat(hmInfo.get(noSeq).get("Spatial Resolution").split(" +")[1]);
		pxz = Float.parseFloat(hmInfo.get(noSeq).get("Spatial Resolution").split(" +")[2]);

		slope = Double.parseDouble(hmInfo.get(noSeq).get("Scaling slope and intercept").split(" +")[0]);
		intercept = Double.parseDouble(hmInfo.get(noSeq).get("Scaling slope and intercept").split(" +")[1]);

		order = hmOrderImage.get(noSeq)[0].toString();

		c = (int) hmOrderImage.get(noSeq)[1];
		z = (int) hmOrderImage.get(noSeq)[2];
		t = (int) hmOrderImage.get(noSeq)[3];
		
//		System.out.println("c , z , t : "+c+" , "+z+" , "+t);


		FileInfo fi = new FileInfo();

		tmp = hmInfo.get(noSeq).get("Data Type");

		if (tmp.contains("NIFTI_TYPE_INT16")) {
			fi.fileType = FileInfo.GRAY16_SIGNED;
			signed16bit = true;
		} else if (tmp.contains("NIFTI_TYPE_UINT16"))
			fi.fileType = FileInfo.GRAY16_UNSIGNED;
		else if (tmp.contains("NIFTI_TYPE_FLOAT32"))
			fi.fileType = FileInfo.GRAY32_FLOAT;
		else if (tmp.contains("NIFTI_TYPE_FLOAT64"))
			fi.fileType = FileInfo.GRAY64_FLOAT;
		else
			fi.fileType = FileInfo.GRAY8;

		tmp = hmInfo.get(noSeq).get("File path");

		fi.fileFormat = FileInfo.RAW;
		fi.fileName = tmp;
		fi.width = w;
		fi.height = h;
		fi.offset = (int) offheader;
		fi.gapBetweenImages = 0;

		if (hmInfo.get(noSeq).get("Byte Order").contains("little"))
			fi.intelByteOrder = true;
		else
			fi.intelByteOrder = false;
		fi.unit = "mm";
		fi.valueUnit = "mm";

		fi.nImages = Integer.parseInt(hmInfo.get(noSeq).get("Images In Acquisition"));

		fi.pixelWidth = pxw;
		fi.pixelHeight = pxh;
		fi.pixelDepth = pxz;
		fi.frameInterval = 1;

		imp = new FileOpener(fi).open(false);

		if (pxw * w == pxh * h) {
			if (w > h) {
				h = w;
				pxh = pxw;
			} else {
				w = h;
				pxw = pxh;
			}
		} else {
			if (w > h) {
				h *= pxh / pxw;
				pxh = pxw;
			} else {
				w *= pxw / pxh;
				pxw = pxh;
			}
		}

		ImageStack stack = new ImageStack(w, h);
		Calibration cal = imp.getCalibration();
		cal.pixelWidth = pxw;
		cal.pixelHeight = pxh;
		for (int i = 1; i <= imp.getStackSize(); i++) {
			imp.setSlice(i);
			stack.addSlice(imp.getProcessor().resize(w, h, true));
		}
		imp = new ImagePlus("", stack);
		imp.setCalibration(cal);
		imp.setTitle("Seq No. " + title);

		if (signed16bit)
			imp.getCalibration().setSigned16BitCalibration();
//		else
//			imp = convertToGray32(imp);

		if ((slope != 1.0 && slope != 0.0) || intercept != 0.0) {
			imp = convertToGray32(imp);

			for (int i = 1; i <= imp.getStackSize(); i++) {
				imp.setSlice(i);
				imp.getProcessor().multiply(slope);
				imp.getProcessor().add(intercept);
			}
		}

		if (c == 1 && z == 1 && t == 1)
			;
		else
			imp = HyperStackConverter.toHyperStack(imp, c, z, t, order, "grayscale");
		imp.resetDisplayRange();

		if (show) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					new StackWindow(imp);
				}
			});
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
		}
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