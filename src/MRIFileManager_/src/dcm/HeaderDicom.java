package dcm;

import ij.plugin.DICOM;
import loci.common.DebugTools;

import java.io.File;
import java.util.Scanner;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomInputStream;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;


public class HeaderDicom {

	private boolean isJpeglossless;

	public HeaderDicom() {

	}

	public String getHeaderDicom(String pathDicom) {
		String txt = null, UID = null;
		isJpeglossless = false;
		try {
//			InputStream reader = new FileInputStream(pathDicom);
			DICOM dcm = new DICOM();
			txt = dcm.getInfo(pathDicom);
			dcm.close();
		} catch (Exception e) {
			txt = "";
			new GetStackTrace(e, this.getClass().toString());
		}

		UID = searchParam(txt, "Transfer Syntax UID");
		if (UID.contentEquals("1.2.840.10008.1.2.4.70")) {
			isJpeglossless = true;
			txt = getHeaderDicomJpegLossLess(pathDicom);
		}
//		System.out.println(this + txt);
		return txt;
	}

//	public String getHeaderDicom2(String pathDicom) {
//		DebugTools.enableLogging("OFF");
//
//		String txt = null;
//
//		AttributeList list = new AttributeList();
//		DicomInputStream dis;
//		try {
//			dis = new DicomInputStream(new File(pathDicom));
//			list.read(dis);
//		} catch (Exception e1) {
//			txt = "";
//		}
//		txt = redoTxt(list.toString());
//		return txt;
//	}

	public boolean isJpegLossLess() {
		return isJpeglossless;
	}

	private String getHeaderDicomJpegLossLess(String pathDicom) {

		DebugTools.enableLogging("OFF");

		String txt = null;

		AttributeList list = new AttributeList();
		DicomInputStream dis;
		try {
			dis = new DicomInputStream(new File(pathDicom));
			list.read(dis);
		} catch (Exception e1) {
			txt = "";
		}
		txt = redoTxt(list.toString());
		return txt;
	}

	private String redoTxt(String txt) {
		Scanner scan = new Scanner(txt);
		String line = "", txtOut = "", field = "", value = "";
		while (scan.hasNextLine()) {
			line = scan.nextLine();
//			if (!line.substring(0,1).contentEquals("%")) {
			if (line.substring(0, 1).contentEquals("(")) {
//				line = line.replace("0x", "");
				line = line.replaceAll("[()]", "");
//				int startindex = line.indexOf("VR")-1;
//				int endindex = line.indexOf("> <")+3;
//				if (endindex==2)
//					endindex = line.indexOf("> [")+3;
//				if (endindex==2)
//					endindex = line.length();
//				toBeDeleted = line.substring(startindex, endindex);
//				line = line.replace(toBeDeleted, ": ");
//				line = line.substring(0, line.length()-1);
				field = line.substring(0, line.indexOf("VR") - 1);
				field = field.replace("0x", "");
				int endindex;
				if ((endindex = line.indexOf("> <")) != -1)
					value = line.substring(endindex + 3, line.length() - 1);
				else if ((endindex = line.indexOf("> [")) != -1) {
					value = line.substring(endindex + 3, line.length() - 1);
					if (value.contains("0x")) {
						value = value.replace("0x", "");
						try {
							value = String.valueOf(Integer.parseInt(value, 16));
						} catch (Exception e) {
						}
					}
				}

				line = field + ": " + value;
				line = returnDictio(line);
				txtOut += line + "\r\n";
			}
		}
		scan.close();
		txtOut = txtOut.replace("PatientName", "Patient's Name");
		txtOut = txtOut.replace("PatientSex", "Patient's Sex");
		txtOut = txtOut.replace("PatientWeight", "Patient's Weight");
		txtOut = txtOut.replace("PatientBirthDate", "Patient's Birth Date");

		return txtOut;
	}

	private String returnDictio(String key) {
		String newKey = key, tmp;
		for (String dg : ParamMRI2.dictionaryMRISystem.keySet()) {
			tmp = ParamMRI2.dictionaryMRISystem.get(dg).get("keyName");

			if (newKey.contains(tmp.replaceAll("[()]", "").replaceAll("\\s+", ""))) {
				newKey = newKey.replace(tmp.replaceAll("[()]", "").replaceAll("\\s+", ""), tmp).trim();
				break;
			}
		}
		return newKey;
	}

	private String searchParam(String txt, String paramToFind) {
		String resul = "";
		try {
			resul = txt.substring(txt.indexOf(paramToFind));
			resul = resul.substring(resul.indexOf(":") + 1, resul.indexOf("\n"));
		} catch (Exception e) {
		}
		return resul.trim();
	}
}