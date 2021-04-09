package exportFiles;

import java.io.FileWriter;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class GenerateBvecsBvals implements ParamMRI2 {

	private boolean successfull = false;

	public GenerateBvecsBvals(String directory, String name, Object linebasket, String constr, String oneOrTwoFiles) {

		String txt = "";
		String[] dd = listBasket_hmInfo.get(linebasket).get("Direction Diffusion").toString().split(" +");
		String[] bveff = listBasket_hmInfo.get(linebasket).get("B-values effective").toString().split(" +");
		int DwAoImages = Integer
				.parseInt(listBasket_hmInfo.get(linebasket).get("Diffusion Ao Images number").toString());


		if (constr.contentEquals("Bruker")) {
			for (int i = 0; i < DwAoImages; i++)
				txt += "0.0 0.0 0.0 " + bveff[i] + "\n";

			if (listBasket_hmInfo.get(linebasket).get("Slice Orientation").toString().contentEquals("coronal")
					|| listBasket_hmInfo.get(linebasket).get("Slice Orientation").toString()
							.contentEquals("sagittal")) {

				for (int i = 0; i < dd.length / 3; i++)
					txt += "-" + dd[1 + 3 * i] + " -" + dd[0 + 3 * i] + " " + dd[2 + 3 * i] + " "
							+ bveff[i + DwAoImages] + "\n";
			}

			else {
				for (int i = 0; i < dd.length / 3; i++)
					txt += dd[0 + 3 * i] + " " + dd[1 + 3 * i] + " " + dd[2 + 3 * i] + " " + bveff[i + DwAoImages]
							+ "\n";
			}
		} else if (constr.contentEquals("Philips") && dd.length > 3) {
			for (int i = 0; i < dd.length / 3; i++)
				txt += "-" + dd[1 + 3 * i] + " " + dd[2 + 3 * i] + " -" + dd[0 + 3 * i] + " " + bveff[i] + "\n";
		}

		txt = txt.replaceAll("--", "");

		if (!txt.isEmpty())
			try {
				FileWriter writer = new FileWriter(directory + PrefParam.separator + name + "-bvecs-bvals-MRtrix.txt");
				writer.write(txt);
				writer.flush();
				writer.close();
				successfull = true;
			} catch (Exception e) {
				successfull = false;
				new GetStackTrace(e, this.getClass().toString());
				System.out.println("Error: impossible to create '" + directory + PrefParam.separator + name
						+ "-bvecs-bvals-MRtrix.txt" + "'");
			}
	}

	public boolean fileCreated() {
		return successfull;
	}
}