package dcm;


public class ListTagsValue {

	private String[] getListTagValue(StringBuffer hdr, String Manufacturer) {

		String bvalue_field = "0018,9087";
		if (Manufacturer.contains("Philips"))
			bvalue_field = "2001,1003";

		String[] tags = {
			"Echo Numbers(s)",
			"Slice Number",
			"Repetition Time",
			"Echo Time",
			"Inversion Time",
			"Slice Location",
			"Image Type",
			"Temporal Position",
			"Rescale Intercept",
			"Rescale Slope",
			"Acquisition Time",
			"Image Position (Patient)",
			"Image Orientation (Patient)",
			bvalue_field,						// Bval
			"0018,0020",						// Scanning sequence - type of data (SE, IR, GR, EP, RM)
			"2005,1429",						// Label Type (ASL)
			"2005,100E",						// Scale Slope Philips
			"0018,9087",						// bvecs
			"2005,1413"
		};
		
		String[] sublist = new String[tags.length + 2];
		
		for (int i=0; i<tags.length; i++) {
			sublist[i + 2] = searchParam(hdr, tags[i]);
		}
		
		String exp_num = "-?\\d+(\\.\\d+)?(E-?\\d+)?(E\\+?\\d+)?(E?\\d+)?(e-?\\d+)?(e\\+?\\d+)?(e?\\d+)?";
		sublist[8] = new ChangeSyntax().NewSyntaxType(sublist[8]);
		if (!sublist[10].matches(exp_num))
			sublist[10] = "0";
		if (!sublist[11].matches(exp_num))
			sublist[11] = "1";
		sublist[16] = new ChangeSyntax().NewSyntaxScanSeq(sublist[16]);
		if (!sublist[18].matches(exp_num))
			sublist[18] = "1";
		if (Manufacturer.contains("Philips"))
			sublist[19] = searchParam(hdr, "2005,10B0") + " " + searchParam(hdr, "2005,10B1") + " "
					+ searchParam(hdr, "2005,10B2");
				
		return sublist;
		
	}

	private String searchParam(StringBuffer txt, String paramToFind) {
		String resul = "";
		int indx = txt.indexOf(paramToFind);
		try {
			if (indx != -1) {
				resul = txt.substring(indx);
				resul = resul.substring(resul.indexOf(":") + 1, resul.indexOf("\n"));
			}
		} catch (Exception e) {
			resul = "";
		}
		return resul.trim();
	}

}
