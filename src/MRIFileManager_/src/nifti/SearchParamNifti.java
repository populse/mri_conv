package nifti;

import java.util.Scanner;

import MRIFileManager.GetStackTrace;

public class SearchParamNifti {

	String paramToFind, fichier;

	public SearchParamNifti(String paramToFind, String fichier) {
		
		this.paramToFind = paramToFind;
		this.fichier = fichier;
	}

	public String result() {

		String res = "";
		Scanner sc;
		try {
			sc = new Scanner(fichier);
			String line;
			while (sc.hasNext()) {
				line = sc.nextLine();
				if (line.contains(paramToFind)) {
					res = line.substring(line.indexOf(":") + 1);
					res = res.trim();
				}
			}
		} catch (Exception e) {
			new GetStackTrace(e);
		}
		return res;
	}
}