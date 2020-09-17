package brukerParavision;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import abstractClass.PrefParam;

public class Search2dseq extends PrefParam {

	private List<String> result = new ArrayList<>();
	private List<String> listSeq = new ArrayList<>();
	private List<Integer> noSeq = new ArrayList<>();
	private String rep;
	private String prePdata = "";
	private int compt = 0;

	public Search2dseq(String rep) {
		this.rep = rep;
		search(new File(rep));
		triSeq();
	};

	public void search(File file) {

		String fileFound;

		if (file.isDirectory()) {
			// do you have permission to read this directory?
			if (file.canRead()) {

				for (File temp : file.listFiles()) {

					if (temp.isDirectory() && !temp.toString().contains("dicom")) {
						search(temp);
					} else {
						if (temp.getName().toLowerCase().equals("2dseq") && temp.length() != 0
								&& new File(temp.getParentFile() + separator + "visu_pars").exists()) {
							fileFound = temp.getAbsoluteFile().toString();
							noSeq.add(Integer.parseInt(
									fileFound.substring(rep.length() + 1, fileFound.indexOf("pdata") - 1)) * 100
									+ Integer.parseInt(fileFound.substring(fileFound.indexOf("pdata") + 6,
											fileFound.lastIndexOf(separator))));

							if (noSeq.size() > 1) {
								if (fileFound.substring(rep.length() + 1, fileFound.indexOf("pdata") - 1)
										.contentEquals(prePdata)) {

									result.add(String.valueOf(noSeq.get(noSeq.size() - 1)) + " " + fileFound + "*");
									if (compt < 1)
										result.set(result.size() - 2, result.get(result.size() - 2).toString() + "**");
									compt++;
								} else {
									result.add(String.valueOf(noSeq.get(noSeq.size() - 1)) + " " + fileFound);
									prePdata = fileFound.substring(rep.length() + 1, fileFound.indexOf("pdata") - 1);
									compt = 0;
								}
							} else {
								result.add(String.valueOf(noSeq.get(noSeq.size() - 1)) + " " + fileFound);
								prePdata = fileFound.substring(rep.length() + 1, fileFound.indexOf("pdata") - 1);
							}
						}
					}
				}
			} else {
				System.out.println(file.getAbsoluteFile() + "Permission Denied");
			}
		}
	}

	private void triSeq() {

		Collections.sort(noSeq);

		for (int i = 0; i < noSeq.size(); i++) {
			for (int j = 0; j < result.size(); j++) {
				if (Integer.parseInt(result.get(j).substring(0, result.get(j).indexOf(" "))) == noSeq.get(i)) {
					listSeq.add(result.get(j).substring(result.get(j).indexOf(" ") + 1, result.get(j).length()));
					result.remove(j);
				}
			}
		}
	}

	public List<String> getList2dseq() {
		return listSeq;
	}
}
