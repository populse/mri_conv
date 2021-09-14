import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import MRIFileManager.DictionaryYaml2;
import MRIFileManager.UtilsSystem;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;
import bids.ListUnderRep;
import bids.SearchBids;

public class BidsToNifti extends PrefParam implements ParamMRI2 {

	public static void main(String[] args) {

		if (args.length < 4) {
			System.out.println("not enough arguments and options (type: java -classpath MRIManager.jar Help)");
			return;
		}

		hmSeq.clear();
		hmInfo.clear();
		hmOrderImage.clear();

		String[] listFiles = args[0].split(";"); // get list fo files

		String outRepertory = "", options = "", nrep = "";
		separator = File.separator;
		formatCurrent = "[Bids]          ";
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
				tm = options.substring(options.indexOf("[ExportOptions]") + 15);
				tm = tm.substring(0, 6);
				tm = tm.trim();
				for (int i = tm.length(); i < 5; i++) {
					tm += "0";
				}
				PrefParam.namingOptionsNiftiExport = tm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (new File(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml").exists()) {
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_System.yml", "Nifti").loadDictionarySystem();
			new DictionaryYaml2(UtilsSystem.pathOfJar() + "DictionaryMRI_User.yml", "Nifti").loadDictionaryUser();
		}

		for (int i = 0; i < listFiles.length; i++) {
			hmSeq.clear();
			String pathFile = listFiles[i];
			if (!pathFile.endsWith(separator)) {
				pathFile+=separator;
			}
			if (new File(pathFile+"dataset_description.json").exists()) {
				String[] hmValue = new String[3];
//				pathFile = pathFile.substring(0, pathFile.indexOf("dataset_description.json"));
				String[] listSub = new ListUnderRep().listesousrep(pathFile);
				for (String ll : listSub) {
					hmValue[1] = ll;
//					System.out.println(pathFile + ll + separator);
					String[] listStudy = new BidsToNifti().listStudy(pathFile + ll);
					if (listStudy.length >= 1)
						for (String mm : listStudy) {
							hmValue[2] = mm;
							List<String> listRep = new SearchBids(pathFile + ll + separator+mm).getList();
//							System.out.println(pathFile + ll + separator+mm);
							for (int j = 0; j < listRep.size(); j++) {
								hmValue[0] = listRep.get(j);
								hmSeq.put(String.valueOf(j), hmValue);
								new MriToNifti().convertToNifti(listRep.get(j), nrep, String.valueOf(j), "",outRepertory);
//								System.out.println(listRep.get(i));
							}
						}
					else {
						hmValue[2] = "";
//						System.out.println(pathFile + ll + separator);
						List<String> listRep = new SearchBids(pathFile + ll).getList();
						for (int j = 0; j < listRep.size(); j++) {
							hmValue[0] = listRep.get(j);
							hmSeq.put(String.valueOf(j), hmValue);
							new MriToNifti().convertToNifti(listRep.get(j), nrep, String.valueOf(j), "",outRepertory);
//							System.out.println(listRep.get(i));
						}
					}
//					String[] listStudy = new ListUnderRep().listesousrep(pathFile+ll+separator);
//					String[] hmValue = new String[3];
//					hmValue[1] = listStudy[0];
//					hmValue[2] = listStudy[1];
//
//					List<String> listRep = new SearchBids(pathFile).getList();
//					for (int j = 0; j < listRep.size(); j++) {
//						hmValue[0] = listRep.get(j);
//						hmSeq.put(String.valueOf(j), hmValue);
//						new MriToNifti().convertToNifti(listRep.get(j), nrep, String.valueOf(j), outRepertory);
//					}
				}
			} else
				System.out.println(pathFile+"dataset_description.json" + " doesn't exist");
		}
	}

	private String[] listStudy(String path) {
		String listProtocol = "anat,fmap,func,dwi,meg,beh,derivatives,.datalad";

		File repertory = new File(path);
		String[] listrep = repertory.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (new File(dir, name).isDirectory() && !listProtocol.contains(name));
			}
		});
		return listrep;
	}
}