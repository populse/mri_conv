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
	private int DwAoImages;
	private HashMap<String, String> hmInfo_tmp;
	private String linebasket;

	public GenerateBvecsBvals2(String directory, String name, Object linebasket, String constr, String bvec_bval_txt) {
		
		this.linebasket = (String) linebasket;
		
		hmInfo_tmp = listBasket_hmInfo.get(linebasket);
		
		dd = hmInfo_tmp.get("Direction Diffusion").toString().split(" +");
		bveff = hmInfo_tmp.get("B-values effective").toString().split(" +");
		DwAoImages = Integer.parseInt(hmInfo_tmp.get("Diffusion Ao Images number").toString());

		String[] ddtmp = deleteDuplicate(hmInfo_tmp.get("Direction Diffusion").toString()).split(" +");

		if (constr.contentEquals("Bruker") && ddtmp.length > 1) {
			if (hmInfo_tmp.get("Slice Orientation").toString().contentEquals("coronal")
					|| hmInfo_tmp.get("Slice Orientation").toString().contentEquals("sagittal"))
				getTxt("-y, -x, z", bvec_bval_txt, directory, name);
			else
				getTxt("x, y, z", bvec_bval_txt, directory, name);

		} else if (constr.contentEquals("Philips") && ddtmp.length > 1) {
			getTxt("-z, -x, y", bvec_bval_txt, directory, name);
		} else if (constr.contentEquals("Dicom") && ddtmp.length > 1) {
			getTxt("-z, -x, y", bvec_bval_txt, directory, name);
		}
	}

	private void getTxt(String order, String type, String directory, String name) {

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
				new GetStackTrace(e);
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ "-bvecs-bvals-MRtrix.txt" + "'");
			}
		if (!txtBvecs.isEmpty())
			try {
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + "-bvecs-MRtrix.txt");
				writer.write(txtBvecs);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e);
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ "-bvecs-MRtrix.txt" + "'");
			}
		
		if (!txtBvals.isEmpty())
			try {
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + "-bvals-MRtrix.txt");
				writer.write(txtBvals);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e);
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ "-bvals-MRtrix.txt" + "'");
			}
		
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