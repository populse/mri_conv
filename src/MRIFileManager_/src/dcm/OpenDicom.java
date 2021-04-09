package dcm;

import java.util.Arrays;
import java.util.HashMap;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.measure.Calibration;
import ij.plugin.DICOM;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;
import loci.common.DebugTools;
import loci.formats.ChannelSeparator;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

public class OpenDicom extends Thread implements ParamMRI2 {

	private String noSeq;
	private ImagePlus imp;
	private Boolean show;
	private HashMap<String, String> infoImage;
	private Object[] orderImage;
	private String[] listFile;

	public OpenDicom(HashMap<String, String> infoImage, Object[] orderImage, String[] listFile, String noSeq,
			Boolean show) {
		
		this.noSeq = noSeq;
		this.show = show;
		this.infoImage = infoImage;
		this.orderImage = orderImage;
		this.listFile = listFile;
		
		if (infoImage.get("Scale Slope").trim().isEmpty())
			Prefs.openDicomsAsFloat = true;
		else
			Prefs.openDicomsAsFloat = false;

		if (!show)
			run();
	}

	@Override
	public void run() {

		int c, z, t;

		c = Integer.parseInt(orderImage[1].toString());
		z = Integer.parseInt(orderImage[2].toString());
		t = Integer.parseInt(orderImage[3].toString());

		String order = (String) orderImage[0];
		Calibration cal = new Calibration();
		String note = "";

		try {
			note = infoImage.get("Note").toString();
		} catch (Exception e) {
		}

		try {
			cal.pixelDepth = Double.parseDouble(infoImage.get("Slice Separation"));
			cal.setUnit("mm");
			cal.pixelHeight = Double.parseDouble(infoImage.get("Spatial Resolution").split(" +")[1]);
			cal.pixelWidth = Double.parseDouble(infoImage.get("Spatial Resolution").split(" +")[0]);

		} catch (Exception e) {

		}
		
		IJ.run("DICOM...", "ignore");

		if (!note.contains("JpegLossLess")) {
			imp = noJpegLossLess();
			if (cal != null)
				imp.setCalibration(cal);
			if (!Prefs.openDicomsAsFloat)
				imp = normalizationDicom(imp);

		} else {
			imp = JpegLossLess();
			if (cal != null)
				imp.setCalibration(cal);
			if (infoImage.get("Modality").trim().contentEquals("CT"))
				imp = normalizationCT(imp);
			else
				imp = normalizationDicom(imp);
		}

		try {
			imp = HyperStackConverter.toHyperStack(imp, c, z, t, order, "grayscale");

			for (int i = 0; i < imp.getNChannels(); i++) {
				imp.setC(i + 1);
				imp.resetDisplayRange();
			}
		} catch (Exception e) {
			// JOptionPane.showMessageDialog(wind, "problem with number of stack");
			// IJ.log(noSeq + " : problem with number of stack");
		}

		if (show)
			imp.show();

	}

	private ImagePlus noJpegLossLess() {

		ImagePlus imptmp;
		ImageStack ims;

		if (listFile.length == 1) {

			String listOffsets = infoImage.get("Offsets Image");

			if (listOffsets.trim().contentEquals("0")) {
				Prefs.openDicomsAsFloat = false;
				DICOM dcm = new DICOM();
				dcm.open(listFile[0]);
				imptmp = new ImagePlus(noSeq, dcm.getImageStack());
				dcm.close();

			} else {
				DICOM dcm = new DICOM();
				dcm.open(listFile[0]);
				imptmp = new ImagePlus(noSeq, dcm.getImageStack());
				ims = new ImageStack(imptmp.getWidth(), imptmp.getHeight());
				for (String off : listOffsets.split(" +")) {
					imptmp.setSlice(Integer.parseInt(off) + 1);
					ims.addSlice(imptmp.getChannelProcessor());
				}
				imptmp = new ImagePlus(noSeq, ims);
				dcm.close();
			}
		} else {
			imptmp = new ImagePlus(listFile[0]);
			ims = new ImageStack(imptmp.getWidth(), imptmp.getHeight());
			DICOM dcm;

			for (String kk : listFile) {
				dcm = new DICOM();
				dcm.open(kk);
				imptmp = new ImagePlus(noSeq, dcm.getImageStack());
				ims.addSlice(imptmp.getChannelProcessor());
				dcm.close();
			}

			imptmp = new ImagePlus(noSeq, ims);
		}
		return imptmp;
	}

	private ImagePlus JpegLossLess() {

		ImagePlus imptmp = null;

		DebugTools.enableLogging("OFF");
		String id = listFile[0];
		ImageProcessorReader r = new ImageProcessorReader(new ChannelSeparator(LociPrefs.makeImageReader()));

		try {
			r.setGroupFiles(false);
			r.setId(id);
			int width = r.getSizeX();
			int height = r.getSizeY();
			ImageStack stack = new ImageStack(width, height);
			for (int i = 0; i < listFile.length; i++) {
				r.setId(listFile[i]);
				ImageProcessor ip = r.openProcessors(0)[0];
				stack.addSlice("" + (i + 1), ip);
			}
			imptmp = new ImagePlus("", stack);
			r.close();
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		return imptmp;
	}

	private ImagePlus normalizationDicom(ImagePlus imptmp) {
		String reslope = null, reintercept = null, slope = null;
		reslope = infoImage.get("Rescale Slope");
		reintercept = infoImage.get("Rescale Intercept");
		slope = infoImage.get("Scale Slope");
		double[] RS = Arrays.asList(reslope.split(" +")).stream().mapToDouble(Double::parseDouble).toArray();
		double[] RI = Arrays.asList(reintercept.split(" +")).stream().mapToDouble(Double::parseDouble).toArray();
		// Float[] SS = Arrays.stream(slope.split("
		// +")).map(Float::valueOf).toArray(Float[]::new);
		double[] SS = null;
		if (!slope.trim().isEmpty())
			SS = Arrays.asList(slope.split(" +")).stream().mapToDouble(Double::parseDouble).toArray();

		if (infoImage.get("Pixel Representation").trim().contentEquals("1"))
			imptmp.getCalibration().setSigned16BitCalibration();
		else
			imptmp = convertToGray32(imptmp);

//		System.out.println(this);
//		System.out.println("RS = " + Arrays.toString(RS));
//		System.out.println("RI = " + Arrays.toString(RI));
//		System.out.println("SS = " + Arrays.toString(SS));

		
		if (RS.length == 1) {
			for (int i = 0; i < imptmp.getStackSize(); i++) {
				imptmp.setSlice(i + 1);
				if (RS[0] != 1.0)
					imptmp.getProcessor().multiply(RS[0]);
				if (RI[0] != 0.0)
					imptmp.getProcessor().add(RI[0]);
				if (!slope.trim().isEmpty())
					imptmp.getProcessor().multiply(1 / (RS[0] * SS[0]));
			}

		} else
			for (int i = 0; i < imptmp.getStackSize(); i++) {
				imptmp.setSlice(i + 1);
				if (RS[i] != 1.0)
					imptmp.getProcessor().multiply(RS[i]);
				if (RI[i] != 0.0)
					imptmp.getProcessor().add(RI[i]);
				if (!slope.trim().isEmpty())
					imptmp.getProcessor().multiply(1 / (RS[i] * SS[i]));
			}
		return imptmp;
	}

	private ImagePlus normalizationCT(ImagePlus imptmp) {
		String reintercept = null;
		reintercept = infoImage.get("Rescale Intercept");
		double[] RI = Arrays.asList(reintercept.split(" +")).stream().mapToDouble(Double::parseDouble).toArray();
		double decal = 0.0;

		if (infoImage.get("Pixel Representation").trim().contentEquals("1"))
			imptmp.getCalibration().setSigned16BitCalibration();

		if (infoImage.get("Pixel Representation").trim().contentEquals("0") && RI[0] < 0.0) {
			imptmp.getCalibration().setSigned16BitCalibration();
			decal = 32768;
		}

		if (RI.length == imptmp.getStackSize())
			for (int i = 0; i < imptmp.getStackSize(); i++) {
				imptmp.setSlice(i + 1);
				imptmp.getProcessor().add(decal + RI[i]);
			}

		return imptmp;
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

	public ImagePlus getImp() {
		imp.setTitle("");
		return imp;
	}
}