package exportFiles;

import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import Jama.Matrix;
import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class GenerateBvecsBvals2 extends PrefParam implements ParamMRI2 {

	private boolean successfull = false;
	private String[] dd;
	private String[] bveff;
	private String[] dgo, dgo_mrt;
	private int DwAoImages;
	private HashMap<String, String> hmInfo_tmp;
	private String linebasket;

	public GenerateBvecsBvals2(String directory, String name, Object linebasket, String constr, String bvec_bval_txt) {

//		System.out.println(this + " : " + bvec_bval_txt);
		this.linebasket = (String) linebasket;

		hmInfo_tmp = listBasket_hmInfo.get(linebasket);

//		System.out.println("Gradient Orientation : " + hmInfo_tmp.get("Gradient Orientation"));
//		System.out.println("Gradient Vectors : " + hmInfo_tmp.get("Gradient Vectors"));

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
			dgo_mrt = hmInfo_tmp.get("Gradient Vectors").toString().split(" +");
			if (hmInfo_tmp.get("Slice Orientation").toString().contentEquals("coronal")
					|| hmInfo_tmp.get("Slice Orientation").toString().contentEquals("sagittal"))
				if (dgo.length > 1)
					getTxt_Bruker("-y, -x, z", bvec_bval_txt, directory, name);
				else
					getTxt("-y, -x, z", bvec_bval_txt, directory, name, false);
			else
				if (dgo.length > 1)
					getTxt_Bruker("x, y, z", bvec_bval_txt, directory, name);
				else
					getTxt("x, y, z", bvec_bval_txt, directory, name, false);

		} else if (constr.contentEquals("Philips") && ddtmp.length > 3) {
			hmInfo_tmp.put("Image Orientation Patient", R_tot(hmInfo_tmp.get("Image Orientation Patient").split(" +"), hmInfo_tmp.get("Slice Orientation").toString()));
//			System.out.println("philips orientation patient bbb: " + hmInfo_tmp.get("Image Orientation Patient"));
			getTxt("-z, -x, y", bvec_bval_txt, directory, name, true);
		} else if (constr.contentEquals("Dicom") && ddtmp.length > 3) {
//			getTxt("-z, -x, y", bvec_bval_txt, directory, name);
			getTxt("x, y, z", bvec_bval_txt, directory, name, true);
		}
	}

	private void getTxt(String order, String type, String directory, String name, Boolean fsl_conv) {

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
				bvecsV[j][DwAoImages + i] = bvecsV[j][DwAoImages + i].replace("--", "");
				bvalsV[DwAoImages + i] = bvalsV[DwAoImages + i].replace("--", "");
			}

		if (type.contentEquals("1") || type.contentEquals("3"))
			for (int i = 0; i < bvecsV[0].length; i++)
				txtToReturn += String.join(" ", bvecsV[0][i], bvecsV[1][i], bvecsV[2][i], bvalsV[i]) + "\n";
		if (type.contentEquals("2") || type.contentEquals("3")) {
			if (fsl_conv)
				bvecsV = bvecs_fsl(bvecsV);
			txtBvecs = String.join("\n", String.join(" ", bvecsV[0]), String.join(" ", bvecsV[1]),
					String.join(" ", bvecsV[2]));
			txtBvals = String.join(" ", bvalsV);
		}

//		txtBvecs = txtBvecs.replaceAll("--", "");
//		txtBvals = txtBvals.replaceAll("--", "");
//		txtToReturn = txtToReturn.replaceAll("--", "");

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


	private void getTxt_Bruker(String order, String type, String directory, String name) {

		String txtBvecs = "", txtBvals = "", txtToReturn = "";
		String[] indice = { "x", "y", "z" };
		String[] list = order.split(",");
		String[] sign = { "", "", "" };
		String[] sign_mrt = { "", "", "-" };
		int[] rang = { 0, 1, 2 };
		int[] rang_mrt = { 0, 1, 2 };
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
		String[][] bvecsV_mrtrix = new String[3][dgo.length / 3];
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
					bvecsV_mrtrix[j][i] = "0.0";
					zero = true;
				}
				else {	
					bvecsV[j][i] = sign[j] + dgo[(i * 3) + rang[j]];
					bvecsV_mrtrix[j][i] = sign_mrt[j] + dgo_mrt[(i * 3) + rang_mrt[j]];
					zero = false;
				}
				bvecsV[j][i] = bvecsV[j][i].replace("--", "");
				bvecsV_mrtrix[j][i] = bvecsV_mrtrix[j][i].replace("--", "");
			}
			if (zero)
				bvalsV[i] = "0.0";
			else
				bvalsV[i] = bveff[i];
			bvalsV[i] = bvalsV[i].replace("--", "");

		}

//		bvecsV_mrtrix = bvecs_mrtrix(bvecsV_mrtrix);
		
		if (type.contentEquals("1") || type.contentEquals("3"))
			for (int i = 0; i < bvecsV[0].length; i++)
				txtToReturn += String.join(" ", bvecsV_mrtrix[0][i], bvecsV_mrtrix[1][i], bvecsV_mrtrix[2][i], bvalsV[i]) + "\n";
//				txtToReturn += String.join(" ", bvecsV[0][i], bvecsV[1][i], bvecsV[2][i], bvalsV[i]) + "\n";

		if (type.contentEquals("2") || type.contentEquals("3")) {
//			bvecsV = bvecs_fsl(bvecsV);
			txtBvecs = String.join("\n", String.join(" ", bvecsV[0]), String.join(" ", bvecsV[1]),
					String.join(" ", bvecsV[2]));
			txtBvals = String.join(" ", bvalsV);
		}

//		txtBvecs = txtBvecs.replaceAll("--", "");
//		txtBvals = txtBvals.replaceAll("--", "");
//		txtToReturn = txtToReturn.replaceAll("--", "");

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

	
//	private String[][] bvecs_mrtrix(String[][] bvecs) {
//		String[][] result = new String[bvecs.length][bvecs[0].length];
//		
//		String[] grad_orient = hmInfo_tmp.get("Gradient Orientation").toString().split(" +");
//		
//		System.out.println("grad orient : " + Arrays.toString(grad_orient));
//		
//		double[] v1 = Arrays.stream(Arrays.copyOfRange(grad_orient, 0, 3)).mapToDouble(Double::parseDouble)
//				.toArray();
//		double[] v2 = Arrays.stream(Arrays.copyOfRange(grad_orient, 3, 6)).mapToDouble(Double::parseDouble)
//				.toArray();
//		double[] v3 = Arrays.stream(Arrays.copyOfRange(grad_orient, 6, 9)).mapToDouble(Double::parseDouble)
//				.toArray();
//		
//		for (int i = 0; i < bvecs[0].length; i++) {
//			result[0][i] = String.valueOf(v1[0] * Double.parseDouble(bvecs[0][i])
//					+ v1[1] * Double.parseDouble(bvecs[1][i]) + v1[2] * Double.parseDouble(bvecs[2][i]));
//			result[1][i] = String.valueOf(v2[0] * Double.parseDouble(bvecs[0][i])
//					+ v2[1] * Double.parseDouble(bvecs[1][i]) + v2[2] * Double.parseDouble(bvecs[2][i]));
//			result[2][i] = String.valueOf(v3[0] * Double.parseDouble(bvecs[0][i])
//					+ v3[1] * Double.parseDouble(bvecs[1][i]) + v3[2] * Double.parseDouble(bvecs[2][i]));
//
//		}
//		
//		return result;
//
//	}
	
	private String[][] bvecs_fsl(String[][] bvecs) {
		
//		System.out.println("bvecs for fsl calculate : " + Arrays.toString(bvecs[0]));

		String[][] result = new String[bvecs.length][bvecs[0].length];
		
		String hmInf = hmInfo_tmp.get("Image Orientation Patient");
		String[] listOrientation;
		
//		System.out.println("hmInf :" + hmInf);
				
		if (hmInf.contains("\\"))
				listOrientation = hmInfo_tmp.get("Image Orientation Patient").split("\\\\");
		else
			listOrientation = hmInfo_tmp.get("Image Orientation Patient").split(" +");
		
//		System.out.println(Arrays.toString(listOrientation));
		
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

	private String R_tot(String[] angle, String orientation) {
		
		float angAP = Float.valueOf(angle[0]);
		float angFH = Float.valueOf(angle[1]);
		float angRL = Float.valueOf(angle[2]);
		
		double[][] r1 = { { 1, 0, 0, 0 }, { 0, Math.cos(angRL), -Math.sin(angRL), 0 },
				{ 0, Math.sin(angRL), Math.cos(angRL), 0 }, { 0, 0, 0, 1 } };

		double[][] r2 = { { Math.cos(angAP), 0, Math.sin(angAP), 0 }, { 0, 1, 0, 0 },
				{ -Math.sin(angAP), 0, Math.cos(angAP), 0 }, { 0, 0, 0, 1 } };

		double[][] r3 = { { Math.cos(angFH), -Math.sin(angFH), 0, 0 }, { Math.sin(angFH), Math.cos(angFH), 0, 0 },
				{ 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
		
		String[] list = new String[6] ;
		
		Matrix rot = new Matrix(r1).times(new Matrix(r2)).times(new Matrix(r3));
		
		if (orientation.contentEquals("sagittal")) {
			list[0] = String.valueOf(rot.get(1, 0));
			list[1] = String.valueOf(rot.get(1, 1));
			list[2] = String.valueOf(rot.get(1, 2));
			list[3] = String.valueOf(-rot.get(2, 0));
			list[4] = String.valueOf(-rot.get(2, 1));
			list[5] = String.valueOf(-rot.get(2, 2));
		}

		else if (orientation.contentEquals("coronal")) {
			list[0] = String.valueOf(rot.get(0, 0));
			list[1] = String.valueOf(rot.get(0, 1));
			list[2] = String.valueOf(rot.get(0, 2));
			list[3] = String.valueOf(-rot.get(2, 0));
			list[4] = String.valueOf(-rot.get(2, 1));
			list[5] = String.valueOf(-rot.get(2, 2));
		}

		else if (orientation.contentEquals("axial")) {
			list[0] = String.valueOf(rot.get(0, 0));
			list[1] = String.valueOf(rot.get(0, 1));
			list[2] = String.valueOf(rot.get(0, 2));
			list[3] = String.valueOf(rot.get(1, 0));
			list[4] = String.valueOf(rot.get(1, 1));
			list[5] = String.valueOf(rot.get(1, 2));
		}

//		for(int i=0;i<4;i++)
//		    for(int j=0;j<4;j++) {
//		    	System.out.println(rot.get(i, j));
//		    }

		return String.join(" ", list);
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