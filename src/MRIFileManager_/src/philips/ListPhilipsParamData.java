package philips;

import java.util.HashMap;

public interface ListPhilipsParamData {
	
	HashMap<String, String> getInfoImageAcq();
	
	HashMap<String, String> getInfoImageCal();
	
	StringBuffer[] getListImgNbr();
	
	StringBuffer[] getListImgNbrAcq();
	
	StringBuffer[] getListImgNbrCal();
	
	StringBuffer[] getListRSI();
	
	int getMiddleOffsetAcq();
	
	int getMiddleOffsetCal();
	
	int getNImage();
	
}