package dcm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import MRIFileManager.Dateformatmodif;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListDicomData extends PrefParam implements ParamMRI2 {

	private String[] paramListData = headerListData;
	private String headerDicom,formatDicom;
	private boolean JpegLossLess;

	public ListDicomData(String chemDicom) {
		
		String chemDicom_hmData;
		headerDicom = chemDicom + separator + "DIRFILE";

		formatDicom = "DIRFILE";

		if (!new File(headerDicom).exists()) {
			headerDicom = chemDicom + separator + "DICOMDIR";
//			headerDicom = "D:\\DICOMDIR";
			formatDicom = "DICOMDIR";
			if (!new File(headerDicom).exists()) {
				headerDicom = null;
				formatDicom = "DCM";
			}
		}
		
		chemDicom_hmData=headerDicom;
		
		hmData.put(chemDicom, chemDicom_hmData);

		if (!formatDicom.contentEquals("DCM"))
			headerDicom = new HeaderDicom().getHeaderDicom(headerDicom);

		String pathRel = "";
		
		switch (formatDicom) {

		case "DIRFILE":
			pathRel = searchParam(headerDicom, "00E1,1019  ---: ");
			pathRel = pathRel.replace("\\", separator);
			if (pathRel.isEmpty()) {
				pathRel = searchParam(headerDicom, "0004,1500  ---: ");
				pathRel = pathRel.replace("\\", separator);
			}
			headerDicom = chemDicom + separator + pathRel;

			for (int i = 0; i < pathRel.split('\\' + separator).length; i++) {
				if (!new File(headerDicom).exists()) {
					pathRel = pathRel.substring(pathRel.indexOf(separator) + 1);
					headerDicom = chemDicom + separator + pathRel;
				} else
					break;
			}
			break;

		case "DICOMDIR":
			pathRel = searchParam(headerDicom, "0004,1500");
			pathRel = pathRel.replace("\\", separator);
			headerDicom = chemDicom + separator + pathRel;
			break;

		case "DCM":
			File dcmFile = new File(chemDicom);
			for (File gh : dcmFile.listFiles()) {
				try {
					RandomAccessFile raf = new RandomAccessFile(gh, "r");
					raf.seek(128);
					if (raf.readLine().substring(0, 4).contains("DICM")) {
						raf.close();
						headerDicom = gh.getAbsolutePath();
						raf.close();
						break;
					}
					raf.close();
				} catch (Throwable ed) {
				}
			}
			break;

		default:
			break;
		}

		HeaderDicom hdrdcm = new HeaderDicom();
		
		headerDicom = hdrdcm.getHeaderDicom(headerDicom);
		JpegLossLess = hdrdcm.isJpegLossLess();
		
	}

	public Object[] listParamDataDicom() throws IOException {
		
		Object[] resul = new Object[paramListData.length];

		for (int i = 2; i < resul.length-1; i++)
			resul[i] = searchParam(headerDicom, dictionaryMRISystem.get(paramListData[i]).get("keyName"));

		resul[4] = new Dateformatmodif(resul[4].toString(), dictionaryMRISystem.get(paramListData[4]).get("format"),
				dictionaryJsonSystem.get(paramListData[4]).get("format")).getNewFormatDate();

		resul[8]=formatDicom;
		if (JpegLossLess)
			resul[8]+=", JpegLossLess";

		return resul;
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