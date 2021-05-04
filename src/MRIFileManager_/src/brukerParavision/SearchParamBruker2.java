package brukerParavision;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchParamBruker2 {

	String paramToFind, fichier;

	public SearchParamBruker2(String paramToFind, String fichier) {
		this.paramToFind = paramToFind;
		this.fichier = fichier;
	}

	public String result() {
		String ligne, ligneb = "";

		try {
			BufferedReader lecteurAvecBuffer = new BufferedReader(new StringReader(fichier));
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				if (ligne.indexOf(paramToFind) != -1) {
					if (!ligne.contains("(")) {
						ligne = ligne.substring(ligne.indexOf("=") + 1);
						ligne = ligne.replace("<", "");
						ligne = ligne.replace(">", "");
						return ligne;
					} else if (ligne.contains("(") && !ligne.contains(")")) {
						ligne = ligne.substring(ligne.indexOf("(")).trim();

						while (!ligneb.contains("##$") && !ligneb.contains("$$")) {
							ligneb = lecteurAvecBuffer.readLine();
							if (!ligneb.contains("##$") && !ligneb.contains("$$"))
								ligne += ligneb;
						}
						ligne = ligne.replace("(", "");
						ligne = ligne.replace(")", "");
						return ligne;
					}

					else if (ligne.contains("(") && ligne.contains(")")) {
						ligneb = lecteurAvecBuffer.readLine();
						String tmp = ligne.substring(ligne.indexOf("(") + 1, ligne.indexOf(")")).trim();
						ligne = "";
						if (ligneb.contains("##$") || ligneb.contains("$$"))
							ligne = tmp;
						else {
							while (!ligneb.contains("##$") && !ligneb.contains("$$")) {
								if (!ligneb.contains("##$") && !ligneb.contains("$$"))
									ligne += ligneb;
								ligneb = lecteurAvecBuffer.readLine();
							}
							ligne = ligne.replace("<", "");
							ligne = ligne.replace(">", "");

						}
						return ligne;
					}
				}
			}
			lecteurAvecBuffer.close();
		} catch (Exception exc) {
			return "";
		}

		if (paramToFind.contentEquals("$SUBJECT_date=")) { // for Paravision360
			String date = fichier.substring(fichier.indexOf("$SUBJECT_study_date=(") + 21);
			date = date.substring(0, date.indexOf(","));
			try {
				long timestamp = Long.parseLong(date);
				Date datef = new Date(timestamp * 1000);
				SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = jdf.format(datef);
			} catch (Exception e) {
				return date;
			}
			return date;
		}
		return "";
	}
}