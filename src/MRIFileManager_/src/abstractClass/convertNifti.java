package abstractClass;

import ij.ImagePlus;

public interface convertNifti {
	
	public abstract ImagePlus convertToNifti(String lb);
	
	public abstract void AffineQuaternion(String lb);
	
	public abstract double [][] srow();
	
	public abstract double[] quaterns();

}