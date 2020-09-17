package philips;

import java.io.IOException;

import abstractClass.ParamMRI2;
import abstractClass.PrefParam;

public class ListPhilipsSequence extends PrefParam implements ParamMRI2 {

	private String[] listParamSeq = headerListSeq;
	public static String noSeqCurrent,noSeqCurrentCal;
	public static String[] listScanSeq;
	public static boolean hasCalcImg,hasAcqImg;

	public ListPhilipsSequence(String pathPhilips, String iter) {
		new FillHmsPhilips(pathPhilips,iter);
	}

	public Object[] ListSeqPhilipsAcq() throws IOException {

		Object[] resul = new String[listParamSeq.length];
				
		resul[0]=noSeqCurrent;
		
		for (int i = 1; i < resul.length; i++) {
			try {
				resul[i] = hmInfo.get(noSeqCurrent).get(listParamSeq[i]);
				if (resul[i]==null)
					resul[i]="";
			} catch (Exception e) {
				resul[i] = "";
			}
		}
		return resul;
	}
	
	public Object[] ListSeqPhilipsCal() throws IOException {
		
		Object[] resul = new String[listParamSeq.length];
		
		resul[0]=noSeqCurrentCal;
		
		for (int i = 1; i < resul.length; i++) {
			try {
				resul[i] = hmInfo.get(noSeqCurrentCal).get(listParamSeq[i]);
				if (resul[i]==null)
					resul[i]="";
			} catch (Exception e) {
				resul[i] = "";
			}
		}
		return resul;
		
		
		
	}
}