package brukerParavision;

import abstractClass.ParamMRI2;
import abstractClass.convertNifti;
import ij.ImagePlus;

public class ConvertBrukerToNifti implements ParamMRI2, convertNifti {

//	private AffineQuaternionBruker_multiOrientation bts;
	private AffineQuaternionBruker_mrtrix bts;

	@Override
	public ImagePlus convertToNifti(String lb) {
		return new OpenBruker(listBasket_hmInfo.get(lb), listBasket_hmOrderImage.get(lb), false, true, lb).getImp();
	}

	@Override
	public void AffineQuaternion(String lb) {
		String tmp="";
		if (lb.contains("[")) 
			tmp = lb.substring(0,lb.lastIndexOf("[")).trim();
		if (tmp.endsWith("-axial"))
			bts = new AffineQuaternionBruker_mrtrix(listBasket_hmInfo.get(lb).get("File path"), "axial");
//			bts = new AffineQuaternionBruker_multiOrientation(listBasket_hmInfo.get(lb).get("File path"), "axial");
		else if (tmp.endsWith("-coronal"))
			bts = new AffineQuaternionBruker_mrtrix(listBasket_hmInfo.get(lb).get("File path"), "coronal");
		else if (tmp.endsWith("-sagittal"))
			bts = new AffineQuaternionBruker_mrtrix(listBasket_hmInfo.get(lb).get("File path"), "sagittal");
		else
			bts = new AffineQuaternionBruker_mrtrix(listBasket_hmInfo.get(lb).get("File path"), "");
//		bts = new AffineQuaternionBruker(listBasket_hmInfo.get(lb).get("File path"));
	}

	@Override
	public double[][] srow() {
		return bts.getMat();
	}

	@Override
	public double[] quaterns() {
		return bts.getQuatern();
	}
}