import java.io.File;
import java.util.List;

import MRIFileManager.DictionaryYaml2;
import MRIFileManager.UtilsSystem;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

import brukerParavision.Search2dseq;

public class BrukerToNifti extends PrefParam implements ParamMRI2 {

	public static void main(String[] args) {

		if (args.length < 4) {
			System.out.println("not enough arguments and options (type: java -cp MRIManager.jar --help)");
			return;
		}

//		for (int i=0; i<args.length; i++)
//			System.out.println("args["+i+"] = "+args[i]);

		hmSeq.clear();
		hmInfo.clear();
		hmOrderImage.clear();

		String[] listFiles = args[0].split(";"); // get list fo files

		String outRepertory = "", options = "", nrep = "";
		separator = File.separator;
		formatCurrent = "[Bruker]        ";
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
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml", "Bruker").loadDictionarySystem();
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_User.yml", "Bruker").loadDictionaryUser();
		}

		for (int i = 0; i < listFiles.length; i++) {
			String tmpFile = listFiles[i];
			if (new File(tmpFile).exists()) {

				String tmp2dseq, serialNumber;
				if (tmpFile.substring(tmpFile.lastIndexOf(separator) + 1).contentEquals("2dseq")) {
					try {
						serialNumber=tmpFile.substring(0,tmpFile.indexOf("pdata")-1);
						serialNumber = serialNumber.substring(serialNumber.lastIndexOf(separator)+1);
						serialNumber+="-"+tmpFile.substring(tmpFile.indexOf("pdata")+6,tmpFile.lastIndexOf("2dseq")-1);
						new MriToNifti().convertToNifti(tmpFile, nrep, String.valueOf(i), serialNumber,outRepertory);
					} catch (Exception e) {
						System.out.println(tmpFile + " ..... no exported (probably corrupted files)");
					}
				} else {
					if (!tmpFile.endsWith(separator)) {
						tmpFile += separator;
					}
					tmpFile += "subject";
					if (new File(tmpFile).exists()) {
						List<String> list2dseq = new BrukerToNifti().search2dseq(tmpFile);
						String prefixSeq = ("000000").substring(0, String.valueOf(list2dseq.size()).length());

						for (int j = 0; j < list2dseq.size(); j++) {
							tmp2dseq = list2dseq.get(j);
							serialNumber=tmp2dseq.substring(0,tmp2dseq.indexOf("pdata")-1);
							serialNumber = serialNumber.substring(serialNumber.lastIndexOf(separator)+1);
							
							if (tmp2dseq.contains("*"))
								serialNumber+="-"+tmp2dseq.substring(tmp2dseq.indexOf("pdata")+6,tmp2dseq.lastIndexOf("2dseq")-1);
							new MriToNifti().convertToNifti(tmp2dseq.replace("*", ""), nrep, (prefixSeq + j).substring(String.valueOf(j).length()),serialNumber,
									outRepertory);
						}
					} else {
						System.out.println(tmpFile + " doesn't exist");
					}
				}
			} else
				System.out.println(tmpFile + " doesn't exist");
		}
	}

	private List<String> search2dseq(String subjectPath) {
//		System.out.println("\nExporting .... " + subjectPath);
		String tmpFile = subjectPath.substring(0, subjectPath.lastIndexOf(separator));
		return new Search2dseq(tmpFile).getList2dseq();
	}
}
