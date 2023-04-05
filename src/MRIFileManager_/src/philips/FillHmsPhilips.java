package philips;

import java.util.HashMap;

import MRIFileManager.GetStackTrace;
import abstractClass.ParamMRI2;

public class FillHmsPhilips implements ParamMRI2 {

	public FillHmsPhilips(String pathPhilips, String iter) {

		HashMap<String, String> listParam = null, listParamCal;
		Object[] listOrderStack = null,listOrderStackCal;
		String iter2=iter;

		try {
			ListPhilipsParam listPhilipsPar = new ListPhilipsParam(pathPhilips);
		
			if (ListPhilipsSequence.hasAcqImg && ListPhilipsSequence.hasCalcImg ) {
				String prefixSeq=("000000").substring(0, iter.length());
				iter2=(prefixSeq + (Integer.parseInt(iter)+1)).substring(String.valueOf(Integer.parseInt(iter)+1).length());
			}

			if (ListPhilipsSequence.hasAcqImg) {
				listParam = listPhilipsPar.ListParamValueAcq(iter);
				listOrderStack = listPhilipsPar.ListOrderStackAcq("", "");
//				ListPhilipsSequence.noSeqCurrent = listParam.get("noSeq");
//				hmInfo.put(listParam.get("noSeq"), listParam);
//				hmOrderImage.put(listParam.get("noSeq"), listOrderStack);
				String[] hmValue = new String[1];
				hmValue[0] = pathPhilips;
//				hmSeq.put(listParam.get("noSeq"), hmValue);
				
				ListPhilipsSequence.noSeqCurrent = iter;
				hmInfo.put(iter, listParam);
				hmOrderImage.put(iter, listOrderStack);
				hmSeq.put(iter, hmValue);
			}

			if (ListPhilipsSequence.hasCalcImg){
				listParamCal = listPhilipsPar.ListParamValueCal(iter2);
				listOrderStackCal = listPhilipsPar.ListOrderStackCal("", "");
//				ListPhilipsSequence.noSeqCurrentCal = listParamCal.get("noSeq");
//				hmInfo.put(listParamCal.get("noSeq"), listParamCal);
//				hmOrderImage.put(listParamCal.get("noSeq"), listOrderStackCal);
				String[] hmValue = new String[1];
				hmValue[0] = pathPhilips;
//				hmSeq.put(listParamCal.get("noSeq"), hmValue);
				
				ListPhilipsSequence.noSeqCurrentCal = iter2;
				hmInfo.put(iter2, listParamCal);
				hmOrderImage.put(iter2, listOrderStackCal);
				hmSeq.put(iter2, hmValue);
			}

			listParam = null;
			listPhilipsPar = null;
			System.gc();

		} catch (Exception e) {
			new GetStackTrace(e, this.getClass().toString());
		}
	}
}