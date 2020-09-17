import java.io.File;

import MRIFileManager.DictionaryYaml2;
import MRIFileManager.UtilsSystem;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import dcm.SearchDicom;

public class DicomToNifti extends PrefParam implements ParamMRI2 {

	public static void main(String[] args) {

		if (args.length < 4) {
			System.out.println("not enough arguments and options (type: java -classpath MRIManager.jar Help)");
			return;
		}

		String[] listFiles = args[0].split(";");

//		String outRepertory = "", nrep = "";
		String options = "";
		separator = File.separator;
		formatCurrent = "[Dicom]        ";
		namingOptionsNiftiExport = "00000";

//		try {
//			outRepertory = args[1]; // get directory export
//		} catch (Exception e) {
//			System.out.println("export repetory missing or not available");
//			return;
//		}
//
//		try {
//			nrep = args[2]; // get naming option
//		} catch (Exception e) {
//			System.out.println("options not found or not correct");
//			return;
//		}

		try {
			options = args[3];
			if (options.contains("[ExportOptions]")) {
				String tm;
				tm = options.substring(options.indexOf("[ExportOptions]") + 15).trim();
				tm = tm.substring(0, 5);
//				tm = tm.trim();
//				for (int i = tm.length(); i < 5; i++) {
//					tm += "0";
//				}
				PrefParam.namingOptionsNiftiExport = tm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (new File(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml").exists()) {
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml", "Dicom").loadDictionarySystem();
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_User.yml", "Dicom").loadDictionaryUser();
		}

		for (int i = 0; i < listFiles.length; i++) {
			String pathFile = listFiles[i];
			String nameFile = pathFile.substring(pathFile.lastIndexOf(separator) + 1).toLowerCase();
			if (!nameFile.contentEquals("dicomdir") && !nameFile.contentEquals("firfile")) {
				StringBuffer[] listDcm = new SearchDicom(pathFile).listDicom();
				System.out.println("search list of Dicom files ........");
				for (StringBuffer ll : listDcm)
					System.out.println(ll);
			}
		}
	}
}
