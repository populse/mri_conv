package dcm;

/*
 * imageOrientation : row and column direction cosines from (0020,0037) Image Orientation (Patient).
 */

public class DicomOrientation {
	
	private static final double obliquityThresholdCosineValue = 0.8;
	public String orientationFound;
	
	public DicomOrientation(String imageOrientation) {
		
		String[] listOrientation = imageOrientation.split("\\\\"); 
		Double[] coord = new Double[6];
		
		for (int i=0;i<6;i++){
			coord[i]=Double.parseDouble(listOrientation[i]);
		}
		orientationFound = makeImageOrientationLabelFromImageOrientationPatient(coord[0],coord[1],coord[2],coord[3],coord[4],coord[5]);
	}
	
	/**
	 * <p>Get a label describing the axial, coronal or sagittal plane from row and column unit vectors (direction cosines) as found in ImageOrientationPatient.</p>
	 *
	 * <p>Some degree of deviation from one of the standard orthogonal planes is allowed before deciding the plane is OBLIQUE.</p>
	 *
	 * @param	rowX
	 * @param	rowY
	 * @param	rowZ
	 * @param	colX
	 * @param	colY
	 * @param	colZ
	 * @return		the string describing the plane of orientation, AXIAL, CORONAL, SAGITTAL or OBLIQUE
	 */
	private String makeImageOrientationLabelFromImageOrientationPatient(double rowX,double rowY,double rowZ,double colX,double colY,double colZ) {
		String label = null;
		String rowAxis = getMajorAxisFromPatientRelativeDirectionCosine(rowX,rowY,rowZ);
		String colAxis = getMajorAxisFromPatientRelativeDirectionCosine(colX,colY,colZ);
		if (rowAxis != null && colAxis != null) {
			if      ((rowAxis.equals("R") || rowAxis.equals("L")) && (colAxis.equals("A") || colAxis.equals("P"))) label="axial";
			else if ((colAxis.equals("R") || colAxis.equals("L")) && (rowAxis.equals("A") || rowAxis.equals("P"))) label="axial";
		
			else if ((rowAxis.equals("R") || rowAxis.equals("L")) && (colAxis.equals("H") || colAxis.equals("F"))) label="coronal";
			else if ((colAxis.equals("R") || colAxis.equals("L")) && (rowAxis.equals("H") || rowAxis.equals("F"))) label="coronal";
		
			else if ((rowAxis.equals("A") || rowAxis.equals("P")) && (colAxis.equals("H") || colAxis.equals("F"))) label="sagittal";
			else if ((colAxis.equals("A") || colAxis.equals("P")) && (rowAxis.equals("H") || rowAxis.equals("F"))) label="sagittal";
		}
		else {
			label="oblique";
		}
		return label;
	}
	
	/**
	 * <p>Get a label describing the major axis from a unit vector (direction cosine) as found in ImageOrientationPatient.</p>
	 *
	 * <p>Some degree of deviation from one of the standard orthogonal axes is allowed before deciding no major axis applies and returning null.</p>
	 *
	 * @param	x
	 * @param	y
	 * @param	z
	 * @return		the string describing the orientation of the vector, or null if oblique
	 */
	private String getMajorAxisFromPatientRelativeDirectionCosine(double x,double y,double z) {
		String axis = null;
		
		String orientationX = x < 0 ? "R" : "L";
		String orientationY = y < 0 ? "A" : "P";
		String orientationZ = z < 0 ? "F" : "H";

		double absX = Math.abs(x);
		double absY = Math.abs(y);
		double absZ = Math.abs(z);

		// The tests here really don't need to check the other dimensions,
		// just the threshold, since the sum of the squares should be == 1.0
		// but just in case ...
		
		if (absX>obliquityThresholdCosineValue && absX>absY && absX>absZ) {
			axis=orientationX;
		}
		else if (absY>obliquityThresholdCosineValue && absY>absX && absY>absZ) {
			axis=orientationY;
		}
		else if (absZ>obliquityThresholdCosineValue && absZ>absX && absZ>absY) {
			axis=orientationZ;
		}
		return axis;
	}
	
	
	/**
	 * 
	 * @return	return calculated orientation 
	 */
	public String getOrientationDicom() {
		return orientationFound;
	}

}