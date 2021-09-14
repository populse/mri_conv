package brukerParavision;

import java.awt.Color;
import java.awt.Frame;
import java.util.HashMap;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Plot;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.measure.Calibration;
import ij.plugin.Duplicator;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;

public class OpenBruker implements ParamMRI2 {

	private int c, z, t, off;
	private float resolZ;
	private ImagePlus imp;
//	private String[] lp = null;
	private Plot profPlot;
//	private HashMap<String, String> infoImage;
	private boolean convert;
	private String logParametric, title, order;

	public OpenBruker(HashMap<String, String> infoImage, Object[] orderImage, Boolean show, Boolean convert,
			String title) {
//		this.infoImage = infoImage;
		this.convert = convert;
		this.title = title;
		String tmp = null, scanmode = "";
		int w, h;
		float pxw, pxh;
		float fovX, fovY;

//		float scaleX = 1, scaleY = 1;

		scanmode = infoImage.get("Scan Mode");

		if (!scanmode.contains("1")) {
			/*****************************************************
			 * create imp
			 *****************************************************/
			tmp = infoImage.get("Scan Resolution");
			w = Integer.parseInt(tmp.split(" +")[0]);
			h = Integer.parseInt(tmp.split(" +")[1]);

			pxw = Float.parseFloat(infoImage.get("Spatial Resolution").split(" +")[0]);
			pxh = Float.parseFloat(infoImage.get("Spatial Resolution").split(" +")[1]);

			fovX = Float.parseFloat(infoImage.get("FOV").split(" +")[0]);
			fovY = Float.parseFloat(infoImage.get("FOV").split(" +")[1]);

			String sliceOrientation = infoImage.get("Slice Orientation");
			String readDirection = infoImage.get("Read Direction");

			int g;
			float ee;

			if ((sliceOrientation.contentEquals("coronal") && readDirection.contentEquals("H_F"))
					|| (sliceOrientation.contentEquals("sagittal") && readDirection.contentEquals("H_F"))
					|| (sliceOrientation.contentEquals("axial") && readDirection.contentEquals("A_P"))) {
				g = w;
				w = h;
				h = g;
				ee = pxw;
				pxw = pxh;
				pxh = ee;
			}

			order = orderImage[0].toString();
			c = (int) orderImage[1];
			z = (int) orderImage[2];
			t = (int) orderImage[3];

//			System.out.println("c , z , t : "+c+" , "+z+" , "+t);

			if (orderImage[4] != null)
				logParametric = orderImage[4].toString();
			off = (int) orderImage[5];

			FileInfo fi = new FileInfo();

			tmp = infoImage.get("Data Type");

			if (tmp.contains("_16BIT_SGN_INT"))
				fi.fileType = FileInfo.GRAY16_SIGNED;
			else if (tmp.contains("_32BIT_SGN_INT"))
				fi.fileType = FileInfo.GRAY32_INT;
			else if  (tmp.contains("_32BIT_FLOAT"))
				fi.fileType = FileInfo.GRAY32_FLOAT;
			else
				fi.fileType = FileInfo.GRAY8;

			tmp = infoImage.get("File path");

			fi.fileFormat = FileInfo.RAW;
			fi.fileName = tmp;
			fi.width = w;
			fi.height = h;
			fi.offset = 0;
			fi.gapBetweenImages = 0;

			if (infoImage.get("Byte Order").contains("littleEndian"))
				fi.intelByteOrder = true;
			else
				fi.intelByteOrder = false;
			fi.unit = "mm";
			fi.valueUnit = "mm";

			if (scanmode.contains("2"))
				tmp = "1";
			else
				tmp = infoImage.get("Scan Resolution").split(" +")[2];

			fi.nImages = Integer.parseInt(tmp) * Integer.parseInt(infoImage.get("Images In Acquisition"));

			fi.pixelWidth = pxw;
			fi.pixelHeight = pxh;

			if (scanmode.contains("2")) {
				if (infoImage.get("Slice Separation").split(" +").length == 1)
					resolZ = Float.parseFloat(infoImage.get("Slice Separation"));
				else
					resolZ = Float.parseFloat(infoImage.get("Slice Thickness"));
				if (logParametric != null) {
					if (logParametric.contentEquals("multiOrientation")
							&& Integer.parseInt(infoImage.get("Number Of Slice")) == 1)
						resolZ = Float.parseFloat(infoImage.get("Slice Thickness"));
					else
						resolZ = Float.parseFloat(infoImage.get("Slice Separation"));
				}

			} else
				resolZ = 10 * Float.parseFloat(infoImage.get("FOV").split(" +")[2])
						/ Float.parseFloat(infoImage.get("Scan Resolution").split(" +")[2]);

			fi.pixelDepth = resolZ;

			FileOpener fo = new FileOpener(fi);

			imp = fo.open(false);

			imp = convertToGray32(imp);

			/**************************
			 * Normalization
			 *****************************************************/

			NormalizationImageIRM scal = new NormalizationImageIRM(infoImage.get("File path"));

			double[] factor = null, offset = null;

			try {
				factor = scal.factorNormalize();
				offset = scal.offsetNormalize();
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
			
			if (scanmode.contains("2")) {

				try {
					if (factor.length != 1 && offset.length != 1) {
						for (int i = 0; i < imp.getStackSize(); i++) {
							imp.setSlice(i + 1);
							imp.getProcessor().multiply(factor[i]);
							imp.getProcessor().add(offset[i]);
						}
					}

					if (factor.length != 1 && offset.length == 1) {
						if (offset[0] != 0)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().multiply(factor[i]);
								imp.getProcessor().add(offset[0]);
							}
						else
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().multiply(factor[i]);
							}
					}

					if (factor.length == 1 && offset.length != 1) {
						if (factor[0] != 1)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().multiply(factor[0]);
								imp.getProcessor().add(offset[i]);
							}
						else
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().add(offset[i]);
							}
					}

					if (factor.length == 1 && offset.length == 1) {
						if (factor[0] != 1 && offset[0] != 0)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().multiply(factor[0]);
								imp.getProcessor().add(offset[0]);
							}
						else if (factor[0] != 1)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().multiply(factor[0]);
							}
						else if (offset[0] != 0)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().add(offset[0]);
							}
						else
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
							}
					}

				} catch (Exception e) {
					new GetStackTrace(e, this.getClass().toString());
				}

				imp.updateImage();
			}

			/************************************
			 * non isotropic image for display
			 ***********************************/
			if (!convert) {
				if (fovX == fovY) {
					if (w > h) {
//						scaleY = w / h;
						h = w;
						pxh = pxw;
					} else {
//						scaleX = h / w;
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
			}
			imp.resetDisplayRange();

			/******************************************************
			 * hyperstack ?
			 *****************************************************/
			try {

				if (c != 1 && t != 1 && z != 1)
					imp = HyperStackConverter.toHyperStack(imp, z, c, t, order, "grayscale");
				else if ((c != 1 || t != 1) && z != 1)
					imp = HyperStackConverter.toHyperStack(imp, c, z, t, order, "grayscale");
				else
					imp = HyperStackConverter.toHyperStack(imp, c, z, t, order, "grayscale");

			} catch (Exception e) {
			}

			if (scanmode.contains("3")) {

				try {
					if (factor.length != 1 && offset.length != 1) {
						for (int j = 0; j < imp.getNFrames(); j++) {
							imp.setT(j + 1);
							for (int i = 0; i < imp.getNSlices(); i++) {
								imp.setZ(i + 1);
								imp.getProcessor().multiply(factor[j]);
								imp.getProcessor().add(offset[j]);
							}
						}
					}

					if (factor.length != 1 && offset.length == 1) {
						if (offset[0] != 0)
							for (int j = 0; j < imp.getNFrames(); j++) {
								imp.setT(j + 1);
								for (int i = 0; i < imp.getNSlices(); i++) {
									imp.setZ(i + 1);
									imp.getProcessor().multiply(factor[j]);
									imp.getProcessor().add(offset[0]);
								}
							}
						else
							for (int j = 0; j < imp.getNFrames(); j++) {
								imp.setT(j + 1);
								for (int i = 0; i < imp.getNSlices(); i++) {
									imp.setZ(i + 1);
									imp.getProcessor().multiply(factor[j]);
								}
							}
					}

					if (factor.length == 1 && offset.length != 1) {
						if (factor[0] != 1)
							for (int j = 0; j < imp.getNFrames(); j++) {
								imp.setT(j + 1);
								for (int i = 0; i < imp.getNSlices(); i++) {
									imp.setSlice(i + 1);
									imp.getProcessor().multiply(factor[0]);
									imp.getProcessor().add(offset[j]);
								}
							}
						else
							for (int j = 0; j < imp.getNFrames(); j++) {
								imp.setT(j + 1);
								for (int i = 0; i < imp.getNSlices(); i++) {
									imp.setSlice(i + 1);
									imp.getProcessor().add(offset[j]);
								}
							}
					}

					if (factor.length == 1 && offset.length == 1) {
						if (factor[0] != 1 && offset[0] != 0)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().multiply(factor[0]);
								imp.getProcessor().add(offset[0]);
							}
						else if (factor[0] != 1)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().multiply(factor[0]);
							}
						else if (offset[0] != 0)
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
								imp.getProcessor().add(offset[0]);
							}
						else
							for (int i = 0; i < imp.getStackSize(); i++) {
								imp.setSlice(i + 1);
							}
					}

				} catch (Exception e) {
					new GetStackTrace(e, this.getClass().toString());
				}

				imp.updateImage();
			}

			if (show) {
//				if (infoImage.get("Slice Orientation").split(" +").length == 1) {
//					listPosition();
//					EventQueue.invokeLater(new Runnable() {
//						@Override
//						public void run() {
//							for (int k = 1; k < c + 1; k++)
//								for (int j = 1; j < t + 1; j++)
//									for (int i = 1; i < z + 1; i++) {
//										// IJ.setSlice(i+(j-1)*z);
//										IJ.setSlice(imp.getStackIndex(k, i, j));
//										if (lp.length != 1)
//											IJ.run("Set Label...", "label=Pos:" + Float.valueOf(lp[i - 1]) + " mm");
//										else
//											try {
//												IJ.run("Set Label...", "label=Pos:" + Float.valueOf(lp[0]) + " mm");
//											} catch (Exception e) {
//											}
//									}
//						}
//					});
//				}

				imp.setTitle("Seq No. " + title);
				imp.resetDisplayRange();
//				BufferedImage buf = imp.getBufferedImage();
//				buf = createTransform(buf, buf.getWidth(), buf.getHeight());
//				new ImagePlus("", buf).show();
				imp.show();

				// IJ.run("Size...", "width=240 height=240 depth=40 average
				// interpolation=Bilinear");

				// System.out.println(orderIm[4]);

				if (orderImage[4] != null) {

					IJ.log("Seq No. " + title + "\n" + orderImage[4]);
					Frame lw = WindowManager.getFrame("Log");
					lw.setSize(500, 400);
					lw.setLocale(imp.getWindow().getLocale());
				}

				imp.resetStack();
			}
		} // end if dim!=1

		else if (scanmode.contains("1")) {
			FileInfo fi = new FileInfo();

			double bdw = Double.parseDouble(infoImage.get("BandWidth"));
			tmp = infoImage.get("Scan Resolution");
			w = Integer.parseInt(tmp);

			double resol = bdw / w;

			tmp = infoImage.get("Data Type");

			if (tmp.contains("_16BIT_SGN_INT"))
				fi.fileType = FileInfo.GRAY16_SIGNED;
			else if (tmp.contains("_32BIT_SGN_INT"))
				fi.fileType = FileInfo.GRAY32_INT;
			else if  (tmp.contains("_32BIT_FLOAT"))
				fi.fileType = FileInfo.GRAY32_FLOAT;
			else
				fi.fileType = FileInfo.GRAY8;

			tmp = infoImage.get("File path");

			fi.fileName = tmp;
			fi.width = w;
			fi.height = 1;
			fi.offset = 0;
			fi.nImages = 1;
			fi.intelByteOrder = true;
			fi.unit = "Hz";
			fi.valueUnit = "Hz";
			fi.pixelWidth = resol;

			FileOpener fo = new FileOpener(fi);
			imp = fo.open(false);
			imp.setRoi(0, 0, w, 1);

			double X0[] = new double[w];
			for (int i = 0; i < w; i++) {
				X0[i] = -(bdw / 2) + resol * i;
			}

			profPlot = new Plot(title, "Frequency (Hz)", "Amplitude");
			profPlot.setSize(600, 300);
			profPlot.setColor(Color.blue);
			profPlot.addPoints(X0, imp.getProcessor().getLine(0, 0, w, 0), Plot.LINE);

			if (show) {
				profPlot.show();
			}
		}
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

//	private void listPosition() {
//
//		String sliceorient = infoImage.get("Slice Orientation");
//		String pathVisupars = infoImage.get("File path");
//		pathVisupars = pathVisupars.substring(0, pathVisupars.lastIndexOf(PrefParam.separator) + 1);
//		pathVisupars = pathVisupars + "visu_pars";
//		int dimension = Integer.parseInt(scanmode);
//
//		if (sliceorient.equalsIgnoreCase("axial"))
//			try {
//				lp = new ListPositionFrame(pathVisupars, 1).listofPositionFrame()[2];
//				if (dimension == 3) {
//					BigDecimal resol = new BigDecimal(String.valueOf(resolZ));
//					BigDecimal pos = new BigDecimal(lp[0]);
//					lp = new String[z];
//					for (int i = 0; i < z; i++) {
//						lp[i] = String.valueOf((resol.multiply(new BigDecimal(String.valueOf(i)))).add(pos));
//						lp[i] = lp[i].substring(0, lp[i].indexOf(".") + 2);
//						if (lp[i].contains("E"))
//							lp[i] = "0";
//					}
//				}
//
//			} catch (Exception e) {
//				new GetStackTrace(e);
//			}
//		else if (sliceorient.equals("coronal"))
//			try {
//				lp = new ListPositionFrame(pathVisupars, -1).listofPositionFrame()[2];
//				if (dimension == 3) {
//					BigDecimal resol = new BigDecimal(String.valueOf(resolZ));
//					resol = resol.multiply(new BigDecimal(String.valueOf("-1")));
//					BigDecimal pos = new BigDecimal(lp[0]);
//					lp = new String[z];
//					for (int i = 0; i < z; i++) {
//						lp[z - i - 1] = String.valueOf((resol.multiply(new BigDecimal(String.valueOf(i)))).add(pos));
//						lp[z - i - 1] = lp[z - i - 1].substring(0, lp[z - i - 1].indexOf(".") + 2);
//						if (lp[z - i - 1].contains("E"))
//							lp[z - i - 1] = "0";
//					}
//				}
//			} catch (Exception e) {
//				new GetStackTrace(e);
//			}
//		else if (sliceorient.equals("sagittal"))
//			try {
//				lp = new ListPositionFrame(pathVisupars, 1).listofPositionFrame()[2];
//				if (dimension == 3) {
//
//					BigDecimal resol = new BigDecimal(String.valueOf(resolZ));
//					BigDecimal pos = new BigDecimal(lp[0]);
//					lp = new String[z];
//					for (int i = 0; i < z; i++) {
//						lp[i] = String.valueOf((resol.multiply(new BigDecimal(String.valueOf(i)))).add(pos));
//						lp[i] = lp[i].substring(0, lp[i].indexOf(".") + 2);
//						if (lp[i].contains("E"))
//							lp[i] = "0";
//					}
//				}
//			} catch (Exception e) {
//				new GetStackTrace(e);
//			}
//	}

	public ImagePlus getImp() {
		if (logParametric != null) {
			if (convert && logParametric.contentEquals("multiOrientation")) {
				imp = new Duplicator().run(imp, off + 1, off + (z / 3));
			} else if (convert && PrefParam.namingOptionsNiftiExport.substring(0, 1).contentEquals("1")) {
				imp.setTitle("");
				String[] listInf = logParametric.split("\n");
				String descr = listInf[0].substring(0, listInf[0].indexOf(":"));
				descr = descr.trim().replaceAll(" +", "-");
				String label = "";
				int rang = 0;
				for (int d = 1; d < listInf.length; d++) {
					label = listInf[d].substring(listInf[d].indexOf(" - ") + 3, listInf[d].indexOf(" ["));
					label = label.trim().replaceAll(" +", "-");
					if (title.contains(descr + "-" + label)) {
						rang = Integer
								.parseInt(listInf[d].substring(listInf[d].indexOf("t:") + 2, listInf[d].indexOf("/")));
						if (imp.getNFrames() == 1)
							imp = new Duplicator().run(imp, 1, imp.getNChannels(), rang, rang, 1, 1);
						else
							imp = new Duplicator().run(imp, 1, imp.getNChannels(), 1, imp.getNSlices(), rang, rang);
						imp.setTitle(label);
					}
				}
			}
		}
		return imp;
	}

//	private BufferedImage createTransform(BufferedImage img, int imgWidth, int imgHeight) {
//		BufferedImage buffer = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_BYTE_GRAY);
//		Graphics2D g = buffer.createGraphics();
//		g.drawImage(img, null, null);
//		AffineTransform reflectTransform = new AffineTransform(-0.5, 0, 0, -0.5, 0, 0);
//		g.drawImage(img, reflectTransform, null);
//		g.setTransform(reflectTransform);
//		g.dispose();
//		return buffer;
//	}
}