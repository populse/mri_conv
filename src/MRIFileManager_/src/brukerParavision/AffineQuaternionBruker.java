/***********************************************************
 * this class calculate quaternion and affine transformation
 * without RECO_offset and RECO_ft_size 
 ***********************************************************/

package brukerParavision;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Jama.Matrix;
import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;

public class AffineQuaternionBruker {

	private String chemAcqp, chemMethod, chemReco;
	private float[] dim, vox, fov;
	// private String sl_mode;
	private String[] dimensions, fieldOfView, listTransp;
	private float sl_thick, sl_sepn;
	private Matrix affine;
	private Matrix quatern;
	private int recoTransp;

	private String[][] acqParams = { { "##$ACQ_grad_matrix=", "2" }, { "##$ACQ_fov=", "2" },
			{ "##$ACQ_slice_sepn=", "2" }, { "##$NSLICES=", "1" }, { "##$ACQ_slice_thick=", "1" },
			{ "##$ACQ_slice_sepn_mode=", "1" }, { "##$NI=", "1" } };

	private String[][] methodParams = { { "##$PVM_SPackArrReadOffset=", "2" }, { "##$PVM_SPackArrPhase1Offset=", "2" },
			{ "##$PVM_SPackArrSliceOffset=", "2" }, { "##$PVM_SPackArrGradOrient=", "2" }, // see for quaternion !!
			{ "##$PVM_SliceGeo=" } };

	private String[][] recoParams = { { "##$RECO_offset=", "2" }, { "##$RECO_ft_size=", "2" }, { "##$RECO_size=", "2" },
			{ "##$RECO_fov=", "2" }, { "##$RECO_transposition=", "2" } };

	private double[] voxs;
	private double[] offs;

	public AffineQuaternionBruker(String chem2dseq) {

		String chemSeq = chem2dseq.substring(0, chem2dseq.indexOf("pdata") - 1);

		chemReco = chem2dseq.substring(0, chem2dseq.lastIndexOf(PrefParam.separator) + 1) + "reco";
		chemAcqp = chemSeq + PrefParam.separator + "acqp";
		chemMethod = chemSeq + PrefParam.separator + "method";

		File fileReco = new File(chemReco);

		if (!fileReco.exists()) {
			chemReco = chemReco.substring(0, chemReco.indexOf("pdata") + 6) + "1" + PrefParam.separator + "reco";
		}

		try {
			dimensions = tabScaling(recoParams[2][0], chemReco).split(" "); // list of RECO_size
			fieldOfView = tabScaling(recoParams[3][0], chemReco).split(" "); // list of RECO_fov

		} catch (Exception e) {
			new GetStackTrace(e);
		}

		try {
			listTransp = tabScaling(recoParams[4][0], chemReco).split(" ");
		} catch (Exception e) {
			new GetStackTrace(e);
		}

		recoTransp = Integer.parseInt(listTransp[0]);

		dim = new float[3];
		fov = new float[3];
		vox = new float[3];

		for (int i = 0; i < dimensions.length; i++) {
			dim[i] = Float.parseFloat(dimensions[i]);
			fov[i] = Float.parseFloat(fieldOfView[i]);
			vox[i] = fov[i] / dim[i];
		}

		// sl_mode = searchParam(acqParams[5][0], chemAcqp,acqParams[5][1]);
		// //ACQ_slice_sepn_mode
		sl_thick = Float.parseFloat(searchParam(acqParams[4][0], chemAcqp, acqParams[4][1])); // ACQ_slice_thick

		try {
			sl_sepn = Float.parseFloat(searchParam(acqParams[2][0], chemAcqp, acqParams[2][1])); // ACQ_slice_sepn
		} catch (Exception e) {
			// sl_sepn=sl_thick;
		}

		if (dimensions.length < 3) {
			// dim[2] = NSLICES ou size(RECO_transposition) ????
			// dim[2] = listTransp.length;
			dim[2] = Integer.parseInt(searchParam(acqParams[3][0], chemAcqp, acqParams[3][1])); // NSLICES

			// vox[2] = sl_thick ou sl_spen ????
			// vox[2] = sl_thick;
			vox[2] = sl_sepn / 10;

			// fov[2] =1 ou sl_thick ou vox[2] * dim[2] / 10; ????
			// fov[2]=1;
			// fov[2] = sl_thick;
			fov[2] = vox[2] * dim[2];
		}

		if (dimensions.length == 3) {
			// dim[2] = Integer.parseInt(searchParam(acqParams[3][0], chemAcqp,
			// acqParams[3][1])); //NSLICES
			vox[2] = sl_thick / (10 * dim[2]);
			// fov[2] = vox[2] * dim[2] / 10;
			sl_sepn = vox[2];
			sl_thick = sl_sepn;
		}

		// for (int i=0;i<3;i++) {
		// System.out.println(chem2dseq);
		// System.out.println(dim[i]+" , "+fov[i]+" , "+vox[i]);
		// }

		// String affichRecoTransp = "";
		// for (String st:listTransp)
		// affichRecoTransp+=(st+" ");
		// IJ.log("\n"+seq);
		// IJ.log("dimension="+dimensions.length );
		// IJ.log("RECO_transposition="+affichRecoTransp);
		// IJ.log("dim[0]="+dim[0]+" , dim[1]="+dim[1]+" , dim[2]="+dim[2]);
		// IJ.log("vox[0]="+vox[0]+" , vox[1]="+vox[1]+" , vox[2]="+vox[2]);
		// IJ.log("fov[0]="+fov[0]+" , fov[1]="+fov[1]+" , fov[2]="+fov[2]);
		// IJ.log("sl_sepn="+sl_sepn+" , sl_mode="+sl_mode+" ,
		// sl_thick="+sl_thick+"\n");

		affine = rotn();
		affine = affine.times(trans());
		affine = affine.times(swaps());
		affine = affine.times(ft2mm());
//		affine = affine.times(recoff());
//		affine = affine.times(sliceGeo());
		affine = affine.times(start());

//		quatern = orient();
		quatern = orient2();
		quatern = quatern.transpose();
		quatern = quatern.times(start2());
		quatern = quatern.transpose();

	}

//	private Matrix sliceGeo() {
//		String[] listGeoMatrix = null;
//		String tmpGeoMatrix = "";
//
//		try {
//			tmpGeoMatrix = tabScaling(methodParams[4][0], chemMethod);
//			tmpGeoMatrix = tmpGeoMatrix.substring(tmpGeoMatrix.indexOf("(((") + 3);
//			tmpGeoMatrix = tmpGeoMatrix.substring(0, tmpGeoMatrix.indexOf(","));
//			System.out.println(this + " : " + tmpGeoMatrix);
//		} catch (Exception e) {
//			new GetStackTrace(e);
//		}
//
//		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
//
//		listGeoMatrix = tmpGeoMatrix.split(" "); // list of ACQ_grad_matrix
//
//		resul[0][0] = Double.parseDouble(listGeoMatrix[0]);
//		resul[1][0] = Double.parseDouble(listGeoMatrix[1]);
//		resul[2][0] = Double.parseDouble(listGeoMatrix[2]);
//
//		resul[0][1] = Double.parseDouble(listGeoMatrix[3]);
//		resul[1][1] = Double.parseDouble(listGeoMatrix[4]);
//		resul[2][1] = Double.parseDouble(listGeoMatrix[5]);
//
//		resul[0][2] = Double.parseDouble(listGeoMatrix[6]);
//		resul[1][2] = Double.parseDouble(listGeoMatrix[7]);
//		resul[2][2] = Double.parseDouble(listGeoMatrix[8]);
//
//		return new Matrix(resul);
//	}

	private Matrix rotn() {
		String[] listGradMatrix = null;
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
		try {
			listGradMatrix = tabScaling(acqParams[0][0], chemAcqp).split(" "); // list of ACQ_grad_matrix
		} catch (Exception e) {
			new GetStackTrace(e);
		}
		resul[0][0] = Double.parseDouble(listGradMatrix[0]);
		resul[1][0] = Double.parseDouble(listGradMatrix[1]);
		resul[2][0] = Double.parseDouble(listGradMatrix[2]);

		resul[0][1] = Double.parseDouble(listGradMatrix[3]);
		resul[1][1] = Double.parseDouble(listGradMatrix[4]);
		resul[2][1] = Double.parseDouble(listGradMatrix[5]);

		resul[0][2] = Double.parseDouble(listGradMatrix[6]);
		resul[1][2] = Double.parseDouble(listGradMatrix[7]);
		resul[2][2] = Double.parseDouble(listGradMatrix[8]);

//		 System.out.println("rotn");
//		 System.out.println(resul[0][0]+" "+resul[0][1]+" "+resul[0][2]+" "+resul[0][3]);
//		 System.out.println(resul[1][0]+" "+resul[1][1]+" "+resul[1][2]+" "+resul[1][3]);
//		 System.out.println(resul[2][0]+" "+resul[2][1]+" "+resul[2][2]+" "+resul[2][3]);
//		 System.out.println(resul[3][0]+" "+resul[3][1]+" "+resul[3][2]+" "+resul[3][3]);
//		 System.out.println(" ");

		return new Matrix(resul);
	}

	private Matrix trans() {
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
		resul[0][3] = Double.parseDouble(searchParam(methodParams[0][0], chemMethod, methodParams[0][1])); // PVM_SPackArrReadOffset
		resul[1][3] = Double.parseDouble(searchParam(methodParams[1][0], chemMethod, methodParams[1][1])); // PVM_SPackArrPhase1Offset
		resul[2][3] = Double.parseDouble(searchParam(methodParams[2][0], chemMethod, methodParams[2][1])); // PVM_SPackArrSliceOffset
		return new Matrix(resul);
	}

	private Matrix swaps() {

		double[][] resul = { { -1., 0., 0., 0. }, { 0., -1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };

		// if (patient_pos.contentEquals("Head_Prone"))
		// resul[2][2]=-1;

		return new Matrix(resul);
	}

	private Matrix ft2mm() {

		String[] listAcqFov = null;
		String[] listRecoSize = null;
		String[] listReco_ft_Size = null;
		voxs = new double[3];
		offs = new double[3];
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };

		try {
//			listAcqFov = tabScaling(acqParams[1][0], chemAcqp).split(" "); // list of ACQ_fov
//			listReco_ft_Size = tabScaling(recoParams[1][0], chemReco).split(" "); // list of RECO_ft_size  ??????
//			listRecoSize = tabScaling(recoParams[2][0], chemReco).split(" ");
			listAcqFov = fieldOfView;
			listRecoSize = tabScaling(recoParams[2][0], chemReco).split(" "); // list of RECO_size ?????
			listReco_ft_Size = listRecoSize;
		} catch (Exception e) {
			new GetStackTrace(e);
			// FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()+"\n----------------\n"+GetStackTrace.getMessage());
		}

		for (int i = 0; i < dimensions.length; i++) {
			voxs[i] = (10.00 * Double.parseDouble(listAcqFov[i]) / Double.parseDouble(listReco_ft_Size[i]));
			offs[i] = (Double.parseDouble(listRecoSize[i]) + 1) / 2.00 * -voxs[i];
		}

		voxs[2] = (double) fov[2] * 10 / dim[2];
		offs[2] = (dim[2] - 1) * (-voxs[2]) / 2.00;

		resul[0][0] = voxs[0];
		resul[0][3] = offs[0];
		resul[1][1] = voxs[1];
		resul[1][3] = offs[1];
		resul[2][2] = voxs[2];
		resul[2][3] = offs[2];
		return new Matrix(resul);
	}

//	private Matrix recoff() {
//
//		String[] listRecOff = null;
//
//		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
//
//		try {
//			listRecOff = tabScaling(recoParams[0][0], chemReco).split(" ");// list of RECO_offsets
//		} catch (Exception e) {
//			new GetStackTrace(e);
//			// FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()+"\n----------------\n"+GetStackTrace.getMessage());
//		}
//		resul[0][3] = Double.parseDouble(listRecOff[0]);
//		// resul[1][3] = Double.parseDouble(listRecOff[1]);
//		resul[1][3] = Double.parseDouble(listRecOff[listRecOff.length / 2]);
//
//		if (dimensions.length < 3)
//			resul[2][3] = 0;
//		else
//			resul[2][3] = Double.parseDouble(listRecOff[2]);
//
////		 System.out.println("recoff");
////		 System.out.println(resul[0][0]+" "+resul[0][1]+" "+resul[0][2]+" "+resul[0][3]);
////		 System.out.println(resul[1][0]+" "+resul[1][1]+" "+resul[1][2]+" "+resul[1][3]);
////		 System.out.println(resul[2][0]+" "+resul[2][1]+" "+resul[2][2]+" "+resul[2][3]);
////		 System.out.println(resul[3][0]+" "+resul[3][1]+" "+resul[3][2]+" "+resul[3][3]);
////		 System.out.println(" ");
//
//		return new Matrix(resul);
//
//	}

//	private Matrix orient() {
//
//		String[] listGradOrient = null;
//		try {
//			listGradOrient = tabScaling(methodParams[3][0], chemMethod).split(" "); // list of SPackArrGradOrient in
//																					// method file
//
//		} catch (Exception e) {
//			new GetStackTrace(e);
//		}
//
//		double[][] resul = new double[3][3];
//
//		resul[0][0] = Double.parseDouble(listGradOrient[0]);
//		resul[0][1] = Double.parseDouble(listGradOrient[1]);
//		resul[0][2] = Double.parseDouble(listGradOrient[2]);
//
//		resul[1][0] = Double.parseDouble(listGradOrient[3]);
//		resul[1][1] = Double.parseDouble(listGradOrient[4]);
//		resul[1][2] = Double.parseDouble(listGradOrient[5]);
//
//		resul[2][0] = Double.parseDouble(listGradOrient[6]);
//		resul[2][1] = Double.parseDouble(listGradOrient[7]);
//		resul[2][2] = Double.parseDouble(listGradOrient[8]);
//
//		// System.out.println("orient");
//		// System.out.println(resul[0][0]+" "+resul[0][1]+" "+resul[0][2]);
//		// System.out.println(resul[1][0]+" "+resul[1][1]+" "+resul[1][2]);
//		// System.out.println(resul[2][0]+" "+resul[2][1]+" "+resul[2][2]);
//		// System.out.println(" ");
//
//		return new Matrix(resul);
//	}

	private Matrix orient2() {

		String[] listGradOrient = null;
		try {
			listGradOrient = tabScaling(acqParams[0][0], chemAcqp).split(" "); // list of SPackArrGradOrient in method
																				// file
		} catch (Exception e) {
			new GetStackTrace(e);
		}

		double[][] resul = new double[3][3];

		resul[0][0] = Double.parseDouble(listGradOrient[0]);
		resul[0][1] = Double.parseDouble(listGradOrient[1]);
		resul[0][2] = Double.parseDouble(listGradOrient[2]);

		resul[1][0] = Double.parseDouble(listGradOrient[3]);
		resul[1][1] = Double.parseDouble(listGradOrient[4]);
		resul[1][2] = Double.parseDouble(listGradOrient[5]);

		resul[2][0] = Double.parseDouble(listGradOrient[6]);
		resul[2][1] = Double.parseDouble(listGradOrient[7]);
		resul[2][2] = Double.parseDouble(listGradOrient[8]);
		return new Matrix(resul);
	}

	private Matrix start() {
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
		if (recoTransp == 1) {
			resul[0][0] = 0;
			resul[0][1] = 1;
			resul[1][0] = 1;
			resul[1][1] = 0;
		}
		return new Matrix(resul);
	}

	private Matrix start2() {
		double[][] resul = { { 1., 0., 0. }, { 0., 1., 0. }, { 0., 0., 1. } };
		if (recoTransp == 1) {
			resul[0][0] = 0;
			resul[0][1] = 1;
			resul[1][0] = 1;
			resul[1][1] = 0;
		}
		return new Matrix(resul);
	}

	private String searchParam(String paramToFind, String fichier, String order) {
		BufferedReader lecteurAvecBuffer = null;
		String ligne;
		boolean find = false;
		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(fichier));
			while ((ligne = lecteurAvecBuffer.readLine()) != null) {
				if (ligne.indexOf(paramToFind) != -1) {
					if (order == "2")
						ligne = lecteurAvecBuffer.readLine();
					else
						ligne = ligne.substring(ligne.indexOf("=") + 1);
					lecteurAvecBuffer.close();
					find = true;
					return ligne;
				}
				if (find)
					break;

			}
			lecteurAvecBuffer.close();
		} catch (Exception exc) {
			// return "not found";
			return "0";
		}

		// return "not found";
		return "0";
	}

	private String tabScaling(String paramToFind, String fichier) throws NumberFormatException, IOException {
		String ligne;
		String resul = "";
		BufferedReader lecteurAvecBuffer = null;

		int inc = 1;

		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(fichier));
		} catch (Exception e) {
			new GetStackTrace(e);
		}
		while ((ligne = lecteurAvecBuffer.readLine()) != null) {
			if (ligne.indexOf(paramToFind) != -1) {
				String txt = ligne.substring(ligne.indexOf("(") + 2, ligne.indexOf(" )"));
				String[] listOfTxt = txt.split(", ");

				for (int i = 0; i < listOfTxt.length; i++)
					inc = inc * Integer.parseInt(listOfTxt[i]);

				int i = 0;
				while (i < inc) {
					ligne = lecteurAvecBuffer.readLine();
					for (int j = 0; j < ligne.split(" ").length; j++) {
						resul += ligne.split(" ")[j] + " ";
					}
					i += ligne.split(" ").length;
				}
				break;
			}
		}
		lecteurAvecBuffer.close();
		try {
			resul = resul.substring(0, resul.length() - 1);

		} catch (Exception e) {
			new GetStackTrace(e);
			// FileManagerFrame.getBugText().setText(FileManagerFrame.getBugText().getText()+"\n----------------\n"+GetStackTrace.getMessage());
			resul = "not found";
		}
		return resul;
	}

	public double[][] getMat() {

		// IJ.log(mat.getArray()[0][0]+" "+mat.getArray()[0][1]+"
		// "+mat.getArray()[0][2]+" "+mat.getArray()[0][3]);
		// IJ.log(mat.getArray()[1][0]+" "+mat.getArray()[1][1]+"
		// "+mat.getArray()[1][2]+" "+mat.getArray()[1][3]);
		// IJ.log(mat.getArray()[2][0]+" "+mat.getArray()[2][1]+"
		// "+mat.getArray()[2][2]+" "+mat.getArray()[2][3]);
		// IJ.log(mat.getArray()[3][0]+" "+mat.getArray()[3][1]+"
		// "+mat.getArray()[3][2]+" "+mat.getArray()[3][3]);

		return affine.getArray();
	}

	public double[] getQuatern() {

		double[][] o = quatern.getArray();

		double[] quaterns = new double[5];
		double qfac = 1.0, a, b, c, d;
		double det = (o[0][0] * o[1][1] * o[2][2]) - (o[0][0] * o[2][1] * o[1][2]) - (o[1][0] * o[0][1] * o[2][2])
				+ (o[1][0] * o[2][1] * o[0][2]) + (o[2][0] * o[0][1] * o[1][2]) - (o[2][0] * o[1][1] * o[0][2]);

		if (det < 0.0) {
			o[0][2] = -o[0][2];
			o[1][2] = -o[1][2];
			o[2][2] = -o[2][2];
			qfac = -1.0;
		}

		a = o[0][0] + o[1][1] + o[2][2] + 1.0;
		if (a > 0.5) {
			a = 0.5 * Math.sqrt(a);
			b = 0.25 * (o[2][1] - o[1][2]) / a;
			c = 0.25 * (o[0][2] - o[2][0]) / a;
			d = 0.25 * (o[1][0] - o[0][1]) / a;
		} else {
			double xd = 1.0 + o[0][0] - o[1][1] - o[2][2];
			double yd = 1.0 + o[1][1] - o[0][0] - o[2][2];
			double zd = 1.0 + o[2][2] - o[0][0] - o[1][1];
			if (xd > 1.0) {
				b = 0.5 * Math.sqrt(xd);
				c = 0.25 * (o[0][1] + o[1][0]) / b;
				d = 0.25 * (o[0][2] + o[2][1]) / b;
				a = 0.25 * (o[2][1] - o[1][2]) / b;
			} else if (yd > 1.0) {
				c = 0.5 * Math.sqrt(yd);
				b = 0.25 * (o[0][1] + o[1][0]) / c;
				d = 0.25 * (o[1][2] + o[2][1]) / c;
				a = 0.25 * (o[0][2] - o[2][0]) / c;
			} else {
				d = 0.5 * Math.sqrt(zd);
				b = 0.25 * (o[0][2] + o[2][0]) / d;
				c = 0.25 * (o[1][2] + o[2][1]) / d;
				a = 0.25 * (o[1][0] - o[0][1]) / d;
			}
			if (a < 0.0) {
				b = -b;
				c = -c;
				d = -d;
			}
		}

		quaterns[0] = qfac;
		quaterns[1] = a;
		quaterns[2] = b;
		quaterns[3] = c;
		quaterns[4] = d;

		if (recoTransp == 1) {
			quaterns[2] = c;
			quaterns[3] = b;
		}
		//
		// for (double fg:quaterns)
		// System.out.println(fg);
		//
		return quaterns;
	}

}