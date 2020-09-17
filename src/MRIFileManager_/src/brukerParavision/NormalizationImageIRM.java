package brukerParavision;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import abstractClass.PrefParam;

public class NormalizationImageIRM extends PrefParam {

	private String file;
	private String SlopeToFind = "##$VisuCoreDataSlope=(";
	private String OffsetToFind = "##$VisuCoreDataOffs=(";
//	private String MinToFind="##$VisuCoreDataMin=("; 
//	private String MaxToFind="##$VisuCoreDataMax=("; 

	public NormalizationImageIRM(String file) {
		file = file.substring(0, file.lastIndexOf(separator)) + separator + "visu_pars";
		this.file = file;
	}

	public double[] factorNormalize() throws NumberFormatException, IOException {
		return tabScaling(SlopeToFind);
	}

	public double[] offsetNormalize() throws NumberFormatException, IOException {
		return tabScaling(OffsetToFind);
	}

	private double[] tabScaling(String paramToFind) throws NumberFormatException, IOException {
		double[] f = null;
		int inc = 0;
		boolean find = false;
		BufferedReader lecteurAvecBuffer = new BufferedReader(new FileReader(file));
		String ligne;
		double Max = 0, Min = 0;

		while ((ligne = lecteurAvecBuffer.readLine()) != null && !find) {
			if (ligne.indexOf(paramToFind) != -1) {
				inc = Integer.parseInt(ligne.substring(ligne.indexOf("(") + 2, ligne.indexOf(" )")));
				f = new double[inc];
				int i = 0;
				while (i < inc) {
					ligne = lecteurAvecBuffer.readLine();
					for (int j = 0; j < ligne.split(" +").length; j++) {
						f[j + i] = Float.parseFloat(ligne.split(" +")[j]);
						if (i + j == 0) {
							Max = f[0];
							Min = f[0];
						}
						Max = Math.max(Max, f[j + i]);
						Min = Math.min(Min, f[j + i]);
					}
					i += ligne.split(" +").length;
				}
				find = true;
			}
		}
		lecteurAvecBuffer.close();
		if (Max == Min) {
			double[] f1 = new double[1];
			f1[0] = Max;
			return f1;
		} else
			return f;
	}
}