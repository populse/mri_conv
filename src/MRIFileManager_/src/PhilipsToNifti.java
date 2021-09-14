import java.io.File;
import java.util.List;

import MRIFileManager.DictionaryYaml2;
import MRIFileManager.UtilsSystem;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import philips.SearchPhilips;

public class PhilipsToNifti extends PrefParam implements ParamMRI2 {

	public static void main(String[] args) {

		if (args.length < 4) {
			System.out.println("not enough arguments and options (type: java -classpath MRIManager.jar Help)");
			return;
		}

		String[] listFiles = args[0].split(";"); // get list fo files

		String outRepertory = "", options = "", nrep = "";
		separator = File.separator;
		formatCurrent = "[Philips]       ";
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
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml", "Philips").loadDictionarySystem();
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_User.yml", "Philips").loadDictionaryUser();
		}

		for (int i = 0; i < listFiles.length; i++) {
			String prefixSeq = ("000000").substring(0, String.valueOf(listFiles.length).length());

			String pathFile = listFiles[i];
			String ext = pathFile.substring(pathFile.lastIndexOf(separator) + 1).toLowerCase();
			if (new File(pathFile).exists()) {
				if (ext.endsWith(".rec") || ext.endsWith(".xml"))
					try {
						new MriToNifti().convertToNifti(pathFile, nrep, (prefixSeq + i).substring(String.valueOf(i).length()), "",outRepertory);
					} catch (Exception e) {
						System.out.println(pathFile + " ..... no exported (probably corrupted files)");
					}
				else {
					List<String> listparfile = new SearchPhilips(pathFile).getList();
					prefixSeq = ("000000").substring(0, String.valueOf(listparfile.size()).length());
					for (int j = 0; j < listparfile.size(); j++) {
						new MriToNifti().convertToNifti(pathFile + listparfile.get(j), nrep, (prefixSeq + j).substring(String.valueOf(j).length()),"",
								outRepertory);
					}
				}

			} else
				System.out.println(pathFile + " doesn't exist");

		}
	}
}
