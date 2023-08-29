package exportFiles;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import MRIFileManager.DictionaryYaml2;
import MRIFileManager.FileManagerFrame;
import MRIFileManager.GetStackTrace;
import MRIFileManager.OpenImageJ;
import MRIFileManager.UtilsSystem;
import MRIFileManager.passMatlab;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import abstractClass.convertNifti;
import brukerParavision.ConvertBrukerToNifti;
import dcm.ConvertDicomToNifti;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileInfo;
import ij.io.ImageWriter;
import ij.measure.Calibration;
import ij.plugin.Duplicator;
import ij.process.ColorProcessor;
import philips.ConvertPhilipsToNifti;


public class ConvertImage2 extends PrefParam implements ParamMRI2 {

	public static final int ANALYZE_7_5 = 0;
	public static final int NIFTI_ANALYZE = 1;
	public static final int NIFTI_FILE = 2;
	public boolean littleEndian = false; // Change this to force little endian output
	private int output_type = NIFTI_FILE; // Change this to change default output

	private String directory = "", suffix = ".nii", choiceExport = "Nifti-1";
	private convertNifti conv;
	private FileManagerFrame wind;

	private ImagePlus imp;
	private double[] quaterns = new double[5];
	private double[][] srow = new double[3][4];
	private int qform, sform;
	private Boolean bvec_bval = false;
	private Boolean continuSave = false, noAll = false;

	// constructor overloading for script windowless
	public ConvertImage2(int qform, int sform, convertNifti conv, String seqSel, String repWork,
			String repertoryExport, String constructor) {
		bvec_bval = false;
		continuSave = true;

		boolean answ = false;

		for (int j = 0; j < 5; j++)
			quaterns[j] = 0.0; // initialization
		for (int j = 0; j < 3; j++)
			srow[j][j] = 1.0; // initialization

		this.qform = qform;
		this.sform = sform;

		imp = conv.convertToNifti(seqSel);
		conv.AffineQuaternion(seqSel);
		quaterns = conv.quaterns();
		srow = conv.srow();

//		answ = save(imp, repWork, repertoryExport, ".nii", seqSel, true);

		System.out.print("export to ..... " + repWork + separator + repertoryExport + suffix + '\n');
		String tmp = namingOptionsNiftiExport;
		boolean bool = (tmp.substring(3, 4).contentEquals("0")) ? false : true;
		String onetwofile = tmp.substring(4, 5);
		String tmpotf = onetwofile;
		boolean fc;
		if (bool) {
			try {
				if (Integer.parseInt(onetwofile) > 3)
					tmpotf = String.valueOf(Integer.parseInt(onetwofile) - 4);
				fc = new GenerateBvecsBvals2(repWork, repertoryExport, seqSel, constructor, tmpotf).fileCreated();

				if (fc)
					bvec_bval = true;

				if (fc && Integer.parseInt(onetwofile) > 3) {
					qform = 1;
					sform = 1;
					FileInfo fi = imp.getFileInfo();
					for (int j = 0; j < 5; j++)
						quaterns[j] = 0.0; // initialization
					for (int j = 0; j < 3; j++)
						for (int k = 0; k < 4; k++)
							srow[j][k] = 0.0; // initialization
					srow[0][0] = fi.pixelWidth;
					srow[1][1] = fi.pixelHeight;
					srow[2][2] = fi.pixelDepth;
					srow[3][3] = 1;
					save(imp, repWork, repertoryExport + "-zero_transform", suffix, seqSel, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		answ = save(imp, repWork, repertoryExport, ".nii", seqSel, true);
		if (!answ)
			System.out.println("not exported (" + repWork + " doesn't exist or unauthorized ?) ");

		imp.close();
		imp = null;
		System.gc();

	}

	// constructor overloading for Bids reading (windowless mode)
	public ConvertImage2(String seqSel, String repWork, String repertoryExport) {
		continuSave = true;
		boolean answ = false;
		String fileOrigin = listBasket_hmInfo.get(seqSel).get("File path");
		answ = recordBidsUnzip(fileOrigin, repWork, repertoryExport, seqSel);

		if (answ)
			System.out.println("exported to " + repWork + separator + repertoryExport + suffix);
	}

	// constructor usual
	public ConvertImage2(FileManagerFrame wind) {

		this.wind = wind;

		Boolean ok = true;

		if (FilestmpExportNifit == null) {
			final JFileChooser rep = new JFileChooser();
			rep.setAcceptAllFileFilterUsed(false);
			rep.setCurrentDirectory(new File(outExport));
			rep.setSelectedFile(new File(outExport));
			rep.setApproveButtonText("Select this directory");
			rep.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			rep.setLocale(Locale.ENGLISH);
			rep.updateUI();

			switch (rep.showOpenDialog(null)) {
			case JFileChooser.APPROVE_OPTION:
				directory = rep.getSelectedFile().getPath();
				FilestmpExportNifit = rep.getSelectedFile();
				wind.getpathExportNifti().setText(directory);
				ok = true;
				break;

			case JFileChooser.CANCEL_OPTION:
				ok = false;
				break;
			}
		}

		else {
			directory = FilestmpExportNifit.toString();
			File f = new File(directory);
			if (!f.exists())
				f.mkdirs();
			ok = true;
		}

		if (ok) {
			convertToNifti();
			listinBasket.removeAllElements();
			listBasket_hmOrderImage.clear();
			listBasket_hmInfo.clear();
			listBasket_hmSeq.clear();
		}
	}

	private void convertToNifti() {

		Object lb;
		String newDirectory;

		FileManagerFrame.dlg.setVisible(true);

		String title = "Conversion ";
		Boolean error = false, answ = false;
		String logExportNifti = "";
		String JsonMIA = "";
		String nameFile = "";

		String forCur = formatCurrent;
		forCur = forCur.replace("[", "");
		forCur = forCur.replace("]", "");
		boolean changeDict = false;

		choiceExport = wind.getChoiceExport().getSelectedItem().toString();

		for (int i = 0; i < listinBasket.size(); i++) {
			
			bvec_bval = false;

			error = false;
			answ = false;
			FileManagerFrame.dlg.setTitle(title + i * 100 / listinBasket.size() + " %");
			lb = listinBasket.getElementAt(i);

			newDirectory = directory;

			for (int j = 0; j < 5; j++)
				quaterns[j] = 0.0; // initialization
			for (int j = 0; j < 3; j++)
				srow[j][j] = 1.0; // initialization

			String typeLib = lb.toString().substring(0, lb.toString().indexOf("]"));

			if (typeLib.contains("Bruker")) {
				if (!forCur.contentEquals("Bruker")) {
					changeDict = true;
					forCur = "Bruker";
				}
				qform = 0; // ??? qform = 2
				sform = 2; // ??? sform = 1
				conv = new ConvertBrukerToNifti();
			} else if (typeLib.contains("Dicom")) {
				if (!forCur.contentEquals("Dicom")) {
					changeDict = true;
					forCur = "Dicom";
				}
				qform = 0; // ??? qform = 1
				sform = 2; // ??? sform = 1
				conv = new ConvertDicomToNifti();
			} else if (typeLib.contains("Philips")) {
				if (!forCur.contentEquals("Philips")) {
					changeDict = true;
					forCur = "Philips";
				}
				qform = 0; // ??? qform = 1
				sform = 2; // ??? sform = 1
				conv = new ConvertPhilipsToNifti();
			} else if (typeLib.contains("Nifti")) {
				if (!forCur.contentEquals("Nifti")) {
					changeDict = true;
					forCur = "Nifti";
				}
			} else if (typeLib.contains("Bids")) {
				if (!forCur.contentEquals("Bids")) {
					changeDict = true;
					forCur = "Bids";
				}
			}

			if (changeDict) {
				new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml", forCur);
				new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_User.yml", forCur);
				changeDict = false;
			}

			try {

				List<String> list = new ArrayList<String>();

				nameFile = (lb.toString().substring(lb.toString().indexOf("]") + 2)).replaceAll(" +", "");
				nameFile = nameFile.substring(0, nameFile.indexOf("[")).trim();

				if (choiceExport.contentEquals("BIDS")) {
					String tmpF = directory + separator + nameFile.substring(0, nameFile.indexOf(separator));
					if (!list.contains(tmpF)) {
						new Description_Bids_Json(tmpF);
						list.add(tmpF);
					}
				}

				String[] lm = createRepertories(newDirectory, nameFile);
				newDirectory = lm[0];
				nameFile = lm[1];
				nameFile = new ReplacecharForbidden().charReplace(nameFile);

				if (forCur.contentEquals("Nifti")) {

					String fileOrigin = listBasket_hmInfo.get(lb).get("File path");
					answ = copyNifti(fileOrigin, newDirectory, nameFile, lb);

					// Files.copy(new File(fileOrigin).toPath(),
					// new File(newDirectory + separator + nameFile + ".nii").toPath(),
					// StandardCopyOption.REPLACE_EXISTING);
					//
					// logExportNifti += "Export success for " + lb + "\n";
					//
					// fileOrigin = fileOrigin.replace(".nii", ".json");
					//
					// if (new File(fileOrigin).exists()) {
					// nameFile = nameFile.replace(".nii", "");
					// Files.copy(new File(fileOrigin).toPath(),
					// new File(newDirectory + separator + nameFile + ".json").toPath(),
					// StandardCopyOption.REPLACE_EXISTING);
					// } else
					// new ConvertToJson2(newDirectory, nameFile, lb, null);
				}

				else if (forCur.contentEquals("Bids")) {
					String fileOrigin = listBasket_hmInfo.get(lb).get("File path");
//					System.out.println(this + ": fileOrigin= " + fileOrigin);
//					System.out.println(this + ": newDirectory= " + newDirectory);
//					System.out.println(this + ": nameFile= " + nameFile);
					answ = recordBidsUnzip(fileOrigin, newDirectory, nameFile, lb);
					File fileBvec = new File(fileOrigin.replace(".nii.gz", ".bvec"));
					File fileBval = new File(fileOrigin.replace(".nii.gz", ".bval"));

					if (fileBvec.exists()) {
						bvec_bval = true;
						Files.copy(fileBvec.toPath(), new File(newDirectory + separator + nameFile + ".bvec").toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
					if (fileBval.exists())
						Files.copy(fileBval.toPath(), new File(newDirectory + separator + nameFile + ".bval").toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				}

				else {
					
					imp = conv.convertToNifti((String) lb);
					conv.AffineQuaternion((String) lb);
					quaterns = conv.quaterns();
					srow = conv.srow();

					String tmp = namingOptionsNiftiExport;
					boolean bool = (tmp.substring(3, 4).contentEquals("0")) ? false : true;
					String onetwofile = tmp.substring(4, 5);
					String tmpotf = onetwofile;
					if (bool) {
						try {
							if (Integer.parseInt(onetwofile) > 3)
								tmpotf = String.valueOf(Integer.parseInt(onetwofile) - 4);
							boolean fc = new GenerateBvecsBvals2(newDirectory, nameFile, lb, forCur, tmpotf)
									.fileCreated();
							if (fc)
								bvec_bval = true;

							if (fc && Integer.parseInt(onetwofile) > 3) {
								onetwofile = String.valueOf(Integer.parseInt(onetwofile) - 4);
								qform = 1;
								sform = 1;
								FileInfo fi = imp.getFileInfo();
								for (int j = 0; j < 5; j++)
									quaterns[j] = 0.0; // initialization
								for (int j = 0; j < 3; j++)
									for (int k = 0; k < 4; k++)
										srow[j][k] = 0.0; // initialization
								srow[0][0] = fi.pixelWidth;
								srow[1][1] = fi.pixelHeight;
								srow[2][2] = fi.pixelDepth;
								srow[3][3] = 1;
								answ = save(imp, newDirectory, nameFile + "-zero_transform", suffix, lb, false);
							}
//							new GenerateBvecsBvals2(newDirectory, nameFile, lb, forCur, onetwofile);
						} catch (Exception e) {
							new GetStackTrace(e, this.getClass().toString());
						}
					}

					if (lb.toString().contains("Bruker") && lb.toString().contains("cineASL")) {
						
						ImagePlus imp1 = new Duplicator().run(imp, 1, imp.getNChannels()/2, 1, 1, 1, imp.getNFrames());
						ImagePlus imp2 = new Duplicator().run(imp, 1 + imp.getNChannels()/2, imp.getNChannels(), 1, 1, 1, imp.getNFrames());
						answ = save(imp1, newDirectory, nameFile + "_control", suffix, lb, true);
						if (answ) {
							logExportNifti += "Export success for " + lb +  "(control)\n";
							JsonMIA += new LogJsonMIA(directory, lb, nameFile + "_control", error, bvec_bval).getJson() + ",";
						}
						answ = save(imp2, newDirectory, nameFile + "_marquage", suffix, lb, true);
						if (answ) {
							logExportNifti += "Export success for " + lb +  "(marquage)\n";
							JsonMIA += new LogJsonMIA(directory, lb, nameFile + "_marquage", error, bvec_bval).getJson() + ",";
						}
					}
					answ = save(imp, newDirectory, nameFile, suffix, lb, true);

					imp.close();
					imp = null;
					System.gc();
				}

				if (answ)
					logExportNifti += "Export success for " + lb + "\n";

			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
				logExportNifti += "Export failed for " + lb + "\n";
				logExportNifti += "      " + e.toString() + "\n";
				error = true;
			}
			JsonMIA += new LogJsonMIA(directory, lb, nameFile, error, bvec_bval).getJson() + ",";
		} // end for

		JsonMIA = JsonMIA.substring(0, JsonMIA.lastIndexOf(","));
		JsonMIA = "[" + JsonMIA + "]";

		FileManagerFrame.dlg.setVisible(false);

		Object[] options = { "Ok", "See log file" };
		int n = 0;

		if (!error && logExportNifti.length() != 0)
			n = JOptionPane.showOptionDialog(wind, "Export completed with success", "log message",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title

		else if (!error && logExportNifti.length() == 0)
			JOptionPane.showMessageDialog(wind, "No exported data", "log message", JOptionPane.YES_NO_OPTION); // default
																												// button
																												// title

		else if (logExportNifti.length() != 0)
			n = JOptionPane.showOptionDialog(wind, "Warning : some conversions are failed !!", "log message",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String pathLogExport = directory + separator + "logExport_" + dateFormat.format(date);

		if (LogExport && logExportNifti.length() != 0) {

			try {
				FileWriter printRep = new FileWriter(pathLogExport);
				printRep.write(logExportNifti);
				printRep.close();
			}

			catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}

			try {
				FileWriter printRep = new FileWriter(pathLogExport + ".json");
				printRep.write(JsonMIA);
				printRep.close();
			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
			}
		}

		if (MIA)
			new passMatlab(JsonMIA);

		if (n == 1) {
			new OpenImageJ();
			IJ.log(logExportNifti);
		}
	}

	public boolean copyNifti(String fileOrigin, String directory, String name, Object title) {

		if (!noAll) {

			if (name == null)
				return false;

			name = name.trim();
			directory = directory.trim();

			if (!directory.endsWith(PrefParam.separator))
				directory += PrefParam.separator;

			int n = 0;

			if (new File(directory + name + ".nii").exists() && !continuSave) {
				n = JOptionPane.showOptionDialog(null,
						directory + name + ".nii" + "\n" + "This file already exits, Do you want to overwrite it? ",
						"Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new Object[] { "Yes", "Yes to all", "No", "No to all" }, "No");
			}

			if (n == 3) {
				noAll = true;
				return false;
			}

			if (n == 2)
				return false;

			if (n == 1)
				continuSave = true;

			try {
				Files.copy(new File(fileOrigin).toPath(), new File(directory + separator + name + ".nii").toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				fileOrigin = fileOrigin.replace(".nii", ".json");

				if (new File(fileOrigin).exists()) {
					name = name.replace(".nii", "");
					if (listBasket_hmInfo.get(title).get("DataAnonymized").contentEquals("yes")) {
						System.out.println(this + " : " + "create new Json because of anonymization");
						String obj = new ChangeJson(fileOrigin, title).newObj();
						FileWriter writer = new FileWriter(directory + separator + name + ".json");
						writer.write(obj);
						writer.flush();
						writer.close();
					} else
						Files.copy(new File(fileOrigin).toPath(),
								new File(directory + separator + name + ".json").toPath(),
								StandardCopyOption.REPLACE_EXISTING);
				} else
					new ConvertToJson2(directory, name, title, null);
				return true;

			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
				IJ.log("Nifti_Writer: " + e.getMessage());
				return false;
			}
		} else
			return false;
	}

	public boolean recordBidsUnzip(String fileOrigin, String directory, String name, Object title) {
		if (!noAll) {
			if (name == null)
				return false;
			name = name.trim();
			directory = directory.trim();
			if (!directory.endsWith(PrefParam.separator))
				directory += PrefParam.separator;
			int n = 0;
			if (new File(directory + name + ".nii").exists() && !continuSave) {
				n = JOptionPane.showOptionDialog(null,
						directory + name + ".nii" + "\n" + "This file already exits, Do you want to overwrite it? ",
						"Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new Object[] { "Yes", "Yes to all", "No", "No to all" }, "No");
			}

			if (n == 3) {
				noAll = true;
				return false;
			}

			if (n == 2)
				return false;

			if (n == 1)
				continuSave = true;

			try {

				String newFile = directory + separator + name + ".nii";

				decompressGzipFile(fileOrigin, newFile);

				// Files.copy(new File(fileOrigin).toPath(), new File(directory + separator +
				// name + ".nii.gz").toPath(),
				// StandardCopyOption.REPLACE_EXISTING);

				fileOrigin = fileOrigin.replace(".nii.gz", ".json");

				// if (new File(fileOrigin).exists()) {
				// name = name.replace(".nii.gz", "");
				// if (listBasket_hmInfo.get(title).get("DataAnonymized").contentEquals("yes"))
				// {
				// System.out.println(this + " : " + "create new Json because of
				// anonymization");
				// String obj = new ChangeJson(fileOrigin, title).newObj();
				// FileWriter writer = new FileWriter(directory + separator + name + ".json");
				// writer.write(obj);
				// writer.flush();
				// writer.close();
				// } else
				// Files.copy(new File(fileOrigin).toPath(),
				// new File(directory + separator + name + ".json").toPath(),
				// StandardCopyOption.REPLACE_EXISTING);
				// } else
				new ConvertToJson2(directory, name, title, null);
				return true;

			} catch (Exception e) {
				new GetStackTrace(e, this.getClass().toString());
				IJ.log("Nifti_Writer: " + e.getMessage());
				return false;
			}

		} else
			return false;
	}

	public boolean save(ImagePlus imp, String directory, String name, String suffixe, Object title,
			boolean convertjson) {

		if (!noAll) {

			if (name == null)
				return false;
			name = name.trim();
			directory = directory.trim();

			if (!directory.endsWith(PrefParam.separator))
				directory += PrefParam.separator;

			int n = 0;

			if (new File(directory + name + suffixe).exists() && !continuSave) {
				n = JOptionPane.showOptionDialog(null,
						directory + name + suffixe + "\n" + "This file already exits, Do you want to overwrite it? ",
						"Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						new Object[] { "Yes", "Yes to all", "No", "No to all" }, "No");
			}

			if (n == 3) {
				noAll = true;
				return false;
			}

			if (n == 2)
				return false;

			if (n == 1)
				continuSave = true;

			try {
				String hdrFile = directory + name + suffixe;
				FileOutputStream stream = new FileOutputStream(hdrFile);
				DataOutputStream output = new DataOutputStream(stream);

				IJ.showStatus("Saving as Analyze: " + directory + name + suffixe);

				writeHeader(imp, output, output_type);

				if (imp.getCalibration().isSigned16Bit())
					imp = convertToSigned(imp);

				// System.out.println(imp.getCalibration().isSigned16Bit()+" ,
				// "+imp.getProcessor().getPixel(1, 1)+" , "+imp.getFileInfo().fileType);

				FileInfo fi = imp.getFileInfo();
				if (imp.getCalibration().isSigned16Bit())
					fi.fileType = FileInfo.GRAY16_SIGNED;
				fi.intelByteOrder = littleEndian;
				if (fi.fileType != FileInfo.RGB) {
					// if (imp.getStackSize()>1 && imp.getStack().isVirtual()) {
					// fi.virtualStack = (VirtualStack)imp.getStack();
					// fi.fileName = "FlipTheseImages";
					// }

					if (choiceExport.contentEquals("Nifti-1")) {
						ImageWriter iw = new ImageWriter(fi);
						iw.write(output);
					}

					if (choiceExport.contentEquals("BIDS")) {
						ImageWriter iw = new ImageWriter(fi);
						iw.write(output);
						compressGzipFile(hdrFile, hdrFile + ".gz");
//						compressGzipFile((FileInputStream) fi.inputStream, hdrFile + ".gz");
						new File(hdrFile).delete();
					}
					// else {
					// GZIPOutputStream zstream = new GZIPOutputStream(stream);
					// DataOutputStream output2 = new DataOutputStream(zstream);
					//
					// iw.write(output2);
					// zstream.close();
					// output2.close();
					// }

				} else {
					writeRGBPlanar(imp, output);
				}

				output.close();
				stream.close();

				if (convertjson)
					new ConvertToJson2(directory, name, title, imp.getTitle());

				// String tmp = namingOptionsNiftiExport;
				// boolean bool = (tmp.substring(3,4).contentEquals("0"))?false:true;
				// if (bool)
				// new GenerateBvecs(directory, name, title);

				return true;
			} catch (IOException e) {
				// new GetStackTrace(e, this.getClass().toString());
				// IJ.log("Nifti_Writer: " + e.getMessage());
				return false;
			}
		} else
			return false;
	}

	private void decompressGzipFile(String gzipFile, String newFile) {

		try {
			FileInputStream fis = new FileInputStream(gzipFile);
			GZIPInputStream gis = new GZIPInputStream(fis);
			FileOutputStream fos = new FileOutputStream(newFile);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = gis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			gis.close();

		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
	}

	private void compressGzipFile(String file, String gzipFile) {
		try {
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(gzipFile);
			GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				gzipOS.write(buffer, 0, len);
			}
			gzipOS.close();
			fos.close();
			fis.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

//	private void compressGzipFile(FileInputStream is, String gzipFile) {
//		try {
//			FileOutputStream fos = new FileOutputStream(gzipFile);
//			GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
//			byte[] buffer = new byte[1024];
//			int len;
//			while ((len = is.read(buffer)) != -1) {
//				gzipOS.write(buffer, 0, len);
//			}
//			gzipOS.close();
//			fos.close();
//			is.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	private void writeHeader(ImagePlus imp, DataOutputStream output, int type) throws IOException {

//		System.out.println(this);
//		System.out.println(" imp pixeldepth = "+imp.getCalibration().pixelDepth);
//		System.out.println(" imp pixelwidth = "+imp.getCalibration().pixelWidth);
//		System.out.println(" imp pixelheight = "+imp.getCalibration().pixelHeight);

		FileInfo fi = imp.getFileInfo();
		fi.pixelDepth = imp.getCalibration().pixelDepth;

//		System.out.println("fi pixeldepth = "+fi.pixelDepth);
//		System.out.println("fi pixelwidth = "+fi.pixelWidth);
//		System.out.println("fi pixelheight = "+fi.pixelHeight);
//		System.out.println("Nslices = "+imp.getNSlices());

		short bitsallocated, datatype;
		// Boolean signed16bit = false;

		NiftiHeader nfti_hdr = (NiftiHeader) imp.getProperty("nifti");

		// if (imp.getCalibration().isSigned16Bit()) {
		// signed16bit = true;
		// }

		Calibration c = imp.getCalibration();

		switch (fi.fileType) {
		case FileInfo.GRAY8:
			datatype = 2; // DT_UNSIGNED_CHAR
			bitsallocated = 8;
			break;
		case FileInfo.GRAY16_SIGNED:
			datatype = 4; // DT_SIGNED_SHORT
			bitsallocated = 16;
			break;
		case FileInfo.GRAY16_UNSIGNED:
			datatype = 4; // DT_SIGNED_SHORT
			bitsallocated = 16;
			break;
		case FileInfo.GRAY32_INT:
			datatype = 8; // DT_SIGNED_INT
			bitsallocated = 32;
			break;
		case FileInfo.GRAY32_FLOAT:
			datatype = 16; // DT_FLOAT
			bitsallocated = 32;
			break;
		case FileInfo.RGB:
			datatype = 128; // DT_RGB
			bitsallocated = 24;
			break;
		default:
			datatype = 0; // DT_UNKNOWN
			bitsallocated = (short) (fi.getBytesPerPixel() * 8);
		}

		// header_key

		writeInt(output, 348); // sizeof_hdr
		int i;
		for (i = 0; i < 10; i++)
			output.write(0); // data_type
		for (i = 0; i < 18; i++)
			output.write(0); // db_name
		writeInt(output, 0); // extents
		output.writeShort(0); // session_error
		output.writeByte('r'); // regular
		output.writeByte(0); // hkey_un dim_info

//		 System.out.println(this + " : dimension");
//		 System.out.println("NDimensions = "+imp.getNDimensions());
//		 System.out.println("NSlices = "+imp.getNSlices());
//		 System.out.println("NChannels = "+imp.getNChannels());
//		 System.out.println("NFrames = "+imp.getNFrames());

		if (imp.getNDimensions() == 1) {
			writeShort(output, (short) 4); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			writeShort(output, (short) 1); // dim[3]
			writeShort(output, (short) 1); // dim[4]
		}

		else if (imp.getNDimensions() == 2) {
			writeShort(output, (short) 3); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			// int rap = Integer.parseInt(BasketManager.listBasket.get(seqSel)[4])
			// / Integer.parseInt(BasketManager.listBasket.get(seqSel)[6]);
			writeShort(output, (short) 1); // dim[3]
			writeShort(output, (short) 1); // dim[4]
			writeShort(output, (short) 1); // dim[5]
		} else if (imp.getNDimensions() == 3 && imp.getNSlices() == 1) {
			writeShort(output, (short) 4); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			writeShort(output, (short) 1); // dim[3]
			writeShort(output, (short) fi.nImages); // dim[4]
			writeShort(output, (short) 1); // dim[5]
		} else if (imp.getNDimensions() == 3) {
			writeShort(output, (short) 3); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			writeShort(output, (short) fi.nImages); // dim[3]
			writeShort(output, (short) 1); // dim[4]
			writeShort(output, (short) 1); // dim[5]
		} else if (imp.getNDimensions() == 4 && imp.getNSlices() == 1) {
			writeShort(output, (short) 5); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			writeShort(output, (short) 1); // dim[3]
			writeShort(output, (short) imp.getNFrames()); // dim[4]
			writeShort(output, (short) imp.getNChannels()); // dim[5]
		} else if (imp.getNDimensions() == 4 && imp.getNChannels() == 1) {
			writeShort(output, (short) 4); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			writeShort(output, (short) imp.getNSlices()); // dim[3]
			writeShort(output, (short) imp.getNFrames()); // dim[4]
			writeShort(output, (short) 1); // dim[5]
		} else if (imp.getNDimensions() == 4 && imp.getNFrames() == 1) {
			writeShort(output, (short) 4); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			writeShort(output, (short) imp.getNChannels()); // dim[3]
			writeShort(output, (short) imp.getNSlices()); // dim[4]
			writeShort(output, (short) 1); // dim[5]
		} else if (imp.getNDimensions() == 5) {
			writeShort(output, (short) 5); // dim[0]
			writeShort(output, (short) fi.width); // dim[1]
			writeShort(output, (short) fi.height); // dim[2]
			writeShort(output, (short) imp.getNChannels()); // dim[5]
			writeShort(output, (short) imp.getNSlices()); // dim[3]
			writeShort(output, (short) imp.getNFrames()); // dim[4]
		}

		for (i = 0; i < 2; i++)
			output.writeShort(1); // dim[6-7]

		writeFloat(output, (nfti_hdr == null) ? 0.0 : nfti_hdr.intent_p1); // intent_p1
		writeFloat(output, (nfti_hdr == null) ? 0.0 : nfti_hdr.intent_p2); // intent_p2
		writeFloat(output, (nfti_hdr == null) ? 0.0 : nfti_hdr.intent_p3); // intent_p3
		writeShort(output, (nfti_hdr == null) ? 0 : nfti_hdr.intent_code); // intent_code

		writeShort(output, datatype); // datatype
		writeShort(output, bitsallocated); // bitpix
		if (nfti_hdr == null) {
			output.writeShort(0); // dim_un0
		} else {
			writeShort(output, nfti_hdr.slice_start); // slice_start
		}

		// double [] quaterns = new double [ 5 ];
		int qform_code = NiftiHeader.NIFTI_XFORM_UNKNOWN;
		int sform_code = NiftiHeader.NIFTI_XFORM_UNKNOWN;
		double qoffset_x = 0.0, qoffset_y = 0.0, qoffset_z = 0.0;
		qoffset_x = srow[0][3];
		qoffset_y = srow[1][3];
		qoffset_z = srow[2][3];
		// for (i=0; i<5; i++) quaterns[i] = 0.0;
		// double [][] srow = new double[3][4];
		// for (i=0; i<3; i++) srow[i][i] = 1.0;

		qform_code = qform;
		sform_code = sform;

		float[] pixdims = new float[8];
		pixdims[0] = (float) quaterns[0];
		pixdims[1] = (float) fi.pixelWidth;
		pixdims[2] = (float) fi.pixelHeight;
		pixdims[3] = Math.abs((float) fi.pixelDepth);
		pixdims[4] = (float) fi.frameInterval;

		if ((type != ANALYZE_7_5) && (nfti_hdr != null)) {
			for (i = 5; i < 8; i++)
				pixdims[i] = nfti_hdr.pixdim[i];
		}

		for (i = 0; i < 8; i++)
			writeFloat(output, pixdims[i]); // pixdim[4-7]

		writeFloat(output, (type == NIFTI_FILE) ? 352 : 0); // vox_offset
		double[] coeff = new double[2];
		coeff[0] = 0.0;
		coeff[1] = 1.0;

		if (c.getFunction() == Calibration.STRAIGHT_LINE) {
			coeff = c.getCoefficients();
		}

		// System.out.println(coeff[0]+" , "+coeff[1]);

		double c_offset;
		c_offset = 0.0;

		// c_offset = coeff[0];
		// if (signed16bit) {
		// if (coeff[1] != 0.0)
		// c_offset += 32768.0 * coeff[1];
		// else
		// c_offset += 32768.0;
		// }

		// if (signed16bit)
		// c_offset = 32768.0;
		//
		// else

		writeFloat(output, coeff[1]); // scl_slope
		writeFloat(output, c_offset); // scl_inter

		writeShort(output, (short) 0); // slice_end
		output.write((nfti_hdr != null) ? nfti_hdr.slice_code : 0); // slice_code

		String unit = c.getUnit().toLowerCase().trim();
		byte xyzt_unit = NiftiHeader.UNITS_UNKNOWN;
		if (unit.equals("meter") || unit.equals("metre") || unit.equals("m")) {
			xyzt_unit |= NiftiHeader.UNITS_METER;
		} else if (unit.equals("mm")) {
			xyzt_unit |= NiftiHeader.UNITS_MM;
		} else if (unit.equals("micron")) {
			xyzt_unit |= NiftiHeader.UNITS_MICRON;
		}
		output.write(xyzt_unit); // xyzt_units

		double min = imp.getProcessor().getMin();
		double max = imp.getProcessor().getMax() + 1;

		writeFloat(output, coeff[0] + (coeff[1] * max)); // cal_max
		writeFloat(output, coeff[0] + (coeff[1] * min)); // cal_min

		writeFloat(output, (nfti_hdr != null) ? nfti_hdr.slice_duration : 0.0); // slice_duration
		writeFloat(output, (nfti_hdr != null) ? nfti_hdr.toffset : 0.0); // toffset
		writeFloat(output, 0.0); // glmax
		writeFloat(output, 0.0); // glmin

		String desc = (nfti_hdr == null) ? new String() : nfti_hdr.descrip.trim();
		int length = desc.length();
		if (length > 80) {
			output.writeBytes(desc.substring(0, 80));
		} else {
			output.writeBytes(desc);
			for (i = length; i < 80; i++)
				output.write(0);
		}

		desc = (nfti_hdr == null) ? "" : nfti_hdr.aux_file.trim();
		length = desc.length();
		if (length > 24) {
			output.writeBytes(desc.substring(0, 24));
		} else {
			output.writeBytes(desc);
			for (i = length; i < 24; i++)
				output.write(0);
		}

		writeShort(output, (short) qform_code); // qform_code
		writeShort(output, (short) sform_code); // sform_code
		writeFloat(output, quaterns[2]); // quatern_b
		writeFloat(output, quaterns[3]); // quatern_c
		writeFloat(output, quaterns[4]); // quatern_d

		writeFloat(output, qoffset_x); // qoffset_x
		writeFloat(output, qoffset_y); // qoffset_y
		writeFloat(output, qoffset_z); // qoffset_z

		for (i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++)
				writeFloat(output, srow[i][j]); // srow_x, srow_y, srow_z
		}

		desc = (nfti_hdr == null) ? "" : nfti_hdr.intent_name.trim();
		length = desc.length();
		if (length > 16) {
			output.writeBytes(desc.substring(0, 16));
		} else {
			output.writeBytes(desc);
			for (i = length; i < 16; i++)
				output.write(0);
		}

		output.writeBytes((type == NIFTI_ANALYZE) ? "ni1\0" : "n+1\0");
		if (type == NIFTI_FILE)
			output.writeInt(0); // extension
	}

	private void writeRGBPlanar(ImagePlus imp, OutputStream out) throws IOException {
		ImageStack stack = null;
		int nImages = imp.getStackSize();
		if (nImages > 1)
			stack = imp.getStack();

		int w = imp.getWidth();
		int h = imp.getHeight();
		byte[] r, g, b;

		for (int i = 1; i <= nImages; i++) {
			ColorProcessor cp = (nImages == 1) ? (ColorProcessor) imp.getProcessor()
					: (ColorProcessor) stack.getProcessor(i);
			r = new byte[w * h];
			g = new byte[w * h];
			b = new byte[w * h];
			cp.getRGB(r, g, b);
			out.write(r, 0, w * h);
			out.write(g, 0, w * h);
			out.write(b, 0, w * h);
			IJ.showProgress((double) i / nImages);
		}
	}

	private void writeInt(DataOutputStream input, int value) throws IOException {
		if (littleEndian) {
			byte b1 = (byte) (value & 0xff);
			byte b2 = (byte) ((value >> 8) & 0xff);
			byte b3 = (byte) ((value >> 16) & 0xff);
			byte b4 = (byte) ((value >> 24) & 0xff);
			input.writeByte(b1);
			input.writeByte(b2);
			input.writeByte(b3);
			input.writeByte(b4);
		} else {
			input.writeInt(value);
		}
	}

	private void writeShort(DataOutputStream input, short value) throws IOException {

		if (littleEndian) {
			byte b1 = (byte) (value & 0xff);
			byte b2 = (byte) ((value >> 8) & 0xff);
			input.writeByte(b1);
			input.writeByte(b2);
		} else {
			input.writeShort(value);
		}
	}

	private void writeFloat(DataOutputStream input, float value) throws IOException {
		writeInt(input, Float.floatToIntBits(value));
	}

	private void writeFloat(DataOutputStream input, double value) throws IOException {
		writeFloat(input, (float) value);
	}

	private String[] createRepertories(String pathRoot, String newRep) {

		String[] ls = new String[2];
		ls[0] = pathRoot;
		ls[1] = newRep;

		if (newRep.contains(separator)) {

			ls[1] = newRep.substring(newRep.lastIndexOf(separator));

			newRep = newRep.substring(0, newRep.lastIndexOf(separator));

			File f = new File(pathRoot + separator + newRep);

			if (!f.exists())
				f.mkdirs();

			ls[0] = pathRoot + separator + newRep;
		}
		return ls;
	}

	private ImagePlus convertToSigned(ImagePlus imgpl) {

		for (int i = 0; i < imgpl.getStackSize(); i++) {
			imgpl.setSlice(i + 1);
			for (int row = 0; row < imgpl.getWidth(); row++)
				for (int col = 0; col < imgpl.getHeight(); col++)
					if (imgpl.getProcessor().getPixelValue(row, col) >= 0)
						imgpl.getProcessor().putPixelValue(row, col,
								imgpl.getProcessor().getPixelValue(row, col) - 32768);
					else
						imgpl.getProcessor().putPixelValue(row, col,
								imgpl.getProcessor().getPixelValue(row, col) + 32768);
		}
		return imgpl;
	}
}