package philips;

import java.util.HashMap;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import abstractClass.ParamMRI2;

public class AffineQuaternionPhilips implements ParamMRI2 {

	private HashMap<String, String> InfoImage = new HashMap<>();

	private float dimx, dimy, dimz;
	private float angAP, angFH, angRL;
	private float offAP, offFH, offRL;

	private float fovAP, fovFH, fovRL;

	private String orientation, FOV, Angulation, Offcentre;
	// private String direction;
	private final double degToRad = Math.PI / 180;

	private Matrix A_tot, Q_tot;
	private Matrix offsetA;
	private double[] quaterns = new double[5];

	public AffineQuaternionPhilips(String lb) {

		InfoImage = listBasket_hmInfo.get(lb);

		orientation = InfoImage.get("Slice Orientation");
		FOV = InfoImage.get("FOV");
		Angulation = InfoImage.get("Angulation Midslice");
		Offcentre = InfoImage.get("Position Midslice");

		dimx = Float.parseFloat(InfoImage.get("Scan Resolution").split(" +")[0]);
		dimy = Float.parseFloat(InfoImage.get("Scan Resolution").split(" +")[1]);
		dimz = Float.parseFloat(InfoImage.get("Number Of Slice"));

		// System.out.println(lb+" : "+orientation+" , "+FOV+" , "+Angulation+" ,
		// "+Offcentre);
		// System.out.println(lb+" : "+dimx+" , "+dimy+" , "+dimz);

		fovAP = Float.parseFloat(FOV.split(" +")[0]);
		fovFH = Float.parseFloat(FOV.split(" +")[1]);
		fovRL = Float.parseFloat(FOV.split(" +")[2]);

		angAP = Float.parseFloat(Angulation.split(" +")[0]) * (float) degToRad;
		angFH = Float.parseFloat(Angulation.split(" +")[1]) * (float) degToRad;
		angRL = Float.parseFloat(Angulation.split(" +")[2]) * (float) degToRad;

		offAP = Float.parseFloat(Offcentre.split(" +")[0]);
		offFH = Float.parseFloat(Offcentre.split(" +")[1]);
		offRL = Float.parseFloat(Offcentre.split(" +")[2]);

		A_tot = patient_to_tal().times(R_tot()).times(Zm()).times(lmm()).times(analyze_to_dicom());
		// A_tot = A_tot.times(R_tot());
		// A_tot = A_tot.times(Zm());
		// A_tot = A_tot.times(lmm());
		// A_tot = A_tot.times(analyze_to_dicom());

		offsetA = new Matrix(A_tot.getArray());
		offsetA = offsetA.times(p_orig());

		A_tot.set(0, 3, -offsetA.get(0, 0) - offRL);
		A_tot.set(1, 3, -offsetA.get(1, 0) - offAP);
		A_tot.set(2, 3, -offsetA.get(2, 0) + offFH);
		// A_tot.set(0, 3, -offsetA.get(0,0)+offRL);
		// A_tot.set(1, 3, -offsetA.get(1,0)+offAP);
		// A_tot.set(2, 3, -offsetA.get(2,0)-offFH);
		Q_tot = A_tot.copy();

		qoffset_xyz();
		quatern_bcd();

	}

	private Matrix R_tot() {

		double[][] r1 = { { 1, 0, 0, 0 }, { 0, Math.cos(angRL), -Math.sin(angRL), 0 },
				{ 0, Math.sin(angRL), Math.cos(angRL), 0 }, { 0, 0, 0, 1 } };

		double[][] r2 = { { Math.cos(angAP), 0, Math.sin(angAP), 0 }, { 0, 1, 0, 0 },
				{ -Math.sin(angAP), 0, Math.cos(angAP), 0 }, { 0, 0, 0, 1 } };

		double[][] r3 = { { Math.cos(angFH), -Math.sin(angFH), 0, 0 }, { Math.sin(angFH), Math.cos(angFH), 0, 0 },
				{ 0, 0, 1, 0 }, { 0, 0, 0, 1 } };

		return new Matrix(r1).times(new Matrix(r2)).times(new Matrix(r3));
	}

	private Matrix Zm() {
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };
		float lXmm = 0, lYmm = 0, lZmm = 0;

		// transversal (axial or 1)
		if (orientation.toLowerCase().equals("tra") || orientation.toLowerCase().equals("transversal")
				|| orientation.equals("1") || orientation.toLowerCase().equals("axial")) {
			lXmm = fovRL / dimx;
			lYmm = fovAP / dimy;
			lZmm = fovFH / dimz;

			if (lXmm > lYmm)
				lYmm = lXmm;
			else
				lXmm = lYmm;
		}

		// sagittal or 2
		if (orientation.toLowerCase().equals("sag") || orientation.toLowerCase().equals("sagittal")
				|| orientation.equals("2")) {
			lXmm = fovRL / dimz;
			lYmm = fovAP / dimx;
			lZmm = fovFH / dimy;

			if (lYmm > lZmm)
				lZmm = lYmm;
			else
				lYmm = lZmm;
		}

		// coronal or 3
		if (orientation.toLowerCase().equals("cor") || orientation.toLowerCase().equals("coronal")
				|| orientation.equals("3")) {
			lXmm = fovRL / dimx;
			lYmm = fovAP / dimz;
			lZmm = fovFH / dimy;

			if (lXmm > lZmm)
				lZmm = lXmm;
			else
				lXmm = lZmm;
		}

		resul[0][0] = lXmm;
		resul[1][1] = lYmm;
		resul[2][2] = lZmm;

		return new Matrix(resul);
	}

	private Matrix lmm() {
		double[][] resul = { { 1., 0., 0., 0. }, { 0., 1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };

		// transversal do not rotate

		// sagittal
		if (orientation.equals("SAG") || orientation.toLowerCase().equals("sagittal") || orientation.equals("2")) {

			resul[0][0] = 0;
			resul[0][1] = 0;
			resul[0][2] = -1;
			resul[0][3] = 0;
			resul[1][0] = 1;
			resul[1][1] = 0;
			resul[1][2] = 0;
			resul[1][3] = 0;
			resul[2][0] = 0;
			resul[2][1] = -1;
			resul[2][2] = 0;
			resul[2][3] = 0;
			resul[3][0] = 0;
			resul[3][1] = 0;
			resul[3][2] = 0;
			resul[3][3] = 1;
		}

		// coronal
		if (orientation.equals("COR") || orientation.toLowerCase().equals("coronal") || orientation.equals("3")) {

			resul[0][0] = 1;
			resul[0][1] = 0;
			resul[0][2] = 0;
			resul[0][3] = 0;
			resul[1][0] = 0;
			resul[1][1] = 0;
			resul[1][2] = 1;
			resul[1][3] = 0;
			resul[2][0] = 0;
			resul[2][1] = -1;
			resul[2][2] = 0;
			resul[2][3] = 0;
			resul[3][0] = 0;
			resul[3][1] = 0;
			resul[3][2] = 0;
			resul[3][3] = 1;
		}

		return new Matrix(resul);

	}

	private Matrix patient_to_tal() {
		double[][] resul = { { -1., 0., 0., 0. }, { 0., -1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };

		return new Matrix(resul);
	}

	private Matrix analyze_to_dicom() {
		double[][] resul = { { 1., 0., 0., 0. }, { 0., -1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };

		return new Matrix(resul);
	}

	private Matrix p_orig() {
		double[][] resul = new double[4][1];

		resul[0][0] = (dimx - 1) / 2;
		resul[1][0] = (dimy - 2) / 2;
		resul[2][0] = (dimz - 1) / 2;
		resul[3][0] = 1;

		return new Matrix(resul);
	}

	/********************************
	 * Quaternion
	 ***********************************/
	private Matrix qoffset_xyz() {
		double[][] resul = new double[3][1];

		resul[0][0] = Q_tot.get(0, 3);
		resul[1][0] = Q_tot.get(1, 3);
		resul[2][0] = Q_tot.get(2, 3);

		return new Matrix(resul);
	}

	public void quatern_bcd() {

		double[] d1 = new double[3];
		int[] tab = { 0, 1, 2 };
		double qfac;

		double[][] neg = { { -1, -1, -1 }, { -1, -1, -1 }, { -1, -1, -1 } };

		Matrix out = Q_tot;
		Matrix mat, P;

		mat = out.getMatrix(tab, tab);
		mat = mat.arrayTimesEquals(mat);

		d1[0] = Math.sqrt(mat.get(0, 0) + mat.get(1, 0) + mat.get(2, 0));
		d1[1] = Math.sqrt(mat.get(0, 1) + mat.get(1, 1) + mat.get(2, 1));
		d1[2] = Math.sqrt(mat.get(0, 2) + mat.get(1, 2) + mat.get(2, 2));

		if (d1[0] == 0) {
			out.set(0, 0, 1);
			out.set(1, 0, 0);
			out.set(2, 0, 0);
			out.set(3, 0, 0);
			d1[0] = 1;
		} else {
			out.set(0, 0, out.get(0, 0) / d1[0]);
			out.set(1, 0, out.get(1, 0) / d1[0]);
			out.set(2, 0, out.get(2, 0) / d1[0]);
		}
		if (d1[1] == 0) {
			out.set(0, 1, 0);
			out.set(1, 1, 1);
			out.set(2, 1, 0);
			out.set(3, 1, 0);
			d1[1] = 1;
		} else {
			out.set(0, 1, out.get(0, 1) / d1[1]);
			out.set(1, 1, out.get(1, 1) / d1[1]);
			out.set(2, 1, out.get(2, 1) / d1[1]);
		}
		if (d1[2] == 0) {
			out.set(0, 2, 0);
			out.set(1, 2, 0);
			out.set(2, 2, 1);
			out.set(3, 2, 0);
			d1[2] = 1;
		} else {
			out.set(0, 2, out.get(0, 2) / d1[2]);
			out.set(1, 2, out.get(1, 2) / d1[2]);
			out.set(2, 2, out.get(2, 2) / d1[2]);
		}

		SingularValueDecomposition svd = new SingularValueDecomposition(out.getMatrix(tab, tab));

		// P=svd.getU();
		// P=P.times(svd.getV());

		P = svd.getU().arrayTimesEquals(new Matrix(neg));
		P = P.times(svd.getV().arrayTimesEquals(new Matrix(neg)));

		if (P.det() > 0)
			qfac = 1.0;
		else {
			qfac = -1.0;
			P.set(0, 2, -P.get(0, 2));
			P.set(1, 2, -P.get(1, 2));
			P.set(2, 2, -P.get(2, 2));
		}

		double[][] o = P.getArray();
		double a, b, c, d;

		a = P.trace() + 1;

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
				d = 0.25 * (o[0][2] + o[2][0]) / b; // ?? d = 0.25 * (o[0][2]+o[2][0]) / b;
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

		// for (double kk : quaterns)
		// System.out.println(kk);

		// return quaterns;

	}

	public double[][] getsform() {
		return A_tot.getArrayCopy();
	}

	public double[] getquaterns() {
		return quaterns;
	}
}
