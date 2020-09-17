package philips;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;
import abstractClass.convertNifti;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.HyperStackConverter;
import ij.process.ImageProcessor;

public class ConvertPhilipsToNifti implements ParamMRI2,convertNifti {

	private AffineQuaternionPhilips bts;
	
	@Override
	public ImagePlus convertToNifti(String lb) {
		ImagePlus imp = null;
		try {
			imp = new OpenPhilips4(listBasket_hmInfo.get(lb), listBasket_hmOrderImage.get(lb), false,true, lb).getImp();
		} catch (Exception e) {
			new GetStackTrace(e);
		}
		
		if (imp.getNDimensions() == 5)
			imp = HyperStackConverter.toHyperStack(imp, imp.getNSlices(), imp.getNFrames(), imp.getNChannels(), "xytcz", "grayscale");

		
		ImageStack stack = imp.getStack();
		for (int i=1; i<=stack.getSize(); i++) {
			ImageProcessor ip = stack.getProcessor(i);
			ip.flipVertical();
		}

		return imp;
	}

	@Override
	public void AffineQuaternion(String lb) {
//		bts = new AffineQuaternionPhilips(listBasket_hms.get(lb).get("File path"));
		bts = new AffineQuaternionPhilips(lb);
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