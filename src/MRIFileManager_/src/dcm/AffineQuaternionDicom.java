package dcm;

import java.util.Arrays;
import java.util.HashMap;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import abstractClass.ParamMRI2;

public class AffineQuaternionDicom implements ParamMRI2 {

	private HashMap<String, String> InfoImage = new HashMap<>();

	private double nX, nY, nZ;
	private double[] T1 = new double[3];
	private double[] Tn = new double[3];
	private double[] Fn = new double[6];
	private double dx, dy, dz;
	private String sliceOrientation;

	private Matrix A_tot, Q_tot, Q_totb;
	private double[] quaterns = new double[5];

	public AffineQuaternionDicom(String lb) {

		InfoImage = listBasket_hmInfo.get(lb);

		sliceOrientation = InfoImage.get("Slice Orientation");

		nX = Double.parseDouble(InfoImage.get("Scan Resolution").split(" +")[0]);
		nY = Double.parseDouble(InfoImage.get("Scan Resolution").split(" +")[1]);
		nZ = InfoImage.get("Image Position Patient").split(" +").length;

		dx = Double.parseDouble(InfoImage.get("Spatial Resolution").split(" +")[0]);
		dy = Double.parseDouble(InfoImage.get("Spatial Resolution").split(" +")[1]);
		dz = Double.parseDouble(InfoImage.get("Slice Thickness"));
		

		String positionPatient = InfoImage.get("Image Position Patient");
		String orientationPatient = InfoImage.get("Image Orientation Patient");

		int i = 0;
		for (String sf : positionPatient.split(" +")[0].split("\\\\")) {
			T1[i] = Double.parseDouble(sf);
			i++;
		}

		i = 0;
		for (String sf : positionPatient.split(" +")[positionPatient.split(" +").length - 1].split("\\\\")) {
			Tn[i] = Double.parseDouble(sf);
			i++;
		}

		i = 0;
		for (String sf : orientationPatient.split(" +")[0].split("\\\\")) {
			Fn[i] = Double.parseDouble(sf);
			i++;
		}

		/********************** affine transformation *****************************/

		if (!Arrays.equals(T1, Tn))
			A_tot = patient_to_tal().times(R_tot_multi());
		else
			A_tot = patient_to_tal2().times(R_tot_single());
//			A_tot = patient_to_tal().times(R_tot_single());

		/********************* quaternion *****************************************/
		Q_totb = rot();
		// Q_totb.print(5, 5);
		Q_tot = Q_totb.times(-1);
		// Q_tot.print(5, 5);
		Q_tot = Q_tot.times(origin());
		// Q_tot.print(5, 5);
		Q_tot = rot_mat(Q_tot, Q_totb);
		// Q_tot.print(5, 5);
		Q_tot = Q_tot.times(voxel_sz());

		quatern_bcd();

	}

	private Matrix R_tot_multi() {
		// double [][] r = { { Fn[0]*dx , Fn[3]*dy , (T1[0]-Tn[0])/(1-nZ) , T1[0] },
		// { Fn[1]*dx , Fn[4]*dy , (T1[1]-Tn[1])/(1-nZ) , T1[1] },
		// { Fn[2]*dx , Fn[5]*dy , (T1[2]-Tn[2])/(1-nZ) , T1[2] },
		// { 0 , 0 , 0 , 1 }
		// };

		double[][] r = new double[4][4];

		if (sliceOrientation.toLowerCase().contains("oblique")) {
			// if (Arrays.equals(T1, Tn))
			// Tn[2]=0.0;

			r[0][0] = Fn[0] * dx;
			r[0][1] = Fn[3] * dy;
			r[0][2] = (T1[0] - Tn[0]) / (1 - nZ);
			r[0][3] = T1[0];
			r[1][0] = Fn[1] * dx;
			r[1][1] = Fn[4] * dy;
			r[1][2] = (T1[1] - Tn[1]) / (1 - nZ);
			r[1][3] = T1[1];
			r[2][0] = Fn[2] * dx;
			r[2][1] = Fn[5] * dy;
			r[2][2] = (T1[2] - Tn[2]) / (1 - nZ);
			r[2][3] = T1[2];
		}

		if (sliceOrientation.toLowerCase().contains("coronal")) {
			// if (Arrays.equals(T1, Tn))
			// Tn[1]=0.0;

			r[0][0] = Fn[0] * dx;
			r[0][1] = Fn[3] * dy;
			r[0][2] = (T1[0] - Tn[0]) / (1 - nZ);
			r[0][3] = T1[0];
			r[1][0] = Fn[1] * dx;
			r[1][1] = Fn[4] * dy;
			r[1][2] = (T1[1] - Tn[1]) / (1 - nZ);
			r[1][3] = T1[1];
			r[2][0] = Fn[2] * dx;
			r[2][1] = Fn[5] * dy;
			r[2][2] = (T1[2] - Tn[2]) / (1 - nZ);
			r[2][3] = T1[2];
		}

		if (sliceOrientation.toLowerCase().contains("axial")) {
			// if (Arrays.equals(T1, Tn))
			// Tn[2]=0.0;

			r[0][0] = Fn[0] * dx;
			r[0][1] = Fn[3] * dy;
			r[0][2] = (T1[0] - Tn[0]) / (1 - nZ);
			r[0][3] = T1[0];
			r[1][0] = Fn[1] * dx;
			r[1][1] = Fn[4] * dy;
			r[1][2] = (T1[1] - Tn[1]) / (1 - nZ);
			r[1][3] = T1[1];
			r[2][0] = Fn[2] * dx;
			r[2][1] = Fn[5] * dy;
			r[2][2] = (T1[2] - Tn[2]) / (1 - nZ);
			r[2][3] = T1[2];
		}

		if (sliceOrientation.toLowerCase().contains("sagittal")) {
			// if (Arrays.equals(T1, Tn))
			// Tn[0]=0.0;

			r[0][0] = Fn[0] * dx;
			r[0][1] = Fn[3] * dy;
			r[0][2] = (T1[0] - Tn[0]) / (1 - nZ);
			r[0][3] = T1[0];
			r[1][0] = Fn[1] * dx;
			r[1][1] = Fn[4] * dy;
			r[1][2] = (T1[1] - Tn[1]) / (1 - nZ);
			r[1][3] = T1[1];
			r[2][0] = Fn[2] * dx;
			r[2][1] = Fn[5] * dy;
			r[2][2] = (T1[2] - Tn[2]) / (1 - nZ);
			r[2][3] = T1[2];
		}

		r[3][0] = 0;
		r[3][1] = 0;
		r[3][2] = 0;
		r[3][3] = 1;

		return new Matrix(r);
	}
	
	private Matrix R_tot_single() {
		double[][] r = new double[4][4];
		
		double[] Sn = Cross(Fn);

		r[0][0] = Fn[0] * dx;
		r[0][1] = Fn[3] * dy;
		r[0][2] = Sn[0] * dz;
		r[0][3] = T1[0];
		r[1][0] = Fn[1] * dx;
		r[1][1] = Fn[4] * dy;
		r[1][2] = Sn[1] * dz;
		r[1][3] = T1[1];
		r[2][0] = Fn[2] * dx;
		r[2][1] = Fn[5] * dy;
		r[2][2] = Sn[2] * dz;
		r[2][3] = T1[2];

		r[3][0] = 0;
		r[3][1] = 0;
		r[3][2] = 0;
		r[3][3] = 1;

		return new Matrix(r);

	}

	private double[] Cross(double[] a) {
		
		double[] crossing = new double[3];
		
		crossing[0] = a[1]*a[5] - a[2]*a[4];
		crossing[1] = a[2]*a[3] - a[0]*a[5];
		crossing[2] = a[0]*a[4] - a[1]*a[3];

//			    c = [(a[1]*b[2] - a[2]*b[1]) * dz,
//			         (a[2]*b[0] - a[0]*b[2]) * dz,
//			         (a[0]*b[1] - a[1]*b[0]) * dz]
		return crossing;
		
	}
	
	private Matrix patient_to_tal() {
		double[][] resul = { { -1., 0., 0., 0. }, { 0., -1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };

		return new Matrix(resul);
	}

	private Matrix patient_to_tal2() {
		double[][] resul = { { -1., 0., 0., 0. }, { 0., -1., 0., 0. }, { 0., 0., 1., 0. }, { 0., 0., 0., 1. } };

		return new Matrix(resul);
	}

	private Matrix rot() {
		double[][] orient = { { Fn[0], Fn[3] }, { Fn[1], Fn[4] }, { -Fn[2], -Fn[5] } };

		double[][] resul = new double[3][3];

		int[] tab = { 0, 1, 2 };
		int[] tab2 = { 0, 1 };

		Matrix out = new Matrix(orient);

		SingularValueDecomposition svd = new SingularValueDecomposition(out.getMatrix(tab, tab2));

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 2; j++)
				resul[i][j] = orient[i][j];

		resul[0][2] = svd.getU().get(0, 1);
		resul[1][2] = svd.getU().get(1, 1);
		resul[2][2] = svd.getU().get(2, 1);

		Matrix q = new Matrix(resul);

		if (q.det() < 0) {
			resul[0][2] = -resul[0][2];
			resul[1][2] = -resul[1][2];
			resul[2][2] = -resul[2][2];
		}
		q = new Matrix(resul);

		svd = q.svd();
		q = svd.getU().times(svd.getV().transpose());

		return q;
	}

	private Matrix rot_mat(Matrix mat, Matrix mat2) {

		double[][] pwd = mat.getArray();
		double[][] pwd2 = mat2.getArray();
		double[][] resul = new double[4][4];

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				resul[i][j] = pwd2[i][j];

		resul[0][3] = pwd[0][0];
		resul[1][3] = pwd[1][0];
		resul[2][3] = pwd[2][0];
		resul[3][0] = 0;
		resul[3][1] = 0;
		resul[3][2] = 0;
		resul[3][3] = 1;

		return new Matrix(resul);

	}

	private Matrix origin() {
		double[][] resul = new double[3][1];

		resul[0][0] = nX * dx;
		resul[1][0] = nY * dy;
		resul[2][0] = nZ * dz / 2;

		return new Matrix(resul);
	}

	private Matrix voxel_sz() {

		double[][] resul = new double[4][4];

		resul[0][0] = dx;
		resul[0][1] = dy;
		resul[0][2] = dz;
		resul[0][3] = 0;
		resul[1][0] = dx;
		resul[1][1] = dy;
		resul[1][2] = dz;
		resul[1][3] = 0;
		resul[2][0] = dx;
		resul[2][1] = dy;
		resul[2][2] = dz;
		resul[2][3] = 0;
		resul[3][0] = 0;
		resul[3][1] = 0;
		resul[3][2] = 0;
		resul[3][3] = 1;

		return new Matrix(resul);
	}

	private double[] quatern_bcd() {

		Matrix P = Q_tot;

		double qfac = 1.0;

		// if (P.det() > 0)
		// qfac = 1.0;
		// else {
		// qfac = -1.0;
		// P.set(0, 2, - P.get(0, 2));
		// P.set(1, 2, - P.get(1, 2));
		// P.set(2, 2, - P.get(2, 2));
		// }

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
				d = 0.25 * (o[0][2] + o[2][0]) / b; // ?? d = 0.25 * (o[0][2]+o[2][1]) / b;
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

		return quaterns;
	}

	public double[][] getsform() {

		// System.out.println(A_tot.get(0, 0) +" , "+A_tot.get(0, 1) +" , "+A_tot.get(0,
		// 2) +" , "+A_tot.get(0, 3) );
		// System.out.println(A_tot.get(1, 0) +" , "+A_tot.get(1, 1) +" , "+A_tot.get(1,
		// 2) +" , "+A_tot.get(1, 3) );
		// System.out.println(A_tot.get(2, 0) +" , "+A_tot.get(2, 1) +" , "+A_tot.get(2,
		// 2) +" , "+A_tot.get(2, 3) );
		// System.out.println(A_tot.get(3, 0) +" , "+A_tot.get(3, 1) +" , "+A_tot.get(3,
		// 2) +" , "+A_tot.get(3, 3) );

		return A_tot.getArray();
	}

	public double[] getquaterns() {

		return quaterns;
	}
}