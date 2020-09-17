package exportFiles;

import abstractClass.ParamMRI2;

public class CalculTotalSizeBasket implements ParamMRI2 {
	
	private float totalSize=0;
	
	public CalculTotalSizeBasket() {
		
		String seek;
				
		for (int i=0;i<listinBasket.size();i++) {
			seek = listinBasket.get(i);
			seek = seek.substring(seek.lastIndexOf("[")+1,seek.lastIndexOf("Mo")-1).trim();
			totalSize+=Float.parseFloat(seek);
		}
	}
	
	public String getTotalSize() {
		return String.valueOf(totalSize);
	}
}