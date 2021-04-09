/***********************************************************
 * this class calculate quaternion and affine transformation
 * without RECO_offset and RECO_ft_size 
 * multi-oriented images compatible
 ***********************************************************/

package brukerParavision;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import Jama.Matrix;
import MRIFileManager.GetStackTrace;
import abstractClass.PrefParam;

public class AffineQuaternionBruker_mrtrix {

	private String chemAcqp, chemMethod, chemReco, chemVisupars;
	private float[] dim, vox, fov;
	private String[] dimensions, fieldOfView, listTransp;
	private float sl_thick, sl_sepn;
	private Matrix affine;
	private Matrix quatern;
	private int recoTransp, indiceOrientation = 0, indiceImage = 0, NumberImageByOrientation = 1;
	private double determ;

	private String[][] acqParams = { { "##$ACQ_grad_matrix=", "2" }, { "##$ACQ_fov=", "2" },
			{ "##$ACQ_slice_sepn=", "2" }, { "##$NSLICES=", "1" }, { "##$ACQ_slice_thick=", "1" },
			{ "##$ACQ_slice_sepn_mode=", "1" }, { "##$NI=", "1" }, { "##$ACQ_obj_order=", "2" } };

	private String[][] methodParams = { { "##$PVM_SPackArrReadOffset=", "2" }, { "##$PVM_SPackArrPhase1Offset=", "2" },
			{ "##$PVM_SPackArrSliceOffset=", "2" }, { "##$PVM_SPackArrGradOrient=", "2" }, // see for quaternion !!
			{ "##$PVM_SliceGeo=", "2" }, { "$PVM_SPackArrSliceOrient=", "2" } };

	private String[][] recoParams = { { "##$RECO_offset=", "2" }, { "##$RECO_ft_size=", "2" }, { "##$RECO_size=", "2" },
			{ "##$RECO_fov=", "2" }, { "##$RECO_transposition=", "2" } };

	private String[][] visuparsParams = { { "$VisuCoreFrameCount=", "1" }, { "##$VisuCoreOrientation=", "2" } };

	private double[] voxs;
	private double[] offs;

	public AffineQuaternionBruker_mrtrix(String chem2dseq, String orientation) {

		String chemSeq = chem2dseq.substring(0, chem2dseq.indexOf("pdata") - 1);

		chemReco = chem2dseq.substring(0, chem2dseq.lastIndexOf(PrefParam.separator) + 1) + "reco";
		chemVisupars = chem2dseq.substring(0, chem2dseq.lastIndexOf(PrefParam.separator) + 1) + "visu_pars";
		chemAcqp = chemSeq + PrefParam.separator + "acqp";
		chemMethod = chemSeq + PrefParam.separator + "method";

		int ImageInAcquisition = Integer
				.parseInt(searchParam(visuparsParams[0][0], chemVisupars, visuparsParams[0][1]));

		String[] tmp = null, tmplist = null;
		int NumberOfOrientation = 1;
		if (!orientation.isEmpty()) {
			tmp = searchParam(methodParams[5][0], chemMethod, methodParams[5][1]).split(" +");
			NumberOfOrientation = tmp.length;
			tmplist = searchParam(acqParams[7][0], chemAcqp, acqParams[7][1]).split(" +");
			indiceImage = Arrays.asList(tmp).indexOf(orientation);
			NumberImageByOrientation = ImageInAcquisition / NumberOfOrientation;
			indiceOrientation = Arrays.asList(tmplist).indexOf(String.valueOf(indiceImage * NumberImageByOrientation));
		} else {
//			orientation = searchParam(methodParams[5][0], chemMethod, methodParams[5][1]);
			indiceImage = 0;
			indiceOrientation = 0;
			NumberImageByOrientation = ImageInAcquisition / NumberOfOrientation;
		}
		
		File fileReco = new File(chemReco);

		if (!fileReco.exists()) {
			chemReco = chemReco.substring(0, chemReco.indexOf("pdata") + 6) + "1" + PrefParam.separator + "reco";
		}

		try {
			dimensions = tabScaling(recoParams[2][0], chemReco).split(" "); // list of RECO_size
			fieldOfView = tabScaling(recoParams[3][0], chemReco).split(" "); // list of RECO_fov

		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		try {
			listTransp = tabScaling(recoParams[4][0], chemReco).split(" ");
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		recoTransp = Integer.parseInt(listTransp[0 + indiceOrientation]);

		dim = new float[3];
		fov = new float[3];
		vox = new float[3];

		for (int i = 0; i < dimensions.length; i++) {
			dim[i] = Float.parseFloat(dimensions[i]);
			fov[i] = Float.parseFloat(fieldOfView[i]);
			vox[i] = fov[i] / dim[i];
		}

		sl_thick = Float.parseFloat(searchParam(acqParams[4][0], chemAcqp, acqParams[4][1])); // ACQ_slice_thick

		try {
			sl_sepn = Float
					.parseFloat(searchParam(acqParams[2][0], chemAcqp, acqParams[2][1]).split(" +")[indiceImage]); // ACQ_slice_sepn
		} catch (Exception e) {
			// sl_sepn=sl_thick;
		}
		
		if (!orientation.isEmpty() && NumberImageByOrientation == 1)
			sl_sepn = sl_thick;

		if (dimensions.length < 3) {
			dim[2] = Integer.parseInt(searchParam(acqParams[3][0], chemAcqp, acqParams[3][1])) / NumberOfOrientation; // NSLICES
			vox[2] = sl_sepn / 10;
			fov[2] = vox[2] * dim[2];
		}

		if (dimensions.length == 3) {
			vox[2] = sl_thick / (10 * dim[2]);
			sl_sepn = vox[2];
			sl_thick = sl_sepn;
		}

		quatern = orient2();
		quatern = quatern.transpose();
		quatern = quatern.times(start2());
		quatern = quatern.transpose();
		
		affine = rotn();
		affine = affine.times(trans());
		affine = affine.times(swaps());
		affine = affine.times(ft2mm());
		affine = affine.times(start());

	}


	private Matrix rotn() {
		String[] listGradMatrix = null;
		int offset;
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
		try {
			listGradMatrix = tabScaling(acqParams[0][0], chemAcqp).split(" "); // list of ACQ_grad_matrix
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
		offset = 9 * indiceOrientation;

		resul[0][0] = Double.parseDouble(listGradMatrix[0 + offset]);
		resul[1][0] = Double.parseDouble(listGradMatrix[1 + offset]);
		resul[2][0] = Double.parseDouble(listGradMatrix[2 + offset]);

		resul[0][1] = Double.parseDouble(listGradMatrix[3 + offset]);
		resul[1][1] = Double.parseDouble(listGradMatrix[4 + offset]);
		resul[2][1] = Double.parseDouble(listGradMatrix[5 + offset]);

		resul[0][2] = Double.parseDouble(listGradMatrix[6 + offset]);
		resul[1][2] = Double.parseDouble(listGradMatrix[7 + offset]);
		resul[2][2] = Double.parseDouble(listGradMatrix[8 + offset]);

		return new Matrix(resul);
	}

	private Matrix trans() {
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
		resul[0][3] = Double
				.parseDouble(searchParam(methodParams[0][0], chemMethod, methodParams[0][1]).split(" +")[indiceImage]); // PVM_SPackArrReadOffset
		resul[1][3] = Double
				.parseDouble(searchParam(methodParams[1][0], chemMethod, methodParams[1][1]).split(" +")[indiceImage]); // PVM_SPackArrPhase1Offset
		resul[2][3] = Double
				.parseDouble(searchParam(methodParams[2][0], chemMethod, methodParams[2][1]).split(" +")[indiceImage]); // PVM_SPackArrSliceOffset
		
		return new Matrix(resul);
	}

	private Matrix swaps() {

		double[][] resul = { { -1., 0., 0., 0. }, { 0., -1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
		if (determ < 0)
			resul[2][2]=-1;

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
			listAcqFov = fieldOfView;
			listRecoSize = tabScaling(recoParams[2][0], chemReco).split(" "); // list of RECO_size ?????
			listReco_ft_Size = listRecoSize;
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}

		for (int i = 0; i < dimensions.length; i++) {
			voxs[i] = (10.00 * Double.parseDouble(listAcqFov[i]) / Double.parseDouble(listReco_ft_Size[i]));
			offs[i] = (Double.parseDouble(listRecoSize[i])) / 2.00 * (-voxs[i]);
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


	private Matrix orient2() {

		String[] listGradOrient = null;
		int offset;
		try {
			listGradOrient = tabScaling(acqParams[0][0], chemAcqp).split(" "); // list of SPackArrGradOrient in method
		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
		offset = 9 * indiceOrientation;

		double[][] resul = new double[3][3];

		resul[0][0] = Double.parseDouble(listGradOrient[0 + offset]);
		resul[0][1] = Double.parseDouble(listGradOrient[1 + offset]);
		resul[0][2] = Double.parseDouble(listGradOrient[2 + offset]);

		resul[1][0] = Double.parseDouble(listGradOrient[3 + offset]);
		resul[1][1] = Double.parseDouble(listGradOrient[4 + offset]);
		resul[1][2] = Double.parseDouble(listGradOrient[5 + offset]);

		resul[2][0] = Double.parseDouble(listGradOrient[6 + offset]);
		resul[2][1] = Double.parseDouble(listGradOrient[7 + offset]);
		resul[2][2] = Double.parseDouble(listGradOrient[8 + offset]);

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
			new GetStackTrace(e, this.getClass().toString());
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
			new GetStackTrace(e, this.getClass().toString());
			resul = "not found";
		}
		return resul;
	}

	public double[][] getMat() {
		return affine.getArray();
	}

	public double[] getQuatern() {

		double[][] o = quatern.getArray();

		double[] quaterns = new double[5];
		double qfac = 1.0, a, b, c, d;
		double det = (o[0][0] * o[1][1] * o[2][2]) - (o[0][0] * o[2][1] * o[1][2]) - (o[1][0] * o[0][1] * o[2][2])
				+ (o[1][0] * o[2][1] * o[0][2]) + (o[2][0] * o[0][1] * o[1][2]) - (o[2][0] * o[1][1] * o[0][2]);
		
		determ = det;
		
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
		
		/***********************************************************
		 * before modification for MRTrix
		 ***********************************************************/

//		quaterns[0] = qfac;
//		quaterns[1] = a;
//		quaterns[2] = b;
//		quaterns[3] = c;
//		quaterns[4] = d;
//
//		if (recoTransp == 1) {
//			quaterns[2] = c;
//			quaterns[3] = b;
//		}

		/***********************************************************
		 * modification for MRTrix
		 ***********************************************************/

		quaterns[0] = qfac;
		quaterns[1] = d;
		quaterns[2] = c;
		quaterns[3] = b;
		quaterns[4] = -a;

//		System.out.println("recoTransp = "+recoTransp);
//		for (double fg : quaterns)
//			System.out.println(fg);
		//
		return quaterns;
	}
}