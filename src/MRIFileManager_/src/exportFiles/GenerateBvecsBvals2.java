package exportFiles;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class GenerateBvecsBvals2 extends PrefParam implements ParamMRI2 {

	private boolean successfull = false;
	private String[] dd;
	private String[] bveff;
	private String[] dgo;
	private int DwAoImages;
	private HashMap<String, String> hmInfo_tmp;
	private String linebasket;

	public GenerateBvecsBvals2(String directory, String name, Object linebasket, String constr, String bvec_bval_txt) {

		this.linebasket = (String) linebasket;

		hmInfo_tmp = listBasket_hmInfo.get(linebasket);

		dd = hmInfo_tmp.get("Direction Diffusion").toString().split(" +");
		bveff = hmInfo_tmp.get("B-values effective").toString().split(" +");
		DwAoImages = Integer.parseInt(hmInfo_tmp.get("Diffusion Ao Images number").toString());
//		dgo = hmInfo_tmp.get("Diffusion Gradient Orientation").toString().split(" +");
		String[] ddtmp = dd;

//		String[] ddtmp = deleteDuplicate(hmInfo_tmp.get("Direction Diffusion").toString()).split(" +");
//		System.out.println(this + "constructor = " + constr);
//		System.out.println(this + " : dir. diff (" + dd.length + ") = " + hmInfo_tmp.get("Direction Diffusion"));
//		System.out.println(this + " : B-vals eff. (" + bveff.length + ") = " + hmInfo_tmp.get("B-values effective"));
//		System.out.println(this + " : DwAoImages = " + DwAoImages);
//		System.out.println(this + " : dir. grad. orient. (" + dgo.length + ") = " + hmInfo_tmp.get("Diffusion Gradient Orientation"));
//		System.out.println(this + "delete duplicate dir. diff ("+ddtmp.length+") = " + Arrays.toString(ddtmp));

		if (constr.contentEquals("Bruker") && ddtmp.length > 1) {
			dgo = hmInfo_tmp.get("Diffusion Gradient Orientation").toString().split(" +");
			if (hmInfo_tmp.get("Slice Orientation").toString().contentEquals("coronal")
					|| hmInfo_tmp.get("Slice Orientation").toString().contentEquals("sagittal"))
				if (dgo.length > 1)
					getTxt_Bruker("-y, -x, z", bvec_bval_txt, directory, name, false);
				else
					getTxt("-y, -x, z", bvec_bval_txt, directory, name, false);
			else
				if (dgo.length > 1)
					getTxt_Bruker("x, y, z", bvec_bval_txt, directory, name, false);
				else
					getTxt("x, y, z", bvec_bval_txt, directory, name, false);

		} else if (constr.contentEquals("Philips") && ddtmp.length > 3) {
			getTxt("-z, -x, y", bvec_bval_txt, directory, name, false);
		} else if (constr.contentEquals("Dicom") && ddtmp.length > 3) {
//			getTxt("-z, -x, y", bvec_bval_txt, directory, name);
			getTxt("x, y, z", bvec_bval_txt, directory, name, true);
		}
	}

	private void getTxt(String order, String type, String directory, String name, boolean forDicom) {

		String txtBvecs = "", txtBvals = "", txtToReturn = "";
		String[] indice = { "x", "y", "z" };
		String[] list = order.split(",");
		String[] sign = { "", "", "" };
		int[] rang = { 0, 1, 2 };
		String tmp = "";

		for (int i = 0; i < list.length; i++) {
			tmp = list[i].trim();
			if (tmp.contains("-")) {
				sign[i] = "-";
				tmp = tmp.replace("-", "");
			}
			rang[i] = Arrays.asList(indice).indexOf(tmp);
		}

		String[][] bvecsV = new String[3][DwAoImages + dd.length / 3];
		String[] bvalsV = new String[DwAoImages + dd.length / 3];

		for (int i = 0; i < DwAoImages; i++)
			for (int j = 0; j < 3; j++) {
				bvecsV[j][i] = "0.0";
				bvalsV[i] = bveff[i];
			}

		for (int i = 0; i < dd.length / 3; i++)
			for (int j = 0; j < 3; j++) {
				bvecsV[j][DwAoImages + i] = sign[j] + dd[(i * 3) + rang[j]];
				bvalsV[DwAoImages + i] = bveff[DwAoImages + i];
			}
			

		if (forDicom)
			bvecsV = bvecs_dicom(bvecsV);

		if (type.contentEquals("1") || type.contentEquals("3"))
			for (int i = 0; i < bvecsV[0].length; i++)
				txtToReturn += String.join(" ", bvecsV[0][i], bvecsV[1][i], bvecsV[2][i], bvalsV[i]) + "\n";
		if (type.contentEquals("2") || type.contentEquals("3")) {
			txtBvecs = String.join("\n", String.join(" ", bvecsV[0]), String.join(" ", bvecsV[1]),
					String.join(" ", bvecsV[2]));
			txtBvals = String.join(" ", bvalsV);
		}

		txtBvecs = txtBvecs.replaceAll("--", "");
		txtBvals = txtBvals.replaceAll("--", "");
		txtToReturn = txtToReturn.replaceAll("--", "");

		hmInfo_tmp.put("bvecs", txtBvecs);
		hmInfo_tmp.put("bvals", txtBvals);
		listBasket_hmInfo.put(linebasket, hmInfo_tmp);

		if (!txtToReturn.isEmpty())
			try {
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + "-bvecs-bvals-MRtrix.txt");
				writer.write(txtToReturn);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e, this.getClass().toString());
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ "-bvecs-bvals-MRtrix.txt" + "'");
			}
		if (!txtBvecs.isEmpty())
			try {
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + ".bvec");
				writer.write(txtBvecs);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e, this.getClass().toString());
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ ".bvec" + "'");
			}

		if (!txtBvals.isEmpty())
			try {
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + ".bval");
				writer.write(txtBvals);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e, this.getClass().toString());
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ ".bval" + "'");
			}
	}
	
	
	private void getTxt_Bruker(String order, String type, String directory, String name, boolean forDicom) {

		String txtBvecs = "", txtBvals = "", txtToReturn = "";
		String[] indice = { "x", "y", "z" };
		String[] list = order.split(",");
		String[] sign = { "", "", "" };
		int[] rang = { 0, 1, 2 };
		String tmp = "";

		for (int i = 0; i < list.length; i++) {
			tmp = list[i].trim();
			if (tmp.contains("-")) {
				sign[i] = "-";
				tmp = tmp.replace("-", "");
			}
			rang[i] = Arrays.asList(indice).indexOf(tmp);
		}

		String[][] bvecsV = new String[3][dgo.length / 3];
		String[] bvalsV = new String[dgo.length / 3];

//		for (int i = 0; i < DwAoImages; i++)
//			for (int j = 0; j < 3; j++) {
//				bvecsV[j][i] = "0.0";
//				bvalsV[i] = bveff[i];
//			}

		for (int i = 0; i < dgo.length / 3; i++) {
			boolean zero = false;
			for (int j = 0; j < 3; j++) {
				if (Float.valueOf(dgo[(i * 3) + rang[j]]) == 0.0) {
					bvecsV[j][i] = "0.0";
					zero = true;
				}
				else {	
					bvecsV[j][i] = sign[j] + dgo[(i * 3) + rang[j]];
					zero = false;
				}
			}
			if (zero)
				bvalsV[i] = "0.0";
			else
				bvalsV[i] = bveff[i];
		}


		if (type.contentEquals("1") || type.contentEquals("3"))
			for (int i = 0; i < bvecsV[0].length; i++)
				txtToReturn += String.join(" ", bvecsV[0][i], bvecsV[1][i], bvecsV[2][i], bvalsV[i]) + "\n";
		if (type.contentEquals("2") || type.contentEquals("3")) {
			txtBvecs = String.join("\n", String.join(" ", bvecsV[0]), String.join(" ", bvecsV[1]),
					String.join(" ", bvecsV[2]));
			txtBvals = String.join(" ", bvalsV);
		}

		txtBvecs = txtBvecs.replaceAll("--", "");
		txtBvals = txtBvals.replaceAll("--", "");
		txtToReturn = txtToReturn.replaceAll("--", "");

		hmInfo_tmp.put("bvecs", txtBvecs);
		hmInfo_tmp.put("bvals", txtBvals);
		listBasket_hmInfo.put(linebasket, hmInfo_tmp);

		if (!txtToReturn.isEmpty())
			try {
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + "-bvecs-bvals-MRtrix.txt");
				writer.write(txtToReturn);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e, this.getClass().toString());
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ "-bvecs-bvals-MRtrix.txt" + "'");
			}
		if (!txtBvecs.isEmpty())
			try {
//				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + "-bvecs-MRtrix.txt");
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + ".bvec");
				writer.write(txtBvecs);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e, this.getClass().toString());
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ ".bvec" + "'");
			}

		if (!txtBvals.isEmpty())
			try {
//				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + "-bvals-MRtrix.txt");
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + ".bval");
				writer.write(txtBvals);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e, this.getClass().toString());
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ ".bval" + "'");
			}
	}

	private String[][] bvecs_dicom(String[][] bvecs) {

		String[][] result = new String[bvecs.length][bvecs[0].length];

		String[] listOrientation = hmInfo_tmp.get("Image Orientation Patient").split("\\\\");
		double[] v1 = Arrays.stream(Arrays.copyOfRange(listOrientation, 0, 3)).mapToDouble(Double::parseDouble)
				.toArray();
		double[] v2 = Arrays.stream(Arrays.copyOfRange(listOrientation, 3, 6)).mapToDouble(Double::parseDouble)
				.toArray();
		double[] v3 = new double[3];

		v3[0] = v1[1] * v2[2] - v1[2] * v2[1];
		v3[1] = v1[2] * v2[0] - v1[0] * v2[2];
		v3[2] = v1[0] * v2[1] - v1[1] * v2[0];
		
//		System.out.println(this+" : v1 = "+v1[0]+" , "+v1[1]+" , "+v1[2]);
//		System.out.println(this+" : v2 = "+v2[0]+" , "+v2[1]+" , "+v2[2]);
//		System.out.println(this+" : v3 = "+v3[0]+" , "+v3[1]+" , "+v3[2]);

		for (int i = 0; i < bvecs[0].length; i++) {
			result[0][i] = String.valueOf(v1[0] * Double.parseDouble(bvecs[0][i])
					+ v1[1] * Double.parseDouble(bvecs[1][i]) + v1[2] * Double.parseDouble(bvecs[2][i]));
			result[1][i] = String.valueOf(v2[0] * Double.parseDouble(bvecs[0][i])
					+ v2[1] * Double.parseDouble(bvecs[1][i]) + v2[2] * Double.parseDouble(bvecs[2][i]));
			result[2][i] = String.valueOf(v3[0] * Double.parseDouble(bvecs[0][i])
					+ v3[1] * Double.parseDouble(bvecs[1][i]) + v3[2] * Double.parseDouble(bvecs[2][i]));

		}

		return result;
	}

	private String deleteDuplicate(String elements) {

		String resul = "";
		String[] list = null;

		if (!elements.contains("]"))
			list = elements.split(" +");
		else {
			list = elements.split("\\] \\[");
		}

		List<String> array = Arrays.asList(list);
		Set<String> hs = new LinkedHashSet<>(array);
		list = Arrays.copyOf(hs.toArray(), hs.toArray().length, String[].class);

		for (String hh : list)
			resul += hh + " ";

		return resul.trim();
	}

	public boolean fileCreated() {
		return successfull;
	}
}