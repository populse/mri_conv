package MRIFileManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Dateformatmodif {

	private String resul;

	public Dateformatmodif(String date, String oldFormat, String newFormat) {
		
//		System.out.println(date+" , "+oldFormat+" , "+newFormat);

		resul = "";

		if (!date.isEmpty()) {
			SimpleDateFormat dt;
			Date df = null;
			
			for (String jj : oldFormat.split("or")) {
				dt = new SimpleDateFormat(jj.trim(),new Locale("EN","en")); 
				
				try {
					df = dt.parse(date);
					break;
				}
				catch (ParseException e) {
				} 
			}
			SimpleDateFormat dt1 = new SimpleDateFormat(newFormat);
			
			resul = dt1.format(df);
		}
	}

	public String getNewFormatDate() {
		return resul;
	}
}