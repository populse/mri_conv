package brukerParavision;

import java.io.BufferedReader;
import java.io.StringReader;

public class SearchParamBruker2 {

	String paramToFind, fichier;

	public SearchParamBruker2(String paramToFind, String fichier) {
		this.paramToFind = paramToFind;
		this.fichier = fichier;
	}

	public String result() {
		String ligne,ligneb = "";

		try {
			BufferedReader lecteurAvecBuffer = new BufferedReader(new StringReader(fichier));
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				if (ligne.indexOf(paramToFind) != -1) {
					
					if (!ligne.contains("(")) {
						ligne = ligne.substring(ligne.indexOf("=") + 1);
						ligne = ligne.replace("<", "");
						ligne = ligne.replace(">", "");
						return ligne;
					} 
					
					else if (ligne.contains("(") && !ligne.contains(")")) {
						ligne = ligne.substring(ligne.indexOf("(")).trim();

						while (!ligneb.contains("##$") && !ligneb.contains("$$")){
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
						String tmp = ligne.substring(ligne.indexOf("(")+1,ligne.indexOf(")")).trim();
						ligne="";
						if (ligneb.contains("##$") || ligneb.contains("$$")) 
							ligne = tmp;
						else {
							while (!ligneb.contains("##$") && !ligneb.contains("$$")){
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
		return "";
	}
}