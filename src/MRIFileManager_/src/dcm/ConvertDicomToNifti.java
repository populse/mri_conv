package dcm;

import abstractClass.ParamMRI2;
import abstractClass.convertNifti;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.HyperStackConverter;

public class ConvertDicomToNifti implements ParamMRI2, convertNifti {

	private AffineQuaternionDicom bts;

	@Override
	public ImagePlus convertToNifti(String lb) {
		ImagePlus imp = null;
		String noSeq = listBasket_hmInfo.get(lb).get("noSeq");
		
		imp = new OpenDicom(listBasket_hmInfo.get(lb), listBasket_hmOrderImage.get(lb), listBasket_hmSeq.get(lb), noSeq,
				false).getImp();

		try {
			if (imp.getNDimensions() == 4 && imp.getNFrames() == 1)
				imp = HyperStackConverter.toHyperStack(imp, imp.getNSlices(), imp.getNFrames(), imp.getNChannels(),
						"xytcz", "grayscale");

			if (imp.getNDimensions() == 5)
				imp = HyperStackConverter.toHyperStack(imp, imp.getNSlices(), imp.getNFrames(), imp.getNChannels(),
						"xytcz", "grayscale");
		} catch (Exception e) {
			IJ.log(noSeq + " : problem with number of stack");
		}

		return imp;
	}

	@Override
	public void AffineQuaternion(String lb) {
		bts = new AffineQuaternionDicom(lb);

	}

	@Override
	public double[][] srow() {
		return bts.getsform();
	}

	@Override
	public double[] quaterns() {
		return bts.getquaterns();
	}
}