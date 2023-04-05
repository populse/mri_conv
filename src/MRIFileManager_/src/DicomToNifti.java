import java.io.File;
import java.util.Arrays;

import MRIFileManager.DictionaryYaml2;
import MRIFileManager.UtilsSystem;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import dcm.ListDcmSequence;
import dcm.ListDicomDirSequence2;
import dcm.ListDirfileSequence;

public class DicomToNifti extends PrefParam implements ParamMRI2 {

	public static void main(String[] args) {

		String[] words = {"--help", "-help", "--h", "-h"};
		if (containsWords(args[0], words)) {
			showHelp();
			return;
		}

		if (args.length < 4) {
			System.out.println("\n 	not enough arguments and options !");
			showHelp();
			return;
		}

		hmSeq.clear();
		hmInfo.clear();
		hmOrderImage.clear();

		String[] listFiles = args[0].split(";");

		String outRepertory = "", nrep = "";
		String options = "";
		separator = File.separator;
		formatCurrent = "[Dicom]        ";
		namingOptionsNiftiExport = "00000";

		try {
			outRepertory = args[1]; // get directory export
		} catch (Exception e) {
			System.out.println("export repetory missing or not available");
			return;
		}

		try {
			nrep = args[2]; // get naming option
		} catch (Exception e) {
			System.out.println("options not found or not correct");
			return;
		}

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
			if (nameFile.toLowerCase().contentEquals("dicomdir")) {
				try {
					new ListDicomDirSequence2(pathFile, true);
				} catch (Exception e) {
					System.out.println(pathFile + " : " + e);
				}
			}
			else if (nameFile.toLowerCase().contentEquals("dirfile")) {
				try {
					new ListDirfileSequence(pathFile, true);
				} catch (Exception e) {
					System.out.println(pathFile + " : " + e);
				}
			}
			else if (!nameFile.toLowerCase().contentEquals("dicomdir") && !nameFile.toLowerCase().contentEquals("dirfile")) {
				new ListDcmSequence(pathFile, true);
			}

			String prefixSeq = ("000000").substring(0, String.valueOf(hmSeq.size()).length());
			int j = 0;

			new MriToNifti().convertToNifti(nrep, outRepertory, "Dicom");

		}
	}

	public static boolean containsWords(String input, String[] words) {
	    return Arrays.stream(words).anyMatch(input::contains);
	}

	public static void showHelp() {
		System.out.println("\nDicomToNifit : \n");
		System.out.println("	usage : java -cp /path/of/MRIManager.jar data dest namingNifti option\"\n");
		System.out.println("	data : \n");
	}
}
